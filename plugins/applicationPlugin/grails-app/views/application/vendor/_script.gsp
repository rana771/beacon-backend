<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <app:ifAllUrl urls="/vendor/update">
        <li onclick="selectVendor();"><i class="fa fa-edit"></i>Edit</li>
    </app:ifAllUrl>
    <sec:access url="/vendor/delete">
        <li onclick="deleteVendor();"><i class="fa fa-trash-o"></i>Delete</li>
    </sec:access>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script language="javascript">
    var gridVendor, dataSource, dropDownDbType, uploading, vendorModel, sequence, validatorVendor, dropDownDbVendor;

    $(document).ready(function () {
        onLoadVendorPage();
        initVendorGrid();
        initObservable();
    });

    function onLoadVendorPage() {
        initFormWithCustomRule();

        $("#smallImage").kendoUpload({
            multiple: false
        });

        $("#thumbImage").kendoUpload({
            multiple: false
        });

        $('#sequence').kendoNumericTextBox({
            min: 1,
            max: 999999999999,
            format: "#",
            decimals: 0,
            step: 1
        });
        sequence = $("#sequence").data("kendoNumericTextBox");

        bindFormEvents();
        // update page title
        $(document).attr('title', "MIS - Create Vendor");
        loadNumberedMenu(MENU_ID_APPLICATION, "#vendor/show");
    }

    function initDataSource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/vendor/list",
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
                        driver: {type: "string"},
                        dbTypeId: {type: "number"},
                        vendorId: {type: "number"},
                        description: {type: "string"},
                        sequence: {type: "number"},
                        dbTypeName: {type: "string"}
                    }
                },
                parse: function (data) {
                    checkIsErrorGridKendo(data);
                    return data;
                }
            },
            sort: {field: 'sequence', dir: 'asc'},  // default sort
            pageSize: ${com.athena.mis.BaseService.DEFAULT_RESULT_PER_PAGE},
            serverPaging: true,
            serverFiltering: true,
            serverSorting: true
        });
    }

    function initVendorGrid() {
        initDataSource();
        $("#gridVendor").kendoGrid({
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
                {field: "name",title: "Name",width: 100, sortable: true, filterable: {cell: {operator: "contains", dataSource:getBlankDataSource()}}},
                {field: "driver", title: "Driver", width: 80, sortable: false, filterable: false},
                {field: "sequence", title: "Order", width: 50, sortable: true, filterable: false},
                {field: "dbTypeName", title: "DB Type", width: 70, sortable: false, filterable: false}
            ],
            filterable: {mode: "row"},
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridVendor = $("#gridVendor").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function initObservable() {
        vendorModel = kendo.observable(
                {
                    vendor: {
                        id: "",
                        version: "",
                        name: "",
                        description: "",
                        sequence: "",
                        vendorId: "",
                        dbTypeId: ""
                    }
                }
        );
        kendo.bind($("#application_top_panel"), vendorModel);
    }

    function initFormWithCustomRule() {
        validatorVendor = $("#vendorForm").kendoValidator({
            validateOnBlur: false,
            rules: {
                upload: function (input) {
                    if ((input[0].type == "file") && ($(input[0]).is('[validationMessage]'))) {
                        return input.closest(".k-upload").find(".k-file").length;
                    }
                    return true;
                }
            }
        }).data("kendoValidator");
    }

    function bindFormEvents() {
        var actionUrl = "${createLink(controller: 'vendor',action: 'create')}";
        $("#vendorForm").attr('action', actionUrl);

        $('#vendorForm').iframePostForm({
            post: function () {
                uploading = true;
                showLoadingSpinner(true);
                setButtonDisabled($('#create'), true);
            },

            error: function (XMLHttpRequest, textStatus, errorThrown) {
                afterAjaxError(XMLHttpRequest, textStatus);
            },

            complete: function (response) {
                if (uploading == true) {
                    showLoadingSpinner(false);
                    executePostCondition(response);
                    uploading = false;
                    setButtonDisabled($('#create'), false);
                }
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
        // trim field vales before process.
        trimFormValues($("#vendorForm"));

        if (!validatorVendor.validate()) {
            return false;
        }
        return true;
    }

    function executePostCondition(data) {
        var result = eval('(' + data + ')');
        if (result.isError) {
            showError(result.message);
            showLoadingSpinner(false);
        } else {
            try {
                var newEntry = result.vendor;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = gridVendor.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = gridVendor.select();
                    var allItems = gridVendor.items();
                    var selectedIndex = allItems.index(selectedRow);
                    gridVendor.removeRow(selectedRow);
                    gridVendor.dataSource.insert(selectedIndex, newEntry);
                }
                resetForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        clearForm($("#vendorForm"), $('#name'));
        initObservable();
        // reset kendo upload
        $(".k-delete").parent().click();
        var actionUrl = "${createLink(controller: 'vendor',action: 'create')}";
        $("#vendorForm").attr('action', actionUrl);
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function deleteVendor() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var id = getSelectedIdFromGridKendo(gridVendor);
        $.ajax({
            url: "${createLink(controller:'vendor', action: 'delete')}?id=" + id,
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
        if (executeCommonPreConditionForSelectKendo(gridVendor, 'vendor') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected Vendor?')) {
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
        var row = gridVendor.select();
        row.each(function () {
            gridVendor.removeRow($(this));
        });
        resetForm();
        showSuccess(data.message);
    }

    function selectVendor() {
        if (executeCommonPreConditionForSelectKendo(gridVendor, 'vendor') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridVendor'));
        var vendorObj = getSelectedObjectFromGridKendo(gridVendor);
        showVendor(vendorObj);
    }

    function reloadKendoGrid() {
        gridVendor.dataSource.filter([]);
    }

    function showVendor(vendorObj) {
        vendorModel.set('vendor', vendorObj);
        var actionUrl = "${createLink(controller: 'vendor',action: 'update')}";
        $("#vendorForm").attr('action', actionUrl);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

/*    function setDriverName() {
        if (dropDownDbVendor.value() == '') {
            $('#driver').val('');
            return false;
        }
        $('#driver').val(dropDownDbVendor.dataItem().value);
    }*/

</script>
