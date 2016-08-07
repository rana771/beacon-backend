package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import org.apache.log4j.Logger

class GetSchemaInformationGridTagLibActionService extends BaseService implements ActionServiceIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String HTML = "html"

    Map executePreCondition(Map parameters) {
        try {
            if (!parameters.id || !parameters.grid_model) {
                return super.setError(parameters, ERROR_FOR_INVALID_INPUT)
            }
            return parameters
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    Map execute(Map previousResult) {
        try {
            String html = buildSchemaInfoGrid(previousResult)
            previousResult.put(HTML, html)
            return previousResult
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
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

    private String buildSchemaInfoGrid(Map result) {
        String id = result.id
        String gridModel = result.grid_model
        String gridScript = """
            <script type = "text/javascript">
            var dataSourceMetaData, ${gridModel};
            \$(document).ready(function () {
                initGridMetaData();
            });

            function initGridMetaData() {
                initDataSourceMetaData();
                \$("#${id}").kendoGrid({
                    dataSource: dataSourceMetaData,
                    autoBind: false,
                    height: getGridHeightKendo(),
                    selectable: true,
                    sortable: true,
                    resizable: true,
                    reorderable: true,
                    pageable: {
                        refresh: false,
                        buttonCount: 2
                    },
                    columns: [
                        {
                            field: "column_name", title: "Column Name", width: 150, sortable: true, filterable: false,
                            template: "#= concatColumnNameWithColumnKey(column_name, column_key) #"
                        },
                        {
                            field: "data_type", title: "Data Type", width: 150, sortable: true, filterable: false,
                            template: "#= concatDataTypeWithLength(data_type, length) #"
                        },
                        {field: "is_nullable", title: "Nullable", width: 60, sortable: true, filterable: false}
                    ],
                    filterable: {
                        mode: "row"
                    }
                });
                ${gridModel} = \$("#gridMetaData").data("kendoGrid");
                clearGridKendo(gridMetaData);
            }

            function concatColumnNameWithColumnKey(columnName, columnKey) {
                if (columnKey && columnKey != '0') {
                    return columnName + ' ( ' + columnKey + ' )';
                }
                return columnName;
            }

            function concatDataTypeWithLength(dataType, length) {
                if (length == '0') {
                    return dataType;
                }
                return dataType + ' ( ' + length + ' )';
            }

            function initDataSourceMetaData() {
                dataSourceMetaData = new kendo.data.DataSource({
                    transport: {
                        read: {
                            url: "/schemaInformation/listSchemaInfo",
                            dataType: "json",
                            type: "post"
                        }
                    },
                    schema: {
                        type: 'json',
                        data: "list", total: "count",
                        model: {
                            fields: {
                                column_name: {type: "string"},
                                data_type: {type: "string"},
                                is_nullable: {type: "string"},
                                length: {type: "number"},
                                column_key: {type: "string"}
                            }
                        },
                        parse: function (data) {
                            checkIsErrorGridKendo(data);
                            return data;
                        }
                    },
                    pageSize: 15
                });
            }
            </script>
        """

        String html = """
            <div id=${id}></div>
        """
        return html + gridScript
    }
}
