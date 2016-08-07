<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <app:ifAllUrl urls="/district/update">
        <li onclick="editDistrict();"><i class="fa fa-edit"></i>Edit</li>
    </app:ifAllUrl>
    <sec:access url="/district/delete">
        <li onclick="deleteDistrict();"><i class="fa fa-trash-o"></i>Delete</li>
    </sec:access>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script type="text/javascript">
    var gridDistrict, dataSource, districtModel, dropDownCountry;

    $(document).ready(function () {
        onLoadDistrict();
        initDistrictGrid();
        initObservable();
    });

    function onLoadDistrict() {
        initializeForm($('#districtForm'), onSubmitDistrict);
        $(document).attr('title', "MIS - Create district");
        loadNumberedMenu(MENU_ID_APPLICATION, "#district/show");
    }

    function initDataSource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/district/list",
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
                        countryName: {type: "string"},
                        countryId: {type: "number"}
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
        });
    }

    function initDistrictGrid() {
        initDataSource();
        $("#gridDistrict").kendoGrid({
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
                    width: 150,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                },
                {field: "countryName", title: "Country", width: 120, sortable: true, filterable: false}
            ],
            filterable: {mode: "row"},
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridDistrict = $("#gridDistrict").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function initObservable() {
        districtModel = kendo.observable(
                {
                    district: {
                        id: "",
                        version: "",
                        name: "",
                        countryName: "",
                        countryId: ""
                    }
                }
        );
        kendo.bind($("#application_top_panel"), districtModel);
    }

    function executePreCondition() {
        if (validateForm($("#districtForm")) == false)
            return false;
        return true;
    }

    function onSubmitDistrict() {
        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('#create'), true);
        // Spinner Show on AJAX Call
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'district', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'district', action: 'update')}";
        }
        jQuery.ajax({
            type: 'post',
            data: jQuery("#districtForm").serialize(),
            url: actionUrl,
            success: function (data, textStatus) {
                executePostCondition(data);
                setButtonDisabled($('#create'), false);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus)
            },
            complete: function (XMLHttpRequest, textStatus) {
                // Spinner Hide on AJAX Call
                onCompleteAjaxCall();
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
                var newEntry = result.district;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = gridDistrict.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = gridDistrict.select();
                    var allItems = gridDistrict.items();
                    var selectedIndex = allItems.index(selectedRow);
                    gridDistrict.removeRow(selectedRow);
                    gridDistrict.dataSource.insert(selectedIndex, newEntry);
                }
                resetForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        clearForm($("#districtForm"), $('#name'));
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");
    }

    function deleteDistrict() {
        if (executePreConditionForDelete() == false) {
            return;
        }

        showLoadingSpinner(true); // Spinner Show on AJAX Call
        var districtId = getSelectedIdFromGridKendo(gridDistrict);
        $.ajax({
            url: "${createLink(controller: 'district', action: 'delete')}?id=" + districtId,
            success: executePostConditionForDelete,
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus)
            },
            complete: onCompleteAjaxCall,    // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function reloadKendoGrid() {
        gridDistrict.dataSource.filter([]);
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelectKendo(gridDistrict, 'district') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected district?')) {
            return false;
        }
        return true;
    }

    function executePostConditionForDelete(data) {
        if (data.isError) {
            showError(data.message);
            return false;
        }
        var row = gridDistrict.select();
        row.each(function () {
            gridDistrict.removeRow($(this));
        });
        resetForm();
        showSuccess(data.message);
    }

    function editDistrict() {
        if (executeCommonPreConditionForSelectKendo(gridDistrict, 'district') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridDistrict'));
        var districtObj = getSelectedObjectFromGridKendo(gridDistrict);
        showDistrict(districtObj);
    }

    function showDistrict(districtObj) {
        districtModel.set('district', districtObj);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
    }

</script>
