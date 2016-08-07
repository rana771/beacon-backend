package com.athena.mis.application.actions.dbinstancequery

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppDbInstance
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppDbInstanceService
import com.athena.mis.application.service.AppMyFavouriteService
import com.athena.mis.application.service.AppSystemEntityCacheService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Show Db instance query Page
 *  For details go through Use-Case doc named 'ShowDbInstanceQueryActionService'
 */
class ShowDbInstanceQueryActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String VENDOR_NAME = 'vendorName'
    private static final String APP_DB_INSTANCE = 'appDbInstance'

    AppSystemEntityCacheService appSystemEntityCacheService
    AppDbInstanceService appDbInstanceService
    AppMyFavouriteService appMyFavouriteService

    /**
     * 1. check required inputs
     * 2. Get docOfflineDataFeed object from DB
     * 3. check validation
     *
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePreCondition(Map params) {
        String dbInstanceIdStr = params.dbInstanceId ? params.dbInstanceId : params.oId ? params.oId : params.pId
        // check required parameter
        if (!dbInstanceIdStr) {
            return super.setError(params, ERROR_FOR_INVALID_INPUT)
        }
        return params
    }

    /**
     * There is no execute, so return the same map as received
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            String dbInstanceIdStr = result.dbInstanceId ? result.dbInstanceId : result.oId ? result.oId : result.pId
            long dbInstanceId = Long.parseLong(dbInstanceIdStr)
            AppDbInstance appDbInstance = appDbInstanceService.read(dbInstanceId)
            // Check validation
            String errMsg = validation(appDbInstance)
            if (errMsg) {
                appMyFavouriteService.setIsDirtyAndIsDefault(result)
                return super.setError(result, errMsg)
            }
            if (appDbInstance.isReadOnly) {
                appDbInstance.dbName = appDbInstance.dbName + PARENTHESIS_START + "Read Only" + PARENTHESIS_END
            } else {
                appDbInstance.dbName = appDbInstance.dbName + PARENTHESIS_START + "Read/Write" + PARENTHESIS_END
            }
            SystemEntity sysVendor = appSystemEntityCacheService.readByReservedId(appDbInstance.reservedVendorId, appSystemEntityCacheService.SYS_ENTITY_TYPE_VENDOR, appDbInstance.companyId)
            result.put(APP_DB_INSTANCE, appDbInstance)
            String vendorName = sysVendor.key + SINGLE_SPACE + THIRD_BRACKET_START + appDbInstance.generatedName + SINGLE_SPACE + THIRD_BRACKET_END
            result.put(VENDOR_NAME, vendorName)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
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
     * For show there is no success message
     * since the input-parameter already have "isError:false", just return the same map
     *
     * @param result -map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     *
     * @param obj -map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Check AppDbInstance object existence
     *
     * @param appDbInstance - an object of AppDbInstance
     * @return error message when error occurred or null
     */
    private String validation(AppDbInstance appDbInstance) {
        String errMsg
        //check AppDbInstance object existence
        errMsg = checkAppDbInstanceExists(appDbInstance)
        if (errMsg != null) return errMsg
        return null
    }

    private String checkAppDbInstanceExists(AppDbInstance appDbInstance) {
        // Check appDbInstance object existence
        if (!appDbInstance) {
            return ENTITY_NOT_FOUND_ERROR_MESSAGE
        }
        return null
    }
}
