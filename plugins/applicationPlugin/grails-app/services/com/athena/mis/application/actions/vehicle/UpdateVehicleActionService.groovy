package com.athena.mis.application.actions.vehicle

import com.athena.mis.BaseService
import com.athena.mis.application.entity.Vehicle
import com.athena.mis.application.service.VehicleService
import com.athena.mis.ActionServiceIntf
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Update vehicle object and grid data
 *  For details go through Use-Case doc named 'UpdateVehicleActionService'
 */
class UpdateVehicleActionService extends BaseService implements ActionServiceIntf {

    private static final String VEHICLE_UPDATE_SUCCESS_MESSAGE = "Vehicle has been updated successfully"
    private static final String VEHICLE_ALREADY_EXIST = "Same Vehicle name already exist"
    private static final String INVALID_INPUT_MSG = "Failed to update vehicle due to invalid input"
    private static final String OBJ_CHANGED_MSG = "Selected vehicle has been changed, Refresh the page again"
    private static final String VEHICLE = "vehicle"

    VehicleService vehicleService

    private Logger log = Logger.getLogger(getClass())
    /**
     * 1. pull vehicle object
     * 2. check input validation
     * 3. duplicate check for vehicle-name
     * @param parameters -serialized parameters from UI
     * @return - a map vehicle object.
     */
    public Map executePreCondition(Map params) {
        try {
            //Check parameters
            if ((!params.id) || (!params.version) || (!params.name)) {
                return super.setError(params, INVALID_INPUT_MSG)
            }
            long id = Long.parseLong(params.id.toString())
            long version = Long.parseLong(params.version.toString())

            //Check existing of Obj and version matching
            Vehicle oldVehicle = (Vehicle) vehicleService.read(id)
            if ((!oldVehicle) || oldVehicle.version != version) {
                return super.setError(params, OBJ_CHANGED_MSG)
            }
            // Check existing of same vehicle name
            String name = params.name.toString()
            int duplicateCount = vehicleService.countByNameIlikeAndCompanyIdAndIdNotEqual(name, super.companyId, oldVehicle.id)
            if (duplicateCount > 0) {
                return super.setError(params, VEHICLE_ALREADY_EXIST)
            }
            Vehicle vehicle = buildObject(params, oldVehicle)
            params.put(VEHICLE, vehicle)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }
    /**
     * 1. receive vehicle object from pre execute method
     * 2. Update new vehicle
     * This method is in transactional block and will roll back in case of any exception
     * @param result - receive vehicle object from pre execute method
     * @return - Integer value(e.g- 1 for success & 0 for failure)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            Vehicle vehicle = (Vehicle) result.get(VEHICLE)
            vehicleService.update(vehicle)
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
     * @param result - map received from execute method
     * @return - map with success message
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, VEHICLE_UPDATE_SUCCESS_MESSAGE)
    }

    /**
     *
     * @param result - map received from previous method
     * @return - map
     */
    public Map buildFailureResultForUI(Map params) {
        return params
    }

    /**
     * Build Vehicle object
     * @param parameterMap -serialized parameters from UI
     * @param oldVehicle -object of Vehicle
     * @return -new Vehicle object
     */
    private Vehicle buildObject(Map parameterMap, Vehicle oldVehicle) {
        Vehicle vehicle = new Vehicle(parameterMap)
        oldVehicle.name = vehicle.name
        oldVehicle.description = vehicle.description
        oldVehicle.updatedBy = super.appUser.id
        oldVehicle.updatedOn = new Date()
        return oldVehicle
    }
}