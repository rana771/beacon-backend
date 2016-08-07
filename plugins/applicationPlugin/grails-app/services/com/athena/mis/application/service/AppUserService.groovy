package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Company
import com.athena.mis.integration.accounting.AccPluginConnector
import com.athena.mis.integration.budget.BudgPluginConnector
import com.athena.mis.integration.fixedasset.FxdPluginConnector
import com.athena.mis.integration.inventory.InvPluginConnector
import com.athena.mis.integration.procurement.ProcPluginConnector
import com.athena.mis.integration.qsmeasurement.QsPluginConnector
import com.athena.mis.utility.DateUtility
import grails.plugin.springsecurity.SpringSecurityService
import org.apache.log4j.Logger

/**
 * AppUserService is used to handle only CRUD related object manipulation (e.g. list, read, create, delete etc.)
 */
class AppUserService extends BaseDomainService {

    private Logger log = Logger.getLogger(getClass())

    SpringSecurityService springSecurityService
    TestDataModelService testDataModelService
    AppVersionService appVersionService
    CompanyService companyService

    private static final String INSERT_QUERY = """
        INSERT INTO app_user(id, version, account_expired, account_locked, company_id, enabled, login_id, next_expire_date,
            password, password_expired, has_signature, username, cell_number, ip_address, employee_id, is_company_user,
            activation_link, is_activated_by_mail, is_power_user, is_config_manager, is_system_user, is_disable_password_expiration,
            created_on, created_by, updated_by, updated_on, is_reseller, gender_id, email, is_switchable, is_reserved)
        VALUES(NEXTVAL('app_user_id_seq'), :version, :accountExpired, :accountLocked, :companyId, :enabled, :loginId,
            :nextExpireDate, :password, :passwordExpired, :hasSignature, :username, :cellNumber, :ipAddress, :employeeId,
            :isCompanyUser, :activationLink, :isActivatedByMail, :isPowerUser, :isConfigManager, :isSystemUser, :isDisablePasswordExpiration,
            :createdOn, :createdBy, :updatedBy, null, :isReseller, :genderId, :email, :isSwitchable, :isReserved)
    """

    @Override
    public void init() {
        domainClass = AppUser.class
    }

    /**
     * Save AppUser object into DB
     * @param appUser -object of AppUser
     * @return -saved AppUser object
     */
    public AppUser create(AppUser appUser) {
        appUser.version = 0
        Map queryParams = [
                version                    : appUser.version,
                accountExpired             : appUser.accountExpired,
                accountLocked              : appUser.accountLocked,
                companyId                  : appUser.companyId,
                enabled                    : appUser.enabled,
                loginId                    : appUser.loginId,
                nextExpireDate             : DateUtility.getSqlDateWithSeconds(appUser.nextExpireDate),
                password                   : appUser.password,
                passwordExpired            : appUser.passwordExpired,
                hasSignature               : appUser.hasSignature,
                username                   : appUser.username,
                cellNumber                 : appUser.cellNumber,
                ipAddress                  : appUser.ipAddress,
                employeeId                 : appUser.employeeId,
                isCompanyUser              : appUser.isCompanyUser,
                activationLink             : appUser.activationLink,
                isActivatedByMail          : appUser.isActivatedByMail,
                isPowerUser                : appUser.isPowerUser,
                isConfigManager            : appUser.isConfigManager,
                isSystemUser               : appUser.isSystemUser,
                isDisablePasswordExpiration: appUser.isDisablePasswordExpiration,
                createdOn                  : DateUtility.getSqlDateWithSeconds(appUser.createdOn),
                createdBy                  : appUser.createdBy,
                updatedBy                  : appUser.updatedBy,
                isReseller                 : appUser.isReseller,
                genderId                   : appUser.genderId,
                email                      : appUser.email,
                isSwitchable               : appUser.isSwitchable,
                isReserved                 : appUser.isReserved
        ]

        List result = executeInsertSql(INSERT_QUERY, queryParams)
        if (result.size() <= 0) {
            throw new RuntimeException('Error occurred while insert app user information')
        }

        int userId = (int) result[0][0]
        appUser.id = userId
        return appUser
    }

    private static final String UPDATE_QUERY = """
                      UPDATE app_user SET
                          version=version+1,
                          enabled=:enabled,
                          account_locked=:accountLocked,
                          account_expired=:accountExpired,
                          has_signature=:hasSignature,
                          login_id=:loginId,
                          password=:password,
                          username=:userName,
                          company_id=:companyId,
                          cell_number=:cellNumber,
                          ip_address=:ipAddress,
                          employee_id=:employeeId,
                          is_activated_by_mail = :isActivatedByMail,
                          is_power_user=:isPowerUser,
                          is_config_manager=:isConfigManager,
                          is_disable_password_expiration=:isDisablePasswordExpiration,
                          updated_on=:updatedOn,
                          updated_by=:updatedBy,
                          gender_id=:genderId,
                          email=:email,
                          is_switchable=:isSwitchable,
                          is_reserved=:isReserved
                      WHERE
                          id=:id AND
                          version=:version
    """

