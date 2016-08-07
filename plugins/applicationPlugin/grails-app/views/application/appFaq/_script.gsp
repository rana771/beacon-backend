<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <app:ifAllUrl urls="/appFaq/update">
        <li onclick="editAppNote();"><i class="fa fa-edit"></i>Edit</li>
    </app:ifAllUrl>
    <sec:access url="/appFaq/delete">
        <li onclick="deleteAppNote();"><i class="fa fa-trash-o"></i>Delete</li>
    </sec:access>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script type="text/javascript">
    var dataSource, gridAppFaq, appFaqModel, faqEditor;
    var entityTypeId, entityId, pluginId;

    $(document).ready(function () {
        onLoadAppFaqPage();
        initAppNoteGrid();
        initObservable();
    });

    function onLoadAppFaqPage() {
        checkOnLoadError();
        // initialize form with kendo validator & bind onSubmit method
        initializeForm($("#appFaqForm"), onSubmitAppFaq);
        initKendoEditor(); // initialize kendo editor
        entityTypeId = ${entityTypeId};
        entityId = '${entityId}';
        var appFaqMap = ${appFaqMap ? appFaqMap : ''};
        $("#entityTypeId").val(entityTypeId);
        $("#entityId").val(entityId);
        $("#lblEntityName").text(appFaqMap.entityName);
        $("#lblEntityTypeName").text(appFaqMap.entityTypeName);
        $("#lblFormTitle").text(appFaqMap.panelTitle);
        pluginId = appFaqMap.pluginId;
        $("#pluginId").val(pluginId);

        // update page title
        $(document).attr('title', 'MIS - ' + appFaqMap.panelTitle);
        loadMenu(pluginId, appFaqMap.leftMenu);
    }

    function loadMenu(pluginId, leftMenu) {
        var MENU_ID;
        switch (pluginId) {
            case 13:
                MENU_ID = MENU_ID_DOCUMENT;
                break;
            default:
                MENU_ID = MENU_ID_APPLICATION;
                break
        }
        loadNumberedMenu(MENU_ID, "#"+leftMenu);
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
        $("#answer").kendoEditor();
        faqEditor = $("#answer").data("kendoEditor");
    }

    // method called  on submit of the form
    function onSubmitAppFaq() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);    // disable the save button
        showLoadingSpinner(true);   // show loading spinner
        var actionUrl = null;
        // set link for create or update if there is data in hidden field id
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'appFaq', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'appFaq', action: 'update')}";
        }

        // fire ajax method
        jQuery.ajax({
            type: 'post',
            data: jQuery("#appFaqForm").serialize(),  // serialize data from UI and send as parameter
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
        if (!validateForm($("#appFaqForm"))) {
            return false;
        }
        if (faqEditor.value().length > 1000) {
            showError('Too large answer.');
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
                var newEntry = result.appFaq;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = gridAppFaq.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = gridAppFaq.select();
                    var allItems = gridAppFaq.items();
                    var selectedIndex = allItems.index(selectedRow);
                    gridAppFaq.removeRow(selectedRow);
                    gridAppFaq.dataSource.insert(selectedIndex, newEntry);
                }
                resetAppFaqForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetAppFaqForm() {
        initObservable();
        $("#entityTypeId").val(entityTypeId);
        $("#entityId").val(entityId);
        $("#pluginId").val(pluginId);
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function editAppNote() {
        if (executeCommonPreConditionForSelectKendo(gridAppFaq, 'answer') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridAppFaq'));
        resetAppFaqForm();
        showLoadingSpinner(true);
        var appFaq = getSelectedObjectFromGridKendo(gridAppFaq);
        appFaq.answer = htmlDecode(appFaq.answer);
        appFaqModel.set('appFaq', appFaq);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
        showLoadingSpinner(false);
    }

    function deleteAppNote() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var id = getSelectedIdFromGridKendo(gridAppFaq);
        $.ajax({
            url: "${createLink(controller: 'appFaq', action: 'delete')}?id=" + id,
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
        if (executeCommonPreConditionForSelectKendo(gridAppFaq, 'answer') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected answer?')) {
            return false;
        }
        return true;
    }

    function executePostConditionForDelete(data) {
        if (data.isError) {
            showError(data.message);
            return false;
        } else {
            var row = gridAppFaq.select();
            row.each(function () {
                gridAppFaq.removeRow($(this));
            });
            resetAppFaqForm();
            showSuccess(data.message);
        }
    }

    function iniDataSource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/appFaq/list",
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
                        question: {type: "string"},
                        answer: {type: "string"},
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
        $("#gridAppFaq").kendoGrid({
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
                {field: "question", title: "Faq", sortable: false, width: 250,filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}},template: "#=htmlDecode(question)#"},
                {field: "answer", title: "Answer", sortable: false, width: 250, attributes: {style: "white-space:nowrap"},filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}},template: "#=htmlDecode(answer)#"},
                {field: "createdOn", title: "Created By", width: 150, sortable: false, filterable: false,template: "#= (username+' ('+kendo.toString(kendo.parseDate(createdOn, 'yyyy-MM-dd'), 'dd-MMM-yyyy')+')') #"}
            ],
            filterable: {mode: "row"},
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridAppFaq = $("#gridAppFaq").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }


    function reloadKendoGrid() {
        gridAppFaq.dataSource.filter([]);
    }

    function initObservable() {
        appFaqModel = kendo.observable(
                {
                    appFaq: {
                        id: "",
                        version: "",
                        answer: "",
                        question: "",
                        entityTypeId: entityTypeId ? entityTypeId : "",
                        entityId: entityId ? entityId : ""
                    }
                }
        );
        kendo.bind($("#application_top_panel"), appFaqModel);
    }

</script>