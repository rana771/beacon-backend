<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <app:ifAllUrl urls="/appShellScript/update">
        <li onclick="editShellScript();"><i class="fa fa-edit"></i>Edit</li>
    </app:ifAllUrl>
    <sec:access url="/appShellScript/delete">
        <li onclick="deleteShellScript();"><i class="fa fa-trash-o"></i>Delete</li>
    </sec:access>
    <sec:access url="/appShellScript/evaluateSqlScript">
        <li onclick="evaluateShellScript();"><i class="fa fa-play"></i>Evaluate</li>
    </sec:access>
    <sec:access url="/appNote/show">
        <li onclick="addNote();"><i class="fa fa-comment-o"></i>Note</li>
    </sec:access>
    <sec:access url="/appShellScript/downloadReport">
        <li onclick="scriptReport();"><i class="fa fa-file-text"></i>Report</li>
    </sec:access>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script type="text/javascript">
    var appSqlScriptGrid, scriptTypeId, appShellScriptDataSource, appSqllScriptModel, pluginId, dropDownDbInstance,
            entityTypeId;

    $(document).ready(function () {
        pluginId = ${pluginId};
        onLoadShellScript();
        initAppShellScriptGrid();
        initObservable();
    });

    function onLoadShellScript() {
        // initialize form with kendo validator & bind onSubmit event
        initializeForm($('#sqlScriptForm'), onSubmitShellScript);
        scriptTypeId = $("#scriptTypeId").val();
        entityTypeId = $("#entityTypeId").val();
        // update page title
        $(document).attr('title', "MIS - Create SQL Script");
        loadMenu(pluginId);
    }

    function onSubmitShellScript() {
        if (!validateForm($('#sqlScriptForm'))) {
            return false;
        }
        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'appShellScript', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'appShellScript', action:  'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#sqlScriptForm").serialize(),
            url: actionUrl,
            success: function (data, textStatus) {
                executePostConForSubmitShellScript(data);
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

    function executePostConForSubmitShellScript(result) {
        if (result.isError) {
            showError(result.message);
            showLoadingSpinner(false);
        } else {
            try {
                var newEntry = result.appShellScript;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = appSqlScriptGrid.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = appSqlScriptGrid.select();
                    var allItems = appSqlScriptGrid.items();
                    var selectedIndex = allItems.index(selectedRow);
                    appSqlScriptGrid.removeRow(selectedRow);
                    appSqlScriptGrid.dataSource.insert(selectedIndex, newEntry);
                }
                clearShellScriptForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function editShellScript() {
        if (executeCommonPreConditionForSelectKendo(appSqlScriptGrid, 'sql script') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#appSqlScriptGrid'), initScriptDivHeight);
        var shellScript = getSelectedObjectFromGridKendo(appSqlScriptGrid);
        showShellScriptInfo(shellScript);
    }

    function showShellScriptInfo(shellScript) {
        appSqllScriptModel.set('appShellScript', shellScript);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function evaluateShellScript() {
        if (executeCommonPreConditionForSelectKendo(appSqlScriptGrid, 'sql script') == false) {
            return;
        }
        showLoadingSpinner(true);
        var shellScriptId = getSelectedIdFromGridKendo(appSqlScriptGrid);
        var url = "${createLink(controller: 'appShellScript', action: 'evaluateSqlScript')}?id=" + shellScriptId;
        $.ajax({
            url: url,
            success: executePostConForEvaluate,
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus);
            },
            complete: onCompleteAjaxCall,
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConForEvaluate(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            $('#evaluatedSqlScript').val(data.output);
            var selectedRow = appSqlScriptGrid.select();
            var gridData = appSqlScriptGrid.dataItem(selectedRow);
            gridData.set("lastExecutedOn", data.appShellScript.lastExecutedOn);
            gridData.set("numberOfExecution", data.appShellScript.numberOfExecution);
            clearShellScriptFormForEvaluate();
            showSuccess(data.message)
        }
    }

    function clearShellScriptForm() {
        clearForm($("#sqlScriptForm"), $('#name'));
        $('#evaluatedSqlScript').val('');
        $("#scriptTypeId").val(scriptTypeId);
        initObservable();
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function clearShellScriptFormForEvaluate() {
        clearForm($("#sqlScriptForm"), $('#name'));
        $("#scriptTypeId").val(scriptTypeId);
        initObservable();
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function reloadKendoGrid() {
        appSqlScriptGrid.dataSource.filter([]);
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelectKendo(appSqlScriptGrid, 'sql script') == false) {
            return false;
        }
        var isReserved = getSelectedValueFromGridKendo(appSqlScriptGrid, "isReserved");
        if (isReserved) {
            showError("Selected sql script is reserved");
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected sql script?')) {
            return false;
        }
        return true;
    }

    function deleteShellScript() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var shellScriptId = getSelectedIdFromGridKendo(appSqlScriptGrid);
        $.ajax({
            url: "${createLink(controller: 'appShellScript', action: 'delete')}?id=" + shellScriptId,
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
        var row = appSqlScriptGrid.select();
        row.each(function () {
            appSqlScriptGrid.removeRow($(this));
        });
        clearShellScriptForm();
        showSuccess(data.message);
    }

    function initDataSource() {
        appShellScriptDataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/appShellScript/list",
                    dataType: "json",
                    data: {pluginId: pluginId, scriptTypeId: scriptTypeId},
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
                        script: {type: "string"},
                        transactionCode: {type: "string"},
                        lastExecutedOn: {type: "date"},
                        numberOfExecution: {type: "number"},
                        serverInstanceId: {type: "number"},
                        pluginId: {type: "number"},
                        isReserved: {type: "boolean"}
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
            <g:if test="${oId!=null}">
            , filter: {field: "id", operator: "equal", value: ${oId}}
            </g:if>
        });
    }

    function initAppShellScriptGrid() {
        initDataSource();
        $("#appSqlScriptGrid").kendoGrid({
            dataSource: appShellScriptDataSource,
            height: getGridHeightKendo(),
            selectable: true,
            sortable: true,
            resizable: true,
            reorderable: true,
            pageable: {
                pageSizes: [10, 15, 20],
                buttonCount: 4
            },
            columns: [
                {
                    field: "name",
                    title: "Name",
                    width: 110,
                    sortable: false,
                    filterable: false,
                    template: "#= applyTxtCSS(isReserved, name) #"
                },
                {field: "transactionCode", title: "Trans. Code", width: 150, sortable: false, filterable: false},
                {field: "numberOfExecution", title: "Exe Count", width: 50, sortable: false, filterable: false},
                {
                    field: "lastExecutedOn",
                    title: "Last Executed On",
                    width: 105,
                    sortable: false,
                    filterable: false,
                    template: "#= lastExecutedOn ? getFormattedTime(lastExecutedOn):'0:0' #"
                }
            ],
            filterable: {
                mode: "row"
            },

            toolbar: kendo.template($("#gridToolbar").html())
        });
        appSqlScriptGrid = $("#appSqlScriptGrid").data("kendoGrid");
        $("#menuGrid").kendoMenu();
        initScriptDivHeight();
    }

    function getFormattedTime(time) {
        return kendo.toString(kendoParse(time), 'dd-MMM-yyyy [hh:mm:ss tt]');
    }
    function kendoParse(time) {
        return kendo.parseDate(time, 'yyyy-MM-ddTHH:mm:ss');
    }

    function applyTxtCSS(isReserved, name) {
        if (isReserved) {
            return "<b>" + name + "</b>";
        }
        return name
    }

    function initObservable() {
        appSqllScriptModel = kendo.observable(
                {
                    appShellScript: {
                        id: "",
                        version: "",
                        name: "",
                        script: "",
                        serverInstanceId: "",
                        pluginId: pluginId
                    }
                }
        );
        kendo.bind($("#application_top_panel"), appSqllScriptModel);
    }

    function initScriptDivHeight() {
        $('#evaluatedSqlScript').css('height', $('#gridContainer').height());
    }

    <%-- Load Menu and set left-menu selected  --%>
    function loadMenu(pluginId) {
        var menuId = getMenuIdByPluginId(pluginId);  // load menu
        loadNumberedMenu(menuId, "#appShellScript/showSql?plugin=" + pluginId); // set left-menu selected
    }

    function addNote() {
        if (executeCommonPreConditionForSelectKendo(appSqlScriptGrid, 'script') == false) {
            return;
        }
        showLoadingSpinner(true);
        var scriptId = getSelectedIdFromGridKendo(appSqlScriptGrid);
        var loc = "${createLink(controller: 'appNote', action: 'show')}?entityTypeId=" + entityTypeId + "&oId=" + scriptId + "&plugin=" + pluginId + "&url=appShellScript/showSql";
        router.navigate(formatLink(loc));
        return false;
    }

    function scriptReport() {
        if (executeCommonPreConditionForSelectKendo(appSqlScriptGrid, 'script') == false) {
            return;
        }
        showLoadingSpinner(true);
        var scriptId = getSelectedIdFromGridKendo(appSqlScriptGrid);
        if (confirm('Do you want to download the script report now?')) {
            var url = "${createLink(controller: 'appShellScript', action: 'downloadReport')}?id=" + scriptId;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

</script>
