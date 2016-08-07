package com.athena.mis.application.actions.elblognote

import com.athena.mis.application.entity.ElBlogNote
import com.athena.mis.application.service.ElBlogNoteService
import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.AppUser
import com.athena.mis.integration.inventory.InvPluginConnector
import com.athena.mis.utility.DateUtility
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional


class UpdateElBlogNoteActionService extends BaseService implements ActionServiceIntf {

     private Logger log = Logger.getLogger(getClass())

    private static final String EL_BLOG_NOTE = "elBlogNote"
    private static final String EL_BLOG_NOTE_UPDATE_SUCCESS_MESSAGE = "Note has been updated successfully"

    ElBlogNoteService elBlogNoteService
    @Autowired(required = false)

    /**
     * 1. Check Validation
     * 2. Check un-approve transactions for auto approve
     * 3. Build elBlogNote object for update
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

            // build elBlogNote object for update
            getElBlogNote(params)

            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. get the elBlogNote object from map
     * 2. Update existing elBlogNote in DB
     * This method is in transactional block and will roll back in case of any exception
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            ElBlogNote elBlogNote = (ElBlogNote) result.get(EL_BLOG_NOTE)
            elBlogNoteService.update(elBlogNote)
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
        return super.setSuccess(result, EL_BLOG_NOTE_UPDATE_SUCCESS_MESSAGE)
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
     * Build elBlogNote object for update
     *
     * @param params - serialize parameters from UI
     * @return - elBlogNote object
     */
    private ElBlogNote getElBlogNote(Map params) {
        ElBlogNote oldElBlogNote = (ElBlogNote) params.get(EL_BLOG_NOTE)
        ElBlogNote newElBlogNote = new ElBlogNote(params)
        oldElBlogNote.name = newElBlogNote.name
        oldElBlogNote.code = newElBlogNote.code
        AppUser systemUser = super.getAppUser()
        oldElBlogNote.updatedOn = new Date()
        oldElBlogNote.updatedBy = systemUser.id

        // write approval flag holds previous state if user is not config manager

        return oldElBlogNote
    }

    /**
     * 1. Check ElBlogNote object existance
     * 2. Check for duplicate elBlogNote code
     * 3. Check for duplicate elBlogNote name
     * 4. Check parameters
     *
     * @param elBlogNote - object of ElBlogNote
     * @param params - a map from caller method
     * @return -a string containing null value or error message depending on duplicate check
     */
    private String checkValidation(Map params) {
        String errMsg
        //Check parameters

        long elBlogNoteId = Long.parseLong(params.id.toString())
        ElBlogNote elBlogNote = elBlogNoteService.read(elBlogNoteId)

        //check ElBlogNote object existance
        errMsg = checkElBlogNoteExistance(elBlogNote, params)
        if (errMsg != null) return errMsg

        // Check your custom validation here

        params.put(EL_BLOG_NOTE, elBlogNote)
        return null
    }

    /**
     * check ElBlogNote object existance
     *
     * @param elBlogNote - an object of ElBlogNote
     * @param params - a map from caller method
     * @return - error message or null
     */

    private String checkElBlogNoteExistance(ElBlogNote elBlogNote, Map params) {
        long version = Long.parseLong(params.version.toString())
        if (!elBlogNote || elBlogNote.version != version) {
            return ENTITY_NOT_FOUND_ERROR_MESSAGE
        }
        return null
    }

}
