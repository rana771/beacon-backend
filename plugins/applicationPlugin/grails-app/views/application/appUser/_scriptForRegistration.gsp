<script type="text/javascript">
    $(document).ready(function () {
        $('#appUserForm').submit(function() {
            return executePreCondition();
        });
        $('#appUserForm').kendoValidator({validateOnBlur: false});
        $('#confirmPassword').keypress(function (event) {
            $('#retypePassError').hide();
        });
        $('#retypePassError').hide();
        $(document).attr('title','Application user registration');
        showErrorMessage();
    });

    function showErrorMessage() {
        var isError = "${isError}";
        var message = "${message}";
        if(isError == 'true') {
            showError(message);
        }
    }

    function resetForm() {
        clearForm($("#appUserForm"));
        $('#retypePassError').hide();
        $('#companyId').val("${company?.id}");
        $('#username').focus();
    }


    function executePreCondition() {
        trimFormValues($('#appUserForm'));
        if(validateForm($('#appUserForm')) == false) return false;
        if (checkPasswordMatch() == false) return false;
        return true;
    }

    function checkPasswordMatch() {
        if ($("#password").val() != $("#confirmPassword").val()) {
            $('#retypePassError').show();
            return false;
        } else {
            $('#retypePassError').hide();
            return true;
        }
    }

</script>