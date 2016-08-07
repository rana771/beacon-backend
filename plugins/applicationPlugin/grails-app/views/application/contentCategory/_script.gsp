<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <app:ifAllUrl urls="/contentCategory/update">
        <li onclick="editContentCategory();"><i class="fa fa-edit"></i>Edit</li>
    </app:ifAllUrl>
    <sec:access url="/contentCategory/delete">
        <li onclick="deleteContentCategory();"><i class="fa fa-trash-o"></i>Delete</li>
    </sec:access>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script type="text/javascript">
    var gridContentCategory, dataSource, contentTypeDocument, contentCategoryModel;
    var dropDownContentType, numericBoxWidth, numericBoxHeight, numericBoxMaxSize;

    $(document).ready(function () {
        onLoadContentCategoryPage();
        initContentCategoryGrid();
        initObservable();
    });

    function onLoadContentCategoryPage() {
        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#contentCategoryForm"), onSubmitContentCategory);

        $("#width,#height").kendoNumericTextBox({
            decimals: 0,
            min: 0,
            format: "# pixel"
        });
        $("#maxSize").kendoNumericTextBox({
            decimals: 0,
            min: 0,
            format: "# bytes"
        });

        numericBoxWidth = $("#width").data("kendoNumericTextBox");
        numericBoxHeight = $("#height").data("kendoNumericTextBox");
        numericBoxMaxSize = $("#maxSize").data("kendoNumericTextBox");
        contentTypeDocument = $('#hidContentTypeDocumentId').val();
        // update page title
        $(document).attr('title', "Create Content Category");
        loadNumberedMenu(MENU_ID_APPLICATION, "#contentCategory/show");
    }

    function initDataSource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/contentCategory/list",
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
                        contentTypeId: {type: "number"},
                        maxSize: {type: "number"},
                        width: {type: "number"},
                        height: {type: "number"},
                        extension: {type: "string"},
                        contentTypeName: {type: "string"},
                        isReserved: {type: "boolean"}
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

    function initContentCategoryGrid() {
        initDataSource();
        $("#gridContentCategory").kendoGrid({
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
                    field: "name", title: "Name", width: 150, sortable: true,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}},
                    template: "#= makeBoldIfReserved(isReserved, name) #"
                },
                {field: "contentTypeName", title: "Content Type", width: 100, sortable: false, filterable: false},
                {
                    field: "width", title: "Width (pixel)", width: 75, sortable: false, filterable: false,
                    template: "#=width > 0 ? width:'' #"
                },
                {
                    field: "height", title: "Height (pixel)", width: 75, sortable: false, filterable: false,
                    template: "#=height > 0 ? height:'' #"
                },
                {field: "maxSize", title: "Max Size (byte)", width: 85, sortable: false, filterable: false},
                {field: "extension", title: "Extension", width: 85, sortable: false, filterable: false}

            ],
            filterable: {mode: "row"},
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridContentCategory = $("#gridContentCategory").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function makeBoldIfReserved(isReserved, col) {
        if (isReserved) {
            return "<b>" + col + "</b>";
        }
        return col;
    }

    function initObservable() {
        contentCategoryModel = kendo.observable(
                {
                    contentCategory: {
                        id: "",
                        version: "",
                        name: "",
                        contentTypeId: "",
                        width: "",
                        height: "",
                        maxSize: "",
                        extension: ""
                    }
                }
        );
        kendo.bind($("#application_top_panel"), contentCategoryModel);
    }

    function executePreCondition() {
        if (!validateForm($("#contentCategoryForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitContentCategory() {
        if (executePreCondition() == false) {
            return false;
        }
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'contentCategory', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'contentCategory', action: 'update')}";
        }
        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        jQuery.ajax({
            type: 'post',
            data: jQuery("#contentCategoryForm").serialize(),
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
                var newEntry = result.contentCategory;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = gridContentCategory.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = gridContentCategory.select();
                    var allItems = gridContentCategory.items();
                    var selectedIndex = allItems.index(selectedRow);
                    gridContentCategory.removeRow(selectedRow);
                    gridContentCategory.dataSource.insert(selectedIndex, newEntry);
                }
                resetContentCategoryForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetContentCategoryForm() {
        clearForm($("#contentCategoryForm"), $("#contentTypeId"));

        numericBoxWidth.enable(true);
        numericBoxHeight.enable(true);

        $('#labelWidth').removeClass('label-optional');
        $('#labelHeight').removeClass('label-optional');
        $('#labelWidth').addClass('label-required');
        $('#labelHeight').addClass('label-required');
        $('#width').attr('required', true);
        $('#height').attr('required', true);
        $('#width').attr('validationMessage', 'Required');
        $('#height').attr('validationMessage', 'Required');
        initObservable();
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");
        $('#hidContentTypeDocumentId').val(contentTypeDocument);
    }

    function editContentCategory() {
        if (executeCommonPreConditionForSelectKendo(gridContentCategory, 'content category') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridContentCategory'));
        var contentCategoryObj = getSelectedObjectFromGridKendo(gridContentCategory);
        showContentCategory(contentCategoryObj);
    }

    function showContentCategory(contentCategoryObj) {
        contentCategoryModel.set('contentCategory', contentCategoryObj);
        toggleWidthHeight();
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
    }

    function deleteContentCategory() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var id = getSelectedIdFromGridKendo(gridContentCategory);
        $.ajax({
            url: "${createLink(controller:'contentCategory', action:'delete')}?id=" + id,
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
        gridContentCategory.dataSource.filter([]);
    }


    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelectKendo(gridContentCategory, 'content category') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected content category?')) {
            return false;
        }
        return true;
    }

    function executePostConditionForDelete(data) {
        if (data.isError) {
            showError(data.message);
            return false;
        }
        var row = gridContentCategory.select();
        row.each(function () {
            gridContentCategory.removeRow($(this));
        });
        resetContentCategoryForm();
        showSuccess(data.message);
    }

    function toggleWidthHeight() {
        if (dropDownContentType.value() == contentTypeDocument) {
            numericBoxWidth.value('0');
            numericBoxHeight.value('0');
            numericBoxWidth.enable(false);
            numericBoxHeight.enable(false);
            $('#labelWidth').removeClass('label-required');
            $('#labelHeight').removeClass('label-required');
            $('#labelWidth').addClass('label-optional');
            $('#labelHeight').addClass('label-optional');
            $('#width').removeAttr('required');
            $('#width').removeAttr('validationMessage');
            $('#height').removeAttr('required');
            $('#height').removeAttr('validationMessage');
        } else {
            numericBoxWidth.enable(true);
            numericBoxHeight.enable(true);
            $('#labelWidth').removeClass('label-optional');
            $('#labelHeight').removeClass('label-optional');
            $('#labelWidth').addClass('label-required');
            $('#labelHeight').addClass('label-required');
            $('#width').attr('required', 'required');
            $('#width').attr('validationMessage', 'Required');
            $('#height').attr('required', 'required');
            $('#height').attr('validationMessage', 'Required');
        }
    }

</script>
