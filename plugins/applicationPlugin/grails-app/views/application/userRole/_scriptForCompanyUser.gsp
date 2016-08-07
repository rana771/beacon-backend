<%@ page import="com.athena.mis.BaseService" %>

<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <li onclick="editUserRoleForCompanyUser();"><i class="fa fa-edit"></i>Edit</li>
    <sec:access url="/userRole/delete">
        <li onclick="deleteUserRole();"><i class="fa fa-trash-o"></i>Delete</li>
    </sec:access>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script type="text/javascript">
    var dropDownRole, dropDownModule, userId;
    var userRoleModel, dataSourceUserRole, gridUserRole;

    $(document).ready(function () {
        onLoadUserRolePage();
        initUserRoleGrid();
        initObservable();
    });

    function onLoadUserRolePage() {
        checkOnLoadError();
        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#userRoleFormForCompanyUser"), onSubmitUserRole);

        // update page title
        $(document).attr('title', "MIS - User Role Mapping");
        loadNumberedMenu(MENU_ID_APPLICATION, "#${leftMenu}");
    }

    function checkOnLoadError() {
        var isError = ${isError};
        var msg = '${message}';
        if (isError) {
            showError(msg);
            return false;
        } else {
            userId = ${userId};
        }
    }

    function onSubmitUserRole() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#existingRoleId').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'userRole', action: 'createForCompanyUser')}";
        } else {
            actionUrl = "${createLink(controller: 'userRole', action: 'updateForCompanyUser')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#userRoleFormForCompanyUser").serialize(),
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
        if (!validateForm($("#userRoleFormForCompanyUser"))) {
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
                if ($('#existingRoleId').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = gridUserRole.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = gridUserRole.select();
                    var allItems = gridUserRole.items();
                    var selectedIndex = allItems.index(selectedRow);
                    gridUserRole.removeRow(selectedRow);
                    gridUserRole.dataSource.insert(selectedIndex, newEntry);
                }
                resetUserRoleForCompanyUserForm();
                showSuccess(data.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetUserRoleForCompanyUserForm() {
        clearForm($("#userRoleFormForCompanyUser"), dropDownRole);
        initObservable();
        $('#roleId').removeAttr('default_value');
        $('#roleId').removeAttr('plugin_id');
        $('#roleId').reloadMe();
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function editUserRoleForCompanyUser() {
        if (executeCommonPreConditionForSelectKendo(gridUserRole, 'user-role') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridUserRole'));
        showLoadingSpinner(true);
        var selectedRow = getSelectedObjectFromGridKendo(gridUserRole);
        showUserRole(selectedRow);
        showLoadingSpinner(false);
    }

    function showUserRole(selectedRow) {
        userRoleModel.set('userRole', selectedRow);
        $('#roleId').attr('default_value', selectedRow.roleId);
        $('#roleId').attr('plugin_id', selectedRow.pluginId);
        $('#roleId').reloadMe();
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
        resetUserRoleForCompanyUserForm();
        showSuccess(data.message);
    }

    function initUserRoleGrid() {
        initDataSource();
        $("#gridUserRole").kendoGrid({
            dataSource: dataSourceUserRole,
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
                    title: "Role",
                    sortable: true,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                }
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
        dataSourceUserRole = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/userRole/listForCompanyUser?userId=" + userId,
                    dataType: "json",
                    type: "post"
                }
            },
            schema: {
                type: 'json',
                data: "list", total: "count",
                model: {
                    fields: {
                        userId: {type: "number"},
                        roleId: {type: "number"},
                        name: {type: "string"},
                        pluginId: {type: "number"}
                    }
                },
                parse: function (data) {
                    checkIsErrorGridKendo(data);
                    return data;
                }
            },
            sort: {field: 'name', dir: 'asc'},  // default sort
            pageSize: ${BaseService.DEFAULT_RESULT_PER_PAGE},
            serverPaging: true,
            serverFiltering: true,
            serverSorting: true
        });
    }

    function initObservable() {
        userRoleModel = kendo.observable(
                {
                    userRole: {
                        existingRoleId: "",
                        userId: userId,
                        roleId: "",
                        pluginId: ""
                    }
                }
        );
        kendo.bind($("#application_top_panel"), userRoleModel);
    }

    function populateRoleList() {
        var pluginId = dropDownModule.value();
        if (pluginId == '') {
            dropDownRole.setDataSource(getKendoEmptyDataSource());
            return;
        }
        $('#roleId').attr('plugin_id', pluginId);
        $('#roleId').reloadMe();
    }

</script>
