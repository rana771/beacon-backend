package ${domainClass.packageName.replaceAll(".entity",".actions")}.${className.toString().toLowerCase()}

import ${domainClass.packageName}.${className}
import ${domainClass.packageName.replaceAll(".entity",".service")}.${className}Service
import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppSystemEntityCacheService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional



class Delete${className}ActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String DELETE_${FormatUtil.format(className)}_SUCCESS_MESSAGE = "${FormatUtil.getNameString(className)} has been deleted successfully"
    private static final String ${FormatUtil.format(className)} = "${propertyName}"

    ${className}Service ${propertyName}Service

    /**
     * 1. Check Validation
     * 2. Association check for ${propertyName} with different domains
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
            //association check for ${propertyName} with different domains
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
     * Delete ${propertyName} object from DB
     * 1. get the ${propertyName} object from map
     * 2. delete from db
     * This function is in transactional boundary and will roll back in case of any exception
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            ${className} ${propertyName} = (${className}) result.get(${FormatUtil.format(className)})
            ${propertyName}Service.delete(${propertyName})
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
        return super.setSuccess(result, DELETE_${FormatUtil.format(className)}_SUCCESS_MESSAGE)
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
     * 2.check for ${propertyName} existence
     *
     * @param params - a map from caller method
     * @return - error message or null
     */
    private String checkValidation(Map params) {
        // check required parameter
        if (params.id == null) {
            return ERROR_FOR_INVALID_INPUT
        }
        long ${propertyName}Id = Long.parseLong(params.id.toString())
        ${className} ${propertyName} = ${propertyName}Service.read(${propertyName}Id)
        //check for ${propertyName} existence
        if (${propertyName} == null) {
            return ENTITY_NOT_FOUND_ERROR_MESSAGE
        }
        params.put(${FormatUtil.format(className)}, ${propertyName})
        return null
    }

    /**
     * 1. check ${propertyName} content count
     * 2. check association with entity_id(${propertyName}_id) of app_user_entity(user_${propertyName})
     * 3. check association with Budget plugin
     * 4. check association with Procurement plugin
     * 5. check association with Inventory plugin
     * 6. check association with Accounting plugin
     *
     * @param params - a map from caller method
     * @return - specific association message
     */
    private String hasAssociation(Map params) {
        ${className} ${propertyName} = (${className}) params.get(${FormatUtil.format(className)})
        long ${propertyName}Id = ${propertyName}.id
        long companyId = ${propertyName}.companyId
        int count = 0
        String errMsg
        //Check your customer association here

        return null
    }

}
