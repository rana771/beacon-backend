<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <app:ifAllUrl urls="/item/updateInventoryItem">
        <li onclick="editItemInventory();"><i class="fa fa-edit"></i>Edit</li>
    </app:ifAllUrl>
    <sec:access url="/item/deleteInventoryItem">
        <li onclick="deleteItemInventory();"><i class="fa fa-trash-o"></i>Delete</li>
    </sec:access>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script language="javascript">
    var gridItemInventory, dataSource, dropDownItemType, dropDownValuationType, itemModel;

    $(document).ready(function () {
        onLoadItemInventoryPage();
        initInventoryItemGrid();
        initObservable();

        $("#isFinishedProduct").kendoMobileSwitch({onLabel: "YES",offLabel: "NO"});
    });

    function onLoadItemInventoryPage() {
        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#itemInventoryForm"), onSubmitItemInventory);
        // update page title
        $(document).attr('title', "MIS - Create Inventory Item");
        loadNumberedMenu(MENU_ID_APPLICATION, "#item/showInventoryItem");
    }

    function initDataSource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "${createLink(controller:'item', action: 'listInventoryItem')}",
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
                        isFinishedProduct: {type: "boolean"},
                        itemTypeId: {type: "number"},
                        valuationTypeId: {type: "number"},
                        categoryId: {type: "number"},
                        itemTypeName: {type: "string"},
                        valuationTypeName: {type: "string"},
                        isDirty: {type: "boolean"}
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

    function initInventoryItemGrid() {
        initDataSource();
        $("#gridItemInventory").kendoGrid({
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
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}},
                    template: "#= setColor(name, isDirty)#"

                },
                {
                    field: "code",
                    title: "Code",
                    width: 70,
                    sortable: true,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                },
                {field: "unit", title: "Unit", width: 50, sortable: true, filterable: false},
                {field: "itemTypeName", title: "Item Type", width: 70, sortable: true, filterable: false},
                {field: "valuationTypeName", title: "Valuation Type", width: 70, sortable: true, filterable: false},
                {
                    field: "isFinishedProduct",
                    title: "Finished Product",
                    width: 70,
                    sortable: true,
                    filterable: false,
                    template: "#=isFinishedProduct?'YES':'NO' #",
                    attributes: {style: setAlignCenter()},
                    headerAttributes: {style: setAlignCenter()}
                }
            ],
            filterable: {mode: "row"},
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridItemInventory = $("#gridItemInventory").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function setColor(name, isDirty) {
        if (isDirty) {
            return '<span style="color: #ff0000">' + name + '</span>';
        }
        return '<span style="color: inherit">' + name + '</span>';
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
                        itemTypeId: "",
                        valuationTypeId: "",
                        isFinishedProduct: false
                    }
                }
        );
        kendo.bind($("#application_top_panel"), itemModel);
    }

    function executePreCondition() {
        if (!validateForm($("#itemInventoryForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitItemInventory() {
        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'item', action: 'createInventoryItem')}";
        } else {
            actionUrl = "${createLink(controller:'item', action: 'updateInventoryItem')}";
        }
        jQuery.ajax({
            type: 'post',
            data: jQuery("#itemInventoryForm").serialize(),
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
                    var gridData = gridItemInventory.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = gridItemInventory.select();
                    var allItems = gridItemInventory.items();
                    var selectedIndex = allItems.index(selectedRow);
                    gridItemInventory.removeRow(selectedRow);
                    gridItemInventory.dataSource.insert(selectedIndex, newEntry);
                }
                resetItemInventoryForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetItemInventoryForm() {
        clearForm($("#itemInventoryForm"), $("#name"));
        initObservable();
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");
    }

    function deleteItemInventory() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var itemId = getSelectedIdFromGridKendo(gridItemInventory);
        $.ajax({
            url: "${createLink(controller:'item', action: 'deleteInventoryItem')}?itemId=" + itemId,
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
        gridItemInventory.dataSource.filter([]);
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelectKendo(gridItemInventory, 'item') == false) {
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
        var row = gridItemInventory.select();
        row.each(function () {
            gridItemInventory.removeRow($(this));
        });
        resetItemInventoryForm();
        showSuccess(data.message);
    }

    function editItemInventory() {
        if (executeCommonPreConditionForSelectKendo(gridItemInventory, 'item') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridItemInventory'));
        var itemObj = getSelectedObjectFromGridKendo(gridItemInventory);
        showItemInventory(itemObj);
    }

    function showItemInventory(itemObj) {
        itemModel.set('item', itemObj);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
    }

</script>
