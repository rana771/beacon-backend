

<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <sec:access url="/place/update">
        <li onclick="editPlace();"><i class="fa fa-edit"></i>Edit</li>
    </sec:access>
    <sec:access url="/place/delete">
        <li onclick="deletePlace();"><i class="fa fa-trash-o"></i>Delete</li>
    </sec:access>

    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script language="javascript">
    var entityTypeId, appUserEntityTypeId;
    var gridPlace;
    var dataSource, placeModel;

    $(document).ready(function () {
        onLoadPlacePage();
        initPlaceGrid();
        initObservable();
        // init kendo switch
    });

    function onLoadPlacePage() {
        // initialize form with kendo validator & bind onSubmit method
        initializeForm($("#placeForm"), onSubmitPlace);

        // update page title
        $(document).attr('title', "MIS - Create Place");
        loadNumberedMenu(MENU_ID_BEACON, "#place/show");
        entityTypeId = $("#entityTypeId").val();
        appUserEntityTypeId = $("#appUserEntityTypeId").val();
    }

    function executePreCondition() {
        if (!validateForm($("#placeForm"))) {   // check kendo validation
            return false;
        }
       /* if (!customValidateDate($("#startDate"), 'Start date', $("#endDate"), 'end date')) {
            return false;
        }*/
        return true;
    }

    function onSubmitPlace() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'place', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'place', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#placeForm").serialize(),
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
                var newEntry = result.place;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = gridPlace.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = gridPlace.select();
                    var allItems = gridPlace.items();
                    var selectedIndex = allItems.index(selectedRow);
                    gridPlace.removeRow(selectedRow);
                    gridPlace.dataSource.insert(selectedIndex, newEntry);
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
        clearForm($("#placeForm"), $('#name'));
        initObservable();
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function addContent() {
        if (executeCommonPreConditionForSelectKendo(gridPlace, 'place') == false) {
            return;
        }
        showLoadingSpinner(true);
        var placeId = getSelectedIdFromGridKendo(gridPlace);
        var loc = "${createLink(controller:'appAttachment', action: 'show')}?oId=" + placeId + "&url=place/show" + "&entityTypeId=" + entityTypeId;
        router.navigate(formatLink(loc));
        return false;
    }

    function reloadKendoGrid() {
        gridPlace.dataSource.filter([]);
    }

    function addUserPlace() {
        if (executeCommonPreConditionForSelectKendo(gridPlace, 'place') == false) {
            return;
        }
        showLoadingSpinner(true);
        var placeId = getSelectedIdFromGridKendo(gridPlace);
        var loc = "${createLink(controller:'appUserEntity', action: 'show')}?oId=" + placeId + "&url=place/show" + "&entityTypeId=" + appUserEntityTypeId;
        router.navigate(formatLink(loc));
        return false;
    }

    function reloadGrid() {
        gridPlace.dataSource.filter([]);
    }

    function deletePlace() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true); // Spinner Show on AJAX Call
        var placeId = getSelectedIdFromGridKendo(gridPlace);
        $.ajax({
            url: "${createLink(controller:'place', action: 'delete')}?id=" + placeId,
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
        if (executeCommonPreConditionForSelectKendo(gridPlace, 'place') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected Place?')) {
            return false;
        }
        return true;
    }

    function executePostConditionForDelete(data) {
        if (data.isError) {
            showError(data.message);
            return false;
        }
        var row = gridPlace.select();
        row.each(function () {
            gridPlace.removeRow($(this));
        });
        resetForm();
        showSuccess(data.message);
    }

    function editPlace() {
        if (executeCommonPreConditionForSelectKendo(gridPlace, 'place') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridPlace'));
        resetForm();
        showLoadingSpinner(true);
        var place = getSelectedObjectFromGridKendo(gridPlace);
        showPlace(place);
        showLoadingSpinner(false);
    }

    function showPlace(place) {
        placeModel.set('place', place);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function initDatasource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/place/list",
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
        
            latitude: {type: "string"}
        
        ,
        
            longitude: {type: "string"}
        
        ,
        
            geoFrenchRadius: {type: "string"}
        
        
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

    function initPlaceGrid() {
        initDatasource();
        $("#gridPlace").kendoGrid({
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
        field: "latitude",
        title: "Latitude",
        sortable: true,
        filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
        }
        
        ,
        
        {
        field: "longitude",
        title: "Longitude",
        sortable: true,
        filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
        }
        
        ,
        
        {
        field: "geoFrenchRadius",
        title: "Geo French Radius",
        sortable: true,
        filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
        }
        
        
            ],
            filterable: {mode: "row"},
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridPlace = $("#gridPlace").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function initObservable() {
        placeModel = kendo.observable(
                {
        place: {
        
        
        name:""
        
        ,
        
        latitude:""
        
        ,
        
        longitude:""
        
        ,
        
        geoFrenchRadius:""
        
        

                    }
                }
        );
        kendo.bind($("#application_top_panel"), placeModel);
    }



</script>
