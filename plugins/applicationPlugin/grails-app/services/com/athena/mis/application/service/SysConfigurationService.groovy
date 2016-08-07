package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.integration.accounting.AccPluginConnector
import com.athena.mis.integration.arms.ArmsPluginConnector
import com.athena.mis.integration.datapipeline.DataPipeLinePluginConnector
import com.athena.mis.integration.document.DocumentPluginConnector
import com.athena.mis.integration.elearning.ELearningPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.integration.inventory.InvPluginConnector
import com.athena.mis.integration.procurement.ProcPluginConnector
import com.athena.mis.integration.projecttrack.PtPluginConnector
import com.athena.mis.integration.sarb.SarbPluginConnector
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Service class for basic SysConfiguration CRUD (Except Create)
 *  For details go through Use-Case doc named 'SysConfigurationService'
 */
class SysConfigurationService extends BaseDomainService {

    private Logger log = Logger.getLogger(getClass())

    AppUserService appUserService

    private static final String SORT_BY_KEY = "key"
    public static final String SYS_CONFIG_NOT_FOUND_MESSAGE = "System config message not found for:"

    @Override
    public void init() {
        domainClass = SysConfiguration.class
    }

    /**
     * Method to count system configuration
     * @param companyId - company id
     * @return - an integer value of system configuration count
     */
    public int countByCompanyIdAndPluginId(long companyId, int pluginId) {
        int count = SysConfiguration.countByCompanyIdAndPluginId(companyId, pluginId)
        return count
    }

    /**
     * Method to count system configuration
     * @param key - system configuration key
     * @param companyId - company id
     * @return - an integer value of system configuration count
     */
    public int countByKeyAndCompanyIdAndPluginId(String key, long companyId, int pluginId) {
        int count = SysConfiguration.countByKeyIlikeAndCompanyIdAndPluginId(key, companyId, pluginId)
        return count
    }

    /**
     * Method to find the list of system configuration
     * @param companyId - company id
     * @return - a list of system configuration
     */
    public List findAllByCompanyIdAndPluginId(long companyId, int pluginId, BaseService baseService) {
        List sysConList = SysConfiguration.findAllByCompanyIdAndPluginId(companyId, pluginId, [max: baseService.resultPerPage, offset: baseService.start, sort: SORT_BY_KEY, order: ASCENDING_SORT_ORDER, readOnly: true])
        return sysConList
    }

    /**
     * Method to find the list of system configuration
     * @param key - system configuration key
     * @param companyId - company id
     * @return - a list of system configuration
     */
    public List findAllByKeyAndCompanyIdAndPluginId(String key, long companyId, int pluginId, BaseService baseService) {
        List sysConList = SysConfiguration.findAllByKeyIlikeAndCompanyIdAndPluginId(key, companyId, pluginId, [readOnly: true, max: baseService.resultPerPage, offset: baseService.start, sort: SORT_BY_KEY, order: ASCENDING_SORT_ORDER])
        return sysConList
    }

    /**
     * Method to find system configuration object
     * @param sysConId - system configuration id
     * @return - an object of system configuration
     */
    public SysConfiguration findById(int sysConId) {
        SysConfiguration sysCon = SysConfiguration.findById(sysConId, [readOnly: true])
        return sysCon
    }

    /**
     * Method to read system configuration object By Key, PluginId and CompanyId
     * @param sysConId - system configuration id
     * @return - an object of system configuration
     */
    public SysConfiguration readByKeyAndPluginIdAndCompanyId(String key, int pluginId, long companyId) {
        SysConfiguration configuration = SysConfiguration.findByKeyAndPluginIdAndCompanyId(key, pluginId, companyId, [readOnly: true])
        return configuration
    }

    /**
     * get list of all SysConfiguration object(s) of a specific plugin
     * @param pluginId
     * @return -list of SysConfiguration objects
     */
    @Transactional(readOnly = true)
    public List listByPlugin(int pluginId) {
        return SysConfiguration.findAllByPluginId(pluginId, [readOnly: true])
    }

    /**
     * get list of all SysConfiguration object(s) of a specific key
     * @param key
     * @return -list of SysConfiguration objects
     */
    @Transactional(readOnly = true)
    public List listByKey(String key) {
        return SysConfiguration.findAllByKey(key, [readOnly: true])
    }

    /**
     * get list of all SysConfiguration object(s)
     * @return -list of SysConfiguration objects
     */
    @Override
    public List list() {
        return SysConfiguration.list(sort: SysConfiguration.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true)
    }

    private static final String DELETE_ALL = """
        DELETE FROM sys_configuration WHERE company_id = :companyId
    """

    /**
     * Delete all sysConfiguration by companyId
     * @param companyId - id of company
     */
    public void deleteAllByCompanyId(long companyId) {
        Map queryParams = [
                companyId: companyId
        ]
        executeUpdateSql(DELETE_ALL, queryParams)
    }

    /**
     * retrieve system configuration message
     * @param sysConfiguration
     * @return
     */
    public String getSysConfigMessage(SysConfiguration sysConfiguration) {
        if (sysConfiguration && sysConfiguration.message) {
            return sysConfiguration.message
        }
        return SYS_CONFIG_NOT_FOUND_MESSAGE + SINGLE_SPACE + (sysConfiguration ? sysConfiguration.key : EMPTY_SPACE)
    }

