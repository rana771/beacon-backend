package com.athena.mis.application.actions.appnote

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppNote
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppNoteService
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Delete AppNote object from DB and remove it from grid
 *  For details go through Use-Case doc named 'DeleteAppNoteActionService'
 */
class DeleteAppNoteActionService extends BaseService implements ActionServiceIntf {

    AppSystemEntityCacheService appSystemEntityCacheService
    AppNoteService appNoteService

    @Autowired(required = false)
    ExchangeHousePluginConnector exchangeHouseImplService

    private Logger log = Logger.getLogger(getClass())

    private static final String DELETE_SUCCESS_MESSAGE = "Note has been deleted successfully"
    private static final String ENTITY_NOT_FOUND = "Selected note not found"
    private static final String SYSTEM_NOTE_CAN_NOT_BE_DELETED = "System note can not be deleted"
    private static
    final String VERIFIED_AND_APPROVED_NOTE_CAN_NOT_BE_DELETED = "Verified and approved note can not be deleted"
    private static final String APP_NOTE = "appNote"

    /**
     * 1. check required parameter
     * 2. pull AppNote object from service
     * 3. check for AppNote object existence
     * @param params - serialize parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check required parameters
            if (!params.id) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long id = Long.parseLong(params.id.toString())
            AppNote appNote = appNoteService.read(id)
            // check existence of AppNote object
            if (!appNote) {
                return super.setError(params, ENTITY_NOT_FOUND)
            }
            if (exchangeHouseImplService) {
                String errMsg = checkForExhNoteDelete(appNote)
                if (errMsg) {
                    return super.setError(params, errMsg)
                }
            }
            params.put(APP_NOTE, appNote)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }
    /**
     * Delete AppNote object from DB
     * 1. get the AppNote object from map
     * 2. delete from db
     * This function is in transactional boundary and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppNote appNote = (AppNote) result.get(APP_NOTE)
            appNoteService.delete(appNote)
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

    private String checkForExhNoteDelete(AppNote appNote) {
        SystemEntity systemNote = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_NOTE_SYSTEM_NOTE, appSystemEntityCacheService.SYS_ENTITY_TYPE_NOTE, appNote.companyId)
        if (appNote.entityNoteTypeId == systemNote.id) {
            return SYSTEM_NOTE_CAN_NOT_BE_DELETED
        }
        SystemEntity verifiedNote = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_NOTE_VERIFIED_AND_APPROVED, appSystemEntityCacheService.SYS_ENTITY_TYPE_NOTE, appNote.companyId)
        if (appNote.entityNoteTypeId == verifiedNote.id) {
            return VERIFIED_AND_APPROVED_NOTE_CAN_NOT_BE_DELETED
        }
        return null
    }
}
