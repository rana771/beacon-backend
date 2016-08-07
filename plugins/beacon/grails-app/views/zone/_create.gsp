

<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridZone'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    Create Zone
                    <app:myFavourite>
                    </app:myFavourite>
                </div>
            </div>
            <form id="zoneForm" name="zoneForm" class="form-horizontal form-widgets" role="form">
                <div class="panel-body">
                    <input type="hidden" name="id" id="id" value="${zone?.id}"/>
                    <input type="hidden" name="version" id="version" value="${zone?.version}"/>

                    <div class="form-group">
                        <label class="col-md-1 control-label label-required" for="name">Name:</label>
                        <div class="col-md-8">
                            <input type="text" class="k-textbox" id="name" name="name" tabindex="1"
                                   value="${zone?.name}" required validationMessage="Required" data-bind="value: zone.name"/>
                        </div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="name"></span>
                        </div>

                    </div>

                    %{--<div class="form-group">--}%
                        %{--<label class="col-md-1 control-label  label-required" for="email">Email:</label>--}%

                        %{--<div class="col-md-3">--}%
                            %{--<input type="email" class="k-textbox" id="email" name="email" maxlength="255"--}%
                                   %{--placeholder="Unique Email" required data-required-msg="Required"--}%
                                   %{--required validationMessage="Invalid email" value="${zone?.email?:appUser?.loginId}"--}%
                                   %{--tabindex="2"/>--}%
                        %{--</div>--}%

                        %{--<div class="col-md-2 pull-left">--}%
                            %{--<span class="k-invalid-msg" data-for="email"></span>--}%
                        %{--</div>--}%

                        %{--<label class="col-md-1 control-label  label-required" for="companyPhone">Phone:</label>--}%

                        %{--<div class="col-md-2">--}%
                            %{--<input type="text" class="k-textbox" id="companyPhone" name="companyPhone"--}%
                                   %{--tabindex="3"--}%
                                   %{--value="${zone?.companyPhone?:appUser?.cellNumber}" required validationMessage="Required"/>--}%
                        %{--</div>--}%

                        %{--<div class="col-md-2 pull-left">--}%
                            %{--<span class="k-invalid-msg" data-for="companyPhone"></span>--}%
                        %{--</div>--}%
                    %{--</div>--}%



                    <div class="form-group">
                        <label class="col-md-1 control-label label-required"
                               for="color">Address:</label>

                            <div class="col-md-8">
                                <select  name="color" id="color" class="selectpicker show-tick" data-bind="value: zone.color">
                                    <option style="background: green">GREEN</option>
                                    <option style="background: red">RED</option>
                                    <option style="background: blue">BLUE</option>
                                    <option style="background: black">BLACK</option>
                                    <option style="background: blueviolet">BLUE VIOLET</option>
                                    <option style="background: burlywood">BURLY WOOD</option>
                                    <option style="background: rosybrown">ROSY BROWN</option>
                                    <option style="background: greenyellow">GREEN YELLOW</option>
                                    <option style="background: forestgreen">FOREST GREEN</option>
                                    <option style="background: orangered">ORANGE RED</option>
                                    <option style="background: grey">GREY</option>
                                    <option style="background: #3c763d">GREN</option>
                                    <option style="background: indianred">INDIAN RED</option>

                                </select>

                        </div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="color"></span>
                        </div>

                    </div>



                    <div class="form-group">
                        <label class="col-md-1 control-label" for="description">Description:</label>

                        <div class="col-md-8">
                            <textarea type="text" class="k-textbox" id="description" name="description"
                                      tabindex="5" rows="5" cols="5" data-bind="value: zone.description"
                                      value="${zone?.description}"/>
                        </div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="description"></span>
                        </div>

                    </div>

                    <div class="form-group">
                        <label class="col-md-1 control-label label-required" for="beacons">Beacon:</label>

                        <div class="col-md-8">
                            <select id="beacons" name="beacons" maxlength="250"
                                    required  validationMessage="Required"
                                    tabindex="1" autofocus data-bind="value: zone.beacon"/>

                            <script>
                                var kendoMultiSelect
                                $(document).ready(function () {
                                    kendoMultiSelect=  $("#beacons").kendoMultiSelect({
                                        placeholder: "Select Beacons...",
                                        dataTextField: "name",
                                        dataValueField: "id",
                                        autoBind: false,
                                        dataSource: {
                                            serverFiltering: true,
                                            transport: {
                                                read: {
                                                    url: '/beacon/beaconList',
                                                    dataType: 'json'
                                                }
                                            }
                                        }
                                    });
                                });
                            </script>
                        </div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="beacons"></span>
                        </div>

                    </div>

                </div>

                <div class="panel-footer">
                    <app:ifAllUrl urls="/zone/create,/zone/update">
                        <g:if test="${zone}">
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

    <div class="row">
        <div id="gridZone"></div>
    </div>
</div>








