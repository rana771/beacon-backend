<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <app:ifAllUrl urls="/appUser/updateAllUser">
        <li onclick="editAppUserReseller();"><i class="fa fa-edit"></i>Edit</li>
    </app:ifAllUrl>
    <sec:access url="/userRole/showForCompanyUser">
        <li onclick="addUserRole();"><i class="fa fa-star-half-o"></i>Role</li>
    </sec:access>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script type="text/javascript">
    var gridAppUser, appUserModel, dataSource;

    $(document).ready(function () {
        onLoadAppUser();
        initAppUserGrid();
        initObservable();

        $("#enabled").kendoMobileSwitch({onLabel: "YES",offLabel: "NO"});
        $("#accountLocked").kendoMobileSwitch({onLabel: "YES",offLabel: "NO"});
        $("#isPowerUser").kendoMobileSwitch({onLabel: "YES",offLabel: "NO"});
        $("#isConfigManager").kendoMobileSwitch({onLabel: "YES",offLabel: "NO"});
        $("#isDisablePasswordExpiration").kendoMobileSwitch({onLabel: "YES",offLabel: "NO"});
        $("#accountExpired").kendoMobileSwitch({onLabel: "YES",offLabel: "NO"});
        $("#isSwitchable").kendoMobileSwitch({onLabel: "YES", offLabel: "NO"});
        $("#isReserved").kendoMobileSwitch({onLabel: "YES", offLabel: "NO"});
    });

    function onLoadAppUser() {
        initializeForm($("#userForm"), onSubmitUser);

        // update page title
        $('span.headingText').html('User');
        $('#icon_box').attr('class', 'pre-icon-header application_user');
        $(document).attr('title', "MIS - User");
        loadNumberedMenu(MENU_ID_APPLICATION, "#appUser/showAllUser");
    }

    function initObservable() {
        appUserModel = kendo.observable(
                {
                    appUser: {
                        id: "",
                        version: "",
                        loginId: "",
                        username: "",
                        enabled: false,
                        accountLocked: false,
                        isPowerUser: false,
                        cellNumber: "",
                        ipAddress: "",
                        isConfigManager: false,
                        isDisablePasswordExpiration: false,
                        accountExpired: false,
                        isSwitchable: false,
                        isReserved: false
                    }
                }
        );
        kendo.bind($("#application_top_panel"), appUserModel);
    }

    function iniDataSource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/appUser/listAllUser",
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
                        loginId: {type: "string"},
                        username: {type: "string"},
                        enabled: {type: "boolean"},
                        accountExpired: {type: "boolean"},
                        accountLocked: {type: "boolean"},
                        isPowerUser: {type: "boolean"},
                        isConfigManager: {type: "boolean"},
                        isDisablePasswordExpiration: {type: "boolean"},
                        cellNumber: {type: "string"},
                        ipAddress: {type: "string"},
                        companyId: {type: "number"},
                        companyName: {type: "string"},
                        isSwitchable: {type: "boolean"},
                        isReserved: {type: "boolean"}
                    }
                },
                parse: function (data) {
                    checkIsErrorGridKendo(data);
                    return data;
                }
            },
            sort: {field: 'username', dir: 'asc'},  // default sort
            pageSize: ${com.athena.mis.BaseService.DEFAULT_RESULT_PER_PAGE},
            serverPaging: true,
            serverFiltering: true,
            serverSorting: true
        });
    }

    function initAppUserGrid() {
        iniDataSource();
        $("#gridAppUser").kendoGrid({
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
                    field: "username",
                    title: "User Name",
                    width: 150,
                    sortable: true,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                },
                {
                    field: "loginId", title: "Login ID", width: 120, sortable: true,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                },
                {
                    field: "enabled",
                    title: "Enabled",
                    width: 50,
                    sortable: true,
                    filterable: false,
                    template: "#=enabled?'YES':'NO' #",
                    attributes: {style: setAlignCenter()},
                    headerAttributes: {style: setAlignCenter()}
                },
                {
                    field: "accountLocked",
                    title: "Locked",
                    width: 40,
                    sortable: true,
                    filterable: false,
                    template: "#=accountLocked ? 'YES':'NO' #",
                    attributes: {style: setAlignCenter()},
                    headerAttributes: {style: setAlignCenter()}
                },
                {
                    field: "accountExpired",
                    title: "Expired",
                    width: 40,
                    sortable: true,
                    filterable: false,
                    template: "#=accountExpired?'YES':'NO' #",
                    attributes: {style: setAlignCenter()},
                    headerAttributes: {style: setAlignCenter()}
                },
                {
                    field: "companyName",
                    title: "Company",
                    width: 150,
                    sortable: false,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}},
                    attributes: {style: "white-space:nowrap"}
                }
            ],
            filterable: {
                mode: "row"
            },
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridAppUser = $("#gridAppUser").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }


    function reloadKendoGrid() {
        gridAppUser.dataSource.filter([]);
    }

    function onSubmitUser() {
        if (executePreConForSubmitUser() == false) {
            return false;
        }
        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = "${createLink(controller:'appUser', action: 'updateAllUser')}";

        jQuery.ajax({
            type: 'post',
            data: jQuery("#userForm").serialize(),
            url: actionUrl,
            success: function (data, textStatus) {
                onSaveUser(data);
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

    function executePreConForSubmitUser() {
        if (!validateForm($("#userForm"))) {
            return false;
        }
        if ($('#id').val().isEmpty()) {
            showError('User is updated only. Please select from grid to update.');
            return false;
        }
        if (checkPasswordMatch() == false) return false;
        return true;
    }

    function checkPasswordMatch() {
        if ($("#password").val() != $("#confirmPassword").val()) {
            showError("Password mismatched");
            $("#confirmPassword").focus();
            return false;
        } else {
            return true;
        }
    }

    function onSaveUser(result) {
        if (result.isError) {
            showError(result.message);
            return false;
        } else {
            try {
                var newEntry = result.appUser;
                var selectedRow = gridAppUser.select();
                var allItems = gridAppUser.items();
                var selectedIndex = allItems.index(selectedRow);
                gridAppUser.removeRow(selectedRow);
                gridAppUser.dataSource.insert(selectedIndex, newEntry);
                resetAppUserForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetAppUserForm() {
        clearForm($("#userForm"), $('#loginId'));
        initObservable();
    }

    function editAppUserReseller() {
        if (executeCommonPreConditionForSelectKendo(gridAppUser, 'user') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridAppUser'));
        var appUser = getSelectedObjectFromGridKendo(gridAppUser);
        showAppUserForUpdate(appUser);
    }

    function showAppUserForUpdate(appUser) {
        appUserModel.set('appUser', appUser);
    }

    function addUserRole() {
        if (executeCommonPreConditionForSelectKendo(gridAppUser, 'user') == false) {
            return;
        }
        showLoadingSpinner(true);
        var userId = getSelectedIdFromGridKendo(gridAppUser);
        var loc = "${createLink(controller: 'userRole', action: 'showForCompanyUser')}?userId=" + userId + "&leftMenu=appUser/showAllUser";
        router.navigate(formatLink(loc));
        return false;
    }

</script>