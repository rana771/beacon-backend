<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridAppUserEntity'))">
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

            <form id="appUserEntityForm" name="appUserEntityForm" class="form-horizontal form-widgets" role="form">
                <div class="panel-body" style="display: none">
                    <input type="hidden" id="id" name="id" data-bind="value: appUserEntity.id"/>
                    <input type="hidden" id="entityId" name="entityId" value="${oId ? oId : pId}"/>
                    <input type="hidden" id="entityTypeId" name="entityTypeId" value="${entityTypeId}"/>

                    <div class="form-group">
                        <label class="col-md-2 control-label">
                            <span id="lblEntityTypeName"></span></label>

                        <div class="col-md-10">
                            <span id="lblEntityName"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-2 control-label label-required" for="appUserId">User:</label>

                        <div class="col-md-3">
                            <app:dropDownAppUserEntity
                                    id="appUserId"
                                    data_model_name="dropDownUser"
                                    entity_type_id="${reservedId}"
                                    name="appUserId"
                                    entity_id="${oId ? oId : pId}"
                                    tabindex="1"
                                    required="required"
                                    validationmessage="Required"
                                    data-bind="value: appUserEntity.appUserId"
                                    url="${createLink(controller: 'appUserEntity', action: 'dropDownAppUserEntityReload')}">
                            </app:dropDownAppUserEntity>
                        </div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="appUserId"></span>
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
                            class="k-button k-button-icontext" role="button" tabindex="2"
                            aria-disabled="false" onclick='resetForm();'><span
                            class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="row">
        <div id="gridAppUserEntity"></div>
    </div>
</div>
