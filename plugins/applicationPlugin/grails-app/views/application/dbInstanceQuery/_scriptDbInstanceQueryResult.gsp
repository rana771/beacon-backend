<style>
#gridDocSqlResult col, #gridDocSqlResult td, #gridDocSqlResult th {
    width: 150px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}
</style>
<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <li><i class="fa fa-download"></i>Download
        <ul>
            <li onclick="downloadCsv();"><i class="fa fa-file-archive-o"></i>CSV</li>
        </ul>
    </li>
</ul>
</script>

<script type="text/javascript">
    var dbInstanceQueryId,gridDocSqlResult,gridHeight=0;

    $(document).ready(function () {
        if(${isError}) {
            showError('${message}');
            return;
        }
        var noteCount = ${noteCount};
        if (noteCount > 0) {
            $('#lstNotes').show();
            $('#lstNotes').height(250);
            gridHeight = $('#lstNotes').height();
            $('#lstBacklogNotes').height(170);
        }
        dbInstanceQueryId = '${dbInstanceQuery?.id}';
        initResultGrid();
        $(document).attr('title', "APP - Show Query Result");
        loadNumberedMenu(MENU_ID_APPLICATION, "#appDbInstance/show");
    });

    function initResultGrid() {
        $("#gridDocSqlResult").kendoGrid({
            dataSource: {
                transport: {
                    read: {
                        url: "/dbInstanceQuery/listQueryResult?dbInstanceQueryId=" + dbInstanceQueryId,
                        dataType: "json",
                        type: "post"
                    }
                },
                requestStart:showLoadingSpinner(true),
                schema: {
                    type: 'json',
                    data:'lstResult',
                    total: 'count',
                    parse: function (data) {
                        checkIsErrorGridKendo(data);
                        showLoadingSpinner(false);
                        return data;
                    }
                },
                pageSize: ${dbInstanceQuery?.resultPerPage}
            },
            pageable: {
                refresh: false,
                pageSizes: [10, 15, 20],
                buttonCount: 4
            },
            height: getGridHeightKendo()-gridHeight,
            selectable: false,
            sortable: false,
            resizable: true,
            reorderable: false,
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridDocSqlResult =$("#gridDocSqlResult").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function downloadCsv() {
        if(gridDocSqlResult.items().size()==0) {
            showError('Query result is not available');
            return false;
        }
        if (confirm('Do you want to download the CSV now?')) {
            var url = "${createLink(controller: 'dbInstanceQuery', action: 'downloadQueryResultCsv')}?dbInstanceQueryId=" + dbInstanceQueryId;
            document.location = url;
        }
    }

</script>
