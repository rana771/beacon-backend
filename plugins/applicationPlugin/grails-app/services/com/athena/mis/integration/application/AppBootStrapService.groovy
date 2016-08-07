package com.athena.mis.integration.application

import com.athena.mis.BaseBootStrapService
import com.athena.mis.application.actions.bootstrap.AppBootStrapActionService
import com.athena.mis.application.entity.Company
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.integration.accounting.AccPluginConnector
import com.athena.mis.integration.arms.ArmsPluginConnector
import com.athena.mis.integration.beacon.BeaconPluginConnector
import com.athena.mis.integration.budget.BudgPluginConnector
import com.athena.mis.integration.datapipeline.DataPipeLinePluginConnector
import com.athena.mis.integration.document.DocumentPluginConnector
import com.athena.mis.integration.elearning.ELearningPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.integration.fixedasset.FxdPluginConnector
import com.athena.mis.integration.ictpool.IctPoolPluginConnector
import com.athena.mis.integration.inventory.InvPluginConnector
import com.athena.mis.integration.procurement.ProcPluginConnector
import com.athena.mis.integration.projecttrack.PtPluginConnector
import com.athena.mis.integration.qsmeasurement.QsPluginConnector
import com.athena.mis.integration.sarb.SarbPluginConnector
import grails.plugin.springsecurity.SpringSecurityService
import org.springframework.beans.factory.annotation.Autowired

class AppBootStrapService extends BaseBootStrapService {

    SpringSecurityService springSecurityService
    AppBootStrapActionService appBootStrapActionService
    AppSystemEntityCacheService appSystemEntityCacheService

    @Autowired(required = false)
    DataPipeLinePluginConnector dataPipeLineImplService
    @Autowired(required = false)
    DocumentPluginConnector documentImplService
    @Autowired(required = false)
    BudgPluginConnector budgBudgetImplService
    @Autowired(required = false)
    AccPluginConnector accAccountingImplService
    @Autowired(required = false)
    InvPluginConnector invInventoryImplService
    @Autowired(required = false)
    ProcPluginConnector procProcurementImplService
    @Autowired(required = false)
    FxdPluginConnector fxdFixedAssetImplService
    @Autowired(required = false)
    QsPluginConnector qsMeasurementImplService
    @Autowired(required = false)
    PtPluginConnector ptProjectTrackImplService
    @Autowired(required = false)
    ExchangeHousePluginConnector exchangeHouseImplService
    @Autowired(required = false)
    ArmsPluginConnector armsImplService
    @Autowired(required = false)
    ELearningPluginConnector elearningImplService
    @Autowired(required = false)
    SarbPluginConnector sarbImplService
    @Autowired(required = false)
    IctPoolPluginConnector ictPoolImplService
    @Autowired(required = false)
    BeaconPluginConnector beaconImplService

    @Override
    boolean init(Company company) {
        try {
            Map params = [company: company]
            // execute pre condition
            Map preResult = appBootStrapActionService.executePreCondition(params)

            // execute the action
            Map executeResult = appBootStrapActionService.execute(preResult)

            // execute the post actions
            Map postResult = appBootStrapActionService.executePostCondition(executeResult)

            // load default data for existing plugins
            if (budgBudgetImplService) budgBudgetImplService.bootStrap(company)
            if (invInventoryImplService) invInventoryImplService.bootStrap(company)
            if (procProcurementImplService) procProcurementImplService.bootStrap(company)
            if (accAccountingImplService) accAccountingImplService.bootStrap(company)
            if (qsMeasurementImplService) qsMeasurementImplService.bootStrap(company)
            if (fxdFixedAssetImplService) fxdFixedAssetImplService.bootStrap(company)
            if (documentImplService) documentImplService.bootStrap(company)
            if (dataPipeLineImplService) dataPipeLineImplService.bootStrap(company)
            if (ptProjectTrackImplService) ptProjectTrackImplService.bootStrap(company)
            if (exchangeHouseImplService) exchangeHouseImplService.bootStrap(company)
            if (armsImplService) armsImplService.bootStrap(company)
            if (sarbImplService) sarbImplService.bootStrap(company)
            if (elearningImplService) elearningImplService.bootStrap(company)
            if (ictPoolImplService) ictPoolImplService.bootStrap(company)
            if (beaconImplService) beaconImplService.bootStrap(company)

            // reload request map
            springSecurityService.clearCachedRequestmaps()
            // load app system entity cache service
            appSystemEntityCacheService.init()

            return true
        } catch (Exception ex) {
            return false
        }
    }

    @Override
    boolean init() {
        return init(null)
    }
}
