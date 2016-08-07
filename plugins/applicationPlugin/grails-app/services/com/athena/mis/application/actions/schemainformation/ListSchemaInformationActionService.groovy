package com.athena.mis.application.actions.schemainformation

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.dts.DataAdapterFactoryService
import com.athena.mis.application.dts.DataAdapterService
import com.athena.mis.application.entity.AppDbInstance
import com.athena.mis.application.service.AppDbInstanceService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

class ListSchemaInformationActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String DB_INSTANCE_NOT_FOUND = "Selected DB Instance not found"
    private static final String DB_INSTANCE = "dbInstance"

    AppDbInstanceService appDbInstanceService
    DataAdapterFactoryService dataAdapterFactoryService

    @Transactional(readOnly = true)
    Map executePreCondition(Map params) {
        try {
            if (!params.dbInstanceId || !params.tableName) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long dbInstanceId = Long.parseLong(params.dbInstanceId.toString())
            AppDbInstance dbInstance = appDbInstanceService.read(dbInstanceId)
            // check existence of DbInstance object
            if (!dbInstance || !dbInstance.isTested) {
                return super.setError(params, DB_INSTANCE_NOT_FOUND)
            }
            params.put(DB_INSTANCE, dbInstance)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    @Transactional(readOnly = true)
    Map execute(Map result) {
        try {
            AppDbInstance dbInstance = (AppDbInstance) result.get(DB_INSTANCE)
            if (!dbInstance.password) dbInstance.password = EMPTY_SPACE
            String tableName = result.tableName.toString()
            DataAdapterService dataAdapter = dataAdapterFactoryService.createAdapter(dbInstance)
            dataAdapter.setParams(null)
            Map mapLstTable = dataAdapter.selectColumnDetails(tableName)
            Boolean isError = Boolean.parseBoolean(mapLstTable.get(IS_ERROR).toString())
            if (isError.booleanValue()) {
                result.put(LIST, [])
                result.put(COUNT, 0)
                return result
            }
            List<String> lstColumn = mapLstTable.lstResult
            int count = lstColumn.size()
            result.put(LIST, lstColumn)
            result.put(COUNT, count)
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
        return result
    }

    Map buildFailureResultForUI(Map result) {
        return result
    }
}
