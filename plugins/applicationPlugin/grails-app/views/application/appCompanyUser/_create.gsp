<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridAppCompanyUser'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    Create Company User
                    <app:myFavourite>
                    </app:myFavourite>
                </div>
            </div>

            <g:form name='companyUserForm' id="companyUserForm" enctype="multipart/form-data"
                    class="form-horizontal form-widgets" role="form">
                <div class="panel-body" style="display: none">
                    <g:hiddenField name="id" data-bind="value: appUser.id"/>
                    <g:hiddenField name="version" data-bind="value: appUser.version"/>
                    <g:hiddenField name="existingPass" data-bind="value: appUser.password"/>

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
                                    <input type="text" class="k-textbox" id="username" name="username" tabindex="2"
                                           placeholder="User Name" required validationMessage="Required" maxlength="255"
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
                                           placeholder="Letters,Numbers & Special Characters" required tabindex="3"
                                           data-required-msg="Required"
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
                                           tabindex="4"
                                           pattern="^.*(?=.{8,})(((?=.*[a-z])(?=.*[A-Z])(?=.*[\d]))|((?=.*[a-z])(?=.*[A-Z])(?=.*[\W]))|((?=.*[a-z])(?=.*[\d])(?=.*[\W]))|((?=.*[A-Z])(?=.*[\d])(?=.*[\W]))).*$"
                                           placeholder="Confirm password" required data-required-msg="Required"
                                           validationMessage="Invalid Combination or Length"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="confirmPassword"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional"
                                       for="cellNumber">Cell Number:</label>

                                <div class="col-md-6">
                                    <input type="tel" class="k-textbox" id="cellNumber" name="cellNumber"
                                           pattern="\d{11}"
                                           placeholder="Mobile Number" validationMessage="Invalid phone No."
                                           tabindex="5"
                                           data-bind="value: appUser.cellNumber"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="cellNumber"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional" for="ipAddress">IP Address:</label>

                                <div class="col-md-6">
                                    <input type="text" class="k-textbox" id="ipAddress" name="ipAddress" tabindex="6"
                                           pattern="((^|\.)((25[0-5])|(2[0-4]\d)|(1\d\d)|([1-9]?\d))){4}$"
                                           placeholder="IP Address" validationMessage="Invalid IP"
                                           data-bind="value: appUser.ipAddress"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="ipAddress"></span>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="col-md-3 control-label label-required"
                                       for="companyId">Company:</label>

                                <div class="col-md-6">
                                    <app:dropDownCompany
                                            required="true"
                                            tabindex="7"
                                            validationMessage="Required"
                                            dataModelName="dropDownCompany"
                                            name="companyId"
                                            data-bind="value: appUser.appUserCompanyId">
                                    </app:dropDownCompany>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="companyId"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional"
                                       for="enabled">Enabled:</label>

                                <div class="col-md-6">
                                    <input type="checkbox" id="enabled" tabindex="8" name="enabled"
                                                data-bind="checked: appUser.enabled"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="enabled"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional"
                                       for="accountLocked">Account Locked:</label>

                                <div class="col-md-6">
                                    <input type="checkbox" id="accountLocked" tabindex="9" name="accountLocked"
                                                data-bind="checked: appUser.accountLocked"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="accountLocked"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional"
                                       for="accountExpired">Account Expired:</label>

                                <div class="col-md-6">
                                    <input type="checkbox" id="accountExpired" tabindex="10" name="accountExpired"
                                                data-bind="checked: appUser.accountExpired"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="accountExpired"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional"
                                       for="signatureImage">Signature Image:</label>

                                <div class="col-md-6">
                                    <input type="file" tabindex="11" class="form-control-static" id="signatureImage"
                                           name="signatureImage"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="signatureImage"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="panel-footer" style="display: none">
                    <app:ifAllUrl urls="/appUser/createForCompanyUser,/appUser/updateForCompanyUser">
                        <button id="create" name="create" type="submit" data-role="button"
                                class="k-button k-button-icontext"
                                role="button" tabindex="12"
                                aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
                        </button>
                    </app:ifAllUrl>

                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button" tabindex="13"
                            aria-disabled="false" onclick='resetCompanyUserForm();'><span
                            class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </g:form>
        </div>
    </div>

    <div class="row">
        <div id="gridAppCompanyUser"></div>
    </div>
</div>


