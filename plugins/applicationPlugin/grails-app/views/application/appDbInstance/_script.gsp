<%@ page import="com.athena.mis.BaseService" %>

<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <app:ifAllUrl urls="/appDbInstance/update">
        <li onclick="editAppDbInstance();"><i class="fa fa-edit"></i>Edit</li>
    </app:ifAllUrl>
    <app:ifAllUrl urls="/appDbInstance/delete">
        <li onclick="deleteDbInstance();"><i class="fa fa-trash-o"></i>Delete</li>
    </app:ifAllUrl>
    <app:ifAnyUrl
            urls="/appDbInstance/testDBConnection,/appDbInstance/connect,/appDbInstance/disconnect,/appDbInstance/reconnect">
        <li><i class="fa fa-plug"></i>Connection
            <ul>
                <app:ifAllUrl urls="/appDbInstance/connect">
                    <li onclick="createConnection();"><i class="fa fa-check-square-o"></i>Connect</li>
                </app:ifAllUrl>
                <app:ifAllUrl urls="/appDbInstance/disconnect">
                    <li onclick="closeConnection();"><i class="fa fa-close"></i>Disconnect</li>
                </app:ifAllUrl>
                <app:ifAllUrl urls="/appDbInstance/reconnect">
                    <li onclick="recreateConnection();"><i class="fa fa-repeat"></i>Reconnect</li>
                </app:ifAllUrl>
                <app:ifAllUrl urls="/appDbInstance/testDBConnection">
                    <li onclick="testDBConnection();"><i class="fa fa-check-square-o"></i>Test Connection</li>
                </app:ifAllUrl>
            </ul>
        </li>
    </app:ifAnyUrl>
    <app:ifAllUrl urls="/dbInstanceQuery/show">
        <li onclick="showQuery();"><i class="fa fa-search-plus"></i>Query</li>
    </app:ifAllUrl>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script type="text/javascript">
    var dropDownDbVendor, dataSource, appDbInstanceModel, gridDbInstance, dropDownDbType;

    $(document).ready(function () {
        onLoadDbInstance();

        $("#isReadOnly").kendoMobileSwitch({onLabel: "YES",offLabel: "NO"});
        $("#isDeletable").kendoMobileSwitch({onLabel: "YES",offLabel: "NO"});
        $("#isSlave").kendoMobileSwitch({onLabel: "YES",offLabel: "NO"});
    });

    function onLoadDbInstance() {
        initializeForm($('#dbInstanceForm'), onSubmitDbInstance);
        iniAppDbInstance();
        initObservable();

        $(document).attr('title', "App - Create DB Instance");
        loadNumberedMenu(MENU_ID_APPLICATION, "#appDbInstance/show");
    }

    function initDataSource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/appDbInstance/list",
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
                        vendorId: {type: "number"},
                        reservedVendorId: {type: "number"},
                        name: {type: "string"},
                        generatedName: {type: "string"},
                        driver: {type: "string"},
                        url: {type: "string"},
                        port: {type: "string"},
                        userName: {type: "string"},
                        password: {type: "string"},
                        dbName: {type: "string"},
                        isTested: {type: "boolean"},
                        isSlave: {type: "boolean"},
                        isDeletable: {type: "boolean"},
                        isReadOnly: {type: "boolean"},
                        isNative: {type: "boolean"},
                        vendorName: {type: "string"},
                        schemaName: {type: "string"}
                    }
                },
                parse: function (data) {
                    checkIsErrorGridKendo(data);
                    return data;
                }
            },
            sort: {field: 'isTested', dir: 'desc'},  // default sort
            pageSize: ${BaseService.DEFAULT_RESULT_PER_PAGE},
            serverPaging: true,
            serverFiltering: true,
            serverSorting: true
            <g:if test="${oId!=null}">
            , filter: {field: "id", operator: "equal", value: ${oId}}
            </g:if>
        });
    }

    function iniAppDbInstance() {
        initDataSource();
        $("#gridDbInstance").kendoGrid({
            dataSource: dataSource,
            height: getGridHeightKendo(),
            selectable: true,
            sortable: true,
            resizable: true,
            reorderable: true,
            pageable: {
                pageSizes: [10, 15, 20],
                buttonCount: 4
            },
            columns: [
                {
                    field: "vendorName", title: "Vendor", width: 70, sortable: true,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}},
                    template: "#= showLogo(vendorId, vendorName) #"
                },
                {
                    field: "generatedName", title: "Name", width: 100, sortable: true,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}},
                    template: "#= applyTxtCSS(isNative, generatedName) #"
                },
                {
                    field: "dbName", title: "Database Name", width: 50, sortable: true, filterable: false,
                    template: "#= hideTextIfNative(isNative, dbName) #"
                },
                {
                    field: "port", title: "Port", width: 30, sortable: true, filterable: false,
                    template: "#= hideTextIfNative(isNative, port) #"
                },
                {
                    field: "isTested", title: "Connection", width: 40, sortable: true, filterable: false,
                    attributes: {style: setAlignCenter()}, headerAttributes: {style: setAlignCenter()},
                    template: "#= isTested?'Tested':'Un-tested'#"
                },
                {
                    field: "isSlave", title: "Slave", width: 30, sortable: false, filterable: false,
                    attributes: {style: setAlignCenter()}, headerAttributes: {style: setAlignCenter()},
                    template: "#= isSlave?'YES':'NO'#"
                }
            ],
            filterable: {
                mode: "row"
            },
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridDbInstance = $("#gridDbInstance").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function applyTxtCSS(isNative, name) {
        if (isNative) {
            return "<b>" + name + "</b>";
        }
        return name
    }

    function hideTextIfNative(isNative, textValue) {
        if (isNative) {
            return "*****";
        }
        return textValue;
    }

    function showLogo(vendorId, vendorName) {
        var image = "<img src='${createLink(controller: 'vendor',action: 'renderVendorSmallImage')}?id=" + vendorId + "' height='30px'>" + "<p class='pull-right'> " + vendorName + "</p>";
        return image;
    }

    function reloadKendoGrid() {
        gridDbInstance.dataSource.filter([]);
    }

    function initObservable() {
        appDbInstanceModel = kendo.observable(
                {
                    appDbInstance: {
                        id: "",
                        version: "",
                        name: "",
                        generatedName: "",
                        vendorId: "",
                        reservedVendorId: "",
                        driver: "",
                        url: "",
                        port: "",
                        userName: "",
                        password: "",
                        isSlave: false,
                        isDeletable: true,
                        dbName: "",
                        isReadOnly: false,
                        schemaName: ""
                    }
                }
        );
        kendo.bind($("#application_top_panel"), appDbInstanceModel);
    }

    function executePreCondition() {
        // trim field vales before process.
        if (!validateForm($("#dbInstanceForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitDbInstance() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        // Spinner Show on AJAX Call
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'appDbInstance', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'appDbInstance', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#dbInstanceForm").serialize(),
            url: actionUrl,
            success: function (data, textStatus) {
                executePostCondition(data);
                setButtonDisabled($('#create'), false);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus);
            },
            complete: function (XMLHttpRequest, textStatus) {
                // Spinner Hide on AJAX Call
                onCompleteAjaxCall();
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

                var newEntry = result.appDbInstance;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = gridDbInstance.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = gridDbInstance.select();
                    var allItems = gridDbInstance.items();
                    var selectedIndex = allItems.index(selectedRow);
                    gridDbInstance.removeRow(selectedRow);
                    gridDbInstance.dataSource.insert(selectedIndex, newEntry);
                }
                resetForm();
                showSuccess(result.message);
            } catch (e) {
            }
        }
    }

    function resetForm() {
        clearForm($("#dbInstanceForm"), $('#name'));
        setButtonDisabled($('.save'), false);
        initObservable();
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");   // reset create button text
    }

    <%-- Start : Delete operation of DB Instance--%>
    function deleteDbInstance(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }

        showLoadingSpinner(true); // Spinner Show on AJAX Call
        var id = getSelectedIdFromGridKendo(gridDbInstance);
        $.ajax({
            url: "${createLink(controller: 'appDbInstance', action: 'delete')}?id=" + id,
            success: executePostConditionForDelete,
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus);
            },
            complete: onCompleteAjaxCall,    // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelectKendo(gridDbInstance, 'DB Instance') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected DB Instance?')) {
            return false;
        }
        return true;
    }

    <%-- removing selected row and clean input form --%>
    function executePostConditionForDelete(data) {
        if (data.isError) {
            showError(data.message);
            return false;
        }

        var row = gridDbInstance.select();
        row.each(function () {
            gridDbInstance.removeRow($(this));
        });
        resetForm();
        showSuccess(data.message);
    }

    function editAppDbInstance() {
        if (executeCommonPreConditionForSelectKendo(gridDbInstance, 'DB Instance') == false) {
            return;
        }
        var dbInstanceObj = getSelectedObjectFromGridKendo(gridDbInstance);
        if (dbInstanceObj.isNative) {
            showError("Native Database is not update-able");
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridDbInstance'));
        showDbInstance(dbInstanceObj);
    }

    function showDbInstance(dbInstanceObj) {
        appDbInstanceModel.set('appDbInstance', dbInstanceObj);
        appDbInstanceModel.set('name', dbInstanceObj.name);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
    }

    function createConnection() {
        if (executeCommonPreConditionForSelectKendo(gridDbInstance, 'DB Instance') == false) {
            return;
        }
        var id = getSelectedIdFromGridKendo(gridDbInstance);
        var isTested = getSelectedValueFromGridKendo(gridDbInstance, 'isTested');
        if (!isTested) {
            showError("Connection is not tested");
            return false;
        }
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller: 'appDbInstance', action: 'connect')}?id=" + id,
            success: executePostConditionForTestConnection,
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus);
            },
            complete: onCompleteAjaxCall,    // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function closeConnection() {
        if (executeCommonPreConditionForSelectKendo(gridDbInstance, 'DB Instance') == false) {
            return;
        }
        var isTested = getSelectedValueFromGridKendo(gridDbInstance, 'isTested');
        if (!isTested) {
            showError("Connection is not tested");
            return false;
        }
        showLoadingSpinner(true);
        var id = getSelectedIdFromGridKendo(gridDbInstance);
        $.ajax({
            url: "${createLink(controller: 'appDbInstance', action: 'disconnect')}?id=" + id,
            success: executePostConditionForTestConnection,
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus);
            },
            complete: onCompleteAjaxCall,    // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function recreateConnection() {
        if (executeCommonPreConditionForSelectKendo(gridDbInstance, 'DB Instance') == false) {
            return;
        }
        var isTested = getSelectedValueFromGridKendo(gridDbInstance, 'isTested');
        if (!isTested) {
            showError("Connection is not tested");
            return false;
        }
        showLoadingSpinner(true);
        var id = getSelectedIdFromGridKendo(gridDbInstance);
        $.ajax({
            url: "${createLink(controller: 'appDbInstance', action: 'reconnect')}?id=" + id,
            success: executePostConditionForTestConnection,
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus);
            },
            complete: onCompleteAjaxCall,    // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function testDBConnection() {
        if (executeCommonPreConditionForSelectKendo(gridDbInstance, 'DB Instance') == false) {
            return;
        }
        showLoadingSpinner(true);

        var id = getSelectedIdFromGridKendo(gridDbInstance);
        $.ajax({
            url: "${createLink(controller: 'appDbInstance', action: 'testDBConnection')}?id=" + id,
            success: executePostConditionForTestConnection,
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus);
            },
            complete: onCompleteAjaxCall,    // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForTestConnection(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            showSuccess(data.message);
            var dbInstance = data.dbInstance;
            var selectedRow = gridDbInstance.select();
            var allItems = gridDbInstance.items();
            var selectedIndex = allItems.index(selectedRow);
            gridDbInstance.removeRow(selectedRow);
            gridDbInstance.dataSource.insert(selectedIndex, dbInstance);
        }
        resetForm();
        reloadKendoGrid();
    }

    function showQuery() {
        if (executeCommonPreConditionForSelectKendo(gridDbInstance, 'DB Instance') == false) {
            return;
        }
        var dbInstanceObj = getSelectedObjectFromGridKendo(gridDbInstance);
        var dbInstanceId = dbInstanceObj.id;
        var isTested = dbInstanceObj.isTested;
        if (!isTested) {
            showError('Selected DB Instance connection is not tested.');
            return;
        }
        showLoadingSpinner(true);
        var loc = "${createLink(controller:'dbInstanceQuery', action: 'show')}?oId=" + dbInstanceId + "&url=appDbInstance/show";
        router.navigate(formatLink(loc));
        return false;
    }

</script>
