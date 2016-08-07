package com.athena.mis.application.actions.contentcategory

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.service.ContentCategoryService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Get ContentCategory list by content type id (SystemEntity.id e.g: Document, Image) used in AppAttachment CRUD
 *  For details go through Use-Case doc named 'GetContentCategoryListByContentTypeIdActionService'
 */
class GetContentCategoryListByContentTypeIdActionService extends BaseService implements ActionServiceIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String CONTENT_CATEGORY_LIST = "contentCategoryList"

    ContentCategoryService contentCategoryService

    /**
     * 1. check required parameters
     *
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePreCondition(Map params) {
        try {
            //check required parameters
            if (!params.contentTypeId) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * 1. Get ContentCategory list by contentTypeId and isReserved (false)
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map execute(Map result) {
        try {
            long contentTypeId = Long.parseLong(result.contentTypeId.toString())
            // get ContentCategory list by contentTypeId and isReserved (false)
            List lstContentCategory = contentCategoryService.listByContentTypeId(contentTypeId)
            List lstDropDown = contentCategoryService.listForKendoDropdown(lstContentCategory, null, null)
            result.put(CONTENT_CATEGORY_LIST, lstDropDown)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
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
     * There is no Build success, so return the same map as received
     *
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
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
}
