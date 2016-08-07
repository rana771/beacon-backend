package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.application.entity.AppDbInstance
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.entity.Vendor
import org.apache.log4j.Logger

class AppDbInstanceService extends BaseDomainService {

    private Logger log = Logger.getLogger(getClass())

    AppSystemEntityCacheService appSystemEntityCacheService
    VendorService vendorService
    SystemEntityService systemEntityService

    @Override
    public void init() {
        domainClass = AppDbInstance.class
    }

    /**
     * Get sorted list of AppDbInstance by companyId
     * @return - list of AppDbInstance
     */
    @Override
    public List list() {
        long companyId = getCompanyId()
        return AppDbInstance.findAllByCompanyId(companyId, [sort: AppDbInstance.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true])
    }

    public List<AppDbInstance> listByCompany(long companyId) {
        List<AppDbInstance> lstDbInstance = AppDbInstance.findAllByCompanyId(companyId, [readOnly: true])
        return lstDbInstance
    }

    public List<AppDbInstance> findAllByCompanyIdAndIsNative(long companyId, boolean isNative) {
        List<AppDbInstance> lstDbInstance = AppDbInstance.findAllByCompanyIdAndIsNative(companyId, isNative, [readOnly: true])
        return lstDbInstance
    }

    public List<AppDbInstance> findAllByIsTestedAndCompanyId(boolean isTested, long companyId) {
        List<AppDbInstance> lstDbInstance = AppDbInstance.findAllByIsTestedAndCompanyId(isTested, companyId, [readOnly: true])
        return lstDbInstance
    }

    public List<AppDbInstance> findAllByIsTestedAndTypeIdAndCompanyId(boolean isTested, long typeId, long companyId) {
        List<AppDbInstance> lstDbInstance = AppDbInstance.findAllByIsTestedAndTypeIdAndCompanyId(isTested, typeId, companyId, [readOnly: true])
        return lstDbInstance
    }

    public List<AppDbInstance> findAllByCompanyIdAndVendorId(long companyId, long vendorId) {
        List<AppDbInstance> lstDbInstance = AppDbInstance.findAllByCompanyIdAndVendorId(companyId, vendorId, [readOnly: true])
        return lstDbInstance
    }

    public List<AppDbInstance> findAllByCompanyIdAndReservedVendorIdAndIsTested(long companyId, long vendorId, boolean isTested) {
        List<AppDbInstance> lstDbInstance = AppDbInstance.findAllByCompanyIdAndReservedVendorIdAndIsTested(companyId, vendorId, isTested, [readOnly: true])
        return lstDbInstance
    }

    public List<AppDbInstance> findAllByCompanyIdAndReservedVendorIdInListAndIsTested(long companyId, List<Long> vendorIdList, boolean isTested) {
        List<AppDbInstance> lstDbInstance = AppDbInstance.findAllByCompanyIdAndReservedVendorIdInListAndIsTested(companyId, vendorIdList, isTested, [readOnly: true])
        return lstDbInstance
    }

    public List<AppDbInstance> findAllByCompanyIdAndReservedVendorIdNotEqual(long companyId, long vendorId) {
        List<AppDbInstance> lstDbInstance = AppDbInstance.findAllByCompanyIdAndReservedVendorIdNotEqual(companyId, vendorId, [readOnly: true])
        return lstDbInstance
    }

    public List<AppDbInstance> findAllByCompanyIdAndReservedVendorIdNotEqualAndIsTested(long companyId, long vendorId, boolean isTested) {
        List<AppDbInstance> lstDbInstance = AppDbInstance.findAllByCompanyIdAndReservedVendorIdNotEqualAndIsTested(companyId, vendorId, isTested, [readOnly: true])
        return lstDbInstance
    }

    public AppDbInstance findByVendorId(long vendorId) {
        return AppDbInstance.findByVendorId(vendorId, [readOnly: true])
    }

    public AppDbInstance findByReservedVendorId(long reservedVendorId) {
        return AppDbInstance.findByReservedVendorId(reservedVendorId, [readOnly: true])
    }

