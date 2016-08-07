package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.AppBank
import com.athena.mis.application.entity.AppCountry
import com.athena.mis.application.entity.Company
import com.athena.mis.application.entity.Currency
import com.athena.mis.integration.arms.ArmsPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import org.apache.log4j.Logger

/**
 * CurrencyService is used to handle only CRUD related object manipulation
 * (e.g. list, read, create, delete etc.)
 */
class CurrencyService extends BaseDomainService {

    private Logger log = Logger.getLogger(getClass())

    CompanyService companyService
    AppCountryService appCountryService
    AppBankService appBankService

    @Override
    public void init() {
        domainClass = Currency.class
    }

    /**
     * Pull currency object
     * @return - list of currency
     */
    @Override
    public List<Currency> list() {
        List<Currency> currencyList = Currency.findAllByCompanyId(
                super.companyId, [readOnly: true, sort: Currency.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER])
        return currencyList
    }

    public int countByNameIlikeAndCompanyId(String name, long companyId) {
        return Currency.countByNameIlikeAndCompanyId(name, companyId)
    }

    public int countByNameIlikeAndCompanyIdAndIdNotEqual(String name, long companyId, int id) {
        return Currency.countByNameIlikeAndCompanyIdAndIdNotEqual(name, companyId, id)
    }

    public int countBySymbolIlikeAndCompanyId(String symbol, long companyId) {
        return Currency.countBySymbolIlikeAndCompanyId(symbol, companyId)
    }

    public int countBySymbolIlikeAndCompanyIdAndIdNotEqual(String symbol, long companyId, int id) {
        return Currency.countBySymbolIlikeAndCompanyIdAndIdNotEqual(symbol, companyId, id)
    }

    public Currency readLocalCurrency() {
        Company company = companyService.read(super.companyId)
        Currency currency = read(company.currencyId)
        return currency
    }

    public Currency readLocalCurrencyByCompanyId(long companyId) {
        Company company = companyService.read(companyId)
        Currency currency = read(company.currencyId)
        return currency
    }

    public Currency readForeignCurrency() {
        AppBank bank = appBankService.getSystemBank(super.companyId)
        Currency currency = readByCountryId(bank.countryId)
        return currency
    }

    public Currency readByCountryId(long countryId) {
        AppCountry country = appCountryService.read(countryId)
        if (!country) return null
        Currency currency = read(country.currencyId)
        return currency
    }

    public Currency readBySymbol(String symbol) {
        return Currency.findBySymbolIlike(symbol, [readOnly: true])
    }

    public Currency findBySymbolAndCompanyId(String symbol, long companyId) {
        Currency currency = Currency.findBySymbolIlikeAndCompanyId(symbol, companyId, [readOnly: true])
        return currency
    }

    public Currency findByNameIlikeAndCompanyId(String name, long companyId) {
        return Currency.findByNameIlikeAndCompanyId(name, companyId, [readOnly: true])
    }

    private static final String DELETE_ALL = """
        DELETE FROM currency WHERE company_id = :companyId
    """

    /**
     * Delete all currency by companyId
     * @param companyId - id of company
     */
    public void deleteAllByCompanyId(long companyId) {
        Map queryParams = [
                companyId: companyId
        ]
        executeUpdateSql(DELETE_ALL, queryParams)
    }

    /**
     * applicable only for create default currency
     */
    public boolean createDefaultData(long companyId, long systemUserId) {
        try {
            int countByName = Currency.countByNameIlikeAndCompanyId('Bangladesh, Taka', companyId)
            int countBySymbol = Currency.countBySymbolIlikeAndCompanyId('BDT', companyId)
            boolean isCreate = true
            if (countByName > 0 || countBySymbol > 0) {
                isCreate = false
            }
            if (isCreate) {
                new Currency(name: "Bangladesh, Taka", symbol: "BDT", companyId: companyId, createdBy: systemUserId, createdOn: new Date(), updatedBy: 0, updatedOn: null, otherCode: "050").save()
            }

            if (PluginConnector.isPluginInstalled(ExchangeHousePluginConnector.PLUGIN_NAME) || PluginConnector.isPluginInstalled(ArmsPluginConnector.PLUGIN_NAME)) {
                new Currency(name: 'Pound Sterling', symbol: 'GBP', companyId: companyId, createdBy: systemUserId, createdOn: new Date(), updatedBy: 0, updatedOn: null).save()
                new Currency(name: 'Australian Dollar', symbol: 'AUD', companyId: companyId, createdBy: systemUserId, createdOn: new Date(), updatedBy: 0, updatedOn: null).save()
                new Currency(name: 'South African Rand', symbol: 'ZAR', companyId: companyId, createdBy: systemUserId, createdOn: new Date(), updatedBy: 0, updatedOn: null).save()
                new Currency(name: 'US Dollar', symbol: 'USD', companyId: companyId, createdBy: systemUserId, createdOn: new Date(), updatedBy: 0, updatedOn: null, otherCode: "840").save()
            }
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    @Override
    public void createDefaultSchema() {
        String nameIndex = "create unique index currency_name_company_id_idx on currency(lower(name), company_id);"
        executeSql(nameIndex)
        String symbolIndex = "create unique index currency_symbol_company_id_idx on currency(lower(symbol), company_id);"
        executeSql(symbolIndex)
    }

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }
}