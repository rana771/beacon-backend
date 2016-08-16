package com.mis.beacon.device.service

import com.athena.mis.BaseDomainService
import com.mis.beacon.device.Device
import com.athena.mis.utility.DateUtility


class DeviceService extends BaseDomainService {


    @Override
    public void init() {
        domainClass = Device.class
    }

    /**
     * Pull device object
     * @return - list of device
     */
    @Override
    public List<Device> list() {
        return null;
    }

    /**
     * Get list of Device by list of ids
     * @param lstDeviceIds - list of AppGroup.id
     * @return - list of Device by ids
     */
    public List<Device> findAllByIdInList(List<Long> lstDeviceIds) {
        List<Device> lstDevice = null;
        return lstDevice
    }


    @Override
    public void createDefaultSchema() {

    }




    @Override
    public boolean createTestData(long companyId, long userId) {
        //Write your default data insert statement here
        return true
    }


}
