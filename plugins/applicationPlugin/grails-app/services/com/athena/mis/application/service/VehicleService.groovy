package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.application.entity.Vehicle
import com.athena.mis.utility.DateUtility

/**
 * VehicleService is used to handle only CRUD related object manipulation
 * (e.g. list, read, create, delete etc.)
 */
class VehicleService extends BaseDomainService {

    TestDataModelService testDataModelService

    @Override
    public void init() {
        domainClass = Vehicle.class
    }

    /**
     * Pull vehicle object
     * @return - list of vehicle
     */
    @Override
    public List<Vehicle> list() {
        return Vehicle.findAllByCompanyId(companyId, [sort: Vehicle.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true]);
    }

    /**
     * Method to count vehicle
     * @param vehicleName - vehicle name
     * @param companyId - company id
     * @return - an integer value of vehicle count
     */
    public int countByNameIlikeAndCompanyId(String vehicleName, long companyId) {
        int count = Vehicle.countByNameIlikeAndCompanyId(vehicleName, companyId)
        return count
    }

    /**
     * Method to count vehicle
     * @param vehicleName - vehicle name
     * @param companyId - company id
     * @param vehicleId - vehicle id
     * @return - an integer value of vehicle count
     */
    public int countByNameIlikeAndCompanyIdAndIdNotEqual(String vehicleName, long companyId, long vehicleId) {
        int vehicleCount = Vehicle.countByNameIlikeAndCompanyIdAndIdNotEqual(vehicleName, companyId, vehicleId)
        return vehicleCount
    }

    @Override
    public void createDefaultSchema() {
        String nameIndex = "create unique index vehicle_name_company_id_idx on vehicle(lower(name),company_id);"
        executeSql(nameIndex)
    }

    public int countByCompanyId(long companyId) {
        return Vehicle.countByCompanyId(companyId)
    }

    /**
     * Count test data
     * @param companyId
     * @return
     */
    public int countByCompanyIdAndIdLessThan(long companyId){
        return Vehicle.countByCompanyIdAndIdLessThan(companyId, 0)
    }

    public Vehicle findByNameAndCompanyId(String name, long companyId) {
        return Vehicle.findByNameAndCompanyId(name, companyId, [readOnly: true])
    }

    private static final String QUERY_CREATE = """
            INSERT INTO vehicle(id, version, name, description, company_id, created_on, created_by, updated_by)
            VALUES (:id, :version, :name, :description, :companyId, :createdOn, :createdBy, :updatedBy);
    """

    @Override
    public boolean createTestData(long companyId , long userId){

        Vehicle vehicle1 = new Vehicle(name: 'Car', description: 'Its a car', companyId: companyId, createdBy: userId, createdOn: new Date(), updatedBy: 0)
        Vehicle vehicle2 = new Vehicle(name: 'Covered van', description: 'Its a van', companyId: companyId, createdBy: userId, createdOn: new Date(), updatedBy: 0)
        Vehicle vehicle3 = new Vehicle(name: 'General Van', description: 'Covered van', companyId: companyId, createdBy: userId, createdOn: new Date(), updatedBy: 0)
        Vehicle vehicle4 = new Vehicle(name: 'Heavy Truck', description: 'Its Heavy Duty Car', companyId: companyId, createdBy: userId, createdOn: new Date(), updatedBy: 0)
        Vehicle vehicle5 = new Vehicle(name: 'Toyota Jeep', description: 'Jeep description', companyId: companyId, createdBy: userId, createdOn: new Date(), updatedBy: 0)
        Vehicle vehicle6 = new Vehicle(name: 'Truck', description: 'Truck Description', companyId: companyId, createdBy: userId, createdOn: new Date(), updatedBy: 0)
        runSqlForCreateTestData(vehicle1)
        runSqlForCreateTestData(vehicle2)
        runSqlForCreateTestData(vehicle3)
        runSqlForCreateTestData(vehicle4)
        runSqlForCreateTestData(vehicle5)
        runSqlForCreateTestData(vehicle6)
        return true
    }

    public void runSqlForCreateTestData(Vehicle vehicle){
        Map queryParams = [
                id              : testDataModelService.getNextIdForTestData(),
                version         : 0L,
                name            : vehicle.name,
                description     : vehicle.description,
                companyId       : vehicle.companyId,
                createdBy       : vehicle.createdBy,
                createdOn       : DateUtility.getSqlDateWithSeconds(vehicle.createdOn),
                updatedBy       : vehicle.updatedBy
        ]
        executeInsertSql(QUERY_CREATE, queryParams)
    }
}
