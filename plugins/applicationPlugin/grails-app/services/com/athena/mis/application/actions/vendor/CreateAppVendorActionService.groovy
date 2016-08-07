package com.athena.mis.application.actions.vendor

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.entity.Vendor
import com.athena.mis.application.model.ListAppVendorActionServiceModel
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.ListAppVendorActionServiceModelService
import com.athena.mis.application.service.VendorService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.commons.CommonsMultipartFile

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

/**
 *  Class to update vendor CRUD and to show on grid list
 *  For details go through Use-Case doc named 'CreateAppVendorActionService'
 */
class CreateAppVendorActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String VENDOR = "vendor"
    private static final String CREATE_SUCCESS_MESSAGE = "Vendor has been created successfully"
    private static final String VENDOR_ALREADY_EXISTS = "Same vendor name already exist"
    private static final String SMALL_IMAGE = "smallImage"
    private static final String THUMB_IMAGE = "thumbImage"
    private static final String UNRECOGNIZED_SMALL_IMAGE = "Unrecognized small image file"
    private static final String UNRECOGNIZED_THUMB_IMAGE = "Unrecognized thumb image file"
    private static final String PNG_EXTENSION = "png"
    private static final String JPG_EXTENSION = "jpg"
    private static final String BMP_EXTENSION = "bmp"
    private static final String JPEG_EXTENSION = "jpeg"
    private static final String GIF_EXTENSION = "gif"
    private static final String DUPLICATE_ORDER_MSG = "Order already exist"
    private static final String MAX_FILE_SIZE_IMG = "Uploaded image exceeds the size limit. Max size limit 2 MB"
    private static final long TWO_MB_SIZE = 1048576L  // 1024 * 1024

    AppSystemEntityCacheService appSystemEntityCacheService
    VendorService vendorService
    ListAppVendorActionServiceModelService listAppVendorActionServiceModelService

    /**
     * Check different criteria for updating vendor object
     *      2) Check duplicate vendor name
     *      3) Validate vendor logo (if given)
     * @param params -parameter send from UI
     * @param obj -N/A
     * @return -a map containing vendor object for execute method
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            if ((!params.name) || (!params.dbTypeId) || (!params.sequence) || (!params.vendorId)) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long vendorId = Long.parseLong(params.vendorId.toString())
            long companyId = super.companyId
            SystemEntity sysVendor = appSystemEntityCacheService.read(vendorId, appSystemEntityCacheService.SYS_ENTITY_TYPE_VENDOR, companyId)
            // build vendor object
            Vendor vendor = getVendor(params, sysVendor.value)
            // check uniqueness
            String msg = checkUniqueness(vendor)
            if (msg) {
                return super.setError(params, msg)
            }
            // validate vendor logo(if given)
            CommonsMultipartFile smallImage = params.smallImage ? params.smallImage : null
            CommonsMultipartFile thumbImage = params.thumbImage ? params.thumbImage : null

            String imgExt
            if ((thumbImage && !thumbImage.isEmpty())) {
                imgExt = getImageExtension(thumbImage)
                if (!imgExt) {
                    return super.setError(params, UNRECOGNIZED_THUMB_IMAGE)
                }

                if (thumbImage.size > TWO_MB_SIZE) {
                    // Check thumb image size
                    return super.setError(params, MAX_FILE_SIZE_IMG)
                }
            }

            if ((smallImage && !smallImage.isEmpty())) {
                imgExt = getImageExtension(smallImage)
                if (!imgExt) {
                    return super.setError(params, UNRECOGNIZED_SMALL_IMAGE)
                }

                if (smallImage.size > TWO_MB_SIZE) {
                    // Check small image size
                    return super.setError(params, MAX_FILE_SIZE_IMG)
                }
            }

            if ((thumbImage && !thumbImage.isEmpty()) || (smallImage && !smallImage.isEmpty())) {
                vendor = getVendorWithImage(params, smallImage, thumbImage, sysVendor.value)
            }
            params.put(VENDOR, vendor)
            params.put(SMALL_IMAGE, smallImage)
            params.put(THUMB_IMAGE, thumbImage)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Update vendor object
     * @param result -a map from executePreCondition method
     * @return -a map contains vendor object for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional
    public Map execute(Map result) {
        try {
            Vendor vendor = (Vendor) result.get(VENDOR)
            CommonsMultipartFile smallImage = (CommonsMultipartFile) result.get(SMALL_IMAGE)
            CommonsMultipartFile thumbImage = (CommonsMultipartFile) result.get(THUMB_IMAGE)
            if ((smallImage && !smallImage.isEmpty()) || (thumbImage && !thumbImage.isEmpty())) {
                vendorService.create(vendor)
            } else {
                //Save vendor in DB
                vendorService.create(vendor)
            }
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
     * Wrap updated vendor to show on grid
     * @param obj -Vendor object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map buildSuccessResultForUI(Map result) {
        try {
            Map successResultMap = new LinkedHashMap()
            Vendor vendor = (Vendor) result.get(VENDOR)
            ListAppVendorActionServiceModel modelService = listAppVendorActionServiceModelService.read(vendor.id)
            successResultMap.put(VENDOR, modelService)
            return super.setSuccess(successResultMap, CREATE_SUCCESS_MESSAGE)
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
     * @param params -G railsParameterMap
     * @return -Vendor object
     */
    private Vendor getVendor(Map params, String driver) {
        AppUser appUser = super.getAppUser()

        Vendor vendor = new Vendor(params)
        vendor.companyId = appUser.companyId
        vendor.driver = driver
        vendor.smallImage = getPlaceHolderImage()
        vendor.thumbImage = getPlaceHolderImage()
        vendor.createdOn = new Date()
        vendor.createdBy = appUser.id
        return vendor
    }

    private Vendor getVendorWithImage(Map params, CommonsMultipartFile smallImage, CommonsMultipartFile thumbImage, String driver) {
        AppUser appUser = super.getAppUser()
        Vendor vendor = new Vendor(params)
        vendor.driver = driver
        vendor.companyId = appUser.companyId
        vendor.smallImage = getSmallImageFile(smallImage)
        vendor.thumbImage = getThumbImageFile(thumbImage)
        vendor.createdOn = new Date()
        vendor.createdBy = appUser.id
        return vendor
    }

    /**
     * 1. ensure unique vendor name
     * 2. ensure unique vendor code
     * 3. ensure unique vendor email
     * 4. ensure unique vendor web url
     * @param vendor -object of Vendor
     * @return -a string containing error message or null value
     */
    private String checkUniqueness(Vendor vendor) {
        // check unique Vendor Name
        int duplicateNameCount = vendorService.countByNameIlikeAndDbTypeIdAndCompanyId(vendor.name, vendor.dbTypeId, vendor.companyId)
        if (duplicateNameCount > 0) {
            return VENDOR_ALREADY_EXISTS
        }
        // check unique order Name
        int duplicateSeq = vendorService.countBySequenceAndCompanyId(vendor.sequence, vendor.companyId)
        if (duplicateSeq > 0) {
            return DUPLICATE_ORDER_MSG
        }
        return null
    }

    private String getContentExtension(CommonsMultipartFile contentFile) {
        String contentFileName = contentFile.properties.originalFilename.toString()
        int i = contentFileName.lastIndexOf(SINGLE_DOT)
        String uploadedFileExtension = contentFileName.substring(i + 1)
        return uploadedFileExtension
    }

    private byte[] getSmallImageFile(CommonsMultipartFile imageFile) {
        if (imageFile == null) return getPlaceHolderImage()
        InputStream iStream = imageFile.getInputStream()
        BufferedImage tempImg = ImageIO.read(iStream)
        ByteArrayOutputStream buffer = new ByteArrayOutputStream()
        String imageExt = getContentExtension(imageFile)
        ImageIO.write(tempImg, imageExt, buffer)
        byte[] imageBytes = buffer.toByteArray()
        iStream.close()
        buffer.close()
        return imageBytes
    }

    private byte[] getThumbImageFile(CommonsMultipartFile imageFile) {
        if (imageFile == null) return getPlaceHolderImage()
        InputStream iStream = imageFile.getInputStream()
        BufferedImage tempImg = ImageIO.read(iStream)
        ByteArrayOutputStream buffer = new ByteArrayOutputStream()
        String imageExt = getContentExtension(imageFile)
        ImageIO.write(tempImg, imageExt, buffer)
        byte[] imageBytes = buffer.toByteArray()
        iStream.close()
        buffer.close()
        return imageBytes
    }


    private byte[] getPlaceHolderImage() {
        String imgLoc = grailsApplication.config.theme.application + "/images/dbinstance/no_db_logo.png"
        File imgFile = grailsApplication.parentContext.getResource(imgLoc).file
        return imgFile.bytes
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