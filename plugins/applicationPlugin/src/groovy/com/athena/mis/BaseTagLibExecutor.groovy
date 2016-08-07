package com.athena.mis

import grails.converters.JSON

/**
 * Created by Mirza-Ehsan on 13-Apr-2015.
 */
class BaseTagLibExecutor {
    private static final String CLASS_NAME = 'com.athena.mis'
    private static final String MESSAGE = "message"
    private static final String IS_EXCEPTION = "isException"
    private static final String CLASS_SIGNATURE = "classSignature"
    private static final String BREAK = '<br/>'
    private static final String STACK_TRACE = 'stackTrace'
    private static final String IS_ERROR = 'isError'

    public static final String EMPTY_SPACE = ''

    protected void executeTag(ActionServiceIntf action, Map params) {
        Map result = this.getServiceResponse(action, params)
        if (result.isError) {
            result.html = "<span>${result.message}</span>"
        } else if (result.isException) {
            String jsonResult = result as JSON
            result.html = "<a href='javascript:void(0);' onclick='funcSendMailToReportErrorForTag(${jsonResult})'>Please click here to report.</a>"
        }
    }

    private Map getServiceResponse(ActionServiceIntf action, Map params) {

        try {
            // Set default value of Error flag, turn this on only when Error occurs
            params.put(IS_ERROR, Boolean.FALSE)

            // check to see if pre-conditions are met
            Map preResult = action.executePreCondition(params);
            Boolean isError = (Boolean) preResult.isError;
            if (isError.booleanValue() == true) {
                return action.buildFailureResultForUI(preResult);
            }

            // executes the action
            Map executeResult = action.execute(preResult);
            isError = (Boolean) executeResult.isError;
            if (isError.booleanValue() == true) {
                return action.buildFailureResultForUI(preResult);
            }
            // executes the post actions
            Map postResult = action.executePostCondition(executeResult);
            isError = (Boolean) postResult.isError;
            if (isError.booleanValue() == true) {
                return action.buildFailureResultForUI(postResult);
            }
            return action.buildSuccessResultForUI(postResult);

        } catch (RuntimeException re) {
            return unhandledError(params, re)
        }
    }

    private Map unhandledError(Map params, RuntimeException ex) {
        Map exProperties = ex.getProperties();
        List<StackTraceElement> lstStackTraceElement = (List<StackTraceElement>) exProperties.get(STACK_TRACE)
        String classSignature = EMPTY_SPACE
        lstStackTraceElement.each {
            if (it.className.startsWith(CLASS_NAME) && (it.lineNumber > 0)) {
                classSignature = classSignature + it.toString() + BREAK
            }
        }
        params.put(MESSAGE, exProperties.get(MESSAGE))
        params.put(CLASS_SIGNATURE, classSignature)
        params.put(IS_EXCEPTION, Boolean.TRUE)
        return params
    }
}
