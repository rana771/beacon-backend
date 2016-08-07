package com.athena.mis.application.actions.appfaq

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppFaq
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.model.ListAppFaqActionServiceModel
import com.athena.mis.application.service.AppFaqService
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.ListAppFaqActionServiceModelService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Create new AppFaq object and show in grid
 *  For details go through Use-Case doc named 'CreateAppFaqActionService'
 */
class CreateAppFaqActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String APP_FAQ = "appFaq"
    private static final String SAVE_SUCCESS_MESSAGE = "Faq has been successfully saved."

    AppFaqService appFaqService
    AppSystemEntityCacheService appSystemEntityCacheService
    ListAppFaqActionServiceModelService listAppFaqActionServiceModelService

    /**
     * 1. check required parameters
     * 2. build AppFaq object to create with parameters
     * @param params -serialized parameters from UI
     * @return -a map containing AppFaq object for execute method
     * map contains isError(true/false) depending on method success
     */
    public Map executePreCondition(Map result) {
        try {
            // check required parameters
            if ((!result.pluginId) || (!result.entityTypeId) || (!result.entityId)) {
                return super.setError(result, ERROR_FOR_INVALID_INPUT)
            }
            // build AppFaq object to create
            AppFaq appFaq = getAppFaq(result)
            result.put(APP_FAQ, appFaq)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Save AppFaq object in DB
     * @param result -returned from executePreCondition method
     * @return -newly created AppFaq object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppFaq appFaq = (AppFaq) result.get(APP_FAQ)
            appFaqService.create(appFaq)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Do nothing at post condition
     */
    public Map executePostCondition(Map result) {
        return result
    }

    @Transactional(readOnly = true)
    public Map buildSuccessResultForUI(Map result) {
        try {
            AppFaq appFaq = (AppFaq) result.get(APP_FAQ)
            ListAppFaqActionServiceModel object = listAppFaqActionServiceModelService.read(appFaq.id)
            result.put(APP_FAQ, object)
            return super.setSuccess(result, SAVE_SUCCESS_MESSAGE)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Do nothing buildFailureResultForUI
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Build AppFaq object to save in DB
     * @param parameterMap -serialized parameters from UI
     * @return -AppFaq object
     */
    private AppFaq getAppFaq(Map parameterMap) {
        AppFaq appFaq = new AppFaq(parameterMap)
        AppUser appUser = super.getAppUser()
        appFaq.createdBy = appUser.id
        appFaq.createdOn = new Date()
        appFaq.companyId = appUser.companyId
        return appFaq
    }
}
