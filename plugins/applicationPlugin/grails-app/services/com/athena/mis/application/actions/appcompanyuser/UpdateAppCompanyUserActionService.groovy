package com.athena.mis.application.actions.appcompanyuser

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.config.AppConfigurationService
import com.athena.mis.application.entity.*
import com.athena.mis.application.model.ListAppCompanyUserActionServiceModel
import com.athena.mis.application.service.*
import com.athena.mis.utility.DateUtility
import grails.plugin.springsecurity.SpringSecurityService
import org.apache.log4j.Logger
import org.imgscalr.Scalr
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.commons.CommonsMultipartFile

import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 *  Update company user(appUser) object and grid data
 *  For details go through Use-Case doc named 'UpdateAppCompanyUserActionService'
 */
class UpdateAppCompanyUserActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String APP_USER = "appUser"
    private static final String APP_USER_UPDATE_SUCCESS_MESSAGE = "Company user has been updated successfully"
    private static final String APP_USER_NOT_FOUND = "Company user not found or might has been changed"
    private static final String IMAGE_FILE = "imageFile"
    private static final String NEW_SIGNATURE_IMAGE = "newSignatureImage"
    private static final String EXISTING_SIGNATURE_IMAGE = "existingSignatureImage"
    private static final String NOT_AVAILABLE = "Given login id is not available, try another one"
    private static final String EMAIL_EXIST_MSG = "Email ID already exist."
    //constants for image validations
    private static final String UNRECOGNIZED_IMAGE = "Unrecognized image file"
    private static final String UNRECOGNIZED_FILE = "Unrecognized file"
    private static final String SELECT_ONLY = "Select only "
    private static final String TYPE = " type for "
    //constants for image validations
    private static final String MB = " MB"
    private static final String KB = " KB"
    private static final String BYTES = " bytes"
    private static final String PNG_EXTENSION = "png"
    private static final String JPG_EXTENSION = "jpg"
    private static final String BMP_EXTENSION = "bmp"
    private static final String JPEG_EXTENSION = "jpeg"
    private static final String GIF_EXTENSION = "gif"
    private static final String FILE_SIZE_MAX_OF = " size can be maximum of "
    private static
    final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})\$";

    AppAttachmentService appAttachmentService
    AppUserService appUserService
    CompanyService companyService
    SpringSecurityService springSecurityService
    ContentCategoryService contentCategoryService
    AppConfigurationService appConfigurationService
    ListAppCompanyUserActionServiceModelService listAppCompanyUserActionServiceModelService
    AppSystemEntityCacheService appSystemEntityCacheService

    /**
     * Check pre conditions before updating company user
     *      get appUser object and validate the object
     * Get existing company user(appUser) from cache utility by appUser.id
     * Keep the previous signature image if not newly updated
     * If signature image is updated then
     *      1. validate and resize image file
     *      2. convert image to bytes
     *      3. if user already has signature image then update existing image
     *      4. or build new AppAttachment object with image file
     * @param params -serialized params from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            if ((!params.id) || (!params.version)) {
                return setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long id = Long.parseLong(params.id.toString())
            int version = Integer.parseInt(params.version.toString())
            // get existing company user(appUser) from cache utility by appUser.id
            AppUser existingAppUser = appUserService.read(id)
            // check if company user(appUser) exists or not
            if ((!existingAppUser) || (existingAppUser.version != version)) {
                return super.setError(params, APP_USER_NOT_FOUND)
            }
            // build appUser object
            AppUser sessionUser = super.getAppUser()   // get logged in user
            AppUser appUser = buildAppUser(params, existingAppUser, sessionUser)
            int count = appUserService.countByLoginIdIlikeAndIdNotEqual(appUser.loginId, appUser.id)
            if (count > 0) {
                return super.setError(params, NOT_AVAILABLE)
            }

            if(params.email){
                int countEmail = appUserService.countByEmailIlikeAndCompanyIdAndIdNotEqual(params.email, existingAppUser.companyId, existingAppUser.id)
                if (countEmail > 0) {
                    return super.setError(params, EMAIL_EXIST_MSG)
                }
            }

            // check if signature image is updated or not
            CommonsMultipartFile imageFile = params.signatureImage ? params.signatureImage : null

            /*  keep the previous signature image if not newly updated
               if signature image is updated then validate image file */
            if (imageFile && (!imageFile.isEmpty())) {
                appUser.hasSignature = true
                ContentCategory contentCategory = contentCategoryService.readBySystemContentCategory(contentCategoryService.IMAGE_TYPE_SIGNATURE)
                String validateImageMsg = validateContent(imageFile, contentCategory)
                if (validateImageMsg) {
                    return super.setError(params, validateImageMsg)
                }
                // resize image and convert to bytes
                byte[] byteImage = resizeImage(imageFile, contentCategory)
                // pull system entity type(AppUser) object
                SystemEntity contentEntityTypeAppUser = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_CONTENT_ENTITY_APP_USER, appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT_ENTITY, sessionUser.companyId)

                long contentTypeId = contentCategory.contentTypeId
                long entityTypeId = contentEntityTypeAppUser.id
                // get previous signature image of user if exists
                AppAttachment existingSignImage = appAttachmentService.findByEntityTypeIdAndEntityIdAndContentTypeId(entityTypeId, existingAppUser.id, contentTypeId)
                if (existingSignImage) { // update existing AppAttachment(signature image) object
                    existingSignImage.content = byteImage
                    existingSignImage.extension = getImageExtension(imageFile)  // get extension of image file
                    existingSignImage.updatedBy = sessionUser.id
                    existingSignImage.updatedOn = new Date()
                    params.put(EXISTING_SIGNATURE_IMAGE, existingSignImage)
                } else { // build new AppAttachment object with image file
                    AppAttachment newSignImage = buildEntityImageObject(byteImage, contentCategory, imageFile, appUser, contentEntityTypeAppUser.id)
                    params.put(NEW_SIGNATURE_IMAGE, newSignImage)
                }
            }

            params.put(APP_USER, appUser)
            params.put(IMAGE_FILE, imageFile)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Update company user(appUser) object in DB
     * If signature image is updated then
     *      1. if user already has signature image then update existing image
     *      2. or create new AppAttachment object in DB with image file
     * This method is in transactional block and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppUser appUser = (AppUser) result.get(APP_USER)
            CommonsMultipartFile imageFile = (CommonsMultipartFile) result.get(IMAGE_FILE)
            appUserService.update(appUser)  // update company user(appUser) object in DB
            // check if signature image is updated or not
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
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * There is no postCondition, so return the same map as received
     *
     * @param result - resulting map from execute
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * 1. get newly created company user(appUser) object in grid
     * 2. put success message
     * @param result -map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map buildSuccessResultForUI(Map result) {
        try {
            AppUser appUser = (AppUser) result.get(APP_USER)
            ListAppCompanyUserActionServiceModel appUserModel = listAppCompanyUserActionServiceModelService.read(appUser.id)
            Map successResultMap = new LinkedHashMap()
            successResultMap.put(APP_USER, appUserModel)
            successResultMap.put(MESSAGE, APP_USER_UPDATE_SUCCESS_MESSAGE)
            return successResultMap
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     * @param result -map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        Map failureResultMap = new LinkedHashMap()
        return super.setError(failureResultMap, result.get(MESSAGE).toString())
    }

    /**
     * Build AppUser object with parameters
     * @param params -serialized parameters from UI
     * @param existingAppUser -old AppUser object
     * @param sessionUser -logged in user
     * @return -object of AppUser
     */
    private AppUser buildAppUser(Map params, AppUser existingAppUser, AppUser sessionUser) {
        if (params.employeeId.equals(EMPTY_SPACE)) {
            params.employeeId = 0L
        }
        AppUser appUser = new AppUser(params)
        if ((params.password.length() < 1) && params.existingPass.length() > 1) {  // While updating Password is Blank
            appUser.password = params.existingPass
        } else {
            appUser.password = springSecurityService.encodePassword(params.password)
        }

        if (params.id) {
            appUser.id = Long.parseLong(params.id)
            appUser.version = Integer.parseInt(params.version.toString())
        }

        //  although nextExpireDate is set here, it is only effective while create, this value will not reset in Update
        if (params.companyId) {
            appUser.companyId = Long.parseLong(params.companyId)
            appUser.nextExpireDate = new Date() + DateUtility.DATE_RANGE_HUNDRED_EIGHTY
            // default value for new company user
        } else {
            appUser.companyId = sessionUser.companyId
            SysConfiguration sysConfig = (SysConfiguration) appConfigurationService.appSysConfigCacheService.readByKeyAndCompanyId(appConfigurationService.appSysConfigCacheService.DEFAULT_PASSWORD_EXPIRE_DURATION, sessionUser.companyId)
            if (sysConfig) {
                appUser.nextExpireDate = new Date() + Integer.parseInt(sysConfig.value)
            } else {
                appUser.nextExpireDate = new Date()
            }
        }

        if(isValidEmailAddress(params.loginId) && (!params.email)){
            int countEmail = appUserService.countByEmailIlikeAndCompanyId(appUser.loginId, existingAppUser.companyId)
            if (countEmail == 0) {
                appUser.email = params.loginId
            }
        }
        appUser.companyId = existingAppUser.companyId
        appUser.hasSignature = existingAppUser.hasSignature
        appUser.updatedBy = sessionUser.id
        appUser.updatedOn = new Date()
        appUser.isPowerUser = existingAppUser.isPowerUser
        appUser.isConfigManager = existingAppUser.isConfigManager
        appUser.isDisablePasswordExpiration = existingAppUser.isDisablePasswordExpiration
        return appUser
    }

    /**
     * Build AppAttachment object with signature image file
     * @param signImage -signature image in byte form
     * @param contentCategory -object of ContentCategory
     * @param imageFile -signature image file
     * @param appUser -object of AppUser
     * @return -AppAttachment object
     */
    private AppAttachment buildEntityImageObject(byte[] signImage, ContentCategory contentCategory, CommonsMultipartFile imageFile,
                                                 AppUser appUser, long contentAppUserTypeId) {
        AppAttachment appAttachment = new AppAttachment()

        appAttachment.contentCategoryId = contentCategory.id
        appAttachment.contentTypeId = contentCategory.contentTypeId
        appAttachment.entityTypeId = contentAppUserTypeId
        appAttachment.entityId = appUser.id
        appAttachment.companyId = appUser.companyId
        appAttachment.content = signImage
        appAttachment.caption = null
        appAttachment.extension = getImageExtension(imageFile)  // get extension of image file
        appAttachment.createdBy = appUser.id
        appAttachment.createdOn = new Date()
        appAttachment.updatedBy = 0L
        appAttachment.updatedOn = null
        return appAttachment
    }

    // Validating uploaded image type, extension, size etc.
    private String validateContent(CommonsMultipartFile contentFile, ContentCategory contentCategory) {
        SystemEntity contentTypeImage = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_CONTENT_IMAGE, appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT, contentCategory.companyId)
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

    private static boolean isValidEmailAddress(String email) {
        boolean result = false;
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            result = true;
        }
        return result;
    }
}
