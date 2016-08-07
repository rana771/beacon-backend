<%@ page import="com.athena.mis.application.service.AppSystemEntityCacheService; com.athena.mis.PluginConnector" %>

<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridAppAttachment'))">
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

            <form id="appAttachmentForm" name='appAttachmentForm'
                  class="form-horizontal form-widgets" role="form">
                <div class="panel-body" style="display: none">
                    <input type="hidden" name="id" id="id" data-bind="value: appAttachment.id"/>
                    <input type="hidden" name="version" id="version" data-bind="value: appAttachment.version"/>
                    <input type="hidden" name="entityTypeId" id="entityTypeId"
                           value="${entityTypeId ? entityTypeId : 0L}"/>
                    <input type="hidden" name="entityId" id="entityId" value="${oId ? oId : 0L}"/>

                    <div class="form-group">
                        <div class="col-md-6">
                            <div class="form-group">
                                <div class="col-md-3 control-label">
                                    <span id="lblEntityTypeName"></span>
                                </div>

                                <div class="col-md-9">
                                    <span id="lblEntityName"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-required"
                                       for="caption">Caption:</label>

                                <div class="col-md-6">
                                    <textarea type="text" class="k-textbox" id="caption" name="caption" rows="4"
                                              placeholder="255 Char Max" tabindex="1" maxlength="255"
                                              data-bind="value: appAttachment.caption"
                                              required validationMessage="Required"></textarea>
                                </div>

                                <div class="col-md-3">
                                    <span class="k-invalid-msg" data-for="caption"></span>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional"
                                       for="expirationDate">Valid Until:</label>

                                <div class="col-md-6">
                                    <app:dateControl name="expirationDate" placeholder="dd/MM/yyyy" value=""
                                                     data-bind="value: appAttachment.expirationDate" tabindex="2">
                                    </app:dateControl>
                                </div>
                            </div>
                            <div class="form-group">
                                <label id="labelAttachment" class="col-md-3 control-label label-required"
                                       for="contentObj">Attachment:</label>

                                <div class="col-md-6">
                                    <input type="file" tabindex="3" id="contentObj" name="contentObj"
                                           validationMessage="Required"/>
                                </div>

                                <div class="col-md-3">
                                    <span class="k-invalid-msg" data-for="contentObj"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="panel-footer" style="display: none">
                    <button id="create" name="create" type="button" data-role="button"
                            class="k-button k-button-icontext"
                            role="button" tabindex="4"
                            aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
                    </button>

                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button" tabindex="5"
                            aria-disabled="false" onclick='resetAppAttachmentUI();'><span
                            class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="row">
        <div id="gridAppAttachment"></div>
    </div>
</div>
