package com.mis.beacon.integration

import com.athena.mis.application.entity.Company
import com.athena.mis.application.session.AppSessionService
import com.athena.mis.integration.beacon.BeaconPluginConnector
import com.mis.beacon.integration.beacon.BeaconBootStrapService
import org.springframework.beans.factory.annotation.Autowired

class BeaconImplService extends BeaconPluginConnector {

    static transactional = false
    static lazyInit = false

    @Autowired
    AppSessionService appSessionService
    BeaconBootStrapService beaconBootStrapService

    @Override
    public boolean initialize() {
        setPlugin(PLUGIN_NAME, this);
        return true
    }

    @Override
    public String getName() {
        return PLUGIN_NAME;
    }

    @Override
    public int getId() {
        return PLUGIN_ID;
    }

    @Override
    public String getPrefix() {
        return PLUGIN_PREFIX
    }

    @Override
    public void bootStrap(Company company) {
        beaconBootStrapService.init(company);
    }


}
