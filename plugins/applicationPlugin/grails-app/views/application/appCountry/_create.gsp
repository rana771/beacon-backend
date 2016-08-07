<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridCountry'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    Create Country
                    <app:myFavourite>
                    </app:myFavourite>
                </div>
            </div>

            <g:form id="countryForm" name="countryForm" class="form-horizontal form-widgets" role="form">
                <div class="panel-body" style="display: none">
                    <g:hiddenField name="id" data-bind="value: country.id"/>
                    <g:hiddenField name="version" data-bind="value: country.version"/>
                    <div class="form-group">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="col-md-3 control-label label-required" for="name">Name:</label>

                                <div class="col-md-6">
                                    <input type="text" class="k-textbox" id="name" name="name" tabindex="1"
                                           maxlength="255" data-bind="value: country.name"
                                           placeholder="Country Name" required validationMessage="Required" autofocus/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="name"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-required" for="code">Code:</label>

                                <div class="col-md-6">
                                    <input type="text" class="k-textbox" id="code" name="code" tabindex="2"
                                           maxlength="255" data-bind="value: country.code" maxlength="3" minlength="2"
                                           placeholder="Should be Unique" required validationMessage="Required"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="code"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-required" for="isdCode">ISD Code:</label>

                                <div class="col-md-6">
                                    <input type="text" class="k-textbox" id="isdCode" name="isdCode" tabindex="3"
                                           maxlength="255" data-bind="value: country.isdCode"
                                           placeholder="ISD Code" required validationMessage="Required"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="isdCode"></span>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="col-md-3 control-label label-required"
                                       for="nationality">Nationality:</label>

                                <div class="col-md-6">
                                    <input type="text" class="k-textbox" id="nationality" name="nationality"
                                           tabindex="4" data-bind="value: country.nationality"
                                           placeholder="Should be Unique" required validationMessage="Required"
                                           maxlength="255"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="nationality"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-required" for="currencyId">Currency:</label>

                                <div class="col-md-6">
                                    <app:dropDownCurrency
                                            required="true"
                                            dataModelName="dropDownCurrency"
                                            validationMessage="Required"
                                            name="currencyId"
                                            tabindex="5"
                                            data-bind="value: country.currencyId">
                                    </app:dropDownCurrency>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="currencyId"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-required"
                                       for="phoneNumberPattern">Phone Pattern:</label>

                                <div class="col-md-6">
                                    <input type="text" class="k-textbox" id="phoneNumberPattern"
                                           name="phoneNumberPattern" maxlength="255"
                                           data-bind="value: country.phoneNumberPattern"
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

                <div class="panel-footer" style="display: none">
                    <button id="create" name="create" type="submit" data-role="button"
                            class="k-button k-button-icontext"
                            role="button" tabindex="7"
                            aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
                    </button>

                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button" tabindex="8"
                            aria-disabled="false" onclick='resetForm();'><span
                            class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </g:form>
        </div>
    </div>

    <div class="row">
        <div id="gridCountry"></div>
    </div>
</div>
