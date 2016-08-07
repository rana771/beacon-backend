<%@ page import="com.athena.mis.application.config.AppSysConfigCacheService; com.athena.mis.PluginConnector" %>

<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridAppUser'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    Update User
                    <app:myFavourite>
                    </app:myFavourite>
                </div>
            </div>

            <g:form name='userForm' id='userForm' enctype="multipart/form-data" class="form-horizontal form-widgets"
                    role="form">
                <div class="panel-body" style="display: none">
                    <input type="hidden" name="id" id="id" data-bind="value: appUser.id"/>
                    <input type="hidden" name="version" id="version" data-bind="value: appUser.version"/>

                    <div class="form-group">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="col-md-3 control-label label-required" for="loginId">Login ID:</label>

                                <div class="col-md-6">
                                    <input type="text" class="k-textbox" id="loginId" name="loginId" maxlength="255"
                                           placeholder="Unique Login ID (email)" required
                                           validationMessage="Required"
                                           data-bind="value: appUser.loginId" tabindex="1"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="loginId"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-required" for="username">User Name:</label>

                                <div class="col-md-6">
                                    <input type="text" class="k-textbox" id="username" name="username" maxlength="255"
                                           placeholder="User Name" required validationMessage="Required" tabindex="2"
                                           data-bind="value: appUser.username"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="username"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional"
                                       for="password">Password:</label>

                                <div class="col-md-6">
                                    <input type="password" class="k-textbox" id="password" name="password"
                                           pattern="^.*(?=.{8,})(((?=.*[a-z])(?=.*[A-Z])(?=.*[\d]))|((?=.*[a-z])(?=.*[A-Z])(?=.*[\W]))|((?=.*[a-z])(?=.*[\d])(?=.*[\W]))|((?=.*[A-Z])(?=.*[\d])(?=.*[\W]))).*$"
                                           placeholder="Letters,Numbers & Special Characters"
                                           tabindex="3" validationMessage="Invalid Combination or Length"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="password"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional"
                                       for="confirmPassword">Confirm Password:</label>

                                <div class="col-md-6">
                                    <input type="password" class="k-textbox" id="confirmPassword" name="confirmPassword"
                                           pattern="^.*(?=.{8,})(((?=.*[a-z])(?=.*[A-Z])(?=.*[\d]))|((?=.*[a-z])(?=.*[A-Z])(?=.*[\W]))|((?=.*[a-z])(?=.*[\d])(?=.*[\W]))|((?=.*[A-Z])(?=.*[\d])(?=.*[\W]))).*$"
                                           placeholder="Confirm password" tabindex="4"
                                           validationMessage="Invalid Combination or Length"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="confirmPassword"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional"
                                       for="enabled">Enabled:</label>

                                <div class="col-md-6">
                                    <input type="checkbox" id="enabled" name="enabled" tabindex="5"
                                                data-bind="checked: appUser.enabled"/>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional"
                                       for="accountLocked">Account Locked:</label>

                                <div class="col-md-6">
                                    <input type="checkbox" id="accountLocked" name="accountLocked" tabindex="6"
                                                data-bind="checked: appUser.accountLocked"/>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional"
                                       for="isSwitchable">Switchable:</label>

                                <div class="col-md-6">
                                    <input type="checkbox" id="isSwitchable" name="isSwitchable" tabindex="7"
                                           data-bind="checked: appUser.isSwitchable"/>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional"
                                       for="cellNumber">Cell Number:</label>

                                <div class="col-md-6">
                                    <input type="tel" class="k-textbox" id="cellNumber" name="cellNumber"
                                           pattern="<app:showSysConfig
                                                   key="${AppSysConfigCacheService.APPLICATION_PHONE_PATTERN}"
                                                   pluginId="${PluginConnector.PLUGIN_ID}">
                                           </app:showSysConfig>" tabindex="8"
                                           placeholder="Mobile Number" validationMessage="Invalid phone No."
                                           data-bind="value: appUser.cellNumber"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="cellNumber"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional" for="ipAddress">IP Address:</label>

                                <div class="col-md-6">
                                    <input type="text" class="k-textbox" id="ipAddress" name="ipAddress"
                                           pattern="((^|\.)((25[0-5])|(2[0-4]\d)|(1\d\d)|([1-9]?\d))){4}$"
                                           placeholder="IP Address" validationMessage="Invalid IP"
                                           data-bind="value: appUser.ipAddress" tabindex="9"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="ipAddress"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional"
                                       for="isPowerUser">Power User:</label>

                                <div class="col-md-6">
                                    <input type="checkbox" id="isPowerUser" name="isPowerUser" tabindex="10"
                                           data-bind="checked: appUser.isPowerUser"/>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional"
                                       for="isConfigManager">Config Manager:</label>

                                <div class="col-md-6">
                                    <input type="checkbox" id="isConfigManager" name="isConfigManager" tabindex="11"
                                           data-bind="checked: appUser.isConfigManager"/>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional"
                                       for="isDisablePasswordExpiration">Disable Password Expiration:</label>

                                <div class="col-md-6">
                                    <input type="checkbox" id="isDisablePasswordExpiration" name="isDisablePasswordExpiration"
                                           data-bind="checked: appUser.isDisablePasswordExpiration"
                                           tabindex="12"/>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional"
                                       for="accountExpired">Account Expired:</label>

                                <div class="col-md-6">
                                    <input type="checkbox" id="accountExpired" name="accountExpired" tabindex="13"
                                           data-bind="checked: appUser.accountExpired"/>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional"
                                       for="isReserved">Reserved:</label>

                                <div class="col-md-6">
                                    <input type="checkbox" id="isReserved" name="isReserved" tabindex="14"
                                           data-bind="checked: appUser.isReserved"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="panel-footer" style="display: none">
                    <app:ifAllUrl urls="/appUser/updateAllUser">
                        <button id="create" name="create" type="submit" data-role="button"
                                class="k-button k-button-icontext"
                                role="button" tabindex="15"
                                aria-disabled="false"><span class="k-icon k-i-plus"></span>Update
                        </button>
                    </app:ifAllUrl>

                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button" tabindex="16"
                            aria-disabled="false" onclick='resetAppUserForm();'><span
                            class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </g:form>
        </div>
    </div>

    <div class="row">
        <div id="gridAppUser"></div>
    </div>
</div>