<%@ page import="com.athena.mis.BaseService" %>

<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <li onclick="editMailForCompose();"><i class="fa fa-edit"></i>Edit</li>
    <sec:access url="/appMail/deleteMail">
        <li onclick="deleteAppMail();"><i class="fa fa-trash-o"></i>Discard Draft</li>
    </sec:access>
    <sec:access url="/appMail/sendMail">
        <li onclick="sendAppMail();"><i class="fa fa-envelope-o"></i>Send</li>
    </sec:access>
</ul>
</script>

<script type="text/javascript">

    var appMailDataSource, appMailGrid, appMailModel, entityTypeId, uploading, validator, uploadModel, mailId = 0;
    var isCreateAndSend = false;
    var isCreate = false;
    var hasAttachment = false, editor;

    $(document).ready(function () {
        onLoadMailCompose();
    });

    function onLoadMailCompose() {
        checkOnLoadError();
        validator = $("#appMailForm").kendoValidator({
            validateOnBlur: false
        }).data("kendoValidator");
        initAppMailGrid();
        initObservable();
        initKendoEditor();
        uploadModel = $("#attachment").kendoUpload({
            multiple: false,
            async: {
                saveUrl: "/appMail/uploadAttachment",
                autoUpload: true
            },
            upload: onUploadKendo,
            select: resetKendoUpload,
            success: executePostCondition,
            error: onErrorKendo
        }).data("kendoUpload");

        $("#attachment").closest(".k-upload").find("span").text("Add Attachment ... ");

        entityTypeId = $("#entityTypeId").val();
        setValue();
        var leftMenu = '${leftMenu? leftMenu : '#appMail/showForComposeMail'}';
        var pluginId = '${pluginId? pluginId: '1'}';
        var menuId = getMenuId(pluginId);
        // update page title
        $(document).attr('title', "Compose Mail");
        loadNumberedMenu(menuId, leftMenu);
    }

    function setValue() {
        var subject = "${appMessage? appMessage.subject : ''}";
        $('#recipients').val("${email}");
        if (subject != '') {
            $('#subject').val(subject);
            editor.value('${appMessage? appMessage.body : ""}');
        }
    }

    function getMenuId(pluginId) {
        var menuId = '0';
        switch (pluginId) {
            case '1':
                menuId = MENU_ID_APPLICATION;
                break;
            case '15':
                menuId = MENU_ID_E_LEARNING;
                break;
            default:
                menuId = MENU_ID_APPLICATION;
        }
        return menuId;
    }

    function checkOnLoadError() {
        var isError = '${isError}';
        var msg = '${message}';
        if (isError == 'true') {
            showError(msg);
            return false;
        }
    }

    function executePostCondition(e) {
        var result = (e.response ? e.response : e);     // may be called by kendo control or custom ajax
        if (result.isError) {
            showError(result.message);
            $('li.k-file').addClass('k-file-error');
            resetKendoUpload();
        } else {
            mailId = result.appMail.id;
            hasAttachment = true;
            $('#id').val(mailId);
            if (isCreate) {
                saveMail();
            }
            if (isCreateAndSend) {
                createAndSend();
            }
        }
        uploadModel.enable();
    }

    function onUploadKendo(e) {
        e.data = serializeFormAsObject("#appMailForm");
        uploadModel.disable();
    }

    function initKendoEditor() {
        var height = $('#gridAppMail').height() - $("#application_top_panel").height();
        $(".panel-body").height($(".panel-body").height() + height);
        $("#body").height($("#body").height() + height - 85);
        $("#body").kendoEditor(
                {
                    tools: ['bold', 'italic', 'underline', 'justifyLeft', 'justifyCenter', 'justifyRight', 'justifyFull',
                        'insertUnorderedList', 'insertOrderedList', 'indent', 'insertImage']
                }
        );
        editor = $("#body").data("kendoEditor");
        editor.exec("fontSize", {value: "12pt"});
    }

    function bindMailFormEvents() {
        %{--var actionUrl = "${createLink(controller: 'appMail',action: 'createMail')}";--}%
//        $("#appMailForm").attr('action', actionUrl);

        $('#appMailForm').iframePostForm({
            post: function () {
                uploading = true;
                showLoadingSpinner(true);
                setButtonDisabled($('#create'), true);
            },
            complete: function (response) {
                var tmpResponse = $.parseJSON(response);
                if (uploading == true && !tmpResponse.classSignature) {
                    if (isCreateAndSend) {
                        onSendMail(tmpResponse)
                    } else {
                        onSaveMail(tmpResponse);
                    }
                } else {
                    $('#responseText').val(response);
                    $('#runTimeExceptionErrorModal').modal('show');   // define in commonModals.js
                }
                showLoadingSpinner(false);
                uploading = false;
                setButtonDisabled($('#create'), false);
            },
            beforePost: function () {
                if (executePreConForSubmitMail() == false) {
                    return false;
                }
                return true;
            }
        });
    }


    function executePreConForSubmitMail() {
        clearErrors($("#appMailForm"));
        trimFormValues($("#appMailForm"));
        if (!validator.validate()) {
            return false;
        }
        return true;
    }

    function onSaveMail(result) {
        if (result.isError) {
            showError(result.message);
            return false;
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

    function onSendMail(result) {
        if (result.isError) {
            showError(result.message);
            return false;
        }
        resetMailForm();
        reloadKendoGrid();
        showSuccess(result.message);
    }

    function initDataSource() {
        appMailDataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/appMail/listMail",
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
                        recipients: {type: "string"},
                        recipientsCc: {type: "string"}
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
            height: getFullGridHeightKendo(),
            selectable: true,
            sortable: true,
            resizable: true,
            reorderable: true,
            pageable: {
                refresh: true,
                 buttonCount: 4
            },
            columns: [
                {
                    field: "subject", title: "Subject", width: 200, sortable: false, filterable: false,
                    template: "#= subjectTemplate(subject) #"
                }
            ],
            toolbar: kendo.template($("#gridToolbar").html())
        });
        appMailGrid = $("#gridAppMail").data("kendoGrid");
        $("#menuGrid").kendoMenu();
        clearGridKendo(appMailGrid);
    }

    function subjectTemplate(subject) {
        if (subject == '' || subject == null) {
            subject = "[No Subject]";
        }
        return subject;
    }

    function initObservable() {
        appMailModel = kendo.observable(
                {
                    appMail: {
                        id: "",
                        version: "",
                        subject: "",
                        body: "",
                        recipients: "",
                        recipientsCc: ""
                    }
                }
        );
        kendo.bind($("#application_top_panel"), appMailModel);
    }

    function resetMailForm() {
        clearErrors($('#appMailForm'));
        focusFieldElement($('#recipients'));
        initObservable();
        resetKendoUpload();
        $("#create").html("<span class='k-icon k-i-plus'></span>Save As Draft");

        mailId = 0;
        isCreate = false;
        isCreateAndSend = false;
        hasAttachment = false;
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

    function resetKendoUpload() {
        $(".k-upload-files").remove();
        $(".k-upload-status").remove();
    }

    function deleteAppMail() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true); // Spinner Show on AJAX Call
        var mailId = getSelectedIdFromGridKendo(appMailGrid);
        $.ajax({
            url: "${createLink(controller: 'appMail', action: 'deleteMail')}?id=" + mailId,
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

    function editMailForCompose() {
        if (executeCommonPreConditionForSelectKendo(appMailGrid, 'mail') == false) {
            return;
        }
        showLoadingSpinner(true);
        resetMailForm();
        var appMailObj = getSelectedObjectFromGridKendo(appMailGrid);
        appMailObj.body = htmlDecode(appMailObj.body);
        showAppMail(appMailObj);
        showLoadingSpinner(false);
    }

    function showAppMail(appMailObj) {
        appMailModel.set('appMail', appMailObj);
        var actionUrl = "${createLink(controller: 'appMail',action: 'updateMail')}";
        $("#appMailForm").attr('action', actionUrl);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function sendAppMail() {
        if (executeCommonPreConditionForSelectKendo(appMailGrid, 'mail') == false) {
            return;
        }
        if (!confirm('Are you sure you want to send the selected mail?')) {
            return;
        }
        showLoadingSpinner(true);
        var mailId = getSelectedIdFromGridKendo(appMailGrid);
        $.ajax({
            url: "${createLink(controller: 'appMail', action: 'sendMail')}?id=" + mailId,
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

    function createAndSend() {
        if (executePreConForSubmitMail() == false) return false;
        var actionUrl = "${createLink(controller: 'appMail',action: 'createAndSend')}";
        setButtonDisabled($('#send'), true);
        showLoadingSpinner(true);

        if (mailId == 0 && hasAttachment) {
            isCreateAndSend = true;
            return false;
        }

        jQuery.ajax({
            type: 'post',
            data: serializeFormAsObject("#appMailForm"),
            url: actionUrl,
            success: function (data, textStatus) {
                onSendMail(data);
                setButtonDisabled($('#send'), false);
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

    function saveMail() {
        if (executePreConForSubmitMail() == false) return false;
        var actionUrl = "${createLink(controller: 'appMail',action: 'createMail')}";
        setButtonDisabled($('#send'), true);
        showLoadingSpinner(true);

        if (mailId == 0 && hasAttachment) {
            isCreate = true;
            return false;
        }

        jQuery.ajax({
            type: 'post',
            data: serializeFormAsObject("#appMailForm"),
            url: actionUrl,
            success: function (data, textStatus) {
                onSaveMail(data);
                setButtonDisabled($('#send'), false);
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

    function reloadKendoGrid() {
        appMailGrid.dataSource.filter([]);
    }
</script>