package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.application.entity.AppSms
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Company
import com.athena.mis.integration.datapipeline.DataPipeLinePluginConnector
import com.athena.mis.integration.elearning.ELearningPluginConnector
import org.apache.log4j.Logger

/**
 * AppSmsService is used to handle only CRUD related object manipulation
 * (e.g. list, update etc.)
 */
class AppSmsService extends BaseDomainService {

    private Logger log = Logger.getLogger(getClass())

    TestDataModelService testDataModelService
    AppUserService appUserService
    CompanyService companyService

    Logger log = Logger.getLogger(getClass())

    @Override
    public void init() {
        domainClass = AppSms.class
    }

    /**
     * Method to get sms list
     * @return - list of SMS
     */
    @Override
    public List list() {
        return AppSms.list(sort: AppSms.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true)
    }

    private static final String DELETE_ALL = """
        DELETE FROM app_sms WHERE company_id = :companyId
    """

    /**
     * Delete all sms by companyId
     * @param companyId - id of company
     */
    public void deleteAllByCompanyId(long companyId) {
        Map queryParams = [
                companyId: companyId
        ]
        executeUpdateSql(DELETE_ALL, queryParams)
    }

    public List<AppSms> findAllByTransactionCodeAndCompanyIdAndIsActive(String transactionCode, long companyId, boolean isActive) {
        List smsList = AppSms.findAllByTransactionCodeAndCompanyIdAndIsActive(transactionCode, companyId, isActive, [readOnly: true])
        return smsList
    }

    public AppSms findByTransactionCodeAndCompanyIdAndIsActive(String transactionCode, long companyId, boolean isActive) {
        AppSms sms = AppSms.findByTransactionCodeAndCompanyIdAndIsActive(transactionCode, companyId, isActive, [readOnly: true])
        return sms
    }

