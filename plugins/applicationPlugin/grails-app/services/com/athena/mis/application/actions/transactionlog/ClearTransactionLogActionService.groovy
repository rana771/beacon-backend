package com.athena.mis.application.actions.transactionlog

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.service.TransactionLogService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

class ClearTransactionLogActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String CLEAR_LOG_SUCCESS_MSG = "Transaction log clear successfully"
    private static final String LOG_EMPTY_MSG = "Transaction log is empty"
    private static final String ENTITY_ID = 'entityId'
    private static final String ENTITY_TYPE_ID = 'entityTypeId'

    TransactionLogService transactionLogService

    public Map executePreCondition(Map parameters) {
        try {
            // check required parameter
            if (parameters.entityTypeId == null || parameters.entityId == null) {
                return super.setError(parameters, ERROR_FOR_INVALID_INPUT)
            }

            long entityTypeId = Long.parseLong(parameters.entityTypeId.toString())
            long entityId = Long.parseLong(parameters.entityId.toString())

            int resultCount = transactionLogService.countByCompanyIdAndEntityTypeIdAndEntityId(super.getCompanyId(), entityTypeId, entityId)
            if (resultCount == 0) {
                return super.setError(parameters, LOG_EMPTY_MSG)
            }
            parameters.put(ENTITY_TYPE_ID, entityTypeId)
            parameters.put(ENTITY_ID, entityId)
            return parameters
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    @Transactional
    public Map execute(Map previousResult) {
        try {
            long entityTypeId = ((Long) previousResult.get(ENTITY_TYPE_ID)).longValue()
            long entityId = ((Long) previousResult.get(ENTITY_ID)).longValue()
            transactionLogService.deleteAllByCompanyAndEntityTypeAndEntity(super.getCompanyId(), entityTypeId, entityId)
            return previousResult
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    public Map executePostCondition(Map previousResult) {
        return previousResult
    }

    public Map buildSuccessResultForUI(Map executeResult) {
        return setSuccess(executeResult, CLEAR_LOG_SUCCESS_MSG)
    }

    public Map buildFailureResultForUI(Map executeResult) {
        return executeResult
    }
}
