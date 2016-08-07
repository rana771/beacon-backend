<%@ page import="com.athena.mis.BaseService" %>

<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <li onclick="editAnnouncement();"><i class="fa fa-edit"></i><g:message code="app.announcement.edit"/></li>
    <sec:access url="/appMail/deleteAnnouncement">
        <li onclick="deleteAppMail();"><i class="fa fa-trash-o"></i><g:message code="app.announcement.delete"/></li>
    </sec:access>
    <sec:access url="/appMail/sendAnnouncement">
        <li onclick="sendAppMail();"><i class="fa fa-envelope-o"></i><g:message code="app.announcement.send"/></li>
    </sec:access>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i><g:message code="app.announcement.refresh"/></li>
</ul>
</script>

<script language="javascript" type="text/javascript">

    var appMailGrid, appMailDataSource, appMailModel, dropDownRole, userName;

    $(document).ready(function () {
        onLoadAppMailPage();
        initAppMailGrid();
        initObservable();
    });

    function onLoadAppMailPage() {
        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#appMailForm"), onSubmitAppMailForm);
        initKendoEditor(); // initialize kendo editor
        userName = "${userName}";
        // update page title
        $(document).attr('title', "Compose Mail");
        loadNumberedMenu(MENU_ID_APPLICATION, "#appMail/showAnnouncement");
    }

    function initKendoEditor() {
        $("#body").kendoEditor();
    }

    function executePreCondition() {
        if (!validateForm($("#appMailForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitAppMailForm() {
        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'appMail', action: 'createAnnouncement')}";
        } else {
            actionUrl = "${createLink(controller: 'appMail', action: 'updateAnnouncement')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#appMailForm").serialize(),
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
                var newEntry = result.appMail;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = appMailGrid.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = appMailGrid.select();
                    var allItems = appMailGrid.items();
                    var selectedIndex = allItems.index(selectedRow);
                    appMailGrid.removeRow(selectedRow);
                    appMailGrid.dataSource.insert(selectedIndex, newEntry);
                }
                resetMailForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function editAnnouncement() {
        if (executeCommonPreConditionForSelectKendo(appMailGrid, 'mail') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridAppMail'));
        showLoadingSpinner(true);
        resetMailForm();
        var appMailObj = getSelectedObjectFromGridKendo(appMailGrid);
        appMailObj.body = htmlDecode(appMailObj.body);
        showAppMail(appMailObj);
        showLoadingSpinner(false);
    }

    function showAppMail(appMailObj) {
        appMailModel.set('appMail', appMailObj);
        dropDownRole.value(appMailObj.roleId);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function sendAppMail() {
        if (executeCommonPreConditionForSelectKendo(appMailGrid, 'mail') == false) {
            return;
        }
        if (!confirm('Are you sure you want to send the selected mail?')) {
            return;
        }
        var mailId = getSelectedIdFromGridKendo(appMailGrid);
        $.ajax({
            url: "${createLink(controller: 'appMail', action: 'sendAnnouncement')}?id=" + mailId,
            success: executePostConditionForSendMail,
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

    function executePostConditionForSendMail(data) {
        if (data.isError) {
            showError(data.message);
            return false;
        }
        var row = appMailGrid.select();
        row.each(function () {
            appMailGrid.removeRow($(this));
        });
        resetMailForm();
        showSuccess(data.message);
    }

    function initDataSource() {
        appMailDataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/appMail/listAnnouncement",
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
                        subject: {type: "string"},
                        body: {type: "string"},
                        roleId: {type: "string"},
                        roleName: {type: "string"},
                        displayName: {type: "string"}
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
                {field: "roleName", title: "Role", width: 150, sortable: false, filterable: false},
                {field: "displayName", title: "Sender", width: 150, sortable: false, filterable: false},
                {field: "subject", title: "Subject", width: 200, sortable: false, filterable: false}
            ],
            toolbar: kendo.template($("#gridToolbar").html())
        });
        appMailGrid = $("#gridAppMail").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }


    function reloadKendoGrid() {
        appMailGrid.dataSource.filter([]);
    }

    function initObservable() {
        appMailModel = kendo.observable(
                {
                    appMail: {
                        id: "",
                        version: "",
                        subject: "",
                        body: "",
                        roleIds: "",
                        displayName: userName
                    }
                }
        );
        kendo.bind($("#application_top_panel"), appMailModel);
    }

    function resetMailForm() {
        clearErrors($('#appMailForm'));
        initObservable();
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelectKendo(appMailGrid, 'mail') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected mail?')) {
            return false;
        }
        return true;
    }

    function deleteAppMail() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true); // Spinner Show on AJAX Call
        var mailId = getSelectedIdFromGridKendo(appMailGrid);
        $.ajax({
            url: "${createLink(controller: 'appMail', action: 'deleteAnnouncement')}?id=" + mailId,
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
        var row = appMailGrid.select();
        row.each(function () {
            appMailGrid.removeRow($(this));
        });
        resetMailForm();
        showSuccess(data.message);
    }

</script>