    /**
     * insert default SysConfiguration into database of Inventory plugin when application starts with bootstrap
     */
    public boolean createDefaultAppSysConfig(long companyId) {
        try {
            SysConfiguration passwordExpirationTime = new SysConfiguration(key: 'mis.application.defaultPasswordExpireDuration', value: '180')
            passwordExpirationTime.description = """
                Duration for password expire from creation date of user, value= No. of Days, if config not found expire date=current date
            """
            passwordExpirationTime.pluginId = PluginConnector.PLUGIN_ID
            passwordExpirationTime.companyId = companyId
            passwordExpirationTime.transactionCode = 'UpdateAppCompanyUserActionService, CreateAppUserActionService, RegisterAppUserActionService, ResetExpiredPasswordActionService, ExhCreateForCustomerUserActionService'
            passwordExpirationTime.save()

            SysConfiguration disableLogin = new SysConfiguration(key: 'mis.application.isMaintenanceMode', value: 'false')
            disableLogin.description = """
                if value = true, then application is in maintenance mode and only config manager user can login; if value = false then all user can login
            """
            disableLogin.pluginId = PluginConnector.PLUGIN_ID
            disableLogin.companyId = companyId
            disableLogin.transactionCode = 'LoginSuccessActionService'
            disableLogin.message = "Application maintenance in progress, please contact with your administrator"
            disableLogin.save()

            SysConfiguration supportedExtensionsSysConfiguration = new SysConfiguration(key: 'mis.application.supportedExtensions', value: 'pdf, doc')
            supportedExtensionsSysConfiguration.description = 'Determine Supported Extensions, Default Value is pdf,doc'
            supportedExtensionsSysConfiguration.pluginId = PluginConnector.PLUGIN_ID
            supportedExtensionsSysConfiguration.companyId = companyId
            supportedExtensionsSysConfiguration.transactionCode = 'N/A'
            supportedExtensionsSysConfiguration.save()

            SysConfiguration nativeIpAddress = new SysConfiguration(key: 'mis.application.nativeIpAddress', value: '127.0.0.1')
            nativeIpAddress.description = 'Native/Local IP address of server'
            nativeIpAddress.pluginId = PluginConnector.PLUGIN_ID
            nativeIpAddress.companyId = companyId
            nativeIpAddress.transactionCode = 'N/A'
            nativeIpAddress.save()

            SysConfiguration enforceVersionConfig = new SysConfiguration(key: 'mis.application.enforceReleaseVersion', value: 'true')
            enforceVersionConfig.description = 'If config is true , enforce to update application after current release span'
            enforceVersionConfig.message = 'Application is out of date. Please consider updating to latest version'
            enforceVersionConfig.pluginId = PluginConnector.PLUGIN_ID
            enforceVersionConfig.companyId = companyId
            enforceVersionConfig.transactionCode = 'CreateAccVoucherActionService, LoginSuccessActionService, CheckAppVersionTagLibActionService, ExhSendExceptionalTaskToBankWithRestActionService, CreateBudgBudgetActionService, CreateForInvInventoryInFromInventoryActionService, CreateForInvInventoryInFromSupplierActionService, CreateProcPurchaseRequestActionService, CreatePtBacklogActionService'
            enforceVersionConfig.save()

            SysConfiguration appUserRegistration = new SysConfiguration(key: 'mis.application.enableNewUserRegistration', value: 'true')
            appUserRegistration.description = """
                Check if app user registration link will be enabled or not. true = Enabled; false = Disabled. If config not found then default value is false.
            """
            appUserRegistration.pluginId = PluginConnector.PLUGIN_ID
            appUserRegistration.companyId = companyId
            appUserRegistration.transactionCode = 'GetSysConfigUserRegistrationActionService'
            appUserRegistration.message = "New User? Click Here"
            appUserRegistration.save()

            SysConfiguration roleIdForUserRegistration = new SysConfiguration(key: 'mis.application.roleIdForUserRegistration', value: '0')
            roleIdForUserRegistration.description = 'Role id to map with app user at registration. 0 or incorrect value will not assign any role'
            roleIdForUserRegistration.pluginId = PluginConnector.PLUGIN_ID
            roleIdForUserRegistration.companyId = companyId
            roleIdForUserRegistration.transactionCode = 'RegisterAppUserActionService'
            roleIdForUserRegistration.message = "No role found to map with app user"
            roleIdForUserRegistration.save()

            SysConfiguration deploymentMode = new SysConfiguration(key: 'mis.application.deploymentMode', value: '2')
            deploymentMode.description = 'If value = 1 then Application runs in Production mode and mail and sms is sendable, if value = 2 then ' +
                    'Application runs in Development mode and mail and sms is not sendable. Default value is 1. If the value is not 1 or 2 then it will ' +
                    'considered as 1'
            deploymentMode.pluginId = PluginConnector.PLUGIN_ID
            deploymentMode.companyId = companyId
            deploymentMode.transactionCode = 'GetSysConfigLoginTemplateActionService, AppCreateTestDataActionService, AppDeleteTestDataActionService, AppMailService, AppSmsService, LoginController'
            deploymentMode.save()

            SysConfiguration phonePattern = new SysConfiguration(key: 'mis.application.phonePattern', value: '\\d{11}')
            phonePattern.description = 'Determines phone number pattern for user'
            phonePattern.pluginId = PluginConnector.PLUGIN_ID
            phonePattern.companyId = companyId
            phonePattern.transactionCode = 'ShowSysConfigTagLibActionService'
            phonePattern.save()

            SysConfiguration smsUrl = new SysConfiguration(key: 'mis.application.smsUrl', value: """"http://mySmsApi?user=username&password=pwd&sender=abc&SMSText=" + \${content} + "&GSM=" + \${recipient}""")
            smsUrl.description = 'Determines sms url for sending sms'
            smsUrl.pluginId = PluginConnector.PLUGIN_ID
            smsUrl.companyId = companyId
            smsUrl.transactionCode = 'SmsSenderService'
            smsUrl.save()

            SysConfiguration attachmentSize = new SysConfiguration(key: 'mis.application.attachmentSize', value: '5242880')
            attachmentSize.description = 'Determines attachment size. Maximum size is 5 MB. Value should be in byte.'
            attachmentSize.pluginId = PluginConnector.PLUGIN_ID
            attachmentSize.companyId = companyId
            attachmentSize.transactionCode = 'UploadAppAttachmentActionService'
            attachmentSize.save()

            SysConfiguration defaultPlugin = new SysConfiguration(key: 'mis.application.defaultPlugin', value: '1')
            defaultPlugin.description = 'Determines plugin id. Render plugin wise index page.'
            defaultPlugin.pluginId = PluginConnector.PLUGIN_ID
            defaultPlugin.companyId = companyId
            defaultPlugin.transactionCode = 'RenderIndexActionService'
            defaultPlugin.save()

            SysConfiguration showApplicationMenu = new SysConfiguration(key: 'mis.application.showApplicationMenu', value: 'true')
            showApplicationMenu.description = 'Determine application menu show or not. default value true, means show'
            showApplicationMenu.pluginId = PluginConnector.PLUGIN_ID
            showApplicationMenu.companyId = companyId
            showApplicationMenu.transactionCode = ''
            showApplicationMenu.save()

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * insert default SysConfiguration into database of Accounting plugin when application starts with bootstrap
     */
    public boolean createDefaultAccSysConfig(long companyId) {
        try {
            AppUser accAdmin = appUserService.findByLoginIdAndCompanyId('accadmin@athena.com', companyId)

            SysConfiguration showPostedVouchers = new SysConfiguration(key: 'mis.accounting.showPostedVouchers', value: '-1')
            showPostedVouchers.description = """
                ShowPostedVouchers determines whether all posted-only vouchers will be considered to generate reports.
                 0 = consider only posted voucher;
                -1 = consider both vouchers;
                default value is -1
            """
            showPostedVouchers.pluginId = AccPluginConnector.PLUGIN_ID
            showPostedVouchers.companyId = companyId
            showPostedVouchers.transactionCode = 'AccDownloadForCustomGroupBalanceActionService, AccDownloadForCustomGroupBalanceCsvActionService, AccListForCustomGroupBalanceActionService, AccDownloadForFinancialStatementCsvOfLevel2ActionService, AccDownloadForFinancialStatementCsvOfLevel3ActionService, AccDownloadForFinancialStatementCsvOfLevel4ActionService, AccDownloadForFinancialStatementCsvOfLevel5ActionService, AccDownloadForFinancialStatementOfLevel2ActionService, AccDownloadForFinancialStatementOfLevel3ActionService' +
                    ', AccDownloadForFinancialStatementOfLevel4ActionService, AccDownloadForFinancialStatementOfLevel5ActionService, AccListForFinancialStatementOfLevel2ActionService, AccListForFinancialStatementOfLevel3ActionService, AccListForFinancialStatementOfLevel4ActionService, AccListForFinancialStatementOfLevel5ActionService, AccDownloadForIncomeStatementCsvOfLevel4ActionService, AccDownloadForIncomeStatementCsvOfLevel5ActionService, AccDownloadForIncomeStatementOfLevel4ActionService, AccDownloadForIncomeStatementOfLevel5ActionService,' +
                    'AccListForIncomeStatementOfLevel4ActionService, AccListForIncomeStatementOfLevel5ActionService, AccDownloadForGroupLedgerActionService, AccDownloadForGroupLedgerCsvActionService, AccDownloadForLedgerActionService, AccDownloadForLedgerCsvActionService, AccDownloadForSourceLedgerActionService, AccDownloadForSourceLedgerCsvActionService, AccDownloadForSourceLedgerGroupBySourceActionService, AccListForGroupLedgerActionService, AccListForLedgerActionService, AccListForSourceLedgerActionService, AccShowForLedgerActionService, AccDownloadForTrialBalanceCsvOfLevel2ActionService, ' +
                    'AccDownloadForTrialBalanceCsvOfLevel3ActionService, AccDownloadForTrialBalanceCsvOfLevel4ActionService, AccDownloadForTrialBalanceCsvOfLevel5ActionService, AccDownloadForTrialBalanceOfLevel2ActionService, AccDownloadForTrialBalanceOfLevel3ActionService, AccDownloadForTrialBalanceOfLevel4ActionService, AccDownloadForTrialBalanceOfLevel5ActionService, AccListForTrialBalanceOfLevel2ActionService, AccListForTrialBalanceOfLevel3ActionService, AccListForTrialBalanceOfLevel4ActionService, AccListForTrialBalanceOfLevel5ActionService, SendMailForAccUnpostedVoucherActionService, AccDownloadForProjectFundFlowActionService, AccListForProjectFundFlowActionService, AccDownloadForProjectWiseExpenseActionService, ' +
                    'AccDownloadForProjectWiseExpenseCsvActionService, AccListForProjectWiseExpenseActionService, AccListForProjectWiseExpenseDetailsActionService, AccSendMailForProjectWiseExpenseActionService, AccDownloadForSourceWiseBalanceActionService, AccDownloadForSourceWiseBalanceCsvActionService, AccDownloadForVoucherListBySourceIdActionService, AccListForSourceWiseBalanceActionService, AccDownloadForSupplierWisePayableActionService, AccDownloadForSupplierWisePayableCsvActionService, AccListForSupplierWisePayableActionService, AccDownloadForSupplierWisePaymentActionService, AccDownloadForSupplierWisePaymentCsvActionService, AccListForSupplierWisePaymentActionService'
            showPostedVouchers.save()

            SysConfiguration approveIouSlip = new SysConfiguration(key: 'mis.accounting.approveIouSlip', value: accAdmin ? accAdmin.id : 0)
            approveIouSlip.description = 'determines the roles who can approve IOU Slip (e.g. -3,12)'
            approveIouSlip.message = 'Logged in user is not authorized to approve IOU slip'
            approveIouSlip.pluginId = AccPluginConnector.PLUGIN_ID
            approveIouSlip.companyId = companyId
            approveIouSlip.transactionCode = 'ApproveAccIouSlipActionService, AccCreateTestDataImplActionService, AccDeleteTestDataImplActionService'
            approveIouSlip.save()

            SysConfiguration cancelVoucher = new SysConfiguration(key: 'mis.accounting.cancelVoucher', value: accAdmin ? accAdmin.id : 0)
            cancelVoucher.description = 'determines the roles who can cancel voucher (e.g. -3,12)'
            cancelVoucher.message = 'Logged in user is not authorized to cancel voucher'
            cancelVoucher.pluginId = AccPluginConnector.PLUGIN_ID
            cancelVoucher.companyId = companyId
            cancelVoucher.transactionCode = 'AccCreateTestDataImplActionService, AccDeleteTestDataImplActionService, CancelAccVoucherActionService, CheckRoleTagLibActionService'
            cancelVoucher.save()

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * insert default SysConfiguration into database of Inventory plugin when application starts with bootstrap
     */
    public boolean createDefaultInvSysConfig(long companyId) {
        try {
            AppUser invAdmin = appUserService.findByLoginIdAndCompanyId('invadmin@athena.com', companyId)

            SysConfiguration adjustInvConsumption = new SysConfiguration(key: 'mis.inventory.adjustConsumption', value: invAdmin ? invAdmin.id : 0)
            adjustInvConsumption.description = 'determines the roles who can do adjustment of inv consumption (e.g. -3,12)'
            adjustInvConsumption.message = 'Logged in user is not authorized to adjust consumption'
            adjustInvConsumption.pluginId = InvPluginConnector.PLUGIN_ID
            adjustInvConsumption.companyId = companyId
            adjustInvConsumption.transactionCode = 'InvCreateTestDataImplActionService, InvDeleteTestDataImplActionService, AdjustmentForInvConsumptionActionService'
            adjustInvConsumption.save()

            SysConfiguration approveInvConsumption = new SysConfiguration(key: 'mis.inventory.approveConsumption', value: invAdmin ? invAdmin.id : 0)
            approveInvConsumption.description = 'determines the roles who can approve inv consumption (e.g. -3,12)'
            approveInvConsumption.message = 'Logged in user is not authorized to approve consumption'
            approveInvConsumption.pluginId = InvPluginConnector.PLUGIN_ID
            approveInvConsumption.companyId = companyId
            approveInvConsumption.transactionCode = 'InvCreateTestDataImplActionService, InvDeleteTestDataImplActionService, ApproveForInvInventoryConsumptionDetailsActionService, CheckRoleTagLibActionService'
            approveInvConsumption.save()

            SysConfiguration reverseAdjustInvConsumption = new SysConfiguration(key: 'mis.inventory.reverseAdjustConsumption', value: invAdmin ? invAdmin.id : 0)
            reverseAdjustInvConsumption.description = 'determines the roles who can do reverse adjustment of inv consumption (e.g. -3,12)'
            reverseAdjustInvConsumption.message = 'Logged in user is not authorized to do reverse adjustment of consumption'
            reverseAdjustInvConsumption.pluginId = InvPluginConnector.PLUGIN_ID
            reverseAdjustInvConsumption.companyId = companyId
            reverseAdjustInvConsumption.transactionCode = 'InvCreateTestDataImplActionService, InvDeleteTestDataImplActionService, ReverseAdjustmentForInvConsumptionActionService'
            reverseAdjustInvConsumption.save()
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * insert default SysConfiguration into database of Procurement plugin when application starts with bootstrap
     */
    public boolean createDefaultProcSysConfig(long companyId) {
        try {
            AppUser procAdmin = appUserService.findByLoginIdAndCompanyId('procadmin@athena.com', companyId)

            SysConfiguration approvePRAsDirector = new SysConfiguration(key: 'mis.procurement.approvePRAsDirector', value: procAdmin ? procAdmin.id : 0)
            approvePRAsDirector.description = 'determines the roles who can approve PR as Director (e.g. -3,12)'
            approvePRAsDirector.message = 'Logged in user is not authorized to approve PR as Director'
            approvePRAsDirector.pluginId = ProcPluginConnector.PLUGIN_ID
            approvePRAsDirector.companyId = companyId
            approvePRAsDirector.transactionCode = 'ProcCreateTestDataImplActionService, ProcDeleteTestDataImplActionService, ApproveProcPRForDashBoardActionService, ApproveProcPurchaseRequestActionService, CheckRoleTagLibActionService'
            approvePRAsDirector.save()

            SysConfiguration approvePRAsPD = new SysConfiguration(key: 'mis.procurement.approvePRAsPD', value: procAdmin ? procAdmin.id : 0)
            approvePRAsPD.description = 'determines the roles who can approve PR as Project Director (e.g. -3,12)'
            approvePRAsPD.message = 'Logged in user is not authorized to approve PR as Project Director'
            approvePRAsPD.pluginId = ProcPluginConnector.PLUGIN_ID
            approvePRAsPD.companyId = companyId
            approvePRAsPD.transactionCode = 'ProcCreateTestDataImplActionService, ProcDeleteTestDataImplActionService, ApproveProcPRForDashBoardActionService, ApproveProcPurchaseRequestActionService, CheckRoleTagLibActionService'
            approvePRAsPD.save()

            SysConfiguration approvePOAsDirector = new SysConfiguration(key: 'mis.procurement.approvePOAsDirector', value: procAdmin ? procAdmin.id : 0)
            approvePOAsDirector.description = 'determines the roles who can approve PO as Director (e.g. -3,12)'
            approvePOAsDirector.message = 'Logged in user is not authorized to approve PO as Director'
            approvePOAsDirector.pluginId = ProcPluginConnector.PLUGIN_ID
            approvePOAsDirector.companyId = companyId
            approvePOAsDirector.transactionCode = 'ProcCreateTestDataImplActionService, ProcDeleteTestDataImplActionService, ApproveProcPOForDashBoardActionService, ApproveProcPurchaseOrderActionService, CheckRoleTagLibActionService'
            approvePOAsDirector.save()

            SysConfiguration approvePOAsPD = new SysConfiguration(key: 'mis.procurement.approvePOAsPD', value: procAdmin ? procAdmin.id : 0)
            approvePOAsPD.description = 'determines the roles who can approve PO as Project Director (e.g. -3,12)'
            approvePOAsPD.message = 'Logged in user is not authorized to approve PO as Project Director'
            approvePOAsPD.pluginId = ProcPluginConnector.PLUGIN_ID
            approvePOAsPD.companyId = companyId
            approvePOAsPD.transactionCode = 'ProcCreateTestDataImplActionService, ProcDeleteTestDataImplActionService, ApproveProcPOForDashBoardActionService, ApproveProcPurchaseOrderActionService, CheckRoleTagLibActionService'
            approvePOAsPD.save()
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return false
        }
    }

    /**
     * insert default SysConfiguration into database of ProjectTrack plugin when application starts with bootstrap
     */
    public void createDefaultPtSysConfig(long companyId) {
        try {
            AppUser ptAdmin = appUserService.findByLoginIdAndCompanyId('ptadmin@athena.com', companyId)

            SysConfiguration removeBacklogFromSprint = new SysConfiguration(key: 'mis.projectTrack.removeBacklogFromSprint', value: ptAdmin ? ptAdmin.id : 0)
            removeBacklogFromSprint.description = 'determines the roles who can remove defined backlog with owner from sprint (e.g. -3,12)'
            removeBacklogFromSprint.message = 'Logged in user is not authorized to remove backlog with owner from sprint'
            removeBacklogFromSprint.pluginId = PtPluginConnector.PLUGIN_ID
            removeBacklogFromSprint.companyId = companyId
            removeBacklogFromSprint.transactionCode = 'DeletePtBackLogForSprintActionService'
            removeBacklogFromSprint.save()

            SysConfiguration deleteBug = new SysConfiguration(key: 'mis.projectTrack.deleteBug', value: ptAdmin ? ptAdmin.id : 0)
            deleteBug.description = 'determines the roles who can delete bug except the author (e.g. -3,12)'
            deleteBug.message = 'Logged in user is not authorized to delete bug'
            deleteBug.pluginId = PtPluginConnector.PLUGIN_ID
            deleteBug.companyId = companyId
            deleteBug.transactionCode = 'ShowPtBugActionService'
            deleteBug.save()

            SysConfiguration updateBug = new SysConfiguration(key: 'mis.projectTrack.updateBug', value: ptAdmin ? ptAdmin.id : 0)
            updateBug.description = 'determines the roles who can update bug except the author (e.g. -3,12)'
            updateBug.message = 'Logged in user is not authorized to update bug'
            updateBug.pluginId = PtPluginConnector.PLUGIN_ID
            updateBug.companyId = companyId
            updateBug.transactionCode = 'UpdatePtBugActionService'
            updateBug.save()

            SysConfiguration approveChangeRequest = new SysConfiguration(key: 'mis.projectTrack.approveChangeRequest', value: ptAdmin ? ptAdmin.id : 0)
            approveChangeRequest.description = 'determines the roles who can approve change request (e.g. -3,12)'
            approveChangeRequest.message = 'Logged in user is not authorized to approve change request'
            approveChangeRequest.pluginId = PtPluginConnector.PLUGIN_ID
            approveChangeRequest.companyId = companyId
            approveChangeRequest.transactionCode = 'UpdatePtChangeRequestActionService'
            approveChangeRequest.save()

            SysConfiguration rolesAsSoftwareEngineer = new SysConfiguration(key: 'mis.projectTrack.rolesAsSoftwareEngineer', value: ptAdmin ? ptAdmin.id : 0)
            rolesAsSoftwareEngineer.description = 'determines the roles who are software engineer'
            rolesAsSoftwareEngineer.pluginId = PtPluginConnector.PLUGIN_ID
            rolesAsSoftwareEngineer.companyId = companyId
            rolesAsSoftwareEngineer.transactionCode = 'PtCreateTestDataImplActionService, PtDeleteTestDataImplActionService, GetDropDownPtOwnerTagLibActionService'
            rolesAsSoftwareEngineer.save()
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * insert default SysConfiguration into database of ExchangeHouse plugin when application starts with bootstrap
     */
    public boolean createDefaultDataForExh(long companyId) {
        try {
            SysConfiguration sysConfiguration2 = new SysConfiguration(key: 'mis.exchangehouse.EvaluateRegFeeOnLocalCurrency', value: 'true')
            sysConfiguration2.description = """
            Determine if Regular fee will be evaluated based on local/foreign currency.
            true= local(AUD,GBP etc.), false=foreign(BDT). default is true.
        """
            sysConfiguration2.pluginId = ExchangeHousePluginConnector.PLUGIN_ID
            sysConfiguration2.companyId = companyId
            sysConfiguration2.transactionCode = 'ExhCreateTaskForAgentActionService, ExhCreateTaskForAgentForSarbActionService, ExhCreateTaskForCashierActionService, ExhCreateTaskForCashierForSarbActionService, ExhCreateTaskForCustomerActionService, ExhCreateTaskForCustomerForSarbActionService, ExhGetFeeAndCommissionForAgentTaskActionService, ExhGetFeeAndCommissionForTaskActionService, ExhUpdateCustomerTaskForCashierActionService, ExhUpdateCustomerTaskForCashierForSarbActionService, ExhUpdateTaskForAgentActionService, ExhUpdateTaskForAgentForSarbActionService, ExhUpdateTaskForCashierActionService, ExhUpdateTaskForCashierForSarbActionService, ExhUpdateTaskForCustomerActionService, ExhUpdateTaskForCustomerForSarbActionService, UpdateTaskForSarbReplaceTaskActionService'
            sysConfiguration2.message = "Failed to get regular fee"
            sysConfiguration2.save()

            SysConfiguration sysConfiguration4 = new SysConfiguration(key: 'mis.exchangehouse.verifyCustomerSanction', value: 'false')
            sysConfiguration4.description = """
            Check if sanction verification is must
            for customer true = must verify; false = do not verify if config not found then default value is false
        """
            sysConfiguration4.pluginId = ExchangeHousePluginConnector.PLUGIN_ID
            sysConfiguration4.companyId = companyId
            sysConfiguration4.transactionCode = 'ExhShowCustomerActionService, ExhShowCustomerForAgentActionService, ExhShowCustomerForAgentForSarbActionService, ExhShowCustomerForSarbActionService, UpdateExhCustomerProfileActionService, UpdateExhCustomerProfileForSarbActionService, ExhCreateTaskForAgentActionService, ExhCreateTaskForAgentForSarbActionService, ExhCreateTaskForCashierActionService, ExhCreateTaskForCashierForSarbActionService, ExhCreateTaskForCustomerActionService, ExhCreateTaskForCustomerForSarbActionService, ExhSendTaskToExchangeHouseActionService, ExhSendTaskToExhForCustomerActionService, ExhUpdateCustomerTaskForCashierActionService, ExhUpdateCustomerTaskForCashierForSarbActionService, ExhUpdateTaskForAgentActionService, ExhUpdateTaskForAgentForSarbActionService, ExhUpdateTaskForCashierActionService' +
                    ', ExhUpdateTaskForCashierForSarbActionService, ExhUpdateTaskForCustomerActionService, ExhUpdateTaskForCustomerForSarbActionService'
            sysConfiguration4.message = "Required"
            sysConfiguration4.save()

            SysConfiguration sysConfiguration5 = new SysConfiguration(key: 'mis.exchangehouse.verifyBeneficiarySanction', value: 'false')
            sysConfiguration5.description = """
            Check if sanction verification is must for
            beneficiary true = must verify; false = do not verify; if config not found then default value is false.
        """
            sysConfiguration5.pluginId = ExchangeHousePluginConnector.PLUGIN_ID
            sysConfiguration5.companyId = companyId
            sysConfiguration5.transactionCode = 'ExhApproveBeneficiaryForCashierActionService, ExhShowBeneficiaryActionService, ExhShowBeneficiaryForAgentActionService, ExhCreateTaskForAgentActionService, ExhCreateTaskForAgentForSarbActionService, ExhCreateTaskForCashierActionService' +
                    ', ExhCreateTaskForCashierForSarbActionService, ExhCreateTaskForCustomerForSarbActionService, ExhSendTaskToExchangeHouseActionService, ExhSendTaskToExhForCustomerActionService, ExhUpdateCustomerTaskForCashierActionService, ExhUpdateCustomerTaskForCashierForSarbActionService, ExhUpdateTaskForAgentActionService, ExhUpdateTaskForAgentForSarbActionService, ExhUpdateTaskForCashierActionService, ExhUpdateTaskForCashierForSarbActionService, ExhUpdateTaskForCustomerActionService, ExhUpdateTaskForCustomerForSarbActionService'
            sysConfiguration5.message = "Required"
            sysConfiguration5.save()

            SysConfiguration sysConfiguration6 = new SysConfiguration(key: 'mis.exchangehouse.hasPayPointIntegration', value: 'false')
            sysConfiguration6.description = """
            Check if company has integration with payPoint
        """
            sysConfiguration6.pluginId = ExchangeHousePluginConnector.PLUGIN_ID
            sysConfiguration6.companyId = companyId
            sysConfiguration6.transactionCode = 'ShowExhTaskForCashierActionService'
            sysConfiguration6.message = "Make Payment"
            sysConfiguration6.save()

            SysConfiguration sysConfiguration7 = new SysConfiguration(key: 'mis.exchangehouse.customerSurnameRequired', value: 'true')
            sysConfiguration7.description = """
            Check if customer surname is required. true = is required; false = is optional. If config not found then default value is false.
        """
            sysConfiguration7.pluginId = ExchangeHousePluginConnector.PLUGIN_ID
            sysConfiguration7.companyId = companyId
            sysConfiguration7.transactionCode = 'ExhShowCustomerActionService, ExhShowCustomerForAgentActionService, ExhShowCustomerForAgentForSarbActionService, ExhShowCustomerForSarbActionService, ShowExhCustomerProfileActionService, ShowExhCustomerProfileForSarbActionService'
            sysConfiguration7.message = "Required"
            sysConfiguration7.save()

            SysConfiguration sysConfiguration8 = new SysConfiguration(key: 'mis.exchangehouse.validatePostalCode', value: 'false')
            sysConfiguration8.description = """
            Check if company has to validate postal code. true = will be validated; false = no validation. If config not found then default value is false.
        """
            sysConfiguration8.pluginId = ExchangeHousePluginConnector.PLUGIN_ID
            sysConfiguration8.companyId = companyId
            sysConfiguration8.transactionCode = 'ExhCreateCustomerForAgentActionService, ExhCreateCustomerForAgentForSarbActionService, ExhCreateCustomerForCashierActionService, ExhCreateCustomerForCashierForSarbActionService, ExhUpdateCustomerForAgentActionService, ExhUpdateCustomerForAgentForSarbActionService, ExhUpdateCustomerForCashierActionService, ExhUpdateCustomerForCashierForSarbActionService, UpdateExhCustomerProfileActionService, UpdateExhCustomerProfileForSarbActionService'
            sysConfiguration8.message = "Invalid Postal Code."
            sysConfiguration8.save()

            SysConfiguration sysConfiguration9 = new SysConfiguration(key: 'mis.exchangehouse.photoIdNoRequired', value: 'true')
            sysConfiguration9.description = """
            Check if customer photoId is required. true = is required; false = is optional. If config not found then default value is false.
        """
            sysConfiguration9.pluginId = ExchangeHousePluginConnector.PLUGIN_ID
            sysConfiguration9.companyId = companyId
            sysConfiguration9.transactionCode = 'ExhShowCustomerActionService, ExhShowCustomerForAgentActionService, ExhShowCustomerForAgentForSarbActionService, ExhShowCustomerForSarbActionService, ShowExhCustomerProfileActionService, ShowExhCustomerProfileForSarbActionService'
            sysConfiguration9.message = "Required"
            sysConfiguration9.save()

            SysConfiguration sysConfiguration10 = new SysConfiguration(key: 'mis.exchangehouse.beneficiarySurnameRequired', value: 'true')
            sysConfiguration10.description = """
            Check if beneficiary surname is required. true = is required; false = is optional. If config not found then default value is false.
        """
            sysConfiguration10.pluginId = ExchangeHousePluginConnector.PLUGIN_ID
            sysConfiguration10.companyId = companyId
            sysConfiguration10.transactionCode = 'ExhShowApprovedBeneficiariesForCustomerActionService, ExhShowBeneficiaryActionService, ExhShowBeneficiaryForAgentActionService, ExhShowNewBeneficiariesForCustomerActionService'
            sysConfiguration10.message = "Required"
            sysConfiguration10.save()


            SysConfiguration sysConfiguration11 = new SysConfiguration(key: 'mis.exchangehouse.customerDeclarationAmountRequired', value: 'false')
            sysConfiguration11.description = """
            Check if declaration amount is required. true = is required; false = is optional. If config not found then default value is false.
        """
            sysConfiguration11.pluginId = ExchangeHousePluginConnector.PLUGIN_ID
            sysConfiguration11.companyId = companyId
            sysConfiguration11.transactionCode = 'ExhShowCustomerActionService, ExhShowCustomerForAgentActionService, ExhShowCustomerForAgentForSarbActionService, ExhShowCustomerForSarbActionService, ShowExhCustomerProfileActionService, ShowExhCustomerProfileForSarbActionService' +
                    ', ExhCreateTaskForAgentActionService, ExhCreateTaskForAgentForSarbActionService, ExhCreateTaskForCashierActionService, ExhCreateTaskForCashierForSarbActionService, ExhCreateTaskForCustomerActionService, ExhCreateTaskForCustomerForSarbActionService, ExhUpdateCustomerTaskForCashierActionService, ExhUpdateCustomerTaskForCashierForSarbActionService, ExhUpdateTaskForAgentActionService, ExhUpdateTaskForAgentForSarbActionService, ExhUpdateTaskForCashierActionService, ExhUpdateTaskForCashierForSarbActionService, ExhUpdateTaskForCustomerActionService, ExhUpdateTaskForCustomerForSarbActionService, UpdateTaskForSarbReplaceTaskActionService'
            sysConfiguration11.message = "Total amount exceeds the customer's declaration amount"
            sysConfiguration11.save()

            SysConfiguration sysConfiguration12 = new SysConfiguration(key: 'mis.exchangehouse.customerAddressVerificationRequired', value: 'false')
            sysConfiguration12.description = """
            Check if customer address verification is required. true = is required; false = is optional. If config not found then default value is false.
        """
            sysConfiguration12.pluginId = ExchangeHousePluginConnector.PLUGIN_ID
            sysConfiguration12.companyId = companyId
            sysConfiguration12.transactionCode = 'ExhShowCustomerActionService, ExhShowCustomerForAgentActionService, ExhShowCustomerForAgentForSarbActionService, ExhShowCustomerForSarbActionService'
            sysConfiguration12.message = "Required"
            sysConfiguration12.save()

            SysConfiguration sysConfiguration13 = new SysConfiguration(key: 'mis.exchangehouse.maxAmountPerTransaction', value: '10000')
            sysConfiguration13.description = """
            Local currency Amount limit per transaction. If config not found then throw error/exception.
        """
            sysConfiguration13.pluginId = ExchangeHousePluginConnector.PLUGIN_ID
            sysConfiguration13.companyId = companyId
            sysConfiguration13.transactionCode = 'ExhCreateTaskForAgentActionService, ExhCreateTaskForAgentForSarbActionService, ExhCreateTaskForCashierActionService, ExhCreateTaskForCashierForSarbActionService, ExhCreateTaskForCustomerActionService, ExhCreateTaskForCustomerForSarbActionService, ExhUpdateCustomerTaskForCashierActionService, ExhUpdateCustomerTaskForCashierForSarbActionService' +
                    ', ExhUpdateTaskForAgentActionService, ExhUpdateTaskForAgentForSarbActionService, ExhUpdateTaskForCashierActionService, ExhUpdateTaskForCashierForSarbActionService, ExhUpdateTaskForCustomerActionService, ExhUpdateTaskForCustomerForSarbActionService, UpdateTaskForSarbReplaceTaskActionService'
            sysConfiguration13.message = "Task amount exceeds limit. Max limit: "
            sysConfiguration13.save()

            SysConfiguration sysConfiguration15 = new SysConfiguration(key: 'mis.exchangehouse.perCustomerMaxTransactionAmountIn30Days', value: '25000')
            sysConfiguration15.description = """
            Check monthly transaction limit per customer in 30 days. Any negative value will disable this config. Default value 25000.
        """
            sysConfiguration15.pluginId = ExchangeHousePluginConnector.PLUGIN_ID
            sysConfiguration15.companyId = companyId
            sysConfiguration15.transactionCode = 'ExhCreateTaskForAgentActionService, ExhCreateTaskForAgentForSarbActionService, ExhCreateTaskForCashierActionService, ExhCreateTaskForCashierForSarbActionService, ExhCreateTaskForCustomerActionService, ExhCreateTaskForCustomerForSarbActionService, ExhSendTaskToExchangeHouseActionService, ExhSendTaskToExhForCustomerActionService, ExhUpdateCustomerTaskForCashierActionService' +
                    ', ExhUpdateCustomerTaskForCashierForSarbActionService, ExhUpdateTaskForAgentActionService, ExhUpdateTaskForAgentForSarbActionService, ExhUpdateTaskForCashierActionService, ExhUpdateTaskForCashierForSarbActionService, ExhUpdateTaskForCustomerActionService, ExhUpdateTaskForCustomerForSarbActionService'
            sysConfiguration15.message = 'Monthly amount limit exceeds for this customer'
            sysConfiguration15.save()

            SysConfiguration sysConfiguration16 = new SysConfiguration(key: 'mis.exchangehouse.perCustomerMaxTransactionAmountIn90Days', value: '35000')
            sysConfiguration16.description = """
            Check monthly transaction limit per customer in 90 days. Any negative value will disable this config. Default value 35000.
        """
            sysConfiguration16.pluginId = ExchangeHousePluginConnector.PLUGIN_ID
            sysConfiguration16.companyId = companyId
            sysConfiguration16.transactionCode = 'ExhCreateTaskForAgentActionService, ExhCreateTaskForAgentForSarbActionService, ExhCreateTaskForCashierActionService, ExhCreateTaskForCashierForSarbActionService, ExhCreateTaskForCustomerActionService, ExhCreateTaskForCustomerForSarbActionService, ExhSendTaskToExchangeHouseActionService, ExhSendTaskToExhForCustomerActionService' +
                    ', ExhUpdateCustomerTaskForCashierActionService, ExhUpdateCustomerTaskForCashierForSarbActionService, ExhUpdateTaskForAgentActionService, ExhUpdateTaskForAgentForSarbActionService, ExhUpdateTaskForCashierActionService, ExhUpdateTaskForCashierForSarbActionService, ExhUpdateTaskForCustomerActionService, ExhUpdateTaskForCustomerForSarbActionService'
            sysConfiguration16.message = 'Ninety days amount limit exceeds for this customer'
            sysConfiguration16.save()

            SysConfiguration sysConfiguration17 = new SysConfiguration(key: 'mis.exchangehouse.perCustomerMaxTransactionAmountIn365Days', value: '110000')
            sysConfiguration17.description = """
            Check monthly transaction limit per customer in 365 days. Any negative value will disable this config. Default value 110000.
        """
            sysConfiguration17.pluginId = ExchangeHousePluginConnector.PLUGIN_ID
            sysConfiguration17.companyId = companyId
            sysConfiguration17.transactionCode = 'ExhCreateTaskForAgentActionService, ExhCreateTaskForAgentForSarbActionService, ExhCreateTaskForCashierActionService, ExhCreateTaskForCashierForSarbActionService, ExhCreateTaskForCustomerActionService, ExhCreateTaskForCustomerForSarbActionService, ExhSendTaskToExchangeHouseActionService, ExhSendTaskToExhForCustomerActionService' +
                    ', ExhUpdateCustomerTaskForCashierActionService, ExhUpdateCustomerTaskForCashierForSarbActionService, ExhUpdateTaskForAgentActionService, ExhUpdateTaskForAgentForSarbActionService, ExhUpdateTaskForCashierActionService, ExhUpdateTaskForCashierForSarbActionService, ExhUpdateTaskForCustomerActionService, ExhUpdateTaskForCustomerForSarbActionService'
            sysConfiguration17.message = 'Yearly amount limit exceeds for this customer'
            sysConfiguration17.save()

            SysConfiguration sysConfiguration18 = new SysConfiguration(key: 'mis.exchangehouse.verifyCustomerDuplication', value: 'true')
            sysConfiguration18.description = """
           Check if duplicate verification is must for customer. true = must verify; false = don't verify. If config not found then default value is false.
        """
            sysConfiguration18.pluginId = ExchangeHousePluginConnector.PLUGIN_ID
            sysConfiguration18.companyId = companyId
            sysConfiguration18.transactionCode = 'ExhShowCustomerActionService, ExhShowCustomerForAgentActionService, ExhShowCustomerForAgentForSarbActionService, ExhShowCustomerForSarbActionService, UpdateExhCustomerProfileActionService, UpdateExhCustomerProfileForSarbActionService'
            sysConfiguration18.message = 'Required'
            sysConfiguration18.save()

            SysConfiguration sysConfiguration19 = new SysConfiguration(key: 'mis.exchangehouse.perCustomerMaxNoOfTransactionsIn30Days', value: '-1')
            sysConfiguration19.description = """
            Check if max no. of transaction within 30 days verification is must for customer. Any negative value will disable this config. Default value -1.
        """
            sysConfiguration19.pluginId = ExchangeHousePluginConnector.PLUGIN_ID
            sysConfiguration19.companyId = companyId
            sysConfiguration19.transactionCode = 'ExhCreateTaskForAgentActionService, ExhCreateTaskForAgentForSarbActionService, ExhCreateTaskForCashierActionService, ExhCreateTaskForCashierForSarbActionService, ExhCreateTaskForCustomerActionService, ExhCreateTaskForCustomerForSarbActionService, ExhSendTaskToExchangeHouseActionService, ExhSendTaskToExhForCustomerActionService'
            sysConfiguration19.message = 'Monthly no. of task limit exceeds for this customer'
            sysConfiguration19.save()

            SysConfiguration sysConfiguration20 = new SysConfiguration(key: 'mis.exchangehouse.perCustomerMaxNoOfTransactionsIn90Days', value: '-1')
            sysConfiguration20.description = """
            Check if max no. of transaction within 90 days verification is must for customer. Any negative value will disable this config. Default value -1.
        """
            sysConfiguration20.pluginId = ExchangeHousePluginConnector.PLUGIN_ID
            sysConfiguration20.companyId = companyId
            sysConfiguration20.transactionCode = 'ExhCreateTaskForAgentActionService, ExhCreateTaskForAgentForSarbActionService, ExhCreateTaskForCashierActionService, ExhCreateTaskForCashierForSarbActionService, ExhCreateTaskForCustomerActionService, ExhCreateTaskForCustomerForSarbActionService, ExhSendTaskToExchangeHouseActionService, ExhSendTaskToExhForCustomerActionService'
            sysConfiguration20.message = 'Ninety days no. of task limit exceeds for this customer'
            sysConfiguration20.save()

            SysConfiguration sysConfiguration21 = new SysConfiguration(key: 'mis.exchangehouse.perCustomerMaxNoOfTransactionsIn365Days', value: '-1')
            sysConfiguration21.description = """
            Check if max no. of transaction within 365 days verification is must for customer Any negative value will disable this config. Default value -1.
        """
            sysConfiguration21.pluginId = ExchangeHousePluginConnector.PLUGIN_ID
            sysConfiguration21.companyId = companyId
            sysConfiguration21.transactionCode = 'ExhCreateTaskForAgentActionService, ExhCreateTaskForAgentForSarbActionService, ExhCreateTaskForCashierActionService, ExhCreateTaskForCashierForSarbActionService, ExhCreateTaskForCustomerActionService, ExhCreateTaskForCustomerForSarbActionService, ExhSendTaskToExchangeHouseActionService, ExhSendTaskToExhForCustomerActionService'
            sysConfiguration21.message = 'Yearly no. of task limit exceeds for this customer'
            sysConfiguration21.save()

            SysConfiguration sysConfiguration22 = new SysConfiguration(key: 'mis.exchangehouse.customerMinimumAge', value: '18')
            sysConfiguration22.description = """
             Value denotes minimum age of a customer.
        """
            sysConfiguration22.pluginId = ExchangeHousePluginConnector.PLUGIN_ID
            sysConfiguration22.companyId = companyId
            sysConfiguration22.transactionCode = 'ExhCreateCustomerForAgentActionService, ExhCreateCustomerForAgentForSarbActionService, ExhCreateCustomerForCashierActionService, ExhCreateCustomerForCashierForSarbActionService, ExhUpdateCustomerForAgentActionService, ExhUpdateCustomerForAgentForSarbActionService, ExhUpdateCustomerForCashierActionService, ExhUpdateCustomerForCashierForSarbActionService, UpdateExhCustomerProfileActionService, UpdateExhCustomerProfileForSarbActionService'
            sysConfiguration22.message = "Customer age is below 18 years"
            sysConfiguration22.save()

            SysConfiguration sysConfiguration23 = new SysConfiguration(key: 'mis.exchangehouse.isRequiredBeneficiaryPhone', value: 'true')
            sysConfiguration23.description = """
                Check if beneficiary phone no is required or not. If true = must verify; false = do not verify. If config not found then default value is true.
        """
            sysConfiguration23.pluginId = ExchangeHousePluginConnector.PLUGIN_ID
            sysConfiguration23.companyId = companyId
            sysConfiguration23.transactionCode = 'ExhApproveBeneficiaryForCashierActionService, ExhShowApprovedBeneficiariesForCustomerActionService, ExhShowBeneficiaryActionService, ExhShowBeneficiaryForAgentActionService, ExhShowNewBeneficiariesForCustomerActionService, ValidateExhBeneficiaryActionService'
            sysConfiguration23.message = "Required"
            sysConfiguration23.save()

            SysConfiguration sysConfiguration24 = new SysConfiguration(key: 'mis.exchangehouse.maxNumberOfBeneficiaryPerCustomer', value: '5')
            sysConfiguration24.description = """
                Value denotes maximum number of beneficiary per customer.
        """
            sysConfiguration24.pluginId = ExchangeHousePluginConnector.PLUGIN_ID
            sysConfiguration24.companyId = companyId
            sysConfiguration24.transactionCode = 'ExhCreateBeneficiaryActionService, ExhCreateBeneficiaryForAgentActionService, ExhCreateBeneficiaryForCustomerActionService, ExhCreateCustomerBeneficiaryMappingActionService'
            sysConfiguration24.message = "Maximum no. of beneficiary limit exceeds"
            sysConfiguration24.save()

            SysConfiguration sysConfiguration25 = new SysConfiguration(key: 'mis.exchangehouse.maxNumberOfLinkedCustomerPerBeneficiary', value: '5')
            sysConfiguration25.description = """
                Value denotes maximum number of linked customer per beneficiary.
        """
            sysConfiguration25.pluginId = ExchangeHousePluginConnector.PLUGIN_ID
            sysConfiguration25.companyId = companyId
            sysConfiguration25.transactionCode = 'ExhCreateCustomerBeneficiaryMappingActionService'
            sysConfiguration25.message = "Maximum no. of linked customer limit exceeds"
            sysConfiguration25.save()

            SysConfiguration sysConfiguration26 = new SysConfiguration(key: 'mis.exchangehouse.invoiceTermsAndConditions',
                    value: 'I hereby declare that the amount paid to you was or is not derived obtained through any illegal means or transactions, ' +
                            'and I accept the terms & conditions as set out in the framework contract which is available at the premises and on the website.')
            sysConfiguration26.description = """
                Value to be shown in invoice report.
        """
            sysConfiguration26.pluginId = ExchangeHousePluginConnector.PLUGIN_ID
            sysConfiguration26.companyId = companyId
            sysConfiguration26.transactionCode = 'ExhDownloadTaskInvoiceReportActionService, ExhDownloadTaskInvoiceReportForAgentActionService, ExhDownloadTaskInvoiceReportForCustomerActionService, ExhDownloadTaskInvoiceReportForOtherBankActionService, GetExhTaskInvoiceForAgentTaglibActionService, GetExhTaskInvoiceForCustomerTagLibActionService, GetExhTaskInvoiceTagLibActionService, GetExhTaskInvoiceTaglibForOtherBankActionService, ExhCreateTaskForCashierActionService, ExhCreateTaskForCashierForSarbActionService, ExhProcessPaypointPRNActionService, ExhUpdateTaskForCashierActionService, ExhUpdateTaskForCashierForSarbActionService'
            sysConfiguration26.save()

            SysConfiguration sanctionHmTreasuryUrl = new SysConfiguration(key: 'mis.exchangehouse.sanctionHmTreasuryUpdateUrl', value: 'http://hmt-sanctions.s3.amazonaws.com/sanctionsconlist.csv')
            sanctionHmTreasuryUrl.description = 'url to update HM treasury sanction list'
            sanctionHmTreasuryUrl.pluginId = ExchangeHousePluginConnector.PLUGIN_ID
            sanctionHmTreasuryUrl.companyId = companyId
            sanctionHmTreasuryUrl.transactionCode = 'ExhAutoUpdateSanctionListJobActionService'
            sanctionHmTreasuryUrl.message = "Failed to resolve HM Treasury sanction url"
            sanctionHmTreasuryUrl.save()

            SysConfiguration sanctionOfacSdnUrl = new SysConfiguration(key: 'mis.exchangehouse.sanctionOfacSndUpdateUrl', value: 'http://www.treasury.gov/ofac/downloads/sdn.csv')
            sanctionOfacSdnUrl.description = 'url to update ofac sdn sanction list'
            sanctionOfacSdnUrl.pluginId = ExchangeHousePluginConnector.PLUGIN_ID
            sanctionOfacSdnUrl.companyId = companyId
            sanctionOfacSdnUrl.transactionCode = 'ExhAutoUpdateSanctionListJobActionService'
            sanctionOfacSdnUrl.message = "Failed to resolve OFAC SDN sanction url"
            sanctionOfacSdnUrl.save()

            SysConfiguration sanctionOfacAddUrl = new SysConfiguration(key: 'mis.exchangehouse.sanctionOfacAddUpdateUrl', value: 'http://www.treasury.gov/ofac/downloads/add.csv')
            sanctionOfacAddUrl.description = 'url to update ofac add sanction list'
            sanctionOfacAddUrl.pluginId = ExchangeHousePluginConnector.PLUGIN_ID
            sanctionOfacAddUrl.companyId = companyId
            sanctionOfacAddUrl.transactionCode = 'ExhAutoUpdateSanctionListJobActionService'
            sanctionOfacAddUrl.message = "Failed to resolve OFAC ADD sanction url"
            sanctionOfacAddUrl.save()

            SysConfiguration sanctionOfacAltUrl = new SysConfiguration(key: 'mis.exchangehouse.sanctionOfacAltUpdateUrl', value: 'http://www.treasury.gov/ofac/downloads/alt.csv')
            sanctionOfacAltUrl.description = 'url to update ofac alt sanction list'
            sanctionOfacAltUrl.pluginId = ExchangeHousePluginConnector.PLUGIN_ID
            sanctionOfacAltUrl.companyId = companyId
            sanctionOfacAltUrl.transactionCode = 'ExhAutoUpdateSanctionListJobActionService'
            sanctionOfacAltUrl.message = "Failed to resolve OFAC ALT sanction url"
            sanctionOfacAltUrl.save()

            SysConfiguration creditCardCharge = new SysConfiguration(key: 'mis.exchangehouse.creditCardCharge', value: '2')
            creditCardCharge.description = 'Value to calculate credit card charge '
            creditCardCharge.pluginId = ExchangeHousePluginConnector.PLUGIN_ID
            creditCardCharge.companyId = companyId
            creditCardCharge.transactionCode = 'ExhCreateTaskForCashierActionService, ExhCreateTaskForCashierForSarbActionService, ExhGetFeeAndCommissionForTaskActionService, ExhUpdateTaskForCashierActionService, ExhUpdateTaskForCashierForSarbActionService, ShowExhTaskForCashierActionService'
            creditCardCharge.save()

            SysConfiguration maxNoTranThirtyDaysPerCompany = new SysConfiguration(key: 'mis.exchangehouse.maxNoOfTransactionsInThirtyDaysPerCompany', value: '-1')
            maxNoTranThirtyDaysPerCompany.description = 'Check if verification is must for max no. of transactions of a company within 30 days. Any negative value will disable this config. Default value -1.'
            maxNoTranThirtyDaysPerCompany.pluginId = ExchangeHousePluginConnector.PLUGIN_ID
            maxNoTranThirtyDaysPerCompany.companyId = companyId
            maxNoTranThirtyDaysPerCompany.transactionCode = 'ExhCreateTaskForAgentActionService, ExhCreateTaskForAgentForSarbActionService, ExhCreateTaskForCashierActionService, ExhCreateTaskForCashierForSarbActionService, ExhCreateTaskForCustomerActionService, ExhCreateTaskForCustomerForSarbActionService'
            maxNoTranThirtyDaysPerCompany.message = "Per company monthly transaction limit exceeds"
            maxNoTranThirtyDaysPerCompany.save()

            SysConfiguration sendTaskToBankUrl = new SysConfiguration(key: 'mis.exchangehouse.sendTaskToArmsUrl', value: 'http://127.0.0.1:8080/rmsRest/receiveTaskFromExh')
            sendTaskToBankUrl.description = 'url of arms web service for sending task to bank'
            sendTaskToBankUrl.pluginId = ExchangeHousePluginConnector.PLUGIN_ID
            sendTaskToBankUrl.companyId = companyId
            sendTaskToBankUrl.transactionCode = 'ExhSendExceptionalTaskToBankWithRestActionService, ExhSendTaskToBankWithRestActionService'
            sendTaskToBankUrl.message = "Task send to ARMS failed"
            sendTaskToBankUrl.save()

            SysConfiguration sendTaskToBank = new SysConfiguration(key: 'mis.exchangehouse.enableSendToBank', value: 'true')
            sendTaskToBank.description = 'Check whether task can be sent to bank or not. false = disabled, true = enabled'
            sendTaskToBank.pluginId = ExchangeHousePluginConnector.PLUGIN_ID
            sendTaskToBank.companyId = companyId
            sendTaskToBank.transactionCode = 'ExhSendExceptionalTaskToBankWithRestActionService, ExhSendTaskToBankWithRestActionService, ShowAgentTaskForAdminActionService, ShowCustomerTaskForAdminActionService, ShowExhTaskForAdminActionService'
            sendTaskToBank.message = 'Send to bank is disable'
            sendTaskToBank.save()

            SysConfiguration cusAmountLimitPerDay = new SysConfiguration(key: 'mis.exchangehouse.customerAmountLimitPerDay', value: '5000')
            cusAmountLimitPerDay.description = 'Customer amount limit per day. Negative value will disable this config'
            cusAmountLimitPerDay.pluginId = ExchangeHousePluginConnector.PLUGIN_ID
            cusAmountLimitPerDay.companyId = companyId
            cusAmountLimitPerDay.transactionCode = 'ExhCreateTaskForAgentActionService, ExhCreateTaskForAgentForSarbActionService, ExhCreateTaskForCashierActionService, ExhCreateTaskForCashierForSarbActionService, ExhCreateTaskForCustomerActionService, ExhCreateTaskForCustomerForSarbActionService, ExhSendTaskToExchangeHouseActionService, ExhSendTaskToExhForCustomerActionService' +
                    ', ExhUpdateCustomerTaskForCashierActionService, ExhUpdateCustomerTaskForCashierForSarbActionService, ExhUpdateTaskForAgentActionService, ExhUpdateTaskForAgentForSarbActionService, ExhUpdateTaskForCashierActionService, ExhUpdateTaskForCashierForSarbActionService, ExhUpdateTaskForCustomerActionService, ExhUpdateTaskForCustomerForSarbActionService'
            cusAmountLimitPerDay.message = 'Customer amount limit per day exceeds'
            cusAmountLimitPerDay.save()

            SysConfiguration enableCalendarMonth = new SysConfiguration(key: 'mis.exchangehouse.amountLimitPerCustomerWithinCalendarMonth', value: '10000')
            enableCalendarMonth.description = 'Value to define customer transaction amount limit within calendar month. Negative value will disable this config'
            enableCalendarMonth.pluginId = ExchangeHousePluginConnector.PLUGIN_ID
            enableCalendarMonth.companyId = companyId
            enableCalendarMonth.transactionCode = 'ExhCreateTaskForAgentActionService, ExhCreateTaskForAgentForSarbActionService, ExhCreateTaskForCashierActionService, ExhCreateTaskForCashierForSarbActionService, ExhCreateTaskForCustomerActionService, ExhCreateTaskForCustomerForSarbActionService, ExhSendTaskToExchangeHouseActionService, ExhSendTaskToExhForCustomerActionService' +
                    ', ExhUpdateCustomerTaskForCashierActionService, ExhUpdateCustomerTaskForCashierForSarbActionService, ExhUpdateTaskForAgentActionService, ExhUpdateTaskForAgentForSarbActionService, ExhUpdateTaskForCashierActionService, ExhUpdateTaskForCashierForSarbActionService, ExhUpdateTaskForCustomerActionService, ExhUpdateTaskForCustomerForSarbActionService'
            enableCalendarMonth.message = 'Transaction amount limit per customer within a calendar month'
            enableCalendarMonth.save()

            SysConfiguration duplicateVerifyWithPhotoId = new SysConfiguration(key: 'mis.exchangehouse.verifyCustomerDuplicationWithPhotoId', value: 'false')
            duplicateVerifyWithPhotoId.description = 'Check Customer duplication with photo id. false = disabled, true = enabled; if config not found default false'
            duplicateVerifyWithPhotoId.pluginId = ExchangeHousePluginConnector.PLUGIN_ID
            duplicateVerifyWithPhotoId.companyId = companyId
            duplicateVerifyWithPhotoId.transactionCode = 'ExhShowCustomerActionService, ExhShowCustomerForAgentActionService, ExhShowCustomerForAgentForSarbActionService, ExhShowCustomerForSarbActionService, UpdateExhCustomerProfileActionService, UpdateExhCustomerProfileForSarbActionService, VerifyDuplicateExhCustomerActionService, ViewDuplicateExhCustomerDetailsActionService'
            duplicateVerifyWithPhotoId.message = "Please enter customer photo id no and type"
            duplicateVerifyWithPhotoId.save()

            SysConfiguration roleIdForCreateCustomerUser = new SysConfiguration(key: 'mis.exchangehouse.roleIdForCreateCustomerUser', value: '-202')
            roleIdForCreateCustomerUser.description = 'role id to create customer user. 0 or incorrect value will disable the feature'
            roleIdForCreateCustomerUser.pluginId = ExchangeHousePluginConnector.PLUGIN_ID
            roleIdForCreateCustomerUser.companyId = companyId
            roleIdForCreateCustomerUser.transactionCode = 'ExhCreateForCustomerUserActionService'
            roleIdForCreateCustomerUser.message = "No role found to map with customer user"
            roleIdForCreateCustomerUser.save()

            SysConfiguration roleIdForCashierDropDown = new SysConfiguration(key: 'mis.exchangehouse.roleIdForCashierDropDown', value: '-201')
            roleIdForCashierDropDown.description = 'role id for cashier dropdown. 0 or incorrect value will disable the feature'
            roleIdForCashierDropDown.pluginId = ExchangeHousePluginConnector.PLUGIN_ID
            roleIdForCashierDropDown.companyId = companyId
            roleIdForCashierDropDown.transactionCode = 'GetDropDownExhCashierTagLibActionService'
            roleIdForCashierDropDown.save()

            SysConfiguration beneficiaryCountry = new SysConfiguration(key: 'mis.exchangehouse.defaultBeneficiaryCountryId', value: '1')
            beneficiaryCountry.description = 'default country id for beneficiary. No default country for 0 or incorrect value'
            beneficiaryCountry.pluginId = ExchangeHousePluginConnector.PLUGIN_ID
            beneficiaryCountry.companyId = companyId
            beneficiaryCountry.transactionCode = 'ShowSysConfigTagLibActionService'
            beneficiaryCountry.save()

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createDefaultDataForArms(long companyId) {
        try {
            SysConfiguration sanctionHmTreasuryUrl = new SysConfiguration(key: 'mis.arms.sanctionHmTreasuryUpdateUrl', value: 'http://hmt-sanctions.s3.amazonaws.com/sanctionsconlist.csv')
            sanctionHmTreasuryUrl.description = 'url to update HM treasury sanction list'
            sanctionHmTreasuryUrl.pluginId = ArmsPluginConnector.PLUGIN_ID
            sanctionHmTreasuryUrl.companyId = companyId
            sanctionHmTreasuryUrl.transactionCode = 'RmsAutoUpdateSanctionListJobActionService'
            sanctionHmTreasuryUrl.message = "Failed to resolve HM Treasury sanction url"
            sanctionHmTreasuryUrl.save()

            SysConfiguration sanctionOfacSdnUrl = new SysConfiguration(key: 'mis.arms.sanctionOfacSndUpdateUrl', value: 'http://www.treasury.gov/ofac/downloads/sdn.csv')
            sanctionOfacSdnUrl.description = 'url to update ofac sdn sanction list'
            sanctionOfacSdnUrl.pluginId = ArmsPluginConnector.PLUGIN_ID
            sanctionOfacSdnUrl.companyId = companyId
            sanctionOfacSdnUrl.transactionCode = 'RmsAutoUpdateSanctionListJobActionService'
            sanctionOfacSdnUrl.message = "Failed to resolve OFAC SDN sanction url"
            sanctionOfacSdnUrl.save()

            SysConfiguration sanctionOfacAddUrl = new SysConfiguration(key: 'mis.arms.sanctionOfacAddUpdateUrl', value: 'http://www.treasury.gov/ofac/downloads/add.csv')
            sanctionOfacAddUrl.description = 'url to update ofac add sanction list'
            sanctionOfacAddUrl.pluginId = ArmsPluginConnector.PLUGIN_ID
            sanctionOfacAddUrl.companyId = companyId
            sanctionOfacAddUrl.transactionCode = 'RmsAutoUpdateSanctionListJobActionService'
            sanctionOfacAddUrl.message = "Failed to resolve OFAC ADD sanction url"
            sanctionOfacAddUrl.save()

            SysConfiguration sanctionOfacAltUrl = new SysConfiguration(key: 'mis.arms.sanctionOfacAltUpdateUrl', value: 'http://www.treasury.gov/ofac/downloads/alt.csv')
            sanctionOfacAltUrl.description = 'url to update ofac alt sanction list'
            sanctionOfacAltUrl.pluginId = ArmsPluginConnector.PLUGIN_ID
            sanctionOfacAltUrl.companyId = companyId
            sanctionOfacAltUrl.transactionCode = 'RmsAutoUpdateSanctionListJobActionService'
            sanctionOfacAltUrl.message = "Failed to resolve OFAC ALT sanction url"
            sanctionOfacAltUrl.save()

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createDefaultSarbSysConfig(long companyId) {
        try {
            SysConfiguration sysConfiguration1 = new SysConfiguration(key: 'mis.sarb.prod.urlSendTaskToSarb', value: 'https://sarbdexqp.resbank.co.za:444/SARBDEX/sarbdex.aspx?Method=file')
            sysConfiguration1.description = """
            Url of sent task to sarb (Production Mode).
        """
            sysConfiguration1.pluginId = SarbPluginConnector.PLUGIN_ID
            sysConfiguration1.companyId = companyId
            sysConfiguration1.transactionCode = 'SendSarbCurrencyConversionToSarbActionService, SendCancelTaskToSarbActionService, SendNewTaskToSarbActionService, SendRefundTaskToSarbActionService, SendReplaceTaskToSarbActionService'
            sysConfiguration1.message = 'Error in connection. Check production url of sent task to sarb'
            sysConfiguration1.save()

            SysConfiguration sysConfiguration2 = new SysConfiguration(key: 'mis.sarb.prod.urlRetrieveReference', value: 'https://sarbdexqp.resbank.co.za:444/SARBDEX/getmsgbysarbref.asp')
            sysConfiguration2.description = """
            Url of retrieve reference (Production Mode).
        """
            sysConfiguration2.pluginId = SarbPluginConnector.PLUGIN_ID
            sysConfiguration2.companyId = companyId
            sysConfiguration2.transactionCode = 'RetrieveAgainSarbCurrencyConversionResponseActionService, RetrieveSarbCurrencyConversionResponseActionService, RetrieveAgainSarbTaskResponseActionService, RetrieveSarbTaskResponseActionService'
            sysConfiguration2.message = 'Error in connection. Check production url of retrieve reference'
            sysConfiguration2.save()

            SysConfiguration sysConfiguration3 = new SysConfiguration(key: 'mis.sarb.prod.urlRetrieveResponse', value: 'https://sarbdexqp.resbank.co.za:444/SARBDEX/getmsgbysarbref.asp')
            sysConfiguration3.description = """
            url of retrieve response (Production Mode)
        """
            sysConfiguration3.pluginId = SarbPluginConnector.PLUGIN_ID
            sysConfiguration3.companyId = companyId
            sysConfiguration3.transactionCode = 'RetrieveAgainSarbCurrencyConversionResponseActionService, RetrieveSarbCurrencyConversionResponseActionService, RetrieveAgainSarbTaskResponseActionService, RetrieveSarbTaskResponseActionService'
            sysConfiguration3.message = 'Error in connection. Check production url of retrieve response'
            sysConfiguration3.save()

            SysConfiguration sysConfiguration4 = new SysConfiguration(key: 'mis.sarb.dev.urlSendTaskToSarb', value: 'https://sarbdexqp.resbank.co.za:444/SARBDEX/sarbdex.aspx?Method=file')
            sysConfiguration4.description = """
            Url of sent task to sarb (Development Mode).
        """
            sysConfiguration4.pluginId = SarbPluginConnector.PLUGIN_ID
            sysConfiguration4.companyId = companyId
            sysConfiguration4.transactionCode = 'SendSarbCurrencyConversionToSarbActionService, SendCancelTaskToSarbActionService, SendNewTaskToSarbActionService, SendRefundTaskToSarbActionService, SendReplaceTaskToSarbActionService'
            sysConfiguration4.message = 'Error in connection. Check development url of sent task to sarb'
            sysConfiguration4.save()

            SysConfiguration sysConfiguration5 = new SysConfiguration(key: 'mis.sarb.dev.urlRetrieveReference', value: 'https://sarbdexqp.resbank.co.za:444/SARBDEX/getmsgbysarbref.asp')
            sysConfiguration5.description = """
            Url of retrieve reference (Development Mode).
        """
            sysConfiguration5.pluginId = SarbPluginConnector.PLUGIN_ID
            sysConfiguration5.companyId = companyId
            sysConfiguration5.transactionCode = 'RetrieveAgainSarbCurrencyConversionResponseActionService, RetrieveSarbCurrencyConversionResponseActionService, RetrieveAgainSarbTaskResponseActionService, RetrieveSarbTaskResponseActionService'
            sysConfiguration5.message = 'Error in connection. Check development url of retrieve reference'
            sysConfiguration5.save()

            SysConfiguration sysConfiguration6 = new SysConfiguration(key: 'mis.sarb.dev.urlRetrieveResponse', value: 'https://sarbdexqp.resbank.co.za:444/SARBDEX/getmsgbysarbref.asp')
            sysConfiguration6.description = """
            url of retrieve response (Development Mode)
        """
            sysConfiguration6.pluginId = SarbPluginConnector.PLUGIN_ID
            sysConfiguration6.companyId = companyId
            sysConfiguration6.transactionCode = 'RetrieveAgainSarbCurrencyConversionResponseActionService, RetrieveSarbCurrencyConversionResponseActionService, RetrieveAgainSarbTaskResponseActionService, RetrieveSarbTaskResponseActionService'
            sysConfiguration6.message = 'Error in connection. Check development url of retrieve response'
            sysConfiguration6.save()

            SysConfiguration sysConfiguration7 = new SysConfiguration(key: 'mis.sarb.sarbUserName', value: 'SOUTHEASTBANK_FINSURV')
            sysConfiguration7.description = """
            Sarb UserName.
        """
            sysConfiguration7.pluginId = SarbPluginConnector.PLUGIN_ID
            sysConfiguration7.companyId = companyId
            sysConfiguration7.transactionCode = 'RetrieveAgainSarbCurrencyConversionResponseActionService, RetrieveSarbCurrencyConversionResponseActionService, SendSarbCurrencyConversionToSarbActionService, RetrieveAgainSarbTaskResponseActionService, RetrieveSarbTaskResponseActionService, SendCancelTaskToSarbActionService, SendNewTaskToSarbActionService, SendRefundTaskToSarbActionService, SendReplaceTaskToSarbActionService'
            sysConfiguration7.save()

            SysConfiguration sysConfiguration8 = new SysConfiguration(key: 'mis.sarb.sarbPassword', value: 'athena@321')
            sysConfiguration8.description = """
            Sarb Password.
        """
            sysConfiguration8.pluginId = SarbPluginConnector.PLUGIN_ID
            sysConfiguration8.companyId = companyId
            sysConfiguration8.transactionCode = 'RetrieveAgainSarbCurrencyConversionResponseActionService, RetrieveSarbCurrencyConversionResponseActionService, SendSarbCurrencyConversionToSarbActionService, RetrieveAgainSarbTaskResponseActionService, RetrieveSarbTaskResponseActionService, SendCancelTaskToSarbActionService, SendNewTaskToSarbActionService, SendRefundTaskToSarbActionService, SendReplaceTaskToSarbActionService'
            sysConfiguration8.save()

            SysConfiguration sysConfiguration9 = new SysConfiguration(key: 'mis.sarb.branchCode', value: '205')
            sysConfiguration9.description = """
           SARB branch code. Default=205.
        """
            sysConfiguration9.pluginId = SarbPluginConnector.PLUGIN_ID
            sysConfiguration9.companyId = companyId
            sysConfiguration9.transactionCode = 'SendSarbCurrencyConversionToSarbActionService, DownloadSarbAllReportableTransactionsActionService, DownloadSarbAllReportableTransactionsCsvActionService, DownloadSarbCurrencyConversionActionService, DownloadSarbRefundedReportActionService, DownloadSarbReplacedReportActionService, DownloadSarbTransactionBalanceActionService, DownloadSarbTransactionBalanceCsvActionService, DownloadSarbTransactionDetailsActionService, ' +
                    'DownloadSarbTransactionDetailsCsvActionService, DownloadSarbUnsubmittedCsvReportActionService, DownloadSarbUnsubmittedReportActionService, DownloadSarbWarningDetailsActionService, ListSarbTransactionBalanceActionService, SendNewTaskToSarbActionService, SendRefundTaskToSarbActionService, SendReplaceTaskToSarbActionService'
            sysConfiguration9.save()

            SysConfiguration sysConfiguration10 = new SysConfiguration(key: 'mis.sarb.isProductionMode', value: 'false')
            sysConfiguration10.description = """
            Determine if is development/production mode; true = Production, false = development. Default = false.
        """
            sysConfiguration10.pluginId = SarbPluginConnector.PLUGIN_ID
            sysConfiguration10.companyId = companyId
            sysConfiguration10.transactionCode = 'SarbSysConfigCacheService'
            sysConfiguration10.save()

            SysConfiguration sysConfiguration11 = new SysConfiguration(key: 'mis.sarb.sarbFileNameSequence', value: '0')
            sysConfiguration11.description = """
            SARB file counter (only for custom purpose); Config will be considered If value > 0, ignored otherwise.
        """
            sysConfiguration11.pluginId = SarbPluginConnector.PLUGIN_ID
            sysConfiguration11.companyId = companyId
            sysConfiguration11.transactionCode = 'SendSarbCurrencyConversionToSarbActionService, SendCancelTaskToSarbActionService, SendNewTaskToSarbActionService, SendRefundTaskToSarbActionService, SendReplaceTaskToSarbActionService'
            sysConfiguration11.save()

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public void createDefaultDataElSysConfig(long companyId) {
        try {

            SysConfiguration showUpcomingCourse = new SysConfiguration(key: 'mis.elearning.showUpcomingCourseOnHomePage', value: 'true')
            showUpcomingCourse.description = 'Determine upcoming course on el home page show or not. Default value true, means show.'
            showUpcomingCourse.pluginId = ELearningPluginConnector.PLUGIN_ID
            showUpcomingCourse.companyId = companyId
            showUpcomingCourse.transactionCode = ''
            showUpcomingCourse.save()

            SysConfiguration showUserPointer = new SysConfiguration(key: 'mis.elearning.showUserPointerOnDashboard', value: 'true')
            showUserPointer.description = 'Determine user pointer on el dashboard show or not. default value true, means show'
            showUserPointer.pluginId = ELearningPluginConnector.PLUGIN_ID
            showUserPointer.companyId = companyId
            showUserPointer.transactionCode = 'ShowElUserPointerOnDashboardTagLibActionService'
            showUserPointer.save()

            SysConfiguration showElForum = new SysConfiguration(key: 'mis.elearning.showForum', value: 'true')
            showElForum.description = 'Determine el forum show or not. default value true, means show'
            showElForum.pluginId = ELearningPluginConnector.PLUGIN_ID
            showElForum.companyId = companyId
            showElForum.transactionCode = ''
            showElForum.save()
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public void createDefaultDataDocSysConfig(long companyId) {
        try {

            SysConfiguration categorySysConfiguration = new SysConfiguration(key: 'mis.document.categoryLabel', value: 'Department')
            categorySysConfiguration.description = 'Determine Category Label, Default Value is Category'
            categorySysConfiguration.pluginId = DocumentPluginConnector.PLUGIN_ID
            categorySysConfiguration.companyId = companyId
            categorySysConfiguration.transactionCode = 'DocCategoryDetailsTaglibActionService, ShowSysConfigTagLibActionService, DocSysConfigurationCacheService'
            categorySysConfiguration.save()

            SysConfiguration subCategorySysConfiguration = new SysConfiguration(key: 'mis.document.subCategoryLabel', value: 'Course')
            subCategorySysConfiguration.description = 'Determine Sub Category Label, Default Value is Sub Category'
            subCategorySysConfiguration.pluginId = DocumentPluginConnector.PLUGIN_ID
            subCategorySysConfiguration.companyId = companyId
            subCategorySysConfiguration.transactionCode = 'DocCountSubCategoryTaglibActionService, ShowSysConfigTagLibActionService, DocSysConfigurationCacheService'
            subCategorySysConfiguration.save()

            SysConfiguration suCategoryDocumentPath = new SysConfiguration(key: 'mis.document.subCategoryDocumentPath', value: 'D:\\Document')
            suCategoryDocumentPath.description = 'Determine Sub Category Document Path, Default Value is D:\\Document'
            suCategoryDocumentPath.pluginId = DocumentPluginConnector.PLUGIN_ID
            suCategoryDocumentPath.companyId = companyId
            suCategoryDocumentPath.transactionCode = 'GenerateDocDataIndexActionService, CreateDocDocumentAudioActionService, CreateDocDocumentFileActionService, CreateDocDocumentImageActionService, CreateDocDocumentVideoActionService, UpdateDocDocumentAudioActionService, UpdateDocDocumentFileActionService, UpdateDocDocumentImageActionService, UpdateDocDocumentVideoActionService, UpdateDocDocumentTrashAudioFileActionService, UpdateDocDocumentTrashFileActionService, UpdateDocDocumentTrashImageFileActionService, UpdateDocDocumentTrashVideoFileActionService'
            suCategoryDocumentPath.save()

            SysConfiguration docExpireSendInvitation = new SysConfiguration(key: 'mis.document.expirationDurationInvitedMembers', value: '7')
            docExpireSendInvitation.description = 'Determine Document send invitation expiration date.Default Value is 7 day'
            docExpireSendInvitation.message = 'Time to accept invitation has expired'
            docExpireSendInvitation.pluginId = DocumentPluginConnector.PLUGIN_ID
            docExpireSendInvitation.companyId = companyId
            docExpireSendInvitation.transactionCode = 'SendInvitationDocInvitedMembersActionService, ApplyDocMemberJoinRequestForCategoryActionService, ApplyDocMemberJoinRequestForSubCategoryActionService'
            docExpireSendInvitation.save()

            // Followings are used for OCR parse related
            SysConfiguration ocrServerUrl = new SysConfiguration(key: 'mis.document.ocrIntegration.serverUrl', value: 'http://cloud.ocrsdk.com')
            ocrServerUrl.description = 'URL to access the OCR cloud API. Default value is http://cloud.ocrsdk.com'
            ocrServerUrl.pluginId = DocumentPluginConnector.PLUGIN_ID
            ocrServerUrl.companyId = companyId
            ocrServerUrl.transactionCode = 'RetrieveDocDocumentOCRContentJobActionService'
            ocrServerUrl.save()

            SysConfiguration ocrAppId = new SysConfiguration(key: 'mis.document.ocrIntegration.applicationID', value: 'athenaDmsId')
            ocrAppId.description = 'URL to access the OCR cloud API. Default value is: testId'
            ocrAppId.pluginId = DocumentPluginConnector.PLUGIN_ID
            ocrAppId.companyId = companyId
            ocrAppId.transactionCode = 'RetrieveDocDocumentOCRContentJobActionService'
            ocrAppId.save()

            SysConfiguration ocrAppPwd = new SysConfiguration(key: 'mis.document.ocrIntegration.password', value: 'nJrnCGZDPIrhmU0MayCBuQWX')
            ocrAppPwd.description = 'Password to access the OCR cloud API. Default value is: nJrnCGZDPIrhmU0MayCBuQWX'
            ocrAppPwd.pluginId = DocumentPluginConnector.PLUGIN_ID
            ocrAppPwd.companyId = companyId
            ocrAppPwd.transactionCode = 'RetrieveDocDocumentOCRContentJobActionService'
            ocrAppPwd.save()

            SysConfiguration cloudViewerKey = new SysConfiguration(key: 'mis.document.cloudViewerKey', value: 'XH8iUDapE2FjD2upmJgVi1vgL_-4X7W7gNsi2P1AYlQnUjIYlGuK3XbMM6kZHU7L')
            cloudViewerKey.description = 'Cloud Viewer Key to access document viewer instance'
            cloudViewerKey.pluginId = DocumentPluginConnector.PLUGIN_ID
            cloudViewerKey.companyId = companyId
            cloudViewerKey.transactionCode = 'ShowForViewDocDocumentFileActionService, ShowForViewDocDocumentImageActionService'
            cloudViewerKey.save()

            SysConfiguration cloudViewerUrl = new SysConfiguration(key: 'mis.document.cloudViewerUrl', value: 'http://api.accusoft.com/v1/viewer/?key=')
            cloudViewerUrl.description = 'URL to access document cloud viewer instance'
            cloudViewerUrl.pluginId = DocumentPluginConnector.PLUGIN_ID
            cloudViewerUrl.companyId = companyId
            cloudViewerUrl.transactionCode = 'ShowForViewDocDocumentFileActionService, ShowForViewDocDocumentImageActionService'
            cloudViewerUrl.save()

            SysConfiguration isVersionSupported = new SysConfiguration(key: 'mis.document.isVersionSupported', value: 'true')
            isVersionSupported.description = 'Determine document version supported or not. default value true, means supported'
            isVersionSupported.pluginId = DocumentPluginConnector.PLUGIN_ID
            isVersionSupported.companyId = companyId
            isVersionSupported.transactionCode = ''
            isVersionSupported.save()
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public void createDefaultDataDplSysConfig(long companyId) {
        try {
            SysConfiguration dataFeedSourceLocation = new SysConfiguration(key: 'mis.dataPipeLine.dataFeed.offlineSourceLocation', value: 'D:\\DplFileSource')
            dataFeedSourceLocation.description = 'Indicate offline data-feed file source location where user can upload their file. Default value is D:\\DplFileSource'
            dataFeedSourceLocation.pluginId = DataPipeLinePluginConnector.PLUGIN_ID
            dataFeedSourceLocation.companyId = companyId
            dataFeedSourceLocation.transactionCode = 'ListDplOfflineDataFeedSourceFileActionService, ReceiveDplOfflineDataFeedFileActionService, RegisterDplOfflineDataFeedFileActionService'
            dataFeedSourceLocation.save()

            SysConfiguration dataFeedTargetLocation = new SysConfiguration(key: 'mis.dataPipeLine.dataFeed.offlineTargetLocation', value: 'D:\\mount_122')
            dataFeedTargetLocation.description = 'Indicate offline data-feed file target location where received file will saved or uploaded. Default value is D:\\mount_122'
            dataFeedTargetLocation.pluginId = DataPipeLinePluginConnector.PLUGIN_ID
            dataFeedTargetLocation.companyId = companyId
            dataFeedTargetLocation.transactionCode = 'CreateDplOfflineDataFeedActionService, UpdateDplOfflineDataFeedActionService, DeleteDplOfflineDataFeedFileFromTargetActionService, MoveToDataBucketDplOfflineDataFeedFileActionService, ReceiveDplOfflineDataFeedFileActionService, RegisterDplOfflineDataFeedFileActionService'
            dataFeedTargetLocation.save()

            SysConfiguration databaseMountLocation = new SysConfiguration(key: 'mis.dataPipeLine.offlineDataMountLocation', value: '/mnt/mount_122')
            databaseMountLocation.description = 'Provides mount location path for offline data feed of DB server. Default value is /mnt/mount_122'
            databaseMountLocation.pluginId = DataPipeLinePluginConnector.PLUGIN_ID
            databaseMountLocation.companyId = companyId
            databaseMountLocation.transactionCode = 'LoadDplOfflineDataFeedActionService'
            databaseMountLocation.save()

            SysConfiguration dplAwsAccessKeyId = new SysConfiguration(key: 'mis.dataPipeLine.awsAccessKeyId', value: 'AmazonAccessIdExample')
            dplAwsAccessKeyId.description = 'Determine Amazon Redshift Access Key Id.'
            dplAwsAccessKeyId.pluginId = DataPipeLinePluginConnector.PLUGIN_ID
            dplAwsAccessKeyId.companyId = companyId
            dplAwsAccessKeyId.transactionCode = 'ExecuteDplDataTransferFileImportActionService, LoadDplOfflineDataFeedActionService, LoadDataFeedDplOfflineDataFeedFileActionService'
            dplAwsAccessKeyId.save()

            SysConfiguration dplAwsSecretAccessKey = new SysConfiguration(key: 'mis.dataPipeLine.awsSecretAccessKey', value: 'AmazonSecretKeyExample')
            dplAwsSecretAccessKey.description = 'Determine Amazon Redshift Secret Access Key.'
            dplAwsSecretAccessKey.pluginId = DataPipeLinePluginConnector.PLUGIN_ID
            dplAwsSecretAccessKey.companyId = companyId
            dplAwsSecretAccessKey.transactionCode = 'ExecuteDplDataTransferFileImportActionService, LoadDplOfflineDataFeedActionService, LoadDataFeedDplOfflineDataFeedFileActionService'
            dplAwsSecretAccessKey.save()

            SysConfiguration dplAwsBaseUrl = new SysConfiguration(key: 'mis.dataPipeLine.redshift.s3FileMountLocation', value: 's3://habi-data-feed/')
            dplAwsBaseUrl.description = 'Determine Redshift file mount location where files will be copied.'
            dplAwsBaseUrl.pluginId = DataPipeLinePluginConnector.PLUGIN_ID
            dplAwsBaseUrl.companyId = companyId
            dplAwsBaseUrl.transactionCode = 'ExecuteDplDataTransferFileImportActionService'
            dplAwsBaseUrl.save()

            SysConfiguration docDataBucketMountLoc = new SysConfiguration(key: 'mis.dataPipeLine.OfflineDataFeed.s3BucketMountLocation', value: 'D:\\BucketMountLocation')
            docDataBucketMountLoc.description = 'Determine offline data-feed file mounted target location where copied file will saved. Default value is D:\\BucketMountLocation'
            docDataBucketMountLoc.pluginId = DataPipeLinePluginConnector.PLUGIN_ID
            docDataBucketMountLoc.companyId = companyId
            docDataBucketMountLoc.transactionCode = 'DeleteDplOfflineDataFeedFileFromDataBucketActionService, MoveToDataBucketDplOfflineDataFeedFileActionService'
            docDataBucketMountLoc.save()

            SysConfiguration docDataBucketName = new SysConfiguration(key: 'mis.dataPipeLine.dataFeed.bucketName', value: 'D:\\BucketServer')
            docDataBucketName.description = 'Offline data-feed bucket server location where copied file will saved and able to load in Database. Default value is D:\\BucketServer'
            docDataBucketName.pluginId = DataPipeLinePluginConnector.PLUGIN_ID
            docDataBucketName.companyId = companyId
            docDataBucketName.transactionCode = 'LoadDataFeedDplOfflineDataFeedFileActionService'
            docDataBucketName.save()

            SysConfiguration cdcConvertCommand = new SysConfiguration(key: 'mis.dataPipeLine.mySql.logConvertCommand', value: 'mysqlbinlog ${workingDir}/${logName} -v')
            cdcConvertCommand.description = 'Convert cdc binary file to text file command both Master and Slave db instance.'
            cdcConvertCommand.pluginId = DataPipeLinePluginConnector.PLUGIN_ID
            cdcConvertCommand.companyId = companyId
            cdcConvertCommand.transactionCode = 'DplProcessMSSqlLogJobActionService,DplProcessMySqlLogJobActionService'
            cdcConvertCommand.save()

            SysConfiguration cdcCopyCommand = new SysConfiguration(key: 'mis.dataPipeLine.mySql.logCopyCommand', value: 'mysqlbinlog --read-from-remote-server --host=${host} --raw --port=${port} --user=${user} --password=${pwd} ${logName} --result-file=${workingDir}/')
            cdcCopyCommand.description = 'Indicate cdc file copy command for master db instance.'
            cdcCopyCommand.pluginId = DataPipeLinePluginConnector.PLUGIN_ID
            cdcCopyCommand.companyId = companyId
            cdcCopyCommand.transactionCode = 'DplProcessMSSqlLogJobActionService, DplProcessMySqlLogJobActionService'
            cdcCopyCommand.save()

            SysConfiguration dataExportFileMountLocation = new SysConfiguration(key: 'mis.dataPipeLine.dplDataExport.fileMountLocation', value: 'D:\\FileMountLocation')
            dataExportFileMountLocation.description = 'File Path where Exported Csv File is saved.'
            dataExportFileMountLocation.pluginId = DataPipeLinePluginConnector.PLUGIN_ID
            dataExportFileMountLocation.companyId = companyId
            dataExportFileMountLocation.transactionCode = 'CreateDplDataTransferFileActionService, UpdateDplDataTransferFileActionService'
            dataExportFileMountLocation.save()


            SysConfiguration greenPlumFileMountLocation = new SysConfiguration(key: 'mis.dataPipeLine.greenPlum.fileMountLocation', value: '/greenPlumFileMountLocation/')
            greenPlumFileMountLocation.description = 'File path from which files will be copied'
            greenPlumFileMountLocation.pluginId = DataPipeLinePluginConnector.PLUGIN_ID
            greenPlumFileMountLocation.companyId = companyId
            greenPlumFileMountLocation.transactionCode = 'ExecuteDplDataTransferFileImportActionService'
            greenPlumFileMountLocation.save()

            String valueGreenPlumScript = """
                TRUNCATE table "\${tableName}";
                CREATE READABLE EXTERNAL TABLE "ext_\${tableName}" (LIKE "\${tableName}") LOCATION ('gpfdist://localhost:8080\${file_location}') FORMAT 'CSV' (HEADER QUOTE AS '"' DELIMITER AS ',' NULL AS 'null' ESCAPE AS '\\\\');
                INSERT INTO "\${tableName}" SELECT * FROM "ext_\${tableName}";
                Drop EXTERNAL TABLE "ext_\${tableName}";
                commit;
            """
            SysConfiguration greenPlumCopyScript = new SysConfiguration(key: 'mis.dataPipeLine.greenPlum.copyScriptCsv', value: valueGreenPlumScript)
            greenPlumCopyScript.description = 'Copy command script for Greenplum'
            greenPlumCopyScript.pluginId = DataPipeLinePluginConnector.PLUGIN_ID
            greenPlumCopyScript.companyId = companyId
            greenPlumCopyScript.transactionCode = 'ExecuteDplDataImportActionService'
            greenPlumCopyScript.save()

            String greenplumCopyScriptForTxt = """
                TRUNCATE table "\${tableName}";
                CREATE READABLE EXTERNAL TABLE "ext_\${tableName}" (LIKE "\${tableName}") LOCATION ('gpfdist://localhost:8080\${file_location}') FORMAT 'TEXT' (HEADER DELIMITER AS '|' NULL AS 'null' ESCAPE AS '\\\\');
                INSERT INTO "\${tableName}" SELECT * FROM "ext_\${tableName}";
                Drop EXTERNAL TABLE "ext_\${tableName}";
                commit;
            """
            SysConfiguration greenPlumCopyScriptTxt = new SysConfiguration(key: 'mis.dataPipeLine.greenPlum.copyScriptText', value: greenplumCopyScriptForTxt)
            greenPlumCopyScriptTxt.description = 'Copy command script for Greenplum'
            greenPlumCopyScriptTxt.pluginId = DataPipeLinePluginConnector.PLUGIN_ID
            greenPlumCopyScriptTxt.companyId = companyId
            greenPlumCopyScriptTxt.transactionCode = 'ExecuteDplDataImportActionService'
            greenPlumCopyScriptTxt.save()
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    @Override
    public void createDefaultSchema() {
        String keyIndex = "create unique index sys_configuration_key_company_id_idx on sys_configuration(lower(key), company_id);"
        executeSql(keyIndex)
    }

    private static final String UPDATE_SYS_CONFIG_FOR_TEST_DATA = """
        UPDATE sys_configuration
        SET value = :value
        WHERE key = :key
        AND company_id = :companyId
    """

    public void updateSysConfigForTestData(String key, String value, long companyId) {
        Map queryParams = [
                key      : key,
                value    : value,
                companyId: companyId
        ]
        executeUpdateSql(UPDATE_SYS_CONFIG_FOR_TEST_DATA, queryParams)
    }

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }
}
