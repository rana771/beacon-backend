package com.mis.beacon.integration.beacon

import com.athena.mis.BaseBootStrapService
import com.athena.mis.application.entity.Company
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.mis.beacon.bootsrap.BeaconBootStrapActionService
import grails.plugin.springsecurity.SpringSecurityService

class BeaconBootStrapService extends BaseBootStrapService {

    SpringSecurityService springSecurityService
    BeaconBootStrapActionService beaconBootStrapActionService
    AppSystemEntityCacheService appSystemEntityCacheService



    @Override
    boolean init(Company company) {
        try {
            Map params = [company: company]
            // execute pre condition
            Map preResult = beaconBootStrapActionService.executePreCondition(params)

            // execute the action
            Map executeResult = beaconBootStrapActionService.execute(preResult)

            // execute the post actions
            Map postResult = beaconBootStrapActionService.executePostCondition(executeResult)


            // reload request map
            springSecurityService.clearCachedRequestmaps()
            // load app system entity cache service
            appSystemEntityCacheService.init()

            return true
        } catch (Exception ex) {
            return false
        }
    }

    @Override
    boolean init() {
        return init(null)
    }
}
