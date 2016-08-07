package com.mis.beacon.bootsrap

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.config.AppConfigurationService
import com.athena.mis.application.entity.Company
import com.athena.mis.application.service.*
import com.athena.mis.integration.beacon.BeaconPluginConnector
import com.athena.mis.integration.ictpool.IctPoolPluginConnector
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

class BeaconBootStrapActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String COMPANY = "company"
    private static final String IS_DEFAULT_DATA = "isDefaultData"
    private static final String IS_DEFAULT_SCHEMA = "isDefaultSchema"

    SystemEntityTypeService systemEntityTypeService
    AppVersionService appVersionService
    CompanyService companyService
    RequestMapService requestMapService
    SystemEntityService systemEntityService
    AppDbInstanceService appDbInstanceService
    AppGroupService appGroupService
    AppUserService appUserService
    RoleService roleService
    SysConfigurationService sysConfigurationService
    AppThemeService  appThemeService
    AppGroupEntityService appGroupEntityService
    AppUserEntityService appUserEntityService
    RoleModuleService roleModuleService
    AppMyFavouriteService appMyFavouriteService
    AppConfigurationService appConfigurationService

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
                int count = appVersionService.countByPluginId(BeaconPluginConnector.PLUGIN_ID)
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

                company = checkExistenceOfCompany()
                if (!company) return result
                loadDefaultSchema() // create schema for initial run
                loadInitialData()   // load data for initial run
            }
            if (isDefaultData) {
                loadDefaultData(company?.id)  // load default data
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
    private Company checkExistenceOfCompany() {
        Company company = Company.list(readOnly: true, sort: "id", order: "asc")[0]
        return company
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



    }

    private void createDomainModel() {

    }


    private Company loadInitialData() {
        Company company = companyService.createDefaultData()
        requestMapService.createRequestMapForBeacon()
        return company
    }

    private void loadDefaultData(long companyId) {




    }

    private void registerPlugin() {
        appVersionService.registerPlugin(BeaconPluginConnector.PLUGIN_ID, appConfigurationService.getAppPluginVersion(), BeaconPluginConnector.PLUGIN_NAME, BeaconPluginConnector.PLUGIN_PREFIX)
    }

    private void executeUpdatePatch() {
    }

    private void loadCache() {
    }
}
