package com.athena.mis

class BaseJobExecutor { //@todo: handle exception here & send mail

    private static final String IS_ERROR = 'isError'

    protected void executeJob(ActionServiceIntf action, Map params) {
        Map result = this.getServiceResponse(action, params)
    }

    private Map getServiceResponse(ActionServiceIntf action, Map params) {

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
    }

}
