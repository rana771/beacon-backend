<%@ page import="com.athena.mis.application.service.AppSystemEntityCacheService; com.athena.mis.PluginConnector" %>

<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridAppSchedule'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    Update Schedule
                    <app:myFavourite>
                    </app:myFavourite>
                </div>
            </div>

            <form id="appScheduleForm" name='appScheduleForm' class="form-horizontal form-widgets" role="form">
                <div class="panel-body" style="display: none">
                    <input type="hidden" id="id" name="id" data-bind="value: appSchedule.id"/>
                    <input type="hidden" id="version" name="version" data-bind="value: appSchedule.version"/>

                    <app:systemEntityByReserved
                            name="hidScheduleTypeSimpleId"
                            reservedId="${AppSystemEntityCacheService.SIMPLE}"
                            typeId="${AppSystemEntityCacheService.SYS_ENTITY_TYPE_SCHEDULE}"
                            pluginId="${PluginConnector.PLUGIN_ID}">
                    </app:systemEntityByReserved>

                    <div class="form-group">
                        <label class="col-md-2 control-label label-required" for="scheduleTypeId">Schedule Type:</label>

                        <div class="col-md-2">
                            <app:dropDownSystemEntity
                                    dataModelName="dropDownScheduleType"
                                    required="true"
                                    validationMessage="Required"
                                    name="scheduleTypeId" tabindex="1"
                                    onchange="toggleFields()"
                                    data-bind="value: appSchedule.scheduleTypeId"
                                    typeId="${AppSystemEntityCacheService.SYS_ENTITY_TYPE_SCHEDULE}"
                                    pluginId="${PluginConnector.PLUGIN_ID}">
                            </app:dropDownSystemEntity>
                        </div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="scheduleTypeId"></span>
                        </div>
                        <label class="col-md-2 control-label label-required"
                               for="repeatInterval">Repeat Interval:</label>

                        <div class="col-md-2">
                            <input type="text" id="repeatInterval" name="repeatInterval" tabindex="3"
                                   required="required" validationMessage="Required"
                                   data-bind="value: appSchedule.repeatInterval/1000"
                                   placeholder="in second" autofocus/></div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="repeatInterval"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-2 control-label label-required" for="name">Name:</label>

                        <div class="col-md-2">
                            <input type="text" class="k-textbox" id="name" name="name" tabindex="2"
                                   required="required" validationMessage="Required"
                                   data-bind="value: appSchedule.name"
                                   placeholder="" maxlength="255" autofocus/></div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="name"></span>
                        </div>

                        <label class="col-md-2 control-label label-required"
                               for="repeatCount">Repeat Count:</label>

                        <div class="col-md-2">
                            <input type="text" id="repeatCount" name="repeatCount" tabindex="4"
                                   required="required" validationMessage="Required"
                                   data-bind="value: appSchedule.repeatCount"
                                   placeholder="" autofocus/></div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="repeatCount"></span>
                        </div>
                    </div>

                    <div class="form-group" id="containerExp">
                        <label class="col-md-2 control-label label-required"
                               for="cronExpression">Cron Expression:</label>

                        <div class="col-md-2">
                            <input type="text" class="k-textbox" id="cronExpression" name="cronExpression" tabindex="5"
                                   required="required" validationMessage="Required"
                                   data-bind="value: appSchedule.cronExpression"
                                   placeholder="" maxlength="255" autofocus/></div>

                        <div class="col-md-2 pull-left">
                            <a id='cronDetails' href="javascript:void(0)" tabindex='-1' data-placement="right"
                               data-toggle="popover" style="outline: 0" title="s m h D M W Y
                               <button type='button' class='k-button pull-right' style='line-height:0em' onclick='closePopup();'>
                               <span class='k-icon k-i-close'></span></button>"
                               data-content="
                                   s = Second, 0-59</br>
                                   m = Minute, 0-59</br>
                                   h = Hour, 0-23</br>
                                   D = Day of Month, 1-31, ?</br>
                                   M = Month, 1-12 or JAN-DEC</br>
                                   W = Day of Week, 1-7 or SUN-SAT, ?</br>
                                   Y = Year [optional]</br></br>
                                   <strong>Note:</strong> Either Day-of-Week or Day-of-Month must be ?<br>
                                   <strong>e.g.</strong> 0 15 10 * * ?  [Fire at 10:15 am every day]
                                ">
                                <i class="fa fa-question-circle"
                                   style="font-size: 1.5em;color: #D3D3D3;cursor: pointer"></i>
                            </a>

                            <span class="k-invalid-msg" data-for="cronExpression"></span>
                        </div>
                        <label class="col-md-2 control-label label-optional" for="enable">Enable:</label>

                        <div class="col-md-2">
                            <input type="checkbox" tabindex="6" data-bind="checked: appSchedule.enable"
                                                          id="enable" name="enable"/></div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="enable"></span>
                        </div>
                    </div>
                </div>

                <div class="panel-footer" style="display: none">
                    <button id="update" name="update" type="submit" data-role="button"
                            class="k-button k-button-icontext"
                            role="button" tabindex="7"
                            aria-disabled="false"><span class="k-icon k-i-plus"></span>Update
                    </button>

                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button" tabindex="8"
                            aria-disabled="false" onclick='resetForm();'><span
                            class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="row">
        <div id="gridAppSchedule"></div>
    </div>
</div>

