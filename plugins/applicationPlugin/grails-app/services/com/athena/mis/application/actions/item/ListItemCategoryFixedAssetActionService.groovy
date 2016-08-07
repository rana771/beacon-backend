package com.athena.mis.application.actions.item

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.model.ListItemCategoryFixedAssetActionServiceModel
import com.athena.mis.application.service.AppSystemEntityCacheService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to get list of itemObject (Category : FixedAsset) to show on grid
 *  For details go through Use-Case doc named 'ListItemCategoryFixedAssetActionService'
 */
class ListItemCategoryFixedAssetActionService extends BaseService implements ActionServiceIntf {

    AppSystemEntityCacheService appSystemEntityCacheService

    private Logger log = Logger.getLogger(getClass())

    /**
     * No pre-conditions required for searching/listing Item domain
     *
     * @param params - Request parameters
     * @return - same map
     */
    public Map executePreCondition(Map params) {
        return params
    }

    /**
     * 1. initialize params for pagination of list
     *
     * 2. pull all item list from database (if no criteria)
     *
     * 3. pull filtered result from database (if given criteria)
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map execute(Map result) {

        try {
            SystemEntity itemSysEntityObject = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_ITEM_CATEGORY_FIXED_ASSET, appSystemEntityCacheService.SYS_ENTITY_TYPE_ITEM_CATEGORY, companyId)
            Closure filterParam = {
                eq("categoryId", itemSysEntityObject.id)
            }
            Map resultMap = super.getSearchResult(result, ListItemCategoryFixedAssetActionServiceModel.class, filterParam)
            result.put(LIST, resultMap.list)
            result.put(COUNT, resultMap.count)
            return result

        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * There is no postCondition, so return the same map as received
     *
     * @param result - resulting map from execute
     * @return - same map of input-parameter
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * Since success message is handled in BaseService so return the same map
     * @param result -map from execute/executePost method
     * @return - same map of input-parameter
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    /**
     * @param result -map returned from previous methods
     * @return - same map of input-parameter
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }
}
