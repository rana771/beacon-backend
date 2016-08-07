package com.athena.mis.application.actions.testData

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.config.AppConfigurationService
import com.athena.mis.application.entity.AppDbInstance
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.service.*
import com.athena.mis.integration.accounting.AccPluginConnector
import com.athena.mis.integration.arms.ArmsPluginConnector
import com.athena.mis.integration.budget.BudgPluginConnector
import com.athena.mis.integration.document.DocumentPluginConnector
import com.athena.mis.integration.elearning.ELearningPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.integration.inventory.InvPluginConnector
import com.athena.mis.integration.procurement.ProcPluginConnector
import com.athena.mis.integration.projecttrack.PtPluginConnector
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class AppDeleteTestDataActionService extends BaseService implements ActionServiceIntf {

    AppUserService appUserService
    VehicleService vehicleService
    ProjectService projectService
    SupplierService supplierService
    SupplierItemService supplierItemService
    AppCustomerService appCustomerService
    AppDesignationService appDesignationService
    AppEmployeeService appEmployeeService
    ItemService itemService
    AppGroupService appGroupService
    AppMailService appMailService
    AppSmsService appSmsService
    AppDbInstanceService appDbInstanceService
    AppConfigurationService appConfigurationService

    @Autowired(required = false)
    ExchangeHousePluginConnector exchangeHouseImplService
    @Autowired(required = false)
    ArmsPluginConnector armsImplService
    @Autowired(required = false)
    AccPluginConnector accAccountingImplService
    @Autowired(required = false)
    PtPluginConnector ptProjectTrackImplService
    @Autowired(required = false)
    InvPluginConnector invInventoryImplService
    @Autowired(required = false)
    ProcPluginConnector procProcurementImplService
    @Autowired(required = false)
    BudgPluginConnector budgBudgetImplService
    @Autowired(required = false)
    DocumentPluginConnector documentImplService
    @Autowired(required = false)
    ELearningPluginConnector elearningImplService

    private static final String SUCCESS_MSG = "Test data has been removed successfully"
    private static final String SYS_CONFIG_NOT_FOUND = "System configuration not found to delete test data"
    private static final String NOT_DEVELOPMENT_MODE = "Application has to be in development mode to delete test data"
    private static
    final String DB_INSTANCE_NOT_FOUND = "Selected DB Instance is not Native. Test data can only be deleted from Native DB."

    /**
     *
     * @param params
     * @return
     */
    Map executePreCondition(Map params) {
        try {
            long companyId = super.getCompanyId()
            SysConfiguration sysConfiguration = (SysConfiguration) appConfigurationService.appSysConfigCacheService.readByKeyAndCompanyId(appConfigurationService.appSysConfigCacheService.APPLICATION_DEPLOYMENT_MODE, companyId)
            if (!sysConfiguration) {
                return super.setError(params, SYS_CONFIG_NOT_FOUND)
            }
            long deploymentMode = Long.parseLong(sysConfiguration.value)
            if (deploymentMode != 2) {
                return super.setError(params, NOT_DEVELOPMENT_MODE)
            }
            long dbInstanceId = Long.parseLong(params.dbInstanceId.toString())
            AppDbInstance dbInstance = appDbInstanceService.read(dbInstanceId)
            // check existence of DbInstance object
            if (!dbInstance || !dbInstance.isNative) {
                return super.setError(params, DB_INSTANCE_NOT_FOUND)
            }
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Delete the test data
     * @param result - a map returned by precondition method
     * @return - a map containing isError flag and related message
     */
    @Transactional
    Map execute(Map result) {
        try {
            long companyId = super.companyId
            deleteTestDataForOtherModules()
            deleteTestData(companyId)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    Map executePostCondition(Map result) {
        return result
    }

    Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, SUCCESS_MSG)
    }

    Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * delete test data for application module
     * @param companyId
     */
    private void deleteTestData(long companyId) {
        appSmsService.deleteTestData(companyId)
        appMailService.deleteTestData(companyId)
        appGroupService.deleteTestData(companyId)
        supplierItemService.deleteTestData(companyId)
        itemService.deleteTestData(companyId)
        appEmployeeService.deleteTestData(companyId)
        appDesignationService.deleteTestData(companyId)
        appCustomerService.deleteTestData(companyId)
        supplierService.deleteTestData(companyId)
        projectService.deleteTestData(companyId)
        vehicleService.deleteTestData(companyId)
    }

    /**
     * delete test data for other modules if exists
     */
    private void deleteTestDataForOtherModules() {
        if (PluginConnector.isPluginInstalled(ExchangeHousePluginConnector.PLUGIN_NAME)) {
            exchangeHouseImplService.deleteTestData()
        }
        if (PluginConnector.isPluginInstalled(ArmsPluginConnector.PLUGIN_NAME)) {
            armsImplService.deleteTestData()
        }
        if (PluginConnector.isPluginInstalled(AccPluginConnector.PLUGIN_NAME)) {
            accAccountingImplService.deleteTestData()
        }
        if (PluginConnector.isPluginInstalled(PtPluginConnector.PLUGIN_NAME)) {
            ptProjectTrackImplService.deleteTestData()
        }
        if (PluginConnector.isPluginInstalled(InvPluginConnector.PLUGIN_NAME)) {
            invInventoryImplService.deleteTestData()
        }
        if (PluginConnector.isPluginInstalled(ProcPluginConnector.PLUGIN_NAME)) {
            procProcurementImplService.deleteTestData()
        }
        if (PluginConnector.isPluginInstalled(BudgPluginConnector.PLUGIN_NAME)) {
            budgBudgetImplService.deleteTestData()
        }

        if (PluginConnector.isPluginInstalled(DocumentPluginConnector.PLUGIN_NAME)) {
            documentImplService.deleteTestData()
        }
        if (PluginConnector.isPluginInstalled(ELearningPluginConnector.PLUGIN_NAME)) {
            elearningImplService.deleteTestData()
        }
    }
}
