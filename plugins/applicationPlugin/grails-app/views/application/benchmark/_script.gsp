<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <app:ifAllUrl urls="/benchmark/update">
        <li onclick="editBenchmark();"><i class="fa fa-edit"></i>Edit</li>
    </app:ifAllUrl>
    <app:ifAllUrl urls="/benchmark/delete">
        <li onclick="deleteBenchmark();"><i class="fa fa-trash-o"></i>Delete</li>
    </app:ifAllUrl>
    <app:ifAllUrl urls="/benchmark/execute">
        <li onclick="executeBenchmark();"><i class="fa fa-play"></i>Execute</li>
    </app:ifAllUrl>
    <app:ifAllUrl urls="/benchmark/stopBenchMark">
        <li onclick="stopBenchmark();"><i class="fa fa-stop"></i>Stop</li>
    </app:ifAllUrl>
    <app:ifAllUrl urls="/benchmark/clear">
        <li onclick="clearBenchmark();"><i class="fa fa-eraser"></i>Clear</li>
    </app:ifAllUrl>
    <app:ifAllUrl urls="/transactionLog/show">
        <li onclick="monitorBenchmarkTransaction();"><i class="fa fa-search-plus"></i>Monitor Transaction</li>
    </app:ifAllUrl>
    <app:ifAllUrl urls="/benchmark/showReport">
        <li onclick="showBenchmarkReport();"><i class="fa fa-line-chart"></i>Report</li>
    </app:ifAllUrl>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script language="javascript">
    var benchMarkGrid, benchmarkDataSource, benchmarkModel, totalRecord, recordPerBatch, dropDownVolume, entityTypeId;

    $(document).ready(function () {
        checkOnLoadError();
        onLoadBenchmarkPage();
        initBenchmarkGrid();
        initObservable();

        $("#isSimulation").kendoMobileSwitch({onLabel: "YES",offLabel: "NO"});
    });

    function checkOnLoadError() {
        var isError = '${isError}';
        var msg = '${message}';
        if (isError == 'true') {
            showError(msg);
            return false;
        }
    }

    function onLoadBenchmarkPage() {
        // initialize form with kendo validator & bind onSubmit method
        initializeForm($("#benchmarkForm"), onSubmitBenchmark);
        $('#totalRecord').kendoNumericTextBox({
            min: 1,
            max: 999999999999,
            format: "#",
            decimals: 0,
            step: 1000
        });
        totalRecord = $("#totalRecord").data("kendoNumericTextBox");

        $('#recordPerBatch').kendoNumericTextBox({
            min: 50,
            max: 5000,
            format: "#",
            decimals: 0,
            step: 100
        });
        recordPerBatch = $("#recordPerBatch").data("kendoNumericTextBox");
        entityTypeId = $('#hidEntityTypeId').val();

        // update page title
        $(document).attr('title', "MIS - Create Benchmark");
        loadNumberedMenu(MENU_ID_APPLICATION, "#benchmark/show");
    }

    function executePreCondition() {
        if (!validateForm($("#benchmarkForm"))) {   // check kendo validation
            return false;
        }
        if (recordPerBatch.value() > totalRecord.value()) {
            showError('Record per batch can not be greater than Total Record');
            return false;
        }
        var mod = totalRecord.value() % recordPerBatch.value();
        if (mod != 0) {
            showError('Total record has to be multiple of record per batch');
            return false;
        }
        return true;
    }

    function onSubmitBenchmark() {
        if (executePreCondition() == false) {
            return false;
        }
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'benchmark', action: 'create')}";
        } else {
            var type = 'update';
            if (isApplicableToPerform(type) == false) {
                return;
            }
            actionUrl = "${createLink(controller: 'benchmark', action: 'update')}";
        }
        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);

        jQuery.ajax({
            type: 'post',
            data: jQuery("#benchmarkForm").serialize(),
            url: actionUrl,
            success: function (data, textStatus) {
                executePostCondition(data);
                setButtonDisabled($('#create'), false);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus);
            },
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false)
            },
            dataType: 'json'
        });
        return false;
    }

    function executePostCondition(result) {
        if (result.isError) {
            showError(result.message);
            showLoadingSpinner(false);
        } else {
            try {
                var newEntry = result.benchmark;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = benchMarkGrid.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    updateSelectedRow(newEntry);
                }
                resetForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function updateSelectedRow(newEntry) {
        var selectedRow = benchMarkGrid.select();
        var allItems = benchMarkGrid.items();
        var selectedIndex = allItems.index(selectedRow);
        benchMarkGrid.removeRow(selectedRow);
        benchMarkGrid.dataSource.insert(selectedIndex, newEntry);
    }

    function resetForm() {
        // clear all errors, validation messages & form values and bind onFocus method
        clearForm($("#benchmarkForm"), $('#name'));
        initObservable();
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function deleteBenchmark(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        var type = 'delete';
        if (isApplicableToPerform(type) == false) {
            return;
        }
        showLoadingSpinner(true);
        var benchmarkId = getSelectedIdFromGridKendo(benchMarkGrid);

        $.ajax({
            url: "${createLink(controller: 'benchmark', action: 'delete')}?id=" + benchmarkId,
            success: executePostConditionForDelete,
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus);
            },
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelectKendo(benchMarkGrid, 'benchmark') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected benchmark?')) {
            return false;
        }
        return true;
    }

    <%-- removing selected row and clean input form --%>
    function executePostConditionForDelete(data) {
        if (data.isError) {
            showError(data.message);
            return false;
        }
        var row = benchMarkGrid.select();
        row.each(function () {
            benchMarkGrid.removeRow($(this));
        });
        resetForm();
        showSuccess(data.message);
    }

    function editBenchmark() {
        if (executeCommonPreConditionForSelectKendo(benchMarkGrid, 'benchmark') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridBenchmark'));
        var benchmarkObj = getSelectedObjectFromGridKendo(benchMarkGrid);
        showBenchmark(benchmarkObj);
    }

    function showBenchmark(benchmarkObj) {
        benchmarkModel.set('benchmark', benchmarkObj);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function executeBenchmark(com, grid) {
        if (executeCommonPreConditionForSelectKendo(benchMarkGrid, 'benchmark') == false) {
            return;
        }
        if (!confirm('Are you sure you want to execute this benchmark now?')) {
            return false;
        }
        resetForm();
        showLoadingSpinner(true);
        var benchmarkId = getSelectedIdFromGridKendo(benchMarkGrid);
        $.ajax({
            url: "${createLink(controller: 'benchmark', action: 'execute')}?id=" + benchmarkId,
            success: executePostConditionForExecute,
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus)
            },
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function stopBenchmark() {
        if (executeCommonPreConditionForSelectKendo(benchMarkGrid, 'benchmark') == false) {
            return;
        }
        if (!confirm('Are you sure you want to stop execution now?')) {
            return false;
        }
        showLoadingSpinner(true);
        var benchmarkId = getSelectedIdFromGridKendo(benchMarkGrid);
        $.ajax({
            url: "${createLink(controller: 'benchmark', action: 'stopBenchMark')}?id=" + benchmarkId,
            success: executePostConditionForStopExecute,
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus)
            },
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForStopExecute(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            showSuccess('Benchmark will stop after executing the current batch');
            showLoadingSpinner(false);
            resetForm();
        }
    }

    function clearBenchmark() {
        if (executeCommonPreConditionForSelectKendo(benchMarkGrid, 'benchmark') == false) {
            return;
        }
        if (!confirm('Are you sure you want to clear the selected benchmark now?')) {
            return false;
        }
        var type = 'clear';
        if (isApplicableToPerform(type) == false) {
            return;
        }
        resetForm();
        showLoadingSpinner(true);
        var benchmarkId = getSelectedIdFromGridKendo(benchMarkGrid);
        $.ajax({
            url: "${createLink(controller: 'benchmark', action: 'clear')}?id=" + benchmarkId,
            success: executePostConditionForExecute,
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus)
            },
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForExecute(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            showSuccess(data.message);
            updateSelectedRow(data.benchmark);
            resetForm();
        }
        showLoadingSpinner(false);
    }

    function monitorBenchmarkTransaction() {
        if (executeCommonPreConditionForSelectKendo(benchMarkGrid, 'benchmark') == false) {
            return;
        }
        var type = 'monitor';
        if (isApplicableToPerform(type) == false) {
            return;
        }
        var entityId = getSelectedIdFromGridKendo(benchMarkGrid);
        var params = "?entityTypeId=" + entityTypeId + "&oId=" + entityId + "&url=benchmark/show";
        var loc = "${createLink(controller:'transactionLog', action: 'show')}" + params;
        router.navigate(formatLink(loc));
        return false;
    }

    function showBenchmarkReport() {
        if (executeCommonPreConditionForSelectKendo(benchMarkGrid, 'benchmark') == false) {
            return;
        }
        var type = 'htmlReport';
        if (isApplicableToPerform(type) == false) {
            return;
        }

        var benchmarkId = getSelectedIdFromGridKendo(benchMarkGrid);
        var params = "?oId=" + benchmarkId + "&leftMenu=" + 'benchmark' + "&url=benchmark/show";
        var loc = "${createLink(controller:'benchmark', action: 'showReport')}" + params;
        router.navigate(formatLink(loc));
        return false;
    }

    function isApplicableToPerform(type) {
        var startTime = getSelectedValueFromGridKendo(benchMarkGrid, 'startTime');
        var endTime = getSelectedValueFromGridKendo(benchMarkGrid, 'endTime');

        if (!startTime && !endTime) {
            if (type != 'update' && type != 'delete') {
                showError('Benchmark is not executed yet');
                return false;
            }
        }
        if (startTime && endTime) {
            if (type == 'delete') {
                showError('Selected benchmark has transactions');
                return false;
            }
        }
        if (startTime && !endTime) {
            if (type != 'monitor' && type != 'htmlReport') {
                showError('Benchmark is in progress');
                return false;
            }
        }
    }

    function initDataSource() {
        benchmarkDataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/benchmark/list",
                    dataType: "json",
                    type: "post"
                }
            },
            schema: {
                type: 'json',
                data: "list", total: "count",
                model: {
                    fields: {
                        id: {type: "number"},
                        volumeTypeId: {type: "number"},
                        version: {type: "number"},
                        name: {type: "string"},
                        totalRecord: {type: "number"},
                        recordPerBatch: {type: "number"},
                        isSimulation: {type: "boolean"},
                        startTime: {type: "date"},
                        endTime: {type: "date"}
                    }
                },
                parse: function (data) {
                    checkIsErrorGridKendo(data);
                    return data;
                }
            },
            pageSize: ${com.athena.mis.BaseService.DEFAULT_RESULT_PER_PAGE},
            serverPaging: true,
            serverFiltering: true,
            serverSorting: true
            <g:if test="${oId!=null}">
            , filter: {field: "id", operator: "equal", value: ${oId}}
            </g:if>
        });
    }

    function initBenchmarkGrid() {
        initDataSource();
        $("#gridBenchmark").kendoGrid({
            dataSource: benchmarkDataSource,
            height: getGridHeightKendo(),
            selectable: true,
            sortable: true,
            resizable: true,
            reorderable: true,
            pageable: {
                refresh: false,
                pageSizes: [10, 15, 20],
                buttonCount: 4
            },
            columns: [
                {field: "name", title: "Name", width: 60, sortable: true, filterable: false},
                {field: "totalRecord", title: "Total Record", width: 40, sortable: false, filterable: false},
                {field: "recordPerBatch", title: "Record Per Batch", width: 40, sortable: false, filterable: false},
                {
                    field: "startTime", title: "Start Time", width: 60, sortable: false, filterable: false,
                    template: "#= startTime ? getFormattedTime(startTime):'0:0' #"
                },
                {
                    field: "endTime", title: "End Time", width: 60, sortable: false, filterable: false,
                    template: "#= endTime ? getFormattedTime(endTime):'0:0' #"
                },
                {
                    title: "Duration", width: 30, sortable: false, filterable: false,
                    template: "#=  (startTime && endTime) ? msToTime(getDuration(endTime,startTime)): '00:00:00.0' #"
                },
                {
                    field: "isSimulation", title: "Simulation", width: 30, sortable: false, filterable: false,
                    template: "#= isSimulation?'YES':'NO'#"
                }
            ],
            filterable: {
                mode: "row"
            },

            toolbar: kendo.template($("#gridToolbar").html())
        });
        benchMarkGrid = $("#gridBenchmark").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function reloadKendoGrid() {
        benchMarkGrid.dataSource.filter([]);
    }

    function getDuration(end, start) {
        return kendoParse(end) - kendoParse(start);
    }
    function getFormattedTime(time) {
        return kendo.toString(kendoParse(time), 'dd-MMM-yyyy [hh:mm:ss tt]');
    }
    function kendoParse(time) {
        return kendo.parseDate(time, 'yyyy-MM-ddTHH:mm:ss');
    }

    function msToTime(s) {
        function addZ(n) {
            return (n < 10 ? '0' : '') + n;
        }

        var ms = s % 1000;
        s = (s - ms) / 1000;
        var secs = s % 60;
        s = (s - secs) / 60;
        var mins = s % 60;
        var hrs = (s - mins) / 60;
        return addZ(hrs) + ':' + addZ(mins) + ':' + addZ(secs) + '.' + ms;
    }

    function initObservable() {
        benchmarkModel = kendo.observable(
                {
                    benchmark: {
                        id: "",
                        version: "",
                        name: "",
                        totalRecord: "",
                        recordPerBatch: "",
                        isSimulation: false,
                        volumeTypeId: "",
                        startTime: "",
                        endTime: ""
                    }
                }
        );
        kendo.bind($("#application_top_panel"), benchmarkModel);
    }

</script>
