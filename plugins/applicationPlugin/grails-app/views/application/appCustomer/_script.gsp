<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <app:ifAllUrl urls="/appCustomer/update">
        <li onclick="selectCustomer();"><i class="fa fa-edit"></i>Edit</li>
    </app:ifAllUrl>
    <sec:access url="/appCustomer/delete">
        <li onclick="deleteCustomer();"><i class="fa fa-trash-o"></i>Delete</li>
    </sec:access>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script language="javascript">
    var customerGrid, customerDataSource, customerModel;

    $(document).ready(function () {
        onLoadCustomerPage();
        initCustomerGrid();
        initObservable();
    });

    function onLoadCustomerPage() {
        // initialize form with kendo validator & bind onSubmit method
        initializeForm($("#customerForm"), onSubmitCustomer);
        // update page title
        $(document).attr('title', "MIS - Create Customer");
        loadNumberedMenu(MENU_ID_APPLICATION, "#appCustomer/show");
    }

    function executePreCondition() {
        if (!validateForm($("#customerForm"))) {
            return false;
        }
        if (!checkCustomDate($("#dateOfBirth"), "Birth ")) {
            return false;
        }
        return true;
    }

    function onSubmitCustomer() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'appCustomer', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'appCustomer', action:  'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#customerForm").serialize(),
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
                var newEntry = result.customer;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = customerGrid.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = customerGrid.select();
                    var allItems = customerGrid.items();
                    var selectedIndex = allItems.index(selectedRow);
                    customerGrid.removeRow(selectedRow);
                    customerGrid.dataSource.insert(selectedIndex, newEntry);
                }
                resetForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        clearForm($("#customerForm"), $('#fullName'));
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");
    }

    function deleteCustomer() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var customerId = getSelectedIdFromGridKendo(customerGrid);
        $.ajax({
            url: "${createLink(controller:'appCustomer', action: 'delete')}?id=" + customerId,
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
        customerGrid.dataSource.filter([]);
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelectKendo(customerGrid, 'customer') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected Customer?')) {
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
        var row = customerGrid.select();
        row.each(function () {
            customerGrid.removeRow($(this));
        });
        resetForm();
        showSuccess(data.message);
    }

    function selectCustomer() {
        if (executeCommonPreConditionForSelectKendo(customerGrid, 'customer') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridCustomer'));
        var customerObj = getSelectedObjectFromGridKendo(customerGrid);
        showCustomer(customerObj);
    }

    function showCustomer(customerObj) {
        customerModel.set('customer', customerObj);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
    }

    function executePreConditionForEdit(ids) {
        if (ids.length == 0) {
            showError("Please select a Customer to edit");
            return false;
        }
        return true;
    }

    function initDataSource() {
        customerDataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/appCustomer/list",
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
                        fullName: {type: "string"},
                        nickName: {type: "string"},
                        phoneNo: {type: "string"},
                        address: {type: "string"},
                        dateOfBirth: {type: "date"},
                        email: {type: "string"}
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
        $("#gridCustomer").kendoGrid({
            dataSource: customerDataSource,
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
                    field: "fullName", title: "Full Name", width: 180, sortable: true,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                },
                {field: "nickName", title: "Nick Name", width: 120, sortable: true, filterable: false},
                {field: "email", title: "Email", width: 120, sortable: false, filterable: false},
                {
                    field: "dateOfBirth", title: "Date Of Birth", width: 100, sortable: false, filterable: false,
                    template: "#= kendo.toString(kendo.parseDate(dateOfBirth, 'yyyy-MM-dd'), 'dd-MMM-yyyy') #"
                },
                {field: "address", title: "Address", width: 150, sortable: true, filterable: false}
            ],
            filterable: {
                mode: "row"
            },

            toolbar: kendo.template($("#gridToolbar").html())
        });
        customerGrid = $("#gridCustomer").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function initObservable() {
        customerModel = kendo.observable(
                {
                    customer: {
                        id: "",
                        version: "",
                        fullName: "",
                        nickName: "",
                        phoneNo: "",
                        address: "",
                        dateOfBirth: "",
                        email: ""
                    }
                }
        );
        kendo.bind($("#application_top_panel"), customerModel);
    }
</script>
