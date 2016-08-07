<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <sec:access url="/testData/create">
        <li onclick="loadTestData()"><i class="fa fa-edit"></i>Load Test Data</li>
    </sec:access>
    <sec:access url="/testData/delete">
        <li onclick="truncateTestData()"><i class="fa fa-trash-o"></i>Delete Test Data</li>
    </sec:access>
    <sec:access url="/dbInstanceQuery/show">
        <li onclick="showQuery();"><i class="fa fa-search-plus"></i>Query</li>
    </sec:access>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script type="text/javascript">
    var gridTestData, gridMetaData, dropDownDbInstance, dbInstanceId, dropDownType;

    $(document).ready(function () {
        onLoadTableList();
    });

    function onLoadTableList() {
        initializeForm($("#testDataForm"), getTableList);

        // update page title
        $(document).attr('title', 'MIS - Load Test Data');
        loadNumberedMenu(MENU_ID_APPLICATION, "#testData/show");
    }

    function getTableList() {
        if (!validateForm($("#testDataForm"))) {   // check kendo validation
            return false;
        }
        dbInstanceId = dropDownDbInstance.value();
        var typeId = dropDownType.value();

        setButtonDisabled($('#search'), true);
        showLoadingSpinner(true);
        var params = "?dbInstanceId=" + dbInstanceId + "&typeId=" + typeId;
        var url = "${createLink(controller: 'testData', action: 'list')}" + params;
        populateGridKendo(gridTestData, url);
        clearGridKendo(gridMetaData);
        showLoadingSpinner(false);
        setButtonDisabled($('#search'), false);
    }

    function loadTestData() {
        var dbInstanceId = dropDownDbInstance.value();
        if (dbInstanceId == '') {
            showError('Select a db instance');
            return;
        }
        if (!confirm('Are you sure you want to create test data?\n' +
                        'If application has any previous data system will protect the data.\n' +
                        'Would you like to proceed?')) {
            return;
        }
        var url = "${createLink(controller: 'testData', action: 'create')}?dbInstanceId=" + dbInstanceId;
        showLoadingSpinner(true);
        $.ajax({
            url: url,
            type: 'post',
            dataType: 'json',
            success: executePostConditionForLoadData,
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus);
            },
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            }
        });
    }

    function executePostConditionForLoadData(data) {
        if (data.isError) {
            showError(data.message);
            return;
        }
        showSuccess(data.message);
        gridTestData.dataSource.read();
        gridTestData.refresh();
    }

    function truncateTestData() {
        var dbInstanceId = dropDownDbInstance.value();
        if (dbInstanceId == '') {
            showError('Select a db instance');
            return;
        }
        if (!confirm('Are you sure you want to delete all test data of Application plugin?')) {
            return;
        }
        var url = "${createLink(controller: 'testData', action: 'delete')}?dbInstanceId=" + dbInstanceId;
        showLoadingSpinner(true);
        $.ajax({
            url: url,
            type: 'post',
            dataType: 'json',
            success: executePostConditionForLoadData,
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus);
            },
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            }
        });
    }

    function onSelectTestTable() {
        var row = gridTestData.select();
        if (row.size() > 0) {
            var tableName = getSelectedValueFromGridKendo(gridTestData, 'table_name');
            var url = "${createLink(controller: 'schemaInformation', action: 'listSchemaInfo')}?tableName=" + tableName + "&dbInstanceId=" + dbInstanceId;
            populateGridKendo(gridMetaData, url);
            return;
        }
        clearGridKendo(gridMetaData);
    }

    function showQuery() {
        var dbInstanceId = dropDownDbInstance.value();
        if (dbInstanceId == '') {
            showError('Select a db instance');
            return;
        }
        showLoadingSpinner(true);
        var loc = "${createLink(controller: 'dbInstanceQuery', action: 'show')}?oId=" + dbInstanceId + "&url=testData/show";
        router.navigate(formatLink(loc));
        return false;
    }

    function reloadKendoGrid() {
        gridTestData.dataSource.filter([]);
    }

    function reloadKendoMetaDataGrid() {
        gridMetaData.dataSource.filter([]);
    }

</script>