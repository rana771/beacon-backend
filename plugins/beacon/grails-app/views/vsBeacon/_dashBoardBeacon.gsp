<%@ page import="com.athena.mis.integration.beacon.BeaconPluginConnector; com.athena.mis.PluginConnector; com.athena.mis.BaseService" %>

<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <li onclick="setAsDefault();"><i class="fa fa-check-square-o"></i>Set Default</li>
    <li onclick="deleteMyFavourite();"><i class="fa fa-trash-o"></i>Delete</li>
    <li onclick="showPage();"><i class="fa fa-angle-double-right"></i>Navigate</li>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>

</ul>
</script>

<script type="text/javascript">
    var gridMyFavouriteList, dataSource, pluginId, selectedUrl, gridAnnouncement, dataSourceAnnouncement;
    var countAnnouncement = 0;

    $(document).ready(function () {
        $('#divFeatureList').fadeIn();
        $.featureList(
                $("#tabs li a"),
                $("#output li"), {start_item: 0, transition_interval: -1}
        );
        onLoadDashBoard();
    });

    function onLoadDashBoard() {
        pluginId = ${BeaconPluginConnector.PLUGIN_ID};
        initMyFavouriteGrid();

//        initAnnouncementGrid();
    }

    function initDataSource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/appMyFavourite/list?pluginId=" + pluginId,
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
                        featureName: {type: "string"},
                        url: {type: "string"},
                        isDefault: {type: "boolean"},
                        isDirty: {type: "boolean"}
                    }
                },
                parse: function (data) {
                    checkIsErrorGridKendo(data);
                    return data;
                }
            },
            sort: false,  // default sort
            pageSize: ${BaseService.DEFAULT_RESULT_PER_PAGE},
            serverPaging: true,
            serverFiltering: true,
            serverSorting: true
        });
    }

    function initMyFavouriteGrid() {
        initDataSource();
        $("#gridMyFavouriteList").kendoGrid({
            dataSource: dataSource,
            height: $('#divFeatureList').height(),
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
                    field: "featureName", title: "Feature Name", width: 300, sortable: false,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}},
                    template: "#= isDirty?applyTxtCSS(featureName, true):applyTxtCSS(featureName, false) #"
                },
                {
                    field: "isDefault", title: "Default", width: 50, sortable: false, filterable: false,
                    attributes: {style: setAlignCenter()}, headerAttributes: {style: setAlignCenter()},
                    template: "#= isDefault?'YES':'NO'#"
                }
            ],
            filterable: {mode: "row"},
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridMyFavouriteList = $("#gridMyFavouriteList").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }


    function initAnnouncementDataSource() {
        dataSourceAnnouncement = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/appMail/listAnnouncementForDashboard",
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
                        subject: {type: "string"},
                        body: {type: "string"},
                        createdBy: {type: "string"},
                        createdOn: {type: "date"}
                    }
                },
                parse: function (data) {
                    checkIsErrorGridKendo(data);
                    return data;
                }
            },
            sort: false,  // default sort
            pageSize: 1,
            serverPaging: true,
            serverFiltering: true,
            serverSorting: true
        });
    }

    function initAnnouncementGrid() {
        initAnnouncementDataSource();
        $("#gridAnnouncement").kendoGrid({
            dataSource: dataSourceAnnouncement,
            height: $('#divFeatureList').height(),
            selectable: false,
            sortable: true,
            resizable: true,
            reorderable: true,
            pageable: {
                refresh: false,
                buttonCount: 4
            },
            columns: [
                {field: "subject", title: "Announcement(s)", width: 300, sortable: false,filterable: false, template: "#=getAnnouncementTemplate(subject, body, createdOn, createdBy)#"}
            ],
            filterable: {mode: "row"}
        });
        gridAnnouncement = $("#gridAnnouncement").data("kendoGrid");
        countAnnouncement = getAnnouncementCount();
    }

    function getAnnouncementCount() {
        countAnnouncement = gridAnnouncement.dataSource.total();
        return countAnnouncement > 0 ? $("#countAnnouncement").text("(" + countAnnouncement + ")") : '';
    }

    function getAnnouncementTemplate(subject, body, createdOn, createdBy) {
        var announcement = '<b>' + subject + '</b>' + '</br>' + htmlDecode(body) + '</br>' + '<i>Announced By ' + createdBy + '&nbsp;' + 'On ' + kendo.toString(kendo.parseDate(createdOn, 'yyyy-MM-ddTHH:mm:ss'), 'dd-MMM-yy [hh:mm:ss tt]') + '</i>';

        return announcement;
    }


    function refreshGrid(gridModel) {
        gridModel.dataSource.filter([]);
    }

    function reloadKendoGrid() {
        gridMyFavouriteList.dataSource.filter([]);
    }

    function applyTxtCSS(name, isTrue) {
        if (name && isTrue == true) {
            return "<b style='color:#843534; text-decoration: line-through;'>" + name + "</b>";
        }
        return name
    }

    function setAsDefault() {
        if (executeCommonPreConditionForSelectKendo(gridMyFavouriteList, 'a feature') == false) {
            return;
        }
        var isDirty = getSelectedValueFromGridKendo(gridMyFavouriteList, 'isDirty');
        if (isDirty) {
            showError('Invalid bookmark to set default.');
            return;
        }
        if (!confirm('Are you sure you want to set this feature as default?')) {
            return;
        }
        showLoadingSpinner(true);
        var id = getSelectedIdFromGridKendo(gridMyFavouriteList);
        $.ajax({
            url: "${createLink(controller:'appMyFavourite', action:'setAsDefault')}?id=" + id,
            success: executePostConditionForSetDefault,
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus);
            },
            complete: function (XMLHttpOrder, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForSetDefault(result) {
        if (result.isError) {
            showError(result.message);
            showLoadingSpinner(false);
        } else {
            gridMyFavouriteList.dataSource.filter([]);
            showSuccess(result.message);
        }
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelectKendo(gridMyFavouriteList, 'a feature') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected feature?')) {
            return false;
        }
        return true;
    }

    function deleteMyFavourite() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true); // Spinner Show on AJAX Call
        var id = getSelectedIdFromGridKendo(gridMyFavouriteList);
        $.ajax({
            url: "${createLink(controller: 'appMyFavourite', action: 'delete')}?id=" + id,
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

    function executePostConditionForDelete(data) {
        if (data.isError) {
            showError(data.message);
            return false;
        }
        var row = gridMyFavouriteList.select();
        row.each(function () {
            gridMyFavouriteList.removeRow($(this));
        });
        showSuccess(data.message);
    }

    function showPage() {
        if (executeCommonPreConditionForSelectKendo(gridMyFavouriteList, 'a feature') == false) {
            return;
        }
        var isDirty = getSelectedValueFromGridKendo(gridMyFavouriteList, 'isDirty');
        if (isDirty) {
            showError('Invalid bookmark.');
            return;
        }
        showLoadingSpinner(true);
        var loc = getSelectedValueFromGridKendo(gridMyFavouriteList, 'url');
        var bookMarkUrl = loc;
        if (loc.indexOf("&") > 0) {
            bookMarkUrl = loc.split("&").join("_");
            loc = loc + "&isBookMark=true&bookMarkUrl=" + bookMarkUrl;
        }
        router.navigate(formatLink(loc));
    }

    function routeToUrl(data) {
        if (data.isValid) {
            router.navigate(formatLink(selectedUrl));
            return false;
        }
        gridMyFavouriteList.refresh();
        showError(data.message);
        showLoadingSpinner(false);
    }

</script>

<div>
    <app:checkVersion pluginId="${BeaconPluginConnector.PLUGIN_ID}">
    </app:checkVersion>
</div>

<div class="container-fluid" id="divFeatureList">
    <div class="row">
        <div class="col-md-4" style="padding-right: 0">
            <ul id="tabs">
                <li><a href="javascript:;">
                    <span class="dashboard_icon my_favourites"></span>
                    <h5 class="feature">MY FAVOURITES</h5>
                    <span>List of favorite content(s)</span></a>
                </li>
                %{--<li>--}%
                    %{--<a href="javascript:refreshGrid(gridAnnouncement);"--}%
                       %{--onclick="adjustGridContentHeight($('#gridAnnouncement'));getAnnouncementCount();">--}%
                        %{--<span class="dashboard_icon unapproved_po"></span>--}%
                        %{--<h5 class="feature">Announcement <span id="countAnnouncement"></span></h5>--}%
                        %{--<span>List of announcement</span>--}%
                    %{--</a>--}%
                %{--</li>--}%
            </ul>
        </div>

        <div class="col-md-8" style="padding-left: 0">
            <ul id="output">
                <li>
                    <div id="gridMyFavouriteList"></div>
                </li>
                %{--<li>--}%
                    %{--<div id="gridAnnouncement"></div>--}%
                %{--</li>--}%
            </ul>
        </div>
    </div>
</div>
