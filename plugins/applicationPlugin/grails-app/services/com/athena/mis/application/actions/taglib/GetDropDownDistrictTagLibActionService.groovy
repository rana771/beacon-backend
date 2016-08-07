package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppBank
import com.athena.mis.application.entity.AppBankBranch
import com.athena.mis.application.entity.District
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppBankBranchService
import com.athena.mis.application.service.AppBankService
import com.athena.mis.application.service.DistrictService
import com.athena.mis.integration.arms.ArmsPluginConnector
import grails.converters.JSON
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

class GetDropDownDistrictTagLibActionService extends BaseService implements ActionServiceIntf {

    AppBankService appBankService
    DistrictService districtService
    AppBankBranchService appBankBranchService
    @Autowired(required = false)
    ArmsPluginConnector armsImplService

    private static final String NAME = 'name'
    private static final String ID = 'id'
    private static final String URL = 'url'
    private static final String HINTS_TEXT = 'hints_text'
    private static final String SHOW_HINTS = 'show_hints'
    private static final String DEFAULT_VALUE = 'default_value'
    private static final String PLEASE_SELECT = 'Please Select...'
    private static final String REQUIRED = 'required'
    private static final String VALIDATION_MESSAGE = 'validationmessage'
    private static final String DEFAULT_MESSAGE = 'Required'
    private static final String DATA_MODEL_NAME = 'data_model_name'
    private static final String BANK_ID = 'bank_id'
    private static final String COUNTRY_ID = 'country_id'
    private static final String PROCESS = 'process'
    private static final String INSTRUMENT = 'instrument'
    private static final String SELECT_END = "</select>"

    private Logger log = Logger.getLogger(getClass())

