<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <app:ifAllUrl urls="/role/update">
        <li onclick="editRole();"><i class="fa fa-edit"></i>Edit</li>
    </app:ifAllUrl>
    <sec:access url="/role/delete">
        <li onclick="deleteRole();"><i class="fa fa-trash-o"></i>Delete</li>
    </sec:access>
    <sec:access url="/userRole/show">
        <li onclick="showUserRole();"><i class="fa fa-user"></i>User</li>
    </sec:access>
    <sec:access url="/roleModule/show">
        <li onclick="showRoleModule();"><i class="fa fa-cube"></i>Module</li>
    </sec:access>
    <sec:access url="/role/downloadUserRoleReport">
        <li onclick="userRoleReport();"><i class="fa fa-file-text"></i>Report</li>
    </sec:access>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script type="text/javascript">
    var roleListModel = false;
    var gridRole, dataSource, roleModel;
    $(document).ready(function () {
        onLoadRolePage();
        initRoleGrid();
        initObservable();
    });

    function onLoadRolePage() {
        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#roleForm"), onSubmitRole);

        // update page title
        $(document).attr('title', "MIS - Role");
        loadNumberedMenu(MENU_ID_APPLICATION, "#role/show");
    }

    function onSubmitRole() {
        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'role', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'role', action: 'update')}";
        }
        jQuery.ajax({
            type: 'post',
            data: jQuery("#roleForm").serialize(),
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
        if (!validateForm($("#roleForm"))) {
            return false;
        }
        return true;
    }

    function executePostCondition(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            try {
                var newEntry = data.role;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = gridRole.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = gridRole.select();
                    var allItems = gridRole.items();
                    var selectedIndex = allItems.index(selectedRow);
                    gridRole.removeRow(selectedRow);
                    gridRole.dataSource.insert(selectedIndex, newEntry);
                }
                resetForm();
                showSuccess(data.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        clearForm($("#roleForm"), $('#name'));
        initObservable();
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function editRole() {
        if (executePreConditionForEdit() == false) {
            return;
        }
        var role = getSelectedObjectFromGridKendo(gridRole);
        if (role.roleTypeId != 0) {
            showError('Selected role is reserved and can not be edited');
            return false;
        }
        showCreatePanel($('div.expand-div'), $('#gridRole'));
        resetForm();
        showLoadingSpinner(true);
        showRole(role);
        showLoadingSpinner(false);
    }

    function executePreConditionForEdit() {
        if (executeCommonPreConditionForSelectKendo(gridRole, 'role') == false) {
            return false;
        }
        return true;
    }

    function showRole(role) {
        roleModel.set('role', role);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function deleteRole() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true); // Spinner Show on AJAX Call
        var roleId = getSelectedIdFromGridKendo(gridRole);

        $.ajax({
            url: "${createLink(controller: 'role', action: 'delete')}?id=" + roleId,
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
        if (executeCommonPreConditionForSelectKendo(gridRole, 'role') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected role? \n Related right permission will be delete accordingly')) {
            return false;
        }
        var roleTypeId = getSelectedValueFromGridKendo(gridRole, 'roleTypeId');
        if (roleTypeId != 0) {
            showError('Selected role is reserved and can not be deleted');
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
        var row = gridRole.select();
        row.each(function () {
            gridRole.removeRow($(this));
        });
        resetForm();
        showSuccess(data.message);
    }

    function showUserRole() {
        if (executeCommonPreConditionForSelectKendo(gridRole, 'role') == false) {
            return;
        }
        showLoadingSpinner(true);
        var roleId = getSelectedIdFromGridKendo(gridRole);
        var loc = "${createLink(controller: 'userRole', action: 'show')}?oId=" + roleId + "&url=role/show";
        router.navigate(formatLink(loc));
        return false;
    }

    function showRoleModule() {
        if (executeCommonPreConditionForSelectKendo(gridRole, 'role') == false) {
            return;
        }
        showLoadingSpinner(true);
        var roleId = getSelectedIdFromGridKendo(gridRole);
        var loc = "${createLink(controller: 'roleModule', action: 'show')}?oId=" + roleId + "&url=role/show";
        router.navigate(formatLink(loc));
        return false;
    }

    function userRoleReport() {
        showLoadingSpinner(true);
        if (confirm('Do you want to download the user role report now?')) {
            var url = "${createLink(controller: 'role', action: 'downloadUserRoleReport')}";
            document.location = url;
        }
        showLoadingSpinner(false);
    }


    function reloadKendoGrid() {
        gridRole.dataSource.filter([]);
    }

    function initRoleGrid() {
        initDataSource();
        $("#gridRole").kendoGrid({
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
                    field: "id",
                    title: "ID",
                    width: 50,
                    sortable: true,
                    filterable: false

                },
                {
                    field: "name",
                    title: "Role",
                    width: 250,
                    sortable: true,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}},
                    template: "#= setColor(name, roleTypeId) #"
                },
                {
                    field: "moduleCount",
                    title: "Module Count",
                    width: 250,
                    sortable: true,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                },
                {field: "count", title: "User Count", width: 150, sortable: true, filterable: false}
            ],
            filterable: {
                mode: "row"
            },
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridRole = $("#gridRole").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function setColor(name, roleTypeId) {
        if (roleTypeId != 0) {
            return "<b>" + name + "</b>";
        }
        return name;
    }

    function initDataSource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "${createLink(controller: 'role', action: 'list')}",
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
                        count: {type: "number"},
                        roleTypeId: {type: "number"},
                        companyId: {type: "number"},
                        moduleCount: {type: "number"}
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

    function initObservable() {
        roleModel = kendo.observable(
                {
                    role: {
                        id: "",
                        version: "",
                        name: "",
                        count: "",
                        companyId: ""
                    }
                }
        );
        kendo.bind($("#application_top_panel"), roleModel);
    }

</script>
