<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <li onclick="doSelectAll();"><i class="fa fa-check-square-o"></i>Select All</li>
    <li onclick="doDeselectAll();"><i class="fa fa-square-o"></i>Deselect All</li>
    <app:checkSystemUser isConfigManager="true">
        <sec:access url="/appUser/forceLogoutOnlineUser">
            <li onclick="forceLogout();"><i class="fa fa-sign-out"></i>Force Logout</li>
        </sec:access>
    </app:checkSystemUser>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script type="text/javascript">
    var gridOnlineUser, dataSource;

    $(document).ready(function () {

        initOnlineUserGrid();

        // update page title
        $('span.headingText').html('Online Users');
        $('#icon_box').attr('class', 'pre-icon-header online_user');
        $(document).attr('title', "MIS - Online Users");
        loadNumberedMenu(MENU_ID_APPLICATION, "#appUser/showOnlineUser");
    });

    function iniDataSource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/appUser/listOnlineUser",
                    dataType: "json",
                    type: "post"
                }
            },
            schema: {
                type: 'json',
                data: "list", total: "count",
                model: {
                    fields: {
                        id:{type:"string"},
                        userId: {type: "number"},
                        userName: {type: "string"},
                        loginId: {type: "string"},
                        loginTime: {type: "string"},
                        ipAddress: {type: "string"},
                        browser: {type: "string"},
                        operatingSystem: {type: "string"},
                        lastActivity: {type: "string"}
                    }
                },
                parse: function (data) {
                    checkIsErrorGridKendo(data);
                    return data;
                }
            },
//            sort: {field: 'userName', dir: 'asc'},  // default sort
            pageSize: 15,
            serverPaging: true,
            serverFiltering: true,
            serverSorting: true
        });
    }

    function initOnlineUserGrid() {
        iniDataSource();
        $("#gridOnlineUser").kendoGrid({
            dataSource: dataSource,
            height: getGridHeightKendo(),
            selectable: "multiple",
            sortable: true,
            resizable: true,
            reorderable: true,
            pageable: {
                refresh: false,
                pageSizes: [10, 15, 20],
                buttonCount: 4
            },
            columns: [
                {field: "userName", title: "User Name", width: 120, sortable: false, filterable: false},
                {field: "loginId",title: "Login ID", width: 120,sortable: false, filterable: false},
                {field: "loginTime",title: "Login Time",width: 120,sortable: false, filterable: false},
                {field: "ipAddress",title: "IP Address",width: 75,sortable: false, filterable: false},
                {field: "browser", title: "Browser", width: 100, sortable: false, filterable: false},
                {field: "operatingSystem", title: "Operating System", width: 90, sortable: false, filterable: false},
                {field: "lastActivity", title: "Last Activity", width: 100, sortable: false, filterable: false}
            ],
            filterable: {
                mode: "row"
            },
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridOnlineUser = $("#gridOnlineUser").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function doSelectAll() {
        return selectAllFromGridKendo(gridOnlineUser)
    }

    function doDeselectAll() {
        return deSelectAllFromGridKendo(gridOnlineUser)
    }

    function executePreConditionForExpire() {
        if (executeCommonPreConditionForSelectKendo(gridOnlineUser, 'user') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to force logout selected user?')) {
            return false;
        }
        return true;
    }

    function forceLogout() {
        if (executePreConditionForExpire() == false) {
            return;
        }
        showLoadingSpinner(true);
        var ids = getSelectedIdFromGridKendo(gridOnlineUser);
        jQuery.ajax({
            type: 'post',
            url: "${createLink(controller:'appUser', action: 'forceLogoutOnlineUser')}?ids=" + ids,
            success: function (data, textStatus) {
                executePostConditionForExpire(data);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus);
            },
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json'
        });
    }

    function reloadKendoGrid() {
        gridOnlineUser.dataSource.filter([]);
    }

    function executePostConditionForExpire(data) {
        if (data.isError) {
            showError(data.message);
            return false;
        }
        var row = gridOnlineUser.select();
        row.each(function () {
            gridOnlineUser.removeRow($(this));
        });
        showSuccess(data.message);
    }
</script>
<div class="container-fluid">
    <div class="row">
        <div id="gridOnlineUser"></div>
    </div>
</div>

