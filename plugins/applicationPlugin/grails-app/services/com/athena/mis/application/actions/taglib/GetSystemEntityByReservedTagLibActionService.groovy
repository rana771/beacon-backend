package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.SystemEntityService
import com.athena.mis.integration.accounting.AccPluginConnector
import com.athena.mis.integration.arms.ArmsPluginConnector
import com.athena.mis.integration.budget.BudgPluginConnector
import com.athena.mis.integration.datapipeline.DataPipeLinePluginConnector
import com.athena.mis.integration.document.DocumentPluginConnector
import com.athena.mis.integration.elearning.ELearningPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.integration.inventory.InvPluginConnector
import com.athena.mis.integration.projecttrack.PtPluginConnector
import com.athena.mis.integration.sarb.SarbPluginConnector
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/*Renders html of hidden field for reserved system entity object*/

class GetSystemEntityByReservedTagLibActionService extends BaseService implements ActionServiceIntf {

    AppSystemEntityCacheService appSystemEntityCacheService
    SystemEntityService systemEntityService
    @Autowired(required = false)
    AccPluginConnector accAccountingImplService
    @Autowired(required = false)
    BudgPluginConnector budgBudgetImplService
    @Autowired(required = false)
    InvPluginConnector invInventoryImplService
    @Autowired(required = false)
    ExchangeHousePluginConnector exchangeHouseImplService
    @Autowired(required = false)
    PtPluginConnector ptProjectTrackImplService
    @Autowired(required = false)
    ArmsPluginConnector armsImplService
    @Autowired(required = false)
    SarbPluginConnector sarbImplService
    @Autowired(required = false)
    DataPipeLinePluginConnector dataPipeLineImplService
    @Autowired(required = false)
    ELearningPluginConnector elearningImplService
    @Autowired(required = false)
    DocumentPluginConnector documentImplService

    private Logger log = Logger.getLogger(getClass())

    private static final String INPUT_END = ">"
    private static final String NAME = 'name'
    private static final String TYPE_ID = 'typeId'
    private static final String RESERVED_IDS = 'reservedId'

    /** Build a map containing properties of html hidden field
     *  Set values of properties
     * @param parameters - a map of given attributes
     * @param obj - N/A
     * @return - a map containing all necessary properties with value
     */
    public Map executePreCondition(Map params) {
        try {
            String name = params.get(NAME)
            String strTypeId = params.get(TYPE_ID)
            def reservedIds = params.get(RESERVED_IDS)
            if ((!name) || (!reservedIds) || (!strTypeId) || (strTypeId.length() == 0)) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            List<Long> lstReservedIds = []
            if (reservedIds instanceof ArrayList) {
                lstReservedIds = reservedIds
            } else {
                lstReservedIds << (Long) reservedIds
            }
            long typeId = Long.parseLong(strTypeId)
            params.put(TYPE_ID, new Long(typeId))            // set the typeId
            params.put(RESERVED_IDS, lstReservedIds)    // set the reservedId

            return params

        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /** Build the html for hidden field
     * @param parameters - map returned from executePreCondition
     * @param obj - N/A
     * @return - html string for hidden field
     */
    public Map execute(Map result) {
        try {
            Long typeId = (Long) result.get(TYPE_ID)
            List<Long> lstReservedIds = (List<Long>) result.get(RESERVED_IDS)
            long companyId = super.getCompanyId()
            List<Long> lstSysEntityIds = []
            lstReservedIds.each {
                long pluginId = Long.parseLong(result.pluginId.toString())
                long systemEntityId = getIdOfSystemEntityFromCacheService(typeId.longValue(), it.longValue(), companyId, pluginId)
                lstSysEntityIds << Long.valueOf(systemEntityId)
            }
            String strSysEntityIds = systemEntityService.buildCommaSeparatedStringOfIds(lstSysEntityIds)
            String html = buildHtmlForHiddenField(strSysEntityIds, result)
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

    private long getIdOfSystemEntityFromCacheService(long systemEntityTypeId, long reservedId, long companyId, long pluginId) {
        SystemEntity systemEntity
        switch (pluginId) {
            case PluginConnector.PLUGIN_ID:
                systemEntity = appSystemEntityCacheService.readByReservedId(reservedId, systemEntityTypeId, companyId)
                break
            case DocumentPluginConnector.PLUGIN_ID:
                systemEntity = documentImplService.readByReservedId(reservedId, systemEntityTypeId, companyId)
                break
            case DataPipeLinePluginConnector.PLUGIN_ID:
                systemEntity = dataPipeLineImplService.readByReservedId(reservedId, systemEntityTypeId, companyId)
                break
            case PtPluginConnector.PLUGIN_ID:
                systemEntity = ptProjectTrackImplService.readByReservedId(reservedId, systemEntityTypeId, companyId)
                break
            case AccPluginConnector.PLUGIN_ID:
                systemEntity = accAccountingImplService.readByReservedId(reservedId, systemEntityTypeId, companyId)
                break
            case InvPluginConnector.PLUGIN_ID:
                systemEntity = invInventoryImplService.readByReservedId(reservedId, systemEntityTypeId, companyId)
                break
            case BudgPluginConnector.PLUGIN_ID:
                systemEntity = budgBudgetImplService.readByReservedId(reservedId, systemEntityTypeId, companyId)
                break
            case ExchangeHousePluginConnector.PLUGIN_ID:
                systemEntity = exchangeHouseImplService.readByReservedId(reservedId, systemEntityTypeId, companyId)
                break
            case SarbPluginConnector.PLUGIN_ID:
                systemEntity = sarbImplService.readByReservedId(reservedId, systemEntityTypeId, companyId)
                break
            case ArmsPluginConnector.PLUGIN_ID:
                systemEntity = armsImplService.readByReservedId(reservedId, systemEntityTypeId, companyId)
                break
            case ELearningPluginConnector.PLUGIN_ID:
                systemEntity = elearningImplService.readByReservedId(reservedId, systemEntityTypeId, companyId)
                break
            default:
                return 0L
        }
        return systemEntity.id
    }

    /**
     * Get id of system entity
     * @param systemEntityTypeId - type id of system entity
     * @param reservedId - reserved id of system entity
     * @param companyId -id of company
     * @return - id of system entity
     */
    private long getIdOfSystemEntity(long systemEntityTypeId, long reservedId, long companyId) {
        SystemEntity systemEntity
        switch (systemEntityTypeId) {
            default:
                return 0L
        }
        return systemEntity.id
    }

    /** Generate the html for hidden field
     * @param strSysEntityIds - id of system entity
     * @param hiddenFieldAttributes - a map containing the attributes of hidden field
     * @return - html string for hidden field
     */
    private String buildHtmlForHiddenField(String strSysEntityIds, Map hiddenFieldAttributes) {
        // read map values
        String name = hiddenFieldAttributes.get(NAME)

        String html = "<input type='hidden' name= '${name}' id= '${name}' value= '${strSysEntityIds}'" + INPUT_END
        return html
    }
}
