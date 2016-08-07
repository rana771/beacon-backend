package com.athena.mis.application.actions.appcountry

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppCountry
import com.athena.mis.application.service.AppCountryService
import com.athena.mis.integration.arms.ArmsPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Delete country object from DB and cache utility and remove it from grid
 *  For details go through Use-Case doc named 'DeleteCountryActionService'
 */
class DeleteAppCountryActionService extends BaseService implements ActionServiceIntf {

    AppCountryService appCountryService
    @Autowired(required = false)
    ExchangeHousePluginConnector exhImplService
    @Autowired(required = false)
    ArmsPluginConnector armsImplService


    private Logger log = Logger.getLogger(getClass())

    private static final String COUNTRY_DELETE_SUCCESS_MSG = "Country has been successfully deleted!"
    private static final String COUNTRY_NOT_FOUND_MESSAGE = "Selected country not found"
    private static final String COUNTRY_OBJECT = "country"

    private static final String HAS_ASSOCIATION_MESSAGE_COMPANY = " Company(s) Associated with selected Country"
    private static final String HAS_ASSOCIATION_MESSAGE_DISTRICT = " District(s) Associated with selected Country"
    private static final String HAS_ASSOCIATION_MESSAGE_BANK = " Bank(s) Associated with selected Country"
    private static final String HAS_ASSOCIATION_MESSAGE_EXH_AGENT = " Agent(s) Associated with selected Country"
    private static
    final String HAS_ASSOCIATION_MESSAGE_EXH_BENEFICIARY = " Beneficiary(s) Associated with selected Country"
    private static final String HAS_ASSOCIATION_MESSAGE_EXH_CUSTOMER = " Customer(s) Associated with selected Country"
    private static
    final String HAS_ASSOCIATION_MESSAGE_EXH_CUSTOMER_TRACE = " Customer Trace(s) Associated with selected Country"
    private static final String HAS_ASSOCIATION_MESSAGE_EXH_TASK = " Task(s) Associated with selected Country"
    private static
    final String HAS_ASSOCIATION_MESSAGE_EXH_TASK_TRACE = " Task Trace(s) Associated with selected Country"
    private static
    final String HAS_ASSOCIATION_MESSAGE_RMS_EXH = " Exchange House(s) Associated with selected Country"
    private static
    final String HAS_ASSOCIATION_MESSAGE_RMS_TASK = " ARMS Task(s) Associated with selected Country"


