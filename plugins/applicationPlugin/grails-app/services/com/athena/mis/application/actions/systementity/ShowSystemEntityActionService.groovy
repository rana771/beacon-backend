package com.athena.mis.application.actions.systementity

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntityType
import com.athena.mis.application.service.AppMyFavouriteService
import com.athena.mis.application.service.SystemEntityTypeService
import grails.transaction.Transactional
import org.apache.log4j.Logger

/**
 *  Show UI for system entity CRUD and list of system entity for grid
 *  For details go through Use-Case doc named 'ShowSystemEntityActionService'
 */
class ShowSystemEntityActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    SystemEntityTypeService systemEntityTypeService
    AppMyFavouriteService appMyFavouriteService

    private static final String SYSTEM_ENTITY_TYPE_NOT_FOUND = "System entity type not found"
    private static final String LEFT_MENU_PLUGIN = "pluginId"
    private static final String SYSTEM_ENTITY_TYPE_NAME = "systemEntityTypeName"
    private static final String SYSTEM_ENTITY_TYPE_ID = "systemEntityTypeId"

    /**
     * 1. Check required params
     * 2. Get system entity type
     * 3. Check the existence of system entity type object
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map executePreCondition(Map params) {
        try {
            if (!params.oId) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            String plugin = params.plugin ? params.plugin : 1
            int pluginId = Integer.parseInt(plugin)

            // if plugin id is not desired then redirect to application plugin as default
            if (pluginId >= 16) {
                pluginId = 1
            }
            long systemEntityTypeId = Long.parseLong(params.oId.toString())
            SystemEntityType systemEntityType = (SystemEntityType) systemEntityTypeService.read(systemEntityTypeId)
            if (!systemEntityType) {
                appMyFavouriteService.setIsDirtyAndIsDefault(params)
                return super.setError(params, SYSTEM_ENTITY_TYPE_NOT_FOUND)
            }
            params.put(LEFT_MENU_PLUGIN, pluginId)
            params.put(SYSTEM_ENTITY_TYPE_NAME, systemEntityType.name)
            params.put(SYSTEM_ENTITY_TYPE_ID, systemEntityType.id)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * There is no execute process, so return the same map as received
     * @param result - resulting map from execute
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map execute(Map result) {
        return result
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
     * There is no success message
     * since the input-parameter already have "isError:false", just return the same map
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     * @param result - map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }
}
