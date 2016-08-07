<%@ page import="com.athena.mis.application.service.AppSystemEntityCacheService" %>
<g:render template="/application/dbInstanceQuery/scriptDbInstanceQueryResult"/>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">SQL Result Set
        <app:historyBack
                o_id="${oId?oId:pId}"
                url="${url}"
                p_url="${pUrl}"
                c_id="${cId}">
        </app:historyBack>
        </div>
    </div>

    <div class="panel-body">
        <div class="form-group">
            <label class="col-md-2 control-label label-optional" style="text-align: right">Query Name:</label>

            <div class="col-md-10">
                <span id="lblQueryName">${dbInstanceQuery ? dbInstanceQuery.name : ''}</span>
            </div>
        </div>

        <div class="form-group">
            <label class="col-md-2 control-label label-optional" style="text-align: right">Database Name:</label>

            <div class="col-md-4">
                <span id="lblDbName">${databaseName ? databaseName : ''}</span>
            </div>

            <label class="col-md-1 control-label label-optional" style="text-align: right">Vendor:</label>

            <div class="col-md-5">
                <span id="lblVendor">${vendor ? vendor : ''}</span>
            </div>
        </div>

        <div class="form-group">
            <label class="col-md-2 control-label label-optional" style="text-align: right">Query:</label>

            <div class="col-md-10">
                <span id="lblQuery">${dbInstanceQuery ? dbInstanceQuery.sqlQuery : ''}</span>
            </div>
        </div>
    </div>
</div>

<app:ifAllUrl urls="/appNote/listEntityNote">
    <div id="lstNotes" class="panel panel-default" style="display: none;">
        <div class="panel-heading">Notes</div>

        <div class="panel-body">
            <app:appNote
                    datasource="dataSourceNote"
                    id="lstBacklogNotes"
                    entity_type_id="${AppSystemEntityCacheService.SYS_ENTITY_NOTE_DB_QUERY}"
                    entity_id="${dbInstanceQuery?.id}"
                    result_per_page="1">
            </app:appNote>
        </div>
    </div>
</app:ifAllUrl>

<div id="gridDocSqlResult"></div>
