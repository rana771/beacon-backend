package com.athena.mis.application.actions.appschedule

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppSchedule
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppScheduleService
import com.athena.mis.application.service.AppSystemEntityCacheService
import org.apache.log4j.Logger
import org.quartz.JobKey
import org.quartz.Trigger
import org.quartz.impl.matchers.GroupMatcher
import org.springframework.transaction.annotation.Transactional

class UpdateAppScheduleActionService extends BaseService implements ActionServiceIntf {

    AppScheduleService appScheduleService
    AppSystemEntityCacheService appSystemEntityCacheService
    def quartzScheduler

    private static final String UPDATE_SUCCESS_MESSAGE = "Schedule has been updated successfully"
    private static final String OBJ_NOT_FOUND = "Selected Schedule not found"
    private static final String APP_SCHEDULE = "appSchedule"
    private static final String COMPANY_ID = "companyId"

    private Logger log = Logger.getLogger(getClass())

    /**
     * 1. check input validation
     * @param parameters -serialized parameters from UI
     * @return -a map containing necessary objects for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check input validation
            LinkedHashMap checkValidation = checkInputValidation(params)
            Boolean isError = checkValidation.get(IS_ERROR)
            if (isError.booleanValue()) {
                String message = checkValidation.get(MESSAGE)
                return super.setError(params, message)
            }
            AppSchedule appSchedule = (AppSchedule) checkValidation.get(APP_SCHEDULE)
            params.put(APP_SCHEDULE, appSchedule)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Update AppSchedule object in DB
     * @param result -AppSchedule object send from executePreCondition method
     * @return -updated AppSchedule object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppSchedule appSchedule = (AppSchedule) result.get(APP_SCHEDULE)
            appScheduleService.update(appSchedule)
            restartScheduler(appSchedule)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    private void restartScheduler(AppSchedule schedule) {
        stopScheduler(schedule)
        if (schedule.enable) {
            appScheduleService.executeJob(schedule)
        }
    }

    private void stopScheduler(AppSchedule schedule) {
        def lstJobKeys = quartzScheduler.getJobKeys(GroupMatcher.anyJobGroup())
        JobKey jobKey = (JobKey) lstJobKeys.find { it.name.endsWith(schedule.jobClassName) }
        List<Trigger> lstTriggers = quartzScheduler.getTriggersOfJob(jobKey)
        lstTriggers.each {
            Long companyId = (Long) it.jobDataMap.get(COMPANY_ID)
            if (companyId.longValue() == schedule.companyId) {
                quartzScheduler.unscheduleJob(it.key) // remove all existing triggers
            }
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
     * set success message
     * @param result - resulting map from execute
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, UPDATE_SUCCESS_MESSAGE)
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * 1. check required parameters
     * 2. get old AppSchedule object by id
     * 3. build new AppSchedule object for update
     * @param params -serialized parameters from UI
     * @return -a map containing AppSchedule object, isError (true/false) and relevant error message
     */
    private LinkedHashMap checkInputValidation(Map params) {
        LinkedHashMap result = new LinkedHashMap()
        result.put(IS_ERROR, Boolean.TRUE)
        if (!params.id) {
            result.put(MESSAGE, ERROR_FOR_INVALID_INPUT)
            return result
        }
        long scheduleId = Long.parseLong(params.id.toString())
        long version = Long.parseLong(params.version.toString())
        AppSchedule oldAppSchedule = appScheduleService.read(scheduleId)
        // check whether selected  object exists or not
        if (!oldAppSchedule) {
            result.put(MESSAGE, OBJ_NOT_FOUND)
            return result
        }
        if (oldAppSchedule.version != version) {
            result.put(MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
            return result
        }
        AppSchedule appSchedule = buildAppScheduleObject(params, oldAppSchedule)
        result.put(APP_SCHEDULE, appSchedule)
        result.put(IS_ERROR, Boolean.FALSE)
        return result
    }

    /**
     * Build AppSchedule object for update
     * @param parameterMap -serialized parameters from UI
     * @param oldAppSchedule -old AppSchedule object
     * @return -updated AppSchedule object
     */
    private AppSchedule buildAppScheduleObject(Map parameterMap, AppSchedule oldAppSchedule) {
        SystemEntity scheduleTypeSimple = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SIMPLE, appSystemEntityCacheService.SYS_ENTITY_TYPE_SCHEDULE, companyId)
        AppSchedule appSchedule = new AppSchedule(parameterMap)
        oldAppSchedule.name = appSchedule.name
        oldAppSchedule.scheduleTypeId = appSchedule.scheduleTypeId
        if (appSchedule.scheduleTypeId == scheduleTypeSimple.id) {
            oldAppSchedule.repeatInterval = appSchedule.repeatInterval * 1000
            oldAppSchedule.repeatCount = appSchedule.repeatCount
            oldAppSchedule.cronExpression = null
        } else {
            oldAppSchedule.cronExpression = appSchedule.cronExpression
            oldAppSchedule.repeatInterval = 0
            oldAppSchedule.repeatCount = 0
        }
        oldAppSchedule.enable = appSchedule.enable
        oldAppSchedule.updatedOn = new Date()
        oldAppSchedule.updatedBy = super.appUser.id
        return oldAppSchedule
    }
}
