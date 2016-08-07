package com.athena.mis.application.actions.district

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.District
import com.athena.mis.application.service.DistrictService
import com.athena.mis.integration.arms.ArmsPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class DeleteDistrictActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String DISTRICT = "district"
    private static final String DISTRICT_DELETE_SUCCESS_MSG = "District has been successfully deleted"
    private static final String GLOBAL_DISTRICT_CAN_NOT_BE_DELETED = "Global district can not be deleted"

    DistrictService districtService

    @Autowired(required = false)
    ExchangeHousePluginConnector exhImplService
    @Autowired(required = false)
    ArmsPluginConnector armsImplService

    /**
     * check association
     * @param parameters -parameters from UI
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map parameters) {
        try {
            long districtId = Long.parseLong(parameters.id)
            District district = districtService.read(districtId)
            String errMsg = isAssociated(district)
            if (errMsg) {
                return super.setError(parameters, errMsg)
            }
            errMsg = checkForGlobalDistrict(districtId)
            if (errMsg) {
                return super.setError(parameters, errMsg)
            }
            parameters.put(DISTRICT, district)
            return parameters
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Delete District object from DB
     * This function is in transactional boundary and will roll back in case of any exception
     * @param params -serialized parameters from UI
     * @return -a map containing isError(true/false) depending on method success
     */
    @Transactional
    public Map execute(Map result) {
        try {
            District district = (District) result.get(DISTRICT)
            districtService.delete(district)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * There is no post condition, so return the same map as received
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * Received map must contain isError false
     * Return the same map as received
     */
    public Map buildSuccessResultForUI(Map parameters) {
        return super.setSuccess(parameters, DISTRICT_DELETE_SUCCESS_MSG)
    }

    /**
     * Received map must contain isError-true with corresponding error message
     * Return the same map as received
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * 1. check if District has BankBranch
     * 2. check if District has exhTask (if application has Exchange house plugin)
     * 3. check if District has rmsTask (if application has Arms plugin)
     * 4. check if District has rmsPurchase instrument mapping (if application has Arms plugin)
     * @param district -object of District
     * @return -a string containing error message or null value depending on association check
     */
    private String isAssociated(District district) {
        Long districtId = district.id
        String districtName = district.name
        Integer count = 0
        // has Bank Branch
        count = countBankBranch(districtId)
        if (count.intValue() > 0) {
            return getMessageOfAssociation(districtName, count, DOMAIN_BANK_BRANCH)
        }
        //has Task
        if (exhImplService) {
            count = countTask(districtId)
            if (count.intValue() > 0) {
                return getMessageOfAssociation(districtName, count, DOMAIN_TASK)
            }
        }
        if (armsImplService) {
            count = countRmsTask(districtId)
            if (count.intValue() > 0) {
                return getMessageOfAssociation(districtName, count, DOMAIN_TASK)
            }
            count = countRmsPurchaseInstrumentMapping(districtId)
            if (count.intValue() > 0) {
                return getMessageOfAssociation(districtName, count, DOMAIN_TASK)
            }
        }
        return null
    }

    /**
     * count number of row in branch table by bank id
     * @param districtId -districtId
     * @return - count
     */
    private int countBankBranch(Long districtId) {
        String query = """
            SELECT COUNT(id) as count
            FROM app_bank_branch
            WHERE district_id = ${districtId}
        """
        List districtCount = executeSelectSql(query)
        int count = districtCount[0].count
        return count
    }

    /**
     * count number of rows in task table by bank district id
     * @param districtId - district id
     * @return - total count
     */
    private int countTask(Long districtId) {
        String query = """
            SELECT COUNT(id) as count
            FROM exh_task
            WHERE outlet_district_id = ${districtId}
        """
        List districtCount = executeSelectSql(query)
        int count = districtCount[0].count
        return count;
    }

    /**
     * Check association of district with rms_task
     * @param districtId - district id
     * @return total count
     */
    private int countRmsTask(Long districtId) {
        String query = """
            SELECT COUNT(id) as count
            FROM rms_task
            WHERE mapping_district_id = ${districtId}
        """
        List districtCount = executeSelectSql(query)
        int count = districtCount[0].count
        return count;
    }
    private static final String QUERY_COUNT_RMS_PURCHASE_INSTRUMENT_MAPPING = """
        SELECT COUNT(id) as count
        FROM rms_purchase_instrument_mapping
        WHERE district_id = :districtId
    """

    /**
     * Check association of district with rms_purchase_instrument mapping
     * @param districtId - id of district to be deleted
     * @return - count
     */
    private int countRmsPurchaseInstrumentMapping(long districtId) {
        List taskCount = executeSelectSql(QUERY_COUNT_RMS_PURCHASE_INSTRUMENT_MAPPING, [districtId: districtId])
        int count = taskCount[0].count
        return count
    }
    /**
     * Check whether district is global or not
     * @param districtId - id of district to be deleted
     * @return - errMsg or null
     */
    private String checkForGlobalDistrict(long districtId) {
        // global district can not be deleted
        int count = districtService.countByIdAndIsGlobal(districtId, Boolean.TRUE)
        if (count.intValue() > 0) {
            return GLOBAL_DISTRICT_CAN_NOT_BE_DELETED
        }
        return null
    }
}


