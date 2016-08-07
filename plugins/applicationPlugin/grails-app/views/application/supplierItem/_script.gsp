<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <app:ifAllUrl urls="/supplierItem/update">
        <li onclick="editSupplierItem();"><i class="fa fa-edit"></i>Edit</li>
    </app:ifAllUrl>
    <app:ifAllUrl urls="/supplierItem/delete">
        <li onclick="deleteSupplierItem();"><i class="fa fa-trash-o"></i>Delete</li>
    </app:ifAllUrl>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>
<script type="text/javascript">
    var gridSupplierItem, dataSource, dropDownItemType, dropDownSupplierItem, supplier, supplierId, supplierItemModel;

    $(document).ready(function () {
        onLoadSupplierItem();
        initSupplierItemGrid();
        initObservable();
    });

    function onLoadSupplierItem() {
        checkOnLoadError();
        // initialize form with kendo validator & bind onSubmit method
        initializeForm($("#supplierItemForm"), onSubmitSupplierItem);
        supplierId = $('#supplierId').val();
        // update page title
        $(document).attr('title', "MIS - Create Supplier's Item");
        loadNumberedMenu(MENU_ID_APPLICATION, "#supplier/show");
    }

    function checkOnLoadError() {
        var isError = '${isError}';
        var msg = '${message}';
        if (isError == 'true') {
            showError(msg);
            return false;
        }
    }

    function initSupplierItemGrid() {
        initDataSource();
        $("#gridSupplierItem").kendoGrid({
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
                    field: "itemName",
                    title: "Name",
                    width: 120,
                    sortable: true,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                },
                {
                    field: "itemTypeName",
                    title: "Item Type",
                    width: 120,
                    sortable: true,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                },
                {field: "code", title: "Code", width: 100, sortable: true, filterable: false},
                {field: "unit", title: "Unit", width: 100, sortable: true, filterable: false}

            ],
            filterable: {mode: "row"},
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridSupplierItem = $("#gridSupplierItem").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function reloadKendoGrid() {
        gridSupplierItem.dataSource.filter([]);
    }

    function initDataSource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/supplierItem/list",
                    data: {supplierId: supplierId},
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
                        supplierId: {type: "number"},
                        itemTypeId: {type: "number"},
                        itemId: {type: "number"},
                        code: {type: "string"},
                        unit: {type: "string"},
                        itemTypeName: {type: "string"},
                        itemName: {type: "string"}
                    }
                },
                parse: function (data) {
                    checkIsErrorGridKendo(data);
                    return data;
                }
            },
            sort: {field: 'itemName', dir: 'asc'},  // default sort
            pageSize: ${com.athena.mis.BaseService.DEFAULT_RESULT_PER_PAGE},
            serverPaging: true,
            serverFiltering: true,
            serverSorting: true
        });
    }

    function initObservable() {
        supplierItemModel = kendo.observable(
                {
                    supplierItem: {
                        id: "",
                        version: "",
                        itemTypeId: "",
                        itemId: "",
                        code: "",
                        unit: ""
                    }
                }
        );
        kendo.bind($("#application_top_panel"), supplierItemModel);
    }

    function onChangeItemType() {
        var itemTypeId = dropDownItemType.value();
        $('#itemCode').text('');
        $('#itemUnit').text('');
        if (itemTypeId == '') {
            dropDownSupplierItem.setDataSource(getKendoEmptyDataSource(dropDownSupplierItem, null));
            return false;
        }
        $('#itemId').attr('type_id', itemTypeId);
        $('#itemId').attr('supplier_id', supplierId);
        $('#itemId').attr('default_value', '0');
        $('#itemId').reloadMe();

    }

    function resetFormForCreateAgain() {
        dropDownItemType.value('');
        dropDownSupplierItem.setDataSource(getKendoEmptyDataSource(dropDownSupplierItem, null));
        dropDownSupplierItem.value('');
        $('#itemCode').text('');
        $('#itemUnit').text('');
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function onChangeItem() {
        $('#itemCode').text('');
        $('#itemUnit').text('');
        $('#itemCode').text(dropDownSupplierItem.dataItem().code);
        $('#itemUnit').text(dropDownSupplierItem.dataItem().unit);
    }

    function executePreCondition() {
        if (!validateForm($("#supplierItemForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitSupplierItem() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        // Spinner Show on AJAX Call
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'supplierItem', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'supplierItem', action: 'update')}";
        }
        jQuery.ajax({
            type: 'post',
            data: jQuery("#supplierItemForm").serialize(),
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
                var newEntry = result.supplierItem;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = gridSupplierItem.dataSource.data();
                    gridData.unshift(newEntry);
                    resetFormForCreateAgain();
                } else if (newEntry != null) { // updated existing
                    var selectedRow = gridSupplierItem.select();
                    var allItems = gridSupplierItem.items();
                    var selectedIndex = allItems.index(selectedRow);
                    gridSupplierItem.removeRow(selectedRow);
                    gridSupplierItem.dataSource.insert(selectedIndex, newEntry);
                    resetFormSupplierItem();
                }
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetFormSupplierItem() {
        clearForm($("#supplierItemForm"), dropDownItemType);
        initObservable();
        $('#supplierId').val(supplierId);
        dropDownSupplierItem.value('');
        $('#itemId').attr('type_id', '0');
        $('#itemId').attr('default_value', '0');
        $('#itemId').attr('supplier_id', '0');
        $('#itemId').reloadMe();
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelectKendo(gridSupplierItem, 'supplier item') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected Supplier Item?')) {
            return false;
        }
        return true;
    }

    function deleteSupplierItem() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true); // Spinner Show on AJAX Call
        var supplierItemId = getSelectedIdFromGridKendo(gridSupplierItem);
        $.ajax({
            url: "${createLink(controller: 'supplierItem', action: 'delete')}?id=" + supplierItemId,
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

    function executePostConditionForDelete(data) {
        if (data.isError) {
            showError(data.message);
            return false;
        }
        var row = gridSupplierItem.select();
        row.each(function () {
            gridSupplierItem.removeRow($(this));
        });
        resetFormSupplierItem();
        showSuccess(data.message);
    }

    function editSupplierItem() {
        if (executeCommonPreConditionForSelectKendo(gridSupplierItem, 'supplier item') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridSupplierItem'));
        var supplierItemObj = getSelectedObjectFromGridKendo(gridSupplierItem);
        populateSupplierItem(supplierItemObj)
    }

    function populateSupplierItem(supplierItemObj) {
        supplierItemModel.set('supplierItem', supplierItemObj);
        dropDownItemType.value(supplierItemObj.itemTypeId);
        $('#itemId').attr('type_id', supplierItemObj.itemTypeId);
        $('#itemId').attr('supplier_id', supplierItemObj.supplierId);
        $('#itemId').attr('default_value', supplierItemObj.itemId);
        $('#itemId').reloadMe();
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

</script>