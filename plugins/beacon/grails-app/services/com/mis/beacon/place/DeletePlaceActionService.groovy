package com.mis.beacon.place

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.mis.beacon.Place
import com.mis.beacon.service.PlaceService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

class DeletePlaceActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String DELETE_PLACE_SUCCESS_MESSAGE = "Place has been deleted successfully"
    private static final String PLACE = "place"

    PlaceService placeService

    /**
     * 1. Check Validation
     * 2. Association check for place with different domains
     *
     * @param parameters - serialize parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            //Check Validation
            String errMsg = checkValidation(params)
            if (errMsg) {
                return super.setError(params, errMsg)
            }
            //association check for place with different domains
            String associationMsg = hasAssociation(params)
            if (associationMsg != null) {
                return super.setError(params, associationMsg)
            }
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Delete place object from DB
     * 1. get the place object from map
     * 2. delete from db
     * This function is in transactional boundary and will roll back in case of any exception
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            Place place = (Place) result.get(PLACE)
            placeService.delete(place)
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
        return super.setSuccess(result, DELETE_PLACE_SUCCESS_MESSAGE)
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     *
     * @param result - map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * 1.check required parameter
     * 2.check for place existence
     *
     * @param params - a map from caller method
     * @return - error message or null
     */
    private String checkValidation(Map params) {
        // check required parameter
        if (params.id == null) {
            return ERROR_FOR_INVALID_INPUT
        }
        long placeId = Long.parseLong(params.id.toString())
        Place place = placeService.read(placeId)
        //check for place existence
        if (place == null) {
            return ENTITY_NOT_FOUND_ERROR_MESSAGE
        }
        params.put(PLACE, place)
        return null
    }

    /**
     * 1. check place content count
     * 2. check association with entity_id(place_id) of app_user_entity(user_place)
     * 3. check association with Budget plugin
     * 4. check association with Procurement plugin
     * 5. check association with Inventory plugin
     * 6. check association with Accounting plugin
     *
     * @param params - a map from caller method
     * @return - specific association message
     */
    private String hasAssociation(Map params) {
        Place place = (Place) params.get(PLACE)
        long placeId = place.id
        long companyId = place.companyId
        int count = 0
        String errMsg
        //Check your customer association here

        return null
    }

}
