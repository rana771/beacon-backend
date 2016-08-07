<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <app:ifAllUrl urls="/company/update">
        <li onclick="editCompany();"><i class="fa fa-edit"></i>Edit</li>
    </app:ifAllUrl>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script language="javascript">
    var gridCompany, dataSource, validatorCompany, dropDownCountry, uploading, companyModel;

    $(document).ready(function () {
        onLoadCompanyPage();
        initCompanyGrid();
        initObservable();
    });

    function onLoadCompanyPage() {
        // common initializeForm() is not used here due to customValidation/upload
        validatorCompany = $("#companyForm").kendoValidator({
            validateOnBlur: false
        }).data("kendoValidator");

        $("#companyLogo").kendoUpload({
            multiple: false
        });
        $("#companySmallLogo").kendoUpload({
            multiple: false
        });
        bindCompanyFormEvents();
        // update page title
        $(document).attr('title', "MIS - Create Company");
        loadNumberedMenu(MENU_ID_APPLICATION, "#company/show");
    }

    function initDataSource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/company/list",
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
                        countryId: {type: "number"},
                        name: {type: "string"},
                        title: {type: "string"},
                        code: {type: "string"},
                        webUrl: {type: "string"},
                        address1: {type: "string"},
                        address2: {type: "string"},
                        contactName: {type: "string"},
                        contactSurname: {type: "string"},
                        contactPhone: {type: "string"},
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
        });
    }

    function initCompanyGrid() {
        initDataSource();
        $("#gridCompany").kendoGrid({
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
                    field: "name", title: "Name", width: 150, sortable: true, filterable: false,
                    template: "#=name#"
                },
                {field: "code", title: "Code", width: 60, sortable: false, filterable: false},
                {field: "webUrl", title: "Web URL", width: 60, sortable: false, filterable: false},
                {field: "countryName", title: "Country", width: 60, sortable: false, filterable: false}

            ],
            filterable: {mode: "row"},
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridCompany = $("#gridCompany").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function initObservable() {
        companyModel = kendo.observable(
                {
                    company: {
                        id: "",
                        version: "",
                        name: "",
                        code: "",
                        address1: "",
                        address2: "",
                        countryId: "",
                        contactName: "",
                        contactSurname: "",
                        contactPhone: "",
                        countryName: ""
                    }
                }
        );
        kendo.bind($("#application_top_panel"), companyModel);
    }

    function bindCompanyFormEvents() {
        var actionUrl = "${createLink(controller: 'company', action: 'update')}";
        $("#companyForm").attr('action', actionUrl);

        $('#companyForm').iframePostForm({
            post: function () {
                uploading = true;
                showLoadingSpinner(true);
                setButtonDisabled($('#create'), true);
            },
            complete: function (response) {
                var tmpResponse = $.parseJSON(response);
                if (uploading == true && !tmpResponse.classSignature) {
                    executePostCondition(tmpResponse);
                } else {
                    $('#responseText').val(response);
                    $('#runTimeExceptionErrorModal').modal('show');
                }
                showLoadingSpinner(false);
                uploading = false;
                setButtonDisabled($('#create'), false);
            },
            beforePost: function () {
                if (executePreCondition() == false) {
                    return false;
                }
                return true;
            }
        });
    }

    function executePreCondition() {
        if ($('#id').val().isEmpty()) {
            showError('Company can be updated only. Please select from grid to update.');
            return false;
        }
        // trim field vales before process.
        trimFormValues($("#companyForm"));

        if (!validatorCompany.validate()) {
            return false;
        }
        return true;
    }

    function executePostCondition(result) {
        if (result.isError) {
            showError(result.message);
            showLoadingSpinner(false);
        } else {
            try {
                var newEntry = result.company;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = gridCompany.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = gridCompany.select();
                    var allItems = gridCompany.items();
                    var selectedIndex = allItems.index(selectedRow);
                    gridCompany.removeRow(selectedRow);
                    gridCompany.dataSource.insert(selectedIndex, newEntry);
                }
                resetForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        clearForm($("#companyForm"), dropDownCountry);
        initObservable();
        // reset kendo upload
        $(".k-delete").parent().click();
    }

    function deleteCompany() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var companyId = getSelectedIdFromGridKendo(gridCompany);
        $.ajax({
            url: "${createLink(controller:'company', action: 'delete')}?id=" + companyId,
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
        if (executeCommonPreConditionForSelectKendo(gridCompany, 'company') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected Company?')) {
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
        var row = gridCompany.select();
        row.each(function () {
            gridCompany.removeRow($(this));
        });
        resetForm();
        showSuccess(data.message);
    }

    function editCompany() {
        if (executeCommonPreConditionForSelectKendo(gridCompany, 'company') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridCompany'));
        var companyObj = getSelectedObjectFromGridKendo(gridCompany);
        showCompany(companyObj);
    }

    function reloadKendoGrid() {
        gridCompany.dataSource.filter([]);
    }

    function showCompany(companyObj) {
        companyModel.set('company', companyObj);
    }
</script>
