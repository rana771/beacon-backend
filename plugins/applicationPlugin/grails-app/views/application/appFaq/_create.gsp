<%@ page import="com.athena.mis.integration.document.DocumentPluginConnector; com.athena.mis.PluginConnector; com.athena.mis.application.service.AppSystemEntityCacheService" %>
<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridAppFaq'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>
                <div class="panel-title">
                    <span id="lblFormTitle"></span>
                    <app:myFavourite>
                    </app:myFavourite>
                    <app:historyBack
                            o_id="${oId?oId:pId}"
                            url="${url}"
                            p_url="${pUrl}"
                            c_id="${cId}">
                    </app:historyBack>
                </div>
            </div>

            <form id="appFaqForm" name='appFaqForm' class="form-horizontal form-widgets" role="form">
                <div class="panel-body" style="display: none">
                    <input type="hidden" name="id" id="id" data-bind="value: appFaq.id"/>
                    <input type="hidden" name="version" id="version" data-bind="value: appFaq.version"/>
                    <input type="hidden" name="entityTypeId" id="entityTypeId" data-bind="value: appFaq.entityTypeId"/>
                    <input type="hidden" name="entityId" id="entityId" data-bind="value: appFaq.entityId"/>
                    <input type="hidden" name="pluginId" id="pluginId"/>

                    <div class="form-group">
                        <label class="col-md-1 control-label label-optional">
                            <span id="lblEntityTypeName"></span></label>

                        <div class="col-md-11">
                            <span id="lblEntityName"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-1 control-label label-required" for="question">Question:</label>

                        <div class="col-md-11">
                            <input type="text" class="k-textbox" id="question" name="question" tabindex="1" required
                                      validationMessage="Required"
                                      data-bind="value: appFaq.question"
                                       placeholder="Unique question (Max 500 Char)" maxlength="500"/>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-1 control-label label-required"
                               for="answer">Answer:</label>

                        <div class="col-md-11">
                            <textarea type="text" class="k-textbox" id="answer" name="answer" rows="2" maxlength="5000"
                                      placeholder="5000 Char Max" tabindex="2" data-bind="value: appFaq.answer"
                                      required validationMessage="Required"></textarea>
                        </div>
                    </div>
                </div>

                <div class="panel-footer" style="display: none">
                    <button id="create" name="create" type="submit" data-role="button"
                            class="k-button k-button-icontext"
                            role="button" tabindex="3"
                            aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
                    </button>

                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button" tabindex="4"
                            aria-disabled="false" onclick='resetAppFaqForm();'><span
                            class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="row">
        <div id="gridAppFaq"></div>
    </div>
</div>