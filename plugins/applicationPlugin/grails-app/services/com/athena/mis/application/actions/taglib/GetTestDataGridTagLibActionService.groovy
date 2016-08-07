package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import org.apache.log4j.Logger

class GetTestDataGridTagLibActionService extends BaseService implements ActionServiceIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String ON_CREATE = "on_create"
    private static final String ON_DELETE = "on_delete"
    private static final String ON_SELECT = "on_select"
    private static final String GRID_MODEL = "grid_model"
    private static final String HTML = "html"

    Map executePreCondition(Map parameters) {
        try {
            if (!parameters.id || !parameters.grid_model || !parameters.on_create || !parameters.on_delete) {
                return super.setError(parameters, ERROR_FOR_INVALID_INPUT)
            }
            return parameters
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(e)
        }
    }

    Map execute(Map previousResult) {
        try {
            String html = buildTestDataGridHtml(previousResult)
            previousResult.put(HTML, html)
            return previousResult
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(e)
        }
    }

    Map executePostCondition(Map previousResult) {
        return previousResult
    }

    Map buildSuccessResultForUI(Map executeResult) {
        return executeResult
    }

    Map buildFailureResultForUI(Map executeResult) {
        return executeResult
    }

    private String buildTestDataGridHtml(Map result) {
        String id = (String) result.id
        String onCreate = (String) result.get(ON_CREATE)
        String onDelete = (String) result.get(ON_DELETE)
        String gridModel = (String) result.get(GRID_MODEL)
        String onSelect = result.get(ON_SELECT)
        String strOnSelect = EMPTY_SPACE

        if (onSelect) {
            strOnSelect = "change: ${onSelect},"
        }
        String gridToolBar = """
            <script type="text/x-kendo-template" id="gridToolbar">
                <ul id="menuGrid" class="kendoGridMenu">
                    <li onclick="${onCreate}"><i class="fa fa-edit"></i>Load Test Data</li>
                    <li onclick="${onDelete}"><i class="fa fa-trash-o"></i>Delete Test Data</li>
                </ul>
            </script>
        """

        String gridScript = """
            <script type="text/javascript">
                var dataSourceTestData;
                \$(document).ready(function () {
                    initGridTestData();
                });

                function initDataSourceTestData() {
                dataSourceTestData = new kendo.data.DataSource({
                    transport: {
                        read: {
                            url: false,
                            dataType: "json",
                            type: "post"
                        }
                    },
                    schema: {
                        type: 'json',
                        data: "list", total: "count",
                        model: {
                            fields: {
                                table_name: {type: "string"},
                                disk_usage: {type: "number"},
                                table_rows: {type: "number"}
                            }
                        },
                        parse: function (data) {
                            checkIsErrorGridKendo(data);
                            return data;
                        }
                    },
                    pageSize: 15,
                    sort: {field: 'table_name', dir: 'asc'}
                });
            }

            function initGridTestData() {
                initDataSourceTestData();
                \$("#${id}").kendoGrid({
                    dataSource: dataSourceTestData,
                    autoBind: false,
                    height: getGridHeightKendo(),
                    selectable: true,
                    sortable: true,
                    resizable: true,
                    ${strOnSelect}
                    reorderable: true,
                    pageable: {
                        refresh: false,
                        buttonCount: 2
                    },
                    columns: [
                        {field: "table_name", title: "Table Name", width: 150, sortable: true, filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}},
                        {
                            field: "disk_usage", title: "Disk Usage", width: 80, sortable: true, filterable: false,
                            template: "#= getPrettySize(disk_usage)#"
                        },
                        {field: "table_rows", title: "Estimated Rows", width: 80, sortable: true, filterable: false}
                    ],
                    filterable: {
                        mode: "row"
                    },
                    toolbar: kendo.template(\$("#gridToolbar").html())
                });
                ${gridModel} = \$("#gridTestData").data("kendoGrid");
                \$("#menuGrid").kendoMenu();
                clearGridKendo(${gridModel});
            }

            function getPrettySize(size) {
                if (size > 1000000000000) {
                    return roundFloat((size / 1000000000000), 2) + ' TB';
                }
                else if (size > 1000000000) {
                    return roundFloat((size / 1000000000), 2) + ' GB';
                }
                else if (size > 1000000) {
                    return roundFloat((size / 1000000), 2) + ' MB';
                }
                else {
                    return roundFloat((size / 1000), 2) + ' KB';
                }
            }
            </script>
        """
        String html = """
            <div id="${id}"></div>
        """

        return gridToolBar + html + gridScript
    }
}