    /** Build a map containing properties of html select
     *  1. Set default values of properties
     *  2. Overwrite default properties if defined in parameters
     * @param parameters - a map of given attributes
     * @return - a map containing all necessary properties with value
     */
    public Map executePreCondition(Map params) {
        try {
            String name = params.get(NAME)
            String id = params.get(ID)
            String dataModelName = params.get(DATA_MODEL_NAME)
            if ((!id) || (!name) || (!dataModelName)) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }

            // Set default values for optional parameters
            params.put(HINTS_TEXT, params.hints_text ? params.hints_text : PLEASE_SELECT)
            params.put(SHOW_HINTS, params.show_hints ? new Boolean(Boolean.parseBoolean(params.show_hints.toString())) : Boolean.TRUE)
            params.put(REQUIRED, params.required ? new Boolean(Boolean.parseBoolean(params.required.toString())) : Boolean.FALSE)
            params.put(VALIDATION_MESSAGE, params.validationmessage ? params.validationmessage : DEFAULT_MESSAGE)
            params.put(PROCESS, params.process ? new Long(Long.parseLong(params.process.toString())) : null)
            params.put(INSTRUMENT, params.instrument ? new Long(Long.parseLong(params.instrument.toString())) : null)
            params.put(COUNTRY_ID, params.country_id ? new Long(Long.parseLong(params.country_id.toString())) : null)
            params.put(DEFAULT_VALUE, params.default_value ? new Long(Long.parseLong(params.default_value.toString())) : null)

            return params

        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /** Get the list of District
     *  build the html for 'select'
     * @param parameters - map returned from preCondition
     * @return - html string for 'select'
     */
    @Transactional(readOnly = true)
    public Map execute(Map result) {
        try {
            List<District> lstDistrict = getDistrictListForDropDown(result)
            List lstForDropDown = buildListForDropDown(lstDistrict)
            String html = buildDropDown(lstForDropDown, result)
            result.html = html
            return result
        } catch (Exception e) {
            log.error(e.message)
            throw new RuntimeException(e)
        }
    }

    /**
     * Do nothing in post condition
     * @param result - A map returned by execute method
     * @return - returned the received map
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * do nothing for build success operation
     * @param result - A map returned by post condition method.
     * @return - returned the same received map containing isError = false
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    /**
     * Do nothing here
     * @param result - map returned from previous any of method
     * @return - a map containing isError = true & relevant error message
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /** Generate the html for select
     * @param districtList - list for select 'options'
     * @param dropDownAttributes - a map containing the attributes of drop down
     * @return - html string for select
     */

    private List<District> getDistrictListForDropDown(Map dropDownAttributes) {
        List<District> lstDistrict = []
        long companyId = super.companyId
        AppBank bank = appBankService.getSystemBank(companyId)
        Long processId = (Long) dropDownAttributes.get(PROCESS)
        Long instrumentId = (Long) dropDownAttributes.get(INSTRUMENT)
        if (processId && processId.longValue() > 0 && instrumentId && instrumentId.longValue() > 0) {
            long purchaseId = getPurchaseId()
            if (processId == purchaseId) {
                lstDistrict = listDistrictForPurchase(instrumentId, dropDownAttributes)
            } else {
                lstDistrict = listDistrictForDropDown(dropDownAttributes)
            }
            Object processInstrument = armsImplService.getRmsProcessInstrumentMapping(processId, instrumentId)
            if (processInstrument.districtId) {
                dropDownAttributes.put(DEFAULT_VALUE, processInstrument.districtId)
            } else {
                dropDownAttributes.put(DEFAULT_VALUE, null)
            }
        } else {
            lstDistrict = listDistrictForDropDown(dropDownAttributes)
        }
        return lstDistrict
    }

    private String buildDropDown(List lstDistrict, dropDownAttributes) {
        // read map values
        String name = dropDownAttributes.get(NAME)
        String dataModelName = dropDownAttributes.get(DATA_MODEL_NAME)
        String hintsText = dropDownAttributes.get(HINTS_TEXT)
        Boolean showHints = dropDownAttributes.get(SHOW_HINTS)
        Long defaultValue = (Long) dropDownAttributes.get(DEFAULT_VALUE)

        Map attributes = (Map) dropDownAttributes
        String strAttributes = EMPTY_SPACE
        attributes.each {
            if (it.value) {
                strAttributes = strAttributes + "${it.key} = '${it.value}' "
            }
        }
        String strDefaultValue = defaultValue ? defaultValue : ''
        if (showHints.booleanValue()) {
            lstDistrict = districtService.listForKendoDropdown(lstDistrict, null, hintsText)
        }
        String jsonData = lstDistrict as JSON

        String html = "<select ${strAttributes}>\n" + SELECT_END
        String script = """ \n
            <script type="text/javascript">
                \$(document).ready(function () {
                    if (${dataModelName}){
                        ${dataModelName}.destroy();
                    }
                    \$('#${name}').kendoDropDownList({
                        dataTextField:'name',
                        dataValueField:'id',
                        dataSource:${jsonData},
                        value:'${strDefaultValue}'
                    });
                });
                ${dataModelName} = \$("#${name}").data("kendoDropDownList");
            </script>
        """
        return html + script
    }

    /**
     * Build list for drop down
     * @param lstDistrict - list of district
     * @return - list for drop down
     */
    private List buildListForDropDown(List<District> lstDistrict) {
        List lstDropDown = []
        if ((!lstDistrict) || (lstDistrict.size() <= 0)) {
            return lstDropDown
        }
        for (int i = 0; i < lstDistrict.size(); i++) {
            District district = (District) lstDistrict[i]
            lstDropDown << [id: district.id, name: district.name, isGlobal: district.isGlobal]
        }
        return lstDropDown
    }

    /**
     * 1.Get empty list if bank_id equals empty space
     * 2.Get lstDistricts by bankId
     * @param dropDownAttributes -return from previous method
     * @return-all districts if bank_id is 0 otherwise returns districts by bankId
     */
    private List<District> listDistrictForDropDown(Map dropDownAttributes) {
        List<District> lstDistricts = []
        if (dropDownAttributes.get(BANK_ID).equals(EMPTY_SPACE)) {
            return lstDistricts
        } else if (dropDownAttributes.get(BANK_ID)) {
            long bankId = Long.parseLong(dropDownAttributes.get(BANK_ID).toString())
            long countryId = Long.parseLong(dropDownAttributes.get(COUNTRY_ID).toString())
            lstDistricts = (List<District>) listByBankId(bankId, countryId)
        }
        return lstDistricts
    }

    private List<District> listByBankId(long bankId, long countryId) {
        long companyId = super.companyId
        List<District> lstDistrict = []
        if (bankId == 0) {
            if (countryId == 0) {
                lstDistrict = (List<District>) districtService.list(companyId)
            } else {
                lstDistrict = districtService.findAllByCountryIdAndCompanyId(countryId, companyId)
            }
            return lstDistrict
        }
        List<AppBankBranch> lstBankBranchFiltered = appBankBranchService.findAllByBankIdAndCompanyId(bankId, companyId)
        List<Long> lstDistrictIds = lstBankBranchFiltered.collect { it.districtId }
        lstDistrictIds.unique(true)
        lstDistrict = districtService.findAllByIdInList(lstDistrictIds)
        return lstDistrict
    }

    private List<District> listDistrictForPurchase(long instrumentId, Map dropDownAttributes) {
        List<District> districtList = []
        if (!dropDownAttributes.get(BANK_ID) || instrumentId <= 0) {
            return districtList
        }
        long bankId = Long.parseLong(dropDownAttributes.get(BANK_ID).toString())
        String query = """
        select distinct(district_id) as district_id from rms_purchase_instrument_mapping
        where instrument_type_id = :instrumentId and bank_id = :bankId
        """
        Map queryParam = [instrumentId: instrumentId, bankId: bankId]
        List<GroovyRowResult> lstDistrict = executeSelectSql(query, queryParam)
        List<Long> lstDistrictIds = []
        for (int i = 0; i < lstDistrict.size(); i++) {
            lstDistrictIds << (long) lstDistrict[i].district_id
        }
        districtList = districtService.findAllByIdInList(lstDistrictIds)
        return districtList
    }

    private long getPurchaseId() {
        long purchaseId = 0L
        Long reservedPurchaseId = (Long) armsImplService.getProcessTypePurchase()
        if (reservedPurchaseId && reservedPurchaseId.longValue() > 0) {
            SystemEntity purchase = (SystemEntity) armsImplService.readByReservedId(reservedPurchaseId, armsImplService.getSystemEntityProcessType(), companyId)
            purchaseId = purchase.id
        }
        return purchaseId
    }
}
