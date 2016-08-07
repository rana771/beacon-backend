<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <app:ifAllUrl urls="/appUser/update">
        <li onclick="editAppUser();"><i class="fa fa-edit"></i>Edit</li>
    </app:ifAllUrl>
    <sec:access url="/appUser/delete">
        <li onclick="deleteAppUser();"><i class="fa fa-trash-o"></i>Delete</li>
    </sec:access>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script type="text/javascript">
    var gridAppUser, appUserModel, dataSource, validatorUser, dropDownEmployee, uploading, dropDownGender;

    $(document).ready(function () {
        $('#confirmPassword').keypress(function (event) {
            $('#retypePassError').hide();
        });

        onLoadAppUser();
        initAppUserGrid();
        initObservable();

        $("#enabled").kendoMobileSwitch({onLabel: "YES", offLabel: "NO"});
        $("#accountLocked").kendoMobileSwitch({onLabel: "YES", offLabel: "NO"});
        $("#isDisablePasswordExpiration").kendoMobileSwitch({onLabel: "YES", offLabel: "NO"});
        $("#accountExpired").kendoMobileSwitch({onLabel: "YES", offLabel: "NO"});
    });

    function onLoadAppUser() {

        validatorUser = $("#userForm").kendoValidator({
            validateOnBlur: false
        }).data("kendoValidator");

        $("#signatureImage").kendoUpload({multiple: false});

        bindUserFormEvents();

        // update page title
        $('span.headingText').html('User');
        $('#icon_box').attr('class', 'pre-icon-header application_user');
        $(document).attr('title', "MIS - User");
        loadNumberedMenu(MENU_ID_APPLICATION, "#appUser/show");
    }

    function initObservable() {
        appUserModel = kendo.observable(
                {
                    appUser: {
                        id: "",
                        version: "",
                        loginId: "",
                        username: "",
                        employeeId: "",
                        enabled: false,
                        accountLocked: false,
                        cellNumber: "",
                        ipAddress: "",
                        isDisablePasswordExpiration: false,
                        accountExpired: false,
                        genderId: "",
                        email: ""
                    }
                }
        );
        kendo.bind($("#application_top_panel"), appUserModel);
    }

    function iniDataSource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "${createLink(controller:'appUser', action: 'list')}",
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
                        isDisablePasswordExpiration: {type: "boolean"},
                        cellNumber: {type: "string"},
                        ipAddress: {type: "string"},
                        companyId: {type: "number"},
                        employeeId: {type: "number"},
                        employeeName: {type: "string"},
                        genderId: {type: "number"},
                        email: {type: "string"}
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
            height: getGridHeightKendo() - 5,
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
                    field: "loginId",
                    title: "Login ID",
                    width: 120,
                    sortable: false,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                },
                {
                    field: "enabled",
                    title: "Enabled",
                    width: 60,
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
                {field: "employeeName", title: "Employee", width: 120, sortable: false, filterable: false}
            ],
            filterable: {
                mode: "row"
            },
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridAppUser = $("#gridAppUser").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function bindUserFormEvents() {
        var actionUrl = "${createLink(controller: 'appUser',action: 'create')}";
        $("#userForm").attr('action', actionUrl);

        $('#userForm').iframePostForm({
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
                    $('#runTimeExceptionErrorModal').modal('show');   // define in commonModals.js
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
        clearErrors($("#userForm"));
        trimTextFields();
        if (!validatorUser.validate()) {
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

    function trimTextFields() {
        $('#loginid').val($.trim($('#loginid').val()));
        $('#username').val($.trim($('#username').val()));
        $('#cellNumber').val($.trim($('#cellNumber').val()));
        $('#ipAddress').val($.trim($('#ipAddress').val()));
    }

    function onSaveUser(result) {
        if (result.isError) {
            showError(result.message);
            return false;
        } else {
            try {
                var newEntry = result.appUser;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = gridAppUser.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = gridAppUser.select();
                    var allItems = gridAppUser.items();
                    var selectedIndex = allItems.index(selectedRow);
                    gridAppUser.removeRow(selectedRow);
                    gridAppUser.dataSource.insert(selectedIndex, newEntry);
                }
                resetAppUserForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetAppUserForm() {
        // reset form for kendo validator
        validatorUser.hideMessages();
        // reset kendo upload
        $(".k-delete").parent().click();

        clearForm($("#userForm"), $('#loginId'));
        initObservable();
        $('#signatureImage').val('');
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
        $('#editPassMessage').hide();
        $('#retypePassError').hide();
        var actionUrl = "${createLink(controller: 'appUser',action: 'create')}";
        $("#userForm").attr('action', actionUrl);

        $("#lblPassword").removeClass('label-optional');
        $("#lblPassword").addClass('label-required');
        $("#lblConfirmPassword").removeClass('label-optional');
        $("#lblConfirmPassword").addClass('label-required');
        $('#password').attr('required', true);
        $('#confirmPassword').attr('required', true);
    }

    function editAppUser() {
        if (executeCommonPreConditionForSelectKendo(gridAppUser, 'user') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridAppUser'));
        var appUser = getSelectedObjectFromGridKendo(gridAppUser);
        showAppUserForUpdate(appUser);
    }

    function showAppUserForUpdate(appUser) {
        if (appUser.employeeId == 0) {
            appUser.employeeId = "";
        }
        if (appUser.genderId == 0) {
            appUser.genderId = "";
        }
        appUserModel.set('appUser', appUser);
        dropDownGender.value(appUser.genderId);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
        var actionUrl = "${createLink(controller: 'appUser',action: 'update')}";
        $("#userForm").attr('action', actionUrl);

        $('#editPassMessage').show();
        $("#lblPassword").removeClass('label-required');
        $("#lblPassword").addClass('label-optional');
        $("#lblConfirmPassword").removeClass('label-required');
        $("#lblConfirmPassword").addClass('label-optional');
        $("#password").attr('required', false);
        $("#confirmPassword").attr('required', false);
    }

    function deleteAppUser() {
        if (executeCommonPreConditionForSelectKendo(gridAppUser, 'user') == false) {
            return;
        }
        if (!confirm('Are you sure you want to delete the selected User?')) {
            return;
        }
        var userId = getSelectedIdFromGridKendo(gridAppUser);
        showLoadingSpinner(true);	// Spinner Show on AJAX Call
        $.ajax({
            url: "${createLink(controller: 'appUser', action: 'delete')}?id=" + userId,
            dataType: 'json',
            type: 'post',
            success: executePostConditionForDelete,
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus);
            },
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            }
        });
    }

    function reloadKendoGrid() {
        gridAppUser.dataSource.filter([]);
    }

    function executePostConditionForDelete(data) {
        if (data.isError) {
            showError(data.message);
            return false;
        }
        var row = gridAppUser.select();
        row.each(function () {
            gridAppUser.removeRow($(this));
        });
        resetAppUserForm();
        showSuccess(data.message);
    }
</script>