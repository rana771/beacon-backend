<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <app:ifAllUrl urls="/appGroup/update">
        <li onclick="editAppGroup();"><i class="fa fa-edit"></i>Edit</li>
    </app:ifAllUrl>
    <sec:access url="/appGroup/delete">
        <li onclick="deleteGroup();"><i class="fa fa-trash-o"></i>Delete</li>
    </sec:access>
    <sec:access url="/appUserEntity/show">
        <li onclick="addUser();"><i class="fa fa-users"></i>User</li>
    </sec:access>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script language="javascript">
    var gridAppGroup, dataSource, appGroupModel;
    var entityTypeId;

    $(document).ready(function () {
        onLoadAppGroupPage();
        initAppGroupGrid();
        initObservable();
    });

    function onLoadAppGroupPage() {
        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#appGroupForm"), onSubmitAppGroup);
        entityTypeId = $("#entityTypeId").val();
        // update page title
        $(document).attr('title', "MIS - Create Group");
        loadNumberedMenu(MENU_ID_APPLICATION, "#appGroup/show");
    }

    function executePreCondition() {
        if (!validateForm($("#appGroupForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitAppGroup() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'appGroup', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'appGroup', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#appGroupForm").serialize(),
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
                var newEntry = result.appGroup;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = gridAppGroup.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = gridAppGroup.select();
                    var allItems = gridAppGroup.items();
                    var selectedIndex = allItems.index(selectedRow);
                    gridAppGroup.removeRow(selectedRow);
                    gridAppGroup.dataSource.insert(selectedIndex, newEntry);
                }
                resetAppGroupForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetAppGroupForm() {
        clearForm($("#appGroupForm"), $('#name'));
        initObservable();
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function initDataSource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "${createLink(controller: 'appGroup', action: 'list')}",
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
                        name: {type: "string"}
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
            <g:if test="${oId!=null}">
            , filter: {field: "id", operator: "equal", value: ${oId}}
            </g:if>
        });
    }

    function initAppGroupGrid() {
        initDataSource();
        $("#gridAppGroup").kendoGrid({
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
                    width: 200,
                    sortable: false,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                }
            ],
            filterable: {
                mode: "row"
            },
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridAppGroup = $("#gridAppGroup").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function reloadKendoGrid() {
        gridAppGroup.dataSource.filter([]);
    }

    function initObservable() {
        appGroupModel = kendo.observable(
                {
                    appGroup: {
                        id: "",
                        version: "",
                        name: ""
                    }
                }
        );
        kendo.bind($("#application_top_panel"), appGroupModel);
    }

    function deleteGroup() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var appGroupId = getSelectedIdFromGridKendo(gridAppGroup);
        $.ajax({
            url: "${createLink(controller: 'appGroup', action:  'delete')}?id=" + appGroupId,
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
        if (executeCommonPreConditionForSelectKendo(gridAppGroup, 'group') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected Group?')) {
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
        var row = gridAppGroup.select();
        row.each(function () {
            gridAppGroup.removeRow($(this));
        });
        resetAppGroupForm();
        showSuccess(data.message);
    }

    function editAppGroup() {
        if (executeCommonPreConditionForSelectKendo(gridAppGroup, 'group') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridAppGroup'));
        var appGroup = getSelectedObjectFromGridKendo(gridAppGroup);
        showAppGroup(appGroup);
    }

    function showAppGroup(appGroup) {
        appGroupModel.set('appGroup', appGroup);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function addUser() {
        if (executeCommonPreConditionForSelectKendo(gridAppGroup, 'group') == false) {
            return;
        }
        showLoadingSpinner(true);
        var groupId = getSelectedIdFromGridKendo(gridAppGroup);
        var loc = "${createLink(controller: 'appUserEntity', action: 'show')}?oId=" + groupId + "&url=appGroup/show" + "&entityTypeId=" + entityTypeId;
        router.navigate(formatLink(loc));
        return false;
    }

</script>
