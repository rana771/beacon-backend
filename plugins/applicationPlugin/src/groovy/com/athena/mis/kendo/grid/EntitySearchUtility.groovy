package com.athena.mis.kendo.grid

import com.athena.mis.BaseService
import com.athena.mis.application.entity.Project
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

import java.text.SimpleDateFormat

/**
 * Created by Mohammad Ali Azam on 9/22/2014.
 */
class EntitySearchUtility {

    /**
     * Builds dynamic criteria based on filter options sent through the params
     *
     * @param baseService BaseService that pulls some search info, such as from which index search, how many records
     * to return etc.
     * @param domain Domain to search using criteria
     * @param params Request parameter
     * @return List of domains found in search and its total
     */
    public static Map searchForGrid(BaseService baseService, def domain, GrailsParameterMap params) {

        Map<Long, FilterOption> filterOptions = FilterParamsUtil.parseFilterParams(params);
        List<FilterOption> filterOptionList = filterOptions.values() as List<FilterOption>;

        DefaultGrailsDomainClass gClass = new DefaultGrailsDomainClass(domain);
        GrailsDomainClassProperty[] properties = gClass.properties;

        // Searches domain with the criteria based on filter options supplied
        // it dynamically creates conditions based on filter operator sent through the
        // kendo grid
        List lst = domain.withCriteria {
            for (FilterOption filterOption : filterOptionList) {
                switch (filterOption.operator) {
                    case FilterOperator.CONTAINS:
                        ilike("${filterOption.field}", "%${filterOption.value}%")
                        break;

                    case FilterOperator.EQUAL:
                        eq("${filterOption.field}", convertValueToType(properties, filterOption.field, filterOption.value.toString()))
                        break;

                    case FilterOperator.GREATER_THAN:
                        gt("${filterOption.field}", convertValueToType(properties, filterOption.field, filterOption.value.toString()))
                        break;

                    case FilterOperator.GREATER_THAN_OR_EQUAL_TO:
                        gte("${filterOption.field}", convertValueToType(properties, filterOption.field, filterOption.value.toString()))
                        break;
                }
            }
            maxResults(baseService.resultPerPage)
            firstResult(baseService.start)
            order(baseService.sortColumn, baseService.sortOrder)
        } as List;


        // Count query for the above criteria using projection
        List counts = domain.withCriteria {
            for (FilterOption filterOption : filterOptionList) {
                switch (filterOption.operator) {
                    case FilterOperator.CONTAINS:
                        ilike("${filterOption.field}", "%${filterOption.value}%")
                        break;

                    case FilterOperator.EQUAL:
                        eq("${filterOption.field}", convertValueToType(properties, filterOption.field, filterOption.value.toString()))
                        break;

                    case FilterOperator.GREATER_THAN_OR_EQUAL_TO:
                        gte("${filterOption.field}", convertValueToType(properties, filterOption.field, filterOption.value.toString()))
                        break;

                    case FilterOperator.GREATER_THAN:
                        gt("${filterOption.field}", convertValueToType(properties, filterOption.field, filterOption.value.toString()))
                        break;
                }

            }
            projections { rowCount() };
        } as List;
        int total = counts[0] as int
        return [list: lst, count: total]
    }

    private static Object convertValueToType(GrailsDomainClassProperty[] properties, String propertyName,
                                             String propertyValue) {

        Class propertyType = null;
        for (GrailsDomainClassProperty property : properties) {
            if (property.name.equals(propertyName)) {
                propertyType = property.type;
                break;
            }
        }

        if (propertyType != null) {
            switch (propertyType) {
                case int.class:
                case Integer.class:
                    return Integer.parseInt(propertyValue);
                    break;
                case Date.class:
                    return Date.parse("yyyy-MM-dd HH:mm:ss", propertyValue);
                    break;
                case String.class:
                    return propertyValue;
                    break;
            }
        }

        return propertyValue;
    }
}
