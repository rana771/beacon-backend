<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <app:ifAllUrl urls="/currency/update">
        <li onclick="editCurrency();"><i class="fa fa-edit"></i>Edit</li>
    </app:ifAllUrl>
    <sec:access url="/currency/delete">
        <li onclick="deleteCurrency();"><i class="fa fa-trash-o"></i>Delete</li>
    </sec:access>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script type="text/javascript">

    var currencyGrid, currencyDataSource, currencyModel;

    $(document).ready(function () {
        onLoadCurrency();
        initCustomerGrid();
        initObservable();
    });

    function onLoadCurrency() {
        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#currencyForm"), onSubmitCurrency);

        // update page title
        $(document).attr('title', "MIS - Create currency");
        loadNumberedMenu(MENU_ID_APPLICATION, "#currency/show");
    }

    function executePreCondition() {
        if (!validateForm($("#currencyForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitCurrency() {

        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        // Spinner Show on AJAX Call
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'currency', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'currency', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#currencyForm").serialize(),
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
                var newEntry = result.currency;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = currencyGrid.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = currencyGrid.select();
                    var allItems = currencyGrid.items();
                    var selectedIndex = allItems.index(selectedRow);
                    currencyGrid.removeRow(selectedRow);
                    currencyGrid.dataSource.insert(selectedIndex, newEntry);
                }
                resetForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        setButtonDisabled($('#create'), false);
        clearForm($("#currencyForm"), $('#name'));
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");
    }

    <%-- Start : Delete operation of Currency --%>
    function deleteCurrency() {
        if (executePreConditionForDelete() == false) {
            return;
        }

        showLoadingSpinner(true); // Spinner Show on AJAX Call

        var currencyId = getSelectedIdFromGridKendo(currencyGrid);

        $.ajax({
            url: "${createLink(controller: 'currency', action: 'delete')}?id=" + currencyId,
            success: executePostConditionForDelete,
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus)
            },
            complete: onCompleteAjaxCall,	// Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function reloadKendoGrid() {
        currencyGrid.dataSource.filter([]);
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelectKendo(currencyGrid, 'currency') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected Currency?')) {
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
        var row = currencyGrid.select();
        row.each(function () {
            currencyGrid.removeRow($(this));
        });
        resetForm();
        showSuccess(data.message);
    }

    <%-- Start: Edit Currency --%>

    function editCurrency() {
        clearForm($("#currencyForm"));
        if (executeCommonPreConditionForSelectKendo(currencyGrid, 'currency') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridCurrency'));
        var currency = getSelectedObjectFromGridKendo(currencyGrid);
        showCurrency(currency);
    }

    function showCurrency(data) {
        currencyModel.set('currency', data);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
    }

    function initDataSource() {
        currencyDataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/currency/list",
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
                        symbol: {type: "string"},
                        otherCode: {type: "string"}
                    }
                },
                parse: function (data) {
                    checkIsErrorGridKendo(data);
                    return data;
                }
            },
            pageSize: ${com.athena.mis.BaseService.DEFAULT_RESULT_PER_PAGE},
            serverPaging: true,
            serverFiltering: true,
            serverSorting: true
        });
    }

    function initCustomerGrid() {
        initDataSource();
        $("#gridCurrency").kendoGrid({
            dataSource: currencyDataSource,
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
                    width: 180,
                    sortable: true,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                },
                {field: "symbol", title: "Symbol", width: 120, sortable: true, filterable: false},
                {field: "otherCode", title: "Other Code", width: 120, sortable: false, filterable: false}
            ],
            filterable: {
                mode: "row"
            },

            toolbar: kendo.template($("#gridToolbar").html())
        });
        currencyGrid = $("#gridCurrency").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function initObservable() {
        currencyModel = kendo.observable(
                {
                    currency: {
                        id: "",
                        version: "",
                        name: "",
                        symbol: "",
                        otherCode: ""
                    }
                }
        );
        kendo.bind($("#application_top_panel"), currencyModel);
    }
</script>
