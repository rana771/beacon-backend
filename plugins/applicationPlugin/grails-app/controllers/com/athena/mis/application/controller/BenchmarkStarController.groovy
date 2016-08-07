package com.athena.mis.application.controller

import com.athena.mis.application.actions.benchmarkStar.*
import com.athena.mis.application.actions.report.benchmarkStar.DownloadBenchmarkStarReportActionService

class BenchmarkStarController extends BaseController {
    static allowedMethods = [
            show: "POST", create: "POST", update: "POST", delete: "POST", list: "POST", execute: "POST",
            downloadBenchmarkReport: "GET", showForTruncateSampling: "POST", listForTruncateSampling: "POST",
            truncateSampling: "POST", stopBenchMark: "POST", createSchema: "POST", clear: "POST"
    ]

    CreateBenchmarkStarActionService createBenchmarkStarActionService
    UpdateBenchmarkStarActionService updateBenchmarkStarActionService
    ListBenchmarkStarActionService listBenchmarkStarActionService
    DeleteBenchmarkStarActionService deleteBenchmarkStarActionService
    ExecuteBenchmarkStarActionService executeBenchmarkStarActionService
    ShowBenchmarkStarReportActionService showBenchmarkStarReportActionService
    DownloadBenchmarkStarReportActionService downloadBenchmarkStarReportActionService
    ListForTruncateSamplingStarActionService listForTruncateSamplingStarActionService
    TruncateSamplingStarActionService truncateSamplingStarActionService
    ClearBenchmarkStarActionService clearBenchmarkStarActionService
    StopBenchmarkStarActionService stopBenchmarkStarActionService

    /**
     *  Show BenchmarkStar Report
     */
    def show() {
        render(view: '/application/benchmarkStar/show')
    }

    /**
     *  Create BenchmarkStar
     */
    def create() {
        renderOutput(createBenchmarkStarActionService, params)
    }

    /**
     *  Update BenchmarkStar
     */
    def update() {
        renderOutput(updateBenchmarkStarActionService, params)
    }

    /**
     *  List & Search BenchmarkStar
     */
    def list() {
        renderOutput(listBenchmarkStarActionService, params)
    }
    /**
     *  Delete BenchmarkStar
     */
    def delete() {
        renderOutput(deleteBenchmarkStarActionService, params)
    }

    /**
     *  Execute BenchmarkStar
     */
    def execute() {
        renderOutput(executeBenchmarkStarActionService, params)
    }

    /**
     * Stop benchmarkStar execution
     */
    def stopBenchMark() {
        renderOutput(stopBenchmarkStarActionService, params)
    }

    /**
     *  Navigate to BenchmarkStar report
     */
    def showReport() {
        String view = '/application/report/benchmarkStar/show'
        renderView(showBenchmarkStarReportActionService, params, view)
    }

    /**
     * Download benchmark report
     */
    def downloadBenchmarkReport() {
        Map result = (Map) getServiceResponse(downloadBenchmarkStarReportActionService, params).report
        String mime = grailsApplication.config.grails.mime.types[result.format]
        renderOutputStream(result.report.toByteArray(), mime, result.reportFileName)
    }

    /**
     * Show list of sampling star domains
     */
    def showForTruncateSampling() {
        render(view: '/application/benchmarkStar/showTruncateSampling')
    }

    /**
     * Get list of sampling star domains
     */
    def listForTruncateSampling() {
        renderOutput(listForTruncateSamplingStarActionService, params)
    }

    /**
     *  Execute Benchmark
     */
    def truncateSampling() {
        renderOutput(truncateSamplingStarActionService, params)
    }

    /**
     * Clear benchmarkStar
     */
    def clear() {
        renderOutput(clearBenchmarkStarActionService, params)
    }
}
