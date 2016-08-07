<%@ page import="com.athena.mis.BaseService" %>

<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <li><i class="fa fa-cog"></i>Mark As
        <ul>
            <li onclick="MarkAsUnRead();"><i class="fa fa-toggle-off"></i>UnRead</li>
        </ul>
    </li>
    <li onclick="reply();"><i class="fa fa-mail-reply"></i>Reply</li>
    <li onclick="deleteMessage();"><i class="fa fa-trash-o"></i>Delete</li>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script language="javascript">

    var gridAppMessage, appMessageDataSource;

    $(document).ready(function () {
        onLoadAppMailPage();
        initAppMailGrid();
        $('.panel-body').height(getGridHeightKendo() - 15);
    });

    function onLoadAppMailPage() {
        var leftMenu = '${leftMenu? leftMenu : '#appMessage/show'}';
        var pluginId = '${pluginId? pluginId: '1'}';
        var menuId = getMenuId(pluginId);
        // update page title
        $(document).attr('title', "Inbox Message");
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
                break;
        }
        return menuId;
    }

    function previewMessage() {
        var appMessage = getSelectedObjectFromGridKendo(gridAppMessage);
        var url = "${createLink(controller: 'appMessage', action: 'preview')}?id=" + appMessage.id;
        $.ajax({
            url: url,
            success: function (data) {
                executePostConForPreview(data, appMessage);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus);
            },
            complete: onCompleteAjaxCall,
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConForPreview(data, appMessage) {
        if (data.isError) {
            showError(data.message);
            return;
        }
        $('.panel-body').html(data.html);
    }

    function deleteMessage() {
        if (executeCommonPreConditionForSelectKendo(gridAppMessage, 'Message') == false) {
            return;
        }
        if (!confirm('Are you sure you want to delete the selected message?')) {
            return;
        }
        var appMessageId = getSelectedIdFromGridKendo(gridAppMessage);
        $.ajax({
            url: "${createLink(controller: 'appMessage', action: 'delete')}?id=" + appMessageId,
            success: executePostConditionForReCompose,
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

    function executePostConditionForReCompose(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            var row = gridAppMessage.select();
            row.each(function () {
                gridAppMessage.removeRow($(this));
            });
            showSuccess(data.message);
        }
    }

    function MarkAsUnRead() {
        if (executeCommonPreConditionForSelectKendo(gridAppMessage, 'Message') == false) {
            return;
        }
        var appMessage = getSelectedObjectFromGridKendo(gridAppMessage);
        if (!appMessage.isRead) {
            showError("Already marked as un-read.");
            return false;
        }

        showLoadingSpinner(true); // Spinner Show on AJAX Call
        $.ajax({
            url: "${createLink(controller: 'appMessage', action: 'markAsUnRead')}?id=" + appMessage.id,
            success: executePostConditionForMarkAsReadOrUnread,
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus);
            },
            complete: onCompleteAjaxCall,    // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForMarkAsReadOrUnread(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            var selectedRow = gridAppMessage.select();
            var allItems = gridAppMessage.items();
            var selectedIndex = allItems.index(selectedRow);
            gridAppMessage.removeRow(selectedRow);
            gridAppMessage.dataSource.insert(selectedIndex, data.appMessage);
            showSuccess(data.message);
        }
    }

    function initDataSource() {
        appMessageDataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/appMessage/list",
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
                        appMailId: {type: "number"},
                        subject: {type: "string"},
                        body: {type: "string"},
                        senderName: {type: "string"},
                        senderEmail: {type: "string"},
                        createdOn: {type: "date"},
                        isRead: {type: "boolean"},
                        hasAttachment: {type: "boolean"}
                    }
                },
                parse: function (data) {
                    checkIsErrorGridKendo(data);
                    return data;
                }
            },
            sort: {field: 'id', dir: 'desc'},  // default sort
            pageSize: ${BaseService.DEFAULT_RESULT_PER_PAGE},
            serverPaging: true,
            serverFiltering: true,
            serverSorting: true
        });
    }

    function initAppMailGrid() {
        initDataSource();
        $("#gridAppMessage").kendoGrid({
            dataSource: appMessageDataSource,
            height: getGridHeightKendo(),
            selectable: true,
            sortable: true,
            resizable: true,
            reorderable: true,
            pageable: {
                refresh: false,
                buttonCount: 4
            },
            columns: [
                {
                    field: "subject",
                    title: "Subject",
                    width: 150,
                    sortable: false,
                    filterable: false,
                    template: "#= getMailInfo(isRead, subject, senderName, createdOn, hasAttachment)#"
                }
            ],
            change: function () {
                previewMessage();
            },
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridAppMessage = $("#gridAppMessage").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function getMailInfo(isRead, subject, senderName, createdOn, hasAttachment) {
        if (!isRead) {
            subject = "<b>" + subject + "</b>";
        }
//        alert(createdOn);
        var attachment = "";
        if (hasAttachment) {
            attachment = "<i class='fa fa-paperclip pull-right'></i>";
        }
        var mailInfo = subject + attachment + "</br>" +
                "<i>Sent by: " + senderName +
                " On: " + kendo.toString(kendo.parseDate(createdOn, 'yyyy-MM-ddTHH:mm:ss'), 'dd-MMM-yy [hh:mm:ss tt]') +
                "</i>";
        return mailInfo;
    }

    function reloadKendoGrid() {
        gridAppMessage.dataSource.filter([]);
    }

    function reply() {
        if (executeCommonPreConditionForSelectKendo(gridAppMessage, 'message', true) == false) {
            return false;
        }
        var message = getSelectedObjectFromGridKendo(gridAppMessage);
        var url = "/elMail/showForComposeMail?email=" + message.senderEmail + "&id=" + message.id;
        router.navigate(url);
    }

</script>