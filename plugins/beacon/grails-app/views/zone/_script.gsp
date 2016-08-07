

<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <sec:access url="/zone/update">
        <li onclick="editZone();"><i class="fa fa-edit"></i>Edit</li>
    </sec:access>
    <sec:access url="/zone/delete">
        <li onclick="deleteZone();"><i class="fa fa-trash-o"></i>Delete</li>
    </sec:access>

    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script language="javascript">
    var entityTypeId, appUserEntityTypeId;
    var gridZone;
    var dataSource, zoneModel;

    $(document).ready(function () {
        onLoadZonePage();
        initZoneGrid();
        initObservable();
        // init kendo switch
    });

    function onLoadZonePage() {
        // initialize form with kendo validator & bind onSubmit method
        initializeForm($("#zoneForm"), onSubmitZone);

        // update page title
        $(document).attr('title', "MIS - Create Zone");
        loadNumberedMenu(MENU_ID_BEACON, "#zone/show");
        entityTypeId = $("#entityTypeId").val();
        appUserEntityTypeId = $("#appUserEntityTypeId").val();
    }

    function executePreCondition() {
        if (!validateForm($("#zoneForm"))) {   // check kendo validation
            return false;
        }
        if (!customValidateDate($("#startDate"), 'Start date', $("#endDate"), 'end date')) {
            return false;
        }
        return true;
    }

    function onSubmitZone() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'zone', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'zone', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#zoneForm").serialize(),
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
                var newEntry = result.zone;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = gridZone.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = gridZone.select();
                    var allItems = gridZone.items();
                    var selectedIndex = allItems.index(selectedRow);
                    gridZone.removeRow(selectedRow);
                    gridZone.dataSource.insert(selectedIndex, newEntry);
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
        clearForm($("#zoneForm"), $('#name'));
        initObservable();
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function addContent() {
        if (executeCommonPreConditionForSelectKendo(gridZone, 'zone') == false) {
            return;
        }
        showLoadingSpinner(true);
        var zoneId = getSelectedIdFromGridKendo(gridZone);
        var loc = "${createLink(controller:'appAttachment', action: 'show')}?oId=" + zoneId + "&url=zone/show" + "&entityTypeId=" + entityTypeId;
        router.navigate(formatLink(loc));
        return false;
    }

    function reloadKendoGrid() {
        gridZone.dataSource.filter([]);
    }

    function addUserZone() {
        if (executeCommonPreConditionForSelectKendo(gridZone, 'zone') == false) {
            return;
        }
        showLoadingSpinner(true);
        var zoneId = getSelectedIdFromGridKendo(gridZone);
        var loc = "${createLink(controller:'appUserEntity', action: 'show')}?oId=" + zoneId + "&url=zone/show" + "&entityTypeId=" + appUserEntityTypeId;
        router.navigate(formatLink(loc));
        return false;
    }

    function reloadGrid() {
        gridZone.dataSource.filter([]);
    }

    function deleteZone() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true); // Spinner Show on AJAX Call
        var zoneId = getSelectedIdFromGridKendo(gridZone);
        $.ajax({
            url: "${createLink(controller:'zone', action: 'delete')}?id=" + zoneId,
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
        if (executeCommonPreConditionForSelectKendo(gridZone, 'zone') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected Zone?')) {
            return false;
        }
        return true;
    }

    function executePostConditionForDelete(data) {
        if (data.isError) {
            showError(data.message);
            return false;
        }
        var row = gridZone.select();
        row.each(function () {
            gridZone.removeRow($(this));
        });
        resetForm();
        showSuccess(data.message);
    }

    function editZone() {
        if (executeCommonPreConditionForSelectKendo(gridZone, 'zone') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridZone'));
//        resetForm();
        showLoadingSpinner(true);
        var zone = getSelectedObjectFromGridKendo(gridZone);

        var ids=zone.beacon_ids.split(',')
        $('#beacons').data("kendoMultiSelect").value(ids);
        showZone(zone);
        showLoadingSpinner(false);
    }

    function showZone(zone) {
        zoneModel.set('zone', zone);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function initDatasource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/zone/list",
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
        
        
            name: {type: "string"}
        
        ,
        
            color: {type: "string"}
        
        ,
        
            description: {type: "string"}
        
        ,

                        beacon_ids: {type: "string"},
                        beacon: {type: "string"}

        
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

    function initZoneGrid() {
        initDatasource();
        $("#gridZone").kendoGrid({
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
        sortable: true,
        filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
        }
        
        ,
        
        {
        field: "color",
        title: "Color",
        sortable: true,
        filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
        }
        
        ,
        
        {
        field: "description",
        title: "Description",
        sortable: true,
        filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
        }
        
        ,
                {
                    field: "beacon_ids",
                    title: "beacon_ids",
                    sortable: false,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                }

                ,
        {
        field: "beacon",
        title: "Beacons",
        sortable: true,
        filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
        }
        
        
            ],
            filterable: {mode: "row"},
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridZone = $("#gridZone").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function initObservable() {
        zoneModel = kendo.observable(
                {
        zone: {
        
        
        name:""
        
        ,
        
        color:""
        
        ,
        
        description:""
        
        ,

            beacon_ids:"",
            beacon:""

        

                    }
                }
        );
        kendo.bind($("#application_top_panel"), zoneModel);
    }



</script>
