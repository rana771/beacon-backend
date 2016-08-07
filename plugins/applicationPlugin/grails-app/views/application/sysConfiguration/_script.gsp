<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <app:ifAllUrl urls="/systemConfiguration/update">
        <li onclick="editSysConfiguration();"><i class="fa fa-edit"></i>Edit</li>
    </app:ifAllUrl>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script type="text/javascript">
    var systemConfigurationGrid, sysConfigDataSource, systemConfigurationModel, pluginId;

    $(document).ready(function () {
        pluginId = ${pluginId};
        onLoadSysConfigurationPage();
        initSystemConfigurationGrid();
        initObservable();
    });

    function onLoadSysConfigurationPage() {
        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#sysConfigurationForm"), onSubmitSysConfiguration);
        // update page title
        $(document).attr('title', "MIS - Edit System configuration Information");
        loadMenu(pluginId);
    }

    function executePreCondition() {
        if (!validateForm($("#sysConfigurationForm"))) {
            return false;
        }
        if ($('#id').val().isEmpty()) {
            showError('Please select a system configuration to update');
            return false;
        }
        return true;
    }

    function onSubmitSysConfiguration() {
        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);

        jQuery.ajax({
            type: 'post',
            data: jQuery("#sysConfigurationForm").serialize(),
            url: "${createLink(controller: 'systemConfiguration', action: 'update')}",
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
                var newEntry = result.systemConfiguration;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = systemConfigurationGrid.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = systemConfigurationGrid.select();
                    var allItems = systemConfigurationGrid.items();
                    var selectedIndex = allItems.index(selectedRow);
                    systemConfigurationGrid.removeRow(selectedRow);
                    systemConfigurationGrid.dataSource.insert(selectedIndex, newEntry);
                }
                resetForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        initObservable();
        clearForm($("#sysConfigurationForm"), $('#value'));
    }

    function editSysConfiguration() {
        if (executeCommonPreConditionForSelectKendo(systemConfigurationGrid, 'system configuration') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridSystemConfiguration'));
        var sysConfigurationObj = getSelectedObjectFromGridKendo(systemConfigurationGrid);
        showSysConfiguration(sysConfigurationObj);
    }

    function reloadKendoGrid() {
        initSystemConfigurationGrid().dataSource.filter([]);
    }

    function showSysConfiguration(sysConfigurationObj) {
        systemConfigurationModel.set('systemConfiguration', sysConfigurationObj);
    }

    function initDataSource() {
        sysConfigDataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/systemConfiguration/list",
                    dataType: "json",
                    data: {pluginId: pluginId},
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
                        key: {type: "string"},
                        transactionCode: {type: "string"},
                        value: {type: "string"},
                        description: {type: "string"},
                        message: {type: "string"}
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

    function initSystemConfigurationGrid() {
        initDataSource();
        $("#gridSystemConfiguration").kendoGrid({
            dataSource: sysConfigDataSource,
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
                    field: "key", title: "Key", width: 100, sortable: false,
                    filterable: {cell: {operator: "contains", showOperators: false, dataSource: getBlankDataSource()}}
                },
                {field: "value", title: "Value", width: 50, sortable: false, filterable: false},
                {field: "description", title: "Description", width: 200, sortable: false, filterable: false}
            ],
            filterable: {
                mode: "row"
            },

            toolbar: kendo.template($("#gridToolbar").html())
        });
        systemConfigurationGrid = $("#gridSystemConfiguration").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function initObservable() {
        systemConfigurationModel = kendo.observable(
                {
                    systemConfiguration: {
                        id: "",
                        version: "",
                        key: "",
                        value: "",
                        description: "",
                        transactionCode: "",
                        message: ""
                    }
                }
        );
        kendo.bind($("#application_top_panel"), systemConfigurationModel);
    }
    <%-- Load Menu and set left-menu selected  --%>
    function loadMenu(pluginId) {
        var menuId = getMenuIdByPluginId(pluginId);  // load menu
        loadNumberedMenu(menuId, "#systemConfiguration/show?plugin=" + pluginId); // set left-menu selected
    }

</script>
