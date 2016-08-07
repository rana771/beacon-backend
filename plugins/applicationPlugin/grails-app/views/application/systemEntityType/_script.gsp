<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <li onclick="editSystemEntityType();"><i class="fa fa-book"></i>Details</li>
<sec:access url="/systemEntity/show">
    <li onclick="addSystemEntity();"><i class="fa fa-archive"></i>System Entity</li>
</sec:access>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script type="text/javascript">
    var pluginId;
    var systemEntityTypeModel, dataSource, gridSystemEntityType;

    $(document).ready(function () {
        onLoadSystemEntityTypePage()
    });

    function onLoadSystemEntityTypePage() {
        if (${isError}) {
            showError('${message}');
            return;
        }
        pluginId = ${pluginId ? pluginId : 1};
        initSystemEntityTypeGrid();
        initObservable();

        // update page title
        $('span.headingText').html('System Entity type Information');
        $('#icon_box').attr('class', 'pre-icon-header system-entity-type');
        $(document).attr('title', "MIS - System Entity type Information");
        loadMenu(pluginId);
    }

    function resetSystemEntityTypeForm() {
        initObservable();
    }

    function addSystemEntity() {
        if (executeCommonPreConditionForSelectKendo(gridSystemEntityType, 'system entity type') == false) {
            return;
        }
        showLoadingSpinner(true);
        var systemEntityTypeId = getSelectedIdFromGridKendo(gridSystemEntityType);
        var params = "?oId=" + systemEntityTypeId + "&plugin=" + pluginId +"&url=systemEntityType/show";
        var loc = "${createLink(controller:'systemEntity', action: 'show')}" + params;
        router.navigate(formatLink(loc));
        return false;
    }

    function reloadKendoGrid() {
        gridSystemEntityType.dataSource.filter([]);
    }


    function editSystemEntityType() {
        if (executeCommonPreConditionForSelectKendo(gridSystemEntityType, 'system entity type') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridSystemEntityType'));
        resetSystemEntityTypeForm();
        showLoadingSpinner(true);
        var sysEntityType = getSelectedObjectFromGridKendo(gridSystemEntityType);
        showSystemEntityType(sysEntityType);
        showLoadingSpinner(false);
    }

    function showSystemEntityType(sysEntityType) {
        systemEntityTypeModel.set('systemEntityType', sysEntityType);
    }

    <%-- Load Menu and set left-menu selected  --%>
    function loadMenu(pluginId) {
        var menuId = getMenuIdByPluginId(pluginId);  // load menu
        loadNumberedMenu(menuId, "#systemEntityType/show?plugin=" + pluginId); // set left-menu selected
    }

    function initDatasource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/systemEntityType/list",
                    data: { pluginId: pluginId},
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
                        name: {type: "string"},
                        description: {type: "string"},
                        pluginId: {type: "number"},
                        systemEntityId: {type: "number"},
                        systemEntityCount: {type: "number"},
                        companyId: {type: "number"}
                    }
                },
                parse: function (data) {
                    checkIsErrorGridKendo(data);
                    return data;
                }
            },
            sort: {field: 'name', dir: 'asc'},  // default sort
            pageSize: ${com.athena.mis.BaseService.DEFAULT_RESULT_PER_PAGE},
            serverPaging: true,
            serverFiltering: true,
            serverSorting: true
            <g:if test="${oId!=null}">
            ,filter: { field: "id", operator: "equal", value: ${oId} }
            </g:if>
        });
    }

    function initSystemEntityTypeGrid() {
        initDatasource();
        $("#gridSystemEntityType").kendoGrid({
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
                {field: "name", title: "Name", width: 200, sortable: true, filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}},
                {field: "description", title: "Description", width: 300, sortable: false, filterable: false, template: "#=trimTextForKendo(description,70)#"},
                {field: "systemEntityCount", title: "System Entity Count", width: 85, sortable: false, filterable: false, attributes: {style: setAlignRight()}}
            ],
            filterable: {mode: "row"},
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridSystemEntityType = $("#gridSystemEntityType").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function initObservable() {
        systemEntityTypeModel = kendo.observable(
                {
                    systemEntityType: {
                        id: "",
                        version: "",
                        name: "",
                        description: "",
                        pluginId: pluginId,
                        systemEntityId: "",
                        systemEntityCount: "",
                        companyId: ""
                    }
                }
        );
        kendo.bind($("#application_top_panel"), systemEntityTypeModel);
    }
</script>
