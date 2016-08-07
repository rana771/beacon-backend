<!-- For Reset module for all Roles Confirmation -->
<div class="modal fade" id="appResetAllRolesConfirmationModal" tabindex="-1" role="dialog"
     aria-labelledby="appResetAllRolesConfirmationModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title">Confirm Following Issues and Enter Password:</h4>
                <h6 class="modal-title">1. Any Custom Role Will Be De-associated From All Features.</h6>
                <h6 class="modal-title">2. Any System Role And Corresponding Features Will Be Reset.</h6>
                <h6 class="modal-title">3. User Authentication Will Be Required.</h6>
            </div>

            <div class="modal-body">
                <form class="form-horizontal" role="form">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="txtAppPassword">Password:</label>

                        <div class="col-md-6">
                            <input style="width: 50%" type="password" class="k-textbox" id="txtAppPassword"
                                   name="txtAppPassword" required validationMessage="Required"
                                   placeholder="Enter Password.."/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="txtAppPassword"></span>
                        </div>
                    </div>
                </form>

            </div>

            <div class="modal-footer">
                <app:checkSystemUser isConfigManager="true">
                    <app:ifAllUrl urls="/appUser/checkPassword">
                        <button type="button" class="btn btn-primary"
                                onclick="onSubmitResetReqMapConfirmation();">Reset All Roles</button>
                    </app:ifAllUrl>
                </app:checkSystemUser>
                <button type="button" class="btn btn-default" data-dismiss="modal"
                        onclick="exitResetReqMapConfirmForm();">Close</button>
            </div>
        </div>
    </div>
</div>