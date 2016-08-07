package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.AppCountry
import com.athena.mis.application.entity.Currency
import com.athena.mis.integration.arms.ArmsPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import org.apache.log4j.Logger

/**
 * CountryService is used to handle only CRUD related object manipulation
 * (e.g. list, read, create, delete etc.)
 */
class AppCountryService extends BaseDomainService {

    private Logger log = Logger.getLogger(getClass())

    CurrencyService currencyService

    public static final String SORT_ON_NAME = "name"

    @Override
    public void init() {
        domainClass = AppCountry.class
    }

    /**
     * Pull country object
     * @return - list of country
     */
    @Override
    public List<AppCountry> list() {
        return AppCountry.findAllByCompanyId(companyId, [sort: AppCountry.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true])
    }

    //for registration page
    public List<AppCountry> list(long companyId) {
        return AppCountry.findAllByCompanyId(companyId, [sort: AppCountry.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true])
    }

    /**
     * count country by name for create
     * @param name - country name
     * @return - count
     */
    public int countByNameIlikeAndCompanyId(String name, long companyId) {
        int count = AppCountry.countByNameIlikeAndCompanyId(name, companyId)
        return count
    }

    /**
     * count country by code for create
     * @param code - country code
     * @return - count
     */
    public int countByCodeIlikeAndCompanyId(String code, long companyId) {
        int count = AppCountry.countByCodeIlikeAndCompanyId(code, companyId)
        return count
    }
    /**
     * count country by nationality for create
     * @param nationality - country nationality
     * @return - count
     */
    public int countByNationalityIlikeAndCompanyId(String nationality, long companyId) {
        int count = AppCountry.countByNationalityIlikeAndCompanyId(nationality, companyId)
        return count
    }
    /**
     * count country by name for create
     * @param name - country name
     * @return - count
     */
    public int countByNameIlikeAndIdNotEqualAndCompanyId(String name, long id, long companyId) {
        int count = AppCountry.countByNameIlikeAndIdNotEqualAndCompanyId(name, id, companyId)
        return count
    }

    /**
     * count country by code for create
     * @param code - country code
     * @return - count
     */
    public int countByCodeIlikeAndIdNotEqualAndCompanyId(String code, long id, long companyId) {
        int count = AppCountry.countByCodeIlikeAndIdNotEqualAndCompanyId(code, id, companyId)
        return count
    }

    /**
     * count country by nationality for create
     * @param nationality - country nationality
     * @return - count
     */
    public int countByNationalityIlikeAndIdNotEqualAndCompanyId(String nationality, long id, long companyId) {
        int count = AppCountry.countByNationalityIlikeAndIdNotEqualAndCompanyId(nationality, id, companyId)
        return count
    }

    public AppCountry findByCodeIlike(String code) {
        AppCountry country = AppCountry.findByCodeIlike(code, [readOnly: true])
        return country
    }

    public AppCountry findByCompanyIdAndCodeIlike(long companyId, String code) {
        AppCountry country = AppCountry.findByCompanyIdAndCodeIlike(companyId, code, [readOnly: true])
        return country
    }

    public AppCountry findByIdAndCompanyId(long id, long companyId) {
        AppCountry country = AppCountry.findByIdAndCompanyId(id, companyId, [readOnly: true])
        return country
    }

    private static final String DELETE_ALL = """
        DELETE FROM app_country WHERE company_id = :companyId
    """

    /**
     * Delete all country by companyId
     * @param companyId - id of company
     */
    public void deleteAllByCompanyId(long companyId) {
        Map queryParams = [
                companyId: companyId
        ]
        executeUpdateSql(DELETE_ALL, queryParams)
    }

    /**
     * applicable only for create default country
     */
    public boolean createDefaultData(long companyId, long systemUserId) {
        try {
            int countByName = countByNameIlikeAndCompanyId('Bangladesh', companyId)
            int countBySymbol = countByCodeIlikeAndCompanyId('BD', companyId)
            boolean isCreate = true
            if (countByName > 0 || countBySymbol > 0) {
                isCreate = false
            }
            if (isCreate) {
                Currency bdtCurrency = currencyService.findBySymbolAndCompanyId("BDT", companyId)
                if (!bdtCurrency) {
                    bdtCurrency = currencyService.findByNameIlikeAndCompanyId('Bangladesh, Taka', companyId)
                }
                new AppCountry(name: "Bangladesh", code: "BD", phoneNumberPattern: "^(011|015|016|017|018|019)\\d{8}\$", isdCode: "+88", currencyId: bdtCurrency.id, nationality: "Bangladeshi", companyId: companyId, createdBy: systemUserId, createdOn: new Date(), updatedBy: 0, updatedOn: null).save()
            }

            if (PluginConnector.isPluginInstalled(ExchangeHousePluginConnector.PLUGIN_NAME) || PluginConnector.isPluginInstalled(ArmsPluginConnector.PLUGIN_NAME)) {
                Currency gbpCurrency = currencyService.findBySymbolAndCompanyId("GBP", companyId)
                Currency audCurrency = currencyService.findBySymbolAndCompanyId("AUD", companyId)
                Currency zarCurrency = currencyService.findBySymbolAndCompanyId("ZAR", companyId)
                Currency usdCurrency = currencyService.findBySymbolAndCompanyId("USD", companyId)
                new AppCountry(name: "South Africa", code: "ZA", phoneNumberPattern: "^[0-9]{10,15}\$", isdCode: "+27", currencyId: zarCurrency.id, nationality: "South African", companyId: companyId, createdBy: systemUserId, createdOn: new Date(), updatedBy: 0, updatedOn: null).save()
                new AppCountry(name: "United Kingdom", code: "UK", phoneNumberPattern: "^[0-9]{10}\$", isdCode: "+44", currencyId: gbpCurrency.id, nationality: "British", companyId: companyId, createdBy: systemUserId, createdOn: new Date(), updatedBy: 0, updatedOn: null).save()
                new AppCountry(name: "Australia", code: "AU", phoneNumberPattern: "^[0-9]{9}\$", isdCode: "+61", currencyId: audCurrency.id, nationality: "Australian", companyId: companyId, createdBy: systemUserId, createdOn: new Date(), updatedBy: 0, updatedOn: null).save()
                new AppCountry(name: "United States", code: "US", phoneNumberPattern: "^[0-9]{11}\$", isdCode: "+1", currencyId: usdCurrency.id, nationality: "American", companyId: companyId, createdBy: systemUserId, createdOn: new Date(), updatedBy: 0, updatedOn: null).save()
            }
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    @Override
    public void createDefaultSchema() {
        String nameIndex = "create unique index app_country_name_company_id_idx on app_country(lower(name), company_id);"
        executeSql(nameIndex)
        String codeIndex = "create unique index app_country_code_company_id_idx on app_country(lower(code), company_id);"
        executeSql(codeIndex)
    }

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }
}
