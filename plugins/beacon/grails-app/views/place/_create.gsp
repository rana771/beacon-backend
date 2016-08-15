

<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">

            <form id="placeForm" name="placeForm" class="form-horizontal form-widgets" role="form">
                <div class="panel-body">
                 <input type="hidden" name="id" id="id" data-bind="value: place.id"/>
                 <input type="hidden" name="version" id="version" data-bind="value: place.version"/>

               <div class="form-group">

                   <div class="form-group">
                       <label class="col-md-1 control-label label-required" for="latitude">Name:</label>

                       <div class="col-md-2">
                           <input type="text" class="k-textbox" id="name" name="name" maxlength="255"
                                  required
                                  validationMessage="Required" data-bind="value: place.name"
                                  tabindex="1" autofocus/>
                       </div>

                       <div class="col-md-2 pull-left">
                           <span class="k-invalid-msg" data-for="name"></span>
                       </div>
                       <label class="col-md-2 control-label label-required" for="longitude">Geo French Radius:</label>

                       <div class="col-md-2">
                           <input type="text" class="k-textbox" id="geoFrenchRadius" name="geoFrenchRadius" maxlength="255"
                                  required
                                  validationMessage="Required" data-bind="value: place.geoFrenchRadius"
                                  tabindex="1" autofocus/>
                       </div>

                       <div class="col-md-2 pull-left">
                           <span class="k-invalid-msg" data-for="geoFrenchRadius"></span>
                       </div>
                   </div>

                   <div class="clear"></div>

                   <div class="form-group">
                       <label class="col-md-1 control-label label-required" for="signalInterval">&nbsp;</label>

                       <div class="col-md-2">

                       </div>

                       <div class="col-md-2 pull-left">
                           <span class="k-invalid-msg" data-for="signalInterval"></span>
                       </div>

                       <label class="col-md-2 control-label label-required" for="transmissionPower">Radious:</label>

                       <div class="col-md-2">
                           <input id="radiousPower" name="radiousPower"
                                  data-slider-id='radious'  type="text" data-slider-min="0" data-slider-max="30"
                                  data-slider-step="1" data-slider-value="0" data-bind="value: place.geoFrenchRadius"/>

                           <script>
                               $("#radiousPower").slider({
                                   tooltip: 'always'
                               });
                           </script>
                       </div>

                       <div class="col-md-2 pull-left">
                           <span class="k-invalid-msg" data-for="minor"></span>
                       </div>
                   </div>


                   <div class="clear"></div>











                        <g:render template="map"/>

                   </div>
                </div>

                <div class="panel-footer">
                    <app:ifAllUrl urls="/place/create,/place/update">
                        <button id="create" name="create" type="submit" data-role="button"
                                class="k-button k-button-icontext"
                                role="button"
                                aria-disabled="false" tabindex="11"><span class="k-icon k-i-plus"></span>Create
                        </button>
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
        <div id="gridPlace"></div>
    </div>
</div>








