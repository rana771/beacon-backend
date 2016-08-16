package com.mis.beacon.device.device

import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.mis.beacon.device.Device
import com.athena.mis.ActionServiceIntf
import com.athena.mis.utility.DateUtility
import com.mis.beacon.device.service.DeviceService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional


class CreateDeviceActionService  extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String DEVICE = "device"
    private static final String  DEVICE_SAVE_SUCCESS_MESSAGE = "Device has been saved successfully"

    DeviceService deviceService

    /**
     * 1. check Validation
     * 2. build device object
     *
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            AppUser user = super.getAppUser();
            // check Validation
            String errMsg = checkValidation(params, user)
            if (errMsg) {
                return super.setError(params, errMsg)
            }
            // build device object
            Device device = getDevice(params, user)
            params.put(DEVICE, device)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. receive device object from executePreCondition method
     * 2. create new device
     * This method is in transactional block and will roll back in case of any exception
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            Device device = (Device) result.get(DEVICE)
            // save new device object in DB
            deviceService.create(device)
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
     * @param result -map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, DEVICE_SAVE_SUCCESS_MESSAGE)
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
     * Build device object
     *
     * @param params - serialize parameters from UI
     * @param user - an object of AppUser
     * @return - device object
     */
    private Device getDevice(Map params, AppUser user) {
        Device device = new Device(params)
        device.createdOn = new Date()
        device.createdBy = user.id
        device.companyId = user.companyId
        device.updatedBy = 0
        device.updatedOn = null
        return device
    }

    /**
     * 1. check for duplicate device name
     * 2. check for duplicate device code
     *
     * @param device -object of Device
     * @param user - an object of AppUser
     * @return -a string containing null value or error message depending on duplicate check
     */
    private String checkValidation(Map params, AppUser user) {
        String errMsg
            //write your validation message here

        return null
    }



}

