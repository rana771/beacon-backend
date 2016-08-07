package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.Role
import com.athena.mis.application.entity.RoleModule
import com.athena.mis.integration.accounting.AccPluginConnector
import com.athena.mis.integration.arms.ArmsPluginConnector
import com.athena.mis.integration.budget.BudgPluginConnector
import com.athena.mis.integration.datapipeline.DataPipeLinePluginConnector
import com.athena.mis.integration.document.DocumentPluginConnector
import com.athena.mis.integration.elearning.ELearningPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.integration.fixedasset.FxdPluginConnector
import com.athena.mis.integration.inventory.InvPluginConnector
import com.athena.mis.integration.procurement.ProcPluginConnector
import com.athena.mis.integration.projecttrack.PtPluginConnector
import com.athena.mis.integration.qsmeasurement.QsPluginConnector
import com.athena.mis.integration.sarb.SarbPluginConnector
import org.apache.log4j.Logger

/**
 * RoleModuleService is used to handle only CRUD related object manipulation (e.g. list, read, create, delete etc.)
 */
class RoleModuleService extends BaseDomainService {

    private Logger log = Logger.getLogger(getClass())

    RoleService roleService

    @Override
    public void init() {
        domainClass = RoleModule.class
    }

    public RoleModule findByRoleIdAndModuleIdAndCompanyId(long roleId, long moduleId, long companyId) {
        return RoleModule.findByRoleIdAndModuleIdAndCompanyId(roleId, moduleId, companyId, [readOnly: true])
    }

    public List<RoleModule> findAllByRoleId(long roleId) {
        return RoleModule.findAllByRoleId(roleId, [readOnly: true])
    }

    public List<RoleModule> findAllByModuleId(long moduleId) {
        return RoleModule.findAllByModuleId(moduleId, [readOnly: true])
    }

    @Override
    public void createDefaultSchema() {
        String uniqueIndex = "create unique index role_module_role_id_module_id_idx on role_module(role_id, module_id);"
        executeSql(uniqueIndex)
    }

    public boolean update(RoleModule roleModule) {
        String query = """ UPDATE role_module
                        SET module_id = :moduleId
                        WHERE id=:id
        """

        Map queryParams = [id: roleModule.id, moduleId: roleModule.moduleId]
        int updateCount = executeUpdateSql(query, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException("error occurred at RoleModuleService.update")
        }
        return true
    }

    private static final String INSERT_QUERY = """
        INSERT INTO role_module(id, role_id, module_id, company_id)
        VALUES (NEXTVAL('role_module_id_seq'), :roleId, :moduleId, :companyId);
    """

    /**
     * Save RoleModule object into DB
     * @param roleModule - RoleModule object
     * @return - saved RoleModule object
     */
    public RoleModule create(RoleModule roleModule) {
        Map queryParams = [
                roleId   : roleModule.roleId,
                moduleId : roleModule.moduleId,
                companyId: roleModule.companyId
        ]

        List result = executeInsertSql(INSERT_QUERY, queryParams)
        if (result.size() <= 0) {
            throw new RuntimeException('Error occurred while insert Role-Module information')
        }
        int roleModuleId = (int) result[0][0]
        roleModule.id = roleModuleId
        return roleModule
    }

