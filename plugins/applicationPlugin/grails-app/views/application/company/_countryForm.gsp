<div id="country_panel">
    <g:form id="countryFormForReseller" name="countryFormForReseller" class="form-horizontal form-widgets" role="form">
        <div class="panel-body" style="display: none;">
            <g:hiddenField name="countryId" data-bind="value: company.countryId"/>
            <g:hiddenField name="countryVersion" data-bind="value: company.countryVersion"/>
            <div class="form-group">
                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="countryName">Name:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="countryName" name="countryName" tabindex="1"
                                   maxlength="255" data-bind="value: company.countryName"
                                   placeholder="Country Name" required validationMessage="Required" autofocus/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="countryName"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="countryCode">Code:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="countryCode" name="countryCode" tabindex="2"
                                   maxlength="255" data-bind="value: company.countryCode"
                                   placeholder="Should be Unique" required validationMessage="Required"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="countryCode"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="isdCode">ISD Code:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="isdCode" name="isdCode" tabindex="3"
                                   maxlength="255" data-bind="value: company.isdCode"
                                   placeholder="Should be Unique" required validationMessage="Required"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="isdCode"></span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="nationality">Nationality:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="nationality" name="nationality"
                                   tabindex="4" data-bind="value: company.nationality"
                                   placeholder="Should be Unique" required validationMessage="Required"
                                   maxlength="255"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="nationality"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="phoneNumberPattern">Phone Pattern:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="phoneNumberPattern"
                                   name="phoneNumberPattern" maxlength="255"
                                   data-bind="value: company.phoneNumberPattern"
                                   placeholder=" ^[0-9]{11}$" required validationMessage="Required"
                                   tabindex="6"/>
                        </div>

                        <div class="col-md-3 ">
                            <span class="k-invalid-msg" data-for="phoneNumberPattern"></span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </g:form>
</div>
