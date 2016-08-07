package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.Role
import com.athena.mis.application.entity.RoleFeatureMapping
import com.athena.mis.integration.accounting.AccPluginConnector
import com.athena.mis.integration.arms.ArmsPluginConnector
import com.athena.mis.integration.budget.BudgPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.integration.fixedasset.FxdPluginConnector
import com.athena.mis.integration.inventory.InvPluginConnector
import com.athena.mis.integration.procurement.ProcPluginConnector
import com.athena.mis.integration.qsmeasurement.QsPluginConnector
import com.athena.mis.integration.sarb.SarbPluginConnector
import org.apache.log4j.Logger

class RoleFeatureMappingService extends BaseDomainService {

    private Logger log = Logger.getLogger(getClass())

    RoleService roleService
    AppVersionService appVersionService

    @Override
    public void init(){
        domainClass = RoleFeatureMapping.class
    }

    @Override
    public void createDefaultSchema() {
        String sqlIndex = "CREATE UNIQUE INDEX role_feature_mapping_transaction_code_role_type_id_idx ON role_feature_mapping(lower(transaction_code),role_type_id);"
        executeSql(sqlIndex)
    }

    public List<RoleFeatureMapping> findAllByRoleTypeIdAndPluginId(long roleTypeId, long pluginId) {
        List<RoleFeatureMapping> lstRoleFeatureMapping = RoleFeatureMapping.findAllByRoleTypeIdAndPluginId(roleTypeId, pluginId, [readOnly: true])
        return lstRoleFeatureMapping
    }

    public boolean createRoleFeatureMapForApplication() {
        try {
            int count = appVersionService.countByPluginId(PluginConnector.PLUGIN_ID)
            if (count > 0) {
                return true
            }
            // default
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-17', pluginId: 1).save(); // url: '/appUser/checkPassword'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-92', pluginId: 1).save(); // url: '/application/renderApplicationMenu'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-17', pluginId: 1).save(); // url: '/appUser/checkPassword'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-92', pluginId: 1).save(); // url: '/application/renderApplicationMenu'

            // AppUser
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-10', pluginId: 1).save();  // url: '/appUser/show'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-11', pluginId: 1).save(); // url: '/appUser/create'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-13', pluginId: 1).save(); // url: '/appUser/update'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-14', pluginId: 1).save(); // url: '/appUser/delete'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-15', pluginId: 1).save();  // url: '/appUser/list'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-154', pluginId: 1).save(); // url: '/appUser/showOnlineUser'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-155', pluginId: 1).save(); // url: '/appUser/listOnlineUser'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-156', pluginId: 1).save(); // url: '/appUser/forceLogoutOnlineUser'

            // AppUserEntity(User Entity Mapping)
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-238', pluginId: 1).save(); // url: '/appUserEntity/show'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-239', pluginId: 1).save(); // url: '/appUserEntity/create'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-240', pluginId: 1).save(); // url: '/appUserEntity/update'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-241', pluginId: 1).save(); // url: '/appUserEntity/delete'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-242', pluginId: 1).save(); // url: '/appUserEntity/list'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-244', pluginId: 1).save(); // url: '/appUserEntity/dropDownAppUserEntityReload'

            // appMail
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-187', pluginId: 1).save(); // url: '/appMail/show'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-188', pluginId: 1).save(); // url: '/appMail/update'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-189', pluginId: 1).save(); // url: '/appMail/list'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-251', pluginId: 1).save(); // url: '/appMail/testAppMail'

            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-364', pluginId: 1).save(); // url: '/appMail/createAnnouncement'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-365', pluginId: 1).save(); // url: '/appMail/deleteAnnouncement'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-366', pluginId: 1).save(); // url: '/appMail/sendAnnouncement'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-367', pluginId: 1).save(); // url: '/appMail/showAnnouncement'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-368', pluginId: 1).save(); // url: '/appMail/listAnnouncement'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-369', pluginId: 1).save(); // url: '/appMail/updateAnnouncement'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-370', pluginId: 1).save(); // url: '/appMail/showForSend'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-371', pluginId: 1).save(); // url: '/appMail/listForSend'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-372', pluginId: 1).save(); // url: '/appMail/reComposeAnnouncement'

            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-427', pluginId: 1).save(); // url: '/appMail/showForComposeMail'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-428', pluginId: 1).save(); // url: '/appMail/listMail'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-429', pluginId: 1).save(); // url: '/appMail/createMail'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-430', pluginId: 1).save(); // url: '/appMail/updateMail'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-431', pluginId: 1).save(); // url: '/appMail/sendMail'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-432', pluginId: 1).save(); // url: '/appMail/deleteMail'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-435', pluginId: 1).save(); // url: '/appMail/showForSentMail'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-436', pluginId: 1).save(); // url: '/appMail/listForSentMail'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-437', pluginId: 1).save(); // url: '/appMail/previewMail'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-438', pluginId: 1).save(); // url: '/appMail/createAndSend'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-439', pluginId: 1).save(); // url: '/appMail/uploadAttachment'


            // appShellScript
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-253', pluginId: 1).save(); // url: '/appShellScript/show'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-400', pluginId: 1).save(); // url: '/appShellScript/showSql'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-254', pluginId: 1).save(); // url: '/appShellScript/create'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-255', pluginId: 1).save(); // url: '/appShellScript/list'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-257', pluginId: 1).save(); // url: '/appShellScript/update'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-258', pluginId: 1).save(); // url: '/appShellScript/delete'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-259', pluginId: 1).save(); // url: '/appShellScript/evaluate'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-401', pluginId: 1).save(); // url: '/appShellScript/evaluateSqlScript'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-409', pluginId: 1).save(); // url: '/appShellScript/downloadReport'

            // role
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-19', pluginId: 1).save();  // url: '/role/show'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-20', pluginId: 1).save();  // url: '/role/create'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-22', pluginId: 1).save();  // url: '/role/update'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-23', pluginId: 1).save();  // url: '/role/delete'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-24', pluginId: 1).save();  // url: '/role/list'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-315', pluginId: 1).save(); // url: '/role/showMyRole'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-316', pluginId: 1).save(); // url: '/role/listMyRole'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-327', pluginId: 1).save(); // url: '/role/downloadUserRoleReport'

            // role module
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-403', pluginId: 1).save();  // url: '/roleModule/show'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-404', pluginId: 1).save();  // url: '/roleModule/create'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-405', pluginId: 1).save();  // url: '/roleModule/update'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-406', pluginId: 1).save();  // url: '/roleModule/delete'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-407', pluginId: 1).save();  // url: '/roleModule/list'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-408', pluginId: 1).save();  // url: '/roleModule/dropDownRoleModuleReload'

            // request map
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-25', pluginId: 1).save(); // url: '/requestMap/show'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-28', pluginId: 1).save(); // url: '/requestMap/update'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-31', pluginId: 1).save(); // url: '/requestMap/resetRequestMap'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-326', pluginId: 1).save(); // url: '/requestMap/reloadRequestMap'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-331', pluginId: 1).save(); // url: '/requestMap/listAvailableRole'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-332', pluginId: 1).save(); // url: '/requestMap/listAssignedRole'

            // user role
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-32', pluginId: 1).save();  // url: '/userRole/show'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-33', pluginId: 1).save(); // url: '/userRole/create'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-35', pluginId: 1).save(); // url: '/userRole/update'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-36', pluginId: 1).save(); // url: '/userRole/delete'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-37', pluginId: 1).save();  // url: '/userRole/list'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-328', pluginId: 1).save();  // url: '/userRole/dropDownAppUserForRoleReload'

            // appCustomer
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-38', pluginId: 1).save();  // url: '/appCustomer/show'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-39', pluginId: 1).save(); // url: '/appCustomer/create'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-41', pluginId: 1).save(); // url: '/appCustomer/update'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-42', pluginId: 1).save(); // url: '/appCustomer/delete'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-43', pluginId: 1).save();  // url: '/appCustomer/list'
            // appEmployee
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-44', pluginId: 1).save();  // url: '/appEmployee/show'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-45', pluginId: 1).save(); // url: '/appEmployee/create'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-47', pluginId: 1).save(); // url: '/appEmployee/update'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-48', pluginId: 1).save(); // url: '/appEmployee/delete'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-49', pluginId: 1).save();  // url: '/appEmployee/list'
            // company
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-50', pluginId: 1).save(); // url: '/company/show'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-53', pluginId: 1).save(); // url: '/company/update'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-55', pluginId: 1).save(); // url: '/company/list'

            // project
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-56', pluginId: 1).save();  // url: '/project/show'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-57', pluginId: 1).save(); // url: '/project/create'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-59', pluginId: 1).save(); // url: '/project/update'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-60', pluginId: 1).save(); // url: '/project/delete'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-61', pluginId: 1).save();  // url: '/project/list'

            // item
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-157', pluginId: 1).save(); // url: '/item/listItemByItemTypeId'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-339', pluginId: 1).save(); // url: '/item/dropDownItemReload'

            // inventory item
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-158', pluginId: 1).save(); // url:  '/item/showInventoryItem'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-159', pluginId: 1).save(); // url:  '/item/createInventoryItem'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-160', pluginId: 1).save(); // url:  '/item/updateInventoryItem'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-161', pluginId: 1).save(); // url:  '/item/deleteInventoryItem'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-162', pluginId: 1).save(); // url:  '/item/listInventoryItem'

            // non inventory item
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-163', pluginId: 1).save(); // url:  '/item/showNonInventoryItem'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-164', pluginId: 1).save(); // url:  '/item/createNonInventoryItem'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-165', pluginId: 1).save(); // url:  '/item/updateNonInventoryItem'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-166', pluginId: 1).save(); // url:  '/item/deleteNonInventoryItem'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-167', pluginId: 1).save(); // url:  '/item/listNonInventoryItem'

            // app group
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-74', pluginId: 1).save();  // url: '/appGroup/show'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-75', pluginId: 1).save(); // url: '/appGroup/create'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-77', pluginId: 1).save(); // url: '/appGroup/update'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-78', pluginId: 1).save(); // url: '/appGroup/delete'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-79', pluginId: 1).save();  // url: '/appGroup/list'

            // AppCountry
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-93', pluginId: 1).save(); // url: '/appCountry/show'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-94', pluginId: 1).save(); // url: '/appCountry/create'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-95', pluginId: 1).save(); // url: '/appCountry/delete'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-96', pluginId: 1).save(); // url: '/appCountry/update'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-98', pluginId: 1).save(); // url: '/appCountry/list'
            // system config
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-99', pluginId: 1).save(); // url: '/systemConfiguration/show'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-100', pluginId: 1).save(); // url: '/systemConfiguration/list'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-102', pluginId: 1).save(); // url: '/systemConfiguration/update'

            // system entity
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-103', pluginId: 1).save(); // url: '/systemEntity/show'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-104', pluginId: 1).save(); // url: '/systemEntity/create'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-105', pluginId: 1).save(); // url: '/systemEntity/list'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-107', pluginId: 1).save(); // url: '/systemEntity/update'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-173', pluginId: 1).save(); // url: '/systemEntity/delete'

            // system entity type
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-108', pluginId: 1).save(); // url: '/systemEntityType/show'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-109', pluginId: 1).save(); // url: '/systemEntityType/list'

            // appDesignation
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-118', pluginId: 1).save(); // url: '/appDesignation/show'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-119', pluginId: 1).save(); // url: '/appDesignation/create'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-120', pluginId: 1).save(); // url: '/appDesignation/update'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-121', pluginId: 1).save(); // url: '/appDesignation/delete'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-122', pluginId: 1).save(); // url: '/appDesignation/list'
            // appTheme
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-124', pluginId: 1).save(); // url: '/appTheme/showTheme'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-125', pluginId: 1).save(); // url: '/appTheme/updateTheme'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-126', pluginId: 1).save(); // url: '/appTheme/listTheme'

            // appSms
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-180', pluginId: 1).save(); // url: '/appSms/showSms'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-181', pluginId: 1).save(); // url: '/appSms/updateSms'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-182', pluginId: 1).save(); // url: '/appSms/listSms'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-237', pluginId: 1).save(); // url: '/appSms/sendSms'

            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-374', pluginId: 1).save(); // url: '/appSms/create'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-375', pluginId: 1).save(); // url: '/appSms/delete'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-376', pluginId: 1).save(); // url: '/appSms/showForCompose'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-377', pluginId: 1).save(); // url: '/appSms/listForCompose'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-378', pluginId: 1).save(); // url: '/appSms/updateForCompose'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-379', pluginId: 1).save(); // url: '/appSms/sendForCompose'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-380', pluginId: 1).save(); // url: '/appSms/showForSend'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-381', pluginId: 1).save(); // url: '/appSms/listForSend'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-382', pluginId: 1).save(); // url: '/appSms/reCompose'

            // currency
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-128', pluginId: 1).save(); // url: '/currency/show'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-129', pluginId: 1).save(); // url: '/currency/create'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-130', pluginId: 1).save(); // url: '/currency/update'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-131', pluginId: 1).save(); // url: '/currency/list'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-132', pluginId: 1).save(); // url: '/currency/edit'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-133', pluginId: 1).save(); // url: '/currency/delete'

            // content category
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-138', pluginId: 1).save(); // url: '/contentCategory/show'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-140', pluginId: 1).save(); // url: '/contentCategory/list'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-141', pluginId: 1).save(); // url: '/contentCategory/update'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-142', pluginId: 1).save(); // url: '/contentCategory/create'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-143', pluginId: 1).save(); // url: '/contentCategory/delete'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-153', pluginId: 1).save(); // url: '/contentCategory/dropDownContentCategoryReload'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-153', pluginId: 1).save(); // url: '/contentCategory/dropDownContentCategoryReload'

            // AppAttachment
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-144', pluginId: 1).save(); // url: '/appAttachment/show'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-146', pluginId: 1).save(); // url: '/appAttachment/list'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-147', pluginId: 1).save(); // url: '/appAttachment/update'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-148', pluginId: 1).save(); // url: '/appAttachment/create'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-149', pluginId: 1).save(); // url: '/appAttachment/delete'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-421', pluginId: 1).save(); // url: '/appAttachment/upload'

            // app note
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-245', pluginId: 1).save(); // url: '/appNote/show'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-247', pluginId: 1).save(); // url: '/appNote/list'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-248', pluginId: 1).save(); // url: '/appNote/update'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-249', pluginId: 1).save(); // url: '/appNote/create'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-250', pluginId: 1).save(); // url: '/appNote/delete'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-261', pluginId: 1).save(); // url: '/appNote/viewEntityNote'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-345', pluginId: 1).save(); // url: '/appNote/listEntityNote'

            // app faq
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-440', pluginId: 1).save(); // url: '/appFaq/show'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-441', pluginId: 1).save(); // url: '/appFaq/list'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-442', pluginId: 1).save(); // url: '/appFaq/create'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-443', pluginId: 1).save(); // url: '/appFaq/update'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-444', pluginId: 1).save(); // url: '/appFaq/delete'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-445', pluginId: 1).save(); // url: '/appNote/listFaq'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-446', pluginId: 1).save(); // url: '/appNote/reloadFaq'

            //Item Type
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-174', pluginId: 1).save(); // url: '/itemType/show'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-175', pluginId: 1).save(); // url: '/itemType/create'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-176', pluginId: 1).save(); // url: '/itemType/update'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-177', pluginId: 1).save(); // url: '/itemType/delete'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-178', pluginId: 1).save(); // url: '/itemType/list'

            //  bank
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-192', pluginId: 1).save();   //url: '/appBank/show'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-193', pluginId: 1).save();   //url: '/appBank/create'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-194', pluginId: 1).save();   //url: '/appBank/update'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-196', pluginId: 1).save();   //url: '/appBank/list'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-197', pluginId: 1).save();   //url: '/appBank/delete'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-260', pluginId: 1).save(); //url: '/appBank/reloadBankDropDownTagLib'

            //  bankBranch
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-203', pluginId: 1).save(); //url: '/appBankBranch/show'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-204', pluginId: 1).save(); //url: '/appBankBranch/create'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-205', pluginId: 1).save(); //url: '/appBankBranch/update'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-207', pluginId: 1).save(); //url: '/appBankBranch/list'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-208', pluginId: 1).save(); //url: '/appBankBranch/delete'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-209', pluginId: 1).save(); //url: '/appBankBranch/reloadBranchesDropDownByBankAndDistrict'

            //  district
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-211', pluginId: 1).save();  //url: '/district/show'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-212', pluginId: 1).save(); //url: '/district/create'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-213', pluginId: 1).save(); //url: '/district/update'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-215', pluginId: 1).save();  //url: '/district/list'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-216', pluginId: 1).save(); //url: '/district/delete'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-236', pluginId: 1).save(); //url: '/district/reloadDistrictDropDown'

            // Vehicle
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-217', pluginId: 1).save();  //url: '/vehicle/show'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-219', pluginId: 1).save(); //url: '/vehicle/create'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-220', pluginId: 1).save(); //url: '/vehicle/update'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-221', pluginId: 1).save(); //url: '/vehicle/delete'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-222', pluginId: 1).save();  //url: '/vehicle/list'

            // Vendor
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-411', pluginId: 1).save();  //url: '/vendor/show'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-412', pluginId: 1).save();  //url: '/vendor/list'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-413', pluginId: 1).save(); //url: '/vendor/create'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-414', pluginId: 1).save(); //url: '/vendor/update'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-415', pluginId: 1).save(); //url: '/vendor/delete'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-416', pluginId: 1).save();    // url: '/dplTable/renderVendorThumbImage'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-416', pluginId: 1).save();    // url: '/dplTable/renderVendorThumbImage'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-417', pluginId: 1).save();    // url: '/dplTable/renderVendorSmallImage'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-417', pluginId: 1).save();    // url: '/dplTable/renderVendorSmallImage'

            // Supplier
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-223', pluginId: 1).save();  //url: '/supplier/show'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-224', pluginId: 1).save(); //url: '/supplier/create'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-226', pluginId: 1).save(); //url: '/supplier/update'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-227', pluginId: 1).save(); //url: '/supplier/delete'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-228', pluginId: 1).save();  //url: '/supplier/list'

            // Supplier Details/Item
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-229', pluginId: 1).save(); //url: '/supplierItem/show'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-230', pluginId: 1).save(); //url: '/supplierItem/select'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-232', pluginId: 1).save(); //url: '/supplierItem/update'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-233', pluginId: 1).save(); //url: '/supplierItem/delete'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-234', pluginId: 1).save(); //url: '/supplierItem/list'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-235', pluginId: 1).save(); //url: '/supplierItem/dropDownSupplierItemReload'

            //reload app note taglib
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-252', pluginId: 1).save(); //url: '/appNote/reloadEntityNote'

            // Benchmark
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-263', pluginId: 1).save(); //url: '/benchmark/show'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-264', pluginId: 1).save(); //url: '/benchmark/create'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-266', pluginId: 1).save(); //url: '/benchmark/update'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-267', pluginId: 1).save(); //url: '/benchmark/list'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-268', pluginId: 1).save(); //url: '/benchmark/execute'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-272', pluginId: 1).save(); //url: '/benchmark/downloadBenchmarkReport'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-273', pluginId: 1).save(); //url: '/benchmark/showReport'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-274', pluginId: 1).save(); //url: '/benchmark/showForTruncateSampling'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-275', pluginId: 1).save(); //url: '/benchmark/listForTruncateSampling'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-276', pluginId: 1).save(); //url: '/benchmark/truncateSampling'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-277', pluginId: 1).save(); //url: '/benchmark/delete'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-278', pluginId: 1).save(); //url: '/benchmark/stopBenchMark'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-269', pluginId: 1).save(); //url: '/benchmark/clear'

            // Benchmark Star
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-294', pluginId: 1).save(); //url: '/benchmarkStar/show'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-295', pluginId: 1).save(); //url: '/benchmarkStar/create'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-297', pluginId: 1).save(); //url: '/benchmarkStar/update'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-298', pluginId: 1).save(); //url: '/benchmarkStar/list'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-299', pluginId: 1).save(); //url: '/benchmarkStar/execute'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-300', pluginId: 1).save(); //url: '/benchmarkStar/downloadBenchmarkReport'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-301', pluginId: 1).save(); //url: '/benchmarkStar/showReport'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-302', pluginId: 1).save(); //url: '/benchmarkStar/showForTruncateSampling'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-303', pluginId: 1).save(); //url: '/benchmarkStar/listForTruncateSampling'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-304', pluginId: 1).save(); //url: '/benchmarkStar/truncateSampling'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-305', pluginId: 1).save(); //url: '/benchmarkStar/delete'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-309', pluginId: 1).save(); //url: '/benchmarkStar/stopBenchMark'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-308', pluginId: 1).save(); //url: '/benchmarkStar/clear'

            // AppSchedule
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-279', pluginId: 1).save(); //url: '/appSchedule/show'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-280', pluginId: 1).save(); //url: '/appSchedule/list'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-282', pluginId: 1).save(); //url: '/appSchedule/update'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-283', pluginId: 1).save(); //url: '/appSchedule/testExecute'

            // DB Instance
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-284', pluginId: 1).save(); // url: '/appDbInstance/show'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-285', pluginId: 1).save(); // url: '/appDbInstance/list'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-410', pluginId: 1).save(); // url: '/appDbInstance/listForDashboard'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-286', pluginId: 1).save(); // url: '/appDbInstance/create'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-287', pluginId: 1).save(); // url: '/appDbInstance/update'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-288', pluginId: 1).save(); // url: '/appDbInstance/delete'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-290', pluginId: 1).save(); // url: '/appDbInstance/testDBConnection'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-291', pluginId: 1).save(); // url: '/appDbInstance/showResult'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-330', pluginId: 1).save(); // url: '/appDbInstance/listDbTable'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-383', pluginId: 1).save(); // url: '/appDbInstance/dropDownTableColumnReload'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-384', pluginId: 1).save(); // url: '/appDbInstance/dropDownTableReload'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-393', pluginId: 1).save(); // url: '/appDbInstance/dropDownDbInstanceReload'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-418', pluginId: 1).save(); // url: '/appDbInstance/connect'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-419', pluginId: 1).save(); // url: '/appDbInstance/disconnect'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-420', pluginId: 1).save(); // url: '/appDbInstance/reconnect'

            // transaction log
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-312', pluginId: 1).save(); // url: '/transactionLog/show'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-313', pluginId: 1).save(); // url: '/transactionLog/list'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-314', pluginId: 1).save(); // url: url: '/transactionLog/clear'

            //dbInstanceQuery
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-317', pluginId: 1).save();     // url: '/dbInstanceQuery/show'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-318', pluginId: 1).save();     // url: '/dbInstanceQuery/list'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-319', pluginId: 1).save();     // url: '/dbInstanceQuery/create'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-320', pluginId: 1).save();     // url: '/dbInstanceQuery/showQueryResult'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-321', pluginId: 1).save();     // url: '/dbInstanceQuery/downloadQueryResultCsv'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-322', pluginId: 1).save();     // url: '/dbInstanceQuery/listQueryResult'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-324', pluginId: 1).save();     // url: '/dbInstanceQuery/update'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-325', pluginId: 1).save();     // url: '/dbInstanceQuery/delete'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-329', pluginId: 1).save();     // url: '/dbInstanceQuery/execute'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-402', pluginId: 1).save();     // url: '/dbInstanceQuery/downloadQueryResult'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-464', pluginId: 1).save();     // url: '/dbInstanceQuery/executeQuery'

            //AppServerInstance
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-354', pluginId: 1).save();     // url: '/appServerInstance/show'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-355', pluginId: 1).save();     // url: '/appServerInstance/list'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-356', pluginId: 1).save();     // url: '/appServerInstance/create'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-357', pluginId: 1).save();     // url: '/appServerInstance/update'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-358', pluginId: 1).save();     // url: '/appServerInstance/delete'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-394', pluginId: 1).save();     // url: '/appServerInstance/testServerConnection'

            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-354', pluginId: 1).save();     // url: '/appServerInstance/show'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-355', pluginId: 1).save();     // url: '/appServerInstance/list'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-356', pluginId: 1).save();     // url: '/appServerInstance/create'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-357', pluginId: 1).save();     // url: '/appServerInstance/update'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-358', pluginId: 1).save();     // url: '/appServerInstance/delete'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-394', pluginId: 1).save();     // url: '/appServerInstance/testServerConnection'

            //AppServerDbInstanceMapping
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-359', pluginId: 1).save();     // url: '/appServerDbInstanceMapping/show'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-360', pluginId: 1).save();     // url: '/appServerDbInstanceMapping/list'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-361', pluginId: 1).save();     // url: '/appServerDbInstanceMapping/create'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-362', pluginId: 1).save();     // url: '/appServerDbInstanceMapping/update'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-363', pluginId: 1).save();     // url: '/appServerDbInstanceMapping/delete'

            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-359', pluginId: 1).save();     // url: '/appServerDbInstanceMapping/show'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-360', pluginId: 1).save();     // url: '/appServerDbInstanceMapping/list'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-361', pluginId: 1).save();     // url: '/appServerDbInstanceMapping/create'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-362', pluginId: 1).save();     // url: '/appServerDbInstanceMapping/update'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-363', pluginId: 1).save();     // url: '/appServerDbInstanceMapping/delete'

            //schema information
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-385', pluginId: 1).save();    // url: '/schemaInformation/listSchemaInfo'

            //testData
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-386', pluginId: 1).save();    // url: '/testData/list'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-387', pluginId: 1).save();    // url: '/testData/show'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-388', pluginId: 1).save();    // url: '/testData/create'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-389', pluginId: 1).save();    // url: '/testData/delete'

            // app version
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-390', pluginId: 1).save();    // url: '/appVersion/show'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'APP-391', pluginId: 1).save();    // url: '/appVersion/list'

            //AppPage
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-451', pluginId: 1).save();    // url: '/appPage/show'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-452', pluginId: 1).save();    // url: '/appPage/list'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-453', pluginId: 1).save();    // url: '/appPage/create'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-454', pluginId: 1).save();    // url: '/appPage/update'
            new RoleFeatureMapping(roleTypeId: -3, transactionCode: 'APP-455', pluginId: 1).save();    // url: '/appPage/delete'

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createRoleFeatureMapForAccountingPlugin() {
        try {
            int count = appVersionService.countByPluginId(AccPluginConnector.PLUGIN_ID)
            if (count > 0) {
                return true
            }
            // default
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-157', pluginId: 2).save(); // url: '/accPlugin/renderAccountingMenu'
            new RoleFeatureMapping(roleTypeId: -26, transactionCode: 'ACC-157', pluginId: 2).save(); // url: '/accPlugin/renderAccountingMenu'

            // Acc Custom Group
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-1', pluginId: 2).save(); // url:  '/accCustomGroup/show'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-2', pluginId: 2).save(); // url:  '/accCustomGroup/create'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-4', pluginId: 2).save(); // url:  '/accCustomGroup/update'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-5', pluginId: 2).save(); // url:  '/accCustomGroup/delete'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-6', pluginId: 2).save(); // url:  '/accCustomGroup/list'

            // Acc Chart of Account
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-7', pluginId: 2).save(); // url:  '/accChartOfAccount/show'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-8', pluginId: 2).save(); // url:  '/accChartOfAccount/create'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-9', pluginId: 2).save(); // url:  '/accChartOfAccount/select'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-10', pluginId: 2).save(); // url:  '/accChartOfAccount/update'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-11', pluginId: 2).save(); // url:  '/accChartOfAccount/delete'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-12', pluginId: 2).save(); // url:  '/accChartOfAccount/list'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-148', pluginId: 2).save(); // url:  '/accChartOfAccount/listSourceCategoryByAccSource'

            // Acc Group
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-15', pluginId: 2).save(); // url: '/accGroup/show'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-16', pluginId: 2).save(); // url: '/accGroup/create'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-18', pluginId: 2).save(); // url: '/accGroup/update'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-19', pluginId: 2).save(); // url: '/accGroup/delete'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-20', pluginId: 2).save(); // url: '/accGroup/list'

            // Acc Tier1
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-21', pluginId: 2).save(); // url: '/accTier1/show'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-22', pluginId: 2).save(); // url: '/accTier1/create'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-24', pluginId: 2).save(); // url: '/accTier1/update'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-25', pluginId: 2).save(); // url: '/accTier1/delete'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-26', pluginId: 2).save(); // url: '/accTier1/list'

            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-27', pluginId: 2).save(); // url: '/accTier1/listTier1ByAccTypeId'

            // Acc Tier2
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-35', pluginId: 2).save(); // url: '/accTier2/show'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-36', pluginId: 2).save(); // url: '/accTier2/create'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-38', pluginId: 2).save(); // url: '/accTier2/update'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-39', pluginId: 2).save(); // url: '/accTier2/delete'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-40', pluginId: 2).save(); // url: '/accTier2/list'

            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-41', pluginId: 2).save(); // url: '/accTier2/listTier2ByAccTier1Id'

            // AccTier3
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-28', pluginId: 2).save(); // url: '/accTier3/show'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-29', pluginId: 2).save(); // url: '/accTier3/create'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-31', pluginId: 2).save(); // url: '/accTier3/update'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-32', pluginId: 2).save(); // url: '/accTier3/delete'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-33', pluginId: 2).save(); // url: '/accTier3/list'

            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-34', pluginId: 2).save(); // url: '/accTier3/listTier3ByAccTier2Id'

            // Acc Type
            new RoleFeatureMapping(roleTypeId: -26, transactionCode: 'ACC-206', pluginId: 2).save(); // url: '/accType/show'
            new RoleFeatureMapping(roleTypeId: -26, transactionCode: 'ACC-207', pluginId: 2).save(); // url: '/accType/list'
            new RoleFeatureMapping(roleTypeId: -26, transactionCode: 'ACC-209', pluginId: 2).save(); // url: '/accType/update'

            // Acc Ipc
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-185', pluginId: 2).save(); // url: '/accIpc/show'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-186', pluginId: 2).save(); // url: '/accIpc/create'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-188', pluginId: 2).save(); // url: '/accIpc/update'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-189', pluginId: 2).save(); // url: '/accIpc/delete'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-190', pluginId: 2).save(); // url: '/accIpc/list'

            // Acc Lc
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-191', pluginId: 2).save(); // url: '/accLc/show'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-192', pluginId: 2).save(); // url: '/accLc/create'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-193', pluginId: 2).save(); // url: '/accLc/select'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-194', pluginId: 2).save(); // url: '/accLc/update'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-195', pluginId: 2).save(); // url: '/accLc/delete'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-196', pluginId: 2).save(); // url: '/accLc/list'

            // Acc Lease Account
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-198', pluginId: 2).save(); // url: '/accLeaseAccount/show'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-199', pluginId: 2).save(); // url: '/accLeaseAccount/create'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-201', pluginId: 2).save(); // url: '/accLeaseAccount/update'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-202', pluginId: 2).save(); // url: '/accLeaseAccount/delete'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-203', pluginId: 2).save(); // url: '/accLeaseAccount/list'

            // Voucher-Type implementation
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-42', pluginId: 2).save(); // url: '/accVoucherTypeCoa/show'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-43', pluginId: 2).save(); // url: '/accVoucherTypeCoa/create'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-45', pluginId: 2).save(); // url: '/accVoucherTypeCoa/update'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-46', pluginId: 2).save(); // url: '/accVoucherTypeCoa/delete'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-47', pluginId: 2).save(); // url: '/accVoucherTypeCoa/list'

            // Acc Sub-Account implementation
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-48', pluginId: 2).save(); // url: '/accSubAccount/show'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-49', pluginId: 2).save(); // url: '/accSubAccount/create'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-51', pluginId: 2).save(); // url: '/accSubAccount/update'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-52', pluginId: 2).save(); // url: '/accSubAccount/delete'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-53', pluginId: 2).save(); // url: '/accSubAccount/list'

            // Acc Division implementation
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-55', pluginId: 2).save(); // url: '/accDivision/show'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-56', pluginId: 2).save(); // url: '/accDivision/create'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-58', pluginId: 2).save(); // url: '/accDivision/update'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-59', pluginId: 2).save(); // url: '/accDivision/delete'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-60', pluginId: 2).save(); // url: '/accDivision/list'

            // chart of account report
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-76', pluginId: 2).save(); // url: '/accReport/downloadChartOfAccounts'

            // acc financial year
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-114', pluginId: 2).save(); // url: '/accFinancialYear/show'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-115', pluginId: 2).save(); // url: '/accFinancialYear/list'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-116', pluginId: 2).save(); // url: '/accFinancialYear/create'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-117', pluginId: 2).save(); // url: '/accFinancialYear/update'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-118', pluginId: 2).save(); // url: '/accFinancialYear/delete'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'ACC-150', pluginId: 2).save(); // url: '/accFinancialYear/setCurrentFinancialYear'

            // Application feature for ACC development user
            // system config
            new RoleFeatureMapping(roleTypeId: -26, transactionCode: 'APP-99', pluginId: 2).save(); // url: '/systemConfiguration/show'
            new RoleFeatureMapping(roleTypeId: -26, transactionCode: 'APP-100', pluginId: 2).save(); // url: '/systemConfiguration/list'
            new RoleFeatureMapping(roleTypeId: -26, transactionCode: 'APP-102', pluginId: 2).save(); // url: '/systemConfiguration/update'

            // system entity type
            new RoleFeatureMapping(roleTypeId: -26, transactionCode: 'APP-108', pluginId: 2).save(); // url: '/systemEntityType/show'
            new RoleFeatureMapping(roleTypeId: -26, transactionCode: 'APP-109', pluginId: 2).save(); // url: '/systemEntityType/list'

            // system entity
            new RoleFeatureMapping(roleTypeId: -26, transactionCode: 'APP-103', pluginId: 2).save(); // url: '/systemEntity/show'
            new RoleFeatureMapping(roleTypeId: -26, transactionCode: 'APP-104', pluginId: 2).save(); // url: '/systemEntity/create'
            new RoleFeatureMapping(roleTypeId: -26, transactionCode: 'APP-105', pluginId: 2).save(); // url: '/systemEntity/list'
            new RoleFeatureMapping(roleTypeId: -26, transactionCode: 'APP-107', pluginId: 2).save(); // url: '/systemEntity/update'
            new RoleFeatureMapping(roleTypeId: -26, transactionCode: 'APP-173', pluginId: 2).save(); // url: '/systemEntity/delete'

            // appMail
            new RoleFeatureMapping(roleTypeId: -26, transactionCode: 'APP-187', pluginId: 2).save(); // url: '/appMail/show'
            new RoleFeatureMapping(roleTypeId: -26, transactionCode: 'APP-188', pluginId: 2).save(); // url: '/appMail/update'
            new RoleFeatureMapping(roleTypeId: -26, transactionCode: 'APP-189', pluginId: 2).save(); // url: '/appMail/list'
            new RoleFeatureMapping(roleTypeId: -26, transactionCode: 'APP-251', pluginId: 2).save(); // url: '/appMail/testAppMail'

            // sms
            new RoleFeatureMapping(roleTypeId: -26, transactionCode: 'APP-180', pluginId: 2).save(); // url: '/appSms/showSms'
            new RoleFeatureMapping(roleTypeId: -26, transactionCode: 'APP-181', pluginId: 2).save(); // url: '/appSms/updateSms'
            new RoleFeatureMapping(roleTypeId: -26, transactionCode: 'APP-182', pluginId: 2).save(); // url: '/appSms/listSms'
            new RoleFeatureMapping(roleTypeId: -26, transactionCode: 'APP-237', pluginId: 2).save(); // url: '/appSms/sendSms'

            // appTheme
            new RoleFeatureMapping(roleTypeId: -26, transactionCode: 'APP-124', pluginId: 2).save(); // url: '/appTheme/showTheme'
            new RoleFeatureMapping(roleTypeId: -26, transactionCode: 'APP-125', pluginId: 2).save(); // url: '/appTheme/updateTheme'
            new RoleFeatureMapping(roleTypeId: -26, transactionCode: 'APP-126', pluginId: 2).save(); // url: '/appTheme/listTheme'

            // AppAttachment for acc admin
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'APP-144', pluginId: 2).save(); // url: '/appAttachment/show'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'APP-146', pluginId: 2).save(); // url: '/appAttachment/list'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'APP-147', pluginId: 2).save(); // url: '/appAttachment/update'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'APP-148', pluginId: 2).save(); // url: '/appAttachment/create'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'APP-149', pluginId: 2).save(); // url: '/appAttachment/delete'
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'APP-421', pluginId: 2).save(); // url: '/appAttachment/upload'

            // drop down item reload
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'APP-339', pluginId: 2).save(); // url: '/item/dropDownItemReload'

            // drop down content category reload
            new RoleFeatureMapping(roleTypeId: -25, transactionCode: 'APP-153', pluginId: 2).save(); // url: '/contentCategory/dropDownContentCategoryReload'

            //schema information
            new RoleFeatureMapping(roleTypeId: -26, transactionCode: 'APP-385', pluginId: 2).save();    // url: '/schemaInformation/listSchemaInfo'

            // DATA LOAD
            new RoleFeatureMapping(roleTypeId: -26, transactionCode: 'APP-386', pluginId: 2).save(); // url: '/testData/list'

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createRoleFeatureMapForBudgetPlugin() {
        try {
            int count = appVersionService.countByPluginId(BudgPluginConnector.PLUGIN_ID)
            if (count > 0) {
                return true
            }
            // default
            new RoleFeatureMapping(roleTypeId: -19, transactionCode: 'BUDG-38', pluginId: 3).save(); //   url: '/budgPlugin/renderBudgetMenu'
            new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'BUDG-38', pluginId: 3).save(); //   url: '/budgPlugin/renderBudgetMenu'

            // Budget Scope
            new RoleFeatureMapping(roleTypeId: -19, transactionCode: 'BUDG-16', pluginId: 3).save(); //   url: '/budgBudgetScope/show'
            new RoleFeatureMapping(roleTypeId: -19, transactionCode: 'BUDG-17', pluginId: 3).save(); //   url: '/budgBudgetScope/create'
            new RoleFeatureMapping(roleTypeId: -19, transactionCode: 'BUDG-18', pluginId: 3).save(); //   url: '/budgBudgetScope/select'
            new RoleFeatureMapping(roleTypeId: -19, transactionCode: 'BUDG-19', pluginId: 3).save(); //   url: '/budgBudgetScope/update'
            new RoleFeatureMapping(roleTypeId: -19, transactionCode: 'BUDG-20', pluginId: 3).save(); //   url: '/budgBudgetScope/delete'
            new RoleFeatureMapping(roleTypeId: -19, transactionCode: 'BUDG-21', pluginId: 3).save(); //   url: '/budgBudgetScope/list'

            // Project Budget Scope
            new RoleFeatureMapping(roleTypeId: -19, transactionCode: 'BUDG-22', pluginId: 3).save(); //   url: '/budgProjectBudgetScope/show'
            new RoleFeatureMapping(roleTypeId: -19, transactionCode: 'BUDG-98', pluginId: 3).save(); //   url: '/budgProjectBudgetScope/listAvailableScope'
            new RoleFeatureMapping(roleTypeId: -19, transactionCode: 'BUDG-99', pluginId: 3).save(); //   url: '/budgProjectBudgetScope/listAssignedScope'
            new RoleFeatureMapping(roleTypeId: -19, transactionCode: 'BUDG-24', pluginId: 3).save(); //   url: '/budgProjectBudgetScope/update'
            new RoleFeatureMapping(roleTypeId: -19, transactionCode: 'BUDG-25', pluginId: 3).save(); //   url: '/budgProjectBudgetScope/getBudgetScope'

            //Application feature for BUDG development user
            new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'APP-99', pluginId: 3).save(); // url: '/systemConfiguration/show'
            new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'APP-100', pluginId: 3).save(); // url: '/systemConfiguration/list'
            new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'APP-101', pluginId: 3).save(); // url: '/systemConfiguration/select'
            new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'APP-102', pluginId: 3).save(); // url: '/systemConfiguration/update'

            new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'APP-103', pluginId: 3).save(); // url: '/systemEntity/show'
            new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'APP-104', pluginId: 3).save(); // url: '/systemEntity/create'
            new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'APP-105', pluginId: 3).save(); // url: '/systemEntity/list'
            new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'APP-106', pluginId: 3).save(); // url: '/systemEntity/select'
            new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'APP-107', pluginId: 3).save(); // url: '/systemEntity/update'
            new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'APP-173', pluginId: 3).save(); // url: '/systemEntity/delete'

            new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'APP-108', pluginId: 3).save(); // url: '/systemEntityType/show'
            new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'APP-109', pluginId: 3).save(); // url: '/systemEntityType/list'

            new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'APP-187', pluginId: 3).save(); // url: '/appMail/show'
            new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'APP-188', pluginId: 3).save(); // url: '/appMail/update'
            new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'APP-189', pluginId: 3).save(); // url: '/appMail/list'
            new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'APP-190', pluginId: 3).save(); // url: '/appMail/select'
            new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'APP-251', pluginId: 3).save(); // url: '/appMail/testAppMail'

            new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'APP-180', pluginId: 3).save(); // url: '/appSms/showSms'
            new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'APP-181', pluginId: 3).save(); // url: '/appSms/updateSms'
            new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'APP-182', pluginId: 3).save(); // url: '/appSms/listSms'
            new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'APP-237', pluginId: 3).save(); // url: '/appSms/sendSms'

            // appTheme
            new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'APP-124', pluginId: 3).save(); // url: '/appTheme/showTheme'
            new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'APP-125', pluginId: 3).save(); // url: '/appTheme/updateTheme'
            new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'APP-126', pluginId: 3).save(); // url: '/appTheme/listTheme'

            //schema information
            new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'APP-385', pluginId: 3).save();    // url: '/schemaInformation/listSchemaInfo'

            // test data
            new RoleFeatureMapping(roleTypeId: -20, transactionCode: 'APP-386', pluginId: 3).save();    // url: '/testData/list'

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createRoleFeatureMapForProcurementPlugin() {
        try {
            int count = appVersionService.countByPluginId(ProcPluginConnector.PLUGIN_ID)
            if (count > 0) {
                return true
            }
            // default
            new RoleFeatureMapping(roleTypeId: -21, transactionCode: 'PROC-78', pluginId: 5).save(); // url: '/procPlugin/renderProcurementMenu'
            new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'PROC-78', pluginId: 5).save(); // url: '/procPlugin/renderProcurementMenu'

            //Application feature for Procurement development user
            new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'APP-99', pluginId: 5).save(); // url: '/systemConfiguration/show'
            new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'APP-100', pluginId: 5).save(); // url: '/systemConfiguration/list'
            new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'APP-101', pluginId: 5).save(); // url: '/systemConfiguration/select'
            new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'APP-102', pluginId: 5).save(); // url: '/systemConfiguration/update'

            new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'APP-103', pluginId: 5).save(); // url: '/systemEntity/show'
            new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'APP-104', pluginId: 5).save(); // url: '/systemEntity/create'
            new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'APP-105', pluginId: 5).save(); // url: '/systemEntity/list'
            new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'APP-106', pluginId: 5).save(); // url: '/systemEntity/select'
            new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'APP-107', pluginId: 5).save(); // url: '/systemEntity/update'
            new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'APP-173', pluginId: 5).save(); // url: '/systemEntity/delete'

            new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'APP-108', pluginId: 5).save(); // url: '/systemEntityType/show'
            new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'APP-109', pluginId: 5).save(); // url: '/systemEntityType/list'

            new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'APP-187', pluginId: 5).save(); // url: '/appMail/show'
            new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'APP-188', pluginId: 5).save(); // url: '/appMail/update'
            new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'APP-189', pluginId: 5).save(); // url: '/appMail/list'
            new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'APP-190', pluginId: 5).save(); // url: '/appMail/select'
            new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'APP-251', pluginId: 5).save(); // url: '/appMail/testAppMail'

            new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'APP-180', pluginId: 5).save(); // url: '/appSms/showSms'
            new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'APP-181', pluginId: 5).save(); // url: '/appSms/updateSms'
            new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'APP-182', pluginId: 5).save(); // url: '/appSms/listSms'
            new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'APP-237', pluginId: 5).save(); // url: '/appSms/sendSms'

            // appTheme
            new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'APP-124', pluginId: 5).save(); // url: '/appTheme/showTheme'
            new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'APP-125', pluginId: 5).save(); // url: '/appTheme/updateTheme'
            new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'APP-126', pluginId: 5).save(); // url: '/appTheme/listTheme'

            //schema information
            new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'APP-385', pluginId: 5).save();    // url: '/schemaInformation/listSchemaInfo'

            // test data
            new RoleFeatureMapping(roleTypeId: -22, transactionCode: 'APP-386', pluginId: 5).save();    // url: '/testData/list'

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createRoleFeatureMapForInventoryPlugin() {
        try {
            int count = appVersionService.countByPluginId(InvPluginConnector.PLUGIN_ID)
            if (count > 0) {
                return true
            }
            // default
            new RoleFeatureMapping(roleTypeId: -23, transactionCode: 'INV-150', pluginId: 4).save(); // url: '/invPlugin/renderInventoryMenu'
            new RoleFeatureMapping(roleTypeId: -24, transactionCode: 'INV-150', pluginId: 4).save(); // url: '/invPlugin/renderInventoryMenu'

            // For inv inventory
            new RoleFeatureMapping(roleTypeId: -23, transactionCode: 'INV-1', pluginId: 4).save(); // url: '/invInventory/show'
            new RoleFeatureMapping(roleTypeId: -23, transactionCode: 'INV-3', pluginId: 4).save(); // url: '/invInventory/create'
            new RoleFeatureMapping(roleTypeId: -23, transactionCode: 'INV-4', pluginId: 4).save(); // url: '/invInventory/update'
            new RoleFeatureMapping(roleTypeId: -23, transactionCode: 'INV-5', pluginId: 4).save(); // url: '/invInventory/delete'
            new RoleFeatureMapping(roleTypeId: -23, transactionCode: 'INV-6', pluginId: 4).save(); // url: '/invInventory/list'
            new RoleFeatureMapping(roleTypeId: -23, transactionCode: 'INV-238', pluginId: 4).save(); // url: '/invReport/downloadUserInventory'

            // Production Line Item
            new RoleFeatureMapping(roleTypeId: -23, transactionCode: 'INV-72', pluginId: 4).save(); // url: '/invProductionLineItem/show'
            new RoleFeatureMapping(roleTypeId: -23, transactionCode: 'INV-73', pluginId: 4).save(); // url: '/invProductionLineItem/create'
            new RoleFeatureMapping(roleTypeId: -23, transactionCode: 'INV-75', pluginId: 4).save(); // url: '/invProductionLineItem/update'
            new RoleFeatureMapping(roleTypeId: -23, transactionCode: 'INV-76', pluginId: 4).save(); // url: '/invProductionLineItem/delete'
            new RoleFeatureMapping(roleTypeId: -23, transactionCode: 'INV-77', pluginId: 4).save(); // url: '/invProductionLineItem/list'

            // Recalculate valuation
            new RoleFeatureMapping(roleTypeId: -24, transactionCode: 'INV-148', pluginId: 4).save(); // url: '/invInventoryTransaction/reCalculateValuation'
            new RoleFeatureMapping(roleTypeId: -24, transactionCode: 'INV-203', pluginId: 4).save(); // url: '/invInventoryTransaction/showReCalculateValuation'

            // Production Overhead Cost modification
            new RoleFeatureMapping(roleTypeId: -24, transactionCode: 'INV-210', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/showInvModifyOverheadCost'
            new RoleFeatureMapping(roleTypeId: -24, transactionCode: 'INV-211', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/searchInvModifyOverheadCost'
            new RoleFeatureMapping(roleTypeId: -24, transactionCode: 'INV-212', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/updateInvModifyOverheadCost'
            new RoleFeatureMapping(roleTypeId: -24, transactionCode: 'INV-213', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/getInvProdFinishedMaterialByLineItemId'

            //Application feature for Inventory development user
            new RoleFeatureMapping(roleTypeId: -24, transactionCode: 'APP-99', pluginId: 4).save(); // url: '/systemConfiguration/show'
            new RoleFeatureMapping(roleTypeId: -24, transactionCode: 'APP-100', pluginId: 4).save(); // url: '/systemConfiguration/list'
            new RoleFeatureMapping(roleTypeId: -24, transactionCode: 'APP-101', pluginId: 4).save(); // url: '/systemConfiguration/select'
            new RoleFeatureMapping(roleTypeId: -24, transactionCode: 'APP-102', pluginId: 4).save(); // url: '/systemConfiguration/update'

            new RoleFeatureMapping(roleTypeId: -24, transactionCode: 'APP-103', pluginId: 4).save(); // url: '/systemEntity/show'
            new RoleFeatureMapping(roleTypeId: -24, transactionCode: 'APP-104', pluginId: 4).save(); // url: '/systemEntity/create'
            new RoleFeatureMapping(roleTypeId: -24, transactionCode: 'APP-105', pluginId: 4).save(); // url: '/systemEntity/list'
            new RoleFeatureMapping(roleTypeId: -24, transactionCode: 'APP-106', pluginId: 4).save(); // url: '/systemEntity/select'
            new RoleFeatureMapping(roleTypeId: -24, transactionCode: 'APP-107', pluginId: 4).save(); // url: '/systemEntity/update'
            new RoleFeatureMapping(roleTypeId: -24, transactionCode: 'APP-173', pluginId: 4).save(); // url: '/systemEntity/delete'

            new RoleFeatureMapping(roleTypeId: -24, transactionCode: 'APP-108', pluginId: 4).save(); // url: '/systemEntityType/show'
            new RoleFeatureMapping(roleTypeId: -24, transactionCode: 'APP-109', pluginId: 4).save(); // url: '/systemEntityType/list'

            new RoleFeatureMapping(roleTypeId: -24, transactionCode: 'APP-187', pluginId: 4).save(); // url: '/appMail/show'
            new RoleFeatureMapping(roleTypeId: -24, transactionCode: 'APP-188', pluginId: 4).save(); // url: '/appMail/update'
            new RoleFeatureMapping(roleTypeId: -24, transactionCode: 'APP-189', pluginId: 4).save(); // url: '/appMail/list'
            new RoleFeatureMapping(roleTypeId: -24, transactionCode: 'APP-190', pluginId: 4).save(); // url: '/appMail/select'
            new RoleFeatureMapping(roleTypeId: -24, transactionCode: 'APP-251', pluginId: 4).save(); // url: '/appMail/testAppMail'

            new RoleFeatureMapping(roleTypeId: -24, transactionCode: 'APP-180', pluginId: 4).save(); // url: '/appSms/showSms'
            new RoleFeatureMapping(roleTypeId: -24, transactionCode: 'APP-181', pluginId: 4).save(); // url: '/appSms/updateSms'
            new RoleFeatureMapping(roleTypeId: -24, transactionCode: 'APP-182', pluginId: 4).save(); // url: '/appSms/listSms'
            new RoleFeatureMapping(roleTypeId: -24, transactionCode: 'APP-237', pluginId: 4).save(); // url: '/appSms/sendSms'

            // appTheme
            new RoleFeatureMapping(roleTypeId: -24, transactionCode: 'APP-124', pluginId: 4).save(); // url: '/appTheme/showTheme'
            new RoleFeatureMapping(roleTypeId: -24, transactionCode: 'APP-125', pluginId: 4).save(); // url: '/appTheme/updateTheme'
            new RoleFeatureMapping(roleTypeId: -24, transactionCode: 'APP-126', pluginId: 4).save(); // url: '/appTheme/listTheme'

            //Application feature(User Entity Mapping) for Inventory admin
            new RoleFeatureMapping(roleTypeId: -23, transactionCode: 'APP-238', pluginId: 4).save(); // url: '/appUserEntity/show'
            new RoleFeatureMapping(roleTypeId: -23, transactionCode: 'APP-239', pluginId: 4).save(); // url: '/appUserEntity/create'
            new RoleFeatureMapping(roleTypeId: -23, transactionCode: 'APP-240', pluginId: 4).save(); // url: '/appUserEntity/update'
            new RoleFeatureMapping(roleTypeId: -23, transactionCode: 'APP-241', pluginId: 4).save(); // url: '/appUserEntity/delete'
            new RoleFeatureMapping(roleTypeId: -23, transactionCode: 'APP-242', pluginId: 4).save(); // url: '/appUserEntity/list'
            new RoleFeatureMapping(roleTypeId: -23, transactionCode: 'APP-244', pluginId: 4).save(); // url: '/appUserEntity/dropDownAppUserEntityReload'

            //schema information
            new RoleFeatureMapping(roleTypeId: -24, transactionCode: 'APP-385', pluginId: 4).save();    // url: '/schemaInformation/listSchemaInfo'

            // test data
            new RoleFeatureMapping(roleTypeId: -24, transactionCode: 'APP-386', pluginId: 4).save();    // url: '/testData/list'

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createRoleFeatureMapForFixedAssetPlugin() {
        try{
            int count = appVersionService.countByPluginId(FxdPluginConnector.PLUGIN_ID)
            if (count > 0) {
                return true
            }
            // default
            new RoleFeatureMapping(roleTypeId: -27, transactionCode: 'FA-47', pluginId: 7).save(); //   url: '/fxdPlugin/renderFixedAssetMenu'
            new RoleFeatureMapping(roleTypeId: -28, transactionCode: 'FA-47', pluginId: 7).save(); //   url: '/fxdPlugin/renderFixedAssetMenu'

            // fixed asset item
            new RoleFeatureMapping(roleTypeId: -27, transactionCode: 'APP-168', pluginId: 7).save(); // url:  '/item/showFixedAssetItem'
            new RoleFeatureMapping(roleTypeId: -27, transactionCode: 'APP-169', pluginId: 7).save(); // url:  '/item/createFixedAssetItem'
            new RoleFeatureMapping(roleTypeId: -27, transactionCode: 'APP-170', pluginId: 7).save(); // url:  '/item/updateFixedAssetItem'
            new RoleFeatureMapping(roleTypeId: -27, transactionCode: 'APP-171', pluginId: 7).save(); // url:  '/item/deleteFixedAssetItem'
            new RoleFeatureMapping(roleTypeId: -27, transactionCode: 'APP-172', pluginId: 7).save(); // url:  '/item/listFixedAssetItem'

            // system config
            new RoleFeatureMapping(roleTypeId: -28, transactionCode: 'APP-99', pluginId: 7).save(); // url: '/systemConfiguration/show'
            new RoleFeatureMapping(roleTypeId: -28, transactionCode: 'APP-100', pluginId: 7).save(); // url: '/systemConfiguration/list'
            new RoleFeatureMapping(roleTypeId: -28, transactionCode: 'APP-102', pluginId: 7).save(); // url: '/systemConfiguration/update'

            // system entity type
            new RoleFeatureMapping(roleTypeId: -28, transactionCode: 'APP-108', pluginId: 7).save(); // url: '/systemEntityType/show'
            new RoleFeatureMapping(roleTypeId: -28, transactionCode: 'APP-109', pluginId: 7).save(); // url: '/systemEntityType/list'

            // system entity
            new RoleFeatureMapping(roleTypeId: -28, transactionCode: 'APP-103', pluginId: 7).save(); // url: '/systemEntity/show'
            new RoleFeatureMapping(roleTypeId: -28, transactionCode: 'APP-104', pluginId: 7).save(); // url: '/systemEntity/create'
            new RoleFeatureMapping(roleTypeId: -28, transactionCode: 'APP-105', pluginId: 7).save(); // url: '/systemEntity/list'
            new RoleFeatureMapping(roleTypeId: -28, transactionCode: 'APP-107', pluginId: 7).save(); // url: '/systemEntity/update'
            new RoleFeatureMapping(roleTypeId: -28, transactionCode: 'APP-173', pluginId: 7).save(); // url: '/systemEntity/delete'

            // appMail
            new RoleFeatureMapping(roleTypeId: -28, transactionCode: 'APP-187', pluginId: 7).save(); // url: '/appMail/show'
            new RoleFeatureMapping(roleTypeId: -28, transactionCode: 'APP-188', pluginId: 7).save(); // url: '/appMail/update'
            new RoleFeatureMapping(roleTypeId: -28, transactionCode: 'APP-189', pluginId: 7).save(); // url: '/appMail/list'
            new RoleFeatureMapping(roleTypeId: -28, transactionCode: 'APP-251', pluginId: 7).save(); // url: '/appMail/testAppMail'

            // sms
            new RoleFeatureMapping(roleTypeId: -28, transactionCode: 'APP-180', pluginId: 7).save(); // url: '/appSms/showSms'
            new RoleFeatureMapping(roleTypeId: -28, transactionCode: 'APP-181', pluginId: 7).save(); // url: '/appSms/updateSms'
            new RoleFeatureMapping(roleTypeId: -28, transactionCode: 'APP-182', pluginId: 7).save(); // url: '/appSms/listSms'
            new RoleFeatureMapping(roleTypeId: -28, transactionCode: 'APP-237', pluginId: 7).save(); // url: '/appSms/sendSms'

            // appTheme
            new RoleFeatureMapping(roleTypeId: -28, transactionCode: 'APP-124', pluginId: 7).save(); // url: '/appTheme/showTheme'
            new RoleFeatureMapping(roleTypeId: -28, transactionCode: 'APP-125', pluginId: 7).save(); // url: '/appTheme/updateTheme'
            new RoleFeatureMapping(roleTypeId: -28, transactionCode: 'APP-126', pluginId: 7).save(); // url: '/appTheme/listTheme'
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createRoleFeatureMapForQSPlugin() {
        try{
            int count = appVersionService.countByPluginId(QsPluginConnector.PLUGIN_ID)
            if (count > 0) {
                return true
            }
            // default
            new RoleFeatureMapping(roleTypeId: -29, transactionCode: 'QS-41', pluginId: 6).save(); //   url: '/qsPlugin/renderQSMenu'
            new RoleFeatureMapping(roleTypeId: -30, transactionCode: 'QS-41', pluginId: 6).save(); //   url: '/qsPlugin/renderQSMenu'

            //Application feature for Inventory development user
            new RoleFeatureMapping(roleTypeId: -30, transactionCode: 'APP-99', pluginId: 6).save(); // url: '/systemConfiguration/show'
            new RoleFeatureMapping(roleTypeId: -30, transactionCode: 'APP-100', pluginId: 6).save(); // url: '/systemConfiguration/list'
            new RoleFeatureMapping(roleTypeId: -30, transactionCode: 'APP-101', pluginId: 6).save(); // url: '/systemConfiguration/select'
            new RoleFeatureMapping(roleTypeId: -30, transactionCode: 'APP-102', pluginId: 6).save(); // url: '/systemConfiguration/update'

            new RoleFeatureMapping(roleTypeId: -30, transactionCode: 'APP-103', pluginId: 6).save(); // url: '/systemEntity/show'
            new RoleFeatureMapping(roleTypeId: -30, transactionCode: 'APP-104', pluginId: 6).save(); // url: '/systemEntity/create'
            new RoleFeatureMapping(roleTypeId: -30, transactionCode: 'APP-105', pluginId: 6).save(); // url: '/systemEntity/list'
            new RoleFeatureMapping(roleTypeId: -30, transactionCode: 'APP-106', pluginId: 6).save(); // url: '/systemEntity/select'
            new RoleFeatureMapping(roleTypeId: -30, transactionCode: 'APP-107', pluginId: 6).save(); // url: '/systemEntity/update'
            new RoleFeatureMapping(roleTypeId: -30, transactionCode: 'APP-173', pluginId: 6).save(); // url: '/systemEntity/delete'

            new RoleFeatureMapping(roleTypeId: -30, transactionCode: 'APP-108', pluginId: 6).save(); // url: '/systemEntityType/show'
            new RoleFeatureMapping(roleTypeId: -30, transactionCode: 'APP-109', pluginId: 6).save(); // url: '/systemEntityType/list'

            new RoleFeatureMapping(roleTypeId: -30, transactionCode: 'APP-187', pluginId: 6).save(); // url: '/appMail/show'
            new RoleFeatureMapping(roleTypeId: -30, transactionCode: 'APP-188', pluginId: 6).save(); // url: '/appMail/update'
            new RoleFeatureMapping(roleTypeId: -30, transactionCode: 'APP-189', pluginId: 6).save(); // url: '/appMail/list'
            new RoleFeatureMapping(roleTypeId: -30, transactionCode: 'APP-190', pluginId: 6).save(); // url: '/appMail/select'
            new RoleFeatureMapping(roleTypeId: -30, transactionCode: 'APP-251', pluginId: 6).save(); // url: '/appMail/testAppMail'

            new RoleFeatureMapping(roleTypeId: -30, transactionCode: 'APP-180', pluginId: 6).save(); // url: '/appSms/showSms'
            new RoleFeatureMapping(roleTypeId: -30, transactionCode: 'APP-181', pluginId: 6).save(); // url: '/appSms/updateSms'
            new RoleFeatureMapping(roleTypeId: -30, transactionCode: 'APP-182', pluginId: 6).save(); // url: '/appSms/listSms'
            new RoleFeatureMapping(roleTypeId: -30, transactionCode: 'APP-237', pluginId: 6).save(); // url: '/appSms/sendSms'

            // appTheme
            new RoleFeatureMapping(roleTypeId: -30, transactionCode: 'APP-124', pluginId: 6).save(); // url: '/appTheme/showTheme'
            new RoleFeatureMapping(roleTypeId: -30, transactionCode: 'APP-125', pluginId: 6).save(); // url: '/appTheme/updateTheme'
            new RoleFeatureMapping(roleTypeId: -30, transactionCode: 'APP-126', pluginId: 6).save(); // url: '/appTheme/listTheme'

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return false
        }
    }

    public boolean createRoleFeatureMapForExchangeHousePlugin() {
        try {
            int count = appVersionService.countByPluginId(ExchangeHousePluginConnector.PLUGIN_ID)
            if (count > 0) return true

            // default
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-160', pluginId: 9).save(); // url: 'exhExchangeHouse/renderExchangeHouseMenu'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-160', pluginId: 9).save(); // url: 'exhExchangeHouse/renderExchangeHouseMenu'
            new RoleFeatureMapping(roleTypeId: -203, transactionCode: 'EXH-160', pluginId: 9).save(); // url: 'exhExchangeHouse/renderExchangeHouseMenu'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-160', pluginId: 9).save(); // url: 'exhExchangeHouse/renderExchangeHouseMenu'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-160', pluginId: 9).save(); // url: 'exhExchangeHouse/renderExchangeHouseMenu'
            new RoleFeatureMapping(roleTypeId: -206, transactionCode: 'EXH-160', pluginId: 9).save(); // url: 'exhExchangeHouse/renderExchangeHouseMenu'

            // app note for cashier
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'APP-245', pluginId: 9).save(); // url: '/appNote/show'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'APP-247', pluginId: 9).save(); // url: '/appNote/list'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'APP-248', pluginId: 9).save(); // url: '/appNote/update'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'APP-249', pluginId: 9).save(); // url: '/appNote/create'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'APP-250', pluginId: 9).save(); // url: '/appNote/delete'

            // customer
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-1', pluginId: 9).save(); // url: 'exhCustomer/show'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-350', pluginId: 9).save(); // url: 'exhCustomer/showForSarb'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-2', pluginId: 9).save(); // url: 'exhCustomer/showForCustomerDetails'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-2', pluginId: 9).save(); // url: 'exhCustomer/showForCustomerDetails'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-2', pluginId: 9).save(); // url: 'exhCustomer/showForCustomerDetails'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-6', pluginId: 9).save(); // url: 'exhCustomer/list'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-6', pluginId: 9).save(); // url: 'exhCustomer/list'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-6', pluginId: 9).save(); // url: 'exhCustomer/list'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-356', pluginId: 9).save(); // url: 'exhCustomer/listForSarb'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-356', pluginId: 9).save(); // url: 'exhCustomer/listForSarb'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-356', pluginId: 9).save(); // url: 'exhCustomer/listForSarb'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-7', pluginId: 9).save(); // url: 'exhCustomer/delete'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-8', pluginId: 9).save(); // url: 'exhCustomer/showCustomerUser'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-9', pluginId: 9).save(); // url: 'exhCustomer/searchCustomerUser'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-9', pluginId: 9).save(); // url: 'exhCustomer/searchCustomerUser'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-10', pluginId: 9).save(); // url: 'exhCustomer/createCustomerUser'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-264', pluginId: 9).save(); // url: '/exhCustomer/viewDuplicateCustomerDetails'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-264', pluginId: 9).save(); // url: '/exhCustomer/viewDuplicateCustomerDetails'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-264', pluginId: 9).save(); // url: '/exhCustomer/viewDuplicateCustomerDetails'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-264', pluginId: 9).save(); // url: '/exhCustomer/viewDuplicateCustomerDetails'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-320', pluginId: 9).save(); // url: '/exhCustomer/listDuplicateCustomerDetails'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-320', pluginId: 9).save(); // url: '/exhCustomer/listDuplicateCustomerDetails'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-320', pluginId: 9).save(); // url: '/exhCustomer/listDuplicateCustomerDetails'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-320', pluginId: 9).save(); // url: '/exhCustomer/listDuplicateCustomerDetails'

            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-342', pluginId: 9).save(); // url: '/exhCustomer/showCustomerProfile'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-359', pluginId: 9).save(); // url: '/exhCustomer/showCustomerProfileForSarb'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-343', pluginId: 9).save(); // url: '/exhCustomer/updateCustomerProfile'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-360', pluginId: 9).save(); // url: '/exhCustomer/updateCustomerProfileForSarb'

            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-285', pluginId: 9).save(); // url: '/exhCustomer/createForCashier'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-352', pluginId: 9).save(); // url: '/exhCustomer/createForCashierForSarb'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-286', pluginId: 9).save(); // url: '/exhCustomer/createForAgent'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-353', pluginId: 9).save(); // url: '/exhCustomer/createForAgentForSarb'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-287', pluginId: 9).save(); // url: '/exhCustomer/updateForCashier'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-354', pluginId: 9).save(); // url: '/exhCustomer/updateForCashierForSarb'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-288', pluginId: 9).save(); // url: '/exhCustomer/updateForAgent'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-355', pluginId: 9).save(); // url: '/exhCustomer/updateForAgentForSarb'

            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-7', pluginId: 9).save(); // url: 'exhCustomer/delete'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-133', pluginId: 9).save(); // url: '/exhCustomer/showForAgent'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-351', pluginId: 9).save(); // url: '/exhCustomer/showForAgentForSarb'

            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-260', pluginId: 9).save(); //url '/exhCustomer/reloadCustomerDetails'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-260', pluginId: 9).save(); //url '/exhCustomer/reloadCustomerDetails'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-260', pluginId: 9).save(); //url '/exhCustomer/reloadCustomerDetails'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-262', pluginId: 9).save(); //url '/exhCustomer/reloadCustomerSummary'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-262', pluginId: 9).save(); //url '/exhCustomer/reloadCustomerSummary'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-262', pluginId: 9).save(); //url '/exhCustomer/reloadCustomerSummary'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-262', pluginId: 9).save(); //url '/exhCustomer/reloadCustomerSummary'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-263', pluginId: 9).save(); //url '/exhCustomer/verifyDuplicateCustomer'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-263', pluginId: 9).save(); //url '/exhCustomer/verifyDuplicateCustomer'

            //block customer for admin
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-257', pluginId: 9).save(); // url: '/exhCustomer/blockExhCustomer'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-357', pluginId: 9).save(); // url: '/exhCustomer/blockExhCustomerForSarb'
            // block customer for cashier
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-257', pluginId: 9).save(); // url: '/exhCustomer/blockExhCustomer'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-357', pluginId: 9).save(); // url: '/exhCustomer/blockExhCustomerForSarb'
            //unblock customer for admin
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-259', pluginId: 9).save(); // url: '/exhCustomer/unblockExhCustomer'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-358', pluginId: 9).save(); // url: '/exhCustomer/unblockExhCustomerForSarb'

            // Beneficiary
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-11', pluginId: 9).save(); // url: '/exhBeneficiary/show'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-12', pluginId: 9).save(); // url: '/exhBeneficiary/create'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-13', pluginId: 9).save(); // url: '/exhBeneficiary/update'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-13', pluginId: 9).save(); // url: '/exhBeneficiary/update'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-15', pluginId: 9).save(); // url: '/exhBeneficiary/list'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-16', pluginId: 9).save(); // url: '/exhBeneficiary/showNewForCustomer'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-17', pluginId: 9).save(); // url: '/exhBeneficiary/detailsForCustomer'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-18', pluginId: 9).save(); // url: '/exhBeneficiary/listForCustomer'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-232', pluginId: 9).save(); // url: '/exhBeneficiary/showApprovedForCustomer
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-233', pluginId: 9).save(); // url: '/exhBeneficiary/createForCustomer'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-234', pluginId: 9).save(); // url: '/exhBeneficiary/updateForCustomer'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-255', pluginId: 9).save(); // url: '/exhBeneficiary/approveBeneficiary'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-267', pluginId: 9).save(); // url: '/exhBeneficiary/showForAgent'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-272', pluginId: 9).save(); // url: '/exhBeneficiary/createForAgent'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-269', pluginId: 9).save(); // url: '/exhBeneficiary/updateForAgent'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-270', pluginId: 9).save(); // url: '/exhBeneficiary/listForAgent'

            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-296', pluginId: 9).save(); // url: '/exhBeneficiary/validateBeneficiary'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-296', pluginId: 9).save(); // url: '/exhBeneficiary/validateBeneficiary'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-296', pluginId: 9).save(); // url: '/exhBeneficiary/validateBeneficiary'

            // Task
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-170', pluginId: 9).save(); // url: '/exhTask/showExhTaskForAdmin'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-171', pluginId: 9).save(); // url: '/exhTask/showAgentTaskForAdmin'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-172', pluginId: 9).save(); // url: '/exhTask/showCustomerTaskForAdmin'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-176', pluginId: 9).save(); // url: '/exhTask/showExhTaskForCashier'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-177', pluginId: 9).save(); // url: '/exhTask/showAgentTaskForCashier'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-178', pluginId: 9).save(); // url: '/exhTask/showCustomerTaskForCashier'

            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-24', pluginId: 9).save(); // url: '/exhTask/list'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-173', pluginId: 9).save(); // url: '/exhTask/listExhTaskForAdmin'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-174', pluginId: 9).save(); // url: '/exhTask/listAgentTaskForAdmin'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-175', pluginId: 9).save(); // url: '/exhTask/listCustomerTaskForAdmin'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-179', pluginId: 9).save(); // url: '/exhTask/listExhTaskForCashier'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-180', pluginId: 9).save(); // url: '/exhTask/listAgentTaskForCashier'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-181', pluginId: 9).save(); // url: '/exhTask/listCustomerTaskForCashier'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-182', pluginId: 9).save(); // url: '/exhTask/approveTaskForCashier'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-26', pluginId: 9).save(); // url: '/exhTask/showForTaskSearch'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-26', pluginId: 9).save(); // url: '/exhTask/showForTaskSearch'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-134', pluginId: 9).save(); // url: '/exhTask/showForAgent'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-135', pluginId: 9).save(); // url: '/exhTask/listForAgent'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-138', pluginId: 9).save(); // url: '/exhTask/editForAgent'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-185', pluginId: 9).save(); // url: '/exhTask/calculateFeesAndCommission'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-185', pluginId: 9).save(); // url: '/exhTask/calculateFeesAndCommission'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-185', pluginId: 9).save(); // url: '/exhTask/calculateFeesAndCommission'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-341', pluginId: 9).save(); // url: '/exhTask/calculateFeesAndCommissionForAgent'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-137', pluginId: 9).save(); // url: '/exhTask/sendToExchangeHouse'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-168', pluginId: 9).save(); // url: '/exhTask/sendToExhForCustomer'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-239', pluginId: 9).save(); // url: '/exhTask/showForMakePayment'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-333', pluginId: 9).save(); // url: '/exhTask/showForTaskSearchForAgent'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-335', pluginId: 9).save(); // url: '/exhTask/showForTaskSearchForCustomer'
            new RoleFeatureMapping(roleTypeId: -203, transactionCode: 'EXH-338', pluginId: 9).save(); // url: '/exhTask/showForTaskSearchForOtherBank'
            new RoleFeatureMapping(roleTypeId: -203, transactionCode: 'EXH-339', pluginId: 9).save(); // url: '/exhTask/reloadShowTaskDetailsForOtherBank'
            new RoleFeatureMapping(roleTypeId: -203, transactionCode: 'EXH-200', pluginId: 9).save(); // url: '/exhTask/resolveTaskFromTaskStatus'
            new RoleFeatureMapping(roleTypeId: -203, transactionCode: 'EXH-201', pluginId: 9).save(); // url: '/exhTask/searchTaskForOtherBankTaskStatus'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-202', pluginId: 9).save(); // url: '/exhTask/createExhTaskNote'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-202', pluginId: 9).save(); // url: '/exhTask/createExhTaskNote'
            new RoleFeatureMapping(roleTypeId: -203, transactionCode: 'EXH-202', pluginId: 9).save(); // url: '/exhTask/createExhTaskNote'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-202', pluginId: 9).save(); // url: '/exhTask/createExhTaskNote'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-202', pluginId: 9).save(); // url: '/exhTask/createExhTaskNote'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-203', pluginId: 9).save(); // url: '/exhTask/searchTaskForTaskStatus'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-203', pluginId: 9).save(); // url: '/exhTask/searchTaskForTaskStatus'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-344', pluginId: 9).save(); // url: '/exhTask/searchTaskForAgentTaskStatus'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-345', pluginId: 9).save(); // url: '/exhTask/searchTaskForCustomerTaskStatus'

            //TASK CRUD
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-273', pluginId: 9).save(); // url: '/exhTask/createTaskForCashier'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-361', pluginId: 9).save(); // url: '/exhTask/createTaskForCashierForSarb'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-274', pluginId: 9).save(); // url: '/exhTask/createTaskForAgent'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-362', pluginId: 9).save(); // url: '/exhTask/createTaskForAgentForSarb'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-275', pluginId: 9).save(); // url: '/exhTask/createTaskForCustomer'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-363', pluginId: 9).save(); // url: '/exhTask/createTaskForCustomer'

            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-276', pluginId: 9).save(); // url: '/exhTask/updateTaskForCashier'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-364', pluginId: 9).save(); // url: '/exhTask/updateTaskForCashierForSarb'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-279', pluginId: 9).save(); // url: '/exhTask/updateCustomerTaskForCashier'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-365', pluginId: 9).save(); // url: '/exhTask/updateCustomerTaskForCashierForSarb'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-277', pluginId: 9).save(); // url: '/exhTask/updateTaskForAgent'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-366', pluginId: 9).save(); // url: '/exhTask/updateTaskForAgentForSarb'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-278', pluginId: 9).save(); // url: '/exhTask/updateTaskForCustomer'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-367', pluginId: 9).save(); // url: '/exhTask/updateTaskForCustomerForSarb'

            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-291', pluginId: 9).save(); // url: '/exhTask/reloadCurrencyDropDownForExhTask'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-291', pluginId: 9).save(); // url: '/exhTask/reloadCurrencyDropDownForExhTask'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-291', pluginId: 9).save(); // url: '/exhTask/reloadCurrencyDropDownForExhTask'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-291', pluginId: 9).save(); // url: '/exhTask/reloadCurrencyDropDownForExhTask'
            //for exh task summary
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-292', pluginId: 9).save(); // url: '/exhTask/reloadExhTaskSummaryTaglib'

            // task for admin
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-310', pluginId: 9).save(); // url: '/exhTask/sendToBankWithRest'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-315', pluginId: 9).save(); // url: '/exhTask/verifyAndSendExceptionalTaskToBankWithRest'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-30', pluginId: 9).save(); // url: '/exhTask/cancelSpecificTask'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-31', pluginId: 9).save(); // url: '/exhTask/showTaskDetails'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-31', pluginId: 9).save(); // url: '/exhTask/showTaskDetails'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-34', pluginId: 9).save(); // url: '/exhTask/showForCustomer'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-192', pluginId: 9).save(); // url: '/exhTask/showUnApprovedTaskForCustomer'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-193', pluginId: 9).save(); // url: '/exhTask/listUnApprovedTaskForCustomer'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-167', pluginId: 9).save(); // url: '/exhTask/showApprovedTaskForCustomer'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-169', pluginId: 9).save(); // url: '/exhTask/listApprovedTaskForCustomer'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-187', pluginId: 9).save(); // url: '/exhTask/showDisbursedTaskForCustomer'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-198', pluginId: 9).save(); // url: '/exhTask/listDisbursedTaskForCustomer'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-35', pluginId: 9).save(); // url: '/exhTask/listForCustomer'
            new RoleFeatureMapping(roleTypeId: -203, transactionCode: 'EXH-36', pluginId: 9).save(); // url: '/exhTask/showForOtherBankUser'
            new RoleFeatureMapping(roleTypeId: -203, transactionCode: 'EXH-37', pluginId: 9).save(); // url: '/exhTask/listForOtherBankUser'
            new RoleFeatureMapping(roleTypeId: -203, transactionCode: 'EXH-38', pluginId: 9).save(); // url: '/exhTask/resolveTaskForOtherBank'
            new RoleFeatureMapping(roleTypeId: -203, transactionCode: 'EXH-39', pluginId: 9).save(); // url: '/exhTask/downloadCsvForOtherBank'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-258', pluginId: 9).save(); // url: '/exhTask/showDetailsForReplaceTask'

            //bank drop down to reload independently
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-256', pluginId: 9).save(); //url: '/exhTask/reloadBankByTaskStatusAndTaskType'
            new RoleFeatureMapping(roleTypeId: -203, transactionCode: 'EXH-256', pluginId: 9).save(); //url: '/exhTask/reloadBankByTaskStatusAndTaskType'

            // setting remitance purpose
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-56', pluginId: 9).save(); // url: '/exhRemittancePurpose/show'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-57', pluginId: 9).save(); // url: '/exhRemittancePurpose/create'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-58', pluginId: 9).save(); // url: '/exhRemittancePurpose/update'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-60', pluginId: 9).save(); // url: '/exhRemittancePurpose/list'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-61', pluginId: 9).save(); // url: '/exhRemittancePurpose/delete'

            // setting photo id type
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-62', pluginId: 9).save(); // url: '/exhPhotoIdType/show'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-63', pluginId: 9).save(); // url: '/exhPhotoIdType/create'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-64', pluginId: 9).save(); // url: '/exhPhotoIdType/update'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-66', pluginId: 9).save(); // url: '/exhPhotoIdType/list'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-67', pluginId: 9).save(); // url: '/exhPhotoIdType/delete'


            // setting currency conversion
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-95', pluginId: 9).save(); // url: '/exhCurrencyConversion/show'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-96', pluginId: 9).save(); // url: '/exhCurrencyConversion/create'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-97', pluginId: 9).save(); // url: '/exhCurrencyConversion/update'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-98', pluginId: 9).save(); // url: '/exhCurrencyConversion/list'

            // setting Exh Agent
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-145', pluginId: 9).save(); // url: '/exhAgent/show'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-146', pluginId: 9).save(); // url: '/exhAgent/create'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-147', pluginId: 9).save(); // url: '/exhAgent/list'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-149', pluginId: 9).save(); // url: '/exhAgent/update'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-150', pluginId: 9).save(); // url: '/exhAgent/delete'

            // setting Exh Agent Currency Posting
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-139', pluginId: 9).save(); // url: '/exhAgentCurrencyPosting/show'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-140', pluginId: 9).save(); // url: '/exhAgentCurrencyPosting/create'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-141', pluginId: 9).save(); // url: '/exhAgentCurrencyPosting/list'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-143', pluginId: 9).save(); // url: '/exhAgentCurrencyPosting/update'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-144', pluginId: 9).save(); // url: '/exhAgentCurrencyPosting/delete'

            new RoleFeatureMapping(roleTypeId: -206, transactionCode: 'EXH-20', pluginId: 9).save(); // url: '/exhRegularFee/show'
            new RoleFeatureMapping(roleTypeId: -206, transactionCode: 'EXH-27', pluginId: 9).save(); // url: '/exhRegularFee/update'
            new RoleFeatureMapping(roleTypeId: -206, transactionCode: 'EXH-28', pluginId: 9).save(); // url: '/exhRegularFee/calculate'

            // sanction
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-131', pluginId: 9).save(); // url: '/exhSanctionHmTreasury/showSanctionUpload'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-132', pluginId: 9).save(); // url: '/exhSanctionHmTreasury/uploadSanctionFile'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-297', pluginId: 9).save(); // url: '/exhSanctionOfacSdn/showSanctionUpload'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-298', pluginId: 9).save(); // url: '/exhSanctionOfacSdn/uploadSanctionFile'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-299', pluginId: 9).save(); // url: '/exhSanctionOfacAdd/showSanctionUpload'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-300', pluginId: 9).save(); // url: '/exhSanctionOfacAdd/uploadSanctionFile'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-301', pluginId: 9).save(); // url: '/exhSanctionOfacAlt/showSanctionUpload'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-302', pluginId: 9).save(); // url: '/exhSanctionOfacSAlt/uploadSanctionFile'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-303', pluginId: 9).save(); // url: '/exhSanctionModel/show'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-305', pluginId: 9).save(); // url: '/exhSanctionModel/showMatchedSanction'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-305', pluginId: 9).save(); // url: '/exhSanctionModel/showMatchedSanction'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-305', pluginId: 9).save(); // url: '/exhSanctionModel/showMatchedSanction'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-305', pluginId: 9).save(); // url: '/exhSanctionModel/showMatchedSanction'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-306', pluginId: 9).save(); // url: '/exhSanctionModel/list'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-307', pluginId: 9).save(); // url: '/exhSanctionModel/listOfSanction'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-307', pluginId: 9).save(); // url: '/exhSanctionModel/listOfSanction'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-307', pluginId: 9).save(); // url: '/exhSanctionModel/listOfSanction'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-307', pluginId: 9).save(); // url: '/exhSanctionModel/listOfSanction'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-309', pluginId: 9).save(); // url: '/exhSanctionModel/totalSanctionCount'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-309', pluginId: 9).save(); // url: '/exhSanctionModel/totalSanctionCount'

            // Agent wise commission
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-188', pluginId: 9).save(); // url: '/exhReport/showAgentWiseCommissionForAdmin'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-189', pluginId: 9).save(); // url: '/exhReport/listAgentWiseCommissionForAdmin'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-190', pluginId: 9).save(); // url: '/exhReport/showAgentWiseCommissionForAgent'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-191', pluginId: 9).save(); // url: '/exhReport/listAgentWiseCommissionForAgent'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-195', pluginId: 9).save(); // url: '/exhReport/downloadAgentWiseCommissionForAdmin'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-194', pluginId: 9).save(); // url: '/exhReport/downloadAgentWiseCommissionForAgent'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-116', pluginId: 9).save(); // url: '/exhReport/showCashierWiseReportForAdmin'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-117', pluginId: 9).save(); // url: '/exhReport/showCashierWiseReportForCashier'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-118', pluginId: 9).save(); // url: '/exhReport/listCashierWiseReportForAdmin'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-119', pluginId: 9).save(); // url: '/exhReport/listCashierWiseReportForCashier'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-120', pluginId: 9).save(); // url: '/exhReport/showSummaryReportForAdmin'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-121', pluginId: 9).save(); // url: '/exhReport/listReportSummaryForAdmin'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-122', pluginId: 9).save(); // url: '/exhReport/downloadRemittanceSummaryReport'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-123', pluginId: 9).save(); // url: '/exhReport/downloadCashierWiseTaskReport'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-123', pluginId: 9).save(); // url: '/exhReport/downloadCashierWiseTaskReport'

            // Reports
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-107', pluginId: 9).save(); // url: '/exhReport/showCustomerHistory'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-107', pluginId: 9).save(); // url: '/exhReport/showCustomerHistory'

            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-204', pluginId: 9).save(); // url: '/exhReport/downloadCustomerHistory'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-204', pluginId: 9).save(); // url: '/exhReport/downloadCustomerHistory'

            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-109', pluginId: 9).save(); // url: '/exhReport/listForCustomerRemittance'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-109', pluginId: 9).save(); // url: '/exhReport/listForCustomerRemittance'

            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-110', pluginId: 9).save(); // url: '/exhReport/showRemittanceSummary'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-110', pluginId: 9).save(); // url: '/exhReport/showRemittanceSummary'

            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-111', pluginId: 9).save(); // url: '/exhReport/getRemittanceSummaryReport'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-111', pluginId: 9).save(); // url: '/exhReport/getRemittanceSummaryReport'

            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-112', pluginId: 9).save(); // url: '/exhReport/showInvoice'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-112', pluginId: 9).save(); // url: '/exhReport/showInvoice'

            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-115', pluginId: 9).save(); // url: '/exhReport/downloadInvoice'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-115', pluginId: 9).save(); // url: '/exhReport/downloadInvoice'

            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-205', pluginId: 9).save(); // url: '/exhReport/showCustomerRemittanceSummary'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-205', pluginId: 9).save(); // url: '/exhReport/showCustomerRemittanceSummary'

            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-206', pluginId: 9).save(); // url: '/exhReport/downloadCustomerRemittanceSummary'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-206', pluginId: 9).save(); // url: '/exhReport/downloadCustomerRemittanceSummary'

            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-235', pluginId: 9).save(); // url: '/exhReport/downloadRemittanceTransactionCsv'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'EXH-237', pluginId: 9).save(); // url: '/exhReport/downloadCustomerCSV'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-254', pluginId: 9).save(); // url: '/exhReport/downloadCustomer'
            new RoleFeatureMapping(roleTypeId: -12, transactionCode: 'EXH-254', pluginId: 9).save(); // url: '/exhReport/downloadCustomer'

            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-251', pluginId: 9).save(); // url: '/exhReport/listTransactionSummary'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-252', pluginId: 9).save(); // url: '/exhReport/downloadTransactionSummary'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-253', pluginId: 9).save(); // url: '/exhReport/listRemittanceTransaction'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-234', pluginId: 9).save(); // url: '/exhReport/downloadRemittanceTransaction'

            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-265', pluginId: 9).save(); // url: '/exhReport/downloadCustomerTransaction'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-265', pluginId: 9).save(); // url: '/exhReport/downloadCustomerTransaction'

            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-268', pluginId: 9).save(); // url: '/exhReport/downloadBeneficiaryDetails'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-268', pluginId: 9).save(); // url: '/exhReport/downloadBeneficiaryDetails'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-268', pluginId: 9).save(); // url: '/exhReport/downloadBeneficiaryDetails'

            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-293', pluginId: 9).save(); // url: '/exhReport/showLinkedBeneficiary'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-294', pluginId: 9).save(); // url: '/exhReport/listLinkedBeneficiary'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-295', pluginId: 9).save(); // url: '/exhReport/downloadLinkedBeneficiary'

            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-312', pluginId: 9).save(); // url: '/exhReport/showExceptionalTask'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-313', pluginId: 9).save(); // url: '/exhReport/listExceptionalTask'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-314', pluginId: 9).save(); // url: '/exhReport/downloadExceptionalTask'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-316', pluginId: 9).save(); // url: '/exhReport/showRemittedTask'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-317', pluginId: 9).save(); // url: '/exhReport/listRemittedTask'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-318', pluginId: 9).save(); // url: '/exhReport/downloadRemittedTask'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-319', pluginId: 9).save(); // url: '/exhReport/reloadTaskInvoice'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-319', pluginId: 9).save(); // url: '/exhReport/reloadTaskInvoice'

            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-321', pluginId: 9).save(); // url: '/exhReport/downloadCustomerAllDocuments'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-322', pluginId: 9).save(); // url: '/exhReport/showExhCustomer'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-323', pluginId: 9).save(); // url: '/exhReport/listExhCustomer'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-324', pluginId: 9).save(); // url: '/exhReport/downloadCustomerList'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-326', pluginId: 9).save(); // url: '/exhReport/downloadDailyRemittanceSummary'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-326', pluginId: 9).save(); // url: '/exhReport/downloadDailyRemittanceSummary'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-327', pluginId: 9).save(); // url: '/exhReport/showInvoiceForAgent'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-328', pluginId: 9).save(); // url: '/exhReport/reloadInvoiceForAgent'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-329', pluginId: 9).save(); // url: '/exhReport/showInvoiceForCustomer'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-330', pluginId: 9).save(); // url: '/exhReport/reloadInvoiceForCustomer'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-331', pluginId: 9).save(); // url: '/exhReport/downloadInvoiceForCustomer'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-332', pluginId: 9).save(); // url: '/exhReport/downloadInvoiceForAgent'
            new RoleFeatureMapping(roleTypeId: -203, transactionCode: 'EXH-347', pluginId: 9).save(); // url: '/exhReport/showInvoiceForOtherBank'
            new RoleFeatureMapping(roleTypeId: -203, transactionCode: 'EXH-348', pluginId: 9).save(); // url: '/exhReport/reloadInvoiceForOtherBank'
            new RoleFeatureMapping(roleTypeId: -203, transactionCode: 'EXH-349', pluginId: 9).save(); // url: '/exhReport/downloadInvoiceForOtherBank'

            // /exhBeneficiary/listLinkedBeneficiary
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-207', pluginId: 9).save(); // url: '/exhBeneficiary/listLinkedBeneficiary'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-207', pluginId: 9).save(); // url: '/exhBeneficiary/listLinkedBeneficiary'
            // /exhCustomerBeneficiary/create
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-208', pluginId: 9).save(); // url: '/exhCustomerBeneficiary/create'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'EXH-208', pluginId: 9).save(); // url: '/exhCustomerBeneficiary/create'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'EXH-208', pluginId: 9).save(); // url: '/exhCustomerBeneficiary/create'

            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-209', pluginId: 9).save(); // url: '/exhReport/listForCustomerRemittanceSummary'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-209', pluginId: 9).save(); // url: '/exhReport/listForCustomerRemittanceSummary'

            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-210', pluginId: 9).save(); // url: '/exhReport/listCustomerTransactionSummary'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-210', pluginId: 9).save(); // url: '/exhReport/listCustomerTransactionSummary'

            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-211', pluginId: 9).save(); // url: '/exhReport/downloadCustomerTransactionSummary'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-211', pluginId: 9).save(); // url: '/exhReport/downloadCustomerTransactionSummary'

            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-212', pluginId: 9).save(); // url: '/exhReport/showCustomerTransactionSummary'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'EXH-212', pluginId: 9).save(); // url: '/exhReport/showCustomerTransactionSummary'

            // AppAttachment for cashier
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'APP-144', pluginId: 9).save(); // url: '/appAttachment/show'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'APP-146', pluginId: 9).save(); // url: '/appAttachment/list'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'APP-147', pluginId: 9).save(); // url: '/appAttachment/update'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'APP-148', pluginId: 9).save(); // url: '/appAttachment/create'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'APP-149', pluginId: 9).save(); // url: '/appAttachment/delete'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'APP-421', pluginId: 9).save(); // url: '/appAttachment/upload'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'APP-153', pluginId: 9).save(); // url: '/contentCategory/dropDownContentCategoryReload'

            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-243', pluginId: 9).save(); // url: '/exhPostalCode/show
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-244', pluginId: 9).save(); // url: '/exhPostalCode/create
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-246', pluginId: 9).save(); // url: '/exhPostalCode/update
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-247', pluginId: 9).save(); // url: '/exhPostalCode/delete
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-248', pluginId: 9).save(); // url: '/exhPostalCode/list


            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-249', pluginId: 9).save(); // url: '/exhReport/showTransactionSummary
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'EXH-250', pluginId: 9).save(); // url: '/exhReport/showRemittanceTransaction

            //test data
            new RoleFeatureMapping(roleTypeId: -206, transactionCode: 'EXH-363', pluginId: 9).save(); // '/exhTestData/create'
            new RoleFeatureMapping(roleTypeId: -206, transactionCode: 'EXH-364', pluginId: 9).save(); // url: '/exhTestData/delete'

            //some application plugin request map
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'APP-236', pluginId: 9).save(); //url: '/district/reloadDistrictDropDown'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'APP-236', pluginId: 9).save(); //url: '/district/reloadDistrictDropDown'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'APP-236', pluginId: 9).save(); //url: '/district/reloadDistrictDropDown'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'APP-209', pluginId: 9).save(); //url: '/appBankBranch/reloadBranchesDropDownByBankAndDistrict'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'APP-209', pluginId: 9).save(); //url: '/appBankBranch/reloadBranchesDropDownByBankAndDistrict'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'APP-209', pluginId: 9).save(); //url: '/appBankBranch/reloadBranchesDropDownByBankAndDistrict'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'APP-210', pluginId: 9).save(); //url: '/appBankBranch/listDistributionPoint'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'APP-210', pluginId: 9).save(); //url: '/appBankBranch/listDistributionPoint'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'APP-210', pluginId: 9).save(); //url: '/appBankBranch/listDistributionPoint'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'APP-260', pluginId: 9).save(); //url: '/appBank/reloadBankDropDownTagLib'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'APP-260', pluginId: 9).save(); //url: '/appBank/reloadBankDropDownTagLib'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'APP-260', pluginId: 9).save(); //url: '/appBank/reloadBankDropDownTagLib'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'APP-261', pluginId: 9).save(); // url: '/appNote/viewEntityNote'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'APP-261', pluginId: 9).save(); // url: '/appNote/viewEntityNote'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'APP-261', pluginId: 9).save(); // url: '/appNote/viewEntityNote'
            new RoleFeatureMapping(roleTypeId: -201, transactionCode: 'APP-345', pluginId: 9).save(); // url: '/appNote/listEntityNote'
            new RoleFeatureMapping(roleTypeId: -202, transactionCode: 'APP-345', pluginId: 9).save(); // url: '/appNote/listEntityNote'
            new RoleFeatureMapping(roleTypeId: -203, transactionCode: 'APP-345', pluginId: 9).save(); // url: '/appNote/listEntityNote'
            new RoleFeatureMapping(roleTypeId: -204, transactionCode: 'APP-345', pluginId: 9).save(); // url: '/appNote/listEntityNote'
            new RoleFeatureMapping(roleTypeId: -206, transactionCode: 'APP-385', pluginId: 9).save(); // url: '/schemaInformation/listSchemaInfo'
            new RoleFeatureMapping(roleTypeId: -206, transactionCode: 'APP-386', pluginId: 9).save(); // url: '/testData/list'

            //Application plugin request map for exh admin
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'APP-144', pluginId: 9).save(); // url: '/appAttachment/show'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'APP-146', pluginId: 9).save(); // url: '/appAttachment/list'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'APP-147', pluginId: 9).save(); // url: '/appAttachment/update'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'APP-148', pluginId: 9).save(); // url: '/appAttachment/create'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'APP-149', pluginId: 9).save(); // url: '/appAttachment/delete'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'APP-421', pluginId: 9).save(); // url: '/appAttachment/upload'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'APP-153', pluginId: 9).save(); // url: '/contentCategory/dropDownContentCategoryReload'

            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'APP-238', pluginId: 9).save(); // url: '/appUserEntity/show'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'APP-239', pluginId: 9).save(); // url: '/appUserEntity/create'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'APP-240', pluginId: 9).save(); // url: '/appUserEntity/update'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'APP-241', pluginId: 9).save(); // url: '/appUserEntity/delete'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'APP-242', pluginId: 9).save(); // url: '/appUserEntity/list'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'APP-244', pluginId: 9).save(); // url: '/appUserEntity/dropDownAppUserEntityReload'

            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'APP-245', pluginId: 9).save(); // url: '/appNote/show'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'APP-247', pluginId: 9).save(); // url: '/appNote/list'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'APP-248', pluginId: 9).save(); // url: '/appNote/update'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'APP-249', pluginId: 9).save(); // url: '/appNote/create'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'APP-250', pluginId: 9).save(); // url: '/appNote/delete'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'APP-261', pluginId: 9).save(); // url: '/appNote/viewEntityNote'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'APP-252', pluginId: 9).save(); //url: '/appNote/reloadEntityNote'
            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'APP-345', pluginId: 9).save(); // url: '/appNote/listEntityNote'

            new RoleFeatureMapping(roleTypeId: -205, transactionCode: 'APP-210', pluginId: 9).save(); //url: '/appBankBranch/listDistributionPoint'

            //Application request map for Exh development user
            new RoleFeatureMapping(roleTypeId: -206, transactionCode: 'APP-99', pluginId: 9).save(); // url: '/systemConfiguration/show'
            new RoleFeatureMapping(roleTypeId: -206, transactionCode: 'APP-100', pluginId: 9).save(); // url: '/systemConfiguration/list'
            new RoleFeatureMapping(roleTypeId: -206, transactionCode: 'APP-102', pluginId: 9).save(); // url: '/systemConfiguration/update'

            new RoleFeatureMapping(roleTypeId: -206, transactionCode: 'APP-103', pluginId: 9).save(); // url: '/systemEntity/show'
            new RoleFeatureMapping(roleTypeId: -206, transactionCode: 'APP-104', pluginId: 9).save(); // url: '/systemEntity/create'
            new RoleFeatureMapping(roleTypeId: -206, transactionCode: 'APP-105', pluginId: 9).save(); // url: '/systemEntity/list'
            new RoleFeatureMapping(roleTypeId: -206, transactionCode: 'APP-107', pluginId: 9).save(); // url: '/systemEntity/update'
            new RoleFeatureMapping(roleTypeId: -206, transactionCode: 'APP-173', pluginId: 9).save(); // url: '/systemEntity/delete'

            new RoleFeatureMapping(roleTypeId: -206, transactionCode: 'APP-108', pluginId: 9).save(); // url: '/systemEntityType/show'
            new RoleFeatureMapping(roleTypeId: -206, transactionCode: 'APP-109', pluginId: 9).save(); // url: '/systemEntityType/list'

            new RoleFeatureMapping(roleTypeId: -206, transactionCode: 'APP-187', pluginId: 9).save(); // url: '/appMail/show'
            new RoleFeatureMapping(roleTypeId: -206, transactionCode: 'APP-188', pluginId: 9).save(); // url: '/appMail/update'
            new RoleFeatureMapping(roleTypeId: -206, transactionCode: 'APP-189', pluginId: 9).save(); // url: '/appMail/list'
            new RoleFeatureMapping(roleTypeId: -206, transactionCode: 'APP-251', pluginId: 9).save(); // url: '/appMail/testAppMail'

            new RoleFeatureMapping(roleTypeId: -206, transactionCode: 'APP-180', pluginId: 9).save(); // url: '/appSms/showSms'
            new RoleFeatureMapping(roleTypeId: -206, transactionCode: 'APP-181', pluginId: 9).save(); // url: '/appSms/updateSms'
            new RoleFeatureMapping(roleTypeId: -206, transactionCode: 'APP-182', pluginId: 9).save(); // url: '/appSms/listSms'
            new RoleFeatureMapping(roleTypeId: -206, transactionCode: 'APP-237', pluginId: 9).save(); // url: '/appSms/sendSms'

            new RoleFeatureMapping(roleTypeId: -206, transactionCode: 'APP-279', pluginId: 9).save(); //url: '/appSchedule/show'
            new RoleFeatureMapping(roleTypeId: -206, transactionCode: 'APP-280', pluginId: 9).save(); //url: '/appSchedule/list'
            new RoleFeatureMapping(roleTypeId: -206, transactionCode: 'APP-282', pluginId: 9).save(); //url: '/appSchedule/update'
            new RoleFeatureMapping(roleTypeId: -206, transactionCode: 'APP-283', pluginId: 9).save(); //url: '/appSchedule/testExecute'

            // appTheme
            new RoleFeatureMapping(roleTypeId: -206, transactionCode: 'APP-124', pluginId: 9).save(); // url: '/appTheme/showTheme'
            new RoleFeatureMapping(roleTypeId: -206, transactionCode: 'APP-125', pluginId: 9).save(); // url: '/appTheme/updateTheme'
            new RoleFeatureMapping(roleTypeId: -206, transactionCode: 'APP-126', pluginId: 9).save(); // url: '/appTheme/listTheme'

            return true

        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createRoleFeatureMapForProjectTrackPlugin() {
        try {

        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-1', pluginId: 10).save(); // url: '/ptPlugin/renderProjectTrackMenu'
        new RoleFeatureMapping(roleTypeId: -32, transactionCode: 'PT-1', pluginId: 10).save(); // url: '/ptPlugin/renderProjectTrackMenu'

        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-138', pluginId: 10).save(); // url: '/ptBacklog/searchBacklogForGroup'

        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-33', pluginId: 10).save(); // url: '/ptProject/show'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-34', pluginId: 10).save(); // url: '/ptProject/create'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-35', pluginId: 10).save(); // url: '/ptProject/update'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-36', pluginId: 10).save(); // url: '/ptProject/delete'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-38', pluginId: 10).save(); // url: '/ptProject/list'

        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-21', pluginId: 10).save(); // url: '/ptModule/show'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-22', pluginId: 10).save(); // url: '/ptModule/create'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-24', pluginId: 10).save(); // url: '/ptModule/update'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-25', pluginId: 10).save(); // url: '/ptModule/delete'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-26', pluginId: 10).save(); // url: '/ptModule/list'

        //PtEntity
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-184', pluginId: 10).save(); // url: '/ptEntity/show'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-185', pluginId: 10).save(); // url: '/ptEntity/list'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-186', pluginId: 10).save(); // url: '/ptEntity/create'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-187', pluginId: 10).save(); // url: '/ptEntity/update'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-188', pluginId: 10).save(); // url: '/ptEntity/delete'

        //PtEntityBacklog
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-200', pluginId: 10).save(); // url: '/ptEntityBacklog/show'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-201', pluginId: 10).save(); // url: '/ptEntityBacklog/list'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-202', pluginId: 10).save(); // url: '/ptEntityBacklog/create'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-203', pluginId: 10).save(); // url: '/ptEntityBacklog/update'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-204', pluginId: 10).save(); // url: '/ptEntityBacklog/delete'

        //PtPrimaryKey
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-194', pluginId: 10).save(); // url: '/ptPrimaryKey/show'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-195', pluginId: 10).save(); // url: '/ptPrimaryKey/list'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-196', pluginId: 10).save(); // url: '/ptPrimaryKey/create'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-197', pluginId: 10).save(); // url: '/ptPrimaryKey/update'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-198', pluginId: 10).save(); // url: '/ptPrimaryKey/delete'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-199', pluginId: 10).save(); // url: '/ptPrimaryKey/dropDownPtField'

        //PtForeignKey
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-206', pluginId: 10).save(); // url: '/ptForeignKey/show'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-207', pluginId: 10).save(); // url: '/ptForeignKey/list'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-208', pluginId: 10).save(); // url: '/ptForeignKey/create'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-209', pluginId: 10).save(); // url: '/ptForeignKey/delete'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-210', pluginId: 10).save(); // url: '/ptForeignKey/dropDownPtEntity'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-211', pluginId: 10).save(); // url: '/ptField/dropDownFieldReload'


        //PtField
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-189', pluginId: 10).save(); // url: '/ptField/show'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-190', pluginId: 10).save(); // url: '/ptField/list'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-191', pluginId: 10).save(); // url: '/ptField/create'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-192', pluginId: 10).save(); // url: '/ptField/update'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-193', pluginId: 10).save(); // url: '/ptField/delete'

        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-27', pluginId: 10).save(); // url: '/ptProjectModule/show'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-28', pluginId: 10).save(); // url: '/ptProjectModule/create'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-30', pluginId: 10).save(); // url: '/ptProjectModule/update'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-31', pluginId: 10).save(); // url: '/ptProjectModule/delete'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-32', pluginId: 10).save(); // url: '/ptProjectModule/list'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-155', pluginId: 10).save(); // url: '/ptProjectModule/dropDownProjectModuleReload'

        new RoleFeatureMapping(roleTypeId: -32, transactionCode: 'PT-156', pluginId: 10).save(); // url: '/ptBacklog/showPtBacklogForRemove'
        new RoleFeatureMapping(roleTypeId: -32, transactionCode: 'PT-157', pluginId: 10).save(); // url: '/ptBacklog/listPtBacklogForRemove'
        new RoleFeatureMapping(roleTypeId: -32, transactionCode: 'PT-158', pluginId: 10).save(); // url: '/ptBacklog/removePtBacklog'

        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-159', pluginId: 10).save(); // url: '/ptReport/downloadActiveSprintUseCaseReport'
        new RoleFeatureMapping(roleTypeId: -32, transactionCode: 'PT-159', pluginId: 10).save(); // url: '/ptReport/downloadActiveSprintUseCaseReport'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-182', pluginId: 10).save(); // url: '/ptReport/downloadSprintSummaryReport'
        new RoleFeatureMapping(roleTypeId: -32, transactionCode: 'PT-94', pluginId: 10).save(); // url: '/ptReport/showForBacklogDetails'

        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-166', pluginId: 10).save(); // url: '/ptBacklog/listPtBacklogInPlanForWidget'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-174', pluginId: 10).save(); // url: '/ptBacklog/listPtBacklogInProgressForWidget'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-175', pluginId: 10).save(); // url: '/ptBacklog/listPtBacklogDoneForWidget'

        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-178', pluginId: 10).save(); // url: '/ptBacklog/listPtBacklogForListView'
        new RoleFeatureMapping(roleTypeId: -32, transactionCode: 'PT-178', pluginId: 10).save(); // url: '/ptBacklog/listPtBacklogForListView'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-179', pluginId: 10).save(); // url: '/ptBacklog/listSprintPtBacklogForListView'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-177', pluginId: 10).save(); // url: '/ptBacklog/listPtBacklogForOwner'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-205', pluginId: 10).save(); // url: '/ptBacklog/searchBacklogForEntity'

        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-180', pluginId: 10).save(); // url: '/ptBug/listSprintBugForListView'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-181', pluginId: 10).save(); // url: '/ptBug/listPtBugForListView'
        new RoleFeatureMapping(roleTypeId: -32, transactionCode: 'PT-181', pluginId: 10).save(); // url: '/ptBug/listPtBugForListView'

        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-2', pluginId: 10).save(); // url: '/ptBacklog/show'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-3', pluginId: 10).save(); // url: '/ptBacklog/list'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-5', pluginId: 10).save(); // url: '/ptBacklog/create'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-6', pluginId: 10).save(); // url: '/ptBacklog/update'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-7', pluginId: 10).save(); // url: '/ptBacklog/delete'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-39', pluginId: 10).save(); // url: '/ptBacklog/showBackLogForSprint'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-46', pluginId: 10).save(); // url: '/ptBacklog/createBackLogForSprint'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-47', pluginId: 10).save(); // url: '/ptBacklog/deleteBackLogForSprint'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-48', pluginId: 10).save(); // url: '/ptBacklog/listBackLogForSprint'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-71', pluginId: 10).save(); // url: '/ptBacklog/getBacklogList'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-83', pluginId: 10).save(); // url: '/ptBacklog/showForActive'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-84', pluginId: 10).save(); // url: '/ptBacklog/listForActive'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-85', pluginId: 10).save(); // url: '/ptBacklog/showForInActive'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-86', pluginId: 10).save(); // url: '/ptBacklog/listForInActive'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-94', pluginId: 10).save(); // url: '/ptReport/showForBacklogDetails'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-149', pluginId: 10).save(); // url: '/ptBacklog/markAsDefined'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-40', pluginId: 10).save(); // url: '/ptAcceptanceCriteria/show'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-41', pluginId: 10).save(); // url: '/ptAcceptanceCriteria/list'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-43', pluginId: 10).save(); // url: '/ptAcceptanceCriteria/create'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-44', pluginId: 10).save(); // url: '/ptAcceptanceCriteria/update'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-45', pluginId: 10).save(); // url: '/ptAcceptanceCriteria/delete'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-98', pluginId: 10).save(); // url: '/ptAcceptanceCriteria/showForMyBacklog'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-99', pluginId: 10).save(); // url: '/ptBug/reOpenBug'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-100', pluginId: 10).save(); // url: '/ptBug/closeBug'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-102', pluginId: 10).save(); // url: '/ptBug/showBugDetails'
        new RoleFeatureMapping(roleTypeId: -32, transactionCode: 'PT-102', pluginId: 10).save(); // url: '/ptBug/showBugDetails'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-104', pluginId: 10).save(); // url: '/ptBug/showOrphanBug'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-105', pluginId: 10).save(); // url: '/ptBug/createOrphanBug'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-106', pluginId: 10).save(); // url: '/ptBug/updateOrphanBug'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-107', pluginId: 10).save(); // url: '/ptBug/listOrphanBug'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-152', pluginId: 10).save(); // url: '/ptBug/listBugForSearch'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-153', pluginId: 10).save(); // url: '/ptBug/refreshBugDetails'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-72', pluginId: 10).save(); // url: '/ptAcceptanceCriteria/showForMyBacklog'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-73', pluginId: 10).save(); // url: '/ptAcceptanceCriteria/updateForMyBacklog'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-68', pluginId: 10).save(); // url: '/ptModule/listModuleByProject'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-8', pluginId: 10).save(); // url: '/ptBug/show'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-9', pluginId: 10).save(); // url: '/ptBug/create'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-10', pluginId: 10).save(); // url: '/ptBug/update'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-11', pluginId: 10).save(); // url: '/ptBug/delete'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-13', pluginId: 10).save(); // url: '/ptBug/list'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-78', pluginId: 10).save(); // url: '/ptBug/showBugForSprint'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-79', pluginId: 10).save(); // url: '/ptBug/createBugForSprint'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-80', pluginId: 10).save(); // url: '/ptBug/deleteBugForSprint'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-81', pluginId: 10).save(); // url: '/ptBug/listBugForSprint'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-75', pluginId: 10).save(); // url: '/ptBug/showBugForMyTask'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-76', pluginId: 10).save(); // url: '/ptBug/updateBugForMyTask'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-74', pluginId: 10).save(); // url: '/ptBug/downloadBugContent'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-14', pluginId: 10).save(); // url: '/ptSprint/show'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-15', pluginId: 10).save(); // url: '/ptSprint/create'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-16', pluginId: 10).save(); // url: '/ptSprint/update'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-17', pluginId: 10).save(); // url: '/ptSprint/delete'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-18', pluginId: 10).save(); // url: '/ptSprint/list'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-67', pluginId: 10).save(); // url: '/ptSprint/listSprintByProjectId'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-87', pluginId: 10).save(); // url: '/ptSprint/listInActiveSprintByProjectId'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-213', pluginId: 10).save(); // url: '/ptSprint/showProgress'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-61', pluginId: 10).save(); // url: '/ptReport/showReportOpenBacklog'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-62', pluginId: 10).save(); // url: '/ptReport/downloadOpenBacklogReport'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-63', pluginId: 10).save(); // url: '/ptReport/listReportOpenBacklog'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-58', pluginId: 10).save(); // url: '/ptReport/downloadSprintDetails'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-59', pluginId: 10).save(); // url: '/ptReport/showReportSprint'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-60', pluginId: 10).save(); // url: '/ptReport/listSprintDetails'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-64', pluginId: 10).save(); // url: '/ptReport/downloadBugDetails'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-65', pluginId: 10).save(); // url: '/ptReport/showReportBug'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-66', pluginId: 10).save(); // url: '/ptReport/listBugDetails'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-101', pluginId: 10).save(); // url: '/ptReport/downloadBacklogDetailsReport'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-103', pluginId: 10).save(); // url: '/ptReport/downloadPtBugDetails'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-154', pluginId: 10).save(); // url: '/ptReport/downloadPtChangeRequestReportForBacklog'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-109', pluginId: 10).save(); // url: '/ptFlow/show'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-110', pluginId: 10).save(); // url: '/ptFlow/list'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-111', pluginId: 10).save(); // url: '/ptFlow/select'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-112', pluginId: 10).save(); // url: '/ptFlow/create'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-113', pluginId: 10).save(); // url: '/ptFlow/update'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-114', pluginId: 10).save(); // url: '/ptFlow/delete'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-169', pluginId: 10).save(); // url: '/ptSteps/show'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-170', pluginId: 10).save(); // url: '/ptSteps/list'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-171', pluginId: 10).save(); // url: '/ptSteps/create'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-172', pluginId: 10).save(); // url: '/ptSteps/update'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-173', pluginId: 10).save(); // url: '/ptSteps/delete'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-118', pluginId: 10).save(); // url: '/ptBug/bugListForModule'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-119', pluginId: 10).save(); // url: '/ptReport/downloadBacklogDetailsUatReport'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-120', pluginId: 10).save(); // url: '/ptReport/showPtTaskReport'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-121', pluginId: 10).save(); // url: '/ptReport/listPtTaskReport'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-122', pluginId: 10).save(); // url: '/ptReport/downloadPtTaskReport'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-130', pluginId: 10).save(); // url: '/ptReport/downloadSprintDetailsUatReport'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-131', pluginId: 10).save(); // url: '/ptReport/downloadSprintDetailsUseCaseReport'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-140', pluginId: 10).save(); // url: '/ptChangeRequest/show'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-141', pluginId: 10).save(); // url: '/ptChangeRequest/create'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-142', pluginId: 10).save(); // url: '/ptChangeRequest/update'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-143', pluginId: 10).save(); // url: '/ptChangeRequest/delete'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-144', pluginId: 10).save(); // url: '/ptChangeRequest/list'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-146', pluginId: 10).save(); // url: '/ptReport/showPtChangeRequestReport'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-147', pluginId: 10).save(); // url: '/ptReport/showPtChangeRequestReport'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-148', pluginId: 10).save(); // url: '/ptReport/showPtChangeRequestReport'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-150', pluginId: 10).save(); // url: '/ptReport/backlogListForDynamicSearch'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-151', pluginId: 10).save(); // url: '/ptReport/refreshBacklogDetails'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-123', pluginId: 10).save(); // url: '/ptReport/downloadPtTaskUatReport'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-139', pluginId: 10).save(); // url: '/ptReport/downloadPtTaskFeatureList'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-167', pluginId: 10).save(); // url: '/ptReport/showForBugReport'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-168', pluginId: 10).save(); // url: '/ptReport/listForBugReport'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-176', pluginId: 10).save(); // url: '/ptReport/downloadBugReport'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-183', pluginId: 10).save(); // url: '/ptReport/downloadProgressReport'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'PT-212', pluginId: 10).save(); // url: '/ptReport/downloadEntityReport'

        // AppAttachment for admin
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'APP-144', pluginId: 10).save(); // url: '/appAttachment/show'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'APP-146', pluginId: 10).save(); // url: '/appAttachment/list'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'APP-147', pluginId: 10).save(); // url: '/appAttachment/update'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'APP-148', pluginId: 10).save(); // url: '/appAttachment/create'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'APP-149', pluginId: 10).save(); // url: '/appAttachment/delete'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'APP-421', pluginId: 10).save(); // url: '/appAttachment/upload'

        // drop down content category reload
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'APP-153', pluginId: 10).save(); // url: '/contentCategory/dropDownContentCategoryReload'

        // app note for admin
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'APP-245', pluginId: 10).save(); // url: '/appNote/show'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'APP-247', pluginId: 10).save(); // url: '/appNote/list'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'APP-248', pluginId: 10).save(); // url: '/appNote/update'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'APP-249', pluginId: 10).save(); // url: '/appNote/create'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'APP-250', pluginId: 10).save(); // url: '/appNote/delete'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'APP-261', pluginId: 10).save(); // url: '/appNote/viewEntityNote'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'APP-345', pluginId: 10).save(); // url: '/appNote/listEntityNote'
        //reload app note taglib
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'APP-252', pluginId: 10).save(); //url: '/appNote/reloadEntityNote'

        //Application feature for PT development user
        new RoleFeatureMapping(roleTypeId: -32, transactionCode: 'APP-99', pluginId: 10).save(); // url: '/systemConfiguration/show'
        new RoleFeatureMapping(roleTypeId: -32, transactionCode: 'APP-100', pluginId: 10).save(); // url: '/systemConfiguration/list'
        new RoleFeatureMapping(roleTypeId: -32, transactionCode: 'APP-101', pluginId: 10).save(); // url: '/systemConfiguration/select'
        new RoleFeatureMapping(roleTypeId: -32, transactionCode: 'APP-102', pluginId: 10).save(); // url: '/systemConfiguration/update'

        new RoleFeatureMapping(roleTypeId: -32, transactionCode: 'APP-103', pluginId: 10).save(); // url: '/systemEntity/show'
        new RoleFeatureMapping(roleTypeId: -32, transactionCode: 'APP-104', pluginId: 10).save(); // url: '/systemEntity/create'
        new RoleFeatureMapping(roleTypeId: -32, transactionCode: 'APP-105', pluginId: 10).save(); // url: '/systemEntity/list'
        new RoleFeatureMapping(roleTypeId: -32, transactionCode: 'APP-106', pluginId: 10).save(); // url: '/systemEntity/select'
        new RoleFeatureMapping(roleTypeId: -32, transactionCode: 'APP-107', pluginId: 10).save(); // url: '/systemEntity/update'
        new RoleFeatureMapping(roleTypeId: -32, transactionCode: 'APP-173', pluginId: 10).save(); // url: '/systemEntity/delete'

        new RoleFeatureMapping(roleTypeId: -32, transactionCode: 'APP-108', pluginId: 10).save(); // url: '/systemEntityType/show'
        new RoleFeatureMapping(roleTypeId: -32, transactionCode: 'APP-109', pluginId: 10).save(); // url: '/systemEntityType/list'

        new RoleFeatureMapping(roleTypeId: -32, transactionCode: 'APP-187', pluginId: 10).save(); // url: '/appMail/show'
        new RoleFeatureMapping(roleTypeId: -32, transactionCode: 'APP-188', pluginId: 10).save(); // url: '/appMail/update'
        new RoleFeatureMapping(roleTypeId: -32, transactionCode: 'APP-189', pluginId: 10).save(); // url: '/appMail/list'
        new RoleFeatureMapping(roleTypeId: -32, transactionCode: 'APP-190', pluginId: 10).save(); // url: '/appMail/select'
        new RoleFeatureMapping(roleTypeId: -32, transactionCode: 'APP-251', pluginId: 10).save(); // url: '/appMail/testAppMail'

        new RoleFeatureMapping(roleTypeId: -32, transactionCode: 'APP-180', pluginId: 10).save(); // url: '/appSms/showSms'
        new RoleFeatureMapping(roleTypeId: -32, transactionCode: 'APP-181', pluginId: 10).save(); // url: '/appSms/updateSms'
        new RoleFeatureMapping(roleTypeId: -32, transactionCode: 'APP-182', pluginId: 10).save(); // url: '/appSms/listSms'
        new RoleFeatureMapping(roleTypeId: -32, transactionCode: 'APP-237', pluginId: 10).save(); // url: '/appSms/sendSms'

        new RoleFeatureMapping(roleTypeId: -32, transactionCode: 'APP-279', pluginId: 10).save(); //url: '/appSchedule/show'
        new RoleFeatureMapping(roleTypeId: -32, transactionCode: 'APP-280', pluginId: 10).save(); //url: '/appSchedule/list'
        new RoleFeatureMapping(roleTypeId: -32, transactionCode: 'APP-282', pluginId: 10).save(); //url: '/appSchedule/update'
        new RoleFeatureMapping(roleTypeId: -32, transactionCode: 'APP-283', pluginId: 10).save(); //url: '/appSchedule/testExecute'

        // AppUserEntity(User Entity Mapping)
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'APP-238', pluginId: 10).save(); // url: '/appUserEntity/show'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'APP-239', pluginId: 10).save(); // url: '/appUserEntity/create'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'APP-240', pluginId: 10).save(); // url: '/appUserEntity/update'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'APP-241', pluginId: 10).save(); // url: '/appUserEntity/delete'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'APP-242', pluginId: 10).save(); // url: '/appUserEntity/list'
        new RoleFeatureMapping(roleTypeId: -31, transactionCode: 'APP-244', pluginId: 10).save(); // url: '/appUserEntity/dropDownAppUserEntityReload'

        //schema information
        new RoleFeatureMapping(roleTypeId: -32, transactionCode: 'APP-385', pluginId: 10).save();    // url: '/schemaInformation/listSchemaInfo'

        // test data
        new RoleFeatureMapping(roleTypeId: -32, transactionCode: 'APP-386', pluginId: 10).save();    // url: '/testData/list'

        // appTheme
        new RoleFeatureMapping(roleTypeId: -32, transactionCode: 'APP-124', pluginId: 10).save(); // url: '/appTheme/showTheme'
        new RoleFeatureMapping(roleTypeId: -32, transactionCode: 'APP-125', pluginId: 10).save(); // url: '/appTheme/updateTheme'
        new RoleFeatureMapping(roleTypeId: -32, transactionCode: 'APP-126', pluginId: 10).save(); // url: '/appTheme/listTheme'

        return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createRoleFeatureMapForArmsPlugin() {
        try {
            int count = appVersionService.countByPluginId(ArmsPluginConnector.PLUGIN_ID)
            if (count > 0) return true

            //renderArmsMenu for all user
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-1', pluginId: 11).save(); // url: '/arms/renderArmsMenu'
            new RoleFeatureMapping(roleTypeId: -402, transactionCode: 'RMS-1', pluginId: 11).save(); // url: '/arms/renderArmsMenu'
            new RoleFeatureMapping(roleTypeId: -403, transactionCode: 'RMS-1', pluginId: 11).save(); // url: '/arms/renderArmsMenu'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-1', pluginId: 11).save(); // url: '/arms/renderArmsMenu'
            new RoleFeatureMapping(roleTypeId: -406, transactionCode: 'RMS-1', pluginId: 11).save(); // url: '/arms/renderArmsMenu'

            //DropDown for all
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-88', pluginId: 11).save(); // url: '/arms/reloadExchangeHouseDropDown'
            new RoleFeatureMapping(roleTypeId: -402, transactionCode: 'RMS-88', pluginId: 11).save(); // url: '/arms/reloadExchangeHouseDropDown'
            new RoleFeatureMapping(roleTypeId: -403, transactionCode: 'RMS-88', pluginId: 11).save(); // url: '/arms/reloadExchangeHouseDropDown'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-88', pluginId: 11).save(); // url: '/arms/reloadExchangeHouseDropDown'

            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-89', pluginId: 11).save(); // url: '/arms/reloadExchangeHouseFilteredDropDown'
            new RoleFeatureMapping(roleTypeId: -402, transactionCode: 'RMS-89', pluginId: 11).save(); // url: '/arms/reloadExchangeHouseFilteredDropDown'
            new RoleFeatureMapping(roleTypeId: -403, transactionCode: 'RMS-89', pluginId: 11).save(); // url: '/arms/reloadExchangeHouseFilteredDropDown'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-89', pluginId: 11).save(); // url: '/arms/reloadExchangeHouseFilteredDropDown'

            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-90', pluginId: 11).save(); //url: '/rmsTaskList/reloadTaskListDropDown'
            new RoleFeatureMapping(roleTypeId: -402, transactionCode: 'RMS-90', pluginId: 11).save(); // url: '/rmsTaskList/reloadTaskListDropDown'
            new RoleFeatureMapping(roleTypeId: -403, transactionCode: 'RMS-90', pluginId: 11).save(); // url: '/rmsTaskList/reloadTaskListDropDown'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-90', pluginId: 11).save(); // url: '/rmsTaskList/reloadTaskListDropDown'

            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-122', pluginId: 11).save(); //url: '/rmsTaskList/reloadTaskListDropDown'
            new RoleFeatureMapping(roleTypeId: -402, transactionCode: 'RMS-122', pluginId: 11).save(); // url: '/rmsTaskList/reloadTaskListDropDown'
            new RoleFeatureMapping(roleTypeId: -403, transactionCode: 'RMS-122', pluginId: 11).save(); // url: '/rmsTaskList/reloadTaskListDropDown'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-122', pluginId: 11).save(); // url: '/rmsTaskList/reloadTaskListDropDown'

            //reload app note taglib
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'APP-252', pluginId: 11).save(); //url: '/appNote/reloadEntityNote'
            new RoleFeatureMapping(roleTypeId: -402, transactionCode: 'APP-252', pluginId: 11).save(); //url: '/appNote/reloadEntityNote'
            new RoleFeatureMapping(roleTypeId: -403, transactionCode: 'APP-252', pluginId: 11).save(); //url: '/appNote/reloadEntityNote'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'APP-252', pluginId: 11).save(); //url: '/appNote/reloadEntityNote'
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'APP-345', pluginId: 11).save(); //url: '/appNote/listEntityNote'
            new RoleFeatureMapping(roleTypeId: -402, transactionCode: 'APP-345', pluginId: 11).save(); //url: '/appNote/listEntityNote'
            new RoleFeatureMapping(roleTypeId: -403, transactionCode: 'APP-345', pluginId: 11).save(); //url: '/appNote/listEntityNote'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'APP-345', pluginId: 11).save(); //url: '/appNote/listEntityNote'

            // rms ExchangeHouse CRUD For Admin
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-2', pluginId: 11).save(); // url: '/rmsExchangeHouse/show'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-3', pluginId: 11).save(); // url: '/rmsExchangeHouse/create'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-4', pluginId: 11).save(); // url: '/rmsExchangeHouse/update'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-5', pluginId: 11).save(); // url: '/rmsExchangeHouse/delete'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-7', pluginId: 11).save(); // url: '/rmsExchangeHouse/list'

            // rms ExchangeHouseCurrencyPosting for admin
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-10', pluginId: 11).save(); // url: '/rmsExchangeHouseCurrencyPosting/show'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-11', pluginId: 11).save(); // url: '/rmsExchangeHouseCurrencyPosting/create'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-12', pluginId: 11).save(); // url: '/rmsExchangeHouseCurrencyPosting/update'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-13', pluginId: 11).save(); // url: '/rmsExchangeHouseCurrencyPosting/delete'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-15', pluginId: 11).save(); // url: '/rmsExchangeHouseCurrencyPosting/list'

            // rms Process Instrument Mapping for admin
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-16', pluginId: 11).save(); // url: '/rmsProcessInstrumentMapping/show'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-17', pluginId: 11).save(); // url: '/rmsProcessInstrumentMapping/create'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-18', pluginId: 11).save(); // url: '/rmsProcessInstrumentMapping/update'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-19', pluginId: 11).save(); // url: '/rmsProcessInstrumentMapping/delete'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-21', pluginId: 11).save(); // url: '/rmsProcessInstrumentMapping/list'

            // rmsTask CRUD For Remittance User
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-23', pluginId: 11).save(); // url: '/rmsTask/show'
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-24', pluginId: 11).save(); // url: '/rmsTask/create'
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-25', pluginId: 11).save(); // url: '/rmsTask/update'
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-26', pluginId: 11).save(); // url: '/rmsTask/delete'
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-28', pluginId: 11).save(); // url: '/rmsTask/list'

            // rmsTask CRUD For admin
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-23', pluginId: 11).save(); // url: '/rmsTask/show'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-25', pluginId: 11).save(); // url: '/rmsTask/update'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-26', pluginId: 11).save(); // url: '/rmsTask/delete'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-28', pluginId: 11).save(); // url: '/rmsTask/list'

            // rmsTask CRUD For exhHouse User
            new RoleFeatureMapping(roleTypeId: -403, transactionCode: 'RMS-84', pluginId: 11).save(); // url: '/rmsTask/showForExh'
            new RoleFeatureMapping(roleTypeId: -403, transactionCode: 'RMS-24', pluginId: 11).save(); // url: '/rmsTask/create'
            new RoleFeatureMapping(roleTypeId: -403, transactionCode: 'RMS-25', pluginId: 11).save(); // url: '/rmsTask/update'
            new RoleFeatureMapping(roleTypeId: -403, transactionCode: 'RMS-26', pluginId: 11).save(); // url: '/rmsTask/delete'
            new RoleFeatureMapping(roleTypeId: -403, transactionCode: 'RMS-28', pluginId: 11).save(); // url: '/rmsTask/list'
            new RoleFeatureMapping(roleTypeId: -403, transactionCode: 'RMS-86', pluginId: 11).save(); // url: '/rmsTask/sendRmsTaskToBank'

            //rmsTask upload for remittance user
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-29', pluginId: 11).save(); // url: '/rmsTask/showForUploadTask'
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-30', pluginId: 11).save(); // url: '/rmsTask/createForUploadTask'

            //rmsTask upload for exhHouse user
            new RoleFeatureMapping(roleTypeId: -403, transactionCode: 'RMS-85', pluginId: 11).save(); // url: '/rmsTask/showForUploadTaskForExh'
            new RoleFeatureMapping(roleTypeId: -403, transactionCode: 'RMS-30', pluginId: 11).save(); // url: '/rmsTask/createForUploadTask'

            //rmsTask list for remittance user
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-31', pluginId: 11).save(); // url: '/rmsTask/listTaskForTaskList'
            //rmsTask list Map for remittance user
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-32', pluginId: 11).save(); // url: '/rmsTask/showForMapTask'
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-33', pluginId: 11).save(); // url: '/rmsTask/listTaskForMap'
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-34', pluginId: 11).save(); // url: '/rmsTask/mapTask'

            //rms task approve for admin
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-35', pluginId: 11).save(); // url: '/rmsTask/showForApproveTask'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-36', pluginId: 11).save(); // url: '/rmsTask/listTaskForApprove'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-37', pluginId: 11).save(); // url: '/rmsTask/approve'

            //rms reviseTask for admin
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-38', pluginId: 11).save(); // url: '/rmsTask/reviseTask'

            //rms reviseTask for remittance user
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-38', pluginId: 11).save(); // url: '/rmsTask/reviseTask'
            //rms reviseTask for branch user
            new RoleFeatureMapping(roleTypeId: -402, transactionCode: 'RMS-38', pluginId: 11).save(); // url: '/rmsTask/reviseTask'

            //rmsTask details with note for admin
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-44', pluginId: 11).save(); // url: '/rmsTask/showTaskDetailsWithNote'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-45', pluginId: 11).save(); // url: '/rmsTask/searchTaskDetailsWithNote'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-64', pluginId: 11).save(); // url: '/rmsTask/createRmsTaskNote'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-78', pluginId: 11).save(); //url: '/rmsTask/renderTaskDetails'

            //rmsTask details with note for remittance user
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-44', pluginId: 11).save(); // url: '/rmsTask/showTaskDetailsWithNote'
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-45', pluginId: 11).save(); // url: '/rmsTask/searchTaskDetailsWithNote'
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-64', pluginId: 11).save(); // url: '/rmsTask/createRmsTaskNote'
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-78', pluginId: 11).save(); //url: '/rmsTask/renderTaskDetails'

            //rmsTask details with note for branch user
            new RoleFeatureMapping(roleTypeId: -402, transactionCode: 'RMS-44', pluginId: 11).save(); // url: '/rmsTask/showTaskDetailsWithNote'
            new RoleFeatureMapping(roleTypeId: -402, transactionCode: 'RMS-45', pluginId: 11).save(); // url: '/rmsTask/searchTaskDetailsWithNote'
            new RoleFeatureMapping(roleTypeId: -402, transactionCode: 'RMS-64', pluginId: 11).save(); // url: '/rmsTask/createRmsTaskNote'
            new RoleFeatureMapping(roleTypeId: -402, transactionCode: 'RMS-78', pluginId: 11).save(); //url: '/rmsTask/renderTaskDetails'

            //rmsTask details with note for exhHouse user
            new RoleFeatureMapping(roleTypeId: -403, transactionCode: 'RMS-44', pluginId: 11).save(); // url: '/rmsTask/showTaskDetailsWithNote'
            new RoleFeatureMapping(roleTypeId: -403, transactionCode: 'RMS-45', pluginId: 11).save(); // url: '/rmsTask/searchTaskDetailsWithNote'
            new RoleFeatureMapping(roleTypeId: -403, transactionCode: 'RMS-64', pluginId: 11).save(); // url: '/rmsTask/createRmsTaskNote'
            new RoleFeatureMapping(roleTypeId: -403, transactionCode: 'RMS-78', pluginId: 11).save(); //url: '/rmsTask/renderTaskDetails'

            //disburse rmsTask for remittance user
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-79', pluginId: 11).save(); //url: '/rmsTask/disburseRmsTask'
            //disburse rmsTask for branchUser
            new RoleFeatureMapping(roleTypeId: -402, transactionCode: 'RMS-79', pluginId: 11).save(); //url: '/rmsTask/disburseRmsTask'

            //get district For remittance user, admin, branch user, other bank branch user
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'APP-236', pluginId: 11).save(); //url: '/district/reloadDistrictDropDown'
            new RoleFeatureMapping(roleTypeId: -402, transactionCode: 'APP-236', pluginId: 11).save(); //url: '/district/reloadDistrictDropDown'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'APP-236', pluginId: 11).save(); //url: '/district/reloadDistrictDropDown'

            //get branch For remittance user
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'APP-209', pluginId: 11).save(); //url: '/appBankBranch/reloadBranchesDropDownByBankAndDistrict'
            //get branch For branch user
            new RoleFeatureMapping(roleTypeId: -402, transactionCode: 'APP-209', pluginId: 11).save(); //url: '/appBankBranch/reloadBranchesDropDownByBankAndDistrict'
            // get branch For arms admin
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'APP-209', pluginId: 11).save(); //url: '/appBankBranch/reloadBranchesDropDownByBankAndDistrict'

            //get bank For remittance user
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'APP-260', pluginId: 11).save(); //url: '/appBank/reloadBankDropDownTagLib'
            //get bank For branch user
            new RoleFeatureMapping(roleTypeId: -402, transactionCode: 'APP-260', pluginId: 11).save(); //url: '/appBank/reloadBankDropDownTagLib'
            //get bank For arms admin
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'APP-260', pluginId: 11).save(); //url: '/appBank/reloadBankDropDownTagLib'

            //forward task for branch user
            new RoleFeatureMapping(roleTypeId: -402, transactionCode: 'RMS-71', pluginId: 11).save(); // url: '/rmsTask/showTaskDetailsForForward'
            new RoleFeatureMapping(roleTypeId: -402, transactionCode: 'RMS-72', pluginId: 11).save(); // url: '/rmsTask/searchTaskDetailsForForward'
            new RoleFeatureMapping(roleTypeId: -402, transactionCode: 'RMS-73', pluginId: 11).save(); // url: '/rmsTask/forwardRmsTask'

            // rmsTaskList for remittance user
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-39', pluginId: 11).save(); // url: '/rmsTaskList/show'
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-40', pluginId: 11).save(); // url: '/rmsTaskList/create'

            //searchTaskList for remittance user
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-42', pluginId: 11).save(); // url: '/rmsTaskList/showSearchTaskList'
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-43', pluginId: 11).save(); // url: '/rmsTaskList/listSearchTaskList'

            //searchTaskList for admin
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-42', pluginId: 11).save(); // url: '/rmsTaskList/showSearchTaskList'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-43', pluginId: 11).save(); // url: '/rmsTaskList/listSearchTaskList'

            // rmsInstrument for remittance user
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-46', pluginId: 11).save(); // url: '/rmsInstrument/listTaskForProcessInstrument'
            // rmsInstrument for branch user
            new RoleFeatureMapping(roleTypeId: -402, transactionCode: 'RMS-46', pluginId: 11).save(); // url: '/rmsInstrument/listTaskForProcessInstrument'

            //rmsInstrument issue for remittance user
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-47', pluginId: 11).save(); // url: '/rmsInstrument/showForIssuePo'
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-48', pluginId: 11).save(); // url: '/rmsInstrument/downloadTaskReportForIssuePo'
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-49', pluginId: 11).save(); // url: '/rmsInstrument/showForIssueEft'
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-50', pluginId: 11).save(); // url: '/rmsInstrument/downloadTaskReportForIssueEft'
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-51', pluginId: 11).save(); // url: '/rmsInstrument/showForIssueOnline'
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-52', pluginId: 11).save(); // url: '/rmsInstrument/downloadTaskReportForIssueOnline'

            //rmsInstrument issue for branch user
            new RoleFeatureMapping(roleTypeId: -402, transactionCode: 'RMS-55', pluginId: 11).save(); // url: '/rmsInstrument/showForForwardCashCollection'
            new RoleFeatureMapping(roleTypeId: -402, transactionCode: 'RMS-56', pluginId: 11).save(); // url: '/rmsInstrument/downloadTaskReportForForwardCashCollection'
            new RoleFeatureMapping(roleTypeId: -402, transactionCode: 'RMS-57', pluginId: 11).save(); // url: '/rmsInstrument/showForForwardOnline'
            new RoleFeatureMapping(roleTypeId: -402, transactionCode: 'RMS-58', pluginId: 11).save(); // url: '/rmsInstrument/downloadTaskReportForForwardOnline'

            //rms instrument dropDown for admin
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-120', pluginId: 11).save(); // url: '/rmsInstrument/reloadInstrumentDropDown'
            //rms instrument dropDown for remittance user
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-121', pluginId: 11).save(); // url: '/rmsInstrument/reloadBankListFilteredDropDown'
            //rms instrument dropDown for remittance user
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-120', pluginId: 11).save(); // url: '/rmsInstrument/reloadInstrumentDropDown'

            //purchase instrument for remittance user
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-118', pluginId: 11).save(); // url: '/rmsInstrument/showForInstrumentPurchase'
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-119', pluginId: 11).save(); // url: '/rmsInstrument/downloadTaskReportForPurchaseInstrument'

            // rmsReport for remittance user
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-59', pluginId: 11).save(); // url: '/rmsReport/showForListWiseStatusReport'
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-60', pluginId: 11).save(); // url: '/rmsReport/listForListWiseStatusReport'
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-61', pluginId: 11).save(); // url: '/rmsReport/downloadListWiseStatusReport'

            // rmsReport for admin
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-59', pluginId: 11).save(); // url: '/rmsReport/showForListWiseStatusReport'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-60', pluginId: 11).save(); // url: '/rmsReport/listForListWiseStatusReport'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-61', pluginId: 11).save(); // url: '/rmsReport/downloadListWiseStatusReport'

            //search beneficiary for remittance user
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-62', pluginId: 11).save(); // url: '/rmsReport/showBeneficiaryDetails'
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-63', pluginId: 11).save(); // url: '/rmsReport/searchBeneficiaryDetails'
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-83', pluginId: 11).save(); // url: '/rmsReport/searchBeneficiaryForGrid'

            //search beneficiary for admin
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-62', pluginId: 11).save(); // url: '/rmsReport/showBeneficiaryDetails'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-63', pluginId: 11).save(); // url: '/rmsReport/searchBeneficiaryDetails'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-83', pluginId: 11).save(); // url: '/rmsReport/searchBeneficiaryForGrid'

            //search task list plan for remittance user
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-65', pluginId: 11).save(); // url: '/rmsReport/showTaskListPlan'
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-66', pluginId: 11).save(); // url: '/rmsReport/searchTaskListPlan'

            //search task list plan for admin
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-65', pluginId: 11).save(); // url: '/rmsReport/showTaskListPlan'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-66', pluginId: 11).save(); // url: '/rmsReport/searchTaskListPlan'

            //forwarded unpaid task for remittance user
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-74', pluginId: 11).save(); //url: '/rmsTask/showForForwardUnpaidTask'
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-75', pluginId: 11).save(); //url: '/rmsTask/listTaskForForwardUnpaidTask'
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-126', pluginId: 11).save(); //url: '/rmsReport/listTaskDetailsForForwardedUnpaidTasks'

            //forwarded unpaid task for admin
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-74', pluginId: 11).save(); //url: '/rmsTask/showForForwardUnpaidTask'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-75', pluginId: 11).save(); //url: '/rmsTask/listTaskForForwardUnpaidTask'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-126', pluginId: 11).save(); //url: '/rmsReport/listTaskDetailsForForwardedUnpaidTasks'

            //taskTrace for admin
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-76', pluginId: 11).save(); //url: '/rmsTaskTrace/showRmsTaskHistory'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-77', pluginId: 11).save(); //url: '/rmsTaskTrace/searchRmsTaskHistory'
            //taskTrace for remittance user
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-76', pluginId: 11).save(); //url: '/rmsTaskTrace/showRmsTaskHistory'
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-77', pluginId: 11).save(); //url: '/rmsTaskTrace/searchRmsTaskHistory'

            //disburse cash collection for branch user
            new RoleFeatureMapping(roleTypeId: -402, transactionCode: 'RMS-80', pluginId: 11).save(); //url: '/rmsTask/showDisburseCashCollection'
            new RoleFeatureMapping(roleTypeId: -402, transactionCode: 'RMS-81', pluginId: 11).save(); //url: '/rmsTask/searchDisburseCashCollection'
            new RoleFeatureMapping(roleTypeId: -402, transactionCode: 'RMS-82', pluginId: 11).save(); //url: '/rmsTask/disburseCashCollectionRmsTask'

            //manage task for admin
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-91', pluginId: 11).save(); //url: '/rmsTask/showForManageTask'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-148', pluginId: 11).save(); //url: '/rmsTask/searchForManageTask'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-106', pluginId: 11).save(); //url: '/rmsTask/cancelRmsTask'
            //cancel task for remittance user
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-106', pluginId: 11).save(); //url: '/rmsTask/cancelRmsTask'
            //revise task
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-146', pluginId: 11).save(); //url: '/rmsTask/reviseTaskFromManageTask'

            //manage task list for admin
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-93', pluginId: 11).save(); //url: '/rmsTaskList/showForManageTaskList'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-94', pluginId: 11).save(); //url: '/rmsTaskList/listForManageTaskList'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-105', pluginId: 11).save(); //url: '/rmsTasklist/removeFromList'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-108', pluginId: 11).save(); //url: '/rmsTaskList/renameTaskList'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-109', pluginId: 11).save(); //url: '/rmsTaskList/moveTaskToAnotherList'

            //RmsTransactionDay for admin
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-95', pluginId: 11).save(); //url: '/rmsTransactionDay/show'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-96', pluginId: 11).save(); //url: '/rmsTransactionDay/list'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-97', pluginId: 11).save(); //url: '/rmsTransactionDay/openTransactionDay'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-98', pluginId: 11).save(); //url: '/rmsTransactionDay/closeTransactionDay'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-99', pluginId: 11).save(); //url: '/rmsTransactionDay/reOpenTransactionDay'

            //RmsTaskListSummaryModel for admin
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-107', pluginId: 11).save(); //url: '/rmsTaskListSummaryModel/listUnResolvedTaskList'

            ////RmsPurchaseInstrumentMapping for admin
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-110', pluginId: 11).save(); // url: '/rmsPurchaseInstrumentMapping/show'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-111', pluginId: 11).save(); // url: '/rmsPurchaseInstrumentMapping/create'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-112', pluginId: 11).save(); // url: '/rmsPurchaseInstrumentMapping/update'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-113', pluginId: 11).save(); // url: '/rmsPurchaseInstrumentMapping/delete'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-115', pluginId: 11).save(); // url: '/rmsPurchaseInstrumentMapping/list'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-125', pluginId: 11).save(); // url: '/rmsPurchaseInstrumentMapping/evaluateLogic'

            //rmsViewNotes for admin
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-116', pluginId: 11).save(); // url: '/rmsTask/showForViewNotes'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-117', pluginId: 11).save(); // url: '/rmsTask/listForViewNotes'
            //view cancelled tasks
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-123', pluginId: 11).save(); // url: '/rmsReport/showForViewCancelTask'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-124', pluginId: 11).save(); // url: '/rmsReport/listForViewCancelTask'

            //decisionSummary for admin
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-127', pluginId: 11).save(); // url: '/rmsReport/showDecisionSummary'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-128', pluginId: 11).save(); // url: '/rmsReport/listDecisionSummary'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-129', pluginId: 11).save(); // url: '/rmsReport/downloadDecisionSummaryReport'

            //decisionSummary for remittance user
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-127', pluginId: 11).save(); // url: '/rmsReport/showDecisionSummary'
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-128', pluginId: 11).save(); // url: '/rmsReport/listDecisionSummary'
            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-129', pluginId: 11).save(); // url: '/rmsReport/downloadDecisionSummaryReport'

            //branchWiseTransaction for admin
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-130', pluginId: 11).save(); // url: '/rmsReport/showBranchWiseTransaction'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-131', pluginId: 11).save(); // url: '/rmsReport/listBranchWiseTransaction'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-132', pluginId: 11).save(); // url: '/rmsReport/downloadBranchWiseTransaction'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-138', pluginId: 11).save(); // url: '/rmsReport/downloadBranchWiseRemittance'

            //exchange house transaction report
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-133', pluginId: 11).save(); // url: '/rmsReport/showRmsExhWiseTransactionReport'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-134', pluginId: 11).save(); // url: '/rmsReport/listRmsExhWiseTransactionReport'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-135', pluginId: 11).save(); // url: '/rmsReport/downloadRmsExhWiseTransactionReport'

            //search transaction report
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-139', pluginId: 11).save(); // url: '/rmsReport/showSearchTransactionReport'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-140', pluginId: 11).save(); // url: '/rmsReport/listSearchTransactionReport'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-141', pluginId: 11).save(); // url: '/rmsReport/downloadSearchTransactionReport'

            //download per exh wise transaction report
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-142', pluginId: 11).save(); // url: '/rmsReport/downloadPerExhWiseRemittance'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-147', pluginId: 11).save(); // url: '/rmsReport/downloadTaskListPlan'

            new RoleFeatureMapping(roleTypeId: -403, transactionCode: 'RMS-149', pluginId: 11).save(); // url: '/rmsReport/showTransactionStatusReport'
            new RoleFeatureMapping(roleTypeId: -403, transactionCode: 'RMS-150', pluginId: 11).save(); // url: '/rmsReport/listTransactionStatusReport'
            new RoleFeatureMapping(roleTypeId: -403, transactionCode: 'RMS-151', pluginId: 11).save(); // url: '/rmsReport/downloadTransactionStatusReport'

            //app feature for arms admin
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'APP-238', pluginId: 11).save(); // url: '/appUserEntity/show'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'APP-239', pluginId: 11).save(); // url: '/appUserEntity/create'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'APP-240', pluginId: 11).save(); // url: '/appUserEntity/update'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'APP-241', pluginId: 11).save(); // url: '/appUserEntity/delete'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'APP-242', pluginId: 11).save(); // url: '/appUserEntity/list'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'APP-244', pluginId: 11).save(); // url: '/appUserEntity/dropDownAppUserEntityReload'

            //Application feature for arms development user
            new RoleFeatureMapping(roleTypeId: -406, transactionCode: 'APP-99', pluginId: 11).save(); // url: '/systemConfiguration/show'
            new RoleFeatureMapping(roleTypeId: -406, transactionCode: 'APP-100', pluginId: 11).save(); // url: '/systemConfiguration/list'
            new RoleFeatureMapping(roleTypeId: -406, transactionCode: 'APP-102', pluginId: 11).save(); // url: '/systemConfiguration/update'

            new RoleFeatureMapping(roleTypeId: -406, transactionCode: 'APP-103', pluginId: 11).save(); // url: '/systemEntity/show'
            new RoleFeatureMapping(roleTypeId: -406, transactionCode: 'APP-104', pluginId: 11).save(); // url: '/systemEntity/create'
            new RoleFeatureMapping(roleTypeId: -406, transactionCode: 'APP-105', pluginId: 11).save(); // url: '/systemEntity/list'
            new RoleFeatureMapping(roleTypeId: -406, transactionCode: 'APP-107', pluginId: 11).save(); // url: '/systemEntity/update'
            new RoleFeatureMapping(roleTypeId: -406, transactionCode: 'APP-173', pluginId: 11).save(); // url: '/systemEntity/delete'

            new RoleFeatureMapping(roleTypeId: -406, transactionCode: 'APP-108', pluginId: 11).save(); // url: '/systemEntityType/show'
            new RoleFeatureMapping(roleTypeId: -406, transactionCode: 'APP-109', pluginId: 11).save(); // url: '/systemEntityType/list'

            new RoleFeatureMapping(roleTypeId: -406, transactionCode: 'APP-187', pluginId: 11).save(); // url: '/appMail/show'
            new RoleFeatureMapping(roleTypeId: -406, transactionCode: 'APP-188', pluginId: 11).save(); // url: '/appMail/update'
            new RoleFeatureMapping(roleTypeId: -406, transactionCode: 'APP-189', pluginId: 11).save(); // url: '/appMail/list'
            new RoleFeatureMapping(roleTypeId: -406, transactionCode: 'APP-251', pluginId: 11).save(); // url: '/appMail/testAppMail'

            new RoleFeatureMapping(roleTypeId: -406, transactionCode: 'APP-180', pluginId: 11).save(); // url: '/appSms/showSms'
            new RoleFeatureMapping(roleTypeId: -406, transactionCode: 'APP-181', pluginId: 11).save(); // url: '/appSms/updateSms'
            new RoleFeatureMapping(roleTypeId: -406, transactionCode: 'APP-182', pluginId: 11).save(); // url: '/appSms/listSms'
            new RoleFeatureMapping(roleTypeId: -406, transactionCode: 'APP-237', pluginId: 11).save(); // url: '/appSms/sendSms'

            new RoleFeatureMapping(roleTypeId: -406, transactionCode: 'APP-279', pluginId: 11).save(); //url: '/appSchedule/show'
            new RoleFeatureMapping(roleTypeId: -406, transactionCode: 'APP-280', pluginId: 11).save(); //url: '/appSchedule/list'
            new RoleFeatureMapping(roleTypeId: -406, transactionCode: 'APP-282', pluginId: 11).save(); //url: '/appSchedule/update'
            new RoleFeatureMapping(roleTypeId: -406, transactionCode: 'APP-283', pluginId: 11).save(); //url: '/appSchedule/testExecute'

            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-143', pluginId: 11).save(); //url: '/rmsReport/showDistrictWiseTransactionReport'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-144', pluginId: 11).save(); //url: '/rmsReport/listDistrictWiseTransactionReport'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-145', pluginId: 11).save(); //url: '/rmsReport/downloadDistrictWiseTransactionReport'
            new RoleFeatureMapping(roleTypeId: -406, transactionCode: 'APP-385', pluginId: 11).save(); // url: '/schemaInformation/listSchemaInfo'

            new RoleFeatureMapping(roleTypeId: -406, transactionCode: 'APP-386', pluginId: 11).save(); // url: '/testData/list'

            // appTheme
            new RoleFeatureMapping(roleTypeId: -406, transactionCode: 'APP-124', pluginId: 11).save(); // url: '/appTheme/showTheme'
            new RoleFeatureMapping(roleTypeId: -406, transactionCode: 'APP-125', pluginId: 11).save(); // url: '/appTheme/updateTheme'
            new RoleFeatureMapping(roleTypeId: -406, transactionCode: 'APP-126', pluginId: 11).save(); // url: '/appTheme/listTheme'

            // sanction
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-156', pluginId: 11).save(); // url: '/rmsSanctionHmTreasury/showSanctionUpload'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-157', pluginId: 11).save(); // url: '/rmsSanctionHmTreasury/uploadSanctionFile'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-158', pluginId: 11).save(); // url: '/rmsSanctionOfacSdn/showSanctionUpload'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-159', pluginId: 11).save(); // url: '/rmsSanctionOfacSdn/uploadSanctionFile'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-160', pluginId: 11).save(); // url: '/rmsSanctionOfacAdd/showSanctionUpload'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-161', pluginId: 11).save(); // url: '/rmsSanctionOfacAdd/uploadSanctionFile'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-162', pluginId: 11).save(); // url: '/rmsSanctionOfacAlt/showSanctionUpload'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-163', pluginId: 11).save(); // url: '/rmsSanctionOfacAlt/uploadSanctionFile'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-164', pluginId: 11).save(); // url: '/rmsSanctionModel/show'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-165', pluginId: 11).save(); // url: '/rmsSanctionModel/list'

            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-166', pluginId: 11).save(); // url: '/rmsSanctionModel/showMatchedSanction'
            new RoleFeatureMapping(roleTypeId: -402, transactionCode: 'RMS-166', pluginId: 11).save(); // url: '/rmsSanctionModel/showMatchedSanction'
            new RoleFeatureMapping(roleTypeId: -403, transactionCode: 'RMS-166', pluginId: 11).save(); // url: '/rmsSanctionModel/showMatchedSanction'
            new RoleFeatureMapping(roleTypeId: -404, transactionCode: 'RMS-166', pluginId: 11).save(); // url: '/rmsSanctionModel/showMatchedSanction'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-166', pluginId: 11).save(); // url: '/rmsSanctionModel/showMatchedSanction'
            new RoleFeatureMapping(roleTypeId: -406, transactionCode: 'RMS-166', pluginId: 11).save(); // url: '/rmsSanctionModel/showMatchedSanction'

            new RoleFeatureMapping(roleTypeId: -401, transactionCode: 'RMS-167', pluginId: 11).save(); // url: '/rmsSanctionModel/listMatchedSanction'
            new RoleFeatureMapping(roleTypeId: -402, transactionCode: 'RMS-167', pluginId: 11).save(); // url: '/rmsSanctionModel/listMatchedSanction'
            new RoleFeatureMapping(roleTypeId: -403, transactionCode: 'RMS-167', pluginId: 11).save(); // url: '/rmsSanctionModel/listMatchedSanction'
            new RoleFeatureMapping(roleTypeId: -404, transactionCode: 'RMS-167', pluginId: 11).save(); // url: '/rmsSanctionModel/listMatchedSanction'
            new RoleFeatureMapping(roleTypeId: -405, transactionCode: 'RMS-167', pluginId: 11).save(); // url: '/rmsSanctionModel/listMatchedSanction'
            new RoleFeatureMapping(roleTypeId: -406, transactionCode: 'RMS-167', pluginId: 11).save(); // url: '/rmsSanctionModel/listMatchedSanction'

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createRoleFeatureMapForSARBPlugin() {
        try {
            int count = appVersionService.countByPluginId(SarbPluginConnector.PLUGIN_ID)
            if (count > 0) return true

            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-1', pluginId: 12).save(); // url: '/sarb/renderSarbMenu'
            new RoleFeatureMapping(roleTypeId: -302, transactionCode: 'SARB-1', pluginId: 12).save(); // url: '/sarb/renderSarbMenu'

            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'EXH-258', pluginId: 12).save(); // url: '/exhTask/showDetailsForReplaceTask'
            new RoleFeatureMapping(roleTypeId: -302, transactionCode: 'EXH-258', pluginId: 12).save(); // url: '/exhTask/showDetailsForReplaceTask'

            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-2', pluginId: 12).save(); // url: '/sarbProvince/show'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-3', pluginId: 12).save(); // url: '/sarbProvince/create'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-5', pluginId: 12).save(); // url: '/sarbProvince/update'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-6', pluginId: 12).save(); // url: '/sarbProvince/delete'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-7', pluginId: 12).save(); // url: '/sarbProvince/list'

            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-8', pluginId: 12).save(); // url: '/sarbTaskModel/showForSendTaskToSarb'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-9', pluginId: 12).save(); // url: '/sarbTaskModel/listForSendTaskToSarb'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-13', pluginId: 12).save(); // url: '/sarbTaskModel/sendTaskToSarb'

            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-10', pluginId: 12).save(); // url: '/sarbTaskModel/showTaskStatus'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-11', pluginId: 12).save(); // url: '/sarbTaskModel/listTaskStatus'

            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-15', pluginId: 12).save(); // url: '/sarbTaskModel/showTaskForRetrieveResponse'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-16', pluginId: 12).save(); //url: '/sarbTaskModel/sendToRetrieveResponse'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-12', pluginId: 12).save(); // url: '/sarbTaskModel/listSarbTaskForRetrieveResponse'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-17', pluginId: 12).save(); // url: '/sarbTaskModel/retrieveResponseAgain'

            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-18', pluginId: 12).save(); //  url: '/sarbTaskModel/moveForResend'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-19', pluginId: 12).save(); //  url: '/sarbTaskModel/moveForCancel'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-34', pluginId: 12).save(); //  url: '/sarbTaskModel/moveForReplace'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-20', pluginId: 12).save(); //  url: '/sarbTaskModel/sendCancelTaskToSarb'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-36', pluginId: 12).save(); //  url: '/sarbTaskModel/sendReplaceTaskToSarb'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-37', pluginId: 12).save(); //  url: '/sarbTaskModel/sendRefundTaskToSarb'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-47', pluginId: 12).save(); //  url: '/sarbTaskModel/deleteRefundTask'

            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-21', pluginId: 12).save(); //  url: '/sarbReport/showTransactionSummary'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-22', pluginId: 12).save(); //  url: '/sarbReport/listTransactionSummary'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-23', pluginId: 12).save(); //  url: '/sarbReport/downloadTransactionSummary'

            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-24', pluginId: 12).save(); //  url: '/sarbTaskModel/listForCancelTask'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-25', pluginId: 12).save(); // url: '/sarbTaskModel/showTaskForCancel'

            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-26', pluginId: 12).save(); //  url: '/sarbTaskModel/listForReplaceTask'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-27', pluginId: 12).save(); // url: '/sarbTaskModel/showForReplaceTask'

            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-28', pluginId: 12).save(); //  url: '/sarbTaskModel/listForRefundTask'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-29', pluginId: 12).save(); // url: '/sarbTaskModel/showForRefundTask'

            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-32', pluginId: 12).save(); // url: '/sarbTaskModel/updateTaskForReplaceTask'

            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-30', pluginId: 12).save(); //  url: '/sarbTaskModel/showDetailsForRefundTask'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-31', pluginId: 12).save(); //  url: '/sarbTaskModel/createSarbTaskForRefundTask'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-33', pluginId: 12).save(); //  url: '/sarbTaskModel/listRefundTaskForShowStatus'

            //transaction details
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-38', pluginId: 12).save(); //  url: '/sarbTaskModel/showTransactionDetails'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-39', pluginId: 12).save(); //  url: '/sarbTaskModel/listTransactionDetails'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-40', pluginId: 12).save(); //  url: '/sarbTaskModel/downloadTransactionDetails'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-76', pluginId: 12).save(); //  url: '/sarbReport/downloadTransactionDetailsCsv'


            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-43', pluginId: 12).save(); //  url: '/sarbReport/downloadReplacedReport'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-46', pluginId: 12).save(); //  url: '/sarbReport/downloadRefundedReport'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-72', pluginId: 12).save(); //  url: '/sarbReport/downloadUnsubmittedReport'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-77', pluginId: 12).save(); //  url: '/sarbReport/downloadUnsubmittedCsvReport'

            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-78', pluginId: 12).save(); //  url: '/sarbReport/downloadAllReportableTransactions'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-79', pluginId: 12).save(); //  url: '/sarbReport/downloadAllReportableTransactionsCsv'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-80', pluginId: 12).save(); //  url: '/sarbReport/showUnsubmittedTransaction'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-81', pluginId: 12).save(); //  url: '/sarbReport/listUnsubmittedTransaction'

            //SarbCurrencyConversion
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-48', pluginId: 12).save(); //  url: '/sarbCurrencyConversion/show'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-65', pluginId: 12).save(); //  url: '/sarbCurrencyConversion/create'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-50', pluginId: 12).save(); //  url: '/sarbCurrencyConversion/list'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-51', pluginId: 12).save(); //  url: '/sarbCurrencyConversion/update'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-52', pluginId: 12).save(); //  url: '/sarbCurrencyConversion/delete'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-53', pluginId: 12).save(); //  url: '/sarbCurrencyConversion/sendToSarb'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-54', pluginId: 12).save(); //  url: '/sarbCurrencyConversion/showForRetrieveResponse'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-55', pluginId: 12).save(); //  url: '/sarbCurrencyConversion/listForRetrieveResponse'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-56', pluginId: 12).save(); //  url: '/sarbCurrencyConversion/retrieveResponse'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-57', pluginId: 12).save(); //  url: '/sarbCurrencyConversion/retrieveResponseAgain'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-58', pluginId: 12).save(); //  url: '/sarbCurrencyConversion/moveForResend'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-59', pluginId: 12).save(); //  url: '/sarbCurrencyConversion/showForShowStatus'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-60', pluginId: 12).save(); //  url: '/sarbCurrencyConversion/listForShowStatus'

            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-62', pluginId: 12).save(); //  url: '/sarbReport/showForNonReportableReport'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-63', pluginId: 12).save(); //  url: '/sarbReport/listForNonReportableReport'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-64', pluginId: 12).save(); //  url: '/sarbReport/downloadNonReportableReport'
            //warning task details report
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-69', pluginId: 12).save(); //  url: '/sarbReport/showWarningTaskDetails'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-70', pluginId: 12).save(); //  url: '/sarbReport/listWarningTaskDetails'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-71', pluginId: 12).save(); //  url: '/sarbReport/downloadWarningTaskDetails'

            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-73', pluginId: 12).save(); //  url: '/sarbReport/showTransactionBalance'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-74', pluginId: 12).save(); //  url: '/sarbReport/listTransactionBalance'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-75', pluginId: 12).save(); //  url: '/sarbReport/downloadTransactionBalance'
            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'SARB-82', pluginId: 12).save(); //  url: '/sarbReport/downloadTransactionBalanceCsv'

            //For Sarb development user
            new RoleFeatureMapping(roleTypeId: -302, transactionCode: 'SARB-84', pluginId: 12).save(); //  url: '/sarbTaskModel/moveForResendToSarb'
            new RoleFeatureMapping(roleTypeId: -302, transactionCode: 'SARB-83', pluginId: 12).save(); //  url: '/sarbCurrencyConversion/moveForResendToSarb'
            new RoleFeatureMapping(roleTypeId: -302, transactionCode: 'SARB-66', pluginId: 12).save(); //  url: '/sarbReport/showReportingDetails'
            new RoleFeatureMapping(roleTypeId: -302, transactionCode: 'SARB-67', pluginId: 12).save(); //  url: '/sarbReport/listReportingDetails'
            new RoleFeatureMapping(roleTypeId: -302, transactionCode: 'SARB-85', pluginId: 12).save(); //  url: '/sarbReport/deleteSarbDetails'

            new RoleFeatureMapping(roleTypeId: -302, transactionCode: 'APP-99', pluginId: 12).save(); // url: '/systemConfiguration/show'
            new RoleFeatureMapping(roleTypeId: -302, transactionCode: 'APP-100', pluginId: 12).save(); // url: '/systemConfiguration/list'
            new RoleFeatureMapping(roleTypeId: -302, transactionCode: 'APP-102', pluginId: 12).save(); // url: '/systemConfiguration/update'

            new RoleFeatureMapping(roleTypeId: -302, transactionCode: 'APP-103', pluginId: 12).save(); // url: '/systemEntity/show'
            new RoleFeatureMapping(roleTypeId: -302, transactionCode: 'APP-104', pluginId: 12).save(); // url: '/systemEntity/create'
            new RoleFeatureMapping(roleTypeId: -302, transactionCode: 'APP-105', pluginId: 12).save(); // url: '/systemEntity/list'
            new RoleFeatureMapping(roleTypeId: -302, transactionCode: 'APP-107', pluginId: 12).save(); // url: '/systemEntity/update'
            new RoleFeatureMapping(roleTypeId: -302, transactionCode: 'APP-173', pluginId: 12).save(); // url: '/systemEntity/delete'

            new RoleFeatureMapping(roleTypeId: -302, transactionCode: 'APP-108', pluginId: 12).save(); // url: '/systemEntityType/show'
            new RoleFeatureMapping(roleTypeId: -302, transactionCode: 'APP-109', pluginId: 12).save(); // url: '/systemEntityType/list'

            new RoleFeatureMapping(roleTypeId: -302, transactionCode: 'APP-187', pluginId: 12).save(); // url: '/appMail/show'
            new RoleFeatureMapping(roleTypeId: -302, transactionCode: 'APP-188', pluginId: 12).save(); // url: '/appMail/update'
            new RoleFeatureMapping(roleTypeId: -302, transactionCode: 'APP-189', pluginId: 12).save(); // url: '/appMail/list'
            new RoleFeatureMapping(roleTypeId: -302, transactionCode: 'APP-251', pluginId: 12).save(); // url: '/appMail/testAppMail'

            new RoleFeatureMapping(roleTypeId: -302, transactionCode: 'APP-180', pluginId: 12).save(); // url: '/appSms/showSms'
            new RoleFeatureMapping(roleTypeId: -302, transactionCode: 'APP-181', pluginId: 12).save(); // url: '/appSms/updateSms'
            new RoleFeatureMapping(roleTypeId: -302, transactionCode: 'APP-182', pluginId: 12).save(); // url: '/appSms/listSms'
            new RoleFeatureMapping(roleTypeId: -302, transactionCode: 'APP-237', pluginId: 12).save(); // url: '/appSms/sendSms'

            new RoleFeatureMapping(roleTypeId: -302, transactionCode: 'APP-279', pluginId: 12).save(); //url: '/appSchedule/show'
            new RoleFeatureMapping(roleTypeId: -302, transactionCode: 'APP-280', pluginId: 12).save(); //url: '/appSchedule/list'
            new RoleFeatureMapping(roleTypeId: -302, transactionCode: 'APP-282', pluginId: 12).save(); //url: '/appSchedule/update'
            new RoleFeatureMapping(roleTypeId: -302, transactionCode: 'APP-283', pluginId: 12).save(); //url: '/appSchedule/testExecute'
            new RoleFeatureMapping(roleTypeId: -302, transactionCode: 'APP-385', pluginId: 12).save(); // url: '/schemaInformation/listSchemaInfo'

            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'EXH-291', pluginId: 12).save(); // url: '/exhTask/reloadCurrencyDropDownForExhTask'
            new RoleFeatureMapping(roleTypeId: -302, transactionCode: 'EXH-291', pluginId: 12).save(); // url: '/exhTask/reloadCurrencyDropDownForExhTask'

            new RoleFeatureMapping(roleTypeId: -301, transactionCode: 'EXH-185', pluginId: 12).save(); // url: '/exhTask/calculateFeesAndCommission'
            new RoleFeatureMapping(roleTypeId: -302, transactionCode: 'EXH-185', pluginId: 12).save(); // url: '/exhTask/calculateFeesAndCommission'

            // appTheme
            new RoleFeatureMapping(roleTypeId: -302, transactionCode: 'APP-124', pluginId: 12).save(); // url: '/appTheme/showTheme'
            new RoleFeatureMapping(roleTypeId: -302, transactionCode: 'APP-125', pluginId: 12).save(); // url: '/appTheme/updateTheme'
            new RoleFeatureMapping(roleTypeId: -302, transactionCode: 'APP-126', pluginId: 12).save(); // url: '/appTheme/listTheme'

            // SarbCancelRequest
            new RoleFeatureMapping(roleTypeId: -302, transactionCode: 'SARB-86', pluginId: 12).save(); // url: '/sarbCancelRequest/show'
            new RoleFeatureMapping(roleTypeId: -302, transactionCode: 'SARB-87', pluginId: 12).save(); // url: '/sarbCancelRequest/list'
            new RoleFeatureMapping(roleTypeId: -302, transactionCode: 'SARB-88', pluginId: 12).save(); // url: '/sarbCancelRequest/requestForCancel'
            new RoleFeatureMapping(roleTypeId: -302, transactionCode: 'SARB-89', pluginId: 12).save(); // url: '/sarbCancelRequest/retrieveResponse'

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }

    }

    public boolean createRoleFeatureMapForElearning() {
        try {
            // for appMail
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'APP-187', pluginId: 15).save(); // url: '/appMail/show'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'APP-188', pluginId: 15).save(); // url: '/appMail/update'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'APP-189', pluginId: 15).save(); // url: '/appMail/list'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'APP-251', pluginId: 15).save(); // url: '/appMail/testAppMail'

            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'APP-180', pluginId: 15).save(); // url: '/appSms/showSms'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'APP-181', pluginId: 15).save(); // url: '/appSms/updateSms'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'APP-182', pluginId: 15).save(); // url: '/appSms/listSms'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'APP-237', pluginId: 15).save(); // url: '/appSms/sendSms'

            // system config
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'APP-99', pluginId: 15).save(); // url: '/systemConfiguration/show'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'APP-100', pluginId: 15).save(); // url: '/systemConfiguration/list'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'APP-102', pluginId: 15).save(); // url: '/systemConfiguration/update'

            // for system entity role type -802
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'APP-103', pluginId: 15).save(); // url: '/systemEntity/show'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'APP-104', pluginId: 15).save(); // url: '/systemEntity/create'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'APP-105', pluginId: 15).save(); // url: '/systemEntity/list'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'APP-107', pluginId: 15).save(); // url: '/systemEntity/update'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'APP-173', pluginId: 15).save(); // url: '/systemEntity/delete'
            // system entity type
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'APP-108', pluginId: 15).save(); // url: '/systemEntityType/show'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'APP-109', pluginId: 15).save(); // url: '/systemEntityType/list'

            // elResource
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-37', pluginId: 15).save(); // url: '/elResource/show'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-38', pluginId: 15).save(); // url: '/elResource/list'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-39', pluginId: 15).save(); // url: '/elResource/create'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-40', pluginId: 15).save(); // url: '/elResource/delete'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-41', pluginId: 15).save(); // url: '/elResource/listDocumentByCategory'

            // appMail
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'APP-187', pluginId: 15).save(); // url: '/appMail/show'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-187', pluginId: 15).save(); // url: '/appMail/show'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'APP-187', pluginId: 15).save(); // url: '/appMail/show'

            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'APP-188', pluginId: 15).save(); // url: '/appMail/update'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-188', pluginId: 15).save(); // url: '/appMail/update'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'APP-188', pluginId: 15).save(); // url: '/appMail/update'

            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'APP-189', pluginId: 15).save(); // url: '/appMail/list'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-189', pluginId: 15).save(); // url: '/appMail/list'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'APP-189', pluginId: 15).save(); // url: '/appMail/list'

            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'APP-251', pluginId: 15).save(); // url: '/appMail/testAppMail'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-251', pluginId: 15).save(); // url: '/appMail/testAppMail'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'APP-251', pluginId: 15).save(); // url: '/appMail/testAppMail'


            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-162', pluginId: 15).save(); // url: '/elMail/showForComposeMail'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-162', pluginId: 15).save(); // url: '/elMail/showForComposeMail'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-162', pluginId: 15).save(); // url: '/elMail/showForComposeMail'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-162', pluginId: 15).save(); // url: '/elMail/showForComposeMail'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-162', pluginId: 15).save(); // url: '/elMail/showForComposeMail'

            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'APP-428', pluginId: 15).save(); // url: '/appMail/listMail'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-428', pluginId: 15).save(); // url: '/appMail/listMail'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'APP-428', pluginId: 15).save(); // url: '/appMail/listMail'

            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'APP-429', pluginId: 15).save(); // url: '/appMail/createMail'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-429', pluginId: 15).save(); // url: '/appMail/createMail'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'APP-429', pluginId: 15).save(); // url: '/appMail/createMail'

            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'APP-430', pluginId: 15).save(); // url: '/appMail/UpdateMail'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-430', pluginId: 15).save(); // url: '/appMail/UpdateMail'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'APP-430', pluginId: 15).save(); // url: '/appMail/UpdateMail'

            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'APP-431', pluginId: 15).save(); // url: '/appMail/sendMail'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-431', pluginId: 15).save(); // url: '/appMail/sendMail'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'APP-431', pluginId: 15).save(); // url: '/appMail/sendMail'

            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'APP-432', pluginId: 15).save(); // url: '/appMail/deleteMail'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-432', pluginId: 15).save(); // url: '/appMail/deleteMail'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'APP-432', pluginId: 15).save(); // url: '/appMail/deleteMail'

            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-163', pluginId: 15).save(); // url: '/elMail/showForSentMail'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-163', pluginId: 15).save(); // url: '/elMail/showForSentMail'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-163', pluginId: 15).save(); // url: '/elMail/showForSentMail'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-163', pluginId: 15).save(); // url: '/elMail/showForSentMail'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-163', pluginId: 15).save(); // url: '/elMail/showForSentMail'

            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'APP-436', pluginId: 15).save(); // url: '/appMail/listForSentMail'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-436', pluginId: 15).save(); // url: '/appMail/listForSentMail'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'APP-436', pluginId: 15).save(); // url: '/appMail/listForSentMail'

            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'APP-437', pluginId: 15).save(); // url: '/appMail/previewMail'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-437', pluginId: 15).save(); // url: '/appMail/previewMail'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'APP-437', pluginId: 15).save(); // url: '/appMail/previewMail'

            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'APP-438', pluginId: 15).save(); // url: '/appMail/createAndSend'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-438', pluginId: 15).save(); // url: '/appMail/createAndSend'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'APP-438', pluginId: 15).save(); // url: '/appMail/createAndSend'

            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'APP-439', pluginId: 15).save(); // url: '/appMail/uploadAttachment'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-439', pluginId: 15).save(); // url: '/appMail/uploadAttachment'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'APP-439', pluginId: 15).save(); // url: '/appMail/uploadAttachment'

            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-164', pluginId: 15).save(); // url: '/elMail/showMessage'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-164', pluginId: 15).save(); // url: '/elMail/showMessage'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-164', pluginId: 15).save(); // url: '/elMail/showMessage'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-164', pluginId: 15).save(); // url: '/elMail/showMessage'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-164', pluginId: 15).save(); // url: '/elMail/showMessage'

            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'APP-423', pluginId: 15).save(); // url: '/appMessage/list'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-423', pluginId: 15).save(); // url: '/appMessage/list'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'APP-423', pluginId: 15).save(); // url: '/appMessage/list'

            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'APP-424', pluginId: 15).save(); // url: '/appMessage/delete'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-424', pluginId: 15).save(); // url: '/appMessage/delete'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'APP-424', pluginId: 15).save(); // url: '/appMessage/delete'

            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'APP-426', pluginId: 15).save(); // url: '/appMessage/markAsUnRead'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-426', pluginId: 15).save(); // url: '/appMessage/markAsUnRead'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'APP-426', pluginId: 15).save(); // url: '/appMessage/markAsUnRead'

            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'APP-434', pluginId: 15).save(); // url: '/appMessage/preview'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-434', pluginId: 15).save(); // url: '/appMessage/preview'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'APP-434', pluginId: 15).save(); // url: '/appMessage/preview'

            // AppFaq
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'APP-440', pluginId: 15).save(); // url: '/appFaq/show'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-440', pluginId: 15).save(); // url: '/appFaq/show'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'APP-441', pluginId: 15).save(); // url: '/appFaq/list'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-441', pluginId: 15).save(); // url: '/appFaq/list'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'APP-442', pluginId: 15).save(); // url: '/appFaq/create'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-442', pluginId: 15).save(); // url: '/appFaq/create'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'APP-443', pluginId: 15).save(); // url: '/appFaq/update'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-443', pluginId: 15).save(); // url: '/appFaq/update'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'APP-444', pluginId: 15).save(); // url: '/appFaq/delete'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-444', pluginId: 15).save(); // url: '/appFaq/delete'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'APP-445', pluginId: 15).save(); // url: '/appFaq/listFaq'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-445', pluginId: 15).save(); // url: '/appFaq/listFaq'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'APP-446', pluginId: 15).save(); // url: '/appFaq/reloadFaq'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-446', pluginId: 15).save(); // url: '/appFaq/reloadFaq'


            // sms
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-180', pluginId: 15).save(); // url: '/appSms/showSms'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-181', pluginId: 15).save(); // url: '/appSms/updateSms'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-182', pluginId: 15).save(); // url: '/appSms/listSms'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-237', pluginId: 15).save(); // url: '/appSms/sendSms'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-374', pluginId: 15).save(); // url: '/appSms/create'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-375', pluginId: 15).save(); // url: '/appSms/delete'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-376', pluginId: 15).save(); // url: '/appSms/showForCompose'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-377', pluginId: 15).save(); // url: '/appSms/listForCompose'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-378', pluginId: 15).save(); // url: '/appSms/updateForCompose'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-379', pluginId: 15).save(); // url: '/appSms/sendForCompose'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-380', pluginId: 15).save(); // url: '/appSms/showForSend'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-381', pluginId: 15).save(); // url: '/appSms/listForSend'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-382', pluginId: 15).save(); // url: '/appSms/reCompose'

            // appTheme
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'APP-124', pluginId: 15).save(); // url: '/appTheme/showTheme'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'APP-125', pluginId: 15).save(); // url: '/appTheme/updateTheme'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'APP-126', pluginId: 15).save(); // url: '/appTheme/listTheme'

            // Switch user
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'APP-449', pluginId: 15).save(); // url: '/j_spring_security_switch_user'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'APP-449', pluginId: 15).save(); // url: '/j_spring_security_switch_user'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-449', pluginId: 15).save(); // url: '/j_spring_security_switch_user'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'APP-449', pluginId: 15).save(); // url: '/j_spring_security_switch_user'

            // render top menu
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-1', pluginId: 15).save();      // url: '/document/renderElearningMenu'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-1', pluginId: 15).save();      // url: '/document/renderElearningMenu'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-1', pluginId: 15).save();      // url: '/document/renderElearningMenu'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-1', pluginId: 15).save();      // url: '/document/renderElearningMenu'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-1', pluginId: 15).save();      // url: '/document/renderElearningMenu'

            // el my favourite
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-169', pluginId: 15).save();      // url: '/elearning/elMyFavourite/show'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-169', pluginId: 15).save();      // url: '/elearning/elMyFavourite/show'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-169', pluginId: 15).save();      // url: '/elearning/elMyFavourite/show'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-169', pluginId: 15).save();      // url: '/elearning/elMyFavourite/show'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-169', pluginId: 15).save();      // url: '/elearning/elMyFavourite/show'

            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-2', pluginId: 15).save();      // url: '/elCourse/show'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-2', pluginId: 15).save();      // url: '/elCourse/show'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-112', pluginId: 15).save();      // url: '/elCourse/showConfiguration'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-112', pluginId: 15).save();      // url: '/elCourse/showConfiguration'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-113', pluginId: 15).save();      // url: '/elCourse/updateConfiguration'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-113', pluginId: 15).save();      // url: '/elCourse/updateConfiguration'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-105', pluginId: 15).save();      // url: '/elCourse/showMember'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-105', pluginId: 15).save();      // url: '/elCourse/showMember'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-105', pluginId: 15).save();      // url: '/elCourse/showMember'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-105', pluginId: 15).save();      // url: '/elCourse/showMember'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-106', pluginId: 15).save();      // url: '/elCourse/listMember'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-106', pluginId: 15).save();      // url: '/elCourse/listMember'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-106', pluginId: 15).save();      // url: '/elCourse/listMember'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-106', pluginId: 15).save();      // url: '/elCourse/listMember'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-106', pluginId: 15).save();      // url: '/elCourse/listMember'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-3', pluginId: 15).save();      // url: '/elCourse/list'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-3', pluginId: 15).save();      // url: '/elCourse/list'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-4', pluginId: 15).save();      // url: '/elCourse/create'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-4', pluginId: 15).save();      // url: '/elCourse/create'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-5', pluginId: 15).save();      // url: '/elCourse/update'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-5', pluginId: 15).save();      // url: '/elCourse/update'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-6', pluginId: 15).save();      // url: '/elCourse/delete'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-6', pluginId: 15).save();      // url: '/elCourse/delete'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-191', pluginId: 15).save();    // url: '/elCourse/deleteCourseContent'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-7', pluginId: 15).save();      // url: '/elCourse/uploadImage'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-7', pluginId: 15).save();      // url: '/elCourse/uploadImage'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-11', pluginId: 15).save();      // url: '/elCourse/select'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-11', pluginId: 15).save();      // url: '/elCourse/select'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-12', pluginId: 15).save();      // url: '/elCourse/showImage'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-12', pluginId: 15).save();      // url: '/elCourse/showImage'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-12', pluginId: 15).save();      // url: '/elCourse/showImage'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-109', pluginId: 15).save();      // url: '/elCourse/listRunningCourseForOther'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-109', pluginId: 15).save();      // url: '/elCourse/listRunningCourseForOther'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-109', pluginId: 15).save();      // url: '/elCourse/listRunningCourseForOther'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-29', pluginId: 15).save();      // url: '/elCourse/listRunningCourse'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-61', pluginId: 15).save();      // url: '/elCourse/joinCourse'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-61', pluginId: 15).save();      // url: '/elCourse/joinCourse'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-61', pluginId: 15).save();      // url: '/elCourse/joinCourse'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-61', pluginId: 15).save();      // url: '/elCourse/joinCourse'

            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-111', pluginId: 15).save();      // url: '/elCourse/resume'

            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-138', pluginId: 15).save();      // url: '/elCourse/cloneCourse'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-138', pluginId: 15).save();      // url: '/elCourse/cloneCourse'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-141', pluginId: 15).save();      // url: '/elCourse/showCourseStats'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-141', pluginId: 15).save();      // url: '/elCourse/showCourseStats'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-141', pluginId: 15).save();      // url: '/elCourse/showCourseStats'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-141', pluginId: 15).save();      // url: '/elCourse/showCourseStats'

            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-131', pluginId: 15).save();      // url: '/elCourse/showMemberForRunningCourse'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-131', pluginId: 15).save();      // url: '/elCourse/showMemberForRunningCourse'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-131', pluginId: 15).save();      // url: '/elCourse/showMemberForRunningCourse'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-131', pluginId: 15).save();      // url: '/elCourse/showCourseStats'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-131', pluginId: 15).save();      // url: '/elCourse/showMemberForRunningCourse'

            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-151', pluginId: 15).save();      // url: '/elCourse/showReadUnReadContent'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-151', pluginId: 15).save();      // url: '/elCourse/showReadUnReadContent'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-151', pluginId: 15).save();      // url: '/elCourse/showReadUnReadContent'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-151', pluginId: 15).save();      // url: '/elCourse/showReadUnReadContent'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-151', pluginId: 15).save();      // url: '/elCourse/showReadUnReadContent'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-152', pluginId: 15).save();      // url: '/elCourse/listReadUnReadContent'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-152', pluginId: 15).save();      // url: '/elCourse/listReadUnReadContent'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-152', pluginId: 15).save();      // url: '/elCourse/listReadUnReadContent'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-152', pluginId: 15).save();      // url: '/elCourse/listReadUnReadContent'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-152', pluginId: 15).save();      // url: '/elCourse/listReadUnReadContent'

            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-166', pluginId: 15).save();      // url: '/elCourse/leave'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-166', pluginId: 15).save();      // url: '/elCourse/leave'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-166', pluginId: 15).save();      // url: '/elCourse/leave'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-166', pluginId: 15).save();      // url: '/elCourse/leave'


            // delete course and lesson mapping
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'DOC-27', pluginId: 15).save();     // url: '/docCategoryUserMapping/delete'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-27', pluginId: 15).save();     // url: '/docCategoryUserMapping/delete'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-27', pluginId: 15).save();     // url: '/docCategoryUserMapping/delete'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'DOC-27', pluginId: 15).save();     // url: '/docCategoryUserMapping/delete'

            // ElCourseDetails
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-9', pluginId: 15).save();      // url: '/elCourseDetails/show'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-9', pluginId: 15).save();      // url: '/elCourseDetails/show'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-9', pluginId: 15).save();      // url: '/elCourseDetails/show'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-10', pluginId: 15).save();      // url: '/elCourseDetails/update'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-10', pluginId: 15).save();      // url: '/elCourseDetails/update'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-10', pluginId: 15).save();      // url: '/elCourseDetails/update'
            // ElLesson
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-13', pluginId: 15).save();      // url: '/elLesson/show'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-13', pluginId: 15).save();      // url: '/elLesson/show'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-13', pluginId: 15).save();      // url: '/elLesson/show'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-14', pluginId: 15).save();      // url: '/elLesson/list'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-14', pluginId: 15).save();      // url: '/elLesson/list'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-14', pluginId: 15).save();      // url: '/elLesson/list'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-14', pluginId: 15).save();      // url: '/elLesson/list'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-15', pluginId: 15).save();      // url: '/elLesson/create'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-15', pluginId: 15).save();      // url: '/elLesson/create'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-15', pluginId: 15).save();      // url: '/elLesson/create'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-16', pluginId: 15).save();      // url: '/elLesson/update'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-16', pluginId: 15).save();      // url: '/elLesson/update'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-16', pluginId: 15).save();      // url: '/elLesson/update'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-17', pluginId: 15).save();      // url: '/elLesson/delete'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-17', pluginId: 15).save();      // url: '/elLesson/delete'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-17', pluginId: 15).save();      // url: '/elLesson/delete'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-18', pluginId: 15).save();      // url: '/elLesson/reloadList'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-18', pluginId: 15).save();      // url: '/elLesson/reloadList'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-18', pluginId: 15).save();      // url: '/elLesson/reloadList'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-18', pluginId: 15).save();      // url: '/elLesson/reloadList'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-43', pluginId: 15).save();      // url: '/elLesson/reloadAssignmentQuizExamCount'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-43', pluginId: 15).save();      // url: '/elLesson/reloadAssignmentQuizExamCount'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-43', pluginId: 15).save();      // url: '/elLesson/reloadAssignmentQuizExamCount'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-43', pluginId: 15).save();      // url: '/elLesson/reloadAssignmentQuizExamCount'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-168', pluginId: 15).save();      // url: '/elLesson/listChildLesson'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-168', pluginId: 15).save();      // url: '/elLesson/listChildLesson'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-168', pluginId: 15).save();      // url: '/elLesson/listChildLesson'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-168', pluginId: 15).save();      // url: '/elLesson/listChildLesson'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-168', pluginId: 15).save();      // url: '/elLesson/listChildLesson'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-192', pluginId: 15).save();      // url: '/elLesson/reloadListDashboard'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-192', pluginId: 15).save();      // url: '/elLesson/reloadListDashboard'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-192', pluginId: 15).save();      // url: '/elLesson/reloadListDashboard'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-192', pluginId: 15).save();      // url: '/elLesson/reloadListDashboard'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-192', pluginId: 15).save();      // url: '/elLesson/reloadListDashboard'

            //show courses
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-19', pluginId: 15).save();    // url: '/elCourse/showMyCourse'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-19', pluginId: 15).save();    // url: '/elCourse/showMyCourse'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-19', pluginId: 15).save();    // url: '/elCourse/showMyCourse'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-19', pluginId: 15).save();    // url: '/elCourse/showMyCourse'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-20', pluginId: 15).save();    // url: '/elCourse/showUpcoming'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-20', pluginId: 15).save();    // url: '/elCourse/showUpcoming'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-20', pluginId: 15).save();    // url: '/elCourse/showUpcoming'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-20', pluginId: 15).save();    // url: '/elCourse/showUpcoming'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-21', pluginId: 15).save();    // url: '/elCourse/showArchive'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-21', pluginId: 15).save();    // url: '/elCourse/showArchive'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-21', pluginId: 15).save();    // url: '/elCourse/showArchive'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-21', pluginId: 15).save();    // url: '/elCourse/showArchive'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-22', pluginId: 15).save();    // url: '/elCourse/showWatchList'

            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-71', pluginId: 15).save();    // url: '/elCourse/showOtherCourse'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-71', pluginId: 15).save();    // url: '/elCourse/showOtherCourse'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-71', pluginId: 15).save();    // url: '/elCourse/showOtherCourse'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-71', pluginId: 15).save();    // url: '/elCourse/showOtherCourse'

            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-193', pluginId: 15).save();    // url: '/elCourse/showClone'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-193', pluginId: 15).save();    // url: '/elCourse/showClone'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-193', pluginId: 15).save();    // url: '/elCourse/showClone'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-194', pluginId: 15).save();    // url: '/elCourse/listAllCourse'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-194', pluginId: 15).save();    // url: '/elCourse/listAllCourse'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-194', pluginId: 15).save();    // url: '/elCourse/listAllCourse'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-194', pluginId: 15).save();    // url: '/elCourse/listAllCourse'


            //ElQuiz
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-76', pluginId: 15).save();    // url: '/elQuiz/show'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-76', pluginId: 15).save();    // url: '/elQuiz/show'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-76', pluginId: 15).save();    // url: '/elQuiz/show'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-77', pluginId: 15).save();    // url: '/elQuiz/list'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-77', pluginId: 15).save();    // url: '/elQuiz/list'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-77', pluginId: 15).save();    // url: '/elQuiz/list'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-78', pluginId: 15).save();    // url: '/elQuiz/create'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-78', pluginId: 15).save();    // url: '/elQuiz/create'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-78', pluginId: 15).save();    // url: '/elQuiz/create'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-79', pluginId: 15).save();    // url: '/elQuiz/update'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-79', pluginId: 15).save();    // url: '/elQuiz/update'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-79', pluginId: 15).save();    // url: '/elQuiz/update'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-80', pluginId: 15).save();    // url: '/elQuiz/delete'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-80', pluginId: 15).save();    // url: '/elQuiz/delete'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-80', pluginId: 15).save();    // url: '/elQuiz/delete'

            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-186', pluginId: 15).save();    // url: '/elQuiz/sendNotification'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-186', pluginId: 15).save();    // url: '/elQuiz/sendNotification'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-186', pluginId: 15).save();    // url: '/elQuiz/sendNotification'

            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-91', pluginId: 15).save();    // url: '/elQuiz/showQuizWithAnswer'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-92', pluginId: 15).save();    // url: '/elQuiz/showQuiz'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-93', pluginId: 15).save();    // url: '/elQuiz/listQuestionForDropDown'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-97', pluginId: 15).save();    // url: '/elQuiz/submitQuiz'

            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-91', pluginId: 15).save();    // url: '/elQuiz/showQuizWithAnswer'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-92', pluginId: 15).save();    // url: '/elQuiz/showQuiz'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-93', pluginId: 15).save();    // url: '/elQuiz/listQuestionForDropDown'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-97', pluginId: 15).save();    // url: '/elQuiz/submitQuiz'

            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-91', pluginId: 15).save();    // url: '/elQuiz/showQuizWithAnswer'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-92', pluginId: 15).save();    // url: '/elQuiz/showQuiz'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-93', pluginId: 15).save();    // url: '/elQuiz/listQuestionForDropDown'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-97', pluginId: 15).save();    // url: '/elQuiz/submitQuiz'

            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-91', pluginId: 15).save();    // url: '/elQuiz/showQuizWithAnswer'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-92', pluginId: 15).save();    // url: '/elQuiz/showQuiz'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-93', pluginId: 15).save();    // url: '/elQuiz/listQuestionForDropDown'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-97', pluginId: 15).save();    // url: '/elQuiz/submitQuiz'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-157', pluginId: 15).save();    // url: '/elQuiz/showParticipateQuiz'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-157', pluginId: 15).save();    // url: '/elQuiz/showParticipateQuiz'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-157', pluginId: 15).save();    // url: '/elQuiz/showParticipateQuiz'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-157', pluginId: 15).save();    // url: '/elQuiz/showParticipateQuiz'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-157', pluginId: 15).save();    // url: '/elQuiz/showParticipateQuiz'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-158', pluginId: 15).save();    // url: '/elQuiz/listParticipateQuiz'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-158', pluginId: 15).save();    // url: '/elQuiz/listParticipateQuiz'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-158', pluginId: 15).save();    // url: '/elQuiz/listParticipateQuiz'

            // ElQuizQuestion
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-81', pluginId: 15).save();      // url: '/elQuizQuestion/show'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-81', pluginId: 15).save();      // url: '/elQuizQuestion/show'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-81', pluginId: 15).save();      // url: '/elQuizQuestion/show'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-82', pluginId: 15).save();      // url: '/elQuizQuestion/list'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-82', pluginId: 15).save();      // url: '/elQuizQuestion/list'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-82', pluginId: 15).save();      // url: '/elQuizQuestion/list'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-83', pluginId: 15).save();      // url: '/elQuizQuestion/listQuestionByLesson'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-83', pluginId: 15).save();      // url: '/elQuizQuestion/listQuestionByLesson'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-83', pluginId: 15).save();      // url: '/elQuizQuestion/listQuestionByLesson'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-84', pluginId: 15).save();      // url: '/elQuizQuestion/create'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-84', pluginId: 15).save();      // url: '/elQuizQuestion/create'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-84', pluginId: 15).save();      // url: '/elQuizQuestion/create'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-85', pluginId: 15).save();      // url: '/elQuizQuestion/delete'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-85', pluginId: 15).save();      // url: '/elQuizQuestion/delete'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-85', pluginId: 15).save();      // url: '/elQuizQuestion/delete'

            // ElQuizQuestion
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-86', pluginId: 15).save();      // url: '/elAssignmentContent/show'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-86', pluginId: 15).save();      // url: '/elAssignmentContent/show'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-86', pluginId: 15).save();      // url: '/elAssignmentContent/show'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-87', pluginId: 15).save();      // url: '/elAssignmentContent/list'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-87', pluginId: 15).save();      // url: '/elAssignmentContent/list'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-87', pluginId: 15).save();      // url: '/elAssignmentContent/list'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-88', pluginId: 15).save();      // url: '/elAssignmentContent/listDocumentByCategory'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-88', pluginId: 15).save();      // url: '/elAssignmentContent/listDocumentByCategory'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-88', pluginId: 15).save();      // url: '/elAssignmentContent/listDocumentByCategory'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-89', pluginId: 15).save();      // url: '/elAssignmentContent/create'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-89', pluginId: 15).save();      // url: '/elAssignmentContent/create'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-89', pluginId: 15).save();      // url: '/elAssignmentContent/create'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-90', pluginId: 15).save();      // url: '/elAssignmentContent/delete'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-90', pluginId: 15).save();      // url: '/elAssignmentContent/delete'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-90', pluginId: 15).save();      // url: '/elAssignmentContent/delete'


            //For document
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'DOC-186', pluginId: 15).save();    // url: '/docDocument/reloadDocumentBySubcategory'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'DOC-186', pluginId: 15).save();    // url: '/docDocument/reloadDocumentBySubcategory'

            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-1', pluginId: 15).save();    // url: '/document/renderDocumentMenu'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-1', pluginId: 15).save();    // url: '/document/renderDocumentMenu'

            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-19', pluginId: 15).save();    // url: '/docSubCategory/dropDownSubCategoryReload'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-19', pluginId: 15).save();    // url: '/docSubCategory/dropDownSubCategoryReload'

            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-7', pluginId: 15).save();    // url: '/docCategory/viewCategoryDetails'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-7', pluginId: 15).save();    // url: '/docCategory/viewCategoryDetails'

            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-13', pluginId: 15).save();    // url: '/docSubCategory/list'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-216', pluginId: 15).save();    // url: '/docSubCategory/reloadDocSubCategoryTreeList'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-13', pluginId: 15).save();    // url: '/docSubCategory/list'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-216', pluginId: 15).save();    // url: '/docSubCategory/reloadDocSubCategoryTreeList'

//            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-8', pluginId: 15).save();    // url: '/docCategory/showCategories'
//            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-8', pluginId: 15).save();    // url: '/docCategory/showCategories'

            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-18', pluginId: 15).save();    // url: '/docSubCategory/viewSubCategoryDetails'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-18', pluginId: 15).save();    // url: '/docSubCategory/viewSubCategoryDetails'

            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-17', pluginId: 15).save();    // url: '/docSubCategory/showSubCategories'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-17', pluginId: 15).save();    // url: '/docSubCategory/showSubCategories'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'DOC-209', pluginId: 15).save();    // url: '/docSubCategory/listSubCategoryByCategory'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'APP-441', pluginId: 15).save();    // url: '/appFaq/list'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'APP-442', pluginId: 15).save();    // url: '/appFaq/create'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'APP-443', pluginId: 15).save();    // url: '/appFaq/update'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'APP-444', pluginId: 15).save();    // url: '/appFaq/delete'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-209', pluginId: 15).save();    // url: '/docSubCategory/listSubCategoryByCategory'

            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-35', pluginId: 15).save();    // url: '/docInvitedMembers/show'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-35', pluginId: 15).save();    // url: '/docInvitedMembers/show'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-36', pluginId: 15).save();    // url: '/docInvitedMembers/showInvitation'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-36', pluginId: 15).save();    // url: '/docInvitedMembers/showInvitation'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-37', pluginId: 15).save();    // url: '/docInvitedMembers/listInvitation'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-37', pluginId: 15).save();    // url: '/docInvitedMembers/listInvitation'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-38', pluginId: 15).save();    // url: '/docInvitedMembers/showResendInvitation'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-38', pluginId: 15).save();    // url: '/docInvitedMembers/showResendInvitation'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-39', pluginId: 15).save();    // url: '/docInvitedMembers/sendInvitation'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-39', pluginId: 15).save();    // url: '/docInvitedMembers/sendInvitation'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-40', pluginId: 15).save();    // url: '/docInvitedMembers/delete'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-40', pluginId: 15).save();    // url: '/docInvitedMembers/delete'

            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-41', pluginId: 15).save();    // url: '/docMemberJoinRequest/show'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-41', pluginId: 15).save();    // url: '/docMemberJoinRequest/show'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-42', pluginId: 15).save();    // url: '/docMemberJoinRequest/searchJoinRequest'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-42', pluginId: 15).save();    // url: '/docMemberJoinRequest/searchJoinRequest'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-43', pluginId: 15).save();    // url: '/docMemberJoinRequest/approvedForMembership'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-43', pluginId: 15).save();    // url: '/docMemberJoinRequest/approvedForMembership'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-44', pluginId: 15).save();    // url: '/docMemberJoinRequest/applyForCategoryMembership'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-44', pluginId: 15).save();    // url: '/docMemberJoinRequest/applyForCategoryMembership'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-45', pluginId: 15).save();    // url: '/docMemberJoinRequest/applyForSubCategoryMembership'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-45', pluginId: 15).save();    // url: '/docMemberJoinRequest/applyForSubCategoryMembership'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'DOC-46', pluginId: 15).save();    // url: '/docMemberJoinRequest/delete'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-46', pluginId: 15).save();    // url: '/docMemberJoinRequest/delete'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-46', pluginId: 15).save();    // url: '/docMemberJoinRequest/delete'

            // for document viewer
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'DOC-191', pluginId: 15).save();    // url: '/docMemberJoinRequest/showDocumentViewer'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-191', pluginId: 15).save();    // url: '/docMemberJoinRequest/showDocumentViewer'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-191', pluginId: 15).save();    // url: '/docMemberJoinRequest/showDocumentViewer'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'DOC-191', pluginId: 15).save();    // url: '/docMemberJoinRequest/showDocumentViewer'

            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'DOC-202', pluginId: 15).save();    // url: '/docMemberJoinRequest/showDiscussion'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'DOC-203', pluginId: 15).save();    // url: '/docMemberJoinRequest/downloadDocumentForViewer'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'DOC-204', pluginId: 15).save();    // url: '/docReport/downloadArticleForViewer'


            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-202', pluginId: 15).save();    // url: '/docMemberJoinRequest/showDiscussion'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-203', pluginId: 15).save();    // url: '/docMemberJoinRequest/downloadDocumentForViewer'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-204', pluginId: 15).save();    // url: '/docReport/downloadArticleForViewer'

            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-202', pluginId: 15).save();    // url: '/docMemberJoinRequest/showDiscussion'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-203', pluginId: 15).save();    // url: '/docMemberJoinRequest/downloadDocumentForViewer'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-204', pluginId: 15).save();    // url: '/docReport/downloadArticleForViewer'

            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'DOC-202', pluginId: 15).save();    // url: '/docMemberJoinRequest/showDiscussion'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'DOC-203', pluginId: 15).save();    // url: '/docMemberJoinRequest/downloadDocumentForViewer'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'DOC-204', pluginId: 15).save();    // url: '/docReport/downloadArticleForViewer'

            //documentVersion
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-47', pluginId: 15).save();    // url: '/docDocumentVersion/showFile'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-70', pluginId: 15).save();    // url: '/docDocument/showFile'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-47', pluginId: 15).save();    // url: '/docDocumentVersion/showFile'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-70', pluginId: 15).save();    // url: '/docDocument/showFile'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-48', pluginId: 15).save();    // url: '/docDocumentVersion/listFile'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-48', pluginId: 15).save();    // url: '/docDocumentVersion/listFile'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-49', pluginId: 15).save();    // url: '/docDocumentVersion/createFile'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-76', pluginId: 15).save();    // url: '/docDocument/createFile'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-49', pluginId: 15).save();    // url: '/docDocumentVersion/createFile'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-76', pluginId: 15).save();    // url: '/docDocument/createFile'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-50', pluginId: 15).save();    // url: '/docDocumentVersion/updateFile'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-125', pluginId: 15).save();    // url: '/docDocument/updateFile'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-50', pluginId: 15).save();    // url: '/docDocumentVersion/updateFile'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-125', pluginId: 15).save();    // url: '/docDocument/updateFile'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-52', pluginId: 15).save();    // url: '/docDocumentVersion/showArticle'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-55', pluginId: 15).save();    // url: '/docDocumentVersion/showArticle'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-52', pluginId: 15).save();    // url: '/docDocumentVersion/showArticle'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-55', pluginId: 15).save();    // url: '/docDocumentVersion/showArticle'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-53', pluginId: 15).save();    // url: '/docDocumentVersion/listArticle'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-53', pluginId: 15).save();    // url: '/docDocumentVersion/listArticle'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-54', pluginId: 15).save();    // url: '/docDocumentVersion/createArticle'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-63', pluginId: 15).save();    // url: '/docDocumentVersion/createArticle'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-54', pluginId: 15).save();    // url: '/docDocumentVersion/createArticle'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-63', pluginId: 15).save();    // url: '/docDocumentVersion/createArticle'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-56', pluginId: 15).save();    // url: '/docDocumentVersion/updateArticle'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-69', pluginId: 15).save();    // url: '/docDocumentVersion/updateArticle'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-56', pluginId: 15).save();    // url: '/docDocumentVersion/updateArticle'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-69', pluginId: 15).save();    // url: '/docDocumentVersion/updateArticle'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-128', pluginId: 15).save();    // url: '/docDocumentVersion/showAudio'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'DOC-127', pluginId: 15).save();    // url: '/docDocument/showAudio'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-127', pluginId: 15).save();    // url: '/docDocument/showAudio'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'DOC-129', pluginId: 15).save();    // url: '/docDocument/createAudio'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-129', pluginId: 15).save();    // url: '/docDocument/createAudio'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'DOC-133', pluginId: 15).save();    // url: '/docDocument/updateAudio'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-133', pluginId: 15).save();    // url: '/docDocument/updateAudio'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'DOC-158', pluginId: 15).save();    // url: '/docDocument/updateVideo'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-158', pluginId: 15).save();    // url: '/docDocument/updateVideo'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'DOC-141', pluginId: 15).save();    // url: '/docDocument/createVideo'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-141', pluginId: 15).save();    // url: '/docDocument/createVideo'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'DOC-140', pluginId: 15).save();    // url: '/docDocument/showVideo'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-140', pluginId: 15).save();    // url: '/docDocument/showVideo'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-127', pluginId: 15).save();    // url: '/docDocument/showAudio'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-129', pluginId: 15).save();    // url: '/docDocument/createAudio'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-133', pluginId: 15).save();    // url: '/docDocument/updateAudio'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-158', pluginId: 15).save();    // url: '/docDocument/updateVideo'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-141', pluginId: 15).save();    // url: '/docDocument/createVideo'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-140', pluginId: 15).save();    // url: '/docDocument/showVideo'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-128', pluginId: 15).save();    // url: '/docDocumentVersion/showAudio'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-130', pluginId: 15).save();    // url: '/docDocumentVersion/createAudio'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-130', pluginId: 15).save();    // url: '/docDocumentVersion/createAudio'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-131', pluginId: 15).save();    // url: '/docDocumentVersion/updateAudio'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-131', pluginId: 15).save();    // url: '/docDocumentVersion/updateAudio'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-132', pluginId: 15).save();    // url: '/docDocumentVersion/showVideo'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-132', pluginId: 15).save();    // url: '/docDocumentVersion/showVideo'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-134', pluginId: 15).save();    // url: '/docDocumentVersion/createVideo'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-134', pluginId: 15).save();    // url: '/docDocumentVersion/createVideo'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-135', pluginId: 15).save();    // url: '/docDocumentVersion/updateVideo'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-135', pluginId: 15).save();    // url: '/docDocumentVersion/updateVideo'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-157', pluginId: 15).save();    // url: '/docDocumentVersion/showImage'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-165', pluginId: 15).save();    // url: '/docDocumentVersion/showImage'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-157', pluginId: 15).save();    // url: '/docDocumentVersion/showImage'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-165', pluginId: 15).save();    // url: '/docDocumentVersion/showImage'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-159', pluginId: 15).save();    // url: '/docDocumentVersion/createImage'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-196', pluginId: 15).save();    // url: '/docDocumentVersion/createImage'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-159', pluginId: 15).save();    // url: '/docDocumentVersion/createImage'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-196', pluginId: 15).save();    // url: '/docDocumentVersion/createImage'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-160', pluginId: 15).save();    // url: '/docDocumentVersion/updateImage'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-197', pluginId: 15).save();    // url: '/docDocumentVersion/updateImage'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-160', pluginId: 15).save();    // url: '/docDocumentVersion/updateImage'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-197', pluginId: 15).save();    // url: '/docDocumentVersion/updateImage'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-169', pluginId: 15).save();    // url: '/docDocumentVersion/show'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-169', pluginId: 15).save();    // url: '/docDocumentVersion/show'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-170', pluginId: 15).save();    // url: '/docDocumentVersion/list'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-170', pluginId: 15).save();    // url: '/docDocumentVersion/list'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-171', pluginId: 15).save();    // url: '/docDocumentVersion/delete'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-171', pluginId: 15).save();    // url: '/docDocumentVersion/delete'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-172', pluginId: 15).save();    // url: '/docDocumentVersion/restore'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-172', pluginId: 15).save();    // url: '/docDocumentVersion/restore'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-181', pluginId: 15).save();    // url: '/docDocumentVersion/showVersion'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-181', pluginId: 15).save();    // url: '/docDocumentVersion/showVersion'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-182', pluginId: 15).save();    // url: '/docDocumentVersion/listLatestVersion'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-182', pluginId: 15).save();    // url: '/docDocumentVersion/listLatestVersion'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-183', pluginId: 15).save();    // url: '/docDocumentVersion/listPreviousVersion'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-183', pluginId: 15).save();    // url: '/docDocumentVersion/listPreviousVersion'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-184', pluginId: 15).save();    // url: '/docDocumentVersion/deleteVersion'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-184', pluginId: 15).save();    // url: '/docDocumentVersion/deleteVersion'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-185', pluginId: 15).save();    // url: '/docDocumentVersion/restoreTrashedVersion'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-185', pluginId: 15).save();    // url: '/docDocumentVersion/restoreTrashedVersion'

            //DocDocument
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-153', pluginId: 15).save();    // url: '/docDocument/listFileDetails'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-153', pluginId: 15).save();    // url: '/docDocument/listFileDetails'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-154', pluginId: 15).save();    // url: '/docDocument/listImageDetails'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-154', pluginId: 15).save();    // url: '/docDocument/listImageDetails'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-51', pluginId: 15).save();    // url: '/docDocument/movedToTrashFile'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-51', pluginId: 15).save();    // url: '/docDocument/movedToTrashFile'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-177', pluginId: 15).save();    // url: '/docDocument/uploadFile'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-198', pluginId: 15).save();    // url: '/docDocument/uploadFile'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-177', pluginId: 15).save();    // url: '/docDocument/uploadFile'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-198', pluginId: 15).save();    // url: '/docDocument/uploadFile'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-178', pluginId: 15).save();    // url: '/docDocument/uploadImage'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-199', pluginId: 15).save();    // url: '/docDocument/uploadImage'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-178', pluginId: 15).save();    // url: '/docDocument/uploadImage'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-199', pluginId: 15).save();    // url: '/docDocument/uploadImage'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-179', pluginId: 15).save();    // url: '/docDocument/uploadAudio'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-200', pluginId: 15).save();    // url: '/docDocument/uploadAudio'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-201', pluginId: 15).save();    // url: '/docDocumentVersion/uploadAudio'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-179', pluginId: 15).save();    // url: '/docDocument/uploadAudio'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-200', pluginId: 15).save();    // url: '/docDocument/uploadAudio'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-201', pluginId: 15).save();    // url: '/docDocumentVersion/uploadAudio'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-180', pluginId: 15).save();    // url: '/docDocument/uploadVideo'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-180', pluginId: 15).save();    // url: '/docDocument/uploadVideo'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-152', pluginId: 15).save();    // url: '/docDocument/listArticleDetails'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-152', pluginId: 15).save();    // url: '/docDocument/listArticleDetails'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-57', pluginId: 15).save();    // url: '/docDocument/movedToTrashArticle'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-57', pluginId: 15).save();    // url: '/docDocument/movedToTrashArticle'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-59', pluginId: 15).save();    // url: '/docDocument/downloadDocument'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-59', pluginId: 15).save();    // url: '/docDocument/downloadDocument'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-60', pluginId: 15).save();    // url: '/docDocument/showDocumentQueryResult'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-60', pluginId: 15).save();    // url: '/docDocument/showDocumentQueryResult'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-61', pluginId: 15).save();    // url: '/docDocument/searchDocumentQueryResult'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-61', pluginId: 15).save();    // url: '/docDocument/searchDocumentQueryResult'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-64', pluginId: 15).save();    // url: '/docDocument/authenticate'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-64', pluginId: 15).save();    // url: '/docDocument/authenticate'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-126', pluginId: 15).save();    // url: '/docDocument/createComment'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-126', pluginId: 15).save();    // url: '/docDocument/createComment'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'DOC-126', pluginId: 15).save();    // url: '/docDocument/createComment'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-155', pluginId: 15).save();    // url: '/docDocument/listAudioDetails'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-155', pluginId: 15).save();    // url: '/docDocument/listAudioDetails'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-142', pluginId: 15).save();    // url: '/docDocument/movedToTrashAudio'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-142', pluginId: 15).save();    // url: '/docDocument/movedToTrashAudio'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-146', pluginId: 15).save();    // url: '/docDocument/showForViewVideo'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-146', pluginId: 15).save();    // url: '/docDocument/showForViewVideo'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-147', pluginId: 15).save();    // url: '/docDocument/showForViewAudio'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-147', pluginId: 15).save();    // url: '/docDocument/showForViewAudio'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-150', pluginId: 15).save();    // url: '/docDocument/showForViewFile'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-150', pluginId: 15).save();    // url: '/docDocument/showForViewFile'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-156', pluginId: 15).save();    // url: '/docDocument/listVideoDetails'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-156', pluginId: 15).save();    // url: '/docDocument/listVideoDetails'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-143', pluginId: 15).save();    // url: '/docDocument/movedToTrashVideo'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-143', pluginId: 15).save();    // url: '/docDocument/movedToTrashVideo'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-161', pluginId: 15).save();    // url: '/docDocument/movedToTrashImage'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-161', pluginId: 15).save();    // url: '/docDocument/movedToTrashImage'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-162', pluginId: 15).save();    // url: '/docDocument/showForViewImage'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-162', pluginId: 15).save();    // url: '/docDocument/showForViewImage'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-173', pluginId: 15).save();    // url: '/docDocument/checkInForArticle'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-173', pluginId: 15).save();    // url: '/docDocument/checkInForArticle'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-174', pluginId: 15).save();    // url: '/docDocument/checkOutForArticle'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-174', pluginId: 15).save();    // url: '/docDocument/checkOutForArticle'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-175', pluginId: 15).save();    // url: '/docDocument/checkInForFile'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-175', pluginId: 15).save();    // url: '/docDocument/checkInForFile'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-176', pluginId: 15).save();    // url: '/docDocument/checkOutForFile'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-176', pluginId: 15).save();    // url: '/docDocument/checkOutForFile'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-186', pluginId: 15).save();    // url: '/docDocument/reloadDocumentBySubcategory'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-186', pluginId: 15).save();    // url: '/docDocument/reloadDocumentBySubcategory'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-124', pluginId: 15).save();    // url: '/docDocument/changeOwner'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-124', pluginId: 15).save();    // url: '/docDocument/changeOwner'

            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'DOC-188', pluginId: 15).save();    // url: '/docDocument/reloadDocumentViewer'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'DOC-188', pluginId: 15).save();    // url: '/docDocument/reloadDocumentViewer'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-188', pluginId: 15).save();    // url: '/docDocument/reloadDocumentViewer'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-188', pluginId: 15).save();    // url: '/docDocument/reloadDocumentViewer'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'DOC-188', pluginId: 15).save();    // url: '/docDocument/reloadDocumentViewer'

            //DocDocumentReadLog
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-148', pluginId: 15).save();    // url: '/docDocumentReadLog/create'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-148', pluginId: 15).save();    // url: '/docDocumentReadLog/create'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'DOC-148', pluginId: 15).save();    // url: '/docDocumentReadLog/create'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-149', pluginId: 15).save();    // url: '/docDocumentReadLog/delete'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-149', pluginId: 15).save();    // url: '/docDocumentReadLog/delete'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'DOC-149', pluginId: 15).save();    // url: '/docDocumentReadLog/delete'

            //DocDocumentTrash
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-65', pluginId: 15).save();    // url: '/docDocumentTrash/showArticle'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-65', pluginId: 15).save();    // url: '/docDocumentTrash/showArticle'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-66', pluginId: 15).save();    // url: '/docDocumentTrash/listArticle'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-66', pluginId: 15).save();    // url: '/docDocumentTrash/listArticle'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-67', pluginId: 15).save();    // url: '/docDocumentTrash/restoreArticle'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-67', pluginId: 15).save();    // url: '/docDocumentTrash/restoreArticle'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-68', pluginId: 15).save();    // url: '/docDocumentTrash/deleteArticle'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-68', pluginId: 15).save();    // url: '/docDocumentTrash/deleteArticle'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-71', pluginId: 15).save();    // url: '/docDocumentTrash/showFile'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-71', pluginId: 15).save();    // url: '/docDocumentTrash/showFile'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-72', pluginId: 15).save();    // url: '/docDocumentTrash/listFile'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-72', pluginId: 15).save();    // url: '/docDocumentTrash/listFile'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-73', pluginId: 15).save();    // url: '/docDocumentTrash/restoreFile'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-73', pluginId: 15).save();    // url: '/docDocumentTrash/restoreFile'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-74', pluginId: 15).save();    // url: '/docDocumentTrash/deleteFile'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-74', pluginId: 15).save();    // url: '/docDocumentTrash/deleteFile'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-136', pluginId: 15).save();    // url: '/docDocumentTrash/showAudio'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-136', pluginId: 15).save();    // url: '/docDocumentTrash/showAudio'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-137', pluginId: 15).save();    // url: '/docDocumentTrash/listAudio'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-137', pluginId: 15).save();    // url: '/docDocumentTrash/listAudio'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-144', pluginId: 15).save();    // url: '/docDocumentTrash/restoreAudio'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-144', pluginId: 15).save();    // url: '/docDocumentTrash/restoreAudio'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-138', pluginId: 15).save();    // url: '/docDocumentTrash/showVideo'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-138', pluginId: 15).save();    // url: '/docDocumentTrash/showVideo'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-139', pluginId: 15).save();    // url: '/docDocumentTrash/listVideo'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-139', pluginId: 15).save();    // url: '/docDocumentTrash/listVideo'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-145', pluginId: 15).save();    // url: '/docDocumentTrash/restoreVideo'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-145', pluginId: 15).save();    // url: '/docDocumentTrash/restoreVideo'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-163', pluginId: 15).save();    // url: '/docDocumentTrash/showImage'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-163', pluginId: 15).save();    // url: '/docDocumentTrash/showImage'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-164', pluginId: 15).save();    // url: '/docDocumentTrash/listImage'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-164', pluginId: 15).save();    // url: '/docDocumentTrash/listImage'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-166', pluginId: 15).save();    // url: '/docDocumentTrash/restoreImage'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-166', pluginId: 15).save();    // url: '/docDocumentTrash/restoreImage'

            //DocQuestion
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-89', pluginId: 15).save();    // url: '/docQuestion/show'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-89', pluginId: 15).save();    // url: '/docQuestion/show'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-187', pluginId: 15).save();    // url: '/docQuestion/showForLanding'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-187', pluginId: 15).save();    // url: '/docQuestion/showForLanding'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-90', pluginId: 15).save();    // url: '/docQuestion/list'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-90', pluginId: 15).save();    // url: '/docQuestion/list'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-91', pluginId: 15).save();    // url: '/docQuestion/create'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-91', pluginId: 15).save();    // url: '/docQuestion/create'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-92', pluginId: 15).save();    // url: '/docQuestion/update'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-92', pluginId: 15).save();    // url: '/docQuestion/update'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-93', pluginId: 15).save();    // url: '/docQuestion/delete'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-93', pluginId: 15).save();    // url: '/docQuestion/delete'

            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'DOC-207', pluginId: 15).save();    // url: '/docQuestion/showPopUpQuiz'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-207', pluginId: 15).save();    // url: '/docQuestion/showPopUpQuiz'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-207', pluginId: 15).save();    // url: '/docQuestion/showPopUpQuiz'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'DOC-207', pluginId: 15).save();    // url: '/docQuestion/showPopUpQuiz'

            //DocAnswer
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-94', pluginId: 15).save();    // url: '/docAnswer/show'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-94', pluginId: 15).save();    // url: '/docAnswer/show'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-95', pluginId: 15).save();    // url: '/docAnswer/list'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-95', pluginId: 15).save();    // url: '/docAnswer/list'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-96', pluginId: 15).save();    // url: '/docAnswer/create'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-96', pluginId: 15).save();    // url: '/docAnswer/create'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-97', pluginId: 15).save();    // url: '/docAnswer/update'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-97', pluginId: 15).save();    // url: '/docAnswer/update'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-98', pluginId: 15).save();    // url: '/docAnswer/delete'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-98', pluginId: 15).save();    // url: '/docAnswer/delete'

            //DocReport
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-99', pluginId: 15).save();    // url: '/docReport/downloadQuestion'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-99', pluginId: 15).save();    // url: '/docReport/downloadQuestion'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-115', pluginId: 15).save();    // url: '/docReport/downloadAnswer'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-115', pluginId: 15).save();    // url: '/docReport/downloadAnswer'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-116', pluginId: 15).save();    // url: '/docReport/downloadQuestionSet'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-116', pluginId: 15).save();    // url: '/docReport/downloadQuestionSet'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-117', pluginId: 15).save();    // url: '/docReport/downloadAnswerSet'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-117', pluginId: 15).save();    // url: '/docReport/downloadAnswerSet'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-100', pluginId: 15).save();    // url: '/docReport/downloadArticle'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-100', pluginId: 15).save();    // url: '/docReport/downloadArticle'

            //DocQuestionSet
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-106', pluginId: 15).save();    // url: '/docQuestionSet/show'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-106', pluginId: 15).save();    // url: '/docQuestionSet/show'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-107', pluginId: 15).save();    // url: '/docQuestionSet/list'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-107', pluginId: 15).save();    // url: '/docQuestionSet/list'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-108', pluginId: 15).save();    // url: '/docQuestionSet/create'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-108', pluginId: 15).save();    // url: '/docQuestionSet/create'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-109', pluginId: 15).save();    // url: '/docQuestionSet/update'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-109', pluginId: 15).save();    // url: '/docQuestionSet/update'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-110', pluginId: 15).save();    // url: '/docQuestionSet/delete'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-110', pluginId: 15).save();    // url: '/docQuestionSet/delete'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-111', pluginId: 15).save();    // url: '/docQuestionSet/generateQuestionSet'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-111', pluginId: 15).save();    // url: '/docQuestionSet/generateQuestionSet'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-112', pluginId: 15).save();    // url: '/docQuestionSet/clearQuestionSet'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-112', pluginId: 15).save();    // url: '/docQuestionSet/clearQuestionSet'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-168', pluginId: 15).save();    // url: '/docQuestionSet/updateExamTime'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-168', pluginId: 15).save();    // url: '/docQuestionSet/updateExamTime'

            //DocQuestionSetDetails
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-113', pluginId: 15).save();    // url: '/docQuestionSetDetails/show'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-113', pluginId: 15).save();    // url: '/docQuestionSetDetails/show'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-114', pluginId: 15).save();    // url: '/docQuestionSetDetails/list'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-114', pluginId: 15).save();    // url: '/docQuestionSetDetails/list'

            //DocQuestionSetDifficulty
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-118', pluginId: 15).save();    // url: '/docQuestionSetDifficulty/show'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-118', pluginId: 15).save();    // url: '/docQuestionSetDifficulty/show'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-119', pluginId: 15).save();    // url: '/docQuestionSetDifficulty/list'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-119', pluginId: 15).save();    // url: '/docQuestionSetDifficulty/list'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-120', pluginId: 15).save();    // url: '/docQuestionSetDifficulty/create'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-120', pluginId: 15).save();    // url: '/docQuestionSetDifficulty/create'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-121', pluginId: 15).save();    // url: '/docQuestionSetDifficulty/update'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-121', pluginId: 15).save();    // url: '/docQuestionSetDifficulty/update'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-122', pluginId: 15).save();    // url: '/docQuestionSetDifficulty/delete'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-122', pluginId: 15).save();    // url: '/docQuestionSetDifficulty/delete'

            // doc faq
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-208', pluginId: 15).save();     // url: '/docFaq/show'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-210', pluginId: 15).save();     // url: '/docFaq/list'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-211', pluginId: 15).save();     // url: '/docFaq/create'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-212', pluginId: 15).save();     // url: '/docFaq/update'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-213', pluginId: 15).save();     // url: '/docFaq/delete'

            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-208', pluginId: 15).save();     // url: '/docFaq/show'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-210', pluginId: 15).save();     // url: '/docFaq/list'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-211', pluginId: 15).save();     // url: '/docFaq/create'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-212', pluginId: 15).save();     // url: '/docFaq/update'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-213', pluginId: 15).save();     // url: '/docFaq/delete'

            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'APP-441', pluginId: 15).save();      // url: '/appFaq/list'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'DOC-215', pluginId: 15).save();      // url: '/docFaq/showFaqForCourse'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-215', pluginId: 15).save();      // url: '/docFaq/showFaqForCourse'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-215', pluginId: 15).save();      // url: '/docFaq/showFaqForCourse'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'DOC-215', pluginId: 15).save();      // url: '/docFaq/showFaqForCourse'

            // ElAssignment
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-30', pluginId: 15).save();      // url: '/elAssignment/show'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-30', pluginId: 15).save();      // url: '/elAssignment/show'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-30', pluginId: 15).save();      // url: '/elAssignment/show'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-31', pluginId: 15).save();      // url: '/elAssignment/list'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-31', pluginId: 15).save();      // url: '/elAssignment/list'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-31', pluginId: 15).save();      // url: '/elAssignment/list'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-32', pluginId: 15).save();      // url: '/elAssignment/create'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-32', pluginId: 15).save();      // url: '/elAssignment/create'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-32', pluginId: 15).save();      // url: '/elAssignment/create'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-33', pluginId: 15).save();      // url: '/elAssignment/update'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-33', pluginId: 15).save();      // url: '/elAssignment/update'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-33', pluginId: 15).save();      // url: '/elAssignment/update'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-34', pluginId: 15).save();      // url: '/elAssignment/delete'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-34', pluginId: 15).save();      // url: '/elAssignment/delete'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-34', pluginId: 15).save();      // url: '/elAssignment/delete'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-68', pluginId: 15).save();      // url: '/elAssignment/reloadAssignment'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-68', pluginId: 15).save();      // url: '/elAssignment/reloadAssignment'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-68', pluginId: 15).save();      // url: '/elAssignment/reloadAssignment'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-68', pluginId: 15).save();      // url: '/elAssignment/reloadAssignment'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-70', pluginId: 15).save();      // url: '/elAssignment/uploadAssignment'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-96', pluginId: 15).save();      // url: '/elAssignment/showForStudent'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-103', pluginId: 15).save();      // url: '/elAssignment/sendNotification'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-103', pluginId: 15).save();      // url: '/elAssignment/sendNotification'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-103', pluginId: 15).save();      // url: '/elAssignment/sendNotification'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-104', pluginId: 15).save();      // url: '/elAssignment/showActivity'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-104', pluginId: 15).save();      // url: '/elAssignment/showActivity'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-104', pluginId: 15).save();      // url: '/elAssignment/showActivity'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-104', pluginId: 15).save();      // url: '/elAssignment/showActivity'

            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-140', pluginId: 15).save();      // url: '/elAssignment/reSubmit'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-140', pluginId: 15).save();      // url: '/elAssignment/reSubmit'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-140', pluginId: 15).save();      // url: '/elAssignment/reSubmit'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-140', pluginId: 15).save();      // url: '/elAssignment/reSubmit'

            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-153', pluginId: 15).save();      // url: '/elAssignment/showSubmitUnSubmitAssignment'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-153', pluginId: 15).save();      // url: '/elAssignment/showSubmitUnSubmitAssignment'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-153', pluginId: 15).save();      // url: '/elAssignment/showSubmitUnSubmitAssignment'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-153', pluginId: 15).save();      // url: '/elAssignment/showSubmitUnSubmitAssignment'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-153', pluginId: 15).save();      // url: '/elAssignment/showSubmitUnSubmitAssignment'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-154', pluginId: 15).save();      // url: '/elAssignment/listSubmitUnSubmitAssignment'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-154', pluginId: 15).save();      // url: '/elAssignment/listSubmitUnSubmitAssignment'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-154', pluginId: 15).save();      // url: '/elAssignment/listSubmitUnSubmitAssignment'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-154', pluginId: 15).save();      // url: '/elAssignment/listSubmitUnSubmitAssignment'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-154', pluginId: 15).save();      // url: '/elAssignment/listSubmitUnSubmitAssignment'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-159', pluginId: 15).save();      // url: '/elAssignmentContent/showParticipateActivity'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-159', pluginId: 15).save();      // url: '/elAssignmentContent/showParticipateActivity'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-159', pluginId: 15).save();      // url: '/elAssignmentContent/showParticipateActivity'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-159', pluginId: 15).save();      // url: '/elAssignmentContent/showParticipateActivity'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-159', pluginId: 15).save();      // url: '/elAssignmentContent/showParticipateActivity'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-160', pluginId: 15).save();      // url: '/elAssignmentContent/listParticipateActivity'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-160', pluginId: 15).save();      // url: '/elAssignmentContent/listParticipateActivity'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-160', pluginId: 15).save();      // url: '/elAssignmentContent/listParticipateActivity'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-160', pluginId: 15).save();      // url: '/elAssignmentContent/listParticipateActivity'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-160', pluginId: 15).save();      // url: '/elAssignmentContent/listParticipateActivity'

            // ElAssignmentUserMapping
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-62', pluginId: 15).save();      // url: '/elAssignmentUser/show'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-62', pluginId: 15).save();      // url: '/elAssignmentUser/show'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-62', pluginId: 15).save();      // url: '/elAssignmentUser/show'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-63', pluginId: 15).save();      // url: '/elAssignmentUser/list'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-63', pluginId: 15).save();      // url: '/elAssignmentUser/list'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-63', pluginId: 15).save();      // url: '/elAssignmentUser/list'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-64', pluginId: 15).save();      // url: '/elAssignmentUser/create'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-64', pluginId: 15).save();      // url: '/elAssignmentUser/create'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-64', pluginId: 15).save();      // url: '/elAssignmentUser/create'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-65', pluginId: 15).save();      // url: '/elAssignmentUser/update'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-65', pluginId: 15).save();      // url: '/elAssignmentUser/update'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-65', pluginId: 15).save();      // url: '/elAssignmentUser/update'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-66', pluginId: 15).save();      // url: '/elAssignmentUser/delete'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-66', pluginId: 15).save();      // url: '/elAssignmentUser/delete'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-66', pluginId: 15).save();      // url: '/elAssignmentUser/delete'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-67', pluginId: 15).save();      // url: '/elAssignmentUser/dropDownAssignmentUserReload'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-67', pluginId: 15).save();      // url: '/elAssignmentUser/dropDownAssignmentUserReload'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-67', pluginId: 15).save();      // url: '/elAssignmentUser/dropDownAssignmentUserReload'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-73', pluginId: 15).save();      // url: '/elAssignmentUser/listForTeacher'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-73', pluginId: 15).save();      // url: '/elAssignmentUser/listForTeacher'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-73', pluginId: 15).save();      // url: '/elAssignmentUser/listForTeacher'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-74', pluginId: 15).save();      // url: '/elAssignmentUser/assignMark'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-74', pluginId: 15).save();      // url: '/elAssignmentUser/assignMark'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-74', pluginId: 15).save();      // url: '/elAssignmentUser/assignMark'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-75', pluginId: 15).save();      // url: '/elAssignmentUser/downloadAssignment'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-75', pluginId: 15).save();      // url: '/elAssignmentUser/downloadAssignment'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-75', pluginId: 15).save();      // url: '/elAssignmentUser/downloadAssignment'

            // ElExamQuestionMapping
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-54', pluginId: 15).save();      // url: '/elExamQuestionMapping/show'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-54', pluginId: 15).save();      // url: '/elExamQuestionMapping/show'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-54', pluginId: 15).save();      // url: '/elExamQuestionMapping/show'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-55', pluginId: 15).save();      // url: '/elExamQuestionMapping/list'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-55', pluginId: 15).save();      // url: '/elExamQuestionMapping/list'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-55', pluginId: 15).save();      // url: '/elExamQuestionMapping/list'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-56', pluginId: 15).save();      // url: '/elExamQuestionMapping/create'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-56', pluginId: 15).save();      // url: '/elExamQuestionMapping/create'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-56', pluginId: 15).save();      // url: '/elExamQuestionMapping/create'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-57', pluginId: 15).save();      // url: '/elExamQuestionMapping/delete'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-57', pluginId: 15).save();      // url: '/elExamQuestionMapping/delete'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-57', pluginId: 15).save();      // url: '/elExamQuestionMapping/delete'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-58', pluginId: 15).save();      // url: '/elExamQuestionMapping/listQuestionByLesson'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-58', pluginId: 15).save();      // url: '/elExamQuestionMapping/listQuestionByLesson'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-58', pluginId: 15).save();      // url: '/elExamQuestionMapping/listQuestionByLesson'

            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'DOC-220', pluginId: 15).save();      // url: '/docDiscussion/listViewDocDiscussion'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'DOC-220', pluginId: 15).save();      // url: '/docDiscussion/listViewDocDiscussion'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-220', pluginId: 15).save();      // url: '/docDiscussion/listViewDocDiscussion'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-220', pluginId: 15).save();      // url: '/docDiscussion/listViewDocDiscussion'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'DOC-220', pluginId: 15).save();      // url: '/docDiscussion/listViewDocDiscussion'

            // ElUserQuiz
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-107', pluginId: 15).save();      // url: '/elUserQuiz/listForTeacher'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-107', pluginId: 15).save();      // url: '/elUserQuiz/listForTeacher'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-107', pluginId: 15).save();      // url: '/elUserQuiz/listForTeacher'

            // ELUserExam
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-108', pluginId: 15).save();      // url: '/elUserExam/listForTeacher'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-108', pluginId: 15).save();      // url: '/elUserExam/listForTeacher'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-108', pluginId: 15).save();      // url: '/elUserExam/listForTeacher'

            //elBlog
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-115', pluginId: 15).save();    // url: '/elBlog/show'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-115', pluginId: 15).save();    // url: '/elBlog/show'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-115', pluginId: 15).save();    // url: '/elBlog/show'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-116', pluginId: 15).save();    // url: '/elBlog/list'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-116', pluginId: 15).save();    // url: '/elBlog/list'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-116', pluginId: 15).save();    // url: '/elBlog/list'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-116', pluginId: 15).save();    // url: '/elBlog/list'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-116', pluginId: 15).save();    // url: '/elBlog/list'

            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'APP-453', pluginId: 15).save();    // url: '/appPage/create'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'APP-453', pluginId: 15).save();    // url: '/appPage/create'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-453', pluginId: 15).save();    // url: '/appPage/create'

            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'APP-454', pluginId: 15).save();    // url: '/appPage/update'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'APP-454', pluginId: 15).save();    // url: '/appPage/update'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-454', pluginId: 15).save();    // url: '/appPage/update'

            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-195', pluginId: 15).save();    // url: '/appPage/create'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-195', pluginId: 15).save();    // url: '/appPage/create'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-195', pluginId: 15).save();    // url: '/appPage/create'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-195', pluginId: 15).save();    // url: '/appPage/create'

            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-196', pluginId: 15).save();    // url: '/elBlog/update'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-196', pluginId: 15).save();    // url: '/elBlog/update'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-196', pluginId: 15).save();    // url: '/elBlog/update'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-196', pluginId: 15).save();    // url: '/elBlog/update'

            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'APP-455', pluginId: 15).save();    // url: '/elBlog/delete'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-120', pluginId: 15).save();    // url: '/elBlog/select'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-120', pluginId: 15).save();    // url: '/elBlog/select'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-120', pluginId: 15).save();    // url: '/elBlog/select'

            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-117', pluginId: 15).save();    // url: '/elBlog/showBlogByLesson'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-117', pluginId: 15).save();    // url: '/elBlog/showBlogByLesson'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-117', pluginId: 15).save();    // url: '/elBlog/showBlogByLesson'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-117', pluginId: 15).save();    // url: '/elBlog/showBlogByLesson'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-117', pluginId: 15).save();    // url: '/elBlog/showBlogByLesson'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-118', pluginId: 15).save();    // url: '/elBlog/listBlogByLesson'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-118', pluginId: 15).save();    // url: '/elBlog/listBlogByLesson'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-118', pluginId: 15).save();    // url: '/elBlog/listBlogByLesson'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-118', pluginId: 15).save();    // url: '/elBlog/listBlogByLesson'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-118', pluginId: 15).save();    // url: '/elBlog/listBlogByLesson'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-119', pluginId: 15).save();    // url: '/elBlog/viewBlog'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-119', pluginId: 15).save();    // url: '/elBlog/viewBlog'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-119', pluginId: 15).save();    // url: '/elBlog/viewBlog'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-119', pluginId: 15).save();    // url: '/elBlog/viewBlog'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-119', pluginId: 15).save();    // url: '/elBlog/viewBlog'


            // App Note
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'APP-252', pluginId: 15).save();    // url: '/appNote/reloadEntityNote'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-252', pluginId: 15).save();    // url: '/appNote/reloadEntityNote'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'APP-252', pluginId: 15).save();    // url: '/appNote/reloadEntityNote'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'APP-249', pluginId: 15).save();    // url: '/appNote/create'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'APP-249', pluginId: 15).save();    // url: '/appNote/create'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'APP-249', pluginId: 15).save();    // url: '/appNote/create'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-249', pluginId: 15).save();    // url: '/appNote/create'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'APP-249', pluginId: 15).save();    // url: '/appNote/create'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'APP-247', pluginId: 15).save();    // url: '/appNote/list'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'APP-247', pluginId: 15).save();    // url: '/appNote/list'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-247', pluginId: 15).save();    // url: '/appNote/list'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'APP-247', pluginId: 15).save();    // url: '/appNote/list'

            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'APP-345', pluginId: 15).save();      // url: '/appNote/listEntityNote'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'APP-345', pluginId: 15).save();      // url: '/appNote/listEntityNote'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'APP-345', pluginId: 15).save();      // url: '/appNote/listEntityNote'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'APP-345', pluginId: 15).save();      // url: '/appNote/listEntityNote'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'APP-345', pluginId: 15).save();      // url: '/appNote/listEntityNote'

            // ElForum
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-121', pluginId: 15).save();    // url: '/elForum/show'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-121', pluginId: 15).save();    // url: '/elForum/show'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-121', pluginId: 15).save();    // url: '/elForum/show'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-122', pluginId: 15).save();    // url: '/elForum/list'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-122', pluginId: 15).save();    // url: '/elForum/list'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-122', pluginId: 15).save();    // url: '/elForum/list'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-122', pluginId: 15).save();    // url: '/elForum/list'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-122', pluginId: 15).save();    // url: '/elForum/list'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-132', pluginId: 15).save();    // url: '/elForum/showForumByLesson'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-132', pluginId: 15).save();    // url: '/elForum/showForumByLesson'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-132', pluginId: 15).save();    // url: '/elForum/showForumByLesson'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-132', pluginId: 15).save();    // url: '/elForum/showForumByLesson'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-132', pluginId: 15).save();    // url: '/elForum/showForumByLesson'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-123', pluginId: 15).save();    // url: '/elForum/create'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-123', pluginId: 15).save();    // url: '/elForum/create'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-123', pluginId: 15).save();    // url: '/elForum/create'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-124', pluginId: 15).save();    // url: '/elForum/update'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-124', pluginId: 15).save();    // url: '/elForum/update'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-124', pluginId: 15).save();    // url: '/elForum/update'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-125', pluginId: 15).save();    // url: '/elForum/delete'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-125', pluginId: 15).save();    // url: '/elForum/delete'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-125', pluginId: 15).save();    // url: '/elForum/delete'

            // elPost
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-133', pluginId: 15).save();    // url: '/elPost/show'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-133', pluginId: 15).save();    // url: '/elPost/show'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-133', pluginId: 15).save();    // url: '/elPost/show'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-134', pluginId: 15).save();    // url: '/elPost/list'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-134', pluginId: 15).save();    // url: '/elPost/list'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-134', pluginId: 15).save();    // url: '/elPost/list'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-134', pluginId: 15).save();    // url: '/elPost/list'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-134', pluginId: 15).save();    // url: '/elPost/list'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-135', pluginId: 15).save();    // url: '/elPost/showPostByLesson'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-135', pluginId: 15).save();    // url: '/elPost/showPostByLesson'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-135', pluginId: 15).save();    // url: '/elPost/showPostByLesson'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-135', pluginId: 15).save();    // url: '/elPost/showPostByLesson'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-135', pluginId: 15).save();    // url: '/elPost/showPostByLesson'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-136', pluginId: 15).save();    // url: '/elBlog/listBlogByLesson'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-136', pluginId: 15).save();    // url: '/elBlog/listBlogByLesson'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-136', pluginId: 15).save();    // url: '/elBlog/listBlogByLesson'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-136', pluginId: 15).save();    // url: '/elBlog/listBlogByLesson'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-136', pluginId: 15).save();    // url: '/elPost/listBlogByLesson'
            new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-137', pluginId: 15).save();    // url: '/elPost/viewPost'
            new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-137', pluginId: 15).save();    // url: '/elPost/viewPost'
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-137', pluginId: 15).save();    // url: '/elPost/viewPost'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-137', pluginId: 15).save();    // url: '/elPost/viewPost'
            new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-137', pluginId: 15).save();    // url: '/elPost/viewPost'

            // list doc document for modal
            new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'DOC-218', pluginId: 15).save(); // url: '/docDocumentVersion/listForModal'
            new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'DOC-218', pluginId: 15).save(); // url: '/docDocumentVersion/listForModal'
            elExamRoleFeatureMapping()
            elRoutineAndDetailsRoleFeatureMapping()
            elAnnouncementRoleFeatureMapping()
            elReportRoleFeatureMapping()
            return true
        } catch (Exception ex) {
            log.error(ex.message)
            throw new RuntimeException(ex)
        }
    }
    // ElReport
    private void elReportRoleFeatureMapping(){
        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-69', pluginId: 15).save();    // url: '/elReport/downloadMyAssignment'
        new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-69', pluginId: 15).save();    // url: '/elReport/downloadMyAssignment'
        new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-69', pluginId: 15).save();    // url: '/elReport/downloadMyAssignment'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-69', pluginId: 15).save();    // url: '/elReport/downloadMyAssignment'
        new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-69', pluginId: 15).save();    // url: '/elReport/downloadMyAssignment'
        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-94', pluginId: 15).save();    // url: '/elReport/downloadMyExam'
        new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-94', pluginId: 15).save();    // url: '/elReport/downloadMyExam'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-94', pluginId: 15).save();    // url: '/elReport/downloadMyExam'
        new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-94', pluginId: 15).save();    // url: '/elReport/downloadMyExam'
        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-95', pluginId: 15).save();    // url: '/elReport/downloadMyQuiz'
        new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-95', pluginId: 15).save();    // url: '/elReport/downloadMyQuiz'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-95', pluginId: 15).save();    // url: '/elReport/downloadMyQuiz'
        new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-95', pluginId: 15).save();    // url: '/elReport/downloadMyQuiz'
        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-101', pluginId: 15).save();    // url: '/elReport/downloadMyCertificate'
        new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-101', pluginId: 15).save();    // url: '/elReport/downloadMyCertificate'
        new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-101', pluginId: 15).save();    // url: '/elReport/downloadMyCertificate'
        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-167', pluginId: 15).save();    // url: '/elReport/downloadMyCertificate'
        new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-167', pluginId: 15).save();    // url: '/elReport/downloadMyCertificate'
        new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-167', pluginId: 15).save();    // url: '/elReport/downloadMyCertificate'
        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-102', pluginId: 15).save();    // url: '/elReport/downloadMyMarkSheet'
        new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-102', pluginId: 15).save();    // url: '/elReport/downloadMyMarkSheet'
        new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-102', pluginId: 15).save();    // url: '/elReport/downloadMyMarkSheet'

        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-110', pluginId: 15).save();    // url: '/elReport/downloadQuizReportOfStudent'
        new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-110', pluginId: 15).save();    // url: '/elReport/downloadQuizReportOfStudent'
        new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-110', pluginId: 15).save();    // url: '/elReport/downloadQuizReportOfStudent'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-110', pluginId: 15).save();    // url: '/elReport/downloadQuizReportOfStudent'
        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-139', pluginId: 15).save();    // url: '/elReport/downloadResult'
        new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-139', pluginId: 15).save();    // url: '/elReport/downloadResult'
        new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-139', pluginId: 15).save();    // url: '/elReport/downloadResult'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-139', pluginId: 15).save();    // url: '/elReport/downloadResult'
        new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-139', pluginId: 15).save();    // url: '/elReport/downloadResult'

        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-161', pluginId: 15).save();    // url: '/elReport/downloadExamReportOfStudent'
        new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-161', pluginId: 15).save();    // url: '/elReport/downloadExamReportOfStudent'
        new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-161', pluginId: 15).save();    // url: '/elReport/downloadExamReportOfStudent'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-161', pluginId: 15).save();    // url: '/elReport/downloadExamReportOfStudent'

        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-165', pluginId: 15).save();    // url: '/elReport/downloadCourseStudentSummary'
        new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-165', pluginId: 15).save();    // url: '/elReport/downloadCourseStudentSummary'
        new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-165', pluginId: 15).save();    // url: '/elReport/downloadCourseStudentSummary'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-165', pluginId: 15).save();    // url: '/elReport/downloadCourseStudentSummary'

        // myRoutine
        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-189', pluginId: 15).save();    // url: '/elReport/myRoutine'
        new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-189', pluginId: 15).save();    // url: '/elReport/myRoutine'
        new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-189', pluginId: 15).save();    // url: '/elReport/myRoutine'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-189', pluginId: 15).save();    // url: '/elReport/myRoutine'
        // courseRoutine
        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-190', pluginId: 15).save();    // url: '/elReport/courseRoutine'
        new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-190', pluginId: 15).save();    // url: '/elReport/courseRoutine'
        new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-190', pluginId: 15).save();    // url: '/elReport/courseRoutine'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-190', pluginId: 15).save();    // url: '/elReport/courseRoutine'
        new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-190', pluginId: 15).save();    // url: '/elReport/courseRoutine'

    }

    private void elExamRoleFeatureMapping(){
        // ElExam
        new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-45', pluginId: 15).save();    // url: '/elExam/show'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-45', pluginId: 15).save();    // url: '/elExam/show'
        new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-46', pluginId: 15).save();    // url: '/elExam/list'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-46', pluginId: 15).save();    // url: '/elExam/list'
        new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-47', pluginId: 15).save();    // url: '/elExam/create'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-47', pluginId: 15).save();    // url: '/elExam/create'
        new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-48', pluginId: 15).save();    // url: '/elExam/update'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-48', pluginId: 15).save();    // url: '/elExam/update'
        new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-52', pluginId: 15).save();    // url: '/elExam/updateConfiguration'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-52', pluginId: 15).save();    // url: '/elExam/updateConfiguration'
        new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-49', pluginId: 15).save();    // url: '/elExam/delete'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-49', pluginId: 15).save();    // url: '/elExam/delete'
        new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-50', pluginId: 15).save();    // url: '/elExam/showConfiguration'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-50', pluginId: 15).save();    // url: '/elExam/showConfiguration'
        new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-51', pluginId: 15).save();    // url: '/elExam/select'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-51', pluginId: 15).save();    // url: '/elExam/select'
        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-155', pluginId: 15).save();    // url: '/elExam/showParticipateExam'
        new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-155', pluginId: 15).save();    // url: '/elExam/showParticipateExam'
        new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-155', pluginId: 15).save();    // url: '/elExam/showParticipateExam'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-155', pluginId: 15).save();    // url: '/elExam/showParticipateExam'
        new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-155', pluginId: 15).save();    // url: '/elExam/showParticipateExam'
        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-156', pluginId: 15).save();    // url: '/elExam/listParticipateExam'
        new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-156', pluginId: 15).save();    // url: '/elExam/listParticipateExam'
        new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-156', pluginId: 15).save();    // url: '/elExam/listParticipateExam'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-156', pluginId: 15).save();    // url: '/elExam/listParticipateExam'
        new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-156', pluginId: 15).save();    // url: '/elExam/listParticipateExam'

        new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-177', pluginId: 15).save();    // url: '/elExam/showAllQuestionForExam'
        new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-178', pluginId: 15).save();    // url: '/elExam/submitExamForAllQuestion'
        new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-179', pluginId: 15).save();    // url: '/elExam/showAllQuestionForExamWithAnswer'

        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-45', pluginId: 15).save();      // url: '/elExam/show'
        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-46', pluginId: 15).save();      // url: '/elExam/list'
        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-47', pluginId: 15).save();      // url: '/elExam/create'
        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-48', pluginId: 15).save();      // url: '/elExam/update'
        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-52', pluginId: 15).save();      // url: '/elExam/updateConfiguration'
        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-49', pluginId: 15).save();      // url: '/elExam/delete'
        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-50', pluginId: 15).save();      // url: '/elExam/showConfiguration'
        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-51', pluginId: 15).save();      // url: '/elExam/select'

        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-72', pluginId: 15).save();      // url: '/elExam/showExam'
        new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-72', pluginId: 15).save();      // url: '/elExam/showExam'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-72', pluginId: 15).save();      // url: '/elExam/showExam'
        new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-72', pluginId: 15).save();      // url: '/elExam/showExam'

        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-98', pluginId: 15).save();      // url: '/elExam/listQuestionForDropDown'
        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-99', pluginId: 15).save();      // url: '/elExam/showQuestionWithAnswer'
        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-100', pluginId: 15).save();      // url: '/elExam/submitExam'

        new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-98', pluginId: 15).save();      // url: '/elExam/listQuestionForDropDown'
        new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-99', pluginId: 15).save();      // url: '/elExam/showQuestionWithAnswer'
        new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-100', pluginId: 15).save();      // url: '/elExam/submitExam'

        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-98', pluginId: 15).save();      // url: '/elExam/listQuestionForDropDown'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-99', pluginId: 15).save();      // url: '/elExam/showQuestionWithAnswer'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-100', pluginId: 15).save();      // url: '/elExam/submitExam'

        new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-98', pluginId: 15).save();      // url: '/elExam/listQuestionForDropDown'
        new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-99', pluginId: 15).save();      // url: '/elExam/showQuestionWithAnswer'
        new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-100', pluginId: 15).save();      // url: '/elExam/submitExam'

        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-185', pluginId: 15).save();      // url: '/elExam/sendNotification'
        new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-185', pluginId: 15).save();      // url: '/elExam/sendNotification'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-185', pluginId: 15).save();      // url: '/elExam/sendNotification'
    }

    private void elAnnouncementRoleFeatureMapping(){
        // ElAnnouncement
        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-142', pluginId: 15).save();    // url: '/elAnnouncement/show'
        new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-142', pluginId: 15).save();    // url: '/elAnnouncement/show'
        new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-142', pluginId: 15).save();    // url: '/elAnnouncement/show'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-142', pluginId: 15).save();    // url: '/elAnnouncement/show'
        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-143', pluginId: 15).save();    // url: '/elAnnouncement/list'
        new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-143', pluginId: 15).save();    // url: '/elAnnouncement/list'
        new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-143', pluginId: 15).save();    // url: '/elAnnouncement/list'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-143', pluginId: 15).save();    // url: '/elAnnouncement/list'
        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-144', pluginId: 15).save();    // url: '/elAnnouncement/create'
        new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-144', pluginId: 15).save();    // url: '/elAnnouncement/create'
        new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-144', pluginId: 15).save();    // url: '/elAnnouncement/create'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-144', pluginId: 15).save();    // url: '/elAnnouncement/create'
        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-145', pluginId: 15).save();    // url: '/elAnnouncement/update'
        new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-145', pluginId: 15).save();    // url: '/elAnnouncement/update'
        new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-145', pluginId: 15).save();    // url: '/elAnnouncement/update'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-145', pluginId: 15).save();    // url: '/elAnnouncement/update'
        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-146', pluginId: 15).save();    // url: '/elAnnouncement/delete'
        new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-146', pluginId: 15).save();    // url: '/elAnnouncement/delete'
        new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-146', pluginId: 15).save();    // url: '/elAnnouncement/delete'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-146', pluginId: 15).save();    // url: '/elAnnouncement/delete'
        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-147', pluginId: 15).save();    // url: '/elAnnouncement/send'
        new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-147', pluginId: 15).save();    // url: '/elAnnouncement/send'
        new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-147', pluginId: 15).save();    // url: '/elAnnouncement/send'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-147', pluginId: 15).save();    // url: '/elAnnouncement/send'
        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-148', pluginId: 15).save();    // url: '/elAnnouncement/showForReCompose'
        new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-148', pluginId: 15).save();    // url: '/elAnnouncement/showForReCompose'
        new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-148', pluginId: 15).save();    // url: '/elAnnouncement/showForReCompose'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-148', pluginId: 15).save();    // url: '/elAnnouncement/showForReCompose'
        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-149', pluginId: 15).save();    // url: '/elAnnouncement/listForSend'
        new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-149', pluginId: 15).save();    // url: '/elAnnouncement/listForSend'
        new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-149', pluginId: 15).save();    // url: '/elAnnouncement/listForSend'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-149', pluginId: 15).save();    // url: '/elAnnouncement/listForSend'
        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-150', pluginId: 15).save();    // url: '/elAnnouncement/reCompose'
        new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-150', pluginId: 15).save();    // url: '/elAnnouncement/reCompose'
        new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-150', pluginId: 15).save();    // url: '/elAnnouncement/reCompose'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-150', pluginId: 15).save();    // url: '/elAnnouncement/reCompose'

        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-170', pluginId: 15).save();    // url: '/elAnnouncement/showInbox'
        new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-170', pluginId: 15).save();    // url: '/elAnnouncement/showInbox'
        new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-170', pluginId: 15).save();    // url: '/elAnnouncement/showInbox'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-170', pluginId: 15).save();    // url: '/elAnnouncement/showInbox'
        new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-170', pluginId: 15).save();    // url: '/elAnnouncement/showInbox'

        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-171', pluginId: 15).save();    // url: '/elAnnouncement/listInbox'
        new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-171', pluginId: 15).save();    // url: '/elAnnouncement/listInbox'
        new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-171', pluginId: 15).save();    // url: '/elAnnouncement/listInbox'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-171', pluginId: 15).save();    // url: '/elAnnouncement/listInbox'
        new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-171', pluginId: 15).save();    // url: '/elAnnouncement/listInbox'

    }

    private void elRoutineAndDetailsRoleFeatureMapping(){
        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-172', pluginId: 15).save();    // url: '/elRoutine/show'
        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-173', pluginId: 15).save();    // url: '/elRoutine/list'
        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-174', pluginId: 15).save();    // url: '/elRoutine/create'
        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-175', pluginId: 15).save();    // url: '/elRoutine/update'
        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-176', pluginId: 15).save();    // url: '/elRoutine/delete'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-172', pluginId: 15).save();    // url: '/elRoutine/show'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-173', pluginId: 15).save();    // url: '/elRoutine/list'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-174', pluginId: 15).save();    // url: '/elRoutine/create'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-175', pluginId: 15).save();    // url: '/elRoutine/update'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-176', pluginId: 15).save();    // url: '/elRoutine/delete'

        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-187', pluginId: 15).save();    // url: '/elRoutine/showRoutine'
        new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-187', pluginId: 15).save();    // url: '/elRoutine/showRoutine'
        new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-187', pluginId: 15).save();    // url: '/elRoutine/showRoutine'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-187', pluginId: 15).save();    // url: '/elRoutine/showRoutine'
        new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-187', pluginId: 15).save();    // url: '/elRoutine/showRoutine'

        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-188', pluginId: 15).save();    // url: '/elRoutine/listRoutine'
        new RoleFeatureMapping(roleTypeId: -802, transactionCode: 'EL-188', pluginId: 15).save();    // url: '/elRoutine/listRoutine'
        new RoleFeatureMapping(roleTypeId: -803, transactionCode: 'EL-188', pluginId: 15).save();    // url: '/elRoutine/listRoutine'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-188', pluginId: 15).save();    // url: '/elRoutine/listRoutine'
        new RoleFeatureMapping(roleTypeId: -805, transactionCode: 'EL-188', pluginId: 15).save();    // url: '/elRoutine/listRoutine'

        // ElRoutineDetails
        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-180', pluginId: 15).save();    // url: '/elRoutineDetails/show'
        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-181', pluginId: 15).save();    // url: '/elRoutineDetails/list'
        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-182', pluginId: 15).save();    // url: '/elRoutineDetails/create'
        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-183', pluginId: 15).save();    // url: '/elRoutineDetails/update'
        new RoleFeatureMapping(roleTypeId: -801, transactionCode: 'EL-184', pluginId: 15).save();    // url: '/elRoutineDetails/delete'

        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-180', pluginId: 15).save();    // url: '/elRoutineDetails/show'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-181', pluginId: 15).save();    // url: '/elRoutineDetails/list'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-182', pluginId: 15).save();    // url: '/elRoutineDetails/create'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-183', pluginId: 15).save();    // url: '/elRoutineDetails/update'
        new RoleFeatureMapping(roleTypeId: -804, transactionCode: 'EL-184', pluginId: 15).save();    // url: '/elRoutineDetails/delete'

    }

    public boolean createRoleFeatureMapForDocumentPlugin() {
        try {

            // for appMail
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'APP-187', pluginId: 13).save(); // url: '/appMail/show'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'APP-188', pluginId: 13).save(); // url: '/appMail/update'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'APP-189', pluginId: 13).save(); // url: '/appMail/list'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'APP-251', pluginId: 13).save(); // url: '/appMail/testAppMail'
            // for appShellScript
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'APP-253', pluginId: 13).save(); // url: '/appShellScript/show'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'APP-254', pluginId: 13).save(); // url: '/appShellScript/create'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'APP-255', pluginId: 13).save(); // url: '/appShellScript/list'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'APP-257', pluginId: 13).save(); // url: '/appShellScript/update'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'APP-258', pluginId: 13).save(); // url: '/appShellScript/delete'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'APP-259', pluginId: 13).save(); // url: '/appShellScript/evaluate'
            // system config
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'APP-99', pluginId: 13).save(); // url: '/systemConfiguration/show'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'APP-100', pluginId: 13).save(); // url: '/systemConfiguration/list'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'APP-102', pluginId: 13).save(); // url: '/systemConfiguration/update'
            // for system entity role type -502
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'APP-103', pluginId: 13).save(); // url: '/systemEntity/show'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'APP-104', pluginId: 13).save(); // url: '/systemEntity/create'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'APP-105', pluginId: 13).save(); // url: '/systemEntity/list'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'APP-107', pluginId: 13).save(); // url: '/systemEntity/update'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'APP-173', pluginId: 13).save(); // url: '/systemEntity/delete'
            // system entity type
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'APP-108', pluginId: 13).save(); // url: '/systemEntityType/show'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'APP-109', pluginId: 13).save(); // url: '/systemEntityType/list'
            // for AppSchedule
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'APP-279', pluginId: 13).save(); //url: '/appSchedule/show'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'APP-280', pluginId: 13).save(); //url: '/appSchedule/list'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'APP-282', pluginId: 13).save(); //url: '/appSchedule/update'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'APP-283', pluginId: 13).save(); //url: '/appSchedule/testExecute'

            // appTheme
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'APP-124', pluginId: 13).save(); // url: '/appTheme/showTheme'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'APP-125', pluginId: 13).save(); // url: '/appTheme/updateTheme'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'APP-126', pluginId: 13).save(); // url: '/appTheme/listTheme'

            // document
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-1', pluginId: 13).save();      // url: '/document/renderDocumentMenu'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-1', pluginId: 13).save();      // url: '/document/renderDocumentMenu'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-1', pluginId: 13).save();     // url: '/document/renderDocumentMenu'

            // docCategory
//            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-2', pluginId: 13).save();      // url: '/docCategory/show'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-3', pluginId: 13).save();      // url: '/docCategory/list'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-4', pluginId: 13).save();      // url: '/docCategory/create'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-5', pluginId: 13).save();      // url: '/docCategory/update'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-6', pluginId: 13).save();      // url: '/docCategory/delete'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-7', pluginId: 13).save();      // url: '/docCategory/viewCategoryDetails'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-7', pluginId: 13).save();     // url: '/docCategory/viewCategoryDetails'
//            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-8', pluginId: 13).save();      // url: '/docCategory/showCategories'
//            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-8', pluginId: 13).save();     // url: '/docCategory/showCategories'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-2', pluginId: 13).save();      // url: '/docCategory/show'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-3', pluginId: 13).save();      // url: '/docCategory/list'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-4', pluginId: 13).save();      // url: '/docCategory/create'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-5', pluginId: 13).save();      // url: '/docCategory/update'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-6', pluginId: 13).save();      // url: '/docCategory/delete'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-7', pluginId: 13).save();      // url: '/docCategory/viewCategoryDetails'
//            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-8', pluginId: 13).save();      // url: '/docCategory/showCategories'

            // docSubCategory
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-12', pluginId: 13).save();     // url: '/docSubCategory/show'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-209', pluginId: 13).save();     // url: '/docSubCategory/listSubCategoryByCategory'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-13', pluginId: 13).save();     // url: '/docSubCategory/list'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-216', pluginId: 13).save();     // url: '/docSubCategory/reloadDocSubCategoryTreeList'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-14', pluginId: 13).save();     // url: '/docSubCategory/create'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-15', pluginId: 13).save();     // url: '/docSubCategory/update'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-16', pluginId: 13).save();     // url: '/docSubCategory/delete'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-17', pluginId: 13).save();     // url: '/docSubCategory/showSubCategories'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-17', pluginId: 13).save();    // url: '/docSubCategory/showMySubCategories'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-18', pluginId: 13).save();     // url: '/docSubCategory/viewSubCategoryDetails'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-18', pluginId: 13).save();    // url: '/docSubCategory/viewSubCategoryDetails'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-19', pluginId: 13).save();     // url: '/docSubCategory/dropDownSubCategoryReload'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-19', pluginId: 13).save();    // url: '/docSubCategory/dropDownSubCategoryReload'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-20', pluginId: 13).save();     // url: '/docSubCategory/addOrRemoveSubCategoryFavourite'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-20', pluginId: 13).save();    // url: '/docSubCategory/addOrRemoveSubCategoryFavourite'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-21', pluginId: 13).save();     // url: '/docSubCategory/listSubCategoryFavourite'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-21', pluginId: 13).save();    // url: '/docSubCategory/listSubCategoryFavourite'
            // for development user
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-12', pluginId: 13).save();     // url: '/docSubCategory/show'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-209', pluginId: 13).save();     // url: '/docSubCategory/listSubCategoryByCategory'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-13', pluginId: 13).save();     // url: '/docSubCategory/list'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-216', pluginId: 13).save();     // url: '/docSubCategory/reloadDocSubCategoryTreeList'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-14', pluginId: 13).save();     // url: '/docSubCategory/create'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-15', pluginId: 13).save();     // url: '/docSubCategory/update'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-16', pluginId: 13).save();     // url: '/docSubCategory/delete'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-17', pluginId: 13).save();     // url: '/docSubCategory/showSubCategories'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-18', pluginId: 13).save();     // url: '/docSubCategory/viewSubCategoryDetails'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-19', pluginId: 13).save();     // url: '/docSubCategory/dropDownSubCategoryReload'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-20', pluginId: 13).save();     // url: '/docSubCategory/addOrRemoveSubCategoryFavourite'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-21', pluginId: 13).save();     // url: '/docSubCategory/listSubCategoryFavourite'

            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-208', pluginId: 13).save();     // url: '/docFaq/show'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-208', pluginId: 13).save();     // url: '/docFaq/show'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-210', pluginId: 13).save();     // url: '/docFaq/list'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-210', pluginId: 13).save();     // url: '/docFaq/list'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-211', pluginId: 13).save();     // url: '/docFaq/create'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-211', pluginId: 13).save();     // url: '/docFaq/create'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-212', pluginId: 13).save();     // url: '/docFaq/update'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-212', pluginId: 13).save();     // url: '/docFaq/update'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-213', pluginId: 13).save();     // url: '/docFaq/delete'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-213', pluginId: 13).save();     // url: '/docFaq/delete'


                // docCategoryUserMapping
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-23', pluginId: 13).save();     // url: '/docCategoryUserMapping/show'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-24', pluginId: 13).save();     // url: '/docCategoryUserMapping/list'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-25', pluginId: 13).save();     // url: '/docCategoryUserMapping/create'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-26', pluginId: 13).save();     // url: '/docCategoryUserMapping/update'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-27', pluginId: 13).save();     // url: '/docCategoryUserMapping/delete'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-28', pluginId: 13).save();     // url: '/docCategoryUserMapping/dropDownAppUserForCategoryReload'
            // for development user
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-23', pluginId: 13).save();     // url: '/docCategoryUserMapping/show'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-24', pluginId: 13).save();     // url: '/docCategoryUserMapping/list'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-25', pluginId: 13).save();     // url: '/docCategoryUserMapping/create'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-26', pluginId: 13).save();     // url: '/docCategoryUserMapping/update'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-27', pluginId: 13).save();     // url: '/docCategoryUserMapping/delete'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-28', pluginId: 13).save();     // url: '/docCategoryUserMapping/dropDownAppUserForCategoryReload'


                // docSubCategoryUserMapping
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-29', pluginId: 13).save();     // url: '/docSubCategoryUserMapping/show'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-30', pluginId: 13).save();     // url: '/docSubCategoryUserMapping/list'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-31', pluginId: 13).save();     // url: '/docSubCategoryUserMapping/create'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-32', pluginId: 13).save();     // url: '/docSubCategoryUserMapping/update'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-33', pluginId: 13).save();     // url: '/docSubCategoryUserMapping/delete'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-34', pluginId: 13).save();     // url: '/docAllCategoryUserMapping/dropDownAppUserForSubCategoryReload'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-29', pluginId: 13).save();     // url: '/docSubCategoryUserMapping/show'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-30', pluginId: 13).save();     // url: '/docSubCategoryUserMapping/list'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-31', pluginId: 13).save();     // url: '/docSubCategoryUserMapping/create'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-32', pluginId: 13).save();     // url: '/docSubCategoryUserMapping/update'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-33', pluginId: 13).save();     // url: '/docSubCategoryUserMapping/delete'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-34', pluginId: 13).save();     // url: '/docAllCategoryUserMapping/dropDownAppUserForSubCategoryReload'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-34', pluginId: 13).save();     // url: '/docAllCategoryUserMapping/dropDownAppUserForSubCategoryReload'


                // docInvitedMembers
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-35', pluginId: 13).save();     // url: '/docInvitedMembers/show
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-36', pluginId: 13).save();     // url: '/docInvitedMembers/showInvitation'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-37', pluginId: 13).save();     // url: '/docInvitedMembers/listInvitation'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-38', pluginId: 13).save();     // url: '/docInvitedMembers/showResendInvitation'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-39', pluginId: 13).save();     // url: '/docInvitedMembers/sendInvitation'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-40', pluginId: 13).save();     // url: '/docInvitedMembers/delete'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-35', pluginId: 13).save();     // url: '/docInvitedMembers/show
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-36', pluginId: 13).save();     // url: '/docInvitedMembers/showInvitation'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-37', pluginId: 13).save();     // url: '/docInvitedMembers/listInvitation'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-38', pluginId: 13).save();     // url: '/docInvitedMembers/showResendInvitation'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-39', pluginId: 13).save();     // url: '/docInvitedMembers/sendInvitation'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-40', pluginId: 13).save();     // url: '/docInvitedMembers/delete'

            // docMemberJoinRequest
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-41', pluginId: 13).save();     // url: '/docMemberJoinRequest/show'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-42', pluginId: 13).save();     // url: '/docMemberJoinRequest/searchJoinRequest'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-43', pluginId: 13).save();     // url: '/docMemberJoinRequest/approvedForMembership'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-46', pluginId: 13).save();     // url: '/docMemberJoinRequest/delete'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-41', pluginId: 13).save();     // url: '/docMemberJoinRequest/show'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-42', pluginId: 13).save();     // url: '/docMemberJoinRequest/searchJoinRequest'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-43', pluginId: 13).save();     // url: '/docMemberJoinRequest/approvedForMembership'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-46', pluginId: 13).save();     // url: '/docMemberJoinRequest/delete'

            // docDocument
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-55', pluginId: 13).save();     // url: '/docDocument/showArticle'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-55', pluginId: 13).save();    // url: '/docDocument/showArticle'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-55', pluginId: 13).save();    // url: '/docDocument/showArticle'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-63', pluginId: 13).save();     // url: '/docDocument/createArticle'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-63', pluginId: 13).save();    // url: '/docDocument/createArticle'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-69', pluginId: 13).save();     // url: '/docDocument/updateArticle'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-69', pluginId: 13).save();    // url: '/docDocument/updateArticle'

            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-70', pluginId: 13).save();     // url: '/docDocument/showArticle'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-70', pluginId: 13).save();    // url: '/docDocument/showArticle'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-76', pluginId: 13).save();     // url: '/docDocument/createArticle'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-76', pluginId: 13).save();    // url: '/docDocument/createArticle'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-125', pluginId: 13).save();     // url: '/docDocument/updateArticle'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-125', pluginId: 13).save();    // url: '/docDocument/updateArticle'

            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-127', pluginId: 13).save();     // url: '/docDocument/showAudio'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-127', pluginId: 13).save();     // url: '/docDocument/showAudio'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-127', pluginId: 13).save();    // url: '/docDocument/showAudio'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-129', pluginId: 13).save();     // url: '/docDocument/createAudio'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-129', pluginId: 13).save();     // url: '/docDocument/createAudio'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-129', pluginId: 13).save();    // url: '/docDocument/createAudio'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-133', pluginId: 13).save();     // url: '/docDocument/updateAudio'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-133', pluginId: 13).save();     // url: '/docDocument/updateAudio'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-133', pluginId: 13).save();    // url: '/docDocument/updateAudio'

            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-140', pluginId: 13).save();    // url: '/docDocument/showVideo'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-140', pluginId: 13).save();    // url: '/docDocument/showVideo'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-140', pluginId: 13).save();    // url: '/docDocument/showVideo'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-141', pluginId: 13).save();    // url: '/docDocument/createVideo'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-141', pluginId: 13).save();    // url: '/docDocument/createVideo'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-141', pluginId: 13).save();    // url: '/docDocument/createVideo'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-158', pluginId: 13).save();    // url: '/docDocument/updateVideo'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-158', pluginId: 13).save();    // url: '/docDocument/updateVideo'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-158', pluginId: 13).save();    // url: '/docDocument/updateVideo'

            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-165', pluginId: 13).save();    // url: '/docDocument/showVideo'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-165', pluginId: 13).save();    // url: '/docDocument/showVideo'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-196', pluginId: 13).save();    // url: '/docDocument/createVideo'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-196', pluginId: 13).save();    // url: '/docDocument/createImage'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-196', pluginId: 13).save();    // url: '/docDocument/createVideo'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-197', pluginId: 13).save();    // url: '/docDocument/updateVideo'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-197', pluginId: 13).save();    // url: '/docDocument/updateImage'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-197', pluginId: 13).save();    // url: '/docDocument/updateVideo'

            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-47', pluginId: 13).save();     // url: '/docDocumentVersion/showFile'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-47', pluginId: 13).save();    // url: '/docDocumentVersion/showFile'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-48', pluginId: 13).save();     // url: '/docDocument/listFile'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-48', pluginId: 13).save();    // url: '/docDocument/listFile'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-153', pluginId: 13).save();    // url: '/docDocument/listFileDetails'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-153', pluginId: 13).save();    // url: '/docDocument/listFileDetails'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-153', pluginId: 13).save();    // url: '/docDocument/listFileDetails'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-154', pluginId: 13).save();    // url: '/docDocument/listImageDetails'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-154', pluginId: 13).save();    // url: '/docDocument/listImageDetails'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-154', pluginId: 13).save();    // url: '/docDocument/listImageDetails'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-49', pluginId: 13).save();     // url: '/docDocumentVersion/createFile'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-49', pluginId: 13).save();    // url: '/docDocumentVersion/createFile'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-50', pluginId: 13).save();    // url: '/docDocument/updateFile'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-50', pluginId: 13).save();    // url: '/docDocument/updateFile'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-51', pluginId: 13).save();    // url: '/docDocument/movedToTrashFile'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-51', pluginId: 13).save();    // url: '/docDocument/movedToTrashFile
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-123', pluginId: 13).save();   // url: '/docDocument/showDetails
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-123', pluginId: 13).save();   // url: '/docDocument/showDetails
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-123', pluginId: 13).save();   // url: '/docDocument/showDetails
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-124', pluginId: 13).save();   // url: '/docDocumentOwnership/changeOwner'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-124', pluginId: 13).save();   // url: '/docDocumentOwnership/changeOwner'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-124', pluginId: 13).save();   // url: '/docDocumentOwnership/changeOwner'

            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-52', pluginId: 13).save();     // url: '/docDocumentVersion/showArticle'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-52', pluginId: 13).save();    // url: '/docDocumentVersion/showArticle'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-53', pluginId: 13).save();     // url: '/docDocumentVersion/listArticle'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-53', pluginId: 13).save();    // url: '/docDocumentVersion/listArticle'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-54', pluginId: 13).save();     // url: '/docDocument/createArticle'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-54', pluginId: 13).save();    // url: '/docDocument/createArticle'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-56', pluginId: 13).save();     // url: '/docDocument/updateArticle'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-56', pluginId: 13).save();    // url: '/docDocument/updateArticle'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-57', pluginId: 13).save();     // url: '/docDocument/movedToTrashArticle'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-57', pluginId: 13).save();    // url: '/docDocument/movedToTrashArticle'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-152', pluginId: 13).save();    // url: '/docDocument/listArticleDetails'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-152', pluginId: 13).save();    // url: '/docDocument/listArticleDetails'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-152', pluginId: 13).save();    // url: '/docDocument/listArticleDetails'

            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-58', pluginId: 13).save();     // url: '/docDocument/previewArticle'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-58', pluginId: 13).save();    // url: '/docDocument/previewArticle'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-59', pluginId: 13).save();     // url: '/docDocument/downloadDocument'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-59', pluginId: 13).save();    // url: '/docDocument/downloadDocument'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-60', pluginId: 13).save();     // url: '/docDocument/showDocumentQueryResult'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-60', pluginId: 13).save();    // url: '/docDocument/showDocumentQueryResult'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-61', pluginId: 13).save();     // url: '/docDocument/searchDocumentQueryResult'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-61', pluginId: 13).save();    // url: '/docDocument/searchDocumentQueryResult'

            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-128', pluginId: 13).save();    // url: '/docDocument/showAudio'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-128', pluginId: 13).save();    // url: '/docDocument/showAudio'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-128', pluginId: 13).save();    // url: '/docDocument/showAudio'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-146', pluginId: 13).save();    // url: '/docDocument/showForViewVideo'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-146', pluginId: 13).save();    // url: '/docDocument/showForViewVideo'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-146', pluginId: 13).save();    // url: '/docDocument/showForViewVideo'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-147', pluginId: 13).save();    // url: '/docDocument/showForViewAudio'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-147', pluginId: 13).save();    // url: '/docDocument/showForViewAudio'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-147', pluginId: 13).save();    // url: '/docDocument/showForViewAudio'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-150', pluginId: 13).save();    // url: '/docDocument/showForViewFile'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-150', pluginId: 13).save();    // url: '/docDocument/showForViewFile'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-150', pluginId: 13).save();    // url: '/docDocument/showForViewFile'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-155', pluginId: 13).save();    // url: '/docDocument/listAudioDetails'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-155', pluginId: 13).save();    // url: '/docDocument/listAudioDetails'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-155', pluginId: 13).save();    // url: '/docDocument/listAudioDetails'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-130', pluginId: 13).save();    // url: '/docDocument/createAudio'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-130', pluginId: 13).save();    // url: '/docDocument/createAudio'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-131', pluginId: 13).save();    // url: '/docDocument/updateAudio'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-131', pluginId: 13).save();    // url: '/docDocument/updateAudio'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-142', pluginId: 13).save();    // url: '/docDocument/movedToTrashAudio'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-142', pluginId: 13).save();    // url: '/docDocument/movedToTrashAudio'


            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-132', pluginId: 13).save();    // url: '/docDocument/showVideo'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-132', pluginId: 13).save();    // url: '/docDocument/showVideo'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-156', pluginId: 13).save();    // url: '/docDocument/listVideoDetails'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-156', pluginId: 13).save();    // url: '/docDocument/listVideoDetails'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-156', pluginId: 13).save();    // url: '/docDocument/listVideoDetails'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-134', pluginId: 13).save();    // url: '/docDocument/createVideo'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-134', pluginId: 13).save();    // url: '/docDocument/createVideo'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-135', pluginId: 13).save();    // url: '/docDocument/updateVideo'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-135', pluginId: 13).save();    // url: '/docDocument/updateVideo'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-143', pluginId: 13).save();    // url: '/docDocument/movedToTrashVideo'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-143', pluginId: 13).save();    // url: '/docDocument/movedToTrashVideo'

            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-157', pluginId: 13).save();    // url: '/docDocument/showImage'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-157', pluginId: 13).save();    // url: '/docDocument/showImage'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-165', pluginId: 13).save();    // url: '/docDocument/showImage'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-157', pluginId: 13).save();    // url: '/docDocument/showImage'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-159', pluginId: 13).save();    // url: '/docDocument/createImage'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-159', pluginId: 13).save();    // url: '/docDocument/createImage'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-159', pluginId: 13).save();    // url: '/docDocument/createImage'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-160', pluginId: 13).save();    // url: '/docDocument/updateImage'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-160', pluginId: 13).save();    // url: '/docDocument/updateImage'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-160', pluginId: 13).save();    // url: '/docDocument/updateImage'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-161', pluginId: 13).save();    // url: '/docDocument/movedToTrashImage'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-161', pluginId: 13).save();    // url: '/docDocument/movedToTrashImage'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-161', pluginId: 13).save();    // url: '/docDocument/movedToTrashImage'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-162', pluginId: 13).save();    // url: '/docDocument/showForViewImage'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-162', pluginId: 13).save();    // url: '/docDocument/showForViewImage'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-162', pluginId: 13).save();    // url: '/docDocument/showForViewImage'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-173', pluginId: 13).save();    // url: '/docDocument/checkInForArticle'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-173', pluginId: 13).save();    // url: '/docDocument/checkInForArticle'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-173', pluginId: 13).save();    // url: '/docDocument/checkInForArticle'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-174', pluginId: 13).save();    // url: '/docDocument/checkOutForArticle'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-174', pluginId: 13).save();    // url: '/docDocument/checkOutForArticle'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-174', pluginId: 13).save();    // url: '/docDocument/checkOutForArticle'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-175', pluginId: 13).save();    // url: '/docDocument/checkInForFile'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-175', pluginId: 13).save();    // url: '/docDocument/checkInForFile'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-175', pluginId: 13).save();    // url: '/docDocument/checkInForFile'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-176', pluginId: 13).save();    // url: '/docDocument/checkOutForFile'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-176', pluginId: 13).save();    // url: '/docDocument/checkOutForFile'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-176', pluginId: 13).save();    // url: '/docDocument/checkOutForFile'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-177', pluginId: 13).save();    // url: '/docDocument/uploadFile'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-198', pluginId: 13).save();    // url: '/docDocument/uploadFile'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-177', pluginId: 13).save();    // url: '/docDocument/uploadFile'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-198', pluginId: 13).save();    // url: '/docDocument/uploadFile'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-177', pluginId: 13).save();    // url: '/docDocument/uploadFile'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-198', pluginId: 13).save();    // url: '/docDocument/uploadFile'

            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-178', pluginId: 13).save();    // url: '/docDocument/uploadImage'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-199', pluginId: 13).save();    // url: '/docDocument/uploadImage'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-178', pluginId: 13).save();    // url: '/docDocument/uploadImage'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-199', pluginId: 13).save();    // url: '/docDocument/uploadImage'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-178', pluginId: 13).save();    // url: '/docDocument/uploadImage'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-199', pluginId: 13).save();    // url: '/docDocument/uploadImage'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-179', pluginId: 13).save();    // url: '/docDocument/uploadAudio'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-200', pluginId: 13).save();    // url: '/docDocument/uploadAudio'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-179', pluginId: 13).save();    // url: '/docDocument/uploadAudio'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-200', pluginId: 13).save();    // url: '/docDocument/uploadAudio'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-179', pluginId: 13).save();    // url: '/docDocument/uploadAudio'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-200', pluginId: 13).save();    // url: '/docDocument/uploadAudio'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-180', pluginId: 13).save();    // url: '/docDocument/uploadVideo'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-201', pluginId: 13).save();    // url: '/docDocument/uploadVideo'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-180', pluginId: 13).save();    // url: '/docDocument/uploadVideo'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-201', pluginId: 13).save();    // url: '/docDocument/uploadVideo'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-180', pluginId: 13).save();    // url: '/docDocument/uploadVideo'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-201', pluginId: 13).save();    // url: '/docDocument/uploadVideo'



            // for development user
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-47', pluginId: 13).save();     // url: '/docDocument/showFile'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-70', pluginId: 13).save();     // url: '/docDocument/showFile'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-48', pluginId: 13).save();     // url: '/docDocument/listFile'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-49', pluginId: 13).save();     // url: '/docDocument/createFile'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-76', pluginId: 13).save();     // url: '/docDocument/createFile'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-50', pluginId: 13).save();    // url: '/docDocument/updateFile'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-125', pluginId: 13).save();    // url: '/docDocument/updateFile'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-51', pluginId: 13).save();    // url: '/docDocument/movedToTrashFile'

            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-52', pluginId: 13).save();     // url: '/docDocument/showArticle'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-53', pluginId: 13).save();     // url: '/docDocument/listArticle'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-54', pluginId: 13).save();     // url: '/docDocument/createArticle'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-63', pluginId: 13).save();     // url: '/docDocument/createArticle'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-56', pluginId: 13).save();     // url: '/docDocument/updateArticle'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-69', pluginId: 13).save();     // url: '/docDocument/updateArticle'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-57', pluginId: 13).save();     // url: '/docDocument/movedToTrashArticle'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-58', pluginId: 13).save();     // url: '/docDocument/previewArticle'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-59', pluginId: 13).save();     // url: '/docDocument/downloadDocument'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-60', pluginId: 13).save();     // url: '/docDocument/showDocumentQueryResult'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-61', pluginId: 13).save();     // url: '/docDocument/searchDocumentQueryResult'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-126', pluginId: 13).save();    // url: '/docDocument/createComment'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-126', pluginId: 13).save();    // url: '/docDocument/createComment'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-126', pluginId: 13).save();    // url: '/docDocument/createComment'


            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-130', pluginId: 13).save();    // url: '/docDocument/createAudio'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-131', pluginId: 13).save();    // url: '/docDocument/updateAudio'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-142', pluginId: 13).save();    // url: '/docDocument/movedToTrashAudio'

            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-132', pluginId: 13).save();    // url: '/docDocument/showVideo'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-134', pluginId: 13).save();    // url: '/docDocument/createVideo'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-135', pluginId: 13).save();    // url: '/docDocument/updateVideo'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-143', pluginId: 13).save();    // url: '/docDocument/movedToTrashVideo'

            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-188', pluginId: 13).save();    // url: '/docDocument/reloadDocumentViewer'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-188', pluginId: 13).save();    // url: '/docDocument/reloadDocumentViewer'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-188', pluginId: 13).save();    // url: '/docDocument/reloadDocumentViewer'
            // DocDocumentReadLog
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-148', pluginId: 13).save();    // url: '/docDocumentReadLog/create'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-148', pluginId: 13).save();    // url: '/docDocumentReadLog/create'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-148', pluginId: 13).save();    // url: '/docDocumentReadLog/create'

            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-149', pluginId: 13).save();    // url: '/docDocumentReadLog/delete'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-149', pluginId: 13).save();    // url: '/docDocumentReadLog/delete'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-149', pluginId: 13).save();    // url: '/docDocumentReadLog/delete'

                // docDocumentTrash
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-65', pluginId: 13).save();     // url: '/docDocumentTrash/showArticle'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-65', pluginId: 13).save();    // url: '/docDocumentTrash/showArticle'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-66', pluginId: 13).save();     // url: '/docDocumentTrash/listArticle'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-66', pluginId: 13).save();    // url: '/docDocumentTrash/listArticle'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-67', pluginId: 13).save();     // url: '/docDocumentTrash/restoreArticle'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-67', pluginId: 13).save();    // url: '/docDocumentTrash/restoreArticle'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-68', pluginId: 13).save();     // url: '/docDocumentTrash/deleteArticle'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-68', pluginId: 13).save();    // url: '/docDocumentTrash/deleteArticle'

            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-71', pluginId: 13).save();    // url: '/docDocumentTrash/showFile'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-136', pluginId: 13).save();    // url: '/docDocumentTrash/showAudio'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-136', pluginId: 13).save();    // url: '/docDocumentTrash/showAudio'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-136', pluginId: 13).save();    // url: '/docDocumentTrash/showAudio'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-138', pluginId: 13).save();    // url: '/docDocumentTrash/showVideo'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-138', pluginId: 13).save();    // url: '/docDocumentTrash/showVideo'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-138', pluginId: 13).save();    // url: '/docDocumentTrash/showVideo'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-71', pluginId: 13).save();   // url: '/docDocumentTrash/showFile'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-72', pluginId: 13).save();    // url: '/docDocumentTrash/listFile'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-137', pluginId: 13).save();    // url: '/docDocumentTrash/listTrashAudio'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-137', pluginId: 13).save();    // url: '/docDocumentTrash/listTrashAudio'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-137', pluginId: 13).save();    // url: '/docDocumentTrash/listTrashAudio'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-72', pluginId: 13).save();   // url: '/docDocumentTrash/listFile'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-139', pluginId: 13).save();   // url: '/docDocumentTrash/listVideo'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-139', pluginId: 13).save();   // url: '/docDocumentTrash/listVideo'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-139', pluginId: 13).save();   // url: '/docDocumentTrash/listVideo'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-73', pluginId: 13).save();    // url: '/docDocumentTrash/restoreFromTrash'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-73', pluginId: 13).save();   // url: '/docDocumentTrash/restoreFromTrash'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-74', pluginId: 13).save();    // url: '/docDocumentTrash/deleteFile'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-74', pluginId: 13).save();   // url: '/docDocumentTrash/deleteFile'

            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-144', pluginId: 13).save();   // url: '/docDocumentTrash/restoreAudio'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-144', pluginId: 13).save();   // url: '/docDocumentTrash/restoreAudio'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-144', pluginId: 13).save();   // url: '/docDocumentTrash/restoreAudio'

            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-145', pluginId: 13).save();   // url: '/docDocumentTrash/restoreVideo'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-145', pluginId: 13).save();   // url: '/docDocumentTrash/restoreVideo'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-145', pluginId: 13).save();   // url: '/docDocumentTrash/restoreVideo'

            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-163', pluginId: 13).save();   // url: '/docDocumentTrash/showImage'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-163', pluginId: 13).save();   // url: '/docDocumentTrash/showImage'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-163', pluginId: 13).save();   // url: '/docDocumentTrash/showImage'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-164', pluginId: 13).save();   // url: '/docDocumentTrash/listImage'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-164', pluginId: 13).save();   // url: '/docDocumentTrash/listImage'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-164', pluginId: 13).save();   // url: '/docDocumentTrash/listImage
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-166', pluginId: 13).save();   // url: '/docDocumentTrash/restoreImage'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-166', pluginId: 13).save();   // url: '/docDocumentTrash/restoreImage'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-166', pluginId: 13).save();   // url: '/docDocumentTrash/restoreImage

            // for development user
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-65', pluginId: 13).save();     // url: '/docDocumentTrash/showArticle'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-66', pluginId: 13).save();     // url: '/docDocumentTrash/listArticle'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-67', pluginId: 13).save();     // url: '/docDocumentTrash/restoreArticle'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-68', pluginId: 13).save();     // url: '/docDocumentTrash/deleteArticle'

            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-71', pluginId: 13).save();    // url: '/docDocumentTrash/showFile'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-72', pluginId: 13).save();    // url: '/docDocumentTrash/listFile'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-73', pluginId: 13).save();    // url: '/docDocumentTrash/restoreFromTrash'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-74', pluginId: 13).save();    // url: '/docDocumentTrash/deleteFile'

            //docDataIndex
//            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-77', pluginId: 13).save();     // url: '/docDataIndex/show'
//            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-78', pluginId: 13).save();     // url: '/docDataIndex/list'
//            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-79', pluginId: 13).save();     // url: '/docDataIndex/create'
//            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-80', pluginId: 13).save();     // url: '/docDataIndex/update'
//            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-81', pluginId: 13).save();     // url: '/docDataIndex/delete'
//            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-82', pluginId: 13).save();     // url: '/docDataIndex/preview'
//            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-83', pluginId: 13).save();     // url: '/docDataIndex/generateDataIndex'
            // for development user
//            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-77', pluginId: 13).save();     // url: '/docDataIndex/show'
//            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-78', pluginId: 13).save();     // url: '/docDataIndex/list'
//            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-79', pluginId: 13).save();     // url: '/docDataIndex/create'
//            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-80', pluginId: 13).save();     // url: '/docDataIndex/update'
//            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-81', pluginId: 13).save();     // url: '/docDataIndex/delete'
//            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-82', pluginId: 13).save();     // url: '/docDataIndex/preview'
//            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-83', pluginId: 13).save();     // url: '/docDataIndex/generateDataIndex'


                // docDocumentQuery
//            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-84', pluginId: 13).save();     // url: '/docDocumentQuery/show'
//            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-84', pluginId: 13).save();    // url: '/docDocumentQuery/show'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-85', pluginId: 13).save();     // url: '/docDocumentQuery/list'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-85', pluginId: 13).save();    // url: '/docDocumentQuery/list'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-86', pluginId: 13).save();     // url: '/docDocumentQuery/create'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-86', pluginId: 13).save();    // url: '/docDocumentQuery/create'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-87', pluginId: 13).save();     // url: '/docDocumentQuery/update'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-87', pluginId: 13).save();    // url: '/docDocumentQuery/update'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-88', pluginId: 13).save();     // url: '/docDocumentQuery/delete'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-88', pluginId: 13).save();    // url: '/docDocumentQuery/delete'
            // for development user
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-84', pluginId: 13).save();     // url: '/docDocumentQuery/show'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-85', pluginId: 13).save();     // url: '/docDocumentQuery/list'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-86', pluginId: 13).save();     // url: '/docDocumentQuery/create'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-87', pluginId: 13).save();     // url: '/docDocumentQuery/update'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-88', pluginId: 13).save();     // url: '/docDocumentQuery/delete'

            // docQuestions
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-89', pluginId: 13).save();     // url: '/docQuestion/show'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-187', pluginId: 13).save();     // url: '/docQuestion/showForLanding'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-89', pluginId: 13).save();    // url: '/docQuestion/show'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-90', pluginId: 13).save();     // url: '/docQuestion/list'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-90', pluginId: 13).save();    // url: '/docQuestion/list'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-91', pluginId: 13).save();     // url: '/docQuestion/create'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-91', pluginId: 13).save();    // url: '/docQuestion/create'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-92', pluginId: 13).save();     // url: '/docQuestion/update'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-92', pluginId: 13).save();    // url: '/docQuestion/update'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-93', pluginId: 13).save();     // url: '/docQuestion/delete'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-93', pluginId: 13).save();    // url: '/docQuestion/delete'
            // for development user
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-89', pluginId: 13).save();     // url: '/docQuestion/show'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-90', pluginId: 13).save();     // url: '/docQuestion/list'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-91', pluginId: 13).save();     // url: '/docQuestion/create'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-92', pluginId: 13).save();     // url: '/docQuestion/update'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-93', pluginId: 13).save();     // url: '/docQuestion/delete'

            // docAnswer
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-94', pluginId: 13).save();     // url: '/docAnswer/show'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-94', pluginId: 13).save();    // url: '/docAnswer/show'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-95', pluginId: 13).save();     // url: '/docAnswer/list'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-95', pluginId: 13).save();    // url: '/docAnswer/list'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-96', pluginId: 13).save();     // url: '/docAnswer/create'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-96', pluginId: 13).save();    // url: '/docAnswer/create'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-97', pluginId: 13).save();     // url: '/docAnswer/update'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-97', pluginId: 13).save();    // url: '/docAnswer/update'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-98', pluginId: 13).save();     // url: '/docAnswer/delete'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-98', pluginId: 13).save();    // url: '/docAnswer/delete'
            // docAnswer, for development user
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-94', pluginId: 13).save();     // url: '/docAnswer/show'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-95', pluginId: 13).save();     // url: '/docAnswer/list'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-96', pluginId: 13).save();     // url: '/docAnswer/create'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-97', pluginId: 13).save();     // url: '/docAnswer/update'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-98', pluginId: 13).save();     // url: '/docAnswer/delete'

            // docReport
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-99', pluginId: 13).save();     // url: '/docReport/downloadQuestion'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-99', pluginId: 13).save();    // url: '/docReport/downloadQuestion'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-115', pluginId: 13).save();   // url: '/docReport/downloadAnswer'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-115', pluginId: 13).save();   // url: '/docReport/downloadArticle'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-116', pluginId: 13).save();   // url: '/docReport/downloadQuestionSet'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-117', pluginId: 13).save();   // url: '/docReport/downloadAnswerSet'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-100', pluginId: 13).save();    // url: '/docReport/downloadArticle'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-100', pluginId: 13).save();   // url: '/docReport/downloadArticle'
            // for development user
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-99', pluginId: 13).save();     // url: '/docReport/downloadQuestion'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-115', pluginId: 13).save();   // url: '/docReport/downloadAnswer'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-116', pluginId: 13).save();   // url: '/docReport/downloadQuestionSet'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-117', pluginId: 13).save();   // url: '/docReport/downloadAnswerSet'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-100', pluginId: 13).save();    // url: '/docReport/downloadArticle'


            // docDocumentType, development user
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-101', pluginId: 13).save();     // url: '/docDocumentType/show'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-102', pluginId: 13).save();     // url: '/docDocumentType/list'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-103', pluginId: 13).save();     // url: '/docDocumentType/create'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-104', pluginId: 13).save();     // url: '/docDocumentType/update'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-105', pluginId: 13).save();     // url: '/docDocumentType/delete'

                //docQuestionSet
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-106', pluginId: 13).save();     // url: '/docQuestionSet/show'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-107', pluginId: 13).save();     // url: '/docQuestionSet/list'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-108', pluginId: 13).save();     // url: '/docQuestionSet/create'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-109', pluginId: 13).save();     // url: '/docQuestionSet/update'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-110', pluginId: 13).save();     // url: '/docQuestionSet/delete'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-111', pluginId: 13).save();     // url: '/docQuestionSet/generateQuestionSet'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-112', pluginId: 13).save();     // url: '/docQuestionSet/clearQuestionSet'
            // docQuestionSet, development user
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-106', pluginId: 13).save();     // url: '/docQuestionSet/show'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-107', pluginId: 13).save();     // url: '/docQuestionSet/list'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-108', pluginId: 13).save();     // url: '/docQuestionSet/create'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-109', pluginId: 13).save();     // url: '/docQuestionSet/update'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-110', pluginId: 13).save();     // url: '/docQuestionSet/delete'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-111', pluginId: 13).save();     // url: '/docQuestionSet/generateQuestionSet'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-112', pluginId: 13).save();     // url: '/docQuestionSet/clearQuestionSet'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-168', pluginId: 13).save();     // url: '/docQuestionSet/updateExamTime'

            //docQuestionSetDetails
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-113', pluginId: 13).save();     // url: '/docQuestionSetDetails/show'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-114', pluginId: 13).save();     // url: '/docQuestionSetDetails/list'
            // development user
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-113', pluginId: 13).save();     // url: '/docQuestionSetDetails/show'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-114', pluginId: 13).save();     // url: '/docQuestionSetDetails/list'

            //docQuestionSetDifficulty
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-118', pluginId: 13).save();     // url: '/docQuestionSetDifficulty/show'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-119', pluginId: 13).save();     // url: '/docQuestionSetDifficulty/list'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-120', pluginId: 13).save();     // url: '/docQuestionSetDifficulty/create'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-121', pluginId: 13).save();     // url: '/docQuestionSetDifficulty/update'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-122', pluginId: 13).save();     // url: '/docQuestionSetDifficulty/delete'
            // development user
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-118', pluginId: 13).save();     // url: '/docQuestionSetDifficulty/show'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-119', pluginId: 13).save();     // url: '/docQuestionSetDifficulty/list'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-120', pluginId: 13).save();     // url: '/docQuestionSetDifficulty/create'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-121', pluginId: 13).save();     // url: '/docQuestionSetDifficulty/update'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-122', pluginId: 13).save();     // url: '/docQuestionSetDifficulty/delete'

            //docDocumentVersion
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-169', pluginId: 13).save();     // url: '/docDocumentVersion/show'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-170', pluginId: 13).save();     // url: '/docDocumentVersion/list'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-171', pluginId: 13).save();     // url: '/docDocumentVersion/deletedelete'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-172', pluginId: 13).save();     // url: '/docDocumentVersion/restore'

            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-169', pluginId: 13).save();     // url: '/docDocumentVersion/show'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-170', pluginId: 13).save();     // url: '/docDocumentVersion/list'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-171', pluginId: 13).save();     // url: '/docDocumentVersion/deletedelete'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-172', pluginId: 13).save();     // url: '/docDocumentVersion/restore'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-181', pluginId: 13).save();     // url: '/docDocumentVersion/showVersion'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-182', pluginId: 13).save();     // url: '/docDocumentVersion/listLatestVersion'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-183', pluginId: 13).save();     // url: '/docDocumentVersion/ListPreviousVersion'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-184', pluginId: 13).save();     // url: '/docDocumentVersion/deleteVersion'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-185', pluginId: 13).save();     // url: '/docDocumentVersion/restoreTrashedVersion'

            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-169', pluginId: 13).save();     // url: '/docDocumentVersion/show'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-170', pluginId: 13).save();     // url: '/docDocumentVersion/list'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-171', pluginId: 13).save();     // url: '/docDocumentVersion/delete'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-172', pluginId: 13).save();     // url: '/docDocumentVersion/restore'

            // document comment
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'APP-252', pluginId: 13).save(); //url: '/appNote/reloadEntityNote'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'APP-252', pluginId: 13).save(); //url: '/appNote/reloadEntityNote'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'APP-252', pluginId: 13).save(); //url: '/appNote/reloadEntityNote'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-220', pluginId: 13).save(); //url: '/appNote/listEntityNote'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-220', pluginId: 13).save(); //url: '/appNote/listEntityNote'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-220', pluginId: 13).save(); //url: '/appNote/listEntityNote'

            // doucment faq
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'APP-440', pluginId: 13).save(); // url: '/appFaq/show'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'APP-441', pluginId: 13).save(); // url: '/appFaq/list'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'APP-442', pluginId: 13).save(); // url: '/appFaq/create'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'APP-443', pluginId: 13).save(); // url: '/appFaq/update'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'APP-444', pluginId: 13).save(); // url: '/appFaq/delete'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'APP-445', pluginId: 13).save(); // url: '/appFaq/listFaq'
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'APP-446', pluginId: 13).save(); // url: '/appFaq/reloadFaq'

            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'APP-440', pluginId: 13).save(); // url: '/appFaq/show'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'APP-441', pluginId: 13).save(); // url: '/appFaq/list'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'APP-442', pluginId: 13).save(); // url: '/appFaq/create'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'APP-443', pluginId: 13).save(); // url: '/appNote/update'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'APP-444', pluginId: 13).save(); // url: '/appFaq/delete'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'APP-445', pluginId: 13).save(); // url: '/appFaq/listFaq'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'APP-446', pluginId: 13).save(); // url: '/appFaq/reloadFaq'

            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'APP-440', pluginId: 13).save(); // url: '/appFaq/show'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'APP-441', pluginId: 13).save(); // url: '/appFaq/list'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'APP-442', pluginId: 13).save(); // url: '/appFaq/create'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'APP-443', pluginId: 13).save(); // url: '/appFaq/update'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'APP-444', pluginId: 13).save(); // url: '/appFaq/delete'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'APP-445', pluginId: 13).save(); // url: '/appFaq/listFaq'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'APP-446', pluginId: 13).save(); // url: '/appFaq/reloadFaq'

            // list doc document for modal
            new RoleFeatureMapping(roleTypeId: -501, transactionCode: 'DOC-218', pluginId: 13).save(); // url: '/docDocumentVersion/listForModal'
            new RoleFeatureMapping(roleTypeId: -502, transactionCode: 'DOC-218', pluginId: 13).save(); // url: '/docDocumentVersion/listForModal'
            new RoleFeatureMapping(roleTypeId: -503, transactionCode: 'DOC-218', pluginId: 13).save(); // url: '/docDocumentVersion/listForModal'

    } catch (Exception ex) {
        log.error(ex.getMessage())
        throw new RuntimeException(ex)
    }
}

    public boolean createRoleFeatureMapForDataPipeLinePlugin() {
        try {
        // data pipeline
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-1', pluginId: 14).save();
        // for development user
        new RoleFeatureMapping(roleTypeId: -602, transactionCode: 'DPL-1', pluginId: 14).save();

        // system config
        new RoleFeatureMapping(roleTypeId: -602, transactionCode: 'APP-99', pluginId: 14).save(); // url: '/systemConfiguration/show'
        new RoleFeatureMapping(roleTypeId: -602, transactionCode: 'APP-100', pluginId: 14).save(); // url: '/systemConfiguration/list'
        new RoleFeatureMapping(roleTypeId: -602, transactionCode: 'APP-102', pluginId: 14).save(); // url: '/systemConfiguration/update'
        // for system entity role type -602, and data pipeline -602
        new RoleFeatureMapping(roleTypeId: -602, transactionCode: 'APP-103', pluginId: 14).save(); // url: '/systemEntity/show'
        new RoleFeatureMapping(roleTypeId: -602, transactionCode: 'APP-104', pluginId: 14).save(); // url: '/systemEntity/create'
        new RoleFeatureMapping(roleTypeId: -602, transactionCode: 'APP-105', pluginId: 14).save(); // url: '/systemEntity/list'
        new RoleFeatureMapping(roleTypeId: -602, transactionCode: 'APP-107', pluginId: 14).save(); // url: '/systemEntity/update'
        new RoleFeatureMapping(roleTypeId: -602, transactionCode: 'APP-173', pluginId: 14).save(); // url: '/systemEntity/delete'
        // system entity type
        new RoleFeatureMapping(roleTypeId: -602, transactionCode: 'APP-108', pluginId: 14).save(); // url: '/systemEntityType/show'
        new RoleFeatureMapping(roleTypeId: -602, transactionCode: 'APP-109', pluginId: 14).save(); // url: '/systemEntityType/list'
        // for data pipeline transaction log
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'APP-312', pluginId: 14).save(); // url: '/transactionLog/show'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'APP-313', pluginId: 14).save(); // url: '/transactionLog/list'

        // appTheme
        new RoleFeatureMapping(roleTypeId: -602, transactionCode: 'APP-124', pluginId: 14).save(); // url: '/appTheme/showTheme'
        new RoleFeatureMapping(roleTypeId: -602, transactionCode: 'APP-125', pluginId: 14).save(); // url: '/appTheme/updateTheme'
        new RoleFeatureMapping(roleTypeId: -602, transactionCode: 'APP-126', pluginId: 14).save(); // url: '/appTheme/listTheme'


        // dplOfflineDataFeedFile
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-2', pluginId: 14).save();  // url: '/dplOfflineDataFeedFile/show'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-3', pluginId: 14).save();  // url: '/dplOfflineDataFeedFile/showTarget'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-4', pluginId: 14).save();  // url: '/dplOfflineDataFeedFile/showLoaded'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-5', pluginId: 14).save();  // url: '/dplOfflineDataFeedFile/list'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-6', pluginId: 14).save();  // url: '/dplOfflineDataFeedFile/listTargetFile'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-7', pluginId: 14).save();  // url: '/dplOfflineDataFeedFile/listLoadedFile
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-8', pluginId: 14).save();  // url: '/dplOfflineDataFeedFile/receivedFile'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-9', pluginId: 14).save();  // url: '/dplOfflineDataFeedFile/receiveFileFromSource'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-10', pluginId: 14).save(); // url: '/dplOfflineDataFeedFile/deleteFromTarget'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-11', pluginId: 14).save(); // url: '/dplOfflineDataFeedFile/deleteFromDataBucket'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-12', pluginId: 14).save(); // url: '/dplOfflineDataFeedFile/moveToDataBucket'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-13', pluginId: 14).save(); // url: '/dplOfflineDataFeedFile/showDataBucket'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-14', pluginId: 14).save(); // url: '/dplOfflineDataFeedFile/listDataBucket'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-15', pluginId: 14).save(); // url: '/dplOfflineDataFeedFile/loadDataFeed'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-16', pluginId: 14).save(); // url: '/dplOfflineDataFeedFile/showSource'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-17', pluginId: 14).save(); // url: '/dplOfflineDataFeedFile/listSourceFile'
        // for development user
        new RoleFeatureMapping(roleTypeId: -602, transactionCode: 'DPL-3', pluginId: 14).save();  // url: '/dplOfflineDataFeedFile/showTarget'
        new RoleFeatureMapping(roleTypeId: -602, transactionCode: 'DPL-6', pluginId: 14).save();  // url: '/dplOfflineDataFeedFile/listTargetFile'
        new RoleFeatureMapping(roleTypeId: -602, transactionCode: 'DPL-10', pluginId: 14).save(); // url: '/dplOfflineDataFeedFile/deleteFromTarget'


        // dplDataExport
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-19', pluginId: 14).save();    // url: '/dplDataExport/show'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-21', pluginId: 14).save();    // url: '/dplDataExport/list'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-23', pluginId: 14).save();    // url: '/dplDataExport/create'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-24', pluginId: 14).save();    // url: '/dplDataExport/select'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-26', pluginId: 14).save();    // url: '/dplDataExport/update'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-28', pluginId: 14).save();    // url: '/dplDataExport/delete'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-31', pluginId: 14).save();    // url: '/dplDataExport/approve'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-32', pluginId: 14).save();    // url: '/dplDataExport/execute'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-35', pluginId: 14).save();    // url: '/dplDataExport/unApprove'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-36', pluginId: 14).save();    // url: '/dplDataExport/stopExecution'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-38', pluginId: 14).save();    // url: '/dplDataExport/clearLog'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-39', pluginId: 14).save();    // url: '/dplDataExport/showReport'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-160', pluginId: 14).save();    // url: '/transactionLog/showForDplDataExport'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-167', pluginId: 14).save();    // url: '/dplDataExport/checkDataExport'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-168', pluginId: 14).save();    // url: '/dplDataExport/listForDplDashboard'

        // dplDataExportDetails
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-41', pluginId: 14).save();    // url: '/dplDataExportDetails/show'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-43', pluginId: 14).save();    // url: '/dplDataExportDetails/list'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-45', pluginId: 14).save();    // url: '/dplDataExportDetails/create'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-49', pluginId: 14).save();    // url: '/dplDataExportDetails/update'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-51', pluginId: 14).save();    // url: '/dplDataExportDetails/delete'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-135', pluginId: 14).save();    // url: '/dplDataExportDetails/addAllTable'

        // dplDataExportSourceSchema
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-104', pluginId: 14).save();    // url: '/dplDataExportSourceSchema/show'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-105', pluginId: 14).save();    // url: '/dplDataExportSourceSchema/list'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-106', pluginId: 14).save();    // url: '/dplDataExportSourceSchema/create'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-107', pluginId: 14).save();    // url: '/dplDataExportSourceSchema/delete'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'APP-383', pluginId: 14).save();    // url: '/appDbInstance/dropDownTableColumnReload'

        // dplDataImport
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-52', pluginId: 14).save();     // url: '/dplDataImport/show'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-53', pluginId: 14).save();     // url: '/dplDataImport/list'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-54', pluginId: 14).save();     // url: '/dplDataImport/create'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-55', pluginId: 14).save();     // url: '/dplDataImport/update'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-56', pluginId: 14).save();     // url: '/dplDataImport/delete'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-57', pluginId: 14).save();     // url: '/dplDataImport/clearLog'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-58', pluginId: 14).save();     // url: '/dplDataImport/showReport'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-59', pluginId: 14).save();    // url: '/dplDataImport/approve'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-60', pluginId: 14).save();    // url: '/dplDataImport/unApprove'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-61', pluginId: 14).save();    // url: '/dplDataImport/stopExecution'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-62', pluginId: 14).save();    // url: '/dplDataImport/executeImport'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-150', pluginId: 14).save();    // url: '/dplDataImport/checkImport'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-161', pluginId: 14).save();    // url: '/transactionLog/showForDplDataImport'


        // dplDataImportDetails
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-63', pluginId: 14).save();    // url: '/dplDataImportDetails/show'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-64', pluginId: 14).save();    // url: '/dplDataImportDetails/list'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-99', pluginId: 14).save();    // url: '/dplDataImportDetails/listExportedDataSource'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-65', pluginId: 14).save();    // url: '/dplDataImportDetails/create'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-67', pluginId: 14).save();    // url: '/dplDataImportDetails/update'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-68', pluginId: 14).save();    // url: '/dplDataImportDetails/delete'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-137', pluginId: 14).save();    // url: '/dplDataImportDetails/addAllTable'

        // dplCdcLog
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-78', pluginId: 14).save();    // url: '/dplCdcLog/showForMySql'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-79', pluginId: 14).save();    // url: '/dplCdcLog/listForMySql'

        // dplCdcMySql
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-80', pluginId: 14).save();    // url: '/dplCdcMySql/show'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-81', pluginId: 14).save();    // url: '/dplCdcMySql/list'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-82', pluginId: 14).save();    // url: '/dplCdcMySql/create'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-83', pluginId: 14).save();    // url: '/dplCdcMySql/update'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-84', pluginId: 14).save();    // url: '/dplCdcMySql/delete'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-94', pluginId: 14).save();    // url: '/dplCdcMySql/createInitialLog'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-95', pluginId: 14).save();    // url: '/dplCdcMySql/clearInitialLogFile'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-164', pluginId: 14).save();    // url: '/dplCdcMySql/approve'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-165', pluginId: 14).save();    // url: '/dplCdcMySql/unApprove'

        // dplCdcMsSql
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-108', pluginId: 14).save();   // url: '/dplCdcMsSql/show'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-154', pluginId: 14).save();    // url: '/dplCdcMsSql/list'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-142', pluginId: 14).save();    // url: '/dplCdcMsSql/create'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-143', pluginId: 14).save();    // url: '/dplCdcMsSql/update'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-144', pluginId: 14).save();    // url: '/dplCdcMsSql/delete'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-156', pluginId: 14).save();    // url: '/dplCdcMsSql/approve'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-157', pluginId: 14).save();    // url: '/dplCdcMsSql/unApprove'

        // dplCdcLog for MsSql
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-153', pluginId: 14).save();   // url: '/dplCdcLog/showForMsSql'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-155', pluginId: 14).save();    // url: '/dplCdcLog/listForMsSql'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-158', pluginId: 14).save();    // url: '/dplCdcLog/clearLogForMsSql'

        // dplCdcSourceSchemaMySql
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-88', pluginId: 14).save();    // url: '/dplCdcSourceSchemaMySql/show'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-89', pluginId: 14).save();    // url: '/dplCdcSourceSchemaMySql/list'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-90', pluginId: 14).save();    // url: '/dplCdcSourceSchemaMySql/create'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-91', pluginId: 14).save();    // url: '/dplCdcSourceSchemaMySql/update'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-92', pluginId: 14).save();    // url: '/dplCdcSourceSchemaMySql/delete'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-166', pluginId: 14).save();    // url: '/dplCdcSourceSchemaMySql/addAllTable'

        // dplCdcSourceSchemaMsSql
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-123', pluginId: 14).save();    // url: '/dplCdcSourceSchemaMsSql/show'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-124', pluginId: 14).save();    // url: '/dplCdcSourceSchemaMsSql/list'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-125', pluginId: 14).save();    // url: '/dplCdcSourceSchemaMsSql/create'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-126', pluginId: 14).save();    // url: '/dplCdcSourceSchemaMsSql/update'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-127', pluginId: 14).save();    // url: '/dplCdcSourceSchemaMsSql/delete'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-128', pluginId: 14).save();    // url: '/dplCdcSourceSchemaMsSql/dropDownTableReload'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-136', pluginId: 14).save();    // url: '/dplCdcSourceSchemaMsSql/addAllTable'

        // dplReport
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-102', pluginId: 14).save();    // url: '/dplReport/downloadDataExportSummary'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-103', pluginId: 14).save();    // url: '/dplReport/downloadDataImportSummary'

        //icon dashboard
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-109', pluginId: 14).save();    // url: '/appDbInstance/showForDplDashboard'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-110', pluginId: 14).save();    // url: '/appDbInstance/listVendorForDplDashboard'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-111', pluginId: 14).save();    // url: '/appDbInstance/listDbForDplDashboard'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-113', pluginId: 14).save();    // url: '/appDbInstance/createDbInstanceForDplDashboard'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-159', pluginId: 14).save();    // url: '/appDbInstance/updateDbInstanceForDplDashboard'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-138', pluginId: 14).save();    // url: '/appDbInstance/testDbForDplDashboard'

        //dpl table
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-114', pluginId: 14).save();    // url: '/dplTable/showForManageSchema'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-115', pluginId: 14).save();    // url: '/dplTable/scanAndCopyForManageSchema'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-116', pluginId: 14).save();    // url: '/dplTable/distributeForManageSchema'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-117', pluginId: 14).save();    // url: '/dplTable/removeAllForManageSchema'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-122', pluginId: 14).save();    // url: '/dplTable/previewForManageSchema'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-162', pluginId: 14).save();    // url: '/dplTable/previewForManageSchemaForRedShift'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'APP-416', pluginId: 14).save();    // url: '/dplTable/renderVendorThumbImage'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'APP-417', pluginId: 14).save();    // url: '/dplTable/renderVendorSmallImage'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-131', pluginId: 14).save();    // url: '/dplTable/generateScript'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-163', pluginId: 14).save();    // url: '/dplTable/generateScriptForRedshift'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-132', pluginId: 14).save();    // url: '/dplTable/viewAllScript'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-133', pluginId: 14).save();    // url: '/dplTable/updateScript'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-134', pluginId: 14).save();    // url: '/dplTable/runScript'

        //dpl field
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-118', pluginId: 14).save();    // url: '/dplField/show'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-119', pluginId: 14).save();    // url: '/dplField/list'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-120', pluginId: 14).save();    // url: '/dplField/setPrimaryForManageSchema'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-121', pluginId: 14).save();    // url: '/dplField/distributeDplFieldForManageSchema'

        //Dpl SQL Console
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-140', pluginId: 14).save();    // url: '/appDbInstance/showForSqlConsole'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-141', pluginId: 14).save();    // url: '/appDbInstance/listResultForSqlConsole'

        new RoleFeatureMapping(roleTypeId: -602, transactionCode: 'APP-180', pluginId: 14).save(); // url: '/appSms/showSms'
        new RoleFeatureMapping(roleTypeId: -602, transactionCode: 'APP-181', pluginId: 14).save(); // url: '/appSms/updateSms'
        new RoleFeatureMapping(roleTypeId: -602, transactionCode: 'APP-182', pluginId: 14).save(); // url: '/appSms/listSms'
        new RoleFeatureMapping(roleTypeId: -602, transactionCode: 'APP-237', pluginId: 14).save(); // url: '/appSms/sendSms'

        //Dpl Cdc Primary Key MsSql
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-145', pluginId: 14).save();    // url: '/dplCdcPrimaryKey/showForMsSql'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-152', pluginId: 14).save();    // url: '/dplCdcPrimaryKey/showForMySql'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-146', pluginId: 14).save();    // url: '/dplCdcPrimaryKey/list'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-147', pluginId: 14).save();    // url: '/dplCdcPrimaryKey/create'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-148', pluginId: 14).save();    // url: '/dplCdcPrimaryKey/update'
        new RoleFeatureMapping(roleTypeId: -601, transactionCode: 'DPL-149', pluginId: 14).save();    // url: '/dplCdcPrimaryKey/delete'

    } catch (Exception ex) {
        log.error(ex.getMessage())
        throw new RuntimeException(ex)
    }

    }

    public void createTestDataForBudget(long companyId) {
        Role roleDir = roleService.findByNameAndCompanyId('Director', companyId)
        Role rolePd = roleService.findByNameAndCompanyId('Project Director', companyId)

        // render budget menu
        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-38', pluginId: 3).save(); //   url: '/budgPlugin/renderBudgetMenu'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-38', pluginId: 3).save(); //   url: '/budgPlugin/renderBudgetMenu'

        // Budget
        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-1', pluginId: 3).save(); //   url: '/budgBudget/show'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-1', pluginId: 3).save(); //   url: '/budgBudget/show'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-101', pluginId: 3).save(); //   url: '/budgBudget/showForProduction'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-101', pluginId: 3).save(); //   url: '/budgBudget/showForProduction'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-2', pluginId: 3).save(); //   url: '/budgBudget/create'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-2', pluginId: 3).save(); //   url: '/budgBudget/create'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-4', pluginId: 3).save(); //   url: '/budgBudget/update'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-4', pluginId: 3).save(); //   url: '/budgBudget/update'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-5', pluginId: 3).save(); //   url: '/budgBudget/delete'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-5', pluginId: 3).save(); //   url: '/budgBudget/delete'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-6', pluginId: 3).save(); //   url: '/budgBudget/list'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-6', pluginId: 3).save(); //   url: '/budgBudget/list'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-83', pluginId: 3).save(); //   url: '/budgBudget/getBudgetGridForSprint'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-83', pluginId: 3).save(); //   url: '/budgBudget/getBudgetGridForSprint'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-37', pluginId: 3).save(); //   url: '/budgBudget/listBudgetStatus'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-37', pluginId: 3).save(); //   url: '/budgBudget/listBudgetStatus'

        // Budget Details
        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-9', pluginId: 3).save(); //   url: '/budgBudgetDetails/show'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-9', pluginId: 3).save(); //   url: '/budgBudgetDetails/show'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-10', pluginId: 3).save(); //   url: '/budgBudgetDetails/create'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-10', pluginId: 3).save(); //   url: '/budgBudgetDetails/create'


        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-12', pluginId: 3).save(); //   url: '/budgBudgetDetails/update'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-12', pluginId: 3).save(); //   url: '/budgBudgetDetails/update'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-13', pluginId: 3).save(); //   url: '/budgBudgetDetails/delete'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-13', pluginId: 3).save(); //   url: '/budgBudgetDetails/delete'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-14', pluginId: 3).save(); //   url: '/budgBudgetDetails/list'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-14', pluginId: 3).save(); //   url: '/budgBudgetDetails/list'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-15', pluginId: 3).save(); //   url: '/budgBudgetDetails/getItemListBudgetDetails'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-15', pluginId: 3).save(); //   url: '/budgBudgetDetails/getItemListBudgetDetails'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-90', pluginId: 3).save(); //   url: '/budgBudgetDetails/generateBudgetRequirement'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-90', pluginId: 3).save(); //   url: '/budgBudgetDetails/generateBudgetRequirement'

        // Budget Schema
        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-60', pluginId: 3).save(); //   url: '/budgSchema/show'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-60', pluginId: 3).save(); //   url: '/budgSchema/show'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-61', pluginId: 3).save(); //   url: '/budgSchema/create'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-61', pluginId: 3).save(); //   url: '/budgSchema/create'


        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-63', pluginId: 3).save(); //   url: '/budgSchema/update'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-63', pluginId: 3).save(); //   url: '/budgSchema/update'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-64', pluginId: 3).save(); //   url: '/budgSchema/delete'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-64', pluginId: 3).save(); //   url: '/budgSchema/delete'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-65', pluginId: 3).save(); //   url: '/budgSchema/list'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-65', pluginId: 3).save(); //   url: '/budgSchema/list'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-66', pluginId: 3).save(); //   url: '/budgSchema/listItemForBudgetSchema'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-66', pluginId: 3).save(); //   url: '/budgSchema/listItemForBudgetSchema'

        // sprint
        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-67', pluginId: 3).save(); //   url: '/budgSprint/show'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-67', pluginId: 3).save(); //   url: '/budgSprint/show'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-68', pluginId: 3).save(); //   url: '/budgSprint/create'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-68', pluginId: 3).save(); //   url: '/budgSprint/create'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-70', pluginId: 3).save(); //   url: '/budgSprint/update'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-70', pluginId: 3).save(); //   url: '/budgSprint/update'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-71', pluginId: 3).save(); //   url: '/budgSprint/delete'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-71', pluginId: 3).save(); //   url: '/budgSprint/delete'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-72', pluginId: 3).save(); //   url: '/budgSprint/list'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-72', pluginId: 3).save(); //   url: '/budgSprint/list'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-84', pluginId: 3).save(); //   url: '/budgSprint/setCurrentBudgSprint'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-84', pluginId: 3).save(); //   url: '/budgSprint/setCurrentBudgSprint'

        // sprint budget mapping
        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-73', pluginId: 3).save(); //   url: '/budgSprintBudget/show'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-73', pluginId: 3).save(); //   url: '/budgSprintBudget/show'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-74', pluginId: 3).save(); //   url: '/budgSprintBudget/create'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-74', pluginId: 3).save(); //   url: '/budgSprintBudget/create'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-76', pluginId: 3).save(); //   url: '/budgSprintBudget/update'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-76', pluginId: 3).save(); //   url: '/budgSprintBudget/update'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-77', pluginId: 3).save(); //   url: '/budgSprintBudget/delete'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-77', pluginId: 3).save(); //   url: '/budgSprintBudget/delete'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-78', pluginId: 3).save(); //   url: '/budgSprintBudget/list'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-78', pluginId: 3).save(); //   url: '/budgSprintBudget/list'

        // Budget Task
        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-54', pluginId: 3).save(); //   url: '/budgTask/show'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-54', pluginId: 3).save(); //   url: '/budgTask/show'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-55', pluginId: 3).save(); //   url: '/budgTask/create'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-55', pluginId: 3).save(); //   url: '/budgTask/create'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-57', pluginId: 3).save(); //   url: '/budgTask/update'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-57', pluginId: 3).save(); //   url: '/budgTask/update'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-58', pluginId: 3).save(); //   url: '/budgTask/delete'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-58', pluginId: 3).save(); //   url: '/budgTask/delete'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-59', pluginId: 3).save(); //   url: '/budgTask/list'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-59', pluginId: 3).save(); //   url: '/budgTask/list'

        // budget report
        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-26', pluginId: 3).save(); //   url: '/budgReport/showBudgetRpt'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-26', pluginId: 3).save(); //   url: '/budgReport/showBudgetRpt'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-27', pluginId: 3).save(); //   url: '/budgReport/searchBudgetRpt'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-27', pluginId: 3).save(); //   url: '/budgReport/searchBudgetRpt'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-28', pluginId: 3).save(); //   url: '/budgReport/downloadBudgetRpt'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-28', pluginId: 3).save(); //   url: '/budgReport/downloadBudgetRpt'

        // Project status
        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-31', pluginId: 3).save(); //   url: '/budgReport/showProjectStatus'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-31', pluginId: 3).save(); //   url: '/budgReport/showProjectStatus'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-32', pluginId: 3).save(); //   url: '/budgReport/searchProjectStatus'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-32', pluginId: 3).save(); //   url: '/budgReport/searchProjectStatus'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-36', pluginId: 3).save(); //   url: '/budgReport/downloadProjectStatus'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-36', pluginId: 3).save(); //   url: '/budgReport/downloadProjectStatus'

        // Project Costing
        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-51', pluginId: 3).save(); //   url: '/budgReport/listProjectCosting'
        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-52', pluginId: 3).save(); //   url: '/budgReport/downloadProjectCosting'
        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-53', pluginId: 3).save(); //   url: '/budgReport/showProjectCosting'

        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-51', pluginId: 3).save(); //   url: '/budgReport/listProjectCosting'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-52', pluginId: 3).save(); //   url: '/budgReport/downloadProjectCosting'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-53', pluginId: 3).save(); //   url: '/budgReport/showProjectCosting'

        // Consumption deviation
        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-40', pluginId: 3).save(); //   url: '/budgReport/showConsumptionDeviation'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-40', pluginId: 3).save(); //   url: '/budgReport/showConsumptionDeviation'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-41', pluginId: 3).save(); //   url: '/budgReport/listConsumptionDeviation'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-41', pluginId: 3).save(); //   url: '/budgReport/listConsumptionDeviation'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-42', pluginId: 3).save(); //   url: '/budgReport/downloadConsumptionDeviation'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-42', pluginId: 3).save(); //   url: '/budgReport/downloadConsumptionDeviation'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-43', pluginId: 3).save(); //   url: '/budgReport/downloadConsumptionDeviationCsv'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-43', pluginId: 3).save(); //   url: '/budgReport/downloadConsumptionDeviationCsv'

        // sprint report
        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-80', pluginId: 3).save(); //   url: '/budgReport/downloadSprintReport'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-80', pluginId: 3).save(); //   url: '/budgReport/downloadSprintReport'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-81', pluginId: 3).save(); //   url: '/budgReport/showBudgetSprint'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-81', pluginId: 3).save(); //   url: '/budgReport/showBudgetSprint'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-82', pluginId: 3).save(); //   url: '/budgReport/searchBudgetSprint'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-82', pluginId: 3).save(); //   url: '/budgReport/searchBudgetSprint'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-94', pluginId: 3).save(); //   url: '/budgReport/downloadForecastingReport'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-94', pluginId: 3).save(); //   url: '/budgReport/downloadForecastingReport'

        // project budget
        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-91', pluginId: 3).save(); //   url: '/budgReport/showProjectBudget'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-91', pluginId: 3).save(); //   url: '/budgReport/showProjectBudget'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-92', pluginId: 3).save(); //   url: '/budgReport/listProjectBudget'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-92', pluginId: 3).save(); //   url: '/budgReport/listProjectBudget'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-93', pluginId: 3).save(); //   url: '/budgReport/downloadProjectBudget'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-93', pluginId: 3).save(); //   url: '/budgReport/downloadProjectBudget'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-100', pluginId: 3).save(); //   url: '/budgReport/downloadBudgetByItem'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-100', pluginId: 3).save(); //   url: '/budgReport/downloadBudgetByItem'

        // master budget plan
        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-95', pluginId: 3).save(); //   url: '/budgReport/showMasterBudgetPlan'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-95', pluginId: 3).save(); //   url: '/budgReport/showMasterBudgetPlan'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-96', pluginId: 3).save(); //   url: '/budgReport/listMasterBudgetPlan'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-96', pluginId: 3).save(); //   url: '/budgReport/listMasterBudgetPlan'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-97', pluginId: 3).save(); //   url: '/budgReport/downloadMasterBudgetPlan'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-97', pluginId: 3).save(); //   url: '/budgReport/downloadMasterBudgetPlan'

        // drop down budget scope
        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'BUDG-25', pluginId: 3).save(); //   url: '/budgProjectBudgetScope/getBudgetScope'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'BUDG-25', pluginId: 3).save(); //   url: '/budgProjectBudgetScope/getBudgetScope'

        // AppAttachment for director
        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'APP-144', pluginId: 3).save(); // url: '/appAttachment/show'
        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'APP-146', pluginId: 3).save(); // url: '/appAttachment/list'
        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'APP-147', pluginId: 3).save(); // url: '/appAttachment/update'
        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'APP-148', pluginId: 3).save(); // url: '/appAttachment/create'
        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'APP-149', pluginId: 3).save(); // url: '/appAttachment/delete'
        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'APP-421', pluginId: 3).save(); // url: '/appAttachment/upload'
        // drop down content category reload
        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'APP-153', pluginId: 3).save(); // url: '/contentCategory/dropDownContentCategoryReload'

        // AppAttachment for project director
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'APP-144', pluginId: 3).save(); // url: '/appAttachment/show'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'APP-146', pluginId: 3).save(); // url: '/appAttachment/list'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'APP-147', pluginId: 3).save(); // url: '/appAttachment/update'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'APP-148', pluginId: 3).save(); // url: '/appAttachment/create'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'APP-149', pluginId: 3).save(); // url: '/appAttachment/delete'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'APP-421', pluginId: 3).save(); // url: '/appAttachment/upload'
        // drop down content category reload
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'APP-153', pluginId: 3).save(); // url: '/contentCategory/dropDownContentCategoryReload'
    }

    public void createTestDataForProcurement(long companyId) {
        Role roleDir = roleService.findByNameAndCompanyId('Director', companyId)
        Role rolePd = roleService.findByNameAndCompanyId('Project Director', companyId)

        // render proc dashboard
        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-78', pluginId: 5).save(); // url: '/procPlugin/renderProcurementMenu'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-78', pluginId: 5).save(); // url: '/procPlugin/renderProcurementMenu'

        // cancelled PO
        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-97', pluginId: 5).save(); // url:  '/procCancelledPO/show'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-97', pluginId: 5).save(); // url:  '/procCancelledPO/show'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-98', pluginId: 5).save(); // url:  '/procCancelledPO/list'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-98', pluginId: 5).save(); // url:  '/procCancelledPO/list'

        // purchase order
        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-22', pluginId: 5).save(); // url:  '/procPurchaseOrder/approve'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-22', pluginId: 5).save(); // url:  '/procPurchaseOrder/approve'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-80', pluginId: 5).save(); // url:  '/procPurchaseOrder/approvePODashBoard'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-80', pluginId: 5).save(); // url:  '/procPurchaseOrder/approvePODashBoard'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-94', pluginId: 5).save(); // url:  '/procPurchaseOrder/cancelPO'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-94', pluginId: 5).save(); // url:  '/procPurchaseOrder/cancelPO'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-16', pluginId: 5).save(); // url:  '/procPurchaseOrder/create'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-16', pluginId: 5).save(); // url:  '/procPurchaseOrder/create'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-19', pluginId: 5).save(); // url:  '/procPurchaseOrder/delete'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-19', pluginId: 5).save(); // url:  '/procPurchaseOrder/delete'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-90', pluginId: 5).save(); // url:  '/procPurchaseOrder/getPOStatusForDashBoard'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-90', pluginId: 5).save(); // url:  '/procPurchaseOrder/getPOStatusForDashBoard'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-20', pluginId: 5).save(); // url:  '/procPurchaseOrder/list'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-20', pluginId: 5).save(); // url:  '/procPurchaseOrder/list'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-79', pluginId: 5).save(); // url:  '/procPurchaseOrder/listUnApprovedPO'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-79', pluginId: 5).save(); // url:  '/procPurchaseOrder/listUnApprovedPO'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-92', pluginId: 5).save(); // url:  '/procPurchaseOrder/sendForPOApproval'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-92', pluginId: 5).save(); // url:  '/procPurchaseOrder/sendForPOApproval'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-15', pluginId: 5).save(); // url:  '/procPurchaseOrder/show'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-15', pluginId: 5).save(); // url:  '/procPurchaseOrder/show'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-95', pluginId: 5).save(); // url:  '/procPurchaseOrder/unApprovePO'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-95', pluginId: 5).save(); // url:  '/procPurchaseOrder/unApprovePO'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-18', pluginId: 5).save(); // url:  '/procPurchaseOrder/update'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-18', pluginId: 5).save(); // url:  '/procPurchaseOrder/update'

        // Purchase Order Details
        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-24', pluginId: 5).save(); // url:  '/procPurchaseOrderDetails/create'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-24', pluginId: 5).save(); // url:  '/procPurchaseOrderDetails/create'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-27', pluginId: 5).save(); // url:  '/procPurchaseOrderDetails/delete'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-27', pluginId: 5).save(); // url:  '/procPurchaseOrderDetails/delete'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-30', pluginId: 5).save(); // url:  '/procPurchaseOrderDetails/getItemListPurchaseOrderDetails'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-30', pluginId: 5).save(); // url:  '/procPurchaseOrderDetails/getItemListPurchaseOrderDetails'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-28', pluginId: 5).save(); // url:  '/procPurchaseOrderDetails/list'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-28', pluginId: 5).save(); // url:  '/procPurchaseOrderDetails/list'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-23', pluginId: 5).save(); // url:  '/procPurchaseOrderDetails/show'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-23', pluginId: 5).save(); // url:  '/procPurchaseOrderDetails/show'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-26', pluginId: 5).save(); // url:  '/procPurchaseOrderDetails/update'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-26', pluginId: 5).save(); // url:  '/procPurchaseOrderDetails/update'

        // Purchase Request
        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-7', pluginId: 5).save(); // url:  '/procPurchaseRequest/approve'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-7', pluginId: 5).save(); // url:  '/procPurchaseRequest/approve'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-84', pluginId: 5).save(); // url:  '/procPurchaseRequest/approvePRDashBoard'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-84', pluginId: 5).save(); // url:  '/procPurchaseRequest/approvePRDashBoard'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-2', pluginId: 5).save(); // url: '/procPurchaseRequest/create'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-2', pluginId: 5).save(); // url: '/procPurchaseRequest/create'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-5', pluginId: 5).save(); // url:  '/procPurchaseRequest/delete'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-5', pluginId: 5).save(); // url:  '/procPurchaseRequest/delete'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-6', pluginId: 5).save(); // url:  '/procPurchaseRequest/list'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-6', pluginId: 5).save(); // url:  '/procPurchaseRequest/list'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-69', pluginId: 5).save(); // url:  '/procPurchaseRequest/listIndentByProject'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-69', pluginId: 5).save(); // url:  '/procPurchaseRequest/listIndentByProject'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-83', pluginId: 5).save(); // url:  '/procPurchaseRequest/listUnApprovedPR'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-83', pluginId: 5).save(); // url:  '/procPurchaseRequest/listUnApprovedPR'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-93', pluginId: 5).save(); // url:  '/procPurchaseRequest/sentMailForPRApproval'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-93', pluginId: 5).save(); // url:  '/procPurchaseRequest/sentMailForPRApproval'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-1', pluginId: 5).save(); // url: '/procPurchaseRequest/show'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-1', pluginId: 5).save(); // url: '/procPurchaseRequest/show'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-96', pluginId: 5).save(); // url: '/procPurchaseRequest/unApprovePR'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-96', pluginId: 5).save(); // url: '/procPurchaseRequest/unApprovePR'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-4', pluginId: 5).save(); // url:  '/procPurchaseRequest/update'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-4', pluginId: 5).save(); // url:  '/procPurchaseRequest/update'

        // Purchase Request Details
        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-9', pluginId: 5).save(); // url:  '/procPurchaseRequestDetails/create'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-9', pluginId: 5).save(); // url:  '/procPurchaseRequestDetails/create'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-12', pluginId: 5).save(); // url:  '/procPurchaseRequestDetails/delete'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-12', pluginId: 5).save(); // url:  '/procPurchaseRequestDetails/delete'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-14', pluginId: 5).save(); // url:  '/procPurchaseRequestDetails/getItemList'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-14', pluginId: 5).save(); // url:  '/procPurchaseRequestDetails/getItemList'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-13', pluginId: 5).save(); // url:  '/procPurchaseRequestDetails/list'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-13', pluginId: 5).save(); // url:  '/procPurchaseRequestDetails/list'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-8', pluginId: 5).save(); // url:  '/procPurchaseRequestDetails/show'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-8', pluginId: 5).save(); // url:  '/procPurchaseRequestDetails/show'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-11', pluginId: 5).save(); // url:  '/procPurchaseRequestDetails/update'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-11', pluginId: 5).save(); // url:  '/procPurchaseRequestDetails/update'

        // Proc terms and condition
        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-71', pluginId: 5).save(); // url:  '/procTermsAndCondition/create'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-71', pluginId: 5).save(); // url:  '/procTermsAndCondition/create'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-74', pluginId: 5).save(); // url:  '/procTermsAndCondition/delete'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-74', pluginId: 5).save(); // url:  '/procTermsAndCondition/delete'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-75', pluginId: 5).save(); // url:  '/procTermsAndCondition/list'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-75', pluginId: 5).save(); // url:  '/procTermsAndCondition/list'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-70', pluginId: 5).save(); // url:  '/procTermsAndCondition/show'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-70', pluginId: 5).save(); // url:  '/procTermsAndCondition/show'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-73', pluginId: 5).save(); // url:  '/procTermsAndCondition/update'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-73', pluginId: 5).save(); // url:  '/procTermsAndCondition/update'

        // Proc Transport Cost
        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-54', pluginId: 5).save(); // url:  '/procTransportCost/create'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-54', pluginId: 5).save(); // url:  '/procTransportCost/create'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-57', pluginId: 5).save(); // url:  '/procTransportCost/delete'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-57', pluginId: 5).save(); // url:  '/procTransportCost/delete'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-58', pluginId: 5).save(); // url:  '/procTransportCost/list'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-58', pluginId: 5).save(); // url:  '/procTransportCost/list'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-53', pluginId: 5).save(); // url:  '/procTransportCost/show'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-53', pluginId: 5).save(); // url:  '/procTransportCost/show'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-56', pluginId: 5).save(); // url:  '/procTransportCost/update'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-56', pluginId: 5).save(); // url:  '/procTransportCost/update'

        // report
        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-99', pluginId: 5).save(); // url:  '/procReport/showCancelledPORpt'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-99', pluginId: 5).save(); // url:  '/procReport/showCancelledPORpt'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-100', pluginId: 5).save(); // url:  '/procReport/searchCancelledPORpt'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-100', pluginId: 5).save(); // url:  '/procReport/searchCancelledPORpt'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-101', pluginId: 5).save(); // url:  '/procReport/downloadCancelledPORpt'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-101', pluginId: 5).save(); // url:  '/procReport/downloadCancelledPORpt'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-47', pluginId: 5).save(); // url:  '/procReport/showPurchaseOrderRpt'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-47', pluginId: 5).save(); // url:  '/procReport/showPurchaseOrderRpt'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-48', pluginId: 5).save(); // url:  '/procReport/searchPurchaseOrderRpt'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-48', pluginId: 5).save(); // url:  '/procReport/searchPurchaseOrderRpt'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-49', pluginId: 5).save(); // url:  '/procReport/downloadPurchaseOrderRpt'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-49', pluginId: 5).save(); // url:  '/procReport/downloadPurchaseOrderRpt'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-44', pluginId: 5).save(); // url:  '/procReport/showPurchaseRequestRpt'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-44', pluginId: 5).save(); // url:  '/procReport/showPurchaseRequestRpt'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-45', pluginId: 5).save(); // url:  '/procReport/searchPurchaseRequestRpt'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-45', pluginId: 5).save(); // url:  '/procReport/searchPurchaseRequestRpt'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-46', pluginId: 5).save(); // url:  '/procReport/downloadPurchaseRequestRpt'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-46', pluginId: 5).save(); // url:  '/procReport/downloadPurchaseRequestRpt'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-76', pluginId: 5).save(); // url:  '/procReport/showSupplierWisePO'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-76', pluginId: 5).save(); // url:  '/procReport/showSupplierWisePO'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-77', pluginId: 5).save(); // url:  '/procReport/listSupplierWisePO'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-77', pluginId: 5).save(); // url:  '/procReport/listSupplierWisePO'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-82', pluginId: 5).save(); // url:  '/procReport/downloadSupplierWisePO'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-82', pluginId: 5).save(); // url:  '/procReport/downloadSupplierWisePO'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'PROC-91', pluginId: 5).save(); // url:  '/procReport/downloadSupplierWisePOCsv'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'PROC-91', pluginId: 5).save(); // url:  '/procReport/downloadSupplierWisePOCsv'

        new RoleFeatureMapping(roleTypeId: roleDir.id, transactionCode: 'INV-111', pluginId: 5).save(); // url: '/supplier/listAllSupplier'
        new RoleFeatureMapping(roleTypeId: rolePd.id, transactionCode: 'INV-111', pluginId: 5).save(); // url: '/supplier/listAllSupplier'
    }

    public void createTestDataForAccounting() {
        Role accountant = roleService.findByNameAndCompanyId('Accountant', companyId)
        Role cfo = roleService.findByNameAndCompanyId('CFO', companyId)

        // default
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-157', pluginId: 2).save(); // url: '/accPlugin/renderAccountingMenu'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-157', pluginId: 2).save(); // url: '/accPlugin/renderAccountingMenu'

        // Acc Custom Group
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-1', pluginId: 2).save(); // url:  '/accCustomGroup/show'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-2', pluginId: 2).save(); // url:  '/accCustomGroup/create'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-4', pluginId: 2).save(); // url:  '/accCustomGroup/update'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-5', pluginId: 2).save(); // url:  '/accCustomGroup/delete'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-6', pluginId: 2).save(); // url:  '/accCustomGroup/list'

        // Acc Chart of Account
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-7', pluginId: 2).save(); // url:  '/accChartOfAccount/show'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-8', pluginId: 2).save(); // url:  '/accChartOfAccount/create'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-9', pluginId: 2).save(); // url:  '/accChartOfAccount/select'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-10', pluginId: 2).save(); // url:  '/accChartOfAccount/update'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-11', pluginId: 2).save(); // url:  '/accChartOfAccount/delete'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-12', pluginId: 2).save(); // url:  '/accChartOfAccount/list'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-148', pluginId: 2).save(); // url:  '/accChartOfAccount/listSourceCategoryByAccSource'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-13', pluginId: 2).save(); // url: '/accChartOfAccount/listForVoucher'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-13', pluginId: 2).save(); // url: '/accChartOfAccount/listForVoucher'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-14', pluginId: 2).save(); // url: '/accChartOfAccount/listSourceByCoaCode'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-14', pluginId: 2).save(); // url: '/accChartOfAccount/listSourceByCoaCode'

        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-147', pluginId: 2).save(); // url: '/accChartOfAccount/listAccChartOfAccountByAccGroupId'

        // Acc Group
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-15', pluginId: 2).save(); // url: '/accGroup/show'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-16', pluginId: 2).save(); // url: '/accGroup/create'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-18', pluginId: 2).save(); // url: '/accGroup/update'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-19', pluginId: 2).save(); // url: '/accGroup/delete'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-20', pluginId: 2).save(); // url: '/accGroup/list'

        // Acc Tier1
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-21', pluginId: 2).save(); // url: '/accTier1/show'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-22', pluginId: 2).save(); // url: '/accTier1/create'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-24', pluginId: 2).save(); // url: '/accTier1/update'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-25', pluginId: 2).save(); // url: '/accTier1/delete'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-26', pluginId: 2).save(); // url: '/accTier1/list'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-27', pluginId: 2).save(); // url: '/accTier1/listTier1ByAccTypeId'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-27', pluginId: 2).save(); // url: '/accTier1/listTier1ByAccTypeId'

        // Acc Tier2
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-35', pluginId: 2).save(); // url: '/accTier2/show'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-36', pluginId: 2).save(); // url: '/accTier2/create'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-38', pluginId: 2).save(); // url: '/accTier2/update'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-39', pluginId: 2).save(); // url: '/accTier2/delete'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-40', pluginId: 2).save(); // url: '/accTier2/list'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-41', pluginId: 2).save(); // url: '/accTier2/listTier2ByAccTier1Id'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-41', pluginId: 2).save(); // url: '/accTier2/listTier2ByAccTier1Id'

        // AccTier3
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-28', pluginId: 2).save(); // url: '/accTier3/show'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-29', pluginId: 2).save(); // url: '/accTier3/create'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-31', pluginId: 2).save(); // url: '/accTier3/update'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-32', pluginId: 2).save(); // url: '/accTier3/delete'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-33', pluginId: 2).save(); // url: '/accTier3/list'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-34', pluginId: 2).save(); // url: '/accTier3/listTier3ByAccTier2Id'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-34', pluginId: 2).save(); // url: '/accTier3/listTier3ByAccTier2Id'

        // Acc Ipc
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-185', pluginId: 2).save(); // url: '/accIpc/show'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-186', pluginId: 2).save(); // url: '/accIpc/create'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-188', pluginId: 2).save(); // url: '/accIpc/update'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-189', pluginId: 2).save(); // url: '/accIpc/delete'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-190', pluginId: 2).save(); // url: '/accIpc/list'

        // Acc Lc
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-191', pluginId: 2).save(); // url: '/accLc/show'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-192', pluginId: 2).save(); // url: '/accLc/create'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-193', pluginId: 2).save(); // url: '/accLc/select'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-194', pluginId: 2).save(); // url: '/accLc/update'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-195', pluginId: 2).save(); // url: '/accLc/delete'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-196', pluginId: 2).save(); // url: '/accLc/list'

        // Acc Lease Account
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-198', pluginId: 2).save(); // url: '/accLeaseAccount/show'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-199', pluginId: 2).save(); // url: '/accLeaseAccount/create'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-201', pluginId: 2).save(); // url: '/accLeaseAccount/update'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-202', pluginId: 2).save(); // url: '/accLeaseAccount/delete'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-203', pluginId: 2).save(); // url: '/accLeaseAccount/list'

        // Voucher-Type implementation
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-42', pluginId: 2).save(); // url: '/accVoucherTypeCoa/show'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-43', pluginId: 2).save(); // url: '/accVoucherTypeCoa/create'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-45', pluginId: 2).save(); // url: '/accVoucherTypeCoa/update'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-46', pluginId: 2).save(); // url: '/accVoucherTypeCoa/delete'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-47', pluginId: 2).save(); // url: '/accVoucherTypeCoa/list'

        // Acc Sub-Account implementation
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-48', pluginId: 2).save(); // url: '/accSubAccount/show'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-49', pluginId: 2).save(); // url: '/accSubAccount/create'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-51', pluginId: 2).save(); // url: '/accSubAccount/update'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-52', pluginId: 2).save(); // url: '/accSubAccount/delete'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-53', pluginId: 2).save(); // url: '/accSubAccount/list'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-54', pluginId: 2).save(); // url: '/accSubAccount/listSubAccountByCoaId'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-54', pluginId: 2).save(); // url: '/accSubAccount/listSubAccountByCoaId'

        // Acc Division implementation
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-55', pluginId: 2).save(); // url: '/accDivision/show'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-56', pluginId: 2).save(); // url: '/accDivision/create'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-58', pluginId: 2).save(); // url: '/accDivision/update'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-59', pluginId: 2).save(); // url: '/accDivision/delete'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-60', pluginId: 2).save(); // url: '/accDivision/list'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-61', pluginId: 2).save(); // url: '/accDivision/listDivisionListByProjectId'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-61', pluginId: 2).save(); // url: '/accDivision/listDivisionListByProjectId'

        // voucher
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-62', pluginId: 2).save(); // url: '/accVoucher/show'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-62', pluginId: 2).save(); // url: '/accVoucher/show'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-63', pluginId: 2).save(); // url: '/accVoucher/create'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-63', pluginId: 2).save(); // url: '/accVoucher/create'

        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-251', pluginId: 2).save(); // url: '/accVoucher/cancelVoucher

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-64', pluginId: 2).save(); // url: '/accVoucher/select'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-64', pluginId: 2).save(); // url: '/accVoucher/select'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-65', pluginId: 2).save(); // url: '/accVoucher/update'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-65', pluginId: 2).save(); // url: '/accVoucher/update'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-66', pluginId: 2).save(); // url: '/accVoucher/list'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-66', pluginId: 2).save(); // url: '/accVoucher/list'

        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-67', pluginId: 2).save(); // url: '/accVoucher/postVoucher'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-68', pluginId: 2).save(); // url: '/accVoucher/showPayCash'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-68', pluginId: 2).save(); // url: '/accVoucher/showPayCash'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-69', pluginId: 2).save(); // url: '/accVoucher/listPayCash'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-69', pluginId: 2).save(); // url: '/accVoucher/listPayCash'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-70', pluginId: 2).save(); // url: '/accVoucher/showPayBank'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-70', pluginId: 2).save(); // url: '/accVoucher/showPayBank'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-71', pluginId: 2).save(); // url: '/accVoucher/listPayBank'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-71', pluginId: 2).save(); // url: '/accVoucher/listPayBank'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-72', pluginId: 2).save(); // url: '/accVoucher/showReceiveCash'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-72', pluginId: 2).save(); // url: '/accVoucher/showReceiveCash'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-73', pluginId: 2).save(); // url: '/accVoucher/listReceiveCash'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-73', pluginId: 2).save(); // url: '/accVoucher/listReceiveCash'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-74', pluginId: 2).save(); // url: '/accVoucher/showReceiveBank'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-74', pluginId: 2).save(); // url: '/accVoucher/showReceiveBank'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-75', pluginId: 2).save(); // url: '/accVoucher/listReceiveBank'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-75', pluginId: 2).save(); // url: '/accVoucher/listReceiveBank'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-76', pluginId: 2).save(); // url: '/accReport/downloadChartOfAccounts'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-76', pluginId: 2).save(); // url: '/accReport/downloadChartOfAccounts'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-152', pluginId: 2).save(); // url: '/accVoucher/listOfUnApprovedPayCash'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-152', pluginId: 2).save(); // url: '/accVoucher/listOfUnApprovedPayCash'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-153', pluginId: 2).save(); // url: '/accVoucher/listOfUnApprovedPayBank'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-153', pluginId: 2).save(); // url: '/accVoucher/listOfUnApprovedPayBank'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-154', pluginId: 2).save(); // url: '/accVoucher/listOfUnApprovedReceiveCash'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-154', pluginId: 2).save(); // url: '/accVoucher/listOfUnApprovedReceiveCash'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-155', pluginId: 2).save(); // url: '/accVoucher/listOfUnApprovedReceiveBank'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-155', pluginId: 2).save(); // url: '/accVoucher/listOfUnApprovedReceiveBank'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-156', pluginId: 2).save(); // url: '/accVoucher/listOfUnApprovedJournal'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-156', pluginId: 2).save(); // url: '/accVoucher/listOfUnApprovedJournal'

        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-96', pluginId: 2).save(); // url: '/accVoucher/unPostedVoucher'

        //Acc cancelled voucher
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-261', pluginId: 2).save(); // url: '/accCancelledVoucher/showCancelledVoucher'
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-262', pluginId: 2).save(); // url: '/accCancelledVoucher/listCancelledVoucher'

        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-261', pluginId: 2).save(); // url: '/accCancelledVoucher/showCancelledVoucher'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-262', pluginId: 2).save(); // url: '/accCancelledVoucher/listCancelledVoucher'

        // voucher report
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-77', pluginId: 2).save(); // url: '/accReport/showVoucher'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-77', pluginId: 2).save(); // url: '/accReport/showVoucher'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-78', pluginId: 2).save(); // url: '/accReport/searchVoucher'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-78', pluginId: 2).save(); // url: '/accReport/searchVoucher'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-79', pluginId: 2).save(); // url: '/accReport/downloadVoucher'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-79', pluginId: 2).save(); // url: '/accReport/downloadVoucher'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-129', pluginId: 2).save(); // url: '/accReport/downloadVoucherBankCheque'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-129', pluginId: 2).save(); // url: '/accReport/downloadVoucherBankCheque'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-130', pluginId: 2).save(); // url: '/accReport/downloadVoucherBankChequePreview'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-130', pluginId: 2).save(); // url: '/accReport/downloadVoucherBankChequePreview'

        // voucher List
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-80', pluginId: 2).save(); // url: '/accReport/showVoucherList'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-80', pluginId: 2).save(); // url: '/accReport/showVoucherList'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-81', pluginId: 2).save(); // url: '/accReport/searchVoucherList'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-81', pluginId: 2).save(); // url: '/accReport/searchVoucherList'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-82', pluginId: 2).save(); // url: '/accReport/downloadVoucherList'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-82', pluginId: 2).save(); // url: '/accReport/downloadVoucherList'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-83', pluginId: 2).save(); // url: '/accReport/listForVoucherDetails'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-83', pluginId: 2).save(); // url: '/accReport/listForVoucherDetails'

        // Ledger
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-84', pluginId: 2).save(); // url: '/accReport/showLedger'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-84', pluginId: 2).save(); // url: '/accReport/showLedger'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-85', pluginId: 2).save(); // url: '/accReport/listLedger'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-85', pluginId: 2).save(); // url: '/accReport/listLedger'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-86', pluginId: 2).save(); // url: '/accReport/downloadLedger'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-86', pluginId: 2).save(); // url: '/accReport/downloadLedger'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-175', pluginId: 2).save(); // url: '/accReport/downloadLedgerCsv'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-175', pluginId: 2).save(); // url: '/accReport/downloadLedgerCsv'

        // source Ledger
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-87', pluginId: 2).save(); // url: '/accReport/showSourceLedger'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-87', pluginId: 2).save(); // url: '/accReport/showSourceLedger'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-88', pluginId: 2).save(); // url: '/accReport/listSourceLedger'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-88', pluginId: 2).save(); // url: '/accReport/listSourceLedger'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-89', pluginId: 2).save(); // url: '/accReport/downloadSourceLedger'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-89', pluginId: 2).save(); // url: '/accReport/downloadSourceLedger'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-174', pluginId: 2).save(); // url: '/accReport/downloadSourceLedgerCsv'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-174', pluginId: 2).save(); // url: '/accReport/downloadSourceLedgerCsv'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-149', pluginId: 2).save(); // url: '/accReport/listSourceByCategoryAndType'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-149', pluginId: 2).save(); // url: '/accReport/listSourceByCategoryAndType'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-215', pluginId: 2).save(); // url: '/accReport/listSourceCategoryForSourceLedger'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-215', pluginId: 2).save(); // url: '/accReport/listSourceCategoryForSourceLedger'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-259', pluginId: 2).save(); // url: '/accReport/downloadSourceLedgeReportGroupBySource'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-259', pluginId: 2).save(); // url: '/accReport/downloadSourceLedgeReportGroupBySource'

        // Group Ledger
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-90', pluginId: 2).save(); // url: '/accReport/showForGroupLedgerRpt'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-90', pluginId: 2).save(); // url: '/accReport/showForGroupLedgerRpt'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-91', pluginId: 2).save(); // url: '/accReport/listForGroupLedgerRpt'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-91', pluginId: 2).save(); // url: '/accReport/listForGroupLedgerRpt'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-92', pluginId: 2).save(); // url: '/accReport/downloadForGroupLedgerRpt'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-92', pluginId: 2).save(); // url: '/accReport/downloadForGroupLedgerRpt'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-171', pluginId: 2).save(); // url: '/accReport/downloadForGroupLedgerCsvRpt'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-171', pluginId: 2).save(); // url: '/accReport/downloadForGroupLedgerCsvRpt'

        // trial Balance
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-216', pluginId: 2).save(); // url: '/accReport/showTrialBalanceOfLevel3'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-216', pluginId: 2).save(); // url: '/accReport/showTrialBalanceOfLevel3'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-217', pluginId: 2).save(); // url: '/accReport/listTrialBalanceOfLevel3'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-217', pluginId: 2).save(); // url: '/accReport/listTrialBalanceOfLevel3'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-218', pluginId: 2).save(); // url: '/accReport/downloadTrialBalanceOfLevel3'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-218', pluginId: 2).save(); // url: '/accReport/downloadTrialBalanceOfLevel3'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-219', pluginId: 2).save(); // url: '/accReport/downloadTrialBalanceCsvOfLevel3'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-219', pluginId: 2).save(); // url: '/accReport/downloadTrialBalanceCsvOfLevel3'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-220', pluginId: 2).save(); // url: '/accReport/showTrialBalanceOfLevel4'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-220', pluginId: 2).save(); // url: '/accReport/showTrialBalanceOfLevel4'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-221', pluginId: 2).save(); // url: '/accReport/listTrialBalanceOfLevel4'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-221', pluginId: 2).save(); // url: '/accReport/listTrialBalanceOfLevel4'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-222', pluginId: 2).save(); // url: '/accReport/downloadTrialBalanceOfLevel4'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-222', pluginId: 2).save(); // url: '/accReport/downloadTrialBalanceOfLevel4'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-223', pluginId: 2).save(); // url: '/accReport/downloadTrialBalanceCsvOfLevel4'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-223', pluginId: 2).save(); // url: '/accReport/downloadTrialBalanceCsvOfLevel4'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-224', pluginId: 2).save(); // url: '/accReport/showTrialBalanceOfLevel5'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-224', pluginId: 2).save(); // url: '/accReport/showTrialBalanceOfLevel5'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-225', pluginId: 2).save(); // url: '/accReport/listTrialBalanceOfLevel5'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-225', pluginId: 2).save(); // url: '/accReport/listTrialBalanceOfLevel5'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-226', pluginId: 2).save(); // url: '/accReport/downloadTrialBalanceOfLevel5'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-226', pluginId: 2).save(); // url: '/accReport/downloadTrialBalanceOfLevel5'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-227', pluginId: 2).save(); // url: '/accReport/downloadTrialBalanceCsvOfLevel5'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-227', pluginId: 2).save(); // url: '/accReport/downloadTrialBalanceCsvOfLevel5'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-247', pluginId: 2).save(); // url: '/accReport/showTrialBalanceOfLevel2'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-247', pluginId: 2).save(); // url: '/accReport/showTrialBalanceOfLevel2'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-248', pluginId: 2).save(); // url: '/accReport/listTrialBalanceOfLevel2'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-248', pluginId: 2).save(); // url: '/accReport/listTrialBalanceOfLevel2'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-249', pluginId: 2).save(); // url: '/accReport/downloadTrialBalanceOfLevel2'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-249', pluginId: 2).save(); // url: '/accReport/downloadTrialBalanceOfLevel2'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-250', pluginId: 2).save(); // url: '/accReport/downloadTrialBalanceCsvOfLevel2'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-250', pluginId: 2).save(); // url: '/accReport/downloadTrialBalanceCsvOfLevel2'

        // supplier payment report
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-97', pluginId: 2).save(); // url: '/accReport/showSupplierWisePayment'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-97', pluginId: 2).save(); // url: '/accReport/showSupplierWisePayment'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-98', pluginId: 2).save(); // url: '/accReport/listSupplierWisePayment'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-98', pluginId: 2).save(); // url: '/accReport/listSupplierWisePayment'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-99', pluginId: 2).save(); // url: '/accReport/downloadSupplierWisePayment'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-99', pluginId: 2).save(); // url: '/accReport/downloadSupplierWisePayment'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-173', pluginId: 2).save(); // url: '/accReport/downloadSupplierWisePaymentCsv'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-173', pluginId: 2).save(); // url: '/accReport/downloadSupplierWisePaymentCsv'

        // income Statement level 4
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-235', pluginId: 2).save(); // url: '/accReport/showIncomeStatementOfLevel4'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-235', pluginId: 2).save(); // url: '/accReport/showIncomeStatementOfLevel4'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-236', pluginId: 2).save(); // url: '/accReport/listIncomeStatementOfLevel4'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-236', pluginId: 2).save(); // url: '/accReport/listIncomeStatementOfLevel4'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-237', pluginId: 2).save(); // url: '/accReport/downloadIncomeStatementOfLevel4'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-237', pluginId: 2).save(); // url: '/accReport/downloadIncomeStatementOfLevel4'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-238', pluginId: 2).save(); // url: '/accReport/downloadIncomeStatementCsvOfLevel4'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-238', pluginId: 2).save(); // url: '/accReport/downloadIncomeStatementCsvOfLevel4'

        // income Statement level 5
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-239', pluginId: 2).save(); // url: '/accReport/showIncomeStatementOfLevel5'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-239', pluginId: 2).save(); // url: '/accReport/showIncomeStatementOfLevel5'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-240', pluginId: 2).save(); // url: '/accReport/listIncomeStatementOfLevel5'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-240', pluginId: 2).save(); // url: '/accReport/listIncomeStatementOfLevel5'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-241', pluginId: 2).save(); // url: '/accReport/downloadIncomeStatementOfLevel5'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-241', pluginId: 2).save(); // url: '/accReport/downloadIncomeStatementOfLevel5'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-242', pluginId: 2).save(); // url: '/accReport/downloadIncomeStatementCsvOfLevel5'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-242', pluginId: 2).save(); // url: '/accReport/downloadIncomeStatementCsvOfLevel5'

        // financial Statement level 5
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-210', pluginId: 2).save(); // url: '/accReport/showFinancialStatementOfLevel5'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-210', pluginId: 2).save(); // url: '/accReport/showFinancialStatementOfLevel5'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-211', pluginId: 2).save(); // url: '/accReport/listFinancialStatementOfLevel5'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-211', pluginId: 2).save(); // url: '/accReport/listFinancialStatementOfLevel5'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-212', pluginId: 2).save(); // url: '/accReport/downloadFinancialStatementOfLevel5'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-212', pluginId: 2).save(); // url: '/accReport/downloadFinancialStatementOfLevel5'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-234', pluginId: 2).save(); // url: '/accReport/downloadFinancialStatementCsvOfLevel5'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-234', pluginId: 2).save(); // url: '/accReport/downloadFinancialStatementCsvOfLevel5'

        // financial Statement level 4
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-213', pluginId: 2).save(); // url: '/accReport/showFinancialStatementOfLevel4'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-213', pluginId: 2).save(); // url: '/accReport/showFinancialStatementOfLevel4'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-214', pluginId: 2).save(); // url: '/accReport/listFinancialStatementOfLevel4'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-214', pluginId: 2).save(); // url: '/accReport/listFinancialStatementOfLevel4'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-228', pluginId: 2).save(); // url: '/accReport/downloadFinancialStatementOfLevel4'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-228', pluginId: 2).save(); // url: '/accReport/downloadFinancialStatementOfLevel4'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-233', pluginId: 2).save(); // url: '/accReport/downloadFinancialStatementCsvOfLevel4'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-233', pluginId: 2).save(); // url: '/accReport/downloadFinancialStatementCsvOfLevel4'

        // financial Statement level 3
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-229', pluginId: 2).save(); // url: '/accReport/showFinancialStatementOfLevel3'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-229', pluginId: 2).save(); // url: '/accReport/showFinancialStatementOfLevel3'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-230', pluginId: 2).save(); // url: '/accReport/listFinancialStatementOfLevel3'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-230', pluginId: 2).save(); // url: '/accReport/listFinancialStatementOfLevel3'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-231', pluginId: 2).save(); // url: '/accReport/downloadFinancialStatementOfLevel3'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-231', pluginId: 2).save(); // url: '/accReport/downloadFinancialStatementOfLevel3'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-232', pluginId: 2).save(); // url: '/accReport/downloadFinancialStatementCsvOfLevel3'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-232', pluginId: 2).save(); // url: '/accReport/downloadFinancialStatementCsvOfLevel3'

        // financial Statement level 2
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-243', pluginId: 2).save(); // url: '/accReport/showFinancialStatementOfLevel2'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-243', pluginId: 2).save(); // url: '/accReport/showFinancialStatementOfLevel2'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-244', pluginId: 2).save(); // url: '/accReport/listFinancialStatementOfLevel2'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-244', pluginId: 2).save(); // url: '/accReport/listFinancialStatementOfLevel2'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-245', pluginId: 2).save(); // url: '/accReport/downloadFinancialStatementOfLevel2'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-245', pluginId: 2).save(); // url: '/accReport/downloadFinancialStatementOfLevel2'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-246', pluginId: 2).save(); // url: '/accReport/downloadFinancialStatementCsvOfLevel2'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-246', pluginId: 2).save(); // url: '/accReport/downloadFinancialStatementCsvOfLevel2'

        // project wise expense
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-106', pluginId: 2).save(); // url: '/accReport/showProjectWiseExpense'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-106', pluginId: 2).save(); // url: '/accReport/showProjectWiseExpense'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-107', pluginId: 2).save(); // url: '/accReport/listProjectWiseExpense'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-107', pluginId: 2).save(); // url: '/accReport/listProjectWiseExpense'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-108', pluginId: 2).save(); // url: '/accReport/listProjectWiseExpenseDetails'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-108', pluginId: 2).save(); // url: '/accReport/listProjectWiseExpenseDetails'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-109', pluginId: 2).save(); // url: '/accReport/downloadProjectWiseExpense'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-109', pluginId: 2).save(); // url: '/accReport/downloadProjectWiseExpense'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-179', pluginId: 2).save(); // url: '/accReport/downloadProjectWiseExpenseCsv'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-179', pluginId: 2).save(); // url: '/accReport/downloadProjectWiseExpenseCsv'

        // project fund flow
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-254', pluginId: 2).save(); // url: '/accReport/showProjectFundFlowReport'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-254', pluginId: 2).save(); // url: '/accReport/showProjectFundFlowReport'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-255', pluginId: 2).save(); // url: '/accReport/listProjectFundFlowReport'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-255', pluginId: 2).save(); // url: '/accReport/listProjectFundFlowReport'

        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-253', pluginId: 2).save(); // url: '/accReport/downloadProjectFundFlowReport'
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-253', pluginId: 2).save(); // url: '/accReport/downloadProjectFundFlowReport'

        // source wise balance
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-111', pluginId: 2).save(); // url: '/accReport/showSourceWiseBalance'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-111', pluginId: 2).save(); // url: '/accReport/showSourceWiseBalance'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-112', pluginId: 2).save(); // url: '/accReport/listSourceWiseBalance'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-112', pluginId: 2).save(); // url: '/accReport/listSourceWiseBalance'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-113', pluginId: 2).save(); // url: '/accReport/downloadSourceWiseBalance'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-113', pluginId: 2).save(); // url: '/accReport/downloadSourceWiseBalance'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-180', pluginId: 2).save(); // url: '/accReport/downloadSourceWiseBalanceCsv'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-180', pluginId: 2).save(); // url: '/accReport/downloadSourceWiseBalanceCsv'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-205', pluginId: 2).save(); // url: '/accReport/downloadVoucherListBySourceId'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-205', pluginId: 2).save(); // url: '/accReport/downloadVoucherListBySourceId'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-260', pluginId: 2).save(); // url: '/accReport/listSourceCategoryForSourceWiseBalance'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-260', pluginId: 2).save(); // url: '/accReport/listSourceCategoryForSourceWiseBalance'

        // Acc IOU Slip Report
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-141', pluginId: 2).save(); // url: '/accReport/showAccIouSlipRpt'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-141', pluginId: 2).save(); // url: '/accReport/showAccIouSlipRpt'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-142', pluginId: 2).save(); // url: '/accReport/listAccIouSlipRpt'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-142', pluginId: 2).save(); // url: '/accReport/listAccIouSlipRpt'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-143', pluginId: 2).save(); // url: '/accReport/downloadAccIouSlipRpt'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-143', pluginId: 2).save(); // url: '/accReport/downloadAccIouSlipRpt'

        // For Acc-Iou-Slip
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-121', pluginId: 2).save(); // url: '/accIouSlip/show'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-121', pluginId: 2).save(); // url: '/accIouSlip/show'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-122', pluginId: 2).save(); // url: '/accIouSlip/create'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-122', pluginId: 2).save(); // url: '/accIouSlip/create'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-124', pluginId: 2).save(); // url: '/accIouSlip/update'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-124', pluginId: 2).save(); // url: '/accIouSlip/update'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-125', pluginId: 2).save(); // url: '/accIouSlip/delete'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-125', pluginId: 2).save(); // url: '/accIouSlip/delete'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-126', pluginId: 2).save(); // url: '/accIouSlip/list'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-126', pluginId: 2).save(); // url: '/accIouSlip/list'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-127', pluginId: 2).save(); // url: '/accIouSlip/sentNotification'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-127', pluginId: 2).save(); // url: '/accIouSlip/sentNotification'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-151', pluginId: 2).save(); // url: '/accIouSlip/listIndentByProject'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-151', pluginId: 2).save(); // url: '/accIouSlip/listIndentByProject'

        // For Acc-Iou-Purpose
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-131', pluginId: 2).save(); // url: '/accIouPurpose/show'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-131', pluginId: 2).save(); // url: '/accIouPurpose/show'
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-132', pluginId: 2).save(); // url: '/accIouPurpose/create'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-132', pluginId: 2).save(); // url: '/accIouPurpose/create'
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-133', pluginId: 2).save(); // url: '/accIouPurpose/dropDownPurposeReload'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-133', pluginId: 2).save(); // url: '/accIouPurpose/dropDownPurposeReload'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-134', pluginId: 2).save(); // url: '/accIouPurpose/update'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-134', pluginId: 2).save(); // url: '/accIouPurpose/update'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-135', pluginId: 2).save(); // url: '/accIouPurpose/delete'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-135', pluginId: 2).save(); // url: '/accIouPurpose/delete'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-136', pluginId: 2).save(); // url: '/accIouPurpose/list'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-136', pluginId: 2).save(); // url: '/accIouPurpose/list'

        // acc financial year
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-114', pluginId: 2).save(); // url: '/accFinancialYear/show'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-114', pluginId: 2).save(); // url: '/accFinancialYear/show'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-115', pluginId: 2).save(); // url: '/accFinancialYear/list'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-115', pluginId: 2).save(); // url: '/accFinancialYear/list'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-116', pluginId: 2).save(); // url: '/accFinancialYear/create'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-116', pluginId: 2).save(); // url: '/accFinancialYear/create'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-117', pluginId: 2).save(); // url: '/accFinancialYear/update'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-117', pluginId: 2).save(); // url: '/accFinancialYear/update'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-118', pluginId: 2).save(); // url: '/accFinancialYear/delete'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-118', pluginId: 2).save(); // url: '/accFinancialYear/delete'

        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-150', pluginId: 2).save(); // url: '/accFinancialYear/setCurrentFinancialYear'

        // supplier payable report
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-137', pluginId: 2).save(); // url: '/accReport/showSupplierWisePayable'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-137', pluginId: 2).save(); // url: '/accReport/showSupplierWisePayable'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-138', pluginId: 2).save(); // url: '/accReport/listSupplierWisePayable'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-138', pluginId: 2).save(); // url: '/accReport/listSupplierWisePayable'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-139', pluginId: 2).save(); // url: '/accReport/downloadSupplierWisePayable'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-139', pluginId: 2).save(); // url: '/accReport/downloadSupplierWisePayable'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-172', pluginId: 2).save(); // url: '/accReport/downloadSupplierWisePayableCsv'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-172', pluginId: 2).save(); // url: '/accReport/downloadSupplierWisePayableCsv'

        // trial Balance
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-168', pluginId: 2).save(); // url: '/accReport/showCustomGroupBalance'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-168', pluginId: 2).save(); // url: '/accReport/showCustomGroupBalance'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-169', pluginId: 2).save(); // url: '/accReport/listCustomGroupBalance'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-169', pluginId: 2).save(); // url: '/accReport/listCustomGroupBalance'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-170', pluginId: 2).save(); // url: '/accReport/downloadCustomGroupBalance'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-170', pluginId: 2).save(); // url: '/accReport/downloadCustomGroupBalance'

        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-176', pluginId: 2).save(); // url: '/accReport/downloadCustomGroupBalanceCsv'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-176', pluginId: 2).save(); // url: '/accReport/downloadCustomGroupBalanceCsv'

        // indent
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-266', pluginId: 2).save(); // url: '/accIndent/show'
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-267', pluginId: 2).save(); // url: '/accIndent/create'
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-268', pluginId: 2).save(); // url: '/accIndent/update'
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-269', pluginId: 2).save(); // url: '/accIndent/delete'
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-270', pluginId: 2).save(); // url: '/accIndent/list'
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-271', pluginId: 2).save(); // url: '/accIndent/approve'
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-272', pluginId: 2).save(); // url: '/accIndent/listOfUnApprovedIndent'
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-273', pluginId: 2).save(); // url: '/accIndent/approveIndentDashBoard'
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-274', pluginId: 2).save(); // url: '/accIndentDetails/show'
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-275', pluginId: 2).save(); // url: '/accIndentDetails/create'
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-276', pluginId: 2).save(); // url: '/accIndentDetails/update'
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-277', pluginId: 2).save(); // url: '/accIndentDetails/delete'
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-278', pluginId: 2).save(); // url: '/accIndentDetails/list'

        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-266', pluginId: 2).save(); // url: '/accIndent/show'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-267', pluginId: 2).save(); // url: '/accIndent/create'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-268', pluginId: 2).save(); // url: '/accIndent/update'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-269', pluginId: 2).save(); // url: '/accIndent/delete'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-270', pluginId: 2).save(); // url: '/accIndent/list'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-271', pluginId: 2).save(); // url: '/accIndent/approve'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-272', pluginId: 2).save(); // url: '/accIndent/listOfUnApprovedIndent'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-273', pluginId: 2).save(); // url: '/accIndent/approveIndentDashBoard'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-274', pluginId: 2).save(); // url: '/accIndentDetails/show'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-275', pluginId: 2).save(); // url: '/accIndentDetails/create'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-276', pluginId: 2).save(); // url: '/accIndentDetails/update'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-277', pluginId: 2).save(); // url: '/accIndentDetails/delete'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-278', pluginId: 2).save(); // url: '/accIndentDetails/list'

        // indent report
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-279', pluginId: 2).save(); // url: '/accReport/showIndentRpt'
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-280', pluginId: 2).save(); // url: '/accReport/searchIndentRpt'
        new RoleFeatureMapping(roleTypeId: accountant.id, transactionCode: 'ACC-281', pluginId: 2).save(); // url: '/accReport/downloadIndentRpt'

        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-279', pluginId: 2).save(); // url: '/accReport/showIndentRpt'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-280', pluginId: 2).save(); // url: '/accReport/searchIndentRpt'
        new RoleFeatureMapping(roleTypeId: cfo.id, transactionCode: 'ACC-281', pluginId: 2).save(); // url: '/accReport/downloadIndentRpt'
    }

    public void createTestDataForPt() {
        Role engineer = roleService.findByNameAndCompanyId('Software Engineer', companyId)
        Role sqa = roleService.findByNameAndCompanyId('SQA', companyId)

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-1', pluginId: 10).save(); // url: '/ptPlugin/renderProjectTrackMenu'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-1', pluginId: 10).save(); // url: '/ptPlugin/renderProjectTrackMenu'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-2', pluginId: 10).save(); // url: '/ptBacklog/show'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-2', pluginId: 10).save(); // url: '/ptBacklog/show'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-3', pluginId: 10).save(); // url: '/ptBacklog/list'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-3', pluginId: 10).save(); // url: '/ptBacklog/list'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-5', pluginId: 10).save(); // url: '/ptBacklog/create'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-5', pluginId: 10).save(); // url: '/ptBacklog/create'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-6', pluginId: 10).save(); // url: '/ptBacklog/update'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-6', pluginId: 10).save(); // url: '/ptBacklog/update'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-7', pluginId: 10).save(); // url: '/ptBacklog/delete'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-7', pluginId: 10).save(); // url: '/ptBacklog/delete'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-52', pluginId: 10).save(); // url: '/ptBacklog/showMyBacklog'
        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-53', pluginId: 10).save(); // url: '/ptBacklog/listMyBacklog'
        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-55', pluginId: 10).save(); // url: '/ptBacklog/updateMyBacklog'
        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-56', pluginId: 10).save(); // url: '/ptBacklog/removeMyBacklog'
        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-57', pluginId: 10).save(); // url: '/ptBacklog/addToMyBacklog'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-39', pluginId: 10).save(); // url: '/ptBacklog/showBackLogForSprint'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-39', pluginId: 10).save(); // url: '/ptBacklog/showBackLogForSprint'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-46', pluginId: 10).save(); // url: '/ptBacklog/createBackLogForSprint'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-46', pluginId: 10).save(); // url: '/ptBacklog/createBackLogForSprint'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-47', pluginId: 10).save(); // url: '/ptBacklog/deleteBackLogForSprint'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-47', pluginId: 10).save(); // url: '/ptBacklog/deleteBackLogForSprint'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-48', pluginId: 10).save(); // url: '/ptBacklog/listBackLogForSprint'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-48', pluginId: 10).save(); // url: '/ptBacklog/listBackLogForSprint'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-71', pluginId: 10).save(); // url: '/ptBacklog/getBacklogList'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-71', pluginId: 10).save(); // url: '/ptBacklog/getBacklogList'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-83', pluginId: 10).save(); // url: '/ptBacklog/showForActive'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-83', pluginId: 10).save(); // url: '/ptBacklog/showForActive'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-84', pluginId: 10).save(); // url: '/ptBacklog/listForActive'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-84', pluginId: 10).save(); // url: '/ptBacklog/listForActive'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-85', pluginId: 10).save(); // url: '/ptBacklog/showForInActive'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-85', pluginId: 10).save(); // url: '/ptBacklog/showForInActive'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-86', pluginId: 10).save(); // url: '/ptBacklog/listForInActive'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-86', pluginId: 10).save(); // url: '/ptBacklog/listForInActive'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-94', pluginId: 10).save(); // url: '/ptReport/showForBacklogDetails'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-94', pluginId: 10).save(); // url: '/ptReport/showForBacklogDetails'

        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-96', pluginId: 10).save(); // url: '/ptBacklog/acceptStory'


        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-40', pluginId: 10).save(); // url: '/ptAcceptanceCriteria/show'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-41', pluginId: 10).save(); // url: '/ptAcceptanceCriteria/list'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-41', pluginId: 10).save(); // url: '/ptAcceptanceCriteria/list'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-42', pluginId: 10).save(); // url: '/ptAcceptanceCriteria/select'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-42', pluginId: 10).save(); // url: '/ptAcceptanceCriteria/select'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-43', pluginId: 10).save(); // url: '/ptAcceptanceCriteria/create'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-44', pluginId: 10).save(); // url: '/ptAcceptanceCriteria/update'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-45', pluginId: 10).save(); // url: '/ptAcceptanceCriteria/delete'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-98', pluginId: 10).save(); // url: '/ptAcceptanceCriteria/showForMyBacklog'

        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-99', pluginId: 10).save(); // url: '/ptBug/reOpenBug'

        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-100', pluginId: 10).save(); // url: '/ptBug/closeBug'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-102', pluginId: 10).save(); // url: '/ptBug/showBugDetails'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-102', pluginId: 10).save(); // url: '/ptBug/showBugDetails'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-104', pluginId: 10).save(); // url: '/ptBug/showOrphanBug'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-104', pluginId: 10).save(); // url: '/ptBug/showOrphanBug'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-105', pluginId: 10).save(); // url: '/ptBug/createOrphanBug'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-105', pluginId: 10).save(); // url: '/ptBug/createOrphanBug'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-106', pluginId: 10).save(); // url: '/ptBug/updateOrphanBug'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-106', pluginId: 10).save(); // url: '/ptBug/updateOrphanBug'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-107', pluginId: 10).save(); // url: '/ptBug/listOrphanBug'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-107', pluginId: 10).save(); // url: '/ptBug/listOrphanBug'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-108', pluginId: 10).save(); // url: '/ptBug/addToMyBug'
        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-115', pluginId: 10).save(); // url: '/ptBug/showMyBug'
        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-116', pluginId: 10).save(); // url: '/ptBug/listMyBug'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-152', pluginId: 10).save(); // url: '/ptBug/listBugForSearch'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-152', pluginId: 10).save(); // url: '/ptBug/listBugForSearch'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-153', pluginId: 10).save(); // url: '/ptBug/refreshBugDetails'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-153', pluginId: 10).save(); // url: '/ptBug/refreshBugDetails'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-72', pluginId: 10).save(); // url: '/ptAcceptanceCriteria/showForMyBacklog'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-72', pluginId: 10).save(); // url: '/ptAcceptanceCriteria/showForMyBacklog'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-73', pluginId: 10).save(); // url: '/ptAcceptanceCriteria/updateForMyBacklog'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-73', pluginId: 10).save(); // url: '/ptAcceptanceCriteria/updateForMyBacklog'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-68', pluginId: 10).save(); // url: '/ptModule/listModuleByProject'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-68', pluginId: 10).save(); // url: '/ptModule/listModuleByProject'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-8', pluginId: 10).save(); // url: '/ptBug/show'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-8', pluginId: 10).save(); // url: '/ptBug/show'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-9', pluginId: 10).save(); // url: '/ptBug/create'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-9', pluginId: 10).save(); // url: '/ptBug/create'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-10', pluginId: 10).save(); // url: '/ptBug/update'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-10', pluginId: 10).save(); // url: '/ptBug/update'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-11', pluginId: 10).save(); // url: '/ptBug/delete'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-11', pluginId: 10).save(); // url: '/ptBug/delete'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-13', pluginId: 10).save(); // url: '/ptBug/list'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-13', pluginId: 10).save(); // url: '/ptBug/list'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-75', pluginId: 10).save(); // url: '/ptBug/showBugForMyTask'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-76', pluginId: 10).save(); // url: '/ptBug/updateBugForMyTask'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-74', pluginId: 10).save(); // url: '/ptBug/downloadBugContent'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-74', pluginId: 10).save(); // url: '/ptBug/downloadBugContent'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-67', pluginId: 10).save(); // url: '/ptSprint/listSprintByProjectId'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-67', pluginId: 10).save(); // url: '/ptSprint/listSprintByProjectId'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-87', pluginId: 10).save(); // url: '/ptSprint/listInActiveSprintByProjectId'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-87', pluginId: 10).save(); // url: '/ptSprint/listInActiveSprintByProjectId'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-61', pluginId: 10).save(); // url: '/ptReport/showReportOpenBacklog'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-61', pluginId: 10).save(); // url: '/ptReport/showReportOpenBacklog'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-62', pluginId: 10).save(); // url: '/ptReport/downloadOpenBacklogReport'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-62', pluginId: 10).save(); // url: '/ptReport/downloadOpenBacklogReport'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-63', pluginId: 10).save(); // url: '/ptReport/listReportOpenBacklog'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-63', pluginId: 10).save(); // url: '/ptReport/listReportOpenBacklog'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-58', pluginId: 10).save(); // url: '/ptReport/downloadSprintDetails'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-58', pluginId: 10).save(); // url: '/ptReport/downloadSprintDetails'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-59', pluginId: 10).save(); // url: '/ptReport/showReportSprint'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-59', pluginId: 10).save(); // url: '/ptReport/showReportSprint'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-60', pluginId: 10).save(); // url: '/ptReport/listSprintDetails'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-60', pluginId: 10).save(); // url: '/ptReport/listSprintDetails'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-64', pluginId: 10).save(); // url: '/ptReport/downloadBugDetails'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-64', pluginId: 10).save(); // url: '/ptReport/downloadBugDetails'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-65', pluginId: 10).save(); // url: '/ptReport/showReportBug'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-65', pluginId: 10).save(); // url: '/ptReport/showReportBug'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-66', pluginId: 10).save(); // url: '/ptReport/listBugDetails'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-66', pluginId: 10).save(); // url: '/ptReport/listBugDetails'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-101', pluginId: 10).save(); // url: '/ptReport/downloadBacklogDetailsReport'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-101', pluginId: 10).save(); // url: '/ptReport/downloadBacklogDetailsReport'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-103', pluginId: 10).save(); // url: '/ptReport/downloadPtBugDetails'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-103', pluginId: 10).save(); // url: '/ptReport/downloadPtBugDetails'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-154', pluginId: 10).save(); // url: '/ptReport/downloadPtChangeRequestReportForBacklog'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-154', pluginId: 10).save(); // url: '/ptReport/downloadPtChangeRequestReportForBacklog'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-109', pluginId: 10).save(); // url: '/ptFlow/show'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-109', pluginId: 10).save(); // url: '/ptFlow/show'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-110', pluginId: 10).save(); // url: '/ptFlow/list'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-110', pluginId: 10).save(); // url: '/ptFlow/list'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-111', pluginId: 10).save(); // url: '/ptFlow/select'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-111', pluginId: 10).save(); // url: '/ptFlow/select'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-112', pluginId: 10).save(); // url: '/ptFlow/create'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-112', pluginId: 10).save(); // url: '/ptFlow/create'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-113', pluginId: 10).save(); // url: '/ptFlow/update'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-113', pluginId: 10).save(); // url: '/ptFlow/update'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-114', pluginId: 10).save(); // url: '/ptFlow/delete'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-114', pluginId: 10).save(); // url: '/ptFlow/delete'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-169', pluginId: 10).save(); // url: '/ptSteps/show'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-169', pluginId: 10).save(); // url: '/ptSteps/show'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-170', pluginId: 10).save(); // url: '/ptSteps/list'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-170', pluginId: 10).save(); // url: '/ptSteps/list'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-171', pluginId: 10).save(); // url: '/ptSteps/create'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-171', pluginId: 10).save(); // url: '/ptSteps/create'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-172', pluginId: 10).save(); // url: '/ptSteps/update'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-172', pluginId: 10).save(); // url: '/ptSteps/update'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-173', pluginId: 10).save(); // url: '/ptSteps/delete'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-173', pluginId: 10).save(); // url: '/ptSteps/delete'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-118', pluginId: 10).save(); // url: '/ptBug/bugListForModule'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-118', pluginId: 10).save(); // url: '/ptBug/bugListForModule'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-119', pluginId: 10).save(); // url: '/ptReport/downloadBacklogDetailsUatReport'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-119', pluginId: 10).save(); // url: '/ptReport/downloadBacklogDetailsUatReport'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-120', pluginId: 10).save(); // url: '/ptReport/showPtTaskReport'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-120', pluginId: 10).save(); // url: '/ptReport/showPtTaskReport'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-121', pluginId: 10).save(); // url: '/ptReport/listPtTaskReport'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-121', pluginId: 10).save(); // url: '/ptReport/listPtTaskReport'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-122', pluginId: 10).save(); // url: '/ptReport/downloadPtTaskReport'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-122', pluginId: 10).save(); // url: '/ptReport/downloadPtTaskReport'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-130', pluginId: 10).save(); // url: '/ptReport/downloadSprintDetailsUatReport'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-130', pluginId: 10).save(); // url: '/ptReport/downloadSprintDetailsUatReport'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-131', pluginId: 10).save(); // url: '/ptReport/downloadSprintDetailsUseCaseReport'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-131', pluginId: 10).save(); // url: '/ptReport/downloadSprintDetailsUseCaseReport'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-140', pluginId: 10).save(); // url: '/ptChangeRequest/show'
        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-141', pluginId: 10).save(); // url: '/ptChangeRequest/create'
        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-142', pluginId: 10).save(); // url: '/ptChangeRequest/update'
        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-143', pluginId: 10).save(); // url: '/ptChangeRequest/delete'
        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-144', pluginId: 10).save(); // url: '/ptChangeRequest/list'

        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-140', pluginId: 10).save(); // url: '/ptChangeRequest/show'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-141', pluginId: 10).save(); // url: '/ptChangeRequest/create'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-142', pluginId: 10).save(); // url: '/ptChangeRequest/update'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-143', pluginId: 10).save(); // url: '/ptChangeRequest/delete'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-144', pluginId: 10).save(); // url: '/ptChangeRequest/list'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-146', pluginId: 10).save(); // url: '/ptReport/listPtChangeRequestReport'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-146', pluginId: 10).save(); // url: '/ptReport/downloadPtChangeRequestReport'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-147', pluginId: 10).save(); // url: '/ptReport/listPtChangeRequestReport'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-147', pluginId: 10).save(); // url: '/ptReport/downloadPtChangeRequestReport'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-148', pluginId: 10).save(); // url: '/ptReport/listPtChangeRequestReport'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-148', pluginId: 10).save(); // url: '/ptReport/downloadPtChangeRequestReport'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-150', pluginId: 10).save(); // url: '/ptReport/backlogListForDynamicSearch'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-150', pluginId: 10).save(); // url: '/ptReport/backlogListForDynamicSearch'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-151', pluginId: 10).save(); // url: '/ptReport/refreshBacklogDetails'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-151', pluginId: 10).save(); // url: '/ptReport/refreshBacklogDetails'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-123', pluginId: 10).save(); // url: '/ptReport/downloadPtTaskUatReport'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-123', pluginId: 10).save(); // url: '/ptReport/downloadPtTaskUatReport'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-139', pluginId: 10).save(); // url: '/ptReport/downloadPtTaskFeatureList'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-139', pluginId: 10).save(); // url: '/ptReport/downloadPtTaskFeatureList'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-167', pluginId: 10).save(); // url: '/ptReport/showForBugReport'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-167', pluginId: 10).save(); // url: '/ptReport/showForBugReport'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-168', pluginId: 10).save(); // url: '/ptReport/listForBugReport'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-168', pluginId: 10).save(); // url: '/ptReport/listForBugReport'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-176', pluginId: 10).save(); // url: '/ptReport/downloadBugReport'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-176', pluginId: 10).save(); // url: '/ptReport/downloadBugReport'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-178', pluginId: 10).save(); // url: '/ptBacklog/listPtBacklogForListView'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-178', pluginId: 10).save(); // url: '/ptBacklog/listPtBacklogForListView'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-182', pluginId: 10).save(); // url: '/ptReport/downloadSprintSummaryReport'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-182', pluginId: 10).save(); // url: '/ptReport/downloadSprintSummaryReport'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'PT-183', pluginId: 10).save(); // url: '/ptReport/downloadProgressReport'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'PT-183', pluginId: 10).save(); // url: '/ptReport/downloadProgressReport'

        // AppAttachment for engineer
        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'APP-144', pluginId: 10).save(); // url: '/appAttachment/show'
        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'APP-146', pluginId: 10).save(); // url: '/appAttachment/list'
        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'APP-147', pluginId: 10).save(); // url: '/appAttachment/update'
        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'APP-148', pluginId: 10).save(); // url: '/appAttachment/create'
        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'APP-149', pluginId: 10).save(); // url: '/appAttachment/delete'

        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'APP-421', pluginId: 10).save(); // url: '/appAttachment/upload'
        // drop down content category reload
        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'APP-153', pluginId: 10).save(); // url: '/contentCategory/dropDownContentCategoryReload'

        // AppAttachment for sqa
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'APP-144', pluginId: 10).save(); // url: '/appAttachment/show'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'APP-146', pluginId: 10).save(); // url: '/appAttachment/list'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'APP-147', pluginId: 10).save(); // url: '/appAttachment/update'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'APP-148', pluginId: 10).save(); // url: '/appAttachment/create'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'APP-149', pluginId: 10).save(); // url: '/appAttachment/delete'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'APP-421', pluginId: 10).save(); // url: '/appAttachment/upload'
        // drop down content category reload
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'APP-153', pluginId: 10).save(); // url: '/contentCategory/dropDownContentCategoryReload'

        // app note for engineer
        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'APP-245', pluginId: 10).save(); // url: '/appNote/show'
        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'APP-247', pluginId: 10).save(); // url: '/appNote/list'
        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'APP-248', pluginId: 10).save(); // url: '/appNote/update'
        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'APP-249', pluginId: 10).save(); // url: '/appNote/create'
        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'APP-250', pluginId: 10).save(); // url: '/appNote/delete'
        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'APP-261', pluginId: 10).save(); // url: '/appNote/viewEntityNote'
        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'APP-345', pluginId: 10).save(); // url: '/appNote/listEntityNote'
        //reload app note taglib
        new RoleFeatureMapping(roleTypeId: engineer.id, transactionCode: 'APP-252', pluginId: 10).save(); //url: '/appNote/reloadEntityNote'

        // app note for sqa
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'APP-245', pluginId: 10).save(); // url: '/appNote/show'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'APP-247', pluginId: 10).save(); // url: '/appNote/list'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'APP-248', pluginId: 10).save(); // url: '/appNote/update'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'APP-249', pluginId: 10).save(); // url: '/appNote/create'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'APP-250', pluginId: 10).save(); // url: '/appNote/delete'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'APP-261', pluginId: 10).save(); // url: '/appNote/viewEntityNote'
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'APP-345', pluginId: 10).save(); // url: '/appNote/listEntityNote'
        //reload app note taglib
        new RoleFeatureMapping(roleTypeId: sqa.id, transactionCode: 'APP-252', pluginId: 10).save(); //url: '/appNote/reloadEntityNote'
    }

    public void createTestDataForInventory(long companyId) {
        Role roleAuditor = roleService.findByNameAndCompanyId('Inventory Auditor', companyId)
        Role rolePm = roleService.findByNameAndCompanyId('Project Manager', companyId)

        // default
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-150', pluginId: 4).save(); // url: '/invPlugin/renderInventoryMenu'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-150', pluginId: 4).save(); // url: '/invPlugin/renderInventoryMenu'
        // For Dash Board
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-151', pluginId: 4).save(); // url: '/invInventoryTransaction/listOfUnApprovedConsumption'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-186', pluginId: 4).save(); // url: '/invInventoryTransaction/listOfUnApprovedInFromSupplier'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-187', pluginId: 4).save(); // url: '/invInventoryTransaction/listOfUnApprovedInventoryOut'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-188', pluginId: 4).save(); // url: '/invInventoryTransaction/listOfUnApprovedInFromInventory'

        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-151', pluginId: 4).save(); // url: '/invInventoryTransaction/listOfUnApprovedConsumption'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-186', pluginId: 4).save(); // url: '/invInventoryTransaction/listOfUnApprovedInFromSupplier'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-187', pluginId: 4).save(); // url: '/invInventoryTransaction/listOfUnApprovedInventoryOut'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-188', pluginId: 4).save(); // url: '/invInventoryTransaction/listOfUnApprovedInFromInventory'

        //-------------- > For Inventory Transaction < --------------\\
        // Inventory Transaction-in (from supplier)
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-32', pluginId: 4).save(); // url: '/invInventoryTransaction/showInventoryInFromSupplier'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-33', pluginId: 4).save(); // url: '/invInventoryTransaction/createInventoryInFromSupplier'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-35', pluginId: 4).save(); // url: '/invInventoryTransaction/updateInventoryInFromSupplier'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-36', pluginId: 4).save(); // url: '/invInventoryTransaction/deleteInventoryInFromSupplier'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-37', pluginId: 4).save(); // url: '/invInventoryTransaction/listInventoryInFromSupplier'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-38', pluginId: 4).save(); // url: '/invInventoryTransaction/listInventoryByType'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-39', pluginId: 4).save(); // url: '/invInventoryTransaction/listPOBySupplier'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-183', pluginId: 4).save(); // url: '/invInventoryTransaction/listInventoryByTypeAndProject'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-217', pluginId: 4).save(); // url: '/invInventoryTransaction/listFixedAssetByItemAndProject'

        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-32', pluginId: 4).save(); // url: '/invInventoryTransaction/showInventoryInFromSupplier'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-33', pluginId: 4).save(); // url: '/invInventoryTransaction/createInventoryInFromSupplier'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-35', pluginId: 4).save(); // url: '/invInventoryTransaction/updateInventoryInFromSupplier'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-36', pluginId: 4).save(); // url: '/invInventoryTransaction/deleteInventoryInFromSupplier'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-37', pluginId: 4).save(); // url: '/invInventoryTransaction/listInventoryInFromSupplier'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-38', pluginId: 4).save(); // url: '/invInventoryTransaction/listInventoryByType'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-39', pluginId: 4).save(); // url: '/invInventoryTransaction/listPOBySupplier'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-183', pluginId: 4).save(); // url: '/invInventoryTransaction/listInventoryByTypeAndProject'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-217', pluginId: 4).save(); // url: '/invInventoryTransaction/listFixedAssetByItemAndProject'

        // Inventory Transaction-in (from Inventory)
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-40', pluginId: 4).save(); // url: '/invInventoryTransaction/showInventoryInFromInventory'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-41', pluginId: 4).save(); // url: '/invInventoryTransaction/createInventoryInFromInventory'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-42', pluginId: 4).save(); // url: '/invInventoryTransaction/selectInventoryInFromInventory'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-43', pluginId: 4).save(); // url: '/invInventoryTransaction/updateInventoryInFromInventory'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-44', pluginId: 4).save(); // url: '/invInventoryTransaction/deleteInventoryInFromInventory'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-45', pluginId: 4).save(); // url: '/invInventoryTransaction/listInventoryInFromInventory'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-46', pluginId: 4).save(); // url: '/invInventoryTransaction/listInventoryOfTransactionOut'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-47', pluginId: 4).save(); // url: '/invInventoryTransaction/listInvTransaction'

        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-40', pluginId: 4).save(); // url: '/invInventoryTransaction/showInventoryInFromInventory'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-41', pluginId: 4).save(); // url: '/invInventoryTransaction/createInventoryInFromInventory'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-42', pluginId: 4).save(); // url: '/invInventoryTransaction/selectInventoryInFromInventory'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-43', pluginId: 4).save(); // url: '/invInventoryTransaction/updateInventoryInFromInventory'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-44', pluginId: 4).save(); // url: '/invInventoryTransaction/deleteInventoryInFromInventory'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-45', pluginId: 4).save(); // url: '/invInventoryTransaction/listInventoryInFromInventory'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-46', pluginId: 4).save(); // url: '/invInventoryTransaction/listInventoryOfTransactionOut'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-47', pluginId: 4).save(); // url: '/invInventoryTransaction/listInvTransaction'

        // Inventory Consumption
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-48', pluginId: 4).save(); // url: '/invInventoryTransaction/showInventoryConsumption'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-49', pluginId: 4).save(); // url: '/invInventoryTransaction/createInventoryConsumption'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-51', pluginId: 4).save(); // url: '/invInventoryTransaction/updateInventoryConsumption'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-52', pluginId: 4).save(); // url: '/invInventoryTransaction/deleteInventoryConsumption'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-53', pluginId: 4).save(); // url: '/invInventoryTransaction/listInventoryConsumption'

        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-48', pluginId: 4).save(); // url: '/invInventoryTransaction/showInventoryConsumption'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-49', pluginId: 4).save(); // url: '/invInventoryTransaction/createInventoryConsumption'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-51', pluginId: 4).save(); // url: '/invInventoryTransaction/updateInventoryConsumption'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-52', pluginId: 4).save(); // url: '/invInventoryTransaction/deleteInventoryConsumption'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-53', pluginId: 4).save(); // url: '/invInventoryTransaction/listInventoryConsumption'

        // Inventory Out
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-54', pluginId: 4).save(); // url: '/invInventoryTransaction/showInventoryOut'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-55', pluginId: 4).save(); // url: '/invInventoryTransaction/createInventoryOut'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-57', pluginId: 4).save(); // url: '/invInventoryTransaction/updateInventoryOut'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-58', pluginId: 4).save(); // url: '/invInventoryTransaction/deleteInventoryOut'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-59', pluginId: 4).save(); // url: '/invInventoryTransaction/listInventoryOut'

        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-54', pluginId: 4).save(); // url: '/invInventoryTransaction/showInventoryOut'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-55', pluginId: 4).save(); // url: '/invInventoryTransaction/createInventoryOut'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-57', pluginId: 4).save(); // url: '/invInventoryTransaction/updateInventoryOut'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-58', pluginId: 4).save(); // url: '/invInventoryTransaction/deleteInventoryOut'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-59', pluginId: 4).save(); // url: '/invInventoryTransaction/listInventoryOut'

        // Inventory -Production
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-66', pluginId: 4).save(); // url: '/invInventoryTransaction/showInvProductionWithConsumption'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-67', pluginId: 4).save(); // url: '/invInventoryTransaction/createInvProductionWithConsumption'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-68', pluginId: 4).save(); // url: '/invInventoryTransaction/updateInvProductionWithConsumption'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-69', pluginId: 4).save(); // url: '/invInventoryTransaction/deleteInvProductionWithConsumption'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-70', pluginId: 4).save(); // url: '/invInventoryTransaction/selectInvProductionWithConsumption'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-71', pluginId: 4).save(); // url: '/invInventoryTransaction/listInvProductionWithConsumption'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-180', pluginId: 4).save(); // url: '/invInventoryTransaction/showApprovedProdWithConsump'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-181', pluginId: 4).save(); // url: '/invInventoryTransaction/listApprovedProdWithConsump'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-179', pluginId: 4).save(); // url: '/invInventoryTransaction/approveInvProdWithConsumption'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-182', pluginId: 4).save(); // url: '/invInventoryTransaction/adjustInvProductionWithConsumption'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-184', pluginId: 4).save(); // url: '/invInventoryTransaction/reverseAdjust'

        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-66', pluginId: 4).save(); // url: '/invInventoryTransaction/showInvProductionWithConsumption'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-67', pluginId: 4).save(); // url: '/invInventoryTransaction/createInvProductionWithConsumption'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-68', pluginId: 4).save(); // url: '/invInventoryTransaction/updateInvProductionWithConsumption'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-69', pluginId: 4).save(); // url: '/invInventoryTransaction/deleteInvProductionWithConsumption'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-70', pluginId: 4).save(); // url: '/invInventoryTransaction/selectInvProductionWithConsumption'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-71', pluginId: 4).save(); // url: '/invInventoryTransaction/listInvProductionWithConsumption'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-180', pluginId: 4).save(); // url: '/invInventoryTransaction/showApprovedProdWithConsump'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-181', pluginId: 4).save(); // url: '/invInventoryTransaction/listApprovedProdWithConsump'

        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-112', pluginId: 4).save(); // url: '/invProductionDetails/show'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-113', pluginId: 4).save(); // url: '/invProductionDetails/create'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-114', pluginId: 4).save(); // url: '/invProductionDetails/dropDownMaterialReload'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-115', pluginId: 4).save(); // url: '/invProductionDetails/update'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-116', pluginId: 4).save(); // url: '/invProductionDetails/delete'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-117', pluginId: 4).save(); // url: '/invProductionDetails/list'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-118', pluginId: 4).save(); // url: '/invProductionDetails/getBothMaterials'

        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-112', pluginId: 4).save(); // url: '/invProductionDetails/show'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-113', pluginId: 4).save(); // url: '/invProductionDetails/create'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-114', pluginId: 4).save(); // url: '/invProductionDetails/dropDownMaterialReload'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-115', pluginId: 4).save(); // url: '/invProductionDetails/update'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-116', pluginId: 4).save(); // url: '/invProductionDetails/delete'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-117', pluginId: 4).save(); // url: '/invProductionDetails/list'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-118', pluginId: 4).save(); // url: '/invProductionDetails/getBothMaterials'

        // For Inventory Transaction Details
        // For Inventory-out-details
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-78', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/showUnApprovedInventoryOutDetails'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-79', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/createInventoryOutDetails'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-81', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/updateInventoryOutDetails'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-82', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/deleteInventoryOutDetails'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-83', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/listUnApprovedInventoryOutDetails'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-172', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/showApprovedInventoryOutDetails'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-173', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/listApprovedInventoryOutDetails'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-171', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/approveInventoryOutDetails'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-174', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/adjustInvOut'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-192', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/reverseAdjustInvOut'

        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-78', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/showUnApprovedInventoryOutDetails'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-79', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/createInventoryOutDetails'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-81', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/updateInventoryOutDetails'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-82', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/deleteInventoryOutDetails'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-83', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/listUnApprovedInventoryOutDetails'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-172', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/showApprovedInventoryOutDetails'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-173', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/listApprovedInventoryOutDetails'

        // Inventory-in-details (from supplier)
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-90', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/showUnapprovedInvInFromSupplier'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-91', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/createInventoryInDetailsFromSupplier'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-93', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/updateInventoryInDetailsFromSupplier'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-94', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/deleteInventoryInDetailsFromSupplier'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-95', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/listUnapprovedInvInFromSupplier'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-164', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/approveInventoryInDetailsFromSupplier'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-165', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/showApprovedInvInFromSupplier'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-166', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/listApprovedInvInFromSupplier'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-167', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/adjustInvInFromSupplier'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-190', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/reverseAdjustInvInFromSupplier'

        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-90', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/showUnapprovedInvInFromSupplier'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-91', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/createInventoryInDetailsFromSupplier'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-93', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/updateInventoryInDetailsFromSupplier'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-94', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/deleteInventoryInDetailsFromSupplier'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-95', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/listUnapprovedInvInFromSupplier'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-165', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/showApprovedInvInFromSupplier'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-166', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/listApprovedInvInFromSupplier'

        // Inventory-in-details (from Inventory)
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-96', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/showUnapprovedInvInFromInventory'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-97', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/createInventoryInDetailsFromInventory'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-99', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/updateInventoryInDetailsFromInventory'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-100', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/deleteInventoryInDetailsFromInventory'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-101', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/listUnapprovedInvInFromInventory'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-169', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/showApprovedInvInFromInventory'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-170', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/listApprovedInvInFromInventory'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-168', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/approveInventoryInDetailsFromInventory'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-191', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/reverseAdjustInvInFromInventory'

        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-96', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/showUnapprovedInvInFromInventory'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-97', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/createInventoryInDetailsFromInventory'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-99', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/updateInventoryInDetailsFromInventory'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-100', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/deleteInventoryInDetailsFromInventory'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-101', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/listUnapprovedInvInFromInventory'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-169', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/showApprovedInvInFromInventory'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-170', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/listApprovedInvInFromInventory'


        // Inventory consumption Details
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-102', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/showUnApprovedInventoryConsumptionDetails'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-103', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/createInventoryConsumptionDetails'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-105', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/updateInventoryConsumptionDetails'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-106', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/deleteInventoryConsumptionDetails'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-107', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/listUnApprovedInventoryConsumptionDetails'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-108', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/approveInventoryConsumptionDetails'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-176', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/showApprovedInventoryConsumptionDetails'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-177', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/listApprovedInventoryConsumptionDetails'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-178', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/adjustInvConsumption'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-185', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/reverseAdjustInvConsumption'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-158', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/listFixedAssetByInventoryId'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-159', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/listFixedAssetByInventoryIdAndItemId'

        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-102', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/showUnApprovedInventoryConsumptionDetails'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-103', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/createInventoryConsumptionDetails'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-105', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/updateInventoryConsumptionDetails'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-106', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/deleteInventoryConsumptionDetails'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-107', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/listUnApprovedInventoryConsumptionDetails'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-176', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/showApprovedInventoryConsumptionDetails'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-177', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/listApprovedInventoryConsumptionDetails'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-158', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/listFixedAssetByInventoryId'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-159', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/listFixedAssetByInventoryIdAndItemId'

        // Get All Supplier List
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-111', pluginId: 4).save(); // url: '/supplier/listAllSupplier'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-111', pluginId: 4).save(); // url: '/supplier/listAllSupplier'

        // refresh dropDown using remote url
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-233', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/dropDownInventoryItemConsumptionReload'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-233', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/dropDownInventoryItemConsumptionReload'

        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-234', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/dropDownInventoryItemInFromInventoryReload'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-234', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/dropDownInventoryItemInFromInventoryReload'

        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-235', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/dropDownInventoryItemInFromSupplierReload'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-235', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/dropDownInventoryItemInFromSupplierReload'

        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-236', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/dropDownInventoryItemOutReload'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-236', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/dropDownInventoryItemOutReload'

        // Inventory Report
        // inventory invoice
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-119', pluginId: 4).save(); // url: '/invReport/showInvoice'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-120', pluginId: 4).save(); // url: '/invReport/searchInvoice'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-121', pluginId: 4).save(); // url: '/invReport/downloadInvoice'

        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-119', pluginId: 4).save(); // url: '/invReport/showInvoice'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-120', pluginId: 4).save(); // url: '/invReport/searchInvoice'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-121', pluginId: 4).save(); // url: '/invReport/downloadInvoice'

        // inventory stock
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-122', pluginId: 4).save(); // url: '/invReport/inventoryStock'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-124', pluginId: 4).save(); // url: '/invReport/listInventoryStock'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-125', pluginId: 4).save(); // url: '/invReport/downloadInventoryStock'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-223', pluginId: 4).save(); // url: '/invReport/downloadInventoryStockCsv'

        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-122', pluginId: 4).save(); // url: '/invReport/inventoryStock'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-124', pluginId: 4).save(); // url: '/invReport/listInventoryStock'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-125', pluginId: 4).save(); // url: '/invReport/downloadInventoryStock'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-223', pluginId: 4).save(); // url: '/invReport/downloadInventoryStockCsv'

        // item stock
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-126', pluginId: 4).save(); // url: '/invReport/showItemStock'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-127', pluginId: 4).save(); // url: '/invReport/listItemStock'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-128', pluginId: 4).save(); // url: '/invReport/listStockDetailsByItemId'

        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-126', pluginId: 4).save(); // url: '/invReport/showItemStock'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-127', pluginId: 4).save(); // url: '/invReport/listItemStock'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-128', pluginId: 4).save(); // url: '/invReport/listStockDetailsByItemId'

        // Inventory Status
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-160', pluginId: 4).save(); // url: '/invReport/showInventoryStatusWithQuantityAndValue'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-161', pluginId: 4).save(); // url: '/invReport/listInventoryStatusWithQuantityAndValue'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-162', pluginId: 4).save(); // url: '/invReport/downloadInventoryStatusWithQuantityAndValue'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-224', pluginId: 4).save(); // url: '/invReport/downloadInventoryStatusWithQuantityAndValueCsv'

        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-160', pluginId: 4).save(); // url: '/invReport/showInventoryStatusWithQuantityAndValue'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-161', pluginId: 4).save(); // url: '/invReport/listInventoryStatusWithQuantityAndValue'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-162', pluginId: 4).save(); // url: '/invReport/downloadInventoryStatusWithQuantityAndValue'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-224', pluginId: 4).save(); // url: '/invReport/downloadInventoryStatusWithQuantityAndValueCsv'

        // Inventory valuation
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-129', pluginId: 4).save(); // url: '/invReport/showInventoryValuation'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-130', pluginId: 4).save(); // url: '/invReport/searchInventoryValuation'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-131', pluginId: 4).save(); // url: '/invReport/downloadInventoryValuation'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-227', pluginId: 4).save(); // url: '/invReport/downloadInventoryValuationCsv'

        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-129', pluginId: 4).save(); // url: '/invReport/showInventoryValuation'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-130', pluginId: 4).save(); // url: '/invReport/searchInventoryValuation'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-131', pluginId: 4).save(); // url: '/invReport/downloadInventoryValuation'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-227', pluginId: 4).save(); // url: '/invReport/downloadInventoryValuationCsv'

        // transaction list
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-132', pluginId: 4).save(); // url: '/invReport/showInventoryTransactionList'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-133', pluginId: 4).save(); // url: '/invReport/searchInventoryTransactionList'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-134', pluginId: 4).save(); // url: '/invReport/downloadInventoryTransactionList'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-226', pluginId: 4).save(); // url: '/invReport/downloadInventoryTransactionListCsv'

        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-132', pluginId: 4).save(); // url: '/invReport/showInventoryTransactionList'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-133', pluginId: 4).save(); // url: '/invReport/searchInventoryTransactionList'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-134', pluginId: 4).save(); // url: '/invReport/downloadInventoryTransactionList'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-226', pluginId: 4).save(); // url: '/invReport/downloadInventoryTransactionListCsv'

        // Inventory summary
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-138', pluginId: 4).save(); // url: '/invReport/showInventorySummary'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-139', pluginId: 4).save(); // url: '/invReport/listInventorySummary'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-140', pluginId: 4).save(); // url: '/invReport/downloadInventorySummary'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-222', pluginId: 4).save(); // url: '/invReport/downloadInventorySummaryCsv'

        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-138', pluginId: 4).save(); // url: '/invReport/showInventorySummary'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-139', pluginId: 4).save(); // url: '/invReport/listInventorySummary'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-140', pluginId: 4).save(); // url: '/invReport/downloadInventorySummary'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-222', pluginId: 4).save(); // url: '/invReport/downloadInventorySummaryCsv'

        // consumption list
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-141', pluginId: 4).save(); // url: '/invReport/showConsumedItemList'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-142', pluginId: 4).save(); // url: '/invReport/listBudgetOfConsumption'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-143', pluginId: 4).save(); // url: '/invReport/listConsumedItemByBudget'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-220', pluginId: 4).save(); // url: '/invReport/downloadForConsumedItemList'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-239', pluginId: 4).save(); // url: '/invReport/downloadDetailsConsumedItemListCsv'

        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-141', pluginId: 4).save(); // url: '/invReport/showConsumedItemList'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-142', pluginId: 4).save(); // url: '/invReport/listBudgetOfConsumption'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-143', pluginId: 4).save(); // url: '/invReport/listConsumedItemByBudget'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-220', pluginId: 4).save(); // url: '/invReport/downloadForConsumedItemList'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-239', pluginId: 4).save(); // url: '/invReport/downloadDetailsConsumedItemListCsv'

        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-144', pluginId: 4).save(); // url: '/invInventoryTransaction/listAllInventoryByType'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-149', pluginId: 4).save(); // url: '/invInventoryTransaction/listInventoryIsFactoryByType'

        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-144', pluginId: 4).save(); // url: '/invInventoryTransaction/listAllInventoryByType'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-149', pluginId: 4).save(); // url: '/invInventoryTransaction/listInventoryIsFactoryByType'

        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-237', pluginId: 4).save(); // url: '/invInventoryTransaction/dropDownInventoryReload'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-237', pluginId: 4).save(); // url: '/invInventoryTransaction/dropDownInventoryReload'

        // item received stock
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-152', pluginId: 4).save(); // url: '/invReport/showItemReceivedStock'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-153', pluginId: 4).save(); // url: '/invReport/listItemReceivedStock'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-154', pluginId: 4).save(); // url: '/invReport/downloadItemReceivedStock'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-232', pluginId: 4).save(); // url: '/invReport/downloadItemReceivedGroupBySupplier'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-228', pluginId: 4).save(); // url: '/invReport/downloadItemReceivedStockCsv'

        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-152', pluginId: 4).save(); // url: '/invReport/showItemReceivedStock'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-153', pluginId: 4).save(); // url: '/invReport/listItemReceivedStock'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-154', pluginId: 4).save(); // url: '/invReport/downloadItemReceivedStock'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-232', pluginId: 4).save(); // url: '/invReport/downloadItemReceivedGroupBySupplier'
        new RoleFeatureMapping(roleTypeId: -10, transactionCode: 'INV-228', pluginId: 4).save(); // url: '/invReport/downloadItemReceivedStockCsv'

        // item wise budget summary
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-155', pluginId: 4).save(); // url: '/invReport/showItemWiseBudgetSummary'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-156', pluginId: 4).save(); // url: '/invReport/listItemWiseBudgetSummary'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-157', pluginId: 4).save(); // url: '/invReport/downloadItemWiseBudgetSummary'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-229', pluginId: 4).save(); // url: '/invReport/downloadItemWiseBudgetSummaryCsv'

        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-155', pluginId: 4).save(); // url: '/invReport/showItemWiseBudgetSummary'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-156', pluginId: 4).save(); // url: '/invReport/listItemWiseBudgetSummary'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-157', pluginId: 4).save(); // url: '/invReport/downloadItemWiseBudgetSummary'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-229', pluginId: 4).save(); // url: '/invReport/downloadItemWiseBudgetSummaryCsv'

        // production Report
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-193', pluginId: 4).save(); // url: '/invReport/showInventoryProductionRpt'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-194', pluginId: 4).save(); // url: '/invReport/searchInventoryProductionRpt'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-195', pluginId: 4).save(); // url: '/invReport/downloadInventoryProductionRpt'

        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-193', pluginId: 4).save(); // url: '/invReport/showInventoryProductionRpt'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-194', pluginId: 4).save(); // url: '/invReport/searchInventoryProductionRpt'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-195', pluginId: 4).save(); // url: '/invReport/downloadInventoryProductionRpt'

        // Supplier Chalan Report
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-196', pluginId: 4).save(); // url: '/invReport/showSupplierChalan'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-197', pluginId: 4).save(); // url: '/invReport/listSupplierChalan'

        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-196', pluginId: 4).save(); // url: '/invReport/showSupplierChalan'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-197', pluginId: 4).save(); // url: '/invReport/listSupplierChalan'

        // PO Item Received Report
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-198', pluginId: 4).save(); // url: '/invReport/showPoItemReceived'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-199', pluginId: 4).save(); // url: '/invReport/listPoItemReceived'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-200', pluginId: 4).save(); // url: '/invReport/downloadPoItemReceived'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-230', pluginId: 4).save(); // url: '/invReport/downloadPoItemReceivedCsv'

        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-198', pluginId: 4).save(); // url: '/invReport/showPoItemReceived'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-199', pluginId: 4).save(); // url: '/invReport/listPoItemReceived'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-200', pluginId: 4).save(); // url: '/invReport/downloadPoItemReceived'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-230', pluginId: 4).save(); // url: '/invReport/downloadPoItemReceivedCsv'

        // Acknowledge & download supplier chalan report
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-201', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/acknowledgeInvoiceFromSupplier'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-202', pluginId: 4).save(); // url: '/invReport/downloadSupplierChalanReport'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-221', pluginId: 4).save(); // url: '/invReport/downloadSupplierChalanCsvReport'

        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-201', pluginId: 4).save(); // url: '/invInventoryTransactionDetails/acknowledgeInvoiceFromSupplier'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-202', pluginId: 4).save(); // url: '/invReport/downloadSupplierChalanReport'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-221', pluginId: 4).save(); // url: '/invReport/downloadSupplierChalanCsvReport'

        // inventory status with value report
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-204', pluginId: 4).save(); // url: '/invReport/showInventoryStatusWithValue'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-205', pluginId: 4).save(); // url: '/invReport/listInventoryStatusWithValue'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-206', pluginId: 4).save(); // url: '/invReport/downloadInventoryStatusWithValue'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-218', pluginId: 4).save(); // url: '/invReport/downloadInventoryStatusWithValueCsv'

        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-204', pluginId: 4).save(); // url: '/invReport/showInventoryStatusWithValue'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-205', pluginId: 4).save(); // url: '/invReport/listInventoryStatusWithValue'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-206', pluginId: 4).save(); // url: '/invReport/downloadInventoryStatusWithValue'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-218', pluginId: 4).save(); // url: '/invReport/downloadInventoryStatusWithValueCsv'

        // inventory status report with Quantity
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-207', pluginId: 4).save(); // url: '/invReport/showInventoryStatusWithQuantity'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-208', pluginId: 4).save(); // url: '/invReport/listInventoryStatusWithQuantity'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-209', pluginId: 4).save(); // url: '/invReport/downloadInventoryStatusWithQuantity'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-219', pluginId: 4).save(); // url: '/invReport/downloadInventoryStatusWithQuantityCsv'

        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-207', pluginId: 4).save(); // url: '/invReport/showInventoryStatusWithQuantity'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-208', pluginId: 4).save(); // url: '/invReport/listInventoryStatusWithQuantity'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-209', pluginId: 4).save(); // url: '/invReport/downloadInventoryStatusWithQuantity'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-219', pluginId: 4).save(); // url: '/invReport/downloadInventoryStatusWithQuantityCsv'

        // Item-Reconciliation Report
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-214', pluginId: 4).save(); // url: '/invReport/showForItemReconciliation'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-215', pluginId: 4).save(); // url: '/invReport/listForItemReconciliation'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-216', pluginId: 4).save(); // url: '/invReport/downloadForItemReconciliation'
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'INV-225', pluginId: 4).save(); // url: '/invReport/downloadForItemReconciliationCsv'

        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-214', pluginId: 4).save(); // url: '/invReport/showForItemReconciliation'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-215', pluginId: 4).save(); // url: '/invReport/listForItemReconciliation'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-216', pluginId: 4).save(); // url: '/invReport/downloadForItemReconciliation'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'INV-225', pluginId: 4).save(); // url: '/invReport/downloadForItemReconciliationCsv'

        // budget list for consumption
        new RoleFeatureMapping(roleTypeId: rolePm.id, transactionCode: 'BUDG-29', pluginId: 4).save(); // url: '/budgBudget/getBudgetGridByInventory'
        new RoleFeatureMapping(roleTypeId: roleAuditor.id, transactionCode: 'BUDG-29', pluginId: 4).save(); // url: '/budgBudget/getBudgetGridByInventory'
    }

    /**
     * SQL to delete test data from database
     * @param roleTypeIds - list of role type ids
     */
    public void deleteTestData(List<Long> roleTypeIds) {
        if(roleTypeIds.size() > 0) {
            String lstRoleTypeIds = super.buildCommaSeparatedStringOfIds(roleTypeIds)
            String deleteSql = """ DELETE FROM role_feature_mapping WHERE role_type_id IN (${lstRoleTypeIds}) """
            executeUpdateSql(deleteSql)
        }
    }

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }
}
