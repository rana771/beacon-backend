package com.mis.beacon.device.device

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.mis.beacon.device.Device
import com.mis.beacon.device.service.DeviceService
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class UpdateDeviceActionService extends BaseService implements ActionServiceIntf {

     private Logger log = Logger.getLogger(getClass())

    private static final String DEVICE = "device"
    private static final String DEVICE_UPDATE_SUCCESS_MESSAGE = "Device has been updated successfully"

    DeviceService deviceService
    @Autowired(required = false)

    /**
     * 1. Check Validation
     * 2. Check un-approve transactions for auto approve
     * 3. Build device object for update
     *
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            //Check Validation
            String errMsg = checkValidation(params)
            if (errMsg) {
                return super.setError(params, errMsg)
            }

            // check your custom valiation

            // build device object for update
            getDevice(params)

            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. get the device object from map
     * 2. Update existing device in DB
     * This method is in transactional block and will roll back in case of any exception
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            Device device = (Device) result.get(DEVICE)
            deviceService.update(device)
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
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, DEVICE_UPDATE_SUCCESS_MESSAGE)
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     *
     * @param result -map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Build device object for update
     *
     * @param params - serialize parameters from UI
     * @return - device object
     */
    private Device getDevice(Map params) {
        Device oldDevice = (Device) params.get(DEVICE)
        Device newDevice = new Device(params)
        oldDevice.name = newDevice.name
        oldDevice.code = newDevice.code
        AppUser systemUser = super.getAppUser()
        oldDevice.updatedOn = new Date()
        oldDevice.updatedBy = systemUser.id

        // write approval flag holds previous state if user is not config manager

        return oldDevice
    }

    /**
     * 1. Check Device object existance
     * 2. Check for duplicate device code
     * 3. Check for duplicate device name
     * 4. Check parameters
     *
     * @param device - object of Device
     * @param params - a map from caller method
     * @return -a string containing null value or error message depending on duplicate check
     */
    private String checkValidation(Map params) {
        String errMsg
        //Check parameters

        long deviceId = Long.parseLong(params.id.toString())
        Device device = deviceService.read(deviceId)

        //check Device object existance
        errMsg = checkDeviceExistance(device, params)
        if (errMsg != null) return errMsg

        // Check your custom validation here

        params.put(DEVICE, device)
        return null
    }

    /**
     * check Device object existance
     *
     * @param device - an object of Device
     * @param params - a map from caller method
     * @return - error message or null
     */

    private String checkDeviceExistance(Device device, Map params) {
        long version = Long.parseLong(params.version.toString())
        if (!device || device.version != version) {
            return ENTITY_NOT_FOUND_ERROR_MESSAGE
        }
        return null
    }

}
