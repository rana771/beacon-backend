<div id="gridMyRole"></div>

<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <sec:access url="/requestMap/reloadRequestMap">
        <li onclick="reloadRequestMap();"><i class="fa fa-refresh"></i>Reload</li>
    </sec:access>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>


<script type="text/javascript">
    var gridMyRole, dataSource;

    $(document).ready(function () {
        initMyRoleMappingGrid();
        // update page title
        $(document).attr('title', "MIS - My Roles");
        loadNumberedMenu(MENU_ID_APPLICATION, "#role/showMyRole");
    });

    function initMyRoleMappingGrid() {
        initDataSource();
        $("#gridMyRole").kendoGrid({
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
                {field: "roleName", title: "Role", width: 250, sortable: true, filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}}
            ],
            filterable: {
                mode: "row"
            },
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridMyRole = $("#gridMyRole").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function initDataSource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/role/listMyRole",
                    dataType: "json",
                    type: "post"
                }
            },
            schema: {
                type: 'json',
                data: "list", total: "count",
                model: {
                    fields: {
                        id: { type: "number" },
                        roleName: { type: "string"},
                        userId: { type: "number"},
                        companyId: { type: "number"}
                    }
                },
                parse: function (data) {
                    checkIsErrorGridKendo(data);
                    return data;
                }
            },
            sort: {field: 'roleName', dir: 'asc'},  // default sort
            pageSize: ${com.athena.mis.BaseService.DEFAULT_RESULT_PER_PAGE},
            serverPaging: true,
            serverFiltering: true,
            serverSorting: true
        });
    }

    function reloadRequestMap() {
        if (!confirm('Are you sure you want to reload request map?')) {
            return;
        }
        showLoadingSpinner(true); // Spinner Show on AJAX Call
        $.ajax({
            url: "${createLink(controller: 'requestMap', action: 'reloadRequestMap')}",
            success: executePostConditionForReload,
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
        gridMyRole.dataSource.filter([]);
    }


    function executePostConditionForReload(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            populateLeftMenu(data.leftMenuData);
            loadNumberedMenu(MENU_ID_APPLICATION, "#role/showMyRole");
            showSuccess(data.message);
        }
    }

</script>

