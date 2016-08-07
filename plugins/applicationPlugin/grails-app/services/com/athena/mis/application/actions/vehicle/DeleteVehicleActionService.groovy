package com.athena.mis.application.actions.vehicle

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.Vehicle
import com.athena.mis.application.service.VehicleService
import com.athena.mis.integration.inventory.InvPluginConnector
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Delete vehicle object from DB and cache utility and remove it from grid
 *  For details go through Use-Case doc named 'DeleteVehicleActionService'
 */
class DeleteVehicleActionService extends BaseService implements ActionServiceIntf {

    private static final String DELETE_VEHICLE_SUCCESS_MESSAGE = "Vehicle has been deleted successfully"
    private static
    final String HAS_ASSOCIATION_MESSAGE_INVENTORY_TRANSACTION = " inventory transaction is associated with selected vehicle"
    private static final String VEHICLE = "vehicle"

    private Logger log = Logger.getLogger(getClass())

    VehicleService vehicleService

    /**
     * 1. pull vehicle object
     * 2. check for vehicle existence
     * 3. association check for vehicle with inventory transaction
     * @param params - serialize parameters from UI
     * @return - a map containing vehicle object.
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            long vehicleId = Long.parseLong(params.id.toString())
            Vehicle vehicle = (Vehicle) vehicleService.read(vehicleId)
            if (vehicle == null) {
                return super.setError(params, ENTITY_NOT_FOUND_ERROR_MESSAGE)
            }

            Map preResult = (Map) hasAssociation(vehicle)
            Boolean hasAssociation = (Boolean) preResult.get(HAS_ASSOCIATION)
            if (hasAssociation.booleanValue()) {
                String message = preResult.get(MESSAGE)
                return super.setError(params, message)
            }

            params.put(VEHICLE, vehicle)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }
    /**
     * Delete vehicle object from DB
     * 1. delete selected vehicle
     * This function is in transactional boundary and will roll back in case of any exception
     * @param result - parameters from pre execute method
     * @return - map
     */
    @Transactional
    public Map execute(Map result) {
        try {
            Vehicle vehicle = (Vehicle) result.get(VEHICLE)
            vehicleService.delete(vehicle)
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
        return super.setSuccess(result, DELETE_VEHICLE_SUCCESS_MESSAGE)
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
     * 1. check association with vehicle id of inventory transaction
     * @param vehicle - vehicle object
     * @return - a map containing hasAssociation(true/false) & relevant association check message
     */
    private LinkedHashMap hasAssociation(Vehicle vehicle) {
        LinkedHashMap result = new LinkedHashMap()
        long vehicleId = vehicle.id
        Integer count = 0
        result.put(HAS_ASSOCIATION, Boolean.TRUE)

        if (PluginConnector.isPluginInstalled(InvPluginConnector.PLUGIN_NAME)) {
            count = countInventoryTransaction(vehicleId)
            if (count.intValue() > 0) {
                result.put(MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_INVENTORY_TRANSACTION)
                return result
            }
        }

        result.put(HAS_ASSOCIATION, Boolean.FALSE)
        return result
    }

    private static final String INV_INVENTORY_TRANSACTION_DETAILS = """
            SELECT COUNT(id) AS count
            FROM inv_inventory_transaction_details
            WHERE vehicle_id = :vehicleId """
    /**
     * Get total vehicle number of given vehicle-id
     * @param vehicleId - vehicle id
     * @return - total vehicle number
     */
    private int countInventoryTransaction(long vehicleId) {
        List results = executeSelectSql(INV_INVENTORY_TRANSACTION_DETAILS, [vehicleId: vehicleId])
        int count = results[0].count
        return count
    }
}
