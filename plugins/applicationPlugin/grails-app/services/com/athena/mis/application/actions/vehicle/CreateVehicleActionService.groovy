package com.athena.mis.application.actions.vehicle

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Vehicle
import com.athena.mis.application.service.VehicleService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Create new vehicle object and show in grid
 *  For details go through Use-Case doc named 'CreateVehicleActionService'
 */
class CreateVehicleActionService extends BaseService implements ActionServiceIntf {

    private static final String VEHICLE_SAVE_SUCCESS_MESSAGE = "Vehicle has been saved successfully"
    private static final String VEHICLE_ALREADY_EXIST = "Same Vehicle name already exist"
    private static final String INVALID_INPUT_MSG = "Failed to create vehicle due to invalid input"
    private static final String VEHICLE = "vehicle"

    private Logger log = Logger.getLogger(getClass())

    VehicleService vehicleService

    /**
     * 1. input validation check
     * 2. duplicate check for vehicle-name
     * @param params - receive vehicle object from controller
     * @return - map.
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            //Check parameters
            if (!params.name) {
                return super.setError(params, INVALID_INPUT_MSG)
            }
            int duplicateCount = vehicleService.countByNameIlikeAndCompanyId(params.name, super.companyId)
            if (duplicateCount > 0) {
                return super.setError(params, VEHICLE_ALREADY_EXIST)
            }
            Vehicle vehicle = buildObject(params)
            params.put(VEHICLE, vehicle)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }
    /**
     * 1. receive vehicle object from pre execute method
     * 2. create new vehicle
     * This method is in transactional block and will roll back in case of any exception
     * @param result - map received from pre execute method
     * @return - map.
     */
    @Transactional
    public Map execute(Map result) {
        try {
            Vehicle vehicle = (Vehicle) result.get(VEHICLE)
            vehicleService.create(vehicle)
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
     * @param result - map received from executePost method
     * @return - map containing success message
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, VEHICLE_SAVE_SUCCESS_MESSAGE)
    }
    /**
     *
     * @param result - map received from previous method
     * @return - map
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Build Vehicle object
     * @param parameterMap -serialized parameters from UI
     * @return -new vehicle object
     */
    private Vehicle buildObject(Map parameterMap) {
        Vehicle vehicle = new Vehicle(parameterMap)
        vehicle.companyId = companyId
        vehicle.createdOn = new Date()
        vehicle.createdBy = appUser.id
        vehicle.updatedOn = null
        vehicle.updatedBy = 0
        return vehicle
    }
}