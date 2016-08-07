package com.athena.mis.application.actions.supplier

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Supplier
import org.apache.log4j.Logger

/**
 *  Class to get wrappedList of supplier object to show on right grid of : Procurement->SupplierWise PO report
 *  For details go through Use-Case doc named 'GetSupplierListActionService'
 */
class GetSupplierListActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    /**
     * do nothing at pre condition
     */
    public Map executePreCondition(Map params) {
        return params
    }

    /**
     * get list of supplier objects
     * @param params -parameters from UI
     * @param obj -N/A
     * @return -a map contains supplierList & count for buildSuccessResultForUI
     */
    public Map execute(Map result) {
        try {
            Map resultMap = getSearchResult(result, Supplier.class)
            result.put(LIST, resultMap.list)
            result.put(COUNT, resultMap.count)
            return result

        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * do nothing at post condition
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * Wrapped supplier object list to show on grid
     * @param obj -a map contains supplierList and count
     * @return -a map contains wrapped supplierList to show on grid
     */
    public Map buildSuccessResultForUI(Map result) {
        return  result
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

}
