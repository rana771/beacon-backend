package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.application.entity.AppBank
import com.athena.mis.application.entity.AppCountry
import com.athena.mis.application.entity.Company
import org.apache.log4j.Logger

/**
 * AppBankService is used to handle only CRUD related object manipulation (e.g. read, create, update, delete etc.)
 */
class AppBankService extends BaseDomainService {

    private Logger log = Logger.getLogger(getClass())

    CompanyService companyService
    AppCountryService appCountryService

    @Override
    public void init() {
        domainClass = AppBank.class
    }

    /**
     * @return - list of bank
     */
    @Override
    public List<AppBank> list() {
        List<AppBank> lstBank = AppBank.findAllByCompanyId(
                super.companyId,
                [sort    : AppBank.DEFAULT_SORT_FIELD,
                 order   : ASCENDING_SORT_ORDER,
                 readOnly: true]
        )
        return lstBank
    }

    public List<AppBank> findAllByIdIn(List<Long> lstBankIds) {
        return AppBank.findAllByIdInList(lstBankIds, [readOnly: true])
    }

    /**
     * Used to create Bank
     * @param name - bank name to check duplicate
     * @return - duplicate count
     */
    public int countByNameIlikeAndCountryIdAndCompanyId(String name, long countryId, long companyId) {
        int count = AppBank.countByNameIlikeAndCountryIdAndCompanyId(name, countryId, companyId)
        return count
    }
    /**
     * Used to update Bank
     * @param name - bank name to check duplicate
     * @return - duplicate count
     */
    public int countByNameIlikeAndCountryIdAndCompanyIdAndIdNotEqual(String name, long countryId, long companyId, long id) {
        int count = AppBank.countByNameIlikeAndCountryIdAndCompanyIdAndIdNotEqual(name, countryId, companyId, id)
        return count
    }

    /**
     * Used to create Bank
     * @param name - bank code to check duplicate
     * @return - duplicate count
     */
    public int countByCodeIlikeAndCountryIdAndCompanyId(String code, long countryId, long companyId) {
        int count = AppBank.countByCodeIlikeAndCountryIdAndCompanyId(code, countryId, companyId)
        return count
    }
    /**
     * Used to update Bank
     * @param name - bank code to check duplicate
     * @return - duplicate count
     */
    public int countByCodeIlikeAndCountryIdAndCompanyIdAndIdNotEqual(String code, long countryId, long companyId, long id) {
        int count = AppBank.countByCodeIlikeAndCountryIdAndCompanyIdAndIdNotEqual(code, countryId, companyId, id)
        return count
    }
    /**
     * Used to create Bank
     * @param isSystemBank -  only one bank can be system bank with respect to company
     * @param companyId -companyId
     * @return - count depends on systemBank
     */
    public int countByIsSystemBankAndCompanyId(boolean isSystemBank, long companyId) {
        int count = AppBank.countByIsSystemBankAndCompanyId(isSystemBank, companyId)
        return count
    }
    /**
     * Used to update Bank
     * @param isSystemBank -  only one bank can be system bank with respect to company
     * @param companyId -companyId
     * @param id - id of the object
     * @return - count depends on systemBank
     */
    public int countByIsSystemBankAndCompanyIdAndIdNotEqual(boolean isSystemBank, long companyId, long id) {
        int count = AppBank.countByIsSystemBankAndCompanyIdAndIdNotEqual(isSystemBank, companyId, id)
        return count
    }

    public AppBank getSystemBank(long companyId) {
        return AppBank.findByCompanyIdAndIsSystemBank(companyId, Boolean.TRUE, [readOnly: true])
    }

    public List<AppBank> listByCountryId(long countryId) {
        List<AppBank> lstBank = AppBank.findAllByCountryId(countryId, [sort: AppBank.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true])
        return lstBank
    }

    public List<AppBank> listNativeBank() {
        Company company = companyService.read(super.companyId)
        List<AppBank> lstBank = listByCountryId(company.countryId)
        return lstBank
    }
    /**
     * get count total Bank
     * @return count
     */
    public int countByCompanyId(long companyId) {
        return AppBank.countByCompanyId(companyId)
    }

    public AppBank findByCodeAndCompanyId(String code, long companyId) {
        return AppBank.findByCodeAndCompanyId(code, companyId, [readOnly: true])
    }

    private static final String DELETE_ALL = """
        DELETE FROM app_bank WHERE company_id = :companyId
    """

    /**
     * Delete all bank by companyId
     * @param companyId - id of company
     */
    public void deleteAllByCompanyId(long companyId) {
        Map queryParams = [
                companyId: companyId
        ]
        executeUpdateSql(DELETE_ALL, queryParams)
    }

    public boolean createDefaultData(long companyId, long systemUserId) {
        try {
            AppCountry country = appCountryService.findByCompanyIdAndCodeIlike(companyId, "BD")
            new AppBank(version: 0, name: 'Southeast Bank Ltd', code: 'SEBL', companyId: companyId, isSystemBank: true, createdBy: systemUserId, createdOn: new Date(), updatedBy: 0, updatedOn: null, countryId: country.id).save()
            new AppBank(version: 0, name: 'Dutch Bangla Bank Ltd', code: 'DBBL', companyId: companyId, isSystemBank: false, createdBy: systemUserId, createdOn: new Date(), updatedBy: 0, updatedOn: null, countryId: country.id).save()
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    @Override
    public void createDefaultSchema() {
        String nameIndex = "create unique index app_bank_name_country_id_idx on app_bank(lower(name),country_id);"
        executeSql(nameIndex)
        String codeIndex = "create unique index app_bank_code_country_id_idx on app_bank(lower(code),country_id);"
        executeSql(codeIndex)
    }

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }
}
