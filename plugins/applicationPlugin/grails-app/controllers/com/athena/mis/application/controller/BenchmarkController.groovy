package com.athena.mis.application.controller

import com.athena.mis.application.actions.benchmark.*
import com.athena.mis.application.actions.report.benchmark.DownloadBenchmarkReportActionService

class BenchmarkController extends BaseController {
    static allowedMethods = [
            show                   : "POST", create: "POST", update: "POST", delete: "POST", list: "POST", execute: "POST",
            downloadBenchmarkReport: "GET", showForTruncateSampling: "POST", listForTruncateSampling: "POST",
            truncateSampling       : "POST", stopBenchMark: "POST", clear: "POST"
    ]

    CreateBenchmarkActionService createBenchmarkActionService
    UpdateBenchmarkActionService updateBenchmarkActionService
    ListBenchmarkActionService listBenchmarkActionService
    DeleteBenchmarkActionService deleteBenchmarkActionService
    ExecuteBenchmarkActionService executeBenchmarkActionService
    ShowBenchmarkReportActionService showBenchmarkReportActionService
    DownloadBenchmarkReportActionService downloadBenchmarkReportActionService
    ListForTruncateSamplingActionService listForTruncateSamplingActionService
    TruncateSamplingActionService truncateSamplingActionService
    ClearBenchmarkActionService clearBenchmarkActionService
    StopBenchmarkActionService stopBenchmarkActionService

    /**
     *  Show Benchmark
     */
    def show() {
        render(view: '/application/benchmark/show', model: [oId: params.oId?params.oId:null])
    }

    /**
     *  Create Benchmark
     */
    def create() {
        renderOutput(createBenchmarkActionService, params)
    }

    /**
     *  Update Benchmark
     */
    def update() {
        renderOutput(updateBenchmarkActionService, params)
    }

    /**
     *  List & Search Benchmark
     */
    def list() {
        renderOutput(listBenchmarkActionService, params)
    }

    /**
     *  Delete Benchmark
     */
    def delete() {
        renderOutput(deleteBenchmarkActionService, params)
    }

    /**
     *  Execute Benchmark
     */
    def execute() {
        renderOutput(executeBenchmarkActionService, params)
    }

    /**
     * Stop benchmark
     */
    def stopBenchMark() {
        renderOutput(stopBenchmarkActionService, params)
    }

    /**
     *  Navigate to Benchmark report
     */
    def showReport() {
        String view = '/application/report/benchmark/show'
        renderView(showBenchmarkReportActionService, params, view)
    }

    /**
     * Download benchmark report
     */
    def downloadBenchmarkReport() {
        Map result = (Map) getServiceResponse(downloadBenchmarkReportActionService, params).report
        String mime = grailsApplication.config.grails.mime.types[result.format]
        renderOutputStream(result.report.toByteArray(), mime, result.reportFileName)
    }

    /**
     * Show list of sampling domains
     */
    def showForTruncateSampling() {
        render(view: '/application/benchmark/showTruncateSampling')
    }

    /**
     * Get list of sampling domains
     */
    def listForTruncateSampling() {
        renderOutput(listForTruncateSamplingActionService, params)
    }

    /**
     *  Execute Benchmark
     */
    def truncateSampling() {
        renderOutput(truncateSamplingActionService, params)
    }

    /**
     * Clear benchmark
     */
    def clear() {
        renderOutput(clearBenchmarkActionService, params)
    }
}