    /**
     * read dbInstance by id and is tested true
     * @param id
     * @return
     */
    public AppDbInstance findByIdAndIsTested(long id) {
        AppDbInstance dbInstance = AppDbInstance.findByIdAndIsTested(id, Boolean.TRUE, [readOnly: true])
        return dbInstance
    }
    /**
     * @return - count of AppDbInstance by company
     */
    public int count(long companyId) {
        int count = AppDbInstance.countByCompanyId(companyId)
        return count
    }

    /**
     * @return - count of AppDbInstance by company and vendorId
     */
    public int countByVendorIdAndCompanyId(long vendorId, long companyId) {
        int count = AppDbInstance.countByVendorIdAndCompanyId(vendorId, companyId)
        return count
    }

    public int countByNameIlikeAndCompanyId(String name, long companyId) {
        int count = AppDbInstance.countByNameIlikeAndCompanyId(name, companyId)
        return count
    }

    public int countByNameIlikeAndTypeIdAndCompanyId(String name, long typeId, long companyId) {
        int count = AppDbInstance.countByNameIlikeAndTypeIdAndCompanyId(name, typeId, companyId)
        return count
    }

    public int countByNameIlikeAndTypeIdAndCompanyIdAndIdNotEqual(String name, long typeId, long companyId, long docDbInstanceId) {
        int count = AppDbInstance.countByNameIlikeAndTypeIdAndCompanyIdAndIdNotEqual(name, typeId, companyId, docDbInstanceId)
        return count
    }

    public int countByNameIlikeAndCompanyIdAndIdNotEqual(String name, long companyId, long docDbInstanceId) {
        int count = AppDbInstance.countByNameIlikeAndCompanyIdAndIdNotEqual(name, companyId, docDbInstanceId)
        return count
    }

    public AppDbInstance findByIsNativeAndCompanyId(long companyId) {
        return AppDbInstance.findByIsNativeAndCompanyId(true, companyId, [readOnly: true])
    }

    public int countByUrlAndPortAndDbNameAndUserNameAndPasswordAndCompanyId(String url, String port, String dbName, String userName, String password, long companyId) {
        return AppDbInstance.countByUrlAndPortAndDbNameAndUserNameAndPasswordAndCompanyId(url, port, dbName, userName, password, companyId)
    }

    public int countByUrlAndPortAndDbNameAndUserNameAndPasswordAndCompanyIdAndIdNotEqual(String url, String port, String dbName, String userName, String password, long companyId, long id) {
        return AppDbInstance.countByUrlAndPortAndDbNameAndUserNameAndPasswordAndCompanyIdAndIdNotEqual(url, port, dbName, userName, password, companyId, id)
    }

    public AppDbInstance findByNameAndCompanyId(String name, long companyId) {
        return AppDbInstance.findByNameAndCompanyId(name, companyId, [readOnly: true])
    }

    public AppDbInstance findByReservedVendorIdAndCompanyId(long reservedVendorId, long companyId) {
        return AppDbInstance.findByReservedVendorIdAndCompanyId(reservedVendorId, companyId, [readOnly: true])
    }

    private static final String DELETE_ALL = """
        DELETE FROM app_db_instance WHERE company_id = :companyId
    """

