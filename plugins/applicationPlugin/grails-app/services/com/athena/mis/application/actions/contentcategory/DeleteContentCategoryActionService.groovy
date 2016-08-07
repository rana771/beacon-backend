package com.athena.mis.application.actions.contentcategory

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.ContentCategory
import com.athena.mis.application.service.ContentCategoryService
import com.athena.mis.application.service.AppAttachmentService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Delete ContentCategory object from DB and remove it from grid
 *  For details go through Use-Case doc named 'DeleteContentCategoryActionService'
 */
class DeleteContentCategoryActionService extends BaseService implements ActionServiceIntf {

    ContentCategoryService contentCategoryService
    AppAttachmentService appAttachmentService

    private static final String DELETE_SUCCESS_MESSAGE = "Content category has been deleted successfully"
    private static
    final String OBJ_NOT_FOUND = "Selected content category not found, please refresh the content category list"
    private static
    final String ATTACHMENT_FOUND_MSG = " attachment(s) has been created by this content category"
    private static final String IS_RESERVED_MSG = "Selected content category is reserved"
    private static final String CONTENT_CATEGORY = "contentCategory"

    private Logger log = Logger.getLogger(getClass())

    /**
     * 1. check input validation
     * 2. check association
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check input validation
            LinkedHashMap checkValidation = checkInputValidation(params)
            Boolean isError = checkValidation.get(IS_ERROR)
            if (isError.booleanValue()) {
                String message = checkValidation.get(MESSAGE)
                return super.setError(params, message)
            }
            ContentCategory contentCategory = (ContentCategory) checkValidation.get(CONTENT_CATEGORY)
            // check association
            String msg = checkAssociationAndReservedProperties(contentCategory)
            if (msg) {
                return super.setError(params, msg)
            }
            params.put(CONTENT_CATEGORY, contentCategory)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Delete ContentCategory object from DB
     * This function is in transactional block and will roll back in case of any exception
     * @param params -parameters from UI
     * @param obj -N/A
     * @return -a map containing isError(true/false) depending on method success
     */
    @Transactional
    public Map execute(Map result) {
        try {
            ContentCategory contentCategory = (ContentCategory) result.get(CONTENT_CATEGORY)
            contentCategoryService.delete(contentCategory)    // delete employee object from DB
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Do nothing for post operation
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * Remove object from grid
     * Show success message
     * @param obj -N/A
     * @return -a map containing success message for UI
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, DELETE_SUCCESS_MESSAGE)
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * 1. check required parameter
     * 2. get ContentCategory object by id
     * 3. check existence of ContentCategory object
     * @param params -serialized parameters from UI
     * @return - a map containing ContentCategory object, isError (true/false) and relevant error message
     */
    private LinkedHashMap checkInputValidation(Map params) {
        LinkedHashMap result = new LinkedHashMap()
        result.put(IS_ERROR, Boolean.TRUE)
        // check required parameter
        if (!params.id) {
            result.put(MESSAGE, ERROR_FOR_INVALID_INPUT)
            return result
        }
        long contentCategoryId = Long.parseLong(params.id.toString())
        // get ContentCategory object by id
        ContentCategory contentCategory = contentCategoryService.read(contentCategoryId)
        // check if ContentCategory object exists or not
        if (!contentCategory) {
            result.put(MESSAGE, OBJ_NOT_FOUND)
            return result
        }
        result.put(CONTENT_CATEGORY, contentCategory)
        result.put(IS_ERROR, Boolean.FALSE)
        return result
    }

    /**
     * 1. check if ContentCategory object is reserved
     * 2. check association with contentCategoryId of AppAttachment
     * @param contentCategory -object of ContentCategory
     * @return -a string containing error message or null value
     */
    private String checkAssociationAndReservedProperties(ContentCategory contentCategory) {
        // reserved ContentCategory object can not be deleted
        if (contentCategory.isReserved) {
            return IS_RESERVED_MSG
        }
        // check association with contentCategoryId of AppAttachment
        int countAppAttachment = appAttachmentService.countByContentCategoryId(contentCategory.id)
        if (countAppAttachment > 0) {
            return countAppAttachment + ATTACHMENT_FOUND_MSG
        }
        return null
    }
}
