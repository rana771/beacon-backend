<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <app:ifAllUrl urls="/supplier/update">
        <li onclick="editSupplier();"><i class="fa fa-edit"></i>Edit</li>
    </app:ifAllUrl>
    <sec:access url="/supplier/delete">
        <li onclick="deleteSupplier();"><i class="fa fa-trash-o"></i>Delete</li>
    </sec:access>
    <app:ifAllUrl urls="/supplierItem/show">
        <li onclick="addMaterial();"><i class="fa fa-building-o"></i>Item(s)</li>
    </app:ifAllUrl>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>
<script language="javascript">
    var gridSupplier, dataSource, dropDownSupplierType, supplierModel;

    $(document).ready(function () {
        onLoadSupplierPage();
        initSupplierGrid();
        initObservable();
    });

    function onLoadSupplierPage() {
        // initialize form with kendo validator & bind onSubmit method
        initializeForm($("#supplierForm"), onSubmitSupplier);
        // update page title
        $(document).attr('title', "MIS - Create Supplier");
        loadNumberedMenu(MENU_ID_APPLICATION, "#supplier/show");
    }

    function initDataSource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/supplier/list",
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
                        supplierTypeId: {type: "number"},
                        name: {type: "string"},
                        address: {type: "string"},
                        accountName: {type: "string"},
                        bankName: {type: "string"},
                        bankAccount: {type: "string"},
                        itemCount: {type: "number"},
                        supplierTypeName: {type: "string"}
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

    function initSupplierGrid() {
        initDataSource();
        $("#gridSupplier").kendoGrid({
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
                {field: "supplierTypeName", title: "Supplier Type", width: 100, sortable: true, filterable: false},
                {field: "accountName", title: "Account Name", width: 120, sortable: false, filterable: false},
                {field: "address", title: "Address", width: 120, sortable: false, filterable: false},
                {field: "bankAccount", title: "Account No", width: 90, sortable: false, filterable: false},
                {
                    field: "itemCount",
                    title: "Item(s)",
                    width: 40,
                    sortable: false,
                    filterable: false,
                    attributes: {style: setAlignCenter()}
                }

            ],
            filterable: {mode: "row"},
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridSupplier = $("#gridSupplier").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function initObservable() {
        supplierModel = kendo.observable(
                {
                    supplier: {
                        id: "",
                        version: "",
                        name: "",
                        supplierTypeId: "",
                        accountName: "",
                        address: "",
                        bankName: "",
                        bankAccount: "",
                        itemCount: ""
                    }
                }
        );
        kendo.bind($("#application_top_panel"), supplierModel);
    }

    function executePreCondition() {
        if (!validateForm($("#supplierForm"))) {  // check kendo validation
            return false;
        }
        return true;
    }

    function onSubmitSupplier() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#save'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'supplier', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'supplier', action: 'update')}";
        }
        jQuery.ajax({
            type: 'post',
            data: jQuery("#supplierForm").serialize(),
            url: actionUrl,
            success: function (data, textStatus) {
                executePostCondition(data);
                setButtonDisabled($('#save'), false);
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
                var newEntry = result.supplier;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = gridSupplier.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = gridSupplier.select();
                    var allItems = gridSupplier.items();
                    var selectedIndex = allItems.index(selectedRow);
                    gridSupplier.removeRow(selectedRow);
                    gridSupplier.dataSource.insert(selectedIndex, newEntry);
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
        clearForm($("#supplierForm"), dropDownSupplierType);
        initObservable();
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function addMaterial() {
        if (executeCommonPreConditionForSelectKendo(gridSupplier, 'supplier') == false) {
            return;
        }
        showLoadingSpinner(true);
        var supplierId = getSelectedIdFromGridKendo(gridSupplier);
        var loc = "${createLink(controller: 'supplierItem', action: 'show')}?oId=" + supplierId + "&url=supplier/show";
        router.navigate(formatLink(loc));
        return false;
    }

    function reloadKendoGrid() {
        gridSupplier.dataSource.filter([]);
    }


    function deleteSupplier() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var supplierId = getSelectedIdFromGridKendo(gridSupplier);
        $.ajax({
            url: "${createLink(controller: 'supplier', action: 'delete')}?id=" + supplierId,
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
        if (executeCommonPreConditionForSelectKendo(gridSupplier, 'supplier') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected Supplier?')) {
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
        var row = gridSupplier.select();
        row.each(function () {
            gridSupplier.removeRow($(this));
        });
        resetForm();
        showSuccess(data.message);
    }

    function editSupplier() {
        if (executeCommonPreConditionForSelectKendo(gridSupplier, 'supplier') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridSupplier'));
        var supplierObj = getSelectedObjectFromGridKendo(gridSupplier);
        showSupplier(supplierObj);
    }

    function showSupplier(supplierObj) {
        supplierModel.set('supplier', supplierObj);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

</script>
