<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <app:ifAllUrl urls="/appSchedule/update">
        <li onclick="editAppSchedule();"><i class="fa fa-edit"></i>Edit</li>
    </app:ifAllUrl>
    <sec:access url="/appSchedule/testExecute">
        <li onclick="testExecute();"><i class="fa fa-play"></i>Test Execute</li>
    </sec:access>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script language="javascript" type="text/javascript">
    var appScheduleGrid, appScheduleDataSource, appScheduleModel, pluginId, dropDownScheduleType,
            scheduleTypeSimple, repeatInterval, repeatCount;

    $(document).ready(function () {
        pluginId = ${pluginId};
        onLoadAppSchedulePage();
        initAppScheduleGrid();
        initObservable();
        initCronHints();

        $("#enable").kendoMobileSwitch({onLabel: "YES",offLabel: "NO"});
    });

    function onLoadAppSchedulePage() {
        $("#repeatInterval").kendoNumericTextBox({
            decimals: 0,
            min: 10,
            step: 1000,
            format: "# seconds"
        });
        $("#repeatCount").kendoNumericTextBox({
            decimals: 0,
            min: 0,
            format: "# times"
        });

        repeatInterval = $("#repeatInterval").data("kendoNumericTextBox");
        repeatCount = $("#repeatCount").data("kendoNumericTextBox");
        scheduleTypeSimple = $('#hidScheduleTypeSimpleId').val();
        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#appScheduleForm"), onSubmitAppScheduleForm);
        // update page title
        $(document).attr('title', "Update Schedule");
        loadMenu(pluginId);
    }

    function executePreCondition() {
        if (!validateForm($("#appScheduleForm"))) {
            return false;
        }

        if ($('#id').val().isEmpty()) {
            showError('Please select Schedule from grid to update ');
            return false;
        }
        return true;
    }

    function onSubmitAppScheduleForm() {

        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('#update'), true);
        showLoadingSpinner(true);

        var actionUrl = "${createLink(controller: 'appSchedule', action: 'update')}";

        jQuery.ajax({
            type: 'post',
            data: jQuery("#appScheduleForm").serialize(),
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
            var newEntry = result.appSchedule;
            var selectedRow = appScheduleGrid.select();
            var allItems = appScheduleGrid.items();
            var selectedIndex = allItems.index(selectedRow);
            appScheduleGrid.removeRow(selectedRow);
            appScheduleGrid.dataSource.insert(selectedIndex, newEntry);
            resetForm();
            showSuccess(result.message);
        }
    }

    function editAppSchedule() {
        if (executeCommonPreConditionForSelectKendo(appScheduleGrid, 'Schedule') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridAppSchedule'));
        var appScheduleObj = getSelectedObjectFromGridKendo(appScheduleGrid);
        showAppSchedule(appScheduleObj);
    }

    function showAppSchedule(appScheduleObj) {
        appScheduleModel.set('appSchedule', appScheduleObj);
        toggleFields();
    }

    function testExecute() {
        if (executeCommonPreConditionForSelectKendo(appScheduleGrid, 'Schedule') == false) {
            return;
        }
        if (!confirm('Are you sure you want to execute selected schedule?\nNOTE: Corresponding feature will be executed for only once.')) {
            return false;
        }
        showLoadingSpinner(true);
        var appScheduleId = getSelectedIdFromGridKendo(appScheduleGrid);
        $.ajax({
            url: "${createLink(controller:'appSchedule', action: 'testExecute')}?id=" + appScheduleId,
            success: executePostConditionForExecute,
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
        appScheduleGrid.dataSource.filter([]);
    }

    function executePostConditionForExecute(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            showSuccess(data.message);
            resetForm()
        }
    }

    function executePostConditionForSendMail(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            showSuccess(data.message);
        }
    }

    function initDataSource() {
        appScheduleDataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/appSchedule/list",
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
                        name: {type: "string"},
                        actionName: {type: "string"},
                        scheduleTypeId: {type: "number"},
                        repeatInterval: {type: "number"},
                        repeatCount: {type: "number"},
                        cronExpression: {type: "string"},
                        enable: {type: "boolean"}
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

    function initAppScheduleGrid() {
        initDataSource();
        $("#gridAppSchedule").kendoGrid({
            dataSource: appScheduleDataSource,
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
                {field: "name", title: "Name", width: 200, sortable: false, filterable: false},
                {field: "actionName", title: "Action Name", width: 100, sortable: false, filterable: false},
                {field: "repeatInterval", title: "Repeat Interval", width: 70, sortable: false, filterable: false},
                {field: "repeatCount", title: "Repeat Count", width: 70, sortable: false, filterable: false},
                {field: "cronExpression", title: "Cron Expression", width: 130, sortable: false, filterable: false},
                {
                    field: "enable", title: "Enable", width: 40, sortable: false, filterable: false,
                    template: "#= enable?'YES':'NO'#"
                }
            ],
            filterable: {
                mode: "row"
            },

            toolbar: kendo.template($("#gridToolbar").html())
        });
        appScheduleGrid = $("#gridAppSchedule").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function initObservable() {
        appScheduleModel = kendo.observable(
                {
                    appSchedule: {
                        id: "",
                        version: "",
                        name: "",
                        scheduleTypeId: "",
                        repeatInterval: "",
                        repeatCount: "",
                        cronExpression: "",
                        enable: false
                    }
                }
        );
        kendo.bind($("#application_top_panel"), appScheduleModel);
    }

    function resetForm() {
        clearForm($("#appScheduleForm"), dropDownScheduleType);
        initObservable();
        defaultFeatures();
        $("#update").html("<span class='k-icon k-i-plus'></span>Update");
    }

    function toggleFields() {
        clearErrors($("#appScheduleForm"));
        if (dropDownScheduleType.value() == '') {
            defaultFeatures();
        } else if (dropDownScheduleType.value() == scheduleTypeSimple) {
            repeatIntervalEnable();
            repeatCountEnable();
            cronExpressionDisable();
        } else {
            repeatIntervalDisable();
            repeatCountDisable();
            cronExpressionEnable();
        }
    }

    function defaultFeatures() {
        repeatIntervalEnable();
        repeatCountEnable();
        cronExpressionEnable();
    }
    function repeatIntervalEnable() {
        repeatInterval.enable(true);
        $('label[for=repeatInterval]').css({color: 'red'});
        $('#repeatInterval').attr('required', 'required');
        $('#repeatInterval').attr('validationMessage', 'Required');
    }
    function repeatIntervalDisable() {
        $('#repeatInterval').removeAttr('required');
        $('#repeatInterval').removeAttr('validationMessage');
        $('label[for=repeatInterval]').css({color: 'lightGray'});
        repeatInterval.enable(false);
        repeatInterval.value('');
    }
    function repeatCountEnable() {
        repeatCount.enable(true);
        $('label[for=repeatCount]').css({color: 'red'});
        $('#repeatCount').attr('required', 'required');
        $('#repeatCount').attr('validationMessage', 'Required');
    }
    function repeatCountDisable() {
        $('#repeatCount').removeAttr('required');
        $('#repeatCount').removeAttr('validationMessage');
        $('label[for=repeatCount]').css({color: 'lightGray'});
        repeatCount.enable(false);
        repeatCount.value('');
    }
    function cronExpressionEnable() {
        $('#cronExpression').removeAttr('disabled');
        $('label[for=cronExpression]').css({color: 'red'});
        $('#cronExpression').attr('required', 'required');
        $('#cronExpression').attr('validationMessage', 'Required');
    }
    function cronExpressionDisable() {
        $('label[for=cronExpression]').css({color: 'lightGray'});
        $('#cronExpression').removeAttr('required');
        $('#cronExpression').removeAttr('validationMessage');
        $('#cronExpression').attr('disabled', 'disabled');
        $('#cronExpression').val('');
    }
    function loadMenu() {
        var menuId = getMenuIdByPluginId(pluginId);  // load menu
        loadNumberedMenu(menuId, "#appSchedule/show?plugin=" + pluginId); // set left-menu selected
    }
    function initCronHints() {
        $('#cronDetails').popover({
            container: $('#containerExp'),
            html: true
        });
    }

    function closePopup() {
        $('#cronDetails').popover('hide');
    }

</script>