package com.athena.mis.application.actions.contentcategory

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.ContentCategory
import com.athena.mis.application.model.ListContentCategoryActionServiceModel
import com.athena.mis.application.service.ContentCategoryService
import com.athena.mis.application.service.ListContentCategoryActionServiceModelService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Update ContentCategory object and grid data
 *  For details go through Use-Case doc named 'UpdateContentCategoryActionService'
 */
class UpdateContentCategoryActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String CONTENT_CATEGORY = "contentCategory"
    private static final String UPDATE_SUCCESS_MSG = "Content category has been updated successfully"
    private static final String CONTENT_NOT_FOUND = "Content category not found to be updated, refresh the page"
    private static final String NAME_EXIST_MESSAGE = "Given content name already exists for same content type"

    ContentCategoryService contentCategoryService
    ListContentCategoryActionServiceModelService listContentCategoryActionServiceModelService

    /**
     * Get ContentCategory object from cache utility by id
     * Check existence of ContentCategory object
     * Get params from UI and build ContentCategory object for update
     * Check uniqueness of name of ContentCategory object by contentTypeId and companyId
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            if ((!params.id) || (!params.version) || (!params.contentTypeId)) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long contentCategoryId = Long.parseLong(params.id.toString())
            long version = Long.parseLong(params.version.toString())
            // get ContentCategory object from cache utility by id
            ContentCategory oldContentCategory = contentCategoryService.read(contentCategoryId)
            // check if ContentCategory object exists or not
            if ((!oldContentCategory) || (oldContentCategory.version != version)) {
                return super.setError(params, CONTENT_NOT_FOUND)
            }
            // check uniqueness of name of ContentCategory object by contentTypeId and companyId
            long contentTypeId = Long.parseLong(params.contentTypeId.toString())
            int countName = contentCategoryService.countByNameAndContentTypeIdAndIdNotEqual(params.name.toString(), contentTypeId, oldContentCategory.id)
            if (countName > 0) {
                return super.setError(params, NAME_EXIST_MESSAGE)
            }
            // build ContentCategory object for update
            ContentCategory contentCategory = getContentCategory(params, oldContentCategory)
            params.put(CONTENT_CATEGORY, contentCategory)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Update ContentCategory object in DB & update cache utility accordingly
     * This function is in transactional block and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Map execute(Map result) {
        try {
            ContentCategory contentCategory = (ContentCategory) result.get(CONTENT_CATEGORY)
            // update ContentCategory object in DB
            contentCategoryService.update(contentCategory)
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
     * Show updated ContentCategory object in grid
     * Show success message
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map buildSuccessResultForUI(Map result) {
        try {
            ContentCategory contentCategory = (ContentCategory) result.get(CONTENT_CATEGORY)
            ListContentCategoryActionServiceModel contentCategoryModel = listContentCategoryActionServiceModelService.read(contentCategory.id)
            result.put(CONTENT_CATEGORY, contentCategoryModel)
            return super.setSuccess(result, UPDATE_SUCCESS_MSG)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
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
     * Build ContentCategory object for update
     * @param params -serialized parameters from UI
     * @param oldContentCategory -old ContentCategory object
     * @return -updated ContentCategory object
     */
    private ContentCategory getContentCategory(Map params, ContentCategory oldContentCategory) {
        ContentCategory newContentCategory = new ContentCategory(params)
        oldContentCategory.contentTypeId = newContentCategory.contentTypeId
        oldContentCategory.name = newContentCategory.name
        oldContentCategory.extension = newContentCategory.extension
        oldContentCategory.width = newContentCategory.width
        oldContentCategory.height = newContentCategory.height
        oldContentCategory.maxSize = newContentCategory.maxSize
        oldContentCategory.updatedBy = super.getAppUser().id
        oldContentCategory.updatedOn = new Date()
        return oldContentCategory
    }
}
