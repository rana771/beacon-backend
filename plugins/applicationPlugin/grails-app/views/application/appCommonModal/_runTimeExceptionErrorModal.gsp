<!-- For reporting error -->
<div class="modal fade" id="runTimeExceptionErrorModal" tabindex="-1" role="dialog"
     aria-labelledby="runTimeExceptionErrorModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title">Runtime Exception occurred:</h4>
            </div>

            <div class="modal-body">
                <input type="hidden" id="responseText" name="responseText" value="">

                <div>Runtime exception occurred while processing the request.</div>

                <div>Sorry for the inconvenience.</div>

                <div>&nbsp;</div>

                <div>Would you like to report this error?</div>

                <div>&nbsp;</div>

                <textarea style="width: 100%" type="text" class="k-textbox" id="txtErrorComments"
                          name="txtErrorComments" rows="3" maxlength="255"
                          placeholder="Write comments..."></textarea>
            </div>

            <div class="modal-footer" style="padding: 10px">
                <button type="button" class="k-button k-button-icontext"
                        onclick="funcSendMailToReportError();">Report</button>
                <button type="button" class="k-button k-button-icontext" data-dismiss="modal"
                        onclick="closeErrorConsole();">Cancel</button>
            </div>
        </div>
    </div>
</div>

<script language="javascript">
    function closeErrorConsole() {
        $('#responseText').val('');
        $('#txtErrorComments').val('');
        $('#runTimeExceptionErrorModal').modal('hide');
    }

    function funcSendMailToReportError() {
        showLoadingSpinner(true);
        var responseText = $.parseJSON($('#responseText').val());
        var message = responseText.message;
        var classSignature = responseText.classSignature;
        var comments = $('#txtErrorComments').val();
        $.ajax({
            url: "${createLink(controller: 'appMail', action: 'sendMailForError')}?transactionCode=" + 'SendErrorMailActionService'
            + "&message=" + message + "&classSignature=" + classSignature + "&txtErrorComments=" + comments,
            success: executePostConditionForSendErrorMail,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
        return false;
    }

    function executePostConditionForSendErrorMail(data) {
        $('#responseText').val('');
        $('#txtErrorComments').val('');
        if (data.isError) {
            showError(data.message);
        } else {
            showSuccess(data.message);
        }
        $('#runTimeExceptionErrorModal').modal('hide');
    }
</script>