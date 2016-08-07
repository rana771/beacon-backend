<script language="javascript">
    var benchmarkId;
    $(document).ready(function () {
        onLoadBenchmarkDetails();
        $('#printPdfBtn').click(function () {
            downloadBenchmarkReport();
        });
    });

    function onLoadBenchmarkDetails() {
        benchmarkId = ${params.oId};
        createChart();
        // update page title
        $(document).attr('title', "MIS - Benchmark Report");
        loadNumberedMenu(MENU_ID_APPLICATION, "#benchmark/show");
    }

    function downloadBenchmarkReport() {
        showLoadingSpinner(true);
        var params = "?benchmarkId=" + benchmarkId;
        if (confirm('Do you want to download the benchmark report now?')) {
            var url = "${createLink(controller:'benchmark', action: 'downloadBenchmarkReport')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function createChart() {
        $("#chart").kendoChart({
            title: {
                text: "Benchmark Transaction Status \n ${benchmark?.name}"
            },
            legend: {
                position: "bottom"
            },
            seriesDefaults: {
                type: "line",
                style: "smooth",
                markers: {
                    visible: false
                }
            },
            series: [{
                name: "Read Time",
                data: [${benchMarkDetails.read_time}]
            },
                {
                    name: "Processing Time",
                    data: [${benchMarkDetails.processing_time}]
                },
                {
                    name: "Write Time",
                    data: [${benchMarkDetails.write_time}]
                }],
            valueAxis: {
                title: {
                    text: "Milliseconds"
                },
                line: {
                    visible: true
                }
            },
            categoryAxis: {
                title: {
                    text: "Transaction Span (${benchmark?.recordPerBatch} per batch)"
                },
                categories: [${benchMarkDetails.span}],
                majorGridLines: {
                    visible: false
                },
                labels: {
                    step: ${xStep}
                }
            },
            tooltip: {
                visible: true,
                format: "{0}",
                template: "#= series.name#: #= value # ms"
            }
        });
    }
</script>


