package com.athena.mis.application.actions.vendor

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Vendor
import com.athena.mis.application.service.VendorService
import grails.transaction.Transactional
import org.apache.log4j.Logger

class RenderVendorThumbImageActionService extends BaseService implements ActionServiceIntf {
    private final Logger log = Logger.getLogger(getClass())

    private static final String STREAM = "stream"

    VendorService vendorService

    Map executePreCondition(Map parameters) {
        try {
            if (!parameters.id) {
                return super.setError(parameters, ERROR_FOR_INVALID_INPUT)
            }
            return parameters
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    @Transactional(readOnly = true)
    Map execute(Map previousResult) {
        try {
            long id = Long.parseLong(previousResult.id.toString())
            Vendor vendor = vendorService.read(id)
            previousResult.put(STREAM, vendor.thumbImage)
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
