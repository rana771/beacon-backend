package com.athena.mis.application.actions.appfaq

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppFaq
import com.athena.mis.application.model.ListAppFaqActionServiceModel
import com.athena.mis.application.service.AppFaqService
import com.athena.mis.application.service.AppNoteService
import com.athena.mis.application.service.ListAppFaqActionServiceModelService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Update AppFaq object in DB and grid data
 *  For details go through Use-Case doc named 'UpdateAppFaqActionService'
 */
class UpdateAppFaqActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String APP_FAQ = "appFaq"
    private static final String UPDATE_SUCCESS_MESSAGE = "Faq has been updated successfully."
    private static final String NOT_FOUND_MESSAGE = "Selected faq could not be found."

    AppFaqService appFaqService
    ListAppFaqActionServiceModelService listAppFaqActionServiceModelService

    /**
     * 1. check required parameters
     * 2. pull old AppFaq object by id
     * 3. check existence of old AppFaq object 
     * 4. build new AppFaq object for update
     * @param params -serialized parameters from UI
     * @return -a map containing AppAttachment object for execute method
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map result) {
        try {
            // check required parameters
            if (!result.id || !result.version) {
                return super.setError(result, ERROR_FOR_INVALID_INPUT)
            }
            long id = Long.parseLong(result.id.toString())
            AppFaq oldAppFaq = appFaqService.read(id)
            // check existence of old AppFaq object
            if (!oldAppFaq) {
                return super.setError(result, NOT_FOUND_MESSAGE)
            }
            long version = Long.parseLong(result.version.toString())
            if (version != oldAppFaq.version) {
                return super.setError(result, NOT_FOUND_MESSAGE)
            }
            AppFaq appFaq = buildAppNoteObject(result, oldAppFaq)
            result.put(APP_FAQ, appFaq)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Update AppFaq object in DB
     * @param result -AppFaq object send from executePreCondition method
     * @return -newly created AppFaq object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppFaq appFaq = (AppFaq) result.get(APP_FAQ)
            appFaqService.update(appFaq)
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

    /**
     * @param result -updated AppFaq object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map buildSuccessResultForUI(Map result) {
        try {
            AppFaq appFaq = (AppFaq) result.get(APP_FAQ)
            ListAppFaqActionServiceModel object = listAppFaqActionServiceModelService.read(appFaq.id)
            result.put(APP_FAQ, object)
            return super.setSuccess(result, UPDATE_SUCCESS_MESSAGE)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Do nothing at post condition
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Build AppFaq object to update in DB
     * @param parameterMap -serialized parameters from UI
     * @param oldAppFaq -old AppFaq object
     * @return -new AppFaq object
     */
    private AppFaq buildAppNoteObject(Map parameterMap, AppFaq oldAppFaq) {
        AppFaq appFaq = new AppFaq(parameterMap)
        oldAppFaq.question = appFaq.question
        oldAppFaq.answer = appFaq.answer
        oldAppFaq.updatedBy = super.getAppUser().id
        oldAppFaq.updatedOn = new Date()
        return oldAppFaq
    }
}
