package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.entity.Vendor
import com.athena.mis.utility.DateUtility
import org.apache.log4j.Logger

class VendorService extends BaseDomainService {

    private Logger log = Logger.getLogger(getClass())

    AppSystemEntityCacheService appSystemEntityCacheService
    SystemEntityService systemEntityService

    @Override
    public void init() {
        domainClass = Vendor.class
    }

    private static final String INSERT_QUERY = """
        INSERT INTO vendor(
            id,
            version,
            name,
            vendor_id,
            db_type_id,
            driver,
            description,
            sequence,
            small_image,
            thumb_image,
            company_id,
            created_on,
            created_by,
            updated_by,
            updated_on
            )VALUES (
            NEXTVAL('vendor_id_seq'),
            :version,
            :name,
            :vendorId,
            :dbTypeId,
            :driver,
            :description,
            :sequence,
            :smallImage,
            :thumbImage,
            :companyId,
            :createdOn,
            :createdBy,
            :updatedBy,
            :updatedOn
            )
    """

    public Vendor create(Vendor vendor) {
        Map queryParams = [
                version    : vendor.version,
                name       : vendor.name,
                dbTypeId   : vendor.dbTypeId,
                vendorId   : vendor.vendorId,
                driver     : vendor.driver,
                description: vendor.description,
                sequence   : vendor.sequence,
                smallImage : vendor.smallImage,
                thumbImage : vendor.thumbImage,
                companyId  : vendor.companyId,
                createdOn  : DateUtility.getSqlDateWithSeconds(vendor.createdOn),
                createdBy  : vendor.createdBy,
                updatedBy  : vendor.updatedBy,
                updatedOn  : vendor.updatedOn
        ]

        List lstVendor = executeInsertSql(INSERT_QUERY, queryParams)
        if (lstVendor.size() <= 0) {
            throw new RuntimeException("Error occurred while insert vendor")
        }
        int id = (int) lstVendor[0][0]
        vendor.id = id
        return vendor
    }

    private static final String UPDATE_VENDOR_QUERY = """
        UPDATE vendor
        SET version = version+1,
            name =:name,
            db_type_id = :dbTypeId,
            vendor_id = :vendorId,
            driver =:driver,
            description = :description,
            sequence = :sequence,
            small_image = :smallImage,
            thumb_image = :thumbImage,
            updated_on =:updatedOn,
            updated_by =:updatedBy
        WHERE id =:id AND version=:oldVersion
    """

    public Vendor update(Vendor vendor) {
        Map queryParams = [
                id         : vendor.id,
                oldVersion : vendor.version,
                name       : vendor.name,
                vendorId   : vendor.vendorId,
                dbTypeId   : vendor.dbTypeId,
                driver     : vendor.driver,
                description: vendor.description,
                sequence   : vendor.sequence,
                smallImage : vendor.smallImage,
                thumbImage : vendor.thumbImage,
                updatedBy  : vendor.updatedBy,
                updatedOn  : DateUtility.getSqlDateWithSeconds(vendor.updatedOn)
        ]

        int updateCount = executeUpdateSql(UPDATE_VENDOR_QUERY, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException("Failed to update Document")
        }
        return vendor
    }

    @Override
    public void createDefaultSchema() {
        String sqlOnDb = "CREATE UNIQUE INDEX vendor_name_db_type_id_company_id_idx ON vendor(lower(name),db_type_id,company_id);"
        executeSql(sqlOnDb)
    }

    /**
     * Pull vehicle object
     * @return - list of vehicle
     */
    @Override
    public List<Vendor> list() {
        return Vendor.findAllByCompanyId(super.companyId, [sort: Vendor.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true]);
    }

    public int countByNameIlikeAndDbTypeIdAndCompanyId(String name, long typeId, long companyId) {
        int count = Vendor.countByNameIlikeAndDbTypeIdAndCompanyId(name, typeId, companyId)
        return count
    }

    public int countByNameIlikeAndDbTypeIdAndCompanyIdAndIdNotEqual(String name, long dbTypeId, long companyId, long objId) {
        int count = Vendor.countByNameIlikeAndDbTypeIdAndCompanyIdAndIdNotEqual(name, dbTypeId, companyId, objId)
        return count
    }

    public int countBySequenceAndCompanyId(int sequence, long companyId) {
        int count = Vendor.countBySequenceAndCompanyId(sequence, companyId)
        return count
    }

    public int countBySequenceAndCompanyIdAndIdNotEqual(int sequence, long companyId, long objId) {
        int count = Vendor.countBySequenceAndCompanyIdAndIdNotEqual(sequence, companyId, objId)
        return count
    }

    public Vendor findByVendorIdAndCompanyId(long vendorId, long companyId) {
        return Vendor.findByVendorIdAndCompanyId(vendorId, companyId, [readOnly: true])
    }

    private static final String DELETE_ALL = """
        DELETE FROM vendor WHERE company_id = :companyId
    """

