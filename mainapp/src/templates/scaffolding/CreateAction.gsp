package ${domainClass.packageName.replaceAll(".entity",".actions")}.${className.toString().toLowerCase()}

import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import ${domainClass.packageName}.${className}
import ${domainClass.packageName.replaceAll(".entity",".service")}.${className}Service
import com.athena.mis.ActionServiceIntf
import com.athena.mis.utility.DateUtility
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional


class Create${className}ActionService  extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String ${FormatUtil.format(className)} = "${propertyName}"
    private static final String  ${FormatUtil.format(className)}_SAVE_SUCCESS_MESSAGE = "${FormatUtil.getNameString(className)} has been saved successfully"

    ${className}Service ${propertyName}Service

    /**
     * 1. check Validation
     * 2. build ${propertyName} object
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
            // build ${propertyName} object
            ${className} ${propertyName} = get${className}(params, user)
            params.put(${FormatUtil.format(className)}, ${propertyName})
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. receive ${propertyName} object from executePreCondition method
     * 2. create new ${propertyName}
     * This method is in transactional block and will roll back in case of any exception
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            ${className} ${propertyName} = (${className}) result.get(${FormatUtil.format(className)})
            // save new ${propertyName} object in DB
            ${propertyName}Service.create(${propertyName})
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
        return super.setSuccess(result, ${FormatUtil.format(className)}_SAVE_SUCCESS_MESSAGE)
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
     * Build ${propertyName} object
     *
     * @param params - serialize parameters from UI
     * @param user - an object of AppUser
     * @return - ${propertyName} object
     */
    private ${className} get${className}(Map params, AppUser user) {
        ${className} ${propertyName} = new ${className}(params)
        ${propertyName}.createdOn = new Date()
        ${propertyName}.createdBy = user.id
        ${propertyName}.companyId = user.companyId
        ${propertyName}.updatedBy = 0
        ${propertyName}.updatedOn = null
        return ${propertyName}
    }

    /**
     * 1. check for duplicate ${propertyName} name
     * 2. check for duplicate ${propertyName} code
     *
     * @param ${propertyName} -object of ${className}
     * @param user - an object of AppUser
     * @return -a string containing null value or error message depending on duplicate check
     */
    private String checkValidation(Map params, AppUser user) {
        String errMsg
            //write your validation message here

        return null
    }



}

