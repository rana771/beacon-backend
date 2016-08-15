package com.mis.beacon.service

import com.athena.mis.BaseDomainService
import com.athena.mis.application.service.TestDataModelService
import com.mis.beacon.Place

class PlaceService extends BaseDomainService {

    TestDataModelService testDataModelService

    @Override
    public Place read(long id){
     return Place.read(id)

    }

    @Override
    public void init() {
        domainClass = Place.class
    }

    /**
     * Pull place object
     * @return - list of place
     */
    @Override
    public List<Place> list() {
    }

    /**
     * Get list of Place by list of ids
     * @param lstPlaceIds - list of AppGroup.id
     * @return - list of Place by ids
     */
    public List<Place> findAllByIdInList(List<Long> lstPlaceIds) {
        return null
    }


    @Override
    public void createDefaultSchema() {
        String nameIndex = "create unique index place_name_company_id_idx on place(lower(name),company_id);"
        executeSql(nameIndex)
        String codeIndex = "create unique index place_code_company_id_idx on place(lower(code),company_id);"
        executeSql(codeIndex)
    }

    public int countByCompanyId(long companyId) {
        return 0;
    }


    @Override
    public boolean createTestData(long companyId, long userId) {
        //Write your default data insert statement here
        return true
    }


}
