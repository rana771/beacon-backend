package com.athena.mis

import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.session.AppSessionService
import com.athena.mis.kendo.grid.FilterOperator
import com.athena.mis.kendo.grid.FilterOption
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

import javax.sql.DataSource
import java.text.DecimalFormat

//@CompileStatic        @todo: enable in grails-2
class BaseService {

    def sessionFactory

    GrailsApplication grailsApplication

    @Autowired
    AppSessionService appSessionService

    static transactional = false

    // used in action services
    public static final String COUNT = "count"
    public static final String LIST = "list"
    public static final String TO = " To "
    public static final String PERCENTAGE = "%"
    public static final String PIPE = "|"
    public static final String COMA = ","
    public static final String SINGLE_QUOTE = "'"
    public static final String EMPTY_SPACE_COMA = " , "
    public static final String EMPTY_SPACE = ''
    public static final String SINGLE_SPACE = ' '
    public static final String PARENTHESIS_START = " ( "
    public static final String PARENTHESIS_END = " ) "
    public static final String THIRD_BRACKET_START = "["
    public static final String THIRD_BRACKET_END = "]"
    public static final String SEMICOLON = ";"
    public static final String COLON = ":"
    public static final String SINGLE_DOT = "."
    public static final String THREE_DOTS = "..."
    public static final String QUESTION_SIGN = "?"
    public static final String BR = "<br>"
    public static final String UNDERSCORE = "_"
    public static final String HYPHEN = "-"
    public static final String SLASH = "/"
    public static final String STR_ZERO = "0"
    public static final String NOT_APPLICABLE = "N/A"
    public static final String NOT_GIVEN = "Not Given"
    public static final String NONE = "None"
    public static final String IS_ERROR = 'isError'
    public static final String MESSAGE = 'message'
    public static final String ENTITY = 'entity'
    public static final String VERSION = 'version'
    public static final String HAS_ASSOCIATION = "hasAssociation"
    public static final String COMPANY_LOGO = "companyLogo"
    public static final String YES = "YES"
    public static final String TRUE = 'true'
    public static final String FALSE = 'false'
    public static final String ALL = "ALL"
    public static final String NEW_LINE = "\r\n"
    public static final String ERROR_FOR_INVALID_INPUT = "Error occurred for invalid inputs"
    public static final String EXCEPTION = "exception"
    public static final String COMMON_REPORT_DIR = 'COMMON_REPORT_DIR'
    public static final String PLEASE_SELECT_LEVEL = 'Please Select...'

    public static final String DB_CURRENCY_FORMAT = "FM৳ 99,99,999,99,99,990.0099"
    public static final String DB_CURRENCY_FORMAT_CSV = "FM99999999999990.00"
    public static final String DB_QUANTITY_FORMAT = "FM99,99,999,99,99,990.0099"
    public static final String DB_QUANTITY_FORMAT_CSV = "FM99999999999990.0099"

    public static final String PDF_EXTENSION = ".pdf"
    public static final String XLS_EXTENSION = ".xls"
    public static final String CSV_EXTENSION = ".csv"
    public static final String TEXT_EXTENSION = ".txt"
    public static final String XML_EXTENSION = ".xml"
    // --- end ---

    public final static int DEFAULT_RESULT_PER_PAGE = 10;
    public final static String ASCENDING_SORT_ORDER = "asc";
    public final static String DEFAULT_SORT_ORDER = "desc";

    public final static String RESULT_PER_PAGE_KENDO = "take";
    public final static String OFFSET_KENDO = "skip";
    public final static String SORT_COLUMN_KENDO = "sort[0][field]";
    public final static String SORT_ORDER_KENDO = "sort[0][dir]";
    public final static String ID = "id";
    public final static String DEFAULT_SORT_COLUMN = ID;
    public final
    static String ENTITY_NOT_FOUND_ERROR_MESSAGE = "No entity found with this id or might have been deleted by someone!";
    int pageNumber = 1;
    int resultPerPage = DEFAULT_RESULT_PER_PAGE;
    String sortColumn = DEFAULT_SORT_COLUMN;
    String sortOrder = DEFAULT_SORT_ORDER;
    int start;

    // for single search
    String queryType;
    String query;

    // for combined search
    List<String> lstQueryType = []
    List<String> lstQuery = []

