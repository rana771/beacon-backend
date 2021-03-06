package com.athena.mis.application.actions.appuser

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppAttachmentService
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.AppUserService
import com.athena.mis.integration.accounting.AccPluginConnector
import com.athena.mis.integration.budget.BudgPluginConnector
import com.athena.mis.integration.document.DocumentPluginConnector
import com.athena.mis.integration.elearning.ELearningPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.integration.fixedasset.FxdPluginConnector
import com.athena.mis.integration.inventory.InvPluginConnector
import com.athena.mis.integration.procurement.ProcPluginConnector
import com.athena.mis.integration.qsmeasurement.QsPluginConnector
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Delete appUser object from DB and remove it from grid
 *  For details go through Use-Case doc named 'DeleteAppUserActionService'
 */
class DeleteAppUserActionService extends BaseService implements ActionServiceIntf {

    private static final String DELETE_USER_SUCCESS_MESSAGE = "User has been deleted successfully"
    private static final String RESERVED_USER_CAN_NOT_BE_DELETED = "Reserved user can not be deleted"
    private static final String HAS_ASSOCIATION_SYSTEM_USER = "System user could not be deleted"
    private static final String HAS_ASSOCIATION_USER_ROLE = " user role(s) associated with selected user"
    private static final String HAS_ASSOCIATION_USER_ENTITY = "(s) associated with selected user"
    private static final String HAS_ASSOCIATION_MESSAGE_BUDGET = " budget(s) associated with selected user"
    private static
    final String HAS_ASSOCIATION_MESSAGE_BUDGET_DETAILS = " budget details(s) associated with selected user"
    private static
    final String HAS_ASSOCIATION_MESSAGE_PURCHASE_REQUEST = " purchase request(s) associated with selected user"
    private static
    final String HAS_ASSOCIATION_MESSAGE_PURCHASE_ORDER = " purchase order(s) associated with selected user"
    private static
    final String HAS_ASSOCIATION_MESSAGE_INVENTORY_TRANSACTION = " inventory transaction(s) associated with selected user"
    private static final String HAS_ASSOCIATION_MESSAGE_COMPANY = " company(s) associated with selected user"
    private static final String HAS_ASSOCIATION_MESSAGE_EMPLOYEE = " employee(s) associated with selected user"
    private static final String HAS_ASSOCIATION_MESSAGE_PROJECT = " project(s) associated with selected user"
    private static final String HAS_ASSOCIATION_MESSAGE_USER_GROUP = " user-group(s) associated with selected user"
    private static
    final String HAS_ASSOCIATION_MESSAGE_ACC_CHART_OF_ACCOUNT = " chart of account(s) associated with selected user"
    private static final String HAS_ASSOCIATION_MESSAGE_ACC_DIVISION = " division(s) associated with selected user"
    private static
    final String HAS_ASSOCIATION_MESSAGE_ACC_FINANCIAL_YEAR = " financial year(s) associated with selected user"
    private static
    final String HAS_ASSOCIATION_MESSAGE_ACC_IOU_PURPOSE = " iou purpose(s) associated with selected user"
    private static final String HAS_ASSOCIATION_MESSAGE_ACC_IOU_SLIP = " iou slip(s) associated with selected user"
    private static
    final String HAS_ASSOCIATION_MESSAGE_ACC_SUB_ACCOUNT = " sub account(s) associated with selected user"
    private static
    final String HAS_ASSOCIATION_MESSAGE_ACC_VOUCHER_DETAILS = " voucher(s) associated with selected user"
    private static
    final String HAS_ASSOCIATION_MESSAGE_ACC_VOUCHER_TYPE_COA = " voucher type(s) associated with selected user"
    private static
    final String HAS_ASSOCIATION_MESSAGE_FXD_CATEGORY_MAINTENANCE_TYPE = " category maintenance type mapping(s) associated with selected user"
    private static
    final String HAS_ASSOCIATION_MESSAGE_FXD_FIXED_ASSET_DETAILS = " fixed asset details associated with selected user"
    private static
    final String HAS_ASSOCIATION_MESSAGE_FXD_FIXED_ASSET_TRACE = " fixed asset trace(s) associated with selected user"
    private static
    final String HAS_ASSOCIATION_MESSAGE_FXD_MAINTENANCE = " fixed asset maintenance(s) associated with selected user"
    private static
    final String HAS_ASSOCIATION_MESSAGE_FXD_MAINTENANCE_TYPE = " fixed asset maintenance type(s) associated with selected user"
    private static
    final String HAS_ASSOCIATION_MESSAGE_INV_INVENTORY_TRANSACTION_DETAILS = " inventory transaction(s) associated with selected user"
    private static final String HAS_ASSOCIATION_MESSAGE_ACC_INDENT = " indent(s) associated with selected user"
    private static
    final String HAS_ASSOCIATION_MESSAGE_ACC_INDENT_DETAILS = " indent item(s) associated with selected user"
    private static
    final String HAS_ASSOCIATION_MESSAGE_PROC_PURCHASE_ORDER_DETAILS = " PO Item(s) associated with selected user"
    private static
    final String HAS_ASSOCIATION_MESSAGE_PROC_PURCHASE_REQUEST_DETAILS = " PR Item(s) associated with selected user"
    private static
    final String HAS_ASSOCIATION_MESSAGE_PROC_TERMS_AND_CONDITION = " terms & condition(s) associated with selected user"
    private static
    final String HAS_ASSOCIATION_MESSAGE_QS_MEASUREMENT = " QS measurement(s) associated with selected user"
    //Exh
    private static
    final String HAS_ASSOCIATION_MESSAGE_EXH_TASK = " exchange house task(s) associated with selected user"
    private static
    final String HAS_ASSOCIATION_MESSAGE_EXH_AGENT = " exchange house agent(s) associated with selected user"
    private static
    final String HAS_ASSOCIATION_MESSAGE_EXH_AGENT_CURRENCY_POSTING = " exchange house agent currency posting(s) associated with selected user"
    private static
    final String HAS_ASSOCIATION_MESSAGE_EXH_BENEFICIARY = " exchange house beneficiary(s) associated with selected user"
    private static
    final String HAS_ASSOCIATION_MESSAGE_EXH_CURRENCY_CONVERSION = " exchange house currency conversion(s) associated with selected user"
    private static
    final String HAS_ASSOCIATION_MESSAGE_EXH_CUSTOMER = " exchange house customer(s) associated with selected user"
    private static
    final String HAS_ASSOCIATION_MESSAGE_EXH_CUSTOMER_BENEFICIARY_MAPPING = " exchange house customer beneficiary mapping(s) associated with selected user"
    private static
    final String HAS_ASSOCIATION_MESSAGE_EXH_CUSTOMER_TRACE = " exchange house customer trace(s) associated with selected user"
    private static
    final String HAS_ASSOCIATION_MESSAGE_EXH_PHOTO_ID_TYPE = " exchange house photo id type(s) associated with selected user"
    private static
    final String HAS_ASSOCIATION_MESSAGE_EXH_POSTAL_CODE = " exchange house postal code(s) associated with selected user"
    private static
    final String HAS_ASSOCIATION_MESSAGE_EXH_REGULAR_FEE = " exchange house regular fee(s) associated with selected user"
    private static
    final String HAS_ASSOCIATION_MESSAGE_EXH_REMITTANCE_PURPOSE = " exchange house remittance purpose(s) associated with selected user"
    private static
    final String HAS_ASSOCIATION_MESSAGE_EXH_TASK_TRACE = "exchange house task trace(s) associated with selected user"
    private static
    final String HAS_ASSOCIATION_MESSAGE_DOC_CATEGORY = "document category(s) associated with selected user"
    private static
    final String HAS_ASSOCIATION_MESSAGE_EL_COURSE = "course(s) associated with selected user"
    private static final String APP_USER = "appUser"

