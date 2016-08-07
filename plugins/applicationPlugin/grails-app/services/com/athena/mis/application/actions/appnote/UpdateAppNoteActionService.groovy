package com.athena.mis.application.actions.appnote

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppNote
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.model.ListAppNoteActionServiceModel
import com.athena.mis.application.service.AppNoteService
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.ListAppNoteActionServiceModelService
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Update AppNote object in DB and grid data
 *  For details go through Use-Case doc named 'UpdateAppNoteActionService'
 */
class UpdateAppNoteActionService extends BaseService implements ActionServiceIntf {

    AppSystemEntityCacheService appSystemEntityCacheService
    AppNoteService appNoteService
    ListAppNoteActionServiceModelService listAppNoteActionServiceModelService

    @Autowired(required = false)
    ExchangeHousePluginConnector exchangeHouseImplService

    private Logger log = Logger.getLogger(getClass())

    private static final String UPDATE_SUCCESS_MESSAGE = "Note has been updated successfully"
    private static final String NOT_FOUND_MESSAGE = "Selected note not found"
    private static final String SYSTEM_NOTE_CAN_NOT_UPDATE = "System note can not be updated"
    private static final String APP_NOTE = "appNote"

    /**
     * 1. check required parameters
     * 2. pull old AppNote object by id
     * 3. check existence of old AppNote object
     * 4. build new AppNote object for update
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
            AppNote oldAppNote = appNoteService.read(id)
            // check existence of old AppNote object
            if (!oldAppNote) {
                return super.setError(result, NOT_FOUND_MESSAGE)
            }
            long version = Long.parseLong(result.version.toString())
            if(version!= oldAppNote.version){
                return super.setError(result, NOT_FOUND_MESSAGE)
            }
            if(exchangeHouseImplService){
                SystemEntity systemNote = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_NOTE_SYSTEM_NOTE, appSystemEntityCacheService.SYS_ENTITY_TYPE_NOTE, oldAppNote.companyId)
                if (oldAppNote.entityNoteTypeId == systemNote.id) {
                    return super.setError(result, SYSTEM_NOTE_CAN_NOT_UPDATE)
                }
            }
            AppNote appNote = buildAppNoteObject(result, oldAppNote)
            result.put(APP_NOTE, appNote)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Update AppNote object in DB
     * @param result -AppNote object send from executePreCondition method
     * @return -newly created AppNote object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppNote appNote = (AppNote) result.get(APP_NOTE)
            AppNote newAppNote = (AppNote)appNoteService.update(appNote)
            result.put(APP_NOTE, newAppNote)
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
     * @param result -updated AppNote object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map buildSuccessResultForUI(Map result) {
        try {
            AppNote appNote = (AppNote) result.get(APP_NOTE)
            ListAppNoteActionServiceModel object = listAppNoteActionServiceModelService.read(appNote.id)
            result.put(ENTITY, object)
            return super.setSuccess(result,UPDATE_SUCCESS_MESSAGE)
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
     * Build AppNote object to update in DB
     * @param parameterMap -serialized parameters from UI
     * @param oldAppNote -old AppNote object
     * @return -new AppNote object
     */
    private AppNote buildAppNoteObject(Map parameterMap, AppNote oldAppNote) {
        AppNote appNote = new AppNote(parameterMap)
        oldAppNote.note = appNote.note
        oldAppNote.updatedBy = super.getAppUser().id
        oldAppNote.updatedOn = new Date()
        return oldAppNote
    }
}
