<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <app:checkSystemUser isConfigManager="true">
        <app:ifAllUrl urls="/systemEntity/update">
            <li onclick="editSystemEntity();"><i class="fa fa-edit"></i>Edit</li>
        </app:ifAllUrl>
        <sec:access url="/systemEntity/delete">
            <li onclick="deleteSystemEntity();"><i class="fa fa-trash-o"></i>Delete</li>
        </sec:access>
    </app:checkSystemUser>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script type="text/javascript">
    var systemEntityTypeId;
    var gridSystemEntity;
    var systemEntityDataSource, systemEntityModel,pluginId;

    $(document).ready(function () {
        onLoadSystemEntityPage();
        initSystemEntityGrid();
        initObservable();

        $("#isActive").kendoMobileSwitch({onLabel: "YES",offLabel: "NO"});
    });

    function onLoadSystemEntityPage() {
        checkOnLoadError();
        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#systemEntityForm"), onSubmitSystemEntity);

         pluginId = ${pluginId};
        systemEntityTypeId = ${systemEntityTypeId};
        $('#systemEntityTypeId').val(systemEntityTypeId);

        // update page title
        $(document).attr('title', "MIS - Create System Entity");
        loadMenu(pluginId);
    }

    function checkOnLoadError() {
        var isError = '${isError}';
        var msg = '${message}';
        if (isError == 'true') {
            showError(msg);
            return false;
        }
    }

    function executePreCondition() {
        if (!validateForm($("#systemEntityForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitSystemEntity() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'systemEntity', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'systemEntity', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#systemEntityForm").serialize(),
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
                var newEntry = result.systemEntity;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = gridSystemEntity.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = gridSystemEntity.select();
                    var allItems = gridSystemEntity.items();
                    var selectedIndex = allItems.index(selectedRow);
                    gridSystemEntity.removeRow(selectedRow);
                    gridSystemEntity.dataSource.insert(selectedIndex, newEntry);
                }

                resetSystemEntityForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetSystemEntityForm() {
        clearForm($("#systemEntityForm"), $('#key'));
        initObservable();
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
        $('#systemEntityTypeId').val(systemEntityTypeId); // re-assign hidden field value
    }

    <%-- removing selected row and clean input form --%>
    function executePostConditionForDelete(data) {
        if (data.isError){
            showError(data.message);
            return false;
        }
        var row = gridSystemEntity.select();
        row.each(function () {
            gridSystemEntity.removeRow($(this));
        });
        resetSystemEntityForm();
        showSuccess(data.message);
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelectKendo(gridSystemEntity, 'system entity') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected system entity?')) {
            return false;
        }
        return true;
    }

    function deleteSystemEntity() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var systemEntityId = getSelectedIdFromGridKendo(gridSystemEntity);

        $.ajax({
            url: "${createLink(controller:'systemEntity', action: 'delete')}?id=" + systemEntityId,
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
        gridSystemEntity.dataSource.filter([]);
    }

    function editSystemEntity() {
        if (executeCommonPreConditionForSelectKendo(gridSystemEntity, 'system entity ') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridSystemEntity'));
        var systemEntityObj = getSelectedObjectFromGridKendo(gridSystemEntity);
        showSystemEntity(systemEntityObj);
    }

    function showSystemEntity(systemEntityObj) {
        systemEntityModel.set('systemEntity', systemEntityObj);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    <%-- End: Edit operation --%>

    function initDataSource() {
        systemEntityDataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/systemEntity/list",
                    dataType: "json",
                    data:{ systemEntityTypeId: systemEntityTypeId },
                    type: "post"
                }
            },
            schema: {
                type: 'json',
                data: "list", total: "count",
                model: {
                    fields: {
                        id: { type: "number" },
                        version: { type: "number" },
                        key: { type: "string" },
                        value: { type: "string" },
                        isActive: { type: "boolean" },
                        reservedId: { type: "number"}
                    }
                },
                parse: function (data) {
                    checkIsErrorGridKendo(data);
                    return data;
                }
            },
            sort:{field:'key',dir:'asc'},  // default sort
            pageSize: ${com.athena.mis.BaseService.DEFAULT_RESULT_PER_PAGE},
            serverPaging: true,
            serverFiltering: true,
            serverSorting: true
        });
    }

    function initSystemEntityGrid() {
        initDataSource();
        $("#gridSystemEntity").kendoGrid({
            dataSource: systemEntityDataSource,
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
                {field: "key", title: "Key", width: 180, sortable: false, filterable: {cell: {operator: "contains",dataSource:getBlankDataSource()}}},
                {field: "value", title: "Value", width: 120, sortable: false, filterable: false},
                {field: "isActive", title: "Active", width: 60, sortable: false, filterable: false, template: "#= isActive?'YES':'NO'#"},
                {field: "reservedId", title: "Reserved", width: 100, sortable: false, filterable: false, template: "#= reservedId > 0 ?'YES':'NO'#"}
            ],
            filterable: {
                mode: "row"
            },

            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridSystemEntity = $("#gridSystemEntity").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function initObservable() {
        systemEntityModel = kendo.observable(
                {
                    systemEntity: {
                        id: "",
                        version: "",
                        key: "",
                        value: "",
                        isActive: false
                    }
                }
        );
        kendo.bind($("#application_top_panel"), systemEntityModel);
    }

    <%-- Load Menu and set left-menu selected  --%>
    function loadMenu(pluginId) {
        var menuId = getMenuIdByPluginId(pluginId);
        loadNumberedMenu(menuId, "#systemEntityType/show?plugin=" + pluginId);
    }
</script>
