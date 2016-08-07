<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <app:ifAllUrl urls="/userRole/update">
        <li onclick="editUserRole();"><i class="fa fa-edit"></i>Edit</li>
    </app:ifAllUrl>
    <sec:access url="/userRole/delete">
        <li onclick="deleteUserRole();"><i class="fa fa-trash-o"></i>Delete</li>
    </sec:access>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script type="text/javascript">
    var dropDownAppUserRole, roleId;
    var userRoleMappingModel, dataSource, gridUserRole;

    $(document).ready(function () {
        onLoadUserRolePage();
        initUserRoleMappingGrid();
        initObservable();
    });

    function onLoadUserRolePage() {
        checkOnLoadError();
        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#userRoleForm"), onSubmitUserRole);

        // update page title
        $(document).attr('title', "MIS - User Role Mapping");
        loadNumberedMenu(MENU_ID_APPLICATION, "#role/show");

    }
    function checkOnLoadError() {
        var isError = '${isError}';
        var msg = '${message}';
        if (isError == 'true') {
            showError(msg);
            return false;
        } else {
            roleId = '${oId ? oId: "0"}';
        }
    }

    function onSubmitUserRole() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#existingUserId').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'userRole', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'userRole', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#userRoleForm").serialize(),
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
        if (!validateForm($("#userRoleForm"))) {
            return false;
        }
        return true;
    }

    function executePostCondition(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            try {
                var newEntry = data.userRole;
                if ($('#existingUserId').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = gridUserRole.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = gridUserRole.select();
                    var allItems = gridUserRole.items();
                    var selectedIndex = allItems.index(selectedRow);
                    gridUserRole.removeRow(selectedRow);
                    gridUserRole.dataSource.insert(selectedIndex, newEntry);
                }
                resetForm();
                showSuccess(data.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        clearForm($("#userRoleForm"), dropDownAppUserRole);
        initObservable();
        $('#userId').attr('default_value', '');
        $('#userId').reloadMe();
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function editUserRole() {
        if (executeCommonPreConditionForSelectKendo(gridUserRole, 'User-Role') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridUserRole'));
        showLoadingSpinner(true);
        var selectedRow = getSelectedObjectFromGridKendo(gridUserRole);
        showUserRole(selectedRow);
        showLoadingSpinner(false);
    }


    function showUserRole(selectedRow) {
        userRoleMappingModel.set('userRoleMapping', selectedRow);
        $('#existingUserId').attr('value', selectedRow.userId);
        $('#userId').attr('default_value', selectedRow.userId);
        $('#userId').reloadMe();
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function deleteUserRole() {
        var selectedRow = getSelectedObjectFromGridKendo(gridUserRole);
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true); // Spinner Show on AJAX Call
        var userId = selectedRow.userId;
        var roleId = selectedRow.roleId;
        $.ajax({
            url: "${createLink(controller: 'userRole', action: 'delete')}?userId=" + userId + '&roleId=' + roleId,
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
        if (executeCommonPreConditionForSelectKendo(gridUserRole, 'user-role') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected user-role?')) {
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
        var row = gridUserRole.select();
        row.each(function () {
            gridUserRole.removeRow($(this));
        });
        resetForm();
        showSuccess(data.message);
    }

    function initUserRoleMappingGrid() {
        initDataSource();
        $("#gridUserRole").kendoGrid({
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
                {field: "userName", title: "User", width: 120, sortable: true, filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}},
                {field: "loginId", title: "Login ID", width: 100, sortable: false, filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}},
                {field: "email", title: "Email", width: 100, sortable: false, filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}},
                {field: "cellNumber", title: "Phone Number", width: 70, sortable: false, filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}}
            ],
            filterable: {
                mode: "row"
            },
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridUserRole = $("#gridUserRole").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function reloadKendoGrid() {
        gridUserRole.dataSource.filter([]);
    }

    function initDataSource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/userRole/list",
                    data: { roleId: roleId},
                    dataType: "json",
                    type: "post"
                }
            },
            schema: {
                type: 'json',
                data: "list", total: "count",
                model: {
                    fields: {
                        userId: { type: "number" },
                        roleId: { type: "number"},
                        roleName: { type: "string"},
                        userName: { type: "string"},
                        loginId: { type: "string"},
                        email: { type: "string"},
                        cellNumber: { type: "string"},
                        companyId: { type: "number"}
                    }
                },
                parse: function (data) {
                    checkIsErrorGridKendo(data);
                    return data;
                }
            },
            sort: {field: 'userName', dir: 'asc'},  // default sort
            pageSize: ${com.athena.mis.BaseService.DEFAULT_RESULT_PER_PAGE},
            serverPaging: true,
            serverFiltering: true,
            serverSorting: true
        });
    }

    function initObservable() {
        userRoleMappingModel = kendo.observable(
                {
                    userRoleMapping: {
                        existingUserId: "",
                        userId: "",
                        roleId: roleId,
                        roleName: "",
                        userName: "",
                        companyId: ""
                    }
                }
        );
        kendo.bind($("#application_top_panel"), userRoleMappingModel);
    }

</script>
