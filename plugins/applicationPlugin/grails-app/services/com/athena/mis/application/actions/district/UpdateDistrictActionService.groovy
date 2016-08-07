package com.athena.mis.application.actions.district

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.District
import com.athena.mis.application.model.ListDistrictActionServiceModel
import com.athena.mis.application.service.DistrictService
import com.athena.mis.application.service.ListDistrictActionServiceModelService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

class UpdateDistrictActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String DISTRICT = "district"
    private static final String UPDATE_SUCCESS_MSG = "District has been updated successfully"
    private static final String DISTRICT_NAME_MUST_BE_UNIQUE = "District name must be unique"

    DistrictService districtService
    ListDistrictActionServiceModelService listDistrictActionServiceModelService

    /**
     * 1. check required parameters
     * 3. check whether selected district object exists or not
     * 4. build district object for update
     * 5. check uniqueness of District name and global district
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check required params
            if ((!params.id) || (!params.version) || (!params.countryId)) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long districtId = Long.parseLong(params.id.toString())
            // get district object
            District oldDistrict = districtService.read(districtId)
            long version = Long.parseLong(params.version.toString())
            // check whether selected district object exists or not
            if ((!oldDistrict) || (version != oldDistrict.version)) {
                return super.setError(params, ENTITY_NOT_FOUND_ERROR_MESSAGE)
            }
            // build district object for update
            District district = getDistrict(params, oldDistrict)
            // check uniqueness
            String msg = checkUniqueness(district)
            if (msg) {
                return super.setError(params, msg)
            }
            params.put(DISTRICT, district)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * 1. get district object from map *
     * 2. Update District object in DB
     * This function is in transactional block and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            // get district object from map
            District district = (District) result.get(DISTRICT)
            districtService.update(district)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * There is no post condition, so return the same map as received
     */
    public Map executePostCondition(Map result) {
        return result
    }


    /**
     * 1. get new districtModel object form service
     * 2. put success message
     * @param result -map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map buildSuccessResultForUI(Map result) {
        try {
            District district = (District) result.get(DISTRICT)
            ListDistrictActionServiceModel districtModel = listDistrictActionServiceModelService.read(district.id)
            result.put(DISTRICT, districtModel)
            return super.setSuccess(result, UPDATE_SUCCESS_MSG)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Received map must contain isError - true with corresponding error msg
     * @param result -map returned from previous methods
     * @return -a map same as received
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Build District object for update
     * @param parameterMap -serialized parameters from UI
     * @param oldDistrict -old District object
     * @return -updated District object
     */
    private District getDistrict(Map parameterMap, District oldDistrict) {
        AppUser appUser = super.getAppUser()
        District newDistrict = new District(parameterMap)
        oldDistrict.name = newDistrict.name
        oldDistrict.countryId = newDistrict.countryId
        oldDistrict.updatedOn = new Date()
        oldDistrict.updatedBy = appUser.id
        return oldDistrict
    }

    /**
     *  ensure uniqueness for District name
     * @param district -object of District
     * @return -a string containing error message or null value depending on uniqueness check
     */
    private String checkUniqueness(District district) {
        // check for duplicate District name
        int count = districtService.countByNameIlikeAndCountryIdAndIdNotEqual(district.name, district.countryId, district.id)
        if (count > 0) {
            return DISTRICT_NAME_MUST_BE_UNIQUE
        }
        return null
    }
}

