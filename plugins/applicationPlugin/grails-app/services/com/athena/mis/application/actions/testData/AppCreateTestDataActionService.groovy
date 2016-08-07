package com.athena.mis.application.actions.testData

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.config.AppConfigurationService
import com.athena.mis.application.entity.AppDbInstance
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.service.*
import com.athena.mis.integration.accounting.AccPluginConnector
import com.athena.mis.integration.arms.ArmsPluginConnector
import com.athena.mis.integration.budget.BudgPluginConnector
import com.athena.mis.integration.datapipeline.DataPipeLinePluginConnector
import com.athena.mis.integration.document.DocumentPluginConnector
import com.athena.mis.integration.elearning.ELearningPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.integration.inventory.InvPluginConnector
import com.athena.mis.integration.procurement.ProcPluginConnector
import com.athena.mis.integration.projecttrack.PtPluginConnector
import grails.plugin.springsecurity.SpringSecurityService
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class AppCreateTestDataActionService extends BaseService implements ActionServiceIntf {

    SpringSecurityService springSecurityService
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
    BudgPluginConnector budgBudgetImplService
    @Autowired(required = false)
    ProcPluginConnector procProcurementImplService
    @Autowired(required = false)
    AccPluginConnector accAccountingImplService
    @Autowired(required = false)
    InvPluginConnector invInventoryImplService
    @Autowired(required = false)
    PtPluginConnector ptProjectTrackImplService
    @Autowired(required = false)
    ELearningPluginConnector elearningImplService
    @Autowired(required = false)
    DocumentPluginConnector documentImplService
    @Autowired(required = false)
    DataPipeLinePluginConnector dataPipeLineImplService

    private Logger log = Logger.getLogger(getClass())

    private static final String DATA_EXISTS = " already has data. Remove existing data before loading test data"
    private static final String SUCCESS_MSG = "Test data has been loaded successfully"
    private static final String SYS_CONFIG_NOT_FOUND = "System configuration not found to load test data"
    private static final String NOT_DEVELOPMENT_MODE = "Application has to be in development mode to load test data"
    private static
    final String DB_INSTANCE_NOT_FOUND = "Selected DB Instance is not Native. Test data can only be loaded in Native DB."
    private static final String COMPANY_ID = "companyId"

    /**
     * 1. check value of system configuration
     * 2. check for native db instance
     * 3. check existing data
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    Map executePreCondition(Map params) {
        try {
            long companyId = super.getCompanyId()
            SysConfiguration sysConfiguration = (SysConfiguration) appConfigurationService.appSysConfigCacheService.readByKeyAndCompanyId(appConfigurationService.appSysConfigCacheService.APPLICATION_DEPLOYMENT_MODE, companyId)
            if (!sysConfiguration) {
                return super.setError(params, SYS_CONFIG_NOT_FOUND)
            }
            long deploymentMode = Long.parseLong(sysConfiguration.value)
            if (deploymentMode != 2) {
                return super.setError(params, sysConfiguration.message ? sysConfiguration.message : NOT_DEVELOPMENT_MODE)
            }
            long dbInstanceId = Long.parseLong(params.dbInstanceId.toString())
            AppDbInstance dbInstance = appDbInstanceService.read(dbInstanceId)
            // check existence of DbInstance object
            if (!dbInstance || !dbInstance.isNative) {
                return super.setError(params, DB_INSTANCE_NOT_FOUND)
            }
            // check if data already exists then return with appropriate message
            String msg = checkExistenceOfTestData(companyId)
            if (msg) {
                protectPreviousData()
                return super.setError(params, msg)
            }
            params.put(COMPANY_ID, companyId)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. load test data in application module
     * 2. load test data in other existing module
     * This method is in transactional block and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    Map execute(Map result) {
        try {
            long companyId = (long) result.get(COMPANY_ID)
            AppUser systemUser = appUserService.readSystemUserByCompanyId(companyId)
            loadTestData(companyId, systemUser.id)
            loadTestDataForOtherModules(companyId, systemUser.id)
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
    Map executePostCondition(Map result) {
        return result
    }

    /**
     * 1. put success message
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, SUCCESS_MSG)
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     * @param result - map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * check existence of test data
     * @param companyId - id of company
     * @return - appropriate error message if test data exists; otherwise null value
     */
    private String checkExistenceOfTestData(long companyId) {
        int count = 0
        count = projectService.countByCompanyId(companyId)
        if (count > 0) {
            return 'Project' + DATA_EXISTS
        }
        count = vehicleService.countByCompanyId(companyId)
        if (count > 0) {
            return 'Vehicle' + DATA_EXISTS
        }
        count = supplierService.countByCompanyId(companyId)
        if (count > 0) {
            return 'Supplier' + DATA_EXISTS
        }
        count = appDesignationService.countByCompanyId(companyId)
        if (count > 0) {
            return 'Designation' + DATA_EXISTS
        }
        count = itemService.countByCompanyId(companyId)
        if (count > 0) {
            return 'Item' + DATA_EXISTS
        }
        count = appCustomerService.countByCompanyId(companyId)
        if (count > 0) {
            return 'Customer' + DATA_EXISTS
        }
        count = appGroupService.countByCompanyId(companyId)
        if (count > 0) {
            return 'Group' + DATA_EXISTS
        }
        if (PluginConnector.isPluginInstalled(BudgPluginConnector.PLUGIN_NAME)) {
            count = countBudgetScope(companyId)
            if (count > 0) {
                return 'Budget scope' + DATA_EXISTS
            }
        }
        if (PluginConnector.isPluginInstalled(InvPluginConnector.PLUGIN_NAME)) {
            count = countInvProductionLineItem(companyId)
            if (count > 0) {
                return 'Inventory production line item' + DATA_EXISTS
            }
        }
        if (PluginConnector.isPluginInstalled(AccPluginConnector.PLUGIN_NAME)) {
            count = countFinancialYear(companyId)
            if (count > 0) {
                return 'Accounting financial year' + DATA_EXISTS
            }
            count = countTier1(companyId)
            if (count > 0) {
                return 'Acc tier1' + DATA_EXISTS
            }
        }
        if (PluginConnector.isPluginInstalled(PtPluginConnector.PLUGIN_NAME)) {
            count = countPtProject(companyId)
            if (count > 0) {
                return 'Pt project' + DATA_EXISTS
            }
            count = countPtModule(companyId)
            if (count > 0) {
                return 'pt module' + DATA_EXISTS
            }
        }
        if (PluginConnector.isPluginInstalled(ExchangeHousePluginConnector.PLUGIN_NAME)) {
            count = countExchangeHouseTask(companyId)
            if (count > 0) {
                return 'Exchange House' + DATA_EXISTS
            }
        }
        if (PluginConnector.isPluginInstalled(ArmsPluginConnector.PLUGIN_NAME)) {
            count = countArmsTask(companyId)
            if (count > 0) {
                return 'ARMS' + DATA_EXISTS
            }
        }
        if (PluginConnector.isPluginInstalled(DocumentPluginConnector.PLUGIN_NAME)) {
            count = countDocument(companyId)
            if (count > 0) {
                return 'Document' + DATA_EXISTS
            }
        }
        return null
    }

    private static final String COUNT_BUDGET_SCOPE = """
        SELECT COUNT(id) AS count
        FROM budg_budget_scope
        WHERE company_id = :companyId
    """

    /**
     * Count budget scope
     * @param companyId - id of company
     * @return - an integer containing the value of count
     */
    private int countBudgetScope(long companyId) {
        Map queryParams = [
                companyId: companyId
        ]
        List results = executeSelectSql(COUNT_BUDGET_SCOPE, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_INV_PROD_LINE_ITEM = """
        SELECT COUNT(id) AS count
        FROM inv_production_line_item
        WHERE company_id = :companyId
    """

    /**
     * Count inv production line item
     * @param companyId - id of company
     * @return - an integer containing the value of count
     */
    private int countInvProductionLineItem(long companyId) {
        Map queryParams = [
                companyId: companyId
        ]
        List results = executeSelectSql(COUNT_INV_PROD_LINE_ITEM, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_FINANCIAL_YEAR = """
        SELECT COUNT(id) AS count
        FROM acc_financial_year
        WHERE company_id = :companyId
    """

    /**
     * Count financial year
     * @param companyId - id of company
     * @return - an integer containing the value of count
     */
    private int countFinancialYear(long companyId) {
        Map queryParams = [
                companyId: companyId
        ]
        List results = executeSelectSql(COUNT_FINANCIAL_YEAR, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_TIER1 = """
        SELECT COUNT(id) AS count
        FROM acc_tier1
        WHERE company_id = :companyId
    """

    /**
     * Count tier1
     * @param companyId - id of company
     * @return - an integer containing the value of count
     */
    private int countTier1(long companyId) {
        Map queryParams = [
                companyId: companyId
        ]
        List results = executeSelectSql(COUNT_TIER1, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_PT_PROJECT = """
        SELECT COUNT(id) AS count
        FROM pt_project
        WHERE company_id = :companyId
    """

    /**
     * Count pt project
     * @param companyId - id of company
     * @return - an integer containing the value of count
     */
    private int countPtProject(long companyId) {
        Map queryParams = [
                companyId: companyId
        ]
        List results = executeSelectSql(COUNT_PT_PROJECT, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_PT_MODULE = """
        SELECT COUNT(id) AS count
        FROM pt_module
        WHERE company_id = :companyId
    """

    /**
     * Count pt module
     * @param companyId - id of company
     * @return - an integer containing the value of count
     */
    private int countPtModule(long companyId) {
        Map queryParams = [
                companyId: companyId
        ]
        List results = executeSelectSql(COUNT_PT_MODULE, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_EXCHANGE_HOUSE_TASK = """
        SELECT COUNT(id) AS count
        FROM exh_task
        WHERE company_id = :companyId
    """

    /**
     * Count exchangeHouse task
     * @param companyId - id of company
     * @return - an integer containing the value of count
     */
    private int countExchangeHouseTask(long companyId) {
        Map queryParams = [
                companyId: companyId
        ]
        List results = executeSelectSql(COUNT_EXCHANGE_HOUSE_TASK, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_ARMS_TASK = """
        SELECT COUNT(id) AS count
        FROM rms_task
        WHERE company_id = :companyId
    """

    /**
     * Count arms task
     * @param companyId - id of company
     * @return - an integer containing the value of count
     */
    private int countArmsTask(long companyId) {
        Map queryParams = [
                companyId: companyId
        ]
        List results = executeSelectSql(COUNT_ARMS_TASK, queryParams)
        int count = results[0].count
        return count
    }


    private static final String COUNT_DOC_DOCUMENT = """
        SELECT COUNT(id) AS count
        FROM doc_document
        WHERE company_id = :companyId
    """


    /**
     * Count arms task
     * @param companyId - id of company
     * @return - an integer containing the value of count
     */
    private int countDocument(long companyId) {
        Map queryParams = [companyId: companyId]
        List results = executeSelectSql(COUNT_DOC_DOCUMENT, queryParams)
        int count = results[0].count
        return count
    }

    /**
     * load test data in application module
     * @param companyId - id of company
     * @param sysUserId - id of system user
     */
    private void loadTestData(long companyId, long sysUserId) {
        vehicleService.createTestData(companyId, sysUserId)
        projectService.createTestData(companyId, sysUserId)
        supplierService.createTestData(companyId, sysUserId)
        appCustomerService.createTestData(companyId, sysUserId)
        appDesignationService.createTestData(companyId, sysUserId)
        appEmployeeService.createTestData(companyId, sysUserId)
        itemService.createTestData(companyId, sysUserId)
        supplierItemService.createTestData(companyId, sysUserId)
        appGroupService.createTestData(companyId, sysUserId)
        appMailService.createTestData(companyId, sysUserId)
        appSmsService.createTestData(companyId)
    }

    /**
     * load test data in other existing module
     * @param companyId - id of company
     * @param sysUserId - id of system user
     */
    private void loadTestDataForOtherModules(long companyId, long systemUserId) {
        if (PluginConnector.isPluginInstalled(ExchangeHousePluginConnector.PLUGIN_NAME)) {
            exchangeHouseImplService.loadTestData(companyId, systemUserId)
        }
        if (PluginConnector.isPluginInstalled(ArmsPluginConnector.PLUGIN_NAME)) {
            armsImplService.loadTestData(companyId, systemUserId)
        }
        if (PluginConnector.isPluginInstalled(BudgPluginConnector.PLUGIN_NAME)) {
            budgBudgetImplService.loadTestData(companyId, systemUserId)
        }
        if (PluginConnector.isPluginInstalled(ProcPluginConnector.PLUGIN_NAME)) {
            procProcurementImplService.loadTestData(companyId, systemUserId)
        }
        if (PluginConnector.isPluginInstalled(InvPluginConnector.PLUGIN_NAME)) {
            invInventoryImplService.loadTestData(companyId, systemUserId)
        }
        if (PluginConnector.isPluginInstalled(AccPluginConnector.PLUGIN_NAME)) {
            accAccountingImplService.loadTestData(companyId, systemUserId)
        }
        if (PluginConnector.isPluginInstalled(PtPluginConnector.PLUGIN_NAME)) {
            ptProjectTrackImplService.loadTestData(companyId, systemUserId)
        }
        // create test data for data pipeline
        if (PluginConnector.isPluginInstalled(DataPipeLinePluginConnector.PLUGIN_NAME)) {
            dataPipeLineImplService.loadTestData(companyId)
        }
        if (PluginConnector.isPluginInstalled(DocumentPluginConnector.PLUGIN_NAME)) {
            documentImplService.loadTestData(companyId, systemUserId)
        }
        if (PluginConnector.isPluginInstalled(ELearningPluginConnector.PLUGIN_NAME)) {
            elearningImplService.loadTestData(companyId, systemUserId)
        }
        springSecurityService.clearCachedRequestmaps()
    }

    private boolean protectPreviousData() {
        AppUser appUser = super.getAppUser()
        String strQuery = """
            UPDATE app_user set login_id = 'x' || login_id WHERE id <> ${appUser.id} AND company_id = ${appUser.companyId};
            UPDATE sys_configuration set value = '"http://mySmsApi?user=username&password=pwd&sender=abc&SMSText=" + \${content} + "&GSM=" + \${recipient}'
            WHERE key = 'mis.application.smsUrl' AND company_id = ${appUser.companyId};
        """
        executeUpdateSql(strQuery)
        return true
    }
}
