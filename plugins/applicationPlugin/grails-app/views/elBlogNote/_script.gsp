

<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <sec:access url="/elBlogNote/update">
        <li onclick="editElBlogNote();"><i class="fa fa-edit"></i>Edit</li>
    </sec:access>
    <sec:access url="/elBlogNote/delete">
        <li onclick="deleteElBlogNote();"><i class="fa fa-trash-o"></i>Delete</li>
    </sec:access>

    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script language="javascript">
    var entityTypeId, appUserEntityTypeId;
    var gridElBlogNote;
    var dataSource, elBlogNoteModel;

    $(document).ready(function () {
        onLoadElBlogNotePage();
        initElBlogNoteGrid();
        initObservable();
        // init kendo switch
    });

    function onLoadElBlogNotePage() {
        // initialize form with kendo validator & bind onSubmit method
        initializeForm($("#elBlogNoteForm"), onSubmitElBlogNote);

        // update page title
        $(document).attr('title', "MIS - Create ElBlogNote");
        loadNumberedMenu(MENU_ID_APPLICATION, "#elBlogNote/show");
        entityTypeId = $("#entityTypeId").val();
        appUserEntityTypeId = $("#appUserEntityTypeId").val();
    }

    function executePreCondition() {
        if (!validateForm($("#elBlogNoteForm"))) {   // check kendo validation
            return false;
        }
        if (!customValidateDate($("#startDate"), 'Start date', $("#endDate"), 'end date')) {
            return false;
        }
        return true;
    }

    function onSubmitElBlogNote() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'elBlogNote', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'elBlogNote', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#elBlogNoteForm").serialize(),
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
                var newEntry = result.elBlogNote;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = gridElBlogNote.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = gridElBlogNote.select();
                    var allItems = gridElBlogNote.items();
                    var selectedIndex = allItems.index(selectedRow);
                    gridElBlogNote.removeRow(selectedRow);
                    gridElBlogNote.dataSource.insert(selectedIndex, newEntry);
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
        clearForm($("#elBlogNoteForm"), $('#name'));
        initObservable();
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function addContent() {
        if (executeCommonPreConditionForSelectKendo(gridElBlogNote, 'elBlogNote') == false) {
            return;
        }
        showLoadingSpinner(true);
        var elBlogNoteId = getSelectedIdFromGridKendo(gridElBlogNote);
        var loc = "${createLink(controller:'appAttachment', action: 'show')}?oId=" + elBlogNoteId + "&url=elBlogNote/show" + "&entityTypeId=" + entityTypeId;
        router.navigate(formatLink(loc));
        return false;
    }

    function reloadKendoGrid() {
        gridElBlogNote.dataSource.filter([]);
    }

    function addUserElBlogNote() {
        if (executeCommonPreConditionForSelectKendo(gridElBlogNote, 'elBlogNote') == false) {
            return;
        }
        showLoadingSpinner(true);
        var elBlogNoteId = getSelectedIdFromGridKendo(gridElBlogNote);
        var loc = "${createLink(controller:'appUserEntity', action: 'show')}?oId=" + elBlogNoteId + "&url=elBlogNote/show" + "&entityTypeId=" + appUserEntityTypeId;
        router.navigate(formatLink(loc));
        return false;
    }

    function reloadGrid() {
        gridElBlogNote.dataSource.filter([]);
    }

    function deleteElBlogNote() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true); // Spinner Show on AJAX Call
        var elBlogNoteId = getSelectedIdFromGridKendo(gridElBlogNote);
        $.ajax({
            url: "${createLink(controller:'elBlogNote', action: 'delete')}?id=" + elBlogNoteId,
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
        if (executeCommonPreConditionForSelectKendo(gridElBlogNote, 'elBlogNote') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected ElBlogNote?')) {
            return false;
        }
        return true;
    }

    function executePostConditionForDelete(data) {
        if (data.isError) {
            showError(data.message);
            return false;
        }
        var row = gridElBlogNote.select();
        row.each(function () {
            gridElBlogNote.removeRow($(this));
        });
        resetForm();
        showSuccess(data.message);
    }

    function editElBlogNote() {
        if (executeCommonPreConditionForSelectKendo(gridElBlogNote, 'elBlogNote') == false) {
            return;
        }
        showCreatePanel($('div.expand-div'), $('#gridElBlogNote'));
        resetForm();
        showLoadingSpinner(true);
        var elBlogNote = getSelectedObjectFromGridKendo(gridElBlogNote);
        showElBlogNote(elBlogNote);
        showLoadingSpinner(false);
    }

    function showElBlogNote(elBlogNote) {
        elBlogNoteModel.set('elBlogNote', elBlogNote);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function initDatasource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/elBlogNote/list",
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
        
        
            address: {type: "string"}
        
        ,
        
            code: {type: "string"}
        
        ,
        
                     enrolleDate: {type: "date"}
        
        ,
        
            name: {type: "string"}
        
        
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

    function initElBlogNoteGrid() {
        initDatasource();
        $("#gridElBlogNote").kendoGrid({
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
        field: "address",
        title: "Address",
        sortable: true,
        filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
        }
        
        ,
        
        {
        field: "code",
        title: "Code",
        sortable: true,
        filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
        }
        
        ,
        
                        {
                    field: "enrolleDate",
                    title: "Enrolle Date",
                    sortable: true,
                    filterable: {cell: {template: formatFilterableDate}},
                    template: "#= kendo.toString(kendo.parseDate(startDate, 'yyyy-MM-dd'), 'dd-MMM-yyyy') #"
                }
        
        ,
        
        {
        field: "name",
        title: "Name",
        sortable: true,
        filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
        }
        
        
            ],
            filterable: {mode: "row"},
            toolbar: kendo.template($("#gridToolbar").html())
        });
        gridElBlogNote = $("#gridElBlogNote").data("kendoGrid");
        $("#menuGrid").kendoMenu();
    }

    function initObservable() {
        elBlogNoteModel = kendo.observable(
                {
        elBlogNote: {
        
        
        address:""
        
        ,
        
        code:""
        
        ,
        
        enrolleDate:""
        
        ,
        
        name:""
        
        

                    }
                }
        );
        kendo.bind($("#application_top_panel"), elBlogNoteModel);
    }



</script>
