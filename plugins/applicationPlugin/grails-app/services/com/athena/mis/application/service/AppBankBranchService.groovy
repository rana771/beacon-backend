package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppBank
import com.athena.mis.application.entity.AppBankBranch
import com.athena.mis.application.entity.District
import org.apache.log4j.Logger

class AppBankBranchService extends BaseDomainService {

    private Logger log = Logger.getLogger(getClass())

    AppBankService appBankService
    DistrictService districtService

    private static final String FIELD_BANK = "bankId"
    private static final String FIELD_DISTRICT = "districtId"

    @Override
    public void init() {
        domainClass = AppBankBranch.class
    }

    public List<AppBankBranch> findAllByCompanyId(long companyId) {
        List<AppBankBranch> branchList = AppBankBranch.findAllByCompanyId(companyId, [readOnly: true])
        return branchList
    }

    public int countByCompanyId(long companyId) {
        int count = AppBankBranch.countByCompanyId(companyId)
        return count
    }

    public AppBankBranch getGlobalBankBranch(long bankId, long companyId) {
        return AppBankBranch.findByBankIdAndCompanyIdAndIsGlobal(bankId, companyId, Boolean.TRUE, [readOnly: true])
    }

    public int countByCodeIlikeAndBankId(String code, long bankId) {
        int count = AppBankBranch.countByCodeIlikeAndBankId(code, bankId)
        return count
    }

    public int countByNameIlikeAndDistrictIdAndBankId(String name, long districtId, long bankId) {
        int count = AppBankBranch.countByNameIlikeAndDistrictIdAndBankId(name, districtId, bankId)
        return count
    }

    public int countByCodeIlikeAndDistrictIdAndBankIdAndIdNotEqual(String code, long districtId, long bankId, long id) {
        int count = AppBankBranch.countByCodeIlikeAndDistrictIdAndBankIdAndIdNotEqual(code, districtId, bankId, id)
        return count
    }

    public int countByNameIlikeAndDistrictIdAndBankIdAndIdNotEqual(String name, long districtId, long bankId, long id) {
        int count = AppBankBranch.countByNameIlikeAndDistrictIdAndBankIdAndIdNotEqual(name, districtId, bankId, id)
        return count
    }

    public int countByIdAndIsGlobal(long branchId, boolean isGlobal) {
        int count = AppBankBranch.countByIdAndIsGlobal(branchId, isGlobal)
        return count
    }

    public int countByBankIdAndCompanyId(long bankId, long companyId) {
        int count = AppBankBranch.countByBankIdAndCompanyId(bankId, companyId)
        return count
    }

    public List<AppBankBranch> findAllByBankIdAndCompanyId(long bankId, long companyId) {
        List<AppBankBranch> lstBankBranch = AppBankBranch.findAllByBankIdAndCompanyId(bankId, companyId, [sort: AppBankBranch.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true])
        return lstBankBranch
    }

    public AppBankBranch getPrincipleBranch(long bankId, long companyId) {
        AppBankBranch branch = AppBankBranch.findByBankIdAndIsPrincipleBranchAndCompanyId(bankId, Boolean.TRUE, companyId, [readOnly: true])
        return branch
    }

    public List<AppBankBranch> findAllByBankIdAndDistrictIdAndCompanyId(long bankId, long districtId, long companyId) {
        List<AppBankBranch> lstBankBranch = AppBankBranch.findAllByBankIdAndDistrictIdAndCompanyId(bankId, districtId, companyId, [sort: AppBankBranch.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true])
        return lstBankBranch
    }

