<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <app:ifAllUrl urls="/appTheme/updateTheme">
        <li onclick="editTheme();"><i class="fa fa-edit"></i>Edit</li>
    </app:ifAllUrl>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script type="text/javascript">
    var themeGrid, themeModel, themeDateSource, pluginId;

    $(document).ready(function () {
        pluginId = ${pluginId};
        onLoadThemePage();
        initThemeGrid();
        initObservable();
    });

    function onLoadThemePage() {
        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#themeForm"), onSubmitTheme);
        // update page title
        $(document).attr('title', "Update Theme Information");
        loadMenu(pluginId);
    }
    function loadMenu(pluginId) {
        var menuId = getMenuIdByPluginId(pluginId);  // load menu
        loadNumberedMenu(menuId, "#appTheme/showTheme?plugin=" + pluginId); // set left-menu selected
    }
    function executePreCondition() {
        if (!validateForm($("#themeForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitTheme() {
        if (executePreCondition() == false) {
            return false;
        }

        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            showError('Theme is updateable only. Please select from grid to update.');
            return false;
        } else {
            actionUrl = "${createLink(controller: 'appTheme', action: 'updateTheme')}";
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        jQuery.ajax({
            type: 'post',
            data: jQuery("#themeForm").serialize(),
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
                var newEntry = result.theme;
                var selectedRow = themeGrid.select();
                var allItems = themeGrid.items();
                var selectedIndex = allItems.index(selectedRow);
                themeGrid.removeRow(selectedRow);
                themeGrid.dataSource.insert(selectedIndex, newEntry);
                resetThemeForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetThemeForm() {
        clearForm($("#themeForm"), $('#value'));
        initObservable();
    }

    function editTheme() {
        if (executeCommonPreConditionForSelectKendo(themeGrid, 'theme') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridTheme'));
        resetThemeForm();
        showLoadingSpinner(true);
        var selectedObj = getSelectedObjectFromGridKendo(themeGrid);
        themeModel.set('theme', selectedObj);
        showLoadingSpinner(false);
    }

    function reloadKendoGrid() {
        initThemeGrid().dataSource.filter([]);
    }

    function executePostConditionForEdit(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            showTheme(data);
        }
    }

    function showTheme(data) {
        themeModel.set('theme', data.entity);
    }

    function initDataSource() {
        themeDateSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "${createLink(controller: 'appTheme', action: 'listTheme')}",
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
                        key: {type: "string"},
                        value: {type: "string"},
                        description: {type: "string"}
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

    function initThemeGrid() {
        initDataSource();
        $("#gridTheme").kendoGrid({
            dataSource: themeDateSource,
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
                    field: "key",
                    title: "Key",
                    width: 90,
                    sortable: false,
                    filterable: {cell: {operator: "contains", dataSource:getBlankDataSource()}}
                },
                {
                    field: "value", title: "Value", width: 200, sortable: false, filterable: false,
                    attributes: {style: "white-space:nowrap"}
                },
                {
                    field: "description", title: "Description", width: 120, sortable: false, filterable: false,
                    attributes: {style: "white-space:nowrap"}
                }
            ],
            filterable: {
                mode: "row"
            },
           
            toolbar: kendo.template($("#gridToolbar").html())
        });
        themeGrid = $("#gridTheme").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function initObservable() {
        themeModel = kendo.observable(
                {
                    theme: {
                        id: "",
                        version: "",
                        key: "",
                        value: "",
                        description: ""
                    }
                }
        );
        kendo.bind($("#application_top_panel"), themeModel);
    }

</script>
