<%@ page import="com.athena.mis.PluginConnector; com.athena.mis.application.service.AppSystemEntityCacheService" %>

<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridBankBranch'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    Create Bank Branch
                    <app:myFavourite>
                    </app:myFavourite>
                    <app:historyBack
                            o_id="${oId ? oId : pId}"
                            url="${pUrl ? pUrl : url}">
                    </app:historyBack>
                </div>
            </div>

            <app:systemEntityByReserved name="hidEntityType"
                                        reservedId="${AppSystemEntityCacheService.SYS_ENTITY_USER_ENTITY_BANK_BRANCH}"
                                        typeId="${AppSystemEntityCacheService.SYS_ENTITY_TYPE_USER_ENTITY}"
                                        pluginId="${PluginConnector.PLUGIN_ID}">
            </app:systemEntityByReserved>

            <form name='bankBranchForm' id='bankBranchForm' class="form-horizontal form-widgets" role="form">
                <div class="panel-body" style="display: none">
                    <g:hiddenField name="id" value="" id="id" data-bind="value: bankBranch.id"/>
                    <g:hiddenField name="version" value="" id="version" data-bind="value: bankBranch.version"/>
                    <g:hiddenField name="bankId" value="${bankId ? bankId : 0L}" id="bankId"
                                   data-bind="value: bankBranch.bankId"/>

                    <div class="form-group">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="col-md-3 control-label label-required">Bank:</label>
                                <span id="lblBankName" class="col-md-6">${bankName ? bankName : 'None'}</span>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-required" for="districtId">District:</label>

                                <div class="col-md-6">
                                    <app:dropDownDistrict data_model_name="dropDownDistrict" name="districtId"
                                                          id="districtId"
                                                          required="true" validationmessage="Required"
                                                          bank_id="0" country_id="${countryId ? countryId : 0L}"
                                                          data-bind="value: bankBranch.districtId"
                                                          tabindex="1">
                                    </app:dropDownDistrict>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="districtId"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-required" for="name">Name:</label>

                                <div class="col-md-6">
                                    <input type="text" class="k-textbox" id="name" name="name"
                                           data-bind="value: bankBranch.name" tabindex="2"
                                           required validationMessage="Required"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="name"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-required" for="code">Code:</label>

                                <div class="col-md-6">
                                    <input type="text" class="k-textbox" id="code" name="code"
                                           data-bind="value: bankBranch.code" tabindex="3"
                                           required validationMessage="Required"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="code"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional" for="code">Routing No:</label>

                                <div class="col-md-6">
                                    <input type="text" class="k-textbox" id="routingNo" name="routingNo"
                                           data-bind="value: bankBranch.routingNo" tabindex="4"/>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-6">

                            <div class="form-group">
                                <label class="col-md-4 control-label label-optional" for="address">Address:</label>

                                <div class="col-md-8">
                                    <textarea type="text" class="k-textbox" id="address" name="address"
                                              data-bind="value: bankBranch.address" rows="4"
                                              placeholder="255 Char Max" tabindex="5"></textarea>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-4 control-label label-optional"
                                       for="isPrincipleBranch">Principle Branch:</label>

                                <div class="col-md-6">
                                    <input type="checkbox" id="isPrincipleBranch" name="isPrincipleBranch"
                                                data-bind="checked: bankBranch.isPrincipleBranch" tabindex="6"/>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-4 control-label label-optional"
                                       for="isSmeServiceCenter">SME Service Center:</label>

                                <div class="col-md-6">
                                    <input type="checkbox" id="isSmeServiceCenter" name="isSmeServiceCenter"
                                                data-bind="checked: bankBranch.isSmeServiceCenter" tabindex="7"/>
                                </div>
                            </div>

                        </div>
                    </div>
                </div>

                <div class="panel-footer" style="display: none">
                    <app:checkSystemUser isPowerUser="true">
                        <app:ifAllUrl urls="/appBankBranch/create,/appBankBranch/update">
                            <button id="create" name="create" type="submit" data-role="button"
                                    class="k-button k-button-icontext"
                                    role="button" tabindex="8"
                                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
                            </button>
                        </app:ifAllUrl>
                    </app:checkSystemUser>

                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button" tabindex="9"
                            aria-disabled="false" onclick='resetForm();'><span
                            class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="row">
        <div id="gridBankBranch"></div>
    </div>
</div>
