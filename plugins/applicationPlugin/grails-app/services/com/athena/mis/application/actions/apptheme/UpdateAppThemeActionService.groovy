package com.athena.mis.application.actions.apptheme

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.AppTheme
import com.athena.mis.application.service.AppThemeService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Update appTheme object, appTheme CacheUtility and grid data
 *  For details go through Use-Case doc named 'UpdateThemeActionService'
 */
class UpdateAppThemeActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    AppThemeService appThemeService

    private static final String UPDATE_SUCCESS_MSG = "Theme Information has been updated successfully"
    private static final String THEM_NOT_FOUND = "Theme not found to be updated, refresh the page"
    private static final String THEME = "theme"

    /**
     * Get parameters from UI and build appTheme object for update
     * 1. Check the existence of old appTheme object
     * 2. Build new appTheme object
     * @param parameters - serialized parameters from UI
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            long themeId = Integer.parseInt(params.id.toString())
            AppTheme oldTheme = appThemeService.read(themeId)
            if (!oldTheme) {
                return super.setError(params, THEM_NOT_FOUND)
            }
            AppTheme theme = buildThemeObjectForUpdate(params, oldTheme) // build appTheme object for update
            params.put(THEME, theme)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Update appTheme object in DB & update cache utility
     * 1. This function is in transactional block and will roll back in case of any exception
     * @param parameters - N/A
     * @param obj - serialized parameters from UI
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppTheme theme = (AppTheme) result.get(THEME)
            appThemeService.update(theme)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * do nothing for post operation
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * Show updated appTheme object in grid
     * Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, UPDATE_SUCCESS_MSG)
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message to display on page load
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Build appTheme object for update
     * @param params - serialized parameters from UI
     * @param oldTheme - old appTheme object
     * @return - updated appTheme object
     */
    private AppTheme buildThemeObjectForUpdate(Map params, AppTheme oldTheme) {
        AppTheme newTheme = new AppTheme(params)
        AppUser user = super.appUser
        oldTheme.value = newTheme.value
        oldTheme.updatedOn = new Date()
        oldTheme.updatedBy = user.id
        return oldTheme
    }
}

