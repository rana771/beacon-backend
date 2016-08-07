package com.athena.mis.application.actions.appuser

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.config.AppSysConfigCacheService
import com.athena.mis.application.entity.AppAttachment
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.ContentCategory
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppAttachmentService
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.AppUserService
import com.athena.mis.application.service.ContentCategoryService
import grails.plugin.springsecurity.SpringSecurityService
import org.apache.log4j.Logger
import org.imgscalr.Scalr
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.commons.CommonsMultipartFile

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

class UploadAppUserProfileImageActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String MB = " MB"
    private static final String KB = " KB"
    private static final String BYTES = " bytes"
    private static final String UNRECOGNIZED_FILE = "Unrecognized file."
    private static final String FILE_SIZE_MAX_OF = "Image size can be maximum of "
    private static final String PNG_EXTENSION = "png"
    private static final String JPG_EXTENSION = "jpg"
    private static final String JPEG_EXTENSION = "jpeg"
    private static final String GIF_EXTENSION = "gif"
    private static final String NOT_FOUND_MSG = 'User could not be found.'

    private static final String NEW_SIGNATURE_IMAGE = "newSignatureImage"
    private static final String EXISTING_SIGNATURE_IMAGE = "existingSignatureImage"
    private static final String UNRECOGNIZED_IMAGE = "Unrecognized image file"
    private static final String SELECT_ONLY = "Select only "
    private static final String TYPE = " type for "
    private static final String BMP_EXTENSION = "bmp"
    private static final String IMAGE_FILE = "imageFile"
    private static final String APP_USER = "appUser"
    private static final String UPLOAD_SUCCESS_MSG = "Profile Image upload successfully."

    AppSystemEntityCacheService appSystemEntityCacheService
    AppSysConfigCacheService appSysConfigCacheService
    AppAttachmentService appAttachmentService
    AppUserService appUserService
    ContentCategoryService contentCategoryService


    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            AppUser sessionUser = super.getAppUser()
            // check if user exists or not
            if (!sessionUser) {
                return super.setError(params, NOT_FOUND_MSG)
            }
            CommonsMultipartFile imageFile = params.signatureImage ? params.signatureImage : null
            // if signature image is updated then validate image file
            if (imageFile && (!imageFile.isEmpty())) {
                sessionUser.hasSignature = true
                ContentCategory contentCategory = contentCategoryService.readBySystemContentCategory(contentCategoryService.IMAGE_TYPE_SIGNATURE)
                String validateImageMsg = validateContent(imageFile, contentCategory)
                if (validateImageMsg) {
                    return super.setError(params, validateImageMsg)
                }
                // resize image and convert to bytes
//                byte[] byteImage = resizeImage(imageFile, contentCategory)
                byte[] byteImage =  imageFile.bytes
                // pull system entity type(AppUser) object
                SystemEntity contentEntityTypeAppUser = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_CONTENT_ENTITY_APP_USER, appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT_ENTITY, sessionUser.companyId)

                long contentTypeId = contentCategory.contentTypeId
                long entityTypeId = contentEntityTypeAppUser.id
                // get previous signature image of user if exists
                AppAttachment existingSignImage = appAttachmentService.findByEntityTypeIdAndEntityIdAndContentTypeId(entityTypeId, appUser.id, contentTypeId)
                if (existingSignImage) {    // update existing AppAttachment (signature image) object
                    existingSignImage.fileName = imageFile.properties.originalFilename.toString()
                    existingSignImage.content = byteImage
                    existingSignImage.extension = getImageExtension(imageFile)
                    existingSignImage.updatedBy = sessionUser.id
                    existingSignImage.updatedOn = new Date()
                    params.put(EXISTING_SIGNATURE_IMAGE, existingSignImage)
                } else {    // build new AppAttachment object with image file
                    AppAttachment newSignImage = getAppAttachment(byteImage, contentCategory, imageFile, sessionUser, contentEntityTypeAppUser.id)
                    params.put(NEW_SIGNATURE_IMAGE, newSignImage)
                }
            }

            params.put(APP_USER, sessionUser)
            params.put(IMAGE_FILE, imageFile)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    @Transactional
    public Map execute(Map result) {
        try {
            AppUser appUser = (AppUser) result.get(APP_USER)
            appUserService.updateProfileImage(appUser)   // update company user(appUser) object in DB
            CommonsMultipartFile imageFile = (CommonsMultipartFile) result.get(IMAGE_FILE)
            if (imageFile && (!imageFile.isEmpty())) {
                // get old signature image if exists
                AppAttachment existingSignatureImage = (AppAttachment) result.get(EXISTING_SIGNATURE_IMAGE)
                if (existingSignatureImage) {   // update existing signature image
                    appAttachmentService.updateWithContent(existingSignatureImage)
                } else {    // save new AppAttachment (signature image) object in DB
                    AppAttachment newSignatureImage = (AppAttachment) result.get(NEW_SIGNATURE_IMAGE)
                    appAttachmentService.create(newSignatureImage)
                }
            }
            Map executeResult = new LinkedHashMap<>()
            return super.setSuccess(executeResult, UPLOAD_SUCCESS_MSG)
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
     * Do nothing
     * @param result - resulting map from execute
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     * @param result - map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }


    private AppAttachment getAppAttachment(byte[] signImage, ContentCategory contentCategory, CommonsMultipartFile imageFile,
                                           AppUser appUser, long contentAppUserTypeId) {
        AppAttachment appAttachment = new AppAttachment()

        appAttachment.contentCategoryId = contentCategory.id
        appAttachment.contentTypeId = contentCategory.contentTypeId
        appAttachment.entityTypeId = contentAppUserTypeId
        appAttachment.entityId = appUser.id
        appAttachment.companyId = appUser.companyId
        appAttachment.content = signImage
        appAttachment.caption = EMPTY_SPACE
        appAttachment.fileName = imageFile.properties.originalFilename.toString()
        appAttachment.extension = getImageExtension(imageFile)  // get extension of image file
        appAttachment.createdBy = appUser.id
        appAttachment.createdOn = new Date()
        appAttachment.updatedBy = 0L
        return appAttachment
    }

    // Validating uploaded image type, extension, size etc.
    private String validateContent(CommonsMultipartFile contentFile, ContentCategory contentCategory) {
        long companyId = contentCategory.companyId
        SystemEntity contentTypeImage = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_CONTENT_IMAGE, appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT, companyId)
        String returnMsg = null
        long contentFileSize = contentFile.size.longValue()
        String uploadedFileExtension = null

        //Get the extension of the uploaded file
        if (contentCategory.contentTypeId == contentTypeImage.id) {
            uploadedFileExtension = getImageExtension(contentFile)
            if (!uploadedFileExtension) return UNRECOGNIZED_IMAGE
        } else {
            uploadedFileExtension = getContentExtension(contentFile)
            if (!uploadedFileExtension) return UNRECOGNIZED_FILE
        }

        if (contentCategory.extension) {
            boolean extensionMatched = false
            String validExtensions = contentCategory.extension
            for (String currentExtension : validExtensions.split(COMA)) {
                extensionMatched = currentExtension.trim().equalsIgnoreCase(uploadedFileExtension)
                if (extensionMatched) break
            }
            if (!extensionMatched) {
                returnMsg = SELECT_ONLY + contentCategory.extension + TYPE + contentCategory.name
                return returnMsg
            }
        }
        returnMsg = checkMaxSize(contentFileSize, contentCategory)
        return returnMsg
    }

    // Check if uploaded file exceeds content pre-defined size
    private String checkMaxSize(long contentFileSize, ContentCategory contentCategory) {
        String returnMsg = null
        if (contentFileSize <= contentCategory.maxSize) return returnMsg
        long kbSize = 1024L
        long mbSize = 1048576L  // 1024 * 1024
        if (contentCategory.maxSize < kbSize) {
            returnMsg = contentCategory.name + FILE_SIZE_MAX_OF + contentCategory.maxSize + BYTES
        } else if (contentCategory.maxSize < mbSize) {
            long inKb = (contentCategory.maxSize) / kbSize
            returnMsg = contentCategory.name + FILE_SIZE_MAX_OF + inKb + KB
        } else {
            long inMb = (contentCategory.maxSize) / mbSize
            returnMsg = contentCategory.name + FILE_SIZE_MAX_OF + inMb + MB
        }
        return returnMsg
    }

    private String getContentExtension(CommonsMultipartFile contentFile) {
        String contentFileName = contentFile.properties.originalFilename.toString()
        int i = contentFileName.lastIndexOf(SINGLE_DOT)
        String uploadedFileExtension = contentFileName.substring(i + 1)
        return uploadedFileExtension
    }

    // resize image and convert to bytes
    private byte[] resizeImage(CommonsMultipartFile imageFile, ContentCategory contentCategory) {
        if ((contentCategory.width <= 0) || (contentCategory.height <= 0)) {
            return imageFile.bytes        // return without resizing
        }
        BufferedImage tempImg = ImageIO.read(imageFile.getInputStream())
        BufferedImage scaledImg = Scalr.resize(tempImg, Scalr.Mode.FIT_EXACT, contentCategory.width, contentCategory.height)
        // change the image size
        ByteArrayOutputStream buffer = new ByteArrayOutputStream()
        String imageExt = getContentExtension(imageFile)
        ImageIO.write(scaledImg, imageExt, buffer)
        byte[] imageBytes = buffer.toByteArray()
        return imageBytes
    }

    private String getImageExtension(CommonsMultipartFile imageFile) {
        String imageFileName = imageFile.properties.originalFilename.toString()
        String temp = imageFileName.toLowerCase()
        String imageExtension = null

        if (temp.endsWith(PNG_EXTENSION))
            return imageExtension = PNG_EXTENSION
        if (temp.endsWith(GIF_EXTENSION))
            return imageExtension = GIF_EXTENSION
        if (temp.endsWith(BMP_EXTENSION))
            return imageExtension = BMP_EXTENSION
        if (temp.endsWith(JPEG_EXTENSION))
            return imageExtension = JPEG_EXTENSION
        if (temp.endsWith(JPG_EXTENSION))
            return imageExtension = JPG_EXTENSION
        return imageExtension
    }
}
