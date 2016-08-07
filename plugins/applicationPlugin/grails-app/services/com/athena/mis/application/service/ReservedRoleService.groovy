package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.ReservedRole
import com.athena.mis.integration.accounting.AccPluginConnector
import com.athena.mis.integration.arms.ArmsPluginConnector
import com.athena.mis.integration.budget.BudgPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.integration.fixedasset.FxdPluginConnector
import com.athena.mis.integration.inventory.InvPluginConnector
import com.athena.mis.integration.procurement.ProcPluginConnector
import com.athena.mis.integration.qsmeasurement.QsPluginConnector
import com.athena.mis.integration.sarb.SarbPluginConnector
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

class ReservedRoleService extends BaseDomainService {

    private Logger log = Logger.getLogger(getClass())

    // requestMap id for root page which will be mapped with role on role-create
    public static final int REQUEST_MAP_ROOT = -2
    public static final int REQUEST_MAP_LOGOUT = -3
    public static final int MANAGE_PASSWORD = -13
    public static final int CHECK_PASSWORD = -14
    public static final int CHANGE_PASSWORD = -15

    // Role Type for App (range -1 to -200)
    public static final long ROLE_TYPE_APP_ADMIN = -3
    public static final long ROLE_TYPE_APP_DEVELOPMENT = -12


    // For Budget
    public static final long ROLE_TYPE_BUDG_ADMIN = -19
    public static final long ROLE_TYPE_BUDG_DEVELOPMENT = -20
    // For Procurement
    public static final long ROLE_TYPE_PROC_ADMIN = -21
    public static final long ROLE_TYPE_PROC_DEVELOPMENT = -22
    // For Inventory
    public static final long ROLE_TYPE_INV_ADMIN = -23
    public static final long ROLE_TYPE_INV_DEVELOPMENT = -24
    // For Accounting
    public static final long ROLE_TYPE_ACC_ADMIN = -25
    public static final long ROLE_TYPE_ACC_DEVELOPMENT = -26
    // For Fixed Asset
    public static final long ROLE_TYPE_FXD_ADMIN = -27
    public static final long ROLE_TYPE_FXD_DEVELOPMENT = -28
    // For QS
    public static final long ROLE_TYPE_QS_ADMIN = -29
    public static final long ROLE_TYPE_QS_DEVELOPMENT = -30
    // For Project Track
    public static final long ROLE_TYPE_PT_ADMIN = -31
    public static final long ROLE_TYPE_PT_DEVELOPMENT = -32

    // Reseller
    public static final long ROLE_TYPE_APP_RESELLER = -33
    // End of Role Type for MIS

    // Role Type for ExchangeHouse (range -201 to -300)
    public static final long ROLE_TYPE_EXH_CASHIER = -201
    public static final long ROLE_TYPE_EXH_CUSTOMER = -202
    public static final long ROLE_TYPE_EXH_OTHER_BANK = -203
    public static final long ROLE_TYPE_EXH_AGENT = -204
    public static final long ROLE_TYPE_EXH_ADMIN = -205
    public static final long ROLE_TYPE_EXH_DEVELOPMENT = -206

    //Role Type for SARB (range -301 to -400)
    public static final long ROLE_TYPE_SARB_ADMIN = -301
    public static final long ROLE_TYPE_SARB_DEVELOPMENT = -302

    //Role Type for ARMS (range -401 to -500)
    public static final long ROLE_ARMS_REMITTANCE_USER = -401
    public static final long ROLE_ARMS_BRANCH_USER = -402
    public static final long ROLE_ARMS_EXCHANGE_HOUSE_USER = -403
    public static final long ROLE_TYPE_ARMS_ADMIN = -405
    public static final long ROLE_TYPE_ARMS_DEVELOPMENT = -406

    // Role Type for Document (range -501 to -600)
    public static final long ROLE_TYPE_DOCUMENT_ADMINISTRATOR = -501
    public static final long ROLE_TYPE_DOCUMENT_DEVELOPMENT = -502
    public static final long ROLE_TYPE_DOCUMENT_MEMBER = -503

