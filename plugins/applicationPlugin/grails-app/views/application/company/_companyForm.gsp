<div id="company_panel">
    <g:form id='companyForm' name='companyForm' enctype="multipart/form-data"
            class="form-horizontal form-widgets"
            role="form">
        <div class="panel-body" style="display: none;">
            <input type="hidden" id="id" name="id" data-bind="value: company.id"/>
            <input type="hidden" id="version" name="version" data-bind="value: company.id"/>

            <div class="form-group">
                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="name">Name:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="name" name="name" tabindex="1"
                                   maxlength="255"
                                   data-bind="value: company.name"
                                   placeholder="Company Name" required validationMessage="Required" autofocus/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="name"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="title">Title:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="title" name="title" tabindex="2"
                                   maxlength="255"
                                   data-bind="value: company.title"
                                   placeholder="Company Title" autofocus/>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="code">Code:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="code" name="code" tabindex="3"
                                   maxlength="255"
                                   data-bind="value: company.code"
                                   placeholder="Should be Unique" required validationMessage="Required"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="code"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="webUrl">Web URL:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="webUrl" name="webUrl"
                                   tabindex="5" maxlength="255"
                                   data-bind="value: company.webUrl"
                                   placeholder="Should be Unique" required data-required-msg="Required"
                                   validationMessage="Not valid"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="webUrl"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="contactName">Contact Name:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="contactName" name="contactName"
                                   data-bind="value: company.contactName"
                                   tabindex="12" required data-required-msg="Required" maxlength="255"
                                   validationMessage="Not valid"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="contactName"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="contactSurname">Contact Surname:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="contactSurname" name="contactSurname"
                                   data-bind="value: company.contactSurname"
                                   tabindex="13" required data-required-msg="Required" maxlength="255"
                                   validationMessage="Not valid"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="contactSurname"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="contactPhone">Contact Phone:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="contactPhone" name="contactPhone"
                                   data-bind="value: company.contactPhone"
                                   tabindex="14" required data-required-msg="Required" maxlength="255"
                                   validationMessage="Not valid"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="contactPhone"></span>
                        </div>
                    </div>

                </div>

                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional"
                               for="isActive">Is Active:</label>

                        <div class="col-md-6">
                            <input type="checkbox" id="isActive" tabindex="10" name="isActive" data-bind="checked: company.isActive"/>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="address1">Address 1:</label>

                        <div class="col-md-9">
                            <textarea type="text" class="k-textbox" id="address1" name="address1" rows="2"
                                      data-bind="value: company.address1"
                                      placeholder="255 Char Max" required tabindex="15" maxlength="255"
                                      validationMessage="Address 1 is Required"></textarea>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="address2">Address 2:</label>

                        <div class="col-md-9">
                            <textarea type="text" class="k-textbox" id="address2" name="address2" rows="2"
                                      data-bind="value: company.address2"
                                      placeholder="255 Char Max" tabindex="16" maxlength="255"></textarea>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </g:form>
</div>