    private static final String EXECUTING_SQL = " SQL : "
    private static final String EXECUTING_CONTENT = " CONTENT : "
    private static final String VALUES = " Values : "
    private static final String COMPANY_ID_COLUMN = "companyId";

    DataSource dataSource;

    // Kendo filter options
    Map<Long, FilterOption> filterOptions;

    public List executeInsertSql(String query) {
        Sql sql = new Sql(dataSource)
        consolePrint(query)
        return sql.executeInsert(query)
    }

    public List executeInsertSql(String query, Map params) {
        Sql sql = new Sql(dataSource)
        consolePrint(query, params)
        return sql.executeInsert(query, params)
    }

    public int executeUpdateSql(String query) {
        Sql sql = new Sql(dataSource)
        consolePrint(query)
        return sql.executeUpdate(query)
    }

    public int executeUpdateSql(String query, Map params) {
        Sql sql = new Sql(dataSource)
        consolePrint(query, params)
        return sql.executeUpdate(query, params)
    }

    public boolean executeSql(String query) {
        Sql sql = new Sql(dataSource)
        consolePrint(query)
        return sql.execute(query)
    }

    public boolean executeSql(String query, Map params) {
        Sql sql = new Sql(dataSource)
        consolePrint(query, params)
        return sql.execute(query, params)
    }

    public List<GroovyRowResult> executeSelectSql(String query) {
        Sql sql = new Sql(dataSource)
        consolePrint(query)
        return sql.rows(query)
    }

    public List<GroovyRowResult> executeSelectSql(String query, Map params) {
        Sql sql = new Sql(dataSource)
        consolePrint(query, params)
        return sql.rows(query, params)
    }

    private String printSql = null
    private boolean isPrint

    // check value for console print
    private boolean isPrintSql() {
        if (printSql) return isPrint

        printSql = grailsApplication.config.application.printSql
        isPrint = Boolean.parseBoolean(printSql)
        return isPrint
    }

    // ... and add executeDeleteSql, executeInsertSql, count and so on as needed.
    protected void consolePrint(String query, Object params) {
        boolean isPrintSql = isPrintSql()
        if (isPrintSql) {
            println "${EXECUTING_SQL}${query}}"
            if (params) println "${VALUES}${params.toString()}"
        }
    }

    protected void consolePrint(String content) {
        boolean isPrintSql = isPrintSql()
        if (isPrintSql) {
            println "${EXECUTING_CONTENT}${content}"
        }
    }

    /**
     * Initializes list/filter related request parameters for kendo grid
     * @param params request parameters
     */
    void initListing(GrailsParameterMap params) {

        initPagination(params);
        initSorting(params);

        resetFilter();
        parseFilterParams(params);

    }

