package com.athena.mis.application.controller

import com.athena.mis.BaseService
import com.athena.mis.application.actions.benchmarkStar.ClearBenchmarkStarActionService
import com.athena.mis.application.actions.benchmarkStar.CreateBenchmarkStarActionService
import com.athena.mis.application.actions.benchmarkStar.DeleteBenchmarkStarActionService
import com.athena.mis.application.actions.benchmarkStar.ExecuteBenchmarkStarActionService
import com.athena.mis.application.actions.benchmarkStar.ListBenchmarkStarActionService
import com.athena.mis.application.actions.benchmarkStar.ListForTruncateSamplingStarActionService
import com.athena.mis.application.actions.benchmarkStar.ShowBenchmarkStarReportActionService
import com.athena.mis.application.actions.benchmarkStar.StopBenchmarkStarActionService
import com.athena.mis.application.actions.benchmarkStar.TruncateSamplingStarActionService
import com.athena.mis.application.actions.benchmarkStar.UpdateBenchmarkStarActionService
import com.athena.mis.application.actions.report.benchmarkStar.DownloadBenchmarkStarReportActionService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Benchmark
import com.athena.mis.application.entity.BenchmarkStar
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.model.ListForTruncateSamplingStarActionServiceModel
import com.athena.mis.application.service.AppMyFavouriteService
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.BenchmarkStarService
import com.athena.mis.application.service.TransactionLogService
import com.athena.mis.application.session.AppSessionService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Created by Asif on 12/23/2015.
 */

@TestFor(BenchmarkStarController)
@Mock([
        BenchmarkStar,
        SystemEntity,
        BenchmarkStarService,
        CreateBenchmarkStarActionService,
        UpdateBenchmarkStarActionService,
        ListBenchmarkStarActionService,
        DeleteBenchmarkStarActionService,
        ExecuteBenchmarkStarActionService,
        ShowBenchmarkStarReportActionService,
        DownloadBenchmarkStarReportActionService,
        ListForTruncateSamplingStarActionService,
        ListForTruncateSamplingStarActionServiceModel,
        TruncateSamplingStarActionService,
        ClearBenchmarkStarActionService,
        StopBenchmarkStarActionService,
        AppMyFavouriteService,
        AppSystemEntityCacheService,
        TransactionLogService,
        BaseService,
        AppSessionService
])

class BenchmarkStarControllerSpec extends Specification{

    void setup () {
        AppUser appUser = new AppUser()
        appUser.id = 1

        controller.createBenchmarkStarActionService.appSessionService.setAppUser(appUser)
        controller.updateBenchmarkStarActionService.appSessionService.setAppUser(appUser)
        controller.listBenchmarkStarActionService.appSessionService.setAppUser(appUser)
        controller.deleteBenchmarkStarActionService.appSessionService.setAppUser(appUser)
        controller.executeBenchmarkStarActionService.appSessionService.setAppUser(appUser)
        controller.showBenchmarkStarReportActionService.appSessionService.setAppUser(appUser)
        controller.downloadBenchmarkStarReportActionService.appSessionService.setAppUser(appUser)
        controller.listForTruncateSamplingStarActionService.appSessionService.setAppUser(appUser)
        controller.truncateSamplingStarActionService.appSessionService.setAppUser(appUser)
        controller.clearBenchmarkStarActionService.appSessionService.setAppUser(appUser)
        controller.stopBenchmarkStarActionService.appSessionService.setAppUser(appUser)
    }

    def 'Show BenchmarkStar' (){

        when:
        request.method = "POST"
        controller.show()

        then:
        view == '/application/benchmarkStar/show'
    }

    def 'Show Report'(){

        BenchmarkStar benchmarkStar = new BenchmarkStar(
                version : 0,
                name : "Test Benchmark Star",
                startTime : new Date(),
                endTime : new Date(),
                totalRecord : 100000,
                recordPerBatch : 100,
                isSimulation : false,
                companyId : 1
        )

        benchmarkStar.id = 1
        benchmarkStar.save(flush: true, failOnError: true, validate: false)

        controller.params.oId = benchmarkStar.id.toString()

        when:
        request.method = "POST"
        controller.showReport()

        then:

        view == '/application/report/benchmarkStar/show'
    }

