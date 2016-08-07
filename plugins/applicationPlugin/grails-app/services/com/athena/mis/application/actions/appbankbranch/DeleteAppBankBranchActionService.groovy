package com.athena.mis.application.actions.appbankbranch

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppBankBranch
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppBankBranchService
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.AppUserEntityService
import com.athena.mis.integration.arms.ArmsPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class DeleteAppBankBranchActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String USER = "User"
    private static final String BANK_BRANCH = "bankBranch"
    private static final String BANK_DELETE_SUCCESS_MSG = "Bank Branch has been successfully deleted"
    private static final String DOMAIN_PURCHASE_INSTRUMENT_MAPPING = "purchaseInstrumentMapping"
    private static final String GLOBAL_BRANCH_CAN_NOT_BE_DELETED = "Global branch can not be deleted"

    AppBankBranchService appBankBranchService
    AppUserEntityService appUserEntityService
    AppSystemEntityCacheService appSystemEntityCacheService

    @Autowired(required = false)
    ExchangeHousePluginConnector exhImplService
    @Autowired(required = false)
    ArmsPluginConnector armsImplService

    /**
     * 1. get bankBranch object by id from DB
     * 2. check existence of bankBranch
     * 3. check association with relevant domains
     * @param parameters -parameters from UI
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map parameters) {
        try {
            long bankBranchId = Long.parseLong(parameters.id)
            AppBankBranch bankBranch = (AppBankBranch) appBankBranchService.read(bankBranchId)
            if (!bankBranch) {
                return super.setError(parameters, ENTITY_NOT_FOUND_ERROR_MESSAGE)
            }
            String errMsg = isAssociated(bankBranch)
            if (errMsg) {
                return super.setError(parameters, errMsg)
            }
            //Global bankBranch can not be deleted
            errMsg = checkForGlobalBranch(bankBranch.id)
            if (errMsg) {
                return super.setError(parameters, errMsg)
            }
            parameters.put(BANK_BRANCH, bankBranch)
            return parameters
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Delete BankBranch object from DB
     * This function is in transactional boundary and will roll back in case of any exception
     * @param params -serialized parameters from UI
     * @return -a map containing isError(true/false) depending on method success
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppBankBranch branch = (AppBankBranch) result.get(BANK_BRANCH)
            appBankBranchService.delete(branch)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * There is no post condition, so return the same map as received
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * received map must contain isError- false
     * @return -a map containing success message for UI
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, BANK_DELETE_SUCCESS_MSG)
    }

    /**
     * received map must contain isError-false with corresponding error msg
     * return the same map as received
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * 1. check if any user is mapped with BankBranch
     * 2. check if BankBranch has exhTask (if application has Exchange house plugin)
     * 3. check if BankBranch has rmsTask (if application has Arms plugin)
     * 4. check if BankBranch has rmsPurchase instrument mapping (if application has Arms plugin)
     * @param bankBranch -object of bankBranch
     * @return -a string containing error message or null value depending on association check
     */
    private String isAssociated(AppBankBranch bankBranch) {
        Long bankBranchId = bankBranch.id
        String bankBranchName = bankBranch.name
        //hasArmsUser
        int count = countMappingBranchUser(bankBranchId)
        if (count > 0) {
            String strMessage = getMessageOfAssociation(bankBranchName, count, USER)
            return strMessage
        }
        //hasTask
        if (exhImplService) {
            count = countTask(bankBranchId)
            if (count > 0) {
                String strMessage = getMessageOfAssociation(bankBranchName, count, DOMAIN_TASK)
                return strMessage
            }
        }
        if (armsImplService) {
            count = countRmsTask(bankBranchId)
            if (count > 0) {
                String strMessage = getMessageOfAssociation(bankBranchName, count, DOMAIN_TASK)
                return strMessage
            }
            count = countRmsPurchaseInstrumentMapping(bankBranchId)
            if (count > 0) {
                String strMessage = getMessageOfAssociation(bankBranchName, count, DOMAIN_PURCHASE_INSTRUMENT_MAPPING)
                return strMessage
            }
            count = appUserEntityService.countByEntityIdAndEntityTypeId(bankBranchId, appSystemEntityCacheService.SYS_ENTITY_USER_ENTITY_BANK_BRANCH)
            if (count > 0) {
                String strMessage = getMessageOfAssociation(bankBranchName, count, USER)
                return strMessage
            }
        }
        return null
    }

    /**
     * Check for global bankBranch
     * @param bankBranchId - branch id
     * @return - errMsg or null
     */
    private String checkForGlobalBranch(long bankBranchId) {
        //Global bank branch can not be deleted
        int count = appBankBranchService.countByIdAndIsGlobal(bankBranchId, Boolean.TRUE)
        if (count > 0) {
            return GLOBAL_BRANCH_CAN_NOT_BE_DELETED
        }
        return null
    }

    //count number of row in branch table by bank id
    private int countMappingBranchUser(Long bankBranchId) {
        SystemEntity appUserSysEntityObject = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_USER_ENTITY_BANK_BRANCH, appSystemEntityCacheService.SYS_ENTITY_TYPE_USER_ENTITY, super.getCompanyId())
        String query = """
            SELECT COUNT(id) as count
            FROM app_user_entity
            WHERE entity_id =:bankBranchId
            AND entity_type_id =:entityTypeId
        """
        Map queryParams = [
                entityTypeId: appUserSysEntityObject.id,
                bankBranchId: bankBranchId
        ]

        List bankBranchCount = executeSelectSql(query, queryParams)
        int count = bankBranchCount[0].count;
        return count;
    }

    //count number of rows in task table by bank branch id
    private int countTask(long bankBranchId) {
        String queryStrTask = """
            SELECT COUNT(id) as count
            FROM exh_task
            WHERE outlet_branch_id = :bankBranchId
        """
        List taskCount = executeSelectSql(queryStrTask, [bankBranchId: bankBranchId])
        int count = taskCount[0].count
        return count

    }
    private static final String QUERY_COUNT_RMS_BANK_BRANCH = """
        SELECT COUNT(id) as count
        FROM rms_task
        WHERE mapping_branch_id = :bankBranchId
    """

    private int countRmsTask(long bankBranchId) {
        List taskCount = executeSelectSql(QUERY_COUNT_RMS_BANK_BRANCH, [bankBranchId: bankBranchId])
        int count = taskCount[0].count
        return count

    }
    private static final String QUERY_COUNT_RMS_PURCHASE_INSTRUMENT_MAPPING = """
        SELECT COUNT(id) as count
        FROM rms_purchase_instrument_mapping
        WHERE bank_branch_id = :bankBranchId
    """

    private int countRmsPurchaseInstrumentMapping(long bankBranchId) {
        List taskCount = executeSelectSql(QUERY_COUNT_RMS_PURCHASE_INSTRUMENT_MAPPING, [bankBranchId: bankBranchId])
        int count = taskCount[0].count
        return count
    }
}