    /**
     * Parses the request parameters to see if filter options are sent
     * from the browser; if so, it wraps each filter option in FilterOption
     * object which contains the following information:
     * <ul>
     *     <li>Field: Name of the filter to be filtered (hence Domain property)</li>
     *     <li>Operator: Filter operator to be used (e.g., eq for EQUAl, gt for GREATER THAN etc.)</li>
     *     <li>Value: Value to be sought while searching domains</li>
     * </ul>
     *
     * @param params request parameters
     */
    private void parseFilterParams(GrailsParameterMap params) {

        def indexerPattern = ~/(\[\d+\])(\[.*\])/;
        def pattern = ~/filter\[filters\].*/;
        this.filterOptions = new HashMap<Long, FilterOption>();
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String name = entry.key.toString();
            if (pattern.matcher(name).matches()) {
                String prop = name.replaceFirst(/filter\[filters\]/, '');
                def matcher = indexerPattern.matcher(prop);
                if (matcher.matches()) {
                    String index = matcher.group(1).replaceAll(~/\[|\]/, '');
                    String property = matcher.group(2).replaceAll(~/\[|\]/, '');

                    Long optionKey = index.toLong();
                    FilterOption filter = filterOptions.get(optionKey);
                    if (!filter) {
                        filter = new FilterOption();
                        filterOptions.put(optionKey, filter);
                    }

                    try {
                        FilterOption.getField(property).set(filter, entry.value.trim());
                    } catch (Exception ignored) {
                        ignored.printStackTrace();
                    }
                }
            }
        }

    }

    /**
     * Initializes pagination attributes, such as how many records to return
     * and query offset
     *
     * @param params Request parameters
     */
    private void initPagination(GrailsParameterMap params) {
        this.resultPerPage = params.int(RESULT_PER_PAGE_KENDO)
        this.start = params.int(OFFSET_KENDO)    // query offset
    }

    /**
     * Initializes sort options for object list, such as which column to sort in which order
     *
     * @param params Request parameters
     */
    private void initSorting(GrailsParameterMap params) {
        this.sortColumn = params.get(SORT_COLUMN_KENDO) ? params.get(SORT_COLUMN_KENDO) : DEFAULT_SORT_COLUMN
        this.sortOrder = params.get(SORT_ORDER_KENDO) ? params.get(SORT_ORDER_KENDO) : DEFAULT_SORT_ORDER
    }

    /**
     * Resets filter options
     */
    private void resetFilter() {
        if (this.filterOptions != null) {
            this.filterOptions.clear();
        }
    }

    /**
     * A grid is being filtered only when there are filter options mapped in this.filterOptions
     *
     * @return true if there is at least one FilterOption in this.filterOptions, false otherwise
     */
    protected boolean isFiltering() {
        return (this.filterOptions != null && this.filterOptions.size() > 0);
    }

    /**
     * Lists domain objects, return a subset of the list based on
     * record size per page. It also sorts the result based on
     * sort column and order
     *
     * @param domainClass Domain class instance to be listed
     * @return list of domains
     */
    protected Map listForGrid(Class domainClass, Closure additionalFilter) {
        DefaultGrailsDomainClass gClass = new DefaultGrailsDomainClass(domainClass)
        List lst = domainClass.withCriteria {
            if (gClass.hasProperty(COMPANY_ID_COLUMN))
                eq(COMPANY_ID_COLUMN, companyId)
            if (additionalFilter) {
                additionalFilter.delegate = delegate
                additionalFilter()
            }
            maxResults(this.resultPerPage)
            firstResult(this.start)
            order(this.sortColumn, this.sortOrder)
//            setReadOnly(true)
        } as List;

        // Count query for the above criteria using projection
        List counts = domainClass.withCriteria {
            if (gClass.hasProperty(COMPANY_ID_COLUMN))
                eq(COMPANY_ID_COLUMN, companyId)
            if (additionalFilter) {
                additionalFilter.delegate = delegate
                additionalFilter()
            }
            projections { rowCount() };
        } as List;

        int total = counts[0] as int;
        return [list: lst, count: total];

    }

    /**
     * Initializes listing attributes such as start offset, result per page and sort column
     * and sort order, then it determines whether teh request is for searching the domains
     * for a given filter criteria or to search all domains, it eitehr filter the domain entity
     * or return list of all domain entities. Domain class is specified by the domainClass param
     *
     * @param params Request parameters
     * @param domainClass Domain class being searched
     * @return Loist of domain objects
     */
    protected Map getSearchResult(Map parameterMap, Class domainClass, Closure additionalFilter = null) {
        GrailsParameterMap params = (GrailsParameterMap) parameterMap
        this.initListing(params)
        Map resultMap = null
        if (this.isFiltering()) {
            resultMap = this.filterForGrid(domainClass, additionalFilter)
        } else {
            resultMap = this.listForGrid(domainClass, additionalFilter)
        }
        return resultMap
    }

    /**
     * Builds dynamic criteria based on filter options mapped in this.filterOptions
     *
     * @see this.parseFilterParams
     *
     * @param domainClass Domain to search using criteria
     * @return List of domains found in search and its total
     */
    protected Map filterForGrid(Class domainClass, Closure additionalFilter) {

        List<FilterOption> filterOptionList = this.filterOptions.values() as List<FilterOption>;

        DefaultGrailsDomainClass gClass = new DefaultGrailsDomainClass(domainClass);
        GrailsDomainClassProperty[] properties = gClass.properties;

        // Searches domain with the criteria based on filter options mapped in this.filterOptions
        // It dynamically creates conditions based on filter operator sent through the
        // kendo grid
        List lst = domainClass.withCriteria {
            if (gClass.hasProperty(COMPANY_ID_COLUMN))
                eq(COMPANY_ID_COLUMN, this.getCompanyId())
            for (FilterOption filterOption : filterOptionList) {
                switch (filterOption.operator) {
                    case FilterOperator.STARTS_WITH:
                        ilike("${filterOption.field}", "${filterOption.value}%")
                        break;

                    case FilterOperator.ENDS_WITH:
                        ilike("${filterOption.field}", "%${filterOption.value}")
                        break;


                    case FilterOperator.CONTAINS:
                        ilike("${filterOption.field}", "%${filterOption.value}%")
                        break;


                    case FilterOperator.DOES_NOT_CONTAIN:
                        not {
                            ilike("${filterOption.field}", "%${filterOption.value}%")
                        }
                        break;

                    case FilterOperator.EQUAL:
                        eq("${filterOption.field}", convertValueToType(properties, filterOption.field, filterOption.value.toString()))
                        break;

                    case FilterOperator.NOT_EQUAL:
                        ne("${filterOption.field}", convertValueToType(properties, filterOption.field, filterOption.value.toString()))
                        break;

                    case FilterOperator.GREATER_THAN:
                        gt("${filterOption.field}", convertValueToType(properties, filterOption.field, filterOption.value.toString()))
                        break;

                    case FilterOperator.GREATER_THAN_OR_EQUAL_TO:
                        gte("${filterOption.field}", convertValueToType(properties, filterOption.field, filterOption.value.toString()))
                        break;

                    case FilterOperator.LESS_THAN:
                        lt("${filterOption.field}", convertValueToType(properties, filterOption.field, filterOption.value.toString()))
                        break;

                    case FilterOperator.LESS_THAN_OR_EQUAL_TO:
                        lte("${filterOption.field}", convertValueToType(properties, filterOption.field, filterOption.value.toString()))
                        break;


                }
            }
            if (additionalFilter) {
                additionalFilter.delegate = delegate
                additionalFilter()
            }
            maxResults(this.resultPerPage)
            firstResult(this.start)
            order(this.sortColumn, this.sortOrder)
//            setReadOnly(true)
        } as List;

        // Count query for the above criteria using projection
        List counts = domainClass.withCriteria {
            if (gClass.hasProperty(COMPANY_ID_COLUMN))
                eq(COMPANY_ID_COLUMN, this.getCompanyId())
            for (FilterOption filterOption : filterOptionList) {
                switch (filterOption.operator) {
                    case FilterOperator.STARTS_WITH:
                        ilike("${filterOption.field}", "${filterOption.value}%")
                        break;

                    case FilterOperator.ENDS_WITH:
                        ilike("${filterOption.field}", "%${filterOption.value}")
                        break;

                    case FilterOperator.CONTAINS:
                        ilike("${filterOption.field}", "%${filterOption.value}%")
                        break;

                    case FilterOperator.DOES_NOT_CONTAIN:
                        not {
                            ilike("${filterOption.field}", "%${filterOption.value}%")
                        }
                        break;

                    case FilterOperator.EQUAL:
                        eq("${filterOption.field}", convertValueToType(properties, filterOption.field, filterOption.value.toString()))
                        break;

                    case FilterOperator.NOT_EQUAL:
                        ne("${filterOption.field}", convertValueToType(properties, filterOption.field, filterOption.value.toString()))
                        break;

                    case FilterOperator.GREATER_THAN:
                        gt("${filterOption.field}", convertValueToType(properties, filterOption.field, filterOption.value.toString()))
                        break;

                    case FilterOperator.GREATER_THAN_OR_EQUAL_TO:
                        gte("${filterOption.field}", convertValueToType(properties, filterOption.field, filterOption.value.toString()))
                        break;

                    case FilterOperator.LESS_THAN:
                        lt("${filterOption.field}", convertValueToType(properties, filterOption.field, filterOption.value.toString()))
                        break;

                    case FilterOperator.LESS_THAN_OR_EQUAL_TO:
                        lte("${filterOption.field}", convertValueToType(properties, filterOption.field, filterOption.value.toString()))
                        break;
                }
            }
            if (additionalFilter) {
                additionalFilter.delegate = delegate
                additionalFilter()
            }
            projections { rowCount() };
        } as List;
        int total = counts[0] as int
        return [list: lst, count: total]
    }

    /**
     * Sets result state to indicate error by adding a key "isError" with TRUE value
     *
     * @param resultMap Existing result map where the result state will be put
     * @param message Optional message to be set (e.g., to be shown in UI)
     * @return Same resultMap with a status set
     */
    protected Map setError(Map resultMap, String message) {
        resultMap.put(IS_ERROR, Boolean.TRUE)
        if (message != null) {
            resultMap.put(MESSAGE, message)
        }
        return resultMap
    }

    /**
     * Sets result state to indicate success by adding a key "isError" with FALSE value
     *
     * @param resultMap Existing result map where the result state will be put
     * @param message Optional message to be set (e.g., to be shown in UI)
     * @return Same resultMap with a status set
     */
    protected Map setSuccess(Map resultMap, String message) {
        resultMap.put(IS_ERROR, Boolean.FALSE)
        if (message != null) {
            resultMap.put(MESSAGE, message)
        }
        return resultMap
    }

    /**
     * Cast the given value based on the property type by searching the property type in
     * properties array.
     *
     * @param properties Array of properties (of a domain)
     * @param propertyName Name of the property type  of which will be used to cast the value
     * @param propertyValue Property value being converted
     * @return Converted value
     */
    private Object convertValueToType(GrailsDomainClassProperty[] properties, String propertyName,
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
                case char.class:
                case Character.class:
                    return propertyValue;
                case short.class:
                case byte.class:
                case int.class:
                case Integer.class:
                    return Integer.parseInt(propertyValue);
                case long.class:
                case Long.class:
                    return Long.parseLong(propertyValue);
                case float.class:
                case Float.class:
                    return Float.parseFloat(propertyValue);
                case double.class:
                case Double.class:
                    return Double.parseDouble(propertyValue);
                case boolean.class:
                case Boolean.class:
                    return Boolean.parseBoolean(propertyValue);
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

    protected long getCompanyId() {
        if (appSessionService.appUser == null) {
            return 1L
        }
        return appSessionService.companyId;
    }

    protected AppUser getAppUser() {
        return appSessionService.appUser;
    }

    /**
     * Get List of long ids from UI parameter
     * @param params - GrailsParameterMap containing underscore separated string of ids
     * @param key - key to get ids from GrailsParameterMap
     * @return - list of long ids
     */
    public static final List<Long> getIdsFromParams(Map params, String key) {
        List<Long> lstIds = []
        String str = (String) params.get(key)
        List lstStringIds = str.split(UNDERSCORE)
        for (int i = 0; i < lstStringIds.size(); i++) {
            lstIds << Long.parseLong(lstStringIds[i].toString())
        }
        return lstIds
    }

    private static final String AMOUNT_FORMAT = "৳ ##,##,##0.00"

    public static final String makeAmountWithThousandSeparator(double amount) {
        DecimalFormat myFormatter = new DecimalFormat(AMOUNT_FORMAT);
        String output = myFormatter.format(amount);
        return output
    }

    private static final String AMOUNT_FORMAT_WITHOUT_CURRENCY = "##,##,##0.00"

    public static final String formatAmountWithoutCurrency(double amount) {
        DecimalFormat myFormatter = new DecimalFormat(AMOUNT_FORMAT_WITHOUT_CURRENCY);
        String output = myFormatter.format(amount);
        return output
    }

    //Domain
    public static final String DOMAIN_BANK_BRANCH = "Branch" // "BankBranch"
    public static final String DOMAIN_TASK = "Task"
    public static final String DOMAIN_TASK_TRACE = "Task Trace"
    public static final String DOMAIN_COUNTRY = "Country"
    public static final String DOMAIN_CURRENCY_CONVERSION = "Currency Conversion"
    public static final String DOMAIN_SARB_CURRENCY_CONVERSION = "Sarb Currency Conversion"
    public static final String DOMAIN_COMPANY = "Company"
    public static final String DOMAIN_AGENT = "Agent"
    public static final String DOMAIN_AGENT_CURRENCY_POSTING = "Agent Currency Posting"
    private static final String HAS = " has "
    private static final String ASSOCIATED = " associated "
    private static final String S_PLURAL = "(s)"

    // write a message to get associative information
    public String getMessageOfAssociation(String name, int count, String domainName) {
        return name + HAS + count + ASSOCIATED + domainName + S_PLURAL

    }
}
