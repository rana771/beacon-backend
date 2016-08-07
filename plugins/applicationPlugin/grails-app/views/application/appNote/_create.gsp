<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridAppNote'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    <span id="lblFormTitle"></span>
                    <app:myFavourite>
                    </app:myFavourite>
                    <app:historyBack
                            o_id="${oId ? oId : pId}"
                            url="${url}"
                            p_url="${pUrl}"
                            c_id="${cId}">
                    </app:historyBack>
                </div>
            </div>

            <form id="appNoteForm" name='appNoteForm' class="form-horizontal form-widgets" role="form">
                <div class="panel-body" style="display: none">
                    <input type="hidden" name="id" id="id" data-bind="value: appNote.id"/>
                    <input type="hidden" name="version" id="version" data-bind="value: appNote.version"/>
                    <input type="hidden" name="entityTypeId" id="entityTypeId"
                           data-bind="value: appNote.entityTypeId"/>
                    <input type="hidden" name="entityId" id="entityId" data-bind="value: appNote.entityId"/>
                    <input type="hidden" name="pluginId" id="pluginId"/>

                    <div class="form-group">
                        <label class="col-md-1 control-label label-optional">
                            <span id="lblEntityTypeName"></span></label>

                        <div class="col-md-11">
                            <span id="lblEntityName"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-1 control-label label-required"
                               for="note">Note:</label>

                        <div class="col-md-11">
                            <textarea type="text" class="k-textbox" id="note" name="note" rows="2" maxlength="5000"
                                      placeholder="5000 Char Max" tabindex="1" data-bind="value: appNote.note"
                                      required validationMessage="Required"></textarea>
                        </div>
                    </div>
                </div>

                <div class="panel-footer" style="display: none">
                    <button id="create" name="create" type="submit" data-role="button"
                            class="k-button k-button-icontext"
                            role="button" tabindex="2"
                            aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
                    </button>

                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button" tabindex="3"
                            aria-disabled="false" onclick='resetAppNoteForm();'><span
                            class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="row">
        <div id="gridAppNote"></div>
    </div>
</div>