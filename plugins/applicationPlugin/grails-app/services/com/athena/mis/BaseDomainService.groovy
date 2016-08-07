package com.athena.mis

import javax.annotation.PostConstruct

public abstract class BaseDomainService extends BaseService {

    public final static String DESCENDING_SORT_ORDER = "desc";
    public final static String ASCENDING_SORT_ORDER = "asc";
    public final static String NAME = 'name';

    /**
     * abstract method for create default schema which will be implemented in Domain service
     */
    public abstract void createDefaultSchema();

    public Class domainClass

    @PostConstruct
    public abstract void init()

    /**
     * Read object by id in read-only mode
     * @param id - id of object
     * @return - object which has been read
     */
    public Object read(long id) {
        return domainClass.read(id)
    }

    /**
     * Create object in DB
     * @param -object to be saved
     * @return -saved object
     */
    public Object create(Object obj) {
        obj.save()
        return obj
    }

    /**
     * Updates object in DB
     * @param object to persist
     * @return -updated object
     */
    public Object update(Object obj) {
        obj.save()
        return obj
    }

    /**
     * Deletes object from database
     * @param object to delete
     */
    public void delete(Object obj) {
        obj.delete()
    }

    /**
     * get List of objects by domain
     * @param domainClass
     * @return - list of objects
     */
    public List<Object> list() {
        return domainClass.findAllByCompanyId(super.getCompanyId(), [readOnly: true])
    }

    /**
     * abstract method for create test which will be implemented in Domain service
     * @param companyId - Company.id
     * @param systemUserId - AppUser.id
     * @return - true
     */
    public abstract boolean createTestData(long companyId, long systemUserId)

    /**
     * delete test data by companyId
     * @param companyId - Company.id
     * @return - true
     */
    public boolean deleteTestData(long companyId) {
        if (domainClass.DELETE_TEST_DATA_QUERY) {
            Map queryParams = [
                    companyId: companyId
            ]
            executeUpdateSql(domainClass.DELETE_TEST_DATA_QUERY, queryParams)
        }
        return true
    }

    // Build common source dropDown for UI only with Name and ID
    public static final List buildSourceDropDown(List lst, String key) {
        List lstDropDown = []
        if ((lst == null) || (lst.size() <= 0))
            return lstDropDown
        for (int i = 0; i < lst.size(); i++) {
            Object obj = lst[i]
            lstDropDown << [id: obj.id, name: obj.getAt(key)]
        }
        return lstDropDown
    }

    /**
     * make content trim to given length
     * @param content - content that has to be trimmed
     * @param length - length of the trimmed string
     * @return - trimmed string
     */
    public static final String makeDetailsShort(String content, int length) {
        if (!content) return EMPTY_SPACE
        if (content.length() > length) {
            content = content.substring(0, length)
            content = content + THREE_DOTS
        }
        return content
    }

    /**
     * Following method adds the Unselected Object with proper key sets for kendo datasource
     * @param lst -the main list with objects
     * @param textMember - optional value, default is 'name'
     * @return - the main List with unselected value added in first index
     */
    public List listForKendoDropdown(List lst, String textMember, String unselectedText) {
        List lstReturn = []
        Map unseledtedVal = new LinkedHashMap()
        String txtMember = textMember ? textMember : NAME
        String unselectedTxt = unselectedText ? unselectedText : PLEASE_SELECT_LEVEL
        if (lst.size() == 0) {
            unseledtedVal.put(ID, EMPTY_SPACE)
            unseledtedVal.put(txtMember, unselectedTxt)
            lstReturn.add(0, unseledtedVal)
            return lstReturn
        }
        // List is not empty. iterate through each key (except id & textMember)
        // Put these keys (if any) inside unselectedVal (with empty string) for consistency
        Map<String, Object> firstObj
        Object tmp = lst[0]     // pick the first element, assuming all are same
        if (tmp instanceof Map) {
            firstObj = (Map<String, Object>) tmp // groovyRowResult
        } else {
            firstObj = (Map<String, Object>) tmp.properties   // Domains
        }

        for (String key : firstObj.keySet()) {
            unseledtedVal.put(key, EMPTY_SPACE)
        }
        unseledtedVal.put(ID, EMPTY_SPACE)
        unseledtedVal.put(txtMember, unselectedTxt)
        lstReturn.add(0, unseledtedVal)
        lstReturn = lstReturn.plus(1, lst)
        // append the original list (& return a new list in case cache utility object)
        return lstReturn
    }

    public String buildCommaSeparatedStringOfIds(List ids) {
        String strIds = EMPTY_SPACE
        for (int i = 0; i < ids.size(); i++) {
            strIds = strIds + ids[i]
            if ((i + 1) < ids.size()) strIds = strIds + COMA
        }
        return strIds
    }
}
