package ${domainClass.packageName.replaceAll(".entity",".actions")}.${className.toString().toLowerCase()}

import ${domainClass.packageName}.${className}
import ${domainClass.packageName.replaceAll(".entity",".service")}.${className}Service
import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.AppUser
import com.athena.mis.integration.inventory.InvPluginConnector
import com.athena.mis.utility.DateUtility
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional


class Update${className}ActionService extends BaseService implements ActionServiceIntf {

     private Logger log = Logger.getLogger(getClass())

    private static final String ${FormatUtil.format(className)} = "${propertyName}"
    private static final String ${FormatUtil.format(className)}_UPDATE_SUCCESS_MESSAGE = "${FormatUtil.getNameString(className)} has been updated successfully"

    ${className}Service ${propertyName}Service
    @Autowired(required = false)

    /**
     * 1. Check Validation
     * 2. Check un-approve transactions for auto approve
     * 3. Build ${propertyName} object for update
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

            // check your custom valiation

            // build ${propertyName} object for update
            get${className}(params)

            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. get the ${propertyName} object from map
     * 2. Update existing ${propertyName} in DB
     * This method is in transactional block and will roll back in case of any exception
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            ${className} ${propertyName} = (${className}) result.get(${FormatUtil.format(className)})
            ${propertyName}Service.update(${propertyName})
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
        return super.setSuccess(result, ${FormatUtil.format(className)}_UPDATE_SUCCESS_MESSAGE)
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
     * Build ${propertyName} object for update
     *
     * @param params - serialize parameters from UI
     * @return - ${propertyName} object
     */
    private ${className} get${className}(Map params) {
        ${className} old${className} = (${className}) params.get(${FormatUtil.format(className)})
        ${className} new${className} = new ${className}(params)
        old${className}.name = new${className}.name
        old${className}.code = new${className}.code
        AppUser systemUser = super.getAppUser()
        old${className}.updatedOn = new Date()
        old${className}.updatedBy = systemUser.id

        // write approval flag holds previous state if user is not config manager

        return old${className}
    }

    /**
     * 1. Check ${className} object existance
     * 2. Check for duplicate ${propertyName} code
     * 3. Check for duplicate ${propertyName} name
     * 4. Check parameters
     *
     * @param ${propertyName} - object of ${className}
     * @param params - a map from caller method
     * @return -a string containing null value or error message depending on duplicate check
     */
    private String checkValidation(Map params) {
        String errMsg
        //Check parameters

        long ${propertyName}Id = Long.parseLong(params.id.toString())
        ${className} ${propertyName} = ${propertyName}Service.read(${propertyName}Id)

        //check ${className} object existance
        errMsg = check${className}Existance(${propertyName}, params)
        if (errMsg != null) return errMsg

        // Check your custom validation here

        params.put(${FormatUtil.format(className)}, ${propertyName})
        return null
    }

    /**
     * check ${className} object existance
     *
     * @param ${propertyName} - an object of ${className}
     * @param params - a map from caller method
     * @return - error message or null
     */

    private String check${className}Existance(${className} ${propertyName}, Map params) {
        long version = Long.parseLong(params.version.toString())
        if (!${propertyName} || ${propertyName}.version != version) {
            return ENTITY_NOT_FOUND_ERROR_MESSAGE
        }
        return null
    }

}
