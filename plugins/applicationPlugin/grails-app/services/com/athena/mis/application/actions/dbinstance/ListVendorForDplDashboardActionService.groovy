package com.athena.mis.application.actions.dbinstance

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.model.ListVendorForDplDashboardActionServiceModel
import org.apache.log4j.Logger

/**
 * list vendor for Dpl dashboard under settings
 * for details please go through use case named
 */
class ListVendorForDplDashboardActionService extends BaseService implements ActionServiceIntf {
    private final Logger log = Logger.getLogger(getClass())

    private static final String DB_VENDOR_LIST = "dbVendorList"

    Map executePreCondition(Map parameters) {
        return parameters
    }

    /**
     * list vendor for kendo list view
     * @param previousResult
     * @return
     */
    Map execute(Map previousResult) {
        try {
            List<ListVendorForDplDashboardActionServiceModel> vendorList = (List<ListVendorForDplDashboardActionServiceModel>) ListVendorForDplDashboardActionServiceModel.findAllByCompanyId(super.companyId, [sort: 'sequence', order: ASCENDING_SORT_ORDER, readOnly: true])
            previousResult.put(DB_VENDOR_LIST, vendorList)
            return previousResult
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    Map executePostCondition(Map previousResult) {
        return previousResult
    }

    Map buildSuccessResultForUI(Map executeResult) {
        return executeResult
    }

    Map buildFailureResultForUI(Map executeResult) {
        return executeResult
    }
}
