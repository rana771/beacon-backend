package com.mis.beacon.place

import com.mis.beacon.Place
import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.AppUser
import com.athena.mis.integration.inventory.InvPluginConnector
import com.athena.mis.utility.DateUtility
import com.mis.beacon.service.PlaceService
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional


class UpdatePlaceActionService extends BaseService implements ActionServiceIntf {

     private Logger log = Logger.getLogger(getClass())

    private static final String PLACE = "place"
    private static final String PLACE_UPDATE_SUCCESS_MESSAGE = "Place has been updated successfully"

    PlaceService placeService


    /**
     * 1. Check Validation
     * 2. Check un-approve transactions for auto approve
     * 3. Build place object for update
     *
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            AppUser user = super.getAppUser();
            //Check Validation
            String errMsg = checkValidation(params)
            if (errMsg) {
                return super.setError(params, errMsg)
            }

            // check your custom valiation

            // build place object for update
            Place place = getPlace(params, user)
            params.put(PLACE, place)

            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. get the place object from map
     * 2. Update existing place in DB
     * This method is in transactional block and will roll back in case of any exception
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            Place place = (Place) result.get(PLACE)
            placeService.update(place)
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
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, PLACE_UPDATE_SUCCESS_MESSAGE)
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
     * Build place object for update
     *
     * @param params - serialize parameters from UI
     * @return - place object
     */
    private Place getPlace(Map params, AppUser user) {
        Place place = placeService.read(Long.parseLong(params.id))
        place.name = params.name
        place.geoFrenchRadius = params.geoFrenchRadius
        place.longitude = params.longitude
        place.latitude = params.latitude
        place.updatedOn = new Date()
        place.updatedBy = user.id
        return place
    }

    /**
     * 1. Check Place object existance
     * 2. Check for duplicate place code
     * 3. Check for duplicate place name
     * 4. Check parameters
     *
     * @param place - object of Place
     * @param params - a map from caller method
     * @return -a string containing null value or error message depending on duplicate check
     */
    private String checkValidation(Map params) {
        String errMsg
        //Check parameters

        long placeId = Long.parseLong(params.id.toString())
        Place place = placeService.read(placeId)

        //check Place object existance
        errMsg = checkPlaceExistance(place, params)
        if (errMsg != null) return errMsg

        // Check your custom validation here

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


        params.put(PLACE, place)
        return null
    }

    /**
     * check Place object existance
     *
     * @param place - an object of Place
     * @param params - a map from caller method
     * @return - error message or null
     */

    private String checkPlaceExistance(Place place, Map params) {
        long version = Long.parseLong(params.version.toString())
        if (!place || place.version != version) {
            return ENTITY_NOT_FOUND_ERROR_MESSAGE
        }
        return null
    }

}
