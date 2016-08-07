<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <app:ifAllUrl urls="/itemType/update">
        <li onclick="editItemType();"><i class="fa fa-edit"></i>Edit</li>
    </app:ifAllUrl>
    <sec:access url="/itemType/delete">
        <li onclick="deleteItemType();"><i class="fa fa-trash-o"></i>Delete</li>
    </sec:access>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script language="javascript">
    var gridItemType, dataSource, itemTypeModel, dropDownItemCategory;

    $(document).ready(function () {
        onLoadItemTypePage();
        initItemTypeGrid();
        initObservable();
    });

    function onLoadItemTypePage() {
        // initialize form with kendo validator & bind onSubmit method
        initializeForm($("#itemTypeForm"), onSubmitItemType);
        // update page title
        $(document).attr('title', "MIS - Create Item Type");
        loadNumberedMenu(MENU_ID_APPLICATION, "#itemType/show");
    }

    function initDataSource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/itemType/list",
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
                        categoryId: {type: "number"},
                        name: {type: "string"},
                        categoryName: {type: "string"}
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

    function initItemTypeGrid() {
        initDataSource();
        $("#gridItemType").kendoGrid({
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
                    width: 200,
                    sortable: true,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                },
                {field: "categoryName", title: "Category", width: 200, sortable: true, filterable: false}
            ],
            filterable: {mode: "row"},
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridItemType = $("#gridItemType").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }


    function initObservable() {
        itemTypeModel = kendo.observable(
                {
                    itemType: {
                        id: "",
                        version: "",
                        name: "",
                        categoryId: "",
                        categoryName: ""
                    }
                }
        );
        kendo.bind($("#application_top_panel"), itemTypeModel);
    }

    function executePreCondition() {
        if (!validateForm($("#itemTypeForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitItemType() {
        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'itemType', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'itemType', action: 'update')}";
        }
        jQuery.ajax({
            type: 'post',
            data: jQuery("#itemTypeForm").serialize(),
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
                var newEntry = result.itemType;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = gridItemType.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = gridItemType.select();
                    var allItems = gridItemType.items();
                    var selectedIndex = allItems.index(selectedRow);
                    gridItemType.removeRow(selectedRow);
                    gridItemType.dataSource.insert(selectedIndex, newEntry);
                }
                resetItemTypeForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetItemTypeForm() {
        clearForm($("#itemTypeForm"), dropDownItemCategory); // clear errors & form values & bind focus event
        initObservable();
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");
    }


    function deleteItemType() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var id = getSelectedIdFromGridKendo(gridItemType);
        $.ajax({
            url: "${createLink(controller:'itemType', action: 'delete')}?id=" + id,
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

    function reloadKendoGrid() {
        gridItemType.dataSource.filter([]);
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelectKendo(gridItemType, 'item') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected item type?')) {
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
        var row = gridItemType.select();
        row.each(function () {
            gridItemType.removeRow($(this));
        });
        resetItemTypeForm();
        showSuccess(data.message);
    }

    function editItemType() {
        if (executeCommonPreConditionForSelectKendo(gridItemType, 'item type') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridItemType'));
        var itemTypeObj = getSelectedObjectFromGridKendo(gridItemType);
        showNonInventoryItem(itemTypeObj);
    }

    function showNonInventoryItem(itemTypeObj) {
        itemTypeModel.set('itemType', itemTypeObj);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
    }

</script>
