<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <app:checkSystemUser isPowerUser="true">
        <sec:access url="/appCountry/update">
            <li onclick="editCountry();"><i class="fa fa-edit"></i>Edit</li>
        </sec:access>
        <sec:access url="/appCountry/delete">
            <li onclick="deleteCountry();"><i class="fa fa-trash-o"></i>Delete</li>
        </sec:access>
    </app:checkSystemUser>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script language="javascript" type="text/javascript">

    var dropDownCurrency;
    var dataSource, countryModel, gridCountry;

    $(document).ready(function () {
        onLoadCountryPage();
        initCountryGrid();
        initObservable();
    });

    function onLoadCountryPage() {
        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#countryForm"), onSubmitCountryForm);

        // update page title
        $(document).attr('title', "MIS - Create Country");
        loadNumberedMenu(MENU_ID_APPLICATION, "#appCountry/show");
    }

    function executePreCondition() {
        if (!validateForm($("#countryForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitCountryForm() {
        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'appCountry', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'appCountry', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#countryForm").serialize(),
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
                var newEntry = result.country;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = gridCountry.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = gridCountry.select();
                    var allItems = gridCountry.items();
                    var selectedIndex = allItems.index(selectedRow);
                    gridCountry.removeRow(selectedRow);
                    gridCountry.dataSource.insert(selectedIndex, newEntry);
                }
                resetForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }


    function resetForm() {
        clearForm($("#countryForm"), $("#name"));
        initObservable();
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");
    }

    function initDataSource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/appCountry/list",
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
                        isdCode: {type: "string"},
                        nationality: {type: "string"},
                        currencyId: {type: "number"},
                        phoneNumberPattern: {type: "string"}
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

    function initCountryGrid() {
        initDataSource();
        $("#gridCountry").kendoGrid({
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
                    width: 180,
                    sortable: true,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                },
                {
                    field: "code",
                    title: "Code",
                    width: 120,
                    sortable: true,
                    filterable: {cell: {dataSource: getBlankDataSource()}}
                },
                {field: "isdCode", title: "ISD Code", width: 60, sortable: false, filterable: false},
                {field: "nationality", title: "Nationality", width: 100, sortable: true, filterable: false}
            ],
            filterable: {
                mode: "row"

            },

            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridCountry = $("#gridCountry").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function reloadGrid() {
        gridCountry.dataSource.filter([]);
    }

    function editCountry() {
        if (executeCommonPreConditionForSelectKendo(gridCountry, 'country') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridCountry'));
        var country = getSelectedObjectFromGridKendo(gridCountry);
        showCountry(country);
    }

    function showCountry(data) {
        countryModel.set('country', data);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
    }

    function deleteCountry() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var countryId = getSelectedIdFromGridKendo(gridCountry);
        $.ajax({
            url: "${createLink(controller:'appCountry', action:'delete')}?id=" + countryId,
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

    function reloadKendoGrid() {
        gridCountry.dataSource.filter([]);
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelectKendo(gridCountry, 'country') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected country?')) {
            return false;
        }
        return true;
    }

    function executePostConditionForDelete(data) {
        if (data.isError) {
            showError(data.message);
            return false;
        }
        var row = gridCountry.select();
        row.each(function () {
            gridCountry.removeRow($(this));
        });
        resetForm();
        showSuccess(data.message);
    }

    function initObservable() {
        countryModel = kendo.observable(
                {
                    country: {
                        id: "",
                        version: "",
                        name: "",
                        code: "",
                        isdCode: "",
                        nationality: "",
                        currencyId: "",
                        phoneNumberPattern: ""
                    }
                }
        );
        kendo.bind($("#application_top_panel"), countryModel);
    }

</script>