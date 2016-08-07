package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.Company
import com.athena.mis.integration.arms.ArmsPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import org.apache.log4j.Logger

import javax.servlet.http.HttpServletRequest

/**
 *  Service class for basic company CRUD (Create, Update, Delete)
 *  For details go through Use-Case doc named 'CompanyService'
 */
class CompanyService extends BaseDomainService {

    private Logger log = Logger.getLogger(getClass())

    AppVersionService appVersionService

    private Map<String, Company> companyMap = new HashMap<String, Company>()

    @Override
    public void init() {
        domainClass = Company.class
    }

    /**
     * @return -list of company
     */
    @Override
    public List list() {
        return Company.list(sort: Company.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true)
    }

    private static final String SEQUENCE_SQL = "SELECT NEXTVAL('company_id_seq')::bigint AS id;"

    public long getCompanyIdFromSequence() {
        List results = executeSelectSql(SEQUENCE_SQL)
        long companyId = results[0].id
        return companyId
    }

    private static final String HTTP_SLASH = 'http://'

    public Company read(HttpServletRequest request) {
        String url = request.getServerName()

        if (request.getServerPort() > 80) {
            url = url + COLON + request.getServerPort()
        }

        String urlForQuery = PERCENTAGE + url
        String urlWithHttp = HTTP_SLASH + url

        Company company = companyMap.get(url)
        if (company) return company

        company = companyMap.get(urlWithHttp)
        if (company) return company

        company = Company.findByWebUrlIlike(urlForQuery, [readOnly: true])
        if (company == null) return null

        if (company.webUrl.equals(url) || company.webUrl.equals(urlWithHttp)) {
            companyMap.put(company.webUrl, company)
            return company
        }
        return null
    }

    /**
     * get count of company by name
     * @param name - name of company
     * @return - count of company by name
     */
    public int countByNameIlike(String name) {
        int count = Company.countByNameIlike(name)
        return count
    }

    /**
     * get count of company by code
     * @param code - code of company
     * @return - count of company by code
     */
    public int countByCodeIlike(String code) {
        int count = Company.countByCodeIlike(code)
        return count
    }

    /**
     * get count of company by name and id
     * @param name - name of company
     * @param id - id of company
     * @return - count of company by name and id
     */
    public int countByNameIlikeAndIdNotEqual(String name, long id) {
        int count = Company.countByNameIlikeAndIdNotEqual(name, id)
        return count
    }

    /**
     * get count of company by code and id
     * @param code - code of company
     * @param id - id of company
     * @return - count of company by code and id
     */
    public int countByCodeIlikeAndIdNotEqual(String code, long id) {
        int count = Company.countByCodeIlikeAndIdNotEqual(code, id)
        return count
    }

    /**
     * get count of company by url
     * @param url - url of company
     * @return - count of company by url
     */
    public int countByWebUrlIlike(String url) {
        int count = Company.countByWebUrlIlike(url)
        return count
    }

    /**
     * get count of company by url and id
     * @param url - url of company
     * @param id - id of company
     * @return - count of company by url and id
     */
    public int countByWebUrlIlikeAndIdNotEqual(String url, long id) {
        int count = Company.countByWebUrlIlikeAndIdNotEqual(url, id)
        return count
    }

    /**
     * insert default data into database when application starts with bootstrap
     */
    public Company createDefaultData() {
        try {
            int count = appVersionService.countByPluginId(PluginConnector.PLUGIN_ID)
            if (count > 0) {
                return null
            }

            String webUrl = grailsApplication.config.application.webUrl
            Company company
            if (PluginConnector.isPluginInstalled(ExchangeHousePluginConnector.PLUGIN_NAME)) {
                company = new Company(name: "Southeast Exchange Company PTY Ltd.", title: "Subsidiary of Southeast Bank Ltd.",
                        isActive: true, code: "SECL", address1: 'Address line 1', countryId: 2, createdBy: 1,
                        createdOn: new Date(), currencyId: 4, webUrl: webUrl, contactName: 'Test Contact Name',
                        contactSurname: 'Test Contact Surname', contactPhone: '123456789'
                )
                company.id = getCompanyIdFromSequence()
                create(company)
            } else if (PluginConnector.isPluginInstalled(ArmsPluginConnector.PLUGIN_NAME)) {
                company = new Company(name: "Southeast Bank Ltd.", isActive: true, code: "SEBL", address1: 'Address line 1',
                        countryId: 1, createdBy: 1, createdOn: new Date(), currencyId: 1, webUrl: webUrl,
                        contactName: 'Test Contact Name', contactSurname: 'Test Contact Surname', contactPhone: '123456789'
                )
                company.id = getCompanyIdFromSequence()
                create(company)
            } else {
                company = new Company(name: "Athena Software Associates Ltd.", code: "athena", isActive: true,
                        address1: '206/A, Tejgaon Industrial Area, Dhaka-1208', countryId: 1, createdBy: 1,
                        createdOn: new Date(), currencyId: 1, webUrl: webUrl, contactName: 'Md Tajul Islam',
                        contactSurname: 'Tajul', contactPhone: '01511230055'
                )
                company.id = getCompanyIdFromSequence()
                create(company)
            }
            return company
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    private static final String SQL_SEQ_COMPANY = """
        CREATE SEQUENCE company_id_seq
        INCREMENT 1
        MINVALUE 1
        MAXVALUE 9223372036854775807
        START 1
        CACHE 1;
    """

    @Override
    public void createDefaultSchema() {
        String nameIndex = "create unique index company_name_idx on company(lower(name));"
        executeSql(nameIndex)
        String codeIndex = "create unique index company_code_idx on company(lower(code));"
        executeSql(codeIndex)
        String urlIndex = "create unique index company_web_url_idx on company(lower(web_url));"
        executeSql(urlIndex)
        executeSql(SQL_SEQ_COMPANY)
    }

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }
}
