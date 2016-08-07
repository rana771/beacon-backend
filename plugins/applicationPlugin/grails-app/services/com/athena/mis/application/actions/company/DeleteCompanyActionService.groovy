package com.athena.mis.application.actions.company

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.Company
import com.athena.mis.application.entity.Role
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.*
import com.athena.mis.integration.accounting.AccPluginConnector
import com.athena.mis.integration.document.DocumentPluginConnector
import com.athena.mis.integration.elearning.ELearningPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to delete company object from DB
 *          also delete companyLogo from AppAttachment domain
 *  For details go through Use-Case doc named 'DeleteCompanyActionService'
 */
class DeleteCompanyActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String USER_FOUND = " associated user(s) found of this company"
    private static final String DELETE_COMPANY_SUCCESS_MESSAGE = "Company has been deleted successfully"

    AppAttachmentService appAttachmentService
    CompanyService companyService
    AppUserService appUserService
    AppThemeService appThemeService
    AppDbInstanceService appDbInstanceService
    AppVersionService appVersionService
    DistrictService districtService
    AppBankService appBankService
    AppBankBranchService appBankBranchService
    AppSmsService appSmsService
    AppMailService appMailService
    SysConfigurationService sysConfigurationService
    SystemEntityService systemEntityService
    RoleService roleService
    AppCountryService appCountryService
    CurrencyService currencyService
    AppSystemEntityCacheService appSystemEntityCacheService
    ContentCategoryService contentCategoryService
    ItemTypeService itemTypeService
    VendorService vendorService
    AppScheduleService appScheduleService
    AppServerInstanceService appServerInstanceService
    AppShellScriptService appShellScriptService
    DbInstanceQueryService dbInstanceQueryService
    RoleModuleService roleModuleService

    /**
     * 1. check required parameters
     * 2. check if company has any user other than system user
     * @param parameters - serialize parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            if (!params.id) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            // check if any company has any user
           long companyId = Long.parseLong(params.id.toString())
            int count = appUserService.countByCompanyIdAndIsSystemUser(companyId, false)
            if (count > 0) {
                return super.setError(params, count.toString() + USER_FOUND)
            }
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Delete company object from DB
     * 1. delete all default data
     * 2. remove role from request map
     * 3. delete role
     * 4. delete country
     * 5. delete currency
     * 6. delete system user
     * 7. delete company
     * 8. delete company related attachments
     * This function is in transactional boundary and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            long companyId = Long.parseLong(result.id.toString())
            Company company = companyService.read(companyId)
            // delete default data
            deleteAllDefaultData(companyId)
            List<Role> lstRole = roleService.findAllByCompanyId(companyId)
            if (lstRole.size() > 0) {
                for (int i = 0; i < lstRole.size(); i++) {
                    Role role = lstRole[i]
                    removeRoleFromRequestMap(role.authority, role.pluginId)
                    removeUserRole(role.id)
                    removeRoleModule(role.id)
                }
            }
            roleService.deleteAllByCompanyId(companyId)
            appCountryService.deleteAllByCompanyId(companyId)
            currencyService.deleteAllByCompanyId(companyId)
            appUserService.deleteSystemUser(companyId)
            // pull system entity type(Company) object
            SystemEntity contentEntityTypeCompany = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_CONTENT_ENTITY_COMPANY, appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT_ENTITY, companyId)
            //delete company object from DB
            companyService.delete(company)
            //delete all images(e.g : Photo, signature, logo etc) of this Company
            appAttachmentService.delete(companyId, contentEntityTypeCompany.id)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * There is no postCondition, so return the same map as received
     * @param result - resulting map from execute
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * 1. put success message
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, DELETE_COMPANY_SUCCESS_MESSAGE)
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     * @param result - map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * 1. delete appTheme
     * 2. delete db instance
     * 3. delete sms
     * 4. delete mail
     * 5. delete system configuration
     * 6. delete system entity
     * If Accounting plugin is installed then
     *      -- delete acc type
     *      -- delete acc group
     *  If Exchange house plugin is installed then
     *      -- delete regular fee
     *      -- delete district
     *      -- delete bank
     *      -- delete bankBranch
     * @param companyId - id of company
     */
    private void deleteAllDefaultData(long companyId) {
        deleteTheme(companyId)
        deleteDbInstance(companyId)
        deleteSms(companyId)
        deleteAppMail(companyId)
        deleteSystemConfiguration(companyId)
        deleteSystemEntity(companyId)
        deleteContentCategory(companyId)
        deleteItemType(companyId)
        deleteVendor(companyId)
        deleteDistrict(companyId)
        deleteBank(companyId)
        deleteBankBranch(companyId)
        deleteAppSchedule(companyId)
        deleteServerInstance(companyId)
        deleteShellScript(companyId)
        deleteDbInstanceQuery(companyId)

        if (PluginConnector.isPluginInstalled(AccPluginConnector.PLUGIN_NAME)) {
            deleteAccType(companyId)
            deleteAccGroup(companyId)
        }
        if (PluginConnector.isPluginInstalled(ExchangeHousePluginConnector.PLUGIN_NAME)) {
            deleteRegularFee(companyId)
        }

        if (PluginConnector.isPluginInstalled(DocumentPluginConnector.PLUGIN_NAME)) {
            deleteDocDocumentType(companyId)
            deleteDocSubCategoryUserMapping(companyId)
            deleteDocCategoryUserMapping(companyId)
            deleteDocSubCategory(companyId)
            deleteDocCategory(companyId)
        }

        if (PluginConnector.isPluginInstalled(ELearningPluginConnector.PLUGIN_NAME)) {
            deleteAppPage(companyId)
        }
    }

    private void deleteTheme(long companyId) {
        appThemeService.deleteAllByCompanyId(companyId)
    }

    private void deleteDbInstance(long companyId) {
        appDbInstanceService.deleteAllByCompanyId(companyId)
    }

    private void deleteSms(long companyId) {
        appSmsService.deleteAllByCompanyId(companyId)
    }

    private void deleteAppMail(long companyId) {
        appMailService.deleteAllByCompanyId(companyId)
    }

    private void deleteSystemConfiguration(long companyId) {
        sysConfigurationService.deleteAllByCompanyId(companyId)
    }

    private void deleteSystemEntity(long companyId) {
        systemEntityService.deleteAllByCompanyId(companyId)
    }

    private void deleteContentCategory(long companyId) {
        contentCategoryService.deleteAllByCompanyId(companyId)
    }

    private void deleteItemType(long companyId) {
        itemTypeService.deleteAllByCompanyId(companyId)
    }

    private void deleteVendor(long companyId) {
        vendorService.deleteAllByCompanyId(companyId)
    }

    private void deleteAppSchedule(long companyId) {
        appScheduleService.deleteAllByCompanyId(companyId)
    }

    private void deleteServerInstance(long companyId) {
        appServerInstanceService.deleteAllByCompanyId(companyId)
    }

    private void deleteShellScript(long companyId) {
        appShellScriptService.deleteAllByCompanyId(companyId)
    }

    private void deleteDbInstanceQuery(long companyId) {
        dbInstanceQueryService.deleteAllByCompanyId(companyId)
    }

    private void removeRoleModule(long roleId) {
        roleModuleService.deleteAllByRoleId(roleId)
    }

    private static final String DELETE_ALL_ACC_TPE = """
        DELETE FROM acc_type WHERE company_id = :companyId
    """

    /**
     * Delete all accType by companyId
     * @param companyId - id of company
     */
    public void deleteAccType(long companyId) {
        Map queryParams = [
                companyId: companyId
        ]
        executeUpdateSql(DELETE_ALL_ACC_TPE, queryParams)
    }

    private static final String DELETE_ALL_ACC_GROUP = """
        DELETE FROM acc_group WHERE company_id = :companyId
    """

    /**
     * Delete all accGroup by companyId
     * @param companyId - id of company
     */
    public void deleteAccGroup(long companyId) {
        Map queryParams = [
                companyId: companyId
        ]
        executeUpdateSql(DELETE_ALL_ACC_GROUP, queryParams)
    }

    private static final String DELETE_ALL_REGULAR_FEE = """
        DELETE FROM exh_regular_fee WHERE company_id = :companyId
    """

    /**
     * Delete all ExhRegularFee by companyId
     * @param companyId - id of company
     */
    public void deleteRegularFee(long companyId) {
        Map queryParams = [
                companyId: companyId
        ]
        executeUpdateSql(DELETE_ALL_REGULAR_FEE, queryParams)
    }

    private void deleteDistrict(long companyId) {
        districtService.deleteAllByCompanyId(companyId)
    }

    private void deleteBank(long companyId) {
        appBankService.deleteAllByCompanyId(companyId)
    }

    private void deleteBankBranch(long companyId) {
        appBankBranchService.deleteAllByCompanyId(companyId)
    }

    private void removeRoleFromRequestMap(String roleAuthority, long pluginId) {
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
    }

    private static final String DELETE_ALL_USER_ROLE = """
        DELETE FROM user_role WHERE role_id = :roleId
    """

    /**
     * Delete all user role mapping
     * @param roleId - id of Role
     */
    public void removeUserRole(long roleId) {
        Map queryParams = [
                roleId: roleId
        ]
        executeUpdateSql(DELETE_ALL_USER_ROLE, queryParams)
    }


    private static final String DELETE_ALL_DOC_DOCUMENT_TYPE = """
        DELETE FROM doc_document_type WHERE company_id = :companyId
    """

    /**
     * Delete all DocDocumentType by companyId
     * @param companyId - id of company
     */
    public void deleteDocDocumentType(long companyId) {
        Map queryParams = [companyId: companyId]
        executeUpdateSql(DELETE_ALL_DOC_DOCUMENT_TYPE, queryParams)
    }


    private static final String DELETE_ALL_DOC_SUB_CATEGORY_USER_MAPPING = """
        DELETE FROM doc_sub_category_user_mapping WHERE company_id = :companyId
    """

    /**
     * Delete all DocSubCategoryUserMapping by companyId
     * @param companyId - id of company
     */
    public void deleteDocSubCategoryUserMapping(long companyId) {
        Map queryParams = [companyId: companyId]
        executeUpdateSql(DELETE_ALL_DOC_SUB_CATEGORY_USER_MAPPING, queryParams)
    }



    private static final String DELETE_ALL_DOC_CATEGORY_USER_MAPPING = """
        DELETE FROM doc_category_user_mapping WHERE company_id = :companyId
    """

    /**
     * Delete all DocCategoryUserMapping by companyId
     * @param companyId - id of company
     */
    public void deleteDocCategoryUserMapping(long companyId) {
        Map queryParams = [companyId: companyId]
        executeUpdateSql(DELETE_ALL_DOC_CATEGORY_USER_MAPPING, queryParams)
    }


    private static final String DELETE_ALL_DOC_SUB_CATEGORY = """
        DELETE FROM doc_sub_category WHERE company_id = :companyId
    """

    /**
     * Delete all DocSubCategory by companyId
     * @param companyId - id of company
     */
    public void deleteDocSubCategory(long companyId) {
        Map queryParams = [companyId: companyId]
        executeUpdateSql(DELETE_ALL_DOC_SUB_CATEGORY, queryParams)
    }


    private static final String DELETE_ALL_DOC_CATEGORY = """
        DELETE FROM doc_category WHERE company_id = :companyId
    """

    /**
     * Delete all DocCategory by companyId
     * @param companyId - id of company
     */
    public void deleteDocCategory(long companyId) {
        Map queryParams = [companyId: companyId]
        executeUpdateSql(DELETE_ALL_DOC_CATEGORY, queryParams)
    }

    private static final String DELETE_ALL_APP_PAGE = """
        DELETE FROM app_page WHERE company_id = :companyId
    """

    /**
     * Delete all AppPage by companyId
     * @param companyId - id of company
     */
    public void deleteAppPage(long companyId) {
        Map queryParams = [companyId: companyId]
        executeUpdateSql(DELETE_ALL_APP_PAGE, queryParams)
    }
}
