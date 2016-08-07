package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.application.entity.AppCountry
import com.athena.mis.application.entity.District
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

class DistrictService extends BaseDomainService {

    private Logger log = Logger.getLogger(getClass())

    AppCountryService appCountryService

    @Override
    public void init() {
        domainClass = District.class
    }

    @Transactional(readOnly = true)
    public List<District> list(long companyId) {
        List<District> districtList = District.findAllByCompanyId(companyId,
                [sort: District.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true])
        return districtList
    }

    public District getGlobalDistrict(long countryId, long companyId) {
        return District.findByCountryIdAndCompanyIdAndIsGlobal(countryId, companyId, Boolean.TRUE, [readOnly: true])
    }

    /**
     * Used to create District
     * @param name - District name to check duplicate
     * @return - duplicate count
     */
    public int countByNameIlikeAndCountryId(String name, long countryId) {
        int count = District.countByNameIlikeAndCountryId(name, countryId)
        return count
    }
    /**
     * Used to update District
     * @param name - District name to check duplicate
     * @return - duplicate count
     */
    public int countByNameIlikeAndCountryIdAndIdNotEqual(String name, long countryId, long id) {
        int count = District.countByNameIlikeAndCountryIdAndIdNotEqual(name, countryId, id)
        return count
    }
    /**
     * Used to delete District
     * @param districtId - districtId
     * @param isGlobal - true for "ANY DISTRICT" else false
     * @return - count
     */
    public int countByIdAndIsGlobal(long districtId, boolean isGlobal) {
        int count = District.countByIdAndIsGlobal(districtId, isGlobal)
        return count
    }
    /**
     * Count total district count by companyId
     * @return - district count
     */
    public int countByCompanyId(long companyId) {
        return District.countByCompanyId(companyId)
    }
    /**
     * Get district list by id
     * @param lstDistrictIds - districtIds
     * @return - lstDistrict
     */
    public List<District> findAllByIdInList(List<Long> lstDistrictIds) {
        List<District> lstDistrict = District.findAllByIdInList(lstDistrictIds, [sort: District.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true])
        return lstDistrict
    }

    public List<District> findAllByCountryIdAndCompanyId(long countryId, long companyId) {
        List<District> lstDistrict = District.findAllByCountryIdAndCompanyId(countryId, companyId,
                [sort: District.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true])
        return lstDistrict
    }

    /**
     * read district by isGlobal true and companyId
     * @param companyId
     * @return
     */
    public District readGlobalBranchByCompanyId(long companyId) {
        District district = District.findByIsGlobalAndCompanyId(Boolean.TRUE, companyId, [readOnly: true])
        return district
    }

    public District findByNameAndCompanyId(String name, long companyId) {
        return District.findByNameAndCompanyId(name, companyId, [readOnly: true])
    }

    private static final String DELETE_ALL = """
        DELETE FROM district WHERE company_id = :companyId
    """

    /**
     * Delete all district by companyId
     * @param companyId - id of company
     */
    public void deleteAllByCompanyId(long companyId) {
        Map queryParams = [
                companyId: companyId
        ]
        executeUpdateSql(DELETE_ALL, queryParams)
    }

    @Override
    public void createDefaultSchema() {
        String nameIndex = "create unique index district_name_country_id_idx on district(lower(name), country_id);"
        executeSql(nameIndex)
    }

    public boolean createDefaultData(long companyId, long systemUserId) {
        try {
            AppCountry country = appCountryService.findByCompanyIdAndCodeIlike(companyId, "BD")
            new District(version: 0, name: 'ANY DISTRICT', companyId: companyId, isGlobal: Boolean.TRUE, createdBy: systemUserId, createdOn: new Date(), updatedBy: 0, updatedOn: null, countryId: country.id).save()
            new District(version: 0, name: 'Dhaka', companyId: companyId, isGlobal: Boolean.FALSE, createdBy: systemUserId, createdOn: new Date(), updatedBy: 0, updatedOn: null, countryId: country.id).save()
            new District(version: 0, name: 'Chittagong', companyId: companyId, isGlobal: Boolean.FALSE, createdBy: systemUserId, createdOn: new Date(), updatedBy: 0, updatedOn: null, countryId: country.id).save()
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }
}
