package com.athena.mis.application.actions.elblognote

import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.ElBlogNote
import com.athena.mis.application.service.ElBlogNoteService
import com.athena.mis.ActionServiceIntf
import com.athena.mis.utility.DateUtility
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional


class CreateElBlogNoteActionService  extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String EL_BLOG_NOTE = "elBlogNote"
    private static final String  EL_BLOG_NOTE_SAVE_SUCCESS_MESSAGE = "Note has been saved successfully"

    ElBlogNoteService elBlogNoteService

    /**
     * 1. check Validation
     * 2. build elBlogNote object
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
            // build elBlogNote object
            ElBlogNote elBlogNote = getElBlogNote(params, user)
            params.put(EL_BLOG_NOTE, elBlogNote)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. receive elBlogNote object from executePreCondition method
     * 2. create new elBlogNote
     * This method is in transactional block and will roll back in case of any exception
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            ElBlogNote elBlogNote = (ElBlogNote) result.get(EL_BLOG_NOTE)
            // save new elBlogNote object in DB
            elBlogNoteService.create(elBlogNote)
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
        return super.setSuccess(result, EL_BLOG_NOTE_SAVE_SUCCESS_MESSAGE)
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
     * Build elBlogNote object
     *
     * @param params - serialize parameters from UI
     * @param user - an object of AppUser
     * @return - elBlogNote object
     */
    private ElBlogNote getElBlogNote(Map params, AppUser user) {
        ElBlogNote elBlogNote = new ElBlogNote(params)
        elBlogNote.createdOn = new Date()
        elBlogNote.createdBy = user.id
        elBlogNote.companyId = user.companyId
        elBlogNote.updatedBy = 0
        elBlogNote.updatedOn = null
        return elBlogNote
    }

    /**
     * 1. check for duplicate elBlogNote name
     * 2. check for duplicate elBlogNote code
     *
     * @param elBlogNote -object of ElBlogNote
     * @param user - an object of AppUser
     * @return -a string containing null value or error message depending on duplicate check
     */
    private String checkValidation(Map params, AppUser user) {
        String errMsg
            //write your validation message here

        return null
    }



}

