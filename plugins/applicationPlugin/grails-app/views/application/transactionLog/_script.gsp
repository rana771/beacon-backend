<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <li onclick="showComment();"><i class="fa fa-comment-o"></i>Comments</li>
    <li onclick="showException();"><i class="fa fa-warning"></i>Exception</li>
    <li onclick="reloadGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script type="text/javascript">
    var isFileExport, entityId, entityTypeId, dataSource, gridTransactionLog, tableName, dataExportName, entityTypeName;

    $(document).ready(function () {
        onLoadTransactionLog();
        initGridTransactionLog();
    });

    function onLoadTransactionLog() {
        checkOnLoadError();

        entityTypeId = '${entityTypeId}';
        entityId = '${entityId}';
        tableName = '${tableName?tableName:""}';

        $(document).attr('title', "MIS - Transaction Log");
        loadNumberedMenu(${menuId}, "#${leftMenu}");
    }

    function checkOnLoadError() {
        var isError = '${isError}';
        var msg = '${message}';
        if (isError == 'true') {
            showError(msg);
            return false;
        }
    }

    function initDataSource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/transactionLog/list",
                    data: { entityId: entityId, entityTypeId: entityTypeId, tableName: tableName},
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
                        tableName: { type: "string" },
                        recordPerBatch: { type: "number" },
                        timeToRead: { type: "number" },
                        timeToWrite: { type: "number" },
                        startTime: { type: "number" },
                        endTime: { type: "number" },
                        totalRecord: { type: "number" },
                        exception: { type: "string" },
                        comment: { type: "string" }
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

    function initGridTransactionLog() {
        initDataSource();
        $("#gridTransactionLog").kendoGrid({
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
                {field: "totalRecord", title: "Total Records (Approx.)", width: 109, sortable: true, filterable: false, attributes: {class: "text-center"}, headerAttributes: {class: "text-center"}},
                {field: "tableName", title: "Table Name", width: 175, sortable: true, filterable: false},
                {field: "recordPerBatch", title: "Per Batch", width: 90, sortable: true, filterable: false, attributes: {class: "text-center"}, headerAttributes: {class: "text-center"}},
                {field: "timeToRead", title: "Read Time(sec)", width: 124, sortable: true, filterable: false, attributes: {class: "text-center"}, headerAttributes: {class: "text-center"}, template: "#= timeToRead/1000.00#"},
                {field: "timeToWrite", title: "Write Time(sec)", width: 125, sortable: true, filterable: false, attributes: {class: "text-center"}, headerAttributes: {class: "text-center"}, template: "#= timeToWrite/1000.00#"},
                {field: "processingTime", title: "Processing Time(sec)", width: 150, sortable: true, filterable: false, attributes: {class: "text-center"}, headerAttributes: {class: "text-center"}, template: "#= processingTime/1000.00#"},
                {field: "exception", title: "Exception", width: 80, sortable: true, filterable: false, attributes: {class: "text-center"}, headerAttributes: {class: "text-center"}, template: "#=exception?applyTxtCSS():'NO' #"},
                {field: "comment", title: "Comment", width: 130, sortable: true, filterable: false, template: "#=trimTextForKendo(comment,20)#"}
            ],
            filterable: {
                mode: "row"
            },

            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridTransactionLog = $("#gridTransactionLog").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function applyTxtCSS() {
        return "<b style='color:red'>" + 'YES' + "</b>";
    }

    function reloadGrid() {
        gridTransactionLog.dataSource.filter([]);
    }

    function clearLog() {
        var confirmMsg = "Following operations will be performed: \n" +
                "1. All logs will be deleted. \n" +
                "2. Target csv file(s) (if any) will be deleted.\n ";
        if (!confirm(confirmMsg + ' \n Are you sure you want to clear all logs now?')) {
            return false;
        }
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller: 'transactionLog', action: 'clear')}?entityTypeId=" + entityTypeId + "&entityId=" + entityId,
            success: executePostConditionForLog,
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


    function executePostConditionForLog(data) {
        if (data.isError) {
            showError(data.message);
            return;
        }
        showSuccess(data.message);
        reloadGrid();
    }

    function showComment() {
        if (executeCommonPreConditionForSelectKendo(gridTransactionLog, 'Transaction log') == false) {
            return false;
        }
        var comment = getSelectedValueFromGridKendo(gridTransactionLog, 'comment');
        if (comment) {
            alert(comment);
        } else {
            alert("Transaction log has no Comment!");
        }
        return false;
    }

    function showException() {
        if (executeCommonPreConditionForSelectKendo(gridTransactionLog, 'Transaction log') == false) {
            return false;
        }
        var exception = getSelectedValueFromGridKendo(gridTransactionLog, 'exception');
        if (exception) {
            alert(exception);
        } else {
            alert("Transaction log has no Exception!");
        }
        return false;
    }

</script>
