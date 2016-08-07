package com.athena.mis.application.actions.company

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppCountry
import com.athena.mis.application.entity.Company
import com.athena.mis.application.entity.Currency
import com.athena.mis.application.model.ListCompanyForResellerActionServiceModel
import com.athena.mis.application.service.*
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to update company CRUD and to show on grid list
 *  For details go through Use-Case doc named 'UpdateCompanyForResellerActionService'
 */
class UpdateCompanyForResellerActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String COMPANY = "company"
    private static final String COUNTRY = "country"
    private static final String CURRENCY = "currency"
    private static final String NO_COMPANY_USER = "Selected Company has no Company User to activate company"
    private static final String COMPANY_UPDATE_SUCCESS_MESSAGE = "Company has been updated successfully"
    private static final String COMPANY_ALREADY_EXISTS = "Same company name already exists"
    private static final String CODE_ALREADY_EXISTS = "Same company code already exists"
    private static final String URL_ALREADY_EXISTS = "Same company url already exists"
    private final static String COUNTRY_NAME_EXIST_MESSAGE = "Same country name already exists"
    private final static String COUNTRY_CODE_EXIST_MESSAGE = "Same country code already exists"
    private final static String NATIONALITY_EXIST_MESSAGE = "Same nationality already exists"
    private static final String CURRENCY_NAME_EXIST_MESSAGE = "Same currency name already exists"
    private static final String SYMBOL_EXIST_MESSAGE = "Same currency symbol already exists"

    CompanyService companyService
    AppCountryService appCountryService
    CurrencyService currencyService
    AppUserService appUserService
    ListCompanyForResellerActionServiceModelService listCompanyForResellerActionServiceModelService

    /**
     * 1. validate Company,currency & country object
     * 2. check existence of selected country
     * 3. check uniqueness of different properties
     * @param params -serialized parameters from UI
     * @return -a map containing company object and companyLogo for execute method
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            if (!params.currencyId || !params.countryId || !params.id) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long currencyId = Long.parseLong(params.currencyId)
            long countryId = Long.parseLong(params.countryId)
            long companyId = Long.parseLong(params.id)
            if(params.isActive == 'on'){
                int count = appUserService.countByIsCompanyUserAndCompanyId(companyId)
                if(count == 0){
                    return super.setError(params, NO_COMPANY_USER)
                }
            }
            if (params.dirtyCurrency=='true') {
                Currency oldCurrency = currencyService.read(currencyId)
                Currency currency = buildCurrencyObject(oldCurrency, params)
                String currencyMsg = checkCurrencyUniqueness(currency)
                if (currencyMsg) {
                    super.setError(params, currencyMsg)
                }
                params.put(CURRENCY, currency)
            }
            if (params.dirtyCountry=='true') {
                AppCountry oldCountry = appCountryService.read(countryId)
                AppCountry country = buildCountryObject(oldCountry, params)
                String countryMsg = checkCountryUniqueness(country)
                if (countryMsg) {
                    super.setError(params, countryMsg)
                }
                params.put(COUNTRY, country)
            }

            Company oldCompany = companyService.read(companyId)
            Company company = buildCompanyObject(oldCompany, params)
            String companyMsg = checkUniqueness(company)
            if (companyMsg) {
                super.setError(params, companyMsg)
            }

            params.put(COMPANY, company)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Update company,currency & country object in DB
     * @param result -company,currency & country object from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Map execute(Map result) {
        try {
            Company company = (Company) result.get(COMPANY)
            companyService.update(company)
            if (result.dirtyCountry=='true') {
                AppCountry country = (AppCountry) result.get(COUNTRY)
                appCountryService.update(country)
            }
            if (result.dirtyCurrency=='true') {
                Currency currency = (Currency) result.get(CURRENCY)
                currencyService.update(currency)
            }
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
            return super.setSuccess(successResultMap, COMPANY_UPDATE_SUCCESS_MESSAGE)
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
        int duplicateNameCount = companyService.countByNameIlikeAndIdNotEqual(company.name, company.id)
        if (duplicateNameCount > 0) {
            return COMPANY_ALREADY_EXISTS
        }
        // check unique Company Code
        int duplicateCodeCount = companyService.countByCodeIlikeAndIdNotEqual(company.code, company.id)
        if (duplicateCodeCount > 0) {
            return CODE_ALREADY_EXISTS
        }
        // check unique company webUrl
        int urlCount = companyService.countByWebUrlIlikeAndIdNotEqual(company.webUrl, company.id)
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
        int countName = appCountryService.countByNameIlikeAndIdNotEqualAndCompanyId(country.name, country.id, country.companyId)
        if (countName > 0) {
            return COUNTRY_NAME_EXIST_MESSAGE
        }
        // check unique country code
        int countCode = appCountryService.countByCodeIlikeAndIdNotEqualAndCompanyId(country.code, country.id, country.companyId)
        if (countCode > 0) {
            return COUNTRY_CODE_EXIST_MESSAGE
        }
        // check unique country nationality
        int countNationality = appCountryService.countByNationalityIlikeAndIdNotEqualAndCompanyId(country.nationality, country.id, country.companyId)
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
        int countName = currencyService.countByNameIlikeAndCompanyIdAndIdNotEqual(currency.name, currency.companyId, currency.id)
        if (countName > 0) {
            return CURRENCY_NAME_EXIST_MESSAGE
        }
        // check for duplicate Currency symbol
        int countCode = currencyService.countBySymbolIlikeAndCompanyIdAndIdNotEqual(currency.symbol, currency.companyId, currency.id)
        if (countCode > 0) {
            return SYMBOL_EXIST_MESSAGE
        }
        return null
    }

    private Currency buildCurrencyObject(Currency OldCurrency, Map params) {
        OldCurrency.name = params.currencyName.toString()
        OldCurrency.symbol = params.symbol.toString()
        OldCurrency.otherCode = params.otherCode.toString()
        OldCurrency.updatedOn = new Date()
        OldCurrency.updatedBy = super.getAppUser().id
        return OldCurrency
    }

    private AppCountry buildCountryObject(AppCountry oldCountry, Map params) {
        oldCountry.name = params.countryName.toString()
        oldCountry.code = params.countryCode.toString()
        oldCountry.isdCode = params.isdCode.toString()
        oldCountry.nationality = params.nationality.toString()
        oldCountry.phoneNumberPattern = params.phoneNumberPattern
        oldCountry.updatedOn = new Date()
        oldCountry.updatedBy = super.getAppUser().id
        return oldCountry
    }

    private Company buildCompanyObject(Company oldCompany, Map params) {
        oldCompany.name = params.name.toString()
        oldCompany.title = params.title.toString()
        oldCompany.code = params.code.toString()
        oldCompany.webUrl = params.webUrl.toString()
        oldCompany.contactName = params.contactName.toString()
        oldCompany.contactSurname = params.contactSurname.toString()
        oldCompany.contactPhone = params.contactPhone.toString()
        oldCompany.address1 = params.address1.toString()
        oldCompany.address2 = params.address2.toString()
        oldCompany.isActive = params.isActive ? params.isActive : false
        oldCompany.updatedBy = super.getAppUser().id
        oldCompany.updatedOn = new Date()
        return oldCompany
    }
}
