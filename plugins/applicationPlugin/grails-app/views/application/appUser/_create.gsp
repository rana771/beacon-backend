<%@ page import="com.athena.mis.application.service.AppSystemEntityCacheService; com.athena.mis.PluginConnector; com.athena.mis.application.config.AppSysConfigCacheService" %>

<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridAppUser'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    Create User
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
                                           placeholder="Unique Login ID (email)" required data-required-msg="Required"
                                           validationMessage="Required" data-bind="value: appUser.loginId"
                                           tabindex="1"/>
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
                                <label id="lblPassword" class="col-md-3 control-label label-required"
                                       for="password">Password:</label>

                                <div class="col-md-6">
                                    <input type="password" class="k-textbox" id="password" name="password"
                                           pattern="^.*(?=.{8,})(((?=.*[a-z])(?=.*[A-Z])(?=.*[\d]))|((?=.*[a-z])(?=.*[A-Z])(?=.*[\W]))|((?=.*[a-z])(?=.*[\d])(?=.*[\W]))|((?=.*[A-Z])(?=.*[\d])(?=.*[\W]))).*$"
                                           placeholder="Letters,Numbers & Special Characters" required
                                           data-required-msg="Required" tabindex="3"
                                           validationMessage="Invalid Combination or Length"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="password"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label id="lblConfirmPassword" class="col-md-3 control-label label-required"
                                       for="confirmPassword">Confirm Password:</label>

                                <div class="col-md-6">
                                    <input type="password" class="k-textbox" id="confirmPassword" name="confirmPassword"
                                           pattern="^.*(?=.{8,})(((?=.*[a-z])(?=.*[A-Z])(?=.*[\d]))|((?=.*[a-z])(?=.*[A-Z])(?=.*[\W]))|((?=.*[a-z])(?=.*[\d])(?=.*[\W]))|((?=.*[A-Z])(?=.*[\d])(?=.*[\W]))).*$"
                                           placeholder="Confirm password" required data-required-msg="Required"
                                           validationMessage="Invalid Combination or Length" tabindex="4"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="confirmPassword"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label id="lblEmail" class="col-md-3 control-label"
                                       for="email">Email:</label>

                                <div class="col-md-6">
                                    <input type="email" class="k-textbox" id="email" name="email" maxlength="255"
                                           placeholder="Email" tabindex="5" validationMessage="Invalid email"
                                           data-bind="value: appUser.email"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="email"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional"
                                       for="enabled">Enabled:</label>

                                <div class="col-md-6">
                                    <input type="checkbox" id="enabled" name="enabled" tabindex="6"
                                           data-bind="checked: appUser.enabled"/>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional"
                                       for="accountLocked">Account Locked:</label>

                                <div class="col-md-6">
                                    <input type="checkbox" id="accountLocked" name="accountLocked" tabindex="7"
                                           data-bind="checked: appUser.accountLocked"/>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional"
                                       for="accountExpired">Account Expired:</label>

                                <div class="col-md-6">
                                    <input type="checkbox" id="accountExpired" name="accountExpired" tabindex="8"
                                           data-bind="checked: appUser.accountExpired"/>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional"
                                       for="employeeId">Employee:</label>

                                <div class="col-md-6">
                                    <app:dropDownEmployee
                                            name="employeeId"
                                            dataModelName="dropDownEmployee"
                                            data-bind="value: appUser.employeeId"
                                            tabindex="9">
                                    </app:dropDownEmployee>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional"
                                       for="cellNumber">Cell Number:</label>

                                <div class="col-md-6">
                                    <input type="tel" class="k-textbox" id="cellNumber" name="cellNumber"
                                           pattern="<app:showSysConfig
                                                   key="${AppSysConfigCacheService.APPLICATION_PHONE_PATTERN}"
                                                   pluginId="${PluginConnector.PLUGIN_ID}">
                                           </app:showSysConfig>" tabindex="10"
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
                                           data-bind="value: appUser.ipAddress" tabindex="11"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="ipAddress"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional"
                                       for="signatureImage">Signature Image:</label>

                                <div class="col-md-6">
                                    <input type="file" class="form-control-static" id="signatureImage" tabindex="12"
                                           name="signatureImage"/>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional">Gender:</label>

                                <div class="col-md-6">
                                    <app:dropDownSystemEntity dataModelName="dropDownGender"
                                                              pluginId="${PluginConnector.PLUGIN_ID}" name="genderId"
                                                              typeId="${AppSystemEntityCacheService.SYS_ENTITY_TYPE_GENDER}"
                                                              tabindex="13">
                                    </app:dropDownSystemEntity>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="genderId"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional"
                                       for="isDisablePasswordExpiration">Disable Password Expiration:</label>

                                <div class="col-md-6">
                                    <input type="checkbox" id="isDisablePasswordExpiration"
                                           name="isDisablePasswordExpiration"
                                           tabindex="14"
                                           data-bind="checked: appUser.isDisablePasswordExpiration"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="panel-footer" style="display: none">
                    <app:ifAllUrl urls="/appUser/create,/appUser/update">
                        <button id="create" name="create" type="submit" data-role="button"
                                class="k-button k-button-icontext"
                                role="button" tabindex="15"
                                aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
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

