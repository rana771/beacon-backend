package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.RequestMap
import com.athena.mis.application.entity.Role
import com.athena.mis.application.entity.RoleFeatureMapping
import com.athena.mis.integration.accounting.AccPluginConnector
import com.athena.mis.integration.arms.ArmsPluginConnector
import com.athena.mis.integration.budget.BudgPluginConnector
import com.athena.mis.integration.document.DocumentPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.integration.fixedasset.FxdPluginConnector
import com.athena.mis.integration.inventory.InvPluginConnector
import com.athena.mis.integration.procurement.ProcPluginConnector
import com.athena.mis.integration.projecttrack.PtPluginConnector
import com.athena.mis.integration.qsmeasurement.QsPluginConnector
import com.athena.mis.integration.sarb.SarbPluginConnector
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 * RequestMapService is used to handle only CRUD related object manipulation
 * (e.g. list, read, update etc.)
 */
class RequestMapService extends BaseDomainService {

    private Logger log = Logger.getLogger(getClass())

    RoleService roleService
    RoleFeatureMappingService roleFeatureMappingService
    AppVersionService appVersionService

    @Override
    public void init() {
        domainClass = RequestMap.class
    }

    /**
     * Method to get list of RequestMap
     */
    @Override
    public List list() {
        return RequestMap.list(sort: RequestMap.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true);
    }

    public RequestMap findByUrl(String url) {
        return RequestMap.findByUrl(url, [readOnly: true])
    }

    /**
     * Method to update assigned or removed requestMap for particular role
     * @param lstAssignedFeatures - list of assigned features
     * @param roleAuthority - string of role authority
     * @param pluginId - plugin Id
     */
    public boolean update(List<Long> lstAssignedFeatures, String roleAuthority, int pluginId) {
        String queryStr = """
                SELECT id
                FROM request_map
                WHERE
                id NOT IN (${ReservedRoleService.REQUEST_MAP_ROOT},${ReservedRoleService.REQUEST_MAP_LOGOUT})
                AND plugin_id = ${pluginId}
                AND(
                config_attribute LIKE '%,${roleAuthority},%'
                OR
                config_attribute LIKE '${roleAuthority},%'
                OR
                config_attribute LIKE '%,${roleAuthority}'
                OR
                config_attribute =  '${roleAuthority}'
                )
        """

        // First get the current assigned role
        List<GroovyRowResult> result = executeSelectSql(queryStr)

        List<Long> lstOldFeatures = []
        for (int i = 0; i < result.size(); i++) {
            lstOldFeatures << result[i].id
        }

        // Find the common elements
        List lstCommonFeatures = lstOldFeatures.intersect(lstAssignedFeatures)

        List<Long> lstToRemove = (List<Long>) lstOldFeatures.clone()
        // Get the IDs of requestMap where current ROLE has lost the RIGHT
        lstToRemove.removeAll(lstCommonFeatures)      // i.e. ToBeRemoved=(Existing Feature) - (Common Features)
        // Get the IDs of requestMap where current ROLE has gain the RIGHT
        List<Long> lstToAdd = (List<Long>) lstAssignedFeatures.clone()
        lstToAdd.removeAll(lstCommonFeatures)        // i.e. ToBeAdded=(All assigned Feature) - (Common Features)

        // If something to add then execute sql
        if (lstToAdd.size() > 0) {
            String idsForAdd = super.buildCommaSeparatedStringOfIds(lstToAdd)
            String queryToAdd = """
                UPDATE request_map
                SET config_attribute =
                    CASE WHEN config_attribute IS NULL THEN '${roleAuthority}'
                    WHEN config_attribute ='' THEN '${roleAuthority}'
                    ELSE config_attribute || ',${roleAuthority}'
                    END
                WHERE id IN (${idsForAdd})
        """
            executeUpdateSql(queryToAdd)
        }

        // If something to remove then execute sql
        if (lstToRemove.size() > 0) {
            String idsForRemove = super.buildCommaSeparatedStringOfIds(lstToRemove)
            String queryToRemove = """
            UPDATE request_map
            SET config_attribute =
                CASE WHEN config_attribute LIKE '%${roleAuthority},%' THEN
                    REPLACE(config_attribute, '${roleAuthority},' , '')
                WHEN config_attribute LIKE '%,${roleAuthority}%' THEN
                    REPLACE(config_attribute, ',${roleAuthority}' , '')
                ELSE NULL
                END
            WHERE id IN (${idsForRemove})
        """
            executeUpdateSql(queryToRemove)
        }
        return true
    }

    /**
     * Method to update when role name is changed
     * @param previousRole - string of previous role from caller method
     * @param newRole - string of new role from caller method
     * @return
     */
    public boolean update(String previousRole, String newRole) {
        String queryStr = """
            UPDATE request_map
                SET config_attribute =
            CASE WHEN config_attribute LIKE '%,${previousRole},%' THEN
                REPLACE(config_attribute, ',${previousRole},' , ',${newRole},')
            WHEN config_attribute LIKE '%,${previousRole}' THEN
                REPLACE(config_attribute, ',${previousRole}' , ',${newRole}')
            ELSE config_attribute
            END
        """

        executeUpdateSql(queryStr)

        return true;
    }

    /**
     * Method to create request map for application plugin
     * last used transaction_code = 'APP-465'
     */
    public boolean createRequestMapForApplicationPlugin() {
        try {
            int count = appVersionService.countByPluginId(PluginConnector.PLUGIN_ID)
            if (count > 0) {
                return true
            }
            // default
            executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_common, is_viewable)
            VALUES(-2,0,'/','ROLE_-2','INDEX PAGE',1,'APP-1', TRUE, FALSE)""")

            executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_common, is_viewable)
            VALUES(-3,0,'/logout/**','ROLE_-2','LOGOUT ACCESS',1,'APP-2', TRUE, FALSE)""")

            executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_common,is_viewable)
            VALUES(-4,0, '/plugins/applicationplugin-0.1/theme/application/js*//**','IS_AUTHENTICATED_ANONYMOUSLY',null,1,'APP-3',FALSE,FALSE)""")

            executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_common,is_viewable)
            VALUES(-5,0, '/plugins/applicationplugin-0.1/theme/application/css*//**','IS_AUTHENTICATED_ANONYMOUSLY',null,1,'APP-4',FALSE,FALSE)""")

            executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_common,is_viewable)
            VALUES(-6,0, '/plugins/applicationplugin-0.1/theme/application/images*//**','IS_AUTHENTICATED_ANONYMOUSLY',null,1,'APP-5',FALSE,FALSE)""")

            executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_common,is_viewable)
            VALUES(-7,0, '/login/**','IS_AUTHENTICATED_ANONYMOUSLY',null,1,'APP-6',FALSE,FALSE)""")

            executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_common,is_viewable)
            VALUES(-8,0,'/login/checklogin','IS_AUTHENTICATED_ANONYMOUSLY',null,1,'APP-7',FALSE,FALSE)""")

            executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_common,is_viewable)
            VALUES(-9,0,'/logout/index','IS_AUTHENTICATED_ANONYMOUSLY',null,1,'APP-8',FALSE,FALSE)""")

            executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_common,is_viewable)
            VALUES(-10,0,'/jcaptcha/**','IS_AUTHENTICATED_ANONYMOUSLY',null,1,'APP-9',FALSE,FALSE)""")

            executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_common,is_viewable)
            VALUES(-13,0, '/appUser/managePassword','ROLE_-2','MANAGE PASSWORD',1,'APP-16',TRUE,TRUE)""")

            executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_common,is_viewable)
            VALUES(-14,0, '/appUser/checkPassword','ROLE_-2','CHECK PASSWORD',1,'APP-17',FALSE,FALSE)""")

            executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_common,is_viewable)
            VALUES(-15,0, '/appUser/changePassword','ROLE_-2','CHANGE PASSWORD',1,'APP-18', TRUE, FALSE)""")

            executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_common,is_viewable)
            VALUES(-23,0, '/application/renderApplicationMenu','ROLE_-2','Application Module',1,'APP-92',TRUE,FALSE);""")

            new RequestMap(transactionCode: 'APP-448', url: '/plugins/applicationplugin-0.1/views/app//**', featureName: 'application error, 404, login page', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-449', url: '/j_spring_security_switch_user', featureName: 'switch user', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-450', url: '/login/switchUser', featureName: 'switch user ajax', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-461', url: '/appUser/switchToBangla', featureName: 'switch language to bangla', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-462', url: '/appUser/switchToEnglish', featureName: 'switch language to english', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.TRUE, isViewable: Boolean.FALSE).save();

            // AppMyFavourite
            new RequestMap(transactionCode: 'APP-346', url: '/appMyFavourite/create', featureName: 'Add page to my favourite', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.TRUE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-347', url: '/appMyFavourite/list', featureName: 'List of my favourite pages', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.TRUE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-348', url: '/appMyFavourite/delete', featureName: 'Delete my favourite page', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.TRUE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-349', url: '/appMyFavourite/setAsDefault', featureName: 'Set my favourite page as default', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.TRUE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-353', url: '/appMyFavourite/select', featureName: 'Select My Favourite', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.TRUE, isViewable: Boolean.FALSE).save();

            // AppUser
            new RequestMap(transactionCode: 'APP-10', url: '/appUser/show', featureName: 'Show App User', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
            new RequestMap(transactionCode: 'APP-11', url: '/appUser/create', featureName: 'Create App User', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-13', url: '/appUser/update', featureName: 'update App User', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-14', url: '/appUser/delete', featureName: 'Delete App User', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-15', url: '/appUser/list', featureName: 'list App User', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-154', url: '/appUser/showOnlineUser', featureName: 'Show online user', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
            new RequestMap(transactionCode: 'APP-155', url: '/appUser/listOnlineUser', featureName: 'List online user', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-156', url: '/appUser/forceLogoutOnlineUser', featureName: 'Forced logout online user', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-184', url: '/appUser/sendPasswordResetLink', featureName: 'Send mail for reset password', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-185', url: '/appUser/showResetPassword', featureName: 'Show for reset password', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
            new RequestMap(transactionCode: 'APP-186', url: '/appUser/resetPassword', featureName: 'Reset password', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-191', url: '/appUser/resetExpiredPassword', featureName: 'Reset expired password', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-333', url: '/appUser/showAllUser', featureName: 'Show All User', configAttribute: 'ROLE_-2,ROLE_RESELLER', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
            new RequestMap(transactionCode: 'APP-334', url: '/appUser/listAllUser', featureName: 'List of All User', configAttribute: 'ROLE_-2,ROLE_RESELLER', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-335', url: '/appUser/updateAllUser', featureName: 'Update All User', configAttribute: 'ROLE_-2,ROLE_RESELLER', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            // update user profile
            new RequestMap(transactionCode: 'APP-457', url: '/appUser/showProfile', featureName: 'Show User Profile', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.TRUE, isViewable: Boolean.TRUE).save();
            new RequestMap(transactionCode: 'APP-458', url: '/appUser/updateProfile', featureName: 'Update User Profile', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.TRUE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-459', url: '/appUser/renderProfileImage', featureName: 'Render User Profile Image', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.TRUE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-460', url: '/appUser/uploadProfileImage', featureName: 'Upload User Profile Image', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.TRUE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-463', url: '/appUser/uploadDocument', featureName: 'Upload User Document', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.TRUE, isViewable: Boolean.FALSE).save();

            // app user Registration
            new RequestMap(transactionCode: 'APP-350', url: '/appUser/showRegistration', featureName: 'Show app user registration', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 1, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-351', url: '/appUser/register', featureName: 'Register app user', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-352', url: '/appUser/activate', featureName: 'Activate app user account', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // App Mail
            new RequestMap(transactionCode: 'APP-187', url: '/appMail/show', featureName: 'Show App mail', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save()
            new RequestMap(transactionCode: 'APP-188', url: '/appMail/update', featureName: 'Update App mail', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-189', url: '/appMail/list', featureName: 'List of app mail', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-251', url: '/appMail/testAppMail', featureName: 'Test app mail', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-262', url: '/appMail/sendMailForError', featureName: 'Send mail to report error', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-364', url: '/appMail/createAnnouncement', featureName: 'Create Announcement', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-365', url: '/appMail/deleteAnnouncement', featureName: 'Delete Announcement', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-366', url: '/appMail/sendAnnouncement', featureName: 'Send Announcement', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-367', url: '/appMail/showAnnouncement', featureName: 'Show Announcement', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save()
            new RequestMap(transactionCode: 'APP-368', url: '/appMail/listAnnouncement', featureName: 'List Announcement For Compose', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-433', url: '/appMail/listAnnouncementForDashboard', featureName: 'List Announcement in Dashboard', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.TRUE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-369', url: '/appMail/updateAnnouncement', featureName: 'Update Announcement', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-370', url: '/appMail/showForSend', featureName: 'Show Sent App Mail', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save()
            new RequestMap(transactionCode: 'APP-371', url: '/appMail/listForSend', featureName: 'List Sent App Mail', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-372', url: '/appMail/reComposeAnnouncement', featureName: 'Re-Compose Announcement', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-427', url: '/appMail/showForComposeMail', featureName: 'Show Mail Compose', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save()
            new RequestMap(transactionCode: 'APP-428', url: '/appMail/listMail', featureName: 'List Mail', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-429', url: '/appMail/createMail', featureName: 'Create Mail', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-430', url: '/appMail/UpdateMail', featureName: 'Update mail', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-431', url: '/appMail/sendMail', featureName: 'Send Mail', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-432', url: '/appMail/deleteMail', featureName: 'Delete Mail', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-435', url: '/appMail/showForSentMail', featureName: 'Show Sent Mail', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save()
            new RequestMap(transactionCode: 'APP-436', url: '/appMail/listForSentMail', featureName: 'List Sent Mail', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-437', url: '/appMail/previewMail', featureName: 'Preview Sent Mail', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-438', url: '/appMail/createAndSend', featureName: 'Create and send Mail', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-439', url: '/appMail/uploadAttachment', featureName: 'Upload attachment for Mail', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()

            // AppMessage
            new RequestMap(transactionCode: 'APP-422', url: '/appMessage/show', featureName: 'Show Message', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.TRUE, isViewable: Boolean.TRUE).save()
            new RequestMap(transactionCode: 'APP-423', url: '/appMessage/list', featureName: 'List of Message', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.TRUE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-424', url: '/appMessage/delete', featureName: 'Delete Message', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.TRUE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-426', url: '/appMessage/markAsUnRead', featureName: 'Mark as Un-Read Message', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.TRUE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-434', url: '/appMessage/preview', featureName: 'Message show in Details', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.TRUE, isViewable: Boolean.FALSE).save()

            //Document Shell Script Request Map
            new RequestMap(transactionCode: 'APP-253', url: '/appShellScript/show', featureName: 'Show Shell Script', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-400', url: '/appShellScript/showSql', featureName: 'Show SQL Script', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-254', url: '/appShellScript/create', featureName: 'Create Shell Script', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-255', url: '/appShellScript/list', featureName: 'List Shell Script', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-257', url: '/appShellScript/update', featureName: 'Update Shell Script', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-258', url: '/appShellScript/delete', featureName: 'Delete Shell Script', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-259', url: '/appShellScript/evaluate', featureName: 'Evaluate Shell Script', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-401', url: '/appShellScript/evaluateSqlScript', featureName: 'Evaluate SQL Script', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-409', url: '/appShellScript/downloadReport', featureName: 'Download Script Report', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // role
            new RequestMap(transactionCode: 'APP-19', url: '/role/show', featureName: 'Show Role', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save()
            new RequestMap(transactionCode: 'APP-20', url: '/role/create', featureName: 'Create Role', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-22', url: '/role/update', featureName: 'Update Role', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-23', url: '/role/delete', featureName: 'Delete Role', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-24', url: '/role/list', featureName: 'List Role', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-315', url: '/role/showMyRole', featureName: 'Show My Role', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save()
            new RequestMap(transactionCode: 'APP-316', url: '/role/listMyRole', featureName: 'List My Role', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-327', url: '/role/downloadUserRoleReport', featureName: 'Download user role report', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()

            // role module
            new RequestMap(transactionCode: 'APP-403', url: '/roleModule/show', featureName: 'Show Role Module', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save()
            new RequestMap(transactionCode: 'APP-404', url: '/roleModule/create', featureName: 'Create Role Module', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-405', url: '/roleModule/update', featureName: 'Update Role Module', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-406', url: '/roleModule/delete', featureName: 'Delete Role Module', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-407', url: '/roleModule/list', featureName: 'List Role Module', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-408', url: '/roleModule/dropDownRoleModuleReload', featureName: 'Reload module drop down', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()

            // requestMap
            new RequestMap(transactionCode: 'APP-25', url: '/requestMap/show', featureName: 'Show Request Map', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save()
            new RequestMap(transactionCode: 'APP-28', url: '/requestMap/update', featureName: 'Update Request Map', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-31', url: '/requestMap/resetRequestMap', featureName: 'Reset Request Map By Plugin ID', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-326', url: '/requestMap/reloadRequestMap', featureName: 'Reload Request Map', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-331', url: '/requestMap/listAvailableRole', featureName: 'Get list of available features', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-332', url: '/requestMap/listAssignedRole', featureName: 'Get list of assigned features', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()

            // userRole
            new RequestMap(transactionCode: 'APP-32', url: '/userRole/show', featureName: 'Show User Role', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save()
            new RequestMap(transactionCode: 'APP-33', url: '/userRole/create', featureName: 'Create User Role', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-35', url: '/userRole/update', featureName: 'Update User Role', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-36', url: '/userRole/delete', featureName: 'Delete User Role', configAttribute: 'ROLE_-2,ROLE_RESELLER', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-37', url: '/userRole/list', featureName: 'List User Role', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-328', url: '/userRole/dropDownAppUserForRoleReload', featureName: 'User Role Dropdown reload', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-340', url: '/userRole/showForCompanyUser', featureName: 'Show user role mapping for company user', configAttribute: 'ROLE_-2,ROLE_RESELLER', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save()
            new RequestMap(transactionCode: 'APP-341', url: '/userRole/listForCompanyUser', featureName: 'List user role mapping for company user', configAttribute: 'ROLE_-2,ROLE_RESELLER', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-342', url: '/userRole/createForCompanyUser', featureName: 'Create user role mapping for company user', configAttribute: 'ROLE_-2,ROLE_RESELLER', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-343', url: '/userRole/updateForCompanyUser', featureName: 'Update user role mapping for company user', configAttribute: 'ROLE_-2,ROLE_RESELLER', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-344', url: '/userRole/dropDownRoleForCompanyUserReload', featureName: 'Reload role drop down for company user', configAttribute: 'ROLE_-2,ROLE_RESELLER', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()

            // AppCustomer
            new RequestMap(transactionCode: 'APP-38', url: '/appCustomer/show', featureName: 'Show Customer', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save()
            new RequestMap(transactionCode: 'APP-39', url: '/appCustomer/create', featureName: 'Create Customer', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-41', url: '/appCustomer/update', featureName: 'Update Customer', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-42', url: '/appCustomer/delete', featureName: 'Delete Customer', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-43', url: '/appCustomer/list', featureName: 'List Customer', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()

            // AppEmployee
            new RequestMap(transactionCode: 'APP-44', url: '/appEmployee/show', featureName: 'Show Employee', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
            new RequestMap(transactionCode: 'APP-45', url: '/appEmployee/create', featureName: 'Create Employee', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-47', url: '/appEmployee/update', featureName: 'Update Employee', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-48', url: '/appEmployee/delete', featureName: 'Delete Employee', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-49', url: '/appEmployee/list', featureName: 'List Employee', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();

            // company
            new RequestMap(transactionCode: 'APP-50', url: '/company/show', featureName: 'Show Company', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
            new RequestMap(transactionCode: 'APP-51', url: '/company/create', featureName: 'Create Company', configAttribute: 'ROLE_-2,ROLE_RESELLER', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-53', url: '/company/update', featureName: 'Update Company', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-54', url: '/company/delete', featureName: 'Delete Company', configAttribute: 'ROLE_-2,ROLE_RESELLER', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-55', url: '/company/list', featureName: 'List Company', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-336', url: '/company/showForReseller', featureName: 'Show Company for Reseller', configAttribute: 'ROLE_-2,ROLE_RESELLER', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
            new RequestMap(transactionCode: 'APP-337', url: '/company/listForReseller', featureName: 'List Company for Reseller', configAttribute: 'ROLE_-2,ROLE_RESELLER', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-338', url: '/company/updateForReseller', featureName: 'Update Company for Reseller', configAttribute: 'ROLE_-2,ROLE_RESELLER', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();

            // project
            new RequestMap(transactionCode: 'APP-56', url: '/project/show', featureName: 'Show Project', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
            new RequestMap(transactionCode: 'APP-57', url: '/project/create', featureName: 'Create Project', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-59', url: '/project/update', featureName: 'Update Project', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-60', url: '/project/delete', featureName: 'Delete Project', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-61', url: '/project/list', featureName: 'List Project', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();

            // AppUserEntity
            new RequestMap(transactionCode: 'APP-238', url: '/appUserEntity/show', featureName: 'Show appUser Entity', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save()
            new RequestMap(transactionCode: 'APP-239', url: '/appUserEntity/create', featureName: 'Create appUser Entity', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-240', url: '/appUserEntity/update', featureName: 'Update appUser Entity', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-241', url: '/appUserEntity/delete', featureName: 'Delete appUser Entity', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-242', url: '/appUserEntity/list', featureName: 'List appUser Entity', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-244', url: '/appUserEntity/dropDownAppUserEntityReload', featureName: 'Reload data for app user entity drop down', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-392', url: '/appUserEntity/listUserByEntityTypeAndEntity', featureName: 'Get list of mapped entity', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()

            // ItemType
            new RequestMap(transactionCode: 'APP-174', url: '/itemType/show', featureName: 'Show item type', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save()
            new RequestMap(transactionCode: 'APP-175', url: '/itemType/create', featureName: 'Create item type', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-176', url: '/itemType/update', featureName: 'Update item type', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-177', url: '/itemType/delete', featureName: 'Delete item type', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-178', url: '/itemType/list', featureName: 'List item type', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()

            // item
            new RequestMap(transactionCode: 'APP-157', url: '/item/listItemByItemTypeId', featureName: 'Get Item list by item type ID', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-339', url: '/item/dropDownItemReload', featureName: 'Reload Item Drop Down', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();

            // inventory item
            new RequestMap(transactionCode: 'APP-158', url: '/item/showInventoryItem', featureName: 'Show Inventory Item', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
            new RequestMap(transactionCode: 'APP-159', url: '/item/createInventoryItem', featureName: 'Create Inventory Item', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-160', url: '/item/updateInventoryItem', featureName: 'Update Inventory Item', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-161', url: '/item/deleteInventoryItem', featureName: 'Delete Inventory Item', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-162', url: '/item/listInventoryItem', featureName: 'List Inventory Item', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();

            // non inventory item
            new RequestMap(transactionCode: 'APP-163', url: '/item/showNonInventoryItem', featureName: 'Show Non-Inventory Item', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
            new RequestMap(transactionCode: 'APP-164', url: '/item/createNonInventoryItem', featureName: 'Create Non-Inventory Item', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-165', url: '/item/updateNonInventoryItem', featureName: 'Update Non-Inventory Item', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-166', url: '/item/deleteNonInventoryItem', featureName: 'Delete Non-Inventory Item', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-167', url: '/item/listNonInventoryItem', featureName: 'List Non-Inventory Item', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();

            // fixed asset item
            new RequestMap(transactionCode: 'APP-168', url: '/item/showFixedAssetItem', featureName: 'Show Fixed Asset Item', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
            new RequestMap(transactionCode: 'APP-169', url: '/item/createFixedAssetItem', featureName: 'Create Fixed Asset Item', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-170', url: '/item/updateFixedAssetItem', featureName: 'Update Fixed Asset Item', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-171', url: '/item/deleteFixedAssetItem', featureName: 'Delete Fixed Asset Item', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-172', url: '/item/listFixedAssetItem', featureName: 'List Fixed Asset Item', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();

            // app group
            new RequestMap(transactionCode: 'APP-74', url: '/appGroup/show', featureName: 'Show Group', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
            new RequestMap(transactionCode: 'APP-75', url: '/appGroup/create', featureName: 'Create Group', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-77', url: '/appGroup/update', featureName: 'Update Group', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-78', url: '/appGroup/delete', featureName: 'Delete Group', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-79', url: '/appGroup/list', featureName: 'List Group', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();

            // app company user
            new RequestMap(transactionCode: 'APP-86', url: '/appUser/showForCompanyUser', featureName: 'Show Company User', configAttribute: 'ROLE_-2,ROLE_RESELLER', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
            new RequestMap(transactionCode: 'APP-87', url: '/appUser/createForCompanyUser', featureName: 'Create Company User', configAttribute: 'ROLE_-2,ROLE_RESELLER', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-88', url: '/appUser/deleteForCompanyUser', featureName: 'Delete Company User', configAttribute: 'ROLE_-2,ROLE_RESELLER', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-89', url: '/appUser/listForCompanyUser', featureName: 'List Company User', configAttribute: 'ROLE_-2,ROLE_RESELLER', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-91', url: '/appUser/updateForCompanyUser', featureName: 'Update Company User', configAttribute: 'ROLE_-2,ROLE_RESELLER', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();

            // AppCountry
            new RequestMap(transactionCode: 'APP-93', url: '/appCountry/show', featureName: 'Show country', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
            new RequestMap(transactionCode: 'APP-94', url: '/appCountry/create', featureName: 'Create country', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-95', url: '/appCountry/delete', featureName: 'Delete country', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-96', url: '/appCountry/update', featureName: 'Update country', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-98', url: '/appCountry/list', featureName: 'List country', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();

            // AppDesignation
            new RequestMap(transactionCode: 'APP-118', url: '/appDesignation/show', featureName: 'Show designation', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save()
            new RequestMap(transactionCode: 'APP-119', url: '/appDesignation/create', featureName: 'Create designation', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-120', url: '/appDesignation/update', featureName: 'Update designation', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-121', url: '/appDesignation/delete', featureName: 'Delete designation', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-122', url: '/appDesignation/list', featureName: 'List designation', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()

            // system Configuration
            new RequestMap(transactionCode: 'APP-99', url: '/systemConfiguration/show', featureName: 'Show system configuration', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
            new RequestMap(transactionCode: 'APP-100', url: '/systemConfiguration/list', featureName: 'List system configuration', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-102', url: '/systemConfiguration/update', featureName: 'Update system configuration', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();

            // systemEntity
            new RequestMap(transactionCode: 'APP-103', url: '/systemEntity/show', featureName: 'Show system entity', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
            new RequestMap(transactionCode: 'APP-104', url: '/systemEntity/create', featureName: 'Create system entity', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-105', url: '/systemEntity/list', featureName: 'List system entity', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-107', url: '/systemEntity/update', featureName: 'Update system entity', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-173', url: '/systemEntity/delete', featureName: 'Delete system entity', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();

            // systemEntityType
            new RequestMap(transactionCode: 'APP-108', url: '/systemEntityType/show', featureName: 'Show system entity type', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
            new RequestMap(transactionCode: 'APP-109', url: '/systemEntityType/list', featureName: 'List system entity type', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();

            //AppTheme
            new RequestMap(transactionCode: 'APP-124', url: '/appTheme/showTheme', featureName: 'Show Theme', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
            new RequestMap(transactionCode: 'APP-125', url: '/appTheme/updateTheme', featureName: 'Update Theme', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-126', url: '/appTheme/listTheme', featureName: 'List Theme', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-447', url: '/appTheme/reloadTheme', featureName: 'Reload Theme', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.TRUE, isViewable: Boolean.FALSE).save();

            //SMS
            new RequestMap(transactionCode: 'APP-180', url: '/appSms/showSms', featureName: 'Show sms', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
            new RequestMap(transactionCode: 'APP-181', url: '/appSms/updateSms', featureName: 'Update sms', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-182', url: '/appSms/listSms', featureName: 'List sms', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-237', url: '/appSms/sendSms', featureName: 'Send sms', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-374', url: '/appSms/create', featureName: 'Create sms', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-375', url: '/appSms/delete', featureName: 'Delete sms', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-376', url: '/appSms/showForCompose', featureName: 'Show sms for compose', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
            new RequestMap(transactionCode: 'APP-377', url: '/appSms/listForCompose', featureName: 'List compose sms', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-378', url: '/appSms/updateForCompose', featureName: 'Update compose sms', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-379', url: '/appSms/sendForCompose', featureName: 'Send compose sms', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-380', url: '/appSms/showForSend', featureName: 'Show sent sms', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
            new RequestMap(transactionCode: 'APP-381', url: '/appSms/listForSend', featureName: 'List sent sms', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-382', url: '/appSms/reCompose', featureName: 'Re-Compose sms', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();

            //setting currency
            new RequestMap(transactionCode: 'APP-128', url: '/currency/show', featureName: 'Show currency', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save()
            new RequestMap(transactionCode: 'APP-129', url: '/currency/create', featureName: 'Create currency', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-130', url: '/currency/update', featureName: 'Update currency', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-131', url: '/currency/list', featureName: 'List currency', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-133', url: '/currency/delete', featureName: 'Delete currency', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()

            // Content Category
            new RequestMap(transactionCode: 'APP-138', url: '/contentCategory/show', featureName: 'Show content category', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
            new RequestMap(transactionCode: 'APP-140', url: '/contentCategory/list', featureName: 'List content category', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-141', url: '/contentCategory/update', featureName: 'Update content category', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-142', url: '/contentCategory/create', featureName: 'Create content category', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-143', url: '/contentCategory/delete', featureName: 'Delete content category', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-153', url: '/contentCategory/dropDownContentCategoryReload', featureName: 'Reload Content Category Drop Down', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();

            // AppAttachment
            new RequestMap(transactionCode: 'APP-144', url: '/appAttachment/show', featureName: 'Show attachment', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
            new RequestMap(transactionCode: 'APP-146', url: '/appAttachment/list', featureName: 'List attachment', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-147', url: '/appAttachment/update', featureName: 'Update attachment', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-148', url: '/appAttachment/create', featureName: 'Create attachment', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-149', url: '/appAttachment/delete', featureName: 'Delete attachment', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-152', url: '/appAttachment/downloadContent', featureName: 'Download Attachment', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.TRUE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-465', url: '/appAttachment/downloadImageContent', featureName: 'Download Image Attachment', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.TRUE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-421', url: '/appAttachment/upload', featureName: 'Upload attachment', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();

            // App Note
            new RequestMap(transactionCode: 'APP-245', url: '/appNote/show', featureName: 'Show app note', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
            new RequestMap(transactionCode: 'APP-247', url: '/appNote/list', featureName: 'List app note', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-248', url: '/appNote/update', featureName: 'Update app note', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-249', url: '/appNote/create', featureName: 'Create app note', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-250', url: '/appNote/delete', featureName: 'Delete app note', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-261', url: '/appNote/viewEntityNote', featureName: 'View all app notes', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-345', url: '/appNote/listEntityNote', featureName: 'List all app notes for listview', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();

            // App Faq
            new RequestMap(transactionCode: 'APP-440', url: '/appFaq/show', featureName: 'Show App Faq', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
            new RequestMap(transactionCode: 'APP-441', url: '/appFaq/list', featureName: 'List App Faq', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-442', url: '/appFaq/create', featureName: 'Create App Faq', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-443', url: '/appFaq/update', featureName: 'Update App Faq', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-444', url: '/appFaq/delete', featureName: 'Delete App Faq', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-445', url: '/appFaq/listFaq', featureName: 'List All App Faqs for List View', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-446', url: '/appFaq/reloadFaq', featureName: 'Refresh App Faq TagLib', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // bank
            new RequestMap(transactionCode: 'APP-192', url: '/appBank/show', featureName: 'Show Bank', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-193', url: '/appBank/create', featureName: 'Create Bank', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-194', url: '/appBank/update', featureName: 'Update Bank', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-196', url: '/appBank/list', featureName: 'List Bank', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-197', url: '/appBank/delete', featureName: 'Delete Bank', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-260', url: '/appBank/reloadBankDropDownTagLib', featureName: 'Refresh bank dropDown', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // appBankBranch
            new RequestMap(transactionCode: 'APP-203', url: '/appBankBranch/show', featureName: 'Show bank branch', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-204', url: '/appBankBranch/create', featureName: 'Create bank branch', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-205', url: '/appBankBranch/update', featureName: 'Update bank branch', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-207', url: '/appBankBranch/list', featureName: 'List bank branch', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-208', url: '/appBankBranch/delete', featureName: 'Delete bank branch', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-209', url: '/appBankBranch/reloadBranchesDropDownByBankAndDistrict', featureName: 'Get dropdown branches by bank and district independently', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-210', url: '/appBankBranch/listDistributionPoint', featureName: 'List & Search Distribution Point for Admin & Cashier', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // district
            new RequestMap(transactionCode: 'APP-211', url: '/district/show', featureName: 'Show District', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-212', url: '/district/create', featureName: 'Create District', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-213', url: '/district/update', featureName: 'Update District', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-215', url: '/district/list', featureName: 'List District', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-216', url: '/district/delete', featureName: 'Delete District', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-236', url: '/district/reloadDistrictDropDown', featureName: 'Reload District dropdown independently', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // vehicle
            new RequestMap(transactionCode: 'APP-217', url: '/vehicle/show', featureName: 'Show vehicle', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-219', url: '/vehicle/create', featureName: 'Create vehicle', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-220', url: '/vehicle/update', featureName: 'Update vehicle', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-221', url: '/vehicle/delete', featureName: 'Delete vehicle', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-222', url: '/vehicle/list', featureName: 'List vehicle', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // Vendor
            new RequestMap(transactionCode: 'APP-411', url: '/vendor/show', featureName: 'Show Vendor', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-412', url: '/vendor/list', featureName: 'List Vendor', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-413', url: '/vendor/create', featureName: 'Create Vendor', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-414', url: '/vendor/update', featureName: 'Update Vendor', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-415', url: '/vendor/delete', featureName: 'Delete Vendor', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-416', url: '/vendor/renderVendorThumbImage', featureName: 'Render thumb image', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-417', url: '/vendor/renderVendorSmallImage', featureName: 'Render small image', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // supplier
            new RequestMap(transactionCode: 'APP-223', url: '/supplier/show', featureName: 'Show Supplier', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-224', url: '/supplier/create', featureName: 'Create Supplier', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-226', url: '/supplier/update', featureName: 'Update Supplier', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-227', url: '/supplier/delete', featureName: 'Delete Supplier', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-228', url: '/supplier/list', featureName: 'List Supplier', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // supplier item
            new RequestMap(transactionCode: 'APP-229', url: '/supplierItem/show', featureName: 'Show Supplier-Item', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-230', url: '/supplierItem/create', featureName: 'Create Supplier-Item', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-232', url: '/supplierItem/update', featureName: 'Update Supplier-Item', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-233', url: '/supplierItem/delete', featureName: 'Delete Supplier-Item', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-234', url: '/supplierItem/list', featureName: 'List Supplier-Item', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-235', url: '/supplierItem/dropDownSupplierItemReload', featureName: 'Reload Supplier Item Drop Down', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            //reload app note taglib
            new RequestMap(transactionCode: 'APP-252', url: '/appNote/reloadEntityNote', featureName: 'Refresh app note tagLib', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // benchmark
            new RequestMap(transactionCode: 'APP-263', url: '/benchmark/show', featureName: 'Show Benchmark', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-264', url: '/benchmark/create', featureName: 'Create Benchmark', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-266', url: '/benchmark/update', featureName: 'Update Benchmark', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-267', url: '/benchmark/list', featureName: 'List Benchmark', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-268', url: '/benchmark/execute', featureName: 'Execute Benchmark', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-272', url: '/benchmark/downloadBenchmarkReport', featureName: 'Download Benchmark Report', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-273', url: '/benchmark/showReport', featureName: 'Show Benchmark Report', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-274', url: '/benchmark/showForTruncateSampling', featureName: 'Show sampling domains', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-275', url: '/benchmark/listForTruncateSampling', featureName: 'List sampling domains', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-276', url: '/benchmark/truncateSampling', featureName: 'Truncate sampling domains', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-277', url: '/benchmark/delete', featureName: 'Delete benchmark', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-278', url: '/benchmark/stopBenchMark', featureName: 'Stop benchmark execution', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-269', url: '/benchmark/clear', featureName: 'Clear Benchmark', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // Benchmark Star
            new RequestMap(transactionCode: 'APP-294', url: '/benchmarkStar/show', featureName: 'Show Benchmark Star', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-295', url: '/benchmarkStar/create', featureName: 'Create Benchmark Star', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-297', url: '/benchmarkStar/update', featureName: 'Update Benchmark Star', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-298', url: '/benchmarkStar/list', featureName: 'Update Benchmark Star', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-299', url: '/benchmarkStar/delete', featureName: 'Delete benchmark Star', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-300', url: '/benchmarkStar/showForTruncateSampling', featureName: 'Show sampling domains for Benchmark Star', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-301', url: '/benchmarkStar/listForTruncateSampling', featureName: 'List sampling domains for Benchmark Star', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-302', url: '/benchmarkStar/truncateSampling', featureName: 'Truncate sampling domains for Benchmark Star', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-303', url: '/benchmarkStar/execute', featureName: 'Execute Benchmark Star', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-304', url: '/benchmarkStar/showReport', featureName: 'Show Benchmark Star Report', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-305', url: '/benchmarkStar/downloadBenchmarkReport', featureName: 'Download Benchmark Star Report', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-309', url: '/benchmarkStar/stopBenchMark', featureName: 'Stop benchmark Star execution', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-308', url: '/benchmarkStar/clear', featureName: 'Clear Benchmark Star', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // AppSchedule
            new RequestMap(transactionCode: 'APP-279', url: '/appSchedule/show', featureName: 'Show appSchedule', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-280', url: '/appSchedule/list', featureName: 'List appSchedule', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-282', url: '/appSchedule/update', featureName: 'Update appSchedule', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'APP-283', url: '/appSchedule/testExecute', featureName: 'Test appSchedule', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // DB Instance Request Map
            new RequestMap(transactionCode: 'APP-284', url: '/appDbInstance/show', featureName: 'Show DB Instance', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-285', url: '/appDbInstance/list', featureName: 'List/Search DB Instance', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-410', url: '/appDbInstance/listForDashboard', featureName: 'List/Search DB Instance for DPL Dashboard', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-286', url: '/appDbInstance/create', featureName: 'Create DB Instance', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-287', url: '/appDbInstance/update', featureName: 'Update DB Instance', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-288', url: '/appDbInstance/delete', featureName: 'Delete DB Instance', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-290', url: '/appDbInstance/testDBConnection', featureName: 'Test DB Connection', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-291', url: '/appDbInstance/showResult', featureName: 'Show Query Result', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-330', url: '/appDbInstance/listDbTable', featureName: 'Get List of Table for db instance', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-383', url: '/appDbInstance/dropDownTableColumnReload', featureName: 'Reload Table Column Drop down', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-384', url: '/appDbInstance/dropDownTableReload', featureName: 'Reload Table Drop down', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-393', url: '/appDbInstance/dropDownDbInstanceReload', featureName: 'Reload DB Instance Drop down', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-418', url: '/appDbInstance/connect', featureName: 'Connect DB Instance', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-419', url: '/appDbInstance/disconnect', featureName: 'Disconnect DB Instance', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-420', url: '/appDbInstance/reconnect', featureName: 'Reconnect DB Instance', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()

            //Transaction Log
            new RequestMap(transactionCode: 'APP-312', url: '/transactionLog/show', featureName: 'Show transaction log details', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-313', url: '/transactionLog/list', featureName: 'List transaction log details', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-314', url: '/transactionLog/clear', featureName: 'Clear transaction log details', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // DbInstanceQuery
            new RequestMap(transactionCode: 'APP-317', url: '/dbInstanceQuery/show', featureName: 'Show Query', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-318', url: '/dbInstanceQuery/list', featureName: 'List Query', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-319', url: '/dbInstanceQuery/create', featureName: 'Create Query', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-320', url: '/dbInstanceQuery/showQueryResult', featureName: 'Show Query Result', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-321', url: '/dbInstanceQuery/downloadQueryResultCsv', featureName: 'Download Query Result in CSV', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-322', url: '/dbInstanceQuery/listQueryResult', featureName: 'List Query Result', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-324', url: '/dbInstanceQuery/update', featureName: 'Update Query', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-325', url: '/dbInstanceQuery/delete', featureName: 'Delete Query', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-329', url: '/dbInstanceQuery/execute', featureName: 'Execute Query', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-402', url: '/dbInstanceQuery/downloadQueryResult', featureName: 'Download Query Result', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-464', url: '/dbInstanceQuery/executeQuery', featureName: 'Execute query from create panel', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // AppServerInstance
            new RequestMap(transactionCode: 'APP-354', url: '/appServerInstance/show', featureName: 'Show Server Instance', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-355', url: '/appServerInstance/list', featureName: 'List Server Instance', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-356', url: '/appServerInstance/create', featureName: 'Create Server Instance', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-357', url: '/appServerInstance/update', featureName: 'Update Server Instance', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-358', url: '/appServerInstance/delete', featureName: 'Delete Server Instance', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-394', url: '/appServerInstance/testServerConnection', featureName: 'Test Server Connection', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // AppServerDbInstanceMapping
            new RequestMap(transactionCode: 'APP-359', url: '/appServerDbInstanceMapping/show', featureName: 'Show Server Instance Mapping', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-360', url: '/appServerDbInstanceMapping/list', featureName: 'List Server Instance Mapping', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-361', url: '/appServerDbInstanceMapping/create', featureName: 'Create Server Instance Mapping', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-362', url: '/appServerDbInstanceMapping/update', featureName: 'Update Server Instance Mapping', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-363', url: '/appServerDbInstanceMapping/delete', featureName: 'Delete Server Instance Mapping', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'APP-385', url: '/schemaInformation/listSchemaInfo', featureName: 'List Schema Information', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-386', url: '/testData/list', featureName: 'List Test Data', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-387', url: '/testData/show', featureName: 'Show Test Data', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-388', url: '/testData/create', featureName: 'Create Test Data', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-389', url: '/testData/delete', featureName: 'Delete Test Data', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // app version
            new RequestMap(transactionCode: 'APP-390', url: '/appVersion/show', featureName: 'Show Release History', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-391', url: '/appVersion/list', featureName: 'List Release History', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // AppPage
            new RequestMap(transactionCode: 'APP-451', url: '/appPage/show', featureName: 'Show app page', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-452', url: '/appPage/list', featureName: 'List app page', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-453', url: '/appPage/create', featureName: 'Create app page', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-454', url: '/appPage/update', featureName: 'Update app page', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-455', url: '/appPage/delete', featureName: 'Delete app page', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'APP-456', url: '/appPage/showPage', featureName: 'Delete app page', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Method to create request map for budget
     */
    // last used transaction_code = 'BUDG-104'
    public boolean createRequestMapForBudget() {
        try {
            int count = appVersionService.countByPluginId(BudgPluginConnector.PLUGIN_ID)
            if (count > 0) {
                return true
            }
            executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_viewable,is_common)
            VALUES(-19,0, '/budgPlugin/renderBudgetMenu','ROLE_-2','Budget Module',3,'BUDG-38',FALSE,FALSE)""")

            // BOQ implementation
            new RequestMap(transactionCode: 'BUDG-1', url: '/budgBudget/show', featureName: 'Show Budget', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-2', url: '/budgBudget/create', featureName: 'Create Budget', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-4', url: '/budgBudget/update', featureName: 'Update Budget', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-5', url: '/budgBudget/delete', featureName: 'Delete Budget', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-6', url: '/budgBudget/list', featureName: 'List Budget', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-101', url: '/budgBudget/showForProduction', featureName: 'Show Budget for Production', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();

            new RequestMap(transactionCode: 'BUDG-29', url: '/budgBudget/getBudgetGridByInventory', featureName: 'Get Budget Grid List By Inventory', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-30', url: '/budgBudget/getBudgetListForQs', featureName: 'Get Budget Grid List For QS', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-37', url: '/budgBudget/listBudgetStatus', featureName: 'Get Budget Status list for Dash Board', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-83', url: '/budgBudget/getBudgetGridForSprint', featureName: 'Get Budget List for Sprint', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            //budg task
            new RequestMap(transactionCode: 'BUDG-54', url: '/budgTask/show', featureName: 'Show budget task', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-55', url: '/budgTask/create', featureName: 'Create budget task', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-57', url: '/budgTask/update', featureName: 'Update budget task', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-58', url: '/budgTask/delete', featureName: 'Delete budget task', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-59', url: '/budgTask/list', featureName: 'List budget task', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-85', url: '/budgTask/showTaskForSprint', featureName: 'Show list of budget for task', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-88', url: '/budgTask/listTaskForSprint', featureName: 'Get list of budget for task', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-89', url: '/budgTask/updateTaskForSprint', featureName: 'update task list status', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // BudgetDetails
            new RequestMap(transactionCode: 'BUDG-9', url: '/budgBudgetDetails/show', featureName: 'Show Budget Details', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-10', url: '/budgBudgetDetails/create', featureName: 'Create Budget Details', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-12', url: '/budgBudgetDetails/update', featureName: 'Update Budget Details', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-13', url: '/budgBudgetDetails/delete', featureName: 'Delete Budget Details', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-14', url: '/budgBudgetDetails/list', featureName: 'List Budget Details', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            new RequestMap(transactionCode: 'BUDG-15', url: '/budgBudgetDetails/getItemListBudgetDetails', featureName: 'Get Inventory Item by Budget and Item Type', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-90', url: '/budgBudgetDetails/generateBudgetRequirement', featureName: 'Generate budget requirements according to schema', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();

            // budget schema
            new RequestMap(transactionCode: 'BUDG-60', url: '/budgSchema/show', featureName: 'Show budget schema', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-61', url: '/budgSchema/create', featureName: 'Create budget schema', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-62', url: '/budgSchema/select', featureName: 'Select budget schema', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-63', url: '/budgSchema/update', featureName: 'Update budget schema', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-64', url: '/budgSchema/delete', featureName: 'Delete budget schema', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-65', url: '/budgSchema/list', featureName: 'List budget schema', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-66', url: '/budgSchema/listItemForBudgetSchema', featureName: 'Get list of item for budget schema', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // sprint
            new RequestMap(transactionCode: 'BUDG-67', url: '/budgSprint/show', featureName: 'Show sprint', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-68', url: '/budgSprint/create', featureName: 'Create sprint', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-70', url: '/budgSprint/update', featureName: 'Update sprint', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-71', url: '/budgSprint/delete', featureName: 'Delete sprint', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-72', url: '/budgSprint/list', featureName: 'List sprint', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-84', url: '/budgSprint/setCurrentBudgSprint', featureName: 'Set Current Budget sprint', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-86', url: '/budgSprint/showForCurrentSprint', featureName: 'Show current sprint', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-87', url: '/budgSprint/listForCurrentSprint', featureName: 'List current sprint', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // sprint budget
            new RequestMap(transactionCode: 'BUDG-73', url: '/budgSprintBudget/show', featureName: 'Show sprint budget', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-74', url: '/budgSprintBudget/create', featureName: 'Create sprint budget', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-76', url: '/budgSprintBudget/update', featureName: 'Update sprint budget', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-77', url: '/budgSprintBudget/delete', featureName: 'Delete sprint budget', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-78', url: '/budgSprintBudget/list', featureName: 'List sprint budget', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            //budgBudgetScope
            new RequestMap(transactionCode: 'BUDG-16', url: '/budgBudgetScope/show', featureName: 'Show Budget Scope', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-17', url: '/budgBudgetScope/create', featureName: 'Create Budget Scope', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-18', url: '/budgBudgetScope/select', featureName: 'Select Budget Scope', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-19', url: '/budgBudgetScope/update', featureName: 'Update Budget Scope', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-20', url: '/budgBudgetScope/delete', featureName: 'Delete Budget Scope', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-21', url: '/budgBudgetScope/list', featureName: 'List Budget Scope', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            //BudgProjectBudgetScope
            new RequestMap(transactionCode: 'BUDG-22', url: '/budgProjectBudgetScope/show', featureName: 'Show Project Budget Scope', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-24', url: '/budgProjectBudgetScope/update', featureName: 'Update Project Budget Scope', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-25', url: '/budgProjectBudgetScope/getBudgetScope', featureName: 'Get Budget Scope', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-98', url: '/budgProjectBudgetScope/listAvailableScope', featureName: 'Get available budget scope', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-99', url: '/budgProjectBudgetScope/listAssignedScope', featureName: 'Get assigned budget scope', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // report
            new RequestMap(transactionCode: 'BUDG-26', url: '/budgReport/showBudgetRpt', featureName: 'Show Budget Report', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-27', url: '/budgReport/searchBudgetRpt', featureName: 'Search Budget Report', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-28', url: '/budgReport/downloadBudgetRpt', featureName: 'Download Budget Report', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // project status
            new RequestMap(transactionCode: 'BUDG-31', url: '/budgReport/showProjectStatus', featureName: 'Show Project Status', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-32', url: '/budgReport/searchProjectStatus', featureName: 'Search Project Status', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-36', url: '/budgReport/downloadProjectStatus', featureName: 'Download Project Status', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // project costing
            new RequestMap(transactionCode: 'BUDG-51', url: '/budgReport/listProjectCosting', featureName: 'List Project Costing', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-52', url: '/budgReport/downloadProjectCosting', featureName: 'Download Project Costing', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-53', url: '/budgReport/showProjectCosting', featureName: 'Show Project Costing', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // consumption deviation
            new RequestMap(transactionCode: 'BUDG-40', url: '/budgReport/showConsumptionDeviation', featureName: 'Show Consumption Deviation', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-41', url: '/budgReport/listConsumptionDeviation', featureName: 'List Consumption Deviation', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-42', url: '/budgReport/downloadConsumptionDeviation', featureName: 'Download Consumption Deviation', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-43', url: '/budgReport/downloadConsumptionDeviationCsv', featureName: 'Download Consumption Deviation Csv Report', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // budget sprint
            new RequestMap(transactionCode: 'BUDG-80', url: '/budgReport/downloadSprintReport', featureName: 'Download budget sprint report', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-81', url: '/budgReport/showBudgetSprint', featureName: 'Show budget sprint report', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-82', url: '/budgReport/searchBudgetSprint', featureName: 'Search budget sprint report', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-94', url: '/budgReport/downloadForecastingReport', featureName: 'Download forecasting report for sprint', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // project budget
            new RequestMap(transactionCode: 'BUDG-91', url: '/budgReport/showProjectBudget', featureName: 'Show project budget', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-92', url: '/budgReport/listProjectBudget', featureName: 'List project budget', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-93', url: '/budgReport/downloadProjectBudget', featureName: 'Download project budget', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-100', url: '/budgReport/downloadBudgetByItem', featureName: 'Download Budget List by Item report', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // master budget plan
            new RequestMap(transactionCode: 'BUDG-95', url: '/budgReport/showMasterBudgetPlan', featureName: 'Show master budget plan', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-96', url: '/budgReport/listMasterBudgetPlan', featureName: 'List master budget plan', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'BUDG-97', url: '/budgReport/downloadMasterBudgetPlan', featureName: 'Download master budget plan', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Method to create request map for procurement
     * last used transaction_code = 'PROC-104'
     */
    public boolean createRequestMapForProcurement() {
        try {
            int count = appVersionService.countByPluginId(ProcPluginConnector.PLUGIN_ID)
            if (count > 0) {
                return true
            }
            executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_viewable,is_common)
            VALUES(-16,0, '/procPlugin/renderProcurementMenu','ROLE_-2','Procurement Module',5,'PROC-78',FALSE,FALSE)""")

            // purchaseRequest
            new RequestMap(transactionCode: 'PROC-1', url: '/procPurchaseRequest/show', featureName: 'Show Purchase Request', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-2', url: '/procPurchaseRequest/create', featureName: 'Create Purchase Request', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-4', url: '/procPurchaseRequest/update', featureName: 'Update Purchase Request', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-5', url: '/procPurchaseRequest/delete', featureName: 'Delete Purchase Request', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-6', url: '/procPurchaseRequest/list', featureName: 'List Purchase Request', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-7', url: '/procPurchaseRequest/approve', featureName: 'Approve Purchase Request', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-93', url: '/procPurchaseRequest/sentMailForPRApproval', featureName: 'Send mail for approval of Purchase Request', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // get budget info  for Purchase Request
            new RequestMap(transactionCode: 'PROC-69', url: '/procPurchaseRequest/listIndentByProject', featureName: 'Get Indent List by Project', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-83', url: '/procPurchaseRequest/listUnApprovedPR', featureName: 'List For All Unapproved PR To Show On Dash Board', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-84', url: '/procPurchaseRequest/approvePRDashBoard', featureName: 'Approve PR From Dash Board', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-96', url: '/procPurchaseRequest/unApprovePR', featureName: 'Un Approve Purchase Request', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // purchaseRequestDetails
            new RequestMap(transactionCode: 'PROC-8', url: '/procPurchaseRequestDetails/show', featureName: 'Show Purchase Request Details of Material', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-9', url: '/procPurchaseRequestDetails/create', featureName: 'Create Purchase Request Details of Material', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-11', url: '/procPurchaseRequestDetails/update', featureName: 'Update Purchase Request Details of Material', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-12', url: '/procPurchaseRequestDetails/delete', featureName: 'Delete Purchase Request Details of Material', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-13', url: '/procPurchaseRequestDetails/list', featureName: 'List Purchase Request Details of Material', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-14', url: '/procPurchaseRequestDetails/getItemList', featureName: 'Get Item List for Purchase Request', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // purchaseOrder

            new RequestMap(transactionCode: 'PROC-80', url: '/procPurchaseOrder/approvePODashBoard', featureName: 'Approved PO For Dash Board', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            new RequestMap(transactionCode: 'PROC-15', url: '/procPurchaseOrder/show', featureName: 'Show Purchase Order', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-16', url: '/procPurchaseOrder/create', featureName: 'Create Purchase Order', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-94', url: '/procPurchaseOrder/cancelPO', featureName: 'Cancel Purchase Order', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-18', url: '/procPurchaseOrder/update', featureName: 'Update Purchase Order', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-19', url: '/procPurchaseOrder/delete', featureName: 'Delete Purchase Order', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-20', url: '/procPurchaseOrder/list', featureName: 'List Purchase Order', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-95', url: '/procPurchaseOrder/unApprovePO', featureName: 'Un Approve Purchase Order', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            new RequestMap(transactionCode: 'PROC-22', url: '/procPurchaseOrder/approve', featureName: 'Approve Purchase Order', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-79', url: '/procPurchaseOrder/listUnApprovedPO', featureName: 'List Un-Approved PO', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-90', url: '/procPurchaseOrder/getPOStatusForDashBoard', featureName: 'Get PO Status in Dash Board', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-92', url: '/procPurchaseOrder/sendForPOApproval', featureName: 'Send for PO Approval', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // cancelled PO
            new RequestMap(transactionCode: 'PROC-97', url: '/procCancelledPO/show', featureName: 'Show all cancelled PO', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-98', url: '/procCancelledPO/list', featureName: 'List of all cancelled PO', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // purchaseOrderDetails
            new RequestMap(transactionCode: 'PROC-23', url: '/procPurchaseOrderDetails/show', featureName: 'Show Purchase Order Details', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-24', url: '/procPurchaseOrderDetails/create', featureName: 'Create Purchase Order Details', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-26', url: '/procPurchaseOrderDetails/update', featureName: 'Update Purchase Order Details', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-27', url: '/procPurchaseOrderDetails/delete', featureName: 'Delete Purchase Order Details', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-28', url: '/procPurchaseOrderDetails/list', featureName: 'List Purchase Order Details', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-30', url: '/procPurchaseOrderDetails/getItemListPurchaseOrderDetails', featureName: 'Get item list for purchase order details', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // purchaseRequestReport
            new RequestMap(transactionCode: 'PROC-44', url: '/procReport/showPurchaseRequestRpt', featureName: 'Show Purchase Request Report', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-45', url: '/procReport/searchPurchaseRequestRpt', featureName: 'Search Purchase Request Report', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-46', url: '/procReport/downloadPurchaseRequestRpt', featureName: 'Download Purchase Request Report', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // purchaseOrderReport
            new RequestMap(transactionCode: 'PROC-47', url: '/procReport/showPurchaseOrderRpt', featureName: 'Show Purchase Order Report', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-48', url: '/procReport/searchPurchaseOrderRpt', featureName: 'Search Purchase Order Report', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-49', url: '/procReport/downloadPurchaseOrderRpt', featureName: 'Download Purchase Order Report', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-99', url: '/procReport/showCancelledPORpt', featureName: 'Show Cancelled Purchase Order Report', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-100', url: '/procReport/searchCancelledPORpt', featureName: 'Search Cancelled Purchase Order Report', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-101', url: '/procReport/downloadCancelledPORpt', featureName: 'Download Cancelled Purchase Order Report', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // procTransportCost
            new RequestMap(transactionCode: 'PROC-53', url: '/procTransportCost/show', featureName: 'Show Transport Cost', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-54', url: '/procTransportCost/create', featureName: 'Create Transport Cost', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-56', url: '/procTransportCost/update', featureName: 'Update Transport Cost', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-57', url: '/procTransportCost/delete', featureName: 'Delete Transport Cost', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-58', url: '/procTransportCost/list', featureName: 'List Transport Cost', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // get budget info  for Purchase Request
            new RequestMap(transactionCode: 'PROC-70', url: '/procTermsAndCondition/show', featureName: 'Show Procurement Terms and Condition', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-71', url: '/procTermsAndCondition/create', featureName: 'Create Procurement Terms and Condition', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-73', url: '/procTermsAndCondition/update', featureName: 'Update Procurement Terms and Condition', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-74', url: '/procTermsAndCondition/delete', featureName: 'Delete Procurement Terms and Condition', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-75', url: '/procTermsAndCondition/list', featureName: 'List Procurement Terms and Condition', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // Supplier Wise Purchase Order Report
            new RequestMap(transactionCode: 'PROC-76', url: '/procReport/showSupplierWisePO', featureName: 'Show Supplier wise Purchase Order Report', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-77', url: '/procReport/listSupplierWisePO', featureName: 'List Supplier wise Purchase Order Report', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-82', url: '/procReport/downloadSupplierWisePO', featureName: 'Download Supplier wise Purchase Order Report', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'PROC-91', url: '/procReport/downloadSupplierWisePOCsv', featureName: 'Download Supplier wise Purchase Order Csv Report', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Method to create request map for accounting plugin
     * last used transaction_code = 'ACC-284'
     */
    public boolean createRequestMapForAccounting() {
        try {
            int count = appVersionService.countByPluginId(AccPluginConnector.PLUGIN_ID)
            if (count > 0) {
                return true
            }
            executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_viewable,is_common)
            VALUES(-18,0, '/accPlugin/renderAccountingMenu','ROLE_-2','Accounting Module',2,'ACC-157',FALSE,FALSE)""")

            // Acc Custom Group
            new RequestMap(transactionCode: 'ACC-1', url: '/accCustomGroup/show', featureName: 'Show Custom Group', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-2', url: '/accCustomGroup/create', featureName: 'Create Custom Group', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-4', url: '/accCustomGroup/update', featureName: 'Update Custom Group', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-5', url: '/accCustomGroup/delete', featureName: 'Delete Custom Group', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-6', url: '/accCustomGroup/list', featureName: 'List Custom Group', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // Acc Chart of Account
            new RequestMap(transactionCode: 'ACC-7', url: '/accChartOfAccount/show', featureName: 'Show Chart of Account', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-8', url: '/accChartOfAccount/create', featureName: 'Create Chart of Account', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-9', url: '/accChartOfAccount/select', featureName: 'Select Chart of Account', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-10', url: '/accChartOfAccount/update', featureName: 'Update Chart of Account', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-11', url: '/accChartOfAccount/delete', featureName: 'Delete Chart of Account', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-12', url: '/accChartOfAccount/list', featureName: 'List Chart of Account', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-148', url: '/accChartOfAccount/listSourceCategoryByAccSource', featureName: 'Get Source-Category By AccSourceId', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-13', url: '/accChartOfAccount/listForVoucher', featureName: 'List For Voucher', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-14', url: '/accChartOfAccount/listSourceByCoaCode', featureName: 'Get source list by acc-chart-of-account code', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-147', url: '/accChartOfAccount/listAccChartOfAccountByAccGroupId', featureName: 'List Of COA By Group Id', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // Acc Group
            new RequestMap(transactionCode: 'ACC-15', url: '/accGroup/show', featureName: 'Show Account Group', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-16', url: '/accGroup/create', featureName: 'Create Account Group', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-18', url: '/accGroup/update', featureName: 'Update Account Group', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-19', url: '/accGroup/delete', featureName: 'Delete Account Group', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-20', url: '/accGroup/list', featureName: 'List Account Group', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // AccTier1
            new RequestMap(transactionCode: 'ACC-21', url: '/accTier1/show', featureName: 'Show Tier 1', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-22', url: '/accTier1/create', featureName: 'Create Tier 1', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-24', url: '/accTier1/update', featureName: 'Update Tier 1', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-25', url: '/accTier1/delete', featureName: 'Delete Tier 1', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-26', url: '/accTier1/list', featureName: 'List Tier 1', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-27', url: '/accTier1/listTier1ByAccTypeId', featureName: 'Get Tier 1 List by Account Type Id', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // AccTier3
            new RequestMap(transactionCode: 'ACC-28', url: '/accTier3/show', featureName: 'Show Tier 3', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-29', url: '/accTier3/create', featureName: 'Create Tier 3', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-31', url: '/accTier3/update', featureName: 'Update Tier 3', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-32', url: '/accTier3/delete', featureName: 'Delete Tier 3', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-33', url: '/accTier3/list', featureName: 'List Tier 3', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            new RequestMap(transactionCode: 'ACC-34', url: '/accTier3/listTier3ByAccTier2Id', featureName: 'Populate Tier3 for Tier2', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // AccTier2
            new RequestMap(transactionCode: 'ACC-35', url: '/accTier2/show', featureName: 'Show Tier 2', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-36', url: '/accTier2/create', featureName: 'Create Tier 2', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-38', url: '/accTier2/update', featureName: 'Update Tier 2', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-39', url: '/accTier2/delete', featureName: 'Delete Tier 2', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-40', url: '/accTier2/list', featureName: 'List Tier 2', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-41', url: '/accTier2/listTier2ByAccTier1Id', featureName: 'Get Tier 2 List by Tier 1 Id', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // AccIpc
            new RequestMap(transactionCode: 'ACC-185', url: '/accIpc/show', featureName: 'Show IPC', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-186', url: '/accIpc/create', featureName: 'Create IPC', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-188', url: '/accIpc/update', featureName: 'Update IPC', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-189', url: '/accIpc/delete', featureName: 'Delete IPC', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-190', url: '/accIpc/list', featureName: 'List IPC', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // AccType
            new RequestMap(transactionCode: 'ACC-206', url: '/accType/show', featureName: 'Show Account Type', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-207', url: '/accType/list', featureName: 'List Account Type', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-209', url: '/accType/update', featureName: 'Update Account Type', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // AccLc
            new RequestMap(transactionCode: 'ACC-191', url: '/accLc/show', featureName: 'Show LC', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-192', url: '/accLc/create', featureName: 'Create LC', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-193', url: '/accLc/select', featureName: 'Select LC', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-194', url: '/accLc/update', featureName: 'Update LC', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-195', url: '/accLc/delete', featureName: 'Delete LC', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-196', url: '/accLc/list', featureName: 'List LC', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // AccLeaseAccount
            new RequestMap(transactionCode: 'ACC-198', url: '/accLeaseAccount/show', featureName: 'Show Lease Account', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-199', url: '/accLeaseAccount/create', featureName: 'Create Lease Account', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-201', url: '/accLeaseAccount/update', featureName: 'Update Lease Account', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-202', url: '/accLeaseAccount/delete', featureName: 'Delete Lease Account', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-203', url: '/accLeaseAccount/list', featureName: 'List Lease Account', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // Voucher-Type implementation
            new RequestMap(transactionCode: 'ACC-42', url: '/accVoucherTypeCoa/show', featureName: 'Show Acc Voucher Type Coa', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'ACC-43', url: '/accVoucherTypeCoa/create', featureName: 'Create Acc Voucher Type Coa', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'ACC-45', url: '/accVoucherTypeCoa/update', featureName: 'Update Acc Voucher Type Coa', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'ACC-46', url: '/accVoucherTypeCoa/delete', featureName: 'Delete Acc Voucher Type Coa', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'ACC-47', url: '/accVoucherTypeCoa/list', featureName: 'List Acc Voucher Type Coa', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // AccSubAccount implementation
            new RequestMap(transactionCode: 'ACC-48', url: '/accSubAccount/show', featureName: 'Show Sub Account', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'ACC-49', url: '/accSubAccount/create', featureName: 'Create Sub Account', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'ACC-51', url: '/accSubAccount/update', featureName: 'Update Sub Account', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'ACC-52', url: '/accSubAccount/delete', featureName: 'Delete Sub Account', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'ACC-53', url: '/accSubAccount/list', featureName: 'List Sub Account', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'ACC-54', url: '/accSubAccount/listSubAccountByCoaId', featureName: 'List By Chart Of Accounts', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // AccDivision implementation
            new RequestMap(transactionCode: 'ACC-55', url: '/accDivision/show', featureName: 'Show Division', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'ACC-56', url: '/accDivision/create', featureName: 'Create Division', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'ACC-58', url: '/accDivision/update', featureName: 'Update Division', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'ACC-59', url: '/accDivision/delete', featureName: 'Delete Division', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'ACC-60', url: '/accDivision/list', featureName: 'List Division', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'ACC-61', url: '/accDivision/listDivisionListByProjectId', featureName: 'Get Division List By Project', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // voucher
            new RequestMap(transactionCode: 'ACC-62', url: '/accVoucher/show', featureName: 'Show Acc Voucher', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'ACC-63', url: '/accVoucher/create', featureName: 'Create Acc Voucher', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'ACC-251', url: '/accVoucher/cancelVoucher', featureName: 'Cancel Acc Voucher', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'ACC-64', url: '/accVoucher/select', featureName: 'Select Acc Voucher', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'ACC-65', url: '/accVoucher/update', featureName: 'Update Acc Voucher', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'ACC-66', url: '/accVoucher/list', featureName: 'List Acc Voucher', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'ACC-67', url: '/accVoucher/postVoucher', featureName: 'Post Acc Voucher', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'ACC-68', url: '/accVoucher/showPayCash', featureName: 'Show Pay Cash', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'ACC-69', url: '/accVoucher/listPayCash', featureName: 'List Pay Cash', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'ACC-70', url: '/accVoucher/showPayBank', featureName: 'Show Pay Bank', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'ACC-71', url: '/accVoucher/listPayBank', featureName: 'List Pay Bank', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'ACC-72', url: '/accVoucher/showReceiveCash', featureName: 'Show Receive Cash', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'ACC-73', url: '/accVoucher/listReceiveCash', featureName: 'List Receive Cash', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'ACC-74', url: '/accVoucher/showReceiveBank', featureName: 'Show Receive Bank', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'ACC-75', url: '/accVoucher/listReceiveBank', featureName: 'List Receive Bank', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'ACC-76', url: '/accReport/downloadChartOfAccounts', featureName: 'Download Chart Of Accounts', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            new RequestMap(transactionCode: 'ACC-152', url: '/accVoucher/listOfUnApprovedPayCash', featureName: 'List Of UnApproved Pay Cash To Show On Dash Board', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'ACC-153', url: '/accVoucher/listOfUnApprovedPayBank', featureName: 'List Of UnApproved Pay Bank To Show On Dash Board', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'ACC-154', url: '/accVoucher/listOfUnApprovedReceiveCash', featureName: 'List Of UnApproved Receive Cash To Show On Dash Board', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'ACC-155', url: '/accVoucher/listOfUnApprovedReceiveBank', featureName: 'List Of UnApproved Receive Bank To Show On Dash Board', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'ACC-156', url: '/accVoucher/listOfUnApprovedJournal', featureName: 'List Of UnApproved Journal To Show On Dash Board', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //Acc cancelled voucher
            new RequestMap(transactionCode: 'ACC-261', url: '/accCancelledVoucher/showCancelledVoucher', featureName: 'Show Cancelled voucher', configAttribute: 'ROLE_-2', pluginId: 2, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
            new RequestMap(transactionCode: 'ACC-262', url: '/accCancelledVoucher/listCancelledVoucher', featureName: 'List Cancelled voucher', configAttribute: 'ROLE_-2', pluginId: 2, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();

            // voucher report
            new RequestMap(transactionCode: 'ACC-77', url: '/accReport/showVoucher', featureName: 'Show Voucher Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-78', url: '/accReport/searchVoucher', featureName: 'Search Voucher Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-79', url: '/accReport/downloadVoucher', featureName: 'Download Voucher Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-129', url: '/accReport/downloadVoucherBankCheque', featureName: 'Download Voucher Bank Cheque Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-130', url: '/accReport/downloadVoucherBankChequePreview', featureName: 'Download Voucher Bank Cheque Preview Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-257', url: '/accReport/sendMailForUnpostedVoucher', featureName: 'Send mail for un-posted voucher report', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-263', url: '/accReport/showCancelledVoucher', featureName: 'Show Cancelled Voucher Report', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-264', url: '/accReport/searchCancelledVoucher', featureName: 'Search Cancelled Voucher Report', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-265', url: '/accReport/downloadCancelledVoucher', featureName: 'Download Cancelled Voucher Report', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // voucherList
            new RequestMap(transactionCode: 'ACC-80', url: '/accReport/showVoucherList', featureName: 'Show Voucher List Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-81', url: '/accReport/searchVoucherList', featureName: 'Search Voucher List Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-82', url: '/accReport/downloadVoucherList', featureName: 'Download Voucher List Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-83', url: '/accReport/listForVoucherDetails', featureName: 'Show Voucher Details List Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // ledger
            new RequestMap(transactionCode: 'ACC-84', url: '/accReport/showLedger', featureName: 'Show Ledger Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-85', url: '/accReport/listLedger', featureName: 'List Ledger Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-86', url: '/accReport/downloadLedger', featureName: 'Download Ledger Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-175', url: '/accReport/downloadLedgerCsv', featureName: 'Download Ledger CSV Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // sourceLedger
            new RequestMap(transactionCode: 'ACC-87', url: '/accReport/showSourceLedger', featureName: 'Show Source Ledger Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-88', url: '/accReport/listSourceLedger', featureName: 'List Source Ledger Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-89', url: '/accReport/downloadSourceLedger', featureName: 'Download Source Ledger Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-174', url: '/accReport/downloadSourceLedgerCsv', featureName: 'Download Source Ledger CSV Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-149', url: '/accReport/listSourceByCategoryAndType', featureName: 'List Source By Type and Category', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-215', url: '/accReport/listSourceCategoryForSourceLedger', featureName: 'List Source Category for Source Ledger', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-259', url: '/accReport/downloadSourceLedgeReportGroupBySource', featureName: 'Download Source Ledger Report Group By Source', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // groupLedger
            new RequestMap(transactionCode: 'ACC-90', url: '/accReport/showForGroupLedgerRpt', featureName: 'Show Group Ledger Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-91', url: '/accReport/listForGroupLedgerRpt', featureName: 'List Group Ledger Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-92', url: '/accReport/downloadForGroupLedgerRpt', featureName: 'Download Group Ledger Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-171', url: '/accReport/downloadForGroupLedgerCsvRpt', featureName: 'Download Group Ledger CSV Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // Level-2 trialBalance
            new RequestMap(transactionCode: 'ACC-247', url: '/accReport/showTrialBalanceOfLevel2', featureName: 'Show level 2 trial balance report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-248', url: '/accReport/listTrialBalanceOfLevel2', featureName: 'List level 2 trial balance report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-249', url: '/accReport/downloadTrialBalanceOfLevel2', featureName: 'Download level 2 trial balance report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-250', url: '/accReport/downloadTrialBalanceCsvOfLevel2', featureName: 'Download level 2 trial balance Csv report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // Level-3 trialBalance
            new RequestMap(transactionCode: 'ACC-216', url: '/accReport/showTrialBalanceOfLevel3', featureName: 'Show level 3 trial balance report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-217', url: '/accReport/listTrialBalanceOfLevel3', featureName: 'List level 3 trial balance report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-218', url: '/accReport/downloadTrialBalanceOfLevel3', featureName: 'Download level 3 trial balance report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-219', url: '/accReport/downloadTrialBalanceCsvOfLevel3', featureName: 'Download level 3 trial balance Csv report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // Level-4 trialBalance
            new RequestMap(transactionCode: 'ACC-220', url: '/accReport/showTrialBalanceOfLevel4', featureName: 'Show level 4 trial balance report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-221', url: '/accReport/listTrialBalanceOfLevel4', featureName: 'List level 4 trial balance report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-222', url: '/accReport/downloadTrialBalanceOfLevel4', featureName: 'Download level 4 trial balance report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-223', url: '/accReport/downloadTrialBalanceCsvOfLevel4', featureName: 'Download level 4 trial balance Csv report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // Level-5 trialBalance
            new RequestMap(transactionCode: 'ACC-224', url: '/accReport/showTrialBalanceOfLevel5', featureName: 'Show level 5 trial balance report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-225', url: '/accReport/listTrialBalanceOfLevel5', featureName: 'List level 5 trial balance report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-226', url: '/accReport/downloadTrialBalanceOfLevel5', featureName: 'Download level 5 trial balance report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-227', url: '/accReport/downloadTrialBalanceCsvOfLevel5', featureName: 'Download level 5 trial balance Csv report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            new RequestMap(transactionCode: 'ACC-96', url: '/accVoucher/unPostedVoucher', featureName: 'Un-Post Acc Voucher', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // supplier payment report
            new RequestMap(transactionCode: 'ACC-97', url: '/accReport/showSupplierWisePayment', featureName: 'Show Supplier Purchase Order report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-98', url: '/accReport/listSupplierWisePayment', featureName: 'List Supplier Purchase Order report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-99', url: '/accReport/downloadSupplierWisePayment', featureName: 'Download Supplier Purchase Order report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-173', url: '/accReport/downloadSupplierWisePaymentCsv', featureName: 'Download Supplier Purchase Order CSV report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // incomeStatement level 4
            new RequestMap(transactionCode: 'ACC-235', url: '/accReport/showIncomeStatementOfLevel4', featureName: 'Show income statement report of level 4', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-236', url: '/accReport/listIncomeStatementOfLevel4', featureName: 'List income statement report of level 4', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-237', url: '/accReport/downloadIncomeStatementOfLevel4', featureName: 'Download income statement report of level 4', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-238', url: '/accReport/downloadIncomeStatementCsvOfLevel4', featureName: 'Download income statement CSV report of level 4', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // incomeStatement level 5
            new RequestMap(transactionCode: 'ACC-239', url: '/accReport/showIncomeStatementOfLevel5', featureName: 'Show income statement report of level 5', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-240', url: '/accReport/listIncomeStatementOfLevel5', featureName: 'List income statement report of level 5', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-241', url: '/accReport/downloadIncomeStatementOfLevel5', featureName: 'Download income statement report of level 5', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-242', url: '/accReport/downloadIncomeStatementCsvOfLevel5', featureName: 'Download income statement CSV report of level 5', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // financialStatement level 5
            new RequestMap(transactionCode: 'ACC-210', url: '/accReport/showFinancialStatementOfLevel5', featureName: 'Show financial statement of level 5', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-211', url: '/accReport/listFinancialStatementOfLevel5', featureName: 'List financial statement of level 5', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-212', url: '/accReport/downloadFinancialStatementOfLevel5', featureName: 'Download financial statement of level 5', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-234', url: '/accReport/downloadFinancialStatementCsvOfLevel5', featureName: 'Download financial statement csv of level 5', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // financialStatement level 4
            new RequestMap(transactionCode: 'ACC-213', url: '/accReport/showFinancialStatementOfLevel4', featureName: 'Show financial statement of level 4', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-214', url: '/accReport/listFinancialStatementOfLevel4', featureName: 'List financial statement of level 4', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-228', url: '/accReport/downloadFinancialStatementOfLevel4', featureName: 'Download financial statement of level 4', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-233', url: '/accReport/downloadFinancialStatementCsvOfLevel4', featureName: 'Download financial statement csv of level 4', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // financialStatement level 3
            new RequestMap(transactionCode: 'ACC-229', url: '/accReport/showFinancialStatementOfLevel3', featureName: 'Show financial statement of level 3', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-230', url: '/accReport/listFinancialStatementOfLevel3', featureName: 'List financial statement of level 3', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-231', url: '/accReport/downloadFinancialStatementOfLevel3', featureName: 'Download financial statement of level 3', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-232', url: '/accReport/downloadFinancialStatementCsvOfLevel3', featureName: 'Download financial statement csv of level 3', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // financialStatement level 2
            new RequestMap(transactionCode: 'ACC-243', url: '/accReport/showFinancialStatementOfLevel2', featureName: 'Show financial statement of level 2', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-244', url: '/accReport/listFinancialStatementOfLevel2', featureName: 'List financial statement of level 2', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-245', url: '/accReport/downloadFinancialStatementOfLevel2', featureName: 'Download financial statement of level 2', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-246', url: '/accReport/downloadFinancialStatementCsvOfLevel2', featureName: 'Download financial statement csv of level 2', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            //project wise expense
            new RequestMap(transactionCode: 'ACC-106', url: '/accReport/showProjectWiseExpense', featureName: 'Show project wise expense ', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-107', url: '/accReport/listProjectWiseExpense', featureName: 'List project wise expense ', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-108', url: '/accReport/listProjectWiseExpenseDetails', featureName: 'List project wise expense details ', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-109', url: '/accReport/downloadProjectWiseExpense', featureName: 'Download project wise expense', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-179', url: '/accReport/downloadProjectWiseExpenseCsv', featureName: 'Download project wise expense CSV report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-256', url: '/accReport/sendMailForProjectWiseExpense', featureName: 'Send mail for Project wise expense', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            //project fund flow
            new RequestMap(transactionCode: 'ACC-253', url: '/accReport/downloadProjectFundFlowReport', featureName: 'Download project fund flow report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-254', url: '/accReport/showProjectFundFlowReport', featureName: 'Show project fund flow', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-255', url: '/accReport/listProjectFundFlowReport', featureName: 'List project fund flow ', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // source wise balance
            new RequestMap(transactionCode: 'ACC-111', url: '/accReport/showSourceWiseBalance', featureName: 'Show Source Balance report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-112', url: '/accReport/listSourceWiseBalance', featureName: 'List Source Balance report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-113', url: '/accReport/downloadSourceWiseBalance', featureName: 'Download Source Wise Balance report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-180', url: '/accReport/downloadSourceWiseBalanceCsv', featureName: 'Download Source Wise Balance Csv report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-205', url: '/accReport/downloadVoucherListBySourceId', featureName: 'Download Voucher List By SourceId', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-260', url: '/accReport/listSourceCategoryForSourceWiseBalance', featureName: 'List Source Category for Source wise balance', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // indent report
            new RequestMap(transactionCode: 'ACC-279', url: '/accReport/showIndentRpt', featureName: 'Show Indent Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-280', url: '/accReport/searchIndentRpt', featureName: 'Search Indent Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-281', url: '/accReport/downloadIndentRpt', featureName: 'Download Indent Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            //Acc IOU Slip Report
            new RequestMap(transactionCode: 'ACC-141', url: '/accReport/showAccIouSlipRpt', featureName: 'Show Acc Iou Slip', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-142', url: '/accReport/listAccIouSlipRpt', featureName: 'List Acc Iou Slip', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-143', url: '/accReport/downloadAccIouSlipRpt', featureName: 'Download Acc Iou Slip', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // get total of voucher
            new RequestMap(transactionCode: 'ACC-160', url: '/accReport/retrieveTotalOfPayCash', featureName: 'Get total amount of posted Pay Cash Voucher', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-161', url: '/accReport/retrieveTotalOfPayCheque', featureName: 'Get total amount of posted Pay Cheque Voucher', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-162', url: '/accReport/retrieveTotalOfPayChequeOnChequeDate', featureName: 'Get total amount of posted Pay Cheque Voucher on cheque date', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // accIndent
            new RequestMap(transactionCode: 'ACC-266', url: '/accIndent/show', featureName: 'Show Indent', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'ACC-267', url: '/accIndent/create', featureName: 'Create Indent', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'ACC-268', url: '/accIndent/update', featureName: 'Update Indent', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'ACC-269', url: '/accIndent/delete', featureName: 'Delete Indent', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'ACC-270', url: '/accIndent/list', featureName: 'List Indent', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'ACC-271', url: '/accIndent/approve', featureName: 'Approve Indent', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'ACC-272', url: '/accIndent/listOfUnApprovedIndent', featureName: 'List Of Unapproved Indent at Dash Board', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'ACC-273', url: '/accIndent/approveIndentDashBoard', featureName: 'Approve Indent at DashBoard', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // indentDetails
            new RequestMap(transactionCode: 'ACC-274', url: '/accIndentDetails/show', featureName: 'Show Indent Details', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-275', url: '/accIndentDetails/create', featureName: 'Create Indent Details', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-276', url: '/accIndentDetails/update', featureName: 'Update Indent Details', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-277', url: '/accIndentDetails/delete', featureName: 'Delete Indent Details', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-278', url: '/accIndentDetails/list', featureName: 'List Indent Details', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            //For Acc-Iou-Slip
            new RequestMap(transactionCode: 'ACC-121', url: '/accIouSlip/show', featureName: 'Show IOU Slip', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-122', url: '/accIouSlip/create', featureName: 'Create IOU Slip', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-124', url: '/accIouSlip/update', featureName: 'Update IOU Slip', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-125', url: '/accIouSlip/delete', featureName: 'Delete IOU Slip', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-126', url: '/accIouSlip/list', featureName: 'List IOU Slip', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-127', url: '/accIouSlip/sentNotification', featureName: 'Send for Approval of IOU Slip', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-128', url: '/accIouSlip/approve', featureName: 'Approve IOU Slip', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-151', url: '/accIndent/listIndentByProject', featureName: 'Get Indent List for IOU Slip', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            //For Acc-Iou-Purpose
            new RequestMap(transactionCode: 'ACC-131', url: '/accIouPurpose/show', featureName: 'Show For Acc Iou Purpose', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-132', url: '/accIouPurpose/create', featureName: 'Create For Acc Iou Purpose', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-133', url: '/accIouPurpose/dropDownPurposeReload', featureName: 'Reload purpose drop down for Acc Iou Purpose', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-134', url: '/accIouPurpose/update', featureName: 'Update For Acc Iou Purpose', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-135', url: '/accIouPurpose/delete', featureName: 'Delete For Acc Iou Purpose', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-136', url: '/accIouPurpose/list', featureName: 'List For Acc Iou Purpose', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            //acc financial year
            new RequestMap(transactionCode: 'ACC-114', url: '/accFinancialYear/show', featureName: 'Show For Acc Financial Year', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-115', url: '/accFinancialYear/list', featureName: 'List For Acc Financial Year', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-116', url: '/accFinancialYear/create', featureName: 'Create For Acc Financial Year', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-117', url: '/accFinancialYear/update', featureName: 'Update For Acc Financial Year', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-118', url: '/accFinancialYear/delete', featureName: 'Delete For Acc Financial Year', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-150', url: '/accFinancialYear/setCurrentFinancialYear', featureName: 'Set Current  Financial Year', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // supplier payable report
            new RequestMap(transactionCode: 'ACC-137', url: '/accReport/showSupplierWisePayable', featureName: 'Show Supplier Wise Payable Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-138', url: '/accReport/listSupplierWisePayable', featureName: 'List Supplier Wise Payable Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-139', url: '/accReport/downloadSupplierWisePayable', featureName: 'Download Supplier Wise Payable Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-172', url: '/accReport/downloadSupplierWisePayableCsv', featureName: 'Download Supplier Wise Payable CSV Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-258', url: '/accReport/sendMailForSupplierWisePayable', featureName: 'Send mail for Supplier wise payable report', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // trialBalance
            new RequestMap(transactionCode: 'ACC-168', url: '/accReport/showCustomGroupBalance', featureName: 'Show Custom Group Balance report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-169', url: '/accReport/listCustomGroupBalance', featureName: 'List Custom Group Balance report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-170', url: '/accReport/downloadCustomGroupBalance', featureName: 'Download Custom Group Balance report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'ACC-176', url: '/accReport/downloadCustomGroupBalanceCsv', featureName: 'Download Custom Group Balance Csv report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createRequestMapForQs() {
        try {
            int count = appVersionService.countByPluginId(QsPluginConnector.PLUGIN_ID)
            if (count > 0) {
                return true
            }
            executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_viewable,is_common)
            VALUES(-20,0, '/qsPlugin/renderQSMenu','ROLE_-2','QS Module',6,'QS-41',FALSE,FALSE)""")

            //qs Measurement
            new RequestMap(transactionCode: 'QS-1', url: '/qsMeasurement/show', featureName: 'Show Qs Measurement', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'QS-2', url: '/qsMeasurement/create', featureName: 'Create Qs Measurement', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'QS-4', url: '/qsMeasurement/update', featureName: 'Update Qs Measurement', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'QS-5', url: '/qsMeasurement/delete', featureName: 'Delete Qs Measurement', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'QS-6', url: '/qsMeasurement/list', featureName: 'List Qs Measurement', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'QS-7', url: '/qsMeasurement/showGovt', featureName: 'Show Qs Measurement(Govt)', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'QS-40', url: '/qsMeasurement/listQsStatusForDashBoard', featureName: 'Show Qs Status in Dash Board', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            //budget Contract details report
            new RequestMap(transactionCode: 'QS-15', url: '/qsReport/showBudgetContractDetails', featureName: 'Show Budget Contract Details', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'QS-16', url: '/qsReport/listBudgetContractDetails', featureName: 'List Budget Contract Details', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'QS-23', url: '/qsReport/downloadBudgetContractDetails', featureName: 'Download Budget Contract Details', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'QS-42', url: '/qsReport/downloadBudgetContractCsvDetails', featureName: 'Download Budget Contract CSV Details', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            //budget financial summary report
            new RequestMap(transactionCode: 'QS-17', url: '/qsReport/showBudgetFinancialSummary', featureName: 'Show Budget Financial Summary', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'QS-18', url: '/qsReport/listBudgetFinancialSummary', featureName: 'List Budget Financial Summary', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'QS-24', url: '/qsReport/downloadBudgetFinancialSummary', featureName: 'Download Budget Financial Summary', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'QS-43', url: '/qsReport/downloadBudgetFinancialCsvSummary', featureName: 'Download Budget Financial Summary CSV Report', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            //get Budget for QS
            new RequestMap(transactionCode: 'QS-19', url: '/qsMeasurement/getBudgetForQS', featureName: 'Get budget for QS', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            //QS Measurement report
            new RequestMap(transactionCode: 'QS-13', url: '/qsReport/showQsMeasurementRpt', featureName: 'Show Qs-Measurement Report', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'QS-14', url: '/qsReport/listQsMeasurementRpt', featureName: 'List Qs-Measurement Report', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'QS-33', url: '/qsReport/downloadQsMeasurementRpt', featureName: 'Download Qs-Measurement Report', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'QS-45', url: '/qsReport/downloadQsMeasurementCsvRpt', featureName: 'Download Qs-Measurement CSV Report', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            //budget wise qs report
            new RequestMap(transactionCode: 'QS-20', url: '/qsReport/showBudgetWiseQs', featureName: 'Show Budget Wise Qs Report', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'QS-21', url: '/qsReport/listBudgetWiseQs', featureName: 'List Budget Wise Qs Report', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'QS-22', url: '/qsReport/downloadBudgetWiseQs', featureName: 'Download Budget Wise Qs Report', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'QS-46', url: '/qsReport/downloadBudgetWiseQsCsv', featureName: 'Download Budget Wise Qs Report', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            //Combined QS Measurement Report
            new RequestMap(transactionCode: 'QS-30', url: '/qsReport/showCombinedQSM', featureName: 'Show Combined QS Measurement Report', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'QS-31', url: '/qsReport/listCombinedQSM', featureName: 'List Combined QS Measurement Report', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'QS-32', url: '/qsReport/downloadCombinedQSM', featureName: 'Download Combined QS Measurement Report', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'QS-44', url: '/qsReport/downloadCombinedQSMCsv', featureName: 'Download Combined QS Measurement CSV Report', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    // last used transaction_code = 'INV-242'
    public boolean createRequestMapForInventory() {
        try {
            int count = appVersionService.countByPluginId(InvPluginConnector.PLUGIN_ID)
            if (count > 0) {
                return true
            }
            executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_viewable,is_common)
            VALUES(-17,0, '/invPlugin/renderInventoryMenu','ROLE_-2','Inventory Module',4,'INV-150',FALSE,FALSE)""")

            //For Dash Board
            new RequestMap(transactionCode: 'INV-151', url: '/invInventoryTransaction/listOfUnApprovedConsumption', featureName: 'List For All Unapproved Consumption To Show On Dash Board', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-186', url: '/invInventoryTransaction/listOfUnApprovedInFromSupplier', featureName: 'List For All Unapproved In from Supplier To Show On Dash Board', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-187', url: '/invInventoryTransaction/listOfUnApprovedInventoryOut', featureName: 'List For All Unapproved Inventory Out To Show On Dash Board', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-188', url: '/invInventoryTransaction/listOfUnApprovedInFromInventory', featureName: 'List For All Unapproved Inventory In From Inventory To Show On Dash Board', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            //For inv inventory
            new RequestMap(transactionCode: 'INV-1', url: '/invInventory/show', featureName: 'Show inventory', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-3', url: '/invInventory/create', featureName: 'Create inventory', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-4', url: '/invInventory/update', featureName: 'Update inventory', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-5', url: '/invInventory/delete', featureName: 'Delete inventory', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-6', url: '/invInventory/list', featureName: 'List inventory', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-238', url: '/invReport/downloadUserInventory', featureName: 'Download user inventory list report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            //-------------- > For Inventory Transaction < --------------\\

            //for sending mail for all pending Inventory Transaction
            new RequestMap(transactionCode: 'INV-231', url: '/invInventoryTransaction/sendMailForInventoryTransaction', featureName: 'Send mail for all pending inventory transaction', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();

            //for Inventory Transaction-in (from supplier)
            new RequestMap(transactionCode: 'INV-32', url: '/invInventoryTransaction/showInventoryInFromSupplier', featureName: 'Show Inventory-In', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-33', url: '/invInventoryTransaction/createInventoryInFromSupplier', featureName: 'Create Inventory-In', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-35', url: '/invInventoryTransaction/updateInventoryInFromSupplier', featureName: 'Update Inventory-In', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-36', url: '/invInventoryTransaction/deleteInventoryInFromSupplier', featureName: 'Delete Inventory-In', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-37', url: '/invInventoryTransaction/listInventoryInFromSupplier', featureName: 'List Inventory-In', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            new RequestMap(transactionCode: 'INV-38', url: '/invInventoryTransaction/listInventoryByType', featureName: 'Get Inventory List By Type', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-183', url: '/invInventoryTransaction/listInventoryByTypeAndProject', featureName: 'Get Inventory List By Type And Project', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-217', url: '/invInventoryTransaction/listFixedAssetByItemAndProject', featureName: 'Get Fixed Asset List By Item And Project', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-39', url: '/invInventoryTransaction/listPOBySupplier', featureName: 'Get PO List Of Supplier', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            //for Inventory Transaction-in (from Inventory)
            new RequestMap(transactionCode: 'INV-40', url: '/invInventoryTransaction/showInventoryInFromInventory', featureName: 'Show Inventory-In From Inventory', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-41', url: '/invInventoryTransaction/createInventoryInFromInventory', featureName: 'Create Inventory-In From Inventory', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-42', url: '/invInventoryTransaction/selectInventoryInFromInventory', featureName: 'Select Inventory-In From Inventory', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-43', url: '/invInventoryTransaction/updateInventoryInFromInventory', featureName: 'Update Inventory-In From Inventory', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-44', url: '/invInventoryTransaction/deleteInventoryInFromInventory', featureName: 'Delete Inventory-In From Inventory', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-45', url: '/invInventoryTransaction/listInventoryInFromInventory', featureName: 'List Inventory-In From Inventory', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            new RequestMap(transactionCode: 'INV-46', url: '/invInventoryTransaction/listInventoryOfTransactionOut', featureName: 'Get Inventory Transaction Out List', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-47', url: '/invInventoryTransaction/listInvTransaction', featureName: 'Get Inventory Transaction List', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            //for Inventory Consumption
            new RequestMap(transactionCode: 'INV-48', url: '/invInventoryTransaction/showInventoryConsumption', featureName: 'Show Inventory-Consumption Transaction', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-49', url: '/invInventoryTransaction/createInventoryConsumption', featureName: 'Create Inventory-Consumption Transaction', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-51', url: '/invInventoryTransaction/updateInventoryConsumption', featureName: 'Update Inventory-Consumption Transaction', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-52', url: '/invInventoryTransaction/deleteInventoryConsumption', featureName: 'Delete Inventory-Consumption Transaction', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-53', url: '/invInventoryTransaction/listInventoryConsumption', featureName: 'List Inventory-Consumption Transaction', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            //for Inventory Out
            new RequestMap(transactionCode: 'INV-54', url: '/invInventoryTransaction/showInventoryOut', featureName: 'Show Inventory-Out', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-55', url: '/invInventoryTransaction/createInventoryOut', featureName: 'Create Inventory-Out', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-57', url: '/invInventoryTransaction/updateInventoryOut', featureName: 'Update Inventory-Out', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-58', url: '/invInventoryTransaction/deleteInventoryOut', featureName: 'Delete Inventory-Out', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-59', url: '/invInventoryTransaction/listInventoryOut', featureName: 'List Inventory-Out', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            //For Inventory -Production
            new RequestMap(transactionCode: 'INV-66', url: '/invInventoryTransaction/showInvProductionWithConsumption', featureName: 'Show Inventory production', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-67', url: '/invInventoryTransaction/createInvProductionWithConsumption', featureName: 'Create Inventory production', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-68', url: '/invInventoryTransaction/updateInvProductionWithConsumption', featureName: 'Update Inventory production', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-69', url: '/invInventoryTransaction/deleteInvProductionWithConsumption', featureName: 'Delete Inventory production', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-70', url: '/invInventoryTransaction/selectInvProductionWithConsumption', featureName: 'Select Inventory production', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-71', url: '/invInventoryTransaction/listInvProductionWithConsumption', featureName: 'List Inventory production', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            new RequestMap(transactionCode: 'INV-179', url: '/invInventoryTransaction/approveInvProdWithConsumption', featureName: 'Approve Inventory Production With Consumption', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-180', url: '/invInventoryTransaction/showApprovedProdWithConsump', featureName: 'Show For Approved Prod.With Consumption', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-181', url: '/invInventoryTransaction/listApprovedProdWithConsump', featureName: 'List For Approved Prod.With Consumption', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-182', url: '/invInventoryTransaction/adjustInvProductionWithConsumption', featureName: 'Adjust Inv.Production With Consumption', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-184', url: '/invInventoryTransaction/reverseAdjust', featureName: 'Reverse Adjustment for Inv.Production With Consumption', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            //For Production Line Item
            new RequestMap(transactionCode: 'INV-72', url: '/invProductionLineItem/show', featureName: 'Show Production Line Item', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-73', url: '/invProductionLineItem/create', featureName: 'Create Production Line Item', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-75', url: '/invProductionLineItem/update', featureName: 'Update Production Line Item', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-76', url: '/invProductionLineItem/delete', featureName: 'Delete Production Line Item', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-77', url: '/invProductionLineItem/list', featureName: 'List Production Line Item', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            //For Inventory Transaction Details
            //For Inventory-out-details
            new RequestMap(transactionCode: 'INV-78', url: '/invInventoryTransactionDetails/showUnApprovedInventoryOutDetails', featureName: 'Show Inventory-Out Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-79', url: '/invInventoryTransactionDetails/createInventoryOutDetails', featureName: 'Create Unapproved Inventory-Out Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-81', url: '/invInventoryTransactionDetails/updateInventoryOutDetails', featureName: 'Update Unapproved Inventory-Out Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-82', url: '/invInventoryTransactionDetails/deleteInventoryOutDetails', featureName: 'Delete Unapproved Inventory-Out Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-83', url: '/invInventoryTransactionDetails/listUnApprovedInventoryOutDetails', featureName: 'List Unapproved Inventory-Out Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            new RequestMap(transactionCode: 'INV-171', url: '/invInventoryTransactionDetails/approveInventoryOutDetails', featureName: ' Approved Inventory-Out Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-172', url: '/invInventoryTransactionDetails/showApprovedInventoryOutDetails', featureName: 'Show Approved Inventory-Out Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-173', url: '/invInventoryTransactionDetails/listApprovedInventoryOutDetails', featureName: 'List Approved Inventory-Out Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-174', url: '/invInventoryTransactionDetails/adjustInvOut', featureName: 'Adjustment For Inv. Out', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-192', url: '/invInventoryTransactionDetails/reverseAdjustInvOut', featureName: 'Reverse Adjustment For Inventory Out', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            //For Inventory-in-details (from supplier)
            new RequestMap(transactionCode: 'INV-90', url: '/invInventoryTransactionDetails/showUnapprovedInvInFromSupplier', featureName: 'Show Inventory-In Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-91', url: '/invInventoryTransactionDetails/createInventoryInDetailsFromSupplier', featureName: 'Create Inventory-In Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-93', url: '/invInventoryTransactionDetails/updateInventoryInDetailsFromSupplier', featureName: 'Update Inventory-In Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-94', url: '/invInventoryTransactionDetails/deleteInventoryInDetailsFromSupplier', featureName: 'Delete Inventory-In Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-95', url: '/invInventoryTransactionDetails/listUnapprovedInvInFromSupplier', featureName: 'List Inventory-In Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            new RequestMap(transactionCode: 'INV-164', url: '/invInventoryTransactionDetails/approveInventoryInDetailsFromSupplier', featureName: 'Approve Inventory In Details From Supplier', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-165', url: '/invInventoryTransactionDetails/showApprovedInvInFromSupplier', featureName: 'Show approved Inventory-In Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-166', url: '/invInventoryTransactionDetails/listApprovedInvInFromSupplier', featureName: 'List approved Inventory-In Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-167', url: '/invInventoryTransactionDetails/adjustInvInFromSupplier', featureName: 'Adjustment For Inv. In From Supplier', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-190', url: '/invInventoryTransactionDetails/reverseAdjustInvInFromSupplier', featureName: 'Reverse Adjustment For Inv. In From Supplier', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            //For Inventory-in-details (from Inventory)
            new RequestMap(transactionCode: 'INV-96', url: '/invInventoryTransactionDetails/showUnapprovedInvInFromInventory', featureName: 'Show Inventory-In From Inventory Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-97', url: '/invInventoryTransactionDetails/createInventoryInDetailsFromInventory', featureName: 'Create Inventory-In From Inventory Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-99', url: '/invInventoryTransactionDetails/updateInventoryInDetailsFromInventory', featureName: 'Update Inventory-In From Inventory Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-100', url: '/invInventoryTransactionDetails/deleteInventoryInDetailsFromInventory', featureName: 'Delete Inventory-In From Inventory Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-101', url: '/invInventoryTransactionDetails/listUnapprovedInvInFromInventory', featureName: 'List Inventory-In From Inventory Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            new RequestMap(transactionCode: 'INV-168', url: '/invInventoryTransactionDetails/approveInventoryInDetailsFromInventory', featureName: 'Approve Inventory In Details From Inventory', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-169', url: '/invInventoryTransactionDetails/showApprovedInvInFromInventory', featureName: 'Show approved Inventory-In From Inventory Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-170', url: '/invInventoryTransactionDetails/listApprovedInvInFromInventory', featureName: 'List approved Inventory-In From Inventory Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-191', url: '/invInventoryTransactionDetails/reverseAdjustInvInFromInventory', featureName: 'Reverse Adjustment For Inv. In From Inventory', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            //For Inventory consumption Details
            new RequestMap(transactionCode: 'INV-102', url: '/invInventoryTransactionDetails/showUnApprovedInventoryConsumptionDetails', featureName: 'Show Un approved Inv.Consumption Details Transaction', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-103', url: '/invInventoryTransactionDetails/createInventoryConsumptionDetails', featureName: 'Create Inventory-Consumption Details Transaction', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-105', url: '/invInventoryTransactionDetails/updateInventoryConsumptionDetails', featureName: 'Update Inventory-Consumption Details Transaction', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-106', url: '/invInventoryTransactionDetails/deleteInventoryConsumptionDetails', featureName: 'Delete Inventory-Consumption Details Transaction', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-107', url: '/invInventoryTransactionDetails/listUnApprovedInventoryConsumptionDetails', featureName: 'List Un approved Inv.Consumption Details Transaction', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            new RequestMap(transactionCode: 'INV-108', url: '/invInventoryTransactionDetails/approveInventoryConsumptionDetails', featureName: 'Approve Inventory-Consumption Details Transaction', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-176', url: '/invInventoryTransactionDetails/showApprovedInventoryConsumptionDetails', featureName: 'Show Approved Inventory-Consumption Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-177', url: '/invInventoryTransactionDetails/listApprovedInventoryConsumptionDetails', featureName: 'List Approved Inventory-Consumption Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-178', url: '/invInventoryTransactionDetails/adjustInvConsumption', featureName: 'Adjust For Inv. Consumption', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-185', url: '/invInventoryTransactionDetails/reverseAdjustInvConsumption', featureName: 'Reverse adjust For Inv. Consumption', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();

            new RequestMap(transactionCode: 'INV-158', url: '/invInventoryTransactionDetails/listFixedAssetByInventoryId', featureName: 'Get Fixed Asset list by inventory ID', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-159', url: '/invInventoryTransactionDetails/listFixedAssetByInventoryIdAndItemId', featureName: 'Get Fixed Asset list by inventory ID and item ID', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // refresh dropDown using remote url
            new RequestMap(transactionCode: 'INV-233', url: '/invInventoryTransactionDetails/dropDownInventoryItemConsumptionReload', featureName: 'Reload inventory item consumption dropDown', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-234', url: '/invInventoryTransactionDetails/dropDownInventoryItemInFromInventoryReload', featureName: 'Reload inventory item in from inventory dropDown', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-235', url: '/invInventoryTransactionDetails/dropDownInventoryItemInFromSupplierReload', featureName: 'Reload inventory item in from supplier dropDown', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-236', url: '/invInventoryTransactionDetails/dropDownInventoryItemOutReload', featureName: 'Reload inventory item out dropDown', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-237', url: '/invInventoryTransaction/dropDownInventoryReload', featureName: 'Reload inventory dropDown', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // For Get All Supplier List
            new RequestMap(transactionCode: 'INV-111', url: '/supplier/listAllSupplier', featureName: 'Get Supplier List', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            //For Production Line Item Details
            new RequestMap(transactionCode: 'INV-112', url: '/invProductionDetails/show', featureName: 'Show Production Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-113', url: '/invProductionDetails/create', featureName: 'Create Production Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-115', url: '/invProductionDetails/update', featureName: 'Update Production Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-116', url: '/invProductionDetails/delete', featureName: 'Delete Production Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-117', url: '/invProductionDetails/list', featureName: 'List Production Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-118', url: '/invProductionDetails/getBothMaterials', featureName: 'Get Both Materials for Production Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-114', url: '/invProductionDetails/dropDownMaterialReload', featureName: 'Reload Drop Down Material', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // Recalculate valuation
            new RequestMap(transactionCode: 'INV-148', url: '/invInventoryTransaction/reCalculateValuation', featureName: 'Re-Calculate ALL valuation', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-203', url: '/invInventoryTransaction/showReCalculateValuation', featureName: 'Show Re-Calculate-Valuation', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();

            // Production Overhead Cost modification
            new RequestMap(transactionCode: 'INV-210', url: '/invInventoryTransactionDetails/showInvModifyOverheadCost', featureName: 'Show Production Overhead Cost for Modification', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-211', url: '/invInventoryTransactionDetails/searchInvModifyOverheadCost', featureName: 'Search Production Overhead Cost for Modification', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-212', url: '/invInventoryTransactionDetails/updateInvModifyOverheadCost', featureName: 'Modify Overhead Cost in Production', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-213', url: '/invInventoryTransactionDetails/getInvProdFinishedMaterialByLineItemId', featureName: 'Get Finished Material by Production Line Item', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            //**********************************
            //Inventory Report
            //*******************************

            //for inventory invoice
            new RequestMap(transactionCode: 'INV-119', url: '/invReport/showInvoice', featureName: 'Show Invoice of Inventory-In', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-120', url: '/invReport/searchInvoice', featureName: 'Search Invoice of Inventory-In', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-121', url: '/invReport/downloadInvoice', featureName: 'Download Invoice of Inventory-In', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            //for inventory stock
            new RequestMap(transactionCode: 'INV-122', url: '/invReport/inventoryStock', featureName: 'Show Inventory Stock', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-124', url: '/invReport/listInventoryStock', featureName: 'list Inventory Stock', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-125', url: '/invReport/downloadInventoryStock', featureName: 'Download Inventory Stock', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-223', url: '/invReport/downloadInventoryStockCsv', featureName: 'Download Inventory Stock Csv Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            //for item stock
            new RequestMap(transactionCode: 'INV-126', url: '/invReport/showItemStock', featureName: 'Show Item Stock List', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-127', url: '/invReport/listItemStock', featureName: 'List Item Stock', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-128', url: '/invReport/listStockDetailsByItemId', featureName: 'Get Item Stock Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            //For Inventory Status
            new RequestMap(transactionCode: 'INV-160', url: '/invReport/showInventoryStatusWithQuantityAndValue', featureName: 'Show Inventory Status with quantity And Value Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-161', url: '/invReport/listInventoryStatusWithQuantityAndValue', featureName: 'List Inventory Status with quantity And Value Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-162', url: '/invReport/downloadInventoryStatusWithQuantityAndValue', featureName: 'Download Inventory Status with quantity And Value Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-224', url: '/invReport/downloadInventoryStatusWithQuantityAndValueCsv', featureName: 'Download Inventory Status with quantity And Value Csv Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // for Inventory valuation
            new RequestMap(transactionCode: 'INV-129', url: '/invReport/showInventoryValuation', featureName: 'Show Inventory Valuation', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-130', url: '/invReport/searchInventoryValuation', featureName: 'Search Inventory Valuation', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-131', url: '/invReport/downloadInventoryValuation', featureName: 'Download Inventory Valuation', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-227', url: '/invReport/downloadInventoryValuationCsv', featureName: 'Download Inventory Valuation', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            new RequestMap(transactionCode: 'INV-132', url: '/invReport/showInventoryTransactionList', featureName: 'Show all Inventory Transaction List', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-133', url: '/invReport/searchInventoryTransactionList', featureName: 'Search all Inventory Transaction List', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-134', url: '/invReport/downloadInventoryTransactionList', featureName: 'Download all Inventory Transaction List', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-226', url: '/invReport/downloadInventoryTransactionListCsv', featureName: 'Download all Inventory Transaction List Csv Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // for Inventory summary
            new RequestMap(transactionCode: 'INV-138', url: '/invReport/showInventorySummary', featureName: 'Show Inventory Summary', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-139', url: '/invReport/listInventorySummary', featureName: 'Search Inventory Summary', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-140', url: '/invReport/downloadInventorySummary', featureName: 'Download Inventory Summary', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-222', url: '/invReport/downloadInventorySummaryCsv', featureName: 'Download Inventory Csv Summary', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            new RequestMap(transactionCode: 'INV-141', url: '/invReport/showConsumedItemList', featureName: 'Show Consumed Item List', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-142', url: '/invReport/listBudgetOfConsumption', featureName: 'Budget List of Consumption', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-143', url: '/invReport/listConsumedItemByBudget', featureName: 'Get Consumed Item List', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-220', url: '/invReport/downloadForConsumedItemList', featureName: 'Download Consumed Item List Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-239', url: '/invReport/downloadDetailsConsumedItemListCsv', featureName: 'Download Dtails Consumption List in CSV', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            new RequestMap(transactionCode: 'INV-144', url: '/invInventoryTransaction/listAllInventoryByType', featureName: 'Get All Inventory List by Inventory type', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-149', url: '/invInventoryTransaction/listInventoryIsFactoryByType', featureName: 'Get Inventory List of is factory by Inventory type', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            new RequestMap(transactionCode: 'INV-152', url: '/invReport/showItemReceivedStock', featureName: 'Show Item Received Stock Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-153', url: '/invReport/listItemReceivedStock', featureName: 'List Item Received Stock Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-232', url: '/invReport/downloadItemReceivedGroupBySupplier', featureName: 'Download Item Received Stock Group By Supplier Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-154', url: '/invReport/downloadItemReceivedStock', featureName: 'Download Item Received Stock Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-228', url: '/invReport/downloadItemReceivedStockCsv', featureName: 'Download Item Received Stock Csv Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            new RequestMap(transactionCode: 'INV-155', url: '/invReport/showItemWiseBudgetSummary', featureName: 'Show Item Wise Budget Summary Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-156', url: '/invReport/listItemWiseBudgetSummary', featureName: 'List Item Wise Budget Summary Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-157', url: '/invReport/downloadItemWiseBudgetSummary', featureName: 'Download Item Wise Budget Summary Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-229', url: '/invReport/downloadItemWiseBudgetSummaryCsv', featureName: 'Download Item Wise Budget Summary Csv Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            //For production Report
            new RequestMap(transactionCode: 'INV-193', url: '/invReport/showInventoryProductionRpt', featureName: 'Show Inventory Production Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-194', url: '/invReport/searchInventoryProductionRpt', featureName: 'Search Inventory Production Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-195', url: '/invReport/downloadInventoryProductionRpt', featureName: 'Download Inventory Production Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // Supplier Challlan Report
            new RequestMap(transactionCode: 'INV-196', url: '/invReport/showSupplierChalan', featureName: 'Show Supplier Chalan Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-197', url: '/invReport/listSupplierChalan', featureName: 'List Supplier Chalan Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // PO Item Received Report
            new RequestMap(transactionCode: 'INV-198', url: '/invReport/showPoItemReceived', featureName: 'Show PO Item Received Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-199', url: '/invReport/listPoItemReceived', featureName: 'List PO Item Received Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-200', url: '/invReport/downloadPoItemReceived', featureName: 'Download PO Item Received Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-230', url: '/invReport/downloadPoItemReceivedCsv', featureName: 'Download PO Item Received Csv Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // Acknowledge & download supplier chalan report
            new RequestMap(transactionCode: 'INV-201', url: '/invInventoryTransactionDetails/acknowledgeInvoiceFromSupplier', featureName: 'Acknowledge Chalan', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-202', url: '/invReport/downloadSupplierChalanReport', featureName: 'Download Chalan Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-221', url: '/invReport/downloadSupplierChalanCsvReport', featureName: 'Download Chalan CSV Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // inventory status with value report
            new RequestMap(transactionCode: 'INV-204', url: '/invReport/showInventoryStatusWithValue', featureName: 'Show Inventory Status With Value', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-205', url: '/invReport/listInventoryStatusWithValue', featureName: 'List Inventory Status With Value', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-206', url: '/invReport/downloadInventoryStatusWithValue', featureName: 'Download Inventory Status With Value', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-218', url: '/invReport/downloadInventoryStatusWithValueCsv', featureName: 'Download Inventory Status With Value CSV Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // inventory status report with Quantity
            new RequestMap(transactionCode: 'INV-207', url: '/invReport/showInventoryStatusWithQuantity', featureName: 'Show Inventory Status With Quantity', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-208', url: '/invReport/listInventoryStatusWithQuantity', featureName: 'List Inventory Status With Quantity', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-209', url: '/invReport/downloadInventoryStatusWithQuantity', featureName: 'Download Inventory Status With Quantity', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-219', url: '/invReport/downloadInventoryStatusWithQuantityCsv', featureName: 'Download Inventory Status With Quantity CSV Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // Item-Reconciliation Report
            new RequestMap(transactionCode: 'INV-214', url: '/invReport/showForItemReconciliation', featureName: 'Show Item Reconciliation Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-215', url: '/invReport/listForItemReconciliation', featureName: 'List Item Reconciliation Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-216', url: '/invReport/downloadForItemReconciliation', featureName: 'Download Item Reconciliation Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'INV-225', url: '/invReport/downloadForItemReconciliationCsv', featureName: 'Download Item Reconciliation Csv Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createRequestMapForFixedAsset() {
        try {
            int count = appVersionService.countByPluginId(FxdPluginConnector.PLUGIN_ID)
            if (count > 0) {
                return true
            }
            executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_viewable,is_common)
            VALUES(-21,0, '/fxdPlugin/renderFixedAssetMenu','ROLE_-2','Fixed Asset Module',7,'FA-47',FALSE,FALSE)""")

            //for Fixed Asset Details
            new RequestMap(transactionCode: 'FA-1', url: '/fxdFixedAssetDetails/show', featureName: 'Show fixed asset details', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'FA-2', url: '/fxdFixedAssetDetails/create', featureName: 'Create fixed asset details', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'FA-3', url: '/fxdFixedAssetDetails/delete', featureName: 'Delete fixed asset details', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'FA-4', url: '/fxdFixedAssetDetails/list', featureName: 'List fixed asset details', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'FA-5', url: '/fxdFixedAssetDetails/select', featureName: 'Select fixed asset details', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'FA-6', url: '/fxdFixedAssetDetails/update', featureName: 'Update fixed asset details', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'FA-7', url: '/fxdFixedAssetDetails/getFixedAssetList', featureName: 'Get fixed asset List Of PO and Inventory', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            //For Fixed Asset Trace
            new RequestMap(transactionCode: 'FA-16', url: '/fxdFixedAssetTrace/show', featureName: 'Show Fixed Asset Trace', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'FA-17', url: '/fxdFixedAssetTrace/list', featureName: 'List Fixed Asset Trace', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'FA-18', url: '/fxdFixedAssetTrace/create', featureName: 'Create Fixed Asset Trace', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'FA-19', url: '/fxdFixedAssetTrace/listItemList', featureName: 'Get Item List of Fixed Asset', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            //for Maintenance Type
            new RequestMap(transactionCode: 'FA-24', url: '/fxdMaintenanceType/show', featureName: 'Show fixed maintenance type', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'FA-25', url: '/fxdMaintenanceType/create', featureName: 'Create fixed maintenance type', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'FA-26', url: '/fxdMaintenanceType/delete', featureName: 'Delete fixed maintenance type', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'FA-27', url: '/fxdMaintenanceType/list', featureName: 'List fixed maintenance type', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'FA-29', url: '/fxdMaintenanceType/update', featureName: 'Update fixed maintenance type', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            //for Category Maintenance Type
            new RequestMap(transactionCode: 'FA-30', url: '/fxdCategoryMaintenanceType/show', featureName: 'Show fixed maintenance type', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'FA-31', url: '/fxdCategoryMaintenanceType/create', featureName: 'Create fixed maintenance type', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'FA-32', url: '/fxdCategoryMaintenanceType/delete', featureName: 'Delete fixed maintenance type', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'FA-33', url: '/fxdCategoryMaintenanceType/list', featureName: 'List fixed maintenance type', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'FA-35', url: '/fxdCategoryMaintenanceType/update', featureName: 'Update fixed maintenance type', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'FA-54', url: '/fxdCategoryMaintenanceType/dropDownFxdMaintenanceTypeReload', featureName: 'Reload fixed maintenance type dropDown', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            //for Maintenance
            new RequestMap(transactionCode: 'FA-40', url: '/fxdMaintenance/show', featureName: 'Show fixed asset maintenance', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'FA-41', url: '/fxdMaintenance/create', featureName: 'Create fixed asset maintenance', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'FA-42', url: '/fxdMaintenance/delete', featureName: 'Delete fixed asset maintenance', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'FA-43', url: '/fxdMaintenance/list', featureName: 'List fixed asset maintenance', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'FA-45', url: '/fxdMaintenance/update', featureName: 'Update fixed asset maintenance', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'FA-46', url: '/fxdMaintenance/listMaintenanceTypeAndModelListByItemId', featureName: 'Get Maintenance Type and Model List by item', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            //** ****************************
            //Fixed Asset Report
            //*******************************

            new RequestMap(transactionCode: 'FA-9', url: '/fxdFixedAssetReport/showConsumptionAgainstAsset', featureName: 'Show Consumption Against Fixed Asset Report', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'FA-10', url: '/fxdFixedAssetReport/listConsumptionAgainstAsset', featureName: 'List Consumption Against Fixed Asset Report', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'FA-11', url: '/fxdFixedAssetReport/getConsumptionAgainstAssetDetails', featureName: 'Get Consumption Against fixed Asset Details', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'FA-12', url: '/fxdFixedAssetReport/downloadConsumptionAgainstAsset', featureName: 'Download Consumption Against Fixed Asset Report', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'FA-53', url: '/fxdFixedAssetReport/downloadConsumptionAgainstAssetDetailsCsv', featureName: 'Download Consumption Against Fixed Asset CSV Report', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            new RequestMap(transactionCode: 'FA-13', url: '/fxdFixedAssetReport/showPendingFixedAsset', featureName: 'Show Pending Fixed Asset Report', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'FA-14', url: '/fxdFixedAssetReport/listPendingFixedAsset', featureName: 'List Pending Fixed Asset Report', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'FA-15', url: '/fxdFixedAssetReport/downloadPendingFixedAsset', featureName: 'Download Pending Fixed Asset Report', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            new RequestMap(transactionCode: 'FA-20', url: '/fxdFixedAssetReport/showCurrentFixedAsset', featureName: 'Show Current Fixed Asset Report', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'FA-21', url: '/fxdFixedAssetReport/listCurrentFixedAsset', featureName: 'List Current Fixed Asset Report', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'FA-22', url: '/fxdFixedAssetReport/downloadCurrentFixedAsset', featureName: 'Download Current Fixed Asset Report', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'FA-51', url: '/fxdFixedAssetReport/downloadCurrentFixedAssetCsv', featureName: 'Download Current Fixed Asset Csv Report', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            new RequestMap(transactionCode: 'FA-48', url: '/fxdFixedAssetReport/showConsumptionAgainstAssetDetails', featureName: 'Show Consumption Against Asset Details Report', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'FA-49', url: '/fxdFixedAssetReport/listConsumptionAgainstAssetDetails', featureName: 'List Consumption Against Asset Details Report', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'FA-50', url: '/fxdFixedAssetReport/downloadConsumptionAgainstAssetDetails', featureName: 'Download Consumption Against Asset Details Report', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createRequestMapForExchangeHouse() {
        try {
            int count = appVersionService.countByPluginId(ExchangeHousePluginConnector.PLUGIN_ID)
            if (count > 0) return true

            executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_viewable,is_common)
        VALUES(-25,0,'/exhExchangeHouse/renderExchangeHouseMenu','ROLE_-2','Exchange House Module',9,'EXH-160',FALSE,FALSE)""")

            // Customer
            new RequestMap(transactionCode: 'EXH-1', url: '/exhCustomer/show', featureName: 'show customer for cashier', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-350', url: '/exhCustomer/showForSarb', featureName: 'show customer for cashier for sarb', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-2', url: '/exhCustomer/showCustomerDetails', featureName: 'Show customer details', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-133', url: '/exhCustomer/showForAgent', featureName: 'show customer for agent', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-351', url: '/exhCustomer/showForAgentForSarb', featureName: 'show customer for agent for sarb', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-6', url: '/exhCustomer/list', featureName: 'List customer', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-356', url: '/exhCustomer/listForSarb', featureName: 'List customer for sarb', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-7', url: '/exhCustomer/delete', featureName: 'Delete customer', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-8', url: '/exhCustomer/showCustomerUser', featureName: 'Show for customer-user', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-9', url: '/exhCustomer/searchCustomerUser', featureName: 'Search for customer-user', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-10', url: '/exhCustomer/createCustomerUser', featureName: 'Create for customer-user', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'EXH-285', url: '/exhCustomer/createForCashier', featureName: 'Create customer for cashier', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-352', url: '/exhCustomer/createForCashierForSarb', featureName: 'Create customer for cashier for sarb', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-286', url: '/exhCustomer/createForAgent', featureName: 'Create customer for agent', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-353', url: '/exhCustomer/createForAgentForSarb', featureName: 'Create customer for agent for sarb', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-287', url: '/exhCustomer/updateForCashier', featureName: 'Update customer for cashier', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-354', url: '/exhCustomer/updateForCashierForSarb', featureName: 'Update customer for cashier for sarb', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-288', url: '/exhCustomer/updateForAgent', featureName: 'Update customer for agent', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-355', url: '/exhCustomer/updateForAgentForSarb', featureName: 'Update customer for agent for sarb', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()


            new RequestMap(transactionCode: 'EXH-257', url: '/exhCustomer/blockExhCustomer', featureName: 'Block exchange house customer.', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-357', url: '/exhCustomer/blockExhCustomerForSarb', featureName: 'Block exchange house customer for Sarb.', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-259', url: '/exhCustomer/unblockExhCustomer', featureName: 'Unblock exchange house customer.', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-358', url: '/exhCustomer/unblockExhCustomerForSarb', featureName: 'Unblock exchange house customer for sarb.', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-260', url: '/exhCustomer/reloadCustomerDetails', featureName: 'Reload Customer Details', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-262', url: '/exhCustomer/reloadCustomerSummary', featureName: 'Reload Customer Summary', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-263', url: '/exhCustomer/verifyDuplicateCustomer', featureName: 'Verify customer duplication', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-264', url: '/exhCustomer/viewDuplicateCustomerDetails', featureName: 'View customer details for duplicate customer', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-320', url: '/exhCustomer/listDuplicateCustomerDetails', featureName: 'List customer details for duplicate customer', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'EXH-342', url: '/exhCustomer/showCustomerProfile', featureName: 'Show customer profile for customer', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-359', url: '/exhCustomer/showCustomerProfileForSarb', featureName: 'Show customer profile for customer for sarb', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-343', url: '/exhCustomer/updateCustomerProfile', featureName: 'Update customer profile for customer', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-360', url: '/exhCustomer/updateCustomerProfileForSarb', featureName: 'Update customer profile for customer for sarb', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // Beneficiary
            new RequestMap(transactionCode: 'EXH-11', url: '/exhBeneficiary/show', featureName: 'Show Beneficiary', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-12', url: '/exhBeneficiary/create', featureName: 'Create Beneficiary', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-233', url: '/exhBeneficiary/createForCustomer', featureName: 'Create Beneficiary for Customer', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-13', url: '/exhBeneficiary/update', featureName: 'Update Beneficiary', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-234', url: '/exhBeneficiary/updateForCustomer', featureName: 'Update Beneficiary for Customer', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-15', url: '/exhBeneficiary/list', featureName: 'List Beneficiary', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-207', url: '/exhBeneficiary/listLinkedBeneficiary', featureName: 'List of Linked Beneficiary', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-16', url: '/exhBeneficiary/showNewForCustomer', featureName: 'Show new beneficiary for customer', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-232', url: '/exhBeneficiary/showApprovedForCustomer', featureName: 'Show approved beneficiary for customer', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-17', url: '/exhBeneficiary/detailsForCustomer', featureName: 'Show beneficiary details for customer login', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-18', url: '/exhBeneficiary/listForCustomer', featureName: 'List beneficiary details for customer login', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-255', url: '/exhBeneficiary/approveBeneficiary', featureName: 'Approve beneficiary', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-267', url: '/exhBeneficiary/showForAgent', featureName: 'Show Beneficiary For Agent', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-272', url: '/exhBeneficiary/createForAgent', featureName: 'Create Beneficiary For Agent', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-269', url: '/exhBeneficiary/updateForAgent', featureName: 'Update Beneficiary For Agent', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-270', url: '/exhBeneficiary/listForAgent', featureName: 'List Beneficiary For Agent', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-296', url: '/exhBeneficiary/validateBeneficiary', featureName: 'Validate Beneficiary', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // Customer  Beneficiary Mapping
            new RequestMap(transactionCode: 'EXH-208', url: '/exhCustomerBeneficiary/create', featureName: 'Map an existing beneficiary with customer', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // Task
            new RequestMap(transactionCode: 'EXH-170', url: '/exhTask/showExhTaskForAdmin', featureName: 'Show Exh Task for Admin', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-171', url: '/exhTask/showAgentTaskForAdmin', featureName: 'Show Agent Task for Admin', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-172', url: '/exhTask/showCustomerTaskForAdmin', featureName: 'Show Customer Task for Admin', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-176', url: '/exhTask/showExhTaskForCashier', featureName: 'Show Exh Task for Cashier', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-177', url: '/exhTask/showAgentTaskForCashier', featureName: 'Show Agent Task for Cashier', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-178', url: '/exhTask/showCustomerTaskForCashier', featureName: 'Show Customer Task for Cashier', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-24', url: '/exhTask/list', featureName: 'List Task', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-173', url: '/exhTask/listExhTaskForAdmin', featureName: 'List Exh Task for Admin', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-174', url: '/exhTask/listAgentTaskForAdmin', featureName: 'List Agent Task for Admin', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-175', url: '/exhTask/listCustomerTaskForAdmin', featureName: 'List Customer Task for Admin', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-179', url: '/exhTask/listExhTaskForCashier', featureName: 'List Exh Task for Cashier', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-180', url: '/exhTask/listAgentTaskForCashier', featureName: 'List Agent Task for Cashier', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-181', url: '/exhTask/listCustomerTaskForCashier', featureName: 'List Customer Task for Cashier', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-182', url: '/exhTask/approveTaskForCashier', featureName: 'Approve Task For Cashier', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-26', url: '/exhTask/showForTaskSearch', featureName: 'Show for task search', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-134', url: '/exhTask/showForAgent', featureName: 'Show task for agent', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-135', url: '/exhTask/listForAgent', featureName: 'List task for agent', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-138', url: '/exhTask/editForAgent', featureName: 'Edit task for agent', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-291', url: '/exhTask/reloadCurrencyDropDownForExhTask', featureName: 'Reload currency dropdown for exh', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-292', url: '/exhTask/reloadExhTaskSummaryTaglib', featureName: 'Reload exh task summary', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-200', url: '/exhTask/resolveTaskFromTaskStatus', featureName: 'Resolve task for other bank from task status details', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-201', url: '/exhTask/searchTaskForOtherBankTaskStatus', featureName: 'Search task for other bank task status details', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-202', url: '/exhTask/createExhTaskNote', featureName: 'Create task note from task status details', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-203', url: '/exhTask/searchTaskForTaskStatus', featureName: 'Search task for task status details', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-344', url: '/exhTask/searchTaskForAgentTaskStatus', featureName: 'Search task for agent task status details', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-345', url: '/exhTask/searchTaskForCustomerTaskStatus', featureName: 'Search task for customer task status details', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            //TASK CRUD
            new RequestMap(transactionCode: 'EXH-273', url: '/exhTask/createTaskForCashier', featureName: 'Create Exh Task for Cashier', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-361', url: '/exhTask/createTaskForCashierForSarb', featureName: 'Create Exh Task for Cashier for sarb', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-274', url: '/exhTask/createTaskForAgent', featureName: 'Create Exh Task for Agent', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-362', url: '/exhTask/createTaskForAgentForSarb', featureName: 'Create Exh Task for Agent for sarb', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-275', url: '/exhTask/createTaskForCustomer', featureName: 'Create Exh Task for Customer', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-363', url: '/exhTask/createTaskForCustomerForSarb', featureName: 'Create Exh Task for Customer for sarb', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-276', url: '/exhTask/updateTaskForCashier', featureName: 'Update Exh Task for Cashier', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-364', url: '/exhTask/updateTaskForCashierForSarb', featureName: 'Update Exh Task for Cashier For Sarb', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-279', url: '/exhTask/updateCustomerTaskForCashier', featureName: 'Update Customer Task for Cashier', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-365', url: '/exhTask/updateCustomerTaskForCashierForSarb', featureName: 'Update Customer Task for Cashier for sarb', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-277', url: '/exhTask/updateTaskForAgent', featureName: 'Update Task for Agent', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-366', url: '/exhTask/updateTaskForAgentForSarb', featureName: 'Update Task for Agent for sarb', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-278', url: '/exhTask/updateTaskForCustomer', featureName: 'Update Task for Customer', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-367', url: '/exhTask/updateTaskForCustomerForSarb', featureName: 'Update Task for Customer for sarb', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'EXH-185', url: '/exhTask/calculateFeesAndCommission', featureName: 'Calculate fees and commission', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-341', url: '/exhTask/calculateFeesAndCommissionForAgent', featureName: 'Calculate regular fees and commission for agent', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'EXH-137', url: '/exhTask/sendToExchangeHouse', featureName: 'Send task to exchange house', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-168', url: '/exhTask/sendToExhForCustomer', featureName: 'Send task to exchange house for customer', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'EXH-333', url: '/exhTask/showForTaskSearchForAgent', featureName: 'Show for task search for agent', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-335', url: '/exhTask/showForTaskSearchForCustomer', featureName: 'Show for task search for customer', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-338', url: '/exhTask/showForTaskSearchForOtherBank', featureName: 'Show for task search for other bank', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-339', url: '/exhTask/reloadShowTaskDetailsForOtherBank', featureName: 'Reload task details taglib for other bank', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            //task for admin
            new RequestMap(transactionCode: 'EXH-310', url: '/exhTask/sendToBankWithRest', featureName: 'Send task to Bank using arms web service', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-315', url: '/exhTask/verifyAndSendExceptionalTaskToBankWithRest', featureName: 'Send exceptional task to Bank using arms web service', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-30', url: '/exhTask/cancelSpecificTask', featureName: 'Cancel task', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-31', url: '/exhTask/showTaskDetails', featureName: 'Show task details', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-34', url: '/exhTask/showForCustomer', featureName: 'Show task for customer', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-192', url: '/exhTask/showUnApprovedTaskForCustomer', featureName: 'Show UnApproved Customer Task', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-193', url: '/exhTask/listUnApprovedTaskForCustomer', featureName: 'List UnApproved Customer Task', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-167', url: '/exhTask/showApprovedTaskForCustomer', featureName: 'Show Approved Customer Task', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-169', url: '/exhTask/listApprovedTaskForCustomer', featureName: 'List Approved Customer Task', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-187', url: '/exhTask/showDisbursedTaskForCustomer', featureName: 'Show Disbursed Customer Task', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-198', url: '/exhTask/listDisbursedTaskForCustomer', featureName: 'List Disbursed Customer Task', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-35', url: '/exhTask/listForCustomer', featureName: 'List task for customer', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-36', url: '/exhTask/showForOtherBankUser', featureName: 'Show task for other bank user', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-37', url: '/exhTask/listForOtherBankUser', featureName: 'List task for other bank user', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-38', url: '/exhTask/resolveTaskForOtherBank', featureName: 'Resolve task for other bank user', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-39', url: '/exhTask/downloadCsvForOtherBank', featureName: 'Download CSV for other bank user', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-258', url: '/exhTask/showDetailsForReplaceTask', featureName: 'Show Details For Replace Task', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //bank drop down for admin and other bank user
            new RequestMap(transactionCode: 'EXH-256', url: '/exhTask/reloadBankByTaskStatusAndTaskType', featureName: 'Reload bank drop down independently', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            /*Make payment*/
            new RequestMap(transactionCode: 'EXH-239', url: '/exhTask/showForMakePayment', featureName: 'Show For make payment', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-240', url: '/exhTask/callbackForPayPointUserReturn', featureName: 'Callback For PayPoint User Return', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-241', url: '/exhTask/showForPayPointUserReturn', featureName: 'Show For PayPoint User Return', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-242', url: '/exhTask/callbackForPayPointPRN', featureName: 'Callback For PayPointPRN', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()

            //setting remitance purpose
            new RequestMap(transactionCode: 'EXH-56', url: '/exhRemittancePurpose/show', featureName: 'Show remittance purpose', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-57', url: '/exhRemittancePurpose/create', featureName: 'Create remittance purpose', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-58', url: '/exhRemittancePurpose/update', featureName: 'Update remittance purpose', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-60', url: '/exhRemittancePurpose/list', featureName: 'List remittance purpose', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-61', url: '/exhRemittancePurpose/delete', featureName: 'Delete remittance purpose', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //setting photo id type
            new RequestMap(transactionCode: 'EXH-62', url: '/exhPhotoIdType/show', featureName: 'Show Photo-Id-Type', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-63', url: '/exhPhotoIdType/create', featureName: 'Create Photo-Id-Type', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-64', url: '/exhPhotoIdType/update', featureName: 'Update Photo-Id-Type', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-66', url: '/exhPhotoIdType/list', featureName: 'List Photo-Id-Type', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-67', url: '/exhPhotoIdType/delete', featureName: 'Delete Photo-Id-Type', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //setting currency conversion
            new RequestMap(transactionCode: 'EXH-95', url: '/exhCurrencyConversion/show', featureName: 'Show currency conversion', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-96', url: '/exhCurrencyConversion/create', featureName: 'Create currency conversion', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-97', url: '/exhCurrencyConversion/update', featureName: 'Update currency conversion', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-98', url: '/exhCurrencyConversion/list', featureName: 'List currency conversion', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // setting Exh Agent
            new RequestMap(transactionCode: 'EXH-145', url: '/exhAgent/show', featureName: 'Show Agent', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-146', url: '/exhAgent/create', featureName: 'Create Agent', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-147', url: '/exhAgent/list', featureName: 'List Agent', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-149', url: '/exhAgent/update', featureName: 'Update Agent', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-150', url: '/exhAgent/delete', featureName: 'Delete Agent', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // setting Exh Agent Currency Posting
            new RequestMap(transactionCode: 'EXH-139', url: '/exhAgentCurrencyPosting/show', featureName: 'Show Agent Currency Posting', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-140', url: '/exhAgentCurrencyPosting/create', featureName: 'Create Agent Currency Posting', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-141', url: '/exhAgentCurrencyPosting/list', featureName: 'List Agent Currency Posting', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-143', url: '/exhAgentCurrencyPosting/update', featureName: 'Update Agent Currency Posting', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-144', url: '/exhAgentCurrencyPosting/delete', featureName: 'Delete Agent Currency Posting', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // settings Exh Regular Fee
            new RequestMap(transactionCode: 'EXH-20', url: '/exhRegularFee/show', featureName: 'Show Regular Fee for Super Admin', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-27', url: '/exhRegularFee/update', featureName: 'Update Regular Fee', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-28', url: '/exhRegularFee/calculate', featureName: 'Calculate Regular Fee', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // Reports
            new RequestMap(transactionCode: 'EXH-107', url: '/exhReport/showCustomerHistory', featureName: 'Show Customer History', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-205', url: '/exhReport/showCustomerRemittanceSummary', featureName: 'Show Customer remittance Summary', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-204', url: '/exhReport/downloadCustomerHistory', featureName: 'Download Customer History', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-206', url: '/exhReport/downloadCustomerRemittanceSummary', featureName: 'Download Customer Customer Remittance Summary', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-209', url: '/exhReport/listForCustomerRemittanceSummary', featureName: 'List Customer Remittance Summary', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-109', url: '/exhReport/listForCustomerRemittance', featureName: 'List remittance details by customer', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-110', url: '/exhReport/showRemittanceSummary', featureName: 'Show remittance summary', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-111', url: '/exhReport/populateRemittanceSummaryReport', featureName: 'Get remittance summary report', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-112', url: '/exhReport/showInvoice', featureName: 'Show Invoice', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-115', url: '/exhReport/downloadInvoice', featureName: 'Download invoice', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-235', url: '/exhReport/downloadRemittanceTransactionCsv', featureName: 'Download Remittance Transaction CSV', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-237', url: '/exhReport/downloadCustomerCSV', featureName: 'Download Customer in CSV', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'EXH-251', url: '/exhReport/listTransactionSummary', featureName: 'List Transaction Summary', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-252', url: '/exhReport/downloadTransactionSummary', featureName: 'Download Transaction Summary', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-253', url: '/exhReport/listRemittanceTransaction', featureName: 'List Remittance Transaction', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-254', url: '/exhReport/downloadRemittanceTransaction', featureName: 'Download Remittance Transaction', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-265', url: '/exhReport/downloadCustomerTransaction', featureName: 'Download Customer Transaction', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-268', url: '/exhReport/downloadBeneficiaryDetails', featureName: 'Download beneficiary details report', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-293', url: '/exhReport/showLinkedBeneficiary', featureName: 'Show linked beneficiary report', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-294', url: '/exhReport/listLinkedBeneficiary', featureName: 'List linked beneficiary report', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-295', url: '/exhReport/downloadLinkedBeneficiary', featureName: 'Download linked beneficiary report', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-312', url: '/exhReport/showExceptionalTask', featureName: 'Show exceptional task', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-313', url: '/exhReport/listExceptionalTask', featureName: 'List exceptional task', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-314', url: '/exhReport/downloadExceptionalTask', featureName: 'Download exceptional task', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-319', url: '/exhReport/reloadTaskInvoice', featureName: 'Reload task invoice', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-321', url: '/exhReport/downloadCustomerAllDocuments', featureName: 'Download customer all documents', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-322', url: '/exhReport/showExhCustomer', featureName: 'Show all customer list', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-323', url: '/exhReport/listExhCustomer', featureName: 'All customer list', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-324', url: '/exhReport/downloadCustomerList', featureName: 'Download all customer list', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'EXH-326', url: '/exhReport/downloadDailyRemittanceSummary', featureName: 'Download Daily Remittance Summary Report', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-327', url: '/exhReport/showInvoiceForAgent', featureName: 'Show Invoice For Agent', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-328', url: '/exhReport/reloadInvoiceForAgent', featureName: 'Reload invoice for agent', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-329', url: '/exhReport/showInvoiceForCustomer', featureName: 'Show Invoice For Customer', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-330', url: '/exhReport/reloadInvoiceForCustomer', featureName: 'Reload invoice for customer', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-331', url: '/exhReport/downloadInvoiceForCustomer', featureName: 'Download invoice for customer', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-332', url: '/exhReport/downloadInvoiceForAgent', featureName: 'Download invoice for agent', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-347', url: '/exhReport/showInvoiceForOtherBank', featureName: 'Show invoice for other bank', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-348', url: '/exhReport/reloadInvoiceForOtherBank', featureName: 'Reload invoice for other bank', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-349', url: '/exhReport/downloadInvoiceForOtherBank', featureName: 'Download invoice for other bank', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // rest
            new RequestMap(transactionCode: 'EXH-325', url: '/exhRest/verifySanction', featureName: 'Verify sanction for rest api', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-346', url: '/exhRest/updateTaskStatus', featureName: 'Receive updated task status from arms', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // Agent wise commission
            new RequestMap(transactionCode: 'EXH-188', url: '/exhReport/showAgentWiseCommissionForAdmin', featureName: 'Show Agent Wise Commission', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-189', url: '/exhReport/listAgentWiseCommissionForAdmin', featureName: 'List Agent Wise Commission', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-190', url: '/exhReport/showAgentWiseCommissionForAgent', featureName: 'Show Agent Commission', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-191', url: '/exhReport/listAgentWiseCommissionForAgent', featureName: 'List Commission', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-195', url: '/exhReport/downloadAgentWiseCommissionForAdmin', featureName: 'Download Agent Wise Commission for Admin', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-194', url: '/exhReport/downloadAgentWiseCommissionForAgent', featureName: 'Download Agent Wise Commission for Agent', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'EXH-116', url: '/exhReport/showCashierWiseReportForAdmin', featureName: 'Show cashier wise report for admin', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-117', url: '/exhReport/showCashierWiseReportForCashier', featureName: 'Show cashier wise report for cashier', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-118', url: '/exhReport/listCashierWiseReportForAdmin', featureName: 'List cashier wise report for admin', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-119', url: '/exhReport/listCashierWiseReportForCashier', featureName: 'List cashier wise report for cashier', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'EXH-120', url: '/exhReport/showSummaryReportForAdmin', featureName: 'Show summary report for admin', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-121', url: '/exhReport/listReportSummaryForAdmin', featureName: 'List summary report for admin', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-122', url: '/exhReport/downloadRemittanceSummaryReport', featureName: 'Download remittance summary report', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'EXH-123', url: '/exhReport/downloadCashierWiseTaskReport', featureName: 'Download cashier wise task report', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'EXH-212', url: '/exhReport/showCustomerTransactionSummary', featureName: 'Show customer wise transaction report', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-210', url: '/exhReport/listCustomerTransactionSummary', featureName: 'List customer wise transaction report', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-211', url: '/exhReport/downloadCustomerTransactionSummary', featureName: 'Download customer wise transaction report', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //Remitted Task
            new RequestMap(transactionCode: 'EXH-316', url: '/exhReport/showRemittedTask', featureName: 'Show Remitted Task', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-317', url: '/exhReport/listRemittedTask', featureName: 'List Remitted Task', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-318', url: '/exhReport/downloadRemittedTask', featureName: 'Download Remitted Task', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //sanction
            new RequestMap(transactionCode: 'EXH-131', url: '/exhSanctionHmTreasury/showSanctionUpload', featureName: 'Show for sanction upload', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-132', url: '/exhSanctionHmTreasury/uploadSanctionFile', featureName: 'Upload sanction', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-297', url: '/exhSanctionOfacSdn/showSanctionUpload', featureName: 'Show for OFAC SDN sanction upload', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-298', url: '/exhSanctionOfacSdn/uploadSanctionFile', featureName: 'Upload OFAC SDN sanction', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-299', url: '/exhSanctionOfacAdd/showSanctionUpload', featureName: 'Show for OFAC ADD sanction upload', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-300', url: '/exhSanctionOfacAdd/uploadSanctionFile', featureName: 'Upload OFAC ADD sanction', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-301', url: '/exhSanctionOfacAlt/showSanctionUpload', featureName: 'Show for OFAC ALT sanction upload', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-302', url: '/exhSanctionOfacAlt/uploadSanctionFile', featureName: 'Upload OFAC ALT sanction', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-303', url: '/exhSanctionModel/show', featureName: 'Show sanction', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-305', url: '/exhSanctionModel/showMatchedSanction', featureName: 'Show sanction from customer and beneficiary', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-306', url: '/exhSanctionModel/list', featureName: 'List sanction', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-307', url: '/exhSanctionModel/listOfSanction', featureName: 'List of sanction from customer & Beneficiary', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-309', url: '/exhSanctionModel/totalSanctionCount', featureName: 'Count sanction from customer & Beneficiary', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'EXH-249', url: '/exhReport/showTransactionSummary', featureName: 'Transaction Summary', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-250', url: '/exhReport/showRemittanceTransaction', featureName: 'Transaction Summary', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            //postal code
            new RequestMap(transactionCode: 'EXH-243', url: '/exhPostalCode/show', featureName: 'Show Postal Code', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-244', url: '/exhPostalCode/create', featureName: 'Create Postal Code', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-246', url: '/exhPostalCode/update', featureName: 'Update Postal Code', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-247', url: '/exhPostalCode/delete', featureName: 'Delete Postal Code', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EXH-248', url: '/exhPostalCode/list', featureName: 'List Postal Code', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()


            return true

        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    // last used transaction_code = 'PT-213'
    public boolean createRequestMapForProjectTrack() {
        try {
            executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_viewable,is_common)
        VALUES(-26,0,'/ptPlugin/renderProjectTrackMenu','ROLE_-2','Project Track Module Menu',10,'PT-1',TRUE,FALSE)""")

            //PtBacklog
            new RequestMap(transactionCode: 'PT-2', url: '/ptBacklog/show', featureName: 'Show all backlog', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-3', url: '/ptBacklog/list', featureName: 'List all backlog', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-5', url: '/ptBacklog/create', featureName: 'Create a backlog', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-6', url: '/ptBacklog/update', featureName: 'Update a backlog', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-7', url: '/ptBacklog/delete', featureName: 'Delete a backlog', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'PT-52', url: '/ptBacklog/showMyBacklog', featureName: 'Show all my backlog', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-53', url: '/ptBacklog/listMyBacklog', featureName: 'List all my backlog', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-55', url: '/ptBacklog/updateMyBacklog', featureName: 'Update my backlog', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-56', url: '/ptBacklog/removeMyBacklog', featureName: 'Remove from my backlog', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-57', url: '/ptBacklog/addToMyBacklog', featureName: 'add to my backlog', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-156', url: '/ptBacklog/showPtBacklogForRemove', featureName: 'Show all backlog for Remove', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-157', url: '/ptBacklog/listPtBacklogForRemove', featureName: 'List all backlog for Remove', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-158', url: '/ptBacklog/removePtBacklog', featureName: 'Remove backlog', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'PT-39', url: '/ptBacklog/showBackLogForSprint', featureName: 'Show Backlog for sprint', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-46', url: '/ptBacklog/createBackLogForSprint', featureName: 'Create Backlog for sprint', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-47', url: '/ptBacklog/deleteBackLogForSprint', featureName: 'Delete Backlog for sprint', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-48', url: '/ptBacklog/listBackLogForSprint', featureName: 'List Backlog for sprint', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-71', url: '/ptBacklog/backlogListForModule', featureName: 'Get Backlog By moduleId', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'PT-83', url: '/ptBacklog/showForActive', featureName: 'Show Active Backlog', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-84', url: '/ptBacklog/listForActive', featureName: 'List Active Backlog', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-85', url: '/ptBacklog/showForInActive', featureName: 'Show Inactive Backlog', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-86', url: '/ptBacklog/listForInActive', featureName: 'List Inactive Backlog', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-96', url: '/ptBacklog/acceptStory', featureName: 'Accept Story(Backlog)', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'PT-138', url: '/ptBacklog/searchBacklogForGroup', featureName: 'Search backlog for group', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'PT-149', url: '/ptBacklog/markAsDefined', featureName: 'Mark backlog as defined', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-150', url: '/ptBacklog/backlogListForDynamicSearch', featureName: 'Reload Backlog list taglib for dynamic search', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-151', url: '/ptReport/refreshBacklogDetails', featureName: 'Refresh backlog details for search task page', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-166', url: '/ptBacklog/listPtBacklogInPlanForWidget', featureName: 'List backlogs in plan for widget', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-174', url: '/ptBacklog/listPtBacklogInProgressForWidget', featureName: 'List backlogs in progress for widget', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-175', url: '/ptBacklog/listPtBacklogDoneForWidget', featureName: 'List completed backlogs for widget', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-177', url: '/ptBacklog/listPtBacklogForOwner', featureName: 'List in progress backlog for owner for dash board', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-178', url: '/ptBacklog/listPtBacklogForListView', featureName: 'List backlog for list view', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-179', url: '/ptBacklog/listSprintPtBacklogForListView', featureName: 'List sprint backlog for list view', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-205', url: '/ptBacklog/searchBacklogForEntity', featureName: 'Search backlog for entity', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //PtAcceptanceCriteria
            new RequestMap(transactionCode: 'PT-40', url: '/ptAcceptanceCriteria/show', featureName: 'Show all acceptance criteria', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-41', url: '/ptAcceptanceCriteria/list', featureName: 'List all acceptance criteria', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-43', url: '/ptAcceptanceCriteria/create', featureName: 'Create an acceptance criteria', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-44', url: '/ptAcceptanceCriteria/update', featureName: 'Update an acceptance criteria', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-45', url: '/ptAcceptanceCriteria/delete', featureName: 'Delete an acceptance criteria', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-98', url: '/ptAcceptanceCriteria/listForMyBacklog', featureName: 'List all acceptance criteria for my backlog', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-72', url: '/ptAcceptanceCriteria/showForMyBacklog', featureName: 'Show all acceptance criteria for my backlog', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-73', url: '/ptAcceptanceCriteria/updateForMyBacklog', featureName: 'Update an acceptance criteria for my backlog', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //PtFlow
            new RequestMap(transactionCode: 'PT-109', url: '/ptFlow/show', featureName: 'Show all flow', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-110', url: '/ptFlow/list', featureName: 'List all flow', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-112', url: '/ptFlow/create', featureName: 'Create a flow', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-113', url: '/ptFlow/update', featureName: 'Update a flow', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-114', url: '/ptFlow/delete', featureName: 'Delete a flow', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //PtModule
            new RequestMap(transactionCode: 'PT-21', url: '/ptModule/show', featureName: 'Show all Module', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-22', url: '/ptModule/create', featureName: 'Create a Module', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-24', url: '/ptModule/update', featureName: 'Update a Module', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-25', url: '/ptModule/delete', featureName: 'Delete a Module', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-26', url: '/ptModule/list', featureName: 'List all Module', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-68', url: '/ptModule/listModuleByProject', featureName: 'Get Module By projectId', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //PtEntity
            new RequestMap(transactionCode: 'PT-184', url: '/ptEntity/show', featureName: 'Show all Entity', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-185', url: '/ptEntity/list', featureName: 'List all Entity', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-186', url: '/ptEntity/create', featureName: 'Create an Entity', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-187', url: '/ptEntity/update', featureName: 'Update an Entity', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-188', url: '/ptEntity/delete', featureName: 'Delete an Entity', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // PtEntityBacklog
            new RequestMap(transactionCode: 'PT-200', url: '/ptEntityBacklog/show', featureName: 'Show Mapped Backlog With Entity', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-201', url: '/ptEntityBacklog/list', featureName: 'List Mapped Backlog With Entity', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-202', url: '/ptEntityBacklog/create', featureName: 'Create Mapped Backlog With Entity', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-203', url: '/ptEntityBacklog/update', featureName: 'Update Mapped Backlog With Entity', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-204', url: '/ptEntityBacklog/delete', featureName: 'Delete Mapped Backlog With Entity', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //PtPrimaryKey
            new RequestMap(transactionCode: 'PT-194', url: '/ptPrimaryKey/show', featureName: 'Show all Primary Key', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-195', url: '/ptPrimaryKey/list', featureName: 'List all Primary Key', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-196', url: '/ptPrimaryKey/create', featureName: 'Create an Primary Key', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-197', url: '/ptPrimaryKey/update', featureName: 'Update an Primary Key', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-198', url: '/ptPrimaryKey/delete', featureName: 'Delete an Primary Key', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-199', url: '/ptPrimaryKey/dropDownPtField', featureName: 'Drop down for Field', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //PtForeignKey
            new RequestMap(transactionCode: 'PT-206', url: '/ptForeignKey/show', featureName: 'Show all Foreign Key', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-207', url: '/ptForeignKey/list', featureName: 'List all Foreign Key', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-208', url: '/ptForeignKey/create', featureName: 'Create an Foreign Key', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-209', url: '/ptForeignKey/delete', featureName: 'Delete an Foreign Key', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-210', url: '/ptForeignKey/dropDownPtEntity', featureName: 'Drop down for Entity', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-211', url: '/ptField/dropDownFieldReload', featureName: 'Reload Drop down for Field', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // PtField
            new RequestMap(transactionCode: 'PT-189', url: '/ptField/show', featureName: 'Show field', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-190', url: '/ptField/list', featureName: 'List field', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-191', url: '/ptField/create', featureName: 'Create field', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-192', url: '/ptField/update', featureName: 'Update field', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-193', url: '/ptField/delete', featureName: 'Delete field', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //ptProjectModule
            new RequestMap(transactionCode: 'PT-27', url: '/ptProjectModule/show', featureName: 'Show all Project Module', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-28', url: '/ptProjectModule/create', featureName: 'Create a Project Module', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-30', url: '/ptProjectModule/update', featureName: 'Update a Project Module', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-31', url: '/ptProjectModule/delete', featureName: 'Delete a Project Module', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-32', url: '/ptProjectModule/list', featureName: 'List of all Project Module', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-155', url: '/ptProjectModule/dropDownProjectModuleReload', featureName: 'Dropdown for Project Module', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // ptBug
            new RequestMap(transactionCode: 'PT-8', url: '/ptBug/show', featureName: 'Show Project track Bug', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-9', url: '/ptBug/create', featureName: 'Create Project track Bug', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-10', url: '/ptBug/update', featureName: 'Update Project track Bug', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-11', url: '/ptBug/delete', featureName: 'Delete Project track Bug', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-13', url: '/ptBug/list', featureName: 'List Project track Bug', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // ptSteps
            new RequestMap(transactionCode: 'PT-169', url: '/ptSteps/show', featureName: 'Show all steps', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-170', url: '/ptSteps/list', featureName: 'List steps', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-171', url: '/ptSteps/create', featureName: 'Create steps', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-172', url: '/ptSteps/update', featureName: 'Update steps', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-173', url: '/ptSteps/delete', featureName: 'Delete steps', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'PT-75', url: '/ptBug/showBugForMyTask', featureName: 'Show Bug List For My Task', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-76', url: '/ptBug/updateBugForMyTask', featureName: 'Update Bug For My Task', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'PT-78', url: '/ptBug/showBugForSprint', featureName: 'Show Bug for sprint', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-79', url: '/ptBug/createBugForSprint', featureName: 'Create Bug for sprint', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-80', url: '/ptBug/deleteBugForSprint', featureName: 'Delete Bug for sprint', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-81', url: '/ptBug/listBugForSprint', featureName: 'List Bug for sprint', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'PT-99', url: '/ptBug/reOpenBug', featureName: 'Bug re-open', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-100', url: '/ptBug/closeBug', featureName: 'Set bug status close', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'PT-102', url: '/ptBug/showBugDetails', featureName: 'Show bug details', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'PT-104', url: '/ptBug/showOrphanBug', featureName: 'Show orphan bug', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-105', url: '/ptBug/createOrphanBug', featureName: 'Create orphan bug', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-106', url: '/ptBug/updateOrphanBug', featureName: 'Update orphan bug', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-107', url: '/ptBug/listOrphanBug', featureName: 'List orphan bug', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-108', url: '/ptBug/addToMyBug', featureName: 'Add bug to my bug', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'PT-115', url: '/ptBug/showMyBug', featureName: 'Show my bug', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-116', url: '/ptBug/listMyBug', featureName: 'List my bug', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'PT-118', url: '/ptBug/bugListForModule', featureName: 'Get Bug By moduleId', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'PT-152', url: '/ptBug/listBugForSearch', featureName: 'List bug for search', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-153', url: '/ptBug/refreshBugDetails', featureName: 'Show bug details for report', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'PT-180', url: '/ptBug/listSprintBugForListView', featureName: 'Show bug list view for sprint', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-181', url: '/ptBug/listPtBugForListView', featureName: 'Show bug list view', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()

            // PtProject
            new RequestMap(transactionCode: 'PT-33', url: '/ptProject/show', featureName: 'Show Project of Project track', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-34', url: '/ptProject/create', featureName: 'Create Project of Project track', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-35', url: '/ptProject/update', featureName: 'Update Project of Project track', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-36', url: '/ptProject/delete', featureName: 'Delete Project of Project track', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-38', url: '/ptProject/list', featureName: 'List Project of Project track', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // PtSprint
            new RequestMap(transactionCode: 'PT-14', url: '/ptSprint/show', featureName: 'Show Sprint Project track', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-15', url: '/ptSprint/create', featureName: 'Create Sprint of Project track', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-16', url: '/ptSprint/update', featureName: 'Update Sprint of Project track', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-17', url: '/ptSprint/delete', featureName: 'Delete Sprint of Project track', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-18', url: '/ptSprint/list', featureName: 'List Sprint of Project track', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-67', url: '/ptSprint/listSprintByProjectId', featureName: 'Get Sprint By projectId', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-87', url: '/ptSprint/listInActiveSprintByProjectId', featureName: 'Get Inactive Sprint By Project ID', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-213', url: '/ptSprint/showProgress', featureName: 'Show Progress Report on Pie Chart', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // PtChangeRequest
            new RequestMap(transactionCode: 'PT-140', url: '/ptChangeRequest/show', featureName: 'Show ChangeRequest', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-141', url: '/ptChangeRequest/create', featureName: 'Create ChangeRequest', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-142', url: '/ptChangeRequest/update', featureName: 'Update ChangeRequest', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-143', url: '/ptChangeRequest/delete', featureName: 'Delete ChangeRequest', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-144', url: '/ptChangeRequest/list', featureName: 'List ChangeRequest', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //PtReport
            new RequestMap(transactionCode: 'PT-61', url: '/ptReport/showReportOpenBacklog', featureName: 'Show all open backlog', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-62', url: '/ptReport/downloadOpenBacklogReport', featureName: 'download open backlog report', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-63', url: '/ptReport/listReportOpenBacklog', featureName: 'list open backlog report', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-58', url: '/ptReport/downloadSprintDetails', featureName: 'Download Sprint Details', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-59', url: '/ptReport/showReportSprint', featureName: 'Show sprint report', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-60', url: '/ptReport/listSprintDetails', featureName: 'List Sprint Details', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-64', url: '/ptReport/downloadBugDetails', featureName: 'Download Bug Details', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-65', url: '/ptReport/showReportBug', featureName: 'Show Bug Details', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-66', url: '/ptReport/listBugDetails', featureName: 'List Bug Details', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-94', url: '/ptReport/showForBacklogDetails', featureName: 'Show for backlog details', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-101', url: '/ptReport/downloadBacklogDetailsReport', featureName: 'Download backlog details report', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-103', url: '/ptReport/downloadPtBugDetails', featureName: 'Download individual Pt Bug Details', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-120', url: '/ptReport/showPtTaskReport', featureName: 'show task details', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-121', url: '/ptReport/listPtTaskReport', featureName: 'list of all task', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-122', url: '/ptReport/downloadPtTaskReport', featureName: 'Download task details', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-119', url: '/ptReport/downloadBacklogDetailsUatReport', featureName: 'Download backlog details UAT report', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-130', url: '/ptReport/downloadSprintDetailsUatReport', featureName: 'Download sprint details UAT report', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-131', url: '/ptReport/downloadSprintDetailsUseCaseReport', featureName: 'Download sprint details use case report', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-139', url: '/ptReport/downloadPtTaskFeatureList', featureName: 'Download sprint feature list report', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-146', url: '/ptReport/showPtChangeRequestReport', featureName: 'Show ChangeRequest report', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-147', url: '/ptReport/listPtChangeRequestReport', featureName: 'List ChangeRequest report', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-148', url: '/ptReport/downloadPtChangeRequestReport', featureName: 'Download ChangeRequest report', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-154', url: '/ptReport/downloadPtChangeRequestReportForBacklog', featureName: 'Download ChangeRequest report for backlog', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-159', url: '/ptReport/downloadActiveSprintUseCaseReport', featureName: 'Download active sprint use case report', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-182', url: '/ptReport/downloadSprintSummaryReport', featureName: 'Download sprint summary report', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-123', url: '/ptReport/downloadPtTaskUatReport', featureName: 'Download all task for UAT report', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-167', url: '/ptReport/showForBugReport', featureName: 'Show bug report', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-168', url: '/ptReport/listForBugReport', featureName: 'List bug report', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-176', url: '/ptReport/downloadBugReport', featureName: 'Download bug report', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-183', url: '/ptReport/downloadProgressReport', featureName: 'Download progress report', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-212', url: '/ptReport/downloadEntityReport', featureName: 'Download entity report', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            // report assignment
            new RequestMap(transactionCode: 'PT-163', url: '/ptReport/showAssignment', featureName: 'Show Assignment', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-164', url: '/ptReport/listAssignment', featureName: 'List Assignment', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'PT-165', url: '/ptReport/downloadAssignment', featureName: 'Download Assignment Report', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createRequestMapForARMS() {
        try {
            int count = appVersionService.countByPluginId(ArmsPluginConnector.PLUGIN_ID)
            if (count > 0) return true

            executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_viewable,is_common)
        VALUES(-27,0,'/arms/renderArmsMenu','ROLE_-2','ARMS Module Menu',11,'RMS-1',TRUE,FALSE)""")

            //taglib refresh
            new RequestMap(transactionCode: 'RMS-88', url: '/rmsExchangeHouse/reloadExchangeHouseDropDown', featureName: 'Refresh exchange house dropdown', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-89', url: '/rmsExchangeHouse/reloadExchangeHouseFilteredDropDown', featureName: 'Refresh filtered exchange house dropdown', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-122', url: '/rmsTask/reloadTaskDetailsTagLib', featureName: 'Refresh task details', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // rmsExchangeHouse
            new RequestMap(transactionCode: 'RMS-2', url: '/rmsExchangeHouse/show', featureName: 'Show Exchange House', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-3', url: '/rmsExchangeHouse/create', featureName: 'Create Exchange House', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-4', url: '/rmsExchangeHouse/update', featureName: 'Update Exchange House', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-5', url: '/rmsExchangeHouse/delete', featureName: 'Delete Exchange House', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-7', url: '/rmsExchangeHouse/list', featureName: 'List Exchange House', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // rmsExchangeHouseCurrencyPosting
            new RequestMap(transactionCode: 'RMS-10', url: '/rmsExchangeHouseCurrencyPosting/show', featureName: 'Show Exchange House Currency Posting', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-11', url: '/rmsExchangeHouseCurrencyPosting/create', featureName: 'Create Exchange House Currency Posting', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-12', url: '/rmsExchangeHouseCurrencyPosting/update', featureName: 'Update Exchange House Currency Posting', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-13', url: '/rmsExchangeHouseCurrencyPosting/delete', featureName: 'Delete Exchange House Currency Posting', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-15', url: '/rmsExchangeHouseCurrencyPosting/list', featureName: 'List Exchange House Currency Posting', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // rmsProcessInstrumentMapping
            new RequestMap(transactionCode: 'RMS-16', url: '/rmsProcessInstrumentMapping/show', featureName: 'Show Process Instrument Mapping', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-17', url: '/rmsProcessInstrumentMapping/create', featureName: 'Create Process Instrument Mapping', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-18', url: '/rmsProcessInstrumentMapping/update', featureName: 'Update Process Instrument Mapping', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-19', url: '/rmsProcessInstrumentMapping/delete', featureName: 'Delete Process Instrument Mapping', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-21', url: '/rmsProcessInstrumentMapping/list', featureName: 'List Process Instrument Mapping', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // rmsTask
            new RequestMap(transactionCode: 'RMS-23', url: '/rmsTask/show', featureName: 'Show Task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-84', url: '/rmsTask/showForExh', featureName: 'Show Task ExchangeHouse User', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-24', url: '/rmsTask/create', featureName: 'Create Task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-25', url: '/rmsTask/update', featureName: 'Update Task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-26', url: '/rmsTask/delete', featureName: 'Delete Task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-28', url: '/rmsTask/list', featureName: 'List Task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-29', url: '/rmsTask/showForUploadTask', featureName: 'Show upload Task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-30', url: '/rmsTask/createForUploadTask', featureName: 'Create Tasks for upload Task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-31', url: '/rmsTask/listTaskForTaskList', featureName: 'List Task to create TaskList', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-32', url: '/rmsTask/showForMapTask', featureName: 'Show for map Task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-33', url: '/rmsTask/listTaskForMap', featureName: 'List Task to map Task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-34', url: '/rmsTask/mapTask', featureName: 'Map Task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-35', url: '/rmsTask/showForApproveTask', featureName: 'Show for approve Task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-36', url: '/rmsTask/listTaskForApprove', featureName: 'List Task to approve Task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-37', url: '/rmsTask/approve', featureName: 'Approve Task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-38', url: '/rmsTask/reviseTask', featureName: 'Revise Task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-44', url: '/rmsTask/showTaskDetailsWithNote', featureName: 'Show task details', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-45', url: '/rmsTask/searchTaskDetailsWithNote', featureName: 'Search task details', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-64', url: '/rmsTask/createRmsTaskNote', featureName: 'Create Rms Task note', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-78', url: '/rmsTask/renderTaskDetails', featureName: 'Show Task Details', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-79', url: '/rmsTask/disburseRmsTask', featureName: 'Disburse RMS task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-85', url: '/rmsTask/showForUploadTaskForExh', featureName: 'Show upload Task for exh user', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-86', url: '/rmsTask/sendRmsTaskToBank', featureName: 'Send Rms Task to bank for Exh user', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-146', url: '/rmsTask/reviseTaskFromManageTask', featureName: 'Revise Rms Task from manage task UI', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //forward task
            new RequestMap(transactionCode: 'RMS-71', url: '/rmsTask/showTaskDetailsForForward', featureName: 'Show task details for forward', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-72', url: '/rmsTask/searchTaskDetailsForForward', featureName: 'Search task details for forward', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-73', url: '/rmsTask/forwardRmsTask', featureName: 'Forward task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //manage task
            new RequestMap(transactionCode: 'RMS-91', url: '/rmsTask/showForManageTask', featureName: 'Show for manage task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-148', url: '/rmsTask/searchForManageTask', featureName: 'Search for manage task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-106', url: '/rmsTask/cancelRmsTask', featureName: 'Cancel rms task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // rmsTaskList
            new RequestMap(transactionCode: 'RMS-39', url: '/rmsTaskList/show', featureName: 'Show Task List', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-40', url: '/rmsTaskList/create', featureName: 'Create Task List', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-42', url: '/rmsTaskList/showSearchTaskList', featureName: 'Show Task List for search', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-43', url: '/rmsTaskList/listSearchTaskList', featureName: 'List for search Task List', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-90', url: '/rmsTaskList/reloadTaskListDropDown', featureName: 'Reload task list drop down', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // rmsInstrument
            new RequestMap(transactionCode: 'RMS-46', url: '/rmsInstrument/listTaskForProcessInstrument', featureName: 'List Task for process instrument', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-47', url: '/rmsInstrument/showForIssuePo', featureName: 'Show for issue PO', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-48', url: '/rmsInstrument/downloadTaskReportForIssuePo', featureName: 'Download task for issue PO', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-49', url: '/rmsInstrument/showForIssueEft', featureName: 'Show for issue EFT', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-50', url: '/rmsInstrument/downloadTaskReportForIssueEft', featureName: 'Download task for issue EFT', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-51', url: '/rmsInstrument/showForIssueOnline', featureName: 'Show for issue online', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-52', url: '/rmsInstrument/downloadTaskReportForIssueOnline', featureName: 'Download task for issue online', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-118', url: '/rmsInstrument/showForInstrumentPurchase', featureName: 'Show for instrument purchase', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-119', url: '/rmsInstrument/downloadTaskReportForPurchaseInstrument', featureName: 'Download csv task report for instrument purchase', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'RMS-55', url: '/rmsInstrument/showForForwardCashCollection', featureName: 'Show for forward cash collection', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-56', url: '/rmsInstrument/downloadTaskReportForForwardCashCollection', featureName: 'Download task for forward cash collection', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-57', url: '/rmsInstrument/showForForwardOnline', featureName: 'Show for forward online', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-58', url: '/rmsInstrument/downloadTaskReportForForwardOnline', featureName: 'Download task for forward online', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //for dropdown refresh
            new RequestMap(transactionCode: 'RMS-120', url: '/rmsInstrument/reloadInstrumentDropDown', featureName: 'Refresh Instrument dropdown', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-121', url: '/rmsInstrument/reloadBankListFilteredDropDown', featureName: 'Refresh bank dropdown for instrument purchase', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // rmsReport
            new RequestMap(transactionCode: 'RMS-59', url: '/rmsReport/showForListWiseStatusReport', featureName: 'Show for list wise status report', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-60', url: '/rmsReport/listForListWiseStatusReport', featureName: 'List for list wise status report', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-61', url: '/rmsReport/downloadListWiseStatusReport', featureName: 'Download list wise status report', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-62', url: '/rmsReport/showBeneficiaryDetails', featureName: 'Show beneficiary details', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-63', url: '/rmsReport/searchBeneficiaryDetails', featureName: 'Search task for beneficiary details', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-83', url: '/rmsReport/searchBeneficiaryForGrid', featureName: 'Search beneficiary for Grid', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-65', url: '/rmsReport/showTaskListPlan', featureName: 'Show Task List Plan', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-66', url: '/rmsReport/searchTaskListPlan', featureName: 'Search Task List Plan', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-123', url: '/rmsReport/showForViewCancelTask', featureName: 'Show For View Cancel Task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-124', url: '/rmsReport/listForViewCancelTask', featureName: 'List For View Cancel Task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-130', url: '/rmsReport/showBranchWiseTransaction', featureName: 'Show branch wise transaction report', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-131', url: '/rmsReport/listBranchWiseTransaction', featureName: 'List branch wise transaction report', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-132', url: '/rmsReport/downloadBranchWiseTransaction', featureName: 'Download branch wise transaction report', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-138', url: '/rmsReport/downloadBranchWiseRemittance', featureName: 'Download branch wise remittance report', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'RMS-133', url: '/rmsReport/showRmsExhWiseTransactionReport', featureName: 'Show exchange house wise transaction report', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-134', url: '/rmsReport/listRmsExhWiseTransactionReport', featureName: 'List exchange house wise transaction report', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-135', url: '/rmsReport/downloadRmsExhWiseTransactionReport', featureName: 'Download exchange house wise transaction report', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-139', url: '/rmsReport/showSearchTransactionReport', featureName: 'Show search transaction report', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-140', url: '/rmsReport/listSearchTransactionReport', featureName: 'List search transaction report', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-141', url: '/rmsReport/downloadSearchTransactionReport', featureName: 'Download search transaction report', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-142', url: '/rmsReport/downloadPerExhWiseRemittance', featureName: 'Download per EXH wise remittance report', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-147', url: '/rmsReport/downloadTaskListPlan', featureName: 'Download task list plan report', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-149', url: '/rmsReport/showTransactionStatusReport', featureName: 'Show Transaction Status Report', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-150', url: '/rmsReport/listTransactionStatusReport', featureName: 'List Transaction Status Report', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-151', url: '/rmsReport/downloadTransactionStatusReport', featureName: 'Download Transaction Status Report', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //forwarded unpaid task
            new RequestMap(transactionCode: 'RMS-74', url: '/rmsReport/showForForwardUnpaidTask', featureName: 'Show for forwarded unpaid task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-75', url: '/rmsReport/listTaskForForwardUnpaidTask', featureName: 'List forwarded unpaid task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-126', url: '/rmsReport/listTaskDetailsForForwardedUnpaidTasks', featureName: 'List forwarded unpaid task details', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //taskTrace
            new RequestMap(transactionCode: 'RMS-76', url: '/rmsTaskTrace/showRmsTaskHistory', featureName: 'Show Task History', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-77', url: '/rmsTaskTrace/searchRmsTaskHistory', featureName: 'Search Task History', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //disburse cash collection task
            new RequestMap(transactionCode: 'RMS-80', url: '/rmsTask/showDisburseCashCollection', featureName: 'Show for disburse cash collection task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-81', url: '/rmsTask/searchDisburseCashCollection', featureName: 'Search for disburse cash collection task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-82', url: '/rmsTask/disburseCashCollectionRmsTask', featureName: 'Disburse cash collection rms task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //manage task list
            new RequestMap(transactionCode: 'RMS-93', url: '/rmsTaskList/showForManageTaskList', featureName: 'Show for manage task list', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-94', url: '/rmsTaskList/listForManageTaskList', featureName: 'List for manage task list', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-105', url: '/rmsTaskList/removeFromList', featureName: 'Remove from task list', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-108', url: '/rmsTaskList/renameTaskList', featureName: 'Rename task list', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-109', url: '/rmsTaskList/moveTaskToAnotherList', featureName: 'Move task from task list', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //RmsTransactionDay
            new RequestMap(transactionCode: 'RMS-95', url: '/rmsTransactionDay/show', featureName: 'Show transaction day', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-96', url: '/rmsTransactionDay/list', featureName: 'List transaction day', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-97', url: '/rmsTransactionDay/openTransactionDay', featureName: 'Open transcation day', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-98', url: '/rmsTransactionDay/closeTransactionDay', featureName: 'Close transaction day', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-99', url: '/rmsTransactionDay/reOpenTransactionDay', featureName: 'Reopen transaction day', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //RmsTaskListSummaryModel
            new RequestMap(transactionCode: 'RMS-107', url: '/rmsTaskListSummaryModel/listUnResolvedTaskList', featureName: 'List un-resolved task list', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //RmsPurchaseInstrumentMapping
            new RequestMap(transactionCode: 'RMS-110', url: '/rmsPurchaseInstrumentMapping/show', featureName: 'Show Purchase Instrument Mapping', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-111', url: '/rmsPurchaseInstrumentMapping/create', featureName: 'Create Purchase Instrument Mapping', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-112', url: '/rmsPurchaseInstrumentMapping/update', featureName: 'Update Purchase Instrument Mapping', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-113', url: '/rmsPurchaseInstrumentMapping/delete', featureName: 'Delete Purchase Instrument Mapping', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-115', url: '/rmsPurchaseInstrumentMapping/list', featureName: 'List Purchase Instrument Mapping', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-125', url: '/rmsPurchaseInstrumentMapping/evaluateLogic', featureName: 'List Purchase Instrument Mapping', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //rmsTask for view notes
            new RequestMap(transactionCode: 'RMS-116', url: '/rmsTask/showForViewNotes', featureName: 'Show for view notes', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-117', url: '/rmsTask/listForViewNotes', featureName: 'Show for view notes', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //decision summary
            new RequestMap(transactionCode: 'RMS-127', url: '/rmsReport/showDecisionSummary', featureName: 'Show decision summary', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-128', url: '/rmsReport/listDecisionSummary', featureName: 'List decision summary', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-129', url: '/rmsReport/downloadDecisionSummaryReport', featureName: 'Download decision summary pdf report', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //REST reqest map
            new RequestMap(transactionCode: 'RMS-136', url: '/rmsRest/receiveTaskFromExh', featureName: 'Receive task from exchange house through web service', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 11, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();

            //districtWise transaction report
            new RequestMap(transactionCode: 'RMS-143', url: '/rmsReport/showDistrictWiseTransactionReport', featureName: 'Show for district wise transaction report', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-144', url: '/rmsReport/listDistrictWiseTransactionReport', featureName: 'List for district wise transaction report', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-145', url: '/rmsReport/downloadDistrictWiseTransactionReport', featureName: 'Download for district wise transaction report', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //sanction
            new RequestMap(transactionCode: 'RMS-156', url: '/rmsSanctionHmTreasury/showSanctionUpload', featureName: 'Show for sanction upload', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-157', url: '/rmsSanctionHmTreasury/uploadSanctionFile', featureName: 'Upload sanction', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-158', url: '/rmsSanctionOfacSdn/showSanctionUpload', featureName: 'Show for OFAC SDN sanction upload', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-159', url: '/rmsSanctionOfacSdn/uploadSanctionFile', featureName: 'Upload OFAC SDN sanction', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-160', url: '/rmsSanctionOfacAdd/showSanctionUpload', featureName: 'Show for OFAC ADD sanction upload', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-161', url: '/rmsSanctionOfacAdd/uploadSanctionFile', featureName: 'Upload OFAC ADD sanction', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-162', url: '/rmsSanctionOfacAlt/showSanctionUpload', featureName: 'Show for OFAC ALT sanction upload', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-163', url: '/rmsSanctionOfacAlt/uploadSanctionFile', featureName: 'Upload OFAC ALT sanction', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-164', url: '/rmsSanctionModel/show', featureName: 'Show sanction', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-165', url: '/rmsSanctionModel/list', featureName: 'List sanction', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-166', url: '/rmsSanctionModel/showMatchedSanction', featureName: 'Show matched sanction', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'RMS-167', url: '/rmsSanctionModel/listMatchedSanction', featureName: 'List matched sanction', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //last use transaction code 'RMS-167'
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    // last used transaction code = 'SARB-89'
    public boolean createRequestMapForSARB() {
        try {
            int count = appVersionService.countByPluginId(SarbPluginConnector.PLUGIN_ID)
            if (count > 0) return true

            new RequestMap(transactionCode: 'SARB-1', url: '/sarb/renderSarbMenu', featureName: 'Render menu for province', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //province

            new RequestMap(transactionCode: 'SARB-2', url: '/sarbProvince/show', featureName: 'Show province', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-3', url: '/sarbProvince/create', featureName: 'Create province', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-5', url: '/sarbProvince/update', featureName: 'Update province', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-6', url: '/sarbProvince/delete', featureName: 'Delete province', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-7', url: '/sarbProvince/list', featureName: 'List province', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'SARB-8', url: '/sarbTaskModel/showForSendTaskToSarb', featureName: 'Show For Send task to SARB', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-9', url: '/sarbTaskModel/listForSendTaskToSarb', featureName: 'List for Send task to SARB', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-13', url: '/sarbTaskModel/sendTaskToSarb', featureName: 'Send task to SARB', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //showTaskStatus
            new RequestMap(transactionCode: 'SARB-10', url: '/sarbTaskModel/showTaskStatus', featureName: 'Show Task Status', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-11', url: '/sarbTaskModel/listTaskStatus', featureName: 'List Task Status', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'SARB-15', url: '/sarbTaskModel/showTaskForRetrieveResponse', featureName: 'Show Retrieve response', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-16', url: '/sarbTaskModel/retrieveResponse', featureName: 'Send Retrieve response', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-12', url: '/sarbTaskModel/listSarbTaskForRetrieveResponse', featureName: 'List Retrieve response', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-17', url: '/sarbTaskModel/retrieveResponseAgain', featureName: 'Retrieve SARB task response again', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'SARB-18', url: '/sarbTaskModel/moveForResend', featureName: 'Move task to send again', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-19', url: '/sarbTaskModel/moveForCancel', featureName: 'Move Sarb Task For Cancel', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-34', url: '/sarbTaskModel/moveForReplace', featureName: 'Move task to replace task', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-20', url: '/sarbTaskModel/sendCancelTaskToSarb', featureName: 'Send Cancel SarbTask To Sarb', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-36', url: '/sarbTaskModel/sendReplaceTaskToSarb', featureName: 'Send Replace SarbTask To Sarb', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-37', url: '/sarbTaskModel/sendRefundTaskToSarb', featureName: 'Send Refund SarbTask To Sarb', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'SARB-24', url: '/sarbTaskModel/listForCancelTask', featureName: 'List For Cancel Task', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-25', url: '/sarbTaskModel/showTaskForCancel', featureName: 'Show For Cancel Task', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'SARB-26', url: '/sarbTaskModel/listForReplaceTask', featureName: 'List For Replace Task', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-27', url: '/sarbTaskModel/showForReplaceTask', featureName: 'Show For Replace task', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'SARB-30', url: '/sarbTaskModel/showDetailsForRefundTask', featureName: 'Show Task details for refund task', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-31', url: '/sarbTaskModel/createSarbTaskForRefundTask', featureName: 'Create new task for send to sarb for refund', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'SARB-28', url: '/sarbTaskModel/listForRefundTask', featureName: 'List For Refund Task', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-29', url: '/sarbTaskModel/showForRefundTask', featureName: 'Show For Refund task', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'SARB-32', url: '/sarbTaskModel/updateTaskForReplaceTask', featureName: 'Update Task For Replace Task', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-33', url: '/sarbTaskModel/listRefundTaskForShowStatus', featureName: 'List Refund Task For Show Status', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-47', url: '/sarbTaskModel/deleteRefundTask', featureName: 'Delete Refund task', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'SARB-84', url: '/sarbTaskModel/moveForResendToSarb', featureName: 'Forcefully move task for resend from retrieve response', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //SarbCurrencyConversion
            new RequestMap(transactionCode: 'SARB-48', url: '/sarbCurrencyConversion/show', featureName: 'Show for sarb currency conversion', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-65', url: '/sarbCurrencyConversion/create', featureName: 'Create sarb currency conversion', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-50', url: '/sarbCurrencyConversion/list', featureName: 'List for sarb currency conversion', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-51', url: '/sarbCurrencyConversion/update', featureName: 'Update for sarb currency conversion', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-52', url: '/sarbCurrencyConversion/delete', featureName: 'Delete for sarb currency conversion', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-53', url: '/sarbCurrencyConversion/sendToSarb', featureName: 'Send non-reportable currency conversion to sarb', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-54', url: '/sarbCurrencyConversion/showForRetrieveResponse', featureName: 'Show non-reportable currency conversion to retrieve response', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-55', url: '/sarbCurrencyConversion/listForRetrieveResponse', featureName: 'List non-reportable currency conversion to retrieve response', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-56', url: '/sarbCurrencyConversion/retrieveResponse', featureName: 'Retrieve response of non-reportable currency conversion', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-57', url: '/sarbCurrencyConversion/retrieveResponseAgain', featureName: 'Retrieve response again of non-reportable currency conversion', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-58', url: '/sarbCurrencyConversion/moveForResend', featureName: 'Move sarb currency conversion for resend to SARB', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-59', url: '/sarbCurrencyConversion/showForShowStatus', featureName: 'Show non-reportable currency conversion status', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-60', url: '/sarbCurrencyConversion/listForShowStatus', featureName: 'List non-reportable currency conversion status', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-83', url: '/sarbCurrencyConversion/moveForResendToSarb', featureName: 'Forcefully move currency conversion for resend from retrieve response', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //report
            new RequestMap(transactionCode: 'SARB-21', url: '/sarbReport/showTransactionSummary', featureName: 'Show for transaction Summary', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-22', url: '/sarbReport/listTransactionSummary', featureName: 'List for transaction Summary', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-23', url: '/sarbReport/downloadTransactionSummary', featureName: 'Download for transaction Summary', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'SARB-38', url: '/sarbReport/showTransactionDetails', featureName: 'Show for transaction details', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-39', url: '/sarbReport/listTransactionDetails', featureName: 'List for transaction details', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-40', url: '/sarbReport/downloadTransactionDetails', featureName: 'Download for transaction details pdf report', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-76', url: '/sarbReport/downloadTransactionDetailsCsv', featureName: 'Download for transaction details csv report', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'SARB-43', url: '/sarbReport/downloadReplacedReport', featureName: 'Download for cancel or replace task report', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-46', url: '/sarbReport/downloadRefundedReport', featureName: 'Download for refund task report', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'SARB-62', url: '/sarbReport/showForNonReportableReport', featureName: 'Show for NonReportable report', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-63', url: '/sarbReport/listForNonReportableReport', featureName: 'List for NonReportable report', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-64', url: '/sarbReport/downloadNonReportableReport', featureName: 'Download for NonReportable report', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-66', url: '/sarbReport/showReportingDetails', featureName: 'Show Reporting Details', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-67', url: '/sarbReport/listReportingDetails', featureName: 'List Reporting Details', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'SARB-69', url: '/sarbReport/showWarningTaskDetails', featureName: 'Show warning task details', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-70', url: '/sarbReport/listWarningTaskDetails', featureName: 'List warning task details', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-71', url: '/sarbReport/downloadWarningTaskDetails', featureName: 'Download warning task details', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'SARB-72', url: '/sarbReport/downloadUnsubmittedReport', featureName: 'Download unsubmitted pdf report', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-77', url: '/sarbReport/downloadUnsubmittedCsvReport', featureName: 'Download unsubmitted csv report', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'SARB-73', url: '/sarbReport/showTransactionBalance', featureName: 'Show Transanction Balance', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-74', url: '/sarbReport/listTransactionBalance', featureName: 'List Transanction Balance', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-75', url: '/sarbReport/downloadTransactionBalance', featureName: 'Download Transanction Balance', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-82', url: '/sarbReport/downloadTransactionBalanceCsv', featureName: 'Download Transanction Balance CSV', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'SARB-78', url: '/sarbReport/downloadAllReportableTransactions', featureName: 'Download all reportable transactions pdf', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-79', url: '/sarbReport/downloadAllReportableTransactionsCsv', featureName: 'Download all reportable transactions csv', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'SARB-80', url: '/sarbReport/showUnsubmittedTransaction', featureName: 'Show unsubmitted transaction', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-81', url: '/sarbReport/listUnsubmittedTransaction', featureName: 'List unsubmitted transaction', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'SARB-85', url: '/sarbReport/deleteSarbDetails', featureName: 'Delete and activate prevoios sarb details', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //SarbCancelRequest
            new RequestMap(transactionCode: 'SARB-86', url: '/sarbCancelRequest/show', featureName: 'Show sarb cancel request', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-87', url: '/sarbCancelRequest/list', featureName: 'List sarb cancel request', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-88', url: '/sarbCancelRequest/requestForCancel', featureName: 'Send request to sarb for cancel', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'SARB-89', url: '/sarbCancelRequest/retrieveResponse', featureName: 'Get response of cancel request', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Method to create request map for Elearning plugin
     * last used transaction_code = 'EL-196'
     */
    public boolean createRequestMapForElearning() {
        try {
            new RequestMap(transactionCode: 'EL-1', url: '/elearning/renderElearnMenu', featureName: 'Render Menu for E-learning', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-24', url: '/plugins/elearning-0.1/images//**', featureName: 'elearning portal images', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 15, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'EL-53', url: '/el/loginPage', featureName: 'elearning login page', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 15, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'EL-114', url: '/el/index', featureName: 'elearning index page', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 15, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();

            // el portal
            new RequestMap(transactionCode: 'EL-26', url: '/elPortal/showAllCourse', featureName: 'Show All Course for login page', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-27', url: '/elPortal/showLoginPage', featureName: 'Show Login page for Elearning', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-28', url: '/elPortal/showHomePage', featureName: 'Show Home page for Elearning', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-35', url: '/elPortal/listAllCourse', featureName: 'List view of Course', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-36', url: '/elPortal/refreshCourseList', featureName: 'Refresh List view of Course', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-42', url: '/elPortal/showCourseDetails', featureName: 'Show course details', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-44', url: '/elPortal/showSignUpPage', featureName: 'Show Sign up page', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-59', url: '/elPortal/register', featureName: 'Register user for elearning', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-60', url: '/elPortal/activate', featureName: 'activate user by mail for elearning', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()

            // elCourse
            new RequestMap(transactionCode: 'EL-2', url: '/elCourse/show', featureName: 'Show Course', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-112', url: '/elCourse/showConfiguration', featureName: 'Show Course Configuration', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-113', url: '/elCourse/updateConfiguration', featureName: 'Update Course Configuration', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-105', url: '/elCourse/showMember', featureName: 'Show Course Member', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-106', url: '/elCourse/listMember', featureName: 'List/Searc Course Member', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-3', url: '/elCourse/list', featureName: 'List/Search Course', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-4', url: '/elCourse/create', featureName: 'Create Course', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-5', url: '/elCourse/update', featureName: 'Update Course', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-6', url: '/elCourse/delete', featureName: 'Delete Course', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-191', url: '/elCourse/deleteCourseContent', featureName: 'Delete Course With Content', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-7', url: '/elCourse/uploadImage', featureName: 'Upload Course Image', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-8', url: '/elCourse/renderCourseImage', featureName: 'Render Course Image', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-11', url: '/elCourse/select', featureName: 'Select Course', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-12', url: '/elCourse/showImage', featureName: 'Show Course Image', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-29', url: '/elCourse/listRunningCourse', featureName: 'List Running course', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-109', url: '/elCourse/listRunningCourseForOther', featureName: 'List Running course for other', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-61', url: '/elCourse/joinCourse', featureName: 'join course', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-111', url: '/elCourse/resume', featureName: 'join course', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-131', url: '/elCourse/showMemberForRunningCourse', featureName: 'Show member for running course', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-138', url: '/elCourse/cloneCourse', featureName: 'Clone course', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-141', url: '/elCourse/showCourseStats', featureName: 'Show course stats', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-151', url: '/elCourse/showReadUnReadContent', featureName: 'Show Read or UnRead Content', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-152', url: '/elCourse/listReadUnReadContent', featureName: 'List Read or UnRead Content', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-166', url: '/elCourse/leave', featureName: 'Course Leave', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-193', url: '/elCourse/showClone', featureName: 'Show course clone', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-194', url: '/elCourse/listAllCourse', featureName: 'Show all course list', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // ElCourseDetails
            new RequestMap(transactionCode: 'EL-9', url: '/elCourseDetails/show', featureName: 'Show Course Details', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-10', url: '/elCourseDetails/update', featureName: 'Update Details Course', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // elLesson
            new RequestMap(transactionCode: 'EL-13', url: '/elLesson/show', featureName: 'Show lesson', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-14', url: '/elLesson/list', featureName: 'List lesson', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-15', url: '/elLesson/create', featureName: 'Create lesson', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-16', url: '/elLesson/update', featureName: 'Update lesson', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-17', url: '/elLesson/delete', featureName: 'delete lesson', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-18', url: '/elLesson/reloadList', featureName: 'reload taglib for lesson list', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-43', url: '/elLesson/reloadAssignmentQuizExamCount', featureName: 'reload taglib for count of assignment, quiz and exam', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-168', url: '/elLesson/listChildLesson', featureName: 'List child lesson', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-192', url: '/elLesson/reloadListDashboard', featureName: 'List lesson for dashboard', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //show courses
            new RequestMap(transactionCode: 'EL-19', url: '/elCourse/showMyCourse', featureName: 'show my course', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-20', url: '/elCourse/showUpcoming', featureName: 'show upcoming course', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-21', url: '/elCourse/showArchive', featureName: 'show archived course', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-22', url: '/elCourse/showWatchList', featureName: 'show watch list', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-71', url: '/elCourse/showOtherCourse', featureName: 'show other course', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()

            // ElAssignment
            new RequestMap(transactionCode: 'EL-30', url: '/elAssignment/show', featureName: 'Show Assignment', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-31', url: '/elAssignment/list', featureName: 'List/Search Assignment', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-32', url: '/elAssignment/create', featureName: 'Create Assignment', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-33', url: '/elAssignment/update', featureName: 'Update Assignment', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-34', url: '/elAssignment/delete', featureName: 'Delete Assignment', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-68', url: '/elAssignment/reloadAssignment', featureName: 'Reload assignment', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-70', url: '/elAssignment/uploadAssignment', featureName: 'Upload assignment', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-96', url: '/elAssignment/showForStudent', featureName: 'Show assignment for student', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-103', url: '/elAssignment/sendNotification', featureName: 'Send assignment notification to all', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-104', url: '/elAssignment/showActivity', featureName: 'Show Activity for all', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-140', url: '/elAssignment/reSubmit', featureName: 'Make student re-submit assignment', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-153', url: '/elAssignment/showSubmitUnSubmitAssignment', featureName: 'Show Submit-UnSubmit Assignment', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-154', url: '/elAssignment/listSubmitUnSubmitAssignment', featureName: 'List Submit-UnSubmit Assignment', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // ElAssignmentUserMapping
            new RequestMap(transactionCode: 'EL-62', url: '/elAssignmentUser/show', featureName: 'Show Assignment User Mapping', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-63', url: '/elAssignmentUser/list', featureName: 'List/Search Assignment User Mapping', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-64', url: '/elAssignmentUser/create', featureName: 'Create Assignment User Mapping', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-65', url: '/elAssignmentUser/update', featureName: 'Update Assignment User Mapping', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-66', url: '/elAssignmentUser/delete', featureName: 'Delete Assignment User Mapping', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-67', url: '/elAssignmentUser/dropDownAssignmentUserReload', featureName: 'Reload Dropdown Assignment User', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-73', url: '/elAssignmentUser/listForTeacher', featureName: 'List Assignment User for teacher', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-74', url: '/elAssignmentUser/assignMark', featureName: 'Assign mark for assignment User', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-75', url: '/elAssignmentUser/downloadAssignment', featureName: 'Download assignment', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // ElExam
            new RequestMap(transactionCode: 'EL-45', url: '/elExam/show', featureName: 'Show Exam', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-46', url: '/elExam/list', featureName: 'List/Search Exam', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-47', url: '/elExam/create', featureName: 'Create Exam', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-48', url: '/elExam/update', featureName: 'Update Exam', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-52', url: '/elExam/updateConfiguration', featureName: 'Update Exam', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-49', url: '/elExam/delete', featureName: 'Delete Exam', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-50', url: '/elExam/showConfiguration', featureName: 'Show Exam Configuration', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-51', url: '/elExam/select', featureName: 'Edit Exam', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-72', url: '/elExam/showExam', featureName: 'Show exam for running course', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-98', url: '/elExam/listQuestionForDropDown', featureName: 'Get list of question by Exam for drop down', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-99', url: '/elExam/showQuestionWithAnswer', featureName: 'Show question with answer for exam', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-100', url: '/elExam/submitExam', featureName: 'Submit Exam', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-155', url: '/elExam/showParticipateExam', featureName: 'Show Participate Exam', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-156', url: '/elExam/listParticipateExam', featureName: 'List Participate Exam', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-177', url: '/elExam/showAllQuestionForExam', featureName: 'Show all question for Exam', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-178', url: '/elExam/submitExamForAllQuestion', featureName: 'Submit Exam for all question', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-179', url: '/elExam/showAllQuestionForExamWithAnswer', featureName: 'Show all question for Exam with answer', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-185', url: '/elExam/sendNotification', featureName: 'Send Exam Notification to ALL Participants', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // ELUserExam
            new RequestMap(transactionCode: 'EL-108', url: '/elUserExam/listForTeacher', featureName: 'Get list of exam for teacher', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // ElExamQuestionMapping
            new RequestMap(transactionCode: 'EL-54', url: '/elExamQuestionMapping/show', featureName: 'Show Exam Question Mapping', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-55', url: '/elExamQuestionMapping/list', featureName: 'List/Search Exam Question Mapping', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-58', url: '/elExamQuestionMapping/listQuestionByLesson', featureName: 'List Questions By Sub Category or Lesson', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-56', url: '/elExamQuestionMapping/create', featureName: 'Create Exam Question Mapping', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-57', url: '/elExamQuestionMapping/delete', featureName: 'Delete Exam Question Mapping', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // ElQuiz
            new RequestMap(transactionCode: 'EL-76', url: '/elQuiz/show', featureName: 'Show Quiz', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-77', url: '/elQuiz/list', featureName: 'List/Search Quiz', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-78', url: '/elQuiz/create', featureName: 'Create Quiz', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-79', url: '/elQuiz/update', featureName: 'Update Quiz', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-80', url: '/elQuiz/delete', featureName: 'Delete Quiz', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-91', url: '/elQuiz/showQuizWithAnswer', featureName: 'Show Quiz with answer', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-92', url: '/elQuiz/showQuiz', featureName: 'Show Quiz for Lesson', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-93', url: '/elQuiz/listQuestionForDropDown', featureName: 'Get list of question by Quiz for drop down', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-97', url: '/elQuiz/submitQuiz', featureName: 'Submit Quiz', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-157', url: '/elQuiz/showParticipateQuiz', featureName: 'Show Participate Quiz', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-158', url: '/elQuiz/listParticipateQuiz', featureName: 'List Participate Quiz', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-186', url: '/elQuiz/sendNotification', featureName: 'Send Quiz Notification to ALL Participants', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // ElUserQuiz
            new RequestMap(transactionCode: 'EL-107', url: '/elUserQuiz/listForTeacher', featureName: 'Get list of quiz for teacher', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // ElQuizQuestion
            new RequestMap(transactionCode: 'EL-81', url: '/elQuizQuestion/show', featureName: 'Show Quiz Question', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-82', url: '/elQuizQuestion/list', featureName: 'List/Search Quiz Question', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-83', url: '/elQuizQuestion/listQuestionByLesson', featureName: 'List Questions By Sub Category or Lesson', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-84', url: '/elQuizQuestion/create', featureName: 'Create Quiz Question', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-85', url: '/elQuizQuestion/delete', featureName: 'Delete Quiz Question', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // ElAssignmentContent
            new RequestMap(transactionCode: 'EL-86', url: '/elAssignment/showAssignmentContent', featureName: 'Show Assignment Content', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-87', url: '/elAssignment/listAssignmentContent', featureName: 'List/Search Assignment Content', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-88', url: '/elAssignment/listDocumentByCategory', featureName: 'List Document By Sub Category or Lesson', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-89', url: '/elAssignment/createAssignmentContent', featureName: 'Create Assignment Content', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-90', url: '/elAssignment/deleteAssignmentContent', featureName: 'Delete Assignment Content', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-159', url: '/elAssignment/showParticipateActivity', featureName: 'Show Participate Activity', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-160', url: '/elAssignment/listParticipateActivity', featureName: 'List Participate Activity', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // ElResource
            new RequestMap(transactionCode: 'EL-37', url: '/elResource/show', featureName: 'Show Resource', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-38', url: '/elResource/list', featureName: 'List/Search Resource', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-39', url: '/elResource/create', featureName: 'Create Resource', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-40', url: '/elResource/delete', featureName: 'Delete Resource', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-41', url: '/elResource/listDocumentByCategory', featureName: 'List Document By Category', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-126', url: '/elResource/video', featureName: 'Render video', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-127', url: '/elResource/audio', featureName: 'Render Audio', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-128', url: '/elResource/file', featureName: 'Render File', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-129', url: '/elResource/image', featureName: 'Render Image', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-130', url: '/elResource/article', featureName: 'Render Article', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // ElReport
            new RequestMap(transactionCode: 'EL-69', url: '/elReport/downloadMyAssignment', featureName: 'Download My Assignment Report', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-94', url: '/elReport/downloadMyExam', featureName: 'Download My Exam Report', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-95', url: '/elReport/downloadMyQuiz', featureName: 'Download My Quiz Report', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-101', url: '/elReport/downloadMyCertificate', featureName: 'Download My Certificate Report', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-167', url: '/elReport/checkDownloadCertificate', featureName: 'Check Download Certificate Report', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-102', url: '/elReport/downloadMyMarkSheet', featureName: 'Download My Mark Sheet Report', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-110', url: '/elReport/downloadQuizReportOfStudent', featureName: 'Download Quiz Report for student', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-139', url: '/elReport/downloadResult', featureName: 'Download Progress Report', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-161', url: '/elReport/downloadExamReportOfStudent', featureName: 'Download Exam Report of Student', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-165', url: '/elReport/downloadCourseStudentSummary', featureName: 'Download Student Course Summary', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-189', url: '/elReport/downloadMyRoutine', featureName: 'Download My Routine Report', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-190', url: '/elReport/downloadCourseRoutine', featureName: 'Download Course Routine Report', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // elBlog
            new RequestMap(transactionCode: 'EL-115', url: '/elBlog/show', featureName: 'Show Blog', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-116', url: '/elBlog/list', featureName: 'List Blog', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-195', url: '/elBlog/create', featureName: 'Create Blog', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-196', url: '/elBlog/update', featureName: 'Update Blog', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-117', url: '/elBlog/showBlogByLesson', featureName: 'Show Blog', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-118', url: '/elBlog/listBlogByLesson', featureName: 'List Blog By Lesson', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-119', url: '/elBlog/viewBlog', featureName: 'View Blog', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-120', url: '/elBlog/select', featureName: 'Edit Blog', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'EL-121', url: '/elForum/show', featureName: 'Show Forum', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-132', url: '/elForum/listForumByLesson', featureName: 'Search/List Forum By Lesson', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-122', url: '/elForum/list', featureName: 'List/Search Forum', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-123', url: '/elForum/create', featureName: 'Create Forum', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-124', url: '/elForum/update', featureName: 'Update Forum', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-125', url: '/elForum/delete', featureName: 'Delete Forum', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // elPost
            new RequestMap(transactionCode: 'EL-133', url: '/elPost/show', featureName: 'Show Post', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-134', url: '/elPost/list', featureName: 'List Post', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-135', url: '/elPost/showPostByLesson', featureName: 'Show Post By Lesson', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-136', url: '/elPost/listPostByLesson', featureName: 'List Post By Lesson', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-137', url: '/elPost/viewPost', featureName: 'View Post', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // ElAnnouncement
            new RequestMap(transactionCode: 'EL-142', url: '/elAnnouncement/show', featureName: 'Show El Announcement', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-170', url: '/elAnnouncement/showInbox', featureName: 'Show El Announcement Inbox', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-143', url: '/elAnnouncement/list', featureName: 'List/Search El Announcement', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-171', url: '/elAnnouncement/listInbox', featureName: 'List El Announcement Inbox', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-144', url: '/elAnnouncement/create', featureName: 'Create El Announcement', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-145', url: '/elAnnouncement/update', featureName: 'Update El Announcement', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-146', url: '/elAnnouncement/delete', featureName: 'Delete El Announcement', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-147', url: '/elAnnouncement/send', featureName: 'Send El Announcement', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-148', url: '/elAnnouncement/showForReCompose', featureName: 'Show Recompose El Announcement', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-149', url: '/elAnnouncement/listForSend', featureName: 'List Send El Announcement', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-150', url: '/elAnnouncement/reCompose', featureName: 'Re-compose El Announcement', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //ElMail
            new RequestMap(transactionCode: 'EL-162', url: '/elMail/showForComposeMail', featureName: 'Show for compose mail', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-163', url: '/elMail/showForSentMail', featureName: 'Show For Send Mail', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-164', url: '/elMail/showMessage', featureName: 'Show inbox message', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()

            // elMyFavourite
            new RequestMap(transactionCode: 'EL-169', url: '/elMyFavourite/show', featureName: 'Show My Favourites', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // elRoutine
            new RequestMap(transactionCode: 'EL-172', url: '/elRoutine/show', featureName: 'Show El Routine', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-173', url: '/elRoutine/list', featureName: 'List/Search El Routine', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-174', url: '/elRoutine/create', featureName: 'Create El Routine', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-175', url: '/elRoutine/update', featureName: 'Update El Routine', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-176', url: '/elRoutine/delete', featureName: 'Delete El Routine', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-187', url: '/elRoutine/showRoutine', featureName: 'Show El Routine for Course', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-188', url: '/elRoutine/listRoutine', featureName: 'List El Routine for Course', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // elRoutineDetails
            new RequestMap(transactionCode: 'EL-180', url: '/elRoutineDetails/show', featureName: 'Show El Routine Details', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-181', url: '/elRoutineDetails/list', featureName: 'List/Search El Routine Details', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-182', url: '/elRoutineDetails/create', featureName: 'Create El Routine Details', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-183', url: '/elRoutineDetails/update', featureName: 'Update El Routine Details', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'EL-184', url: '/elRoutineDetails/delete', featureName: 'Delete El Routine Details', configAttribute: 'ROLE_-2', pluginId: 15, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Method to create request map for Document plugin
     * last used transaction_code = 'DOC-221'
     */
    public boolean createRequestMapForDocument() {
        try {
            new RequestMap(transactionCode: 'DOC-1', url: '/document/renderDocumentMenu', featureName: 'Render menu for Document', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-221', url: '/plugins/document-0.1/images//**', featureName: 'document images', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 13, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();

            // docCategory
            new RequestMap(transactionCode: 'DOC-2', url: '/docCategory/show', featureName: 'Show Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-3', url: '/docCategory/list', featureName: 'List/Search Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-4', url: '/docCategory/create', featureName: 'Create Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-5', url: '/docCategory/update', featureName: 'Update Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-6', url: '/docCategory/delete', featureName: 'Delete Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-7', url: '/docCategory/viewCategoryDetails', featureName: 'View Category details', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-8', url: '/docCategory/showCategories', featureName: 'Show Categories', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-9', url: '/docCategory/showCategory/**', featureName: 'Show Category For Generic User', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-10', url: '/docCategory/showAcceptInvitation', featureName: 'Show Invitation Accept Page', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-11', url: '/docCategory/acceptInvitation', featureName: 'Accept Invitation', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-151', url: '/docDocument/renderDocument', featureName: 'Render document', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-167', url: '/showDoc/*', featureName: 'Show document', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-189', url: '/showDocViewer/*', featureName: 'Show document viewer', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-190', url: '/docDocument/renderDocumentViewer', featureName: 'Render document for viewer', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // docSubCategory
            new RequestMap(transactionCode: 'DOC-12', url: '/docSubCategory/show', featureName: 'Show Sub Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'DOC-13', url: '/docSubCategory/list', featureName: 'List/Search Sub Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-209', url: '/docSubCategory/listSubCategoryByCategory', featureName: 'List/Search Sub Category For FAQ', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-14', url: '/docSubCategory/create', featureName: 'Create Sub Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-15', url: '/docSubCategory/update', featureName: 'Update Sub Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-16', url: '/docSubCategory/delete', featureName: 'Delete Sub Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-17', url: '/docSubCategory/showSubCategories', featureName: 'Show Sub Categories', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-18', url: '/docSubCategory/viewSubCategoryDetails', featureName: 'View Sub Category Details', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-19', url: '/docSubCategory/dropDownSubCategoryReload', featureName: 'DropDown Sub Category reload', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-20', url: '/docSubCategory/addOrRemoveSubCategoryFavourite', featureName: 'Add or Remove Sub Category to Favourite List', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-21', url: '/docSubCategory/listSubCategoryFavourite', featureName: 'List and Search Sub Category from Favourite List', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-22', url: '/docSubCategory/showSubCategory/**', featureName: 'Show Sub Category For Generic User', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-216', url: '/docSubCategory/reloadDocSubCategoryTreeList', featureName: 'Reload sub category TreeList TagLib ', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // docFaq
            new RequestMap(transactionCode: 'DOC-208', url: '/docFaq/show', featureName: 'Show Document FAQ', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-210', url: '/docFaq/list', featureName: 'List/Search DOC FAQ', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-211', url: '/docFaq/create', featureName: 'Create DOC FAQ', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-212', url: '/docFaq/update', featureName: 'Update DOC FAQ', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-213', url: '/docFaq/delete', featureName: 'Delete DOC FAQ', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-215', url: '/docFaq/showFaqForCourse', featureName: 'Show FAQ for Course', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // docCategoryUserMapping
            new RequestMap(transactionCode: 'DOC-23', url: '/docCategoryUserMapping/show', featureName: 'Show App User Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-24', url: '/docCategoryUserMapping/list', featureName: 'List/Search App User Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-25', url: '/docCategoryUserMapping/create', featureName: 'Create App User Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-26', url: '/docCategoryUserMapping/update', featureName: 'Update App User Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-27', url: '/docCategoryUserMapping/delete', featureName: 'Delete App User Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-28', url: '/docCategoryUserMapping/dropDownAppUserForCategoryReload', featureName: 'View App User For Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()

            // docSubCategoryUserMapping
            new RequestMap(transactionCode: 'DOC-29', url: '/docSubCategoryUserMapping/show', featureName: 'Show App User Sub Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-30', url: '/docSubCategoryUserMapping/list', featureName: 'List/Search App User Sub Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-31', url: '/docSubCategoryUserMapping/create', featureName: 'Create App User Sub Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-32', url: '/docSubCategoryUserMapping/update', featureName: 'Update App User Sub Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-33', url: '/docSubCategoryUserMapping/delete', featureName: 'Delete App User Sub Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-34', url: '/docSubCategoryUserMapping/dropDownAppUserForSubCategoryReload', featureName: 'View App User For Sub Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()

            // docInvitedMembers
            new RequestMap(transactionCode: 'DOC-35', url: '/docInvitedMembers/show', featureName: 'Show Send Invitation', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-36', url: '/docInvitedMembers/showInvitation', featureName: 'Show Invitations', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-37', url: '/docInvitedMembers/listInvitation', featureName: 'List of Outstanding Invitations', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-38', url: '/docInvitedMembers/showResendInvitation', featureName: 'Show resend invitation', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-39', url: '/docInvitedMembers/sendInvitation', featureName: 'Send Invitation', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-40', url: '/docInvitedMembers/delete', featureName: 'Delete Invitations', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()

            // docMemberJoinRequest
            new RequestMap(transactionCode: 'DOC-41', url: '/docMemberJoinRequest/show', featureName: 'Show Requested Members', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-42', url: '/docMemberJoinRequest/searchJoinRequest', featureName: 'Search Requested Member', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-43', url: '/docMemberJoinRequest/approvedForMembership', featureName: 'Approved Request for Member', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-44', url: '/docMemberJoinRequest/applyForCategoryMembership', featureName: 'Sent Request for Category Membership', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-45', url: '/docMemberJoinRequest/applyForSubCategoryMembership', featureName: 'Sent Request for Sub-Category Membership', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-46', url: '/docMemberJoinRequest/delete', featureName: 'Delete Member Join Request', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // docDocument
            new RequestMap(transactionCode: 'DOC-55', url: '/docDocument/showArticle', featureName: 'Show Articles For Document', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-63', url: '/docDocument/createArticle', featureName: 'Create Article For Document', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-69', url: '/docDocument/updateArticle', featureName: 'Update Article For Document', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'DOC-70', url: '/docDocument/showFile', featureName: 'Show File For Document', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-76', url: '/docDocument/createFile', featureName: 'Create File For Document', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-125', url: '/docDocument/updateFile', featureName: 'Update File For Document', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'DOC-127', url: '/docDocument/showAudio', featureName: 'Show Audio For Document', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-129', url: '/docDocument/createAudio', featureName: 'Create Audio For Document', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-133', url: '/docDocument/updateAudio', featureName: 'Update Audio For Document', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'DOC-140', url: '/docDocument/showVideo', featureName: 'Show Video For Document', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-141', url: '/docDocument/createVideo', featureName: 'Create Video For Document', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-158', url: '/docDocument/updateVideo', featureName: 'Update Video For Document', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'DOC-165', url: '/docDocument/showImage', featureName: 'Show Image For Document', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-196', url: '/docDocument/createImage', featureName: 'Create Image For Document', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-197', url: '/docDocument/updateImage', featureName: 'Update Image For Document', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'DOC-177', url: '/docDocument/uploadFile', featureName: 'Upload File', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-178', url: '/docDocument/uploadImage', featureName: 'Upload Image', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-179', url: '/docDocument/uploadAudio', featureName: 'Upload Audio', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-180', url: '/docDocument/uploadVideo', featureName: 'Upload Video', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // docDocumentVersion
            new RequestMap(transactionCode: 'DOC-47', url: '/docDocumentVersion/showFile', featureName: 'Show File', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-48', url: '/docDocumentVersion/listFile', featureName: 'List of Document Content i.e. File, Audio, Video, Image', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-153', url: '/docDocument/listFileDetails', featureName: 'Show List of File Details', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-154', url: '/docDocument/listImageDetails', featureName: 'Show List of Image Details', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-49', url: '/docDocumentVersion/createFile', featureName: 'Create File', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-50', url: '/docDocumentVersion/updateFile', featureName: 'Update Document File', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-51', url: '/docDocument/movedToTrashFile', featureName: 'Moved to Trash File', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-198', url: '/docDocumentVersion/uploadFile', featureName: 'Upload File', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-199', url: '/docDocumentVersion/uploadImage', featureName: 'Upload Image', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-200', url: '/docDocumentVersion/uploadAudio', featureName: 'Upload Audio', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-201', url: '/docDocumentVersion/uploadVideo', featureName: 'Upload Video', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-188', url: '/docDocument/reloadDocumentViewer', featureName: 'Show document viewer', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'DOC-191', url: '/docDocument/showDocumentViewer', featureName: 'Show document viewer', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-202', url: '/docDocument/showDiscussion', featureName: 'Show discussion for document', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-203', url: '/docDocument/downloadDocumentForViewer', featureName: 'Download document for viewer', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-204', url: '/docReport/downloadArticleForViewer', featureName: 'Download article for viewer', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'DOC-52', url: '/docDocumentVersion/showArticle', featureName: 'Show Articles', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-53', url: '/docDocumentVersion/listArticle', featureName: 'List of Articles', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-152', url: '/docDocument/listArticleDetails', featureName: 'Show List of Articles', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-54', url: '/docDocumentVersion/createArticle', featureName: 'Create Article', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-56', url: '/docDocumentVersion/updateArticle', featureName: 'Update Article', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-57', url: '/docDocument/movedToTrashArticle', featureName: 'Moved to Trash Article', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'DOC-58', url: '/docDocument/showForViewArticle', featureName: 'Preview Article', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-59', url: '/docDocument/downloadDocument', featureName: 'Download document ', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-60', url: '/docDocument/showDocumentQueryResult', featureName: 'Show article query result', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-61', url: '/docDocument/searchDocumentQueryResult', featureName: 'Search article query result', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-64', url: '/docDocument/authenticate', featureName: 'Authenticate File Privilege for Annotation or View', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'DOC-123', url: '/docDocument/showDetails', featureName: 'Show Document Details', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-126', url: '/docDocument/createComment', featureName: 'Add Document Comment', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'DOC-128', url: '/docDocumentVersion/showAudio', featureName: 'Show document Audio', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-155', url: '/docDocument/listAudioDetails', featureName: 'Show List of Audio Details', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-130', url: '/docDocumentVersion/createAudio', featureName: 'Create Audio', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-131', url: '/docDocumentVersion/updateAudio', featureName: 'Update Document Audio', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-142', url: '/docDocument/movedToTrashAudio', featureName: 'Moved to Trash Audio File', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-146', url: '/docDocument/showForViewVideo', featureName: 'View Video File', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-147', url: '/docDocument/showForViewAudio', featureName: 'View Audio File', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-150', url: '/docDocument/showForViewFile', featureName: 'View File', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'DOC-132', url: '/docDocumentVersion/showVideo', featureName: 'Show document Video', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-156', url: '/docDocument/listVideoDetails', featureName: 'Show List of Video Details', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-134', url: '/docDocumentVersion/createVideo', featureName: 'Create Video', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-135', url: '/docDocumentVersion/updateVideo', featureName: 'Update Document Video', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-143', url: '/docDocument/movedToTrashVideo', featureName: 'Moved to Trash Video File', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()


            new RequestMap(transactionCode: 'DOC-157', url: '/docDocumentVersion/showImage', featureName: 'Show Document Image', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-159', url: '/docDocumentVersion/createImage', featureName: 'Create Image', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-160', url: '/docDocumentVersion/updateImage', featureName: 'Update Document Image', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-161', url: '/docDocument/movedToTrashImage', featureName: 'Moved to Trash Image File', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-162', url: '/docDocument/showForViewImage', featureName: 'View Image File', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-173', url: '/docDocument/checkInForArticle', featureName: 'Check in Article document', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-174', url: '/docDocument/checkOutForArticle', featureName: 'Check out Article document', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-175', url: '/docDocument/checkInForFile', featureName: 'Check out File document', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-176', url: '/docDocument/checkOutForFile', featureName: 'Check out File document', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-186', url: '/docDocument/reloadDocumentBySubcategory', featureName: 'Reload document list by subCategory taglib', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-217', url: '/docDocument/listDocumentBySubCategory', featureName: 'Content List View By Course', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-218', url: '/docDocumentVersion/listForModal', featureName: 'List doc document for modal', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'DOC-157', url: '/docDocumentVersion/showImage', featureName: 'Show Document Image', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-159', url: '/docDocumentVersion/createImage', featureName: 'Create Image', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-160', url: '/docDocumentVersion/updateImage', featureName: 'Update Document Image', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-161', url: '/docDocument/movedToTrashImage', featureName: 'Moved to Trash Image File', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-162', url: '/docDocument/showForViewImage', featureName: 'View Image File', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-173', url: '/docDocument/checkInForArticle', featureName: 'Check in Article document', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-174', url: '/docDocument/checkOutForArticle', featureName: 'Check out Article document', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-175', url: '/docDocument/checkInForFile', featureName: 'Check out File document', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-176', url: '/docDocument/checkOutForFile', featureName: 'Check out File document', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-186', url: '/docDocument/reloadDocumentBySubcategory', featureName: 'Reload document list by subCategory taglib', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-217', url: '/docDocument/listDocumentBySubCategory', featureName: 'Content List View By Course', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-218', url: '/docDocumentVersion/listForModal', featureName: 'List doc document for modal', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'DOC-157', url: '/docDocumentVersion/showImage', featureName: 'Show Document Image', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-159', url: '/docDocumentVersion/createImage', featureName: 'Create Image', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-160', url: '/docDocumentVersion/updateImage', featureName: 'Update Document Image', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-161', url: '/docDocument/movedToTrashImage', featureName: 'Moved to Trash Image File', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-162', url: '/docDocument/showForViewImage', featureName: 'View Image File', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-173', url: '/docDocument/checkInForArticle', featureName: 'Check in Article document', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-174', url: '/docDocument/checkOutForArticle', featureName: 'Check out Article document', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-175', url: '/docDocument/checkInForFile', featureName: 'Check out File document', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-176', url: '/docDocument/checkOutForFile', featureName: 'Check out File document', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-186', url: '/docDocument/reloadDocumentBySubcategory', featureName: 'Reload document list by subCategory taglib', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-218', url: '/docDocumentVersion/listForModal', featureName: 'List doc document for modal', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()


            // docDocumentOwnership
            new RequestMap(transactionCode: 'DOC-124', url: '/docDocument/changeOwner', featureName: 'Change ownership of Document', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // docDocumentReadLog
            new RequestMap(transactionCode: 'DOC-148', url: '/docDocumentReadLog/create', featureName: 'Mark as Read Document', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-149', url: '/docDocumentReadLog/delete', featureName: 'Mark as Unread Document', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //DocDocumentTrash
            new RequestMap(transactionCode: 'DOC-65', url: '/docDocumentTrash/showArticle', featureName: 'Show Trash Articles', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-66', url: '/docDocumentTrash/listArticle', featureName: 'List Of Trash Articles', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-67', url: '/docDocumentTrash/restoreArticle', featureName: 'Restore Article From Trash', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-68', url: '/docDocumentTrash/deleteArticle', featureName: 'Delete Article', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'DOC-71', url: '/docDocumentTrash/showFile', featureName: 'Show Trash Files', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-72', url: '/docDocumentTrash/listFile', featureName: 'List Of Trash Files', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-73', url: '/docDocumentTrash/restoreFile', featureName: 'Restore File From Trash', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-74', url: '/docDocumentTrash/deleteFile', featureName: 'Delete File', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'DOC-136', url: '/docDocumentTrash/showAudio', featureName: 'Show Trash Audio Files', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-137', url: '/docDocumentTrash/listAudio', featureName: 'List Of Trash Audio Files', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-144', url: '/docDocumentTrash/restoreAudio', featureName: 'Restore Audio File From Trash', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'DOC-138', url: '/docDocumentTrash/showVideo', featureName: 'Show Trash Video Files', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-139', url: '/docDocumentTrash/listVideo', featureName: 'List Of Trash Video Files', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-145', url: '/docDocumentTrash/restoreVideo', featureName: 'Restore Video File From Trash', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            new RequestMap(transactionCode: 'DOC-163', url: '/docDocumentTrash/showImage', featureName: 'Show Trash Image Files', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-164', url: '/docDocumentTrash/listImage', featureName: 'List Of Trash Image Files', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-166', url: '/docDocumentTrash/restoreImage', featureName: 'Restore Image File From Trash', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // docDataIndex
            new RequestMap(transactionCode: 'DOC-77', url: '/docDataIndex/show', featureName: 'Show Data Index', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-78', url: '/docDataIndex/list', featureName: 'List Data Index', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-79', url: '/docDataIndex/create', featureName: 'Create Data Index', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-80', url: '/docDataIndex/update', featureName: 'Update Data Index', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-81', url: '/docDataIndex/delete', featureName: 'Delete Data Index', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-82', url: '/docDataIndex/preview', featureName: 'Preview Data Index', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-83', url: '/docDataIndex/generateDataIndex', featureName: 'Generate All Data Index', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // docDocumentQuery
            new RequestMap(transactionCode: 'DOC-84', url: '/docDocumentQuery/show', featureName: 'Show/Search Document Query', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-85', url: '/docDocumentQuery/list', featureName: 'List Document Query', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-86', url: '/docDocumentQuery/create', featureName: 'Create Document Query', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-87', url: '/docDocumentQuery/update', featureName: 'Update Document Query', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-88', url: '/docDocumentQuery/delete', featureName: 'Delete Document Query', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // docQuestions
            new RequestMap(transactionCode: 'DOC-89', url: '/docQuestion/show', featureName: 'Show Questions', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-187', url: '/docQuestion/showForLanding', featureName: 'Show Questions From Left Menu', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-90', url: '/docQuestion/list', featureName: 'List Questions', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-91', url: '/docQuestion/create', featureName: 'Create Questions', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-92', url: '/docQuestion/update', featureName: 'Update Questions', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-93', url: '/docQuestion/delete', featureName: 'Delete Questions', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-207', url: '/docQuestion/showPopUpQuiz', featureName: 'Show pop up quiz', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // docAnswers
            new RequestMap(transactionCode: 'DOC-94', url: '/docAnswer/show', featureName: 'Show Answers', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-95', url: '/docAnswer/list', featureName: 'List Answers', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-96', url: '/docAnswer/create', featureName: 'Create Answers', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-97', url: '/docAnswer/update', featureName: 'Update Answers', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-98', url: '/docAnswer/delete', featureName: 'Delete Answers', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // report
            new RequestMap(transactionCode: 'DOC-99', url: '/docReport/downloadQuestion', featureName: 'Download Question', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-115', url: '/docReport/downloadAnswer', featureName: 'Download Answer', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-116', url: '/docReport/downloadQuestionSet', featureName: 'Download Question Set', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-117', url: '/docReport/downloadAnswerSet', featureName: 'Download Answer Set', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-100', url: '/docReport/downloadArticle', featureName: 'Download article', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //DocDocument Type
            new RequestMap(transactionCode: 'DOC-101', url: '/docDocumentType/show', featureName: 'Show Document Type', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-102', url: '/docDocumentType/list', featureName: 'List document type', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-103', url: '/docDocumentType/create', featureName: 'Create Document Type', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-104', url: '/docDocumentType/update', featureName: 'Update Document Type', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-105', url: '/docDocumentType/delete', featureName: 'Delete Document Type', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // docQuestionSet
            new RequestMap(transactionCode: 'DOC-106', url: '/docQuestionSet/show', featureName: 'Show Question Set', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-107', url: '/docQuestionSet/list', featureName: 'List Question Set', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-108', url: '/docQuestionSet/create', featureName: 'Create Question Set', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-109', url: '/docQuestionSet/update', featureName: 'Update Question Set', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-110', url: '/docQuestionSet/delete', featureName: 'Delete Question Set', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-111', url: '/docQuestionSet/generateQuestionSet', featureName: 'Generate Question Set', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'DOC-112', url: '/docQuestionSet/clearQuestionSet', featureName: 'Clear Question Set', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-168', url: '/docQuestionSet/updateExamTime', featureName: 'Update Exam Time', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // docQuestionSetDetails
            new RequestMap(transactionCode: 'DOC-113', url: '/docQuestionSetDetails/show', featureName: 'Show Question Set Details', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-114', url: '/docQuestionSetDetails/list', featureName: 'List Question Set Details', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // docQuestionSetDifficulty
            new RequestMap(transactionCode: 'DOC-118', url: '/docQuestionSetDifficulty/show', featureName: 'Show Question Set Difficulty', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-119', url: '/docQuestionSetDifficulty/list', featureName: 'List Question Set Difficulty', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-120', url: '/docQuestionSetDifficulty/create', featureName: 'Create Question Set Difficulty', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-121', url: '/docQuestionSetDifficulty/update', featureName: 'Update Question Set Difficulty', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-122', url: '/docQuestionSetDifficulty/delete', featureName: 'Delete Question Set Difficulty', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // docDocumentVersion
            new RequestMap(transactionCode: 'DOC-169', url: '/docDocumentVersion/show', featureName: 'Show Document Version', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-170', url: '/docDocumentVersion/list', featureName: 'List Document Version', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-171', url: '/docDocumentVersion/delete', featureName: 'Delete Document Version', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-172', url: '/docDocumentVersion/restore', featureName: 'Restory Document Version', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-181', url: '/docDocumentVersion/showVersion', featureName: 'Show trash Document Version', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-182', url: '/docDocumentVersion/listLatestVersion', featureName: 'list trash Document Version', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-183', url: '/docDocumentVersion/listPreviousVersion', featureName: 'list trash Document previous Version', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-184', url: '/docDocumentVersion/deleteVersion', featureName: 'delete trash Document Version', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DOC-185', url: '/docDocumentVersion/restoreTrashedVersion', featureName: 'Restore Trashed Version', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        // docDiscussion
//        new RequestMap(transactionCode: 'DOC-126', url: '/docDiscussion/create', featureName: 'Add Document Discussion', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-220', url: '/docDiscussion/listViewDocDiscussion', featureName: 'List Discussion in List View', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()


        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Method to create request map for Data Pipeline plugin
     * last used transaction_code = 'DPL-168'
     */
    public boolean createRequestMapForDataPipeLine() {
        try {
            new RequestMap(transactionCode: 'DPL-1', url: '/dataPipeLine/renderDataPipeLineMenu', featureName: 'Render menu for Data Pipeline', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //DocOfflineDataFeedFile
            new RequestMap(transactionCode: 'DPL-2', url: '/dplOfflineDataFeedFile/show', featureName: 'Show Data File', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-3', url: '/dplOfflineDataFeedFile/showTarget', featureName: 'Show Target Data File', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-4', url: '/dplOfflineDataFeedFile/showLoaded', featureName: 'Show Loaded in DB File', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-5', url: '/dplOfflineDataFeedFile/list', featureName: 'List Data File', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-6', url: '/dplOfflineDataFeedFile/listTargetFile', featureName: 'List of Target Data File', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-7', url: '/dplOfflineDataFeedFile/listLoadedFile', featureName: 'List of Loaded in DB File', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-8', url: '/dplOfflineDataFeedFile/receivedFile', featureName: 'Receive Data File', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-9', url: '/dplOfflineDataFeedFile/receiveFileFromSource', featureName: 'Receive data feed from source', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-10', url: '/dplOfflineDataFeedFile/deleteFromTarget', featureName: 'Delete offline data feed file from target', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-11', url: '/dplOfflineDataFeedFile/deleteFromDataBucket', featureName: 'Delete offline data feed file from data bucket', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-12', url: '/dplOfflineDataFeedFile/moveToDataBucket', featureName: 'Move to S3', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-13', url: '/dplOfflineDataFeedFile/showDataBucket', featureName: 'Show Data Bucket', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-14', url: '/dplOfflineDataFeedFile/listDataBucket', featureName: 'List Data Bucket', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-15', url: '/dplOfflineDataFeedFile/loadDataFeed', featureName: 'Load Data-Feed to DB', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-16', url: '/dplOfflineDataFeedFile/showSource', featureName: 'Show Source Data-Feed', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-17', url: '/dplOfflineDataFeedFile/listSourceFile', featureName: 'List Source Data-Feed', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // dplDataExport
            new RequestMap(transactionCode: 'DPL-19', url: '/dplDataExport/show', featureName: 'Show Data Export', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-21', url: '/dplDataExport/list', featureName: 'List Data Export', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-23', url: '/dplDataExport/create', featureName: 'Create Data Export', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-24', url: '/dplDataExport/select', featureName: 'Select Data Export', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-26', url: '/dplDataExport/update', featureName: 'Update Data Export', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-28', url: '/dplDataExport/delete', featureName: 'Delete Data Export', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-31', url: '/dplDataExport/approve', featureName: 'Approve Data Export', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-32', url: '/dplDataExport/execute', featureName: 'Execute Data Export', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-35', url: '/dplDataExport/unApprove', featureName: 'Un-approve Data Export', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-36', url: '/dplDataExport/stopExecution', featureName: 'Stop execution data Export', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-38', url: '/dplDataExport/clearLog', featureName: 'Clear Data Export transaction log', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-39', url: '/dplDataExport/showReport', featureName: 'Show Data Export Report', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'DPL-100', url: '/dplDataExport/sendDataExportMail', featureName: 'Data Export Report Summary', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 14, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'DPL-160', url: '/transactionLog/showForDplDataExport', featureName: 'Show transaction log for data export', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'DPL-167', url: '/dplDataExport/checkDataExport', featureName: 'Check Data Export before execution', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'DPL-168', url: '/dplDataExport/listForDplDashboard', featureName: 'List Data export for Dpl Dashboard', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

            // dplDataExportDetails
            new RequestMap(transactionCode: 'DPL-41', url: '/dplDataExportDetails/show', featureName: 'Show Data Transfer Details', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-43', url: '/dplDataExportDetails/list', featureName: 'List Data Transfer Details', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-45', url: '/dplDataExportDetails/create', featureName: 'Create Data transfer Details', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-49', url: '/dplDataExportDetails/update', featureName: 'Update Data Transfer Details', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-51', url: '/dplDataExportDetails/delete', featureName: 'Delete Data Transfer Details', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-135', url: '/dplDataExportDetails/addAllTable', featureName: 'Add All Table to Data Transfer Details for File', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // dplDataExportSourceSchema
            new RequestMap(transactionCode: 'DPL-104', url: '/dplDataExportSourceSchema/show', featureName: 'Show Data Transfer Source Schema', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-105', url: '/dplDataExportSourceSchema/list', featureName: 'List Data Transfer Source Schema', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-106', url: '/dplDataExportSourceSchema/create', featureName: 'Create Data Transfer Source Schema', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-107', url: '/dplDataExportSourceSchema/delete', featureName: 'Delete Data Transfer Source Schema', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // dplDataImport
            new RequestMap(transactionCode: 'DPL-52', url: '/dplDataImport/show', featureName: 'Show Data Import', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-53', url: '/dplDataImport/list', featureName: 'List data Import', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-54', url: '/dplDataImport/create', featureName: 'Create Data Import', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-55', url: '/dplDataImport/update', featureName: 'Update Data Import', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-56', url: '/dplDataImport/delete', featureName: 'Delete Data Import', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-57', url: '/dplDataImport/clearLog', featureName: 'Clear Data Import Transaction Log', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-58', url: '/dplDataImport/showReport', featureName: 'Show Data Import Report', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
            new RequestMap(transactionCode: 'DPL-59', url: '/dplDataImport/approve', featureName: 'Approve data Import', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-60', url: '/dplDataImport/unApprove', featureName: 'Un-approve data Import', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-61', url: '/dplDataImport/stopExecution', featureName: 'Stop Data Import execution ', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-62', url: '/dplDataImport/executeImport', featureName: 'Execute Data Import', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-101', url: '/dplDataImport/sendDataImportMail', featureName: 'Data Import Report Summary', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-150', url: '/dplDataImport/checkImport', featureName: 'Check Data Import File Mount Location', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-161', url: '/transactionLog/showForDplDataImport', featureName: 'Show transaction log for data import', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();

            // dplDataImportDetails
            new RequestMap(transactionCode: 'DPL-63', url: '/dplDataImportDetails/show', featureName: 'Show Data Import Details', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-64', url: '/dplDataImportDetails/list', featureName: 'List Data Import Details', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-99', url: '/dplDataImportDetails/listExportedDataSource', featureName: 'List Data Exported Details', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-65', url: '/dplDataImportDetails/create', featureName: 'Create Data Import Details', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-67', url: '/dplDataImportDetails/update', featureName: 'Update Data Import Details', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-68', url: '/dplDataImportDetails/delete', featureName: 'Delete Data Import Details', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-137', url: '/dplDataImportDetails/addAllTable', featureName: 'Add All Table to Data Import Details', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // dplCdcLog
            new RequestMap(transactionCode: 'DPL-78', url: '/dplCdcLog/showForMySql', featureName: 'Show CDC Log for MySql', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-79', url: '/dplCdcLog/listForMySql', featureName: 'List CDC Log for MySql', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // dplCdcMySql
            new RequestMap(transactionCode: 'DPL-80', url: '/dplCdcMySql/show', featureName: 'Show Change Data Capture (CDC) for MySQL', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-81', url: '/dplCdcMySql/list', featureName: 'List Change Data Capture (CDC)', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-82', url: '/dplCdcMySql/create', featureName: 'Create Change Data Capture (CDC)', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-83', url: '/dplCdcMySql/update', featureName: 'Update Change Data Capture (CDC)', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-84', url: '/dplCdcMySql/delete', featureName: 'Delete Change Data Capture (CDC)', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-94', url: '/dplCdcMySql/createInitialLog', featureName: 'Create Initial Log File of CDC', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-95', url: '/dplCdcMySql/clearInitialLogFile', featureName: 'Clean Initial Log File of CDC', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-164', url: '/dplCdcMySql/approve', featureName: 'Approve CDC for MYSQL', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-165', url: '/dplCdcMySql/unApprove', featureName: 'Unapprove CDC for MYSQL', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // dplCdcMsSql
            new RequestMap(transactionCode: 'DPL-108', url: '/dplCdcMsSql/show', featureName: 'Show Change Data Capture (CDC) for MSSQL', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-154', url: '/dplCdcMsSql/list', featureName: 'List Change Data Capture (CDC) for MSSQL', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-142', url: '/dplCdcMsSql/create', featureName: 'Create Change Data Capture (CDC) for MSSQL', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-143', url: '/dplCdcMsSql/update', featureName: 'Update Change Data Capture (CDC) for MSSQL', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-144', url: '/dplCdcMsSql/delete', featureName: 'Delete Change Data Capture (CDC) for MSSQL', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-156', url: '/dplCdcMsSql/approve', featureName: 'Approve CDC for MSSQL', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-157', url: '/dplCdcMsSql/unApprove', featureName: 'Unapprove CDC for MSSQL', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // dplCdcLog for MsSql
            new RequestMap(transactionCode: 'DPL-153', url: '/dplCdcLog/showForMsSql', featureName: 'Show CDC Log for MSSQL', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-155', url: '/dplCdcLog/listForMsSql', featureName: 'List CDC  Log for MSSQL', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-158', url: '/dplCdcLog/clearLogForMsSql', featureName: 'Clear CDC  Log for MSSQL', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // dplCdcSourceSchemaMySql
            new RequestMap(transactionCode: 'DPL-88', url: '/dplCdcSourceSchemaMySql/show', featureName: 'Show CDC Source Schema', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-89', url: '/dplCdcSourceSchemaMySql/list', featureName: 'List CDC Source Schema', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-90', url: '/dplCdcSourceSchemaMySql/create', featureName: 'Create CDC Source Schema', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-91', url: '/dplCdcSourceSchemaMySql/update', featureName: 'Update CDC Source Schema', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-92', url: '/dplCdcSourceSchemaMySql/delete', featureName: 'Delete CDC Source Schema', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-166', url: '/dplCdcSourceSchemaMySql/addAllTable', featureName: 'Add all table to CDC Source Schema', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //report
            new RequestMap(transactionCode: 'DPL-102', url: '/dplReport/downloadDataExportSummary', featureName: 'Download Data Export summary report', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-103', url: '/dplReport/downloadDataImportSummary', featureName: 'Download Data Import summary report', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //icon Dashboard
            new RequestMap(transactionCode: 'DPL-109', url: '/appDbInstance/showForDplDashboard', featureName: 'Show Dpl Dashboard', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-110', url: '/appDbInstance/listVendorForDplDashboard', featureName: 'List vendor for Dpl Dashboard', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-111', url: '/appDbInstance/listDbForDplDashboard', featureName: 'List Db for Dpl Dashboard', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-113', url: '/appDbInstance/createDbInstanceForDplDashboard', featureName: 'Create Db Instance for Dpl Dashboard', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-159', url: '/appDbInstance/updateDbInstanceForDplDashboard', featureName: 'Update Db Instance for Dpl Dashboard', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-138', url: '/appDbInstance/testDbForDplDashboard', featureName: 'Test DB connection for Dpl Dashboard', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //dpl table
            new RequestMap(transactionCode: 'DPL-114', url: '/dplTable/showForManageSchema', featureName: 'show dpl table for manage schema', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-115', url: '/dplTable/scanAndCopyForManageSchema', featureName: 'scan and copy dpl table for manage schema', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-116', url: '/dplTable/distributeForManageSchema', featureName: 'distribute table for manage schema', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-117', url: '/dplTable/removeAllForManageSchema', featureName: 'remove all table for manage schema', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-122', url: '/dplTable/previewForManageSchema', featureName: 'preview for manage schema', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-162', url: '/dplTable/previewForManageSchemaForRedShift', featureName: 'preview for manage schema for redshift', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-131', url: '/dplTable/generateScript', featureName: 'Generate dpl table script', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-163', url: '/dplTable/generateScriptForRedshift', featureName: 'Generate dpl table script for redshift', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-132', url: '/dplTable/viewAllScript', featureName: 'View all table script', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-133', url: '/dplTable/updateScript', featureName: 'update dpl table script', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-134', url: '/dplTable/runScript', featureName: 'execute dpl table script', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //dpl field
            new RequestMap(transactionCode: 'DPL-118', url: '/dplField/show', featureName: 'show field', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-119', url: '/dplField/list', featureName: 'list field', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-120', url: '/dplField/setPrimaryForManageSchema', featureName: 'Set primary for dpl field for manage schema', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-121', url: '/dplField/distributeDplFieldForManageSchema', featureName: 'distribute for dpl field for manage schema', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            // Dpl Source Schema MsSql
            new RequestMap(transactionCode: 'DPL-123', url: '/dplCdcSourceSchemaMsSql/show', featureName: 'Show CDC Source Schema for MsSql', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-124', url: '/dplCdcSourceSchemaMsSql/list', featureName: 'List CDC Source Schema for MsSql', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-125', url: '/dplCdcSourceSchemaMsSql/create', featureName: 'Create CDC Source Schema for MsSql', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-126', url: '/dplCdcSourceSchemaMsSql/update', featureName: 'Update CDC Source Schema for MsSql', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-127', url: '/dplCdcSourceSchemaMsSql/delete', featureName: 'Delete CDC Source Schema for MsSql', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-128', url: '/dplCdcSourceSchemaMsSql/dropDownTableReload', featureName: 'Refresh Table Drop Down for MsSql Source Schema', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-136', url: '/dplCdcSourceSchemaMsSql/addAllTable', featureName: 'Add All Table to CDC Source Schema for MsSql', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //Dpl SQL Console
            new RequestMap(transactionCode: 'DPL-140', url: '/appDbInstance/showForSqlConsole', featureName: 'Show sql console', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-141', url: '/appDbInstance/listResultForSqlConsole', featureName: 'List result for sql console', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

            //Dpl Cdc Primary Key MsSql
            new RequestMap(transactionCode: 'DPL-145', url: '/dplCdcPrimaryKey/showForMsSql', featureName: 'Show Dpl Cdc Primary Key For MsSql', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-152', url: '/dplCdcPrimaryKey/showForMySql', featureName: 'Show Dpl Cdc Primary Key For MySql', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-146', url: '/dplCdcPrimaryKey/list', featureName: 'List Dpl Cdc Primary Key', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-147', url: '/dplCdcPrimaryKey/create', featureName: 'Create Dpl Cdc Primary Key', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-148', url: '/dplCdcPrimaryKey/update', featureName: 'Update Dpl Cdc Primary Key', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
            new RequestMap(transactionCode: 'DPL-149', url: '/dplCdcPrimaryKey/delete', featureName: 'Delete Dpl Cdc Primary Key', configAttribute: 'ROLE_-2', pluginId: 14, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }


    public boolean createRequestMapForIctPool() {
        new RequestMap(transactionCode: 'ICT-1', url: '/ictPool/renderIctPoolMenu', featureName: 'Render menu for ICT POOL', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 17, isViewable: Boolean.FALSE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'ICT-2', url: '/plugins/ict-pool-0.1/images/**', featureName: 'Render images for ICT POOL', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 17, isViewable: Boolean.FALSE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'ICT-3', url: '/plugins/ict-pool-0.1/css/**', featureName: 'Render  css ICT POOL', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 17, isViewable: Boolean.FALSE, isCommon: Boolean.TRUE).save()



        new RequestMap(transactionCode: 'ICT-4', url: '/companyProfile/show', featureName: 'Show Company Profile', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 17, isViewable: Boolean.TRUE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'ICT-5', url: '/companyProfile/create', featureName: 'Create Company Profile', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 17, isViewable: Boolean.TRUE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'ICT-6', url: '/companyProfile/update', featureName: 'Update Company Profile', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 17, isViewable: Boolean.TRUE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'ICT-7', url: '/companyProfile/delete', featureName: 'Delete Company Profile', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 17, isViewable: Boolean.TRUE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'ICT-8', url: '/companyProfile/list', featureName: 'List Company Profile', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 17, isViewable: Boolean.TRUE, isCommon: Boolean.TRUE).save()



        new RequestMap(transactionCode: 'ICT-9', url: '/businessArea/show', featureName: 'Show Business Area', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 17, isViewable: Boolean.TRUE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'ICT-10', url: '/businessArea/create', featureName: 'Create Business Area', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 17, isViewable: Boolean.TRUE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'ICT-11', url: '/businessArea/update', featureName: 'Delete Business Area', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 17, isViewable: Boolean.TRUE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'ICT-12', url: '/businessArea/delete', featureName: 'Update Business Area', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 17, isViewable: Boolean.TRUE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'ICT-13', url: '/businessArea/list', featureName: 'List Business Area', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 17, isViewable: Boolean.TRUE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'ICT-14', url: '/businessArea/othersInfoByBusinessType', featureName: 'List Business Area', configAttribute: 'ROLE_-2', pluginId: 17, isViewable: Boolean.TRUE, isCommon: Boolean.TRUE).save()


        new RequestMap(transactionCode: 'ICT-15', url: '/companyCapacity/show', featureName: 'Show Company Capacity', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 17, isViewable: Boolean.TRUE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'ICT-16', url: '/companyCapacity/create', featureName: 'Create Company Capacity', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 17, isViewable: Boolean.TRUE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'ICT-17', url: '/companyCapacity/update', featureName: 'Update Company Capacity', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 17, isViewable: Boolean.TRUE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'ICT-18', url: '/companyCapacity/delete', featureName: 'Delete Company Capacity', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 17, isViewable: Boolean.TRUE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'ICT-19', url: '/companyCapacity/list', featureName: 'List Company Capacity', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 17, isViewable: Boolean.TRUE, isCommon: Boolean.TRUE).save()



        new RequestMap(transactionCode: 'ICT-20', url: '/contactPersonInfo/show', featureName: 'Show Contact Person Info', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 17, isViewable: Boolean.TRUE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'ICT-21', url: '/contactPersonInfo/create', featureName: 'Create Contact Person Info', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 17, isViewable: Boolean.TRUE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'ICT-22', url: '/contactPersonInfo/update', featureName: 'Update Contact Person Info', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 17, isViewable: Boolean.TRUE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'ICT-23', url: '/contactPersonInfo/delete', featureName: 'Delete Contact Person Info', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 17, isViewable: Boolean.TRUE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'ICT-24', url: '/contactPersonInfo/list', featureName: 'List Contact Person Info', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 17, isViewable: Boolean.TRUE, isCommon: Boolean.TRUE).save()

        new RequestMap(transactionCode: 'ICT-25', url: '/correspondentInfo/show', featureName: 'Show Correspondent Info', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 17, isViewable: Boolean.TRUE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'ICT-26', url: '/correspondentInfo/create', featureName: 'Create Correspondent Info', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 17, isViewable: Boolean.TRUE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'ICT-27', url: '/correspondentInfo/update', featureName: 'Update Correspondent Info', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 17, isViewable: Boolean.TRUE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'ICT-28', url: '/correspondentInfo/delete', featureName: 'Delete Correspondent Info', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 17, isViewable: Boolean.TRUE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'ICT-29', url: '/correspondentInfo/list', featureName: 'List Correspondent Info', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 17, isViewable: Boolean.TRUE, isCommon: Boolean.TRUE).save()

        new RequestMap(transactionCode: 'ICT-30', url: '/hrInfo/show', featureName: 'Show Hr Info', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 17, isViewable: Boolean.TRUE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'ICT-31', url: '/hrInfo/create', featureName: 'Create Hr Info', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 17, isViewable: Boolean.TRUE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'ICT-32', url: '/hrInfo/update', featureName: 'Update Hr Info', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 17, isViewable: Boolean.TRUE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'ICT-33', url: '/hrInfo/delete', featureName: 'Delete Hr Info', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 17, isViewable: Boolean.TRUE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'ICT-34', url: '/hrInfo/list', featureName: 'List Hr Info', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 17, isViewable: Boolean.TRUE, isCommon: Boolean.TRUE).save()

        new RequestMap(transactionCode: 'ICT-35', url: '/jointVenture/show', featureName: 'Show Joint Venture', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 17, isViewable: Boolean.TRUE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'ICT-36', url: '/jointVenture/create', featureName: 'Create Joint Venture', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 17, isViewable: Boolean.TRUE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'ICT-37', url: '/jointVenture/update', featureName: 'Update Joint Venture', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 17, isViewable: Boolean.TRUE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'ICT-38', url: '/jointVenture/delete', featureName: 'Delete Joint Venture', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 17, isViewable: Boolean.TRUE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'ICT-39', url: '/jointVenture/list', featureName: 'List Joint Venture', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 17, isViewable: Boolean.TRUE, isCommon: Boolean.TRUE).save()

        new RequestMap(transactionCode: 'ICT-40', url: '/ownersInfo/show', featureName: 'Show Owners Info', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 17, isViewable: Boolean.TRUE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'ICT-41', url: '/ownersInfo/create', featureName: 'Create Owners Info', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 17, isViewable: Boolean.TRUE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'ICT-42', url: '/ownersInfo/update', featureName: 'Update Owners Info', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 17, isViewable: Boolean.TRUE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'ICT-43', url: '/ownersInfo/delete', featureName: 'Delete Owners Info', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 17, isViewable: Boolean.TRUE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'ICT-44', url: '/ownersInfo/list', featureName: 'List Owners Info', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 17, isViewable: Boolean.TRUE, isCommon: Boolean.TRUE).save()
    }

    public void createRequestMapForBeacon(){
        new RequestMap(transactionCode: 'BCON-1', url: '/vsBeacon/renderBeaconMenu', featureName: 'Render menu for BEACON', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 18, isViewable: Boolean.FALSE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'BCON-2', url: '/beacon/ict-pool-0.1/images/**', featureName: 'Render images for BEACON', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 18, isViewable: Boolean.FALSE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'BCON-3', url: '/plugins/ict-pool-0.1/css/**', featureName: 'Render css for BEACON', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 18, isViewable: Boolean.FALSE, isCommon: Boolean.TRUE).save()


        new RequestMap(transactionCode: 'BCON-4', url: '/rest/**', featureName: 'Rest API Call', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 18, isViewable: Boolean.FALSE, isCommon: Boolean.TRUE).save()



        new RequestMap(transactionCode: 'BCON-5', url: '/marchant/show', featureName: 'Show Marchant', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 18, isViewable: Boolean.FALSE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'BCON-6', url: '/marchant/create', featureName: 'Create Marchant', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 18, isViewable: Boolean.FALSE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'BCON-7', url: '/marchant/update', featureName: 'Update Marchant', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 18, isViewable: Boolean.FALSE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'BCON-8', url: '/marchant/delete', featureName: 'Delete Marchant', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 18, isViewable: Boolean.FALSE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'BCON-9', url: '/marchant/list', featureName: 'List Marchant', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 18, isViewable: Boolean.FALSE, isCommon: Boolean.TRUE).save()


        new RequestMap(transactionCode: 'BCON-10', url: '/beacon/show', featureName: 'Show Beacon', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 18, isViewable: Boolean.FALSE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'BCON-11', url: '/beacon/create', featureName: 'Create Beacon', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 18, isViewable: Boolean.FALSE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'BCON-12', url: '/beacon/update', featureName: 'Update Beacon', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 18, isViewable: Boolean.FALSE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'BCON-13', url: '/beacon/delete', featureName: 'Delete Beacon', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 18, isViewable: Boolean.FALSE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'BCON-14', url: '/beacon/list', featureName: 'List Beacon', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 18, isViewable: Boolean.FALSE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'BCON-15', url: '/beacon/beaconList', featureName: 'List Beacon For campaing', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 18, isViewable: Boolean.FALSE, isCommon: Boolean.TRUE).save()


        new RequestMap(transactionCode: 'BCON-16', url: '/campaign/show', featureName: 'Show Campaign', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 18, isViewable: Boolean.FALSE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'BCON-17', url: '/campaign/create', featureName: 'Create Campaign', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 18, isViewable: Boolean.FALSE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'BCON-18', url: '/campaign/update', featureName: 'Update Campaign', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 18, isViewable: Boolean.FALSE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'BCON-19', url: '/campaign/delete', featureName: 'Delete Campaign', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 18, isViewable: Boolean.FALSE, isCommon: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'BCON-20', url: '/campaign/list', featureName: 'List Campaign', configAttribute: 'ROLE_-2,ROLE_-12_1,ROLE_-3_1', pluginId: 18, isViewable: Boolean.FALSE, isCommon: Boolean.TRUE).save()


    }
    /**
     * Method to reset default request maps by plugin Id and role id
     * @param pluginId - plugin id
     * @param role - object of role
     * @return - a boolean value
     */
    @Transactional
    public boolean resetDefaultRequestMapsByPluginId(long pluginId, Role role) {
        switch (pluginId) {
            case PluginConnector.PLUGIN_ID:
                resetRequestMapToDefault(pluginId, role)
                createApplicationRequestMap(role.authority)
                break
            case BudgPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(BudgPluginConnector.PLUGIN_NAME)) {
                    resetRequestMapToDefault(pluginId, role)
                }
                break
            case ProcPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(ProcPluginConnector.PLUGIN_NAME)) {
                    resetRequestMapToDefault(pluginId, role)
                }
                break
            case AccPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(AccPluginConnector.PLUGIN_NAME)) {
                    resetRequestMapToDefault(pluginId, role)
                }
                break
            case QsPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(QsPluginConnector.PLUGIN_NAME)) {
                    resetRequestMapToDefault(pluginId, role)
                }
                break
            case InvPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(InvPluginConnector.PLUGIN_NAME)) {
                    resetRequestMapToDefault(pluginId, role)
                }
                break
            case FxdPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(FxdPluginConnector.PLUGIN_NAME)) {
                    resetRequestMapToDefault(pluginId, role)
                }
                break
            case ExchangeHousePluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(ExchangeHousePluginConnector.PLUGIN_NAME)) {
                    resetRequestMapToDefault(pluginId, role)
                }
                break
            case PtPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(PtPluginConnector.PLUGIN_NAME)) {
                    resetRequestMapToDefault(pluginId, role)
                }
                break
            case SarbPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(SarbPluginConnector.PLUGIN_NAME)) {
                    resetRequestMapToDefault(pluginId, role)
                }
                break
            case ArmsPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(ArmsPluginConnector.PLUGIN_NAME)) {
                    resetRequestMapToDefault(pluginId, role)
                }
                break
            case DocumentPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(DocumentPluginConnector.PLUGIN_NAME)) {
                    resetRequestMapToDefault(pluginId, role)
                }
                break
            default:
                break
        }
        return true
    }

    /**
     * Method to reset request map to default
     * @param pluginId - plugin id
     * @param role - object f role
     * @return - a boolean value
     */
    private boolean resetRequestMapToDefault(long pluginId, Role role) {
        // remove role from request map
        removeRoleFromRequestMap(role.authority, pluginId)
        // append role in request map
        List<RoleFeatureMapping> lstRoleFeatureMapping = roleFeatureMappingService.findAllByRoleTypeIdAndPluginId(role.roleTypeId, pluginId)
        for (int i = 0; i < lstRoleFeatureMapping.size(); i++) {
            RoleFeatureMapping roleFeatureMapping = lstRoleFeatureMapping[i]
            appendDefaultRoleInRequestMap(role.authority, roleFeatureMapping.transactionCode)
        }
        return true
    }

    /**
     * Method for giving access in root, logout, manage password & change password pages for default roles
     * @param role - string of role from caller method
     * @return - a boolean value
     */
    private boolean createApplicationRequestMap(String role) {
        String updateQuery = """
           UPDATE request_map
            SET config_attribute =
                CASE WHEN config_attribute LIKE '%${role},%' THEN
                    config_attribute
                WHEN config_attribute LIKE '%,${role}' THEN
                    config_attribute
                WHEN config_attribute LIKE '${role},%' THEN
                    config_attribute
                ELSE config_attribute || ',${role}'
                END
            WHERE is_common = true;
        """
        executeUpdateSql(updateQuery)
        return true
    }

    /**
     * Method to remove role from request map
     * @param roleAuthority - string of role authority from caller method
     * @param pluginId - plugin id
     * @return - a boolean value
     */
    public boolean removeRoleFromRequestMap(String roleAuthority, long pluginId) {
        String queryRemoveRole = """
            UPDATE request_map
            SET config_attribute =
                CASE WHEN config_attribute LIKE '%${roleAuthority},%' THEN
                    REPLACE(config_attribute, '${roleAuthority},' , '')
                WHEN config_attribute LIKE '%,${roleAuthority}' THEN
                    REPLACE(config_attribute, ',${roleAuthority}' , '')
                WHEN config_attribute LIKE '${roleAuthority},%' THEN
                    REPLACE(config_attribute, '${roleAuthority},' , '')
                ELSE config_attribute
                END
            WHERE plugin_id = ${pluginId}
        """
        executeUpdateSql(queryRemoveRole)
        return true
    }

    /**
     * Method to remove role from request map
     * @param roleAuthority - string of role authority from caller method
     * @param transactionCode - request map transaction code
     * @return - a boolean value
     */
    public boolean removeRoleFromRequestMap(String roleAuthority, String transactionCode) {
        String queryRemoveRole = """
            UPDATE request_map
            SET config_attribute =
                CASE WHEN config_attribute LIKE '%${roleAuthority},%' THEN
                    REPLACE(config_attribute, '${roleAuthority},' , '')
                WHEN config_attribute LIKE '%,${roleAuthority}' THEN
                    REPLACE(config_attribute, ',${roleAuthority}' , '')
                WHEN config_attribute LIKE '${roleAuthority},%' THEN
                    REPLACE(config_attribute, '${roleAuthority},' , '')
                ELSE config_attribute
                END
            WHERE transaction_code IN (${transactionCode})
        """
        executeUpdateSql(queryRemoveRole)
        return true
    }

    /**
     * Method to append default role in request map
     * @param role - string of role from caller method
     * @param transactionCode - string of transaction code from caller method
     * @return - a boolean value
     */
    private boolean appendDefaultRoleInRequestMap(String role, String transactionCode) {
        String updateQuery = """
           UPDATE request_map
           SET
           config_attribute = config_attribute || ',${role}'
           WHERE
           transaction_code = '${transactionCode}';
        """
        executeUpdateSql(updateQuery)
        return true
    }

    public boolean appendRoleInRequestMap(long pluginId) {
        List<Role> lstRole = roleService.list()
        for (int i = 0; i < lstRole.size(); i++) {
            Role role = lstRole[i]
            createApplicationRequestMap(role.authority)

            List<RoleFeatureMapping> lstRoleFeatureMapping = roleFeatureMappingService.findAllByRoleTypeIdAndPluginId(role.roleTypeId, pluginId)
            for (int j = 0; j < lstRoleFeatureMapping.size(); j++) {
                RoleFeatureMapping roleFeatureMapping = lstRoleFeatureMapping[j]
                appendDefaultRoleInRequestMap(role.authority, roleFeatureMapping.transactionCode)
            }
        }
        return true
    }

    public boolean appendRoleInRequestMap(long pluginId, long companyId) {
        List<Role> lstRole = roleService.findAllByCompanyId(companyId)
        for (int i = 0; i < lstRole.size(); i++) {
            Role role = lstRole[i]
            createApplicationRequestMap(role.authority)

            List<RoleFeatureMapping> lstRoleFeatureMapping = roleFeatureMappingService.findAllByRoleTypeIdAndPluginId(role.roleTypeId, pluginId)
            for (int j = 0; j < lstRoleFeatureMapping.size(); j++) {
                RoleFeatureMapping roleFeatureMapping = lstRoleFeatureMapping[j]
                appendDefaultRoleInRequestMap(role.authority, roleFeatureMapping.transactionCode)
            }
        }
        return true
    }

    @Override
    public void createDefaultSchema() {
        String nameIndex = "create unique index request_map_transaction_code_idx on request_map(lower(transaction_code));"
        executeSql(nameIndex)
        String codeIndex = "create unique index request_map_url_idx on request_map(lower(url));"
        executeSql(codeIndex)
    }

    public void createTestDataForBudget() {
        List<Role> lstRole = []
        Role roleDir = roleService.findByNameAndCompanyId('Director', companyId)
        Role rolePd = roleService.findByNameAndCompanyId('Project Director', companyId)
        lstRole << roleDir
        lstRole << rolePd
        long pluginId = BudgPluginConnector.PLUGIN_ID
        for (int i = 0; i < lstRole.size(); i++) {
            Role role = lstRole[i]
            createApplicationRequestMap(role.authority)

            List<RoleFeatureMapping> lstRoleFeatureMapping = roleFeatureMappingService.findAllByRoleTypeIdAndPluginId(role.id, pluginId)
            for (int j = 0; j < lstRoleFeatureMapping.size(); j++) {
                RoleFeatureMapping roleFeatureMapping = lstRoleFeatureMapping[j]
                appendDefaultRoleInRequestMap(role.authority, roleFeatureMapping.transactionCode)
            }
        }
    }

    public void createTestDataForProcurement() {
        List<Role> lstRole = []
        Role roleDir = roleService.findByNameAndCompanyId('Director', companyId)
        Role rolePd = roleService.findByNameAndCompanyId('Project Director', companyId)
        lstRole << roleDir
        lstRole << rolePd
        long pluginId = ProcPluginConnector.PLUGIN_ID
        for (int i = 0; i < lstRole.size(); i++) {
            Role role = lstRole[i]
            createApplicationRequestMap(role.authority)

            List<RoleFeatureMapping> lstRoleFeatureMapping = roleFeatureMappingService.findAllByRoleTypeIdAndPluginId(role.id, pluginId)
            for (int j = 0; j < lstRoleFeatureMapping.size(); j++) {
                RoleFeatureMapping roleFeatureMapping = lstRoleFeatureMapping[j]
                appendDefaultRoleInRequestMap(role.authority, roleFeatureMapping.transactionCode)
            }
        }
    }

    public void createTestDataForAccounting() {
        List<Role> lstRole = []
        Role roleAccountant = roleService.findByNameAndCompanyId('Accountant', companyId)
        Role roleCfo = roleService.findByNameAndCompanyId('CFO', companyId)
        lstRole << roleAccountant
        lstRole << roleCfo
        long pluginId = AccPluginConnector.PLUGIN_ID
        for (int i = 0; i < lstRole.size(); i++) {
            Role role = lstRole[i]
            createApplicationRequestMap(role.authority)

            List<RoleFeatureMapping> lstRoleFeatureMapping = roleFeatureMappingService.findAllByRoleTypeIdAndPluginId(role.id, pluginId)
            for (int j = 0; j < lstRoleFeatureMapping.size(); j++) {
                RoleFeatureMapping roleFeatureMapping = lstRoleFeatureMapping[j]
                appendDefaultRoleInRequestMap(role.authority, roleFeatureMapping.transactionCode)
            }
        }
    }

    public void createTestDataForPt() {
        List<Role> lstRole = []
        Role engineer = roleService.findByNameAndCompanyId('Software Engineer', companyId)
        Role sqa = roleService.findByNameAndCompanyId('SQA', companyId)
        lstRole << engineer
        lstRole << sqa
        long pluginId = PtPluginConnector.PLUGIN_ID
        for (int i = 0; i < lstRole.size(); i++) {
            Role role = lstRole[i]
            createApplicationRequestMap(role.authority)
            List<RoleFeatureMapping> lstRoleFeatureMapping = roleFeatureMappingService.findAllByRoleTypeIdAndPluginId(role.id, pluginId)
            for (int j = 0; j < lstRoleFeatureMapping.size(); j++) {
                RoleFeatureMapping roleFeatureMapping = lstRoleFeatureMapping[j]
                appendDefaultRoleInRequestMap(role.authority, roleFeatureMapping.transactionCode)
            }
        }
    }

    public void createTestDataForInventory() {
        List<Role> lstRole = []
        Role roleAuditor = roleService.findByNameAndCompanyId('Inventory Auditor', companyId)
        Role rolePm = roleService.findByNameAndCompanyId('Project Manager', companyId)
        lstRole << roleAuditor
        lstRole << rolePm
        long pluginId = InvPluginConnector.PLUGIN_ID
        for (int i = 0; i < lstRole.size(); i++) {
            Role role = lstRole[i]
            createApplicationRequestMap(role.authority)

            List<RoleFeatureMapping> lstRoleFeatureMapping = roleFeatureMappingService.findAllByRoleTypeIdAndPluginId(role.id, pluginId)
            for (int j = 0; j < lstRoleFeatureMapping.size(); j++) {
                RoleFeatureMapping roleFeatureMapping = lstRoleFeatureMapping[j]
                appendDefaultRoleInRequestMap(role.authority, roleFeatureMapping.transactionCode)
            }
        }
    }

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }
}
