<%@ page import="com.athena.mis.BaseService" %>

<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <li onclick="editSmsForCompose();"><i class="fa fa-edit"></i>Edit</li>
    <sec:access url="/appSms/delete">
        <li onclick="deleteSms();"><i class="fa fa-trash-o"></i>Delete</li>
    </sec:access>
    <sec:access url="/appSms/sendForCompose">
        <li onclick="sendSms();"><i class="fa fa-send"></i>Send SMS</li>
    </sec:access>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script language="javascript" type="text/javascript">

    var smsGrid, smsDataSource, smsModel, dropDownRole;

    $(document).ready(function () {
        onLoadSmsPage();
        initSmsGrid();
        initObservable();
    });

    function onLoadSmsPage() {
        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#smsForm"), onSubmitSmsForm);
        // update page title
        $(document).attr('title', "Compose SMS");
        loadNumberedMenu(MENU_ID_APPLICATION, "#appSms/showForCompose");
    }

    function executePreCondition() {
        if (!validateForm($("#smsForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitSmsForm() {
        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'appSms', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'appSms', action: 'updateForCompose')}";
        }

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
            try {
                var newEntry = result.sms;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = smsGrid.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = smsGrid.select();
                    var allItems = smsGrid.items();
                    var selectedIndex = allItems.index(selectedRow);
                    smsGrid.removeRow(selectedRow);
                    smsGrid.dataSource.insert(selectedIndex, newEntry);
                }
                resetSmsForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function editSmsForCompose() {
        if (executeCommonPreConditionForSelectKendo(smsGrid, 'sms') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridSms'));
        showLoadingSpinner(true);
        resetSmsForm();
        var smsObj = getSelectedObjectFromGridKendo(smsGrid);
        showSms(smsObj);
        showLoadingSpinner(false);
    }

    function showSms(smsObj) {
        smsModel.set('sms', smsObj);
        dropDownRole.value(smsObj.roleId);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function sendSms() {
        if (executeCommonPreConditionForSelectKendo(smsGrid, 'sms') == false) {
            return;
        }
        if (!confirm('Are you sure you want to send the selected sms?')) {
            return;
        }
        var smsId = getSelectedIdFromGridKendo(smsGrid);
        $.ajax({
            url: "${createLink(controller: 'appSms', action: 'sendForCompose')}?id=" + smsId,
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

    function executePostConditionForSendSms(data) {
        if (data.isError) {
            showError(data.message);
            return false;
        }
        var row = smsGrid.select();
        row.each(function () {
            smsGrid.removeRow($(this));
        });
        resetSmsForm();
        showSuccess(data.message);
    }

    function initDataSource() {
        smsDataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/appSms/listForCompose",
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
                        body: {type: "string"},
                        roleId: {type: "string"},
                        roleName: {type: "string"}
                    }
                },
                parse: function (data) {
                    checkIsErrorGridKendo(data);
                    return data;
                }
            },
            sort: {field: 'id', dir: 'desc'},  // default sort
            pageSize: ${BaseService.DEFAULT_RESULT_PER_PAGE},
            serverPaging: true,
            serverFiltering: true,
            serverSorting: true
        });
    }

    function initSmsGrid() {
        initDataSource();
        $("#gridSms").kendoGrid({
            dataSource: smsDataSource,
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
                {field: "roleName", title: "Role", width: 150, sortable: false, filterable: false},
                {field: "body", title: "Body", width: 300, sortable: false, filterable: false}
            ],
            toolbar: kendo.template($("#gridToolbar").html())
        });
        smsGrid = $("#gridSms").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function reloadKendoGrid() {
        smsGrid.dataSource.filter([]);
    }

    function initObservable() {
        smsModel = kendo.observable(
                {
                    sms: {
                        id: "",
                        version: "",
                        body: "",
                        roleId: ""
                    }
                }
        );
        kendo.bind($("#application_top_panel"), smsModel);
    }

    function resetSmsForm() {
        clearForm($("#smsForm"), $("#roleId"));
        initObservable();
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelectKendo(smsGrid, 'sms') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected sms?')) {
            return false;
        }
        return true;
    }

    function deleteSms() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true); // Spinner Show on AJAX Call
        var smsId = getSelectedIdFromGridKendo(smsGrid);
        $.ajax({
            url: "${createLink(controller: 'appSms', action: 'delete')}?id=" + smsId,
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

    function executePostConditionForDelete(data) {
        if (data.isError) {
            showError(data.message);
            return false;
        }
        var row = smsGrid.select();
        row.each(function () {
            smsGrid.removeRow($(this));
        });
        resetSmsForm();
        showSuccess(data.message);
    }

</script>