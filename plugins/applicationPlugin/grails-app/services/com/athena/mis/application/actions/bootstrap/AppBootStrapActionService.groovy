package com.athena.mis.application.actions.bootstrap

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.config.AppConfigurationService
import com.athena.mis.application.deployment.AppUpdatePatchExecutorService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Company
import com.athena.mis.application.service.*
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

class AppBootStrapActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String COMPANY = "company"
    private static final String IS_DEFAULT_DATA = "isDefaultData"
    private static final String IS_DEFAULT_SCHEMA = "isDefaultSchema"

    SystemEntityTypeService systemEntityTypeService
    AppVersionService appVersionService
    ProjectService projectService
    AppDbInstanceService appDbInstanceService
    AppGroupService appGroupService
    AppGroupEntityService appGroupEntityService
    AppScheduleService appScheduleService
    AppShellScriptService appShellScriptService
    AppUserService appUserService
    AppUserEntityService appUserEntityService
    AppBankService appBankService
    AppBankBranchService appBankBranchService
    BenchmarkService benchmarkService
    BenchmarkStarService benchmarkStarService
    CompanyService companyService
    ContentCategoryService contentCategoryService
    AppCountryService appCountryService
    CurrencyService currencyService
    DbInstanceQueryService dbInstanceQueryService
    AppDesignationService appDesignationService
    DistrictService districtService
    ItemService itemService
    ItemTypeService itemTypeService
    RequestMapService requestMapService
    RoleService roleService
    SupplierService supplierService
    SysConfigurationService sysConfigurationService
    SystemEntityService systemEntityService
    VehicleService vehicleService
    AppThemeService appThemeService
    RoleModuleService roleModuleService
    AppMyFavouriteService appMyFavouriteService
    AppServerDbInstanceMappingService appServerDbInstanceMappingService
    AppServerInstanceService appServerInstanceService
    ReservedRoleService reservedRoleService
    ReservedSystemEntityService reservedSystemEntityService
    RoleFeatureMappingService roleFeatureMappingService
    SupplierItemService supplierItemService
    UserRoleService userRoleService
    VendorService vendorService
    AppMailService appMailService
    AppSmsService appSmsService
    AppConfigurationService appConfigurationService
    AppUpdatePatchExecutorService appUpdatePatchExecutorService

    ListAppVendorActionServiceModelService listAppVendorActionServiceModelService
    ListCompanyActionServiceModelService listCompanyActionServiceModelService
    ListAppEmployeeActionServiceModelService listAppEmployeeActionServiceModelService
    ListAppUserActionServiceModelService listAppUserActionServiceModelService
    ListAppCompanyUserActionServiceModelService listAppCompanyUserActionServiceModelService
    ListDistrictActionServiceModelService listDistrictActionServiceModelService
    ListUserRoleActionServiceModelService listUserRoleActionServiceModelService
    ListBankActionServiceModelService listBankActionServiceModelService
    ListBankBranchActionServiceModelService listBankBranchActionServiceModelService
    ListRoleActionServiceModelService listRoleActionServiceModelService
    ListMyRoleActionServiceModelService listMyRoleActionServiceModelService
    ListAppUserEntityActionServiceModelService listAppUserEntityActionServiceModelService
    ListItemTypeActionServiceModelService listItemTypeActionServiceModelService
    ListItemCategoryInventoryActionServiceModelService listItemCategoryInventoryActionServiceModelService
    ListItemCategoryNonInvActionServiceModelService listItemCategoryNonInvActionServiceModelService
    ListSupplierActionServiceModelService listSupplierActionServiceModelService
    ListSupplierItemActionServiceModelService listSupplierItemActionServiceModelService
    ListContentCategoryActionServiceModelService listContentCategoryActionServiceModelService
    ListSystemEntityTypeActionServiceModelService listSystemEntityTypeActionServiceModelService
    ListItemCategoryFixedAssetActionServiceModelService listItemCategoryFixedAssetActionServiceModelService
    ListAppNoteActionServiceModelService listAppNoteActionServiceModelService
    ListAllAppUserActionServiceModelService listAllAppUserActionServiceModelService
    ListCompanyForResellerActionServiceModelService listCompanyForResellerActionServiceModelService
    ListUserRoleForCompanyUserActionServiceModelService listUserRoleForCompanyUserActionServiceModelService
    ListForSendAppMailActionServiceModelService listForSendAppMailActionServiceModelService
    ListAppAnnouncementActionServiceModelService listAppAnnouncementActionServiceModelService
    ListForComposeSmsActionServiceModelService listForComposeSmsActionServiceModelService
    ListAppServerDbInstanceMappingActionServiceModelService listAppServerDbInstanceMappingActionServiceModelService
    ListAppServerInstanceActionServiceModelService listAppServerInstanceActionServiceModelService
    ListForSendSmsActionServiceModelService listForSendSmsActionServiceModelService
    SchemaInformationModelService schemaInformationModelService
    TestDataModelService testDataModelService
    ListRoleModuleActionServiceModelService listRoleModuleActionServiceModelService
    ListAppShellScriptActionServiceModelService listAppShellScriptActionServiceModelService
    ListAppDbInstanceActionServiceModelService listAppDbInstanceActionServiceModelService
    ListVendorForDplDashboardActionServiceModelService listVendorForDplDashboardActionServiceModelService
    ListForTruncateSamplingActionServiceModelService listForTruncateSamplingActionServiceModelService
    ListForTruncateSamplingStarActionServiceModelService listForTruncateSamplingStarActionServiceModelService
    ListAnnouncementAppMailActionServiceModelService listAnnouncementAppMailActionServiceModelService
    ListAppMessageActionServiceModelService listAppMessageActionServiceModelService
    ListAppFaqActionServiceModelService listAppFaqActionServiceModelService


    @Override
    @Transactional(readOnly = true)
    Map executePreCondition(Map params) {
        try {
            boolean isDefaultData = false
            boolean isDefaultSchema = false
            Company company = (Company) params.get(COMPANY)
            if (company) {
                isDefaultData = checkIfDefaultDataLoaded(company)   // check default data load for a new company
            } else {
                int count = appVersionService.countByPluginId(PluginConnector.PLUGIN_ID)
                if (count == 0) {
                    // load default data for initial run
                    isDefaultData = true
                    isDefaultSchema = true
                }
            }
            params.put(IS_DEFAULT_DATA, isDefaultData)
            params.put(IS_DEFAULT_SCHEMA, isDefaultSchema)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    // check if default data should be loaded for company
    private boolean checkIfDefaultDataLoaded(Company company) {
        Company existingCompany = (Company) companyService.read(company.id)
        if (!existingCompany) {
            return false
        } else {
            return !existingCompany.isDefaultDataLoaded
        }
    }

    @Override
    @Transactional
    Map execute(Map result) {
        try {
            boolean isDefaultData = (boolean) result.get(IS_DEFAULT_DATA)
            boolean isDefaultSchema = (boolean) result.get(IS_DEFAULT_SCHEMA)
            Company company = (Company) result.get(COMPANY)

            if (isDefaultSchema) {
                loadDefaultSchema() // create schema for initial run
                company = loadInitialData()   // load data for initial run
            }
            if (isDefaultData) {
                loadDefaultData(company.id)  // load default data
            }
            if (isDefaultSchema) {
                registerPlugin()     // register plugin
            }

            if (!isDefaultSchema && !isDefaultData) {
                executeUpdatePatch()    // execute update patch
            }

            loadCache()             // load cache
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    @Override
    Map executePostCondition(Map result) {
        return result
    }

    @Override
    Map buildSuccessResultForUI(Map result) {
        return result
    }

    @Override
    Map buildFailureResultForUI(Map result) {
        return result
    }

    private void loadDefaultSchema() {
        createSchema()
        createDomainModel()
    }

    private void createSchema() {
        companyService.createDefaultSchema()
        systemEntityService.createDefaultSchema()
        appDbInstanceService.createDefaultSchema()
        appGroupService.createDefaultSchema()
        projectService.createDefaultSchema()
        appScheduleService.createDefaultSchema()
        appShellScriptService.createDefaultSchema()
        appUserService.createDefaultSchema()
        appBankService.createDefaultSchema()
        appBankBranchService.createDefaultSchema()
        benchmarkService.createDefaultSchema()
        benchmarkStarService.createDefaultSchema()
        contentCategoryService.createDefaultSchema()
        appCountryService.createDefaultSchema()
        currencyService.createDefaultSchema()
        dbInstanceQueryService.createDefaultSchema()
        appDesignationService.createDefaultSchema()
        districtService.createDefaultSchema()
        itemService.createDefaultSchema()
        itemTypeService.createDefaultSchema()
        requestMapService.createDefaultSchema()
        roleService.createDefaultSchema()
        supplierService.createDefaultSchema()
        sysConfigurationService.createDefaultSchema()
        appThemeService.createDefaultSchema()
        vehicleService.createDefaultSchema()
        appGroupEntityService.createDefaultSchema()
        appUserEntityService.createDefaultSchema()
        schemaInformationModelService.createDefaultSchema()
        roleModuleService.createDefaultSchema()
        appMyFavouriteService.createDefaultSchema()
        appServerDbInstanceMappingService.createDefaultSchema()
        appServerInstanceService.createDefaultSchema()
        appVersionService.createDefaultSchema()
        reservedRoleService.createDefaultSchema()
        reservedSystemEntityService.createDefaultSchema()
        roleFeatureMappingService.createDefaultSchema()
        supplierItemService.createDefaultSchema()
        systemEntityTypeService.createDefaultSchema()
        userRoleService.createDefaultSchema()
        vendorService.createDefaultSchema()
    }

    private void createDomainModel() {
        listCompanyActionServiceModelService.createDefaultSchema()
        listAppEmployeeActionServiceModelService.createDefaultSchema()
        listAppUserActionServiceModelService.createDefaultSchema()
        listAppCompanyUserActionServiceModelService.createDefaultSchema()
        listDistrictActionServiceModelService.createDefaultSchema()
        listUserRoleActionServiceModelService.createDefaultSchema()
        listBankActionServiceModelService.createDefaultSchema()
        listBankBranchActionServiceModelService.createDefaultSchema()
        listRoleActionServiceModelService.createDefaultSchema()
        listMyRoleActionServiceModelService.createDefaultSchema()
        listAppUserEntityActionServiceModelService.createDefaultSchema()
        listItemTypeActionServiceModelService.createDefaultSchema()
        listItemCategoryInventoryActionServiceModelService.createDefaultSchema()
        listItemCategoryNonInvActionServiceModelService.createDefaultSchema()
        listSupplierActionServiceModelService.createDefaultSchema()
        listSupplierItemActionServiceModelService.createDefaultSchema()
        listContentCategoryActionServiceModelService.createDefaultSchema()
        listSystemEntityTypeActionServiceModelService.createDefaultSchema()
        listAppNoteActionServiceModelService.createDefaultSchema()
        listAllAppUserActionServiceModelService.createDefaultSchema()
        listItemCategoryFixedAssetActionServiceModelService.createDefaultSchema()
        listCompanyForResellerActionServiceModelService.createDefaultSchema()
        listUserRoleForCompanyUserActionServiceModelService.createDefaultSchema()
        listForSendAppMailActionServiceModelService.createDefaultSchema()
        listAppAnnouncementActionServiceModelService.createDefaultSchema()
        listForComposeSmsActionServiceModelService.createDefaultSchema()
        listAppServerInstanceActionServiceModelService.createDefaultSchema()
        listAppServerDbInstanceMappingActionServiceModelService.createDefaultSchema()
        listForSendSmsActionServiceModelService.createDefaultSchema()
        testDataModelService.createDefaultSchema()
        listRoleModuleActionServiceModelService.createDefaultSchema()
        listAppShellScriptActionServiceModelService.createDefaultSchema()
        listAppDbInstanceActionServiceModelService.createDefaultSchema()
        listAppVendorActionServiceModelService.createDefaultSchema()
        listVendorForDplDashboardActionServiceModelService.createDefaultSchema()
        listForTruncateSamplingActionServiceModelService.createDefaultSchema()
        listForTruncateSamplingStarActionServiceModelService.createDefaultSchema()
        listAnnouncementAppMailActionServiceModelService.createDefaultSchema()
        listAppMessageActionServiceModelService.createDefaultSchema()
        listAppFaqActionServiceModelService.createDefaultSchema()
    }


    private Company loadInitialData() {
        Company company = companyService.createDefaultData()

        systemEntityTypeService.createDefaultDataForApp()
        reservedSystemEntityService.createDefaultDataForApplication()
        reservedRoleService.createDefaultDataForApplication()
        roleService.createRoleForReseller()
        roleFeatureMappingService.createRoleFeatureMapForApplication()
        requestMapService.createRequestMapForApplicationPlugin()
        appUserService.createReseller(company.id)
        userRoleService.createDefaultDataForReseller()

        return company
    }

    private void loadDefaultData(long companyId) {
        appUserService.createDefaultDataForApplication(companyId)

        AppUser systemUser = appUserService.readSystemUserByCompanyId(companyId)
        long sysUserId = systemUser ? systemUser.id : 0L

        sysConfigurationService.createDefaultAppSysConfig(companyId)
        systemEntityService.createDefaultDataForApp(companyId, sysUserId)
        appThemeService.createDefaultData(companyId)

        roleService.createDefaultDataForApplication(companyId, sysUserId)
        requestMapService.appendRoleInRequestMap(PluginConnector.PLUGIN_ID, companyId)
        userRoleService.createDefaultDataForApplication(companyId)

        appMailService.createDefaultDataForApplication(companyId, sysUserId)
        contentCategoryService.createDefaultData(companyId, sysUserId)
        itemTypeService.createDefaultData(companyId, sysUserId)

        vendorService.createDefaultDataForVendor(companyId, sysUserId)
        appDbInstanceService.createDefaultDataForDBInstance(companyId, sysUserId)
        appSmsService.createDefaultData(companyId)
        appScheduleService.createDefaultData(companyId)
        appServerInstanceService.createDefaultDataForServerInstance(companyId, sysUserId)
        appShellScriptService.createDefaultData(companyId, sysUserId)
        dbInstanceQueryService.createDefaultData(companyId, sysUserId)
        roleModuleService.createDefaultDataForApplication(companyId)

        currencyService.createDefaultData(companyId, sysUserId)
        appCountryService.createDefaultData(companyId, sysUserId)
        appBankService.createDefaultData(companyId, sysUserId)
        districtService.createDefaultData(companyId, sysUserId)
        appBankBranchService.createDefaultData(companyId, sysUserId)
    }

    private void registerPlugin() {
        appVersionService.registerPlugin(PluginConnector.PLUGIN_ID, appConfigurationService.getAppPluginVersion(), PluginConnector.PLUGIN_NAME, PluginConnector.PLUGIN_PREFIX)
    }

    private void executeUpdatePatch() {
        appUpdatePatchExecutorService.init()
    }

    private void loadCache() {
        appScheduleService.executeByPlugin(PluginConnector.PLUGIN_ID)
        appConfigurationService.appSysConfigCacheService.init()
    }
}