    AppUserService appUserService
    AppAttachmentService appAttachmentService
    AppSystemEntityCacheService appSystemEntityCacheService

    private Logger log = Logger.getLogger(getClass())

    /**
     * 1. check required parameter
     * 2. pull appUser object from service
     * 3. check for appUser object existence
     * 4. check association of appUser with relevant domains
     * @param parameters - serialize parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePreCondition(Map params) {
        try {
            // check required parameter
            if (!params.id) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long appUserId = Long.parseLong(params.id.toString())
            // get appUser object from cache utility by appUserId
            AppUser appUser = appUserService.read(appUserId)
            // check if appUser exists or not
            if (!appUser) {
                return super.setError(params, ENTITY_NOT_FOUND_ERROR_MESSAGE)
            }
            // restrict delete operation when reserved
            if (appUser.isReserved) {
                return super.setError(params, RESERVED_USER_CAN_NOT_BE_DELETED)
            }
            // check association of appUser with relevant domains
            String msg = hasAssociation(appUser)
            if (msg) {
                return super.setError(params, msg)
            }
            params.put(APP_USER, appUser)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Delete appUser object from DB
     * 1. get the appUser object from map
     * 2. delete from db
     * 3. delete all images(e.g : Photo, signature, logo etc) of appUser
     * This function is in transactional boundary and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppUser appUser = (AppUser) result.get(APP_USER)
            long companyId = appUser.companyId
            long appUserId = appUser.id
            appUserService.delete(appUser)  // delete appUser object from DB
            // pull system entity type(AppUser) object
            SystemEntity contentEntityTypeAppUser = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_CONTENT_ENTITY_APP_USER, appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT_ENTITY, companyId)
            // delete all images(e.g : Photo, signature, logo etc) of this AppUser
            appAttachmentService.delete(appUserId, contentEntityTypeAppUser.id)
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
        return super.setSuccess(result, DELETE_USER_SUCCESS_MESSAGE)
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
     * Check association of appUser with relevant domains
     *      1. check association with userId of UserRole
     *      2. check association with appUserId of AppUserEntity
     *      3. check association with createdBy and updatedBy of Company
     *      4. check association with createdBy and updatedBy of Employee
     *      5. check association with createdBy and updatedBy of Project
     *      6. check association with createdBy and updatedBy of AppGroup
     * If Budget plugin is installed then
     *      1. check association with createdBy and updatedBy of BudgBudget
     *      2. check association with createdBy and updatedBy of BudgBudgetDetails
     * If Procurement plugin is installed then
     *      1. check association with createdBy, updatedBy, approvedByDirectorId and approvedByProjectDirectorId of ProcPurchaseRequest
     *      2. check association with createdBy, updatedBy, approvedByDirectorId and approvedByProjectDirectorId of ProcPurchaseOrder
     *      3. check association with createdBy and updatedBy of ProcPurchaseRequestDetails
     *      4. check association with createdBy and updatedBy of ProcPurchaseOrderDetails
     *      5. check association with createdBy and updatedBy of ProcTermsAndCondition
     * If Accounting plugin is installed then
     *      1. check association with createdBy of AccChartOfAccount
     *      2. check association with createdBy and updatedBy of AccDivision
     *      3. check association with createdBy and updatedBy of AccFinancialYear
     *      4. check association with createdBy and updatedBy of AccIndent
     *      5. check association with createdBy and updatedBy of AccIndentDetails
     *      6. check association with createdBy and updatedBy of AccIouPurpose
     *      7. check association with createdBy and updatedBy of AccIouSlip
     *      8. check association with createdBy and updatedBy of AccSubAccount
     *      9. check association with createdBy of AccVoucherDetails
     *      10. check association with createdBy and updatedBy of AccVoucherTypeCoa
     * If QS plugin is installed then
     *      1. check association with createdBy and updatedBy of QsMeasurement
     * If Inventory plugin is installed then
     *      1. check association with createdBy and updatedBy of InvInventoryTransaction
     *      2. check association with createdBy and updatedBy of InvInventoryTransactionDetails
     * If FixedAsset plugin is installed then
     *      1. check association with createdBy and updatedBy of FxdCategoryMaintenanceType
     *      2. check association with createdBy and updatedBy of FxdFixedAssetDetails
     *      3. check association with createdBy of FxdFixedAssetTrace
     *      4. check association with createdBy and updatedBy of FxdMaintenance
     *      5. check association with createdBy and updatedBy of FxdMaintenanceType
     * @param appUser -object of AppUser
     * @return -a string containing error message or null value depending on association check
     */
    private String  hasAssociation(AppUser appUser) {
        long appUserId = appUser.id
        int count = 0
        // system user could not be deleted
        if (appUser.isSystemUser) {
            return HAS_ASSOCIATION_SYSTEM_USER
        }
        // count user-role mapping associated with user
        count = countUserRole(appUserId)
        if (count > 0) {
            return count.toString() + HAS_ASSOCIATION_USER_ROLE
        }
        // count user entity mapping associated with user
        String msg = countUserEntity(appUserId)
        if (msg) {
            return msg
        }
        // count number of company created or updated by user
        count = countCompany(appUserId)
        if (count > 0) {
            return count.toString() + HAS_ASSOCIATION_MESSAGE_COMPANY
        }
        // count number of employee created or updated by user
        count = countEmployee(appUserId)
        if (count > 0) {
            return count.toString() + HAS_ASSOCIATION_MESSAGE_EMPLOYEE
        }
        // count number of project created or updated by user
        count = countProject(appUserId)
        if (count > 0) {
            return count.toString() + HAS_ASSOCIATION_MESSAGE_PROJECT
        }
        // count number of group created or updated by user
        count = countAppGroup(appUserId)
        if (count > 0) {
            return count.toString() + HAS_ASSOCIATION_MESSAGE_USER_GROUP
        }

        if (PluginConnector.isPluginInstalled(BudgPluginConnector.PLUGIN_NAME)) {
            // count number of budget created or updated by user
            count = countBudget(appUserId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_BUDGET
            }
            // count number of budget details created or updated by user
            count = countBudgetDetails(appUserId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_BUDGET_DETAILS
            }
        }

        if (PluginConnector.isPluginInstalled(ProcPluginConnector.PLUGIN_NAME)) {
            // count number of purchase request created, updated or approved by user
            count = countPurchaseRequest(appUserId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_PURCHASE_REQUEST
            }
            // count number of purchase order created, updated or approved by user
            count = countPurchaseOrder(appUserId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_PURCHASE_ORDER
            }
            // count number of purchase order details created or updated by user
            count = countProcPurchaseOrderDetails(appUserId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_PROC_PURCHASE_ORDER_DETAILS
            }
            // count number of purchase request details created or updated by user
            count = countProcPurchaseRequestDetails(appUserId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_PROC_PURCHASE_REQUEST_DETAILS
            }
            // count number of terms and condition created or updated by user
            count = countProcTermsAndCondition(appUserId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_PROC_TERMS_AND_CONDITION
            }
        }

        if (PluginConnector.isPluginInstalled(AccPluginConnector.PLUGIN_NAME)) {
            // count number of chart of account created by user
            count = countAccChartOfAccount(appUserId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_ACC_CHART_OF_ACCOUNT
            }
            // count number of division created or updated by user
            count = countAccDivision(appUserId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_ACC_DIVISION
            }
            // count number of financial year created or updated by user
            count = countAccFinancialYear(appUserId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_ACC_FINANCIAL_YEAR
            }
            // count number of indent created or updated by user
            count = countAccIndent(appUserId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_ACC_INDENT
            }
            // count number of indent details created or updated by user
            count = countAccIndentDetails(appUserId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_ACC_INDENT_DETAILS
            }
            // count number of iou purpose created or updated by user
            count = countAccIouPurpose(appUserId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_ACC_IOU_PURPOSE
            }
            // count number of iou slip created or updated by user
            count = countAccIouSlip(appUserId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_ACC_IOU_SLIP
            }
            // count number of sub account created or updated by user
            count = countAccSubAccount(appUserId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_ACC_SUB_ACCOUNT
            }
            // count number of voucher details created by user
            count = countAccVoucherDetails(appUserId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_ACC_VOUCHER_DETAILS
            }
            // count number of voucher type coa created or updated by user
            count = countAccVoucherTypeCoa(appUserId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_ACC_VOUCHER_TYPE_COA
            }
        }

        if (PluginConnector.isPluginInstalled(QsPluginConnector.PLUGIN_NAME)) {
            // count number of qs measurement created or updated by user
            count = countQsMeasurement(appUserId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_QS_MEASUREMENT
            }
        }

        if (PluginConnector.isPluginInstalled(InvPluginConnector.PLUGIN_NAME)) {
            // count number of inventory transaction created or updated by user
            count = countInventoryTransaction(appUserId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_INVENTORY_TRANSACTION
            }
            // count number of inventory transaction details created or updated by user
            count = countInvInventoryTransactionDetails(appUserId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_INV_INVENTORY_TRANSACTION_DETAILS
            }
        }

        if (PluginConnector.isPluginInstalled(FxdPluginConnector.PLUGIN_NAME)) {
            // count number of category maintenance type created or updated by user
            count = countFxdCategoryMaintenanceType(appUserId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_FXD_CATEGORY_MAINTENANCE_TYPE
            }
            // count number of fixed asset details created or updated by user
            count = countFxdFixedAssetDetails(appUserId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_FXD_FIXED_ASSET_DETAILS
            }
            // count number of fixed asset trace created by user
            count = countFxdFixedAssetTrace(appUserId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_FXD_FIXED_ASSET_TRACE
            }
            // count number of fxd maintenance created or updated by user
            count = countFxdMaintenance(appUserId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_FXD_MAINTENANCE
            }
            // count number of fxd maintenance type created or updated by user
            count = countFxdMaintenanceType(appUserId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_FXD_MAINTENANCE_TYPE
            }
        }

        if (PluginConnector.isPluginInstalled(ExchangeHousePluginConnector.PLUGIN_NAME)) {
            //count number of task created, approved and confirmed exception by user
            count = countExhTask(appUserId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_EXH_TASK
            }
            //count number of agent created, updated by user
            count = countExhAgent(appUserId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_EXH_AGENT
            }
            //count number of agent currency posting created, updated by user
            count = countExhAgentCurrencyPosting(appUserId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_EXH_AGENT_CURRENCY_POSTING
            }
            //count number of beneficiary created, updated, approved and confirmed exception by user
            count = countExhBeneficiary(appUserId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_EXH_BENEFICIARY
            }
            //count number of currency conversion created, updated by user
            count = countExhCurrencyConversion(appUserId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_EXH_CURRENCY_CONVERSION
            }
            //count number of customer created, confirmed exception by user
            count = countExhCustomer(appUserId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_EXH_CUSTOMER
            }
            //count number of customer beneficiary mapping created, updated by user
            count = countExhCustomerBeneficiaryMapping(appUserId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_EXH_CUSTOMER_BENEFICIARY_MAPPING
            }
            //count number of customer trace created, confirmed exception by user
            count = countExhCustomerTrace(appUserId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_EXH_CUSTOMER_TRACE
            }
            //count number of photo id type created, updated by user
            count = countExhPhotoIdType(appUserId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_EXH_PHOTO_ID_TYPE
            }
            //count number of postal code created, updated by user
            count = countExhPostalCode(appUserId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_EXH_POSTAL_CODE
            }
            //count number of regular fee created, updated by user
            count = countExhRegularFee(appUserId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_EXH_REGULAR_FEE
            }
            //count number of remittance purpose created, updated by user
            count = countExhRemittancePurpose(appUserId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_EXH_REMITTANCE_PURPOSE
            }
            //count number of task trace created, approved and confirmed exception by user
            count = countExhTaskTrace(appUserId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_EXH_TASK_TRACE
            }
        }

        if (PluginConnector.isPluginInstalled(DocumentPluginConnector.PLUGIN_NAME)) {
            // count number of DocCategory created and updated by user
            count = countDocCategory(appUserId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_DOC_CATEGORY
            }
            // count number of DocCategory mapped user
            count = countDocCategoryUserMapping(appUserId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_DOC_CATEGORY
            }
        }

        if (PluginConnector.isPluginInstalled(ELearningPluginConnector.PLUGIN_NAME)) {
            // count number of course created and updated by user
            count = countElCourse(appUserId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_EL_COURSE
            }
            // count number of ElCourse mapped user
            count = countDocCategoryUserMapping(appUserId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_DOC_CATEGORY
            }
        }
        return null
    }

