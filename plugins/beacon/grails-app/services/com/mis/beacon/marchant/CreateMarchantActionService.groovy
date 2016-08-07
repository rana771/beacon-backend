package com.mis.beacon.marchant

import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.mis.beacon.Marchant
import com.athena.mis.ActionServiceIntf
import com.athena.mis.utility.DateUtility
import com.mis.beacon.service.MarchantService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional


class CreateMarchantActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String MARCHANT = "marchant"
    private static final String MARCHANT_CODE_ALREADY_EXISTS = "Same marchant code already exists"
    private static final String MARCHANT_NAME_ALREADY_EXISTS = "Same marchant name already exists"
    private static final String MARCHANT_SAVE_SUCCESS_MESSAGE = "Marchant has been saved successfully"

    MarchantService marchantService

    /**
     * 1. check Validation
     * 2. build marchant object
     *
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            AppUser user = super.getAppUser();
            // check Validation
            String errMsg = checkValidation(params, user)
            if (errMsg) {
                return super.setError(params, errMsg)
            }
            // build marchant object
            Marchant marchant = getMarchant(params, user)
            params.put(MARCHANT, marchant)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. receive marchant object from executePreCondition method
     * 2. create new marchant
     * This method is in transactional block and will roll back in case of any exception
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            Marchant marchant = (Marchant) result.get(MARCHANT)
            // save new marchant object in DB
            marchantService.create(marchant)
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
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * 1. put success message
     *
     * @param result -map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, MARCHANT_SAVE_SUCCESS_MESSAGE)
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     *
     * @param result -map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Build marchant object
     *
     * @param params - serialize parameters from UI
     * @param user - an object of AppUser
     * @return - marchant object
     */
    private Marchant getMarchant(Map params, AppUser user) {
        Marchant marchant = new Marchant(params);
        marchant.appUser = super.getAppUser();
        marchant.apiKey=UUID.randomUUID().toString();
        return marchant;
    }

    /**
     * 1. check for duplicate marchant name
     * 2. check for duplicate marchant code
     *
     * @param marchant -object of Marchant
     * @param user - an object of AppUser
     * @return -a string containing null value or error message depending on duplicate check
     */
    private String checkValidation(Map params, AppUser user) {
        String errMsg
        //Check parameters
        if (!params.name) {
            return ERROR_FOR_INVALID_INPUT
        }
        //check for duplicate marchant code
//        errMsg = checkMarchantCountByCode(params, user)
//        if (errMsg != null) return errMsg

        //check for duplicate marchant name
//        errMsg = checkMarchantCountByName(params, user)
//        if (errMsg != null) return errMsg

        return null
    }

    /**
     * check for duplicate marchant code
     *
     * @param marchant - an object of Marchant
     * @param user - an object of AppUser
     * @return - error message or null
     */
    private String checkMarchantCountByCode(Map params, AppUser user) {
//        int count = marchantService.countByCodeIlikeAndCompanyId(params.code, user.companyId)
//        if (count > 0) {
//            return MARCHANT_CODE_ALREADY_EXISTS
//        }
        return null
    }

    /**
     * check for duplicate marchant name
     *
     * @param marchant - an object of Marchant
     * @param user - an object of AppUser
     * @return - error message or null
     */
    private String checkMarchantCountByName(Map params, AppUser user) {
//        int count = marchantService.countByNameIlikeAndCompanyId(params.name, user.companyId)
//        if (count > 0) {
//            return MARCHANT_NAME_ALREADY_EXISTS
//        }
        return null
    }

}

