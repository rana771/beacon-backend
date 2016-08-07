package com.athena.mis.application.actions.dbinstance

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.dts.DataAdapterFactoryService
import com.athena.mis.application.dts.DataAdapterService
import com.athena.mis.application.entity.AppDbInstance
import com.athena.mis.application.service.AppDbInstanceService
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.integration.datapipeline.DataPipeLinePluginConnector
import com.athena.mis.integration.document.DocumentPluginConnector
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 * update AppDbInstance for Dpl dashboard under settings
 * for details please go through use case named 'UpdateAppDbInstanceForDplDashboardActionService'
 */
class UpdateAppDbInstanceForDplDashboardActionService extends BaseService implements ActionServiceIntf {
    private final Logger log = Logger.getLogger(getClass())

    private static final String DB_NAME_MUST_BE_UNIQUE = 'DB Instance database name must be unique within Type'
    private static final String DB_INSTANCE = "dbInstance"
    private static final String OBJ_NOT_FOUND = "DB Instance not found."
    private static final String VERSION_MISMATCHED = "Object version mismatched, please refresh"
    private static final String SUCCESS_MESSAGE = "DB Instance updated successfully"
    private static
    final String ASSOCIATION_MSG_WITH_DATA_EXPORT = " data export is associated with this DB instance"
    private static
    final String ASSOCIATION_MSG_WITH_DATA_INDEX = " data index is associated with this DB instance"
    private static
    final String SAME_DB_CONNECTION_EXISTS = 'DB Instance already exists with same connection configuration'
    private static final String PUBLIC_SCHEMA = "public"
    private static final String DBO_SCHEMA = "dbo"

    AppSystemEntityCacheService appSystemEntityCacheService
    AppDbInstanceService appDbInstanceService
    DataAdapterFactoryService dataAdapterFactoryService

    @Autowired(required = false)
    DocumentPluginConnector documentImplService
    @Autowired(required = false)
    DataPipeLinePluginConnector dataPipeLineImplService

