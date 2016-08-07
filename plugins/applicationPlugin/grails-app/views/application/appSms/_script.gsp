<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <app:ifAllUrl urls="/appSms/updateSms">
        <li onclick="editSms();"><i class="fa fa-edit"></i>Edit</li>
    </app:ifAllUrl>
    <sec:access url="/appSms/sendSms">
        <li onclick="sendSms();"><i class="fa fa-envelope-o"></i>Send SMS</li>
    </sec:access>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script type="text/javascript">
    var smsGrid, appMailDataSource, smsModel, pluginId;

    $(document).ready(function () {
        pluginId = ${pluginId};
        onLoadSmsPage();
        initAppMailGrid();
        initObservable();

        $("#isActive").kendoMobileSwitch({onLabel: "YES",offLabel: "NO"});
    });

    function onLoadSmsPage() {
        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#smsForm"), onSubmitSms);
        // update page title
        $(document).attr('title', "Update SMS");
        loadMenu(pluginId);
    }

    function executePreCondition() {
        if (!validateForm($("#smsForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitSms() {
        if (executePreCondition() == false) {
            return false;
        }

        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            showError('Sms can be updated only. Please select from grid to update');
            return false;
        } else {
            actionUrl = "${createLink(controller: 'appSms', action: 'updateSms')}";
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        jQuery.ajax({
            type: 'post',
            data: jQuery("#smsForm").serialize(),
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
            var newEntry = result.sms;
            var selectedRow = smsGrid.select();
            var allItems = smsGrid.items();
            var selectedIndex = allItems.index(selectedRow);
            smsGrid.removeRow(selectedRow);
            smsGrid.dataSource.insert(selectedIndex, newEntry);
            resetSmsForm();
            showSuccess(result.message);
        }
    }

    function resetSmsForm() {
        clearForm($("#smsForm"), $('#recipients'));
        $('#transactionCode').text('');
        $('#description').text('');
        resetRequiredFields();
        initObservable();
    }

    function editSms() {
        if (executeCommonPreConditionForSelectKendo(smsGrid, 'sms') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridSms'));
        resetSmsForm();
        var smsObj = getSelectedObjectFromGridKendo(smsGrid);
        showSms(smsObj);
    }

    function showSms(data) {
        smsModel.set('sms', data);
        toggleRequiredRecipients(data.isManualSend);
    }

    function sendSms() {
        if (executeCommonPreConditionForSelectKendo(smsGrid, 'sms') == false) {
            return;
        }

        var isManualSend = getSelectedValueFromGridKendo(smsGrid, 'isManualSend');
        if (!isManualSend) {
            showError('The selected sms can not be send manually');
            return false;
        }

        var transactionCode = getSelectedValueFromGridKendo(smsGrid, 'transactionCode');
        var controllerStr = getSelectedValueFromGridKendo(smsGrid, 'controllerName');
        var actionStr = getSelectedValueFromGridKendo(smsGrid, 'actionName');

        if (!controllerStr || !actionStr) {
            showError('Selected sms has no controller or action');
            return false;
        }
        if (!confirm('Are you sure you want to send this sms now?')) {
            return false;
        }

        var strUrl = '/' + controllerStr + '/' + actionStr;
        showLoadingSpinner(true);
        $.ajax({
            url: strUrl,
            data: {transactionCode: transactionCode},
            success: executePostConditionForSendSms,
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
        smsGrid.dataSource.filter([]);
    }

    function executePostConditionForSendSms(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            showSuccess(data.message);
            resetSmsForm();
        }
    }

    function initDataSource() {
        appMailDataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/appSms/listSms",
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
                        body: {type: "string"},
                        description: {type: "string"},
                        transactionCode: {type: "string"},
                        recipients: {type: "string"},
                        isActive: {type: "boolean"},
                        isRequiredRecipients: {type: "boolean"},
                        isManualSend: {type: "boolean"},
                        controllerName: {type: "string"},
                        actionName: {type: "string"}
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
        $("#gridSms").kendoGrid({
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
                {field: "transactionCode", title: "Transaction Code", width: 200, sortable: false, filterable: false},
                {field: "body", title: "Body", width: 200, sortable: false, filterable: false},
                {field: "description", title: "Description", width: 200, sortable: false, filterable: false},
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
        smsGrid = $("#gridSms").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function initObservable() {
        smsModel = kendo.observable(
                {
                    sms: {
                        id: "",
                        body: "",
                        description: "",
                        transactionCode: "",
                        recipients: "",
                        isActive: false,
                        isRequiredRecipients: "",
                        isManualSend: "",
                        controllerName: "",
                        actionName: ""
                    }
                }
        );
        kendo.bind($("#application_top_panel"), smsModel);
    }

    function resetRequiredFields() {
        $('#recipients').removeAttr('disabled');
        $('#labelRecipients').removeClass('label-required');
        $('#labelRecipients').addClass('label-optional');
        $('#recipients').removeAttr('required');
        $('#recipients').removeAttr('validationMessage');
        return false;
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
    function loadMenu(pluginId) {
        var menuId = getMenuIdByPluginId(pluginId);  // load menu
        loadNumberedMenu(menuId, "#appSms/showSms?plugin=" + pluginId); // set left-menu selected
    }

</script>
