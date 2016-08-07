package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Role
import com.athena.mis.utility.DateUtility
import org.apache.log4j.Logger

/**
 *  Service class for basic role CRUD (Create, Update, Delete)
 *  For details go through Use-Case doc named 'RoleService'
 */
class RoleService extends BaseDomainService {

    private Logger log = Logger.getLogger(getClass())

    TestDataModelService testDataModelService

    @Override
    public void init() {
        domainClass = Role.class
    }

    /**
     * @return -list of role object
     */
    @Override
    public List list() {
        return Role.list(sort: Role.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true)
    }

    public List<Role> findAllByIdInList(List<Long> lstRoleIds) {
        return Role.findAllByIdInList(lstRoleIds, [readOnly: true])
    }

    public Role findByIdAndCompanyId(long roleId, long companyId) {
        return Role.findByIdAndCompanyId(roleId, companyId,[readOnly: true])
    }

    public Role findByIdInListAndPluginId(List<Long> lstRoleIds, long pluginId) {
        return Role.findByIdInListAndPluginId(lstRoleIds, pluginId, [readOnly: true])
    }

    /**
     * Method to find the role object
     * @param companyId - company id
     * @param roleTypeId - role type id
     * @return - an object of role
     */
    public Role findByCompanyIdAndRoleTypeId(long companyId, long roleTypeId) {
        Role roleObject = Role.findByCompanyIdAndRoleTypeId(companyId, roleTypeId, [readOnly: true])
        return roleObject
    }

    public List<Role> findAllByPluginIdAndCompanyIdAndRoleTypeId(long pluginId, long companyId, List<Long> roleTypeIdList) {
        List<Role> roleList = Role.findAllByPluginIdAndCompanyIdAndRoleTypeIdNotInList(pluginId, companyId, roleTypeIdList, [readOnly: true])
        return roleList
    }

    /**
     * Method to count the role
     * @param roleName - role name
     * @param companyId - company id
     * @return - an integer value of role count
     */
    public int countByNameIlikeAndCompanyId(String roleName, long companyId) {
        int countDuplicate = Role.countByNameIlikeAndCompanyId(roleName, companyId)
        return countDuplicate
    }

    /**
     * @return -a map contains search result list and count
     */
    public Map search(BaseService bs) {
        List<Role> roleList = Role.withCriteria {
            ilike(bs.queryType, PERCENTAGE + bs.query + PERCENTAGE)
            eq('companyId', super.getCompanyId())
            maxResults(bs.resultPerPage)
            firstResult(bs.start)
            order(Role.DEFAULT_SORT_FIELD, ASCENDING_SORT_ORDER)
            setReadOnly(true)
        } as List

        List counts = Role.withCriteria {
            ilike(bs.queryType, PERCENTAGE + bs.query + PERCENTAGE)
            eq('companyId', super.getCompanyId())
            projections { rowCount() }
        }

        Integer total = (Integer) counts[0]
        return [roleList: roleList, count: total]
    }

    /**
     * Method to count role
     * @param name - role name
     * @param companyId - company id
     * @param id - role id
     * @return - an integer value of role count
     */
    public int countByNameIlikeAndCompanyIdAndIdNotEqual(String name, long companyId, long id) {
        int countDuplicate = Role.countByNameIlikeAndCompanyIdAndIdNotEqual(name, companyId, id)
        return countDuplicate
    }

    /**
     * Method to find the list of all role
     * @param companyId - company id
     * @param roleTypeId - role type id
     * @return - a list of role
     */
    public List<Role> findAllByCompanyIdAndRoleTypeIdNotEqual(long companyId, long roleTypeId) {
        List<Role> lstDefaultRole = Role.findAllByCompanyIdAndRoleTypeIdNotEqual(companyId, roleTypeId, [readOnly: true])
        return lstDefaultRole
    }

    /**
     * Method to find all role
     * @param companyId - company id
     * @return - a list of role
     */
    public List<Role> findAllByCompanyId(long companyId) {
        List<Role> lstRole = Role.findAllByCompanyId(companyId, [readOnly: true])
        return lstRole
    }

    public List<Role> listForDropDown() {
        long companyId = super.getCompanyId()
        List<Role> lstRole = Role.findAllByCompanyIdAndIsReseller(companyId, false, [readOnly: true, sort: Role.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER])
        return lstRole
    }

    public Role findByAuthority(String authority) {
        return Role.findByAuthority(authority, [readOnly: true])
    }