    /**
     * pull country obj by id
     * check if country exists
     * check association for country for delete
     * @param parameters
     * @return parameters
     */
    @Transactional(readOnly = true)
    Map executePreCondition(Map parameters) {
        try {
            long countryId = Long.parseLong(parameters.id.toString())
            AppCountry country = appCountryService.read(countryId)
            if (!country) {
                return super.setError(parameters, COUNTRY_NOT_FOUND_MESSAGE)
            }

            String msg = checkAssociationForDelete(country.id)
            if (msg) {
                return super.setError(parameters, msg)
            }
            parameters.put(COUNTRY_OBJECT, country)
            return parameters
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * delete country obj from DB
     * @param previousResult
     * @return previousResult
     */
    @Transactional
    Map execute(Map previousResult) {
        try {
            AppCountry country = (AppCountry) previousResult.get(COUNTRY_OBJECT)
            appCountryService.delete(country)
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
        return super.setSuccess(executeResult, COUNTRY_DELETE_SUCCESS_MSG)
    }

    Map buildFailureResultForUI(Map executeResult) {
        return executeResult
    }

    /**
     * check association of country
     * @param countryId
     * @return association msg
     */
    private String checkAssociationForDelete(long countryId) {
        int count
        String msg

        count = countCompany(countryId)
        if (count > 0) {
            msg = count.toString() + HAS_ASSOCIATION_MESSAGE_COMPANY
            return msg
        }

        count = countDistrict(countryId)
        if (count > 0) {
            msg = count.toString() + HAS_ASSOCIATION_MESSAGE_DISTRICT
            return msg
        }

        count = countBank(countryId)
        if (count > 0) {
            msg = count.toString() + HAS_ASSOCIATION_MESSAGE_BANK
            return msg
        }

        if(exhImplService){
            count = countExhAgent(countryId)
            if (count > 0) {
                msg = count.toString() + HAS_ASSOCIATION_MESSAGE_EXH_AGENT
                return msg
            }
            count = countExhBeneficiary(countryId)
            if (count > 0) {
                msg = count.toString() + HAS_ASSOCIATION_MESSAGE_EXH_BENEFICIARY
                return msg
            }
            count = countExhCustomer(countryId)
            if (count > 0) {
                msg = count.toString() + HAS_ASSOCIATION_MESSAGE_EXH_CUSTOMER
                return msg
            }
            count = countExhCustomerTrace(countryId)
            if (count > 0) {
                msg = count.toString() + HAS_ASSOCIATION_MESSAGE_EXH_CUSTOMER_TRACE
                return msg
            }
            count = countExhTask(countryId)
            if (count > 0) {
                msg = count.toString() + HAS_ASSOCIATION_MESSAGE_EXH_TASK
                return msg
            }
            count = countExhTaskTrace(countryId)
            if (count > 0) {
                msg = count.toString() + HAS_ASSOCIATION_MESSAGE_EXH_TASK_TRACE
                return msg
            }
        }
        if(armsImplService){
            count = countRmsExchangeHouse(countryId)
            if (count > 0) {
                msg = count.toString() + HAS_ASSOCIATION_MESSAGE_RMS_EXH
                return msg
            }

            count = countRmsTask(countryId)
            if (count > 0) {
                msg = count.toString() + HAS_ASSOCIATION_MESSAGE_RMS_TASK
                return msg
            }
        }
        return null
    }

    private static final String COUNT_COMPANY = """
        SELECT COUNT(id) AS count
        FROM company
        WHERE country_id=:countryId
    """

    private int countCompany(long countryId) {
        Map queryParams = [
                countryId: countryId
        ]
        List results = executeSelectSql(COUNT_COMPANY, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_DISTRICT = """
        SELECT COUNT(id) AS count
        FROM district
        WHERE country_id=:countryId
    """

    private int countDistrict(long countryId) {
        Map queryParams = [
                countryId: countryId
        ]
        List results = executeSelectSql(COUNT_DISTRICT, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_BANK = """
        SELECT COUNT(id) AS count
        FROM app_bank
        WHERE country_id=:countryId
    """

    private int countBank(long countryId) {
        Map queryParams = [
                countryId: countryId
        ]
        List results = executeSelectSql(COUNT_BANK, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_EXH_AGENT = """
        SELECT COUNT(id) AS count
        FROM exh_agent
        WHERE country_id=:countryId
    """

    private int countExhAgent(long countryId) {
        Map queryParams = [
                countryId: countryId
        ]
        List results = executeSelectSql(COUNT_EXH_AGENT, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_EXH_BENEFICIARY = """
        SELECT COUNT(id) AS count
        FROM exh_beneficiary
        WHERE country_id=:countryId
    """

    private int countExhBeneficiary(long countryId) {
        Map queryParams = [
                countryId: countryId
        ]
        List results = executeSelectSql(COUNT_EXH_BENEFICIARY, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_EXH_CUSTOMER = """
        SELECT COUNT(id) AS count
        FROM exh_customer
        WHERE country_id=:countryId
    """

    private int countExhCustomer(long countryId) {
        Map queryParams = [
                countryId: countryId
        ]
        List results = executeSelectSql(COUNT_EXH_CUSTOMER, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_EXH_CUSTOMER_TRACE = """
        SELECT COUNT(id) AS count
        FROM exh_customer_trace
        WHERE country_id=:countryId
    """

    private int countExhCustomerTrace(long countryId) {
        Map queryParams = [
                countryId: countryId
        ]
        List results = executeSelectSql(COUNT_EXH_CUSTOMER_TRACE, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_EXH_TASK = """
        SELECT COUNT(id) AS count
        FROM exh_task
        WHERE receiving_country_id=:countryId
    """

    private int countExhTask(long countryId) {
        Map queryParams = [
                countryId: countryId
        ]
        List results = executeSelectSql(COUNT_EXH_TASK, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_EXH_TASK_TRACE = """
        SELECT COUNT(id) AS count
        FROM exh_task_trace
        WHERE receiving_country_id=:countryId
    """

    private int countExhTaskTrace(long countryId) {
        Map queryParams = [
                countryId: countryId
        ]
        List results = executeSelectSql(COUNT_EXH_TASK_TRACE, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_RMS_EXH = """
        SELECT COUNT(id) AS count
        FROM rms_exchange_house
        WHERE country_id=:countryId
    """

    private int countRmsExchangeHouse(long countryId) {
        Map queryParams = [
                countryId: countryId
        ]
        List results = executeSelectSql(COUNT_RMS_EXH, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_RMS_TASK = """
        SELECT COUNT(id) AS count
        FROM rms_task
        WHERE country_id=:countryId
    """

    private int countRmsTask(long countryId) {
        Map queryParams = [
                countryId: countryId
        ]
        List results = executeSelectSql(COUNT_RMS_TASK, queryParams)
        int count = results[0].count
        return count
    }
}
