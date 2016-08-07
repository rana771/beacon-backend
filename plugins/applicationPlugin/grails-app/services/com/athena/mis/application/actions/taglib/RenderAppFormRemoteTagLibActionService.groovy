package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import org.apache.log4j.Logger

class RenderAppFormRemoteTagLibActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    /**
     * check required inputs for form control
     *  1. check if name & url is given
     *
     * @param parameters - a map of given attributes
     * @return - same map of input parameter
     */
    public Map executePreCondition(Map params) {
        try {
            if (!params.name || !params.url) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Build html for form object
     *  1. check & set individual attributes & properties with necessary syntax
     *  2. build the html of form
     *  3. put html object in common map
     *
     * @param parameters - a map of given attributes
     * @return - a map with all previous values including output html
     */

    public Map execute(Map params) {
        try {
            String id = params.id ? params.id : params.name
            params.before ? (params.before = params.before + SEMICOLON) : (params.before = EMPTY_SPACE)
            params.onLoading ? (params.onLoading = params.onLoading + SEMICOLON) : (params.onLoading = EMPTY_SPACE)
            params.onFailure ? (params.onFailure = params.onFailure + SEMICOLON) : (params.onFailure = EMPTY_SPACE)
            params.onComplete ? (params.onComplete = params.onComplete + SEMICOLON) : (params.onComplete = EMPTY_SPACE)
            params.after ? (params.after = params.after + SEMICOLON) : (params.after = EMPTY_SPACE)
            params.update ? (params.update = "jQuery('#${params.update}').html(data);") : (params.update = EMPTY_SPACE)
            params.class ? (params.class = "class='${params.class}'") : (params.class = EMPTY_SPACE)
            if (params.onSuccess) {
                params.onSuccess = params.onSuccess + SEMICOLON
            }

            String html = """<form id="${id}" name="${params.name}" action="${params.url}" method="post" ${
                params.class
            } onsubmit="${params.before}${
                params.onLoading
            }jQuery.ajax({type:'POST',data:jQuery(this).serialize(), url:'${
                params.url
            }',success:function(data,textStatus){${
                params.onSuccess ? params.onSuccess : params.update
            }},error:function(XMLHttpRequest,textStatus,errorThrown){${
                params.onFailure
            }},complete:function(XMLHttpRequest,textStatus){${params.onComplete}}});${params.after} return false;">
            ${params.body()}
            </form>
            """
            params.html = html
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * do not nothing for post condition
     */
    public Map executePostCondition(Map obj) {
        return obj
    }

    /**
     * do not nothing for build success result
     */
    public Map buildSuccessResultForUI(Map obj) {
        return obj
    }

    /**
     * do not nothing for build failure result
     */
    public Map buildFailureResultForUI(Map obj) {
        return obj
    }

}
