package com.athena.mis.application.actions.contentcategory

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.ContentCategory
import com.athena.mis.application.model.ListContentCategoryActionServiceModel
import com.athena.mis.application.service.ContentCategoryService
import com.athena.mis.application.service.ListContentCategoryActionServiceModelService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Create new ContentCategory object and show in grid
 *  For details go through Use-Case doc named 'CreateContentCategoryActionService'
 */
class CreateContentCategoryActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String CONTENT_CATEGORY = "contentCategory"
    private static final String CREATE_SUCCESS_MSG = "Content category has been created successfully"
    private static final String NAME_EXIST_MESSAGE = "Given content name already exists for same content type"

    ContentCategoryService contentCategoryService
    ListContentCategoryActionServiceModelService listContentCategoryActionServiceModelService

    /**
     * Get parameters from UI and build ContentCategory object
     * Check uniqueness of name of ContentCategory object by contentTypeId and companyId
     * @param params -serialized parameters from UI
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Map executePreCondition(Map params) {
        try {
            long contentTypeId = Long.parseLong(params.contentTypeId.toString())
            // check uniqueness of name of ContentCategory object by contentTypeId and companyId
            int countName = contentCategoryService.countByNameAndContentTypeId(params.name, contentTypeId)
            if (countName > 0) {
                return super.setError(params, NAME_EXIST_MESSAGE)
            }
            // build ContentCategory object with params
            ContentCategory contentCategory = getContentCategory(params)
            params.put(CONTENT_CATEGORY, contentCategory)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Save ContentCategory object in DB
     * This method is in transactional block and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Map execute(Map result) {
        try {
            ContentCategory contentCategory = (ContentCategory) result.get(CONTENT_CATEGORY)
            // save new ContentCategory object in DB
            contentCategoryService.create(contentCategory)
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
     * Show newly created ContentCategory object in grid
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
            return super.setSuccess(result, CREATE_SUCCESS_MSG)
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
     * Build ContentCategory object
     * @param params -serialized parameters from UI
     * @return -new ContentCategory object
     */
    private ContentCategory getContentCategory(Map params) {
        AppUser appUser = super.getAppUser()
        long contentCategoryId = contentCategoryService.getContentCategoryId()
        ContentCategory contentCategory = new ContentCategory(params)
        contentCategory.id = contentCategoryId
        contentCategory.systemContentCategory = contentCategoryId
        contentCategory.createdBy = appUser.id
        contentCategory.createdOn = new Date()
        contentCategory.companyId = appUser.companyId
        return contentCategory
    }
}
