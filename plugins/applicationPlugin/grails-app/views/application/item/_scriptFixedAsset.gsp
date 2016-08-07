<script type="text/x-kendo-template" id="gridToolbarFixedAsset">
    <ul id="menuGridFixedAsset" class="kendoGridMenu">
        <app:ifAllUrl urls="/item/updateFixedAssetItem">
            <li onclick="editItem();"><i class="fa fa-edit"></i>Edit</li>
        </app:ifAllUrl>
        <sec:access url="/item/deleteFixedAssetItem">
            <li onclick="deleteItem();"><i class="fa fa-trash-o"></i>Delete</li>
        </sec:access>
        <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
    </ul>
</script>

<script language="javascript">
    var dropDownItemType, itemModel, gridFixedAssetModel, fixedAssetModel;
    var itemListModel = false;

    $(document).ready(function () {
        onLoadFixedAssetPage();
        initFixedAssetGrid();
        initObservable();

        $("#isIndividualEntity").kendoMobileSwitch({onLabel: "YES",offLabel: "NO"});
    });

    function onLoadFixedAssetPage() {

        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#fixedAssetItemForm"), onSubmitItem);
        $(document).attr('title', "MIS - Create Fixed Asset Item"); //updating page title
        loadNumberedMenu(MENU_ID_FIXED_ASSET, "#item/showFixedAssetItem");

    }

    function initFixedAssetGrid(){

        initDataSource();
        $("#gridFixedAsset").kendoGrid({
            dataSource: dataSource,
            height: getGridHeightKendo(),
            selectable: true,
            sortable: true,
            resizable: true,
            reorderable: true,
            columnMenu: { filterable: false, sortable:false},
            pageable: {
                refresh: false,
                pageSizes: [10, 15, 20],
                buttonCount: 4
            },
            columns: [
                {field: "name", title: "Name", width: 150, sortable: true,
                    filterable: {cell: {operator: "contains", dataSource:getBlankDataSource()}}
                },
                {field: "code", title: "Code", width: 70, sortable: true,
                    filterable: {cell: {operator: "contains", dataSource:getBlankDataSource()}}
                },
                {field: "unit",title: "Unit",width: 50,sortable: false,filterable: false},
                {field: "itemTypeName",title: "Item Type",width: 70, sortable: true,filterable: false},
                {field: "isIndividualEntity",title: "Individual Entity",width: 70,sortable: true, filterable: false,
                    template: "#=isIndividualEntity?'YES':'NO' #",
                    attributes: {style: setAlignCenter()},headerAttributes: {style: setAlignCenter()}
                }
            ],
            filterable: {mode: "row"},
            toolbar: kendo.template($("#gridToolbarFixedAsset").html())
        });
        gridFixedAssetModel = $("#gridFixedAsset").data("kendoGrid");
        $("#menuGridFixedAsset").kendoMenu();
    }

    function initDataSource(){
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/item/listFixedAssetItem",
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
                        isIndividualEntity: {type: "boolean"},
                        categoryId: {type: "number"},
                        itemTypeId: {type: "number"},
                        itemTypeName: {type: "string"}
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

    function initObservable() {
        fixedAssetModel = kendo.observable(
                {
                    asset: {
                        id: "",
                        version: "",
                        name: "",
                        code: "",
                        unit: "",
                        itemTypeId: "",
                        isIndividualEntity: false
                    }
                }
        );
        kendo.bind($("#application_top_panel"), fixedAssetModel);
    }


    function editItem() {

        if (executeCommonPreConditionForSelectKendo(gridFixedAssetModel, 'fixed asset') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridFixedAsset'));
        var itemObj = getSelectedObjectFromGridKendo(gridFixedAssetModel);
        showFixedAsset(itemObj);
    }

    function showFixedAsset(itemObj) {
        fixedAssetModel.set('asset', itemObj);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
    }

    function resetFixedAssetItemForm() {
        clearForm($("#fixedAssetItemForm"), $('#name'));
        initObservable();
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }


    function onSubmitItem() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'item', action: 'createFixedAssetItem')}";
        } else {
            actionUrl = "${createLink(controller:'item', action: 'updateFixedAssetItem')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#fixedAssetItemForm").serialize(),
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

    function executePreCondition() {
        if (!validateForm($("#fixedAssetItemForm"))) {
            return false;
        }
        return true;
    }

    function executePostCondition(result) {
        if (result.isError) {
            showError(result.message);
            showLoadingSpinner(false);
        } else {
            try {
                var newEntry = result.item;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = gridFixedAssetModel.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = gridFixedAssetModel.select();
                    var allItems = gridFixedAssetModel.items();
                    var selectedIndex = allItems.index(selectedRow);
                    gridFixedAssetModel.removeRow(selectedRow);
                    gridFixedAssetModel.dataSource.insert(selectedIndex, newEntry);
                }
                resetFixedAssetItemForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }


    function deleteItem() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var itemId = getSelectedIdFromGridKendo(gridFixedAssetModel);

        $.ajax({
            url: "${createLink(controller:'item', action: 'deleteFixedAssetItem')}?id=" + itemId,
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
        gridFixedAssetModel.dataSource.filter([]);
    }

    function executePreConditionForDelete() {

        if (executeCommonPreConditionForSelectKendo(gridFixedAssetModel, 'fixed item') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected item?')) {
            return false;
        }
        return true;
    }

    function executePostConditionForDelete(data) {

        if (data.isError) {
            showError(data.message);
            return false;
        }
        var row = gridFixedAssetModel.select();
        row.each(function () {
            gridFixedAssetModel.removeRow($(this));
        });
        resetFixedAssetItemForm();      // why do we need to refresh FORM!!! on delete action
        showSuccess(data.message);
    }

</script>
