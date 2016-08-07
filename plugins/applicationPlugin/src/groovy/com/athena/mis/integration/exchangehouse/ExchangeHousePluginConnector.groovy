package com.athena.mis.integration.exchangehouse

import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.Company
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.entity.SystemEntity

public abstract class ExchangeHousePluginConnector extends PluginConnector {

    public static final String PLUGIN_NAME = "ExchangeHouse"
    public static final int PLUGIN_ID = 9
    public static final String PLUGIN_PREFIX = "EXH"

    // get plugin version
    public abstract int getPluginVersion()

    // init list after create, update and delete
    public abstract void initByType(long typeId)

    // get list of active SystemEntity object
    public abstract List<SystemEntity> listByIsActive(long typeId, long companyId)

    // read SystemEntity object by reserved id
    public abstract SystemEntity readByReservedId(long reservedId, long systemEntityTypeId, long companyId)

    //return customer object
    public abstract Object readCustomer(long id)

    public abstract void initExhSysConfigCacheUtility()
    // init bootstrap of exh plugin
    public abstract void bootStrap(Company company)

    public abstract List<Long> listTaskStatusForSarb()

    public abstract List<Long> listTaskStatusForExcludingSarb()

    public abstract SystemEntity readExhPaymentMethod(long id)

    // get system entity of reserved type (BANK DEPOSIT); used in serb xml generation
    public abstract SystemEntity readBankDepositPaymentMethod(long companyId)
    // get system entity of reserved type (Cash Collection); used in serb
    public abstract SystemEntity readCashCollectionPaymentMethod(long companyId)

    public abstract void initSession()

    public abstract Object readExhRemittancePurpose(long id)

    public abstract SystemEntity readExhPaidBy(long id)

    public abstract SystemEntity readExhTaskStatusRefund(long companyId)

    public abstract Map createExhTaskForRefundTask(long taskId, double refundAmount)

    public abstract Map updateExhTaskForReplaceTask(Map params)

    // read the exhTask object by id
    public abstract Object readTask(long taskId)

    //delete exhTask object by id
    public abstract boolean deleteTask(long taskId)

    //get exhAgentId
    public abstract long getExhAgentId()

    //get exhCustomerId
    public abstract long getExhCustomerId()

    //list systemEntity id for sarb unsubmitted report
    public abstract List<Long> listAllStatusForSarb()

    public abstract SystemEntity readExhTaskStatusNew(long companyId)

    public abstract Object readAgentById(long agentId)

    //create exhRegularFee
    public abstract void createDefaultDataForExhRegularFee(long companyId, long systemUserId)

    //create default schema ExhSearchCustomerForSarbActionServiceModel for sarb
    public abstract void createDefaultSchemaForExhCustomer()

    public abstract Class getDistributionPointClass()

    //read System Configuration of exchange house
    public abstract SysConfiguration readSysConfig(String key, long companyId)

    public abstract void loadTestData(long companyId, long systemUserId)

    public abstract void deleteTestData()

    public abstract long getSystemEntityTypePaidBy()

    public abstract long getSystemEntityTypePaymentMethod()

    public abstract long getSystemEntityTypeTaskStatus()

    public abstract long getSystemEntityTypeTaskType()

    // render common modal html
    public abstract String renderModalHtml()

    // sys config list for grid
    public abstract Map sysConfigListForGrid(BaseService baseService, long companyId)

    // sys config search list for grid
    public abstract Map sysConfigSearchListForGrid(BaseService baseService, long companyId)
}
