<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">

        <div class="panel-title">
            <span>Create Your Account</span>
            <span class="pull-right">
                <a href="/"><i class="fa fa-home"></i> Homepage</a>
            </span>
        </div>

    </div>

    <form name="appUserForm" id="appUserForm" action="${createLink(controller: 'appUser', action: 'register')}"
          method="post" class="form-horizontal form-widgets" role="form">
        <input type="hidden" id="companyId" name="companyId" value="${company?.id}" />
        <div class="panel-body">
            <div class="col-md-12">
                %{--<div class="form-group">--}%
                    %{--<label class="col-md-4 control-label label-optional">Company Name:</label>--}%

                    %{--<div class="col-md-8">--}%
                        %{--<span id="companyName" name="companyName">${company?.name}</span>--}%
                    %{--</div>--}%

                %{--</div>--}%

                <div class="form-group">
                    <label class="col-md-4 control-label label-required" for="username">Full Name:</label>

                    <div class="col-md-5">
                        <input type="text" class="k-textbox" id="username" name="username" tabindex="1"
                               value="${username}" required validationMessage="Required"/>
                    </div>

                    <div class="col-md-2 pull-left">
                        <span class="k-invalid-msg" data-for="username"></span>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-md-4 control-label label-optional" for="cellNumber">Contact:</label>

                    <div class="col-md-5">
                        <input type="text" class="k-textbox" id="cellNumber" name="cellNumber" tabindex="2"
                               value="${cellNumber}" />
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-md-4 control-label label-required" for="loginId">Login ID(Email):</label>

                    <div class="col-md-5">
                        <input type="text" class="k-textbox" id="loginId" name="loginId" tabindex="12"
                               value="${loginId}" required validationMessage="Required" placeholder="example@domain.com"/>
                    </div>

                    <div class="col-md-2 pull-left">
                        <span class="k-invalid-msg" data-for="loginId"></span>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-md-4 control-label label-optional" for="confirmPassword">Password Hints:</label>
                    <span class="col-md-8">Min 8 characters and combination of letters, numbers and special characters.</span>
                </div>

                <div class="form-group">
                    <label class="col-md-4 control-label label-required" for="password">Password:</label>

                    <div class="col-md-5">
                        <input type="password" class="k-textbox" id="password" name="password" tabindex="13"
                               pattern="^.*(?=.{8,})(((?=.*[a-z])(?=.*[A-Z])(?=.*[\d]))|((?=.*[a-z])(?=.*[A-Z])(?=.*[\W]))|((?=.*[a-z])(?=.*[\d])(?=.*[\W]))|((?=.*[A-Z])(?=.*[\d])(?=.*[\W]))).*$"
                               required data-required-msg="Required" validationMessage="Invalid password"/>
                    </div>

                    <div class="col-md-2 pull-left">
                        <span class="k-invalid-msg" data-for="password"></span>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-md-4 control-label label-required" for="confirmPassword">Confirm Password:</label>

                    <div class="col-md-5">
                        <input type="password" class="k-textbox" id="confirmPassword" name="confirmPassword"
                               tabindex="14"
                               pattern="^.*(?=.{8,})(((?=.*[a-z])(?=.*[A-Z])(?=.*[\d]))|((?=.*[a-z])(?=.*[A-Z])(?=.*[\W]))|((?=.*[a-z])(?=.*[\d])(?=.*[\W]))|((?=.*[A-Z])(?=.*[\d])(?=.*[\W]))).*$"
                               required data-required-msg="Required" validationMessage="Invalid password"/>
                    </div>

                    <div class="col-md-2 pull-left">
                        <span class="k-invalid-msg" data-for="confirmPassword"></span>
                        <span id='retypePassError' class="k-widget k-tooltip k-tooltip-validation"
                              data-for="confirmPassword" role="alert">
                            <span class="k-icon k-warning"> </span> Password mismatch</span>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-md-4 control-label label-optional" for="image">Security Check:</label>

                    <div class="col-md-5">
                        <jcaptcha:jpeg style=" border: 1px solid #B6C7D8; margin-left: 4px; width: 140px;"
                                       name="image"/>
                    </div>

                </div>

                <div class="form-group">
                    <label class="col-md-4 control-label label-required" for="captcha">Type Security Text:</label>

                    <div class="col-md-5">
                        <input type="text" class="k-textbox" id="captcha" name="captcha" tabindex="29"
                               required validationMessage="Required"/>
                    </div>

                    <div class="col-md-2 pull-left">
                        <span class="k-invalid-msg" data-for="captcha"></span>
                    </div>
                </div>

            </div>

        </div>

        <div class="panel-footer">

            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="30"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Sign Up
            </button>
            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="31"
                    aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Reset
            </button>
        </div>
    </form>
</div>