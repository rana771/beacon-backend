<% import grails.persistence.Event %>
<% import org.codehaus.groovy.grails.orm.hibernate.support.ClosureEventTriggeringInterceptor as Events %>
<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, \$('#grid${className}'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    Create ${FormatUtil.getNameString(className)}
                    <app:myFavourite>
                    </app:myFavourite>
                </div>
            </div>
            <form id="${propertyName}Form" name="${propertyName}Form" class="form-horizontal form-widgets" role="form">
                <div class="panel-body" style="display: none">
                 <input type="hidden" name="id" id="id" data-bind="value: ${propertyName}.id"/>
                 <input type="hidden" name="version" id="version" data-bind="value: ${propertyName}.version"/>

               <div class="form-group">

                       <%  excludedProps = Event.allEvents.toList() << 'version' << 'test' << 'id'<<'createdBy'<<'createdOn'<<'updatedBy'<<'updatedOn'
                       props = domainClass.properties.findAll { !excludedProps.contains(it.name) }
                       Collections.sort(props, comparator.constructors[0].newInstance([domainClass] as Object[]))
                       props.eachWithIndex  { p, index ->
                           if (!Collection.class.isAssignableFrom(p.type)) {
                               cp = domainClass.constrainedProperties[p.name]
                               display = (cp ? cp.display : true)
                               if (display) { %>
                       <%if(p.type == Date.class){%>
                   <div class="col-md-6">
                       <label class="col-md-3 control-label label-required" for="name">${p.naturalName}:</label>

                       <div class="col-md-6">
                           <app:dateControl
                                   name="${p.name}"
                                   required="true"
                                   validationMessage="Required"
                                   tabindex="3"
                                   value=""
                                   data-bind="value: ${p.naturalName}.${p.name}">
                           </app:dateControl>
                       </div>

                       <div class="col-md-3 pull-left">
                           <span class="k-invalid-msg" data-for="${p.name}"></span>
                       </div>
                   </div>
                       <% }else if(p.type == double || p.type == Double.class || p.type == float || p.type == Float.class){ %>
                   <div class="col-md-6">
                       <label class="col-md-3 control-label label-required" for="name">${p.naturalName}:</label>

                       <div class="col-md-6">
                           <input type="text" class="k-textbox" id="${p.name}" name="${p.name}" maxlength="255"
                                  required
                                  validationMessage="Required" data-bind="value: ${propertyName}.${p.name}"
                                  tabindex="1" autofocus/>
                       </div>

                       <div class="col-md-3 pull-left">
                           <span class="k-invalid-msg" data-for="${p.name}"></span>
                       </div>
                   </div>
                       <% }else if(p.type == int || p.type == Integer.class || p.type == long || p.type == Long.class || p.type == byte || p.type == Byte.class){ %>
                   <div class="col-md-6">
                       <label class="col-md-3 control-label label-required" for="name">${p.naturalName}:</label>

                       <div class="col-md-6">
                           <input type="text" class="k-textbox" id="${p.name}" name="${p.name}" maxlength="255"
                                  required
                                  validationMessage="Required" data-bind="value: ${propertyName}.${p.name}"
                                  tabindex="1" autofocus/>
                       </div>

                       <div class="col-md-3 pull-left">
                           <span class="k-invalid-msg" data-for="${p.name}"></span>
                       </div>
                   </div>
                       <%}else{%>
                       <div class="col-md-6">
                           <label class="col-md-3 control-label label-required" for="name">${p.naturalName}:</label>

                           <div class="col-md-6">
                               <input type="text" class="k-textbox" id="${p.name}" name="${p.name}" maxlength="255"
                                required
                                      validationMessage="Required" data-bind="value: ${propertyName}.${p.name}"
                                      tabindex="1" autofocus/>
                           </div>

                           <div class="col-md-3 pull-left">
                               <span class="k-invalid-msg" data-for="${p.name}"></span>
                           </div>
                       </div>

                       <% } %>
                      <% }   }   } %>

                   </div>
                </div>

                <div class="panel-footer" style="display: none">
                    <app:ifAllUrl urls="/${propertyName}/create,/${propertyName}/update">
                        <button id="create" name="create" type="submit" data-role="button"
                                class="k-button k-button-icontext"
                                role="button"
                                aria-disabled="false" tabindex="11"><span class="k-icon k-i-plus"></span>Create
                        </button>
                    </app:ifAllUrl>

                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button"
                            aria-disabled="false" onclick='resetForm();' tabindex="12"><span
                            class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="row">
        <div id="grid${className}"></div>
    </div>
</div>








