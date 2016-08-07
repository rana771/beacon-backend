<%@ page import="com.athena.mis.BaseService" %>

<script language="javascript" type="text/javascript">

    var gridSentMail, dataSource;

    $(document).ready(function () {
        onLoadSentMail();
        initSentMailGrid();
        $('.panel-body').height(getGridHeightKendo() - 15);
    });

    function onLoadSentMail() {
        var leftMenu = '${leftMenu? leftMenu : '#appMail/showForSentMail'}';
        var pluginId = '${pluginId? pluginId: '1'}';
        var menuId = getMenuId(pluginId);
        // update page title
        $(document).attr('title', "Sent Items");
        loadNumberedMenu(menuId, leftMenu);
    }

    function getMenuId(pluginId) {
        var menuId = '0';
        switch (pluginId) {
            case '1':
                menuId = MENU_ID_APPLICATION;
                break;
            case '15':
                menuId = MENU_ID_E_LEARNING;
                break;
            default:
                menuId = MENU_ID_APPLICATION;
        }
        return menuId;
    }

    function initDataSource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/appMail/listForSentMail",
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
                        subject: {type: "string"},
                        updatedOn: {type: "date"}
                    }
                },
                parse: function (data) {
                    checkIsErrorGridKendo(data);
                    return data;
                }
            },
            sort: {field: 'updatedOn', dir: 'desc'},  // default sort
            pageSize: ${BaseService.DEFAULT_RESULT_PER_PAGE},
            serverPaging: true,
            serverFiltering: true,
            serverSorting: true
        });
    }

    function initSentMailGrid() {
        initDataSource();
        $("#gridSentMail").kendoGrid({
            dataSource: dataSource,
            height: getGridHeightKendo(),
            selectable: true,
            sortable: true,
            resizable: true,
            reorderable: true,
            pageable: {
                refresh: true,
                buttonCount: 4
            },
            columns: [
                {
                    field: "subject",
                    title: "Subject",
                    width: 150,
                    sortable: false,
                    filterable: false
                }
            ],
            change: function () {
                previewMail();
            }
        });
        gridSentMail = $("#gridSentMail").data("kendoGrid");
        clearGridKendo(gridSentMail);
    }

    function previewMail() {
        var mailId = getSelectedIdFromGridKendo(gridSentMail);
        $.ajax({
            url: "${createLink(controller: 'appMail', action: 'previewMail')}?mailId=" + mailId,
            success: executePostConditionForPreviewMail,
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

    function executePostConditionForPreviewMail(data) {
        if (data.isError) {
            showError(data.message);
            return;
        }
        var heightBefore = $('.panel-body').height();
        $('.panel-body').html(data.html);
        var heightAfter = $('.panel-body > table').height();
        if (heightAfter > heightBefore) {
            $('.panel-body').height(heightAfter);
        } else {
            $('.panel-body').height(getGridHeightKendo() - 15);
        }
    }

</script>