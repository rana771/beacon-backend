package com.athena.mis.application.actions.vehicle

import com.athena.mis.BaseService
import com.athena.mis.application.entity.Vehicle
import com.athena.mis.application.service.VehicleService
import com.athena.mis.ActionServiceIntf
import org.apache.log4j.Logger

/**
 *  Show list of vehicle for grid
 *  For details go through Use-Case doc named 'ListVehicleActionService'
 */
class ListVehicleActionService extends BaseService implements ActionServiceIntf {

    VehicleService vehicleService

    private Logger log = Logger.getLogger(getClass())

    /**
     *
     * @param params - serialized parameters from UI
     * @return - map
     */
    public Map executePreCondition(Map params) {
        return params
    }
    /**
     *
     * @param result - map received from preExecute method
     * @return
     */
    public Map execute(Map result) {
        try {
            Map resultMap = super.getSearchResult(result, Vehicle.class)
            result.put(LIST, resultMap.list)
            result.put(COUNT, resultMap.count)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }
    /**
     *
     * @param result - map received from execute method
     * @return - map
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     *
     * @param result - map received from post execute method
     * @return - map
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }
    /**
     *
     * @param result - map received from previous method
     * @return - map
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }
}
