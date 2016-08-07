<%@ page import="com.athena.mis.BaseService" %>
<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script language="javascript">
    var dropDownModule, gridReleaseHistory, dataSourceReleaseHistory;

    $(document).ready(function () {
        onLoadProjectPage();
        initProjectGrid();
    });

    function onLoadProjectPage() {
        // initialize form with kendo validator & bind onSubmit method
        initializeForm($("#versionForm"), onSubmitVersion);

        // update page title
        $(document).attr('title', "MIS - Release History");
        loadNumberedMenu(MENU_ID_APPLICATION, "#appVersion/show");
    }

    function executePreCondition() {
        if (!validateForm($("#versionForm"))) {   // check kendo validation
            return false;
        }
        return true;
    }

    function onSubmitVersion() {
        if (executePreCondition() == false) {
            return false;
        }

        var pluginId = dropDownModule.value();
        showLoadingSpinner(true);
        setButtonDisabled($('#create'), true);
        var params = "?pluginId=" + pluginId;
        var url = "${createLink(controller: 'appVersion', action: 'list')}" + params;
        populateGridKendo(gridReleaseHistory, url);
        setButtonDisabled($('#create'), false);
        showLoadingSpinner(false);
    }

    function initDataSourceReleaseHistory() {
        dataSourceReleaseHistory = new kendo.data.DataSource({
            transport: {
                read: {
                    url: false,
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
                        releaseNo: {type: "number"},
                        releasedOn: {type: "date"},
                        span: {type: "number"},
                        isCurrent: {type: "boolean"}
                    }
                },
                parse: function (data) {
                    checkIsErrorGridKendo(data);
                    return data;
                }
            },
            sort: {field: 'releaseNo', dir: 'desc'},  // default sort
            pageSize: ${BaseService.DEFAULT_RESULT_PER_PAGE},
            serverPaging: true,
            serverFiltering: true,
            serverSorting: true
        });
    }

    function initProjectGrid() {
        initDataSourceReleaseHistory();
        $("#gridReleaseHistory").kendoGrid({
            dataSource: dataSourceReleaseHistory,
            autoBind: false,
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
                {
                    field: "releaseNo", title: "Registered Version", width: 60, sortable: false, filterable: false,
                    attributes: {style: setAlignCenter()}, headerAttributes: {style: setAlignCenter()}
                },
                {
                    field: "releasedOn", title: "Released On", width: 60, sortable: false, filterable: false,
                    template: "#= kendo.toString(kendo.parseDate(releasedOn, 'yyyy-MM-dd'), 'dd-MMM-yyyy') #"
                },
                {
                    field: "releasedOn", title: "Valid Until", width: 60, sortable: false, filterable: false,
                    template: "#= getValidDate(releasedOn, span) #"
                },
                {
                    field: "isCurrent", title: "Current", width: 60, sortable: false, filterable: false,
                    attributes: {style: setAlignCenter()}, headerAttributes: {style: setAlignCenter()},
                    template: "#= isCurrent?'YES':'NO'#"
                }
            ],
            filterable: {mode: "row"},
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridReleaseHistory = $("#gridReleaseHistory").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function reloadKendoGrid() {
        gridReleaseHistory.dataSource.filter([]);
    }

    function getValidDate(releasedOn, span) {
        var dt = new Date(kendo.toString(releasedOn, 'd'));
        dt.setDate(dt.getDate() + span);
        return kendo.toString(kendo.parseDate(dt, 'yyyy-MM-dd'), 'dd-MMM-yyyy')
    }

</script>

