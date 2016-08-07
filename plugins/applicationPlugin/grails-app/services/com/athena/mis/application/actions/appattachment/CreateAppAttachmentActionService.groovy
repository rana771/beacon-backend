package com.athena.mis.application.actions.appattachment

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppAttachment
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppAttachmentService
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.ProjectService
import com.athena.mis.integration.accounting.AccPluginConnector
import com.athena.mis.integration.budget.BudgPluginConnector
import com.athena.mis.utility.DateUtility
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to create (upload) new AppAttachment object and show on grid list
 *  For details go through Use-Case doc named 'CreateAppAttachmentActionService'
 */
class CreateAppAttachmentActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String SAVE_SUCCESS_MESSAGE = "Attachment has been successfully saved"
    private static final String APP_ATTACHMENT = "appAttachment"

    AppAttachmentService appAttachmentService
    ProjectService projectService
    AppSystemEntityCacheService appSystemEntityCacheService

    @Autowired(required = false)
    BudgPluginConnector budgBudgetImplService
    @Autowired(required = false)
    AccPluginConnector accAccountingImplService

    /**
     * Check different criteria for creating(Uploading) new AppAttachment
     *      1) Check existence of required parameters
     *      2) Validate AppAttachment object
     *      3) Build AppAttachment object for create
     * @param params -parameter send from UI
     * @param obj -N/A
     * @return -a map containing AppAttachment object for execute method
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check here for required params are present
            if (!params.entityTypeId || !params.entityId || !params.attachmentId || !params.caption) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            // build appAttachment object to create
            AppAttachment appAttachment = getAppAttachment(params)
            params.put(APP_ATTACHMENT, appAttachment)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Save AppAttachment object in DB
     * @param parameters -N/A
     * @param obj -AppAttachment Object send from executePreCondition
     * @return -newly created AppAttachment object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppAttachment appAttachment = (AppAttachment) result.get(APP_ATTACHMENT)
            appAttachmentService.createWithoutContent(appAttachment)
            updateContentCount(appAttachment)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * do nothing at post condition
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * Wrap newly created AppAttachment object to show on grid
     * @param obj -newly created AppAttachment object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, SAVE_SUCCESS_MESSAGE)
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Map buildFailureResultForUI(Map result) {
        Map failureResult = new LinkedHashMap<>()
        return super.setError(failureResult, result.get(MESSAGE).toString())
    }

    /**
     * build AppAttachment object to save in DB
     * @param params -GrailsParameterMap
     * @param contentFile -Commons Multi-part File
     * @param contentCategory -object of ContentCategory
     * @param contentTypeImage -object of SystemEntity
     * @return -AppAttachment object
     */
    private AppAttachment getAppAttachment(Map params) {
        long attachmentId = Long.parseLong(params.attachmentId.toString())
        AppAttachment appAttachment = appAttachmentService.read(attachmentId)
        AppAttachment newAppAttachment = new AppAttachment(params)

        appAttachment.expirationDate = params.expirationDate ? DateUtility.parseMaskedDate(params.expirationDate.toString()) : null
        appAttachment.createdBy = super.getAppUser().id
        appAttachment.caption = newAppAttachment.caption
        appAttachment.isDraft = false
        return appAttachment
    }

    /**
     * Update content count counter as content entity type
     * @param appAttachment - object of AppAttachment
     */
    private void updateContentCount(AppAttachment appAttachment) {
        SystemEntity entityContentType = appSystemEntityCacheService.read(appAttachment.entityTypeId, appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT_ENTITY, appAttachment.companyId)
        int count = 1
        int updateCount = 1
        switch (entityContentType.reservedId) {
            case appSystemEntityCacheService.SYS_ENTITY_CONTENT_ENTITY_BUDGET:
                updateCount = (Integer) budgBudgetImplService.updateContentCountForBudget(appAttachment.entityId, count)
                break
            case appSystemEntityCacheService.SYS_ENTITY_CONTENT_ENTITY_PROJECT:
                updateCount = projectService.updateContentCountForProject(appAttachment.entityId, count)
                break
            case appSystemEntityCacheService.SYS_ENTITY_CONTENT_ENTITY_FINANCIAL_YEAR:
                updateCount = (Integer) accAccountingImplService.updateContentCountForFinancialYear(appAttachment.entityId, count)
                break
            default:
                break
        }
        if (updateCount <= 0) {
            throw new RuntimeException("Error occurred updating content count")
        }
    }
}
