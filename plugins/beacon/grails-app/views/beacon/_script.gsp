<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <sec:access url="/beacon/update">
        <li onclick="editBeacon();"><i class="fa fa-edit"></i>Edit</li>
    </sec:access>
    <sec:access url="/beacon/delete">
        <li onclick="deleteBeacon();"><i class="fa fa-trash-o"></i>Delete</li>
    </sec:access>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script language="javascript">
    var gridBeacon, dataSource, beaconModel;

    $(document).ready(function () {

        $("#major").kendoNumericTextBox({
            spinners: false
        });
        $("#minor").kendoNumericTextBox({
            spinners: false
        });
        onLoadBeaconPage();
        initBeaconGrid();
        initObservable();
    });

    function onLoadBeaconPage() {
        initializeForm($("#beaconForm"), onSubmitBeacon);
        $(document).attr('title', "Beacon - Create Beacon");
        loadNumberedMenu(MENU_ID_BEACON, "#beacon/show");
    }

    function executePreCondition() {
        if (!validateForm($("#beaconForm"))) {   // check kendo validation
            return false;
        }
        return true;
    }

    function onSubmitBeacon() {

        if (!validateForm($('#beaconForm'))) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'beacon', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'beacon', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#beaconForm").serialize(),
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
                initBeaconGrid();
                resetForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        clearErrors($("#beaconForm"));
        initObservable();
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function addContent() {
        if (executeCommonPreConditionForSelectKendo(gridBeacon, 'beacon') == false) {
            return;
        }
        showLoadingSpinner(true);
        var beaconId = getSelectedIdFromGridKendo(gridBeacon);
        var loc = "${createLink(controller:'appAttachment', action: 'show')}?oId=" + beaconId + "&url=beacon/show" + "&entityTypeId=" + entityTypeId;
        router.navigate(formatLink(loc));
        return false;
    }

    function reloadKendoGrid() {
        gridBeacon.dataSource.filter([]);
    }

    function addUserBeacon() {
        if (executeCommonPreConditionForSelectKendo(gridBeacon, 'beacon') == false) {
            return;
        }
        showLoadingSpinner(true);
        var beaconId = getSelectedIdFromGridKendo(gridBeacon);
        var loc = "${createLink(controller:'appUserEntity', action: 'show')}?oId=" + beaconId + "&url=beacon/show" + "&entityTypeId=" + appUserEntityTypeId;
        router.navigate(formatLink(loc));
        return false;
    }

    function reloadGrid() {
        gridBeacon.dataSource.filter([]);
    }

    function deleteBeacon() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true); // Spinner Show on AJAX Call
        var beaconId = getSelectedIdFromGridKendo(gridBeacon);
        $.ajax({
            url: "${createLink(controller:'beacon', action: 'delete')}?id=" + beaconId,
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

        if (!confirm('Are you sure you want to delete the selected Beacon?')) {
            return false;
        }
        return true;
    }

    function executePostConditionForDelete(data) {
        alert(data.message);
        if (data.isError) {
            showError(data.message);
            return false;
        }
        var row = gridBeacon.select();
        row.each(function () {
            gridBeacon.removeRow($(this));
        });
        resetForm();
        showSuccess(data.message);
    }

    function editBeacon() {
        showCreatePanel($('div.expand-div'), $('#gridCampaign'));
        resetForm();
        showLoadingSpinner(true);
        var beacon = getSelectedObjectFromGridKendo(gridBeacon);
        showBeacon(beacon);
        showLoadingSpinner(false);
    }

    function showBeacon(beacon) {
       beaconModel.set('beacon', beacon);
        //$("#transmissionPower").slider('setValue', 5);
        //$("#transmissionPower").slider('refresh');
        latlng = new google.maps.LatLng(beacon.latitude, beacon.longitude);
        placeMarker(latlng);
        $('#create').attr('value', 'Update');
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function initDatasource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/beacon/list",
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
                        major: {type: "number"},
                        minor: {type: "number"},
                        name: {type: "string"},
                        uuid: {type: "string"},
                        signal_interval: {type: "number"},
                        transmission_power: {type: "number"},
                        zone_id: {type: "number"},
                        latitude: {type: "number"},
                        longitude: {type: "number"},
                        tag: {type: "string"}

                    }
                },
                parse: function (data) {
                    checkIsErrorGridKendo(data);
                    return data;
                }
            },
            pageSize: ${com.athena.mis.BaseService.DEFAULT_RESULT_PER_PAGE},
            serverPaging: true,
            serverFiltering: true,
            serverSorting: true
        });
    }

    function initBeaconGrid() {
        initDatasource();
        $("#gridBeacon").kendoGrid({
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
                { field: "id",  title: "id",  hidden:true, sortable: true,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                } ,
                {
                    field: "name",
                    title: "Name",
                    sortable: true,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                } ,
                {
                    field: "uuid",
                    title: "Uuid",
                    sortable: true,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                },
                { field: "major", title: "Major", sortable: false, filterable: false,
                    attributes: {style: setAlignRight()},
                    headerAttributes: {style: setAlignRight()}
                } ,

                {
                    field: "minor",
                    title: "Minor",
                    sortable: false,
                    filterable: false,
                    attributes: {style: setAlignRight()},
                    headerAttributes: {style: setAlignRight()}
                },
                {
                    field: "signal_interval",
                    title: "Interval",
                    sortable: false,
                    filterable: false,
                    attributes: {style: setAlignRight()},
                    headerAttributes: {style: setAlignRight()}
                },
                {
                    field: "transmission_power",
                    title: "Transmission",
                    sortable: false,
                    filterable: false,
                    attributes: {style: setAlignRight()},
                    headerAttributes: {style: setAlignRight()}
                },
                {
                    field: "zone_id",
                    title: "ZoneId",
                    sortable: false,
                    filterable: false,
                    attributes: {style: setAlignRight()},
                    headerAttributes: {style: setAlignRight()}
                },
                {
                    field: "zone_name",
                    title: "Zone",
                    sortable: false,
                    filterable: false,
                    attributes: {style: setAlignRight()},
                    headerAttributes: {style: setAlignRight()}
                },
                {
                    field: "latitude",
                    title: "Latitude",
                    sortable: false,
                    filterable: false,
                    attributes: {style: setAlignRight()},
                    headerAttributes: {style: setAlignRight()}
                },
                {
                    field: "longitude",
                    title: "Longitude",
                    sortable: false,
                    filterable: false,
                    attributes: {style: setAlignRight()},
                    headerAttributes: {style: setAlignRight()}
                },
                {
                    field: "tag",
                    title: "Tags",
                    sortable: false,
                    filterable: false,
                    attributes: {style: setAlignRight()},
                    headerAttributes: {style: setAlignRight()}
                },
            ],
            filterable: {mode: "row"},
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridBeacon = $("#gridBeacon").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function initObservable() {
        beaconModel = kendo.observable(
                {
                    beacon: {
                        major: "",
                        minor: "",
                        name: "",
                        uuid: "",
                        signal_interval: "",
                        transmission_power: "",
                        zone_id: "",
                        latitude: "",
                        longitude: "",
                        tag: ""
                   }
                }
        );
        kendo.bind($("#application_top_panel"), beaconModel);
    }



</script>
