package com.athena.mis.application.exception

import com.athena.mis.ActionServiceIntf

/**
 * Created by Mirza-Ehsan on 06-May-2015.
 */
class AppRuntimeException extends RuntimeException {

    private static final String IS_ERROR = 'isError'
    private static final String IS_APP_EXCEPTION = 'isAppException'
    private static final String MESSAGE = 'message'

    private ActionServiceIntf actionServiceIntf
    private Map params

    public AppRuntimeException(ActionServiceIntf action, Map params, String msg) {
        this.actionServiceIntf = action
        params.put(IS_ERROR, Boolean.TRUE)
        params.put(IS_APP_EXCEPTION, Boolean.TRUE)
        params.put(MESSAGE, msg)
        this.params = params
    }

    Map getParams() {
        return params
    }

    ActionServiceIntf getActionServiceIntf() {
        return actionServiceIntf
    }
}
