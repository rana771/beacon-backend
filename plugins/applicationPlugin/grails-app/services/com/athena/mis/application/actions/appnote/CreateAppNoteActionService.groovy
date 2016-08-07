package com.athena.mis.application.actions.appnote

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppNote
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.model.ListAppNoteActionServiceModel
import com.athena.mis.application.service.AppNoteService
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.ListAppNoteActionServiceModelService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Create new AppNote object and show in grid
 *  For details go through Use-Case doc named 'CreateAppNoteActionService'
 */
class CreateAppNoteActionService extends BaseService implements ActionServiceIntf {

    AppSystemEntityCacheService appSystemEntityCacheService
    AppNoteService appNoteService
    ListAppNoteActionServiceModelService listAppNoteActionServiceModelService

    private Logger log = Logger.getLogger(getClass())

    private static final String SAVE_SUCCESS_MESSAGE = "Note has been successfully saved"

    /**
     * 1. check required parameters
     * 2. build AppNote object to create with parameters
     * @param params -serialized parameters from UI
     * @return -a map containing AppNote object for execute method
     * map contains isError(true/false) depending on method success
     */
    public Map executePreCondition(Map result) {
        try {
            // check required parameters
            if ((!result.pluginId) || (!result.entityTypeId) || (!result.entityId)) {
                return super.setError(result, ERROR_FOR_INVALID_INPUT)
            }
            // build AppNote object to create
            AppNote appNote = buildAppNoteObject(result)
            result.put(ENTITY, appNote)
            result.put(IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Save AppNote object in DB
     * @param result -returned from executePreCondition method
     * @return -newly created AppNote object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppNote appNote = (AppNote) result.get(ENTITY)
            AppNote newAppNote = (AppNote) appNoteService.create(appNote)
            result.put(ENTITY, newAppNote)
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
     * @param result -newly created AppNote object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map buildSuccessResultForUI(Map result) {
        try {
            AppNote appNote = (AppNote) result.get(ENTITY)
            ListAppNoteActionServiceModel object = listAppNoteActionServiceModelService.read(appNote.id)
            result.put(ENTITY, object)
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
     * Build AppNote object to save in DB
     * @param parameterMap -serialized parameters from UI
     * @return -AppNote object
     */
    private AppNote buildAppNoteObject(Map parameterMap) {
        AppNote appNote = new AppNote(parameterMap)
        AppUser appUser = super.getAppUser()
        SystemEntity noteType = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_NOTE_NONE, appSystemEntityCacheService.SYS_ENTITY_TYPE_NOTE, appUser.companyId)
        appNote.createdBy = appUser.id
        appNote.entityNoteTypeId = noteType.id
        appNote.createdOn = new Date()
        appNote.companyId = appUser.companyId
        return appNote
    }
}
