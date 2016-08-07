package com.athena.mis.application.actions.systementity

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.entity.SystemEntityType
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.SystemEntityService
import com.athena.mis.application.service.SystemEntityTypeService
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
import org.springframework.transaction.annotation.Transactional

/**
 *  Create new system entity object, add to respective cache utility & show in grid
 *  For details go through Use-Case doc named 'CreateSystemEntityActionService'
 */
class CreateSystemEntityActionService extends BaseService implements ActionServiceIntf {

    private static final String SYSTEM_ENTITY = "systemEntity"
    private static final String CREATE_SUCCESS_MSG = "System entity has been successfully saved"
    private static final String DUPLICATE_ENTITY_MSG = "Same system entity already exists"
    private static final String TYPE_NOT_FOUND_MSG = "System entity type not found"

    private Logger log = Logger.getLogger(getClass())

    SystemEntityService systemEntityService
    SystemEntityTypeService systemEntityTypeService
    AppSystemEntityCacheService appSystemEntityCacheService

    @Autowired(required = false)
    BudgPluginConnector budgBudgetImplService
    @Autowired(required = false)
    AccPluginConnector accAccountingImplService
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
    DocumentPluginConnector documentImplService
    @Autowired(required = false)
    ELearningPluginConnector elearningImplService
    @Autowired(required = false)
    DataPipeLinePluginConnector dataPipeLineImplService

    /**
     * Check pre conditions before creating system entity
     * 1. Check required params existence
     * 2. Check if system entity type object exists or not
     * 3. Build SystemEntity object with parameters
     * 4. Validate the system entity object
     * 5. Check uniqueness of system entity name by type and company
     * @param parameters - serialized params from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map executePreCondition(Map params) {
        try {
            // Check here for required params are present
            if ((!params.systemEntityTypeId)) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long systemEntityTypeId = Long.parseLong(params.systemEntityTypeId.toString())
            // Check if system entity type object exists or not
            SystemEntityType systemEntityType = (SystemEntityType) systemEntityTypeService.read(systemEntityTypeId)
            if (!systemEntityType) {
                return super.setError(params, TYPE_NOT_FOUND_MSG)
            }
            // Build SystemEntity object with parameters
            SystemEntity systemEntity = buildSystemEntityObject(params, systemEntityType)
            // Validate the system entity object as per domain field type
            systemEntity.validate()
            if (systemEntity.hasErrors()) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            // Uniqueness check of system entity name
            int duplicateCount = systemEntityService.countByTypeAndKeyIlikeAndCompanyId(systemEntity.type, systemEntity.key, systemEntity.companyId)
            if (duplicateCount > 0) {
                return super.setError(params, DUPLICATE_ENTITY_MSG)
            }
            params.put(SYSTEM_ENTITY, systemEntity)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Save system entity object in DB and update cache utility
     * 1. Get system entity object from executePreCondition
     * 2. Update cache utility
     * This method is in transactional block and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            SystemEntity sysEntity = (SystemEntity) result.get(SYSTEM_ENTITY)
            systemEntityService.create(sysEntity)   // save new SystemEntity object in DB
            updateSystemEntityCacheService(sysEntity)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * There is no postCondition, so return the same map as received
     * @param result - resulting map from execute
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * 1. put success message
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, CREATE_SUCCESS_MSG)
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     * @param result - map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Update system entity cache service
     * @param systemEntity - object of SystemEntity
     * @return true/false
     */
    private boolean updateSystemEntityCacheService(SystemEntity systemEntity) {
        switch (systemEntity.pluginId) {
            case PluginConnector.PLUGIN_ID:
                appSystemEntityCacheService.initByType(systemEntity.type)
                break
            case DocumentPluginConnector.PLUGIN_ID:
                documentImplService.initByType(systemEntity.type)
                break
            case DataPipeLinePluginConnector.PLUGIN_ID:
                dataPipeLineImplService.initByType(systemEntity.type)
                break
            case ELearningPluginConnector.PLUGIN_ID:
                elearningImplService.initByType(systemEntity.type)
                break
            case PtPluginConnector.PLUGIN_ID:
                ptProjectTrackImplService.initByType(systemEntity.type)
                break
            case ExchangeHousePluginConnector.PLUGIN_ID:
                exchangeHouseImplService.initByType(systemEntity.type)
                break
            case SarbPluginConnector.PLUGIN_ID:
                sarbImplService.initByType(systemEntity.type)
                break
            case AccPluginConnector.PLUGIN_ID:
                accAccountingImplService.initByType(systemEntity.type)
                break
            case InvPluginConnector.PLUGIN_ID:
                invInventoryImplService.initByType(systemEntity.type)
                break
            case BudgPluginConnector.PLUGIN_ID:
                budgBudgetImplService.initByType(systemEntity.type)
                break
            case ArmsPluginConnector.PLUGIN_ID:
                armsImplService.initByType(systemEntity.type)
                break
            default:
                return false
        }
        return true
    }

    /**
     * Build system entity object
     * @param params - serialized params from UI
     * @param systemEntityType - object of SystemEntityType
     * @return - SystemEntity object
     */
    private SystemEntity buildSystemEntityObject(Map params, SystemEntityType systemEntityType) {
        SystemEntity systemEntity = new SystemEntity(params)
        AppUser appUser = super.getAppUser()
        long companyId = appUser.companyId
        long pluginId = systemEntityType.pluginId
        long systemEntityId = systemEntityService.getSystemEntityId(companyId)
        systemEntity.id = systemEntityId
        systemEntity.type = systemEntityType.id
        systemEntity.companyId = companyId
        systemEntity.reservedId = 0
        systemEntity.pluginId = pluginId
        systemEntity.createdBy = appUser.id
        systemEntity.createdOn = new Date()
        systemEntity.updatedBy = 0L
        return systemEntity
    }
}