    /**
     * Delete all vendor by companyId
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
    public boolean createDefaultDataForVendor(long companyId, long systemUserId) {
        try {
            Date currDate = new Date()

            SystemEntity systemEntityPostgres = systemEntityService.findByReservedIdAndCompanyId(appSystemEntityCacheService.SYS_ENTITY_VENDOR_POSTGRES, companyId)
            SystemEntity systemEntityMySQL = systemEntityService.findByReservedIdAndCompanyId(appSystemEntityCacheService.SYS_ENTITY_VENDOR_MYSQL, companyId)
            SystemEntity systemEntityOracle = systemEntityService.findByReservedIdAndCompanyId(appSystemEntityCacheService.SYS_ENTITY_VENDOR_ORACLE, companyId)
            SystemEntity systemEntityMSSQL = systemEntityService.findByReservedIdAndCompanyId(appSystemEntityCacheService.SYS_ENTITY_VENDOR_MSSQL_2008, companyId)
            SystemEntity systemEntityAmazonRedShift = systemEntityService.findByReservedIdAndCompanyId(appSystemEntityCacheService.SYS_ENTITY_VENDOR_AMAZON_REDSHIFT, companyId)
            SystemEntity greenPlumSysObj = systemEntityService.findByReservedIdAndCompanyId(appSystemEntityCacheService.SYS_ENTITY_VENDOR_GREEN_PLUM, companyId)

            SystemEntity sourceDb = systemEntityService.findByReservedIdAndCompanyId(appSystemEntityCacheService.SYS_ENTITY_DB_INSTANCE_SOURCE, companyId)
            SystemEntity targetDb = systemEntityService.findByReservedIdAndCompanyId(appSystemEntityCacheService.SYS_ENTITY_DB_INSTANCE_TARGET, companyId)

            Vendor vendorPostgres = new Vendor(version: 0, name: 'Postgres Server', sequence: 3,
                    vendorId: systemEntityPostgres.id, driver: systemEntityPostgres.value, smallImage: getPostgresVendorImage(), thumbImage: getPostgresVendorImage(),
                    description: "The world's most advanced open source database", dbTypeId: sourceDb.id,
                    companyId: companyId, createdBy: systemUserId, createdOn: currDate)
            vendorPostgres.save()

            Vendor vendorMySQL = new Vendor(version: 0, name: 'MySQL Server', sequence: 1, vendorId: systemEntityMySQL.id,
                    driver: systemEntityMySQL.value, description: "The world's most popular open source database", smallImage: getMySqlVendorImage(), thumbImage: getMySqlVendorImage(),
                    dbTypeId: sourceDb.id, companyId: companyId, createdBy: systemUserId, createdOn: currDate)
            vendorMySQL.save()

            Vendor vendorOracle = new Vendor(version: 0, name: 'Oracle Server', sequence: 4, vendorId: systemEntityOracle.id,
                    driver: systemEntityOracle.value, description: "Can't Break it, Can't Break in", dbTypeId: sourceDb.id, smallImage: getOracleVendorImage(), thumbImage: getOracleVendorImage(),
                    companyId: companyId, createdBy: systemUserId, createdOn: currDate)
            vendorOracle.save()

            Vendor vendorMSSQL = new Vendor(version: 0, name: 'MSSQL Server', sequence: 2, vendorId: systemEntityMSSQL.id,
                    driver: systemEntityMSSQL.value, description: "Breakthrough performance and faster insights across cloud and on-premises", smallImage: getMSSQLVendorImage(), thumbImage: getMSSQLVendorImage(),
                    dbTypeId: sourceDb.id, companyId: companyId, createdBy: systemUserId, createdOn: currDate)
            vendorMSSQL.save()

            Vendor vendorRedshift = new Vendor(version: 0, name: 'Amazon RedShift', sequence: 5,
                    vendorId: systemEntityAmazonRedShift.id, driver: systemEntityAmazonRedShift.value, description: "Cloud Data Warehouse Solutions", smallImage: getRedshiftVendorImage(), thumbImage: getRedshiftVendorImage(),
                    dbTypeId: targetDb.id, companyId: companyId, createdBy: systemUserId, createdOn: currDate)
            vendorRedshift.save()

            Vendor vendorGreenPlum = new Vendor(version: 0, name: 'Greenplum', sequence: 6, vendorId: greenPlumSysObj.id, smallImage: getGreenPlumVendorImage(), thumbImage: getGreenPlumVendorImage(),
                    driver: greenPlumSysObj.value, description: "Shared-nothing, massively parallel processing database",
                    dbTypeId: targetDb.id, companyId: companyId, createdBy: systemUserId, createdOn: currDate)
            vendorGreenPlum.save()
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    private byte[] getPostgresVendorImage() {
        String imgLoc = grailsApplication.config.theme.application + "/images/dbinstance/postgres_logo.gif"
        File imgFile = grailsApplication.parentContext.getResource(imgLoc).file
        return imgFile.bytes
    }

    private byte[] getMySqlVendorImage() {
        String imgLoc = grailsApplication.config.theme.application + "/images/dbinstance/mysql_logo.gif"
        File imgFile = grailsApplication.parentContext.getResource(imgLoc).file
        return imgFile.bytes
    }

    private byte[] getMSSQLVendorImage() {
        String imgLoc = grailsApplication.config.theme.application + "/images/dbinstance/mssql_logo.gif"
        File imgFile = grailsApplication.parentContext.getResource(imgLoc).file
        return imgFile.bytes
    }

    private byte[] getOracleVendorImage() {
        String imgLoc = grailsApplication.config.theme.application + "/images/dbinstance/oracle_logo.gif"
        File imgFile = grailsApplication.parentContext.getResource(imgLoc).file
        return imgFile.bytes
    }

    private byte[] getRedshiftVendorImage() {
        String imgLoc = grailsApplication.config.theme.application + "/images/dbinstance/redshift_logo.gif"
        File imgFile = grailsApplication.parentContext.getResource(imgLoc).file
        return imgFile.bytes
    }

    private byte[] getGreenPlumVendorImage() {
        String imgLoc = grailsApplication.config.theme.application + "/images/dbinstance/greenplum_logo.gif"
        File imgFile = grailsApplication.parentContext.getResource(imgLoc).file
        return imgFile.bytes
    }

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }
}
