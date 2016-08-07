

<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <sec:access url="/testDomain/update">
        <li onclick="editTestDomain();"><i class="fa fa-edit"></i>Edit</li>
    </sec:access>
    <sec:access url="/testDomain/delete">
        <li onclick="deleteTestDomain();"><i class="fa fa-trash-o"></i>Delete</li>
    </sec:access>
    <sec:access url="/appUserEntity/show">
        <li onclick="addUserTestDomain();"><i class="fa fa-users"></i>User</li>
    </sec:access>
    <sec:access url="/appAttachment/show">
        <li onclick="addContent();"><i class="fa fa-paperclip"></i>Attachment</li>
    </sec:access>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script language="javascript">
    var entityTypeId, appUserEntityTypeId;
    var gridTestDomain;
    var dataSource, testDomainModel;

    $(document).ready(function () {
        onLoadTestDomainPage();
        initTestDomainGrid();
        initObservable();
        // init kendo switch
        $("#isApproveInFromSupplier").kendoMobileSwitch({
            onLabel: "YES",
            offLabel: "NO"
        });
        $("#isApproveInFromInventory").kendoMobileSwitch({
            onLabel: "YES",
            offLabel: "NO"
        });
       $("#isApproveInvOut").kendoMobileSwitch({
            onLabel: "YES",
            offLabel: "NO"
        });
        $("#isApproveConsumption").kendoMobileSwitch({
            onLabel: "YES",
            offLabel: "NO"
        });
        $("#isApproveProduction").kendoMobileSwitch({
            onLabel: "YES",
            offLabel: "NO"
        });
    });

    function onLoadTestDomainPage() {
        // initialize form with kendo validator & bind onSubmit method
        initializeForm($("#testDomainForm"), onSubmitTestDomain);

        // update page title
        $(document).attr('title', "MIS - Create TestDomain");
        loadNumberedMenu(MENU_ID_APPLICATION, "#testDomain/show");
        entityTypeId = $("#entityTypeId").val();
        appUserEntityTypeId = $("#appUserEntityTypeId").val();
    }

    function executePreCondition() {
        if (!validateForm($("#testDomainForm"))) {   // check kendo validation
            return false;
        }
        if (!customValidateDate($("#startDate"), 'Start date', $("#endDate"), 'end date')) {
            return false;
        }
        return true;
    }

    function onSubmitTestDomain() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'testDomain', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'testDomain', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#testDomainForm").serialize(),
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
                var newEntry = result.testDomain;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = gridTestDomain.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = gridTestDomain.select();
                    var allItems = gridTestDomain.items();
                    var selectedIndex = allItems.index(selectedRow);
                    gridTestDomain.removeRow(selectedRow);
                    gridTestDomain.dataSource.insert(selectedIndex, newEntry);
                }
                resetForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        // clear all errors, validation messages & form values and bind onFocus method
        clearForm($("#testDomainForm"), $('#name'));
        initObservable();
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function addContent() {
        if (executeCommonPreConditionForSelectKendo(gridTestDomain, 'testDomain') == false) {
            return;
        }
        showLoadingSpinner(true);
        var testDomainId = getSelectedIdFromGridKendo(gridTestDomain);
        var loc = "${createLink(controller:'appAttachment', action: 'show')}?oId=" + testDomainId + "&url=testDomain/show" + "&entityTypeId=" + entityTypeId;
        router.navigate(formatLink(loc));
        return false;
    }

    function reloadKendoGrid() {
        gridTestDomain.dataSource.filter([]);
    }

    function addUserTestDomain() {
        if (executeCommonPreConditionForSelectKendo(gridTestDomain, 'testDomain') == false) {
            return;
        }
        showLoadingSpinner(true);
        var testDomainId = getSelectedIdFromGridKendo(gridTestDomain);
        var loc = "${createLink(controller:'appUserEntity', action: 'show')}?oId=" + testDomainId + "&url=testDomain/show" + "&entityTypeId=" + appUserEntityTypeId;
        router.navigate(formatLink(loc));
        return false;
    }

    function reloadGrid() {
        gridTestDomain.dataSource.filter([]);
    }

    function deleteTestDomain() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true); // Spinner Show on AJAX Call
        var testDomainId = getSelectedIdFromGridKendo(gridTestDomain);
        $.ajax({
            url: "${createLink(controller:'testDomain', action: 'delete')}?id=" + testDomainId,
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
        if (executeCommonPreConditionForSelectKendo(gridTestDomain, 'testDomain') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected TestDomain?')) {
            return false;
        }
        return true;
    }

    function executePostConditionForDelete(data) {
        if (data.isError) {
            showError(data.message);
            return false;
        }
        var row = gridTestDomain.select();
        row.each(function () {
            gridTestDomain.removeRow($(this));
        });
        resetForm();
        showSuccess(data.message);
    }

    function editTestDomain() {
        if (executeCommonPreConditionForSelectKendo(gridTestDomain, 'testDomain') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridTestDomain'));
        resetForm();
        showLoadingSpinner(true);
        var testDomain = getSelectedObjectFromGridKendo(gridTestDomain);
        showTestDomain(testDomain);
        showLoadingSpinner(false);
    }

    function showTestDomain(testDomain) {
        testDomainModel.set('testDomain', testDomain);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function initDatasource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/testDomain/list",
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
        
        
        companyId: {type: "number"}
        
        ,
        
            name: {type: "string"}
        
        ,
        
            shortName: {type: "string"}
        
        
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
            <g:if test="${oId!=null}">
            , filter: {field: "id", operator: "equal", value: ${oId}}
            </g:if>
        });
    }

    function initTestDomainGrid() {
        initDatasource();
        $("#gridTestDomain").kendoGrid({
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
                    field: "companyId",
                    title: "Company Id",
                    sortable: false,
                    filterable: false,
                    attributes: {style: setAlignRight()},
                    headerAttributes: {style: setAlignRight()}
                }
        
        ,
        
        {
        field: "name",
        title: "Name",
        sortable: true,
        filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
        }
        
        ,
        
        {
        field: "shortName",
        title: "Short Name",
        sortable: true,
        filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
        }
        
        
            ],
            filterable: {mode: "row"},
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridTestDomain = $("#gridTestDomain").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function initObservable() {
        testDomainModel = kendo.observable(
                {
        testDomain: {
        
        
        companyId:""
        
        ,
        
        name:""
        
        ,
        
        shortName:""
        
        

                    }
                }
        );
        kendo.bind($("#application_top_panel"), testDomainModel);
    }



</script>
