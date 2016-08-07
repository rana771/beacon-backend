package com.athena.mis.application.actions.appmyfavourite

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppMyFavourite
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.service.AppMyFavouriteService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Get AppMyFavourite object
 *  For details go through Use-Case doc named 'SelectAppMyFavouriteActionService'
 */
class SelectAppMyFavouriteActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String ID = "id"
    private static final String IS_MY_FAVOURITE = "isMyFavourite"

    AppMyFavouriteService appMyFavouriteService

    /**
     * 1. check required parameters
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePreCondition(Map params) {
        try {
            // check required parameters
            if (!params.path) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. get AppMyFavourite object if exists
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map execute(Map result) {
        try {
            result.put(ID, 0L)
            result.put(IS_MY_FAVOURITE, Boolean.FALSE)
            // get url
            String url = result.path
            url = url.replace(UNDERSCORE, '&')
            AppUser user = super.getAppUser()
            AppMyFavourite myFavourite = appMyFavouriteService.findByUrlAndUserId(url, user.id)
            if (myFavourite) {
                result.put(ID, myFavourite.id)
                result.put(IS_MY_FAVOURITE, Boolean.TRUE)
            }
            return result
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
     * Since there is no success message return the same map
     * @param result - map from execute/executePost method
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
}