    /**
     * check necessary parameters
     * read AppDbInstance object
     * check validation
     * close data adapter connection if exists
     * build AppDbInstance object for update
     * @param parameters - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    Map executePreCondition(Map parameters) {
        try {
            //Check parameters
            if ((!parameters.id) || (!parameters.name) || (!parameters.vendorId) || (!parameters.url) || (!parameters.port) || (!parameters.userName) || (!parameters.dbName)) {
                return super.setError(parameters, ERROR_FOR_INVALID_INPUT)
            }
            long dbInstanceId = Long.parseLong(parameters.id.toString())
            AppDbInstance oldDbInstance = (AppDbInstance) appDbInstanceService.read(dbInstanceId)
            long version = Long.parseLong(parameters.version.toString())
            // check validation
            String errMsg = checkValidation(parameters, oldDbInstance, version)
            if (errMsg) {
                return super.setError(parameters, errMsg)
            }
            // close data adapter connection if exists
            closeAdapterConnection(oldDbInstance)
            AppDbInstance newDbInstance = buildDbInstanceObj(parameters, oldDbInstance)
            parameters.put(DB_INSTANCE, newDbInstance)
            return parameters
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * update AppDbInstance object
     * @param previousResult
     * @return Map
     */
    @Transactional
    Map execute(Map previousResult) {
        try {
            AppDbInstance appDbInstance = (AppDbInstance) previousResult.get(DB_INSTANCE)
            appDbInstanceService.update(appDbInstance)
            return previousResult
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    Map executePostCondition(Map previousResult) {
        return previousResult
    }

    Map buildSuccessResultForUI(Map executeResult) {
        return super.setSuccess(executeResult, SUCCESS_MESSAGE)
    }

    Map buildFailureResultForUI(Map executeResult) {
        return executeResult
    }

    /**
     * build AppDbInstance object for update
     * @param params
     * @param oldAppDbInstance
     * @return AppDbInstance object
     */
    private AppDbInstance buildDbInstanceObj(Map params, AppDbInstance oldAppDbInstance) {
        AppDbInstance newDbInstance = new AppDbInstance(params)
        if (!params.password) {
            oldAppDbInstance.password = EMPTY_SPACE
        }
        oldAppDbInstance.name = newDbInstance.name
        oldAppDbInstance.port = newDbInstance.port
        oldAppDbInstance.dbName = newDbInstance.dbName
        oldAppDbInstance.userName = newDbInstance.userName
        oldAppDbInstance.schemaName = newDbInstance.schemaName
        oldAppDbInstance.url = newDbInstance.url
        oldAppDbInstance.isTested = Boolean.FALSE
        if (!oldAppDbInstance.schemaName) {
            oldAppDbInstance.schemaName = getSchemaName(oldAppDbInstance.reservedVendorId)
        }
        oldAppDbInstance.connectionString = buildConnectionString(oldAppDbInstance)
        return oldAppDbInstance
    }

    /**
     * 1. check oldDbInstance object existence
     * 2. check oldDbInstance object version
     * 3. check for duplicate name
     * 4. check for duplicate connection
     * 5. check association
     *
     * @param params - serialize parameters form UI
     * @param oldDbInstance - an object of AppDbInstance
     * @param dbInstanceId - AppDbInstance object id
     * @param version - last version from UI
     * @return - error message or null value depending on validation check
     */
    private String checkValidation(Map params, AppDbInstance oldDbInstance, long version) {
        String errMsg = checkDbExistence(oldDbInstance)
        if (errMsg) return errMsg
        errMsg = checkVersion(oldDbInstance, version)
        if (errMsg) return errMsg
        errMsg = checkDuplicate(oldDbInstance, params)
        if (errMsg) return errMsg
        errMsg = checkDuplicateConnection(oldDbInstance, params)
        if (errMsg) return errMsg
        errMsg = checkAssociation(oldDbInstance)
        if (errMsg) return errMsg
        return null
    }

    /**
     * check oldDbInstance object existence
     * @param oldDbInstance
     * @return - error message or null value depending on validation check
     */
    private String checkDbExistence(AppDbInstance oldDbInstance) {
        if (!oldDbInstance) {
            return OBJ_NOT_FOUND
        }
        return null
    }

    /**
     * check object version
     * @param oldDbInstance
     * @param version
     * @return - error message or null value depending on validation check
     */
    private String checkVersion(AppDbInstance oldDbInstance, long version) {
        if (oldDbInstance.version != version) {
            return VERSION_MISMATCHED
        }
        return null
    }

    /**
     * check duplicate database name
     * @param oldDbInstance
     * @param params
     * @return - error message or null value depending on validation check
     */
    private String checkDuplicate(AppDbInstance oldDbInstance, Map params) {
        int duplicateCount = appDbInstanceService.countByNameIlikeAndTypeIdAndCompanyIdAndIdNotEqual(params.name.toString(), oldDbInstance.typeId, companyId, oldDbInstance.id)
        if (duplicateCount > 0) {
            return DB_NAME_MUST_BE_UNIQUE
        }
        return null
    }

    /**
     * check duplicate database connection
     * @param oldDbInstance - object of AppDbInstance
     * @param params - serialized parameters from UI
     * @return - error message or null value depending on validation check
     */
    private String checkDuplicateConnection(AppDbInstance oldDbInstance, Map params) {
        int duplicateCount = appDbInstanceService.countByUrlAndPortAndDbNameAndUserNameAndPasswordAndCompanyIdAndIdNotEqual(params.url.toString(), params.port.toString(), params.dbName.toString(), params.userName.toString(), params.password.toString(), oldDbInstance.companyId, oldDbInstance.id)
        if (duplicateCount > 0) {
            return SAME_DB_CONNECTION_EXISTS
        }
        return null
    }

    /**
     * 1. check data export association if dataPipeLine is enabled
     * 2. check data index association if document plugin is enabled
     * @param oldDbInstance
     * @return - error message or null value depending on validation check
     */
    private String checkAssociation(AppDbInstance oldDbInstance) {
        String errMsg = checkDataExportAssociation(oldDbInstance)
        if (errMsg) return errMsg
        errMsg = checkDataIndexAssociation(oldDbInstance)
        if (errMsg) return errMsg
        return null
    }

    /**
     * check data export association if dataPipeLine is enabled
     * @param oldDbInstance
     * @return - error message or null value depending on validation check
     */
    private String checkDataExportAssociation(AppDbInstance oldDbInstance) {
        if (dataPipeLineImplService) {
            int countDataExport = dataPipeLineImplService.countDplDataExportByDbInstanceId(oldDbInstance.id)
            if (countDataExport > 0) {
                return countDataExport.toString() + ASSOCIATION_MSG_WITH_DATA_EXPORT
            }
        }
        return null
    }

    /**
     * check data index association if document plugin is enabled
     * @param oldDbInstance
     * @return - error message or null value depending on validation check
     */
    private String checkDataIndexAssociation(AppDbInstance oldDbInstance) {
        if (documentImplService) {
            int countDataIndex = documentImplService.countDataIndexByDbInstanceId(oldDbInstance.id)
            if (countDataIndex > 0) {
                return countDataIndex.toString() + ASSOCIATION_MSG_WITH_DATA_INDEX
            }
        }
        return null
    }

    /**
     * get default schema name by vendor if not given
     * @param reservedVendorId - reserved vendor id
     * @return - schema name
     */
    private String getSchemaName(long reservedVendorId) {
        String schemaName = null
        switch (reservedVendorId) {
            case appSystemEntityCacheService.SYS_ENTITY_VENDOR_POSTGRES:
                schemaName = PUBLIC_SCHEMA
                break
            case appSystemEntityCacheService.SYS_ENTITY_VENDOR_AMAZON_REDSHIFT:
                schemaName = PUBLIC_SCHEMA
                break
            case appSystemEntityCacheService.SYS_ENTITY_VENDOR_GREEN_PLUM:
                schemaName = PUBLIC_SCHEMA
                break
            case appSystemEntityCacheService.SYS_ENTITY_VENDOR_MSSQL_2008:
                schemaName = DBO_SCHEMA
                break
            case appSystemEntityCacheService.SYS_ENTITY_VENDOR_MSSQL_2012:
                schemaName = DBO_SCHEMA
                break
            default:
                break
        }
        return schemaName
    }

    /**
     * get connection string
     * @param dbInstance - object of AppDbInstance
     * @return - connection string
     */
    private String buildConnectionString(AppDbInstance dbInstance) {
        DataAdapterService dataAdapterService = dataAdapterFactoryService.createAdapter(dbInstance)
        return dataAdapterService.getConnectionString()
    }

    /**
     * close data adapter connection if exists
     * @param oldDbInstance - object of AppDbInstance
     */
    private void closeAdapterConnection(AppDbInstance oldDbInstance) {
        DataAdapterService dataAdapter = dataAdapterFactoryService.createAdapter(oldDbInstance)
        dataAdapter.closeConnection()
        dataAdapterFactoryService.deleteAdapter(oldDbInstance)
    }
}
