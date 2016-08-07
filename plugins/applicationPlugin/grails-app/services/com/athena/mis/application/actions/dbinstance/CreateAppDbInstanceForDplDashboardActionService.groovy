package com.athena.mis.application.actions.dbinstance

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.dts.DataAdapterFactoryService
import com.athena.mis.application.dts.DataAdapterService
import com.athena.mis.application.entity.AppDbInstance
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.entity.Vendor
import com.athena.mis.application.service.AppDbInstanceService
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.VendorService
import grails.transaction.Transactional
import org.apache.log4j.Logger

/**
 * create AppDbInstance for Dpl dashboard under settings
 * for details please go through use case named 'CreateAppDbInstanceForDplDashboardActionService'
 */
class CreateAppDbInstanceForDplDashboardActionService extends BaseService implements ActionServiceIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String DOC_DB_INSTANCE = 'docDbInstance'
    private static final String CREATE_SUCCESS_MESSAGE = 'DB Instance has been successfully saved'
    private static final String DB_NAME_MUST_BE_UNIQUE = 'DB Instance database name must be unique within Type'
    private static final String JDBC = 'jdbc'
    private static
    final String SAME_DB_CONNECTION_EXISTS = 'DB Instance already exists with same connection configuration'

    AppSystemEntityCacheService appSystemEntityCacheService
    AppDbInstanceService appDbInstanceService
    VendorService vendorService
    DataAdapterFactoryService dataAdapterFactoryService

    /**
     * check necessary parameters
     * read vendor from service
     * check for duplicate database instance
     * build object for create
     * @param parameters
     * @return Map
     */
    @Transactional(readOnly = true)
    Map executePreCondition(Map parameters) {
        try {
            //Check parameters
            if ((!parameters.name) || (!parameters.vendorId) || (!parameters.url) || (!parameters.port) || (!parameters.userName) || (!parameters.dbName)) {
                return super.setError(parameters, ERROR_FOR_INVALID_INPUT)
            }

            AppUser appUser = super.getAppUser()
            Vendor vendor = vendorService.read(Long.parseLong(parameters.vendorId.toString()))
            // check duplication
            String errMsg = checkDuplicate(parameters, vendor.dbTypeId, appUser)
            if (errMsg) {
                return super.setError(parameters, errMsg)
            }

            //build DbInstance object
            AppDbInstance appDbInstance = getAppDbInstance(parameters, appUser, vendor)

            parameters.put(DOC_DB_INSTANCE, appDbInstance)
            return parameters
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * create new AppDbInstance
     * @param previousResult
     * @return
     */
    @Transactional
    Map execute(Map previousResult) {
        try {
            // save new AppDbInstance object in DB
            AppDbInstance appDbInstance = (AppDbInstance) previousResult.get(DOC_DB_INSTANCE)

            // save docDbInstance object into DB
            appDbInstanceService.create(appDbInstance)

            return previousResult
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    Map executePostCondition(Map previousResult) {
        return previousResult
    }

    Map buildSuccessResultForUI(Map executeResult) {
        return super.setSuccess(executeResult, CREATE_SUCCESS_MESSAGE)
    }

    Map buildFailureResultForUI(Map executeResult) {
        return executeResult
    }

    /**
     * Build AppDbInstance object for create
     *
     * @param params - serialize parameters form UI
     * @return new docDbInstance object
     */
    private AppDbInstance getAppDbInstance(Map params, AppUser appUser, Vendor vendor) {
        SystemEntity typeSource = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_DB_INSTANCE_SOURCE, appSystemEntityCacheService.SYS_ENTITY_TYPE_DB_INSTANCE, companyId)
        AppDbInstance appDbInstance = new AppDbInstance(params)
        if (!params.password) {
            appDbInstance.password = EMPTY_SPACE
        }
        appDbInstance.isReadOnly = false
        appDbInstance.isNative = false
        appDbInstance.isTested = false
        appDbInstance.isDeletable = true
        if (vendor.dbTypeId == typeSource.id) {
            appDbInstance.generatedName = appDbInstance.name + " (Slave)"
            appDbInstance.isSlave = true
        } else {
            appDbInstance.generatedName = appDbInstance.name + " (Master)"
            appDbInstance.isSlave = false
        }
        SystemEntity vendorSysEntity = appSystemEntityCacheService.read(vendor.vendorId, appSystemEntityCacheService.SYS_ENTITY_TYPE_VENDOR, vendor.companyId)
        appDbInstance.typeId = vendor.dbTypeId
        appDbInstance.reservedVendorId = vendorSysEntity.reservedId
        if (!appDbInstance.schemaName) {
            appDbInstance.schemaName = getSchemaName(appDbInstance.reservedVendorId)
        }
        appDbInstance.driver = vendor.driver
        // build url for different vendor
        appDbInstance.url = buildUrl(params, appDbInstance.reservedVendorId)
        appDbInstance.connectionString = buildConnectionString(appDbInstance)
        appDbInstance.companyId = appUser.companyId
        appDbInstance.createdBy = appUser.id
        appDbInstance.createdOn = new Date()
        return appDbInstance
    }

    /**
     * build url for different vendor
     * @param params
     * @param reservedId
     * @return
     */
    private String buildUrl(Map params, long reservedId) {
        switch (reservedId) {
            case appSystemEntityCacheService.SYS_ENTITY_VENDOR_POSTGRES:
                return 'jdbc:postgresql://' + params.url
                break
            case appSystemEntityCacheService.SYS_ENTITY_VENDOR_MYSQL:
                return 'jdbc:mysql://' + params.url
                break
            case appSystemEntityCacheService.SYS_ENTITY_VENDOR_ORACLE:
                return 'jdbc:oracle:thin:@' + params.url
                break
            case appSystemEntityCacheService.SYS_ENTITY_VENDOR_MSSQL_2008:
                return 'jdbc:sqlserver://' + params.url
                break
            case appSystemEntityCacheService.SYS_ENTITY_VENDOR_MSSQL_2012:
                return 'jdbc:sqlserver://' + params.url
                break
            case appSystemEntityCacheService.SYS_ENTITY_VENDOR_AMAZON_REDSHIFT:
                return 'jdbc:redshift://' + params.url
                break
            case appSystemEntityCacheService.SYS_ENTITY_VENDOR_GREEN_PLUM:
                return 'jdbc:postgresql://' + params.url
                break
            default:
                return ''
        }
    }

    /**
     * get default schema name by vendor if not given
     * @param reservedVendorId
     * @return schemaName
     */
    private String getSchemaName(long reservedVendorId) {
        String schemaName = null
        switch (reservedVendorId) {
            case appSystemEntityCacheService.SYS_ENTITY_VENDOR_POSTGRES:
                schemaName = "public"
                break
            case appSystemEntityCacheService.SYS_ENTITY_VENDOR_AMAZON_REDSHIFT:
                schemaName = "public"
                break
            case appSystemEntityCacheService.SYS_ENTITY_VENDOR_GREEN_PLUM:
                schemaName = "public"
                break
            case appSystemEntityCacheService.SYS_ENTITY_VENDOR_MSSQL_2008:
                schemaName = "dbo"
                break
            case appSystemEntityCacheService.SYS_ENTITY_VENDOR_MSSQL_2012:
                schemaName = "dbo"
                break
            default:
                break
        }
        return schemaName
    }

    /**
     * 1. check for duplicate db instance
     * 2. check for duplicate connection
     * @param params - serialized parameters from UI
     * @param typeId - vendor type id
     * @param appUser - object of AppUser
     * @return - error message or null value depending on check validation
     */
    private String checkDuplicate(Map params, long typeId, AppUser appUser) {
        int duplicateCount = appDbInstanceService.countByNameIlikeAndTypeIdAndCompanyId(params.name.toString(), typeId, appUser.companyId)
        // Check duplicate DBInstance name
        if (duplicateCount > 0) {
            return DB_NAME_MUST_BE_UNIQUE
        }
        // check for duplicate connection
        duplicateCount = appDbInstanceService.countByUrlAndPortAndDbNameAndUserNameAndPasswordAndCompanyId(params.url.toString(), params.port.toString(), params.dbName.toString(), params.userName.toString(), params.password.toString(), appUser.companyId)
        if (duplicateCount > 0) {
            return SAME_DB_CONNECTION_EXISTS
        }
        return null
    }

    /**
     * get connection string
     * @param dbInstance - object of AppDbInstance
     * @return - connection string
     */
    private String buildConnectionString(AppDbInstance dbInstance) {
        DataAdapterService dataAdapterService = dataAdapterFactoryService.createAdapter(dbInstance)
        String conString = dataAdapterService.getConnectionString()
        return conString
    }
}
