<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <sec:access url="/dbInstanceQuery/update">
        <li onclick="editDbInstanceQuery();"><i class="fa fa-edit"></i>Edit</li>
    </sec:access>
    <sec:access url="/dbInstanceQuery/delete">
        <li onclick="deleteDbInstanceQuery();"><i class="fa fa-trash-o"></i>Delete</li>
    </sec:access>
    <sec:access url="/dbInstanceQuery/listQueryResult">
        <li onclick="showQueryResult();"><i class="fa fa-database"></i>Query Result</li>
    </sec:access>
    <sec:access url="/dbInstanceQuery/execute">
        <li onclick="execute();"><i class="fa fa-play"></i>Execute</li>
    </sec:access>
    <li onclick="showMessage();"><i class="fa fa-comment"></i>Message</li>
    <sec:access url="/appNote/show">
        <li onclick="addNote();"><i class="fa fa-comment-o"></i>Note</li>
    </sec:access>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script type="text/x-kendo-template" id="gridToolbarDbTable">
<ul id="menuGridDbTable" class="kendoGridMenu">
    <li onclick="reloadDbTableGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script type="text/javascript">
    var dataSource, gridDbInstanceQuery, dbInstanceQueryModel, rpp, dbInstanceId, gridDbTableList,
            dbTableDataSource, entityTypeId, dropDownQueryType;

    $(document).ready(function () {
        onLoadDbInstanceQuery();
        initDbInstanceQueryGird();
        initObservable();
        initDbTableGrid();
    });

    function onLoadDbInstanceQuery() {
        checkOnLoadError();
        initializeForm($('#dbInstanceQueryForm'), onSubmitDbInstanceQuery);

        $('#resultPerPage').kendoNumericTextBox({
            min: 1,
            max: 500,
            decimals: 0,
            format: "#"
        });
        rpp = $("#resultPerPage").data("kendoNumericTextBox");

        dbInstanceId = ${appDbInstance.id};
        entityTypeId = $("#entityTypeId").val();

        $(document).attr('title', "MIS - Create DB Instance Query");
        loadNumberedMenu(MENU_ID_APPLICATION, "#appDbInstance/show");
    }

    function checkOnLoadError() {
        var isError = '${isError}';
        var msg = '${message}';
        if (isError == 'true') {
            showError(msg);
            return false;
        }
    }

    function onSubmitDbInstanceQuery() {
        if (!validateForm($('#dbInstanceQueryForm'))) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'dbInstanceQuery', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'dbInstanceQuery', action:  'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#dbInstanceQueryForm").serialize(),
            url: actionUrl,
            success: function (data, textStatus) {
                executePostCondition(data);
                setButtonDisabled($('#create'), false);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus);
            },
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json'
        });
        return false;
    }

    function executePostCondition(data) {
        if (data.isError) {
            showError(data.message);
            showLoadingSpinner(false);
        } else {
            var newEntry = data.dbInstanceQuery;
            if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                var gridData = gridDbInstanceQuery.dataSource.data();
                gridData.unshift(newEntry);
            } else if (newEntry != null) { // updated existing
                var selectedRow = gridDbInstanceQuery.select();
                var allItems = gridDbInstanceQuery.items();
                var selectedIndex = allItems.index(selectedRow);
                gridDbInstanceQuery.removeRow(selectedRow);
                gridDbInstanceQuery.dataSource.insert(selectedIndex, newEntry);
            }
            resetDbInstanceQueryForm();
            showSuccess(data.message);
        }
    }

    function resetDbInstanceQueryForm() {
        clearForm($("#dbInstanceQueryForm"));
        initObservable();
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function initDbInstanceQueryGird() {
        initDataSource();
        $("#gridDbInstanceQuery").kendoGrid({
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
                {
                    field: "name", title: "Name", width: 200, sortable: true,
                    template: "#= makeBoldIfReserved(isReserved, name) #",
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                },
                {
                    field: "sqlQuery", title: "SQL Query", width: 300, filterable: false,
                    template: "#=trimTextForKendo(sqlQuery,100)#"
                },
                {field: "numberOfExecution", title: "No. Of Execution", width: 100, filterable: false}
            ],
            filterable: {
                mode: "row"
            },
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridDbInstanceQuery = $("#gridDbInstanceQuery").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function makeBoldIfReserved(isReserved, name) {
        if (isReserved) {
            return "<b>" + name + "</b>";
        }
        return name;
    }

    function editDbInstanceQuery() {
        if (executeCommonPreConditionForSelectKendo(gridDbInstanceQuery, 'Query') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridDbInstanceQuery'));
        var dataFeedQueryObj = getSelectedObjectFromGridKendo(gridDbInstanceQuery);
        showDbInstanceQuery(dataFeedQueryObj);
    }

    function showDbInstanceQuery(dataFeedQueryObj) {
        if (dataFeedQueryObj.queryTypeId == 0) {
            dataFeedQueryObj.queryTypeId = "";
        }
        dbInstanceQueryModel.set('dbInstanceQuery', dataFeedQueryObj);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function deleteDbInstanceQuery() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true); // Spinner Show on AJAX Call
        var id = getSelectedIdFromGridKendo(gridDbInstanceQuery);
        $.ajax({
            url: "${createLink(controller: 'dbInstanceQuery', action: 'delete')}?id=" + id,
            success: executePostConditionForDelete,
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus);
            },
            complete: onCompleteAjaxCall,    // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForDelete(data) {
        if (data.isError) {
            showError(data.message);
            return false;
        }
        var row = gridDbInstanceQuery.select();
        row.each(function () {
            gridDbInstanceQuery.removeRow($(this));
        });
        resetDbInstanceQueryForm();
        showSuccess(data.message);
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelectKendo(gridDbInstanceQuery, 'Query') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected Query?')) {
            return false;
        }
        return true;
    }

    function showQueryResult() {
        if (executeCommonPreConditionForSelectKendo(gridDbInstanceQuery, 'Query') == false) {
            return;
        }
        var id = getSelectedIdFromGridKendo(gridDbInstanceQuery);
        var loc = "${createLink(controller: 'dbInstanceQuery', action: 'showQueryResult')}?cId=" + id + "&pId=" + dbInstanceId + "&pUrl=appDbInstance/show" + "&url=dbInstanceQuery/show";
        router.navigate(formatLink(loc));
    }

    function initDataSource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/dbInstanceQuery/list",
                    data: {dbInstanceId: dbInstanceId},
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
                        version: {type: "number"},
                        name: {type: "string"},
                        sqlQuery: {type: "string"},
                        numberOfExecution: {type: "number"},
                        resultPerPage: {type: "number"},
                        message: {type: "string"},
                        queryTypeId: {type: "number"}
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
            <g:if test="${cId!=null}">
            , filter: {field: "id", operator: "equal", value: ${cId}}
            </g:if>
        });
    }

    function initObservable() {
        dbInstanceQueryModel = kendo.observable(
                {
                    dbInstanceQuery: {
                        id: "",
                        version: "",
                        name: "",
                        sqlQuery: "",
                        dbInstanceId: dbInstanceId,
                        resultPerPage: 10,
                        queryTypeId: ""
                    }
                }
        );
        kendo.bind($("#application_top_panel"), dbInstanceQueryModel);
    }

    function execute() {
        if (executeCommonPreConditionForSelectKendo(gridDbInstanceQuery, 'Query') == false) {
            return;
        }
        if (!confirm('Are you sure you want to execute the selected query?')) {
            return;
        }

        showLoadingSpinner(true);	// Spinner Show on AJAX Call
        var dataFeedQueryId = getSelectedIdFromGridKendo(gridDbInstanceQuery);
        $.ajax({
            url: "${createLink(controller: 'dbInstanceQuery', action: 'execute')}?id=" + dataFeedQueryId,
            success: executePostConditionForExecute,
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus);
            },
            complete: onCompleteAjaxCall,    // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForExecute(data) {
        if (data.isError) {
            showLoadingSpinner(false);
            showError(data.message);
            return;
        }
        var newEntry = data.dbInstanceQuery;
        var selectedRow = gridDbInstanceQuery.select();
        var allItems = gridDbInstanceQuery.items();
        var selectedIndex = allItems.index(selectedRow);
        gridDbInstanceQuery.removeRow(selectedRow);
        gridDbInstanceQuery.dataSource.insert(selectedIndex, newEntry);
        $('#modalLabelQueryExecuteMessage').html(data.message); // set query execute message in Modal form
        $('#viewQueryExecuteMessageModal').modal('show');       // show Modal
        resetDbInstanceQueryForm();
    }

    function closeQueryExecuteMessageModal() {
        $('#modalLabelQueryExecuteMessage').text('');       // clean query execute message in Modal form
        $('#viewQueryExecuteMessageModal').modal('hide');   // hide modal
    }

    function showMessage() {
        if (executeCommonPreConditionForSelectKendo(gridDbInstanceQuery, 'Query') == false) {
            return;
        }
        var dbInstanceQuery = getSelectedValueFromGridKendo(gridDbInstanceQuery);
        if (dbInstanceQuery.numberOfExecution == 0) {
            showInfo('Selected query has not been executed yet');
            return;
        }
        $('#modalLabelQueryExecuteMessage').html(dbInstanceQuery.message); // set query execute message in Modal form
        $('#viewQueryExecuteMessageModal').modal('show');       // show Modal
    }

    function addNote() {
        if (executeCommonPreConditionForSelectKendo(gridDbInstanceQuery, 'Query') == false) {
            return;
        }
        showLoadingSpinner(true);
        var queryId = getSelectedIdFromGridKendo(gridDbInstanceQuery);
        var loc = "${createLink(controller: 'appNote', action: 'show')}?cId=" + queryId + "&pId=" + dbInstanceId + "&pUrl=appDbInstance/show" + "&entityTypeId=" + entityTypeId + "&url=dbInstanceQuery/show";
        router.navigate(formatLink(loc));
        return false;
    }

    function reloadKendoGrid() {
        gridDbInstanceQuery.dataSource.filter([]);
    }

    function initDbTableGridDataSource() {
        dbTableDataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/appDbInstance/listDbTable?id=" + dbInstanceId,
                    dataType: "json",
                    type: "post"
                }
            },
            schema: {
                type: 'json',
                data: "list", total: "count",
                model: {
                    fields: {
                        table_name: {type: "string"},
                        table_rows: {type: "number"}
                    }
                },
                parse: function (data) {
                    checkIsErrorGridKendo(data);
                    return data;
                }
            },
            sort: {field: 'table_name', dir: 'asc'},  // default sort
            pageSize: ${com.athena.mis.BaseService.DEFAULT_RESULT_PER_PAGE},
            serverPaging: false,
            serverFiltering: false,
            serverSorting: false
        });
    }

    function initDbTableGrid() {
        initDbTableGridDataSource();
        $("#gridDbTableList").kendoGrid({
            dataSource: dbTableDataSource,
            height: getFullGridHeight() + 80,
            selectable: true,
            sortable: true,
            resizable: true,
            reorderable: true,
            pageable: {
                buttonCount: 1,
                previousNext: false,
                numeric: true
            },
            columns: [
                {
                    field: "table_name", title: "Table", width: 100, sortable: true,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                },
                {field: "table_rows", title: "Count", width: 30, sortable: true, filterable: false}
            ],
            filterable: {
                mode: "row"
            },
            toolbar: kendo.template($("#gridToolbarDbTable").html())
        });
        gridDbTableList = $("#gridDbTableList").data("kendoGrid");
        $("#menuGridDbTable").kendoMenu();
    }

    function reloadDbTableGrid() {
        populateGridKendo(gridDbTableList, '/appDbInstance/listDbTable?id=' + dbInstanceId);
    }

    function executeQuery() {
        var strQuery = $("#sqlQuery").val().trim();
        if (strQuery == '') {
            showError("Write query to execute");
            return;
        }
        showLoadingSpinner(true);	// Spinner Show on AJAX Call
        $.ajax({
            url: "${createLink(controller: 'dbInstanceQuery', action: 'executeQuery')}?",
            data: {
                dbInstanceId:dbInstanceId,
                sqlQuery: $("#sqlQuery").val().trim()
            },
            success: executePostConditionForExecuteQuery,
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus);
            },
            complete: onCompleteAjaxCall,    // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForExecuteQuery(data) {
        if (data.isError) {
            showLoadingSpinner(false);
            showError(data.message);
            return;
        }
        $('#modalLabelQueryExecuteMessage').html(data.message); // set query execute message in Modal form
        $('#viewQueryExecuteMessageModal').modal('show');       // show Modal
    }

</script>

