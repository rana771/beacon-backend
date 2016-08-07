<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading">
                <span class="pull-left"><i class="panel-icon fa fa-history"></i></span>
                <div class="panel-title">
                    Search Release History
                    <app:myFavourite>
                    </app:myFavourite>
                </div>
            </div>

            <form id="versionForm" name="versionForm" class="form-horizontal form-widgets" role="form">
                <div class="panel-body">
                    <div class="form-group">
                        <label class="col-md-1 control-label label-required" for="pluginId">Plugin:</label>

                        <div class="col-md-3">
                            <app:dropDownModules
                                    data_model_name="dropDownModule"
                                    name="pluginId"
                                    id="pluginId"
                                    required="true"
                                    validationmessage="Required"
                                    tabindex="1"
                                    show_version="true">
                            </app:dropDownModules>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="pluginId"></span>
                        </div>
                    </div>
                </div>

                <div class="panel-footer">
                    <app:ifAllUrl urls="/appVersion/list">
                        <button id="create" name="create" type="submit" data-role="button"
                                class="k-button k-button-icontext" role="button" tabindex="2"
                                aria-disabled="false"><span class="k-icon k-i-search"></span>Search
                        </button>
                    </app:ifAllUrl>
                </div>
            </form>
        </div>
    </div>

    <div class="row">
        <div id="gridReleaseHistory"></div>
    </div>
</div>