    /**
     * Delete all dbInstance by companyId
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
    public boolean createDefaultDataForDBInstance(long companyId, long systemUserId) {
        try {
            Date currDate = new Date()
            SystemEntity systemEntityPostgres = systemEntityService.findByReservedIdAndCompanyId(appSystemEntityCacheService.SYS_ENTITY_VENDOR_POSTGRES, companyId)
            SystemEntity systemEntityMySQL = systemEntityService.findByReservedIdAndCompanyId(appSystemEntityCacheService.SYS_ENTITY_VENDOR_MYSQL, companyId)
            SystemEntity systemEntityOracle = systemEntityService.findByReservedIdAndCompanyId(appSystemEntityCacheService.SYS_ENTITY_VENDOR_ORACLE, companyId)
            SystemEntity systemEntityMSSQL = systemEntityService.findByReservedIdAndCompanyId(appSystemEntityCacheService.SYS_ENTITY_VENDOR_MSSQL_2008, companyId)
            SystemEntity systemEntityAmazonRedShift = systemEntityService.findByReservedIdAndCompanyId( appSystemEntityCacheService.SYS_ENTITY_VENDOR_AMAZON_REDSHIFT, companyId)
            SystemEntity greenPlumSysObj = systemEntityService.findByReservedIdAndCompanyId(appSystemEntityCacheService.SYS_ENTITY_VENDOR_GREEN_PLUM, companyId)

            Vendor vendorPostgres = vendorService.findByVendorIdAndCompanyId(systemEntityPostgres.id, companyId)
            Vendor vendorMySql = vendorService.findByVendorIdAndCompanyId(systemEntityMySQL.id, companyId)
            Vendor vendorOracle = vendorService.findByVendorIdAndCompanyId(systemEntityOracle.id, companyId)
            Vendor vendorMSSQL = vendorService.findByVendorIdAndCompanyId(systemEntityMSSQL.id, companyId)
            Vendor vendorRedshift = vendorService.findByVendorIdAndCompanyId(systemEntityAmazonRedShift.id, companyId)
            Vendor vendorGreenPlum = vendorService.findByVendorIdAndCompanyId(greenPlumSysObj.id, companyId)

            String strUrl = dataSource.getConnection().getMetaData().getURL()
            List<String> lstStrDbName = strUrl.split(SLASH)
            String dbName = lstStrDbName[lstStrDbName.size() - 1]
            List<String> lstStrUrl = strUrl.split(COLON)
            String url = EMPTY_SPACE
            for (int i = 0; i < lstStrUrl.size() - 1; i++) {
                if (i == (lstStrUrl.size() - 2)) {
                    url = url + lstStrUrl[i]
                } else {
                    url = url + lstStrUrl[i] + COLON
                }
            }

            AppDbInstance nativeDbInstance = new AppDbInstance(version: 0, name: 'System DB', generatedName: 'System DB (Native & Master)', vendorId: vendorPostgres.id,
                    driver: 'org.postgresql.Driver', url: url, dbName: dbName, userName: 'postgres', password: '', port: '5432',
                    connectionString: url + ':5432/' + dbName + '?user=postgres&password=', typeId: vendorPostgres.dbTypeId,
                    isNative: true, companyId: companyId, createdBy: systemUserId, createdOn: currDate, reservedVendorId: systemEntityPostgres.reservedId, isTested: true,
                    schemaName: 'public', isDeletable: true)
            nativeDbInstance.password = ''
            nativeDbInstance.save()

            AppDbInstance dbInstancePostgres = new AppDbInstance(version: 0, name: 'Postgres Server', generatedName: 'Postgres Server (Master)', vendorId: vendorPostgres.id,
                    driver: 'org.postgresql.Driver', url: 'jdbc:postgresql://192.168.1.155', dbName: 'demo_db_1', userName: 'postgres', password: '', port: '5432',
                    connectionString: 'jdbc:postgresql://192.168.1.155:5432/demo_db_1?user=postgres&password=', typeId: vendorPostgres.dbTypeId,
                    isNative: Boolean.FALSE, companyId: companyId, createdBy: systemUserId, createdOn: currDate, reservedVendorId: systemEntityPostgres.reservedId,
                    schemaName: 'public', isDeletable: true)
            dbInstancePostgres.password = ''
            dbInstancePostgres.save()

            AppDbInstance dbInstanceMysql = new AppDbInstance(version: 0, name: 'MySQL Server', generatedName: 'MySQL Server (Master)', vendorId: vendorMySql.id,
                    driver: 'com.mysql.jdbc.Driver', url: 'jdbc:mysql://192.168.1.155', dbName: 'demo_db_2', userName: 'root', password: '', port: '3306',
                    connectionString: 'jdbc:mysql://192.168.1.155:3306/demo_database?user=root&password=', typeId: vendorMySql.dbTypeId,
                    isNative: Boolean.FALSE, companyId: companyId, createdBy: systemUserId, createdOn: currDate, reservedVendorId: systemEntityMySQL.reservedId,
                    isDeletable: true)
            dbInstanceMysql.password = ''
            dbInstanceMysql.save()

            AppDbInstance dbInstanceMSSQL = new AppDbInstance(version: 0, name: 'MSSQL Server', generatedName: 'MSSQL Server (Master)', vendorId: vendorMSSQL.id,
                    driver: 'com.microsoft.sqlserver.jdbc.SQLServerDriver', url: 'jdbc:sqlserver://localhost', dbName: 'demo_db_4', userName: 'root', password: '', port: '1433',
                    connectionString: 'jdbc:sqlserver://localhost;databaseName=demo_db_4;user=root;password=', typeId: vendorMSSQL.dbTypeId,
                    isNative: Boolean.FALSE, companyId: companyId, createdBy: systemUserId, createdOn: currDate, reservedVendorId: systemEntityMSSQL.reservedId,
                    schemaName: 'dbo', isDeletable: true)
            dbInstanceMSSQL.password = ''
            dbInstanceMSSQL.save()

            AppDbInstance dbInstanceAmazonRedshift = new AppDbInstance(version: 0, name: 'Amazon RedShift', generatedName: 'Amazon RedShift (Master)', vendorId: vendorRedshift.id,
                    driver: 'com.amazon.redshift.jdbc4.Driver', url: 'jdbc:redshift://my-2nd-cluster.cukdvuoyp64h.us-west-2.redshift.amazonaws.com', dbName: 'demo_db_5', userName: 'tajul', password: 'Athena321', port: '5439',
                    connectionString: 'jdbc:redshift://my-2nd-cluster.cukdvuoyp64h.us-west-2.redshift.amazonaws.com:5439/demo_db_5?user=tajul&password=Athena321',
                    typeId: vendorRedshift.dbTypeId,
                    isNative: Boolean.FALSE, companyId: companyId, createdBy: systemUserId, createdOn: currDate, reservedVendorId: systemEntityAmazonRedShift.reservedId,
                    schemaName: 'public', isDeletable: true)
            dbInstanceAmazonRedshift.save()

            AppDbInstance greenPlum = new AppDbInstance(version: 0, name: 'Green Plum', generatedName: 'Greenplum Server (Master)', vendorId: vendorGreenPlum.id,
                    driver: 'org.postgresql.Driver', url: 'jdbc:postgresql://192.168.1.155', dbName: 'demo_db_6', userName: 'postgres', password: '', port: '5432',
                    connectionString: 'jdbc:postgresql://192.168.1.155:5432/demo_db_6?user=postgres&password=', typeId: vendorGreenPlum.dbTypeId,
                    isNative: Boolean.FALSE, companyId: companyId, createdBy: systemUserId, createdOn: currDate, reservedVendorId: greenPlumSysObj.reservedId,
                    schemaName: 'public', isDeletable: true)
            greenPlum.password = ''
            greenPlum.save()

            AppDbInstance dbInstanceOracle = new AppDbInstance(version: 0, name: 'Oracle Server', generatedName: 'Oracle Server (Master)', vendorId: vendorOracle.id,
                    driver: 'oracle.jdbc.driver.OracleDriver', url: 'jdbc:oracle:thin:@localhost', dbName: 'demo_db_3', userName: 'root', password: '', port: '1521',
                    connectionString: 'jdbc:oracle:thin:@localhost/demo_db_3?user=root&password=', typeId: vendorOracle.dbTypeId,
                    isNative: Boolean.FALSE, companyId: companyId, createdBy: systemUserId, createdOn: currDate, reservedVendorId: systemEntityOracle.reservedId,
                    isDeletable: true)
            dbInstanceOracle.password = ''
            dbInstanceOracle.save()

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    @Override
    public void createDefaultSchema() {
        String sqlOnDb = "CREATE UNIQUE INDEX app_db_instance_name_type_id_company_id_idx ON app_db_instance(lower(name),type_id,company_id);"
        executeSql(sqlOnDb)

        String forConString = "CREATE UNIQUE INDEX app_db_instance_url_port_db_name_user_name_password_company_id_idx ON app_db_instance(lower(url),port,lower(db_name),lower(user_name),lower(password),company_id);"
        executeSql(forConString)
    }

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }
}