    public boolean createDefaultDataForApplication(long companyId) {
        try {
            Role roleAdmin = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_APP_ADMIN)
            Role roleDevAdmin = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_APP_DEVELOPMENT)
            long moduleId = PluginConnector.PLUGIN_ID
            new RoleModule(roleId: roleAdmin.id, moduleId: moduleId, companyId: companyId).save()
            new RoleModule(roleId: roleDevAdmin.id, moduleId: moduleId, companyId: companyId).save()
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public void createDefaultDataForPT(long companyId) {
        try {
            Role roleAdmin = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_PT_ADMIN)
            Role roleDevAdmin = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_PT_DEVELOPMENT)
            long moduleId = PtPluginConnector.PLUGIN_ID
            new RoleModule(roleId: roleAdmin.id, moduleId: moduleId, companyId: companyId).save()
            new RoleModule(roleId: roleDevAdmin.id, moduleId: moduleId, companyId: companyId).save()
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createDefaultDataForBudget(long companyId) {
        try {
            Role roleAdmin = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_BUDG_ADMIN)
            Role roleDevAdmin = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_BUDG_DEVELOPMENT)
            long moduleId = BudgPluginConnector.PLUGIN_ID
            new RoleModule(roleId: roleAdmin.id, moduleId: moduleId, companyId: companyId).save()
            new RoleModule(roleId: roleDevAdmin.id, moduleId: moduleId, companyId: companyId).save()
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createDefaultDataForProcurement(long companyId) {
        try {
            Role roleAdmin = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_PROC_ADMIN)
            Role roleDevAdmin = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_PROC_DEVELOPMENT)
            long moduleId = ProcPluginConnector.PLUGIN_ID
            new RoleModule(roleId: roleAdmin.id, moduleId: moduleId, companyId: companyId).save()
            new RoleModule(roleId: roleDevAdmin.id, moduleId: moduleId, companyId: companyId).save()
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return false
        }
    }

    public boolean createDefaultDataForInventory(long companyId) {
        try {
            Role roleAdmin = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_INV_ADMIN)
            Role roleDevAdmin = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_INV_DEVELOPMENT)
            long moduleId = InvPluginConnector.PLUGIN_ID
            new RoleModule(roleId: roleAdmin.id, moduleId: moduleId, companyId: companyId).save()
            new RoleModule(roleId: roleDevAdmin.id, moduleId: moduleId, companyId: companyId).save()
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createDefaultDataForAccounting(long companyId) {
        try {
            Role roleAdmin = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_ACC_ADMIN)
            Role roleDevAdmin = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_ACC_DEVELOPMENT)
            long moduleId = AccPluginConnector.PLUGIN_ID
            new RoleModule(roleId: roleAdmin.id, moduleId: moduleId, companyId: companyId).save()
            new RoleModule(roleId: roleDevAdmin.id, moduleId: moduleId, companyId: companyId).save()
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createDefaultDataForQS(long companyId) {
        try {
            Role roleAdmin = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_QS_ADMIN)
            Role roleDevAdmin = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_QS_DEVELOPMENT)
            long moduleId = QsPluginConnector.PLUGIN_ID
            new RoleModule(roleId: roleAdmin.id, moduleId: moduleId, companyId: companyId).save()
            new RoleModule(roleId: roleDevAdmin.id, moduleId: moduleId, companyId: companyId).save()
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return false
        }
    }

    public boolean createDefaultDataForFixedAsset(long companyId) {
        try {
            Role roleAdmin = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_FXD_ADMIN)
            Role roleDevAdmin = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_FXD_DEVELOPMENT)
            long moduleId = FxdPluginConnector.PLUGIN_ID
            new RoleModule(roleId: roleAdmin.id, moduleId: moduleId, companyId: companyId).save()
            new RoleModule(roleId: roleDevAdmin.id, moduleId: moduleId, companyId: companyId).save()
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createDefaultDataForExh(long companyId) {
        try {
            Role roleAdmin = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_EXH_ADMIN)
            Role roleDevAdmin = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_EXH_DEVELOPMENT)
            long moduleId = ExchangeHousePluginConnector.PLUGIN_ID
            new RoleModule(roleId: roleAdmin.id, moduleId: moduleId, companyId: companyId).save()
            new RoleModule(roleId: roleDevAdmin.id, moduleId: moduleId, companyId: companyId).save()

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createDefaultDataForSarb(long companyId) {
        try {
            Role roleAdmin = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_SARB_ADMIN)
            Role roleDevAdmin = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_SARB_DEVELOPMENT)
            long moduleId = SarbPluginConnector.PLUGIN_ID
            new RoleModule(roleId: roleAdmin.id, moduleId: moduleId, companyId: companyId).save()
            new RoleModule(roleId: roleDevAdmin.id, moduleId: moduleId, companyId: companyId).save()

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createDefaultDataForArms(long companyId) {
        try {
            Role roleAdmin = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_ARMS_ADMIN)
            Role roleDevAdmin = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_ARMS_DEVELOPMENT)
            long moduleId = ArmsPluginConnector.PLUGIN_ID
            new RoleModule(roleId: roleAdmin.id, moduleId: moduleId, companyId: companyId).save()
            new RoleModule(roleId: roleDevAdmin.id, moduleId: moduleId, companyId: companyId).save()

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public void createDefaultDataForDocument(long companyId) {
        try {
            Role roleAdmin = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_DOCUMENT_ADMINISTRATOR)
            Role roleDevAdmin = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_DOCUMENT_DEVELOPMENT)
            long moduleId = DocumentPluginConnector.PLUGIN_ID
            new RoleModule(roleId: roleAdmin.id, moduleId: moduleId, companyId: companyId).save()
            new RoleModule(roleId: roleDevAdmin.id, moduleId: moduleId, companyId: companyId).save()
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public void createDefaultDataForElearning(long companyId) {
        try {
            Role roleAdmin = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_E_LEARNING_ADMINISTRATOR)
            Role roleDevAdmin = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_E_LEARNING_DEVELOPMENT)
            Role roleStudent = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_E_LEARNING_STUDENT)
            Role roleTeacher = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_E_LEARNING_TEACHER)
            Role roleCoordinator = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_E_LEARNING_COORDINATOR)
            long moduleId = ELearningPluginConnector.PLUGIN_ID
            long docModuleId = DocumentPluginConnector.PLUGIN_ID
            new RoleModule(roleId: roleAdmin.id, moduleId: moduleId, companyId: companyId).save()
            new RoleModule(roleId: roleDevAdmin.id, moduleId: moduleId, companyId: companyId).save()
            new RoleModule(roleId: roleStudent.id, moduleId: moduleId, companyId: companyId).save()
            new RoleModule(roleId: roleTeacher.id, moduleId: moduleId, companyId: companyId).save()
            new RoleModule(roleId: roleCoordinator.id, moduleId: moduleId, companyId: companyId).save()
            new RoleModule(roleId: roleTeacher.id, moduleId: docModuleId, companyId: companyId).save()
            new RoleModule(roleId: roleCoordinator.id, moduleId: docModuleId, companyId: companyId).save()
            new RoleModule(roleId: roleAdmin.id, moduleId: docModuleId, companyId: companyId).save()
            new RoleModule(roleId: roleDevAdmin.id, moduleId: docModuleId, companyId: companyId).save()
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public void createDefaultDataForPipeline(long companyId) {
        try {
            Role roleAdmin = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_DATA_PIPE_LINE_ADMINISTRATOR)
            Role roleDevAdmin = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_DATA_PIPE_LINE_DEVELOPMENT)
            long moduleId = DataPipeLinePluginConnector.PLUGIN_ID
            new RoleModule(roleId: roleAdmin.id, moduleId: moduleId, companyId: companyId).save()
            new RoleModule(roleId: roleDevAdmin.id, moduleId: moduleId, companyId: companyId).save()
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    private static final String DELETE_ALL = """
        DELETE FROM role_module WHERE role_id = :roleId
    """

    /**
     * Delete all RoleModule by roleId
     * @param roleId - id of Role
     */
    public void deleteAllByRoleId(long roleId) {
        Map queryParams = [
                roleId: roleId
        ]
        executeUpdateSql(DELETE_ALL, queryParams)
    }

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }
}
