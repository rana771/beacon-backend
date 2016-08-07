package com.athena.mis.kendo.grid

/**
 * Created by Mohammad Ali Azam on 9/22/2014.
 */
public interface FilterOperator {

    static final String EQUAL = "eq"
    static final String NOT_EQUAL = "neq"
    static final String STARTS_WITH = "startswith";
    static final String ENDS_WITH = "endswith";
    static final String CONTAINS = "contains";
    static final String DOES_NOT_CONTAIN = "doesnotcontain";
    static final String GREATER_THAN = "gt";
    static final String GREATER_THAN_OR_EQUAL_TO = "gte";
    static final String LESS_THAN = "lt";
    static final String LESS_THAN_OR_EQUAL_TO = "lte";

}