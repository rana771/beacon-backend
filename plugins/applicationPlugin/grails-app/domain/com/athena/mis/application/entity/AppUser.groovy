package com.athena.mis.application.entity

/**
 * <p>
 * <strong>Module:</strong> Application </br>
 * <strong>Purpose:</strong> AppUser is user of application.
 * AppUser has association with few other domains as listed below.
 * </p>
 *
 * <p><strong>Foreign Reference:</strong> Other domain, which has foreign key reference of AppUser:</p>
 * <ul>
 *     <strong>Application Plugin</strong>
 *     <li>{@link com.athena.mis.application.entity.AppUserEntity#appUserId}</li>
 *     <li>{@link com.athena.mis.application.entity.AppAttachment#entityId}</li>
 *
 *     <strong>Procurement Plugin</strong>
 *     <li>{@link com.athena.mis.procurement.entity.ProcPurchaseRequest#approvedByDirectorId}</li>
 *     <li>{@link com.athena.mis.procurement.entity.ProcPurchaseRequest#approvedByProjectDirectorId}</li>
 *     <li>{@link com.athena.mis.procurement.entity.ProcPurchaseOrder#approvedByDirectorId}</li>
 *     <li>{@link com.athena.mis.procurement.entity.ProcPurchaseOrder#approvedByProjectDirectorId}</li>
 *     <li>{@link com.athena.mis.procurement.entity.ProcCancelledPO#approvedByDirectorId}</li>
 *     <li>{@link com.athena.mis.procurement.entity.ProcCancelledPO#approvedByProjectDirectorId}</li>
 *     <li>{@link com.athena.mis.procurement.entity.ProcCancelledPO#cancelledBy}</li>
 *
 *     <strong>Inventory Plugin</strong>
 *     <li>{@link com.athena.mis.inventory.entity.InvInventoryTransactionDetails#approvedBy}</li>
 *     <li>{@link com.athena.mis.inventory.entity.InvInventoryTransactionDetails#invoiceAcknowledgedBy}</li>
 *
 *     <strong>Accounting Plugin</strong>
 *     <li>{@link com.athena.mis.accounting.entity.AccCancelledVoucherDetails#cancelledBy}</li>
 *     <li>{@link com.athena.mis.accounting.entity.AccIouSlip#approvedBy}</li>
 *     <li>{@link com.athena.mis.accounting.entity.AccVoucher#postedBy}</li>
 *
 *     <strong>Fixed Asset Plugin</strong>
 *     <strong>QS Plugin</strong>
 *     <strong>Project Track Plugin</strong>
 *     <li>{@link com.athena.mis.projecttrack.entity.PtBacklog#ownerId}</li>
 *     <li>{@link com.athena.mis.projecttrack.entity.PtBacklog#acceptedBy}</li>
 *
 *     <strong>Document Plugin</strong>
 *     <li>{@link com.athena.mis.document.entity.DocCategoryUserMapping#userId}</li>
 *     <li>{@link com.athena.mis.document.entity.DocSubCategoryUserMapping#userId}</li>
 *     <li>{@link com.athena.mis.document.entity.DocInvitedMembers#resendBy}</li>
 *     <li>{@link com.athena.mis.document.entity.DocMemberJoinRequest#approvedBy}</li>
 *
 *     <strong>ExchangeHouse Plugin</strong>
 *     <li>{@link com.athena.mis.exchangehouse.entity.ExhBeneficiary#approvedBy}</li>
 *     <li>{@link com.athena.mis.exchangehouse.entity.ExhBeneficiary#exceptionConfirmedBy}</li>
 *     <li>{@link com.athena.mis.exchangehouse.entity.ExhCustomer#userId}</li>
 *     <li>{@link com.athena.mis.exchangehouse.entity.ExhCustomer#exceptionConfirmedBy}</li>
 *     <li>{@link com.athena.mis.exchangehouse.entity.ExhCustomerTrace#exceptionConfirmedBy}</li>
 *     <li>{@link com.athena.mis.exchangehouse.entity.ExhTask#userId}</li>
 *     <li>{@link com.athena.mis.exchangehouse.entity.ExhTask#approvedBy}</li>
 *     <li>{@link com.athena.mis.exchangehouse.entity.ExhTask#exceptionConfirmedBy}</li>
 *     <li>{@link com.athena.mis.exchangehouse.entity.ExhTaskTrace#userId}</li>
 *     <li>{@link com.athena.mis.exchangehouse.entity.ExhTaskTrace#approvedBy}</li>
 *     <li>{@link com.athena.mis.exchangehouse.entity.ExhTaskTrace#exceptionConfirmedBy}</li>
 *
 *     <strong>ARMS Plugin</strong>
 *     <li>{@link com.athena.mis.arms.entity.RmsTransactionDay#openedBy}</li>
 *     <li>{@link com.athena.mis.arms.entity.RmsTransactionDay#closedBy}</li>
 *
 *     <strong>Each domain has foreign key reference of AppUser as createdBy and updatedBy EXCEPT followings</strong>
 *
 *     <li>{@link com.athena.mis.application.entity.AppMail (createdBy)}</li>
 *     <li>{@link AppSms (createdBy)}</li>
 *     <li>{@link com.athena.mis.application.entity.SysConfiguration (createdBy)}</li>
 *     <li>{@link com.athena.mis.application.entity.AppTheme (createdBy)}</li>
 *     <li>{@link com.athena.mis.application.entity.AppGroupEntity (createdBy and updatedBy)}</li>
 *     <li>{@link com.athena.mis.application.entity.AppUserEntity (createdBy and updatedBy)}</li>
 *     <li>{@link com.athena.mis.application.entity.RequestMap (createdBy and updatedBy)}</li>
 *     <li>{@link com.athena.mis.application.entity.ReservedSystemEntity (createdBy and updatedBy)}</li>
 *     <li>{@link com.athena.mis.application.entity.RoleFeatureMapping (createdBy and updatedBy)}</li>
 *     <li>{@link ReservedRole ( createdBy and updatedBy )}</li>
 *     <li>{@link com.athena.mis.application.entity.SystemEntityType ( createdBy and updatedBy )}</li>
 *     <li>{@link com.athena.mis.application.entity.UserRole ( createdBy and updatedBy )}</li>
 *
 *     <li>{@link com.athena.mis.budget.entity.BudgProjectBudgetScope (createdBy and updatedBy)}</li>
 *
 *     <li>{@link com.athena.mis.accounting.entity.AccVoucherDetails (updatedBy)}</li>
 *     <li>{@link com.athena.mis.accounting.entity.AccCancelledVoucherDetails (updatedBy)}</li>
 *     <li>{@link com.athena.mis.accounting.entity.AccCType (createdBy)}</li>
 *
 *     <li>{@link com.athena.mis.document.entity.DocInvitedMembers (updatedBy)}</li>
 *     <li>{@link com.athena.mis.document.entity.DocInvitedMembersCategory (createdBy and updatedBy)}</li>
 *     <li>{@link com.athena.mis.document.entity.DocInvitedMembersCategory (createdBy and updatedBy)}</li>
 *     <li>{@link com.athena.mis.document.entity.DocMemberJoinRequest (createdBy and updatedBy)}</li>
 *
 *     <li>{@link com.athena.mis.exchangehouse.entity.ExhCustomer (createdBy and updatedBy)}</li>
 *     <li>{@link com.athena.mis.exchangehouse.entity.ExhCustomerTrace (updatedBy)}</li>
 *     <li>{@link com.athena.mis.exchangehouse.entity.ExhPaymentResponseNotification (createdBy and updatedBy)}</li>
 *     <li>{@link com.athena.mis.exchangehouse.entity.ExhSanctionHmTreasury (createdBy and updatedBy)}</li>
 *     <li>{@link com.athena.mis.exchangehouse.entity.ExhSanctionOfacAdd (createdBy and updatedBy)}</li>
 *     <li>{@link com.athena.mis.exchangehouse.entity.ExhSanctionOfacAlt (createdBy and updatedBy)}</li>
 *     <li>{@link com.athena.mis.exchangehouse.entity.ExhSanctionOfacSdn (createdBy and updatedBy)}</li>
 *     <li>{@link com.athena.mis.exchangehouse.entity.ExhTask (createdBy and updatedBy)}</li>
 *     <li>{@link com.athena.mis.exchangehouse.entity.ExhTaskTrace (createdBy and updatedBy)}</li>
 *
 *     <li>{@link com.athena.mis.sarb.entity.SarbCustomerDetails (createdBy)}</li>
 *     <li>{@link com.athena.mis.sarb.entity.SarbCustomerDetailsTrace (createdBy and updatedBy)}</li>
 *     <li>{@link com.athena.mis.sarb.entity.SarbProvince (createdBy and updatedBy)}</li>
 *     <li>{@link com.athena.mis.sarb.entity.SarbTaskDetails (updatedBy)}</li>
 *
 *     <li>{@link com.athena.mis.arms.entity.RmsCommissionDetails (createdBy and updatedBy)}</li>
 *     <li>{@link com.athena.mis.arms.entity.RmsExchangeHouse (createdBy and updatedBy)}</li>
 *     <li>{@link com.athena.mis.arms.entity.RmsProcessInstrumentMapping (createdBy and updatedBy)}</li>
 *     <li>{@link com.athena.mis.arms.entity.RmsPurchaseInstrumentMapping (createdBy and updatedBy)}</li>
 *     <li>{@link com.athena.mis.arms.entity.RmsTask (createdBy and updatedBy)}</li>
 *     <li>{@link com.athena.mis.arms.entity.RmsTaskList (updatedBy)}</li>
 *     <li>{@link com.athena.mis.arms.entity.RmsTaskTrace (updatedBy)}</li>
 *     <li>{@link com.athena.mis.arms.entity.RmsTransactionDay (createdBy and updatedBy)}</li>
 *
 *     // todo write all foreign key references for all domain in all plugins
 * </ul>
 *
 * <p><strong>Local Reference:</strong> Has-a relationship with other domains:</p>
 * <ul>
 *     <li>{@link com.athena.mis.application.entity.AppUser#id as createdBy}</li>
 *     <li>{@link com.athena.mis.application.entity.AppUser#id as updatedBy}</li>
 *     <li>{@link com.athena.mis.application.entity.Company#id as companyId}</li>
 *     <li>{@link com.athena.mis.application.entity.AppEmployee#id as employeeId}</li>
 * </ul>
 *
 * <p><strong>Cross Reference:</strong> many-to-many reference with AppUser:</p>
 * <ul>
 *     <li>AppUser VS {@link com.athena.mis.application.entity.Project} </br>
 *      AppUser has many Project in {@link com.athena.mis.application.entity.AppUserEntity#entityId} </br>
 *      Project has many AppUser in {@link com.athena.mis.application.entity.AppUserEntity#appUserId} </br>
 *     </li>
 *
 *     <li>AppUser VS {@link com.athena.mis.projecttrack.entity.PtProject} </br>
 *      AppUser has many PtProject in {@link com.athena.mis.application.entity.AppUserEntity#entityId} </br>
 *      PtProject has many AppUser in {@link com.athena.mis.application.entity.AppUserEntity#appUserId} </br>
 *     </li>
 *
 *     <li>AppUser VS {@link com.athena.mis.inventory.entity.InvInventory} </br>
 *      AppUser has many InvInventory in {@link com.athena.mis.application.entity.AppUserEntity#entityId} </br>
 *      InvInventory has many AppUser in {@link com.athena.mis.application.entity.AppUserEntity#appUserId} </br>
 *     </li>
 *
 *     <li>AppUser VS {@link com.athena.mis.application.entity.AppGroup} </br>
 *      AppUser has many AppGroup in {@link com.athena.mis.application.entity.AppUserEntity#entityId} </br>
 *      AppGroup has many AppUser in {@link com.athena.mis.application.entity.AppUserEntity#appUserId} </br>
 *     </li>
 *
 *     <li>AppUser VS {@link AppBankBranch} </br>
 *      AppUser has many BankBranch in {@link com.athena.mis.application.entity.AppUserEntity#entityId} </br>
 *      BankBranch has many AppUser in {@link com.athena.mis.application.entity.AppUserEntity#appUserId} </br>
 *     </li>
 *
 *     <li>AppUser VS {@link com.athena.mis.exchangehouse.entity.ExhAgent} </br>
 *      AppUser has many ExhAgent in {@link com.athena.mis.application.entity.AppUserEntity#entityId} </br>
 *      ExhAgent has many AppUser in {@link com.athena.mis.application.entity.AppUserEntity#appUserId} </br>
 *     </li>
 *
 *     <li>AppUser VS {@link com.athena.mis.arms.entity.RmsExchangeHouse} </br>
 *      AppUser has many RmsExchangeHouse in {@link com.athena.mis.application.entity.AppUserEntity#entityId} </br>
 *      RmsExchangeHouse has many AppUser in {@link com.athena.mis.application.entity.AppUserEntity#appUserId} </br>
 *     </li>
 * </ul>
 *
 */
