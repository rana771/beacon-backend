<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <app:ifAllUrl urls="/vehicle/update">
        <li onclick="editVehicle();"><i class="fa fa-edit"></i>Edit</li>
    </app:ifAllUrl>
    <sec:access url="/vehicle/delete">
        <li onclick="deleteVehicle();"><i class="fa fa-trash-o"></i>Delete</li>
    </sec:access>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script language="javascript">
    var gridVehicle, selectedRowId = 0;
    var dataSource, vehicleModel;

    $(document).ready(function () {
        onLoadVehiclePage();
        initVehicleGrid();
        initObservable();
    });

    function onLoadVehiclePage() {
        // initialize form with kendo validator & bind onSubmit method
        initializeForm($("#vehicleForm"), onSubmitVehicle);

        // update page title
        $(document).attr('title', "MIS - Add Vehicle");
        loadNumberedMenu(MENU_ID_APPLICATION, "#vehicle/show");
    }

    function executePreCondition() {
        if (!validateForm($("#vehicleForm"))) {   // check kendo validation
            return false;
        }
        return true;
    }

    function onSubmitVehicle() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'vehicle', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'vehicle', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#vehicleForm").serialize(),
            url: actionUrl,
            success: function (data, textStatus) {
                executePostCondition(data);
                setButtonDisabled($('#create'), false);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus);
            },
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false)
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
                var newEntry = result.vehicle;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = gridVehicle.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = gridVehicle.select();
                    var allItems = gridVehicle.items();
                    var selectedIndex = allItems.index(selectedRow);
                    gridVehicle.removeRow(selectedRow);
                    gridVehicle.dataSource.insert(selectedIndex, newEntry);
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
        clearForm($("#vehicleForm"), $('#name'));
        initObservable();
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function deleteVehicle() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var vehicleId = getSelectedIdFromGridKendo(gridVehicle);

        $.ajax({
            url: "${createLink(controller: 'vehicle', action: 'delete')}?id=" + vehicleId,
            success: executePostConditionForDelete,
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

    function reloadKendoGrid() {
        gridVehicle.dataSource.filter([]);
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelectKendo(gridVehicle, 'vehicle') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected Vehicle?')) {
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
        var row = gridVehicle.select();
        row.each(function () {
            gridVehicle.removeRow($(this));
        });
        resetForm();
        showSuccess(data.message);
    }

    function editVehicle() {
        if (executeCommonPreConditionForSelectKendo(gridVehicle, 'Vehicle') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridVehicle'));
        var vehicleObj = getSelectedObjectFromGridKendo(gridVehicle);
        showVehicle(vehicleObj);
    }

    function showVehicle(vehicleObj) {
        vehicleModel.set('vehicle', vehicleObj);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function initDatasource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/vehicle/list",
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
                        description: {type: "string"}
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

    function initVehicleGrid() {
        initDatasource();
        $("#gridVehicle").kendoGrid({
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
                    field: "name",
                    title: "Name",
                    width: 120,
                    sortable: true,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                },
                {
                    field: "description",
                    title: "Description",
                    width: 150,
                    sortable: false,
                    filterable: false,
                    template: "#= trimTextForKendo(description,80)#"
                }
            ],
            filterable: {
                mode: "row"
            },
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridVehicle = $("#gridVehicle").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function initObservable() {
        vehicleModel = kendo.observable(
                {
                    vehicle: {
                        id: "",
                        version: "",
                        name: "",
                        description: ""
                    }
                }
        );
        kendo.bind($("#application_top_panel"), vehicleModel);
    }

</script>
