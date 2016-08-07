<% import grails.persistence.Event %>
<% import org.codehaus.groovy.grails.orm.hibernate.support.ClosureEventTriggeringInterceptor as Events %>
<script type="text/x-kendo-template" id="gridToolbar">
<ul id="menuGrid" class="kendoGridMenu">
    <sec:access url="/${propertyName}/update">
        <li onclick="edit${className}();"><i class="fa fa-edit"></i>Edit</li>
    </sec:access>
    <sec:access url="/${propertyName}/delete">
        <li onclick="delete${className}();"><i class="fa fa-trash-o"></i>Delete</li>
    </sec:access>

    <li onclick="reloadKendoGrid();"><i class="fa fa-refresh"></i>Refresh</li>
</ul>
</script>

<script language="javascript">
    var entityTypeId, appUserEntityTypeId;
    var grid${className};
    var dataSource, ${propertyName}Model;

    \$(document).ready(function () {
        onLoad${className}Page();
        init${className}Grid();
        initObservable();
        // init kendo switch
    });

    function onLoad${className}Page() {
        // initialize form with kendo validator & bind onSubmit method
        initializeForm(\$("#${propertyName}Form"), onSubmit${className});

        // update page title
        \$(document).attr('title', "MIS - Create ${className}");
        loadNumberedMenu(MENU_ID_APPLICATION, "#${propertyName}/show");
        entityTypeId = \$("#entityTypeId").val();
        appUserEntityTypeId = \$("#appUserEntityTypeId").val();
    }

    function executePreCondition() {
        if (!validateForm(\$("#${propertyName}Form"))) {   // check kendo validation
            return false;
        }
        if (!customValidateDate(\$("#startDate"), 'Start date', \$("#endDate"), 'end date')) {
            return false;
        }
        return true;
    }

    function onSubmit${className}() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled(\$('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if (\$('#id').val().isEmpty()) {
            actionUrl = "\${createLink(controller:'${propertyName}', action: 'create')}";
        } else {
            actionUrl = "\${createLink(controller: '${propertyName}', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#${propertyName}Form").serialize(),
            url: actionUrl,
            success: function (data, textStatus) {
                executePostCondition(data);
                setButtonDisabled(\$('#create'), false);
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
                var newEntry = result.${propertyName};
                if (\$('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = grid${className}.dataSource.data();
                    gridData.unshift(newEntry);
                } else if (newEntry != null) { // updated existing
                    var selectedRow = grid${className}.select();
                    var allItems = grid${className}.items();
                    var selectedIndex = allItems.index(selectedRow);
                    grid${className}.removeRow(selectedRow);
                    grid${className}.dataSource.insert(selectedIndex, newEntry);
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
        clearForm(\$("#${propertyName}Form"), \$('#name'));
        initObservable();
        \$('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function addContent() {
        if (executeCommonPreConditionForSelectKendo(grid${className}, '${propertyName}') == false) {
            return;
        }
        showLoadingSpinner(true);
        var ${propertyName}Id = getSelectedIdFromGridKendo(grid${className});
        var loc = "\${createLink(controller:'appAttachment', action: 'show')}?oId=" + ${propertyName}Id + "&url=${propertyName}/show" + "&entityTypeId=" + entityTypeId;
        router.navigate(formatLink(loc));
        return false;
    }

    function reloadKendoGrid() {
        grid${className}.dataSource.filter([]);
    }

    function addUser${className}() {
        if (executeCommonPreConditionForSelectKendo(grid${className}, '${propertyName}') == false) {
            return;
        }
        showLoadingSpinner(true);
        var ${propertyName}Id = getSelectedIdFromGridKendo(grid${className});
        var loc = "\${createLink(controller:'appUserEntity', action: 'show')}?oId=" + ${propertyName}Id + "&url=${propertyName}/show" + "&entityTypeId=" + appUserEntityTypeId;
        router.navigate(formatLink(loc));
        return false;
    }

    function reloadGrid() {
        grid${className}.dataSource.filter([]);
    }

    function delete${className}() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true); // Spinner Show on AJAX Call
        var ${propertyName}Id = getSelectedIdFromGridKendo(grid${className});
        \$.ajax({
            url: "\${createLink(controller:'${propertyName}', action: 'delete')}?id=" + ${propertyName}Id,
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
        if (executeCommonPreConditionForSelectKendo(grid${className}, '${propertyName}') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected ${className}?')) {
            return false;
        }
        return true;
    }

    function executePostConditionForDelete(data) {
        if (data.isError) {
            showError(data.message);
            return false;
        }
        var row = grid${className}.select();
        row.each(function () {
            grid${className}.removeRow(\$(this));
        });
        resetForm();
        showSuccess(data.message);
    }

    function edit${className}() {
        if (executeCommonPreConditionForSelectKendo(grid${className}, '${propertyName}') == false) {
            return;
        }
        showCreatePanel(\$('div.expand-div'), \$('#grid${className}'));
        resetForm();
        showLoadingSpinner(true);
        var ${propertyName} = getSelectedObjectFromGridKendo(grid${className});
        show${className}(${propertyName});
        showLoadingSpinner(false);
    }

    function show${className}(${propertyName}) {
        ${propertyName}Model.set('${propertyName}', ${propertyName});
        \$('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function initDatasource() {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/${propertyName}/list",
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
        <%  excludedProps = Event.allEvents.toList() << 'version' << 'test' << 'id'<<'createdBy'<<'createdOn'<<'updatedBy'<<'updatedOn'
        props = domainClass.properties.findAll { !excludedProps.contains(it.name) }
        Collections.sort(props, comparator.constructors[0].newInstance([domainClass] as Object[]))
        props.eachWithIndex  { p, index ->
            if (!Collection.class.isAssignableFrom(p.type)) {
                cp = domainClass.constrainedProperties[p.name]
                display = (cp ? cp.display : true)
                if (display) { %>
        <%if(p.type == Date.class){%>
                     ${p.name}: {type: "date"}
        <% }else if(p.type == double || p.type == Double.class || p.type == float || p.type == Float.class){ %>
        ${p.name}: {type: "number"}
        <% }else if(p.type == int || p.type == Integer.class || p.type == long || p.type == Long.class || p.type == byte || p.type == Byte.class){ %>
        ${p.name}: {type: "number"}
        <%}else{%>
            ${p.name}: {type: "string"}
        <% } %>
        <% if(props.size() -1 > index){ %>,<%} }   }   } %>
                    }
                },
                parse: function (data) {
                    checkIsErrorGridKendo(data);
                    return data;
                }
            },
            sort: {field: 'name', dir: 'asc'},  // default sort
            pageSize: \${com.athena.mis.BaseService.DEFAULT_RESULT_PER_PAGE},
            serverPaging: true,
            serverFiltering: true,
            serverSorting: true
            <g:if test="\${oId!=null}">
            , filter: {field: "id", operator: "equal", value: \${oId}}
            </g:if>
        });
    }

    function init${className}Grid() {
        initDatasource();
        \$("#grid${className}").kendoGrid({
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
        <%  excludedProps = Event.allEvents.toList() << 'version' << 'test' << 'id'<<'createdBy'<<'createdOn'<<'updatedBy'<<'updatedOn'
        props = domainClass.properties.findAll { !excludedProps.contains(it.name) }
        Collections.sort(props, comparator.constructors[0].newInstance([domainClass] as Object[]))
        props.eachWithIndex  { p, index ->
            if (!Collection.class.isAssignableFrom(p.type)) {
                cp = domainClass.constrainedProperties[p.name]
                display = (cp ? cp.display : true)
                if (display) { %>
        <%if(p.type == Date.class){%>
                        {
                    field: "${p.name}",
                    title: "${p.naturalName}",
                    sortable: true,
                    filterable: {cell: {template: formatFilterableDate}},
                    template: "#= kendo.toString(kendo.parseDate(startDate, 'yyyy-MM-dd'), 'dd-MMM-yyyy') #"
                }
        <% }else if(p.type == double || p.type == Double.class || p.type == float || p.type == Float.class){ %>
                {
                    field: "${p.name}",
                    title: "${p.naturalName}",
                    sortable: false,
                    filterable: false,
                    attributes: {style: setAlignRight()},
                    headerAttributes: {style: setAlignRight()}
                }
        <% }else if(p.type == int || p.type == Integer.class || p.type == long || p.type == Long.class || p.type == byte || p.type == Byte.class){ %>
                {
                    field: "${p.name}",
                    title: "${p.naturalName}",
                    sortable: false,
                    filterable: false,
                    attributes: {style: setAlignRight()},
                    headerAttributes: {style: setAlignRight()}
                }
        <%}else{%>
        {
        field: "${p.name}",
        title: "${p.naturalName}",
        sortable: true,
        filterable: {cell: {operator: "contains", dataSource: getBlankDataSource()}}
        }
        <% } %>
        <% if(props.size() -1 > index){ %>,<%} }   }   } %>
            ],
            filterable: {mode: "row"},
            toolbar: kendo.template(\$("#gridToolbar").html())
        });
        grid${className} = \$("#grid${className}").data("kendoGrid");
        \$("#menuGrid").kendoMenu();
    }

    function initObservable() {
        ${propertyName}Model = kendo.observable(
                {
        ${propertyName}: {
        <%  excludedProps = Event.allEvents.toList() << 'version' << 'test' << 'id'<<'createdBy'<<'createdOn'<<'updatedBy'<<'updatedOn'
        props = domainClass.properties.findAll { !excludedProps.contains(it.name) }
        Collections.sort(props, comparator.constructors[0].newInstance([domainClass] as Object[]))
        props.eachWithIndex  { p, index ->
            if (!Collection.class.isAssignableFrom(p.type)) {
                cp = domainClass.constrainedProperties[p.name]
                display = (cp ? cp.display : true)
                if (display) { %>
        <%if(p.type == Boolean.class || p.type==boolean.class){%>
            ${p.name}:false
        <%}else{%>
        ${p.name}:""
        <% } %>
        <% if(props.size() -1 > index){ %>,<%} }   }   } %>

                    }
                }
        );
        kendo.bind(\$("#application_top_panel"), ${propertyName}Model);
    }



</script>
