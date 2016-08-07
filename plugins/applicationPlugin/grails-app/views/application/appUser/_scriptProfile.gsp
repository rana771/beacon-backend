<script type="text/javascript">
    var uploading, aboutMeModel, validatorImage, uploadModel, uploadUserDocumentModel;

    $(document).ready(function () {
        initKendoEditor();
        checkOnLoadError();
        onLoadAppUser();
    });

    function onLoadAppUser() {
        initializeForm($('#userForm'), bindFormEvents);
        initUserProfileForm();
        initUserDocumentUpload();

        // update page title
        $('span.headingText').html('User Profile');
        $('#icon_box').attr('class', 'pre-icon-header application_user');
        $(document).attr('title', "MIS - User");
    }


    function checkOnLoadError() {
        var isError = '${isError}';
        var msg = '${message}';
        if (isError == 'true') {
            showError(msg);
            return false;
        }
    }

    function bindFormEvents() {
        if (executePreConForSubmitUser() == false) return false;
        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        jQuery.ajax({
            type: 'post',
            data: serializeFormAsObject("#userForm"),
            url: "${createLink(controller: 'appUser',action: 'updateProfile')}",
            success: function (data, textStatus) {
                executePostConditionAfterUpdate(data);
                setButtonDisabled($('#create'), false);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus)
            },
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json'
        });
    }

    function executePostConditionAfterUpdate(result) {
        if (result.isError) {
            showError(result.message);
            return false;
        } else {
            try {
                $("#username").text(result.userName);
                resetAppUserForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function trimTextFields() {
        $('#username').val($.trim($('#username').val()));
        $('#email').val($.trim($('#email').val()));
        $('#cellNumber').val($.trim($('#cellNumber').val()));
    }

    function initKendoEditor() {
        $("#aboutMe").height(20);
        $("#aboutMe").kendoEditor(
                {tools: ['bold','italic','underline','strikethrough','justifyLeft','justifyCenter','justifyRight','justifyFull']}
        );
        aboutMeModel = $("#aboutMe").data("kendoEditor");
    }


    function executePreConForSubmitUser() {
        clearErrors($("#userForm"));
        // trim field vales before process.
        trimFormValues($("#userForm"));

        if (!validatorImage.validate()) {
            return false;
        }

        if ($("#dateOfBirth").val()) {
            if (!checkCustomDate($("#dateOfBirth"), "Birth ")) {
                return false;
            }
        }

        return true;
    }

    function onUploadKendo(e) {
        e.data = serializeFormAsObject("#userForm");
    }

    function clearUpload() {
        $(".k-upload-files").remove();
        $(".k-upload-status").remove();
    }

    function resetAppUserForm() {
        $(".k-delete").parent().click();
        clearUpload();
    }

    function initUserProfileForm() {
        validatorImage = $("#userForm").kendoValidator({
            validateOnBlur: false,
            rules: {
                upload: function (input) {
                    if ((input[0].type == "file") && ($(input[0]).is('[validationMessage]'))) {
                        return input.closest(".k-upload").find(".k-file").length;
                    }
                    return true;
                }
            }
        }).data("kendoValidator");

        uploadModel = $("#signatureImage").kendoUpload({
            multiple: false,
            async: {
                saveUrl: "/appUser/uploadProfileImage",
                autoUpload: true
            },
            upload: onUploadKendo,
            select: checkValidationOnSelect,
            success: executePostCondition,
            error: onErrorKendo
        }).data("kendoUpload");
    }


    function initUserDocumentUpload() {
        validatorImage = $("#userForm").kendoValidator({
            validateOnBlur: false,
            rules: {
                upload: function (input) {
                    if ((input[0].type == "file") && ($(input[0]).is('[validationMessage]'))) {
                        return input.closest(".k-upload").find(".k-file").length;
                    }
                    return true;
                }
            }
        }).data("kendoValidator");

        uploadUserDocumentModel = $("#userDocument").kendoUpload({
            multiple: false,
            async: {
                saveUrl: "/appUser/uploadDocument",
                autoUpload: true
            },
            upload: onUploadKendo,
            select: checkValidationOnSelectForResume,
            success: executePostCondition,
            error: onErrorKendo
        }).data("kendoUpload");
    }

    function checkValidationOnSelectForResume(e) {
        var files = e.files;

        $.each(files, function () {

            if ((this.extension.toLowerCase() != ".pdf") && (this.extension.toLowerCase() != ".doc") && (this.extension.toLowerCase() != ".docx") && (this.extension.toLowerCase() != ".ppt")) {
                showError("Only .pdf, .doc, .docx & .ppt files can be uploaded");
                e.preventDefault();
                return;
            }

            if (this.size / 1024 / 1024 > 5) {
                showError("Max 5 MB file size is allowed!");
                e.preventDefault();
                return;
            }
        });
        return false;

    }


    function checkValidationOnSelect(e) {
        var files = e.files;

        $.each(files, function () {

            if ((this.extension.toLowerCase() != ".jpg") && (this.extension.toLowerCase() != ".png") && (this.extension.toLowerCase() != ".jpeg")) {
                showError("Only .jpg, .jpeg .gif files can be uploaded");
                e.preventDefault();
                return;
            }

            if (this.size / 1024 / 1024 > 5) {
                showError("Max 5 MB file size is allowed!");
                e.preventDefault();
                return;
            }
        });
        return false;

    }

    function executePostCondition(e) {
        var result = (e.response ? e.response : e);     // may be called by kendo control or custom ajax
        if (result.isError) {
            showError(result.message);
            $('li.k-file').addClass('k-file-error');
            clearUpload();
        }
        showSuccess(result.message);
        clearUpload();
    }

    function downloadUserDocument(id) {
        var confirmMsg = 'Do you want to download the resume now?';
        showLoadingSpinner(true);
        if (confirm(confirmMsg)) {
            var url = "${createLink(controller: 'appAttachment', action: 'downloadContent')}?appAttachmentId=" + id;
            document.location = url;
        }
        showLoadingSpinner(false);
        return true;
    }

</script>