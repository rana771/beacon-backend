package com.athena.mis.application.actions.company

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.*
import com.athena.mis.application.model.ListCompanyActionServiceModel
import com.athena.mis.application.service.*
import org.apache.log4j.Logger
import org.imgscalr.Scalr
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.commons.CommonsMultipartFile

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

/**
 *  Class to update company CRUD and to show on grid list
 *  For details go through Use-Case doc named 'UpdateCompanyActionService'
 */
class UpdateCompanyActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String COMPANY = "company"
    private static final String COMPANY_UPDATE_SUCCESS_MESSAGE = "Company has been updated successfully"
    private static final String COMPANY_ALREADY_EXISTS = "Same company name already exist"
    private static final String CODE_ALREADY_EXISTS = "Same company code already exists"
    private static final String URL_ALREADY_EXISTS = "Same company url already exists"
    private static final String EXISTING_LOGO = "ExistingLogo"
    private static final String EXISTING_SMALL_LOGO = "ExistingSmallLogo"
    private static final String COUNTRY_NOT_EXIST = "Country does not exist"
    private static final String IMAGE_FILE = "imageFile"
    private static final String SMALL_IMAGE_FILE = "smallImageFile"
    private static final String NEW_COMPANY_LOGO = "newCompanyLogo"
    private static final String NEW_COMPANY_SMALL_LOGO = "newCompanySmallLogo"
    //constants for image validations
    public static final String UNRECOGNIZED_IMAGE = "Unrecognized image file"
    public static final String UNRECOGNIZED_FILE = "Unrecognized file"
    public static final String SELECT_ONLY = "Select only "
    public static final String TYPE = " type for "
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

    CompanyService companyService
    AppAttachmentService appAttachmentService
    AppCountryService appCountryService
    ContentCategoryService contentCategoryService
    ListCompanyActionServiceModelService listCompanyActionServiceModelService
    AppSystemEntityCacheService appSystemEntityCacheService

    /**
     * Check different criteria for updating company object
     *      1) Check existence of country
     *      2) Check duplicate company name and code
     *      3) Validate company logo (if given)
     * @param params -parameter send from UI
     * @param obj -N/A
     * @return -a map containing company object and companyLogo(if given) for execute method
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            if ((!params.id) || (!params.version)) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long id = Long.parseLong(params.id.toString())
            Company oldCompany = (Company) companyService.read(id)
            if (!oldCompany) {
                //check existence of object
                return super.setError(params, COUNTRY_NOT_EXIST)
            }
            //build company object
            AppUser sessionUser = super.getAppUser()
            Company company = (Company) getCompany(params, oldCompany, sessionUser)
            // check uniqueness
            String msg = checkUniqueness(company)
            if (msg) {
                return super.setError(params, msg)
            }
            // validate company logo(if given)
            CommonsMultipartFile imageFile = params.companyLogo ? params.companyLogo : null
            // pull system entity type(Company) object
            String validationMsg = buildCompanyLogo(imageFile, params, oldCompany)
            if (validationMsg) {
                return super.setError(params, validationMsg)
            }
            CommonsMultipartFile smallImageFile = params.companySmallLogo ? params.companySmallLogo : null
            // pull system entity type(Company) object
            validationMsg = buildSmallCompanyLogo(smallImageFile, params, oldCompany)
            if (validationMsg) {
                return super.setError(params, validationMsg)
            }
            params.put(COMPANY, company)
            params.put(IMAGE_FILE, imageFile)
            params.put(SMALL_IMAGE_FILE, smallImageFile)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Update company object and companyLogo(if given)
     * @param parameters -N/A
     * @param result -companyObject & companyLogo(if given) from executePreCondition method
     * @return -a map contains company object for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional
    public Map execute(Map result) {
        try {
            Company company = (Company) result.get(COMPANY)
            //Save company in DB
            companyService.update(company)
            createOrUpdateAppAttachmentForImageFile(result)
            createOrUpdateAppAttachmentForSmallImageFile(result)
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
     * Wrap updated company to show on grid
     * @param obj -Company object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map buildSuccessResultForUI(Map result) {
        try {
            Map successResultMap = new LinkedHashMap()
            Company company = (Company) result.get(COMPANY)
            ListCompanyActionServiceModel companyModel = listCompanyActionServiceModelService.read(company.id)
            successResultMap.put(COMPANY, companyModel)
            return super.setSuccess(successResultMap, COMPANY_UPDATE_SUCCESS_MESSAGE)
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
        Map failureResultMap = new LinkedHashMap()
        return super.setError(failureResultMap, result.get(MESSAGE).toString())
    }

    /**
     * build companyObject to update
     * @param params -GrailsParameterMap
     * @param oldCompany -Company object
     * @param sessionUser -object of AppUser (logged in user)
     * @return -Company object
     */
    private Company getCompany(Map params, Company oldCompany, AppUser sessionUser) {
        Company company = new Company(params)
        AppCountry country = appCountryService.read(company.countryId)
        oldCompany.currencyId = country.currencyId
        oldCompany.name = company.name
        oldCompany.title = company.title
        oldCompany.code = company.code
        oldCompany.webUrl = company.webUrl
        oldCompany.address1 = company.address1
        oldCompany.address2 = company.address2
        oldCompany.countryId = company.countryId
        oldCompany.updatedOn = new Date()
        oldCompany.updatedBy = sessionUser.id
        oldCompany.contactName = company.contactName
        oldCompany.contactSurname = company.contactSurname
        oldCompany.contactPhone = company.contactPhone
        return oldCompany
    }

    /**
     * 1. ensure unique company name
     * 2. ensure unique company code
     * 3. ensure unique company web url
     * @param company -object of Company
     * @return -a string containing error message or null value
     */
    private String checkUniqueness(Company company) {
        // check unique Company Name
        int duplicateNameCount = companyService.countByNameIlikeAndIdNotEqual(company.name, company.id)
        if (duplicateNameCount > 0) {
            return COMPANY_ALREADY_EXISTS
        }
        // check unique company code
        int duplicateCodeCount = companyService.countByCodeIlikeAndIdNotEqual(company.code, company.id)
        if (duplicateCodeCount > 0) {
            return CODE_ALREADY_EXISTS
        }
        // check unique company webUrl
        int urlCount = companyService.countByWebUrlIlikeAndIdNotEqual(company.webUrl, company.id)
        if (urlCount > 0) {
            return URL_ALREADY_EXISTS
        }
        return null
    }

    /**
     * build companyLogo(AppAttachment) object
     * @param logoImage -byte array of companyLogo
     * @param imageFile -CommonsMultipartFile of companyLogo
     * @param existingCompanyLogo -AppAttachment
     * @param sessionUser -object of AppUser (logged in user)
     * @return -AppAttachment object
     */
    private AppAttachment buildCompanyLogoObjectForUpdate(byte[] logoImage, CommonsMultipartFile imageFile, AppAttachment existingCompanyLogo, AppUser sessionUser) {
        AppAttachment newCompanyLogo = new AppAttachment()

        newCompanyLogo.id = existingCompanyLogo.id
        newCompanyLogo.version = existingCompanyLogo.version
        newCompanyLogo.contentCategoryId = existingCompanyLogo.contentCategoryId
        newCompanyLogo.contentTypeId = existingCompanyLogo.contentTypeId
        newCompanyLogo.entityTypeId = existingCompanyLogo.entityTypeId
        newCompanyLogo.entityId = existingCompanyLogo.entityId
        newCompanyLogo.companyId = existingCompanyLogo.companyId
        newCompanyLogo.content = logoImage
        newCompanyLogo.caption = existingCompanyLogo.caption
        newCompanyLogo.fileName = imageFile.properties.originalFilename.toString()
        //Get extension of uploaded imageFile(companyLogo) and set extension
        newCompanyLogo.extension = getImageExtension(imageFile)
        newCompanyLogo.createdBy = existingCompanyLogo.createdBy
        newCompanyLogo.createdOn = existingCompanyLogo.createdOn
        newCompanyLogo.updatedBy = sessionUser.id
        newCompanyLogo.updatedOn = new Date()
        return newCompanyLogo
    }

    /**
     * build companyLogo(AppAttachment) object
     * @param logoImage -byte array of companyLogo
     * @param contentCategory -ContentCategory
     * @param imageFile -CommonsMultipartFile of companyLogo
     * @param company -object of Company
     * @param entityTypeId -SystemEntity.id (entity type - company)
     * @param sessionUser -object of AppUser (logged in user)
     * @return -AppAttachment object
     */
    private AppAttachment buildCompanyLogoObjectForCreate(byte[] logoImage, ContentCategory contentCategory, CommonsMultipartFile imageFile, Company company, long entityTypeId, AppUser sessionUser) {
        AppAttachment appAttachment = new AppAttachment()

        appAttachment.contentCategoryId = contentCategory.id
        appAttachment.contentTypeId = contentCategory.contentTypeId
        appAttachment.entityTypeId = entityTypeId
        appAttachment.entityId = company.id  //Set appAttachment.entityId=company.Id
        appAttachment.content = logoImage
        appAttachment.caption = EMPTY_SPACE
        appAttachment.fileName = imageFile.properties.originalFilename.toString()
        //Get extension of uploaded imageFile(companyLogo) and set extension
        appAttachment.extension = getImageExtension(imageFile)
        appAttachment.createdBy = sessionUser.id
        appAttachment.createdOn = new Date()
        appAttachment.updatedBy = 0L
        appAttachment.companyId = sessionUser.companyId
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

    private String buildCompanyLogo(CommonsMultipartFile imageFile, Map params, Company oldCompany) {
        long companyId = super.getCompanyId()
        AppUser sessionUser = super.getAppUser()

        SystemEntity contentEntityTypeCompany = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_CONTENT_ENTITY_COMPANY, appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT_ENTITY, companyId)

        if (imageFile && !imageFile.isEmpty()) {
            ContentCategory contentCategory = contentCategoryService.readBySystemContentCategory(contentCategoryService.IMAGE_TYPE_LOGO)

            long entityTypeId = contentEntityTypeCompany.id
            AppAttachment existingCompanyLogo = appAttachmentService.findByEntityTypeIdAndEntityIdAndContentTypeIdAndContentCategoryId(entityTypeId, companyId, contentCategory.contentTypeId, contentCategory.id)
            params.put(EXISTING_LOGO, existingCompanyLogo)

            //Validate companyLogo(e.g: check if image extension is valid or not)
            String validateImageMsg = validateContent(imageFile, contentCategory)
            if (validateImageMsg) {
                return validateImageMsg
            }

            // resize image and convert to bytes
            byte[] byteImage = resizeImage(imageFile, contentCategory)

            if (!existingCompanyLogo) { //if logo (AppAttachment) not found then create
                // Build AppAttachment object
                AppAttachment companyLogo = buildCompanyLogoObjectForCreate(byteImage, contentCategory, imageFile, oldCompany, entityTypeId, sessionUser)
                params.put(NEW_COMPANY_LOGO, companyLogo)
            } else {
                // Build AppAttachment object
                AppAttachment newCompanyLogo = buildCompanyLogoObjectForUpdate(byteImage, imageFile, existingCompanyLogo, sessionUser)
                params.put(NEW_COMPANY_LOGO, newCompanyLogo)
            }
        }
        return null
    }

    private String buildSmallCompanyLogo(CommonsMultipartFile smallImageFile, Map params, Company oldCompany) {
        AppUser sessionUser = super.getAppUser()

        long companyId = super.getCompanyId()
        SystemEntity contentEntityTypeCompany = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_CONTENT_ENTITY_COMPANY, appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT_ENTITY, companyId)

        if (smallImageFile && !smallImageFile.isEmpty()) {
            ContentCategory contentCategory = contentCategoryService.readBySystemContentCategory(contentCategoryService.IMAGE_TYPE_LOGO_SMALL)

            long entityTypeId = contentEntityTypeCompany.id
            AppAttachment existingCompanyLogo = appAttachmentService.findByEntityTypeIdAndEntityIdAndContentTypeIdAndContentCategoryId(entityTypeId, companyId, contentCategory.contentTypeId, contentCategory.id)
            params.put(EXISTING_SMALL_LOGO, existingCompanyLogo)

            //Validate companyLogo(e.g: check if image extension is valid or not)
            String validateImageMsg = validateContent(smallImageFile, contentCategory)
            if (validateImageMsg) {
                return validateImageMsg
            }

            // resize image and convert to bytes
            byte[] byteImage = resizeImage(smallImageFile, contentCategory)

            if (!existingCompanyLogo) { //if logo (AppAttachment) not found then create
                // Build AppAttachment object
                AppAttachment companyLogo = buildCompanyLogoObjectForCreate(byteImage, contentCategory, smallImageFile, oldCompany, entityTypeId, sessionUser)
                params.put(NEW_COMPANY_SMALL_LOGO, companyLogo)
            } else {
                // Build AppAttachment object
                AppAttachment newCompanyLogo = buildCompanyLogoObjectForUpdate(byteImage, smallImageFile, existingCompanyLogo, sessionUser)
                params.put(NEW_COMPANY_SMALL_LOGO, newCompanyLogo)
            }
        }
        return null
    }

    private void createOrUpdateAppAttachmentForImageFile(Map result) {
        //if new companyLogo is given then update companyLogo in AppAttachment domain; replace previous image
        CommonsMultipartFile imageFile = (CommonsMultipartFile) result.get(IMAGE_FILE)
        if (imageFile && !imageFile.isEmpty()) {
            AppAttachment newCompanyLogo = (AppAttachment) result.get(NEW_COMPANY_LOGO)
            AppAttachment existingCompanyLogo = (AppAttachment) result.get(EXISTING_LOGO)
            if (!existingCompanyLogo) {
                appAttachmentService.create(newCompanyLogo)
            } else {
                appAttachmentService.updateWithContent(newCompanyLogo)
            }
        }
    }

    private void createOrUpdateAppAttachmentForSmallImageFile(Map result) {
        CommonsMultipartFile smallImageFile = (CommonsMultipartFile) result.get(SMALL_IMAGE_FILE)
        if (smallImageFile && !smallImageFile.isEmpty()) {
            AppAttachment newCompanyLogo = (AppAttachment) result.get(NEW_COMPANY_SMALL_LOGO)
            AppAttachment existingCompanyLogo = (AppAttachment) result.get(EXISTING_SMALL_LOGO)
            if (!existingCompanyLogo) {
                appAttachmentService.create(newCompanyLogo)
            } else {
                appAttachmentService.updateWithContent(newCompanyLogo)
            }
        }
    }
}