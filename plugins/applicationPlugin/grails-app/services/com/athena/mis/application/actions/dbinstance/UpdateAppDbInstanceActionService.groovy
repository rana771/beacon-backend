package com.athena.mis.application.actions.dbinstance

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.dts.DataAdapterFactoryService
import com.athena.mis.application.dts.DataAdapterService
import com.athena.mis.application.entity.AppDbInstance
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.entity.Vendor
import com.athena.mis.application.model.ListAppDbInstanceActionServiceModel
import com.athena.mis.application.service.AppDbInstanceService
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.ListAppDbInstanceActionServiceModelService
import com.athena.mis.application.service.VendorService
import com.athena.mis.integration.datapipeline.DataPipeLinePluginConnector
import com.athena.mis.integration.document.DocumentPluginConnector
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Update new DBInstance object and show in grid
 *  For details go through Use-Case doc named 'UpdateAppDbInstanceActionService'
 */
class UpdateAppDbInstanceActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String APP_DB_INSTANCE = 'appDbInstance'
    private static final String UPDATE_SUCCESS_MESSAGE = 'DB Instance has been successfully updated.'
    private static final String NAME_MUST_BE_UNIQUE = 'DB Instance name must be unique.'
    private static final String OBJ_NOT_FOUND = "DB Instance not found."
    private static
    final String ASSOCIATION_MSG_WITH_DATA_EXPORT = " data export is associated with this DB instance"
    private static
    final String ASSOCIATION_MSG_WITH_DATA_INDEX = " data index is associated with this DB instance"
    private static final String VERSION_MISMATCHED = "Object version mismatched, please refresh"
    private static
    final String SAME_DB_CONNECTION_EXISTS = 'DB Instance already exists with same connection configuration'
    private static final String PUBLIC_SCHEMA = "public"
    private static final String DBO_SCHEMA = "dbo"

    AppSystemEntityCacheService appSystemEntityCacheService
    AppDbInstanceService appDbInstanceService
    VendorService vendorService
    ListAppDbInstanceActionServiceModelService listAppDbInstanceActionServiceModelService
    DataAdapterFactoryService dataAdapterFactoryService

    @Autowired(required = false)
    DocumentPluginConnector documentImplService
    @Autowired(required = false)
    DataPipeLinePluginConnector dataPipeLineImplService

    /**
     * 1. Check required parameters
     * 2. Pull AppDbInstance object
     * 3. Check Validation
     * 4. close data adapter connection if exists
     * 5. build AppDbInstance object for update
     *
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            //Check parameters
            if ((!params.name) || (!params.version) || (!params.name) || (!params.vendorId) || (!params.url) || (!params.port) || (!params.userName) || (!params.dbName)) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long dbInstanceId = Long.parseLong(params.id.toString())
            AppDbInstance oldDbInstance = (AppDbInstance) appDbInstanceService.read(dbInstanceId)
            long version = Long.parseLong(params.version.toString())

            long vendorId = Long.parseLong(params.vendorId.toString())
            Vendor vendor = vendorService.read(vendorId)
            String errorMsg = checkValidation(params, oldDbInstance, dbInstanceId, version, vendor)
            // check validation
            if (errorMsg) {
                return super.setError(params, errorMsg)
            }
            // close data adapter connection if exists
            closeAdapterConnection(oldDbInstance)
            AppDbInstance newDbInstance = getAppDbInstance(params, oldDbInstance, vendor)

            params.put(APP_DB_INSTANCE, newDbInstance)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. get the AppDbInstance object from pre-condition
     * 2. Update existing AppDbInstance in DB
     * This method is in transactional block and will roll back in case of any exception
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppDbInstance appDbInstance = (AppDbInstance) result.get(APP_DB_INSTANCE)
            appDbInstanceService.update(appDbInstance)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * There is no postCondition, so return the same map as received
     *
     * @param result - resulting map from execute
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePostCondition(Map result) {
        return result;
    }

    /**
     * 1. put success message
     *
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map buildSuccessResultForUI(Map result) {
        try {
            AppDbInstance appDbInstance = (AppDbInstance) result.get(APP_DB_INSTANCE)
            ListAppDbInstanceActionServiceModel actionServiceModel = listAppDbInstanceActionServiceModelService.read(appDbInstance.id)
            Map successResultMap = new LinkedHashMap()
            successResultMap.put(APP_DB_INSTANCE, actionServiceModel)
            return super.setSuccess(successResultMap, UPDATE_SUCCESS_MESSAGE)
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     *
     * @param obj - map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * build  oldDbInstance object for update
     *
     * @param params - serialize parameters form UI
     * @param oldDbInstance - oldDbInstance object
     * @return oldDbInstance - AppDbInstance object with updated value
     */
    private AppDbInstance getAppDbInstance(Map params, AppDbInstance oldDbInstance, Vendor vendor) {
        AppDbInstance newDbInstance = new AppDbInstance(params)
        if (!params.password) {
            oldDbInstance.password = EMPTY_SPACE
        } else {
            oldDbInstance.password = newDbInstance.password
        }
        boolean isSlave = params.isSlave ? true : false
        if (isSlave) {
            if (oldDbInstance.isNative) {
                oldDbInstance.generatedName = newDbInstance.name + " (Native & Slave)"
            } else {
                oldDbInstance.generatedName = newDbInstance.name + " (Slave)"
            }
        } else {
            if (oldDbInstance.isNative) {
                oldDbInstance.generatedName = newDbInstance.name + " (Native & Master)"
            } else {
                oldDbInstance.generatedName = newDbInstance.name + " (Master)"
            }
        }

        SystemEntity sysVendor = appSystemEntityCacheService.read(vendor.vendorId, appSystemEntityCacheService.SYS_ENTITY_TYPE_VENDOR, vendor.companyId)

        oldDbInstance.name = newDbInstance.name
        oldDbInstance.vendorId = newDbInstance.vendorId
        oldDbInstance.typeId = vendor.dbTypeId
        oldDbInstance.reservedVendorId = sysVendor.reservedId
        oldDbInstance.driver = vendor.driver
        oldDbInstance.url = newDbInstance.url
        oldDbInstance.port = newDbInstance.port
        oldDbInstance.dbName = newDbInstance.dbName
        oldDbInstance.userName = newDbInstance.userName
        oldDbInstance.isSlave = newDbInstance.isSlave
        oldDbInstance.isDeletable = newDbInstance.isDeletable
        oldDbInstance.updatedOn = new Date()
        oldDbInstance.updatedBy = super.getAppUser().id
        oldDbInstance.isTested = false
        oldDbInstance.isReadOnly = newDbInstance.isReadOnly
        oldDbInstance.schemaName = newDbInstance.schemaName
        if (!oldDbInstance.schemaName) {
            oldDbInstance.schemaName = getSchemaName(oldDbInstance.reservedVendorId)
        }
        oldDbInstance.connectionString = buildConnectionString(oldDbInstance)
        return oldDbInstance
    }

    /**
     * 1. check oldDbInstance object existence
     * 2. check version
     * 3. check for duplicate name
     * 4. check for duplicate connection
     * 5. check association with data export for dataPipeLine plugin
     * 6. check association with data indexing for document plugin
     *
     * @param params - serialize parameters form UI
     * @param oldDbInstance - an object of AppDbInstance
     * @param dbInstanceId - AppDbInstance object id
     * @param version - last version from UI
     * @return error message when error occurred or null
     */
    private String checkValidation(Map params, AppDbInstance oldDbInstance, long dbInstanceId, long version, Vendor vendor) {
        // check oldDbInstance object existence
        if (!oldDbInstance) {
            return OBJ_NOT_FOUND
        }
        if (oldDbInstance.version != version) {
            return VERSION_MISMATCHED
        }

        int duplicateCount = appDbInstanceService.countByNameIlikeAndTypeIdAndCompanyIdAndIdNotEqual(params.name, vendor.dbTypeId, super.getCompanyId(), dbInstanceId)
        // Check duplicate DBInstance name
        if (duplicateCount > 0) {
            return NAME_MUST_BE_UNIQUE
        }
        // check for duplicate connection
        duplicateCount = appDbInstanceService.countByUrlAndPortAndDbNameAndUserNameAndPasswordAndCompanyIdAndIdNotEqual(params.url.toString(), params.port.toString(), params.dbName.toString(), params.userName.toString(), params.password.toString(), oldDbInstance.companyId, oldDbInstance.id)
        if (duplicateCount > 0) {
            return super.setError(params, SAME_DB_CONNECTION_EXISTS)
        }
        if (dataPipeLineImplService) {
            int countDataExport = dataPipeLineImplService.countDplDataExportByDbInstanceId(oldDbInstance.id)
            if (countDataExport > 0) {
                return countDataExport.toString() + ASSOCIATION_MSG_WITH_DATA_EXPORT
            }
        }

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
