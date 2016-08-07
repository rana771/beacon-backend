<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridCampaign'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    Create Campaign
                    <app:myFavourite>
                    </app:myFavourite>
                </div>
            </div>

            <form id="campaignForm" name="campaignForm" class="form-horizontal form-widgets" role="form">
                <div class="panel-body">
                    <input type="hidden" name="id" id="id" data-bind="value: campaign.id"/>
                    <input type="hidden" name="version" id="version" data-bind="value: campaign.version"/>

                    <div class="form-group">
                        <label class="col-md-1 control-label" for="marchant">Merchant</label>

                        <div class="col-md-9">
                            <span id="marchant">${marchant?.name ? marchant?.name + " (" : "Merchant Not Create. Please Create Merchant First"}&nbsp;${marchant?.apiKey ? marchant?.apiKey + " )" : ""}</span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-1 control-label label-required" for="beacons">Beacon:</label>

                        <div class="col-md-9">
                            <select id="beacons" name="beacons" maxlength="255"
                                    required  validationMessage="Required"
                                    tabindex="1" autofocus/>

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
                    <div class="form-group">
                        <label class="col-md-1 control-label label-required" for="subject">Subject:</label>

                        <div class="col-md-9">
                            <input type="text" class="k-textbox" id="subject" name="subject" maxlength="255"
                                   required placeholder="Campaign Subject will display in mobile"
                                   validationMessage="Required" data-bind="value: campaign.subject"
                                   tabindex="2" autofocus/>
                        </div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="subject"></span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-md-1 control-label label-required" for="name">Name:</label>

                        <div class="col-md-3">
                            <input type="text" class="k-textbox" id="name" name="name" maxlength="255"
                                   placeholder="Campaign Name" required
                                   validationMessage="Required" data-bind="value: campaign.name"
                                   tabindex="3" autofocus/>
                        </div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="name"></span>
                        </div>

                        <label class="col-md-1 control-label label-required"
                               for="title">Title:</label>

                        <div class="col-md-3">
                            <input type="text" class="k-textbox" id="title"
                                   name="title"
                                   tabindex="4" data-bind="value: campaign.title"
                                   required  validationMessage="Required"
                                   placeholder="Notification Title"
                                   maxlength="255"/>
                        </div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="title"></span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-md-1 control-label label-required"
                               for="ticker">Ticker:</label>

                        <div class="col-md-9">
                            <input type="text" class="k-textbox" id="ticker"
                                   name="ticker"
                                   tabindex="5" data-bind="value: campaign.ticker"
                                   placeholder="Notification Ticker"
                                   maxlength="255"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="ticker"></span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-md-1 control-label label-required"
                               for="message">Message:</label>

                        <div class="col-md-9">
                            <input type="text" class="k-textbox" id="message" name="message" maxlength="255"
                                   placeholder="Notification Message" required
                                   validationMessage="Required" data-bind="value: campaign.message"
                                   tabindex="6" autofocus/>
                        </div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="message"></span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-md-1 control-label"
                               for="startTime">Start Time:</label>

                        <div class="col-md-3">
                            <app:dateControl
                                    name="startTime"
                                    id="startTime"
                                    tabindex="7"
                                    value=""
                                    data-bind="value: campaign.startTime">
                            </app:dateControl>
                        </div>

                        <label class="col-md-3 control-label"
                               for="bonusRewardPoint">End Time:</label>

                        <div class="col-md-3">
                            <app:dateControl
                                    name="endTime"
                                    id="endTime"
                                    tabindex="8"
                                    value=""
                                    data-bind="value: campaign.endTime">
                            </app:dateControl>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-md-1 control-label label-required"
                               for="bonusRewardPoint">Point:</label>

                        <div class="col-md-3">
                            <input type="text" class="k-textbox" id="bonusRewardPoint"
                                   name="bonusRewardPoint"
                                   tabindex="9" data-bind="value: campaign.bonus_reward_point"
                                   placeholder="Bonus Reward Point" required validationMessage="Required"
                                   maxlength="255"/>
                        </div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="bonusRewardPoint"></span>
                        </div>

                        <label class="col-md-1 control-label"
                               for="isScheduleAlways">Active:</label>

                        <div class="col-md-3">
                            <input type="checkbox" id="isScheduleAlways" tabindex="10"
                                   name="isScheduleAlways"
                                   data-bind="checked: campaign.isScheduleAlways"/>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-md-1 control-label label-required" for="template">Template:</label>

                        <div class="col-md-9">
                            <textarea class="k-textbox" id="template" name="template"
                                      required validationMessage="Required" data-bind="value: campaign.template"
                                      rows="2" cols="2"
                                      tabindex="11" autofocus/>
                        </div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="template"></span>
                        </div>
                    </div>

                </div>

                <div class="panel-footer">
                    <app:ifAllUrl urls="/campaign/create,/campaign/update">
                        <button id="create" name="create" type="submit" data-role="button"
                                class="k-button k-button-icontext"
                                role="button"
                                aria-disabled="false" tabindex="12"><span class="k-icon k-i-plus"></span>Create
                        </button>
                    </app:ifAllUrl>

                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button"
                            aria-disabled="false" onclick='resetForm();' tabindex="13"><span
                            class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="row">
        <div id="gridCampaign"></div>
    </div>
</div>








