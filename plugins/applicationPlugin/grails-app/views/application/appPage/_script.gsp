<%@ page import="com.athena.mis.BaseService" %>

<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <li onclick="editAppPage();"><i class="fa fa-edit"></i>Edit</li>
    <sec:access url="/appPage/delete">
        <li onclick="deleteAppPage();"><i class="fa fa-trash-o"></i>Delete</li>
    </sec:access>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script type="text/javascript">

    var dataSource, gridAppPage, appPageModel;

    $(document).ready(function () {
        onLoadAppPage();
    });

    function onLoadAppPage() {
        checkOnLoadError();
        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#appPageForm"), onSubmitAppPage);
        initKendoEditor();
        initAppPageGrid();
        initObservable();

        $("#isCommentable").kendoMobileSwitch({
            onLabel: "YES",
            offLabel: "NO"
        });

        // update page title
        $(document).attr('title', "Page");
        loadNumberedMenu(MENU_ID_APPLICATION, "#appPage/show");
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
        $("#body").kendoEditor(
                {
                    tools: ['formatting', 'bold','italic','underline','justifyLeft', 'justifyCenter', 'justifyRight', 'justifyFull',
                        'insertUnorderedList', 'insertOrderedList', 'indent','createLink', 'insertImage', 'createTable', 'viewHtml']
                }
        );
    }

    function executePreCondition() {
        if (!validateForm($("#appPageForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitAppPage() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);    // disable the save button
        showLoadingSpinner(true);   // show loading spinner
        var actionUrl = null;
        // set link for create or update if there is data in hidden field id
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'appPage', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'appPage', action: 'update')}";
        }

        // fire ajax method
        jQuery.ajax({
            type: 'post',
            data: jQuery("#appPageForm").serialize(),  // serialize data from UI and send as parameter
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

    // execute post condition after create or update
    function executePostCondition(result) {
        if (result.isError) {
            showError(result.message);
            showLoadingSpinner(false);
        } else {
            try {
                var newEntry = result.appPage;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = gridAppPage.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = gridAppPage.select();
                    var allItems = gridAppPage.items();
                    var selectedIndex = allItems.index(selectedRow);
                    gridAppPage.removeRow(selectedRow);
                    gridAppPage.dataSource.insert(selectedIndex, newEntry);
                }
                resetForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        clearErrors($('#appPageForm'));
        focusFieldElement($('#title'));
        initObservable();
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function initDataSource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/appPage/list",
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
                        title: {type: "string"},
                        entityTypeId: {type: "number"},
                        body: {type: "string"},
                        isCommentable: {type: "boolean"}
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

    function initAppPageGrid() {
        initDataSource();
        $("#gridAppPage").kendoGrid({
            dataSource: dataSource,
            height: getGridHeightKendo(),
            selectable: true,
            sortable: true,
            resizable: true,
            reorderable: true,
            pageable: {
                refresh: true,
                pageSizes: [10, 15, 20],
                buttonCount: 4
            },
            columns: [
                {
                    field: "title", title: "title", width: 200, sortable: false, filterable: false
                }
            ],
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridAppPage = $("#gridAppPage").data("kendoGrid");
        $("#menuGrid").kendoMenu();
        clearGridKendo(gridAppPage);
    }

    function editAppPage() {
        if (executeCommonPreConditionForSelectKendo(gridAppPage, 'answer') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridAppPage'));
        var page = getSelectedObjectFromGridKendo(gridAppPage);
        page.body = htmlDecode(page.body);
        showPage(page);
    }

    function showPage(data) {
        appPageModel.set('appPage', data);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelectKendo(gridAppPage, 'page') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected page?')) {
            return false;
        }
        return true;
    }

    function deleteAppPage() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var id = getSelectedIdFromGridKendo(gridAppPage);
        $.ajax({
            url: "${createLink(controller: 'appPage', action: 'delete')}?id=" + id,
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
        } else {
            var row = gridAppPage.select();
            row.each(function () {
                gridAppPage.removeRow($(this));
            });
            resetForm();
            showSuccess(data.message);
        }
    }

    function reloadKendoGrid() {
        gridAppPage.dataSource.filter([]);
    }

    function initObservable() {
        appPageModel = kendo.observable(
                {
                    appPage: {
                        id: "",
                        version: "",
                        title: "",
                        body: "",
                        entityTypeId: "0",
                        isCommentable: ""
                    }
                }
        );
        kendo.bind($("#application_top_panel"), appPageModel);
    }
</script>