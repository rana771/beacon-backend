<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <app:ifAllUrl urls="/item/updateNonInventoryItem">
        <li onclick="editNonInventoryItem();"><i class="fa fa-edit"></i>Edit</li>
    </app:ifAllUrl>
    <sec:access url="/item/deleteNonInventoryItem">
        <li onclick="deleteNonInventoryItem();"><i class="fa fa-trash-o"></i>Delete</li>
    </sec:access>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script language="javascript">
    var gridItemNonInventory, dataSource, dropDownItemType, itemModel;

    $(document).ready(function () {
        onLoadNonInventoryItemPage();
        initNonInventoryItemGrid();
        initObservable();
    });

    function onLoadNonInventoryItemPage() {
        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#nonInventoryItemForm"), onSubmitNonInventoryItem);
        // update page title
        $(document).attr('title', "MIS - Create Non-Inventory Item");
        loadNumberedMenu(MENU_ID_APPLICATION, "#item/showNonInventoryItem");
    }

    function initDataSource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/item/listNonInventoryItem",
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
                        code: {type: "string"},
                        unit: {type: "string"},
                        itemTypeId: {type: "number"},
                        itemTypeName: {type: "string"}
                    }
                },
                parse: function (data) {
                    checkIsErrorGridKendo(data);
                    return data;
                }
            },
            sort: {field: 'id', dir: 'asc'},  // default sort
            pageSize: ${com.athena.mis.BaseService.DEFAULT_RESULT_PER_PAGE},
            serverPaging: true,
            serverFiltering: true,
            serverSorting: true
        });
    }

    function initNonInventoryItemGrid() {
        initDataSource();
        $("#gridItemNonInventory").kendoGrid({
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
                    width: 150,
                    sortable: true,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                },
                {
                    field: "code",
                    title: "Code",
                    width: 70,
                    sortable: true,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                },
                {field: "unit", title: "Unit", width: 70, sortable: true, filterable: false},
                {field: "itemTypeName", title: "Item Type", width: 70, sortable: true, filterable: false}
            ],
            filterable: {mode: "row"},
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridItemNonInventory = $("#gridItemNonInventory").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function initObservable() {
        itemModel = kendo.observable(
                {
                    item: {
                        id: "",
                        version: "",
                        name: "",
                        code: "",
                        unit: "",
                        itemTypeId: ""
                    }
                }
        );
        kendo.bind($("#application_top_panel"), itemModel);
    }

    function executePreCondition() {
        if (!validateForm($("#nonInventoryItemForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitNonInventoryItem() {
        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'item', action: 'createNonInventoryItem')}";
        } else {
            actionUrl = "${createLink(controller:'item', action: 'updateNonInventoryItem')}";
        }
        jQuery.ajax({
            type: 'post',
            data: jQuery("#nonInventoryItemForm").serialize(),
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
                var newEntry = result.item;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = gridItemNonInventory.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = gridItemNonInventory.select();
                    var allItems = gridItemNonInventory.items();
                    var selectedIndex = allItems.index(selectedRow);
                    gridItemNonInventory.removeRow(selectedRow);
                    gridItemNonInventory.dataSource.insert(selectedIndex, newEntry);
                }
                resetNonInventoryItemForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetNonInventoryItemForm() {
        clearForm($("#nonInventoryItemForm"), $('#name'));
        initObservable();
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");
    }

    function deleteNonInventoryItem() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var itemId = getSelectedIdFromGridKendo(gridItemNonInventory);
        $.ajax({
            url: "${createLink(controller:'item', action: 'deleteNonInventoryItem')}?id=" + itemId,
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
        gridItemNonInventory.dataSource.filter([]);
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelectKendo(gridItemNonInventory, 'item') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected item?')) {
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
        var row = gridItemNonInventory.select();
        row.each(function () {
            gridItemNonInventory.removeRow($(this));
        });
        resetNonInventoryItemForm();
        showSuccess(data.message);
    }

    function editNonInventoryItem() {
        if (executeCommonPreConditionForSelectKendo(gridItemNonInventory, 'item') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridItemNonInventory'));
        var itemObj = getSelectedObjectFromGridKendo(gridItemNonInventory);
        showNonInventoryItem(itemObj);
    }

    function showNonInventoryItem(itemObj) {
        itemModel.set('item', itemObj);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
    }

</script>