    private static final String INSERT_QUERY = """
        INSERT INTO role(id, version, authority, name, company_id, role_type_id, created_on, created_by, updated_by,
        updated_on, plugin_id, is_reseller)
        VALUES (:id, :version, :authority, :name, :companyId, :roleTypeId, :createdOn, :createdBy, :updatedBy,
        null, :pluginId, :isReseller);
    """

    /**
     * SQL to save role object in database
     * @param role -Role object
     * @return -newly created role object
     */
    public Role create(Role role) {
        Map queryParams = [
                id        : role.id,
                version   : role.version,
                authority : role.authority,
                name      : role.name,
                companyId : role.companyId,
                roleTypeId: role.roleTypeId,
                createdOn : DateUtility.getSqlDateWithSeconds(role.createdOn),
                createdBy : role.createdBy,
                updatedBy : role.updatedBy,
                pluginId  : role.pluginId,
                isReseller: role.isReseller
        ]
        List result = executeInsertSql(INSERT_QUERY, queryParams)
        if (result.size() <= 0) {
            throw new RuntimeException('Error occurred while insert role information')
        }
        return role
    }

    private static final String UPDATE_QUERY = """
                    UPDATE role SET
                          version= :newVersion,
                          name = :name,
                          updated_on=:updatedOn,
                          updated_by=:updatedBy
                    WHERE
                          id=:id AND
                          version=:version
    """

