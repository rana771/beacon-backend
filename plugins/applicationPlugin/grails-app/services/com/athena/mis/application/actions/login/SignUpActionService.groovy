package com.athena.mis.application.actions.login

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.service.AppUserService
import grails.plugin.springsecurity.SpringSecurityService
import org.apache.log4j.Logger

class SignUpActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    AppUserService appUserService
    SpringSecurityService springSecurityService

    @Override
    Map executePreCondition(Map params) {
        if (!params.username || !params.loginId || !params.password) {
            return super.setError(params, ERROR_FOR_INVALID_INPUT)
        }
        return params
    }

    @Override
    Map execute(Map result) {
        try {
            AppUser appUser = getAppUserObject(result)
            appUserService.create(appUser)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    @Override
    Map executePostCondition(Map result) {
        return result
    }

    @Override
    Map buildSuccessResultForUI(Map result) {
        return result
    }

    @Override
    Map buildFailureResultForUI(Map result) {
        return result
    }

    private AppUser getAppUserObject(Map params) {
        AppUser appUser = new AppUser(params)
        appUser.password = springSecurityService.encodePassword(params.password)
        appUser.companyId = 1
        appUser.createdBy = 1
        appUser.createdOn = new Date()
        appUser.enabled = true
        appUser.nextExpireDate = new Date() + 360
        return appUser
    }
}
