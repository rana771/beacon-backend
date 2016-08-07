package com.athena.mis.application.actions.project

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Project
import com.athena.mis.application.service.ProjectService
import com.athena.mis.integration.inventory.InvPluginConnector
import com.athena.mis.utility.DateUtility
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Update project object and show in grid
 *  For details go through Use-Case doc named 'UpdateProjectActionService'
 */
class UpdateProjectActionService extends BaseService implements ActionServiceIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String PROJECT = "project"
    private static final String PROJECT_CODE_ALREADY_EXISTS = "Same project code already exists"
    private static final String PROJECT_NAME_ALREADY_EXISTS = "Same project name already exists"
    private static final String PROJECT_UPDATE_SUCCESS_MESSAGE = "Project has been updated successfully"
    private static
    final String HAS_UNAPPROVED_CONSUMPTION = " Consumption(s) is unapproved of this project. Approve all transactions to change auto approve property"
    private static
    final String HAS_UNAPPROVED_PRODUCTION = " Production(s) is unapproved of this project. Approve all transactions to change auto approve property"
    private static
    final String HAS_UNAPPROVED_OUT = " Inventory Out(s) is unapproved of this project. Approve all transactions to change auto approve property"
    private static
    final String HAS_UNAPPROVED_IN_FROM_INVENTORY = " In from Inventory(s) is unapproved of this project. Approve all transactions to change auto approve property"
    private static
    final String HAS_UNAPPROVED_IN_FROM_SUPPLIER = " In from Supplier(s) is unapproved of this project. Approve all transactions to change auto approve property"

    ProjectService projectService
    @Autowired(required = false)
    InvPluginConnector invInventoryImplService

    /**
     * 1. Check Validation
     * 2. Check un-approve transactions for auto approve
     * 3. Build project object for update
     *
     * @param params - serialized parameters from UI
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

            // check un-approve transactions for auto approve
            if (PluginConnector.isPluginInstalled(InvPluginConnector.PLUGIN_NAME)) {
                String msg = checkUnApproveTransactionsForAutoApprove(params)
                if (msg) {
                    return super.setError(params, msg)
                }
            }
            // build project object for update
            getProject(params)

            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. get the project object from map
     * 2. Update existing project in DB
     * This method is in transactional block and will roll back in case of any exception
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            Project project = (Project) result.get(PROJECT)
            projectService.update(project)
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
        return super.setSuccess(result, PROJECT_UPDATE_SUCCESS_MESSAGE)
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
     * Build project object for update
     *
     * @param params - serialize parameters from UI
     * @return - project object
     */
    private Project getProject(Map params) {
        Project oldProject = (Project) params.get(PROJECT)
        params.startDate = DateUtility.parseMaskedDate(params.startDate.toString())
        params.endDate = DateUtility.parseMaskedDate(params.endDate.toString())
        Project newProject = new Project(params)
        oldProject.name = newProject.name
        oldProject.code = newProject.code
        oldProject.description = newProject.description
        oldProject.startDate = newProject.startDate
        oldProject.endDate = newProject.endDate
        AppUser systemUser = super.getAppUser()
        oldProject.updatedOn = new Date()
        oldProject.updatedBy = systemUser.id

        // auto approval flag holds previous state if user is not config manager
        if (systemUser.isConfigManager) {
            oldProject.isApproveConsumption = newProject.isApproveConsumption
            oldProject.isApproveProduction = newProject.isApproveProduction
            oldProject.isApproveInvOut = newProject.isApproveInvOut
            oldProject.isApproveInFromInventory = newProject.isApproveInFromInventory
            oldProject.isApproveInFromSupplier = newProject.isApproveInFromSupplier
        }
        return oldProject
    }

    /**
     * 1. Check Project object existance
     * 2. Check for duplicate project code
     * 3. Check for duplicate project name
     * 4. Check parameters
     *
     * @param project - object of Project
     * @param params - a map from caller method
     * @return -a string containing null value or error message depending on duplicate check
     */
    private String checkValidation(Map params) {
        String errMsg
        //Check parameters
        if ((!params.id) || (!params.version) || (!params.name) || (!params.startDate) || (!params.endDate) || (!params.code)) {
            return ERROR_FOR_INVALID_INPUT
        }
        long projectId = Long.parseLong(params.id.toString())
        Project project = projectService.read(projectId)

        //check Project object existance
        errMsg = checkProjectExistance(project, params)
        if (errMsg != null) return errMsg

        //check for duplicate project code
        errMsg = checkProjectCountByCode(project, params)
        if (errMsg != null) return errMsg

        //check for duplicate project name
        errMsg = checkProjectCountByName(project, params)
        if (errMsg != null) return errMsg
        params.put(PROJECT, project)
        return null
    }

    /**
     * check Project object existance
     *
     * @param project - an object of Project
     * @param params - a map from caller method
     * @return - error message or null
     */

    private String checkProjectExistance(Project project, Map params) {
        long version = Long.parseLong(params.version.toString())
        if (!project || project.version != version) {
            return ENTITY_NOT_FOUND_ERROR_MESSAGE
        }
        return null
    }

    /**
     * check for duplicate project code
     *
     * @param project - an object of Project
     * @param params - a map from caller method
     * @return - error message or null
     */
    private String checkProjectCountByCode(Project project, Map params) {
        int count = projectService.countByCodeIlikeAndCompanyIdAndIdNotEqual(params.code, project.companyId, project.id)
        if (count > 0) {
            return PROJECT_CODE_ALREADY_EXISTS
        }
        return null
    }

    /**
     * check for duplicate project name
     *
     * @param project - an object of Project
     * @param params - a map from caller method
     * @return - error message or null
     */
    private String checkProjectCountByName(Project project, Map params) {
        int count = projectService.countByNameIlikeAndCompanyIdAndIdNotEqual(params.name, project.companyId, project.id)
        if (count > 0) {
            return PROJECT_NAME_ALREADY_EXISTS
        }
        return null
    }

    private static final String STR_QUERY = """
        SELECT COUNT(iitd.id) AS count
        FROM inv_inventory_transaction_details iitd
        LEFT JOIN inv_inventory_transaction iit ON iit.id = iitd.inventory_transaction_id
        WHERE approved_by = 0
        AND iit.transaction_type_id = :transactionTypeId
        AND iit.transaction_entity_type_id = :transactionEntityTypeId
        AND iit.project_id = :projectId
    """

    /**
     * Check un-approve transactions for auto approve
     *
     * @param params -serialized parameters from UI
     * @param project -object of Project
     * @return -a string containing relevant message or null value depending on count of un-approved transactions
     */
    private String checkUnApproveTransactionsForAutoApprove(Map params) {
        Project project = (Project) params.get(PROJECT)
        Project newProject = new Project(params)

        String msg = null
        long transactionTypeId
        long transactionEntityTypeId
        int count
        if (!project.isApproveConsumption && newProject.isApproveConsumption) {
            transactionTypeId = invInventoryImplService.getInvTransactionTypeIdConsumption()
            transactionEntityTypeId = invInventoryImplService.getTransactionEntityTypeIdNone()
            Map queryParams = [
                    transactionTypeId: transactionTypeId,
                    transactionEntityTypeId: transactionEntityTypeId,
                    projectId: project.id
            ]
            List result = executeSelectSql(STR_QUERY, queryParams)
            count = result[0].count
            if (count > 0) {
                return count + HAS_UNAPPROVED_CONSUMPTION
            }
        }
        if (!project.isApproveProduction && newProject.isApproveProduction) {
            transactionTypeId = invInventoryImplService.getInvTransactionTypeIdProduction()
            transactionEntityTypeId = invInventoryImplService.getTransactionEntityTypeIdNone()
            Map queryParams = [
                    transactionTypeId: transactionTypeId,
                    transactionEntityTypeId: transactionEntityTypeId,
                    projectId: project.id
            ]
            List result = executeSelectSql(STR_QUERY, queryParams)
            count = result[0].count
            if (count > 0) {
                return count + HAS_UNAPPROVED_PRODUCTION
            }
        }
        if (!project.isApproveInvOut && newProject.isApproveInvOut) {
            transactionTypeId = invInventoryImplService.getInvTransactionTypeIdOut()
            transactionEntityTypeId = invInventoryImplService.getTransactionEntityTypeIdInventory()
            Map queryParams = [
                    transactionTypeId: transactionTypeId,
                    transactionEntityTypeId: transactionEntityTypeId,
                    projectId: project.id
            ]
            List result = executeSelectSql(STR_QUERY, queryParams)
            count = result[0].count
            if (count > 0) {
                return count + HAS_UNAPPROVED_OUT
            }
        }
        if (!project.isApproveInFromInventory && newProject.isApproveInFromInventory) {
            transactionTypeId = invInventoryImplService.getInvTransactionTypeIdIn()
            transactionEntityTypeId = invInventoryImplService.getTransactionEntityTypeIdInventory()
            Map queryParams = [
                    transactionTypeId: transactionTypeId,
                    transactionEntityTypeId: transactionEntityTypeId,
                    projectId: project.id
            ]
            List result = executeSelectSql(STR_QUERY, queryParams)
            count = result[0].count
            if (count > 0) {
                return count + HAS_UNAPPROVED_IN_FROM_INVENTORY
            }
        }
        if (!project.isApproveInFromSupplier && newProject.isApproveInFromSupplier) {
            transactionTypeId = invInventoryImplService.getInvTransactionTypeIdIn()
            transactionEntityTypeId = invInventoryImplService.getTransactionEntityTypeIdSupplier()
            Map queryParams = [
                    transactionTypeId: transactionTypeId,
                    transactionEntityTypeId: transactionEntityTypeId,
                    projectId: project.id
            ]
            List result = executeSelectSql(STR_QUERY, queryParams)
            count = result[0].count
            if (count > 0) {
                return count + HAS_UNAPPROVED_IN_FROM_SUPPLIER
            }
        }
        return msg
    }
}
