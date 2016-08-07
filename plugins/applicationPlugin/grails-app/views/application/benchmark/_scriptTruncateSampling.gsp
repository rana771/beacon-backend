<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <app:ifAllUrl urls="/benchmark/truncateSampling">
        <li onclick="truncateSamplingDomain();"><i class="fa fa-building-o"></i>Truncate</li>
    </app:ifAllUrl>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>
<script type="text/javascript">
    var gridTruncateSampling, dataSource, truncateSamplingListModel = false;
    var dropDownDbInstance, dbInstanceId;

    $(document).ready(function () {
        onLoadSamplingListForm();
        initTruncateSamplingGrid();
    });

    function onLoadSamplingListForm() {
        // update page title
        $(document).attr('title', "MIS - Truncate Sampling");
        loadNumberedMenu(MENU_ID_APPLICATION, "#benchmark/showForTruncateSampling");
    }

    function initDataSource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/benchmark/listForTruncateSampling",
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
                        volumeName: {type: "string"},
                        totalCount: {type: "number"}
                    }
                },
                parse: function (data) {
                    checkIsErrorGridKendo(data);
                    return data;
                }
            },
            sort: {field: 'id', dir: 'asc'},  // default sort
            pageSize: ${com.athena.mis.BaseService.DEFAULT_RESULT_PER_PAGE},
            serverPaging: true,
            serverFiltering: true,
            serverSorting: true
        });
    }

    function initTruncateSamplingGrid() {
        initDataSource();
        $("#gridTruncateSampling").kendoGrid({
            dataSource: dataSource,
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
                {field: "volumeName", title: "Type", width: 100, sortable: false, filterable: false},
                {field: "totalCount", title: "No. of Records", width: 100, sortable: false, filterable: false}
            ],
            filterable: {
                mode: "row"
            },
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridTruncateSampling = $("#gridTruncateSampling").data("kendoGrid");
        $("#menuGrid").kendoMenu();
        clearGridKendo(gridTruncateSampling);
    }

    function executePreCondition() {
        if (!validateForm($("#truncateSamplingForm"))) {   // check kendo validation
            return false;
        }
        return true;
    }

    function truncateSamplingDomain() {
        if (executeCommonPreConditionForSelectKendo(gridTruncateSampling, 'sampling domain') == false) {
            return;
        }
        var samplingObj = getSelectedObjectFromGridKendo(gridTruncateSampling);
        if (samplingObj.totalCount == 0) {
            showError("Selected sampling domain has no data");
            return false;
        }
        if (!confirm('Are you sure you want to truncate the selected sampling domain?')) {
            return;
        }
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller: 'benchmark', action: 'truncateSampling')}?reservedId=" + samplingObj.id + "&dbInstanceId=" + dbInstanceId,
            success: executePostConditionForTruncateSampling,
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

    function reloadKendoGrid() {
        gridTruncateSampling.dataSource.filter([]);
    }

    function executePostConditionForTruncateSampling(data) {
        if (data.isError) {
            showError(data.message);
            return false;
        }
        var selectedRow = gridTruncateSampling.select();
        var gridData = gridTruncateSampling.dataItem(selectedRow);
        gridData.set("totalCount", "0");
        showSuccess(data.message);
    }

</script>