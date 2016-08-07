<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <app:ifAllUrl urls="/company/updateForReseller">
        <li onclick="selectCompany();"><i class="fa fa-edit"></i>Edit</li>
    </app:ifAllUrl>
    <sec:access url="/company/delete">
        <li onclick="deleteCompany();"><i class="fa fa-trash-o"></i>Delete</li>
    </sec:access>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script language="javascript">
    var gridCompany, dataSource, validatorCompany, validatorCountry, validatorCurrency, dropDownCountry, companyModel;
    var dirtyCountry = false;
    var dirtyCurrency = false;

    $(document).ready(function () {
        onLoadCompanyPage();
        initCompanyGrid();
        initObservable();
        bindEventsWithControl();

        $("#isActive").kendoMobileSwitch({onLabel: "YES",offLabel: "NO", enable:false});
    });

    function onLoadCompanyPage() {
        initValidator();
        // update page title
        $(document).attr('title', "MIS - Create Company");
        loadNumberedMenu(MENU_ID_APPLICATION, "#company/showForReseller");
    }
    function bindEventsWithControl() {
        $('#companyForm').bind('keypress', function (e) {
            if ((e.keyCode || e.which) == 13) {
                onSubmitCompany();
                return false;
            }
        });
        $('#countryFormForReseller').bind('keypress', function (e) {
            if ((e.keyCode || e.which) == 13) {
                onSubmitCompany();
                return false;
            }
        });
        $('#countryFormForReseller').bind('change keyup', function (e) {
            dirtyCountry = true;
            return false;

        });
        $('#currencyFormForReseller').bind('keypress', function (e) {
            if ((e.keyCode || e.which) == 13) {
                onSubmitCompany();
                return false;
            }
        });
        $('#currencyFormForReseller').bind('change keyup', function (e) {
            dirtyCurrency = true;
            return false;

        });
    }
    function initValidator() {
        validatorCompany = $("#companyForm").kendoValidator({
            validateOnBlur: false
        }).data("kendoValidator");

        validatorCountry = $("#countryFormForReseller").kendoValidator({
            validateOnBlur: false
        }).data("kendoValidator");

        validatorCurrency = $("#currencyFormForReseller").kendoValidator({
            validateOnBlur: false
        }).data("kendoValidator");
    }

    function executePreCondition() {
        // trim field vales before process.
        trimFormValues($("#companyForm"));
        trimFormValues($("#countryFormForReseller"));
        trimFormValues($("#currencyFormForReseller"));

        if (!validatorCompany.validate()) {
            $('#createCompanyPanel a[href="#fragment-1"]').tab('show');
            validatorCountry.validate();
            validatorCurrency.validate();
            return false;
        } else if (!validatorCountry.validate()) {
            $('#createCompanyPanel a[href="#fragment-2"]').tab('show');
            validatorCurrency.validate();
            return false;
        } else if (!validatorCurrency.validate()) {
            $('#createCompanyPanel a[href="#fragment-3"]').tab('show');
            return false;
        }
        if ($('#id').val().isEmpty()) {
            if (!confirm('After creating a new company all cache utility will be re-pulled. ' +
                    'Please, make sure that all application related activities are stopped during this period of time.')) {
                return false;
            }
        }
        return true;
    }

    function onSubmitCompany() {
        if (executePreCondition() == false) {
            return false;
        }
        var formData = $('#companyForm').serializeArray();
        var countryFormData = $('#countryFormForReseller').serializeArray();
        var currencyFormData = $('#currencyFormForReseller').serializeArray();
        formData = formData.concat(countryFormData);
        formData = formData.concat(currencyFormData);

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'company', action: 'create')}";
        } else {
            formData.push({name: 'dirtyCountry', value: dirtyCountry});
            formData.push({name: 'dirtyCurrency', value: dirtyCurrency});
            actionUrl = "${createLink(controller: 'company', action: 'updateForReseller')}";
        }
        jQuery.ajax({
            type: 'post',
            url: actionUrl,
            data: formData,
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

    function initDataSource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/company/listForReseller",
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
                        isActive: {type: "boolean"},
                        name: {type: "string"},
                        title: {type: "string"},
                        code: {type: "string"},
                        webUrl: {type: "string"},
                        address1: {type: "string"},
                        address2: {type: "string"},
                        contactName: {type: "string"},
                        contactSurname: {type: "string"},
                        contactPhone: {type: "string"},
                        countryName: {type: "string"},
                        countryVersion: {type: "number"},
                        countryCode: {type: "string"},
                        isdCode: {type: "string"},
                        nationality: {type: "string"},
                        phoneNumberPattern: {type: "string"},
                        currencyId: {type: "number"},
                        currencyVersion: {type: "number"},
                        currencyName: {type: "string"},
                        symbol: {type: "string"},
                        otherCode: {type: "string"}
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
                {field: "name", title: "Name", width: 130, sortable: true, filterable: false},
                {
                    field: "code", title: "Code", width: 40, sortable: false, filterable: false,
                    attributes: {style: setAlignCenter()}, headerAttributes: {style: setAlignCenter()}
                },
                {field: "webUrl", title: "Web URL", width: 60, sortable: false, filterable: false},
                {
                    field: "isActive",
                    title: "Active",
                    width: 30,
                    sortable: false,
                    filterable: false,
                    template: "#=isActive ? 'YES':'NO' #",
                    attributes: {style: setAlignCenter()},
                    headerAttributes: {style: setAlignCenter()}
                },
                {field: "countryName", title: "Country", width: 50, sortable: false, filterable: false}

            ],
            filterable: {mode: "row"},
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridCompany = $("#gridCompany").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function reloadKendoGrid() {
        gridCompany.dataSource.filter([]);
    }

    function initObservable() {
        companyModel = kendo.observable(
                {
                    company: {
                        id: "",
                        version: "",
                        isActive: false,
                        name: "",
                        code: "",
                        address1: "",
                        address2: "",
                        countryId: "",
                        contactName: "",
                        contactSurname: "",
                        contactPhone: "",
                        countryName: "",
                        countryVersion: "",
                        countryCode: "",
                        isdCode: "",
                        nationality: "",
                        phoneNumberPattern: "",
                        currencyId: "",
                        currencyVersion: "",
                        currencyName: "",
                        symbol: "",
                        otherCode: ""
                    }
                }
        );
        kendo.bind($("#application_top_panel"), companyModel);
    }

    function resetForm() {
        clearForm($("#companyForm"), dropDownCountry);
        clearForm($("#countryFormForReseller"), null);
        clearForm($("#currencyFormForReseller"), null);
        $("#isActive").data("kendoMobileSwitch").enable(false);
        dirtyCountry = false;
        dirtyCurrency = false;
        initObservable();
        $('#createCompanyPanel a[href="#fragment-1"]').tab('show');
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function selectCompany() {
        if (executeCommonPreConditionForSelectKendo(gridCompany, 'company') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridCompany'));
        resetForm();
        var companyObj = getSelectedObjectFromGridKendo(gridCompany);
        $("#isActive").data("kendoMobileSwitch").enable(true);
        companyModel.set('company', companyObj);
        $('#createCompanyPanel a[href="#fragment-1"]').tab('show');
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function deleteCompany() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true); // Spinner Show on AJAX Call
        var companyId = getSelectedIdFromGridKendo(gridCompany);
        $.ajax({
            url: "${createLink(controller: 'company', action: 'delete')}?id=" + companyId,
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
        if (!confirm('Are you sure you want to delete the selected company?')) {
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

</script>
