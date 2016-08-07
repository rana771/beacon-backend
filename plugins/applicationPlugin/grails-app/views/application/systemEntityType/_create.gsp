<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridSystemEntityType'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    About System Entity Type
                    <app:myFavourite>
                    </app:myFavourite>
                </div>
            </div>

            <g:form name='systemEntityTypeForm' class="form-horizontal form-widgets" role="form">
                <div class="panel-body" style="display: none">

                    <div class="form-group">
                        <label class="col-md-1 control-label label-optional" for="name">Name:</label>

                        <div class="col-md-10"><span id="name" data-bind="text: systemEntityType.name"></span></div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-1 control-label label-optional" for="description">Description:</label>

                        <div class="col-md-10"><span id="description"
                                                     data-bind="text: systemEntityType.description"></span></div>
                    </div>
                </div>

                <div class="panel-footer" style="display: none">
                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button"
                            aria-disabled="false" onclick='resetSystemEntityTypeForm();'><span
                            class="k-icon k-i-funnel-clear"></span>Clear
                    </button>
                </div>
            </g:form>
        </div>
    </div>

    <div class="row">
        <div id="gridSystemEntityType"></div>
    </div>
</div>