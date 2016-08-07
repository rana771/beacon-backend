package com.athena.mis.application.actions.appcompanyuser

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
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
 *  Create new company user(appUser) object and show in grid
 *  For details go through Use-Case doc named 'CreateAppCompanyUserActionService'
 */
class CreateAppCompanyUserActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String APP_USER = "appUser"
    private static
    final String APP_USER_SAVE_SUCCESS_MESSAGE = "Company user has been saved successfully.Now active your Company for Log-In"
    private static final String IMAGE_FILE = "imageFile"
    private static final String SIGNATURE_IMAGE = "signatureImage"
    private static final String HAS_USER = "The selected company already has a company user"
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

    AppSystemEntityCacheService appSystemEntityCacheService
    AppAttachmentService appAttachmentService
    AppUserService appUserService
    RoleService roleService
    ContentCategoryService contentCategoryService
    SpringSecurityService springSecurityService
    ListAppCompanyUserActionServiceModelService listAppCompanyUserActionServiceModelService

    /**
     * Check pre conditions before creating company user
     *      1. check if the company already has a company user or not
     *      2. get appUser object and validate the object
     * If user has signature image then
     *      1. validate and resize image file
     *      2. convert image to bytes and build AppAttachment object with image file
     * @param params -serialized params from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check if the company already has a company user or not
            long companyId = Long.parseLong(params.companyId.toString())
            int companyUser = appUserService.countByIsCompanyUserAndCompanyId(companyId)
            if (companyUser > 0) {
                return super.setError(params, HAS_USER)
            }
            // build appUser object
            AppUser sessionUser = super.getAppUser()   // get logged in user
            AppUser appUser = buildAppUser(params, sessionUser)
            int count = appUserService.countByLoginIdIlike(appUser.loginId)
            if (count > 0) {
                return super.setError(params, NOT_AVAILABLE)
            }

            if(appUser.email){
                int countEmail = appUserService.countByEmailIlikeAndCompanyId(appUser.email, appUser.companyId)
                if (countEmail > 0) {
                    return super.setError(params, EMAIL_EXIST_MSG)
                }
            }
            // check if user has signature image or not
            CommonsMultipartFile imageFile = params.signatureImage ? params.signatureImage : null
            // if user has signature image then validate image file
            if (imageFile && (!imageFile.isEmpty())) {
                appUser.hasSignature = true
                // get ContentCategory object for signature of AppUser
                ContentCategory contentCategory = contentCategoryService.readBySystemContentCategory(contentCategoryService.IMAGE_TYPE_SIGNATURE)
                String validateImageMsg = validateContent(imageFile, contentCategory)
                if (validateImageMsg) {
                    return super.setError(params, validateImageMsg)
                }
                // resize image and convert to bytes
                byte[] byteImage = resizeImage(imageFile, contentCategory)
                // build AppAttachment object with image file
                AppAttachment signatureImage = buildEntityImageObject(byteImage, contentCategory, imageFile, sessionUser)
                params.put(SIGNATURE_IMAGE, signatureImage)
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
     * Save company user(appUser) object in DB
     * Save signature image (AppAttachment object) in DB
     * Assign admin & development role for the created company user
     * This method is in transactional block and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppUser appUser = (AppUser) result.get(APP_USER)
            appUserService.create(appUser)   // save new company user(appUser) object in DB
            CommonsMultipartFile imageFile = (CommonsMultipartFile) result.get(IMAGE_FILE)
            if (imageFile && (!imageFile.isEmpty())) {
                AppAttachment signatureImage = (AppAttachment) result.get(SIGNATURE_IMAGE)
                signatureImage.entityId = appUser.id   // set appUserId as entityId
                appAttachmentService.create(signatureImage) // save signature image (AppAttachment object) in DB
            }

            // admin and development role assign for the created company user
            assignAdminAndDevelopmentRoleForCompanyUser(appUser)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
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
            return super.setSuccess(successResultMap, APP_USER_SAVE_SUCCESS_MESSAGE)
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     * @param result -map returned from previous methods
     * @return - new map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        Map failureResultMap = new LinkedHashMap()
        return super.setError(failureResultMap, result.get(MESSAGE).toString())
    }

    /**
     * Build AppUser object with parameters
     * @param params -serialized parameters from UI
     * @param sessionUser -object of AppUser
     * @return -object of AppUser
     */
    private AppUser buildAppUser(Map params, AppUser sessionUser) {
        if (params.employeeId.equals(EMPTY_SPACE)) {
            params.employeeId = 0L
        }
        AppUser appUser = new AppUser(params)

        if(isValidEmailAddress(params.loginId) && (!params.email)){
            int countEmail = appUserService.countByEmailIlikeAndCompanyId(appUser.loginId, appUser.companyId)
            if (countEmail == 0) {
                appUser.email = params.loginId
            }
        }

        appUser.password = springSecurityService.encodePassword(params.password)
        appUser.nextExpireDate = new Date() + DateUtility.DATE_RANGE_HUNDRED_EIGHTY
        // default value for new company user
        appUser.isCompanyUser = true    // hard coded true for this use-case only
        appUser.isPowerUser = true      // hard coded true for this use-case only
        appUser.isConfigManager = true  // hard coded true for this use-case only
        appUser.isSystemUser = false    // hard coded false for crud operation (set true only by script)
        appUser.isDisablePasswordExpiration = true  // hard coded true for this use-case only
        appUser.createdOn = new Date()
        appUser.createdBy = sessionUser.id
        return appUser
    }

    /**
     * Build AppAttachment object with signature image file
     * @param signImage -signature image in byte form
     * @param contentCategory -object of ContentCategory
     * @param imageFile -signature image file
     * @param sessionUser -object of AppUser
     * @return -AppAttachment object
     */
    private AppAttachment buildEntityImageObject(byte[] signImage, ContentCategory contentCategory, CommonsMultipartFile imageFile, AppUser sessionUser) {
        // pull system entity type(AppUser) object
        long companyId = sessionUser.companyId
        SystemEntity contentEntityTypeAppUser = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_CONTENT_ENTITY_APP_USER, appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT_ENTITY, companyId)

        AppAttachment appAttachment = new AppAttachment()
        appAttachment.contentCategoryId = contentCategory.id
        appAttachment.contentTypeId = contentCategory.contentTypeId
        appAttachment.entityTypeId = contentEntityTypeAppUser.id
        appAttachment.entityId = 0L  // set appAttachment.entityId = appUser.Id after creating new AppUser
        appAttachment.content = signImage
        appAttachment.caption = null
        appAttachment.extension = getImageExtension(imageFile)  // get extension of image file
        appAttachment.createdBy = sessionUser.id
        appAttachment.createdOn = new Date()
        appAttachment.updatedBy = 0L
        appAttachment.updatedOn = null
        appAttachment.companyId = companyId
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

    private static final String INSERT_QUERY = """
        INSERT INTO user_role(role_id,user_id)
        VALUES(:roleId,:userId);
    """

    /**
     * Assign admin role for the created company user
     * @param appUser -object of AppUser
     * @return -boolean value true
     */
    private boolean assignAdminAndDevelopmentRoleForCompanyUser(AppUser appUser) {
        Role role = roleService.findByCompanyIdAndRoleTypeId(appUser.companyId, ReservedRoleService.ROLE_TYPE_APP_ADMIN)
        Map queryParams = [
                userId: appUser.id,
                roleId: role.id
        ]
        executeInsertSql(INSERT_QUERY, queryParams)
        role = roleService.findByCompanyIdAndRoleTypeId(appUser.companyId, ReservedRoleService.ROLE_TYPE_APP_DEVELOPMENT)
        queryParams = [
                userId: appUser.id,
                roleId: role.id
        ]
        executeInsertSql(INSERT_QUERY, queryParams)
        return true
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
