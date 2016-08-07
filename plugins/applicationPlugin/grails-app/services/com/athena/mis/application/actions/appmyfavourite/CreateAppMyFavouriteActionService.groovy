package com.athena.mis.application.actions.appmyfavourite

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppMyFavourite
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.RequestMap
import com.athena.mis.application.service.AppMyFavouriteService
import com.athena.mis.application.service.RequestMapService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Add page to "My Favourite"
 *  For details go through Use-Case doc named 'CreateAppMyFavouriteActionService'
 */
class CreateAppMyFavouriteActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String MY_FAVOURITE = "myFavourite"
    private static final String SUCCESS_MESSAGE = "This page has been added to 'My Favourite' list"
    private static final String ALREADY_EXISTS = "This page already exists in 'My Favourite'"
    private static final String ID = "id"

    RequestMapService requestMapService
    AppMyFavouriteService appMyFavouriteService

    /**
     * 1. check required parameters
     * 2. get url
     * 3. get request map object by url
     * 4. check existence of request map object
     * 5. check if url already exists in "My Favourite"
     * 6. build AppMyFavourite object to create
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check required parameters
            if (!params.path) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            // get url
            String url = params.path
            // only for those url that originated in Application plugin but usages in different plugins
            long localPluginId = 0L
            if(url.contains('plugin=')){
                try{
                    localPluginId = Long.parseLong(url.substring(url.lastIndexOf("plugin=") + 7))
                }catch (Exception e){ }
            }
            String urlWithOutParam = url.replace(QUESTION_SIGN, COMA)
            url = url.replace(UNDERSCORE, '&')
            List<String> strUrls = urlWithOutParam.split(COMA)
            urlWithOutParam = strUrls[0]
            urlWithOutParam = SLASH + urlWithOutParam
            // get request map object by url
            RequestMap requestMap = requestMapService.findByUrl(urlWithOutParam)
            // check existence of request map object
            if (!requestMap) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            AppUser user = super.getAppUser()
            int count = appMyFavouriteService.countByUrlAndUserId(url, user.id)
            if (count > 0) {
                return super.setError(params, ALREADY_EXISTS)
            }
            // build AppMyFavourite object to create
            AppMyFavourite myFavourite = buildObject(requestMap, user, url, localPluginId)
            params.put(MY_FAVOURITE, myFavourite)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. receive AppMyFavourite object from executePreCondition method
     * 2. create new AppMyFavourite object
     * This method is in transactional block and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppMyFavourite myFavourite = (AppMyFavourite) result.get(MY_FAVOURITE)
            appMyFavouriteService.create(myFavourite)
            result.put(ID, myFavourite.id)
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
     * 1. put success message
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, SUCCESS_MESSAGE)
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     * @param result - map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Build AppMyFavourite object for create
     * @param requestMap - object of requestMap
     * @param appUser - logged in user
     * @param url - url of show page
     * @return - object of AppMyFavourite
     */
    private AppMyFavourite buildObject(RequestMap requestMap, AppUser appUser, String url, long localPluginId) {
        AppMyFavourite myFavourite = new AppMyFavourite()
        myFavourite.userId = appUser.id
        myFavourite.companyId = appUser.companyId
        myFavourite.url = url
        myFavourite.featureName = requestMap.featureName
        myFavourite.pluginId = localPluginId > 0 ? localPluginId : requestMap.pluginId
        myFavourite.createdOn = new Date()
        return myFavourite
    }
}
