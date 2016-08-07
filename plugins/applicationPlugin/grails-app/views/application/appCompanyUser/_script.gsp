<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <app:ifAllUrl urls="/appUser/updateForCompanyUser">
        <li onclick="editAppCompanyUser();"><i class="fa fa-edit"></i>Edit</li>
    </app:ifAllUrl>
    <sec:access url="/userRole/showForCompanyUser">
        <li onclick="addUserRole();"><i class="fa fa-star-half-o"></i>Role</li>
    </sec:access>
%{--<sec:access url="/appUser/deleteForCompanyUser">
    <li onclick="deleteAppCompanyUser();"><i class="fa fa-trash-o"></i>Delete</li>
</sec:access>--}%
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>
<script type="text/javascript">

    var gridAppCompanyUser, appCompanyUserModel, dataSource, validatorCompanyUser, dropDownCompany, uploading;

    $(document).ready(function () {
        $('#confirmPassword').keypress(function (event) {
            $('#retypePassError').hide();
        });
        onLoadAppUser();
        initAppCompanyUserGrid();
        initObservable();

        $("#enabled").kendoMobileSwitch({onLabel: "YES",offLabel: "NO"});
        $("#accountLocked").kendoMobileSwitch({onLabel: "YES",offLabel: "NO"});
        $("#accountExpired").kendoMobileSwitch({onLabel: "YES",offLabel: "NO"});
    });

    function onLoadAppUser() {
        validatorCompanyUser = $("#companyUserForm").kendoValidator({
            validateOnBlur: false
        }).data("kendoValidator");

        $("#signatureImage").kendoUpload({multiple: false});

        bindUserFormEvents();
        // update page title
        $(document).attr('title', "MIS - Company User");
        loadNumberedMenu(MENU_ID_APPLICATION, "#appUser/showForCompanyUser");
    }

    function initObservable() {
        appCompanyUserModel = kendo.observable(
                {
                    appUser: {
                        id: "",
                        version: "",
                        loginId: "",
                        username: "",
                        appUserCompanyId: "",
                        existingPass: "",
                        enabled: false,
                        accountLocked: false,
                        cellNumber: "",
                        ipAddress: "",
                        accountExpired: false
                    }
                }
        );
        kendo.bind($("#application_top_panel"), appCompanyUserModel);
    }

    function iniDataSource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "${createLink(controller:'appUser', action: 'listForCompanyUser')}",
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
                        cellNumber: {type: "string"},
                        password: {type: "string"},
                        ipAddress: {type: "string"},
                        appUserCompanyId: {type: "number"},
                        companyName: {type: "string"}
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

    function initAppCompanyUserGrid() {
        iniDataSource();
        $("#gridAppCompanyUser").kendoGrid({
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
                    width: 160,
                    sortable: true,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                },
                {
                    field: "loginId",
                    title: "Login ID",
                    width: 120,
                    sortable: false,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                },
                {
                    field: "enabled",
                    title: "Enabled",
                    width: 58,
                    sortable: true,
                    filterable: false,
                    template: "#=enabled?'YES':'NO' #",
                    attributes: {style: setAlignCenter()},
                    headerAttributes: {style: setAlignCenter()}
                },
                {
                    field: "accountLocked",
                    title: "Locked",
                    width: 55,
                    sortable: true,
                    filterable: false,
                    template: "#=accountLocked ? 'YES':'NO' #",
                    attributes: {style: setAlignCenter()},
                    headerAttributes: {style: setAlignCenter()}
                },
                {
                    field: "accountExpired",
                    title: "Expired",
                    width: 55,
                    sortable: true,
                    filterable: false,
                    template: "#=accountExpired?'YES':'NO' #",
                    attributes: {style: setAlignCenter()},
                    headerAttributes: {style: setAlignCenter()}
                },
                {field: "companyName", title: "Company", width: 110, sortable: false, filterable: false}
            ],
            filterable: {
                mode: "row"
            },
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridAppCompanyUser = $("#gridAppCompanyUser").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function reloadKendoGrid() {
        gridAppCompanyUser.dataSource.filter([]);
    }

    function bindUserFormEvents() {
        var actionUrl = "${createLink(controller: 'appUser',action: 'createForCompanyUser')}";
        $("#companyUserForm").attr('action', actionUrl);

        $('#companyUserForm').iframePostForm({
            post: function () {
                uploading = true;
                showLoadingSpinner(true);
                setButtonDisabled($('#create'), true);
            },
            complete: function (response) {
                var tmpResponse = $.parseJSON(response);
                if (uploading == true && !tmpResponse.classSignature) {
                    onSaveUser(tmpResponse);
                } else {
                    $('#responseText').val(response);
                    $('#runTimeExceptionErrorModal').modal('show');
                }
                showLoadingSpinner(false);
                uploading = false;
                setButtonDisabled($('#create'), false);
            },
            beforePost: function () {
                if (executePreConForSubmitUser() == false) {
                    return false;
                }
                return true;
            }
        });
    }

    function executePreConForSubmitUser() {
        /*if ($('#id').val().isEmpty()) {
         if (!confirm('After creating a new company user all cache utility will be re-pulled. ' +
         'Please, make sure that all application related activities are stopped during this period of time.')) {
         return false;
         }
         }*/
        clearErrors($("#companyUserForm"));
        var passLength = $("#password").val().length;
        var hiddenPassLength = $("#existingPass").val().length;

        if (hiddenPassLength <= 0) {
            $("#lblPassword").removeClass('label-optional');
            $("#lblPassword").addClass('label-required');
            $("#lblConfirmPassword").removeClass('label-optional');
            $("#lblConfirmPassword").addClass('label-required');
            $("#password").attr('required', true);
            $("#confirmPassword").attr('required', true);
        }
        else if (hiddenPassLength > 0 && passLength == 0) {
            $("#lblPassword").removeClass('label-required');
            $("#lblPassword").addClass('label-optional');
            $("#lblConfirmPassword").removeClass('label-required');
            $("#lblConfirmPassword").addClass('label-optional');
            $("#password").attr('required', false);
            $("#confirmPassword").attr('required', false);
        }
        else {
            $("#lblPassword").removeClass('label-optional');
            $("#lblPassword").addClass('label-required');
            $("#lblConfirmPassword").removeClass('label-optional');
            $("#lblConfirmPassword").addClass('label-required');
            $("#password").attr('required', true);
            $("#confirmPassword").attr('required', true);
        }

        if (!validateForm($("#companyUserForm"))) {
            return false;
        }
        // now check two password fields
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
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = gridAppCompanyUser.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = gridAppCompanyUser.select();
                    var allItems = gridAppCompanyUser.items();
                    var selectedIndex = allItems.index(selectedRow);
                    gridAppCompanyUser.removeRow(selectedRow);
                    gridAppCompanyUser.dataSource.insert(selectedIndex, newEntry);
                }
                resetCompanyUserForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetCompanyUserForm() {
        // reset kendo upload
        $(".k-delete").parent().click();

        clearForm($("#companyUserForm"), $('#loginId'));
        initObservable();
        $('#signatureImage').val('');
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
        $('#editPassMessage').hide();
        $('#retypePassError').hide();
        dropDownCompany.enable(true);
        var actionUrl = "${createLink(controller: 'appUser',action: 'createForCompanyUser')}";
        $("#companyUserForm").attr('action', actionUrl);

        $("#lblPassword").removeClass('label-optional');
        $("#lblPassword").addClass('label-required');
        $("#lblConfirmPassword").removeClass('label-optional');
        $("#lblConfirmPassword").addClass('label-required');
        $('#password').attr('required', true);
        $('#confirmPassword').attr('required', true);
    }

    function editAppCompanyUser() {
        if (executeCommonPreConditionForSelectKendo(gridAppCompanyUser, 'company user') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridAppCompanyUser'));
        var appCompanyUser = getSelectedObjectFromGridKendo(gridAppCompanyUser);
        showAppCompanyUserForUpdate(appCompanyUser);
    }

    function showAppCompanyUserForUpdate(appCompanyUser) {
        appCompanyUserModel.set('appUser', appCompanyUser);
        dropDownCompany.enable(false);

        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
        $('#editPassMessage').show();
        var actionUrl = "${createLink(controller: 'appUser',action: 'updateForCompanyUser')}";
        $("#companyUserForm").attr('action', actionUrl);

        $("#lblPassword").removeClass('label-required');
        $("#lblPassword").addClass('label-optional');
        $("#lblConfirmPassword").removeClass('label-required');
        $("#lblConfirmPassword").addClass('label-optional');
        $("#password").attr('required', false);
        $("#confirmPassword").attr('required', false);
    }

    function deleteAppCompanyUser() {
        if (executeCommonPreConditionForSelectKendo(gridAppCompanyUser, 'company user') == false) {
            return;
        }
        var userId = getSelectedIdFromGridKendo(gridAppCompanyUser);
        if (!confirm('Are you sure you want to delete the selected company user?')) {
            return;
        }
        showLoadingSpinner(true);	// Spinner Show on AJAX Call
        $.ajax({
            url: "${createLink(controller: 'appUser', action: 'deleteForCompanyUser')}?id=" + userId,
            success: executePostConditionForDelete,
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus);
            },
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            }, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForDelete(data) {
        if (data.isError) {
            showError(data.message);
            return false;
        }
        var row = gridAppCompanyUser.select();
        row.each(function () {
            gridAppCompanyUser.removeRow($(this));
        });
        resetCompanyUserForm();
        showSuccess(data.message);
    }

    function addUserRole() {
        if (executeCommonPreConditionForSelectKendo(gridAppCompanyUser, 'company user') == false) {
            return;
        }
        showLoadingSpinner(true);
        var userId = getSelectedIdFromGridKendo(gridAppCompanyUser);
        var loc = "${createLink(controller: 'userRole', action: 'showForCompanyUser')}?userId=" + userId + "&leftMenu=appUser/showForCompanyUser";
        router.navigate(formatLink(loc));
        return false;
    }

</script>
