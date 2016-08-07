<script type="text/javascript">
    var dropDownRole, dropDownModule, gridAvailableRole, gridAssignedRole, dataSourceAvailable, dataSourceAssigned;

    $(document).ready(function () {
        onLoadRequestMapPage();
    });

    function onLoadRequestMapPage() {
        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#frmSearchRole"), onSubmitFrmSearchRole);
        initAvailableRoleGrid();
        initAssignedRoleGrid();

        // update page title
        $(document).attr('title', "MIS - Role-Right Mapping");
        loadNumberedMenu(MENU_ID_APPLICATION, "#requestMap/show");
    }

    function onSubmitFrmSearchRole() {
        if (!validateForm($("#frmSearchRole"))) {
            return false;
        }
        var roleId = dropDownRole.value();
        var pluginId = dropDownModule.value();

        $('#hidRole').val(roleId);
        $('#hidPluginId').val(pluginId);

        var urlAvailable = '/requestMap/listAvailableRole?roleId=' + roleId + '&pluginId=' + pluginId;
        var urlAssigned = '/requestMap/listAssignedRole?roleId=' + roleId + '&pluginId=' + pluginId;
        populateGridKendo(gridAvailableRole, urlAvailable);
        populateGridKendo(gridAssignedRole, urlAssigned);
    }

    function saveRequestMapAttributes() {
        var assignedFeatureIds = '';
        var assignedData = gridAssignedRole.dataSource.data();
        $.each(assignedData, function (index, data) {
            assignedFeatureIds += data.id + '_';
        });

        var roleId = $('#hidRole').val();
        var pluginId = $('#hidPluginId').val();
        var url = "${createLink(controller: 'requestMap',action: 'update')}?assignedFeatureIds=" + assignedFeatureIds + '&roleId=' + roleId + '&pluginId=' + pluginId;

        showLoadingSpinner(true);
        jQuery.ajax({
            type: 'post',
            url: url,
            success: function (data, textStatus) {
                executePostConditionForUpdate(data);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus)
            },
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json'
        });
    }

    function executePostConditionForUpdate(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            populateLeftMenu(data.leftMenuData);
            loadNumberedMenu(MENU_ID_APPLICATION, "#requestMap/show");
            showSuccess(data.message);
        }
    }

    function addDataToSelectedRole() {
        if (executeCommonPreConditionForSelectKendo(gridAvailableRole, 'Available Role', false) == false) {
            return false;
        }
        var rows = gridAvailableRole.select();
        rows.each(function (e) {
            var data = gridAvailableRole.dataItem($(this));
            gridAssignedRole.dataSource.insert(0, data);
        });
        rows.each(function (e) {
            gridAvailableRole.removeRow($(this));
        });
    }

    function addAllDataToSelectedRole() {
        var count = gridAvailableRole.dataSource.data().length;
        if (!confirm('Do you want to assign ' + count + ' right(s) to the selected role?')) {
            return;
        }
        var availableRows = gridAvailableRole.dataSource.data();
        var newData = $.merge(availableRows, gridAssignedRole.dataSource.data());
        gridAssignedRole.dataSource.data(newData);
        gridAvailableRole.dataSource.data([]);
        gridAvailableRole.refresh();
    }

    function removeDataFromSelectedRole() {
        if (executeCommonPreConditionForSelectKendo(gridAssignedRole, 'Assigned Role', false) == false) {
            return false;
        }
        var rows = gridAssignedRole.select();
        rows.each(function (e) {
            var data = gridAssignedRole.dataItem($(this));
            gridAvailableRole.dataSource.insert(0, data);
        });
        rows.each(function (e) {
            gridAssignedRole.removeRow($(this));
        });
    }

    function removeAllDataFromSelectedRole() {
        var count = gridAssignedRole.dataSource.data().length;
        if (!confirm('Do you want to remove ' + count + ' right(s) from the selected role?')) {
            return;
        }
        var assignedRows = gridAssignedRole.dataSource.data();
        var newData = $.merge(assignedRows, gridAvailableRole.dataSource.data());
        gridAvailableRole.dataSource.data(newData);
        gridAssignedRole.dataSource.data([]);
        gridAssignedRole.refresh();
    }

    function discardChanges() {
        var hidRoleId = $('#hidRole').val();
        var hidPluginName = $('#hidPluginId').val();
        if ((hidRoleId == '') || (hidPluginName == '')) {
            showError('No changes found to discard');
            return false;
        }
        dropDownRole.value(hidRoleId);
        $('#pluginId').val(hidPluginName);
        onSubmitFrmSearchRole();
        return false;
    }

    function authenticateRequestMapReset() {
        if (!validateForm($("#frmSearchRole"))) {
            return false;
        }
        $('#appResetAllRolesConfirmationModal').modal('show');    // show Modal
        return false;
    }

    function checkPassword(tempPass) {
        if (tempPass.length == 0) return false;
        showLoadingSpinner(true);	// Spinner Show on AJAX Call
        var actionUrl = "${createLink(controller: 'appUser', action: 'checkPassword')}?pass=" + tempPass;
        jQuery.ajax({
            type: 'post',
            url: actionUrl,
            success: function (data, textStatus) {
                executePostConditionToCheckPassword(data);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus)
            },
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json'
        });
    }

    function executePostConditionToCheckPassword(data) {
        if (data.isError) {
            showError(data.message);
            $('#txtAppPassword').val('');
            return false;
        }
        resetDefaultRequestMap();
    }

    function resetDefaultRequestMap() {
        var roleId = $('#roleId').val();
        var pluginId = $('#pluginId').val();
        showLoadingSpinner(true);	// Spinner Show on AJAX Call
        $.ajax({
            url: "${createLink(controller:'requestMap', action:'resetRequestMap')}?pluginId=" + pluginId + '&roleId=' + roleId,
            success: executePostConditionForResetReqMap,
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus)
            },
            complete: onCompleteAjaxCall,
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForResetReqMap(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            //showSuccess(data.message);
            alert('Application Role-Right Mapping Successfully Reset to Default. \nPlease Login Again.');
            var logoutLink = "${createLink(controller:'logout', action:'index')}";
            document.location = logoutLink;
        }
    }

    function onSubmitResetReqMapConfirmation() {
        var password = $('#txtAppPassword').val();
        if (password == '') {
            showError("Please write down legal password");
            return false;
        } else {
            var regPass = /^.*(?=.{8,})(((?=.*[a-z])(?=.*[A-Z])(?=.*[\d]))|((?=.*[a-z])(?=.*[A-Z])(?=.*[\W]))|((?=.*[a-z])(?=.*[\d])(?=.*[\W]))|((?=.*[A-Z])(?=.*[\d])(?=.*[\W]))).*$/;
            if (!regPass.test(password)) {
                showError('Invalid Authentication');
            } else {
                checkPassword(password);
                $('#appResetAllRolesConfirmationModal').modal('hide');
            }
        }
        return false;
    }

    function exitResetReqMapConfirmForm() {
        $('#txtAppPassword').val('');
        $('#appResetAllRolesConfirmationModal').modal('hide');
        return false;
    }

    function onChangeDropdownRole() {
        if (dropDownRole.value() == '') {
            $("#pluginId").attr('role_id', '0');
        } else {
            $("#pluginId").attr('role_id', dropDownRole.value());
        }
        $("#pluginId").reloadMe();
    }

    function initDataSourceAvailable() {
        dataSourceAvailable = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/requestMap/listAvailableRole",
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
                        feature_name: {type: "string"},
                        transaction_code: {type: "string"},
                        url: {type: "string"}
                    }
                },
                parse: function (data) {
                    checkIsErrorGridKendo(data);
                    return data;
                }
            },
            pageSize: ${com.athena.mis.BaseService.DEFAULT_RESULT_PER_PAGE},
            serverPaging: false,
            serverFiltering: false,
            serverSorting: false
        });
    }

    function initAvailableRoleGrid() {
        initDataSourceAvailable();
        $("#gridAvailableRole").kendoGrid({
            autoBind: false,
            dataSource: dataSourceAvailable,
            height: getGridHeightKendo() - 115,
            selectable: 'multiple',
            sortable: true,
            resizable: true,
            reorderable: true,
            pageable: {
                numeric: false
            },
            columns: [
                {
                    field: "feature_name",
                    title: "Feature Name",
                    sortable: true,
                    width: 80,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                },
                {
                    field: "transaction_code",
                    title: "Code",
                    sortable: true,
                    width: 50,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                }
            ],
            filterable: {mode: "row"}
        });
        gridAvailableRole = $("#gridAvailableRole").data("kendoGrid");
    }

    function initDataSourceAssigned() {
        dataSourceAssigned = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/requestMap/listAssignedRole",
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
                        feature_name: {type: "string"},
                        transaction_code: {type: "string"},
                        url: {type: "string"}
                    }
                },
                parse: function (data) {
                    checkIsErrorGridKendo(data);
                    return data;
                }
            },
            pageSize: ${com.athena.mis.BaseService.DEFAULT_RESULT_PER_PAGE},
            serverPaging: false,
            serverFiltering: false,
            serverSorting: false
        });
    }

    function initAssignedRoleGrid() {
        initDataSourceAssigned();
        $("#gridAssignedRole").kendoGrid({
            autoBind: false,
            dataSource: dataSourceAssigned,
            height: getGridHeightKendo() - 115,
            selectable: 'multiple',
            sortable: true,
            resizable: true,
            reorderable: true,
            pageable: {
                numeric: false
            },
            columns: [
                {
                    field: "feature_name",
                    title: "Feature Name",
                    sortable: true,
                    width: 80,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                },
                {
                    field: "transaction_code",
                    title: "Code",
                    sortable: true,
                    width: 50,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                }
            ],
            filterable: {mode: "row"}
        });
        gridAssignedRole = $("#gridAssignedRole").data("kendoGrid");
    }

</script>
