package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.application.entity.AppMail
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Role
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.utility.DateUtility
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger

// AppMailService is used to send auto mail in different events

class AppMailService extends BaseDomainService {

    private Logger log = Logger.getLogger(getClass())

    TestDataModelService testDataModelService
    AppUserService appUserService
    RoleService roleService
    AppSystemEntityCacheService appSystemEntityCacheService

    @Override
    public void init() {
        domainClass = AppMail.class
    }

    @Override
    public void createDefaultSchema() {}

    /**
     * @return -list of appMail object
     */
    @Override
    public List<AppMail> list() {
        List<AppMail> mailList = AppMail.list([max: resultPerPage, start: start, readOnly: true])
        return mailList
    }

    public int count() {
        int count = AppMail.count()
        return count
    }

    public AppMail get(long id) {
        AppMail appMail = AppMail.get(id)
        return appMail
    }

    private static final String DELETE_ALL = """
        DELETE FROM app_mail WHERE company_id = :companyId
    """

    /**
     * Delete all appMail by companyId
     * @param companyId - id of company
     */
    public void deleteAllByCompanyId(long companyId) {
        Map queryParams = [
                companyId: companyId
        ]
        executeUpdateSql(DELETE_ALL, queryParams)
    }

    /**
     * get mail address of roles with appUser entity mapping
     * @param companyId - company id
     * @param roleIds - app mail coma separated role ids
     * @param entityIds - list of entity ids / null for all users in role
     * @param entityTypeReservedId - app user entity type reserved id / null for all users in role
     * @return
     */
    public String getAllMailAddresses(long companyId, String roleIds, List<Long> entityIds, Long entityTypeReservedId) {
        SystemEntity entityTypeObj = null
        String filterAppUserEntityMapping = EMPTY_SPACE
        //email addresses of all user's in role
        if (entityIds && entityTypeReservedId && (entityIds.size() > 0) && (entityTypeReservedId != 0)) {
            String entityIdsStr = super.buildCommaSeparatedStringOfIds(entityIds)
            entityTypeObj = appSystemEntityCacheService.readByReservedId(entityTypeReservedId, appSystemEntityCacheService.SYS_ENTITY_TYPE_USER_ENTITY, companyId)
            filterAppUserEntityMapping = """
            RIGHT JOIN app_user_entity au_en ON au_en.app_user_id = au.id
            AND au_en.entity_type_id = :entityTypeId AND au_en.entity_id IN (${entityIdsStr})
            """
        }
        String query = """
            SELECT DISTINCT(login_id)
            FROM app_user au
            ${filterAppUserEntityMapping}
            WHERE au.company_id = :companyId
            AND au.enabled = true
            AND au.id IN
            (SELECT ur.user_id FROM user_role ur
            WHERE ur.role_id IN (${roleIds}))
        """

        Map queryParams = [
                entityTypeId: entityTypeObj ? entityTypeObj.id : 0L,
                companyId   : companyId
        ]
        List<GroovyRowResult> result = executeSelectSql(query, queryParams)
        String comaSeparatedMail = EMPTY_SPACE
        if (result.size() > 0) {
            comaSeparatedMail = result[0][0].toString()
            for (int i = 1; i < result.size(); i++) {
                comaSeparatedMail += (COMA + result[i][0].toString())
            }
        }
        return comaSeparatedMail
    }

    public AppMail findByTransactionCodeAndCompanyIdAndIsActive(String transactionCode, long companyId, Boolean boolValue) {
        AppMail appMail = AppMail.findByTransactionCodeAndCompanyIdAndIsActive(transactionCode, companyId, boolValue, [readOnly: true])
        return appMail
    }

    /**
     * create default mail data for application
     * @param companyId
     */
    public boolean createDefaultDataForApplication(long companyId, long sysUserId) {
        try {
            Map queryParams = [
                    companyId: companyId,
                    createdBy: sysUserId,
                    createdOn: DateUtility.getSqlDateWithSeconds(new Date())
            ]
            executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send, is_required_recipients, recipients, controller_name, action_name, updated_by, has_send, is_announcement,
            created_by, created_on)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, '<div>
            <p>
                Dear \${userName}, <br/>
                Your Login ID : \${loginId}
            </p>
            <p>
                To reset your password please click the link below (or copy/paste into your web browser):<br/>
                <a target="_blank" href="\${link}">\${link}</a>
            </p>
            <p>
                For security reason, you will be asked for a security code.<br/>
                Your security code is <strong>\${securityCode}</strong>
            </p>
            <p>
                <strong>Please Note, you must reset your password within 24 hours of your request.</strong>
            </p>
            <p>
                Regards,<br/>
                \${company}
            </p>
            <i>This is an auto-generated email, which does not need reply.<br/></i>
            </div>', :companyId, true, 'html', 'Your request for password reset', 'SendMailForPasswordResetActionService', null, 1, false, false, false, null, null, null, 0, false, false, :createdBy, :createdOn);
        """, queryParams)

            executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send, is_required_recipients, recipients, controller_name, action_name, updated_by, has_send, is_announcement,
            created_by, created_on)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, '<div>
                <p>
                    Dear Concerned, <br/>
                         This is a test purpose mail .If you receive this mail,
                         it means our mail sending functionality is ok.
                </p>

                <i>This is an auto-generated email, which does not need reply.<br/>
           </div>', :companyId, true, 'html', 'Test AppMail :', 'TestAppMailActionService', null, 1, false, true, true, 'info@athena.com.bd', 'appMail','testAppMail', 0, false, false, :createdBy, :createdOn);
        """, queryParams)

            executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send, is_required_recipients, recipients, controller_name, action_name, updated_by, has_send, is_announcement,
            created_by, created_on)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, '<div>
            <p>
                Dear Concerned, <br/>
                This mail reports a run time exception error. <br/><br/>
                        <b>Comments : </b> \${comments} <br/><br/>
                        <b>Error Message : </b> \${message} <br/><br/>
                        <b>Class Signature : </b> \${classSignature} <br/>
                        <b>Company : </b> \${companyName} <br/>
                        <b>Login User : </b> \${userName} <br/>
            </p>
            <i>Note: This is an auto-generated email, which does not need to reply.<br/></i>
            </div>', :companyId, true, 'html', 'Error Report(QA) :', 'SendErrorMailActionService', null, 1, false, false, true, 'info@athena.com.bd', null, null, 0, false, false, :createdBy, :createdOn);
        """, queryParams)

            String mailBody = """<div>
                <p>
                    Dear Mr/Ms \${name}, <br/>
                    Thank you for completing your registration. Please activate your account.<br/>
                    To activate your user account please click the link below: <br/>
                    <a target="_blank" href="\${link}">\${link}</a>
                </p>

                If you have already activated your account, please ignore this mail. <br/>
                <i>Note: This is an auto-generated email, which does not need reply.<br/></i>
            </div>"""
            executeInsertSql(
                    """
            INSERT INTO "app_mail"
            ("id", "version", "body", "company_id", "is_active", "is_required_role_ids", "mime_type", "plugin_id", "subject", "transaction_code", "is_manual_send", "controller_name", "action_name", "is_required_recipients", "recipients", "updated_by", "has_send", "is_announcement",
            "created_by", "created_on")
            VALUES (NEXTVAL('app_mail_id_seq'), 0, '${mailBody}', '${companyId}',
            't', 'FALSE', 'html', '1', 'Activate your user account', 'RegisterAppUserActionService','FALSE', null, null, 'f', null, 0, false, false, :createdBy, :createdOn);
            """, queryParams)

            executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send,
            controller_name, action_name, is_required_recipients, recipients, updated_on, updated_by, recipients_cc, display_name, has_send, is_announcement, created_by, created_on)
            VALUES (NEXTVAL('app_mail_id_seq'), 0,
            '<div>
            <p>
                Dear Concerned, <br/>
                This is a notification mail about Maintenance Script Scheduler. <br/><br/>
                        <b>Server Instance : </b> \${serverInstance} <br/><br/>
                        <b>Script : </b> \${script} <br/><br/>
                        <b>Execution Count : </b> \${executionCount} <br/>
                        <b>Executed On : </b> \${executedOn} <br/><br/>
                        <b>Message : </b> \${message} <br/><br/>
            </p>
            <i>Note: This is an auto-generated email, which does not need to reply.<br/></i>
            </div>',
            ${companyId}, true, 'html', 'Maintenance Script Scheduler', 'AppMaintenanceShellScriptJobActionService', null, 1, false, false, null, null, true,
            'info@athena.com.bd', null, 0, null, null, false, false, :createdBy, :createdOn);
        """, queryParams)

            executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send,
            controller_name, action_name, is_required_recipients, recipients, updated_on, updated_by, recipients_cc, display_name, has_send, is_announcement, created_by, created_on)
            VALUES (NEXTVAL('app_mail_id_seq'), 0,
            '<div>
            <p>
                Dear Concerned, <br/>
                This is a notification mail about Maintenance SQL Scheduler. <br/><br/>
                        \${script}
            </p>
            <i>Note: This is an auto-generated email, which does not need to reply.<br/></i>
            </div>',
            ${companyId}, true, 'html', 'Maintenance SQL Scheduler', 'AppMaintenanceSqlScriptJobActionService', null, 1, false, false, null, null, true,
            'info@athena.com.bd', null, 0, null, null, false, false, :createdBy, :createdOn);
        """, queryParams)

            executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send,
            controller_name, action_name, is_required_recipients, recipients, updated_on, updated_by, recipients_cc, display_name, has_send, is_announcement, created_by, created_on)
            VALUES (NEXTVAL('app_mail_id_seq'), 0,
            '<div>
            <p>
                Dear Concerned, <br/>
                This is a notification mail about Backup Script Scheduler. <br/><br/>
                        <b>Server Instance : </b> \${serverInstance} <br/><br/>
                        <b>Script : </b> \${script} <br/><br/>
                        <b>Execution Count : </b> \${executionCount} <br/>
                        <b>Executed On : </b> \${executedOn} <br/><br/>
                        <b>Message : </b> \${message} <br/><br/>
            </p>
            <i>Note: This is an auto-generated email, which does not need to reply.<br/></i>
            </div>',
            ${companyId}, true, 'html', 'Backup Script Scheduler', 'AppBackupShellScriptJobActionService', null, 1, false, false, null, null, true,
            'info@athena.com.bd', null, 0, null, null, false, false, :createdBy, :createdOn);
        """, queryParams)

            executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send,
            controller_name, action_name, is_required_recipients, recipients, updated_on, updated_by, recipients_cc, display_name, has_send, is_announcement,
            created_by, created_on)
            VALUES (NEXTVAL('app_mail_id_seq'), 0,
            '<div>
            <p>
                Dear Concerned, <br/>
                This is a notification mail about Backup SQL Scheduler. <br/><br/>
                        <b>DB Instance : </b> \${dbInstance} <br/><br/>
                        <b>Script : </b> \${script} <br/><br/>
                        <b>Message : </b> \${message} <br/><br/>
                        <b>Execution count : </b> \${executionCount} <br/>
                        <b>Executed On : </b> \${executedOn} <br/><br/>
            </p>
            <i>Note: This is an auto-generated email, which does not need to reply.<br/></i>
            </div>',
            ${companyId}, true, 'html', 'Backup SQL Scheduler', 'AppBackupSqlScriptJobActionService', null, 1, false, false, null, null, true,
            'info@athena.com.bd', null, 0, null, null, false, false, :createdBy, :createdOn);
        """, queryParams)
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createDefaultDataForProcurement(long companyId, long sysUserId) {
        try {
            AppUser procAdmin = appUserService.findByLoginIdAndCompanyId('procadmin@athena.com', companyId)
            Map queryParams = [
                    companyId: companyId,
                    createdBy: sysUserId,
                    createdOn: DateUtility.getSqlDate(new Date())
            ]
            executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send, is_required_recipients, recipients, controller_name, action_name, updated_by, has_send, is_announcement,
            created_by, created_on)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, '<div>
            <p>
                Dear Concerned, <br/>
                The following purchase order requires your approval for further processing. <br/>
            </p>
            <p>
                <b>PO No:</b> \${poId} <br/>
                <b>Created On:</b> \${poCreatedOn} <br/>
                <b>Created By:</b> \${poCreatedBy} <br/>
            </p>
            <p>
                The purchase order report is attached with this mail.
            </p>
            If you have already approved above PO, please ignore this mail. <br/>
            <i>Note: This is an auto-generated email, which does not need reply.<br/></i>
            </div>', :companyId, true, 'html', 'MIS Notification-PO approval', 'SendForApprovalProcPurchaseOrderActionService', '${
                procAdmin ? procAdmin.id : 0
            }', 5, true, false, false, null, null, null, 0, false, false, :createdBy, :createdOn);
            """, queryParams)

            executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send, is_required_recipients, recipients, controller_name, action_name, updated_by, has_send, is_announcement,
            created_by, created_on)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, '<div>
            <p>
                Dear Concerned, <br/>
                The following purchase request requires your approval for further processing. <br/>
            </p>
            <p>
                <b>PR No:</b> \${prId} <br/>
                <b>Created On:</b> \${prCreatedOn} <br/>
                <b>Created By:</b> \${prCreatedBy} <br/>
            </p>
            <p>
                The purchase request report is attached with this mail.
            </p>
            If you have already approved above PR, please ignore this mail. <br/>
            <i>Note: This is an auto-generated email, which does not need reply.<br/></i>
            </div>', :companyId, true, 'html', 'MIS Notification-PR approval', 'SentMailProcPurchaseRequestActionService', '${
                procAdmin ? procAdmin.id : 0
            }', 5, true, false, false, null, null, null, 0, false, false, :createdBy, :createdOn);
            """, queryParams)

            executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send, is_required_recipients, recipients, controller_name, action_name, updated_by, has_send, is_announcement,
            created_by, created_on)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, '<div>
            <p>
                Dear Concerned, <br/>
                The following purchase request has been re-opened for editing/modification. <br/>
            </p>
            <p>
                <b>PR No:</b> \${prId} <br/>
                <b>Opened On:</b> \${prOpenedOn} <br/>
                <b>Opened By:</b> \${prOpenedBy} <br/>
            </p>
            The modified/final purchase request should come to you later for further approval. <br/>
            <i>Note: This is an auto-generated email, which does not need reply.<br/></i>
            </div>', :companyId, true, 'html', 'MIS Notification-PR Re-opened', 'UnApproveProcPurchaseRequestActionService', '${
                procAdmin ? procAdmin.id : 0
            }', 5, true, false, false, null, null, null, 0, false, false, :createdBy, :createdOn);
            """, queryParams)

            executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send, is_required_recipients, recipients, controller_name, action_name, updated_by, has_send, is_announcement,
            created_by, created_on)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, '<div>
            <p>
                Dear Concerned, <br/>
                The following purchase order has been re-opened for editing/ modification. <br/>
            </p>
            <p>
                <b>PO No:</b> \${poId} <br/>
                <b>Opened On:</b> \${poOpenedOn} <br/>
                <b>Opened By:</b> \${poOpenedBy} <br/>
            </p>
            The modified/final purchase order should come to you later for further approval. <br/>
            <i>Note: This is an auto-generated email, which does not need reply.<br/></i>
            </div>', :companyId, true, 'html', 'MIS Notification-PO Re-opened', 'UnApproveProcPurchaseOrderActionService', '${
                procAdmin ? procAdmin.id : 0
            }', 5, true, false, false, null, null, null, 0, false, false, :createdBy, :createdOn);
            """, queryParams)
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createDefaultDataForInventory(long companyId, long sysUserId) {
        try {
            AppUser invAdmin = appUserService.findByLoginIdAndCompanyId('invadmin@athena.com', companyId)
            Map queryParams = [
                    companyId: companyId,
                    createdBy: sysUserId,
                    createdOn: DateUtility.getSqlDate(new Date())
            ]
            executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send, is_required_recipients, recipients, controller_name, action_name, updated_by, has_send, is_announcement,
            created_by, created_on)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, '<div>
            <p>
                Dear Concerned, <br/>
                The following inventory consumption requires your approval for further processing. <br/>
            </p>
            <p>
                <b>Inventory Name:</b> \${inventoryName} <br/>
                <b>Item Name:</b> \${itemName} <br/>
                <b>Quantity:</b> \${quantityUnit} <br/>
                <b>Created On:</b> \${createdOn} <br/>
            </p>
            If you have already approved above consumption, please ignore this mail.<br/>
            <i>Note: This is an auto-generated email, which does not need reply.<br/></i>
            </div>', :companyId, true, 'html', 'Consumption approval Notification - Site:', 'CreateForInvInventoryConsumptionDetailsActionService', '${
                invAdmin ? invAdmin.id : 0
            }', 4, true, false, false, null, null, null, 0, false, false, :createdBy, :createdOn);
            """, queryParams)

            executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send, is_required_recipients, recipients, controller_name, action_name, updated_by, has_send, is_announcement,
            created_by, created_on)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, '<div>
            <p>
                Dear Concerned, <br/>
                The following mail contains an attachment of all unapproved inventory transaction. <br/>
            </p>
            <i>Note: This is an auto-generated email, which does not need to reply.<br/></i>
            </div>', :companyId, true, 'html', 'All Unapproved Inventory Transaction:', 'SendMailForInvInventoryTransactionActionService', '${
                invAdmin ? invAdmin.id : 0
            }', 4, true, true, false, null, 'invInventoryTransaction', 'sendMailForInventoryTransaction', 0, false, false, :createdBy, :createdOn);
            """, queryParams)
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createDefaultDataForAccounting(long companyId, long sysUserId) {
        try {
            AppUser accAdmin = appUserService.findByLoginIdAndCompanyId('accadmin@athena.com', companyId)
            Map queryParams = [
                    companyId: companyId,
                    createdBy: sysUserId,
                    createdOn: DateUtility.getSqlDate(new Date())
            ]
            executeInsertSql("""
                INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send, is_required_recipients, recipients, controller_name, action_name, updated_by, has_send, is_announcement,
                created_by, created_on)
                VALUES (NEXTVAL('app_mail_id_seq'), 0, '<div>
                <p>
                    Dear Concerned, <br/>
                    The following IOU Slip requires your approval for further processing. <br/>
                </p>
                <p>
                    <b>Trace No :</b> \${accIouSlipId} <br/>
                    <b>Project     :</b> \${projectName} <br/>
                    <b>Employee    :</b> \${employeeName} <br/>
                    <b>Designation :</b> \${designation} <br/>
                    <b>Total Amount:</b> \${totalAmount} <br/>
                    <b>Created On  :</b> \${createdOn} <br/>
                </p>
                If you have already approved above IOU Slip, please ignore this mail. <br/>
                <i>Note: This is an auto-generated email, which does not need to reply.<br/></i>
                </div>', :companyId, true, 'html', 'MIS Notification for IOU Slip:', 'SentNotificationAccIouSlipActionService', '${
                accAdmin ? accAdmin.id : 0
            }', 2, true, false, false, null, null, null, 0, false, false, :createdBy, :createdOn);
            """, queryParams)

            executeInsertSql("""
                INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send, is_required_recipients, recipients, controller_name, action_name, updated_by, has_send, is_announcement,
                created_by, created_on)
                VALUES (NEXTVAL('app_mail_id_seq'), 0, '<div>
                <p>
                    Dear Concerned, <br/>
                    The following mail contains an attachment of all un-posted voucher. <br/>
                </p>
                <i>Note: This is an auto-generated email, which does not need to reply.<br/></i>
                </div>', :companyId, true, 'html', 'All Un-posted Voucher:', 'SendMailForAccUnpostedVoucherActionService', '${
                accAdmin ? accAdmin.id : 0
            }', 2, true, true, false, null, 'accReport','sendMailForUnpostedVoucher', 0, false, false, :createdBy, :createdOn);
            """, queryParams)

            executeInsertSql("""
                INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send, is_required_recipients, recipients, controller_name, action_name, updated_by, has_send, is_announcement,
                created_by, created_on)
                VALUES (NEXTVAL('app_mail_id_seq'), 0, '<div>
                <p>
                    Dear Concerned, <br/>
                    The following mail contains an attachment of Project wise expense. <br/>
                </p>
                <p>
                    <b>Project     :</b> \${project} <br/>
                    <b>Group       :</b> \${group} <br/>
                    <b>Date Range  :</b> \${dateRange} <br/>
                </p>
                <i>Note: This is an auto-generated email, which does not need to reply.<br/></i>
                </div>', :companyId, true, 'html', 'Project Wise Expense:', 'AccSendMailForProjectWiseExpenseActionService', '${
                accAdmin ? accAdmin.id : 0
            }', 2, true, true, false, null,'accReport','sendMailForProjectWiseExpense', 0, false, false, :createdBy, :createdOn);
            """, queryParams)

            executeInsertSql("""
                INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id,
                      is_required_role_ids, is_manual_send, is_required_recipients, recipients, controller_name, action_name, updated_by, has_send, is_announcement,
                    created_by, created_on
                )
                VALUES (NEXTVAL('app_mail_id_seq'), 0, '<div>
                <p>
                    Dear Concerned, <br/>
                    The following mail contains an attachment of supplier wise payable. <br/>
                </p>
                <p>
                    <b>Project     :</b> \${project} <br/>
                    <b>Date Range  :</b> \${dateRange}  <br/>
                </p>
                <i>Note: This is an auto-generated email, which does not need to reply.<br/></i>
                </div>', :companyId, true, 'html', 'Supplier Wise Payable:', 'AccSendMailForSupplierWisePayableActionService', '${
                accAdmin ? accAdmin.id : 0
            }', 2,
            true, true, false, null, 'accReport', 'sendMailForSupplierWisePayable', 0, false, false, :createdBy, :createdOn);
	        """, queryParams)

            executeInsertSql("""
                INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send, is_required_recipients, recipients, controller_name, action_name, updated_by, has_send, is_announcement,
                created_by, created_on)
                VALUES (NEXTVAL('app_mail_id_seq'), 0, '<div>
                <p>
                    Dear Concerned, <br/>
                    The following Indent requires your approval for further processing. <br/>
                </p>
                <p>
                    <b>Indent Trace No :</b> \${indentId} <br/>
                    <b>Project     :</b> \${projectName} <br/>
                    <b>Created By  :</b> \${createdBy} <br/>
                    <b>Created On  :</b> \${createdOn} <br/>
                </p>
                If you have already approved above Indent, please ignore this mail. <br/>
                <i>Note: This is an auto-generated email, which does not need to reply.<br/></i>
                </div>', :companyId, true, 'html', 'MIS Notification for Indent:', 'CreateAccIndentActionService', '${
                accAdmin ? accAdmin.id : 0
            }', 2, true, false, false, null, null, null, 0, false, false, :createdBy, :createdOn);
            """, queryParams)
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public void createDefaultDataForProjectTrack(long companyId, long sysUserId) {
        try {

            AppUser ptAdmin = appUserService.findByLoginIdAndCompanyId('ptadmin@athena.com', companyId)
            Map queryParams = [
                    companyId: companyId,
                    createdBy: sysUserId,
                    createdOn: DateUtility.getSqlDate(new Date())
            ]

            ///////////////////--------------START----------(Sprint Completed)---------////////////////////
            String sprintStatusMailBody = """
            <div>
                <p>
                    Dear Concerned, <br/>
                    The following Sprint is Completed. So You may Close the Sprint.<br/>
                </p>
                <p>
                    <b>ID            :</b> \${id} <br/>
                    <b>Name          :</b> \${name} <br/>
                    <b>Start Date    :</b> \${startDate} <br/>
                    <b>Completed On  :</b> \${completedOn} <br/>
                    <b>Total Task(s) :</b> \${totalTask} <br/>
                    <b>Total Bug(s)  :</b> \${totalBug} <br/>
                </p>
                <i>Note: This is an auto-generated email, which does not need to reply.<br/></i>
            </div>
        """
            executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send, is_required_recipients, recipients, controller_name, action_name, updated_by, has_send, is_announcement,
            created_by, created_on)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, '${sprintStatusMailBody}', :companyId, true, 'html', 'Sprint Completed', 'PtUpdateSprintActionService',
            '${ptAdmin ? ptAdmin.id : 0}',
            10, true, false, false, null, null, null, 0, false, false, :createdBy, :createdOn);
        """, queryParams)
            ///////////////////--------------END-------------------//////////////////////

            ///////////////////--------------START---------(Task Completed)----------////////////////////
            String taskCompleteBody = """
            <div>
                <p>
                    Dear Concerned, <br/>
                    The following Task is Completed. So You may Accept the Task.<br/>
                </p>
                <p>
                    <b>Use Case ID   :</b> \${useCaseId} <br/>
                    <b>URL           :</b> \${url} <br/>
                    <b>Module        :</b> \${moduleName} <br/>
                    <b>Idea          :</b> \${idea} <br/>
                    <b>Completed By  :</b> \${completedBy} <br/>
                    <b>Completed On  :</b> \${completedOn} <br/>
                </p>
                <i>Note: This is an auto-generated email, which does not need to reply.<br/></i>
            </div>
        """

            executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send,
                controller_name, action_name, is_required_recipients, recipients, updated_on, updated_by, recipients_cc, has_send, is_announcement, created_by, created_on)
                VALUES (NEXTVAL('app_mail_id_seq'), 0, '${taskCompleteBody}', :companyId, true, 'html', 'Task Completed', 'UpdateMyPtBacklogActionService',
               '${ptAdmin ? ptAdmin.id : 0}', 10, true, false, null, null, false, null, null, 0, null, false, false, :createdBy, :createdOn);
            """, queryParams)
            ///////////////////--------------END-------------------//////////////////////

            ///////////////////--------------START-----------(Bug Post)--------////////////////////
            String postBugBody = """
            <div>
                <p>
                    Dear Concerned, <br/>
                    The application encounters a bug. Please fix this bug soon.<br/>
                </p>
                <p>
                    <b>Bug ID            :</b> \${bugId} <br/>
                    <b>Module            :</b> \${moduleName} <br/>
                    <b>Title             :</b> \${title} <br/>
                    <b>Created On        :</b> \${prCreatedOn} <br/>
                    <b>Created By        :</b> \${prCreatedBy} <br/>
                </p>
                <i>Note: This is an auto-generated email, which does not need to reply.<br/></i>
            </div>
        """
            executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send,
                controller_name, action_name, is_required_recipients, recipients, updated_on, updated_by, recipients_cc, has_send, is_announcement, created_by, created_on)
                VALUES (NEXTVAL('app_mail_id_seq'), 0, '${postBugBody}', :companyId, true, 'html', 'Bug posted', 'CreatePtBugActionService',
                '${ptAdmin ? ptAdmin.id : 0}', 10, true, false, null, null, false, null, null, 0, null, false, false, :createdBy, :createdOn);
            """, queryParams)
            ///////////////////--------------END-------------------////////////////////

            /////////////////--------------START----------(Bug update)---------////////////////////
            String updateBugBody = """
            <div>
                <p>
                    Dear Concerned, <br/>
                    The following Bug \${status}. So You may Close the bug.<br/>
                </p>
                <p>
                    <b>Bug ID            :</b> \${bugId} <br/>
                    <b>Module            :</b> \${moduleName} <br/>
                    <b>Title             :</b> \${title} <br/>
                    <b>Posted On         :</b> \${bugPostedOn} <br/>
                    <b>Posted By         :</b> \${bugPostedBy} <br/>
                    <b>\${label} On      :</b> \${statusChangedOn} <br/>
                    <b>\${label} By      :</b> \${statusChangedBy} <br/>
                </p>
                <i>Note: This is an auto-generated email, which does not need to reply.<br/></i>
            </div>
        """
            executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send,
                controller_name, action_name, is_required_recipients, recipients, updated_on, updated_by, recipients_cc, has_send, is_announcement, created_by, created_on)
                VALUES (NEXTVAL('app_mail_id_seq'), 0, '${updateBugBody}', :companyId, true, 'html', 'Bug \${status}', 'UpdatePtBugForMyTaskActionService',
                '${ptAdmin ? ptAdmin.id : 0}', 10, true, false, null, null, false, null, null, 0, null, false, false, :createdBy, :createdOn);
            """, queryParams)
            ///////////////////--------------END-------------------////////////////////

        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createDefaultDataForExchangeHouse(long companyId, long sysUserId) {
        try {
            String customerUserMailBody = """<div>
                <p>
                    Dear Mr/Ms \${name}, <br/>
                    Thank you for registration with us.<br/><br/>
                    Your login information :<br/>
                    Login ID : \${loginId}<br/>
                    Password : \${password}<br/> <br/>
                    Please activate your account by clicking the link below (or copy/paste into your web browser): <br/>
                    <a target="_blank" href="\${link}">\${link}</a>
                </p>

                If you have already activated your account, please ignore this mail. <br/>
                <i>Note: This is an auto-generated email, which does not need reply.<br/></i>
            </div>"""
            executeInsertSql("""
            INSERT INTO "app_mail"
            ("id","version", "body", "company_id", "is_active", "is_required_role_ids", "mime_type", "plugin_id", "subject", "transaction_code", "is_manual_send", "controller_name", "action_name", "is_required_recipients", "recipients", "updated_by", "has_send", "is_announcement",
            created_by, created_on)
            VALUES
            (NEXTVAL('app_mail_id_seq'), 0, '${customerUserMailBody}', ${
                companyId
            }, '1', 'FALSE', 'html', '9', 'Activate your account with SFSA', 'ExhCreateForCustomerUserActionService',
         'FALSE', null, null, 'f', null, 0, false, false, ${sysUserId}, '${DateUtility.getDBDateFormat(new Date())}');
        """)

            String createTaskMailBody = """<div>
                <p>
                    Dear \${customerName}(ID No:\${customerCode}),<br/><br/>

                    We are glad to attach the receipt of your transaction with the mail.<br/><br/>

                    Please note that the receipt is for your information only, you are advised to collect the signed and stamped copy of the receipt.<br/><br/>

                    Your transaction is processed according to the T & C displayed in the premises and on website.<br/><br/>

                    Please find us on Facebook (https://www.facebook.com/pages/Southeast-Financial-Services-Australia-Pty-Ltd/344828868962431) for latest offers and rates.<br/><br/>

                    If you have any query please contact us on 02 95780171.<br/><br/>

                    Thank you for choosing Southeast Financial Services Australia PTY Ltd.<br/><br/>
                </p>
                <p>
                    Best Regards,<br/>
                    Customer Care Team<br/>
                    Southeast Financial Services Australia PTY Ltd.<br/>
                    1/61 Haldon St. Lakemba,<br/>
                    Sydney, Australia 2195<br/>
                    Ph: 02 95780171,<br/>
                    Mob: 0433 663 657<br/>
                    Email: info.sfsa@southeastbank.com.bd<br/>
                    http://www.southeastfinancialservices.com.au<br/>
                </p>
                <i>This is an auto-generated email, which does not need reply.<br/></i>
                </div> <br/><br/>
                """

            executeInsertSql("""
            INSERT INTO "app_mail"
            ("id","version", "body", "company_id", "is_active", "is_required_role_ids", "mime_type", "plugin_id", "subject", "transaction_code", "role_ids", "is_manual_send", "controller_name", "action_name", "is_required_recipients", "recipients", "updated_by", "has_send", "is_announcement",
            created_by, created_on)
            VALUES
            (NEXTVAL('app_mail_id_seq'), 0, '${createTaskMailBody}', ${
                companyId
            }, '1', 'FALSE', 'html', '9', 'Transaction Processed - \${refNo}', 'ExhCreateTaskActionService', null,
        'FALSE', null, null, 'f', null, 0, false, false, ${sysUserId}, '${DateUtility.getDBDateFormat(new Date())}');
        """)

            String sanctionUpdateMailBody = """<div>
            <p>
                Dear Concerned, <br/>
                System has automatically updated the following sanctions on \${date}. <br/><br/>
                <b>HM Treasury status:</b> \${hmTreasuryCount} sanctions imported. <br/>
                <b>OFAC SDN status:</b> \${ofacSdnCount} sanctions imported. <br/>
                <b>OFAC ADD status:</b> \${ofacAddCount} associations imported. <br/>
                <b>OFAC ALT status:</b> \${ofacAltCount} associations imported. <br/>
                \${message}
            </p>
            <i>Note: This is an auto-generated email, which does not need to reply.<br/></i>
            </div>"""

            executeInsertSql("""
            INSERT INTO "app_mail"
            ("id","version", "body", "company_id", "is_active", "is_required_role_ids", "mime_type", "plugin_id", "subject", "transaction_code", "role_ids", "is_manual_send", "controller_name", "action_name", "is_required_recipients", "recipients", "updated_by", "has_send", "is_announcement",
            created_by, created_on)
            VALUES
            (NEXTVAL('app_mail_id_seq'), 0, '${sanctionUpdateMailBody}', ${
                companyId
            }, '1', 'FALSE', 'html', '9', 'Exchange House Sanction Update Status', 'ExhUpdateSanctionListActionService', null,
        'FALSE', null, null, 't', 'info@athena.com.bd', 0, false, false, ${sysUserId}, '${
                DateUtility.getDBDateFormat(new Date())
            }');
        """)

            String payPointPaymentBody = """<div>
                <p>
                    Dear \${customerName}(ID No:\${customerCode}),<br/>
                    We are glad to attach the payment receipt of your transaction with this mail.<br/><br/>
                    Should you have any query please contact us on 0207 790 2434 or 0794 410 8582<br/><br/>
                    Thank you for choosing Southeast Financial Services UK Limited.<br/><br/>
                </p>
                <p>
                    Best Regards,<br/>
                    <b>Customer Care Team</b><br/>
                    Southeast Financial Services UK Limited<br/>
                    22 New Road,<br/>
                    London, E1 2AX<br/>
                    Tel: 02077902434,<br/>
                    Mob: 07575092016, 07944108582<br/>
                    Email: sfsl.customer.care@southeastbank.com.bd<br/>
                    Web: www.southeastfinancialservices.co.uk<br/>
                </p>
                <i>This is an auto-generated email, which does not need reply.<br/></i>
                </div>  <br/><br/>
                """

            executeInsertSql("""
            INSERT INTO "app_mail"
            ("id","version", "body", "company_id", "is_active", "is_required_role_ids", "mime_type", "plugin_id", "subject", "transaction_code", "role_ids", "is_manual_send", "controller_name", "action_name", "is_required_recipients", "recipients", "updated_by", "has_send", "is_announcement",
            created_by, created_on)
            VALUES
            (NEXTVAL('app_mail_id_seq'), 0, '${payPointPaymentBody}', ${
                companyId
            }, 'true', 'FALSE', 'html', '9', 'Transaction Payment Receipt', 'ExhProcessPaypointPRNActionService', null,
        'FALSE', null, null, 'f', 'info@athena.com.bd', 0, false, false, ${sysUserId}, '${
                DateUtility.getDBDateFormat(new Date())
            }');
        """)

            String updateTaskStatusMailBody = """
        <div>
            <p>
                Dear \${customerName}(ID No:\${customerCode}),<br/><br/>

                We are glad to inform you that transaction of Ref No: \${refNo} is disbursed to
                beneficiary (\${beneficiaryName}) from \${distributionPoint} on \${paidOn}.<br/><br/>
                Your transaction is disbursed according to the T & C displayed in the premises and on website.<br/><br/>
                If you have any query please contact us on 02 95780171.<br/><br/>
                Thank you for choosing Southeast Financial Services Australia PTY Ltd.<br/><br/>
            </p>
            <p>
                Best Regards,<br/>
                Customer Care Team<br/>
                Southeast Financial Services Australia PTY Ltd.<br/>
                1/61 Haldon St. Lakemba,<br/>
                Sydney, Australia 2195<br/>
                Ph: 02 95780171,<br/>
                Mob: 0433 663 657<br/>
                Email: info.sfsa@southeastbank.com.bd<br/>
                http://www.southeastfinancialservices.com.au<br/>
            </p>
            <i>This is an auto-generated email, which does not need reply.<br/></i>
        </div> <br/><br/>
        """

            executeInsertSql("""
            INSERT INTO "app_mail"
            ("id","version", "body", "company_id", "is_active", "is_required_role_ids", "mime_type", "plugin_id", "subject", "transaction_code", "role_ids", "is_manual_send", "controller_name", "action_name", "is_required_recipients", "recipients", "updated_by", "has_send", "is_announcement",
            created_by, created_on)
            VALUES
            (NEXTVAL('app_mail_id_seq'), 0, '${updateTaskStatusMailBody}', ${
                companyId
            }, '1', 'FALSE', 'html', '9', 'Transaction Disbursed - \${refNo}', 'ExhUpdateTaskStatusForRestActionService', null,
        'FALSE', null, null, 'f', null, 0, false, false, ${sysUserId}, '${DateUtility.getDBDateFormat(new Date())}');
        """)

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createDefaultDataForArms(long companyId, long sysUserId) {
        try {
            String sanctionUpdateMailBody = """<div>
            <p>
                Dear Concerned, <br/>
                System has automatically updated the following sanctions on \${date}. <br/><br/>
                <b>HM Treasury status:</b> \${hmTreasuryCount} sanctions imported. <br/>
                <b>OFAC SDN status:</b> \${ofacSdnCount} sanctions imported. <br/>
                <b>OFAC ADD status:</b> \${ofacAddCount} associations imported. <br/>
                <b>OFAC ALT status:</b> \${ofacAltCount} associations imported. <br/>
                \${message}
            </p>
            <i>Note: This is an auto-generated email, which does not need to reply.<br/></i>
            </div>"""

            executeInsertSql("""
            INSERT INTO "app_mail"
            ("id","version", "body", "company_id", "is_active", "is_required_role_ids", "mime_type", "plugin_id", "subject", "transaction_code", "role_ids", "is_manual_send", "controller_name", "action_name", "is_required_recipients", "recipients", "updated_by", "has_send", "is_announcement",
            created_by, created_on)
            VALUES
            (NEXTVAL('app_mail_id_seq'), 0, '${sanctionUpdateMailBody}', ${
                companyId
            }, '1', 'FALSE', 'html', '11', 'Sanction Update Status', 'RmsUpdateSanctionListActionService', null,
        'FALSE', null, null, 't', 'info@athena.com.bd', 0, false, false, ${sysUserId}, '${
                DateUtility.getDBDateFormat(new Date())
            }');
        """)

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public void createDefaultDataForDocument(long companyId, long sysUserId) {
        try {
            String mailSubject = "Invitation to join \${categoryLabel} \${categoryName}"
            String categoryDetailsMailBodyForSubCategory = """<div>
            <p>
                \${message}<br/>
            </p>
            <p>
                <strong>\${categoryLabel} Details:</strong>
            </p>
            <p>
                Name: \${categoryName}, <br/>
                Description : \${categoryDescription}<br/>
            </p>
            <p>
                <strong>\${subCategoryLabel} Details:</strong>
            </p>
            <div>
               \${subCategoryDetails}
            </div>
            <p>
               Please click the  <a href="\${invitationLink}">Invitation Link</a></strong>  to be a member of this category.
            </p>
            <p>
                <strong>Please note, this invitation will expire after \${expireTime} days.</strong>
            </p>
            <i>This is an auto-generated email, which does not need reply.<br/></i>
            </div>"""
            executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send, controller_name, action_name, is_required_recipients, updated_by, has_send, is_announcement, created_by, created_on)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, :categoryDetailsMailBody, ${companyId}, true, 'html'
                            ,:mailSubject, 'SendInvitationDocInvitedMembersActionServiceForSubCategory', null, 13, false, false, null, null, false, 0, false, false, ${
                sysUserId
            }, '${DateUtility.getDBDateFormat(new Date())}');
        """, [categoryDetailsMailBody: categoryDetailsMailBodyForSubCategory, mailSubject: mailSubject])

            String categoryDetailsMailBodyForCategory = """<div>
            <p>
                \${message}<br/>
            </p>
            <p>
                <strong>\${categoryLabel} Details:</strong>
            </p>
            <p>
                Name: \${categoryName}, <br/>
                Description : \${categoryDescription}<br/>
            </p>
            <p>
               Please click the  <a href="\${invitationLink}">Invitation Link</a></strong>  to be a member of this category.
            </p>
            <p>
                <strong>Please note, this invitation will expire after \${expireTime} days.</strong>
            </p>
            <i>This is an auto-generated email, which does not need reply.<br/></i>
            </div>"""
            executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send, controller_name, action_name, is_required_recipients, updated_by, has_send, is_announcement,
            created_by, created_on)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, :categoryDetailsMailBody, ${companyId}, true, 'html'
                            ,:mailSubject, 'SendInvitationDocInvitedMembersActionServiceForCategory', null, 13, false, false, null, null, false, 0, false, false, ${
                sysUserId
            }, '${DateUtility.getDBDateFormat(new Date())}');
        """, [categoryDetailsMailBody: categoryDetailsMailBodyForCategory, mailSubject: mailSubject])

            /* ApprovedDocMemberJoinRequestActionService*/
            String categoryApproveMailSubject = "Congratulations to be a member of \${categoryLabel} \${categoryName}"
            String categoryApprovedMailBody =
                    """ <div>
                    <p>
                    <strong>
                        Congratulations,
                        your application for the membership of following \${categoryLabel} has been approved.
                    </strong>
                    <p>
                        <strong>\${categoryLabel} Details:</strong> <br/>
                        Name: \${categoryName}, <br/>
                        Description : \${categoryDescription} <br/>
                    </p>

                        Login ID: \${loginId} <br/>
                        \${password} <br/>
                        Please click <a href="\${loginUrl}"> here </a> to login
                    </p>
                    <i>This is an auto-generated email, which does not need reply.<br/></i>
                    </div>
                """
            executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send, controller_name, action_name, is_required_recipients, updated_by, has_send, is_announcement,
            created_by, created_on)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, :categoryApprovedMailBody, ${companyId}, true, 'html'
                    ,:categoryApproveMailSubject, 'ApprovedDocMemberJoinRequestForCategoryActionService', null, 13, false, false, null, null, false, 0, false, false, ${
                sysUserId
            }, '${DateUtility.getDBDateFormat(new Date())}');
        """, [categoryApprovedMailBody: categoryApprovedMailBody, categoryApproveMailSubject: categoryApproveMailSubject])


            String subCategoryApproveMailSubject = "Congratulations to be a member of \${subCategoryLabel} '\${subCategoryName}'"
            String subCategoryApprovedMailBody =
                    """ <div>
                    <p>
                    <strong>
                        Congratulations,
                        your application for the membership of following \${subCategoryLabel} has been approved.
                    </strong>

                    <p>
                    <strong>\${subCategoryLabel} Details:</strong> <br/>
                    Name: \${subCategoryName} <br/>
                    Description :\${subCategoryDescription} <br/>
                    </p>
                    <p>
                        <strong>\${categoryLabel} Details:</strong> <br/>
                        Name: \${categoryName} <br/>
                        Description : \${categoryDescription} <br/>
                    </p>
                        Login ID: \${loginId} <br/>
                        \${password} <br/>
                        Please click <a href="\${loginUrl}"> here </a> to login
                    </p>
                    <i>This is an auto-generated email, which does not need reply.<br/></i>
                    </div>
                """
            executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send, controller_name, action_name, is_required_recipients, updated_by, has_send, is_announcement,
            created_by, created_on)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, :subCategoryApprovedMailBody, ${companyId}, true, 'html'
                    ,:subCategoryApproveMailSubject, 'ApprovedDocMemberJoinRequestForSubCategoryActionService', null, 13, false, false, null, null, false, 0, false, false, ${
                sysUserId
            }, '${DateUtility.getDBDateFormat(new Date())}');
        """, [subCategoryApprovedMailBody: subCategoryApprovedMailBody, subCategoryApproveMailSubject: subCategoryApproveMailSubject])

            // file upload notification mail body
            String uploadFileNotificationSubject = " \${subCategoryName} upload file notification "
            String uploadFileNotificationMailBody =
                    """ <div>
                        <p>
                        <strong>Notification!</strong>
                        <p> Your are notify for uploading file of <strong>\${subCategoryName}</strong>  </p>

                        <p>File Name: \${contentName} <br/></p>

                        <i>This is an auto-generated email, which does not need reply.<br/></i>
                    </div>
                """
            executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send, controller_name, action_name, is_required_recipients, updated_by, has_send, is_announcement,
            created_by, created_on)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, :uploadContentNotificationMailBody, ${companyId}, true, 'html'
                    ,:uploadContentNotificationSubject, 'CreateDocDocumentFileActionService', null, 13, false, false, null, null, false, 0, false, false, ${
                sysUserId
            }, '${DateUtility.getDBDateFormat(new Date())}');
        """, [uploadContentNotificationMailBody: uploadFileNotificationMailBody, uploadContentNotificationSubject: uploadFileNotificationSubject])

            // image upload notification mail body
            String uploadImageNotificationSubject = " \${subCategoryName} upload image notification "
            String uploadImageNotificationMailBody =
                    """ <div>
                        <p>
                        <strong>Notification!</strong>
                        <p> Your are notify for uploading image of <strong>\${subCategoryName}</strong>  </p>

                        <p>Image File Name: \${contentName} <br/></p>

                        <i>This is an auto-generated email, which does not need reply.<br/></i>
                    </div>
                """
            executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send, controller_name, action_name, is_required_recipients, updated_by, has_send, is_announcement,
            created_by, created_on)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, :uploadContentNotificationMailBody, ${companyId}, true, 'html'
                    ,:uploadContentNotificationSubject, 'CreateDocDocumentImageActionService', null, 13, false, false, null, null, false, 0, false, false, ${
                sysUserId
            }, '${DateUtility.getDBDateFormat(new Date())}');
        """, [uploadContentNotificationMailBody: uploadImageNotificationMailBody, uploadContentNotificationSubject: uploadImageNotificationSubject])

            // audio upload notification mail body
            String uploadAudioNotificationSubject = " \${subCategoryName} upload audio notification "
            String uploadAudioNotificationMailBody =
                    """ <div>
                        <p>
                        <strong>Notification!</strong>
                        <p> Your are notify for uploading audio of <strong>\${subCategoryName}</strong>  </p>

                        <p>Audio File Name: \${contentName} <br/></p>

                        <i>This is an auto-generated email, which does not need reply.<br/></i>
                    </div>
                """
            executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send, controller_name, action_name, is_required_recipients, updated_by, has_send, is_announcement,
            created_by, created_on)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, :uploadContentNotificationMailBody, ${companyId}, true, 'html'
                    ,:uploadContentNotificationSubject, 'CreateDocDocumentAudioActionService', null, 13, false, false, null, null, false, 0, false, false, ${
                sysUserId
            }, '${DateUtility.getDBDateFormat(new Date())}');
        """, [uploadContentNotificationMailBody: uploadAudioNotificationMailBody, uploadContentNotificationSubject: uploadAudioNotificationSubject])

            // video upload notification mail body
            String uploadVideoNotificationSubject = " \${subCategoryName} upload video notification "
            String uploadVideoNotificationMailBody =
                    """ <div>
                        <p>
                        <strong>Notification!</strong>
                        <p> Your are notify for uploading video of <strong>\${subCategoryName}</strong>  </p>

                        <p>Video File Name: \${contentName} <br/></p>

                        <i>This is an auto-generated email, which does not need reply.<br/></i>
                    </div>
                """
            executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send, controller_name, action_name, is_required_recipients, updated_by, has_send, is_announcement,
            created_by, created_on)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, :uploadContentNotificationMailBody, ${companyId}, true, 'html'
                    ,:uploadContentNotificationSubject, 'CreateDocDocumentVideoActionService', null, 13, false, false, null, null, false, 0, false, false, ${
                sysUserId
            }, '${DateUtility.getDBDateFormat(new Date())}');
        """, [uploadContentNotificationMailBody: uploadVideoNotificationMailBody, uploadContentNotificationSubject: uploadVideoNotificationSubject])

            // new article notification mail body
            String subCategoryAddArticleNotificationSubject = "New/Update article notification "
            String subCategoryAddArticleNotificationMailBody =
                    """ <div>
                        <p>
                        <strong>Notification!</strong>
                        <p> Your are notify for new/update article in <strong>\${subCategoryName}</strong></p>
                        <p>Article Title: \${docTitle} <br/></p>
                        <i>This is an auto-generated email, which does not need reply.<br/></i>
                    </div>
                """
            executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send, controller_name, action_name, is_required_recipients, updated_by, has_send, is_announcement,
            created_by, created_on)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, :subCategoryAddArticleNotificationMailBody, ${companyId}, true, 'html'
                    ,:subCategoryAddArticleNotificationSubject, 'CreateDocDocumentArticleActionService', null, 13, false, false, null, null, false, 0, false, false, ${
                sysUserId
            }, '${DateUtility.getDBDateFormat(new Date())}');
        """, [subCategoryAddArticleNotificationMailBody: subCategoryAddArticleNotificationMailBody, subCategoryAddArticleNotificationSubject: subCategoryAddArticleNotificationSubject])

            // new article notification mail body
            String changeOwnershipOfDocumentNotificationSub = "Change document ownership notification "
            String changeOwnershipOfDocumentNotificationBody =
                    """ <div>
                        <p>
                        <strong>Notification!</strong>
                        <p>Dear Owner,</p>
                        <p>Your are notify for document ownership details:</p>
                        <p>
                            <strong>\${categoryLabel}:</strong> \${categoryName}<br/>
                        </p>
                         <p>
                            <strong>\${subCategoryLabel}:</strong><strong>\${subCategoryName}</strong> <br/>
                        </p>
                        <p>Document Title: \${docTitle} <br/></p>
                        <p>File: \${fileName} <br/></p>
                        <p>Current Owner: \${currOwner} <br/></p>
                         <p>Ownership Date: \${date} <br/></p>
                        <i>This is an auto-generated email, which does not need reply.<br/></i>
                    </div>
                """
            executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send, controller_name, action_name, is_required_recipients, updated_by, has_send, is_announcement,
            created_by, created_on)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, :changeOwnershipOfDocumentNotificationBody, ${companyId}, true, 'html'
                    ,:changeOwnershipOfDocumentNotificationSub, 'ChangeDocDocumentOwnerActionService', null, 13, false, false, null, null, false, 0, false, false, ${
                sysUserId
            }, '${DateUtility.getDBDateFormat(new Date())}');
        """, [changeOwnershipOfDocumentNotificationBody: changeOwnershipOfDocumentNotificationBody, changeOwnershipOfDocumentNotificationSub: changeOwnershipOfDocumentNotificationSub])

            String categoryAcceptMailBody = """<div>
            <p>
            <strong>
                Congratulations,
                your membership of following \${categoryLabel} has been accepted.
            </strong>
            <p>
                <strong>\${categoryLabel} Details:</strong> <br/>
                Name: \${categoryName}, <br/>
                Description : \${categoryDescription} <br/>
            </p>
                Login ID: \${loginId} <br/>
                Please click <a href="\${loginUrl}"> here </a> to login
            </p>
            <i>This is an auto-generated email, which does not need reply.<br/></i>
            </div>"""
            executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send, controller_name, action_name, is_required_recipients, updated_by, has_send, is_announcement,
            created_by, created_on)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, :categoryAcceptMailBody, ${companyId}, true, 'html'
                    ,:categoryApproveMailSubject, 'AcceptInvitationDocInvitedMemberActionService', null, 13, false, false, null, null, false, 0, false, false, ${
                sysUserId
            }, '${DateUtility.getDBDateFormat(new Date())}');
        """, [categoryAcceptMailBody: categoryAcceptMailBody, categoryApproveMailSubject: categoryApproveMailSubject])

            String checkInMailSubject = "Document checked in by \${owner}"
            String checkInMailBody = """<div>
            <p>
            <strong>
                Dear \${username},
                your document has been checked in by \${owner}
            </strong>
            <p>
                <strong>Document Details:</strong> <br/>
                Name: \${fileName}, <br/>
                Title: \${title}, <br/>
                Type: \${documentCategoryType} <br/>
            <i>This is an auto-generated email, which does not need reply.<br/></i>
            </div>"""
            executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send, controller_name, action_name, is_required_recipients, updated_by, has_send, is_announcement,
            created_by, created_on)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, :checkInMailBody, ${companyId}, true, 'html'
                    ,:checkInMailSubject, 'CheckInDocDocumentForFileActionService', null, 13, false, false, null, null, false, 0, false, false, ${
                sysUserId
            }, '${DateUtility.getDBDateFormat(new Date())}');
        """, [checkInMailBody: checkInMailBody, checkInMailSubject: checkInMailSubject])

        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * create default mail data for DataPipeLine
     * @param companyId
     */
    public void createDefaultDataForDataPipeLine(long companyId, long sysUserId) {
        try {
            String dataExportMailSubject = "Data export has successfully completed."
            String dataExportMailBody = """<div>
            <p>
                 <strong>Name: </strong> \${name}.
            </p>
            <p>
                <strong>Path: </strong> \${path}
            </p>
            <p>
                <strong>Source Database: </strong> \${sourceDb}
            </p>

            <i>This is an auto-generated email, which does not need reply.<br/></i>
            </div>"""
            executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send, controller_name, action_name, is_required_recipients, updated_by, has_send, recipients, is_announcement,
            created_by, created_on)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, :singleLogMailBody, ${companyId}, true, 'html'
                            ,:mailSubject, 'SendMailDplDataExportActionService', null, 14, false, false, null, null, true, 0, false, 'info@athena.com.bd', false, ${
                sysUserId
            }, '${DateUtility.getDBDateFormat(new Date())}');
        """, [singleLogMailBody: dataExportMailBody, mailSubject: dataExportMailSubject])

            String dataImportMailSubject = "Data import has successfully completed."
            String dataImportMailBody = """<div>
            <p>
                 <strong>Name: </strong> \${name}.
            </p>
            <p>
                <strong>Path: </strong> \${path}
            </p>
            <p>
                <strong>Source Database: </strong> \${targetDb}
            </p>

            <i>This is an auto-generated email, which does not need reply.<br/></i>
            </div>"""
            executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send, controller_name, action_name, is_required_recipients, updated_by, has_send, recipients, is_announcement,
            created_by, created_on)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, :singleLogMailBody, ${companyId}, true, 'html'
                            ,:mailSubject, 'SendMailDplDataImportActionService', null, 14, false, false, null, null, true, 0, false, 'info@athena.com.bd', false, ${
                sysUserId
            }, '${DateUtility.getDBDateFormat(new Date())}');
        """, [singleLogMailBody: dataImportMailBody, mailSubject: dataImportMailSubject])

            String dayWiseLogMailSubject = "Summary Report for CDC operations"
            String DayWiseMailBodyForCdcLogFile = """<div>
            <p>
                Day end summary report of  CDC operations. All details information are in PDF file.
            </p>

            <i>This is an auto-generated email, which does not need reply.<br/></i>
            </div>"""
            executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send, controller_name, action_name, is_required_recipients, updated_by, has_send, recipients, is_announcement,
            created_by, created_on)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, :singleLogMailBody, ${companyId}, true, 'html'
                            ,:mailSubject, 'DplCdcSendMailDailyJobActionService', null, 14, false, false, null, null, true, 0, false, 'info@athena.com.bd', false, ${
                sysUserId
            }, '${DateUtility.getDBDateFormat(new Date())}');
        """, [singleLogMailBody: DayWiseMailBodyForCdcLogFile, mailSubject: dayWiseLogMailSubject])

            String processCdcMsSqlMailSubject = "CDC Report for \${cdcName} of \${vendorName}(\${startTime})"
            String processCdcMsSqlMailBody = """<div>
            <p>
                Dear Concerned, <br/>
                Report of CDC operation for \${vendorName}
                        <b>CDC : </b> \${cdcName} <br/><br/>
                        <b>Vendor : </b> \${vendorName} <br/><br/>
                        <b>Start Time : </b> \${startTime} <br/><br/>
                        <b>End Time : </b> \${endTime} <br/><br/>
                        <b>Source DB : </b> \${sourceDb} <br/><br/>
                        <b>Target DB : </b> \${targetDb} <br/><br/>
            </p>

            <i>This is an auto-generated email, which does not need reply.<br/></i>
            </div>"""
            executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send, controller_name, action_name, is_required_recipients, updated_by, has_send, recipients, is_announcement,
            created_by, created_on)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, :mailBody, ${companyId}, true, 'html'
                            ,:mailSubject, 'DplProcessMSSqlLogJobActionService', null, 14, false, false, null, null, true, 0, false, 'info@athena.com.bd', false, ${
                sysUserId
            }, '${DateUtility.getDBDateFormat(new Date())}');
        """, [mailBody: processCdcMsSqlMailBody, mailSubject: processCdcMsSqlMailSubject])

            executeInsertSql("""
            INSERT INTO app_mail(id, version, body, company_id, is_active, mime_type, subject, transaction_code,
            role_ids, plugin_id, is_required_role_ids, is_manual_send, is_required_recipients, recipients,
            controller_name, action_name, updated_by, has_send, is_announcement, created_by, created_on)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, '<div>
            <p>
                Dear Concerned, <br/>
                This mail reports an error that occurred during CDC execution. <br/><br/>
                        <b>CDC : </b> \${cdcName} <br/><br/>
                        <b>Vendor : </b> \${vendorName} <br/><br/>
                        <b>Start Time : </b> \${startTime} <br/><br/>
                        <b>End Time : </b> \${endTime} <br/><br/>
                        <b>Source DB : </b> \${sourceDb} <br/><br/>
                        <b>Target DB : </b> \${targetDb} <br/><br/>
                        <b>Error Message : </b> \${errorMessage} <br/>
            </p>
            <i>Note: This is an auto-generated email, which does not need to reply.<br/></i>
            </div>', ${companyId}, true, 'html', 'Error Report(CDC) :', 'SendErrorMailForCdcActionService', null, 14,
            false, false, true, 'info@athena.com.bd', null, null, 0, false, false, ${sysUserId}, '${DateUtility.getDBDateFormat(new Date())}');
        """)

        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public void createDefaultDataForELearning(long companyId, long sysUserId) {
        String assignmentMailSubject = """ New Assignment For \${lessonTitle} Under \${courseName}"""
        String assignmentMailBody = """
            <div>
                <p>Dear participants,</p>
                <p> new assignment is created on \${lessonTitle} of \${courseName} by \${username}. You are requested to submit this assignment by \${assignmentDeadLine}</p>
                <p>Thanks for your participation</p>
            </div>
        """

        executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send, controller_name, action_name, is_required_recipients, updated_by, has_send, recipients, is_announcement,
            created_by, created_on)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, :mailBody, ${companyId}, true, 'html'
                            ,:mailSubject, 'ElNotifyForAssignmentActionService', null, 15, false, false, null, null, false, 0, false, null, false, ${
            sysUserId
        }, '${DateUtility.getDBDateFormat(new Date())}');
        """, [mailBody: assignmentMailBody, mailSubject: assignmentMailSubject])

        String joinRequestMailSubject = """\${username} is interested in \${courseName}"""
        String joinRequestMailBody = """
            <div>
                <p>Dear sir,</p>
                <p> This is \${username}, interested to enroll in \${courseName}.</p>
                <p>Best regards,<br/></p>
                <p>\${username}</p>
                <p>Email: \${email}</p>
                <p>Phone: \${phone}</p>
            </div>
        """

        executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send, controller_name, action_name, is_required_recipients, updated_by, has_send, recipients, is_announcement,
            created_by, created_on)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, :mailBody, ${companyId}, true, 'html'
                            ,:mailSubject, 'ElJoinCourseActionService', null, 15, false, false, null, null, false, 0, false, null, false, ${
            sysUserId
        }, '${DateUtility.getDBDateFormat(new Date())}');
        """, [mailBody: joinRequestMailBody, mailSubject: joinRequestMailSubject])

        // Assignment mark assign mail notification
        String subAssignMarkForAssignment = "assignment mark on [ \${assignment_title} ]"
        String bodyOfAssignMarkForAssignment =
                """ <div>
                        <p>
                         Dear Concerned, <br/>
                         <p>Your are notify for marking your assignment:</p>
                         <p><strong>Course:</strong> \${course_name}<br/></p>
                         <p><strong>Lesson:</strong>\${lesson_title}<br/></p>
                         <p><strong>Assignment:</strong> \${assignment_title} <br/></p>
                         <p><strong>Total Mark:</strong> \${total_mark} <br/></p>
                         <p><strong>Obtain Mark: </strong>\${obtain_mark} <br/></p>
                         <p><strong>Marked By:</strong> \${marked_by} <br/></p>
                        <i>This is an auto-generated email, which does not need reply.<br/></i>
                    </div>
                """
        executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send, controller_name, action_name, is_required_recipients, updated_by, has_send, is_announcement,
            created_by, created_on)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, :bodyOfAssignMarkForAssignment, ${companyId}, true, 'html'
                    ,:subAssignMarkForAssignment, 'AssignMarkForAssignmentUserActionService', null, 15, false, false, null, null, false, 0, false, false, ${sysUserId}, '${DateUtility.getDBDateFormat(new Date())}');
        """, [bodyOfAssignMarkForAssignment: bodyOfAssignMarkForAssignment, subAssignMarkForAssignment: subAssignMarkForAssignment])

        // Exam mail notification
        String subjectExamNotify = "\${exam_name} exam is created for \${lesson_title} Under \${course_name}"
        String bodyExamNotify =
                """ <div>
                        <p>
                        <p>Dear participants,</p><br/>
                         <p>Your are notify for creating a new exam</p>
                         <p><strong>Course:</strong> \${course_name}<br/></p>
                         <p><strong>Lesson:</strong>\${lesson_title}<br/></p>
                         <p><strong>Exam:</strong> \${exam_name} <br/></p>
                         <p><strong>Duration (Min):</strong> \${duration} <br/></p>
                         <p><strong>Total Score :</strong> \${total_score} <br/></p>
                        <i>This is an auto-generated email, which does not need reply.<br/></i>
                    </div>
                """
        executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send, controller_name, action_name, is_required_recipients, updated_by, has_send, is_announcement,
            created_by, created_on)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, :bodyExamNotify, ${companyId}, true, 'html'
                    ,:subjectExamNotify, 'NotifyElExamActionService', null, 15, false, false, null, null, false, 0, false, false, ${sysUserId}, '${DateUtility.getDBDateFormat(new Date())}');
        """, [bodyExamNotify: bodyExamNotify, subjectExamNotify: subjectExamNotify])

        // Quiz mail notification
        String subjectQuizNotify = "\${quiz_name} quiz is created for \${lesson_title} Under \${course_name}"
        String bodyQuizNotify =
                """ <div>
                        <p>
                        <p>Dear participants,</p><br/>
                         <p>Your are notify for creating a new quiz</p>
                         <p><strong>Course:</strong> \${course_name}<br/></p>
                         <p><strong>Lesson:</strong>\${lesson_title}<br/></p>
                         <p><strong>Quiz:</strong> \${quiz_name} <br/></p>
                         <p><strong>Total Score :</strong> \${total_score} <br/></p>
                        <i>This is an auto-generated email, which does not need reply.<br/></i>
                    </div>
                """
        executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send, controller_name, action_name, is_required_recipients, updated_by, has_send, is_announcement,
            created_by, created_on)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, :bodyQuizNotify, ${companyId}, true, 'html'
                    ,:subjectQuizNotify, 'NotifyElQuizActionService', null, 15, false, false, null, null, false, 0, false, false, ${sysUserId}, '${DateUtility.getDBDateFormat(new Date())}');
        """, [bodyQuizNotify: bodyQuizNotify, subjectQuizNotify:  subjectQuizNotify])
    }

    @Override
    public boolean createTestData(long companyId, long systemUserId) {
        Role adminRole = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_APP_ADMIN)
        Map queryParams = [
                id       : testDataModelService.getNextIdForTestData(),
                version  : 0L,
                companyId: companyId,
                createdBy: systemUserId,
                createdOn: DateUtility.getSqlDate(new Date())
        ]
        executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send, is_required_recipients, updated_by, has_send, display_name, is_announcement,
            created_by, created_on)
            VALUES (:id, :version, '<div>
                <p>
                    Dear Concerned, <br/>
                         This is a sample mail.
                </p>

                <i>This is an auto-generated email, which does not need reply.<br/>
           </div>', :companyId, true, 'html', 'Sample Mail :', 'SendAppAnnouncementActionService', '${adminRole.id}', 0, true, true, false, 0, false, 'Athena', false, :createdBy, :createdOn);
        """, queryParams)
    }
}