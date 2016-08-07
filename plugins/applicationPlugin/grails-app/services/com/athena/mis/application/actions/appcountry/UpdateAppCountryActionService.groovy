package com.athena.mis.application.actions.appcountry

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppCountry
import com.athena.mis.application.entity.Company
import com.athena.mis.application.service.AppCountryService
import com.athena.mis.application.service.CompanyService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Update new country object and show in grid
 *  For details go through Use-Case doc named 'UpdateCountryActionService'
 */
class UpdateAppCountryActionService extends BaseService implements ActionServiceIntf {

    AppCountryService appCountryService
    CompanyService companyService

    private Logger log = Logger.getLogger(getClass())

    private final static String COUNTRY_UPDATE_SUCCESS_MESSAGE = " Country has been updated successfully"
    private final static String COUNTRY_OBJECT = "country"
    private final static String OBJ_NOT_FOUND = "Selected country not found"
    private final static String NAME_EXIST_MESSAGE = "Same country name already exists"
    private final static String CODE_EXIST_MESSAGE = "Same country code already exists"
    private final static String NATIONALITY_EXIST_MESSAGE = "Same nationality already exists"
    private final
    static String VERSION_MISMATCH_MSG = "Update failed for version mismatch. Please refresh and try again"

    /**
     * check if country obj exits
     * build new country obj
     * check if country is unique
     * @param parameters
     * @return parameters
     */
    @Transactional(readOnly = true)
    Map executePreCondition(Map parameters) {
        try {
            long countryId = Long.parseLong(parameters.id.toString())
            long version = Long.parseLong(parameters.version.toString())
            AppCountry oldCountry = appCountryService.read(countryId)
            if (!oldCountry) {
                return super.setError(parameters, OBJ_NOT_FOUND)
            }
            if (version != oldCountry.version) {
                return super.setError(parameters, VERSION_MISMATCH_MSG)
            }
            AppCountry country = buildCountryObject(oldCountry, parameters)

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
     * update company if country's currency is changed
     * @param previousResult
     * @return previousResult
     */
    @Transactional
    Map execute(Map previousResult) {
        try {
            AppCountry country = (AppCountry) previousResult.get(COUNTRY_OBJECT)
            appCountryService.update(country)
            updateCompany(country)
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
        return super.setSuccess(executeResult, COUNTRY_UPDATE_SUCCESS_MESSAGE)
    }

    Map buildFailureResultForUI(Map executeResult) {
        return executeResult
    }

    /**
     * Build country object
     * @param params -serialized parameters from UI
     * @return -new country object
     */
    private AppCountry buildCountryObject(AppCountry oldCountry, Map parameterMap) {
        AppCountry newCountry = new AppCountry(parameterMap)
        oldCountry.code = newCountry.code
        oldCountry.isdCode = newCountry.isdCode
        oldCountry.name = newCountry.name
        oldCountry.nationality = newCountry.nationality
        oldCountry.currencyId = Long.parseLong(parameterMap.currencyId.toString())
        oldCountry.updatedOn = new Date()
        oldCountry.updatedBy = super.appUser.id
        if (parameterMap.phoneNumberPattern) {
            oldCountry.phoneNumberPattern = newCountry.phoneNumberPattern      // for super admin
        }
        return oldCountry
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
        int countName = appCountryService.countByNameIlikeAndIdNotEqualAndCompanyId(country.name, country.id, country.companyId)
        if (countName > 0) {
            return NAME_EXIST_MESSAGE
        }
        // check unique country code
        int countCode = appCountryService.countByCodeIlikeAndIdNotEqualAndCompanyId(country.code, country.id, country.companyId)
        if (countCode > 0) {
            return CODE_EXIST_MESSAGE
        }
        // check unique country nationality
        int countNationality = appCountryService.countByNationalityIlikeAndIdNotEqualAndCompanyId(country.nationality, country.id, country.companyId)
        if (countNationality > 0) {
            return NATIONALITY_EXIST_MESSAGE
        }
        return null
    }

    /**
     * update currency of company
     * @param country
     */
    private void updateCompany(AppCountry country) {
        Company company = companyService.read(country.companyId)
        if (company.countryId == country.id && company.currencyId != country.currencyId) {
            company.currencyId = country.currencyId
            companyService.update(company)
        }
    }
}
