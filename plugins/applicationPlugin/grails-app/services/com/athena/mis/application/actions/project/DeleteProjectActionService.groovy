package com.athena.mis.application.actions.project

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.ProjectService
import com.athena.mis.integration.accounting.AccPluginConnector
import com.athena.mis.integration.budget.BudgPluginConnector
import com.athena.mis.integration.inventory.InvPluginConnector
import com.athena.mis.integration.procurement.ProcPluginConnector
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Delete project object from DB
 *  For details go through Use-Case doc named 'DeleteProjectActionService'
 */
class DeleteProjectActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String DELETE_PROJECT_SUCCESS_MESSAGE = "Project has been deleted successfully"
    private static final String HAS_ASSOCIATION_USER_PROJECT = " user is associated with selected project"
    private static final String HAS_ASSOCIATION_ATTACHMENT_PROJECT = " attachment is associated with selected project"
    private static final String HAS_ASSOCIATION_MESSAGE_BUDGET = " budget is associated with selected project"
    private static
    final String HAS_ASSOCIATION_MESSAGE_BUDGET_DETAILS = " budget details is associated with selected project"
    private static
    final String HAS_ASSOCIATION_MESSAGE_PURCHASE_REQUEST = " purchase request is associated with selected project"
    private static
    final String HAS_ASSOCIATION_MESSAGE_PURCHASE_ORDER = " purchase order is associated with selected project"
    private static
    final String HAS_ASSOCIATION_MESSAGE_INVENTORY_TRANSACTION = " store transaction is associated with selected project"
    private static final String HAS_ASSOCIATION_MESSAGE_INVENTORY = " store is associated with selected project"
    private static final String HAS_ASSOCIATION_MESSAGE_VOUCHER_DETAILS = " voucher is associated with this project"
    private static final String PROJECT = "project"

    AppSystemEntityCacheService appSystemEntityCacheService
    ProjectService projectService

    /**
     * 1. Check Validation
     * 2. Association check for project with different domains
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
            //association check for project with different domains
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
     * Delete project object from DB
     * 1. get the project object from map
     * 2. delete from db
     * This function is in transactional boundary and will roll back in case of any exception
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            Project project = (Project) result.get(PROJECT)
            projectService.delete(project)
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
        return super.setSuccess(result, DELETE_PROJECT_SUCCESS_MESSAGE)
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
     * 2.check for project existence
     *
     * @param params - a map from caller method
     * @return - error message or null
     */
    private String checkValidation(Map params) {
        // check required parameter
        if (params.id == null) {
            return ERROR_FOR_INVALID_INPUT
        }
        long projectId = Long.parseLong(params.id.toString())
        Project project = projectService.read(projectId)
        //check for project existence
        if (project == null) {
            return ENTITY_NOT_FOUND_ERROR_MESSAGE
        }
        params.put(PROJECT, project)
        return null
    }

    /**
     * 1. check project content count
     * 2. check association with entity_id(project_id) of app_user_entity(user_project)
     * 3. check association with Budget plugin
     * 4. check association with Procurement plugin
     * 5. check association with Inventory plugin
     * 6. check association with Accounting plugin
     *
     * @param params - a map from caller method
     * @return - specific association message
     */
    private String hasAssociation(Map params) {
        Project project = (Project) params.get(PROJECT)
        long projectId = project.id
        long companyId = project.companyId
        int count = 0
        String errMsg
        //check project content count
        if (project.contentCount > 0) {
            return project.contentCount.toString() + HAS_ASSOCIATION_ATTACHMENT_PROJECT
        }
        //check association with entity_id(project_id) of app_user_entity(user_project)
        count = countUserProject(projectId, companyId)
        if (count > 0) {
            return count.toString() + HAS_ASSOCIATION_USER_PROJECT
        }
        //check association with Budget plugin
        errMsg = checkBudgetAssociation(projectId, companyId)
        if (errMsg != null) return errMsg
        //check association with Procurement plugin
        errMsg = checkProcurementAssociation(projectId, companyId)
        if (errMsg != null) return errMsg
        //check association with Inventory plugin
        errMsg = checkInventoryAssociation(projectId, companyId)
        if (errMsg != null) return errMsg
        //check association with Accounting plugin
        errMsg = checkAccountingAssociation(projectId)
        if (errMsg != null) return errMsg

        return null
    }

    /**
     * 1. check association with project_id of voucher_details
     *
     * @param projectId - Project id
     * @return specific association message
     */
    private String checkAccountingAssociation(long projectId) {
        int count
        if (PluginConnector.isPluginInstalled(AccPluginConnector.PLUGIN_NAME)) {
            count = countVoucherDetails(projectId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_VOUCHER_DETAILS
            }
        }
        return null
    }

    /**
     * 1. check association with project_id of inventory
     * 2. check association with project_id of inventory_transaction
     *
     * @param projectId - Project id
     * @param companyId - Company id
     * @return specific association message
     */
    private String checkInventoryAssociation(long projectId, long companyId) {
        int count
        if (PluginConnector.isPluginInstalled(InvPluginConnector.PLUGIN_NAME)) {
            count = countInventory(projectId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_INVENTORY
            }
            count = countInventoryTransaction(projectId, companyId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_INVENTORY_TRANSACTION
            }
        }
        return null
    }

    /**
     * 1. check association with project_id of purchase_request
     * 2. check association with project_id of purchase_order
     *
     * @param projectId - Project id
     * @param companyId - Company id
     * @return specific association message
     */
    private String checkProcurementAssociation(long projectId, long companyId) {
        int count
        if (PluginConnector.isPluginInstalled(ProcPluginConnector.PLUGIN_NAME)) {
            count = countPurchaseRequest(projectId, companyId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_PURCHASE_REQUEST
            }
            count = countPurchaseOrder(projectId, companyId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_PURCHASE_ORDER
            }
        }
        return null
    }

    /**
     * 1. check association with project_id of budg_budget
     * 2. check association with project_id of budget_details
     *
     * @param projectId - Project id
     * @param companyId - Company id
     * @return specific association message
     */
    private String checkBudgetAssociation(long projectId, long companyId) {
        int count
        if (PluginConnector.isPluginInstalled(BudgPluginConnector.PLUGIN_NAME)) {
            count = countBudget(projectId, companyId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_BUDGET
            }
            count = countBudgetDetails(projectId, companyId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_BUDGET_DETAILS
            }
        }
        return null
    }

    private static final String SELECT_QUERY = """
            SELECT COUNT(id) AS count
            FROM app_user_entity
            WHERE entity_id =:projectId AND
            entity_type_id =:entityTypeId
    """

    /**
     * Get total user_entity(user_project) number of given project-id
     *
     * @param projectId - project id
     * @param companyId -Company.id
     * @return - total user_entity(user_project) number
     */
    private int countUserProject(long projectId, long companyId) {
        SystemEntity appUserSysEntityObject = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_USER_ENTITY_PROJECT, appSystemEntityCacheService.SYS_ENTITY_TYPE_USER_ENTITY, companyId)
        Map queryParams = [
                projectId   : projectId,
                entityTypeId: appUserSysEntityObject.id
        ]
        List results = executeSelectSql(SELECT_QUERY, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_QUERY = """
            SELECT COUNT(id) AS count
            FROM budg_budget
            WHERE project_id =:projectId AND
            company_id =:companyId
    """

    /**
     * Get total budget number of given project-id
     *
     * @param projectId - project id
     * @param companyId -Company.id
     * @return - total budget number
     */
    private int countBudget(long projectId, long companyId) {
        Map queryParams = [
                projectId: projectId,
                companyId: companyId
        ]
        List results = executeSelectSql(COUNT_QUERY, queryParams)
        int count = results[0].count
        return count
    }

    /**
     * Get total budget_details number of given project-id
     *
     * @param projectId - project id
     * @param companyId -Company.id
     * @return - total budget_details number
     */
    private int countBudgetDetails(long projectId, long companyId) {
        String queryStr = """
            SELECT COUNT(id) AS count
            FROM budg_budget_details
            WHERE project_id = ${projectId} AND
                  company_id = ${companyId}
        """
        List results = executeSelectSql(queryStr)
        int count = results[0].count
        return count
    }

    /**
     * Get total purchase_request number of given project-id
     *
     * @param projectId - project id
     * @param companyId -Company.id
     * @return - total purchase_request number
     */
    private int countPurchaseRequest(long projectId, long companyId) {
        String queryStr = """
            SELECT COUNT(id) AS count
            FROM proc_purchase_request
            WHERE project_id = ${projectId} AND
                  company_id = ${companyId}
        """
        List results = executeSelectSql(queryStr)
        int count = results[0].count
        return count
    }

    /**
     * Get total purchase_order number of given project-id
     *
     * @param projectId - project id
     * @param companyId -Company.id
     * @return - total purchase_order number
     */
    private int countPurchaseOrder(long projectId, long companyId) {
        String queryStr = """
            SELECT COUNT(id) AS count
            FROM proc_purchase_order
            WHERE project_id = ${projectId} AND
                  company_id = ${companyId}
        """
        List results = executeSelectSql(queryStr)
        int count = results[0].count
        return count
    }

    /**
     * Get total inventory number of given project-id
     *
     * @param projectId - project id
     * @return - total inventory number
     */
    private int countInventory(long projectId) {
        String queryStr = """
            SELECT COUNT(id) AS count
            FROM inv_inventory
            WHERE project_id = ${projectId}
        """
        List results = executeSelectSql(queryStr)
        int count = results[0].count
        return count
    }

    /**
     * Get total inventory_transaction number of given project-id
     *
     * @param projectId - project id
     * @param companyId -Company.id
     * @return - total inventory_transaction number
     */
    private int countInventoryTransaction(long projectId, long companyId) {
        String queryStr = """
            SELECT COUNT(id) AS count
            FROM inv_inventory_transaction
            WHERE project_id = ${projectId} AND
                  company_id = ${companyId}
        """
        List results = executeSelectSql(queryStr)
        int count = results[0].count
        return count
    }

    /**
     * Get total voucher_details number of given project-id
     *
     * @param projectId - project id
     * @return - total voucher_details number
     */
    private int countVoucherDetails(long projectId) {
        String queryStr = """
            SELECT COUNT(id) AS count
            FROM acc_voucher_details
            WHERE project_id=${projectId}
        """
        List results = executeSelectSql(queryStr)
        int count = results[0].count
        return count
    }
}
