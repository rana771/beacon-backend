<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <sec:access url="/appServerInstance/update">
        <li onclick="editServerInstance();"><i class="fa fa-edit"></i>Edit</li>
    </sec:access>
    <sec:access url="/appServerInstance/delete">
        <li onclick="deleteServerInstance();"><i class="fa fa-trash-o"></i>Delete</li>
    </sec:access>
    <sec:access url="/appServerInstance/testServerConnection">
        <li onclick="testDBConnection();"><i class="fa fa-check-square-o"></i>Test Connection</li>
    </sec:access>
    <sec:access url="/appServerDbInstanceMapping/show">
        <li onclick="addServerDbInstance();"><i class="fa fa-database"></i>Database</li>
    </sec:access>
    <sec:access url="/appShellScript/show">
        <li onclick="showConsole();"><i class="fa fa-keyboard-o"></i>Console</li>
    </sec:access>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script language="javascript">
    var gridServerInstance, sshPort;
    var dataSource, serverInstanceModel;

    $(document).ready(function () {
        onLoadServerInstancePage();
        initServerInstanceGrid();
        initObservable();
    });

    function onLoadServerInstancePage() {
        // initialize form with kendo validator & bind onSubmit method
        initializeForm($("#serverInstanceForm"), onSubmitServerInstance);

        $('#sshPort').kendoNumericTextBox({
            min: 0,
            max: 999999,
            format: "#",
            decimals: 0,
            step: 1
        });
        sshPort = $("#sshPort").data("kendoNumericTextBox");
        // update page title
        $(document).attr('title', "MIS - Create Server Instance");
        loadNumberedMenu(MENU_ID_APPLICATION, "#appServerInstance/show");
    }

    function executePreCondition() {
        if (!validateForm($("#serverInstanceForm"))) {   // check kendo validation
            return false;
        }
        return true;
    }

    function onSubmitServerInstance() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'appServerInstance', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'appServerInstance', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#serverInstanceForm").serialize(),
            url: actionUrl,
            success: function (data, textStatus) {
                executePostCondition(data);
                setButtonDisabled($('#create'), false);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus)
            },
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json'
        });
        return false;
    }

    function executePostCondition(result) {
        if (result.isError) {
            showError(result.message);
            showLoadingSpinner(false);
        } else {
            try {
                var newEntry = result.serverInstance;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = gridServerInstance.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = gridServerInstance.select();
                    var allItems = gridServerInstance.items();
                    var selectedIndex = allItems.index(selectedRow);
                    gridServerInstance.removeRow(selectedRow);
                    gridServerInstance.dataSource.insert(selectedIndex, newEntry);
                }
                resetForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        // clear all errors, validation messages & form values and bind onFocus method
        clearForm($("#serverInstanceForm"), $('#name'));
        initObservable();
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function addServerDbInstance() {
        if (executeCommonPreConditionForSelectKendo(gridServerInstance, 'Server Instance') == false) {
            return;
        }
        var appServerInstance = getSelectedObjectFromGridKendo(gridServerInstance);
        if (!appServerInstance.isTested) {
            showError('This instance connection is not tested.');
            return;
        }
        showLoadingSpinner(true);
        var loc = "${createLink(controller:'appServerDbInstanceMapping', action: 'show')}?oId=" + appServerInstance.id + "&url=appServerInstance/show";
        router.navigate(formatLink(loc));
        return false;
    }

    function showConsole() {
        if (executeCommonPreConditionForSelectKendo(gridServerInstance, 'Server Instance') == false) {
            return;
        }
        var appServerInstance = getSelectedObjectFromGridKendo(gridServerInstance);
        if (!appServerInstance.isTested) {
            showError('This instance connection is not tested.');
            return;
        }
        showLoadingSpinner(true);
        var loc = "${createLink(controller:'appShellScript', action: 'show')}?oId=" + appServerInstance.id + "&url=appServerInstance/show";
        router.navigate(formatLink(loc));
        return false;
    }

    function reloadKendoGrid() {
        gridServerInstance.dataSource.filter([]);
    }

    function deleteServerInstance() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true); // Spinner Show on AJAX Call
        var serverInstanceId = getSelectedIdFromGridKendo(gridServerInstance);
        $.ajax({
            url: "${createLink(controller:'appServerInstance', action: 'delete')}?id=" + serverInstanceId,
            success: executePostConditionForDelete,
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

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelectKendo(gridServerInstance, 'Server Instance') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected Server Instance?')) {
            return false;
        }
        return true;
    }

    <%-- removing selected row and clean input form --%>
    function executePostConditionForDelete(data) {
        if (data.isError) {
            showError(data.message);
            return false;
        }
        var row = gridServerInstance.select();
        row.each(function () {
            gridServerInstance.removeRow($(this));
        });
        resetForm();
        showSuccess(data.message);
    }

    function editServerInstance() {
        if (executeCommonPreConditionForSelectKendo(gridServerInstance, 'Server Instance') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridServerInstance'));
        resetForm();
        showLoadingSpinner(true);
        var serverInstance = getSelectedObjectFromGridKendo(gridServerInstance);
        showServerInstance(serverInstance);
        showLoadingSpinner(false);
    }

    function showServerInstance(serverInstance) {
        serverInstanceModel.set('serverInstance', serverInstance);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function testDBConnection() {
        if (executeCommonPreConditionForSelectKendo(gridServerInstance, 'Server Instance') == false) {
            return;
        }
        showLoadingSpinner(true);
        var id = getSelectedIdFromGridKendo(gridServerInstance);
        $.ajax({
            url: "${createLink(controller: 'appServerInstance', action: 'testServerConnection')}?id=" + id,
            success: executePostConditionForTestConnection,
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus);
            },
            complete: onCompleteAjaxCall,    // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForTestConnection(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            showSuccess(data.message);
        }
        var serverInstance = data.serverInstance;
        var selectedRow = gridServerInstance.select();
        var allItems = gridServerInstance.items();
        var selectedIndex = allItems.index(selectedRow);
        gridServerInstance.removeRow(selectedRow);
        gridServerInstance.dataSource.insert(selectedIndex, serverInstance);
        resetForm();
    }

    function initDatasource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/appServerInstance/list",
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
                        isTested: {type: "boolean"},
                        isNative: {type: "boolean"},
                        name: {type: "string"},
                        sshHost: {type: "string"},
                        sshPort: {type: "number"},
                        sshUserName: {type: "string"},
                        sshPassword: {type: "string"},
                        osVendorId: {type: "number"},
                        osVendorName: {type: "string"}
                    }
                },
                parse: function (data) {
                    checkIsErrorGridKendo(data);
                    return data;
                }
            },
            sort: {field: 'name', dir: 'asc'},  // default sort
            pageSize: ${com.athena.mis.BaseService.DEFAULT_RESULT_PER_PAGE},
            serverPaging: true,
            serverFiltering: true,
            serverSorting: true
            <g:if test="${oId!=null}">
            ,filter: { field: "id", operator: "equal", value: ${oId} }
            </g:if>
        });
    }

    function initServerInstanceGrid() {
        initDatasource();
        $("#gridServerInstance").kendoGrid({
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
                    field: "name", title: "Name", width: 180, sortable: true,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}},
                    template: "#=isNative?applyTxtCSS(name, true):applyTxtCSS(name, false) #"
                },
                {field: "sshHost", title: "SSH Host", width: 120, sortable: true, filterable: false},
                {field: "sshPort", title: "SSH Port", width: 120, sortable: false, filterable: false},
                {field: "sshUserName", title: "SSH User", width: 120, sortable: false, filterable: false},
                {field: "osVendorName", title: "OS Vendor", width: 120, sortable: false, filterable: false},
                {
                    field: "isTested", title: "Connection Tested", width: 120, sortable: false, filterable: false,
                    template: "#= isTested ? 'YES' :'NO' #"
                }
            ],
            filterable: {mode: "row"},
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridServerInstance = $("#gridServerInstance").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function applyTxtCSS(name, isTrue) {
        if (name && isTrue == true) {
            return "<b>" + name + "</b>";
        }
        return name
    }

    function initObservable() {
        serverInstanceModel = kendo.observable(
                {
                    serverInstance: {
                        id: "",
                        version: "",
                        name: "",
                        sshHost: "",
                        sshPort: 22,
                        sshUserName: "",
                        sshPassword: "",
                        osVendorId: "",
                        osVendorName: ""
                    }
                }
        );
        kendo.bind($("#application_top_panel"), serverInstanceModel);
    }

</script>