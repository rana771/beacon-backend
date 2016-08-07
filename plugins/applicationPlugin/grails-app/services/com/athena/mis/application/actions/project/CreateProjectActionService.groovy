package com.athena.mis.application.actions.project

import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Project
import com.athena.mis.application.service.ProjectService
import com.athena.mis.ActionServiceIntf
import com.athena.mis.utility.DateUtility
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Create new project object and show in grid
 *  For details go through Use-Case doc named 'CreateProjectActionService'
 */
class CreateProjectActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String PROJECT = "project"
    private static final String PROJECT_CODE_ALREADY_EXISTS = "Same project code already exists"
    private static final String PROJECT_NAME_ALREADY_EXISTS = "Same project name already exists"
    private static final String PROJECT_SAVE_SUCCESS_MESSAGE = "Project has been saved successfully"

    ProjectService projectService

    /**
     * 1. check Validation
     * 2. build project object
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
            // build project object
            Project project = getProject(params, user)
            params.put(PROJECT, project)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. receive project object from executePreCondition method
     * 2. create new project
     * This method is in transactional block and will roll back in case of any exception
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            Project project = (Project) result.get(PROJECT)
            // save new project object in DB
            projectService.create(project)
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
        return super.setSuccess(result, PROJECT_SAVE_SUCCESS_MESSAGE)
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
     * Build project object
     *
     * @param params - serialize parameters from UI
     * @param user - an object of AppUser
     * @return - project object
     */
    private Project getProject(Map params, AppUser user) {
        params.startDate = DateUtility.parseMaskedDate(params.startDate.toString())
        params.endDate = DateUtility.parseMaskedDate(params.endDate.toString())
        Project project = new Project(params)
        project.createdOn = new Date()
        project.createdBy = user.id
        project.companyId = user.companyId
        project.updatedBy = 0
        project.updatedOn = null
        project.contentCount = 0
        return project
    }

    /**
     * 1. check for duplicate project name
     * 2. check for duplicate project code
     *
     * @param project -object of Project
     * @param user - an object of AppUser
     * @return -a string containing null value or error message depending on duplicate check
     */
    private String checkValidation(Map params, AppUser user) {
        String errMsg
        //Check parameters
        if ((!params.name) || (!params.startDate) || (!params.endDate) || (!params.code)) {
            return ERROR_FOR_INVALID_INPUT
        }
        //check for duplicate project code
        errMsg = checkProjectCountByCode(params, user)
        if (errMsg != null) return errMsg

        //check for duplicate project name
        errMsg = checkProjectCountByName(params, user)
        if (errMsg != null) return errMsg

        return null
    }

    /**
     * check for duplicate project code
     *
     * @param project - an object of Project
     * @param user - an object of AppUser
     * @return - error message or null
     */
    private String checkProjectCountByCode(Map params, AppUser user) {
        int count = projectService.countByCodeIlikeAndCompanyId(params.code, user.companyId)
        if (count > 0) {
            return PROJECT_CODE_ALREADY_EXISTS
        }
        return null
    }

    /**
     * check for duplicate project name
     *
     * @param project - an object of Project
     * @param user - an object of AppUser
     * @return - error message or null
     */
    private String checkProjectCountByName(Map params, AppUser user) {
        int count = projectService.countByNameIlikeAndCompanyId(params.name, user.companyId)
        if (count > 0) {
            return PROJECT_NAME_ALREADY_EXISTS
        }
        return null
    }

}
