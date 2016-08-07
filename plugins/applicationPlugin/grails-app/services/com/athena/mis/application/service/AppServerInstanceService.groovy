package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.application.entity.AppServerInstance
import com.athena.mis.application.entity.SystemEntity
import org.apache.log4j.Logger

class AppServerInstanceService extends BaseDomainService {

    private Logger log = Logger.getLogger(getClass())

    TestDataModelService testDataModelService
    SystemEntityService systemEntityService
    AppSystemEntityCacheService appSystemEntityCacheService

    @Override
    public void init() {
        domainClass = AppServerInstance.class
    }

    public List<AppServerInstance> listByCompany(long companyId) {
        List<AppServerInstance> lstDbInstance = AppServerInstance.findAllByCompanyId(companyId, [readOnly: true])
        return lstDbInstance
    }

    public int countByNameIlikeAndCompanyId(String name, long companyId) {
        return AppServerInstance.countByNameIlikeAndCompanyId(name, companyId)
    }

    public int countByNameIlikeAndCompanyIdAndIdNotEqual(String name, long companyId, long id) {
        return AppServerInstance.countByNameIlikeAndCompanyIdAndIdNotEqual(name, companyId, id)
    }

    public int countByCompanyId(long companyId) {
        return AppServerInstance.countByCompanyId(companyId)
    }

    public List<AppServerInstance> findAllByIsTestedAndCompanyId(boolean isTested, long companyId) {
        return AppServerInstance.findAllByIsTestedAndCompanyId(isTested, companyId, [readOnly: true])
    }

    public AppServerInstance findByNameAndCompanyId(String name, long companyId) {
        return AppServerInstance.findByNameAndCompanyId(name, companyId, [readOnly: true])
    }

    private static final String DELETE_ALL = """
        DELETE FROM app_server_instance WHERE company_id = :companyId
    """

    /**
     * Delete all AppServerInstance by companyId
     * @param companyId - id of company
     */
    public void deleteAllByCompanyId(long companyId) {
        Map queryParams = [
                companyId: companyId
        ]
        executeUpdateSql(DELETE_ALL, queryParams)
    }

    /**
     * Inert default data into database when application starts with bootstrap
     */
    public boolean createDefaultDataForServerInstance(long companyId, long systemUserId) {
        try {
            SystemEntity vendorLinux = systemEntityService.findByReservedIdAndCompanyId(appSystemEntityCacheService.SYS_ENTITY_OS_VENDOR_LINUX, companyId)

            AppServerInstance appServerInstance = new AppServerInstance(name: 'Native Server Instance', osVendorId: vendorLinux.id, sshHost: '127.0.0.1', sshPassword: '123', sshPort: 22, sshUserName: 'root', companyId: companyId, createdBy: systemUserId, createdOn: new Date(), isTested: true, isNative: true)
            appServerInstance.save()
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    @Override
    public void createDefaultSchema() {
        String sqlIndex = "CREATE UNIQUE INDEX app_server_instance_name_company_id_idx ON app_server_instance(lower(name),company_id);"
        executeSql(sqlIndex)
    }

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }

}
