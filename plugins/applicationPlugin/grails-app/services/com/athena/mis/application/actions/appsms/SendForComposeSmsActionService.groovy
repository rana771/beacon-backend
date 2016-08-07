package com.athena.mis.application.actions.appsms

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.SmsSenderService
import com.athena.mis.application.config.AppConfigurationService
import com.athena.mis.application.entity.AppSms
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.service.AppSmsService
import com.athena.mis.utility.DateUtility
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

import static grails.async.Promises.task

/**
 *  Send sms to recipients
 *  For details go through Use-Case doc named 'SendForComposeSmsActionService'
 */
class SendForComposeSmsActionService extends BaseService implements ActionServiceIntf {

    private static final String SUCCESS_MESSAGE = "SMS has been sent successfully"
    private static final String RECIPIENTS_NOT_FOUND = 'Recipients not found'
    private static final String SMS_OBJ = "sms"
    private static final String NOT_PRODUCTION_MODE = "SMS compose is not sent because application is not in production mode."

    private Logger log = Logger.getLogger(getClass())

    AppSmsService appSmsService
    SmsSenderService smsSenderService
    AppConfigurationService appConfigurationService

    /**
     * 1. check validation
     * @param params - serialize parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check validation
            String errMsg = checkValidation(params)
            if (errMsg) {
                return super.setError(params, errMsg)
            }
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. send sms
     * 2. update sms object
     * This function is in transactional boundary and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppSms sms = (AppSms) result.get(SMS_OBJ)
            sendSms(sms)
            updateSmsObject(sms)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
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
     * 1. put success message
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, SUCCESS_MESSAGE)
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     * @param result - map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * 1. check required parameter
     * 2. check sms object existence
     * 3. get recipients
     * @param params - serialized parameters from UI
     * @return - error message or null value
     */
    private String checkValidation(Map params) {
        // check required parameter
        if (!params.id) {
            return ERROR_FOR_INVALID_INPUT
        }
        long smsId = Long.parseLong(params.id.toString())
        AppSms sms = appSmsService.read(smsId)
        // check mail object existence
        if (!sms) {
            return ENTITY_NOT_FOUND_ERROR_MESSAGE
        }
        String recipients = getRecipients(sms)
        if (!recipients) {
            return RECIPIENTS_NOT_FOUND
        }
        String checkValidationMsg = getDeploymentMode(sms.companyId)
        if (checkValidationMsg) {
            return checkValidationMsg
        }
        sms.recipients = recipients
        params.put(SMS_OBJ, sms)
        return null
    }
    /**
     * get deployment mode config value
     * @param companyId - id of company
     * @return - value of deployment mode config
     */
    private String getDeploymentMode(long companyId) {
        int deploymentMode = 1
        SysConfiguration sysConfiguration = (SysConfiguration) appConfigurationService.appSysConfigCacheService.readByKeyAndCompanyId(appConfigurationService.appSysConfigCacheService.APPLICATION_DEPLOYMENT_MODE, companyId)
        if (sysConfiguration) {
            deploymentMode = Integer.parseInt(sysConfiguration.value)
        }
        if (deploymentMode != 1) {
            return NOT_PRODUCTION_MODE
        }
        return null
    }

    /**
     * Get recipients
     * @param sms - object of AppSms
     * @return - cell numbers of recipients
     */
    private String getRecipients(AppSms sms) {
        String strQuery = """
            SELECT DISTINCT(au.cell_number)
            FROM app_user au
            WHERE au.enabled = true
            AND au.cell_number IS NOT NULL
            AND au.id IN
            (SELECT ur.user_id FROM user_role ur
            WHERE ur.role_id IN (${sms.roleId}))
        """
        List<GroovyRowResult> result = executeSelectSql(strQuery)
        if (result.size() > 0) {
            String recipients = result[0][0].toString()
            for (int i = 1; i < result.size(); i++) {
                recipients += (COMA + result[i][0].toString())
            }
            return recipients
        }
        return null
    }

    /**
     * Send sms
     * @param sms - object of AppSms
     */
    private void sendSms(AppSms sms) {
        sms.body = '"""' + sms.body + '"""'
        sms.body = new GroovyShell().evaluate(sms.body)
        task {smsSenderService.sendSms(sms)}
    }

    private static final String STR_QUERY = """
        UPDATE app_sms SET
            has_send = true,
            version = version + 1,
            updated_by = :updatedBy,
            updated_on = :updatedOn,
            is_active = false
        WHERE id = :id
    """

    /**
     * Update sms object
     * @param sms - object of AppSms
     */
    private void updateSmsObject(AppSms sms) {
        Map queryParams = [
                id       : sms.id,
                updatedBy: super.getAppUser().id,
                updatedOn: DateUtility.getSqlDateWithSeconds(new Date()),
        ]
        executeUpdateSql(STR_QUERY, queryParams)
    }
}