class AppUser implements Serializable {

    public static final String DEFAULT_SORT_ORDER = "username"

    long id                     // primary key (Auto generated by its own sequence)
    long version                 // entity version in the persistence layer
    String loginId              // user login name (unique within a company)
    String username             // user name of AppUser
    String password             // password of AppUser
    boolean enabled             // flag to enable AppUser
    boolean hasSignature        // flag to determine if user has any signature image
    long companyId              // Company.id
    boolean accountExpired      // flag to determine is account expired or not
    boolean accountLocked       // flag to determine is account locked or not
    boolean passwordExpired     // flag to determine is user password expired or not
    boolean isCompanyUser       // flag to determine if AppUser is company user or not
    Date nextExpireDate         // DateTime till password is valid
    String cellNumber           // AppUser cell Number
    String ipAddress            // AppUser IP address
    long employeeId             // AppEmployee.id
    String activationLink       // encoded link id based on loginId
    boolean isActivatedByMail   // flag to active user by mail
    String passwordResetLink    // encoded link id based on loginId + current time
    Date passwordResetValidity  // valid date time to reset password (valid till 24 hrs after sending request)
    String passwordResetCode    // security code to reset password (sent by mail)
    boolean isPowerUser         // if true then Admin user
    boolean isConfigManager     // if true then development user
    boolean isSystemUser        // if true then system user (allowed to do system manipulation like write and execute AppShellScript)
    boolean isDisablePasswordExpiration      // if true then AppUser password will not expire
    long createdBy              // AppUser.id
    Date createdOn              // Object creation DateTime
    long updatedBy              // AppUser.id
    Date updatedOn              // Object Updated DateTime
    boolean isReseller          // true only for reseller user, otherwise false
    long genderId               // SystemEntity.id
    String email                // mail id of user
    boolean isSwitchable        // flag to determine whether user is switchable or not
    boolean isReserved          // flag to determine whether user is deletable or not

    static constraints = {
        cellNumber(nullable: true)
        ipAddress(nullable: true)
        activationLink(nullable: true)
        passwordResetLink(nullable: true)
        passwordResetValidity(nullable: true)
        passwordResetCode(nullable: true)
        createdBy(nullable: false)
        createdOn(nullable: false)
        updatedBy(nullable: false)
        updatedOn(nullable: true)
        genderId(nullable: true)
        email(nullable: true)
    }

    static mapping = {
        id generator: 'sequence', params: [sequence: 'app_user_id_seq']
        loginId index: 'app_user_login_id_idx'
        companyId index: 'app_user_company_id_idx'
        employeeId index: 'app_user_employee_id_idx'
        username index: 'app_user_username_idx'
        createdBy index: 'app_user_created_by_idx'
        updatedBy index: 'app_user_updated_by_idx'

        // unique index on "login_id" using AppUserService.createDefaultSchema()
        // <domain_name><property_name_1>idx
        // todo activationLink index
    }

    Set<Role> getAuthorities() {
        List<Long> lstRoleIds = UserRole.findAllByUserId(this.id, [readOnly: true]).collect { it.roleId }
        Role.findAllByIdInList(lstRoleIds)
    }

    public String toString() {
        return this.username
    }
}

