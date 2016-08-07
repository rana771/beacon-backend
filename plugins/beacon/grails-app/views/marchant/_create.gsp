<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridMarchant'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    Save Change Merchant
                    <app:myFavourite>
                    </app:myFavourite>
                </div>
            </div>

            <form id="marchantForm" name="marchantForm" class="form-horizontal form-widgets" role="form">
                <div class="panel-body">
                    <input type="hidden" name="id" id="id" value="${marchant?.id}"/>
                    <input type="hidden" name="version" id="version" value="${marchant?.version}"/>

                    <div class="form-group">
                        <label class="col-md-1 control-label label-required" for="name">Name:</label>
                        <div class="col-md-8">
                            <input type="text" class="k-textbox" id="name" name="name" tabindex="1"
                                   value="${marchant?.name?:appUser?.username}" required validationMessage="Required"/>
                        </div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="name"></span>
                        </div>

                    </div>

                    <div class="form-group">
                        <label class="col-md-1 control-label  label-required" for="email">Email:</label>

                        <div class="col-md-3">
                            <input type="email" class="k-textbox" id="email" name="email" maxlength="255"
                                   placeholder="Unique Email" required data-required-msg="Required"
                                   required validationMessage="Invalid email" value="${marchant?.email?:appUser?.loginId}"
                                   tabindex="2"/>
                        </div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="email"></span>
                        </div>

                        <label class="col-md-1 control-label  label-required" for="companyPhone">Phone:</label>

                        <div class="col-md-2">
                            <input type="text" class="k-textbox" id="companyPhone" name="companyPhone"
                                   tabindex="3"
                                   value="${marchant?.companyPhone?:appUser?.cellNumber}" required validationMessage="Required"/>
                        </div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="companyPhone"></span>
                        </div>
                    </div>

                    %{--<div class="form-group">--}%
                        %{--<label class="col-md-1 control-label  label-required" for="companyPhone">Phone:</label>--}%

                        %{--<div class="col-md-5">--}%
                            %{--<input type="text" class="k-textbox" id="companyPhone" name="companyPhone"--}%
                                   %{--tabindex="3"--}%
                                   %{--value="${marchant?.companyPhone?:appUser?.cellNumber}" required validationMessage="Required"/>--}%
                        %{--</div>--}%

                        %{--<div class="col-md-2 pull-left">--}%
                            %{--<span class="k-invalid-msg" data-for="companyPhone"></span>--}%
                        %{--</div>--}%
                    %{--</div>--}%

                    <div class="form-group">
                        <label class="col-md-1 control-label label-required"
                               for="companyAddress">Address:</label>

                        <div class="col-md-8">
                            <input type="text" class="k-textbox" id="companyAddress" name="companyAddress"
                                   tabindex="4"
                                   value="${marchant?.companyAddress}" required validationMessage="Required"/>
                        </div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="companyAddress"></span>
                        </div>

                    </div>

                    <div class="form-group">
                        <label class="col-md-1 control-label" for="apiKey">API:</label>

                        <div class="col-md-8">
                            <input type="text" class="k-textbox" id="apiKey" name="apiKey" tabindex="100"
                                   value="${marchant?.apiKey ?: 'Auto Generated'}" readonly="readonly"/>
                        </div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="apiKey"></span>
                        </div>

                    </div>

                    <div class="form-group">
                        <label class="col-md-1 control-label" for="description">Remarks:</label>

                        <div class="col-md-8">
                            <textarea type="text" class="k-textbox" id="description" name="description"
                                   tabindex="5" rows="5" cols="5"
                                   value="${marchant?.description}"/>
                        </div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="description"></span>
                        </div>

                    </div>

                </div>

                <div class="panel-footer">
                    <app:ifAllUrl urls="/marchant/create,/marchant/update">
                        <g:if test="${marchant}">
                            <button id="create" name="create" type="submit" data-role="button"
                                    class="k-button k-button-icontext"
                                    role="button"
                                    aria-disabled="false" tabindex="6"><span class="k-icon k-i-plus"></span>Save Change
                            </button>
                        </g:if>
                        <g:else>
                            <button id="create" name="create" type="submit" data-role="button"
                                    class="k-button k-button-icontext"
                                    role="button"
                                    aria-disabled="false" tabindex="6"><span class="k-icon k-i-plus"></span>Save Change
                            </button>
                        </g:else>
                    </app:ifAllUrl>

                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button"
                            aria-disabled="false" onclick='resetForm();' tabindex="12"><span
                            class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </form>
        </div>
    </div>

</div>








