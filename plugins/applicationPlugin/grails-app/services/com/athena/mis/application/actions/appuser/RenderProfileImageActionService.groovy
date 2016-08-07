package com.athena.mis.application.actions.appuser

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.config.AppConfigurationService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppAttachmentService
import com.athena.mis.application.service.AppSystemEntityCacheService
import grails.transaction.Transactional
import org.apache.log4j.Logger

class RenderProfileImageActionService extends BaseService implements ActionServiceIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String STREAM = "stream"

    AppAttachmentService appAttachmentService
    AppSystemEntityCacheService appSystemEntityCacheService
    AppConfigurationService appConfigurationService

    public Map executePreCondition(Map params) {
        return params
    }

    @Transactional(readOnly = true)
    public Map execute(Map result) {
        try {
            AppUser sessionUser = super.getAppUser()
            SystemEntity contentEntityTypeAppUser = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_CONTENT_ENTITY_APP_USER, appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT_ENTITY, sessionUser.companyId)
            InputStream appAttachment = appAttachmentService.readContentByTypeAndEntity(contentEntityTypeAppUser.id, sessionUser.id, sessionUser.companyId)
            if (!appAttachment) {
                result.put(STREAM, getNotFoundImage())
                return result
            }
            result.put(STREAM, appAttachment)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Get image for placeholder
     */
    private byte[] getNotFoundImage() {
        String imgLoc = appConfigurationService.getAppImageDir() + "/user-no-image.jpg"
        File imgFile = new File(imgLoc)
        return imgFile.bytes
    }

    public Map executePostCondition(Map result) {
        return result
    }

    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    public Map buildFailureResultForUI(Map result) {
        return result
    }
}
