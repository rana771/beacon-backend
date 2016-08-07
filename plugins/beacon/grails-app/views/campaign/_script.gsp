<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <sec:access url="/campaign/update">
        <li onclick="editCampaign();"><i class="fa fa-edit"></i>Edit</li>
    </sec:access>
    <sec:access url="/campaign/delete">
        <li onclick="deleteCampaign();"><i class="fa fa-trash-o"></i>Delete</li>
    </sec:access>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script language="javascript">
    //    var entityTypeId, appUserEntityTypeId;
    //    var gridCampaign;
    //    var dataSource, campaignModel;
    var gridCampaign, dataSource, campaignModel, dropDownBeacon, dropDownMarchant;

    $(document).ready(function () {
        $("#bonusRewardPoint").kendoNumericTextBox({
            spinners: false
        });
        initKendoEditor();
        onLoadCampaignPage();
        initCampaignGrid();
        initObservable();

//        $("#beacons").data("kendoMultiSelect").value([ { name: "Chang", id: 1 },{ name: "Uncle Bob's Organic Dried Pears", id: 2 }]);
//        $("#beacons").value([{ name: "Chang", id: 1 },{ name: "Uncle Bob's Organic Dried Pears", id: 2 }]);
//        console.log(JSON.parseJSON("{{id:11,name:Test Beacon},{id:12,name:Athena Beacon}}"));
        // init kendo switch
//        $("#isApproveInFromSupplier").kendoMobileSwitch({
//            onLabel: "YES",
//            offLabel: "NO"
//        });
//        $("#isApproveInFromInventory").kendoMobileSwitch({
//            onLabel: "YES",
//            offLabel: "NO"
//        });
//       $("#isApproveInvOut").kendoMobileSwitch({
//            onLabel: "YES",
//            offLabel: "NO"
//        });
//        $("#isApproveConsumption").kendoMobileSwitch({
//            onLabel: "YES",
//            offLabel: "NO"
//        });
//        $("#isApproveProduction").kendoMobileSwitch({
//            onLabel: "YES",
//            offLabel: "NO"
//        });
    });

    function initKendoEditor() {
        $("#template").kendoEditor(
                {
                    tools: ['formatting', 'bold','italic','underline','justifyLeft', 'justifyCenter', 'justifyRight', 'justifyFull',
                        'insertUnorderedList', 'insertOrderedList', 'indent','createLink', 'insertImage', 'createTable', 'viewHtml']
                }
        );
    }

    function onLoadCampaignPage() {
        // initialize form with kendo validator & bind onSubmit method
        initializeForm($("#campaignForm"), onSubmitCampaign);

        // update page title
        $(document).attr('title', "Beacon - Create Campaign");
        loadNumberedMenu(MENU_ID_BEACON, "#campaign/show");
    }

    function executePreCondition() {
        if (!validateForm($("#campaignForm"))) {   // check kendo validation
            return false;
        }
        return true;
    }

    function onSubmitCampaign() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'campaign', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'campaign', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#campaignForm").serialize(),
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
            try {
                initCampaignGrid();
                resetForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        // clear all errors, validation messages & form values and bind onFocus method
        clearErrors($("#campaignForm"));
        $('#beacons').data("kendoMultiSelect").value([]);
        initObservable();
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function addContent() {
        if (executeCommonPreConditionForSelectKendo(gridCampaign, 'campaign') == false) {
            return;
        }
        showLoadingSpinner(true);
        var campaignId = getSelectedIdFromGridKendo(gridCampaign);
        var loc = "${createLink(controller:'appAttachment', action: 'show')}?oId=" + campaignId + "&url=campaign/show" + "&entityTypeId=" + entityTypeId;
        router.navigate(formatLink(loc));
        return false;
    }

    function reloadKendoGrid() {
        gridCampaign.dataSource.filter([]);
    }

    function addUserCampaign() {
        if (executeCommonPreConditionForSelectKendo(gridCampaign, 'campaign') == false) {
            return;
        }
        showLoadingSpinner(true);
        var campaignId = getSelectedIdFromGridKendo(gridCampaign);
        var loc = "${createLink(controller:'appUserEntity', action: 'show')}?oId=" + campaignId + "&url=campaign/show" + "&entityTypeId=" + appUserEntityTypeId;
        router.navigate(formatLink(loc));
        return false;
    }

    function reloadGrid() {
        gridCampaign.dataSource.filter([]);
    }

    function deleteCampaign() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true); // Spinner Show on AJAX Call
        var campaignId = getSelectedIdFromGridKendo(gridCampaign);
        $.ajax({
            url: "${createLink(controller:'campaign', action: 'delete')}?id=" + campaignId,
            success: executePostConditionForDelete,
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus)
            },
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelectKendo(gridCampaign, 'campaign') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected Campaign?')) {
            return false;
        }
        return true;
    }

    function executePostConditionForDelete(data) {
        if (data.isError) {
            showError(data.message);
            return false;
        }
        var row = gridCampaign.select();
        row.each(function () {
            gridCampaign.removeRow($(this));
        });
        resetForm();
        showSuccess(data.message);
    }

    function editCampaign() {
        if (executeCommonPreConditionForSelectKendo(gridCampaign, 'campaign') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridCampaign'));
        resetForm();
        showLoadingSpinner(true);
        var campaign = getSelectedObjectFromGridKendo(gridCampaign);
        var ids=campaign.beacon_ids.split(',')
        $('#beacons').data("kendoMultiSelect").value(ids);
        campaign.template = htmlDecode(campaign.template);
        showCampaign(campaign);
        showLoadingSpinner(false);
    }

    function showCampaign(campaign) {
        campaignModel.set('campaign', campaign);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
        if(campaign.is_schedule_always)
            document.getElementById('isScheduleAlways').checked = true;
        else
            document.getElementById('isScheduleAlways').checked = false;
    }

    function initDatasource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/campaign/list",
                    dataType: "json",
                    type: "post"
                }
            },
            schema: {
                type: 'json',
                data: "list", total: "count",
                model: {
                    fields: {
                        id: {type: "number"},
                        version: {type: "number"},
                        beacons: {type: "string"},
                        bonus_reward_point: {type: "number"},
                        name: {type: "string"},
                        subject: {type: "string"},
                        template: {type: "string"},
                        beacon_ids: {type: "string"},
                        startTime: {type: "date"},
                        title: {type: "string"},
                        message: {type: "string"},
                        ticker: {type: "string"},
                        is_schedule_always: {type: 'bool'}
                    }
                },
                parse: function (data) {
                    checkIsErrorGridKendo(data);
                    return data;
                }
            },
            sort: {field: 'name', dir: 'asc'},  // default sort
            pageSize: ${com.athena.mis.BaseService.DEFAULT_RESULT_PER_PAGE},
            serverPaging: true,
            serverFiltering: true,
            serverSorting: true
        });
    }

    function initCampaignGrid() {
        initDatasource();
        $("#gridCampaign").kendoGrid({
            dataSource: dataSource,
            height: getGridHeightKendo(),
            selectable: true,
            sortable: true,
            resizable: true,
            reorderable: true,
            pageable: {
                refresh: false,
                pageSizes: [10, 15, 20],
                buttonCount: 4
            },
            columns: [

                {
                    field: "name",
                    title: "Name",
                    sortable: true,
                    hidden:true,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                },

                {
                    field: "subject",
                    title: "Subject",
                    sortable: true,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                },

                {
                    field: "title",
                    title: "Message Title",
                    sortable: false,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                },


                {
                    field: "ticker", title: "Message Ticker", sortable: true,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                }

                ,

                {
                    field: "is_schedule_always",
                    title: "Active?",
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                }
                ,

                {
                    field: "message",
                    title: "Message",
                    sortable: true,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                }
                ,

                {
                    field: "bonus_reward_point",
                    title: "Point",
                    sortable: true,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                }

                ,

                {
                    field: "beacons",
                    title: "Beacons",
                    sortable: true,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                },
                {
                    field: "beacon_ids",
                    title: "ids",
                    hidden:true,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                },
                {
                    field: "template",
                    title: "Template",
                    hidden:true,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                }




            ],
            filterable: {mode: "row"},
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridCampaign = $("#gridCampaign").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function initObservable() {
        campaignModel = kendo.observable(
                {
                    campaign: {


                        beacons: ""

                        ,

                        bonus_reward_point: ""

                        ,

                        endTime: ""

                        ,



                        name: ""

                        ,

                        startTime: ""

                        ,

                        subject: "" ,
                        title: "" ,
                        beacon_ids: "" ,
                        message: "" ,
                        ticker: "" ,

                        template: "",
                        is_schedule_always: false


                    }
                }
        );
        kendo.bind($("#application_top_panel"), campaignModel);
    }



</script>
