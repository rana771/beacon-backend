<%@ page import="com.mis.beacon.Zone; com.mis.beacon.Marchant" %>


<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridBeacon'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    Create Beacon
                    <app:myFavourite>
                    </app:myFavourite>
                </div>
            </div>

            <form id="beaconForm" name="beaconForm" class="form-horizontal form-widgets" role="form">
                <div class="panel-body">
                    <input type="hidden" name="id" id="id" data-bind="value: beacon.id"/>
                    <input type="hidden" name="version" id="version" data-bind="value: beacon.version"/>


                    <div class="panel-body">
                        <div class="col-md-12">
                            <div class="form-group">
                                <label class="col-md-1 control-label" for="name">Merchant/KEY : </label>

                                <div class="col-md-8">
                                    <span>${marchant?.name ? marchant?.name + " (" : "Merchant Not Create. Please Create Merchant First"}&nbsp;${marchant?.apiKey ? marchant?.apiKey + " )" : ""}</span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-1 control-label label-required" for="name">Name:</label>

                                <div class="col-md-8">
                                    <input type="text" class="k-textbox" id="name" name="name" maxlength="255"
                                           placeholder="Beacon Name" required
                                           validationMessage="Required" data-bind="value: beacon.name"
                                           tabindex="1" autofocus/>
                                </div>

                                <div class="col-md-2 pull-left">
                                    <span class="k-invalid-msg" data-for="name"></span>
                                </div>

                            </div>


                            <div class="form-group">
                                <label class="col-md-1 control-label label-required" for="uuid">UUID:</label>

                                <div class="col-md-8">
                                    <input type="text" class="k-textbox" id="uuid" name="uuid" maxlength="255"
                                           placeholder="73676723-7400-0000-ffff-0000ffff0000" required
                                           validationMessage="Required" data-bind="value: beacon.uuid"
                                           tabindex="2" autofocus/>
                                </div>

                                <div class="col-md-2 pull-left">
                                    <span class="k-invalid-msg" data-for="uuid"></span>
                                </div>

                            </div>

                            <div class="form-group">
                                <label class="col-md-1 control-label label-required" for="major">Major:</label>

                                <div class="col-md-2">
                                    <input type="text" class="k-textbox" id="major" name="major" maxlength="255"
                                           required validationMessage="Required" data-bind="value: beacon.major"
                                           tabindex="3" autofocus/>
                                </div>

                                <div class="col-md-2 pull-left">
                                    <span class="k-invalid-msg" data-for="major"></span>
                                </div>
                                <label class="col-md-2 control-label label-required" for="minor">Minor:</label>

                                <div class="col-md-2">
                                    <input type="text" class="k-textbox" id="minor" name="minor" maxlength="255"
                                           required validationMessage="Required" data-bind="value: beacon.minor"
                                           tabindex="4" autofocus/>
                                </div>

                                <div class="col-md-2 pull-left">
                                    <span class="k-invalid-msg" data-for="minor"></span>
                                </div>
                            </div>


                            <div class="form-group">
                                <label class="col-md-1 control-label label-required" for="signalInterval">Interval(Sec):</label>

                                <div class="col-md-2">
                                    <input type="text" class="k-textbox" id="signalInterval" name="signalInterval" maxlength="255"
                                           required validationMessage="Required" data-bind="value: beacon.signal_interval"
                                           tabindex="3" autofocus/>
                                </div>

                                <div class="col-md-2 pull-left">
                                    <span class="k-invalid-msg" data-for="signalInterval"></span>
                                </div>

                                <label class="col-md-2 control-label label-required" for="transmissionPower">Transmission Power:</label>

                                <div class="col-md-2">
                                    <input id="transmissionPower" name="transmissionPower"
                                           data-slider-id='transmissionPower'  type="text" data-slider-min="0" data-slider-max="30"
                                           data-slider-step="1" data-slider-value="0" data-bind="value: beacon.transmission_power"/>

                                <script>
                                    $("#transmissionPower").slider({
                                        tooltip: 'always'
                                    });
                                </script>
                                </div>

                                <div class="col-md-2 pull-left">
                                    <span class="k-invalid-msg" data-for="minor"></span>
                                </div>
                            </div>


                            <div class="form-group">
                                <label class="col-md-1 control-label label-required" for="zone">Zone :</label>

                                <div class="col-md-8">
                                    <g:select class="form-control" id="zon" name="zone.id" from="${zoneList}"
                                     noSelection="['':'Select Zone']" optionValue="name" optionKey="id"
                                              data-bind="value: beacon.zone_id"/>

                                </div>

                                <div class="col-md-2 pull-left">
                                    <span class="k-invalid-msg" data-for="uuid"></span>
                                </div>

                            </div>
                        </div>



                        %{--Map--}%


                                <g:render template="map"/>


                        %{--End Map--}%
                    </div>
                </div>



                <div class="panel-footer">
                    <app:ifAllUrl urls="/beacon/create,/beacon/update">
                        <button id="create" name="create" type="submit" data-role="button"
                                class="k-button k-button-icontext"
                                role="button" tabindex="5"
                                aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
                        </button>

                        <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                                class="k-button k-button-icontext" role="button" tabindex="6"
                                aria-disabled="false" onclick='resetForm();'><span
                                class="k-icon k-i-close"></span>Cancel
                        </button>
                    </app:ifAllUrl>
                </div>
            </form>
        </div>



    </div>
</div>

<div class="row">
    <div id="gridBeacon"></div>
</div>
</div>








