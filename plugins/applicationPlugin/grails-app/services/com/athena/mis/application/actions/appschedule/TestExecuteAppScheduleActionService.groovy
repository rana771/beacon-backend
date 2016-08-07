package com.athena.mis.application.actions.appschedule

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppSchedule
import com.athena.mis.application.service.AppScheduleService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

class TestExecuteAppScheduleActionService extends BaseService implements ActionServiceIntf {

    AppScheduleService appScheduleService

    private static final String NOT_FOUND_MASSAGE = "Selected schedule is not found"
    private static final String SUCCESS_MASSAGE = "Selected schedule executed successfully"

    private Logger log = Logger.getLogger(getClass())

    /**
     * There is no preCondition, so return the same map as received
     * @param result - resulting map from execute
     * @return - same map of input-parameter
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check required parameters
            if (!params.id) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long appScheduleId = Long.parseLong(params.id.toString())
            AppSchedule appSchedule = (AppSchedule) appScheduleService.read(appScheduleId)
            if (!appSchedule) {
                return super.setError(params, NOT_FOUND_MASSAGE)
            }
            params.put(ENTITY, appSchedule)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * @param parameters -parameters from UI
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    public Map execute(Map params) {
        try {
            AppSchedule schedule = (AppSchedule) params.get(ENTITY)
            runScheduler(schedule)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    private void runScheduler(AppSchedule schedule) {
        Class clazz = Class.forName(schedule.jobClassName)
        clazz.triggerNow(['companyId':schedule.companyId])
    }

    /**
     * There is no postCondition, so return the same map as received
     * @param result - resulting map from execute
     * @return - same map of input-parameter
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * set success message
     * @param result -map returned from execute method
     * @return -a map containing success message
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result,SUCCESS_MASSAGE)
    }

    /**
     * Build failure result in case of any error
     * @param result -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }
}
