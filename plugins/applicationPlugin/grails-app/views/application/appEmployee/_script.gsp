<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <app:checkSystemUser isPowerUser="true">
        <app:ifAllUrl urls="/appEmployee/update">
            <li onclick="editEmployee();"><i class="fa fa-edit"></i>Edit</li>
        </app:ifAllUrl>
        <sec:access url="/appEmployee/delete">
            <li onclick="deleteEmployee();"><i class="fa fa-trash-o"></i>Delete</li>
        </sec:access>
    </app:checkSystemUser>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script language="javascript">
    var dropDownDesignation;
    var employeeModel, dataSource, gridEmployee;

    $(document).ready(function () {
        onLoadEmployeePage();
        initEmployeeGrid();
        initObservable();
    });

    function onLoadEmployeePage() {
        initializeForm($("#employeeForm"), onSubmitEmployee);

        $(document).attr('title', "MIS - Create Employee");
        loadNumberedMenu(MENU_ID_APPLICATION, "#appEmployee/show");
    }

    function onSubmitEmployee() {
        if (executePreConditionForSubmit() == false) {
            return false;
        }
        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'appEmployee', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'appEmployee', action: 'update')}";
        }
        jQuery.ajax({
            type: 'post',
            data: jQuery("#employeeForm").serialize(),
            url: actionUrl,
            success: function (data, textStatus) {
                executePostConditionForSubmit(data);
                setButtonDisabled($('#create'), false);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus);
            },
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);  // stop loading spinner
            },
            dataType: 'json'
        });
        return false;
    }

    function executePreConditionForSubmit() {
        // validate form data
        if (!validateForm($("#employeeForm"))) {
            return false;
        }
        if (!checkCustomDate($("#dateOfJoin"), "Joining  ")) {
            return false;
        }
        // check validation of birth date
        if ($("#dateOfBirth").val()) {
            if (!checkCustomDate($("#dateOfBirth"), "Birth ")) {
                return false;
            }
            if (!customValidateDate($("#dateOfBirth"), 'Date of Birth', $("#dateOfJoin"), 'Date of Join')) {
                return false;
            }
        }
        return true;
    }

    function executePostConditionForSubmit(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            try {
                var newEntry = data.employee;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = gridEmployee.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = gridEmployee.select();
                    var allItems = gridEmployee.items();
                    var selectedIndex = allItems.index(selectedRow);
                    gridEmployee.removeRow(selectedRow);
                    gridEmployee.dataSource.insert(selectedIndex, newEntry);
                }
                resetForm();
                showSuccess(data.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function editEmployee() {
        if (executeCommonPreConditionForSelectKendo(gridEmployee, 'employee') == false) {
            return false;
        }
        showCreatePanel($('div.expand-div'), $('#gridEmployee'));
        var employee = getSelectedObjectFromGridKendo(gridEmployee);
        showEmployee(employee);
    }

    function showEmployee(employeeObj) {
        employeeModel.set('employee', employeeObj);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelectKendo(gridEmployee, 'employee') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected employee?')) {
            return false;
        }
        return true;
    }

    function deleteEmployee() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var employeeId = getSelectedIdFromGridKendo(gridEmployee);

        $.ajax({
            url: "${createLink(controller:'appEmployee', action:  'delete')}?id=" + employeeId,
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
        gridEmployee.dataSource.filter([]);
    }

    function executePostConditionForDelete(data) {
        if (data.isError) {
            showError(data.message);
            return false;
        }
        var row = gridEmployee.select();
        row.each(function () {
            gridEmployee.removeRow($(this));
        });
        resetForm();
        showSuccess(data.message);
    }

    // reset the form
    function resetForm() {
        clearForm($("#employeeForm"), $('#fullName'));
        initObservable();
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");
    }

    function initEmployeeGrid() {
        initDataSource();
        $("#gridEmployee").kendoGrid({
            dataSource: dataSource,
            height: getGridHeightKendo(),
            selectable: true,
            sortable: true,
            resizable: true,
            reorderable: false,
            pageable: {
                refresh: false,
                pageSizes: [10, 15, 20],
                buttonCount: 4
            },
            columns: [
                {
                    field: "fullName",
                    title: "Full Name",
                    width: 120,
                    sortable: true,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                },
                {
                    field: "nickName",
                    title: "Nick Name",
                    width: 120,
                    sortable: true,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                },
                {field: "designationName", title: "Designation", width: 70, sortable: false, filterable: false},
                {
                    field: "mobileNo",
                    title: "Mobile",
                    width: 110,
                    sortable: true,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                },
                {
                    field: "email",
                    title: "Email",
                    width: 110,
                    sortable: true,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                },
                {
                    field: "dateOfJoin", title: "Joining Date", width: 60, sortable: true, filterable: false,
                    template: "#= kendo.toString(kendo.parseDate(dateOfJoin, 'yyyy-MM-dd'), 'dd-MMM-yyyy') #"
                }
            ],
            filterable: {
                mode: "row"
            },

            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridEmployee = $("#gridEmployee").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function initDataSource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "${createLink(controller:'appEmployee', action: 'list')}",
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
                        companyId: {type: "number"},
                        fullName: {type: "string"},
                        nickName: {type: "string"},
                        designationId: {type: "number"},
                        designationName: {type: "string"},
                        mobileNo: {type: "string"},
                        email: {type: "string"},
                        dateOfJoin: {type: "date"},
                        dateOfBirth: {type: "date"},
                        address: {type: "string"}
                    }
                },
                parse: function (data) {
                    checkIsErrorGridKendo(data);
                    return data;
                }
            },
            sort: {field: 'fullName', dir: 'asc'},  // default sort
            pageSize: ${com.athena.mis.BaseService.DEFAULT_RESULT_PER_PAGE},
            serverPaging: true,
            serverFiltering: true,
            serverSorting: true
        });
    }

    function initObservable() {
        employeeModel = kendo.observable(
                {
                    employee: {
                        id: "",
                        version: "",
                        companyId: "",
                        fullName: "",
                        nickName: "",
                        designationId: "",
                        designationName: "",
                        mobileNo: "",
                        email: "",
                        dateOfJoin: "",
                        dateOfBirth: "",
                        address: ""
                    }
                }
        );
        kendo.bind($("#application_top_panel"), employeeModel);
    }

</script>
