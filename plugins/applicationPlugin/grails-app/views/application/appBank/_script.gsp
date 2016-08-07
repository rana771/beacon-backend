<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <li onclick="editBank();"><i class="fa fa-edit"></i>Edit</li>
    <li onclick="deleteBank();"><i class="fa fa-trash-o"></i>Delete</li>
    <li onclick="showBranch();"><i class="fa fa-bank"></i>Branch</li>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script type="text/javascript">
    var gridBank, bankModel, dataSource, dropDownCountry;

    $(document).ready(function () {
        onLoadBank();
        initBankGrid();
        initObservable();

        $("#isSystemBank").kendoMobileSwitch({onLabel: "YES",offLabel: "NO"});
    });

    function onLoadBank() {
        initializeForm($('#bankForm'), onSubmitBank);
        $(document).attr('title', "MIS - Create Bank");
        loadNumberedMenu(MENU_ID_APPLICATION, "#appBank/show");
    }

    function initDataSource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/appBank/list",
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
                        countryId: {type: "number"},
                        isSystemBank: {type: "boolean"},
                        countryName: {type: "string"}
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

    function initBankGrid() {
        initDataSource();
        $("#gridBank").kendoGrid({
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
                    width: 150,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}},
                    template: "#= makeBoldIfSystemBank(isSystemBank, name) #"
                },
                {
                    field: "code",
                    title: "Code",
                    sortable: true,
                    width: 50,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}},
                    template: "#= makeBoldIfSystemBank(isSystemBank, code) #"
                },
                {
                    field: "countryName",
                    title: "Country",
                    width: 120, sortable: true,
                    filterable: false,
                    template: "#= makeBoldIfSystemBank(isSystemBank, countryName) #"
                }
            ],
            filterable: {mode: "row"},
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridBank = $("#gridBank").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function makeBoldIfSystemBank(isSystemBank, col) {
        if (isSystemBank) {
            col = "<b>" + col + "</b>";
            return col;
        }
        return col;
    }

    function initObservable() {
        bankModel = kendo.observable(
                {
                    bank: {
                        id: "",
                        version: "",
                        name: "",
                        code: "",
                        isSystemBank: false,
                        countryId: ""
                    }
                }
        );
        kendo.bind($("#application_top_panel"), bankModel);
    }

    function executePreCondition() {
        // trim field vales before process.
        if (!validateForm($("#bankForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitBank() {
        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('#create'), true);
        // Spinner Show on AJAX Call
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'appBank', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'appBank', action: 'update')}";
        }
        jQuery.ajax({
            type: 'post',
            data: jQuery("#bankForm").serialize(),
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

    function executePostCondition(data) {
        if (data.isError) {
            showError(data.message);
            showLoadingSpinner(false);
        } else {
            try {
                var newEntry = data.bank;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = gridBank.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = gridBank.select();
                    var allItems = gridBank.items();
                    var selectedIndex = allItems.index(selectedRow);
                    gridBank.removeRow(selectedRow);
                    gridBank.dataSource.insert(selectedIndex, newEntry);
                }
                resetForm();
                showSuccess(data.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        clearForm($("#bankForm"), $('#name'));
        setButtonDisabled($('#save'), false);
        initObservable();
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");   // reset create button text
    }

    <%-- Start : Delete operation of Bank --%>
    function deleteBank() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true); // Spinner Show on AJAX Call
        var bankId = getSelectedIdFromGridKendo(gridBank);
        $.ajax({
            url: "${createLink(controller: 'appBank', action: 'delete')}?id=" + bankId,
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
        if (executeCommonPreConditionForSelectKendo(gridBank, 'bank') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected Bank?')) {
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
        var row = gridBank.select();
        row.each(function () {
            gridBank.removeRow($(this));
        });
        resetForm();
        showSuccess(data.message);
    }

    function editBank() {
        if (executeCommonPreConditionForSelectKendo(gridBank, 'bank') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridBank'));
        var bankObj = getSelectedObjectFromGridKendo(gridBank);
        showBank(bankObj);
    }

    function showBank(bankObj) {
        bankModel.set('bank', bankObj);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
    }

    function showBranch() {
        if (executeCommonPreConditionForSelectKendo(gridBank, 'bank') == false) {
            return false;
        }
        var bankId = getSelectedIdFromGridKendo(gridBank);
        showLoadingSpinner(true);
        var loc = "${createLink(controller: 'appBankBranch', action: 'show')}?oId=" + bankId + "&url=appBank/show";
        router.navigate(formatLink(loc));
        return false;
    }

    function reloadKendoGrid() {
        gridBank.dataSource.filter([]);
    }
</script>
