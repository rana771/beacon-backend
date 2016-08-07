<%@ page import="com.athena.mis.BaseService" %>

<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <sec:access url="/appSms/reCompose">
        <li onclick="reCompose();"><i class="fa fa-edit"></i>Re-Compose</li>
    </sec:access>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script language="javascript" type="text/javascript">

    var smsGrid, smsDataSource;

    $(document).ready(function () {
        onLoadSmsPage();
        initSmsGrid();
    });

    function onLoadSmsPage() {
        // update page title
        $(document).attr('title', "Sent SMS");
        loadNumberedMenu(MENU_ID_APPLICATION, "#appSms/showForSend");
    }

    function reCompose() {
        if (executeCommonPreConditionForSelectKendo(smsGrid, 'sms') == false) {
            return;
        }
        if (!confirm('Are you sure you want to re-compose the selected sms?')) {
            return;
        }
        var smsId = getSelectedIdFromGridKendo(smsGrid);
        $.ajax({
            url: "${createLink(controller: 'appSms', action: 'reCompose')}?id=" + smsId,
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
        smsDataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/appSms/listForSend",
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
                        body: {type: "string"},
                        sendBy: {type: "string"},
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

    function initSmsGrid() {
        initDataSource();
        $("#gridSms").kendoGrid({
            dataSource: smsDataSource,
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
                {field: "sendBy", title: "Send By", width: 150, sortable: false, filterable: false},
                {
                    field: "updatedOn", title: "Send On", width: 150, sortable: false, filterable: false,
                    template: "#= kendo.toString(kendo.parseDate(updatedOn, 'yyyy-MM-dd'), 'dd MMMM, yyyy [hh:mm tt]') #"
                },
                {field: "body", title: "Body", width: 300, sortable: false, filterable: false}
            ],
            toolbar: kendo.template($("#gridToolbar").html())
        });
        smsGrid = $("#gridSms").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function reloadKendoGrid() {
        smsGrid.dataSource.filter([]);
    }

</script>