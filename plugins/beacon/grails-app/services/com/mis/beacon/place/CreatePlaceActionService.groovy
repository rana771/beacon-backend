package com.mis.beacon.place

import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.mis.beacon.Place
import com.athena.mis.ActionServiceIntf
import com.athena.mis.utility.DateUtility
import com.mis.beacon.service.PlaceService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional


class CreatePlaceActionService  extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String PLACE = "place"
    private static final String  PLACE_SAVE_SUCCESS_MESSAGE = "Place has been saved successfully"

    PlaceService placeService

    /**
     * 1. check Validation
     * 2. build place object
     *
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            AppUser user = super.getAppUser();
            // check Validation
            String errMsg = checkValidation(params, user)
            if (errMsg) {
                return super.setError(params, errMsg)
            }
            // build place object
            Place place = getPlace(params, user)
            params.put(PLACE, place)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. receive place object from executePreCondition method
     * 2. create new place
     * This method is in transactional block and will roll back in case of any exception
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            Place place = (Place) result.get(PLACE)
            // save new place object in DB
            placeService.create(place)
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
     * 1. put success message
     *
     * @param result -map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, PLACE_SAVE_SUCCESS_MESSAGE)
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     *
     * @param result -map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Build place object
     *
     * @param params - serialize parameters from UI
     * @param user - an object of AppUser
     * @return - place object
     */
    private Place getPlace(Map params, AppUser user) {
        Place place = new Place()
        place.name = params.name
        place.geoFrenchRadius = params.geoFrenchRadius
        place.longitude = params.longitude
        place.latitude = params.latitude
        place.createdOn = new Date()
        place.createdBy = user.id
        place.updatedBy = 0
        return place
    }

    /**
     * 1. check for duplicate place name
     * 2. check for duplicate place code
     *
     * @param place -object of Place
     * @param user - an object of AppUser
     * @return -a string containing null value or error message depending on duplicate check
     */
    private String checkValidation(Map params, AppUser user) {
        String errMsg
        //Check parameters
        if (!params.name) {
            return ERROR_FOR_INVALID_INPUT
        }
        if (!params.geoFrenchRadius) {
            return ERROR_FOR_INVALID_INPUT
        }
        if (!params.longitude) {
            return ERROR_FOR_INVALID_INPUT
        }
        if (!params.latitude) {
            return ERROR_FOR_INVALID_INPUT
        }


        return null
    }



}

