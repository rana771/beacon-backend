<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <app:ifAllUrl urls="/appServerDbInstanceMapping/update">
        <li onclick="editAppServerDbInstance();"><i class="fa fa-edit"></i>Edit</li>
    </app:ifAllUrl>
    <sec:access url="/appServerDbInstanceMapping/delete">
        <li onclick="deleteAppServerDbInstanceMapping();"><i class="fa fa-trash-o"></i>Delete</li>
    </sec:access>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script language="javascript">
    var appServerInstanceId, dbVendorId, appServerDbInstanceMappingModel, dataSource, gridServerMapping, dropDownDbInstance;

    $(document).ready(function () {
        onLoadAppServerDbInstanceMappingPage();
        initAppServerDbInstanceMappingGrid();
        initObservable();
    });

    function onLoadAppServerDbInstanceMappingPage() {
        checkOnLoadError();
        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#appServerMappingForm"), onSubmitAppServerDbInstanceMapping);

        // update page title
        $(document).attr('title', "MIS - Create Server Mapping");
        loadNumberedMenu(MENU_ID_APPLICATION, "#appServerInstance/show");
    }

    function checkOnLoadError() {
        var isError = '${isError}';
        var msg = '${message}';
        if (isError == 'true') {
            showError(msg);
            return false;
        } else {
            appServerInstanceId = '${appServerInstanceId?appServerInstanceId:"0"}';
            dbVendorId = '${dbVendorId?dbVendorId:"0"}';
        }
    }

    function executePreConditionForSubmit() {
        if (!validateForm($("#appServerMappingForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitAppServerDbInstanceMapping() {
        if (executePreConditionForSubmit() == false) {
            return false;
        }
        setButtonDisabled($('#create'), true);
        // Spinner Show on AJAX Call
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'appServerDbInstanceMapping', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'appServerDbInstanceMapping', action: 'update')}";
        }
        jQuery.ajax({
            type: 'post',
            data: jQuery("#appServerMappingForm").serialize(),
            url: actionUrl,
            success: function (data, textStatus) {
                executePostConditionForSubmit(data);
                setButtonDisabled($('#create'), false);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus);
            },
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);  // stop loading spinner
            },
            dataType: 'json'
        });
        return false;
    }


    function executePostConditionForSubmit(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            try {
                var newEntry = data.appServerDbInstanceMapping;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = gridServerMapping.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = gridServerMapping.select();
                    var allItems = gridServerMapping.items();
                    var selectedIndex = allItems.index(selectedRow);
                    gridServerMapping.removeRow(selectedRow);
                    gridServerMapping.dataSource.insert(selectedIndex, newEntry);
                }
                resetForm();
                showSuccess(data.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        clearForm($("#appServerMappingForm"), $('#appServerInstanceId'));
        initObservable();
        $('#appDbInstanceId').attr('default_value', '');
        $('#appDbInstanceId').reloadMe();
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function deleteAppServerDbInstanceMapping() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true); // Spinner Show on AJAX Call
        var appServerInstanceMappingId = getSelectedIdFromGridKendo(gridServerMapping);
        $.ajax({
            url: "${createLink(controller:'appServerDbInstanceMapping', action:  'delete')}?id=" + appServerInstanceMappingId,
            success: executePostConditionForDelete,
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus);
            },
            complete: onCompleteAjaxCall,    // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function reloadKendoGrid() {
        gridServerMapping.dataSource.filter([]);
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelectKendo(gridServerMapping, ' Server Instance') == false) {
            return false;
        }
        if (!confirm('Are you sure to delete the selected Server Instance?')) {
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
        var row = gridServerMapping.select();
        row.each(function () {
            gridServerMapping.removeRow($(this));
        });
        resetForm();
        showSuccess(data.message);
    }

    function editAppServerDbInstance() {
        if (executeCommonPreConditionForSelectKendo(gridServerMapping, 'Server DB Instance') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridServerMapping'));
        var serverDbInstanceObj = getSelectedObjectFromGridKendo(gridServerMapping);
        showServerDbInstance(serverDbInstanceObj);
    }

    function showServerDbInstance(serverDbInstanceObj) {
        appServerDbInstanceMappingModel.set('appServerDbInstanceMapping', serverDbInstanceObj);
        $('#appDbInstanceId').attr('default_value', serverDbInstanceObj.appDbInstanceId);
        $('#appDbInstanceId').reloadMe();
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
    }

    function initDataSource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/appServerDbInstanceMapping/list",
                    data: {appServerInstanceId: appServerInstanceId},
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
                        appServerInstanceId: {type: "number"},
                        appDbInstanceId: {type: "number"},
                        dbVendorId: {type: "number"},
                        appServerInstanceName: {type: "string"},
                        appDbInstanceName: {type: "string"},
                        dbVendorName: {type: "string"}
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

    function initAppServerDbInstanceMappingGrid() {
        initDataSource();
        $("#gridServerMapping").kendoGrid({
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
            columns: [{
                field: 'appDbInstanceName',
                title: "DB Instance",
                width: 180,
                sortable: true,
                filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
            }
            ],
            filterable: {mode: "row"},
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridServerMapping = $("#gridServerMapping").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function initObservable() {
        appServerDbInstanceMappingModel = kendo.observable(
                {
                    appServerDbInstanceMapping: {
                        id: "",
                        version: "",
                        appServerInstanceId: appServerInstanceId,
                        appDbInstanceId: "",
                        dbVendorId: dbVendorId,
                        appServerInstanceName: "",
                        appDbInstanceName: "",
                        dbVendorName: ""
                    }
                }
        );
        kendo.bind($("#application_top_panel"), appServerDbInstanceMappingModel);
    }

</script>