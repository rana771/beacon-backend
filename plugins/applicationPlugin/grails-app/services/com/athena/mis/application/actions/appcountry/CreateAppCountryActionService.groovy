package com.athena.mis.application.actions.appcountry

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppCountry
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.service.AppCountryService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Create new designation object and show in grid
 *  For details go through Use-Case doc named 'CreateCountryActionService'
 */
class CreateAppCountryActionService extends BaseService implements ActionServiceIntf {

    AppCountryService appCountryService

    private Logger log = Logger.getLogger(getClass())

    private final static String COUNTRY_CREATE_SUCCESS_MESSAGE = "Country has been successfully saved"
    private final static String COUNTRY_OBJECT = "country"
    private final static String NAME_EXIST_MESSAGE = "Same country name already exists"
    private final static String CODE_EXIST_MESSAGE = "Same country code already exists"
    private final static String NATIONALITY_EXIST_MESSAGE = "Same nationality already exists"
    private static final String DEFAULT_PHONE_NUMBER_PATTERN = '[0-9]{11}'

    /**
     * 1. Build country object
     * 2. check uniqueness of country properties
     * @param parameters
     * @return parameters
     */
    @Transactional(readOnly = true)
    Map executePreCondition(Map parameters) {
        try {
            AppCountry country = buildCountryObject(parameters)
            // check uniqueness
            String msg = checkUniqueness(country)
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
     * save country obj to DB
     * @param previousResult
     * @return
     */
    @Transactional
    Map execute(Map previousResult) {
        try {
            AppCountry country = (AppCountry) previousResult.get(COUNTRY_OBJECT)
            appCountryService.create(country)
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
        return super.setSuccess(executeResult, COUNTRY_CREATE_SUCCESS_MESSAGE)
    }

    Map buildFailureResultForUI(Map executeResult) {
        return executeResult
    }

    /**
     * Build country object
     * @param params -serialized parameters from UI
     * @return -new country object
     */
    private AppCountry buildCountryObject(Map params) {
        AppCountry country = new AppCountry(params)
        AppUser appUser = super.appUser
        country.companyId = appUser.companyId
        if (params.phoneNumberPattern) {
            country.phoneNumberPattern = params.phoneNumberPattern     // for super admin
        } else {
            country.phoneNumberPattern = DEFAULT_PHONE_NUMBER_PATTERN      // for admin
        }
        country.createdOn = new Date()
        country.createdBy = appUser.id
        country.updatedOn = null
        country.updatedBy = 0L
        return country
    }

    /**
     * 1. ensure unique country name
     * 2. ensure unique country code
     * 3. ensure unique country ISD code
     * 4. ensure unique country nationality
     * @param country -object of Country
     * @return -a string containing error message or null value
     */
    private String checkUniqueness(AppCountry country) {
        // check unique country name
        int countName = appCountryService.countByNameIlikeAndCompanyId(country.name, country.companyId)
        if (countName > 0) {
            return NAME_EXIST_MESSAGE
        }
        // check unique country code
        int countCode = appCountryService.countByCodeIlikeAndCompanyId(country.code, country.companyId)
        if (countCode > 0) {
            return CODE_EXIST_MESSAGE
        }
        // check unique country nationality
        int countNationality = appCountryService.countByNationalityIlikeAndCompanyId(country.nationality, country.companyId)
        if (countNationality > 0) {
            return NATIONALITY_EXIST_MESSAGE
        }
        return null
    }
}
