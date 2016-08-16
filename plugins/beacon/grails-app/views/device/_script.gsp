

<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <sec:access url="/device/update">
        <li onclick="editDevice();"><i class="fa fa-edit"></i>Edit</li>
    </sec:access>
    <sec:access url="/device/delete">
        <li onclick="deleteDevice();"><i class="fa fa-trash-o"></i>Delete</li>
    </sec:access>

    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script language="javascript">
    var entityTypeId, appUserEntityTypeId;
    var gridDevice;
    var dataSource, deviceModel;

    $(document).ready(function () {
        onLoadDevicePage();
        initDeviceGrid();
        initObservable();
        // init kendo switch
    });

    function onLoadDevicePage() {
        // initialize form with kendo validator & bind onSubmit method
        initializeForm($("#deviceForm"), onSubmitDevice);

        // update page title
        $(document).attr('title', "MIS - Create Device");
        loadNumberedMenu(MENU_ID_BEACON, "#device/show");
        entityTypeId = $("#entityTypeId").val();
        appUserEntityTypeId = $("#appUserEntityTypeId").val();
    }

    function executePreCondition() {
        if (!validateForm($("#deviceForm"))) {   // check kendo validation
            return false;
        }
        if (!customValidateDate($("#startDate"), 'Start date', $("#endDate"), 'end date')) {
            return false;
        }
        return true;
    }

    function onSubmitDevice() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'device', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'device', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#deviceForm").serialize(),
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
                var newEntry = result.device;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = gridDevice.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = gridDevice.select();
                    var allItems = gridDevice.items();
                    var selectedIndex = allItems.index(selectedRow);
                    gridDevice.removeRow(selectedRow);
                    gridDevice.dataSource.insert(selectedIndex, newEntry);
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
        clearForm($("#deviceForm"), $('#name'));
        initObservable();
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function addContent() {
        if (executeCommonPreConditionForSelectKendo(gridDevice, 'device') == false) {
            return;
        }
        showLoadingSpinner(true);
        var deviceId = getSelectedIdFromGridKendo(gridDevice);
        var loc = "${createLink(controller:'appAttachment', action: 'show')}?oId=" + deviceId + "&url=device/show" + "&entityTypeId=" + entityTypeId;
        router.navigate(formatLink(loc));
        return false;
    }

    function reloadKendoGrid() {
        gridDevice.dataSource.filter([]);
    }

    function addUserDevice() {
        if (executeCommonPreConditionForSelectKendo(gridDevice, 'device') == false) {
            return;
        }
        showLoadingSpinner(true);
        var deviceId = getSelectedIdFromGridKendo(gridDevice);
        var loc = "${createLink(controller:'appUserEntity', action: 'show')}?oId=" + deviceId + "&url=device/show" + "&entityTypeId=" + appUserEntityTypeId;
        router.navigate(formatLink(loc));
        return false;
    }

    function reloadGrid() {
        gridDevice.dataSource.filter([]);
    }

    function deleteDevice() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true); // Spinner Show on AJAX Call
        var deviceId = getSelectedIdFromGridKendo(gridDevice);
        $.ajax({
            url: "${createLink(controller:'device', action: 'delete')}?id=" + deviceId,
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
        if (executeCommonPreConditionForSelectKendo(gridDevice, 'device') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected Device?')) {
            return false;
        }
        return true;
    }

    function executePostConditionForDelete(data) {
        if (data.isError) {
            showError(data.message);
            return false;
        }
        var row = gridDevice.select();
        row.each(function () {
            gridDevice.removeRow($(this));
        });
        resetForm();
        showSuccess(data.message);
    }

    function editDevice() {
        if (executeCommonPreConditionForSelectKendo(gridDevice, 'device') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridDevice'));
        resetForm();
        showLoadingSpinner(true);
        var device = getSelectedObjectFromGridKendo(gridDevice);
        showDevice(device);
        showLoadingSpinner(false);
    }

    function showDevice(device) {
        deviceModel.set('device', device);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function initDatasource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/device/list",
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
        
        
            marchant: {type: "string"}
        
        ,
        
            name: {type: "string"}
        
        ,
        
            deviceType: {type: "string"}
        
        ,
        
            details: {type: "string"}
        
        ,
        
            network: {type: "string"}
        
        ,
        
            isScheduleBroadcast: {type: "string"}
        
        ,
        
            content: {type: "string"}
        
        ,
        
        latitude: {type: "number"}
        
        ,
        
        longitude: {type: "number"}
        
        ,
        
            tags: {type: "string"}
        
        
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
            , filter: {field: "id", operator: "equal", value: ${oId}}
            </g:if>
        });
    }

    function initDeviceGrid() {
        initDatasource();
        $("#gridDevice").kendoGrid({
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
        field: "marchant",
        title: "Marchant",
        sortable: true,
        filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
        }
        
        ,
        
        {
        field: "name",
        title: "Name",
        sortable: true,
        filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
        }
        
        ,
        
        {
        field: "deviceType",
        title: "Device Type",
        sortable: true,
        filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
        }
        
        ,
        
        {
        field: "details",
        title: "Details",
        sortable: true,
        filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
        }
        
        ,
        
        {
        field: "network",
        title: "Network",
        sortable: true,
        filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
        }
        
        ,
        
        {
        field: "isScheduleBroadcast",
        title: "Is Schedule Broadcast",
        sortable: true,
        filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
        }
        
        ,
        
        {
        field: "content",
        title: "Content",
        sortable: true,
        filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
        }
        
        ,
        
                {
                    field: "latitude",
                    title: "Latitude",
                    sortable: false,
                    filterable: false,
                    attributes: {style: setAlignRight()},
                    headerAttributes: {style: setAlignRight()}
                }
        
        ,
        
                {
                    field: "longitude",
                    title: "Longitude",
                    sortable: false,
                    filterable: false,
                    attributes: {style: setAlignRight()},
                    headerAttributes: {style: setAlignRight()}
                }
        
        ,
        
        {
        field: "tags",
        title: "Tags",
        sortable: true,
        filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
        }
        
        
            ],
            filterable: {mode: "row"},
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridDevice = $("#gridDevice").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function initObservable() {
        deviceModel = kendo.observable(
                {
        device: {
        
        
        marchant:""
        
        ,
        
        name:""
        
        ,
        
        deviceType:""
        
        ,
        
        details:""
        
        ,
        
        network:""
        
        ,
        
            isScheduleBroadcast:false
        
        ,
        
        content:""
        
        ,
        
        latitude:""
        
        ,
        
        longitude:""
        
        ,
        
        tags:""
        
        

                    }
                }
        );
        kendo.bind($("#application_top_panel"), deviceModel);
    }



</script>
