package com.athena.mis.application.actions.dbinstance

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.dts.DataAdapterFactoryService
import com.athena.mis.application.dts.DataAdapterService
import com.athena.mis.application.entity.AppDbInstance
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.entity.Vendor
import com.athena.mis.application.model.ListAppDbInstanceActionServiceModel
import com.athena.mis.application.service.AppDbInstanceService
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.ListAppDbInstanceActionServiceModelService
import com.athena.mis.application.service.VendorService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Create new DBInstance object and show in grid
 *  For details go through Use-Case doc named 'CreateAppDbInstanceActionService'
 */
class CreateAppDbInstanceActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String VENDOR = "vendor"
    private static final String APP_DB_INSTANCE = "appDbInstance"
    private static final String NAME_MUST_BE_UNIQUE = 'DB Instance name must be unique'
    private static final String CREATE_SUCCESS_MESSAGE = 'DB Instance has been successfully saved'
    private static final String SAME_DB_CONNECTION_EXISTS = 'DB Instance already exists with same connection configuration'
    private static final String PUBLIC_SCHEMA = "public"
    private static final String DBO_SCHEMA = "dbo"

    AppSystemEntityCacheService appSystemEntityCacheService
    AppDbInstanceService appDbInstanceService
    VendorService vendorService
    ListAppDbInstanceActionServiceModelService listAppDbInstanceActionServiceModelService
    DataAdapterFactoryService dataAdapterFactoryService

    /**
     * 1. check required parameters
     * 2. check for duplicate AppDbInstance name
     * 3. check for duplicate connection
     * 4. build AppDbInstance object
     *
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            //Check parameters
            if ((!params.name) || (!params.vendorId) || (!params.url) || (!params.port) || (!params.userName) || (!params.dbName)) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            AppUser appUser = super.getAppUser()

            long vendorId = Long.parseLong(params.vendorId.toString())
            Vendor vendor = vendorService.read(vendorId)
            int duplicateCount = appDbInstanceService.countByNameIlikeAndTypeIdAndCompanyId(params.name, vendor.dbTypeId, appUser.companyId)
            // Check duplicate DBInstance name
            if (duplicateCount > 0) {
                return super.setError(params, NAME_MUST_BE_UNIQUE)
            }
            // check for duplicate connection
            duplicateCount = appDbInstanceService.countByUrlAndPortAndDbNameAndUserNameAndPasswordAndCompanyId(params.url.toString(), params.port.toString(), params.dbName.toString(), params.userName.toString(), params.password.toString(), appUser.companyId)
            if (duplicateCount > 0) {
                return super.setError(params, SAME_DB_CONNECTION_EXISTS)
            }
            //build DbInstance object
            AppDbInstance appDbInstance = getAppDbInstance(params, appUser, vendor)
            params.put(VENDOR, vendor)
            params.put(APP_DB_INSTANCE, appDbInstance)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * 1. receive AppDbInstance object from executePreCondition method
     * 2. create new AppDbInstance
     * This method is in transactional block and will roll back in case of any exception
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            // save new AppDbInstance object in DB
            AppDbInstance appDbInstance = (AppDbInstance) result.get(APP_DB_INSTANCE)
            // save appDbInstance object into DB
            appDbInstanceService.create(appDbInstance)
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
        return result
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
            return super.setSuccess(successResultMap, CREATE_SUCCESS_MESSAGE)
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
     * Build AppDbInstance object for create
     *
     * @param params - serialize parameters form UI
     * @return new appDbInstance object
     */
    private AppDbInstance getAppDbInstance(Map params, AppUser appUser, Vendor vendor) {
        if (!params.reservedVendorId) params.reservedVendorId = 0L

        AppDbInstance appDbInstance = new AppDbInstance(params)
        if (!params.password) {
            appDbInstance.password = EMPTY_SPACE
        }
        boolean isSlave = params.isSlave ? true : false
        if (isSlave) {
            appDbInstance.generatedName = appDbInstance.name + " (Slave)"
        } else {
            appDbInstance.generatedName = appDbInstance.name + " (Master)"
        }
        SystemEntity sysVendor = appSystemEntityCacheService.read(vendor.vendorId, appSystemEntityCacheService.SYS_ENTITY_TYPE_VENDOR, vendor.companyId)
        appDbInstance.typeId = vendor.dbTypeId
        appDbInstance.reservedVendorId = sysVendor.reservedId
        if (!appDbInstance.schemaName) {
            appDbInstance.schemaName = getDefaultSchemaName(appDbInstance.reservedVendorId)
        }
        appDbInstance.driver = vendor.driver
        appDbInstance.companyId = appUser.companyId
        appDbInstance.createdBy = appUser.id
        appDbInstance.createdOn = new Date()
        appDbInstance.connectionString = getConnectionString(appDbInstance)
        return appDbInstance
    }

    /**
     * get default schema name by vendor if not given
     * @param reservedVendorId - reserved vendor id
     * @return
     */
    private String getDefaultSchemaName(long reservedVendorId) {
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
    private String getConnectionString(AppDbInstance dbInstance) {
        DataAdapterService dataAdapterService = dataAdapterFactoryService.createAdapter(dbInstance)
        String conString = dataAdapterService.getConnectionString()
        return conString
    }
}
