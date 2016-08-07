package com.athena.mis.application.controller

import com.athena.mis.BaseService
import com.athena.mis.application.actions.benchmark.ClearBenchmarkActionService
import com.athena.mis.application.actions.benchmark.CreateBenchmarkActionService
import com.athena.mis.application.actions.benchmark.DeleteBenchmarkActionService
import com.athena.mis.application.actions.benchmark.ExecuteBenchmarkActionService
import com.athena.mis.application.actions.benchmark.ListBenchmarkActionService
import com.athena.mis.application.actions.benchmark.ListForTruncateSamplingActionService
import com.athena.mis.application.actions.benchmark.ShowBenchmarkReportActionService
import com.athena.mis.application.actions.benchmark.StopBenchmarkActionService
import com.athena.mis.application.actions.benchmark.TruncateSamplingActionService
import com.athena.mis.application.actions.benchmark.UpdateBenchmarkActionService
import com.athena.mis.application.actions.report.benchmark.DownloadBenchmarkReportActionService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Benchmark
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.entity.TransactionLog
import com.athena.mis.application.model.ListForTruncateSamplingActionServiceModel
import com.athena.mis.application.service.AppMyFavouriteService
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.BenchmarkService
import com.athena.mis.application.service.TransactionLogService
import com.athena.mis.application.session.AppSessionService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Created by Asif on 12/23/2015.
 */

@TestFor(BenchmarkController)
@Mock([
        Benchmark,
        BenchmarkService,
        SystemEntity,
        AppMyFavouriteService,
        AppSystemEntityCacheService,
        CreateBenchmarkActionService,
        UpdateBenchmarkActionService,
        ListBenchmarkActionService,
        DeleteBenchmarkActionService,
        ExecuteBenchmarkActionService,
        ShowBenchmarkReportActionService,
        DownloadBenchmarkReportActionService,
        ListForTruncateSamplingActionService,
        TruncateSamplingActionService,
        ClearBenchmarkActionService,
        StopBenchmarkActionService,
        ListForTruncateSamplingActionServiceModel,
        TransactionLog,
        TransactionLogService,
        BaseService,
        AppSessionService

])
class BenchmarkControllerSpec extends Specification{

    void setup () {
        AppUser appUser = new AppUser()
        appUser.id = 1

        controller.createBenchmarkActionService.appSessionService.setAppUser(appUser)
        controller.updateBenchmarkActionService.appSessionService.setAppUser(appUser)
        controller.listBenchmarkActionService.appSessionService.setAppUser(appUser)
        controller.deleteBenchmarkActionService.appSessionService.setAppUser(appUser)
        controller.executeBenchmarkActionService.appSessionService.setAppUser(appUser)
        controller.showBenchmarkReportActionService.appSessionService.setAppUser(appUser)
        controller.downloadBenchmarkReportActionService.appSessionService.setAppUser(appUser)
        controller.listForTruncateSamplingActionService.appSessionService.setAppUser(appUser)
        controller.truncateSamplingActionService.appSessionService.setAppUser(appUser)
        controller.clearBenchmarkActionService.appSessionService.setAppUser(appUser)
        controller.stopBenchmarkActionService.appSessionService.setAppUser(appUser)
    }

    def 'Show Benchmark' (){

        setup:
        controller.params.oId = "TEST_ID"

        when:
        request.method = "POST"
        controller.show()

        then:
        model.oId == "TEST_ID"
        view == '/application/benchmark/show'
    }

    def 'Show Report'(){

        Benchmark benchmark = new Benchmark(
                version : 0,
                name : "Test Benhmark",
                totalRecord : 100000,
                recordPerBatch : 100,
                isSimulation : false,
                volumeTypeId : 1000000035,
                companyId : 1
        )

        benchmark.id = 1
        benchmark.save(flush: true, failOnError: true, validate: false)

        when:
        request.method = "POST"
        controller.showReport()

        then:

        view == '/application/report/benchmark/show'
    }

    def 'Show ForTruncateSampling' (){
        when:
        request.method = "POST"
        controller.showForTruncateSampling()

        then:

        view == '/application/benchmark/showTruncateSampling'
    }

    def 'Create Benchmark' () {

        setup:

        controller.params.id = 1
        controller.params.version = "0"
        controller.params.name = "Test Benhmark"
        controller.params.totalRecord = 100000
        controller.params.recordPerBatch = 100
        controller.params.isSimulation = false
        controller.params.volumeTypeId = 1000000035
        controller.params.companyId = 1

        when:
        request.method = 'POST'
        controller.create()

        then:
        response.redirectedUrl == null
        response.json.message == "Benchmark has been successfully saved"
        response.json.isError == false
    }

    def 'Update Benchmark'(){
        setup:

        Benchmark benchmark = new Benchmark(
                version : 0,
                name : "Test Benhmark",
                totalRecord : 100000,
                recordPerBatch : 100,
                isSimulation : false,
                volumeTypeId : 1000000035,
                companyId : 1
        )

        benchmark.id = 1
        benchmark.save(flush: true, failOnError: true, validate: false)

        controller.params.id = 1
        controller.params.version = "0"
        controller.params.name = "Test Benhmark"
        controller.params.totalRecord = 100000
        controller.params.recordPerBatch = 100
        controller.params.isSimulation = false
        controller.params.volumeTypeId = 1000000035
        controller.params.companyId = 1

        when:
        request.method = 'POST'
        controller.update()

        then:
        response.redirectUrl == null
        response.json.message == "Benchmark has been updated successfully"
        response.json.isError == false
    }

    def 'List Benchmark' () {
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

    def 'Clear Benchmark' (){
        setup:
        Benchmark benchmark = new Benchmark(
                version : 0,
                name : "Test Benhmark",
                totalRecord : 100000,
                recordPerBatch : 100,
                isSimulation : false,
                volumeTypeId : 1000000035,
                companyId : 1,
                startTime : new Date(),
                endTime : new Date()
        )

        benchmark.id = 1
        benchmark.save(flush: true, failOnError: true, validate: false)

        TransactionLog tl = new TransactionLog(

                sequence : 1,           // sequence number of transaction log corresponding entity
                entityTypeId : 1,      // SystemEntity.id
                entityId : 1,          // id of corresponding entity i.e DocDataTransfer.id, DocDataImport.id, Benchmark.id etc
                totalRecord : 1000,       // count of total inserted record
                recordPerBatch : 10,     // count of record per batch
                timeToRead : 500,        // record reading time of batch or file
                timeToWrite : 500,       // record writing time of batch or file
                processingTime : 0001,    // record processing time of batch or file
                startTime : new Date(),         // execution start time in millisecond
                endTime  : new Date(),          // execution end time in millisecond
                tableName : "Test Table",       // table name for data transfer or import
                exception : "Test Exception",       // message if any exception occur
                comment : "Any Comment",         // any comment
                companyId : 1,         // Company.id
                createdOn : new Date()         // Objec
        )

        tl.id = 1
        tl.save(flush: true, failOnError: true, validate: false)

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

        systemEntity.reservedId=controller.clearBenchmarkActionService.appSystemEntityCacheService.SYS_ENTITY_TRANSACTION_LOG_BENCHMARK
        systemEntity.save()

        controller.clearBenchmarkActionService.appSystemEntityCacheService.sysEntityMap.put(controller.clearBenchmarkActionService.appSystemEntityCacheService.SYS_ENTITY_TYPE_TRANSACTION_LOG,[systemEntity])

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