    private static final String COUNT_USER_ROLE = """
            SELECT COUNT(user_id) AS count
            FROM user_role
            WHERE user_id = :appUserId
    """

    /**
     * Count user-role mapping associated with user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countUserRole(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_USER_ROLE, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_USER_ENTITY = """
            SELECT COUNT(aue.app_user_id) AS count, se.key
            FROM app_user_entity aue
            LEFT JOIN system_entity se ON se.id = aue.entity_type_id
            WHERE aue.app_user_id = :appUserId
            GROUP BY se.key
    """

    /**
     * Count user-entity mapping associated with user
     * @param appUserId -id of user
     * @return -a string containing null value or message according to user association
     */
    private String countUserEntity(long appUserId) {
        String msg = null
        Map queryParams = [
                appUserId: appUserId,
        ]
        List results = executeSelectSql(COUNT_USER_ENTITY, queryParams)
        if (results.size() <= 0) {
            return msg
        }
        int count = results[0].count
        String entityType = results[0].key
        msg = count + SINGLE_SPACE + entityType + HAS_ASSOCIATION_USER_ENTITY
        return msg
    }

    private static final String COUNT_BUDGET = """
            SELECT COUNT(id) AS count
            FROM budg_budget
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of budget created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countBudget(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_BUDGET, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_BUDGET_DETAILS = """
            SELECT COUNT(id) AS count
            FROM budg_budget_details
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of budget details created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countBudgetDetails(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_BUDGET_DETAILS, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_PURCHASE_REQUEST = """
            SELECT COUNT(id) AS count
            FROM proc_purchase_request
            WHERE created_by = :appUserId OR updated_by = :appUserId OR
                  approved_by_director_id = :appUserId OR
                  approved_by_project_director_id = :appUserId
    """

    /**
     * Count number of purchase request created, updated or approved by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countPurchaseRequest(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_PURCHASE_REQUEST, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_PURCHASE_ORDER = """
            SELECT COUNT(id) AS count
            FROM proc_purchase_order
            WHERE created_by = :appUserId OR
                  updated_by = :appUserId OR
                  approved_by_director_id = :appUserId OR
                  approved_by_project_director_id = :appUserId
    """

    /**
     * Count number of purchase order created, updated or approved by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countPurchaseOrder(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_PURCHASE_ORDER, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_INVENTORY_TRANSACTION = """
            SELECT COUNT(id) AS count
            FROM inv_inventory_transaction
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of inventory transaction created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countInventoryTransaction(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_INVENTORY_TRANSACTION, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_COMPANY = """
            SELECT COUNT(id) AS count
            FROM company
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of company created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countCompany(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_COMPANY, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_EMPLOYEE = """
            SELECT COUNT(id) AS count
            FROM app_employee
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of employee created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countEmployee(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_EMPLOYEE, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_PROJECT = """
            SELECT COUNT(id) AS count
            FROM project
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of project created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countProject(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_PROJECT, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_APP_GROUP = """
            SELECT COUNT(id) AS count
            FROM app_group
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of group created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countAppGroup(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_APP_GROUP, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_ACC_CHART_OF_ACCOUNT = """
            SELECT COUNT(id) AS count
            FROM acc_chart_of_account
            WHERE created_by = :appUserId
    """

    /**
     * Count number of chart of account created by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countAccChartOfAccount(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_ACC_CHART_OF_ACCOUNT, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_ACC_DIVISION = """
            SELECT COUNT(id) AS count
            FROM acc_division
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of division created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countAccDivision(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_ACC_DIVISION, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_ACC_FINANCIAL_YEAR = """
            SELECT COUNT(id) AS count
            FROM acc_financial_year
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of financial year created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countAccFinancialYear(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_ACC_FINANCIAL_YEAR, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_ACC_IOU_PURPOSE = """
            SELECT COUNT(id) AS count
            FROM acc_iou_purpose
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of iou purpose created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countAccIouPurpose(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_ACC_IOU_PURPOSE, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_ACC_IOU_SLIP = """
            SELECT COUNT(id) AS count
            FROM acc_iou_slip
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of iou slip created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countAccIouSlip(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_ACC_IOU_SLIP, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_ACC_SUB_ACCOUNT = """
            SELECT COUNT(id) AS count
            FROM acc_sub_account
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of sub account created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countAccSubAccount(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_ACC_SUB_ACCOUNT, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_ACC_VOUCHER_DETAILS = """
            SELECT COUNT(id) AS count
            FROM acc_voucher_details
            WHERE created_by = :appUserId
    """

    /**
     * Count number of voucher details created by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countAccVoucherDetails(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_ACC_VOUCHER_DETAILS, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_ACC_VOUCHER_TYPE_COA = """
            SELECT COUNT(id) AS count
            FROM acc_voucher_type_coa
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of voucher type coa created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countAccVoucherTypeCoa(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_ACC_VOUCHER_TYPE_COA, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_FXD_CATEGORY_MAINTENANCE_TYPE = """
            SELECT COUNT(id) AS count
            FROM fxd_category_maintenance_type
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of category maintenance type created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countFxdCategoryMaintenanceType(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_FXD_CATEGORY_MAINTENANCE_TYPE, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_FXD_FIXED_ASSET_DETAILS = """
            SELECT COUNT(id) AS count
            FROM fxd_fixed_asset_details
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of fixed asset details created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countFxdFixedAssetDetails(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_FXD_FIXED_ASSET_DETAILS, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_FXD_FIXED_ASSET_TRACE = """
            SELECT COUNT(id) AS count
            FROM fxd_fixed_asset_trace
            WHERE created_by = :appUserId
    """

    /**
     * Count number of fixed asset trace created by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countFxdFixedAssetTrace(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_FXD_FIXED_ASSET_TRACE, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_FXD_MAINTENANCE = """
            SELECT COUNT(id) AS count
            FROM fxd_maintenance
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of fxd maintenance created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countFxdMaintenance(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_FXD_MAINTENANCE, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_FXD_MAINTENANCE_TYPE = """
            SELECT COUNT(id) AS count
            FROM fxd_maintenance_type
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of fxd maintenance type created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countFxdMaintenanceType(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_FXD_MAINTENANCE_TYPE, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_INVENTORY_TRANSACTION_DETAILS = """
            SELECT COUNT(id) AS count
            FROM inv_inventory_transaction_details
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of inventory transaction details created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countInvInventoryTransactionDetails(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_INVENTORY_TRANSACTION_DETAILS, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_PROC_INDENT = """
            SELECT COUNT(id) AS count
            FROM acc_indent
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of indent created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countAccIndent(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_PROC_INDENT, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_PROC_INDENT_DETAILS = """
            SELECT COUNT(id) AS count
            FROM acc_indent_details
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of indent details created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countAccIndentDetails(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_PROC_INDENT_DETAILS, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_PROC_PURCHASE_ORDER_DETAILS = """
            SELECT COUNT(id) AS count
            FROM proc_purchase_order_details
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of purchase order details created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countProcPurchaseOrderDetails(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_PROC_PURCHASE_ORDER_DETAILS, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_PROC_PURCHASE_REQUEST_DETAILS = """
            SELECT COUNT(id) AS count
            FROM proc_purchase_request_details
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of purchase request details created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countProcPurchaseRequestDetails(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_PROC_PURCHASE_REQUEST_DETAILS, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_PROC_TERMS_AND_CONDITION = """
            SELECT COUNT(id) AS count
            FROM proc_terms_and_condition
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of terms and condition created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countProcTermsAndCondition(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_PROC_TERMS_AND_CONDITION, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_QS_MEASUREMENT = """
            SELECT COUNT(id) AS count
            FROM qs_measurement
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of qs measurement created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countQsMeasurement(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_QS_MEASUREMENT, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_EXH_TASK = """
        SELECT count(id) AS count
        FROM exh_task
        WHERE user_id = :appUserId
        OR approved_by = :appUserId OR exception_confirmed_by = :appUserId
    """

    /**
     * Count number of task created or updated or exception confirmed by user
     * @param appUserId
     * @return - an integer containing the value of count
     */
    private int countExhTask(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_EXH_TASK, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_EXH_AGENT = """
        SELECT count(id) AS count
        FROM exh_agent
        WHERE created_by = :appUserId
        OR updated_by = :appUserId
    """

    /**
     * Count number of agent created or updated by user
     * @param appUserId
     * @return - an integer containing the value of count
     */
    private int countExhAgent(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_EXH_AGENT, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_EXH_AGENT_CURRENCY_POSTING = """
        SELECT count(id) AS count
        FROM exh_agent_currency_posting
        WHERE created_by = :appUserId
        OR updated_by = :appUserId
    """

    /**
     * Count number of agent currency created, updated, approved or exception confirmed by user
     * @param appUserId
     * @return - an integer containing the value of count
     */
    private int countExhAgentCurrencyPosting(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_EXH_AGENT_CURRENCY_POSTING, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_EXH_BENEFICIARY = """
        SELECT count(id) AS count
        FROM exh_beneficiary
        WHERE created_by = :appUserId
        OR updated_by = :appUserId
        OR approved_by= :appUserId OR exception_confirmed_by= :appUserId
    """

    /**
     * Count number of beneficiary created or updated by user
     * @param appUserId
     * @return - an integer containing the value of count
     */
    private int countExhBeneficiary(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_EXH_BENEFICIARY, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_EXH_CURRENCY_CONVERSION = """
        SELECT count(id) AS count
        FROM exh_currency_conversion
        WHERE created_by = :appUserId
        OR updated_by = :appUserId
    """

    /**
     * Count number of currency conversion created or updated by user
     * @param appUserId
     * @return - an integer containing the value of count
     */
    private int countExhCurrencyConversion(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_EXH_CURRENCY_CONVERSION, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_EXH_CUSTOMER = """
        SELECT count(id) AS count
        FROM exh_customer
        WHERE user_id = :appUserId
        OR exception_confirmed_by = :appUserId
    """

    /**
     * Count number of customer created or exception confirmed by user
     * @param appUserId
     * @return - an integer containing the value of count
     */
    private int countExhCustomer(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_EXH_CUSTOMER, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_EXH_CUSTOMER_BENEFICIARY_MAPPING = """
        SELECT count(*) AS count
        FROM exh_customer_beneficiary_mapping
        WHERE created_by = :appUserId
        OR updated_by = :appUserId
    """

    /**
     * Count number of customer beneficiary mapping created or updated by user
     * @param appUserId
     * @return - an integer containing the value of count
     */
    private int countExhCustomerBeneficiaryMapping(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_EXH_CUSTOMER_BENEFICIARY_MAPPING, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_EXH_CUSTOMER_TRACE = """
        SELECT count(id) AS count
        FROM exh_customer_trace
        WHERE created_by = :appUserId
        OR exception_confirmed_by = :appUserId
    """

    /**
     * Count number of customer trace created or exception confirmed by user
     * @param appUserId
     * @return - an integer containing the value of count
     */
    private int countExhCustomerTrace(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_EXH_CUSTOMER_TRACE, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_EXH_PHOTO_ID_TYPE = """
        SELECT count(id) AS count
        FROM exh_photo_id_type
        WHERE created_by = :appUserId
        OR updated_by = :appUserId
    """

    /**
     * Count number of photoIdType created or updated  by user
     * @param appUserId
     * @return - an integer containing the value of count
     */
    private int countExhPhotoIdType(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_EXH_PHOTO_ID_TYPE, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_EXH_POSTAL_CODE = """
        SELECT count(id) AS count
        FROM exh_postal_code
        WHERE created_by = :appUserId
        OR updated_by = :appUserId
    """

    /**
     * Count number of postal code created or updated by user
     * @param appUserId
     * @return - an integer containing the value of count
     */
    private int countExhPostalCode(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_EXH_POSTAL_CODE, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_EXH_REGULAR_FEE = """
        SELECT count(id) AS count
        FROM exh_regular_fee
        WHERE created_by = :appUserId
        OR updated_by = :appUserId
    """

    /**
     * Count number of regularFee created or updated by user
     * @param appUserId
     * @return - an integer containing the value of count
     */
    private int countExhRegularFee(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_EXH_REGULAR_FEE, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_EXH_REMITTANCE_PURPOSE = """
        SELECT count(id) AS count
        FROM exh_remittance_purpose
        WHERE created_by = :appUserId
        OR updated_by = :appUserId
    """

    /**
     * Count number of remittance purpose created or updated by user
     * @param appUserId
     * @return - an integer containing the value of count
     */
    private int countExhRemittancePurpose(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_EXH_REMITTANCE_PURPOSE, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_EXH_TASK_TRACE = """
        SELECT count(id) AS count
        FROM exh_task_trace
        WHERE user_id = :appUserId
        OR approved_by = :appUserId OR exception_confirmed_by = :appUserId
    """

    /**
     * Count number of task trace created, approved or exception confirmed by user
     * @param appUserId
     * @return - an integer containing the value of count
     */
    private int countExhTaskTrace(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_EXH_TASK_TRACE, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_DOC_CATEGORY = """
        SELECT count(id) AS count
        FROM doc_category
        WHERE created_by = :appUserId
        OR updated_by = :appUserId
    """

    /**
     * Count number of DocCategory created and updated by user
     * @param appUserId
     * @return - an integer containing the value of count
     */
    private int countDocCategory(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_DOC_CATEGORY, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_DOC_CATEGORY_USER_MAPPING = """
        SELECT count(id) AS count
        FROM doc_category_user_mapping
        WHERE user_id = :appUserId
    """

    /**
     * Count number of DocCategory mapped user
     * @param appUserId
     * @return - an integer containing the value of count
     */
    private int countDocCategoryUserMapping(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_DOC_CATEGORY_USER_MAPPING, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_EL_COURSE = """
        SELECT count(id) AS count
        FROM el_course
        WHERE created_by = :appUserId
        OR updated_by = :appUserId
    """

    /**
     * Count number of ElCourse created and updated by user
     * @param appUserId
     * @return - an integer containing the value of count
     */
    private int countElCourse(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_EL_COURSE, queryParams)
        int count = results[0].count
        return count
    }
}
