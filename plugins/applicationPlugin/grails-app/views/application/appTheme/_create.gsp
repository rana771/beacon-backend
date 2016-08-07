<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridTheme'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    Edit Theme Information
                    <app:myFavourite>
                    </app:myFavourite>
                </div>
            </div>

            <form id="themeForm" name="themeForm" class="form-horizontal form-widgets" role="form">
                <div class="panel-body" style="display: none">
                    <input type="hidden" id="id" name="id" data-bind="value: theme.id"/>

                    <div class="form-group">
                        <label class="col-md-1 control-label label-optional" for="key">Key:</label>

                        <div class="col-md-5"><span id="key" data-bind="text: theme.key"></span></div>
                        <label class="col-md-1 control-label label-optional" for="description">Description:</label>

                        <div class="col-md-5"><span id="description" data-bind="text: theme.description"></span></div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-1 control-label label-required" for="value">Value:</label>

                        <div class="col-md-11">
                            <textarea type="text" class="k-textbox" id="value" name="value" rows="5" maxlength="15359"
                                      data-bind="value: theme.value"
                                      placeholder="Custom CSS/Text Here... .. ." required="" tabindex="1"
                                      validationMessage="Value is Required"></textarea>
                        </div>
                    </div>
                </div>

                <div class="panel-footer" style="display: none">
                    <app:ifAllUrl urls="/appTheme/updateTheme">
                        <button id="create" name="create" type="submit" data-role="button"
                                class="k-button k-button-icontext"
                                role="button" tabindex="2"
                                aria-disabled="false"><span class="k-icon k-i-plus"></span>Update
                        </button>
                    </app:ifAllUrl>

                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button" tabindex="3"
                            aria-disabled="false" onclick='resetThemeForm();'><span
                            class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="row">
        <div id="gridTheme"></div>
    </div>
</div>


