<%@ page import="com.athena.mis.PluginConnector; com.athena.mis.application.service.AppSystemEntityCacheService" %>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-7" style="padding: 0 5px 0 5px;">
            <div class="container-fluid">
                <div class="row">
                    <div id="application_top_panel" class="panel panel-primary">
                        <div class="panel-heading">
                            <span class="pull-left"><i class="panel-icon fa fa-pencil-square"></i></span>
                            <div class="panel-title">
                                Compose Mail
                                <app:myFavourite>
                                </app:myFavourite>
                            </div>
                        </div>

                        <form id="appMailForm" name='appMailForm' enctype="multipart/form-data"
                              class="form-horizontal form-widgets" role="form" method="post">
                            <div class="panel-body">
                                <input type="hidden" id="id" name="id" data-bind="value: appMail.id"/>
                                <input type="hidden" id="version" name="version" data-bind="value: appMail.version"/>

                                <app:systemEntityByReserved
                                        name="entityTypeId"
                                        typeId="${AppSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT_ENTITY}"
                                        reservedId="${AppSystemEntityCacheService.SYS_ENTITY_CONTENT_ENTITY_APP_MAIL}"
                                        pluginId="${PluginConnector.PLUGIN_ID}">
                                </app:systemEntityByReserved>

                                <div class="form-group">

                                <div class="col-md-10">
                                        <input type="text" class="k-textbox" id="recipients" name="recipients"
                                               tabindex="1"
                                               maxlength="255" data-bind="value: appMail.recipients"
                                               placeholder="To"
                                               required validationMessage="Required"/>
                                    </div>

                                    <div class="col-md-2 pull-left">
                                        <span class="k-invalid-msg" data-for="recipients"></span>
                                    </div>
                                </div>

                                <div class="form-group">

                                    <div class="col-md-10">
                                        <input type="text" class="k-textbox" id="recipientsCc" name="recipientsCc"
                                               tabindex="2" placeholder="Cc"
                                               maxlength="255" data-bind="value: appMail.recipientsCc"/>
                                    </div>
                                </div>

                                <div class="form-group">

                                    <div class="col-md-10">
                                        <input type="text" class="k-textbox" id="subject" name="subject" tabindex="3"
                                               maxlength="255"
                                               data-bind="value: appMail.subject"
                                               placeholder="Subject" required validationMessage="Required"/>
                                    </div>

                                    <div class="col-md-2 pull-left">
                                        <span class="k-invalid-msg" data-for="subject"></span>
                                    </div>
                                </div>

                                <div class="form-group">

                                    <div class="col-md-12">
                                        <textarea type="text" class="k-textbox" id="body" name="body" rows="5"
                                                  maxlength="2040"
                                                  placeholder="Dear '$'{userName}, Here is the Body part....."
                                                  required=""
                                                  tabindex="4"
                                                  data-bind="value: appMail.body"
                                                  validationMessage="Required"></textarea>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-12">
                                        <span class="k-invalid-msg" data-for="body"></span>
                                    </div>
                                </div>

                                <div class="form-group">

                                    <div class="col-md-12">
                                        <input type="file" class="form-control-static" id="attachment" tabindex="5"
                                               name="attachment"/>
                                    </div>
                                </div>
                            </div>

                            <div class="panel-footer">

                                <button id="send" name="send" type="button" data-role="button"
                                        class="k-button k-button-icontext"
                                        role="button" tabindex="5" onclick="createAndSend();"
                                        aria-disabled="false"><span class="k-icon k-i-plus"></span>Send
                                </button>

                                <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                                        class="k-button k-button-icontext" role="button" tabindex="6"
                                        aria-disabled="false" onclick='resetMailForm();'><span
                                        class="k-icon k-i-close"></span>Cancel
                                </button>
                                <button id="create" name="create" type="button" data-role="button"
                                        class="k-button k-button-icontext pull-right"
                                        role="button" tabindex="5" onclick="saveMail();"
                                        aria-disabled="false"><span class="k-icon k-i-plus"></span>Save As Draft
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-md-5" style="padding: 0 5px 0 5px;">
            <div class="container-fluid">
                <div class="row">
                    <div id="gridAppMail"></div>
                </div>
            </div>
        </div>
    </div>
</div>