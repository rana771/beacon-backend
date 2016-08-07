package com.athena.mis.application.actions.dbinstance

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppDbInstance
import com.athena.mis.application.service.AppDbInstanceService
import org.apache.log4j.Logger

/**
 *  Show result of object(s) of DocDbInstance
 *  For details go through Use-Case doc named 'ShowResultForDbInstanceActionService'
 */
class ShowResultForDbInstanceActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String NOT_FOUND = "Database instance not found"
    private static final String TEST_CON_SUCCESS = "Connection successful"
    private static final String DB_INSTANCE_ID = "dbInstanceId"
    private static final String NOT_TESTED = "DB instance is not tested"

    AppDbInstanceService appDbInstanceService

    public Map executePreCondition(Map params) {
        // do not nothing for pre condition
        return params
    }

    /**
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * Get DocDbInstance object from cache
     * 1. check appDbInstance object existence
     * 2. initialize jdbcConnection connection by appDbInstance
     * 3. check connection error
     * @return -a map contains isError(true/false) depending on method success
     */
    public Map execute(Map params) {
        try {
            long dbInstanceId = Long.parseLong(params.dbInstanceId.toString())
            AppDbInstance appDbInstance = appDbInstanceService.read(dbInstanceId)
            // check appDbInstance object existence
            if (!appDbInstance) {
                return setError(params, NOT_FOUND)
            }
            // test connection
            if (!appDbInstance.isTested) {
                return setError(params, NOT_TESTED)
            }
            params.put(DB_INSTANCE_ID, dbInstanceId)
            return params
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, TEST_CON_SUCCESS)
    }

    public Map buildFailureResultForUI(Map result) {
        return result
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
}
