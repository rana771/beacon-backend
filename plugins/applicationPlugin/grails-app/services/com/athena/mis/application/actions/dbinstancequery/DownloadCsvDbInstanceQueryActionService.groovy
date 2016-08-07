package com.athena.mis.application.actions.dbinstancequery

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.dts.DataAdapterFactoryService
import com.athena.mis.application.dts.DataAdapterService
import com.athena.mis.application.entity.AppDbInstance
import com.athena.mis.application.entity.DbInstanceQuery
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppDbInstanceService
import com.athena.mis.application.service.AppNoteService
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.DbInstanceQueryService
import org.apache.commons.lang.StringEscapeUtils
import org.apache.log4j.Logger
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.safety.Whitelist

class DownloadCsvDbInstanceQueryActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String FILE_NAME = "fileName"
    private static final String OUTPUT_BYTES = "outputBytes"
    private static final String DB_INSTANCE_QUERY = "dbInstanceQuery"

    AppSystemEntityCacheService appSystemEntityCacheService
    AppDbInstanceService appDbInstanceService
    DbInstanceQueryService dbInstanceQueryService
    AppNoteService appNoteService
    DataAdapterFactoryService dataAdapterFactoryService

    /**
     * check required parameter
     * @param params
     * @return
     */
    public Map executePreCondition(Map params) {
        long dbInstanceQueryId = Long.parseLong(params.dbInstanceQueryId.toString())
        DbInstanceQuery dbInstanceQuery = dbInstanceQueryService.read(dbInstanceQueryId)
        if (!dbInstanceQuery) {
            return setError(params, ERROR_FOR_INVALID_INPUT)
        }
        params.put(DB_INSTANCE_QUERY, dbInstanceQuery)
        return params
    }

    /**
     *
     * @param result
     * @return
     */
    public Map execute(Map result) {
        try {

            DbInstanceQuery dbInstanceQuery = (DbInstanceQuery) result.get(DB_INSTANCE_QUERY)
            AppDbInstance appDbInstance = appDbInstanceService.read(dbInstanceQuery.dbInstanceId)
            SystemEntity vendor = appSystemEntityCacheService.readByReservedId(appDbInstance.reservedVendorId, appSystemEntityCacheService.SYS_ENTITY_TYPE_VENDOR, appDbInstance.companyId)
            SystemEntity entityType = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_NOTE_DB_QUERY, appSystemEntityCacheService.SYS_ENTITY_TYPE_NOTE_ENTITY, appDbInstance.companyId)
            List lstNote = appNoteService.findAllByCompanyIdAndEntityTypeIdAndEntityId(appDbInstance.companyId, entityType.id, dbInstanceQuery.id)

            String strNote = EMPTY_SPACE
            for (int i = 0; i < lstNote.size(); i++) {
                lstNote[i].note = removeHTML(lstNote[i].note)
                if (i == lstNote.size() - 1) {
                    strNote = strNote + lstNote[i].note
                } else {
                    strNote = strNote + lstNote[i].note + NEW_LINE
                }
            }

            DataAdapterService dataAdapter = dataAdapterFactoryService.createAdapter(appDbInstance)
            Map params = [
                    sql      : dbInstanceQuery.sqlQuery,
                    vendor   : vendor.key,
                    dbName   : appDbInstance.dbName,
                    note     : strNote,
                    queryName: dbInstanceQuery.name
            ]
            Map executeResult = dataAdapter.convertSqlToCsvStream(params)
            Boolean isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                return super.setError(result, executeResult.exception.toString())   // csvDataByte
            }
            String fileName = dbInstanceQuery.name + CSV_EXTENSION
            result.put(FILE_NAME, fileName)
            result.put(OUTPUT_BYTES, executeResult.csvFileBytes)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
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

    private static String removeHTML(String input) {
        // breaks multi-level of escaping, preventing &amp;lt;script&amp;gt; to be rendered as <script>
        String replace = input.replace("&amp;", "");
        // decode any encoded html, preventing &lt;script&gt; to be rendered as <script>
        String html = StringEscapeUtils.unescapeHtml(replace);
        // remove all html tags, but maintain line breaks
        String clean = Jsoup.clean(html, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
        // decode html again to convert character entities back into text
        return StringEscapeUtils.unescapeHtml(clean);
    }

}