    // Role Type for Data Pipe Line (range -601 to -700)
    public static final long ROLE_TYPE_DATA_PIPE_LINE_ADMINISTRATOR = -601
    public static final long ROLE_TYPE_DATA_PIPE_LINE_DEVELOPMENT = -602

    // Role Type for Data Pipe Line (range -801 to -900)
    public static final long ROLE_TYPE_E_LEARNING_ADMINISTRATOR = -801
    public static final long ROLE_TYPE_E_LEARNING_DEVELOPMENT = -802
    public static final long ROLE_TYPE_E_LEARNING_TEACHER = -803
    public static final long ROLE_TYPE_E_LEARNING_COORDINATOR = -804
    public static final long ROLE_TYPE_E_LEARNING_STUDENT = -805

    AppVersionService appVersionService

    @Override
    public void init() {
        domainClass = ReservedRole.class
    }

    @Override
    public void createDefaultSchema() {
        String sqlIndex = "CREATE UNIQUE INDEX reserved_role_authority_idx ON reserved_role(lower(authority));"
        executeSql(sqlIndex)
    }

    /**
     * Method to get role type list
     * @return - list of role type
     */
    @Override
    public List list() {
        return ReservedRole.list(sort: ReservedRole.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true)
    }

    /**
     * insert application module default reserved_role into database when application starts with bootstrap
     */
    public boolean createDefaultDataForApplication() {
        try {
            int count = appVersionService.countByPluginId(PluginConnector.PLUGIN_ID)
            if (count > 0) {
                return true
            }
            executeInsertSql("""INSERT INTO reserved_role(id, name, authority, plugin_id) VALUES (-3, 'Application Admin', 'ROLE_-3', 1)""")
            executeInsertSql("""INSERT INTO reserved_role(id, name, authority, plugin_id) VALUES (-12, 'Application Development Admin', 'ROLE_-12', 1)""")
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * insert Budget module default reserved_role into database when application starts with bootstrap
     */
    public boolean createDefaultDataForBudget() {
        try {
            int count = appVersionService.countByPluginId(BudgPluginConnector.PLUGIN_ID)
            if (count > 0) {
                return true
            }
            executeInsertSql("""INSERT INTO reserved_role(id, name, authority, plugin_id) VALUES (-19, 'Budget Admin', 'ROLE_-19', 3)""")
            executeInsertSql("""INSERT INTO reserved_role(id, name, authority, plugin_id) VALUES (-20, 'Budget Development Admin', 'ROLE_-20', 3)""")
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * insert Procurement module default reserved_role into database when application starts with bootstrap
     */
    public boolean createDefaultDataForProcurement() {
        try {
            int count = appVersionService.countByPluginId(ProcPluginConnector.PLUGIN_ID)
            if (count > 0) {
                return true
            }
            executeInsertSql("""INSERT INTO reserved_role(id, name, authority, plugin_id) VALUES (-21, 'Procurement Admin', 'ROLE_-21', 5)""")
            executeInsertSql("""INSERT INTO reserved_role(id, name, authority, plugin_id) VALUES (-22, 'Procurement Development Admin', 'ROLE_-22', 5)""")
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * insert inventory module default reserved_role into database when application starts with bootstrap
     */
    public boolean createDefaultDataForInventory() {
        try {
            int count = appVersionService.countByPluginId(InvPluginConnector.PLUGIN_ID)
            if (count > 0) {
                return true
            }
            executeInsertSql("""INSERT INTO reserved_role(id, name, authority, plugin_id) VALUES (-23, 'Inventory Admin', 'ROLE_-23', 4)""")
            executeInsertSql("""INSERT INTO reserved_role(id, name, authority, plugin_id) VALUES (-24, 'Inventory Development Admin', 'ROLE_-24', 4)""")
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * insert Qs module default reserved_role into database when application starts with bootstrap
     */
    public boolean createDefaultDataForQs() {
        try {
            int count = appVersionService.countByPluginId(QsPluginConnector.PLUGIN_ID)
            if (count > 0) {
                return true
            }
            executeInsertSql("""INSERT INTO reserved_role(id, name, authority, plugin_id) VALUES (-29, 'Qs Admin', 'ROLE_-29', 6)""")
            executeInsertSql("""INSERT INTO reserved_role(id, name, authority, plugin_id) VALUES (-30, 'Qs Development Admin', 'ROLE_-30', 6)""")
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * insert FixedAsset module default reserved_role into database when application starts with bootstrap
     */
    public boolean createDefaultDataForFixedAsset() {
        try {
            int count = appVersionService.countByPluginId(FxdPluginConnector.PLUGIN_ID)
            if (count > 0) {
                return true
            }
            executeInsertSql("""INSERT INTO reserved_role(id, name, authority, plugin_id) VALUES (-27, 'FixedAsset Admin', 'ROLE_-27', 7)""")
            executeInsertSql("""INSERT INTO reserved_role(id, name, authority, plugin_id) VALUES (-28, 'FixedAsset Development Admin', 'ROLE_-28', 7)""")
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * insert accounting module default reserved_role into database when application starts with bootstrap
     */
    public boolean createDefaultDataForAccounting() {
        try {
            int count = appVersionService.countByPluginId(AccPluginConnector.PLUGIN_ID)
            if (count > 0) {
                return true
            }
            executeInsertSql("""INSERT INTO reserved_role(id, name, authority, plugin_id) VALUES (-25, 'Accounting Admin', 'ROLE_-25', 2)""")
            executeInsertSql("""INSERT INTO reserved_role(id, name, authority, plugin_id) VALUES (-26, 'Accounting Development Admin', 'ROLE_-26', 2)""")
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * insert exchangeHouse module default reserved_role into database when application starts with bootstrap
     */
    @Transactional
    public boolean createDefaultDataForExh() {
        try {
            int count = appVersionService.countByPluginId(ExchangeHousePluginConnector.PLUGIN_ID)
            if (count > 0) return true

            executeInsertSql("""INSERT INTO reserved_role(id, name, authority, plugin_id) VALUES (-201,'Cashier','ROLE_-201',9)""")
            executeInsertSql("""INSERT INTO reserved_role(id, name, authority, plugin_id) VALUES (-202,'Customer','ROLE_-202',9)""")
            executeInsertSql("""INSERT INTO reserved_role(id, name, authority, plugin_id) VALUES (-203,'Other Bank','ROLE_-203',9)""")
            executeInsertSql("""INSERT INTO reserved_role(id, name, authority, plugin_id) VALUES (-204,'Agent','ROLE_-204',9)""")
            executeInsertSql("""INSERT INTO reserved_role(id, name, authority, plugin_id) VALUES (-205,'Exh Administrator','ROLE_-205',9)""")
            executeInsertSql("""INSERT INTO reserved_role(id, name, authority, plugin_id) VALUES (-206,'Exh Development','ROLE_-206',9)""")

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * insert project track module default reserved_role into database when application starts with bootstrap
     */
    public void createDefaultDataForPT() {
        try {
            executeInsertSql("""INSERT INTO reserved_role(id, name, authority, plugin_id) VALUES (-31, 'ProjectTrack Admin', 'ROLE_-31', 10)""")
            executeInsertSql("""INSERT INTO reserved_role(id, name, authority, plugin_id) VALUES (-32, 'ProjectTrack Development Admin', 'ROLE_-32', 10)""")

        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createDefaultDataForSarb() {
        try {
            int count = appVersionService.countByPluginId(SarbPluginConnector.PLUGIN_ID)
            if (count > 0) return true

            executeInsertSql("""INSERT INTO reserved_role(id, name, authority, plugin_id) VALUES (-301,'SARB Administrator','ROLE_-301',12)""")
            executeInsertSql("""INSERT INTO reserved_role(id, name, authority, plugin_id) VALUES (-302,'SARB Development','ROLE_-302',12)""")

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Create default reserved_role for ARMS
     */
    public boolean createDefaultDataForArms() {
        try {
            int count = appVersionService.countByPluginId(ArmsPluginConnector.PLUGIN_ID)
            if (count > 0) return true

            executeInsertSql("""INSERT INTO reserved_role(id, name, authority, plugin_id) VALUES (-401,'ARMS Remittance User','ROLE_-401',11)""")
            executeInsertSql("""INSERT INTO reserved_role(id, name, authority, plugin_id) VALUES (-402,'ARMS Branch User','ROLE_-402',11)""")
            executeInsertSql("""INSERT INTO reserved_role(id, name, authority, plugin_id) VALUES (-403,'ARMS ExchangeHouse User','ROLE_-403',11)""")
            executeInsertSql("""INSERT INTO reserved_role(id, name, authority, plugin_id) VALUES (-405,'ARMS Administrator','ROLE_-405',11)""")
            executeInsertSql("""INSERT INTO reserved_role(id, name, authority, plugin_id) VALUES (-406,'ARMS Development','ROLE_-406',11)""")

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Create default reserved_role for Document
     */
    public void createDefaultDataForDocument() {
        try {
            executeInsertSql("""INSERT INTO reserved_role(id, name, authority, plugin_id) VALUES (${ROLE_TYPE_DOCUMENT_ADMINISTRATOR},'Administrator','ROLE_-501',13)""")
            executeInsertSql("""INSERT INTO reserved_role(id, name, authority, plugin_id) VALUES (${ROLE_TYPE_DOCUMENT_DEVELOPMENT},'Development Administrator','ROLE_-502',13)""")
            executeInsertSql("""INSERT INTO reserved_role(id, name, authority, plugin_id) VALUES (${ROLE_TYPE_DOCUMENT_MEMBER},'Member','ROLE_-503',13)""")
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Create default reserved_role for E-learning
     */
    public void createDefaultDataForElearning() {
        try {
            executeInsertSql("""INSERT INTO reserved_role(id, name, authority, plugin_id) VALUES (${ROLE_TYPE_E_LEARNING_ADMINISTRATOR},'EL Administrator','ROLE_-801',15)""")
            executeInsertSql("""INSERT INTO reserved_role(id, name, authority, plugin_id) VALUES (${ROLE_TYPE_E_LEARNING_DEVELOPMENT},'EL Development','ROLE_-802',15)""")
            executeInsertSql("""INSERT INTO reserved_role(id, name, authority, plugin_id) VALUES (${
                ROLE_TYPE_E_LEARNING_TEACHER
            },'Teacher','ROLE_-803',15)""")
            executeInsertSql("""INSERT INTO reserved_role(id, name, authority, plugin_id) VALUES (${
                ROLE_TYPE_E_LEARNING_COORDINATOR
            },'Coordinator','ROLE_-804',15)""")
            executeInsertSql("""INSERT INTO reserved_role(id, name, authority, plugin_id) VALUES (${
                ROLE_TYPE_E_LEARNING_STUDENT
            },'Student','ROLE_-805',15)""")
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Create default reserved_role for Document
     */
    public void createDefaultDataForDataPipeLine() {
        try {
            executeInsertSql("""INSERT INTO reserved_role(id, name, authority, plugin_id) VALUES (${ROLE_TYPE_DATA_PIPE_LINE_ADMINISTRATOR},'Administrator','ROLE_-601',14)""")
            executeInsertSql("""INSERT INTO reserved_role(id, name, authority, plugin_id) VALUES (${ROLE_TYPE_DATA_PIPE_LINE_DEVELOPMENT},'Development Administrator','ROLE_-602',14)""")

        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }
}
