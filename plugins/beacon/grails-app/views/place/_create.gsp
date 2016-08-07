

<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridPlace'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    Create Place
                    <app:myFavourite>
                    </app:myFavourite>
                </div>
            </div>
            <form id="placeForm" name="placeForm" class="form-horizontal form-widgets" role="form">
                <div class="panel-body">
                 <input type="hidden" name="id" id="id" data-bind="value: place.id"/>
                 <input type="hidden" name="version" id="version" data-bind="value: place.version"/>

               <div class="form-group">

                       
                       
                       <div class="col-md-6">
                           <label class="col-md-3 control-label label-required" for="name">Name:</label>

                           <div class="col-md-6">
                               <input type="text" class="k-textbox" id="name" name="name" maxlength="255"
                                required
                                      validationMessage="Required" data-bind="value: place.name"
                                      tabindex="1" autofocus/>
                           </div>

                           <div class="col-md-3 pull-left">
                               <span class="k-invalid-msg" data-for="name"></span>
                           </div>
                       </div>

                       
                      
                       


                       
                      


                       
                      
                       
                       <div class="col-md-6">
                           <label class="col-md-3 control-label label-required" for="name">Geo French Radius:</label>

                           <div class="col-md-6">
                               <input type="text" class="k-textbox" id="geoFrenchRadius" name="geoFrenchRadius" maxlength="255"
                                required
                                      validationMessage="Required" data-bind="value: place.geoFrenchRadius"
                                      tabindex="1" autofocus/>
                           </div>

                           <div class="col-md-3 pull-left">
                               <span class="k-invalid-msg" data-for="geoFrenchRadius"></span>
                           </div>
                       </div>


                   <g:render template="map"/>

                   </div>
                </div>

                <div class="panel-footer" style="display: none">
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








