<%@ page import="com.athena.mis.BaseService" %>

<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <app:ifAllUrl urls="/roleModule/update">
        <li onclick="editRoleModule();"><i class="fa fa-edit"></i>Edit</li>
    </app:ifAllUrl>
    <sec:access url="/roleModule/delete">
        <li onclick="deleteRoleModule();"><i class="fa fa-trash-o"></i>Delete</li>
    </sec:access>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script type="text/javascript">
    var dropDownModule, roleId;
    var roleModuleModel, dataSource, gridRoleModule;

    $(document).ready(function () {
        onLoadRoleModulePage();
        initRoleModuleGrid();
        initObservable();
    });

    function onLoadRoleModulePage() {
        checkOnLoadError();
        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#roleModuleForm"), onSubmitRoleModule);
        // update page title
        $(document).attr('title', "MIS - Role Module Mapping");
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

    function onSubmitRoleModule() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'roleModule', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'roleModule', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#roleModuleForm").serialize(),
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
        if (!validateForm($("#roleModuleForm"))) {
            return false;
        }
        return true;
    }

    function executePostCondition(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            try {
                var newEntry = data.roleModule;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = gridRoleModule.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = gridRoleModule.select();
                    var allItems = gridRoleModule.items();
                    var selectedIndex = allItems.index(selectedRow);
                    gridRoleModule.removeRow(selectedRow);
                    gridRoleModule.dataSource.insert(selectedIndex, newEntry);
                }
                resetForm();
                showSuccess(data.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        clearForm($("#roleModuleForm"), dropDownModule);
        initObservable();
        $('#moduleId').attr('default_value', '');
        $('#moduleId').reloadMe();
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function editRoleModule() {
        if (executeCommonPreConditionForSelectKendo(gridRoleModule, 'a module') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridRoleModule'));
        var selectedRow = getSelectedObjectFromGridKendo(gridRoleModule);
        showRoleModule(selectedRow);
        showLoadingSpinner(false);
    }


    function showRoleModule(selectedRow) {
        roleModuleModel.set('roleModule', selectedRow);
        $('#moduleId').attr('default_value', selectedRow.moduleId);
        $('#moduleId').reloadMe();
        dropDownModule.value(selectedRow.moduleId);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function deleteRoleModule() {
        var id = getSelectedIdFromGridKendo(gridRoleModule);
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true); // Spinner Show on AJAX Call
        $.ajax({
            url: "${createLink(controller: 'roleModule', action: 'delete')}?id=" + id,
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
        if (executeCommonPreConditionForSelectKendo(gridRoleModule, 'a module') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected role module mapping?')) {
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
        var row = gridRoleModule.select();
        row.each(function () {
            gridRoleModule.removeRow($(this));
        });
        resetForm();
        showSuccess(data.message);
    }

    function initRoleModuleGrid() {
        initDataSource();
        $("#gridRoleModule").kendoGrid({
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
                    field: "moduleName", title: "Module", width: 250, sortable: true,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                }
            ],
            filterable: {
                mode: "row"
            },
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridRoleModule = $("#gridRoleModule").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function reloadKendoGrid() {
        gridRoleModule.dataSource.filter([]);
    }

    function initDataSource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/roleModule/list",
                    data: {roleId: roleId},
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
                        roleId: {type: "number"},
                        moduleId: {type: "number"},
                        moduleName: {type: "string"},
                        companyId: {type: "number"}
                    }
                },
                parse: function (data) {
                    checkIsErrorGridKendo(data);
                    return data;
                }
            },
            sort: {field: 'moduleName', dir: 'asc'},  // default sort
            pageSize: ${BaseService.DEFAULT_RESULT_PER_PAGE},
            serverPaging: true,
            serverFiltering: true,
            serverSorting: true
        });
    }

    function initObservable() {
        roleModuleModel = kendo.observable(
                {
                    roleModule: {
                        id: "",
                        roleId: roleId,
                        moduleId: ""
                    }
                }
        );
        kendo.bind($("#application_top_panel"), roleModuleModel);
    }

</script>
