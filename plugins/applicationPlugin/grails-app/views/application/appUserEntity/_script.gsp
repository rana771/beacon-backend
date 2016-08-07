<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <li onclick="editAppUserEntity();"><i class="fa fa-edit"></i>Edit</li>
    <li onclick="deleteAppUserEntity();"><i class="fa fa-trash-o"></i>Delete</li>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>
<script type="text/javascript">
    var gridAppUserEntity, dataSource, appUserEntityModel, dropDownUser, entityTypeId, title, entityId;

    $(document).ready(function () {
        onLoadUserEntityPage();
        initAppUserEntityGrid();
        initObservable();
    });

    function onLoadUserEntityPage() {
        checkOnLoadError();
        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#appUserEntityForm"), onSubmitUserEntity);
        entityId = $('#entityId').val();
        entityTypeId = $('#entityTypeId').val();
        var appUserEntityMap = '${appUserEntityMap}';
        title = '${appUserEntityMap.entityTypeName}';
        var entityTypeName = '${appUserEntityMap?appUserEntityMap.entityTypeName:"None"}';
        var entityName = '${appUserEntityMap?appUserEntityMap.entityName:"None"}';
        $("#lblFormTitle").text('Create User ' + entityTypeName + ' Mapping');
        $("#lblEntityTypeName").text(entityTypeName + ':');
        $("#lblEntityName").text(entityName);
        var pluginId = ${appUserEntityMap?appUserEntityMap.pluginId:1L};
        var leftMenu = '${appUserEntityMap?appUserEntityMap.leftMenu:""}';
        // update page title
        $(document).attr('title', "MIS - User " + title + " Mapping");
        loadMenu(pluginId, leftMenu);
    }

    function checkOnLoadError() {
        var isError = '${isError}';
        var msg = '${message}';
        if (isError == 'true') {
            showError(msg);
            return false;
        }
    }

    function initObservable() {
        appUserEntityModel = kendo.observable(
                {
                    appUserEntity: {
                        id: "",
                        appUserId: "",
                        userName: "",
                        entityTypeId: entityTypeId,
                        entityId: entityId
                    }
                }
        );
        kendo.bind($("#application_top_panel"), appUserEntityModel);
    }

    function iniDataSource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/appUserEntity/list",
                    data: {entityTypeId: entityTypeId, entityId: entityId},
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
                        appUserId: {type: "number"},
                        userName: {type: "string"}
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

    function initAppUserEntityGrid() {
        iniDataSource();
        $("#gridAppUserEntity").kendoGrid({
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
                    field: "userName",
                    title: "User Name",
                    sortable: true,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                }

            ],
            filterable: {mode: "row"},
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridAppUserEntity = $("#gridAppUserEntity").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function reloadKendoGrid() {
        gridAppUserEntity.dataSource.filter([]);
    }

    function onSubmitUserEntity() {
        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'appUserEntity', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'appUserEntity', action: 'update')}";
        }
        jQuery.ajax({
            type: 'post',
            data: jQuery("#appUserEntityForm").serialize(),
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
        if (!validateForm($("#appUserEntityForm"))) {
            return false;
        }
        return true;
    }

    function executePostCondition(result) {
        if (result.isError) {
            showError(result.message);
            showLoadingSpinner(false);
        } else {
            try {
                var newEntry = result.appUserEntity;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = gridAppUserEntity.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = gridAppUserEntity.select();
                    var allItems = gridAppUserEntity.items();
                    var selectedIndex = allItems.index(selectedRow);
                    gridAppUserEntity.removeRow(selectedRow);
                    gridAppUserEntity.dataSource.insert(selectedIndex, newEntry);
                }
                resetForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        clearForm($("#appUserEntityForm"), dropDownUser);
        $('#entityId').val(entityId); // re-assign hidden field value
        $('#entityTypeId').val(entityTypeId); // re-assign hidden field value
        $('#appUserId').attr('default_value', '');
        $('#appUserId').reloadMe();
        initObservable();
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function editAppUserEntity() {
        if (executeCommonPreConditionForSelectKendo(gridAppUserEntity, 'user-project mapping') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridAppUserEntity'));
        dropDownUser.value('');
        var appUserEntityObj = getSelectedObjectFromGridKendo(gridAppUserEntity);
        showUserEntity(appUserEntityObj);
    }

    // show property of user object on UI
    function showUserEntity(appUserEntityObj) {
        appUserEntityModel.set('appUserEntity', appUserEntityObj);
        $('#appUserId').attr('default_value', appUserEntityObj.appUserId);
        $('#appUserId').reloadMe();
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");   // change create button text to update
    }

    function deleteAppUserEntity() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true); // Spinner Show on AJAX Call
        var userEntityMappingId = getSelectedIdFromGridKendo(gridAppUserEntity);
        $.ajax({
            url: "${createLink(controller:'appUserEntity', action: 'delete')}?id=" + userEntityMappingId,
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
        if (executeCommonPreConditionForSelectKendo(gridAppUserEntity, 'user-entity mapping') == false) {
            return false;
        }
        if (!confirm('Are you sure to delete the selected user-entity mapping?')) {
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
        var row = gridAppUserEntity.select();
        row.each(function () {
            gridAppUserEntity.removeRow($(this));
        });
        resetForm();
        showSuccess(data.message);
    }

    function loadMenu(pluginId, leftMenu) {
        var menuId;
        switch (pluginId) {
            case 1:
                menuId = MENU_ID_APPLICATION;
                break;
            case 4:
                menuId = MENU_ID_INVENTORY;
                break;
            case 9:
                menuId = MENU_ID_EXCHANGE_HOUSE;
                break;
            case 10:
                menuId = MENU_ID_PROJECT_TRACK;
                break;
            case 11:
                menuId = MENU_ID_ARMS;
                break;
            default:
                menuId = MENU_ID_APPLICATION;
        }
        loadNumberedMenu(menuId, leftMenu);
    }

</script>
