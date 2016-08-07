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

class CreateDistrictActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String DISTRICT = "district"
    private static final String DISTRICT_CREATE_SUCCESS_MSG = "District has been successfully saved"
    private static final String DISTRICT_NAME_MUST_BE_UNIQUE = "Same district name already exists"

    DistrictService districtService
    ListDistrictActionServiceModelService listDistrictActionServiceModelService

    /**
     * 1. check required parameters
     * 2. build District object with params
     * 3. ensure uniqueness of District name and global district
     * 4. check validation of District object
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check required parameters
            if ((!params.name) || (!params.countryId)) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            // build District object
            District district = getDistrict(params)
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
     * Save District object in DB
     * This method is in transactional block and will roll back in case of any exception
     * This method is in transactional block and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            District district = (District) result.get(DISTRICT)
            districtService.create(district)
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
            return super.setSuccess(result, DISTRICT_CREATE_SUCCESS_MSG)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     * @param result -map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Get newly built district object
     * @param params - serialize params from UI
     * @return - newly built district object
     */
    private District getDistrict(Map params) {
        AppUser appUser = super.getAppUser()
        District district = new District(params)
        district.createdOn = new Date()
        district.createdBy = appUser.id
        district.companyId = appUser.companyId
        return district
    }

    /**
     * 1. ensure uniqueness for District name
     * 2. ensure only one District is global
     * @param district -object of District
     * @return -a string containing error message or null value depending on uniqueness check
     */
    private String checkUniqueness(District district) {
        // check for duplicate District name
        int duplicateName = districtService.countByNameIlikeAndCountryId(district.name, district.countryId)
        if (duplicateName > 0) {
            return DISTRICT_NAME_MUST_BE_UNIQUE
        }
        return null
    }
}
