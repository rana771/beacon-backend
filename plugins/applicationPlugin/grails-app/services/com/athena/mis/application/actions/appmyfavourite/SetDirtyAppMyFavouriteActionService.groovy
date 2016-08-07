package com.athena.mis.application.actions.appmyfavourite

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppMyFavourite
import com.athena.mis.application.service.AppMyFavouriteService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

class SetDirtyAppMyFavouriteActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    AppMyFavouriteService appMyFavouriteService

    private static final String SUCCESS_MESSAGE = 'Selected bookmark page not found.'
    private static final String URL = 'url'

    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            String url = params.url.replace('#', '');
            params.put(URL, url)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    @Transactional
    public Map execute(Map result) {
        try {
            String url = result.get(URL)
            long userId = super.getAppUser().id
            AppMyFavourite appMyFavourite = appMyFavouriteService.findByUrlAndUserIdAndCompanyId(url,userId,companyId)
            if(appMyFavourite){
                appMyFavourite.isDirty = true
                if(appMyFavourite.isDefault){
                    appMyFavourite.isDefault = false
                    updateAppUser(userId)
                }
                appMyFavouriteService.update(appMyFavourite)
            }
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
        return super.setSuccess(result, SUCCESS_MESSAGE)
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     *
     * @param obj - map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    private static final String UPDATE_USER = """
        UPDATE app_user
        SET default_url = null,
        version = version + 1
        WHERE id = :id
    """

    /**
     * Set default url for user
     * @param userId - id of logged in user
     */
    private void updateAppUser(long userId) {
        Map queryParams = [ id : userId ]
        executeUpdateSql(UPDATE_USER, queryParams)
    }
}