    def 'Show ForTruncateSampling' (){
        when:
        request.method = "POST"
        controller.showForTruncateSampling()

        then:

        view == '/application/benchmarkStar/showTruncateSampling'
    }

    def 'Create BenchmarkStar' () {

        setup:

        controller.params.id = 1
        controller.params.version = "0"
        controller.params.name = "Test Benchmark Star"
        controller.params.totalRecord = 100000
        controller.params.recordPerBatch = 100
        controller.params.isSimulation = false
        controller.params.companyId = 1

        when:
        request.method = 'POST'
        controller.create()

        then:
        response.redirectedUrl == null
        response.json.message == "Benchmark has been successfully saved"
        response.json.isError == false
    }

    def 'Update BenchmarkStar' (){
        setup:

        BenchmarkStar benchmarkStar = new BenchmarkStar(
                version : 0,
                name : "Test Benchmark Star",
                totalRecord : 100000,
                recordPerBatch : 100,
                isSimulation : false,
                companyId : 1
        )

        benchmarkStar.id = 1
        benchmarkStar.save(flush: true, failOnError: true, validate: false)

        controller.params.id = 1
        controller.params.version = "0"
        controller.params.name = "Test Benchmark Star"
        controller.params.totalRecord = 100000
        controller.params.recordPerBatch = 100
        controller.params.isSimulation = false
        controller.params.companyId = 1

    }

    def 'List BenchmarkStar' () {
        setup:

        controller.params.page = 1
        controller.params.pageSize = 10
        controller.params."sort[0]['dir']" = 'asc'
        controller.params."sort[0]['field']" = 'name'
        controller.params.take = 10
        controller.params.skip = 0

        when:
        request.method = 'POST'
        controller.list()

        then:
        response.json.isError == false
        response.json.count >= 0
    }

    def 'List ForTruncateSampling'(){

        setup:

        controller.params.page = 1
        controller.params.pageSize = 10
        controller.params."sort[0]['dir']" = 'asc'
        controller.params."sort[0]['field']" = 'name'
        controller.params.take = 10
        controller.params.skip = 0

        when:
        request.method = 'POST'
        controller.listForTruncateSampling()

        then:
        response.json.isError == false
        response.json.count >= 0
    }

    def 'Clear BenchmarkStar' (){
        setup:
        BenchmarkStar benchmarkStar = new BenchmarkStar(
                version : 0,
                name : "Test Benchmark Star",
                totalRecord : 100000,
                recordPerBatch : 100,
                isSimulation : false,
                companyId : 1
        )

        benchmarkStar.id = 1
        benchmarkStar.save(flush: true, failOnError: true, validate: false)

        SystemEntity systemEntity = new SystemEntity()
        systemEntity.id = 1
        systemEntity.version = 0
        systemEntity.key = "Test Key"
        systemEntity.type = 1
        systemEntity.isActive = true
        systemEntity.pluginId = 1
        systemEntity.createdBy = 1
        systemEntity.createdOn = new Date()
        systemEntity.updatedOn = new Date()
        systemEntity.updatedBy = 1
        systemEntity.companyId = 1;

        systemEntity.reservedId=controller.clearBenchmarkStarActionService.appSystemEntityCacheService.SYS_ENTITY_TRANSACTION_LOG_BENCHMARK
        systemEntity.save()

        controller.clearBenchmarkStarActionService.appSystemEntityCacheService.sysEntityMap.put(controller.clearBenchmarkStarActionService.appSystemEntityCacheService.SYS_ENTITY_TYPE_TRANSACTION_LOG,[systemEntity])

        controller.params.id = "1"
        controller.params.version = "0"
        controller.params.name = "Test Benhmark"
        controller.params.totalRecord = 100000
        controller.params.recordPerBatch = 100
        controller.params.isSimulation = false
        controller.params.volumeTypeId = 1000000035
        controller.params.companyId = 1

        when:
        request.method = 'POST'
        controller.clear()

        then:
        response.redirectUrl == null
        response.json.message == "Benchmark has been cleaned successfully"
        response.json.isError == false
    }

}
