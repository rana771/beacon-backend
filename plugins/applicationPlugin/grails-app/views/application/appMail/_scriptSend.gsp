<%@ page import="com.athena.mis.BaseService" %>

<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <sec:access url="/appMail/reComposeAnnouncement">
        <li onclick="reCompose();"><i class="fa fa-envelope-o"></i>Re-Compose</li>
    </sec:access>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script language="javascript" type="text/javascript">

    var appMailGrid, appMailDataSource;

    $(document).ready(function () {
        onLoadAppMailPage();
        initAppMailGrid();
    });

    function onLoadAppMailPage() {
        // update page title
        $(document).attr('title', "Sent Mail");
        loadNumberedMenu(MENU_ID_APPLICATION, "#appMail/showForSend");
    }

    function reCompose() {
        if (executeCommonPreConditionForSelectKendo(appMailGrid, 'mail') == false) {
            return;
        }
        if (!confirm('Are you sure you want to re-compose the selected mail?')) {
            return;
        }
        var mailId = getSelectedIdFromGridKendo(appMailGrid);
        $.ajax({
            url: "${createLink(controller: 'appMail', action: 'reComposeAnnouncement')}?id=" + mailId,
            success: executePostConditionForReCompose,
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

    function executePostConditionForReCompose(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            showSuccess(data.message);
        }
    }

    function initDataSource() {
        appMailDataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/appMail/listForSend",
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
                        subject: {type: "string"},
                        sendTo: {type: "string"},
                        updatedOn: {type: "date"}
                    }
                },
                parse: function (data) {
                    checkIsErrorGridKendo(data);
                    return data;
                }
            },
            sort: {field: 'updatedOn', dir: 'desc'},  // default sort
            pageSize: ${BaseService.DEFAULT_RESULT_PER_PAGE},
            serverPaging: true,
            serverFiltering: true,
            serverSorting: true
        });
    }

    function initAppMailGrid() {
        initDataSource();
        $("#gridAppMail").kendoGrid({
            dataSource: appMailDataSource,
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
                {field: "subject", title: "Subject", width: 300, sortable: false, filterable: false},
                {
                    field: "updatedOn", title: "Send On", width: 150, sortable: false, filterable: false,
                    template: "#= kendo.toString(kendo.parseDate(updatedOn, 'yyyy-MM-dd'), 'dd MMMM, yyyy [hh:mm tt]') #"
                },
                {field: "sendTo", title: "Send To", width: 150, sortable: false, filterable: false}
            ],
            toolbar: kendo.template($("#gridToolbar").html())
        });
        appMailGrid = $("#gridAppMail").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function reloadKendoGrid() {
        appMailGrid.dataSource.filter([]);
    }

</script>