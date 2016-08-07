<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <app:ifAllUrl urls="/appMail/update">
        <li onclick="editAppMail();"><i class="fa fa-edit"></i>Edit</li>
    </app:ifAllUrl>
    <sec:access url="/appMail/testAppMail">
        <li onclick="sendAppMail();"><i class="fa fa-envelope-o"></i>Send Mail</li>
    </sec:access>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script language="javascript" type="text/javascript">

    var appMailGrid, appMailDataSource, appMailModel, pluginId;

    $(document).ready(function () {
        pluginId = ${pluginId};
        onLoadAppMailPage();
        initAppMailGrid();
        initObservable();

        $("#isActive").kendoMobileSwitch({onLabel: "YES",offLabel: "NO"});
    });

    function onLoadAppMailPage() {
        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#appMailForm"), onSubmitAppMailForm);
        // update page title
        $(document).attr('title', "Update Mail");
        loadMenu(pluginId);
    }

    function executePreCondition() {
        if (!validateForm($("#appMailForm"))) {
            return false;
        }
        if ($('#id').val().isEmpty()) {
            showError('Please select mail from grid to update ');
            return false;
        }
        return true;
    }

    function onSubmitAppMailForm() {
        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('#update'), true);
        showLoadingSpinner(true);
        var actionUrl = "${createLink(controller: 'appMail', action: 'update')}";

        jQuery.ajax({
            type: 'post',
            data: jQuery("#appMailForm").serialize(),
            url: actionUrl,
            success: function (data, textStatus) {
                executePostCondition(data);
                setButtonDisabled($('#update'), false);
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
            var newEntry = result.appMail;
            var selectedRow = appMailGrid.select();
            var allItems = appMailGrid.items();
            var selectedIndex = allItems.index(selectedRow);
            appMailGrid.removeRow(selectedRow);
            appMailGrid.dataSource.insert(selectedIndex, newEntry);
            resetForm();
            showSuccess(result.message);
        }
    }

    function editAppMail() {
        if (executeCommonPreConditionForSelectKendo(appMailGrid, 'mail') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridAppMail'));
        var appMailObj = getSelectedObjectFromGridKendo(appMailGrid);
        showAppMail(appMailObj);
    }

    function showAppMail(appMailObj) {
        appMailModel.set('appMail', appMailObj);
        toggleRequiredRoleIds(appMailObj.isRequiredRoleIds);
        toggleRequiredRecipients(appMailObj.isRequiredRecipients);
    }

    function sendAppMail() {
        if (executeCommonPreConditionForSelectKendo(appMailGrid, 'mail') == false) {
            return;
        }
        var isManualSend = getSelectedValueFromGridKendo(appMailGrid, 'isManualSend');
        if (!isManualSend) {
            showError('The selected mail can not be send manually');
            return false;
        }

        var transactionCode = getSelectedValueFromGridKendo(appMailGrid, 'transactionCode');
        var controllerStr = getSelectedValueFromGridKendo(appMailGrid, 'controllerName');
        var actionStr = getSelectedValueFromGridKendo(appMailGrid, 'actionName');

        if (!controllerStr || !actionStr) {
            showError('Mail action not found');
            return false;
        }
        if (!confirm('Are you sure you want to send this mail now?')) {
            return false;
        }
        var strUrl = '/' + controllerStr + '/' + actionStr;
        showLoadingSpinner(true);

        $.ajax({
            url: strUrl,
            data: {transactionCode: transactionCode},
            success: executePostConditionForSendMail,
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus);
            },
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function reloadKendoGrid() {
        appMailGrid.dataSource.filter([]);
    }

    function executePostConditionForSendMail(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            showSuccess(data.message);
            resetForm();
        }
    }

    function initDataSource() {
        appMailDataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/appMail/list",
                    dataType: "json",
                    data: {pluginId: pluginId},
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
                        subject: {type: "string"},
                        body: {type: "string"},
                        transactionCode: {type: "string"},
                        roleIds: {type: "string"},
                        isActive: {type: "boolean"},
                        isManualSend: {type: "boolean"},
                        controllerName: {type: "string"},
                        actionName: {type: "string"},
                        recipients: {type: "string"},
                        recipientsCc: {type: "string"},
                        displayName: {type: "string"},
                        isRequiredRoleIds: {type: "boolean"},
                        isRequiredRecipients: {type: "boolean"}
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
        });
    }

    function initAppMailGrid() {
        initDataSource();
        $("#gridAppMail").kendoGrid({
            dataSource: appMailDataSource,
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
                {field: "subject", title: "Subject", width: 250, sortable: false, filterable: false},
                {field: "transactionCode", title: "Transaction Code", width: 200, sortable: false, filterable: false},
                {field: "roleIds", title: "Role ID", width: 100, sortable: false, filterable: false},
                {
                    field: "isActive", title: "Active", width: 50, sortable: false, filterable: false,
                    template: "#= isActive?'YES':'NO'#"
                },
                {
                    field: "isManualSend", title: "Manual Send", width: 80, sortable: false, filterable: false,
                    template: "#= isManualSend?'YES':'NO'#"
                }
            ],
            filterable: {
                mode: "row"
            },
            toolbar: kendo.template($("#gridToolbar").html())
        });
        appMailGrid = $("#gridAppMail").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function initObservable() {
        appMailModel = kendo.observable(
                {
                    appMail: {
                        id: "",
                        version: "",
                        subject: "",
                        body: "",
                        transactionCode: "",
                        roleIds: "",
                        isActive: false,
                        isManualSend: "",
                        controllerName: "",
                        actionName: "",
                        recipients: "",
                        recipientsCc: "",
                        displayName:""
                    }
                }
        );
        kendo.bind($("#application_top_panel"), appMailModel);
    }

    function resetForm() {
        clearForm($("#appMailForm"), $("#roleIds"));
        initObservable();
        $("#update").html("<span class='k-icon k-i-plus'></span>Update");
        $("#transactionCode").html('');
        resetRequiredFields();
    }

    function resetRequiredFields() {
        $('#roleIds').removeAttr('disabled');
        $('#labelRoleIds').removeClass('label-required');
        $('#labelRoleIds').addClass('label-optional');
        $('#roleIds').removeAttr('required');
        $('#roleIds').removeAttr('validationMessage');

        $('#recipients').removeAttr('disabled');
        $('#labelRecipients').removeClass('label-required');
        $('#labelRecipients').addClass('label-optional');
        $('#recipients').removeAttr('required');
        $('#recipients').removeAttr('validationMessage');
        return false;
    }

    function toggleRequiredRoleIds(entityData) {
        if (entityData) {
            $('#roleIds').removeAttr('disabled');
            $('#labelRoleIds').removeClass('label-optional');
            $('#labelRoleIds').addClass('label-required');
            $('#roleIds').attr('required', 'required');
            $('#roleIds').attr('validationMessage', 'Required');
        } else {
            $('#roleIds').attr('disabled', 'disable');
            $('#labelRoleIds').removeClass('label-required');
            $('#labelRoleIds').addClass('label-optional');
            $('#roleIds').removeAttr('required');
            $('#roleIds').removeAttr('validationMessage');
        }
    }

    function toggleRequiredRecipients(entityData) {
        if (entityData) {
            $('#recipients').removeAttr('disabled');
            $('#labelRecipients').removeClass('label-optional');
            $('#labelRecipients').addClass('label-required');
            $('#recipients').attr('required', 'required');
            $('#recipients').attr('validationMessage', 'Required');
            $('#recipients').focus();
        } else {
            $('#recipients').attr('disabled', 'disable');
            $('#labelRecipients').removeClass('label-required');
            $('#labelRecipients').addClass('label-optional');
            $('#recipients').removeAttr('required');
            $('#recipients').removeAttr('validationMessage');
        }
    }

    <%-- Load Menu and set left-menu selected  --%>
    function loadMenu() {
        var menuId = getMenuIdByPluginId(pluginId);  // load menu
        loadNumberedMenu(menuId, "#appMail/show?plugin=" + pluginId); // set left-menu selected
    }

</script>