    /**
     * Create default data for sms
     * @param companyId -id of company
     */
    public boolean createDefaultData(long companyId) {
        try {
            new AppSms(body: 'This is a TEST SMS', description: 'Test SMS', transactionCode: 'SendSmsActionService', companyId: companyId, hasSend: false,
                    isActive: true, recipients: '+8801511230055', isManualSend: true, pluginId: 1, controllerName: 'appSms', actionName: 'sendSms', isRequiredRecipients: false).save()
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Create default sms for accounting
     * @param companyId - id of company
     */
    public boolean createDefaultDataForAccounting(long companyId) {
        try {
            new AppSms(body: 'Cash Payment On ${currentDate}:\n' +
                    'Posted:${postedAmount}\n' +
                    'UnPosted:${unPostedAmount}', description: 'Get sum of voucher amount (Total of Pay Cash) on current date and send sms',
                    transactionCode: 'GetTotalOfPayCashActionService', companyId: companyId, isActive: true, recipients: '+8801511230055',
                    isManualSend: true, pluginId: 2, controllerName: 'accReport', actionName: 'retrieveTotalOfPayCash', hasSend: false, isRequiredRecipients: false).save()

            new AppSms(body: 'Cheque Payment On ${currentDate}:\n' +
                    'Posted:${postedAmount}\n' +
                    'UnPosted:${unPostedAmount}', description: 'Get sum of voucher amount (Total of Pay Cheque) on current date and send sms',
                    transactionCode: 'GetTotalOfPayChequeActionService', companyId: companyId, isActive: true, recipients: '+8801511230055',
                    isManualSend: true, pluginId: 2, controllerName: 'accReport', actionName: 'retrieveTotalOfPayCheque', hasSend: false, isRequiredRecipients: false).save()
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createDefaultDataForExh(long companyId) {
        try {
            new AppSms(body: '"Your Remittance trxn ("  + task.refNo + ") of BDT:"  + task.amountInForeignCurrency  +   " has been created\nPIN: " +\n' +
                    'task.pinNo + "\nBeneficiary: "   +   beneficiaryFullName +   "\n-SFSL(Ph:02077902434)"', transactionCode: 'ExhCreateTaskActionService_Cus_CashCollection', companyId: companyId, pluginId: 9,
                    isActive: false, isManualSend: false, description: 'SMS to customer on task(Cash Collection) create', hasSend: false, isRequiredRecipients: false).save()

            new AppSms(body: '"Your Remittance trxn ("   + task.refNo +   ") of BDT:"   +   task.amountInForeignCurrency  +  " has been created\nBank A/C: " +\n' +
                    ' beneficiary.accountNo  +  "\nBeneficiary: "  +   beneficiaryFullName +  "\n-SFSL(Ph:02077902434)"', transactionCode: 'ExhCreateTaskActionService_Cus_BankDeposit', companyId: companyId, pluginId: 9,
                    isActive: false, isManualSend: false, description: 'SMS to customer on task(Bank Deposit) create', hasSend: false, isRequiredRecipients: false).save()

            new AppSms(body: '"Your Remittance trxn ("  +  task.refNo + ") of BDT"  +   task.amountInForeignCurrency  +  " is ready for paying from " + \n' +
                    'outletInfo +  "\nHelpline: 016178029363"', transactionCode: 'ExhCreateTaskActionService_Ben_CashCollection', companyId: companyId, pluginId: 9,
                    isActive: false, isManualSend: false, description: 'SMS to beneficiary on task(Cash Collection) create', hasSend: false, isRequiredRecipients: false).save()

            new AppSms(body: '"Your Remittance trxn ("  +  task.refNo +  ") of BDT"  +  task.amountInForeignCurrency +  " for " + \n' +
                    'outletInfo  +  " has been transferred to Dhaka\nHelpline: 016178029363"', transactionCode: 'ExhCreateTaskActionService_Ben_BankDeposit', companyId: companyId, pluginId: 9,
                    isActive: false, isManualSend: false, description: 'SMS to beneficiary on task(Bank Deposit) create', hasSend: false, isRequiredRecipients: false).save()

            new AppSms(body: '"Your Remittance trxn ("  +  refNo +  ") is disbursed to "+ beneficiaryName +" from " +' +
                    'distributionPoint  +  " on "+ paidOn +" Helpline: +88016178029363"', transactionCode: 'ExhUpdateTaskStatusForRestActionService', companyId: companyId, pluginId: 9,
                    isActive: false, isManualSend: false, description: 'SMS to customer on task disbursed from arms', hasSend: false, isRequiredRecipients: false).save()

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createDefaultDataForArms(long companyId) {
        try {
            new AppSms(body: '"Your Remittance ("+ refOrPin +") of BDT:"+ amount +" is ready for paying now from " + mappingInfo + "."', transactionCode: 'ApproveRmsTaskActionService',
                    companyId: companyId, pluginId: 11, isActive: false, isManualSend: false, description: 'SMS to beneficiary on task approve event', recipients: '-', hasSend: false, isRequiredRecipients: false).save()

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public void createDefaultDataForDpl(long companyId) {
        try {
            new AppSms(body: 'Initial Data is Loaded in ${vendorName}, now you can configure ikOn to start CDC.', transactionCode: 'SendMailDplDataImportActionService',
                    companyId: companyId, pluginId: DataPipeLinePluginConnector.PLUGIN_ID, isActive: true, isManualSend: false, description: 'SMS send on data import event', recipients: '+8801511230055', hasSend: false, isRequiredRecipients: true).save()

            new AppSms(body: 'Initial Data is unloaded to ikOn system repository, now you can start loading to Target.', transactionCode: 'SendMailDplDataExportActionService',
                    companyId: companyId, pluginId: DataPipeLinePluginConnector.PLUGIN_ID, isActive: true, isManualSend: false, description: 'SMS send on data export event', recipients: '+8801511230055', hasSend: false, isRequiredRecipients: true).save()

        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public void createDefaultDataForElearning(long companyId) {
        try {
            new AppSms(body: 'Please check email to activate your e-learning account.', transactionCode: 'RegisterAppUserForElActionService',
                    companyId: companyId, pluginId: ELearningPluginConnector.PLUGIN_ID, isActive: true, isManualSend: false, description: 'SMS send on User Registration', recipients: null, hasSend: false, isRequiredRecipients: false).save()
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public void createTestData(long companyId) {
        Company company = (Company) companyService.read(companyId)
        String adminLoginId = 'appadmin@' + company.code.toLowerCase() + '.com'
        AppUser adminUser = appUserService.findByLoginIdAndCompanyId(adminLoginId, companyId)
        Map queryParams = [
                id       : testDataModelService.getNextIdForTestData(),
                version  : 0L,
                companyId: companyId,
                pluginId : 0L,
                updatedBy: 0L
        ]
        executeInsertSql("""
            INSERT INTO app_sms (id, version, body, transaction_code, company_id, has_send, is_active, is_required_recipients, is_manual_send,
            plugin_id, updated_by, role_id)
            VALUES (:id, :version, 'This is a TEST SMS', 'SendForComposeSmsActionService', :companyId, false,
                true, false, true, :pluginId, :updatedBy, '${adminUser.id}');
        """, queryParams)
    }

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }

    @Override
    public void createDefaultSchema() {}
}
