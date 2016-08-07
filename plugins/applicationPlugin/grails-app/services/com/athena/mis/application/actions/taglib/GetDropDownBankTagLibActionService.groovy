package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppBank
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppBankService
import com.athena.mis.integration.arms.ArmsPluginConnector
import grails.converters.JSON
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

class GetDropDownBankTagLibActionService extends BaseService implements ActionServiceIntf {

    AppBankService appBankService
    @Autowired(required = false)
    ArmsPluginConnector armsImplService

    private static final String ID = 'id'
    private static final String NAME = 'name'
    private static final String HINTS_TEXT = 'hints_text'
    private static final String SHOW_HINTS = 'show_hints'
    private static final String DEFAULT_VALUE = 'default_value'
    private static final String PLEASE_SELECT = 'Please Select...'
    private static final String REQUIRED = 'required'
    private static final String VALIDATION_MESSAGE = 'validationmessage'
    private static final String DEFAULT_MESSAGE = 'Required'
    private static final String URL = 'url'
    private static final String PROCESS = 'process'
    private static final String INSTRUMENT = 'instrument'
    private static final String DATA_MODEL_NAME = 'data_model_name'
    private static final String SELECT_END = "</select>"
    private static final String COUNTRY_ID = "country_id"
    private static final String DISABLE_SYSTEM_BANK = "disable_system_bank"

    private Logger log = Logger.getLogger(getClass())

    /** Build a map containing properties of html select
     *  1. Set default values of properties
     *  2. Overwrite default properties if defined in parameters
     * @param params - a map of given attributes
     * @return - a map containing all necessary properties with value
     */
    public Map executePreCondition(Map params) {
        try {
            // Set default values for optional parameters
            params.put(HINTS_TEXT, params.hints_text ? params.hints_text : PLEASE_SELECT)
            params.put(SHOW_HINTS, params.show_hints ? new Boolean(Boolean.parseBoolean(params.show_hints.toString())) : Boolean.TRUE)
            params.put(REQUIRED, params.required ? new Boolean(Boolean.parseBoolean(params.required.toString())) : Boolean.FALSE)
            params.put(VALIDATION_MESSAGE, params.validationmessage ? params.validationmessage : DEFAULT_MESSAGE)
            params.put(DISABLE_SYSTEM_BANK, params.disable_system_bank ? new Boolean(Boolean.parseBoolean(params.disable_system_bank.toString())) : Boolean.FALSE)
            params.put(DEFAULT_VALUE, params.default_value ? new Long(Long.parseLong(params.default_value.toString())) : null)
            params.put(PROCESS, params.process ? new Long(Long.parseLong(params.process.toString())) : null)
            params.put(INSTRUMENT, params.instrument ? new Long(Long.parseLong(params.instrument.toString())) : null)
            params.put(COUNTRY_ID, params.country_id ? new Long(Long.parseLong(params.country_id.toString())) : null)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /** Get the list of Bank
     *  build the html for 'select'
     * @param result - map returned from preCondition
     * @return - html string for 'select'
     */
    @Transactional(readOnly = true)
    public Map execute(Map result) {
        try {
            List<AppBank> bankList = getBankListForDropDown(result)
            String html = buildDropDown(bankList, result)
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

    /**
     *
     * @param dropDownAttributes
     * @return
     */
    private List<AppBank> getBankListForDropDown(Map dropDownAttributes) {
        Long processId = (Long) dropDownAttributes.get(PROCESS)
        Long instrumentId = (Long) dropDownAttributes.get(INSTRUMENT)
        Long countryId = (Long) dropDownAttributes.get(COUNTRY_ID)
        Boolean disableSystemBank = (Boolean) dropDownAttributes.get(DISABLE_SYSTEM_BANK)
        List<AppBank> bankList = []
        if (processId && processId.longValue() > 0 && instrumentId && instrumentId.longValue() > 0) {
            long purchaseId = getPurchaseId()
            if (processId == purchaseId) {
                bankList = listBankForPurchase(instrumentId)
            } else {
                bankList = appBankService.list()
            }
            Object processInstrumentMapping = armsImplService.getRmsProcessInstrumentMapping(processId, instrumentId)
            if (processInstrumentMapping.bankId && processInstrumentMapping.bankId.longValue() > 0) {
                dropDownAttributes.put(DEFAULT_VALUE, processInstrumentMapping.bankId)
            } else {
                dropDownAttributes.put(DEFAULT_VALUE, null)
            }
        } else {
            if (countryId != null) {
                bankList = appBankService.listByCountryId(countryId.longValue())
            } else {
                bankList = appBankService.listNativeBank()
            }
        }

        if (disableSystemBank) {
            bankList = removeSystemBank(bankList)
        }
        return bankList
    }

    /**
     *
     * @param bankList
     * @return
     */
    private List<AppBank> removeSystemBank(List<AppBank> bankList) {
        List<AppBank> lstBank = []
        bankList.each {
            if (!it.isSystemBank) {
                lstBank << it
            }
        }
        return lstBank
    }

    /** Generate the html for select
     * @param lstBank - list for select 'options'
     * @param dropDownAttributes - a map containing the attributes of drop down
     * @return - html string for select
     */
    private String buildDropDown(List<AppBank> lstBank, Map dropDownAttributes) {
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

        String html = "<select ${strAttributes}>\n" + SELECT_END
        String strDefaultValue = defaultValue ? defaultValue : EMPTY_SPACE

        if (showHints.booleanValue()) {
            lstBank = appBankService.listForKendoDropdown(lstBank, null, hintsText)
        }
        String jsonData = lstBank as JSON

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
     *
     * @param instrumentId
     * @return
     */
    private List<AppBank> listBankForPurchase(long instrumentId) {
        if (instrumentId <= 0) {
            return []
        }
        String sql = """
        select distinct(bank_id) as bank_id from rms_purchase_instrument_mapping
        where instrument_type_id = :instrumentId
        """
        Map queryParam = [instrumentId: instrumentId]
        List<GroovyRowResult> lstResult = executeSelectSql(sql, queryParam)
        List<Long> lstBankIds = []
        for (int i = 0; i < lstResult.size(); i++) {
            lstBankIds << (Long) lstResult[i].bank_id
        }
        return appBankService.findAllByIdIn(lstBankIds)
    }

    /**
     *
     * @return
     */
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
