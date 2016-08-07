<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <sec:access url="/project/update">
        <li onclick="editProject();"><i class="fa fa-edit"></i>Edit</li>
    </sec:access>
    <sec:access url="/project/delete">
        <li onclick="deleteProject();"><i class="fa fa-trash-o"></i>Delete</li>
    </sec:access>
    <sec:access url="/appUserEntity/show">
        <li onclick="addUserProject();"><i class="fa fa-users"></i>User</li>
    </sec:access>
    <sec:access url="/appAttachment/show">
        <li onclick="addContent();"><i class="fa fa-paperclip"></i>Attachment</li>
    </sec:access>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script language="javascript">
    var entityTypeId, appUserEntityTypeId;
    var gridProject;
    var dataSource, projectModel;

    $(document).ready(function () {
        onLoadProjectPage();
        initProjectGrid();
        initObservable();
        // init kendo switch
        $("#isApproveInFromSupplier").kendoMobileSwitch({
            onLabel: "YES",
            offLabel: "NO"
        });
        $("#isApproveInFromInventory").kendoMobileSwitch({
            onLabel: "YES",
            offLabel: "NO"
        });
        $("#isApproveInvOut").kendoMobileSwitch({
            onLabel: "YES",
            offLabel: "NO"
        });
        $("#isApproveConsumption").kendoMobileSwitch({
            onLabel: "YES",
            offLabel: "NO"
        });
        $("#isApproveProduction").kendoMobileSwitch({
            onLabel: "YES",
            offLabel: "NO"
        });
    });

    function onLoadProjectPage() {
        // initialize form with kendo validator & bind onSubmit method
        initializeForm($("#projectForm"), onSubmitProject);

        // update page title
        $(document).attr('title', "MIS - Create Project");
        loadNumberedMenu(MENU_ID_APPLICATION, "#project/show");
        entityTypeId = $("#entityTypeId").val();
        appUserEntityTypeId = $("#appUserEntityTypeId").val();
    }

    function executePreCondition() {
        if (!validateForm($("#projectForm"))) {   // check kendo validation
            return false;
        }
        if (!customValidateDate($("#startDate"), 'Start date', $("#endDate"), 'end date')) {
            return false;
        }
        return true;
    }

    function onSubmitProject() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'project', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'project', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#projectForm").serialize(),
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
                var newEntry = result.project;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = gridProject.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = gridProject.select();
                    var allItems = gridProject.items();
                    var selectedIndex = allItems.index(selectedRow);
                    gridProject.removeRow(selectedRow);
                    gridProject.dataSource.insert(selectedIndex, newEntry);
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
        clearForm($("#projectForm"), $('#name'));
        initObservable();
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function addContent() {
        if (executeCommonPreConditionForSelectKendo(gridProject, 'project') == false) {
            return;
        }
        showLoadingSpinner(true);
        var projectId = getSelectedIdFromGridKendo(gridProject);
        var loc = "${createLink(controller:'appAttachment', action: 'show')}?oId=" + projectId + "&url=project/show" + "&entityTypeId=" + entityTypeId;
        router.navigate(formatLink(loc));
        return false;
    }

    function reloadKendoGrid() {
        gridProject.dataSource.filter([]);
    }

    function addUserProject() {
        if (executeCommonPreConditionForSelectKendo(gridProject, 'project') == false) {
            return;
        }
        showLoadingSpinner(true);
        var projectId = getSelectedIdFromGridKendo(gridProject);
        var loc = "${createLink(controller:'appUserEntity', action: 'show')}?oId=" + projectId + "&url=project/show" + "&entityTypeId=" + appUserEntityTypeId;
        router.navigate(formatLink(loc));
        return false;
    }

    function reloadGrid() {
        gridProject.dataSource.filter([]);
    }

    function deleteProject() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true); // Spinner Show on AJAX Call
        var projectId = getSelectedIdFromGridKendo(gridProject);
        $.ajax({
            url: "${createLink(controller:'project', action: 'delete')}?id=" + projectId,
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
        if (executeCommonPreConditionForSelectKendo(gridProject, 'project') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected Project?')) {
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
        var row = gridProject.select();
        row.each(function () {
            gridProject.removeRow($(this));
        });
        resetForm();
        showSuccess(data.message);
    }

    function editProject() {
        if (executeCommonPreConditionForSelectKendo(gridProject, 'project') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridProject'));
        resetForm();
        showLoadingSpinner(true);
        var project = getSelectedObjectFromGridKendo(gridProject);
        showProject(project);
        showLoadingSpinner(false);
    }

    function showProject(project) {
        projectModel.set('project', project);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function initDatasource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/project/list",
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
                        contentCount: {type: "number"},
                        startDate: {type: "date"},
                        endDate: {type: "date"},
                        description: {type: "string"},
                        isApproveInFromSupplier: {type: "boolean"},
                        isApproveInFromInventory: {type: "boolean"},
                        isApproveInvOut: {type: "boolean"},
                        isApproveConsumption: {type: "boolean"},
                        isApproveProduction: {type: "boolean"}
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

    function initProjectGrid() {
        initDatasource();
        $("#gridProject").kendoGrid({
            dataSource: dataSource,
            height: getGridHeightKendo(),
            selectable: true,
            sortable: true,
            resizable: true,
            reorderable: true,
            //columnMenu: {filterable: false, sortable: false},
            pageable: {
                refresh: false,
                pageSizes: [10, 15, 20],
                buttonCount: 4
            },
            columns: [
                {
                    field: "name",
                    title: "Name",
                    sortable: true,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                },
                {
                    field: "code",
                    title: "Code",
                    width: 120,
                    sortable: true,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                },
                {
                    field: "contentCount",
                    title: "Content Count",
                    sortable: false,
                    filterable: false,
                    attributes: {style: setAlignRight()},
                    headerAttributes: {style: setAlignRight()}
                },
                {
                    field: "startDate", title: "Start Date", sortable: true,
                    filterable: {cell: {template: formatFilterableDate}},
                    template: "#= kendo.toString(kendo.parseDate(startDate, 'yyyy-MM-dd'), 'dd-MMM-yyyy') #"
                }
            ],
            filterable: {mode: "row"},
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridProject = $("#gridProject").data("kendoGrid");
        //initKendoColumnMenu(gridProject, $("#menuGrid"));
        $("#menuGrid").kendoMenu();
    }

    function initObservable() {
        projectModel = kendo.observable(
                {
                    project: {
                        id: "",
                        version: "",
                        name: "",
                        code: "",
                        startDate: "",
                        endDate: "",
                        description: "",
                        isApproveInFromSupplier: false, isApproveInFromInventory: false,
                        isApproveInvOut: false, isApproveConsumption: false, isApproveProduction: false
                    }
                }
        );
        kendo.bind($("#application_top_panel"), projectModel);
    }

    /**
     * test kendo column menu
     */
    /*function initKendoColumnMenu(gridObj, kendoMenu) {
        var menu = "<li class='pull-right custom-kendo-column-menu'><i class='fa fa-list c-gray'></i><ul id='kendoColumnMenu'></ul></li>";
        kendoMenu.append(menu);
        for (var i = 0, max = gridObj.columns.length; i < max; i++) {
            var menuItem = "<li><label><input type='checkbox' checked='checked' " +
                    "data-field='" + gridObj.columns[i].field +
                    "'/>&nbsp;" + gridObj.columns[i].title + "</label></li>";
            $('#kendoColumnMenu').append(menuItem);
        }
        $("#kendoColumnMenu > li input[type='checkbox']").change(function () {
            var field = $(this).data("field");
            if ($(this).is(":checked")) {
                gridObj.showColumn(field);
            } else {
                gridObj.hideColumn(field);
            }
        });
    }*/
</script>

%{--<style>
.custom-kendo-column-menu .k-animation-container {
    left: auto !important;
    right: -3px;
}
</style>--}%

