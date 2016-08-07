<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <app:ifAllUrl urls="/benchmarkStar/truncateSampling">
        <li onclick="truncateSamplingDomain();"><i class="fa fa-building-o"></i>Truncate</li>
    </app:ifAllUrl>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>
<script type="text/javascript">
    var truncateSamplingListModel = false;
    var gridTruncateSampling, dataSource, dropDownDbInstance, dbInstanceId;

    jQuery(function ($) {
        onLoadSamplingListForm();
        initTruncateSamplingGrid();
    });

    function onLoadSamplingListForm() {
        // update page title
        $(document).attr('title', "MIS - Truncate Sampling Star");
        loadNumberedMenu(MENU_ID_APPLICATION, "#benchmarkStar/showForTruncateSampling");
    }
    function executePreCondition() {
        if (!validateForm($("#truncateSamplingForm"))) {   // check kendo validation
            return false;
        }
        return true;
    }

    function truncateSamplingDomain() {
        if (executeCommonPreConditionForSelectKendo(gridTruncateSampling, 'Only Large domain') == false) {
            return;
        }
        var selectedObj = getSelectedObjectFromGridKendo(gridTruncateSampling);
        var totalCount = selectedObj.totalCount;
        var domainType = selectedObj.volumeName;
        if (totalCount == 0) {
            showError("Selected sampling domain has no data");
            return false;
        }
        if (domainType == 'Small' || domainType == 'Medium') {
            showError(domainType + " domain can't be truncated due to its relation with Large domain");
            return false;
        }
        if (!confirm('Are you sure you want to truncate the sampling domain? All other domains will be truncated too.')) {
            return;
        }
        showLoadingSpinner(true);

        var params = "?dbInstanceId=" + dbInstanceId;
        $.ajax({
            url: "${createLink(controller: 'benchmarkStar', action: 'truncateSampling')}" + params,
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

    function executePostConditionForTruncateSampling(data) {
        if (data.isError) {
            showError(data.message);
            return;
        }
        gridTruncateSampling.items().each(function () {
            var dataItem = gridTruncateSampling.dataItem($(this));
            dataItem.totalCount=0;
        });
        gridTruncateSampling.refresh();
        showSuccess(data.message);
    }

    function initDataSource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/benchmarkStar/listForTruncateSampling",
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

    function reloadKendoGrid() {
        gridTruncateSampling.dataSource.filter([]);
    }

</script>