package com.athena.mis.application.actions.company

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppCountry
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Company
import com.athena.mis.application.entity.Currency
import com.athena.mis.application.model.ListCompanyForResellerActionServiceModel
import com.athena.mis.application.service.AppCountryService
import com.athena.mis.application.service.CompanyService
import com.athena.mis.application.service.CurrencyService
import com.athena.mis.application.service.ListCompanyForResellerActionServiceModelService
import com.athena.mis.integration.application.AppBootStrapService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to create company CRUD and to show on grid list
 *  For details go through Use-Case doc named 'CreateCompanyForResellerActionService'
 */
class CreateCompanyForResellerActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String COMPANY = "company"
    private static final String COUNTRY = "country"
    private static final String CURRENCY = "currency"
    private static final String COMPANY_SAVE_SUCCESS_MESSAGE = "Company has been saved successfully"
    private static final String COMPANY_ALREADY_EXISTS = "Same company name already exists"
    private static final String CODE_ALREADY_EXISTS = "Same company code already exists"
    private static final String URL_ALREADY_EXISTS = "Same company url already exists"
    private final static String COUNTRY_NAME_EXIST_MESSAGE = "Same country name already exists"
    private final static String COUNTRY_CODE_EXIST_MESSAGE = "Same country code already exists"
    private final static String NATIONALITY_EXIST_MESSAGE = "Same nationality already exists"
    private static final String CURRENCY_NAME_EXIST_MESSAGE = "Same currency name already exists"
    private static final String SYMBOL_EXIST_MESSAGE = "Same currency symbol already exists"
    private static final String DEFAULT_PHONE_NUMBER_PATTERN = '[0-9]{11}'

    AppBootStrapService appBootStrapService
    CompanyService companyService
    AppCountryService appCountryService
    CurrencyService currencyService
    ListCompanyForResellerActionServiceModelService listCompanyForResellerActionServiceModelService

    /**
     * Create company, country & currency
     * 1. get company id from sequence
     * 2. build currency, country & company object
     * 3. check uniqueness of different properties
     * @param params -serialized parameters from UI
     * @return -a map containing company object and companyLogo for execute method
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Map executePreCondition(Map params) {
        try {
            if (!params.name || !params.code || !params.webUrl) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long companyId = companyService.getCompanyIdFromSequence()
            Currency currency = buildCurrencyObject(params, companyId)
            AppCountry country = buildCountryObject(params, companyId)
            Company company = buildCompanyObject(params, companyId)
            String companyMsg = checkUniqueness(company)
            if (companyMsg) {
                super.setError(params, companyMsg)
            }
            String countryMsg = checkCountryUniqueness(country)
            if (countryMsg) {
                super.setError(params, countryMsg)
            }
            String currencyMsg = checkCurrencyUniqueness(currency)
            if (currencyMsg) {
                super.setError(params, currencyMsg)
            }
            params.put(COMPANY, company)
            params.put(COUNTRY, country)
            params.put(CURRENCY, currency)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Save company,country & currency object in DB
     * @param result -company,country & currency object from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Map execute(Map result) {
        try {
            Currency currency = (Currency) result.get(CURRENCY)
            AppCountry country = (AppCountry) result.get(COUNTRY)
            Company company = (Company) result.get(COMPANY)
            currencyService.create(currency)
            country.currencyId = currency.id
            appCountryService.create(country)
            company.countryId = country.id
            company.currencyId = currency.id
            companyService.create(company)

            // load default data for new company
            appBootStrapService.init(company)

            // update company property for default data
            company.isDefaultDataLoaded = true
            companyService.update(company)
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
     * Wrap newly created company to show on grid
     * @param result -newly created Company object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map buildSuccessResultForUI(Map result) {
        try {
            Map successResultMap = new LinkedHashMap()
            Company company = (Company) result.get(COMPANY)
            ListCompanyForResellerActionServiceModel companyModel = listCompanyForResellerActionServiceModelService.read(company.id)
            successResultMap.put(COMPANY, companyModel)
            return super.setSuccess(successResultMap, COMPANY_SAVE_SUCCESS_MESSAGE)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Map buildFailureResultForUI(Map result) {
        Map failureResultMap = new LinkedHashMap()
        return super.setError(failureResultMap, result.get(MESSAGE).toString())
    }

    /**
     * 1. ensure unique company name
     * 2. ensure unique company code
     * 3. ensure unique company webUrl
     * @param company -object of Company
     * @return -a string containing error message or null value
     */
    private String checkUniqueness(Company company) {
        // check unique company name
        int duplicateNameCount = companyService.countByNameIlike(company.name)
        if (duplicateNameCount > 0) {
            return COMPANY_ALREADY_EXISTS
        }
        // check unique Company Code
        int duplicateCodeCount = companyService.countByCodeIlike(company.code)
        if (duplicateCodeCount > 0) {
            return CODE_ALREADY_EXISTS
        }
        // check unique company webUrl
        int urlCount = companyService.countByWebUrlIlike(company.webUrl)
        if (urlCount > 0) {
            return URL_ALREADY_EXISTS
        }
        return null
    }

    /**
     * 1. ensure unique country name
     * 2. ensure unique country code
     * 3. ensure unique country ISD code
     * 4. ensure unique country nationality
     * @param country -object of Country
     * @return -a string containing error message or null value
     */
    private String checkCountryUniqueness(AppCountry country) {
        // check unique country name
        int countName = appCountryService.countByNameIlikeAndCompanyId(country.name, country.companyId)
        if (countName > 0) {
            return COUNTRY_NAME_EXIST_MESSAGE
        }
        // check unique country code
        int countCode = appCountryService.countByCodeIlikeAndCompanyId(country.code, country.companyId)
        if (countCode > 0) {
            return COUNTRY_CODE_EXIST_MESSAGE
        }
        // check unique country nationality
        int countNationality = appCountryService.countByNationalityIlikeAndCompanyId(country.nationality, country.companyId)
        if (countNationality > 0) {
            return NATIONALITY_EXIST_MESSAGE
        }
        return null
    }

    /**
     * 1. ensure uniqueness of Currency name
     * 2. ensure uniqueness of Currency symbol
     * @param currency -object of Currency
     * @return -a string containing error message or null value depending on unique check
     */
    private String checkCurrencyUniqueness(Currency currency) {
        // check for duplicate Currency name
        int countName = currencyService.countByNameIlikeAndCompanyId(currency.name, currency.companyId)
        if (countName > 0) {
            return CURRENCY_NAME_EXIST_MESSAGE
        }
        // check for duplicate Currency symbol
        int countCode = currencyService.countBySymbolIlikeAndCompanyId(currency.symbol, currency.companyId)
        if (countCode > 0) {
            return SYMBOL_EXIST_MESSAGE
        }
        return null
    }

    private Currency buildCurrencyObject(Map params, long companyId) {
        Currency currency = new Currency(params)
        currency.name = params.currencyName.toString()
        currency.companyId = companyId
        currency.createdOn = new Date()
        currency.createdBy = super.getAppUser().id
        return currency
    }

    private AppCountry buildCountryObject(Map params, long companyId) {
        params.currencyId = 0L
        AppCountry country = new AppCountry(params)
        country.companyId = companyId
        AppUser appUser = super.appUser
        if (params.phoneNumberPattern) {
            country.phoneNumberPattern = params.phoneNumberPattern
        } else {
            country.phoneNumberPattern = DEFAULT_PHONE_NUMBER_PATTERN
        }
        country.name = params.countryName.toString()
        country.code = params.countryCode.toString()
        country.createdOn = new Date()
        country.createdBy = appUser.id
        return country
    }

    private Company buildCompanyObject(Map params, long companyId) {
        params.countryId = 0L
        Company company = new Company(params)
        company.isActive = false
        company.id = companyId
        company.createdBy = super.getAppUser().id
        company.createdOn = new Date()
        return company
    }
}
