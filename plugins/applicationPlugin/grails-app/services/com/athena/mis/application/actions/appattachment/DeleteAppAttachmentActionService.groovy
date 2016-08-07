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
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to delete AppAttachment object
 *  For details go through Use-Case doc named 'DeleteAppAttachmentActionService'
 */
class DeleteAppAttachmentActionService extends BaseService implements ActionServiceIntf {

    AppAttachmentService appAttachmentService
    ProjectService projectService
    AppSystemEntityCacheService appSystemEntityCacheService

    @Autowired(required = false)
    BudgPluginConnector budgBudgetImplService
    @Autowired(required = false)
    AccPluginConnector accAccountingImplService

    private Logger log = Logger.getLogger(getClass())

    private static final String DELETE_SUCCESS_MESSAGE = "Attachment has been deleted successfully"
    private static final String APP_ATTACHMENT = "appAttachment"

    /**
     * 1. check required parameter
     * 2. pull AppAttachment object from service
     * 3. check for AppAttachment object existence
     * @param params - serialize parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check existence of required parameter
            if (!params.id) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long appAttachmentId = Long.parseLong(params.id.toString())
            AppAttachment appAttachment = appAttachmentService.read(appAttachmentId)
            // check existence of object
            if (!appAttachment) {
                return super.setError(params, ENTITY_NOT_FOUND_ERROR_MESSAGE)
            }
            params.put(APP_ATTACHMENT, appAttachment)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Delete appAttachment object from DB
     * 1. get the appAttachment object from map
     * 2. delete from db
     * 3. update content count of corresponding object
     * This function is in transactional boundary and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppAttachment appAttachment = (AppAttachment) result.get(APP_ATTACHMENT)
            // delete appAttachment object from DB
            appAttachmentService.delete(appAttachment)
            updateContentCount(appAttachment)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * There is no postCondition, so return the same map as received
     * @param result - resulting map from execute
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * 1. put success message
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, DELETE_SUCCESS_MESSAGE)
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     * @param result - map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Update content count counter as content entity type
     * @param appAttachment - object of AppAttachment
     */
    private void updateContentCount(AppAttachment appAttachment) {
        SystemEntity entityContentType = appSystemEntityCacheService.read(appAttachment.entityTypeId, appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT_ENTITY, appAttachment.companyId)
        int count = -1
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
