package com.athena.mis.application.actions.appbank

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppBank
import com.athena.mis.application.service.AppBankService
import com.athena.mis.integration.arms.ArmsPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class DeleteAppBankActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static String BANK = "bank"
    private static String BANK_DELETE_SUCCESS_MSG = "Bank has been successfully deleted!"
    private static String SYS_BANK_ERROR = "System bank cannot be deleted"

    AppBankService appBankService

    @Autowired(required = false)
    ExchangeHousePluginConnector exhImplService
    @Autowired(required = false)
    ArmsPluginConnector armsImplService

    /**
     * 1. check required parameters
     * 2. pull bank object from service
     * 3. check for bank existence
     * 4. association check for bank with different domains
     * @param parameters - serialize parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map parameters) {
        try {
            // check required parameters
            if (!parameters.id) {
                return super.setError(parameters, ERROR_FOR_INVALID_INPUT)
            }
            long bankId = Long.parseLong(parameters.id.toString())
            AppBank bank = (AppBank) appBankService.read(bankId)
            // check whether selected bank object exists or not
            if (!bank) {
                return super.setError(parameters, ENTITY_NOT_FOUND_ERROR_MESSAGE)
            }
            if(bank.isSystemBank) {
                return super.setError(parameters, SYS_BANK_ERROR)
            }
            String strAssociationMsg = checkAssociation(bank)    // check association of bank with relevant domains
            if (strAssociationMsg) {
                return super.setError(parameters, strAssociationMsg)
            }
            parameters.put(BANK, bank)
            return parameters
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Delete Bank object from DB
     * This function is in transactional boundary and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            // get the bank object from map
            AppBank bank = (AppBank) result.get(BANK)
            // delete bank object from DB
            appBankService.delete(bank)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Do nothing for post operation
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * Remove object from grid
     * Show success message
     * @param result -received map must contain isError-false
     * @return -a map containing success message for UI
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, BANK_DELETE_SUCCESS_MSG)
    }

    /**
     * @param result -received map must contain isError- true with corresponding error message
     * @return -a map same as received
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Check association of Bank with relevant domains
     * @param bank -bank object
     * @return -a map containing hasAssociation(true/false) depending on association and relevant message
     */
    private String checkAssociation(AppBank bank) {
        long bankId = bank.id
        String bankName = bank.name
        int count = 0
        if (exhImplService) {
            count = countTask(bankId)
            if (count.intValue() > 0) {
                return getMessageOfAssociation(bankName, count, DOMAIN_TASK)
            }
        }
        if (armsImplService) {
            count = countRmsTask(bankId)
            if (count.intValue() > 0) {
                return getMessageOfAssociation(bankName, count, DOMAIN_TASK)
            }
            count = countRmsPurchaseInstrumentMapping(bankId)
            if (count.intValue() > 0) {
                return getMessageOfAssociation(bankName, count, DOMAIN_TASK)
            }
        }

        count = countBranch(bankId)                                      // hasBranch
        if (count.intValue() > 0) {
            return getMessageOfAssociation(bankName, count, DOMAIN_BANK_BRANCH)
        }
        return null
    }

    //count number of row in branch table by bank id
    private static final String QUERY_COUNT_BRANCH = """
            SELECT COUNT(id) as count
            FROM app_bank_branch
            WHERE bank_id = :bankId
    """

    private int countBranch(long bankId) {
        List bankCount = executeSelectSql(QUERY_COUNT_BRANCH, [bankId: bankId])
        int count = bankCount[0].count
        return count
    }

    //count number of rows in task table by bank id
    private static final String QUERY_COUNT_TASK = """
            SELECT COUNT(id) as count
            FROM exh_task
            WHERE outlet_bank_id = :bankId
    """

    private int countTask(long bankId) {
        List taskCount = executeSelectSql(QUERY_COUNT_TASK, [bankId: bankId])
        int count = taskCount[0].count
        return count
    }

    //count number of rows in rms_task table by bank id
    private static final String QUERY_COUNT_RMS_TASK = """
            SELECT COUNT(id) as count
            FROM rms_task
            WHERE mapping_bank_id = :bankId
    """

    private int countRmsTask(long bankId) {
        List taskCount = executeSelectSql(QUERY_COUNT_RMS_TASK, [bankId: bankId])
        int count = taskCount[0].count
        return count
    }

    private static final String QUERY_COUNT_RMS_PURCHASE_INSTRUMENT_MAPPING = """
            SELECT COUNT(id) as count
            FROM rms_purchase_instrument_mapping
            WHERE bank_id = :bankId
    """

    private int countRmsPurchaseInstrumentMapping(long bankId) {
        List taskCount = executeSelectSql(QUERY_COUNT_RMS_PURCHASE_INSTRUMENT_MAPPING, [bankId: bankId])
        int count = taskCount[0].count
        return count
    }
}
