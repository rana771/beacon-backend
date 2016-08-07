package com.athena.mis.application.actions.testData

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.dts.DataAdapterFactoryService
import com.athena.mis.application.dts.DataAdapterService
import com.athena.mis.application.entity.AppDbInstance
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppDbInstanceService
import com.athena.mis.application.service.AppSystemEntityCacheService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

class ListTestDataActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String DB_INSTANCE_NOT_FOUND = "Selected DB Instance not found"
    private static final String DB_INSTANCE = "dbInstance"
    private static final String NOT_SUPPORTED = "List of view for red shift is not supported yet"
    private static final String DISK_USAGE = "disk_usage"
    private static final String NOT_TESTED = "DB instance is not tested"

    AppSystemEntityCacheService appSystemEntityCacheService
    AppDbInstanceService appDbInstanceService
    DataAdapterFactoryService dataAdapterFactoryService

    @Transactional(readOnly = true)
    Map executePreCondition(Map params) {
        try {
            if (!params.dbInstanceId || !params.typeId) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long dbInstanceId = Long.parseLong(params.dbInstanceId.toString())
            AppDbInstance dbInstance = appDbInstanceService.read(dbInstanceId)
            // check existence of DbInstance object
            if (!dbInstance) {
                return super.setError(params, DB_INSTANCE_NOT_FOUND)
            }
            SystemEntity vendor = appSystemEntityCacheService.readByReservedId(dbInstance.reservedVendorId, appSystemEntityCacheService.SYS_ENTITY_TYPE_VENDOR, dbInstance.companyId)
            long objectTypeId = Long.parseLong(params.typeId.toString())
            SystemEntity objectType = appSystemEntityCacheService.read(objectTypeId, appSystemEntityCacheService.SYS_ENTITY_TYPE_DB_OBJECT, dbInstance.companyId)
            if (vendor.reservedId == appSystemEntityCacheService.SYS_ENTITY_VENDOR_AMAZON_REDSHIFT && objectType.reservedId == appSystemEntityCacheService.SYS_ENTITY_DB_OBJECT_VIEW) {
                return super.setError(params, NOT_SUPPORTED)
            }
            // check connection
            if (!dbInstance.isTested) {
                return super.setError(params, NOT_TESTED)
            }
            if (!dbInstance.password) dbInstance.password = EMPTY_SPACE
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
            long objectTypeId = Long.parseLong(result.typeId.toString())
            SystemEntity objectType = appSystemEntityCacheService.read(objectTypeId, appSystemEntityCacheService.SYS_ENTITY_TYPE_DB_OBJECT, dbInstance.companyId)
            Map queryParams = [objectType: objectType.key]
            DataAdapterService dataAdapter = dataAdapterFactoryService.createAdapter(dbInstance)
            dataAdapter.setParams(queryParams)
            Map mapLstTable = dataAdapter.listTable(queryParams)
            Boolean isError = Boolean.parseBoolean(mapLstTable.get(IS_ERROR).toString())
            if (isError.booleanValue()) {
                result.put(LIST, [])
                result.put(COUNT, 0)
                return result
            }
            List<String> lstTable = mapLstTable.lstResult
            SystemEntity vendor = appSystemEntityCacheService.readByReservedId(dbInstance.reservedVendorId, appSystemEntityCacheService.SYS_ENTITY_TYPE_VENDOR, dbInstance.companyId)
            if (vendor.reservedId == appSystemEntityCacheService.SYS_ENTITY_VENDOR_AMAZON_REDSHIFT) {
                if (lstTable.size() > 0) {
                    for (int i = 0; i < lstTable.size(); i++) {
                        long size = (long) lstTable[i].getAt(DISK_USAGE)
                        size = size * 1024 * 1024
                        lstTable[i].putAt(DISK_USAGE, size.toString())
                    }
                }
            }
            if (vendor.reservedId == appSystemEntityCacheService.SYS_ENTITY_VENDOR_MSSQL_2008 || vendor.reservedId == appSystemEntityCacheService.SYS_ENTITY_VENDOR_MSSQL_2012) {
                if (lstTable.size() > 0) {
                    for (int i = 0; i < lstTable.size(); i++) {
                        long size = (long) lstTable[i].getAt(DISK_USAGE)
                        size = size * 1024
                        lstTable[i].putAt(DISK_USAGE, size.toString())
                    }
                }
            }
            int count = lstTable.size()
            result.put(LIST, lstTable)
            result.put(COUNT, count)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    Map executePostCondition(Map previousResult) {
        return previousResult
    }

    Map buildSuccessResultForUI(Map executeResult) {
        return executeResult
    }

    Map buildFailureResultForUI(Map executeResult) {
        return executeResult
    }
}
