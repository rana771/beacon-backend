<script language="javascript">

    $(document).ready(function () {
        onLoadMarchantPage();

    });

    function onLoadMarchantPage() {
        // initialize form with kendo validator & bind onSubmit method
        initializeForm($("#marchantForm"), onSubmitMarchant);

        // update page title
        $(document).attr('title', "Beacon - Create Merchant");
        loadNumberedMenu(MENU_ID_BEACON, "#marchant/show");
    }

    function executePreCondition() {
        if (!validateForm($("#marchantForm"))) {   // check kendo validation
            return false;
        }

        return true;
    }

    function onSubmitMarchant() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'marchant', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'marchant', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#marchantForm").serialize(),
            url: actionUrl,
            success: function (data, textStatus) {
                executePostCondition(data);
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
        return false;
    }

    function executePostCondition(result) {
        if (result.isError) {
            showError(result.message);
            showLoadingSpinner(false);
        } else {
            if(result.marchant.id) {
                $("#apiKey").val(result.marchant.apiKey)
                $("#id").val(result.marchant.id)
                $("#version").val(result.marchant.version)
                $('#create').attr('value', 'Save Changes');
                $('#create').html("<span class='k-icon k-i-plus'></span>Save Change");
            }
            showSuccess(result.message);
        }
    }

    function addContent() {
        if (executeCommonPreConditionForSelectKendo(gridMarchant, 'marchant') == false) {
            return;
        }
        showLoadingSpinner(true);
        var marchantId = getSelectedIdFromGridKendo(gridMarchant);
        var loc = "${createLink(controller:'appAttachment', action: 'show')}?oId=" + marchantId + "&url=marchant/show" + "&entityTypeId=" + entityTypeId;
        router.navigate(formatLink(loc));
        return false;
    }

    function resetForm(){
        clearErrors($("#marchantForm"));
    }
</script>
