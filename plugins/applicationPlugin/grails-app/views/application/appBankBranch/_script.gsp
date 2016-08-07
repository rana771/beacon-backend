<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <app:checkSystemUser isPowerUser="true">
        <app:ifAllUrl urls="/appBankBranch/update">
            <li onclick="editBankBranch();"><i class="fa fa-edit"></i>Edit</li>
        </app:ifAllUrl>
        <sec:access url="/appBankBranch/delete">
            <li onclick="deleteBankBranch();"><i class="fa fa-trash-o"></i>Delete</li>
        </sec:access>
    </app:checkSystemUser>
    <app:ifAllUrl urls="/appUserEntity/show">
        <li onclick="viewUser();"><i class="fa fa-users"></i>User</li>
    </app:ifAllUrl>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script type="text/javascript">
    var gridBankBranch, dataSource, dropDownDistrict, bankBranchModel, entityTypeId, bankId;

    $(document).ready(function () {
        onLoadBankBranch();
        initBankBranchGrid();
        initObservable();

        $("#isPrincipleBranch").kendoMobileSwitch({onLabel: "YES",offLabel: "NO"});
        $("#isSmeServiceCenter").kendoMobileSwitch({onLabel: "YES",offLabel: "NO"});
    });

    function onLoadBankBranch() {
        checkOnLoadError();
        initializeForm($('#bankBranchForm'), onSubmitBankBranch);

        bankId = $('#bankId').val();
        entityTypeId = $('#hidEntityType').val();
        $(document).attr('title', "MIS - Create Bank Branch");
        loadNumberedMenu(MENU_ID_APPLICATION, "#appBank/show");
    }

    function checkOnLoadError(){
        var isError = '${isError}';
        var msg = '${message}';
        if (isError == 'true'){
            showError(msg);
            return false;
        }
    }

    function initDataSource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/appBankBranch/list",
                    data: {bankId: bankId},
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
                        name: {type: "string"},
                        code: {type: "string"},
                        routingNo: {type: "string"},
                        address: {type: "string"},
                        isSmeServiceCenter: {type: "boolean"},
                        isPrincipleBranch: {type: "boolean"},
                        bankId: {type: "number"},
                        districtId: {type: "number"},
                        districtName: {type: "string"}
                    }
                },
                parse: function (data) {
                    checkIsErrorGridKendo(data);
                    return data;
                }
            },
            sort: {field: 'districtName', dir: 'asc'},  // default sort
            pageSize: ${com.athena.mis.BaseService.DEFAULT_RESULT_PER_PAGE},
            serverPaging: true,
            serverFiltering: true,
            serverSorting: true
            <g:if test="${cId!=null}">
            ,filter: { field: "id", operator: "equal", value: ${cId} }
            </g:if>
        });
    }

    function initBankBranchGrid() {
        initDataSource();
        $("#gridBankBranch").kendoGrid({
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
                {field: "districtName",title: "District",sortable: true,width: 120,filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}},
                {field: "name",title: "Name",sortable: true, width: 120,filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}},
                {field: "code",title: "Code",sortable: true,width: 75,filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}},
                {field: "address", title: "Address", width: 130, sortable: false, filterable: false},
                {field: "routingNo", title: "Routing No", width: 100, sortable: false, filterable: false},
                {field: "isPrincipleBranch", title: "Principle Branch", width: 80, sortable: true, filterable: false, template: "#=isPrincipleBranch?'YES':'NO' #", attributes: {style: setAlignCenter()},headerAttributes: {style: setAlignCenter()}},
                {field: "isSmeServiceCenter",title: "SME",width: 60,sortable: true,filterable: false,template: "#=isSmeServiceCenter?'YES':'NO' #", attributes: {style: setAlignCenter()},headerAttributes: {style: setAlignCenter()}}
            ],
            filterable: {mode: "row"},
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridBankBranch = $("#gridBankBranch").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function initObservable() {
        bankBranchModel = kendo.observable(
                {
                    bankBranch: {
                        id: "",
                        version: "",
                        name: "",
                        code: "",
                        routingNo: "",
                        address: "",
                        isSmeServiceCenter: false,
                        isPrincipleBranch: false,
                        bankId: bankId,
                        districtId: "",
                        districtName: ""
                    }
                }
        );
        kendo.bind($("#application_top_panel"), bankBranchModel);
    }

    function executePreCondition() {
        if (validateForm($("#bankBranchForm")) == false){
            return false;
        }
        return true;
    }

    function onSubmitBankBranch() {
        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('#create'), true);
        // Spinner Show on AJAX Call
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'appBankBranch', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'appBankBranch', action: 'update')}";
        }
        jQuery.ajax({
            type: 'post',
            data: jQuery("#bankBranchForm").serialize(),
            url: actionUrl,
            success: function (data, textStatus) {
                executePostCondition(data);
                setButtonDisabled($('#create'), false);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus)
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
                var newEntry = result.bankBranch;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = gridBankBranch.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = gridBankBranch.select();
                    var allItems = gridBankBranch.items();
                    var selectedIndex = allItems.index(selectedRow);
                    gridBankBranch.removeRow(selectedRow);
                    gridBankBranch.dataSource.insert(selectedIndex, newEntry);
                }
                resetForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function deleteBankBranch() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true); // Spinner Show on AJAX Call
        var bankBranchId = getSelectedIdFromGridKendo(gridBankBranch);
        $.ajax({
            url: "${createLink(controller: 'appBankBranch', action: 'delete')}?id=" + bankBranchId,
            success: executePostConditionForDelete,
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus)
            },
            complete: onCompleteAjaxCall,    // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelectKendo(gridBankBranch, 'bank brunch') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected Bank Branch?')) {
            return false;
        }
        return true;
    }

    function executePostConditionForDelete(data) {
        if (data.isError) {
            showError(data.message);
            return false;
        }
        var row = gridBankBranch.select();
        row.each(function () {
            gridBankBranch.removeRow($(this));
        });
        resetForm();
        showSuccess(data.message);
    }


    function editBankBranch() {
        if (executeCommonPreConditionForSelectKendo(gridBankBranch, 'bank branch') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridBankBranch'));
        var bankBranchObj = getSelectedObjectFromGridKendo(gridBankBranch);
        showBankBranch(bankBranchObj);
    }

    function showBankBranch(bankBranchObj) {
        bankBranchModel.set('bankBranch', bankBranchObj);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
    }

    function resetForm() {
        clearForm($("#bankBranchForm"), $("#name"));
        setButtonDisabled($('#save'), false);
        initObservable();
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");
    }

    function viewUser() {
        if (executeCommonPreConditionForSelectKendo(gridBankBranch, 'bank branch') == false) {
            return;
        }
        showLoadingSpinner(true);
        var entityId = getSelectedIdFromGridKendo(gridBankBranch);
        var loc = "${createLink(controller: 'appUserEntity', action: 'show')}?cId=" + entityId + "&pId=" + bankId + "&entityTypeId=" + entityTypeId + "&pUrl=appBank/show" + "&url=appBankBranch/show";
        router.navigate(formatLink(loc));
        return false;
    }
    function reloadKendoGrid() {
        gridBankBranch.dataSource.filter([]);
    }
</script>