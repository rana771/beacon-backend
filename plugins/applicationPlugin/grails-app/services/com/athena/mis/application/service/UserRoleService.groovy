package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Company
import com.athena.mis.application.entity.Role
import com.athena.mis.application.entity.UserRole
import org.apache.log4j.Logger

/**
 *  Service class for basic User-Role mapping CRUD (Create, Update, Delete)
 *  For details go through Use-Case doc named 'UserRoleService'
 */
class UserRoleService extends BaseDomainService {

    private Logger log = Logger.getLogger(getClass())

    RoleService roleService
    AppUserService appUserService
    AppVersionService appVersionService
    CompanyService companyService

    @Override
    public void init() {
        domainClass = UserRole.class
    }

    @Override
    public void createDefaultSchema() {
        String sqlIndex = "CREATE UNIQUE INDEX user_role_user_id_role_id_idx ON user_role(user_id,role_id);"
        executeSql(sqlIndex)
    }

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }

    /**
     * SQL to update userRole object in database
     * @param oldUserRole -UserRole object
     * @param userId -AppUser.id
     * @return -int value(updateCount)
     */
    public boolean update(UserRole oldUserRole, long userId) {
        String query = """UPDATE user_role SET
                          user_id=:userId
                      WHERE
                          user_id=:oldUserId AND
                          role_id=:oldRoleId"""

        Map queryParams = [userId: userId, oldUserId: oldUserRole.userId, oldRoleId: oldUserRole.roleId]
        int updateCount = executeUpdateSql(query, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException("error occurred at userRoleService.update")
        }
        return true
    }

    /**
     * SQL to update userRole object in database
     * @param oldUserRole -UserRole object
     * @param roleId -Role.id
     * @return -int value(updateCount)
     */
    public boolean updateForCompanyUser(UserRole oldUserRole, long roleId) {
        String query = """
            UPDATE user_role SET
                role_id = :roleId
            WHERE
                user_id = :oldUserId AND
                role_id = :oldRoleId
        """

        Map queryParams = [roleId: roleId, oldUserId: oldUserRole.userId, oldRoleId: oldUserRole.roleId]
        int updateCount = executeUpdateSql(query, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException("error occurred at userRoleService.update")
        }
        return true
    }

    /**
     * get assigned roleList of a specific AppUser
     * @param userId -AppUser.id
     * @return -list of role
     */
    public List<Role> getRolesByUser(long userId) {
        List<Long> lstRoleIds = UserRole.findAllByUserId(userId, [readOnly: true]).collect { it.roleId }
        List<Role> lstRole = roleService.findAllByIdInList(lstRoleIds)
        return lstRole
    }

    public Long getRoleByUser(long userId, long companyId) {
        Long roleId = UserRole.findByUserId(userId, [readOnly: true]).roleId
        Role role = roleService.findByIdAndCompanyId(roleId, companyId)
        return role.id
    }

    /**
     * get specific UserRole object
     * @param userId -AppUser.id
     * @param roleId -Role.id
     * @return -UserRole object
     */
    public UserRole read(long userId, long roleId) {
        UserRole userRole = UserRole.findByUserIdAndRoleId(userId, roleId, [readOnly: true])
        return userRole
    }

    /**
     * get count of user mapped with role
     * @param roleId - id of role
     * @return -an integer containing the value of count
     */
    public int countByRoleId(long roleId) {
        int count = UserRole.countByRoleId(roleId)
        return count
    }

    /**
     * delete specific userRole object from DB
     * @param userRole -UserRole object
     * @return boolean value (true/false)
     */
    public Boolean delete(UserRole userRole) {
        if (!userRole) {
            return new Boolean(false)
        }
        userRole.delete()
        return new Boolean(true)
    }

    public List<UserRole> findAllByRoleIdInList(List<Long> lstRoleIds) {
        return UserRole.findAllByRoleIdInList(lstRoleIds, [readOnly: true])
    }

    public List<UserRole> findAllByUserId(long userId) {
        return UserRole.findAllByUserId(userId, [readOnly: true])
    }

    /**
     * insert default UserRole object for application plugin when application starts with bootstrap
     */
    public boolean createDefaultDataForApplication(long companyId) {
        try {
            Company company = (Company) companyService.read(companyId)
            String adminLoginId = 'appadmin@' + company.code.toLowerCase() + '.com'
            String superLoginId = 'appsuper@' + company.code.toLowerCase() + '.com'
            AppUser adminUser = appUserService.findByLoginIdAndCompanyId(adminLoginId, companyId)
            AppUser devAdmin = appUserService.findByLoginIdAndCompanyId(superLoginId, companyId)

            Role adminRole = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_APP_ADMIN)
            Role devAdminRole = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_APP_DEVELOPMENT)

            if (adminUser) {
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${adminUser.id}, ${adminRole.id})""")
            }
            if (devAdmin) {
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${devAdmin.id}, ${adminRole.id})""")
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${devAdmin.id}, ${
                    devAdminRole.id
                })""")
            }
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createDefaultDataForReseller() {
        try {
            int count = appVersionService.countByPluginId(PluginConnector.PLUGIN_ID)
            if (count > 0) {
                return true
            }
            AppUser reseller = appUserService.findByLoginId('reseller@athena.com')
            if (reseller) {
                Role role = roleService.findByAuthority('ROLE_RESELLER')
                if (role) {
                    executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${reseller.id},${role.id})""")
                }
            }
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * insert default UserRole objects for Budget plugin when application starts with bootstrap
     */
    public boolean createDefaultDataForBudget(long companyId) {
        try {
            AppUser budgAdmin = appUserService.findByLoginIdAndCompanyId('budgadmin@athena.com', companyId)
            AppUser budgDevAdmin = appUserService.findByLoginIdAndCompanyId('budgsuper@athena.com', companyId)

            Role adminRole = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_BUDG_ADMIN)
            Role devAdminRole = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_BUDG_DEVELOPMENT)

            if (budgAdmin) {
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${budgAdmin.id}, ${adminRole.id})""")
            }
            if (budgDevAdmin) {
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${budgDevAdmin.id}, ${
                    devAdminRole.id
                })""")
            }
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * insert default UserRole objects for Procurement plugin when application starts with bootstrap
     */
    public boolean createDefaultDataForProcurement(long companyId) {
        try {
            AppUser procAdmin = appUserService.findByLoginIdAndCompanyId('procadmin@athena.com', companyId)
            AppUser procDevAdmin = appUserService.findByLoginIdAndCompanyId('procsuper@athena.com', companyId)

            Role adminRole = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_PROC_ADMIN)
            Role devAdminRole = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_PROC_DEVELOPMENT)

            if (procAdmin) {
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${procAdmin.id}, ${adminRole.id})""")
            }
            if (procDevAdmin) {
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${procDevAdmin.id}, ${
                    devAdminRole.id
                })""")
            }
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return false
        }
    }

    /**
     * insert default UserRole objects for inventory plugin when application starts with bootstrap
     */
    public boolean createDefaultDataForInventory(long companyId) {
        try {
            AppUser invAdmin = appUserService.findByLoginIdAndCompanyId('invadmin@athena.com', companyId)
            AppUser invDevAdmin = appUserService.findByLoginIdAndCompanyId('invsuper@athena.com', companyId)

            Role adminRole = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_INV_ADMIN)
            Role devAdminRole = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_INV_DEVELOPMENT)

            if (invAdmin) {
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${invAdmin.id}, ${adminRole.id})""")
            }
            if (invDevAdmin) {
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${invDevAdmin.id}, ${
                    devAdminRole.id
                })""")
            }
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * insert default UserRole objects for Accounting plugin when application starts with bootstrap
     */
    public boolean createDefaultDataForAccounting(long companyId) {
        try {
            AppUser accAdmin = appUserService.findByLoginIdAndCompanyId('accadmin@athena.com', companyId)
            AppUser accDevAdmin = appUserService.findByLoginIdAndCompanyId('accsuper@athena.com', companyId)

            Role adminRole = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_ACC_ADMIN)
            Role devAdminRole = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_ACC_DEVELOPMENT)

            if (accAdmin) {
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${accAdmin.id}, ${adminRole.id})""")
            }
            if (accDevAdmin) {
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${accDevAdmin.id}, ${
                    devAdminRole.id
                })""")
            }
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * insert default UserRole objects for Qs plugin when application starts with bootstrap
     */
    public boolean createDefaultDataForQs(long companyId) {
        try {
            AppUser qsAdmin = appUserService.findByLoginIdAndCompanyId('qsadmin@athena.com', companyId)
            AppUser qsDevAdmin = appUserService.findByLoginIdAndCompanyId('qssuper@athena.com', companyId)

            Role adminRole = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_QS_ADMIN)
            Role devAdminRole = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_QS_DEVELOPMENT)

            if (qsAdmin) {
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${qsAdmin.id}, ${adminRole.id})""")
            }
            if (qsDevAdmin) {
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${qsDevAdmin.id}, ${
                    devAdminRole.id
                })""")
            }
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return false
        }
    }

    /**
     * insert default UserRole objects for Fixed Asset plugin when application starts with bootstrap
     */
    public boolean createDefaultDataForFixedAsset(long companyId) {
        try {
            AppUser fxdAdmin = appUserService.findByLoginIdAndCompanyId('fxdadmin@athena.com', companyId)
            AppUser fxdDevAdmin = appUserService.findByLoginIdAndCompanyId('fxdsuper@athena.com', companyId)

            Role adminRole = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_FXD_ADMIN)
            Role devAdminRole = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_FXD_DEVELOPMENT)

            if (fxdAdmin) {
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${fxdAdmin.id}, ${adminRole.id})""")
            }
            if (fxdDevAdmin) {
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${fxdDevAdmin.id}, ${
                    devAdminRole.id
                })""")
            }
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * insert default UserRole objects for exchangeHouse plugin when application starts with bootstrap
     */
    public boolean createDefaultDataExchangeHouse(long companyId) {
        try {
            AppUser userAdmin = appUserService.findByLoginId('exhadmin@athena.com')
            AppUser devUser = appUserService.findByLoginId('exhsuper@athena.com')
            AppUser userCashier = appUserService.findByLoginId('cashier@athena.com')
            AppUser userAgent = appUserService.findByLoginId('agent@athena.com')
            AppUser userOtherBank = appUserService.findByLoginId('other@athena.com')
            AppUser customer = appUserService.findByLoginId('customer@athena.com')

            Role appAdminRole = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_APP_ADMIN)
            Role exhAdminRole = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_EXH_ADMIN)
            Role appDevRole = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_APP_DEVELOPMENT)
            Role exhDevRole = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_EXH_DEVELOPMENT)
            Role exhCashierRole = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_EXH_CASHIER)
            Role exhCustomerRole = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_EXH_CUSTOMER)
            Role exhAgentRole = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_EXH_AGENT)
            Role exhOtherRole = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_EXH_OTHER_BANK)

            if (userAdmin) {
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${userAdmin.id},${
                    exhAdminRole.id
                })""")
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${userAdmin.id},${
                    appAdminRole.id
                })""")
            }

            if (devUser) {
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${devUser.id},${exhAdminRole.id})""")
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${devUser.id},${appAdminRole.id})""")
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${devUser.id},${exhDevRole.id})""")
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${devUser.id},${appDevRole.id})""")
            }

            if (userCashier) {
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${userCashier.id},${
                    exhCashierRole.id
                })""")
            }

            if (userAgent) {
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${userAgent.id},${
                    exhAgentRole.id
                })""")
            }

            if (userOtherBank) {
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${userOtherBank.id},${
                    exhOtherRole.id
                })""")
            }

            if (customer) {
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${customer.id},${
                    exhCustomerRole.id
                })""")
            }

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createDefaultDataForSarb(long companyId) {
        try {
            AppUser userAdmin = appUserService.findByLoginId('sarbadmin@athena.com')
            AppUser devUser = appUserService.findByLoginId('sarbsuper@athena.com')

            Role appAdminRole = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_APP_ADMIN)
            Role sarbAdminRole = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_SARB_ADMIN)
            Role appDevRole = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_APP_DEVELOPMENT)
            Role sarbDevRole = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_SARB_DEVELOPMENT)

            if (userAdmin) {
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${userAdmin.id},${
                    sarbAdminRole.id
                })""")
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${userAdmin.id},${
                    appAdminRole.id
                })""")
            }

            if (devUser) {
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${devUser.id},${
                    sarbAdminRole.id
                })""")
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${devUser.id},${appAdminRole.id})""")
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${devUser.id},${sarbDevRole.id})""")
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${devUser.id},${appDevRole.id})""")
            }

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Create default data for ARMS
     */
    public boolean createDefaultDataForArms(long companyId) {
        try {
            AppUser userAdmin = appUserService.findByLoginId('armsadmin@athena.com')
            AppUser userArmsDev = appUserService.findByLoginId('armssuper@athena.com')
            AppUser userRemittance = appUserService.findByLoginId('remittance@athena.com')
            AppUser userBranch = appUserService.findByLoginId('branch_banani@athena.com')
            AppUser userExh = appUserService.findByLoginId('exh@athena.com')
            AppUser userOtherBank = appUserService.findByLoginId('other_gulshan@athena.com')

            Role appAdminRole = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_APP_ADMIN)
            Role appDevRole = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_APP_DEVELOPMENT)
            Role armsAdminRole = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_ARMS_ADMIN)
            Role armsDevRole = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_ARMS_DEVELOPMENT)
            Role armsBranchRole = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_ARMS_BRANCH_USER)
            Role armsRemittanceRole = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_ARMS_REMITTANCE_USER)
            Role armsExhRole = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_ARMS_EXCHANGE_HOUSE_USER)

            if (userAdmin) {
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${userAdmin.id},${
                    armsAdminRole.id
                })""")
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${userAdmin.id},${
                    appAdminRole.id
                })""")
            }

            if (userArmsDev) {
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${userArmsDev.id},${
                    armsDevRole.id
                })""")
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${userArmsDev.id},${
                    armsAdminRole.id
                })""")
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${userArmsDev.id},${
                    appDevRole.id
                })""")
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${userArmsDev.id},${
                    appAdminRole.id
                })""")
            }

            if (userRemittance) {
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${userRemittance.id},${
                    armsRemittanceRole.id
                })""")
            }

            if (userBranch) {
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${userBranch.id},${
                    armsBranchRole.id
                })""")
            }

            if (userExh) {
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${userExh.id},${armsExhRole.id})""")
            }

            if (userOtherBank) {
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${userOtherBank.id},${
                    armsBranchRole.id
                })""")
            }

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * insert default UserRole objects for Document plugin when application starts with bootstrap
     */
    public void createDefaultDataDocument(long companyId) {
        try {
            Company company = (Company) companyService.read(companyId)
            String adminLoginId = 'docadmin@' + company.code.toLowerCase() + '.com'
            String superLoginId = 'docsuper@' + company.code.toLowerCase() + '.com'
            String memberLoginId = 'docmember@' + company.code.toLowerCase() + '.com'
            AppUser docAdminUser = appUserService.findByLoginIdAndCompanyId(adminLoginId, companyId)
            Role roleDocAdmin = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_DOCUMENT_ADMINISTRATOR)
            Role roleDocDev = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_DOCUMENT_DEVELOPMENT)
            Role roleDocMem = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_DOCUMENT_MEMBER)
            if (docAdminUser) executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${docAdminUser.id},${
                roleDocAdmin.id
            })""")
            AppUser docDevUser = appUserService.findByLoginIdAndCompanyId(superLoginId, companyId)
            if (docDevUser) executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${docDevUser.id},${
                roleDocDev.id
            })""")
            AppUser docMember = appUserService.findByLoginIdAndCompanyId(memberLoginId, companyId)
            if (docMember) executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${docMember.id},${
                roleDocMem.id
            })""")
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * insert default UserRole objects for Document plugin when application starts with bootstrap
     */
    public void createDefaultDataElearning(long companyId) {
        try {
            Company company = (Company) companyService.read(companyId)
            String adminLoginId = 'eladmin@' + company.code.toLowerCase() + '.com'
            AppUser elAdminUser = appUserService.findByLoginIdAndCompanyId(adminLoginId, companyId)
            Role roleElAdmin = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_E_LEARNING_ADMINISTRATOR)
            Role roleElDev = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_E_LEARNING_DEVELOPMENT)
            Role roleDocAdmin = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_DOCUMENT_ADMINISTRATOR)
            Role roleDocDev = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_DOCUMENT_DEVELOPMENT)
            if (elAdminUser) {
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${elAdminUser.id},${
                    roleElAdmin.id
                })""")
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${elAdminUser.id},${
                    roleDocAdmin.id
                })""")
            }

            String superLoginId = 'elsuper@' + company.code.toLowerCase() + '.com'
            AppUser elDevUser = appUserService.findByLoginIdAndCompanyId(superLoginId, companyId)
            if (elDevUser) {
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${elDevUser.id},${roleElDev.id})""")
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${elDevUser.id},${
                    roleElAdmin.id
                })""")
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${elDevUser.id},${roleDocDev.id})""")
            }

            String teacherLoginId = 'teacher@' + company.code.toLowerCase() + '.com'
            AppUser teacherUser = appUserService.findByLoginIdAndCompanyId(teacherLoginId, companyId)
            Role roleTeacher = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_E_LEARNING_TEACHER)
            if (teacherUser) executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${teacherUser.id},${
                roleTeacher.id
            })""")

            String coordinatorLoginId = 'coordinator@' + company.code.toLowerCase() + '.com'
            AppUser coordinatorUser = appUserService.findByLoginIdAndCompanyId(coordinatorLoginId, companyId)
            Role roleCoordinator = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_E_LEARNING_COORDINATOR)
            if (coordinatorUser) executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${
                coordinatorUser.id
            },${
                roleCoordinator.id
            })""")

            String studentLoginId = 'student@' + company.code.toLowerCase() + '.com'
            AppUser studentUser = appUserService.findByLoginIdAndCompanyId(studentLoginId, companyId)
            Role roleStudent = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_E_LEARNING_STUDENT)
            if (studentUser) executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${studentUser.id},${
                roleStudent.id
            })""")
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * insert default UserRole objects for Data Pipe Line plugin when application starts with bootstrap
     */
    public void createDefaultDataPipeLine(long companyId) {
        try {
            AppUser dplAdminUser = appUserService.findByLoginId('dpladmin@athena.com')
            Role roleDplAdmin = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_DATA_PIPE_LINE_ADMINISTRATOR)
            Role roleDplDev = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_DATA_PIPE_LINE_DEVELOPMENT)
            if (dplAdminUser) executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${dplAdminUser.id},${
                roleDplAdmin.id
            })""")
            AppUser dplDevUser = appUserService.findByLoginId('dplsuper@athena.com')
            if (dplDevUser) executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${dplDevUser.id},${
                roleDplDev.id
            })""")

        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * insert default UserRole object for project track plugin when application starts with bootstrap
     */
    public void createDefaultDataForProjectTrack(long companyId) {
        try {
            AppUser ptAdmin = appUserService.findByLoginIdAndCompanyId('ptadmin@athena.com', companyId)
            AppUser ptDevAdmin = appUserService.findByLoginIdAndCompanyId('ptsuper@athena.com', companyId)

            Role adminRole = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_PT_ADMIN)
            Role devAdminRole = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_PT_DEVELOPMENT)

            if (ptAdmin) {
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${ptAdmin.id}, ${adminRole.id})""")
            }
            if (ptDevAdmin) {
                executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${ptDevAdmin.id}, ${
                    devAdminRole.id
                })""")
            }
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public List getUserIdsByRoleId(long roleId) {
        List userIds = UserRole.withCriteria {
            eq('role.id', roleId)
            projections {
                property('user.id')
            }
        } as List
        return userIds
    }

    /**
     * insert test UserRole object for budget plugin
     */
    public void createTestDataForBudget(long companyId) {
        AppUser director = appUserService.findByLoginId('director@athena.com')
        AppUser projectDirector = appUserService.findByLoginId('pd@athena.com')
        if (director) {
            Role roleDir = roleService.findByNameAndCompanyId('Director', companyId)
            executeInsertSql("""INSERT INTO user_role (user_id, role_id) VALUES(${director.id}, ${roleDir.id})""")
        }
        if (projectDirector) {
            Role rolePd = roleService.findByNameAndCompanyId('Project Director', companyId)
            executeInsertSql("""INSERT INTO user_role (user_id, role_id) VALUES(${projectDirector.id}, ${rolePd.id})""")
        }
    }

    /**
     * insert test UserRole object for accounting plugin
     */
    public void createTestDataForAccounting(long companyId) {
        AppUser accountant = appUserService.findByLoginId('accountant@athena.com')
        AppUser cfo = appUserService.findByLoginId('cfo@athena.com')
        if (accountant) {
            Role roleAccountant = roleService.findByNameAndCompanyId('Accountant', companyId)
            executeInsertSql("""INSERT INTO user_role (user_id, role_id) VALUES(${accountant.id}, ${
                roleAccountant.id
            })""")
        }
        if (cfo) {
            Role roleCfo = roleService.findByNameAndCompanyId('CFO', companyId)
            Role adminRole = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_ACC_ADMIN)
            executeInsertSql("""INSERT INTO user_role (user_id, role_id) VALUES(${cfo.id}, ${roleCfo.id})""")
            executeInsertSql("""INSERT INTO user_role (user_id, role_id) VALUES(${cfo.id}, ${adminRole.id})""")
        }
    }

    /**
     * insert test UserRole object for project track plugin
     */
    public void createTestDataForPt(long companyId) {
        AppUser engineer = appUserService.findByLoginId('engineer@athena.com')
        AppUser sqa = appUserService.findByLoginId('sqa@athena.com')
        if (engineer) {
            Role roleEngineer = roleService.findByNameAndCompanyId('Software Engineer', companyId)
            executeInsertSql("""INSERT INTO user_role (user_id, role_id) VALUES(${engineer.id}, ${roleEngineer.id})""")
        }
        if (sqa) {
            Role roleSqa = roleService.findByNameAndCompanyId('SQA', companyId)
            executeInsertSql("""INSERT INTO user_role (user_id, role_id) VALUES(${sqa.id}, ${roleSqa.id})""")
        }
    }

    /**
     * insert test UserRole object for inventory plugin
     */
    public void createTestDataForInventory(long companyId) {
        AppUser auditor = appUserService.findByLoginId('auditor@athena.com')
        AppUser projectManager = appUserService.findByLoginId('pm@athena.com')
        if (auditor) {
            Role roleAuditor = roleService.findByNameAndCompanyId('Inventory Auditor', companyId)
            executeInsertSql("""INSERT INTO user_role (user_id, role_id) VALUES(${auditor.id}, ${roleAuditor.id})""")
        }
        if (projectManager) {
            Role rolePm = roleService.findByNameAndCompanyId('Project Manager', companyId)
            executeInsertSql("""INSERT INTO user_role (user_id, role_id) VALUES(${projectManager.id}, ${rolePm.id})""")
        }
    }

    /**
     * SQL to delete test data from database
     * @param userIds - list of user ids
     */
    public void deleteTestData(List<Long> userIds) {
        if (userIds.size() > 0) {
            String lstUserIds = super.buildCommaSeparatedStringOfIds(userIds)
            String query = """ DELETE FROM user_role WHERE user_id IN (${lstUserIds}) """
            executeUpdateSql(query)
        }
    }
}
