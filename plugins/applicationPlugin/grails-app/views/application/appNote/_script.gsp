<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <app:ifAllUrl urls="/appNote/update">
        <li onclick="editAppNote();"><i class="fa fa-edit"></i>Edit</li>
    </app:ifAllUrl>
    <sec:access url="/appNote/delete">
        <li onclick="deleteAppNote();"><i class="fa fa-trash-o"></i>Delete</li>
    </sec:access>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script type="text/javascript">
    var dataSource, gridAppNote, appNoteModel, noteEditorModel;
    var entityTypeId, entityId, pluginId;

    $(document).ready(function () {
        onLoadAppNotePage();
        initAppNoteGrid();
        initObservable();
    });

    function onLoadAppNotePage() {
        checkOnLoadError();
        // initialize form with kendo validator & bind onSubmit method
        initializeForm($("#appNoteForm"), onSubmitAppNote);
        initKendoEditor(); // initialize kendo editor
        entityTypeId = ${entityTypeId};
        entityId = '${cId?cId:oId}';
        var appNoteMap = ${appNoteMap ? appNoteMap : ''};
        $("#entityTypeId").val(entityTypeId);
        $("#entityId").val(entityId);
        $("#lblEntityTypeName").text(appNoteMap.entityTypeName);
        $("#lblEntityName").text(appNoteMap.entityName);
        $("#lblFormTitle").text(appNoteMap.panelTitle);
        pluginId = appNoteMap.pluginId;
        $("#pluginId").val(pluginId);

        // update page title
        $(document).attr('title', 'MIS - ' + appNoteMap.panelTitle);
        loadMenu(pluginId, appNoteMap.leftMenu);
    }

    function checkOnLoadError() {
        var isError = '${isError}';
        var msg = '${message}';
        if (isError == 'true') {
            showError(msg);
            return false;
        }
    }

    function initKendoEditor() {
        $("#note").kendoEditor();
        noteEditorModel = $("#note").data("kendoEditor");
    }

    // method called  on submit of the form
    function onSubmitAppNote() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);    // disable the save button
        showLoadingSpinner(true);   // show loading spinner
        var actionUrl = null;
        // set link for create or update if there is data in hidden field id
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'appNote', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'appNote', action: 'update')}";
        }

        // fire ajax method
        jQuery.ajax({
            type: 'post',
            data: jQuery("#appNoteForm").serialize(),  // serialize data from UI and send as parameter
            url: actionUrl,
            success: function (data, textStatus) {
                executePostCondition(data);
                setButtonDisabled($('#create'), false);   // enable the save button
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus)
            },
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);  // stop loading spinner
            },
            dataType: 'json'
        });
        return false;
    }

    function executePreCondition() {
        // validate form data
        if (!validateForm($("#appNoteForm"))) {
            return false;
        }
        if (noteEditorModel.value().length > 1000) {
            showError('Too large description for note.');
            return false;
        }
        return true;
    }

    // execute post condition after create or update
    function executePostCondition(result) {
        if (result.isError) {
            showError(result.message);
            showLoadingSpinner(false);
        } else {
            try {
                var newEntry = result.entity;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = gridAppNote.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = gridAppNote.select();
                    var allItems = gridAppNote.items();
                    var selectedIndex = allItems.index(selectedRow);
                    gridAppNote.removeRow(selectedRow);
                    gridAppNote.dataSource.insert(selectedIndex, newEntry);
                }
                resetAppNoteForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetAppNoteForm() {
        initObservable();
        $("#entityTypeId").val(entityTypeId);
        $("#entityId").val(entityId);
        $("#pluginId").val(pluginId);
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function editAppNote() {
        if (executeCommonPreConditionForSelectKendo(gridAppNote, 'note') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridAppNote'));
        resetAppNoteForm();
        showLoadingSpinner(true);
        var appNote = getSelectedObjectFromGridKendo(gridAppNote);
        appNote.note = htmlDecode(appNote.note);
        appNoteModel.set('appNote', appNote);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
        showLoadingSpinner(false);
    }

    function deleteAppNote() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var id = getSelectedIdFromGridKendo(gridAppNote);
        $.ajax({
            url: "${createLink(controller: 'appNote', action: 'delete')}?id=" + id,
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

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelectKendo(gridAppNote, 'note') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected note?')) {
            return false;
        }
        return true;
    }

    function executePostConditionForDelete(data) {
        if (data.isError) {
            showError(data.message);
            return false;
        } else {
            var row = gridAppNote.select();
            row.each(function () {
                gridAppNote.removeRow($(this));
            });
            resetAppNoteForm();
            showSuccess(data.message);
        }
    }

    function loadMenu(pluginId, leftMenu) {
        var MENU_ID;
        switch (pluginId) {
            case 10:
                MENU_ID = MENU_ID_PROJECT_TRACK;
                break;
            case 9:
                MENU_ID = MENU_ID_EXCHANGE_HOUSE;
                break;
            default:
                MENU_ID = MENU_ID_APPLICATION;
                break
        }
        loadNumberedMenu(MENU_ID, leftMenu);
    }

    function iniDataSource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/appNote/list",
                    data: {entityId: entityId, entityTypeId: entityTypeId},
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
                        companyId: {type: "number"},
                        entityTypeId: {type: "number"},
                        entityId: {type: "number"},
                        note: {type: "string"},
                        entityNoteTypeId: {type: "number"},
                        entityNoteType: {type: "string"},
                        createdOn: {type: "date"},
                        username: {type: "string"}
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

    function initAppNoteGrid() {
        iniDataSource();
        $("#gridAppNote").kendoGrid({
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
                    field: "note", title: "Note", sortable: false, width: 250,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}},
                    template: "#=htmlDecode(note)#"
                },
                {field: "entityNoteType", title: "Note Type", width: 70, sortable: false, filterable: false},
                {
                    field: "createdOn", title: "Created By", width: 100, sortable: false, filterable: false,
                    template: "#= (username+' ('+kendo.toString(kendo.parseDate(createdOn, 'yyyy-MM-dd'), 'dd-MMM-yyyy')+')') #"
                }

            ],
            filterable: {mode: "row"},
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridAppNote = $("#gridAppNote").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }


    function reloadKendoGrid() {
        gridAppNote.dataSource.filter([]);
    }

    function initObservable() {
        appNoteModel = kendo.observable(
                {
                    appNote: {
                        id: "",
                        version: "",
                        note: "",
                        entityTypeId: entityTypeId ? entityTypeId : "",
                        entityId: entityId ? entityId : ""
                    }
                }
        );
        kendo.bind($("#application_top_panel"), appNoteModel);
    }

</script>