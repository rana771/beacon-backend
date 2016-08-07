package com.athena.mis

import com.athena.mis.application.entity.Company
import com.athena.mis.application.service.CompanyService
import com.athena.mis.kendo.grid.FilterOperator
import com.athena.mis.kendo.grid.FilterOption
import org.springframework.beans.factory.annotation.Autowired

class CacheUtility {

    private static final String SORT_ORDER_DESCENDING = "desc"

    @Autowired
    CompanyService companyService

    // map of list by company id
    private Map<Long, List> lstMain = new HashMap<Long, List>()

    // list of all object
    private List lstAll

    // set list by company id
    public void setListByCompanyId(List lstObj) {
        initMain()
        for (int i = 0; i < lstObj.size(); i++) {
            Object obj = lstObj[i]
            Long objCompanyId = new Long(obj.companyId)
            List lstCompany = (List) lstMain.get(objCompanyId)
            lstCompany << obj
            lstMain.put(objCompanyId, lstCompany)
        }
    }

    private void initMain() {
        List<Company> lstCompany = (List<Company>) companyService.list()
        for (int i = 0; i < lstCompany.size(); i++) {
            lstMain.put(lstCompany[i].id, [])
        }
    }

    // set the list of all object
    public void setList(List lstObj) {
        lstAll = lstObj
    }

    // get list by company id
    public List list(long companyId) {
        Long id = new Long(companyId)
        List lstAll = lstMain.get(id)
        return lstAll
    }

    // get list of all object
    public List list() {
        return lstAll
    }

    // get count by company id
    public int count(long companyId) {
        Long id = new Long(companyId)
        List lstAll = lstMain.get(id)
        return lstAll.size()
    }

    // get all count
    public int count() {
        return lstAll.size()
    }

    // read object by id and company id
    public Object read(long id, long companyId) {
        List lstAll = lstMain.get(new Long(companyId))
        int listSize = lstAll.size()
        for (int i = 0; i < listSize; i++) {
            if (lstAll[i].id == id)
                return lstAll[i]
        }
        return null
    }

    // read object by id
    public Object read(long id) {
        int listSize = lstAll.size()
        for (int i = 0; i < listSize; i++) {
            if (lstAll[i].id == id)
                return lstAll[i]
        }
        return null
    }

    // Following method will return a object instance from app scope for any reserved id and company id
    public Object readByReservedAndCompany(long reservedId, long companyId) {
        List lstAll = lstMain.get(new Long(companyId))
        int listSize = lstAll.size()
        for (int i = 0; i < listSize; i++) {
            if (lstAll[i].reservedId == reservedId)
                return lstAll[i]
        }
        return null
    }

    // Following method will return a object instance from app scope for any reserved id
    public Object readByReserved(long reservedId) {
        int listSize = lstAll.size()
        for (int i = 0; i < listSize; i++) {
            if (lstAll[i].reservedId == reservedId)
                return lstAll[i]
        }
        return null
    }

    // Following method will return an object instance for any matching key and companyId
    public Object readByKeyAndCompanyId(String key, long companyId) {
        List lstAll = lstMain.get(new Long(companyId))
        int listSize = lstAll.size()
        for (int i = 0; i < listSize; i++) {
            if (lstAll[i].key.toString().equalsIgnoreCase(key))
                return lstAll[i]
        }
        return null
    }

    // Following method will return an object instance for any matching key
    public Object readByKey(String key) {
        int listSize = lstAll.size()
        for (int i = 0; i < listSize; i++) {
            if (lstAll[i].key.toString().equalsIgnoreCase(key))
                return lstAll[i]
        }
        return null
    }

    // get list of active objects by company id
    public List listByIsActive(long companyId) {
        List lstAll = lstMain.get(new Long(companyId))
        List lstAllActive = lstAll.findAll { it.isActive == true }
        return lstAllActive
    }

    // get list of all active objects
    public List listByIsActive() {
        List lstTemp = list()
        List lstAllActive = lstTemp.findAll { it.isActive == true }
        return lstAllActive
    }

    // get list of reserved and active objects by company id
    public List listByReservedAndIsActive(long companyId) {
        List lstAll = lstMain.get(new Long(companyId))
        int listSize = lstAll.size()
        List list = []
        for (int i = 0; i < listSize; i++) {
            if (lstAll[i].isActive == true && lstAll[i].reservedId > 0) {
                list << lstAll[i]
            }
        }
        return list
    }

    // get list of reserved and active objects
    public List listByReservedAndIsActive() {
        int listSize = lstAll.size()
        List list = []
        for (int i = 0; i < listSize; i++) {
            if (lstAll[i].isActive == true && lstAll[i].reservedId > 0) {
                list << lstAll[i]
            }
        }
        return list
    }

    // for sys configuration
    public Map listForGrid(BaseService baseService, long companyId) {
        int count = this.count(companyId)
        List lstTemp = this.list(companyId)
        List lstSorted = lstTemp.collect()
        sort(lstSorted, baseService.sortColumn, baseService.sortOrder)
        int end = lstSorted.size() > (baseService.start + baseService.resultPerPage) ? (baseService.start + baseService.resultPerPage) : lstSorted.size();
        List list = lstSorted.subList(baseService.start, end);
        return [list: list, count: count]
    }

    private void sort(List lstAll, String fieldName, String sortOrder) {
        if (sortOrder.equalsIgnoreCase(SORT_ORDER_DESCENDING)) {
            lstAll.sort { a, b -> b.properties.get(fieldName) <=> a.properties.get(fieldName) }
        } else {
            lstAll.sort { b, a -> b.properties.get(fieldName) <=> a.properties.get(fieldName) }
        }
    }

    // for sys configuration
    public Map searchForGrid(BaseService baseService, long companyId) {
        List<FilterOption> filterOptionList = baseService.filterOptions.values() as List<FilterOption>

        List lstSearchResult = this.list(companyId)
        for (FilterOption filterOption : filterOptionList) {
            lstSearchResult = lstSearchResult.findAll {
                it.properties.get(filterOption.field) ==~ /(?i).*${filterOption.value}.*/
            }
        }

        sort(lstSearchResult, baseService.sortColumn, baseService.sortOrder)
        int end = lstSearchResult.size() > (baseService.start + baseService.resultPerPage) ? (baseService.start + baseService.resultPerPage) : lstSearchResult.size()
        List lstResult = lstSearchResult.subList(baseService.start, end)
        return [list: lstResult, count: lstSearchResult.size()]
    }
}

