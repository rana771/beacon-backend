package com.athena.mis.application.controller

import com.athena.mis.BaseService
import com.athena.mis.application.actions.transactionlog.ClearTransactionLogActionService
import com.athena.mis.application.actions.transactionlog.ListTransactionLogActionService
import com.athena.mis.application.actions.transactionlog.ShowTransactionLogActionService
import com.athena.mis.application.actions.transactionlog.ShowTransactionLogForDplDataExportActionService
import com.athena.mis.application.actions.transactionlog.ShowTransactionLogForDplDataImportActionService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.TransactionLog
import com.athena.mis.application.service.TransactionLogService
import com.athena.mis.application.session.AppSessionService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Created by Asif on 12/28/2015.
 */

@TestFor(TransactionLogController)

@Mock([
        TransactionLog,
        TransactionLogService,
        ShowTransactionLogActionService,
        ShowTransactionLogForDplDataExportActionService,
        ShowTransactionLogForDplDataImportActionService,
        ListTransactionLogActionService,
        ClearTransactionLogActionService,
        BaseService,
        AppSessionService
])

class TransactionLogControllerSpec extends Specification{

    void setup(){
        AppUser appUser = new AppUser()
        appUser.companyId = 1
        appUser.id = 1

        controller.showTransactionLogActionService.appSessionService.setAppUser(appUser)
        controller.showTransactionLogForDplDataExportActionService.appSessionService.setAppUser(appUser)
        controller.showTransactionLogForDplDataImportActionService.appSessionService.setAppUser(appUser)
        controller.listTransactionLogActionService.appSessionService.setAppUser(appUser)
        controller.clearTransactionLogActionService.appSessionService.setAppUser(appUser)
        }

    def 'Show TransactionLog' (){

        when:
        request.method = "POST"
        controller.show()

        then:
        view == '/application/transactionLog/show'
    }

    def 'Show ForDplDataExport' (){
        when:
        request.method = "POST"
        controller.showForDplDataExport()

        then:
        view == '/dataPipeLine/transactionLog/showForDataExport'
    }

    def 'Show ForDplDataImport' (){
        when:
        request.method = "POST"
        controller.showForDplDataImport()

        then:
        view == '/dataPipeLine/transactionLog/showForDataImport'
    }

    def 'List TransactionLog' () {
        setup:

//        TransactionLog tl = new TransactionLog(
//                sequence           // sequence number of transaction log corresponding entity
//                entityTypeId       // SystemEntity.id
//                entityId           // id of corresponding entity i.e DocDataTransfer.id, DocDataImport.id, Benchmark.id etc
//                totalRecord        // count of total inserted record
//                recordPerBatch      // count of record per batch
//                timeToRead         // record reading time of batch or file
//                timeToWrite        // record writing time of batch or file
//                processingTime     // record processing time of batch or file
//                startTime          // execution start time in millisecond
//                endTime            // execution end time in millisecond
//                tableName        // table name for data transfer or import
//                exception        // message if any exception occur
//                comment          // any comment
//                companyId          // Company.id
//                createdOn          // Object c
//        )
//        tl.id = 1

        controller.params.entityTypeId = 1
        controller.params.entityId =1
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

    def 'Clear TransactionLog' (){

        setup:

        TransactionLog tl = new TransactionLog(
                sequence : 1,           // sequence number of transaction log corresponding entity
                entityTypeId : 1,      // SystemEntity.id
                entityId : 1,          // id of corresponding entity i.e DocDataTransfer.id, DocDataImport.id, Benchmark.id etc
                totalRecord : 1000,        // count of total inserted record
                recordPerBatch : 50,     // count of record per batch
                timeToRead  : 1,        // record reading time of batch or file
                timeToWrite : 1,       // record writing time of batch or file
                processingTime : 1,    // record processing time of batch or file
                startTime  : 0500,        // execution start time in millisecond
                endTime  : 0600,          // execution end time in millisecond
                tableName : "Test Table",       // table name for data transfer or import
                exception : "Test Exception",       // message if any exception occur
                comment : "Testing Purpose",         // any comment
                companyId  : 1,        // Company.id
                createdOn  : new Date()        // Object c
        )
        tl.id = 1
        tl.save(flush: true, failOnError: true, validate: false)

        controller.params.entityTypeId = 1
        controller.params.entityId =1

        when:
        request.method = 'POST'
        controller.clear()

        then:
        response.json.isError == false
        response.json.count >= 0
    }
}
