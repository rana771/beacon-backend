<style type="text/css">
.k-upload-selected,
.k-upload-action {
    display: none;
}
</style>

<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <app:ifAllUrl urls="/appAttachment/update">
        <li onclick="editAppAttachment();"><i class="fa fa-edit"></i>Edit</li>
    </app:ifAllUrl>
    <sec:access url="/appAttachment/delete">
        <li onclick="deleteAppAttachment();"><i class="fa fa-trash-o"></i>Delete</li>
    </sec:access>
    <sec:access url="/appAttachment/downloadContent">
        <li onclick="downloadContent();"><i class="fa fa-download"></i>Download</li>
    </sec:access>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script type="text/javascript">
    var gridAppAttachment, appAttachmentModel, dataSource, validatorAttachment, entityTypeId, entityId;
    var uploadModel, hasAttachment = false;
    var isCreate = false;
    var attachmentId = 0;

    $(document).ready(function () {
        onLoadAppAttachmentPage();
        initAppAttachmentGrid();
        initObservable();
    });

    function onLoadAppAttachmentPage() {
        checkOnLoadError();
        // common initializeForm() is not used here due to customValidation/upload
        initFormWithCustomRule();

        uploadModel = $("#contentObj").kendoUpload({
            multiple: false,
            async: {
                saveUrl: "/appAttachment/upload",
                autoUpload: true
            },
            upload: onUploadKendo,
            select: resetKendoUpload,
            success: executePostCondition,
            error: onErrorKendo
        }).data("kendoUpload");

        $("#create").click(function () {
            onClickCreate();
        });

        populateOnLoadObjects();
    }

    function checkOnLoadError() {
        var isError = '${isError}';
        var msg = '${message}';
        if (isError == 'true') {
            showError(msg);
            return false;
        }
    }

    function initDataSource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/appAttachment/list",
                    data: {entityTypeId: entityTypeId, entityId: entityId},
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
                        entityTypeId: {type: "number"},
                        entityId: {type: "number"},
                        caption: {type: "string"},
                        fileName: {type: "string"},
                        expirationDate: {type: "date"},
                        extension: {type: "string"}
                    }
                },
                parse: function (data) {
                    checkIsErrorGridKendo(data);
                    return data;
                }
            },
            sort: {field: 'caption', dir: 'asc'},  // default sort
            pageSize: ${com.athena.mis.BaseService.DEFAULT_RESULT_PER_PAGE},
            serverPaging: true,
            serverFiltering: true,
            serverSorting: true
        });
    }

    function initAppAttachmentGrid() {
        initDataSource();
        $("#gridAppAttachment").kendoGrid({
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
                {field: "caption",title: "Caption",width: 250,sortable: true,filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}},
                {field: "fileName", title: "File Name", width: 150, sortable: false, filterable: false},
                {field: "extension", title: "Extension", width: 75, sortable: true, filterable: false}
            ],
            filterable: {mode: "row"},
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridAppAttachment = $("#gridAppAttachment").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function reloadKendoGrid() {
        gridAppAttachment.dataSource.filter([]);
    }

    function initObservable() {
        appAttachmentModel = kendo.observable(
                {
                    appAttachment: {
                        id: "",
                        version: "",
                        entityTypeId: "",
                        entityId: "",
                        caption: "",
                        expirationDate: "",
                        extension: ""
                    }
                }
        );
        kendo.bind($("#application_top_panel"), appAttachmentModel);
    }

    function populateOnLoadObjects() {
        entityTypeId = $("#entityTypeId").val();
        entityId = $("#entityId").val();
        var appAttachmentMap = ${appAttachmentMap ? appAttachmentMap : ''};
        var entityTypeName = appAttachmentMap.entityTypeName;
        $("#lblEntityTypeName").text(entityTypeName);
        var entityName = appAttachmentMap.entityName;
        $("#lblEntityName").text(entityName);
        var panelTitle = appAttachmentMap.panelTitle;
        $("#lblFormTitle").text(panelTitle);
        var pluginId = appAttachmentMap.pluginId;
        var leftMenu = appAttachmentMap.leftMenu;
        // update page title
        $(document).attr('title', 'MIS - ' + panelTitle);
        loadMenu(pluginId, leftMenu);
    }

    function onClickCreate() {
        if (executePreCondition() == false) return false;

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);

        if (attachmentId == 0 && hasAttachment) {
            isCreate = true;
            return false;
        }

        var actionUrl = "${createLink(controller: 'appAttachment', action: 'create')}?attachmentId=" + attachmentId;
        if (!isEmpty($('#id').val())) {
            actionUrl = "${createLink(controller: 'appAttachment', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: serializeFormAsObject("#appAttachmentForm"),
            url: actionUrl,
            success: function (data, textStatus) {
                executePostConditionForCreate(data);
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

    function executePostConditionForCreate(result) {
        if (result.isError) {
            showError(result.message);
            setButtonDisabled($('#create'), false);
            showLoadingSpinner(false);
        } else {
            try {
                var newEntry = result.appAttachment;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = gridAppAttachment.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = gridAppAttachment.select();
                    var allItems = gridAppAttachment.items();
                    var selectedIndex = allItems.index(selectedRow);
                    gridAppAttachment.removeRow(selectedRow);
                    gridAppAttachment.dataSource.insert(selectedIndex, newEntry);
                }
                resetAppAttachmentForm();
                resetKendoUpload();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function initFormWithCustomRule() {
        validatorAttachment = $("#appAttachmentForm").kendoValidator({
            validateOnBlur: false,
            rules: {
                upload: function (input) {
                    if ((input[0].type == "file") && ($(input[0]).is('[validationMessage]'))) {
                        return input.closest(".k-upload").find(".k-file").length;
                    }
                    return true;
                }
            }
        }).data("kendoValidator");
    }

    function executePreCondition() {
        // trim field vales before process.
        trimFormValues($("#appAttachmentForm"));

        if (!validatorAttachment.validate()) {
            return false;
        }
        return true;
    }

    function onUploadKendo(e) {
        e.data = serializeFormAsObject("#appAttachmentForm");
    }

    function updateObjectWithOutFile() {
        showLoadingSpinner(true);
        $.ajax({
            type: 'post', dataType: 'json',
            data: jQuery("#appAttachmentForm").serialize(),
            url: "${createLink(controller: 'appAttachment', action: 'update')}",
            success: function (data, textStatus) {
                executePostCondition(data);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus)
            },
            complete: onCompleteAjaxCall
        });
    }

    function resetKendoUpload(e) {
        hasAttachment = false;
        if (e && e.files[0].name) {
            hasAttachment = true;
        }
        $(".k-upload-files").remove();
        $(".k-upload-status").remove();
    }


    function  clearUpload(){
        $(".k-upload-files").remove();
        $(".k-upload-status").remove();
    }

    function executePostCondition(e) {
        var result = (e.response ? e.response : e);     // may be called by kendo control or custom ajax
        if (result.isError) {
            showError(result.message);
            $('li.k-file').addClass('k-file-error');
            uploadModel.enable();
            clearUpload();
        } else {
            attachmentId = result.id;
            if (isCreate) {
                onClickCreate();
            }
        }
    }

    function resetAppAttachmentUI() {
        resetAppAttachmentForm();
        resetKendoUpload();
    }

    function resetAppAttachmentForm() {
        clearForm($("#appAttachmentForm"), $("#caption"));
        $("#entityTypeId").val(entityTypeId);
        $("#entityId").val(entityId);

        setButtonDisabled($('#create'), false);

        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
        initObservable();
        $("#labelAttachment").removeClass('label-optional');
        $("#labelAttachment").addClass('label-required');
        $('#contentObj').attr('validationMessage', 'Required');

        attachmentId = 0;
        isCreate = false;
    }

    function editAppAttachment() {
        if (executeCommonPreConditionForSelectKendo(gridAppAttachment, 'attachment') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridAppAttachment'));
        var appAttachmentObj = getSelectedObjectFromGridKendo(gridAppAttachment);
        showAppAttachment(appAttachmentObj);
    }

    function showAppAttachment(appAttachmentObj) {
        appAttachmentModel.set("appAttachment", appAttachmentObj);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
        $("#labelAttachment").removeClass('label-required');
        $("#labelAttachment").addClass('label-optional');
        $('#contentObj').removeAttr('validationMessage');
        clearUpload();
    }

    function deleteAppAttachment() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var id = getSelectedIdFromGridKendo(gridAppAttachment);
        $.ajax({
            url: "${createLink(controller: 'appAttachment', action: 'delete')}?id=" + id,
            success: executePostConditionForDelete,
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

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelectKendo(gridAppAttachment, 'attachment') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected attachment?')) {
            return false;
        }
        return true;
    }

    function executePostConditionForDelete(data) {
        if (data.isError) {
            showError(data.message);
            return false;
        }
        var row = gridAppAttachment.select();
        row.each(function () {
            gridAppAttachment.removeRow($(this));
        });
        resetAppAttachmentForm();
        showSuccess(data.message);
    }

    function executePreConditionForDownload(ids) {
        var downloadCount = ids.length;
        if (downloadCount == 0) {
            showError("Please select an attachment to download");
            return false;
        }
        return true;
    }

    function downloadContent() {
        if (executeCommonPreConditionForSelectKendo(gridAppAttachment, 'attachment') == false) {
            return;
        }
        var confirmMsg = 'Do you want to download the attachment now?';
        showLoadingSpinner(true);
        var attachmentId = getSelectedIdFromGridKendo(gridAppAttachment);
        if (confirm(confirmMsg)) {
            var url = "${createLink(controller: 'appAttachment', action: 'downloadContent')}?appAttachmentId=" + attachmentId;
            document.location = url;
        }
        showLoadingSpinner(false);
        return true;
    }

    function loadMenu(pluginId, leftMenu) {
        var MENU_ID;
        switch (pluginId) {
            case 1:
                MENU_ID = MENU_ID_APPLICATION;
                break;
            case 2:
                MENU_ID = MENU_ID_ACCOUNTING;
                break;
            case 3:
                MENU_ID = MENU_ID_BUDGET;
                break;
            case 9:
                MENU_ID = MENU_ID_EXCHANGE_HOUSE;
                break;
            case 10:
                MENU_ID = MENU_ID_PROJECT_TRACK;
                break;
            default:
                MENU_ID = MENU_ID_APPLICATION
        }
        loadNumberedMenu(MENU_ID, leftMenu);
    }
</script>