    // following method returns list and count based on search query: used in BankBranch grid searching
    public Map searchByField(String fieldName, String query, List<AppBankBranch> branchList, BaseService baseService) {
        branchList = branchList.findAll {
            if (fieldName.equals(FIELD_BANK)) {
                String bankName = appBankService.read(it.bankId).name
                bankName ==~ /(?i).*${query}.*/
            } else if (fieldName.equals(FIELD_DISTRICT)) {
                String distName = districtService.read(it.districtId).name
                distName ==~ /(?i).*${query}.*/
            } else {
                it.properties.get(fieldName) ==~ /(?i).*${query}.*/
            }
        }
        int end = branchList.size() > (baseService.start + baseService.resultPerPage) ? (baseService.start + baseService.resultPerPage) : branchList.size()
        List lstResult = branchList.subList(baseService.start, end)
        return [list: lstResult, count: branchList.size()]
    }
    /**
     * Get branch list by
     * @param lstBranchIds - branchIds
     * @return lstBankBranch
     */
    public List<AppBankBranch> findAllByIdInList(List<Long> lstBranchIds) {
        List<AppBankBranch> lstBankBranch = AppBankBranch.findAllByIdInList(lstBranchIds, [sort: AppBankBranch.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true])
        return lstBankBranch
    }

    private static final String DELETE_ALL = """
        DELETE FROM app_bank_branch WHERE company_id = :companyId
    """

    /**
     * Delete all bankBranch by companyId
     * @param companyId - id of company
     */
    public void deleteAllByCompanyId(long companyId) {
        Map queryParams = [
                companyId: companyId
        ]
        executeUpdateSql(DELETE_ALL, queryParams)
    }

    public AppBankBranch findByNameIlikeAndCompanyId(String name, long companyId) {
        return AppBankBranch.findByNameIlikeAndCompanyId(name, companyId, [readOnly: true])
    }

    public boolean createDefaultData(long companyId, long systemUserId) {
        try {
            AppBank bank1 = appBankService.findByCodeAndCompanyId('SEBL', companyId)
            AppBank bank2 = appBankService.findByCodeAndCompanyId('DBBL', companyId)

            District dis1 = districtService.findByNameAndCompanyId('ANY DISTRICT', companyId)
            District dis2 = districtService.findByNameAndCompanyId('Dhaka', companyId)
            District dis3 = districtService.findByNameAndCompanyId('Chittagong', companyId)

            new AppBankBranch(version: 0, code: 'ANY', name: 'ANY BRANCH', address: 'Bangladesh', bankId: bank1.id, districtId: dis1.id, isSmeServiceCenter: false, isPrincipleBranch: false, companyId: companyId, isGlobal: true, createdBy: systemUserId, createdOn: new Date(), updatedBy: 0, updatedOn: null).save()
            new AppBankBranch(version: 0, code: 'BNI', name: 'Banani', address: 'Banani, Dhaka', bankId: bank1.id, districtId: dis2.id, isSmeServiceCenter: false, isPrincipleBranch: false, companyId: companyId, isGlobal: false, createdBy: systemUserId, createdOn: new Date(), updatedBy: 0, updatedOn: null).save()
            new AppBankBranch(version: 0, code: 'DBBL', name: 'Gulshan', address: 'Gulshan, Dhaka', bankId: bank2.id, districtId: dis2.id, isSmeServiceCenter: false, isPrincipleBranch: false, companyId: companyId, isGlobal: false, createdBy: systemUserId, createdOn: new Date(), updatedBy: 0, updatedOn: null).save()
            new AppBankBranch(version: 0, code: 'CHT', name: 'Chittagong', address: 'Chittagong', bankId: bank1.id, districtId: dis3.id, isSmeServiceCenter: true, isPrincipleBranch: false, companyId: companyId, isGlobal: false, createdBy: systemUserId, createdOn: new Date(), updatedBy: 0, updatedOn: null).save()
            new AppBankBranch(version: 0, code: 'PB', name: 'Principal Branch', address: 'Dhaka', bankId: bank1.id, districtId: dis2.id, isSmeServiceCenter: false, isPrincipleBranch: true, companyId: companyId, isGlobal: false, createdBy: systemUserId, createdOn: new Date(), updatedBy: 0, updatedOn: null).save()
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    @Override
    public void createDefaultSchema() {
        String nameIndex = "create unique index app_bank_branch_name_bank_id_district_id_idx on app_bank_branch(lower(name),bank_id, district_id);"
        executeSql(nameIndex)
        String codeIndex = "create unique index app_bank_branch_code_bank_id_district_id_idx on app_bank_branch(lower(code),bank_id, district_id);"
        executeSql(codeIndex)
    }

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }
}
