<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading">
                <div class="panel-title">
                    Transaction Log
                    <app:myFavourite></app:myFavourite>
                    <app:historyBack
                            o_id="${oId ? oId : pId}"
                            url="${url}"
                            p_url="${pUrl}"
                            c_id="${cId}">
                    </app:historyBack>
                </div>
            </div>

            <form id='transactionLogForm' name='transactionLogForm'
                  class="form-horizontal form-widgets" role="form">
                <div class="panel-body">
                    <div class="form-group">
                        <label class="col-md-12">${entityTypeName ? entityTypeName : ""} Name: ${dataExportName ? dataExportName : ""}</label>
                    </div>
                </div>
            </form>
        </div>
    </div>

    <div class="row">
        <div id="gridTransactionLog"></div>
    </div>
</div>

