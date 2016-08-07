package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppBank
import com.athena.mis.application.entity.AppBankBranch
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppBankBranchService
import com.athena.mis.application.service.AppBankService
import com.athena.mis.integration.arms.ArmsPluginConnector
import grails.converters.JSON
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

class GetDropDownBankBranchTagLibActionService extends BaseService implements ActionServiceIntf {

    AppBankService appBankService
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
    private static final String PROCESS = 'process'
    private static final String INSTRUMENT = 'instrument'
    private static final String DISTRICT_ID = 'district_id'
    private static final String SELECT_END = "</select>"

    private Logger log = Logger.getLogger(getClass())

    /**
     * Build a map containing properties of html select
     * 1. Set default values of properties
     * 2. Overwrite default properties if defined in parameters
     * @param params -a map of given attributes
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
            params.put(HINTS_TEXT, params.hintsText ? params.hintsText : PLEASE_SELECT)
            params.put(SHOW_HINTS, params.showHints ? new Boolean(Boolean.parseBoolean(params.showHints.toString())) : Boolean.TRUE)
            params.put(REQUIRED, params.required ? new Boolean(Boolean.parseBoolean(params.required.toString())) : Boolean.FALSE)
            params.put(VALIDATION_MESSAGE, params.validationMessage ? params.validationMessage : DEFAULT_MESSAGE)
            params.put(DEFAULT_VALUE, params.default_value ? new Long(Long.parseLong(params.default_value.toString())) : null)
            params.put(PROCESS, params.process ? new Long(Long.parseLong(params.process.toString())) : null)
            params.put(INSTRUMENT, params.instrument ? new Long(Long.parseLong(params.instrument.toString())) : null)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Get the list of District
     * build the html for 'select'
     * @param result - a map returned by precondition method
     * @return - a map consisting html string for 'select'
     */
    @Transactional(readOnly = true)
    public Map execute(Map result) {
        try {
            List<AppBankBranch> lstBankBranch = getBankBranchListForDropDown(result)
            List lstForDropDown = buildListForDropDown(lstBankBranch)
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

    private List<AppBankBranch> getBankBranchListForDropDown(Map dropDownAttributes) {
        List<AppBankBranch> lstBankBranch = []
        long companyId = super.companyId
        AppBank bank = appBankService.getSystemBank(companyId)
        Long processId = (Long) dropDownAttributes.get(PROCESS)
        Long instrumentId = (Long) dropDownAttributes.get(INSTRUMENT)
        if (processId && processId.longValue() > 0 && instrumentId && instrumentId.longValue() > 0) {
            long purchaseId = getPurchaseId()
            if (processId == purchaseId) {
                lstBankBranch = listBankBranchForPurchase(instrumentId, dropDownAttributes)
            } else {
                lstBankBranch = listBankBranchForDropDown(dropDownAttributes, companyId)
            }
            Object processInstrument = armsImplService.getRmsProcessInstrumentMapping(processId, instrumentId)
            if (processInstrument.bankBranchId) {
                dropDownAttributes.put(DEFAULT_VALUE, processInstrument.bankBranchId)
            } else {
                dropDownAttributes.put(DEFAULT_VALUE, null)
            }
        } else {
            lstBankBranch = listBankBranchForDropDown(dropDownAttributes, companyId)
        }
        return lstBankBranch
    }

    private List<AppBankBranch> listBankBranchForDropDown(Map dropDownAttributes, long companyId) {
        if ((!dropDownAttributes.get(BANK_ID)) || (!dropDownAttributes.get(DISTRICT_ID))) {
            return []
        }
        long bankId = Long.parseLong(dropDownAttributes.get(BANK_ID).toString())
        long districtId = Long.parseLong(dropDownAttributes.get(DISTRICT_ID).toString())
        List<AppBankBranch> lstBankBranch = appBankBranchService.findAllByBankIdAndDistrictIdAndCompanyId(bankId, districtId, companyId)
        return lstBankBranch
    }

    private List buildListForDropDown(List lstBankBranch) {
        List lstDropDown = []
        if ((lstBankBranch == null) || (lstBankBranch.size() <= 0)) {
            return lstDropDown
        }
        for (int i = 0; i < lstBankBranch.size(); i++) {
            AppBankBranch bankBranch = (AppBankBranch) lstBankBranch[i]
            lstDropDown << [id: bankBranch.id, name: bankBranch.name, isGlobal: bankBranch.isGlobal]
        }
        return lstDropDown
    }
    /** Generate the html for select
     * @param districtList - list for select 'options'
     * @param dropDownAttributes - a map containing the attributes of drop down
     * @return - html string for select
     */
    private String buildDropDown(List<AppBankBranch> lstBankBranch, dropDownAttributes) {
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
            lstBankBranch = appBankBranchService.listForKendoDropdown(lstBankBranch, null, hintsText)
        }
        String jsonData = lstBankBranch as JSON

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

    private List<AppBankBranch> listBankBranchForPurchase(long instrumentId, Map dropDownAttributes) {
        List<AppBankBranch> bankBranchList = []
        if ((!dropDownAttributes.get(BANK_ID)) || (!dropDownAttributes.get(DISTRICT_ID)) || instrumentId <= 0) {
            return bankBranchList
        }
        long bankId = Long.parseLong(dropDownAttributes.get(BANK_ID).toString())
        long districtId = Long.parseLong(dropDownAttributes.get(DISTRICT_ID).toString())

        String query = """
        select distinct(bank_branch_id) as bank_branch_id from rms_purchase_instrument_mapping
        where instrument_type_id = :instrumentId and bank_id = :bankId and district_id = :districtId
        """
        Map queryParam = [instrumentId: instrumentId, bankId: bankId, districtId: districtId]
        List<GroovyRowResult> lstBankBranches = executeSelectSql(query, queryParam)
        List<Long> lstBankIds = []
        for (int i = 0; i < lstBankBranches.size(); i++) {
            lstBankIds << (long) lstBankBranches[i].bank_branch_id
        }
        bankBranchList = appBankBranchService.findAllByIdInList(lstBankIds)
        return bankBranchList
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
