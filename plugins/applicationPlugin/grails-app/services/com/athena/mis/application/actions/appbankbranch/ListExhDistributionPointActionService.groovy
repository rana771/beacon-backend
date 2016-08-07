package com.athena.mis.application.actions.appbankbranch

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.service.AppBankService
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import grails.transaction.Transactional
import org.apache.log4j.Logger

/**
 *  Show list of bankBranch for grid on Dash Board
 *  For details go through Use-Case doc named 'ListExhDistributionPointActionService'
 */
class ListExhDistributionPointActionService extends BaseService implements ActionServiceIntf {
    private Logger log = Logger.getLogger(getClass())

    AppBankService appBankService
    ExchangeHousePluginConnector exchangeHouseImplService

    /**
     * do nothing
     * initiate parameters for grid
     * @param parameters
     * @return
     */
    Map executePreCondition(Map parameters) {
        return parameters
    }

    /**
     * check whether query parameters exists
     * if exists search list and count with query params
     * else get total list and count
     * @param previousResult
     * @return
     */
    @Transactional(readOnly = true)
    Map execute(Map previousResult) {
        try {
            long companyId = super.companyId
            long countryId = appBankService.getSystemBank(companyId).countryId
            Closure filterCriteria = {
                eq("countryId", countryId)
            }
            def distributionPointClass = exchangeHouseImplService.getDistributionPointClass()
            Map listResult = getSearchResult(previousResult, distributionPointClass, filterCriteria)
            previousResult.put(LIST, listResult.list)
            previousResult.put(COUNT, listResult.count)
            return previousResult
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    Map executePostCondition(Map previousResult) {
        return previousResult
    }

    /**
     * do nothing
     * @param executeResult
     * @return map
     */
    Map buildSuccessResultForUI(Map executeResult) {
        return executeResult
    }

    Map buildFailureResultForUI(Map executeResult) {
        return executeResult
    }
}
