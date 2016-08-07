package com.athena.mis.application.actions.dbinstance

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.dts.DataAdapterFactoryService
import com.athena.mis.application.dts.DataAdapterService
import com.athena.mis.application.entity.AppDbInstance
import com.athena.mis.application.model.ListAppDbInstanceActionServiceModel
import com.athena.mis.application.service.AppDbInstanceService
import com.athena.mis.application.service.ListAppDbInstanceActionServiceModelService
import grails.transaction.Transactional
import org.apache.log4j.Logger

class DisconnectAppDbInstanceActionService extends BaseService implements ActionServiceIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String DB_INSTANCE = 'dbInstance'
    private static final String DB_INSTANCE_NOT_FOUND = "Selected DB Instance not found"
    private static final String SUCCESS_MESSAGE = "Database has been disconnected successfully"

    AppDbInstanceService appDbInstanceService
    ListAppDbInstanceActionServiceModelService listAppDbInstanceActionServiceModelService
    DataAdapterFactoryService dataAdapterFactoryService

    @Transactional(readOnly = true)
    Map executePreCondition(Map parameters) {
        try {
            if (!parameters.id) {
                return super.setError(parameters, ERROR_FOR_INVALID_INPUT)
            }
            long dbInstanceId = Long.parseLong(parameters.id.toString())
            AppDbInstance dbInstance = appDbInstanceService.read(dbInstanceId)
            // check existence of DbInstance object
            if (!dbInstance) {
                return super.setError(parameters, DB_INSTANCE_NOT_FOUND)
            }
            parameters.put(DB_INSTANCE, dbInstance)
            return parameters
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    @Transactional
    Map execute(Map previousResult) {
        try {
            AppDbInstance dbInstance = (AppDbInstance) previousResult.get(DB_INSTANCE)
            if (!dbInstance.password) dbInstance.password = EMPTY_SPACE
            DataAdapterService dataAdapter = dataAdapterFactoryService.createAdapter(dbInstance)
            Map result = dataAdapter.disconnect()
            Boolean isError = (Boolean) result.isError
            if (isError.booleanValue()) {
                dbInstance.isTested = false
                appDbInstanceService.update(dbInstance)
                return super.setError(result, result.get(MESSAGE).toString())
            }
            return previousResult
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    Map executePostCondition(Map previousResult) {
        return previousResult
    }

    @Transactional(readOnly = true)
    Map buildSuccessResultForUI(Map executeResult) {
        try {
            AppDbInstance appDbInstance = (AppDbInstance) executeResult.get(DB_INSTANCE)
            ListAppDbInstanceActionServiceModel model = listAppDbInstanceActionServiceModelService.read(appDbInstance.id)
            executeResult.put(DB_INSTANCE, model)
            return super.setSuccess(executeResult, SUCCESS_MESSAGE)
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    Map buildFailureResultForUI(Map executeResult) {
        return executeResult
    }
}
