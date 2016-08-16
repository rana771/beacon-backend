
<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridDevice'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    Create Device
                    <app:myFavourite>
                    </app:myFavourite>
                </div>
            </div>
            <form id="deviceForm" name="deviceForm" class="form-horizontal form-widgets" role="form">
                <div class="panel-body" style="display: none">
                 <input type="hidden" name="id" id="id" data-bind="value: device.id"/>
                 <input type="hidden" name="version" id="version" data-bind="value: device.version"/>

               <div class="form-group">

                       
                       
                       <div class="col-md-6">
                           <label class="col-md-3 control-label label-required" for="name">Marchant:</label>

                           <div class="col-md-6">
                               <input type="text" class="k-textbox" id="marchant" name="marchant" maxlength="255"
                                required
                                      validationMessage="Required" data-bind="value: device.marchant"
                                      tabindex="1" autofocus/>
                           </div>

                           <div class="col-md-3 pull-left">
                               <span class="k-invalid-msg" data-for="marchant"></span>
                           </div>
                       </div>

                       
                      
                       
                       <div class="col-md-6">
                           <label class="col-md-3 control-label label-required" for="name">Name:</label>

                           <div class="col-md-6">
                               <input type="text" class="k-textbox" id="name" name="name" maxlength="255"
                                required
                                      validationMessage="Required" data-bind="value: device.name"
                                      tabindex="1" autofocus/>
                           </div>

                           <div class="col-md-3 pull-left">
                               <span class="k-invalid-msg" data-for="name"></span>
                           </div>
                       </div>

                       
                      
                       
                       <div class="col-md-6">
                           <label class="col-md-3 control-label label-required" for="name">Device Type:</label>

                           <div class="col-md-6">
                               <input type="text" class="k-textbox" id="deviceType" name="deviceType" maxlength="255"
                                required
                                      validationMessage="Required" data-bind="value: device.deviceType"
                                      tabindex="1" autofocus/>
                           </div>

                           <div class="col-md-3 pull-left">
                               <span class="k-invalid-msg" data-for="deviceType"></span>
                           </div>
                       </div>

                       
                      
                       
                       <div class="col-md-6">
                           <label class="col-md-3 control-label label-required" for="name">Details:</label>

                           <div class="col-md-6">
                               <input type="text" class="k-textbox" id="details" name="details" maxlength="255"
                                required
                                      validationMessage="Required" data-bind="value: device.details"
                                      tabindex="1" autofocus/>
                           </div>

                           <div class="col-md-3 pull-left">
                               <span class="k-invalid-msg" data-for="details"></span>
                           </div>
                       </div>

                       
                      
                       
                       <div class="col-md-6">
                           <label class="col-md-3 control-label label-required" for="name">Network:</label>

                           <div class="col-md-6">
                               <input type="text" class="k-textbox" id="network" name="network" maxlength="255"
                                required
                                      validationMessage="Required" data-bind="value: device.network"
                                      tabindex="1" autofocus/>
                           </div>

                           <div class="col-md-3 pull-left">
                               <span class="k-invalid-msg" data-for="network"></span>
                           </div>
                       </div>

                       
                      
                       
                       <div class="col-md-6">
                           <label class="col-md-3 control-label label-required" for="name">Is Schedule Broadcast:</label>

                           <div class="col-md-6">
                               <input type="text" class="k-textbox" id="isScheduleBroadcast" name="isScheduleBroadcast" maxlength="255"
                                required
                                      validationMessage="Required" data-bind="value: device.isScheduleBroadcast"
                                      tabindex="1" autofocus/>
                           </div>

                           <div class="col-md-3 pull-left">
                               <span class="k-invalid-msg" data-for="isScheduleBroadcast"></span>
                           </div>
                       </div>

                       
                      
                       
                       <div class="col-md-6">
                           <label class="col-md-3 control-label label-required" for="name">Content:</label>

                           <div class="col-md-6">
                               <input type="text" class="k-textbox" id="content" name="content" maxlength="255"
                                required
                                      validationMessage="Required" data-bind="value: device.content"
                                      tabindex="1" autofocus/>
                           </div>

                           <div class="col-md-3 pull-left">
                               <span class="k-invalid-msg" data-for="content"></span>
                           </div>
                       </div>

                       
                      
                       
                   <div class="col-md-6">
                       <label class="col-md-3 control-label label-required" for="name">Latitude:</label>

                       <div class="col-md-6">
                           <input type="text" class="k-textbox" id="latitude" name="latitude" maxlength="255"
                                  required
                                  validationMessage="Required" data-bind="value: device.latitude"
                                  tabindex="1" autofocus/>
                       </div>

                       <div class="col-md-3 pull-left">
                           <span class="k-invalid-msg" data-for="latitude"></span>
                       </div>
                   </div>
                       
                      
                       
                   <div class="col-md-6">
                       <label class="col-md-3 control-label label-required" for="name">Longitude:</label>

                       <div class="col-md-6">
                           <input type="text" class="k-textbox" id="longitude" name="longitude" maxlength="255"
                                  required
                                  validationMessage="Required" data-bind="value: device.longitude"
                                  tabindex="1" autofocus/>
                       </div>

                       <div class="col-md-3 pull-left">
                           <span class="k-invalid-msg" data-for="longitude"></span>
                       </div>
                   </div>
                       
                      
                       
                       <div class="col-md-6">
                           <label class="col-md-3 control-label label-required" for="name">Tags:</label>

                           <div class="col-md-6">
                               <input type="text" class="k-textbox" id="tags" name="tags" maxlength="255"
                                required
                                      validationMessage="Required" data-bind="value: device.tags"
                                      tabindex="1" autofocus/>
                           </div>

                           <div class="col-md-3 pull-left">
                               <span class="k-invalid-msg" data-for="tags"></span>
                           </div>
                       </div>

                       
                      

                   </div>
                </div>

                <div class="panel-footer" style="display: none">
                    <app:ifAllUrl urls="/device/create,/device/update">
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
        <div id="gridDevice"></div>
    </div>
</div>








