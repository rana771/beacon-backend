<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <app:ifAllUrl urls="/appDesignation/update">
        <li onclick="editDesignation();"><i class="fa fa-edit"></i>Edit</li>
    </app:ifAllUrl>
    <sec:access url="/appDesignation/delete">
        <li onclick="deleteDesignation();"><i class="fa fa-trash-o"></i>Delete</li>
    </sec:access>
    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script type="text/javascript">
    var designationGrid, designationDataSource, designationModel;

    $(document).ready(function () {
        onLoadDesignationPage();
        initDesignationGrid();
        initObservable();
    });

    function onLoadDesignationPage() {
        // initialize form with kendo validator & bind onSubmit event
        initializeForm($('#designationForm'), onSubmitCustomer);
        // update page title
        $(document).attr('title', "MIS - Create Designation Information");
        loadNumberedMenu(MENU_ID_APPLICATION, "#appDesignation/show");
    }

    function onSubmitCustomer() {
        if (!validateForm($('#designationForm'))) {
            return false;
        }
        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'appDesignation', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'appDesignation', action:  'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#designationForm").serialize(),
            url: actionUrl,
            success: function (data, textStatus) {
                executePostConForSubmitDesignation(data);
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

    function executePostConForSubmitDesignation(result) {
        if (result.isError) {
            showError(result.message);
            showLoadingSpinner(false);
        } else {
            try {
                var newEntry = result.designation;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = designationGrid.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = designationGrid.select();
                    var allItems = designationGrid.items();
                    var selectedIndex = allItems.index(selectedRow);
                    designationGrid.removeRow(selectedRow);
                    designationGrid.dataSource.insert(selectedIndex, newEntry);
                }
                resetForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function editDesignation() {
        if (executeCommonPreConditionForSelectKendo(designationGrid, 'designation') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridDesignation'));
        var designationObj = getSelectedObjectFromGridKendo(designationGrid);
        showDesignationInfo(designationObj);
    }

    function showDesignationInfo(designationObj) {
        designationModel.set('designation', designationObj);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
    }

    function resetForm() {
        clearForm($("#designationForm"), $('#name'));
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelectKendo(designationGrid, 'designation') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected designation?')) {
            return false;
        }
        return true;
    }

    function deleteDesignation() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var designationId = getSelectedIdFromGridKendo(designationGrid);
        $.ajax({
            url: "${createLink(controller: 'appDesignation', action: 'delete')}?id=" + designationId,
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
        designationGrid.dataSource.filter([]);
    }

    function executePostConditionForDelete(data) {
        if (data.isError) {
            showError(data.message);
            return false;
        }
        var row = designationGrid.select();
        row.each(function () {
            designationGrid.removeRow($(this));
        });
        resetForm();
        showSuccess(data.message);
    }

    function initDataSource() {
        designationDataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/appDesignation/list",
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
                        shortName: {type: "string"}
                    }
                },
                parse: function (data) {
                    checkIsErrorGridKendo(data);
                    return data;
                }
            },
            pageSize: ${com.athena.mis.BaseService.DEFAULT_RESULT_PER_PAGE},
            serverPaging: true,
            serverFiltering: true,
            serverSorting: true
        });
    }

    function initDesignationGrid() {
        initDataSource();
        $("#gridDesignation").kendoGrid({
            dataSource: designationDataSource,
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
                    field: "shortName",
                    title: "Short Name",
                    width: 120,
                    sortable: true,
                    filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
                }
            ],
            filterable: {
                mode: "row"
            },

            toolbar: kendo.template($("#gridToolbar").html())
        });
        designationGrid = $("#gridDesignation").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function initObservable() {
        designationModel = kendo.observable(
                {
                    designation: {
                        id: "",
                        version: "",
                        name: "",
                        shortName: ""
                    }
                }
        );
        kendo.bind($("#application_top_panel"), designationModel);
    }

</script>
