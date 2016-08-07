package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import grails.transaction.Transactional
import org.apache.log4j.Logger

/**
 * build html of entity notes
 * for details go through use-case "GetAppNoteTagLibActionService"
 */
class GetAppNoteTagLibActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String ID = "id"
    private static final String ENTITY_ID = "entity_id"
    private static final String TITLE = "title"
    private static final String DATA_SOURCE = "datasource"
    private static final String ENTITY_TYPE_ID = "entity_type_id"
    private static final String SORT_ORDER = "sort_order"
    private static final String TEMPLATE = "template"
    private static final String RESULT_PER_PAGE = "result_per_page"
    private static final String DEFAULT_TEMPLATE = "<blockquote style='padding: 0 20px;'><span>#=htmlDecode(note)#</span><footer>By #:createdBy# On #:createdOn#</footer></blockquote>"
    private static final String DEFAULT_ORDER = "desc"
    private static final Integer DEFAULT_RP = 5
    private static final String URL = "url"
    private static final String DIV_END = "</div>"

    /**
     * check if propertyName and propertyValue exists
     * @param parameters - attr of tagLib
     * @return - Map containing attr
     */
    public Map executePreCondition(Map params) {
        try {
            Long entityTypeId = Long.valueOf(params.get(ENTITY_TYPE_ID).toString())
            Long entityId = Long.parseLong(params.get(ENTITY_ID).toString())
            if(!entityId || entityId == 0) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }

            String url = params.get(URL) ? params.get(URL) : EMPTY_SPACE
            Integer resultPerPage = params.get(RESULT_PER_PAGE) ? Integer.valueOf(params.get(RESULT_PER_PAGE).toString()) : DEFAULT_RP
            String sortOrder = params.get(SORT_ORDER) ? params.get(SORT_ORDER) : DEFAULT_ORDER
            String template = DEFAULT_TEMPLATE

            params.put(ENTITY_TYPE_ID, entityTypeId)
            params.put(ENTITY_ID, entityId)
            params.put(URL, url)
            params.put(SORT_ORDER, sortOrder)
            params.put(TEMPLATE, template)
            params.put(RESULT_PER_PAGE, resultPerPage)

            return params

        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * build AppNote html
     * @param result - a map returned from precondition method
     * @return - a map consisting desired html
     */
    @Transactional(readOnly = true)
    public Map execute(Map result) {
        try {
            String html = buildAppNoteHtml(result)
            result.html = html
            return result
        } catch (Exception e) {
            log.error(e.message)
            throw new RuntimeException(e)
        }
    }

    /**
     * Do nothing in post condition
     * @param result - A map returned by execute method
     * @return - returned the received map
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * do nothing for build success operation
     * @param result - A map returned by post condition method.
     * @return - returned the same received map containing isError = false
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    /**
     * Do nothing here
     * @param result - map returned from previous any of method
     * @return - a map containing isError = true & relevant error message
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * build app note html
     * @return - html
     */
    private String buildAppNoteHtml(Map attrs) {
        String strId = attrs.get(ID)
        String url = attrs.get(URL)
        String title = attrs.get(TITLE)
        long entityId = Long.parseLong(attrs.get(ENTITY_ID).toString())
        long entityTypeId = Long.parseLong(attrs.get(ENTITY_TYPE_ID).toString())
        String sortOrder = attrs.get(SORT_ORDER)
        String template = attrs.get(TEMPLATE)
        String dataSource = attrs.get(DATA_SOURCE)
        int resultPerPage = (int) attrs.get(RESULT_PER_PAGE)
        boolean hidePager = false
        if (attrs.hide_pager) {
            hidePager = true
        }
        String pagerDiv = "pagerNote"
        if (attrs.pager_id) {
            pagerDiv = attrs.pager_id
        }

        String strUrl = "url = '${url}'"
        String strEntityId = "entity_id = '${entityId}'"
        String strEntityTypeId = "entity_type_id = '${entityTypeId}'"
        String strOrder = "sort_order = '${sortOrder}'"
        String strTitle = title ? "title = '${title}'" : EMPTY_SPACE
        String strRp = "result_per_page = '${resultPerPage}'"
        String strTemplate = """ template="${template}" """
        String divTitle = title ? "<div id='listViewTitlePanel' class='panel panel-primary'><div class='panel-heading'> <span class='pull-left'><i class='panel-icon fa fa-list'></i></span><div class='panel-title'>${title}</div></div><div class='panel-body'> " : EMPTY_SPACE


        String noteHtml = """
        ${divTitle}
            <div style='border-width:0; overflow: auto' id="${strId}" ${strUrl} ${strEntityId}
                 ${strEntityTypeId} ${strTemplate} ${strOrder} ${strRp} ${strTitle}>
            </div>
            <div id="${pagerDiv}"></div>
        ${title ? DIV_END : EMPTY_SPACE}
        ${title ? DIV_END : EMPTY_SPACE}
        """

        String strDataSource = """
            new kendo.data.DataSource({
                transport: {
                    read: {
                        url: "/appNote/listEntityNote?entityTypeId=${entityTypeId}&entityId=${entityId}&sortOrder=${sortOrder}",
                        dataType: "json",
                        type: "post"
                    }
                },
                schema: {
                    type: 'json',
                    data: "list", total: "count",
                    model: {
                        fields: {
                            note: {type: "string"},
                            createdBy: {type: "string"},
                            createdOn: {type: "string"}
                        }
                    },
                    parse: function (data) {
                        checkDataSize(data);
                        return data;
                    }
                },
                pageSize: ${resultPerPage},
                serverPaging: true
            });
        """

        String noteScript = """
        <script type='text/javascript'>
            \$(document).ready(function() {
                ${dataSource} = ${strDataSource}
                \$("#${strId}").kendoListView({
                    dataSource: ${dataSource},
                    dataBound: function(e) {
                        if(this.dataSource.data().length == 0){
                            \$("#${strId}").append("Add your comment...");
                        }
                    },
                    template: "${template}"
                });

                \$('#${pagerDiv}').kendoPager({dataSource: ${dataSource}, refresh: true});

                var height = \$('#${title? "listViewTitlePanel" : strId}').parent().css('max-height');
                \$('#${strId}').css('height', height);
            });

            function checkDataSize(data) {
                if (${hidePager}) {
                    if (data.count == 0) {
                        \$('#${pagerDiv}').hide();
                    }
                }
            }
        </script>
        """
        return noteHtml + noteScript
    }

}