    /**
     * SQL to update role object in database
     * @param role -Role object
     * @return -int value(updateCount)
     */
    public int update(Role role) {
        Map queryParams = [
                newVersion: role.version + 1,
                name      : role.name,
                id        : role.id,
                version   : role.version,
                updatedBy : role.updatedBy,
                updatedOn : DateUtility.getSqlDateWithSeconds(role.updatedOn)
        ]

        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while update role')
        }
        return updateCount
    }

    private static final String DELETE_ALL = """
        DELETE FROM role WHERE company_id = :companyId
    """

    /**
     * Delete all role by companyId
     * @param companyId - id of company
     */
    public void deleteAllByCompanyId(long companyId) {
        Map queryParams = [
                companyId: companyId
        ]
        executeUpdateSql(DELETE_ALL, queryParams)
    }

    /**
     * insert application module default role into database when application starts with bootstrap
     */
    public boolean createDefaultDataForApplication(long companyId, long systemUserId) {
        try {
            executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id,created_on,created_by,updated_by,plugin_id,is_reseller) VALUES(NEXTVAL('role_id_seq'),0,-3,'ROLE_-3_${
                companyId
            }','Application Admin', ${companyId},'${DateUtility.getSqlDateWithSeconds(new Date())}',${
                systemUserId
            },0,1,false)""")
            executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id,created_on,created_by,updated_by,plugin_id,is_reseller) VALUES(NEXTVAL('role_id_seq'),0,-12,'ROLE_-12_${
                companyId
            }','Application Development Admin', ${companyId},'${DateUtility.getSqlDateWithSeconds(new Date())}',${
                systemUserId
            },0,1,false)""")
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * insert reseller role into database when application starts with bootstrap
     */
    public boolean createRoleForReseller() {
        try {
            executeInsertSql("""INSERT INTO role (id, version, role_type_id, authority, name, company_id, created_on, created_by, updated_by, plugin_id, is_reseller)
                VALUES(NEXTVAL('role_id_seq'), 0, -33, 'ROLE_RESELLER', 'Reseller', 1,
                '${DateUtility.getSqlDateWithSeconds(new Date())}', 1, 0, 1, true)
            """)
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * insert Budget module default role into database when application starts with bootstrap
     */
    public boolean createDefaultDataForBudget(long companyId, long systemUserId) {
        try {
            executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id,created_on,created_by,updated_by,plugin_id,is_reseller) VALUES(NEXTVAL('role_id_seq'),0,-19,'ROLE_-19_${
                companyId
            }','Budget Admin',${companyId},'${DateUtility.getSqlDateWithSeconds(new Date())}',${
                systemUserId
            },0,3,false)""")
            executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id,created_on,created_by,updated_by,plugin_id,is_reseller) VALUES(NEXTVAL('role_id_seq'),0,-20,'ROLE_-20_${
                companyId
            }','Budget Development Admin',${companyId},'${DateUtility.getSqlDateWithSeconds(new Date())}',${
                systemUserId
            },0,3,false)""")
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * insert Procurement module default role into database when application starts with bootstrap
     */
    public boolean createDefaultDataForProcurement(long companyId, long systemUserId) {
        try {
            executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id,created_on,created_by,updated_by,plugin_id,is_reseller) VALUES(NEXTVAL('role_id_seq'),0,-21,'ROLE_-21_${
                companyId
            }','Procurement Admin',${companyId},'${DateUtility.getSqlDateWithSeconds(new Date())}',${
                systemUserId
            },0,5,false)""")
            executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id,created_on,created_by,updated_by,plugin_id,is_reseller) VALUES(NEXTVAL('role_id_seq'),0,-22,'ROLE_-22_${
                companyId
            }','Procurement Development Admin',${companyId},'${DateUtility.getSqlDateWithSeconds(new Date())}',${
                systemUserId
            },0,5,false)""")
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return false
        }
    }

    /**
     * insert inventory module default role into database when application starts with bootstrap
     */
    public boolean createDefaultDataForInventory(long companyId, long systemUserId) {
        try {
            executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id,created_on,created_by,updated_by,plugin_id,is_reseller) VALUES(NEXTVAL('role_id_seq'),0,-23,'ROLE_-23_${
                companyId
            }','Inventory Admin',${companyId},'${DateUtility.getSqlDateWithSeconds(new Date())}',${
                systemUserId
            },0,4,false)""")
            executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id,created_on,created_by,updated_by,plugin_id,is_reseller) VALUES(NEXTVAL('role_id_seq'),0,-24,'ROLE_-24_${
                companyId
            }','Inventory Development Admin',${companyId},'${DateUtility.getSqlDateWithSeconds(new Date())}',${
                systemUserId
            },0,4,false)""")
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * insert accounting module default role into database when application starts with bootstrap
     */
    public boolean createDefaultDataForAccounting(long companyId, long systemUserId) {
        try {
            executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id,created_on,created_by,updated_by,plugin_id,is_reseller) VALUES(NEXTVAL('role_id_seq'),0,-25,'ROLE_-25_${
                companyId
            }','Accountant Admin',${companyId},'${DateUtility.getSqlDateWithSeconds(new Date())}',${
                systemUserId
            },0,2,false)""")
            executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id,created_on,created_by,updated_by,plugin_id,is_reseller) VALUES(NEXTVAL('role_id_seq'),0,-26,'ROLE_-26_${
                companyId
            }','Accountant Development Admin',${companyId},'${DateUtility.getSqlDateWithSeconds(new Date())}',${
                systemUserId
            },0,2,false)""")
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * insert Qs module default role into database when application starts with bootstrap
     */
    public boolean createDefaultDataForQs(long companyId, long systemUserId) {
        try {
            executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id,created_on,created_by,updated_by,plugin_id,is_reseller) VALUES(NEXTVAL('role_id_seq'),0,-29,'ROLE_-29_${
                companyId
            }','Qs Admin',${companyId},'${DateUtility.getSqlDateWithSeconds(new Date())}',${systemUserId},0,6,false)""")
            executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id,created_on,created_by,updated_by,plugin_id,is_reseller) VALUES(NEXTVAL('role_id_seq'),0,-30,'ROLE_-30_${
                companyId
            }','Qs Development Admin',${companyId},'${DateUtility.getSqlDateWithSeconds(new Date())}',${
                systemUserId
            },0,6,false)""")
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return false
        }
    }

    /**
     * insert FixedAsset module default role into database when application starts with bootstrap
     */
    public boolean createDefaultDataForFixedAsset(long companyId, long systemUserId) {
        try {
            executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id,created_on,created_by,updated_by,plugin_id,is_reseller) VALUES(NEXTVAL('role_id_seq'),0,-27,'ROLE_-27_${
                companyId
            }','FixedAsset Admin',${companyId},'${DateUtility.getSqlDateWithSeconds(new Date())}',${
                systemUserId
            },0,7,false)""")
            executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id,created_on,created_by,updated_by,plugin_id,is_reseller) VALUES(NEXTVAL('role_id_seq'),0,-28,'ROLE_-28_${
                companyId
            }','FixedAsset Development Admin',${companyId},'${DateUtility.getSqlDateWithSeconds(new Date())}',${
                systemUserId
            },0,7,false)""")
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * insert exchangeHouse module default role into database when application starts with bootstrap
     */
    public boolean createDefaultDataForExh(long companyId, long systemUserId) {
        try {
            executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id,created_on,created_by,updated_by,plugin_id,is_reseller)
        VALUES(-201,0,-201,'ROLE_-201_${companyId}','Cashier',${companyId},'${
                DateUtility.getSqlDateWithSeconds(new Date())
            }',${systemUserId},0,9,false)""")

            executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id,created_on,created_by,updated_by,plugin_id,is_reseller)
        VALUES(-202,0,-202,'ROLE_-202_${companyId}','Customer',${companyId},'${
                DateUtility.getSqlDateWithSeconds(new Date())
            }',${systemUserId},0,9,false)""")

            executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id,created_on,created_by,updated_by,plugin_id,is_reseller)
        VALUES(-203,0,-203,'ROLE_-203_${companyId}','Other Bank',${companyId},'${
                DateUtility.getSqlDateWithSeconds(new Date())
            }',${systemUserId},0,9,false)""")

            executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id,created_on,created_by,updated_by,plugin_id,is_reseller)
        VALUES(-204,0,-204,'ROLE_-204_${companyId}','Agent',${companyId},'${
                DateUtility.getSqlDateWithSeconds(new Date())
            }',${systemUserId},0,9,false)""")

            executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id,created_on,created_by,updated_by,plugin_id,is_reseller)
        VALUES(-205,0,-205,'ROLE_-205_${companyId}','Exh Administrator',${companyId},'${
                DateUtility.getSqlDateWithSeconds(new Date())
            }',${systemUserId},0,9,false)""")

            executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id,created_on,created_by,updated_by,plugin_id,is_reseller)
        VALUES(-206,0,-206,'ROLE_-206_${companyId}','Exh Development',${companyId},'${
                DateUtility.getSqlDateWithSeconds(new Date())
            }',${systemUserId},0,9,false)""")

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createDefaultDataForSarb(long companyId, long systemUserId) {
        try {
            executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id,created_on,created_by,updated_by,plugin_id,is_reseller)
        VALUES(-301,0,-301,'ROLE_-301_${companyId}','SARB Administrator',${companyId},'${
                DateUtility.getSqlDateWithSeconds(new Date())
            }',${systemUserId},0,12,false)""")

            executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id,created_on,created_by,updated_by,plugin_id,is_reseller)
        VALUES(-302,0,-302,'ROLE_-302_${companyId}','SARB Development',${companyId},'${
                DateUtility.getSqlDateWithSeconds(new Date())
            }',${systemUserId},0,12,false)""")

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * insert project track module default role into database when application starts with bootstrap
     */
    public void createDefaultDataForPT(long companyId, long systemUserId) {
        try {
            executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id,created_on,created_by,updated_by,plugin_id,is_reseller) VALUES(NEXTVAL('role_id_seq'),0,-31,'ROLE_-31_${
                companyId
            }','ProjectTrack Admin',${companyId},'${DateUtility.getSqlDateWithSeconds(new Date())}',${
                systemUserId
            },0,10,false)""")
            executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id,created_on,created_by,updated_by,plugin_id,is_reseller) VALUES(NEXTVAL('role_id_seq'),0,-32,'ROLE_-32_${
                companyId
            }','ProjectTrack Development Admin',${companyId},'${DateUtility.getSqlDateWithSeconds(new Date())}',${
                systemUserId
            },0,10,false)""")

        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * create default role for ARMS
     */
    public boolean createDefaultDataForArms(long companyId) {
        try {
            executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id,created_on,created_by,updated_by,plugin_id,is_reseller)
        VALUES(-401,0,-401,'ROLE_-401_${companyId}','ARMS Remittance User',${companyId},'${
                DateUtility.getSqlDateWithSeconds(new Date())
            }',1,0,11,false)""")

            executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id,created_on,created_by,updated_by,plugin_id,is_reseller)
        VALUES(-402,0,-402,'ROLE_-402_${companyId}','ARMS Branch User',${companyId},'${
                DateUtility.getSqlDateWithSeconds(new Date())
            }',1,0,11,false)""")

            executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id,created_on,created_by,updated_by,plugin_id,is_reseller)
        VALUES(-403,0,-403,'ROLE_-403_${companyId}','ARMS ExchangeHouse User',${companyId},'${
                DateUtility.getSqlDateWithSeconds(new Date())
            }',1,0,11,false)""")

            executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id,created_on,created_by,updated_by,plugin_id,is_reseller)
        VALUES(-405,0,-405,'ROLE_-405_${companyId}','ARMS Administrator',${companyId},'${
                DateUtility.getSqlDateWithSeconds(new Date())
            }',1,0,11,false)""")

            executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id,created_on,created_by,updated_by,plugin_id,is_reseller)
        VALUES(-406,0,-406,'ROLE_-406_${companyId}','ARMS Development',${companyId},'${
                DateUtility.getSqlDateWithSeconds(new Date())
            }',1,0,11,false)""")

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * create default role for Document
     */
    public void createDefaultDataForDocument(long companyId) {
        try {
            executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id,created_on,created_by,updated_by,plugin_id,is_reseller)
            VALUES(NEXTVAL('role_id_seq'),0,-501,'ROLE_-501_${companyId}','DOC Administrator',${companyId},'${
                DateUtility.getSqlDateWithSeconds(new Date())
            }',1,0,13,false)""")
            executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id,created_on,created_by,updated_by,plugin_id,is_reseller)
            VALUES(NEXTVAL('role_id_seq'),0,-502,'ROLE_-502_${companyId}','DOC Development',${companyId},'${
                DateUtility.getSqlDateWithSeconds(new Date())
            }',1,0,13,false)""")
            executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id,created_on,created_by,updated_by,plugin_id,is_reseller)
            VALUES(NEXTVAL('role_id_seq'),0,-503,'ROLE_-503_${companyId}','DOC Member',${companyId},'${
                DateUtility.getSqlDateWithSeconds(new Date())
            }',1,0,13,false)""")

        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * create default role for E-Learning
     */
    public void createDefaultDataForElearning(long companyId) {
        try {
            //EL Administrator
            executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id,created_on,created_by,updated_by,plugin_id,is_reseller)
            VALUES(NEXTVAL('role_id_seq'),0,-801,'ROLE_-801_${companyId}','EL Administrator',${companyId},'${DateUtility.getSqlDateWithSeconds(new Date())}',1,0,15,false)""")

            //El Administrator
            executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id,created_on,created_by,updated_by,plugin_id,is_reseller)
            VALUES(NEXTVAL('role_id_seq'),0,-802,'ROLE_-802_${companyId}','EL Development',${companyId},'${DateUtility.getSqlDateWithSeconds(new Date())}',1,0,15,false)""")

            //
            executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id,created_on,created_by,updated_by,plugin_id,is_reseller)
            VALUES(NEXTVAL('role_id_seq'),0,-803,'ROLE_-803_${companyId}','Teacher',${companyId},'${
                DateUtility.getSqlDateWithSeconds(new Date())
            }',1,0,15,false)""")

            executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id,created_on,created_by,updated_by,plugin_id,is_reseller)
            VALUES(NEXTVAL('role_id_seq'),0,-804,'ROLE_-804_${companyId}','Coordinator',${companyId},'${
                DateUtility.getSqlDateWithSeconds(new Date())
            }',1,0,15,false)""")

            executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id,created_on,created_by,updated_by,plugin_id,is_reseller)
            VALUES(NEXTVAL('role_id_seq'),0,-805,'ROLE_-805_${companyId}','Student',${companyId},'${
                DateUtility.getSqlDateWithSeconds(new Date())
            }',1,0,15,false)""")
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * create default role for Document
     */
    public void createDefaultDataForDataPipeLine(long companyId) {
        try {
            executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id,created_on,created_by,updated_by,plugin_id,is_reseller)
            VALUES(NEXTVAL('role_id_seq'),0,-601,'ROLE_-601_1','DPL Administrator',${companyId},'${
                DateUtility.getSqlDateWithSeconds(new Date())
            }',1,0,14,false)""")
            executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id,created_on,created_by,updated_by,plugin_id,is_reseller)
            VALUES(NEXTVAL('role_id_seq'),0,-602,'ROLE_-602_1','DPL Development',${companyId},'${
                DateUtility.getSqlDateWithSeconds(new Date())
            }',1,0,14,false)""")

        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    @Override
    public void createDefaultSchema() {
        String authIndex = "create unique index role_authority_idx on role(lower(authority));"
        executeSql(authIndex)
        String nameIndex = "create unique index role_name_company_id_idx on role(lower(name), company_id);"
        executeSql(nameIndex)
    }

    public Role findByNameAndCompanyId(String name, long companyId) {
        return Role.findByNameAndCompanyId(name, companyId, [readOnly: true])
    }

    /**
     * create test data for budget plugin
     * @param companyId - id of company
     * @param userId - id of user
     */
    public void createTestDataForBudget(long companyId, long userId) {
        long directorId = testDataModelService.getNextIdForTestData()
        String authorityDir = 'ROLE_' + directorId.toString() + UNDERSCORE + companyId.toString()
        Role director = new Role(authority: authorityDir, name: 'Director')
        director.id = directorId
        long pdId = testDataModelService.getNextIdForTestData()
        String authorityPd = 'ROLE_' + pdId.toString() + UNDERSCORE + companyId.toString()
        Role projectDirector = new Role(authority: authorityPd, name: 'Project Director')
        projectDirector.id = pdId
        insertTestData(director, companyId, userId)
        insertTestData(projectDirector, companyId, userId)
    }
    /**
     * create test data for accounting plugin
     * @param companyId - id of company
     * @param userId - id of user
     */
    public void createTestDataForAccounting(long companyId, long userId) {
        long accountantId = testDataModelService.getNextIdForTestData()
        String authorityAccountant = 'ROLE_' + accountantId.toString() + UNDERSCORE + companyId.toString()
        Role accountant = new Role(authority: authorityAccountant, name: 'Accountant')
        accountant.id = accountantId
        long cfoId = testDataModelService.getNextIdForTestData()
        String authorityCfo = 'ROLE_' + cfoId.toString() + UNDERSCORE + companyId.toString()
        Role cfo = new Role(authority: authorityCfo, name: 'CFO')
        cfo.id = cfoId
        insertTestData(accountant, companyId, userId)
        insertTestData(cfo, companyId, userId)
    }
    /**
     * create test data for project track plugin
     * @param companyId - id of company
     * @param userId - id of user
     */
    public void createTestDataForPt(long companyId, long userId) {
        long engineerId = testDataModelService.getNextIdForTestData()
        String authEngineer = 'ROLE_' + engineerId.toString() + UNDERSCORE + companyId.toString()
        Role engineer = new Role(authority: authEngineer, name: 'Software Engineer')
        engineer.id = engineerId
        long qsId = testDataModelService.getNextIdForTestData()
        String authQS = 'ROLE_' + qsId.toString() + UNDERSCORE + companyId.toString()
        Role qs = new Role(authority: authQS, name: 'SQA')
        qs.id = qsId

        insertTestData(engineer, companyId, userId)
        insertTestData(qs, companyId, userId)
    }

    /**
     * create test data for inventory plugin
     * @param companyId - id of company
     * @param userId - id of user
     */
    public void createTestDataForInventory(long companyId, long userId) {
        long auditorId = testDataModelService.getNextIdForTestData()
        String authorityAuditor = 'ROLE_' + auditorId.toString() + UNDERSCORE + companyId.toString()
        Role auditor = new Role(authority: authorityAuditor, name: 'Inventory Auditor')
        auditor.id = auditorId
        long pmId = testDataModelService.getNextIdForTestData()
        String authorityPm = 'ROLE_' + pmId.toString() + UNDERSCORE + companyId.toString()
        Role projectManager = new Role(authority: authorityPm, name: 'Project Manager')
        projectManager.id = pmId
        insertTestData(auditor, companyId, userId)
        insertTestData(projectManager, companyId, userId)
    }

    /**
     * create test data for ELearning
     * @param companyId
     * @param userId
     */
    public void createTestDataForELearning(long companyId, long userId) {
    }

    /**
     * insert test data
     * @param role - object of Role
     * @param companyId - id of company
     * @param userId - id of user
     */
    private void insertTestData(Role role, long companyId, long userId) {
        Map queryParams = [
                id        : role.id,
                version   : 0,
                authority : role.authority,
                name      : role.name,
                companyId : companyId,
                roleTypeId: 0,
                createdOn : DateUtility.getSqlDateWithSeconds(new Date()),
                createdBy : userId,
                updatedBy : 0,
                pluginId  : 0,
                isReseller: false
        ]
        executeInsertSql(INSERT_QUERY, queryParams)
    }

    /**
     * SQL to delete test data from database
     * @param roleIds - list of roleIds
     */
    public void deleteTestData(List<Long> roleIds) {
        if (roleIds.size() > 0) {
            String lstRoleIds = super.buildCommaSeparatedStringOfIds(roleIds)
            String deleteSql = """ DELETE FROM role WHERE id IN (${lstRoleIds}) """
            executeUpdateSql(deleteSql)
        }
    }

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }
}
