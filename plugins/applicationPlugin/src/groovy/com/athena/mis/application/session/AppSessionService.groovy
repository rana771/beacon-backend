package com.athena.mis.application.session

import com.athena.mis.application.entity.*
import com.athena.mis.application.service.*
import com.athena.mis.integration.arms.ArmsPluginConnector
import com.athena.mis.integration.document.DocumentPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.integration.inventory.InvPluginConnector
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.servlet.http.HttpSession

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "session")
class AppSessionService implements Serializable {

    @Autowired(required = false)
    UserRoleService userRoleService
    @Autowired(required = false)
    AppUserEntityService appUserEntityService
    @Autowired(required = false)
    ProjectService projectService
    @Autowired(required = false)
    AppBankBranchService bankBranchService

    @Autowired(required = false)
    InvPluginConnector invInventoryImplService
    @Autowired(required = false)
    ArmsPluginConnector armsImplService
    @Autowired(required = false)
    ExchangeHousePluginConnector exchangeHouseImplService
    @Autowired(required = false)
    DocumentPluginConnector documentImpService

    private AppUser appUser = null

    private List<Long> lstRoleIds = null        // List of role ids that is mapped with loggedIn user
    private List<Long> lstProjectIds = null     // List of project ids that is mapped with loggedIn user

    private List<Role> lstRoles = null          // List of roles that is mapped with loggedIn user
    private List<Project> lstProjects = null    // List of projects that is mapped with loggedIn user

    private List<AppBankBranch> lstBankBranches = []       // List of BankBranches that is mapped with loggedIn user
    private List<Long> lstBankBranchIds = []            // List of BankBranch Ids that is mapped with loggedIn user
    private AppBankBranch userBankBranch = null            // Object of BankBranch that is mapped with loggedIn user
    private Long userBankBranchId = null                // id of BankBranch that is mapped with loggedIn user

    private HttpSession httpSession                     // user session

    // Initialize the values
    @Transactional(readOnly = true)
    public void init(AppUser user, HttpSession session) {
        appUser = user

        lstRoles = userRoleService.getRolesByUser(user.id)
        lstRoleIds = lstRoles*.id

        lstProjects = listUserProjects(user.id)
        lstProjectIds = lstProjects*.id

        lstBankBranches = listUserBankBranch(user.id)
        lstBankBranchIds = lstBankBranches*.id

        httpSession = session

        if (invInventoryImplService) invInventoryImplService.initSession()
        if (armsImplService) armsImplService.initSession()
        if (exchangeHouseImplService) exchangeHouseImplService.initSession()
    }

    // invalidate user session
    public boolean invalidateSession() {
        httpSession.invalidate()
        return true;
    }

    // get use session
    public HttpSession getSession() {
        return httpSession;
    }

    // get list of role ids mapped with user
    public List<Long> getUserRoleIds() {
        return lstRoleIds
    }

    // get list of roles mapped with user
    public List<Role> getUserRoles() {
        return lstRoles
    }

    // get app user
    public AppUser getAppUser() {
        return appUser
    }

    // get company id
    public long getCompanyId() {
        return appUser.companyId
    }

    // check user role
    public boolean hasRole(long roleTypeId) {
        if (!lstRoles) return false
        for (int i = 0; i < lstRoles.size(); i++) {
            Role role = (Role) lstRoles[i]
            if (role.roleTypeId == roleTypeId) {
                return true
            }
        }
        return false
    }

    private static final String COMA = ","

    // check user role
    public boolean hasRole(String roleIds) {
        List<String> strRoleIds = roleIds.split(COMA)
        List<Long> lstIds = []
        for (int i = 0; i < strRoleIds.size(); i++) {
            lstIds << Long.parseLong(strRoleIds[i].trim())
        }
        for (int i = 0; i < lstIds.size(); i++) {
            for (int j = 0; j < lstRoleIds.size(); j++) {
                if (lstIds[i] == lstRoleIds[j]) {
                    return true
                }
            }
        }
        return false
    }

    // get list of project ids mapped with user
    public List<Long> getUserProjectIds() {
        return lstProjectIds
    }

    // get list of projects mapped with user
    public List<Project> getUserProjects() {
        return lstProjects
    }

    // check user project
    public boolean isAccessible(long projectId) {
        for (int i = 0; i < lstProjectIds.size(); i++) {
            long id = (long) lstProjectIds[i]
            if (id == projectId) {
                return true
            }
        }
        return false
    }

    //get list of bankBranches mapped with user
    public List<AppBankBranch> getUserBankBranches() {
        return lstBankBranches
    }

    // get list of bank branch ids mapped with user
    public List<Long> getUserBankBranchIds() {
        return lstBankBranchIds
    }

    // get object of bank branch mapped with user
    public AppBankBranch getUserBankBranch() {
        if (userBankBranch) return userBankBranch
        userBankBranch = null
        if (lstBankBranches.size() > 0) {
            userBankBranch = lstBankBranches[0]
        }
        return userBankBranch
    }

    // Retrieve the bankBranch ID which is associated with user
    public long getUserBankBranchId() {
        if (userBankBranchId) return userBankBranchId.longValue()
        userBankBranchId = new Long(0L)
        if (lstBankBranches.size() > 0) {
            userBankBranchId = lstBankBranchIds[0]
        }
        return userBankBranchId.longValue()
    }

    // instant reset Role
    public void resetRole() {
        long userId = appUser.id
        lstRoles = userRoleService.getRolesByUser(userId)
        lstRoleIds = lstRoles*.id
    }

    /**
     * collect all entity ids from list
     * @param lstAppUserEntity
     * @return
     */
    public List<Long> getEntityIds(List<AppUserEntity> lstAppUserEntity) {
        List<Long> lstEntityIds = []
        lstAppUserEntity.each {
            lstEntityIds << it.entityId.longValue()
        }
        return lstEntityIds
    }

    /**
     * find all projects mapped with user
     * @param userId - logged user id
     * @return - list of appGroup
     */
    private List<Project> listUserProjects(long userId) {
        List<AppUserEntity> lstUserEntityForProject = appUserEntityService.findAllByAppUserIdAndEntityTypeId(userId, AppSystemEntityCacheService.SYS_ENTITY_USER_ENTITY_PROJECT)
        List<Long> lstProjectIds = getEntityIds(lstUserEntityForProject)
        return projectService.findAllByIdInList(lstProjectIds)
    }

    /**
     * find all bank branch mapped with user
     * @param user - logged user
     * @return - list of BankBranch
     */
    private List<AppBankBranch> listUserBankBranch(long userId) {
        List<AppUserEntity> lstUserEntityForBankBranch = appUserEntityService.findAllByAppUserIdAndEntityTypeId(userId, AppSystemEntityCacheService.SYS_ENTITY_USER_ENTITY_BANK_BRANCH)
        List<Long> lstBranchIds = getEntityIds(lstUserEntityForBankBranch)
        return bankBranchService.findAllByIdInList(lstBranchIds)
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }
}