    /**
     * Update AppUser object in DB
     * @param appUser -object of AppUser
     * @return -an integer containing the value of update count
     */
    public AppUser update(AppUser appUser) {

        Map queryParams = [
                id                         : appUser.id,
                version                    : appUser.version,
                enabled                    : appUser.enabled,
                accountLocked              : appUser.accountLocked,
                accountExpired             : appUser.accountExpired,
                hasSignature               : appUser.hasSignature,
                loginId                    : appUser.loginId,
                password                   : appUser.password,
                userName                   : appUser.username,
                companyId                  : appUser.companyId,
                cellNumber                 : appUser.cellNumber,
                ipAddress                  : appUser.ipAddress,
                employeeId                 : appUser.employeeId,
                isActivatedByMail          : appUser.isActivatedByMail,
                isPowerUser                : appUser.isPowerUser,
                isConfigManager            : appUser.isConfigManager,
                isDisablePasswordExpiration: appUser.isDisablePasswordExpiration,
                updatedBy                  : appUser.updatedBy,
                updatedOn                  : DateUtility.getSqlDateWithSeconds(appUser.updatedOn),
                genderId                   : appUser.genderId,
                email                      : appUser.email,
                isSwitchable               : appUser.isSwitchable,
                isReserved                 : appUser.isReserved
        ]

        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException("error occurred at appUserService.update")
        }
        appUser.version = appUser.version + 1
        return appUser
    }

    private static final String UPDATE_PROFILE_QUERY = """
                      UPDATE app_user SET
                          version = version+1,
                          username = :userName,
                          cell_number = :cellNumber,
                          gender_id = :genderId,
                          updated_on = :updatedOn,
                          updated_by = :updatedBy,
                          email = :email
                      WHERE
                          id = :id
    """

    public AppUser updateProfile(AppUser appUser) {

        Map queryParams = [
                id        : appUser.id,
                version   : appUser.version,
                userName  : appUser.username,
                cellNumber: appUser.cellNumber,
                genderId  : appUser.genderId,
                updatedBy : appUser.updatedBy,
                updatedOn : DateUtility.getSqlDateWithSeconds(appUser.updatedOn),
                email     : appUser.email
        ]

        int updateCount = executeUpdateSql(UPDATE_PROFILE_QUERY, queryParams)
        appUser.version = appUser.version + 1
        return appUser
    }


    private static final String UPDATE_PROFILE_IMAGE_QUERY = """
                      UPDATE app_user SET
                          has_signature = :hasSignature,
                          updated_on = :updatedOn,
                          updated_by = :updatedBy
                      WHERE
                          id = :id
    """

    public AppUser updateProfileImage(AppUser appUser) {

        Map queryParams = [
                id          : appUser.id,
                hasSignature: appUser.hasSignature,
                updatedBy   : appUser.updatedBy,
                updatedOn   : DateUtility.getSqlDateWithSeconds(appUser.updatedOn)
        ]

        int updateCount = executeUpdateSql(UPDATE_PROFILE_IMAGE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException("error occurred at appUserService.uploadProfileImage")
        }
        return appUser
    }

    private static final String UPDATE_QUERY_FOR_ENABLED = """
        UPDATE app_user
        SET enabled=:isEnabled,
            version=:version
        WHERE id=:id
        AND company_id=:companyId
    """

    /**
     * For exchange house block and unblock customer
     * Update enabled property of appUser
     * @param appUser - appUser obj
     * @return- updateCount
     */
    public int updateEnabled(AppUser appUser) {
        Map queryParams = [
                isEnabled: appUser.enabled,
                id       : appUser.id,
                companyId: appUser.companyId,
                version  : appUser.version
        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY_FOR_ENABLED, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException("Error occurred while updating app user")
        }
        return updateCount
    }

    /**
     * @return - list of AppUser
     */
    public List listForUtility() {
        return AppUser.list(sort: AppUser.DEFAULT_SORT_ORDER, order: ASCENDING_SORT_ORDER, readOnly: true);
    }

    private static final String UPDATE_PASSWORD_QUERY = """
                  UPDATE app_user SET
                      version=version+1,
                      password=:password
                  WHERE
                      id=:id AND
                      version=:version
    """

    /**
     * Update password of AppUser
     * @param appUser -object of AppUser
     * @return -an integer containing the value of update count
     */
    public int updatePassword(AppUser appUser) {
        Map queryParams = [
                id      : appUser.id,
                version : appUser.version,
                password: appUser.password
        ]

        int updateCount = executeUpdateSql(UPDATE_PASSWORD_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException("Error occurred at appUserService.updatePassword")
        }
        return updateCount
    }

    private static final String UPDATE_DEFAULT_URL_QUERY = """
                  UPDATE app_user
                  SET version = version+1,
                      defaultUrl = NULL
                  WHERE id = :id
    """

    /**
     * Update password of AppUser
     * @param appUser -object of AppUser
     * @return -an integer containing the value of update count
     */
    public int updateDefaultUrl(AppUser appUser) {
        Map queryParams = [
                id      : appUser.id,
                password: appUser.password
        ]

        int updateCount = executeUpdateSql(UPDATE_DEFAULT_URL_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException("Error occurred at appUserService.updateDefaultUrl")
        }
        return updateCount
    }

    private static final String DELETE_SYSTEM_USER = """
        DELETE FROM app_user WHERE company_id = :companyId
    """

    /**
     * Delete systemUser
     * @param companyId - id of company
     */
    public void deleteSystemUser(long companyId) {
        Map queryParams = [
                companyId: companyId
        ]
        executeUpdateSql(DELETE_SYSTEM_USER, queryParams)
    }

    /**
     * Get count of AppUser by companyId
     * @param companyId -id of company
     * @return -an integer containing the value of count
     */
    public int countByCompanyId(long companyId) {
        int count = AppUser.countByCompanyId(companyId)
        return count
    }

    /**
     * Get count of AppUser by companyId and isSystemUser
     * @param companyId - id of company
     * @param isSystemUser - boolean value true/false
     * @return - an integer containing the value of count
     */
    public int countByCompanyIdAndIsSystemUser(long companyId, boolean isSystemUser) {
        int count = AppUser.countByCompanyIdAndIsSystemUser(companyId, isSystemUser)
        return count
    }

    /**
     * Get list of AppUser by isCompanyUser(true/false)
     * @param isCompanyUser -true/false
     * @param baseService -instance of baseService
     * @return -list of AppUser
     */
    public List<AppUser> findAllByIsCompanyUser(boolean isCompanyUser) {
        List<AppUser> lstAppUser = AppUser.findAllByIsCompanyUser(isCompanyUser, [max: resultPerPage, offset: start, sort: sortColumn, order: sortOrder, readOnly: true])
        return lstAppUser
    }

    /**
     * Get count of AppUser by isCompanyUser(true/false)
     * @param isCompanyUser -true/false
     * @return -an integer containing the value of count
     */
    public int countByIsCompanyUser(boolean isCompanyUser) {
        int count = AppUser.countByIsCompanyUser(isCompanyUser)
        return count
    }

    /**
     * Get AppUser object by passwordResetLink and companyId
     * @param passwordResetLink -link for reset password
     * @param companyId -id of company
     * @return -object of AppUser
     */
    public AppUser findByPasswordResetLinkAndCompanyId(String passwordResetLink, long companyId) {
        AppUser appUser = AppUser.findByPasswordResetLinkAndCompanyId(passwordResetLink, companyId, [readOnly: true])
        return appUser
    }

    /**
     * Get AppUser object by loginId and companyId
     * @param loginId -loginId of AppUser
     * @param companyId -id of company
     * @return -object of AppUser
     */
    public AppUser findByLoginIdAndCompanyId(String loginId, long companyId) {
        AppUser appUser = AppUser.findByLoginIdAndCompanyId(loginId, companyId, [readOnly: true])
        return appUser
    }

    /**
     * get switchable user except session user
     * @param isSwitchable
     * @param enabled
     * @param userId
     * @return - List of AppUser
     */
    public List<AppUser> findAllByIsSwitchableAndEnabledAndCompanyIdAndIdNotEqual(boolean isSwitchable, boolean enabled, long companyId, long userId) {
        List<AppUser> appUserList = AppUser.findAllByIsSwitchableAndEnabledAndCompanyIdAndIdNotEqual(isSwitchable, enabled, companyId, userId, [readOnly: true])
        return appUserList
    }

    /**
     * Get AppUser object by loginId
     * @param loginId -loginId of AppUser
     * @return -object of AppUser
     */
    public AppUser findByLoginId(String loginId) {
        AppUser appUser = AppUser.findByLoginId(loginId, [readOnly: true])
        return appUser
    }

    /**
     * Get AppUser list by loginId in list
     * @param loginIds - list of login ids
     * @return - List of AppUser
     */
    public List<AppUser> findAllByLoginIdInList(List<String> loginIds) {
        List<AppUser> appUserList = AppUser.findAllByLoginIdInList(loginIds, [readOnly: true])
        return appUserList
    }

    /**
     * Get AppUser object by activationLink and companyId
     * @param activationLink -activationLink of AppUser
     * @param companyId -id of company
     * @return -object of AppUser
     */
    public AppUser findByActivationLinkAndCompanyId(String activationLink, long companyId) {
        AppUser appUser = AppUser.findByActivationLinkAndCompanyId(activationLink, companyId, [readOnly: true])
        return appUser
    }

    /**
     * get list of AppUser by companyId, enabled(true/false) and list of user ids
     * @param companyId -id of company
     * @param enabled -true/false
     * @param lstUserIds -list of user ids
     * @return -a list of AppUser
     */
    public List<AppUser> findAllByCompanyIdAndEnabledAndIdInList(long companyId, boolean enabled, List lstUserIds) {
        List<AppUser> lstAppUser = AppUser.findAllByCompanyIdAndEnabledAndIdInList(companyId, enabled, lstUserIds, [readOnly: true])
        return lstAppUser
    }

    public int countByLoginIdIlikeAndCompanyId(String loginId, long companyId) {
        int count = AppUser.countByLoginIdIlikeAndCompanyId(loginId, companyId)
        return count
    }

    public int countByLoginIdIlike(String loginId) {
        int count = AppUser.countByLoginIdIlike(loginId)
        return count
    }

    public int countByEmailIlikeAndCompanyId(String loginId, long companyId) {
        int count = AppUser.countByEmailIlikeAndCompanyId(loginId, companyId)
        return count
    }

    public int countByEmailIlikeAndCompanyIdAndIdNotEqual(String loginId, long companyId, long id) {
        int count = AppUser.countByEmailIlikeAndCompanyIdAndIdNotEqual(loginId, companyId,id)
        return count
    }

    public int countByLoginIdIlikeAndIdNotEqual(String loginId, long id) {
        int count = AppUser.countByLoginIdIlikeAndIdNotEqual(loginId, id)
        return count
    }

    public int countByLoginIdIlikeAndCompanyIdAndIdNotEqual(String loginId, long companyId, long id) {
        int count = AppUser.countByLoginIdIlikeAndCompanyIdAndIdNotEqual(loginId, companyId, id)
        return count
    }

    public AppUser findByIdAndCompanyId(long id, long companyId) {
        AppUser user = AppUser.findByIdAndCompanyId(id, companyId, [readOnly: true])
        return user
    }

    public List<AppUser> enableUserList() {
        long companyId = super.getCompanyId()
        List<AppUser> lstAppUser = AppUser.findAllByCompanyIdAndEnabledAndIsReseller(companyId, true, false, [readOnly: true])
        return lstAppUser
    }

    public int countByIsCompanyUserAndCompanyId(long companyId) {
        int count = AppUser.countByIsCompanyUserAndCompanyId(true, companyId)
        return count
    }

    public List<AppUser> findAllByIdInListAndEnabled(List<Long> lstUserIds, boolean enabled) {
        return AppUser.findAllByIdInListAndEnabled(lstUserIds, enabled, [readOnly: true])
    }

    /**
     * Get list and count of AppUser by specific search keyword
     * @return -a map containing list and count of AppUser
     */
    public Map search(BaseService baseService) {
        long companyId = getCompanyId()
        List<AppUser> appUserList = AppUser.withCriteria {
            eq('companyId', companyId)
            eq('isCompanyUser', false)
            ilike(baseService.queryType, PERCENTAGE + baseService.query + PERCENTAGE)
            maxResults(baseService.resultPerPage)
            firstResult(baseService.start)
            order(baseService.sortColumn, baseService.sortOrder)
        } as List

        List counts = AppUser.withCriteria {
            eq('companyId', companyId)
            eq('isCompanyUser', false)
            ilike(baseService.queryType, PERCENTAGE + baseService.query + PERCENTAGE)
            projections { rowCount() }
        } as List

        int total = counts[0] as int
        return [appUserList: appUserList, count: total]
    }

    /**
     * read system user
     * @param companyId
     * @return
     */
    public AppUser readSystemUserByCompanyId(long companyId) {
        return AppUser.findByCompanyIdAndIsSystemUser(companyId, Boolean.TRUE, [readOnly: true])
    }

    /**
     * Create default AppUser objects for Application plugin
     */
    public boolean createReseller(long companyId) {
        try {
            int count = appVersionService.countByPluginId(PluginConnector.PLUGIN_ID)
            if (count > 0) {
                return true
            }
            new AppUser(accountExpired: false, accountLocked: false, isPowerUser: false,
                    enabled: true, loginId: 'reseller@athena.com', isDisablePasswordExpiration: true,
                    nextExpireDate: new Date() + 365, isSystemUser: false,
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: 'Reseller', companyId: 1,
                    isConfigManager: false, createdBy: 1, createdOn: new Date(), isReseller: true, isSwitchable: false, isReserved: false).save()
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createDefaultDataForApplication(long companyId) {
        try {
            Company company = (Company) companyService.read(companyId)
            new AppUser(accountExpired: false, accountLocked: false, isPowerUser: true,
                    enabled: true, loginId: 'appadmin@' + company.code.toLowerCase() + '.com',
                    nextExpireDate: new Date() + 365,
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: 'Administrator', companyId: companyId,
                    createdBy: 1, createdOn: new Date(), isReseller: false, isSwitchable: false, isReserved: true).save()

            new AppUser(accountExpired: false, accountLocked: false, isPowerUser: true,
                    enabled: true, loginId: 'appsuper@' + company.code.toLowerCase() + '.com',
                    nextExpireDate: new Date() + 365, isCompanyUser: true,
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: 'Configuration Manager', companyId: companyId,
                    isConfigManager: true, createdBy: 1, createdOn: new Date(), isReseller: false, isSwitchable: false, isReserved: true).save()

            new AppUser(accountExpired: false, accountLocked: false, isPowerUser: false,
                    enabled: false, loginId: 'systemuser@' + company.code.toLowerCase() + '.com',
                    nextExpireDate: new Date() + 365, isSystemUser: true,
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: 'System User', companyId: companyId,
                    isConfigManager: false, createdBy: 1, createdOn: new Date(), isReseller: false, isSwitchable: false, isReserved: false).save()
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Create system user
     */
    public boolean createSystemUser(long companyId) {
        try {
            new AppUser(accountExpired: false, accountLocked: false, isPowerUser: false,
                    enabled: false, loginId: 'systemuser_' + companyId + '@company.com',
                    nextExpireDate: new Date() + 365, isSystemUser: true,
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: 'System User', companyId: companyId,
                    isConfigManager: false, createdBy: 1, createdOn: new Date(), isReseller: false, isSwitchable: false, isReserved: false).save()
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Create default AppUser objects for Budget plugin
     */
    public boolean createDefaultDataForBudget(long companyId) {
        try {
            int count = appVersionService.countByPluginId(BudgPluginConnector.PLUGIN_ID)
            if (count > 0) {
                return true
            }
            new AppUser(accountExpired: false, accountLocked: false,
                    enabled: true, loginId: 'budgadmin@athena.com',
                    nextExpireDate: new Date() + 365, isPowerUser: true,
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: 'Budget Admin', companyId: companyId,
                    createdBy: 1, createdOn: new Date(), isReseller: false, isSwitchable: false, isReserved: false).save()

            new AppUser(accountExpired: false, accountLocked: false,
                    enabled: true, loginId: 'budgsuper@athena.com',
                    nextExpireDate: new Date() + 365, isConfigManager: true,
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: 'Budget Dev Admin', companyId: companyId,
                    createdBy: 1, createdOn: new Date(), isReseller: false, isSwitchable: false, isReserved: false).save()
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Create default AppUser objects for Procurement plugin
     */
    public boolean createDefaultDataForProcurement(long companyId) {
        try {
            int count = appVersionService.countByPluginId(ProcPluginConnector.PLUGIN_ID)
            if (count > 0) {
                return true
            }
            new AppUser(accountExpired: false, accountLocked: false,
                    enabled: true, loginId: 'procadmin@athena.com',
                    nextExpireDate: new Date() + 365, isPowerUser: true,
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: 'Proc Admin', companyId: companyId,
                    createdBy: 1, createdOn: new Date(), isReseller: false, isSwitchable: false, isReserved: false).save()

            new AppUser(accountExpired: false, accountLocked: false,
                    enabled: true, loginId: 'procsuper@athena.com',
                    nextExpireDate: new Date() + 365, isConfigManager: true,
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: 'Proc Dev Admin', companyId: companyId,
                    createdBy: 1, createdOn: new Date(), isReseller: false, isSwitchable: false, isReserved: false).save()
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Create default AppUser objects for Inventory plugin
     */
    public boolean createDefaultDataForInventory(long companyId) {
        try {
            int count = appVersionService.countByPluginId(InvPluginConnector.PLUGIN_ID)
            if (count > 0) {
                return true
            }
            new AppUser(accountExpired: false, accountLocked: false,
                    enabled: true, loginId: 'invadmin@athena.com',
                    nextExpireDate: new Date() + 365, isPowerUser: true,
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: 'Inv Admin', companyId: companyId,
                    createdBy: 1, createdOn: new Date(), isReseller: false, isSwitchable: false, isReserved: false).save()

            new AppUser(accountExpired: false, accountLocked: false,
                    enabled: true, loginId: 'invsuper@athena.com',
                    nextExpireDate: new Date() + 365, isConfigManager: true,
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: 'Inv Dev Admin', companyId: companyId,
                    createdBy: 1, createdOn: new Date(), isReseller: false, isSwitchable: false, isReserved: false).save()
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Create default AppUser objects for Accounting plugin
     */
    public boolean createDefaultDataForAccounting(long companyId) {
        try {
            int count = appVersionService.countByPluginId(AccPluginConnector.PLUGIN_ID)
            if (count > 0) {
                return true
            }
            new AppUser(accountExpired: false, accountLocked: false,
                    enabled: true, loginId: 'accadmin@athena.com',
                    nextExpireDate: new Date() + 365, isPowerUser: true,
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: 'Acc Admin', companyId: companyId,
                    createdBy: 1, createdOn: new Date(), isReseller: false, isSwitchable: false, isReserved: false).save()

            new AppUser(accountExpired: false, accountLocked: false,
                    enabled: true, loginId: 'accsuper@athena.com',
                    nextExpireDate: new Date() + 365, isConfigManager: true,
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: 'Acc Dev Admin', companyId: companyId,
                    createdBy: 1, createdOn: new Date(), isReseller: false, isSwitchable: false, isReserved: false).save()
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Create default AppUser objects for Qs plugin
     */
    public boolean createDefaultDataForQs(long companyId) {
        try {
            int count = appVersionService.countByPluginId(QsPluginConnector.PLUGIN_ID)
            if (count > 0) {
                return true
            }
            new AppUser(accountExpired: false, accountLocked: false,
                    enabled: true, loginId: 'qsadmin@athena.com',
                    nextExpireDate: new Date() + 365, isPowerUser: true,
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: 'Qs Admin', companyId: companyId,
                    createdBy: 1, createdOn: new Date(), isReseller: false, isSwitchable: false, isReserved: false).save()

            new AppUser(accountExpired: false, accountLocked: false,
                    enabled: true, loginId: 'qssuper@athena.com',
                    nextExpireDate: new Date() + 365, isConfigManager: true,
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: 'Qs Dev Admin', companyId: companyId,
                    createdBy: 1, createdOn: new Date(), isReseller: false, isSwitchable: false, isReserved: false).save()
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Create default AppUser objects for Fixed Asset plugin
     */
    public boolean createDefaultDataForFixedAsset(long companyId) {
        try {
            int count = appVersionService.countByPluginId(FxdPluginConnector.PLUGIN_ID)
            if (count > 0) {
                return true
            }
            new AppUser(accountExpired: false, accountLocked: false,
                    enabled: true, loginId: 'fxdadmin@athena.com',
                    nextExpireDate: new Date() + 365, isPowerUser: true,
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: 'Fxd Admin', companyId: companyId,
                    createdBy: 1, createdOn: new Date(), isReseller: false, isSwitchable: false, isReserved: false).save()

            new AppUser(accountExpired: false, accountLocked: false,
                    enabled: true, loginId: 'fxdsuper@athena.com',
                    nextExpireDate: new Date() + 365, isConfigManager: true,
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: 'Fxd Dev Admin', companyId: companyId,
                    createdBy: 1, createdOn: new Date(), isReseller: false, isSwitchable: false, isReserved: false).save()
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Create default AppUser objects for ExchangeHouse plugin
     */
    public boolean createDefaultDataForExchangeHouse(long companyId, long systemUserId) {
        try {
            new AppUser(accountExpired: false, accountLocked: false, isPowerUser: true,
                    enabled: true, loginId: 'exhadmin@athena.com',
                    nextExpireDate: new Date() + 365,
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: 'Exh Administrator', companyId: companyId,
                    createdBy: 1, createdOn: new Date(), isReseller: false, isSwitchable: false, isReserved: false).save()

            new AppUser(accountExpired: false, accountLocked: false, isPowerUser: true,
                    enabled: true, loginId: 'exhsuper@athena.com',
                    nextExpireDate: new Date() + 365, isCompanyUser: false,
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: 'Exh Development', companyId: companyId,
                    isConfigManager: true, createdBy: 1, createdOn: new Date(), isReseller: false, isSwitchable: false, isReserved: false).save()

            new AppUser(accountExpired: false, accountLocked: false,
                    enabled: true, loginId: 'cashier@athena.com',
                    nextExpireDate: new Date() + 365,
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: 'Cashier', companyId: companyId,
                    createdBy: systemUserId, createdOn: new Date(), isReseller: false, isSwitchable: false, isReserved: false).save()

            new AppUser(accountExpired: false, accountLocked: false,
                    enabled: true, loginId: 'agent@athena.com',
                    nextExpireDate: new Date() + 365,
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: 'Agent', companyId: companyId,
                    createdBy: systemUserId, createdOn: new Date(), isReseller: false, isSwitchable: false, isReserved: false).save()

            new AppUser(accountExpired: false, accountLocked: false,
                    enabled: true, loginId: 'other@athena.com',
                    nextExpireDate: new Date() + 365,
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: 'Other Bank User', companyId: companyId,
                    createdBy: systemUserId, createdOn: new Date(), isReseller: false, isSwitchable: false, isReserved: false).save()
            new AppUser(accountExpired: false, accountLocked: false,
                    enabled: true, loginId: 'customer@athena.com',
                    nextExpireDate: new Date() + 365,
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: 'Sample Customer', companyId: companyId,
                    createdBy: systemUserId, createdOn: new Date(), isReseller: false, isSwitchable: false, isReserved: false).save()

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createDefaultDataForSarb(long companyId) {
        try {
            new AppUser(accountExpired: false, accountLocked: false, isPowerUser: true,
                    enabled: true, loginId: 'sarbadmin@athena.com',
                    nextExpireDate: new Date() + 365,
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: 'SARB Administrator', companyId: companyId,
                    createdBy: 1, createdOn: new Date(), isReseller: false, isSwitchable: false, isReserved: false).save()

            new AppUser(accountExpired: false, accountLocked: false, isPowerUser: true,
                    enabled: true, loginId: 'sarbsuper@athena.com',
                    nextExpireDate: new Date() + 365, isCompanyUser: false,
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: 'SARB Development', companyId: companyId,
                    isConfigManager: true, createdBy: 1, createdOn: new Date(), isReseller: false, isSwitchable: false, isReserved: false).save()

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Create default appUser for ARMS
     */
    public boolean createDefaultDataForArms(long companyId) {
        try {
            new AppUser(accountExpired: false, accountLocked: false, isPowerUser: true,
                    enabled: true, loginId: 'armsadmin@athena.com',
                    nextExpireDate: new Date() + 365,
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: 'ARMS Administrator', companyId: companyId,
                    createdBy: 1, createdOn: new Date(), isReseller: false, isSwitchable: false, isReserved: false).save()

            new AppUser(accountExpired: false, accountLocked: false, isPowerUser: true,
                    enabled: true, loginId: 'armssuper@athena.com',
                    nextExpireDate: new Date() + 365, isCompanyUser: false,
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: 'ARMS Development', companyId: companyId,
                    isConfigManager: true, createdBy: 1, createdOn: new Date(), isReseller: false, isSwitchable: false, isReserved: false).save()

            new AppUser(accountExpired: false, accountLocked: false,
                    enabled: true, loginId: 'remittance@athena.com',
                    nextExpireDate: new Date() + 365,
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: 'Remittance User', companyId: companyId,
                    createdBy: 1, createdOn: new Date(), isReseller: false, isSwitchable: false, isReserved: false).save()

            new AppUser(accountExpired: false, accountLocked: false,
                    enabled: true, loginId: 'branch_banani@athena.com',
                    nextExpireDate: new Date() + 365,
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: 'Banani Branch User', companyId: companyId,
                    createdBy: 1, createdOn: new Date(), isReseller: false, isSwitchable: false, isReserved: false).save()

            new AppUser(accountExpired: false, accountLocked: false,
                    enabled: true, loginId: 'exh@athena.com',
                    nextExpireDate: new Date() + 365,
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: 'ExchangeHouse User', companyId: companyId,
                    createdBy: 1, createdOn: new Date(), isReseller: false, isSwitchable: false, isReserved: false).save()

            new AppUser(accountExpired: false, accountLocked: false,
                    enabled: true, loginId: 'other_gulshan@athena.com',
                    nextExpireDate: new Date() + 365,
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: 'DBBL-Gulshan User', companyId: companyId,
                    createdBy: 1, createdOn: new Date(), isReseller: false, isSwitchable: false, isReserved: false).save()

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Create default AppUser objects for Document plugin
     */
    public void createDefaultDataForDocument(long companyId, long systemUserId) {
        try {
            Company company = (Company) companyService.read(companyId)
            new AppUser(accountExpired: false, accountLocked: false,
                    enabled: true, loginId: 'docadmin@' + company.code.toLowerCase() + '.com', isPowerUser: true,
                    nextExpireDate: new Date() + 365,
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: 'DOC Administrator', companyId: companyId,
                    createdBy: systemUserId, createdOn: new Date(), isReseller: false, isSwitchable: false, isReserved: false).save()

            new AppUser(accountExpired: false, accountLocked: false, isConfigManager: true,
                    enabled: true, loginId: 'docsuper@' + company.code.toLowerCase() + '.com',
                    nextExpireDate: new Date() + 365,
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: 'DOC Development', companyId: companyId,
                    createdBy: systemUserId, createdOn: new Date(), isReseller: false, isSwitchable: false, isReserved: false).save()

            new AppUser(accountExpired: false, accountLocked: false,
                    enabled: true, loginId: 'docmember@' + company.code.toLowerCase() + '.com',
                    nextExpireDate: new Date() + 365,
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: 'DOC Member', companyId: companyId,
                    createdBy: systemUserId, createdOn: new Date(), isReseller: false, isSwitchable: false, isReserved: false).save()
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Create default AppUser objects for Document plugin
     */
    public void createDefaultDataForElearning(long companyId, long systemUserId) {
        try {
            Company company = (Company) companyService.read(companyId)
            new AppUser(accountExpired: false, accountLocked: false,
                    enabled: true, loginId: 'eladmin@' + company.code.toLowerCase() + '.com', isPowerUser: true,
                    nextExpireDate: new Date() + 365, email: 'eladmin@' + company.code.toLowerCase() + '.com', cellNumber: '01710000001',
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: 'e-Learning Admin', companyId: companyId,
                    createdBy: systemUserId, createdOn: new Date(), isReseller: false, isSwitchable: true, isReserved: false).save()

            new AppUser(accountExpired: false, accountLocked: false, isConfigManager: true,
                    enabled: true, loginId: 'elsuper@' + company.code.toLowerCase() + '.com',
                    nextExpireDate: new Date() + 365, email: 'elsuper@' + company.code.toLowerCase() + '.com', cellNumber: '01710000002',
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: 'EL Development', companyId: companyId,
                    createdBy: systemUserId, createdOn: new Date(), isReseller: false, isSwitchable: false, isReserved: false).save()

            new AppUser(accountExpired: false, accountLocked: false,
                    enabled: true, loginId: 'teacher@' + company.code.toLowerCase() + '.com',
                    nextExpireDate: new Date() + 365, email: 'teacher@' + company.code.toLowerCase() + '.com', cellNumber: '01710000003',
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: 'Facilitator', companyId: companyId,
                    createdBy: systemUserId, createdOn: new Date(), isReseller: false, isSwitchable: true, isReserved: false).save()

            new AppUser(accountExpired: false, accountLocked: false,
                    enabled: true, loginId: 'coordinator@' + company.code.toLowerCase() + '.com',
                    nextExpireDate: new Date() + 365, email: 'coordinator@' + company.code.toLowerCase() + '.com', cellNumber: '01710000004',
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: 'Course manager', companyId: companyId,
                    createdBy: systemUserId, createdOn: new Date(), isReseller: false, isSwitchable: true, isReserved: false).save()

            new AppUser(accountExpired: false, accountLocked: false,
                    enabled: true, loginId: 'student@' + company.code.toLowerCase() + '.com',
                    nextExpireDate: new Date() + 365, email: 'student@' + company.code.toLowerCase() + '.com', cellNumber: '01710000005',
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: 'Participants', companyId: companyId,
                    createdBy: systemUserId, createdOn: new Date(), isReseller: false, isSwitchable: true, isReserved: false).save()

        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Create default AppUser objects for Document plugin
     */
    public void createDefaultDataForDataPipeLine(long companyId, long systemUserId) {
        try {
            new AppUser(accountExpired: false, accountLocked: false, isPowerUser: true,
                    enabled: true, loginId: 'dpladmin@athena.com',
                    nextExpireDate: new Date() + 365,
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: 'DPL Administrator', companyId: companyId,
                    createdBy: systemUserId, createdOn: new Date(), isReseller: false, isSwitchable: false, isReserved: false).save()

            new AppUser(accountExpired: false, accountLocked: false, isConfigManager: true,
                    enabled: true, loginId: 'dplsuper@athena.com',
                    nextExpireDate: new Date() + 365,
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: ' DPL Development', companyId: companyId,
                    createdBy: systemUserId, createdOn: new Date(), isReseller: false, isSwitchable: false, isReserved: false).save()

        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Create default AppUser objects for project track plugin
     */
    public void createDefaultDataForProjectTrack(long companyId) {
        try {
            new AppUser(accountExpired: false, accountLocked: false,
                    enabled: true, loginId: 'ptadmin@athena.com',
                    nextExpireDate: new Date() + 365, isPowerUser: true,
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: 'Pt Admin', companyId: companyId,
                    createdBy: 1, createdOn: new Date(), isReseller: false, isSwitchable: false, isReserved: false).save()

            new AppUser(accountExpired: false, accountLocked: false,
                    enabled: true, loginId: 'ptsuper@athena.com',
                    nextExpireDate: new Date() + 365, isConfigManager: true,
                    password: springSecurityService.encodePassword('athena@123'),
                    passwordExpired: false, username: 'Pt Dev Admin', companyId: companyId,
                    createdBy: 1, createdOn: new Date(), isReseller: false, isSwitchable: false, isReserved: false).save()

        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public void createDefaultSchema() {
        String sqlIndex = "create unique index app_user_login_id_company_id_idx on app_user(lower(login_id),company_id);"
        executeSql(sqlIndex)
        String sqlEmailIndex = "create unique index app_user_email_company_id_idx on app_user(lower(email),company_id);"
        executeSql(sqlEmailIndex)
    }

    private static final String INSERT_TEST_DATA = """
        INSERT INTO app_user(id, version, account_expired, account_locked, company_id, enabled, login_id, next_expire_date,
            password, password_expired, has_signature, username, employee_id, is_company_user, is_activated_by_mail,
            is_power_user, is_config_manager, is_system_user, is_disable_password_expiration, created_on, created_by,
            updated_by, is_reseller, is_switchable, is_reserved)
        VALUES(:id, :version, :accountExpired, :accountLocked, :companyId, :enabled, :loginId, :nextExpireDate,
            :password, :passwordExpired, :hasSignature, :username, :employeeId, :isCompanyUser, :isActivatedByMail,
            :isPowerUser, :isConfigManager, :isSystemUser, :isDisablePasswordExpiration, :createdOn, :createdBy,
            :updatedBy, :isReseller, :isSwitchable, :isReserved)
    """

    /**
     * Create default AppUser objects for budget plugin
     */
    public void createTestDataForBudget(long companyId, long userId) {
        AppUser director = new AppUser(enabled: true, loginId: 'director@athena.com', nextExpireDate: new Date() + 365,
                password: springSecurityService.encodePassword('athena@123'), username: 'Director', companyId: companyId,
                createdBy: userId, createdOn: new Date(), isSwitchable: false, isReserved: false)
        AppUser projectDirector = new AppUser(enabled: true, loginId: 'pd@athena.com', nextExpireDate: new Date() + 365,
                password: springSecurityService.encodePassword('athena@123'), username: 'Project Director', companyId: companyId,
                createdBy: userId, createdOn: new Date(), isSwitchable: false, isReserved: false)
        insertTestData(director)
        insertTestData(projectDirector)
    }

    /**
     * Create default AppUser objects for accounting plugin
     */
    public void createTestDataForAccounting(long companyId, long userId) {
        AppUser accountant = new AppUser(enabled: true, loginId: 'accountant@athena.com', nextExpireDate: new Date() + 365,
                password: springSecurityService.encodePassword('athena@123'), username: 'Accountant', companyId: companyId,
                createdBy: userId, createdOn: new Date(), isSwitchable: false, isReserved: false)
        AppUser cfo = new AppUser(enabled: true, loginId: 'cfo@athena.com', nextExpireDate: new Date() + 365,
                password: springSecurityService.encodePassword('athena@123'), username: 'CFO', companyId: companyId,
                createdBy: userId, createdOn: new Date(), isPowerUser: true, isSwitchable: false, isReserved: false)
        insertTestData(accountant)
        insertTestData(cfo)
    }

    /**
     * Create default AppUser objects for project track plugin
     */
    public void createTestDataForPt(long companyId, long userId) {
        AppUser engineer = new AppUser(enabled: true, loginId: 'engineer@athena.com', nextExpireDate: new Date() + 365,
                password: springSecurityService.encodePassword('athena@123'), username: 'Engineer', companyId: companyId,
                createdBy: userId, createdOn: new Date(), isSwitchable: false, isReserved: false)
        AppUser sqa = new AppUser(enabled: true, loginId: 'sqa@athena.com', nextExpireDate: new Date() + 365,
                password: springSecurityService.encodePassword('athena@123'), username: 'SQA', companyId: companyId,
                createdBy: userId, createdOn: new Date(), isSwitchable: false, isReserved: false)
        insertTestData(engineer)
        insertTestData(sqa)
    }

    /**
     * Create default AppUser objects for inventory plugin
     */
    public void createTestDataForInventory(long companyId, long userId) {
        AppUser auditor = new AppUser(enabled: true, loginId: 'auditor@athena.com', nextExpireDate: new Date() + 365,
                password: springSecurityService.encodePassword('athena@123'), username: 'Inventory Auditor', companyId: companyId,
                createdBy: userId, createdOn: new Date(), isSwitchable: false, isReserved: false)
        AppUser projectManager = new AppUser(enabled: true, loginId: 'pm@athena.com', nextExpireDate: new Date() + 365,
                password: springSecurityService.encodePassword('athena@123'), username: 'Project Manager', companyId: companyId,
                createdBy: userId, createdOn: new Date(), isSwitchable: false, isReserved: false)
        insertTestData(auditor)
        insertTestData(projectManager)
    }

    /**
     * insert test data
     * @param appUser - object of AppUser
     */
    private void insertTestData(AppUser appUser) {
        Map queryParams = [
                id                         : testDataModelService.getNextIdForTestData(),
                version                    : appUser.version,
                accountExpired             : appUser.accountExpired,
                accountLocked              : appUser.accountLocked,
                companyId                  : appUser.companyId,
                enabled                    : appUser.enabled,
                loginId                    : appUser.loginId,
                nextExpireDate             : DateUtility.getSqlDateWithSeconds(appUser.nextExpireDate),
                password                   : appUser.password,
                passwordExpired            : appUser.passwordExpired,
                hasSignature               : appUser.hasSignature,
                username                   : appUser.username,
                employeeId                 : appUser.employeeId,
                isCompanyUser              : appUser.isCompanyUser,
                isActivatedByMail          : appUser.isActivatedByMail,
                isPowerUser                : appUser.isPowerUser,
                isConfigManager            : appUser.isConfigManager,
                isSystemUser               : appUser.isSystemUser,
                isDisablePasswordExpiration: appUser.isDisablePasswordExpiration,
                createdOn                  : DateUtility.getSqlDateWithSeconds(appUser.createdOn),
                createdBy                  : appUser.createdBy,
                updatedBy                  : appUser.updatedBy,
                isReseller                 : appUser.isReseller,
                isSwitchable               : appUser.isSwitchable,
                isReserved                 : appUser.isReserved
        ]
        executeInsertSql(INSERT_TEST_DATA, queryParams)
    }

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }

    /**
     * SQL to delete test data from database
     * @param userIds - list of user ids
     */
    public void deleteTestData(List<Long> userIds) {
        if (userIds.size() > 0) {
            String lstUserIds = super.buildCommaSeparatedStringOfIds(userIds)
            String query = """ DELETE FROM app_user WHERE id IN (${lstUserIds}) """
            executeUpdateSql(query)
        }
    }
}
