package com.athena.mis.application.actions.testdomain

import com.athena.mis.application.entity.TestDomain
import com.athena.mis.application.service.TestDomainService
import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.AppUser
import com.athena.mis.integration.inventory.InvPluginConnector
import com.athena.mis.utility.DateUtility
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional


class UpdateTestDomainActionService extends BaseService implements ActionServiceIntf {

     private Logger log = Logger.getLogger(getClass())

    private static final String PROJECT = "testDomain"
    private static final String PROJECT_UPDATE_SUCCESS_MESSAGE = "TestDomain has been updated successfully"

    TestDomainService testDomainService
    @Autowired(required = false)

    /**
     * 1. Check Validation
     * 2. Check un-approve transactions for auto approve
     * 3. Build testDomain object for update
     *
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            //Check Validation
            String errMsg = checkValidation(params)
            if (errMsg) {
                return super.setError(params, errMsg)
            }

            // check your custom valiation

            // build testDomain object for update
            getTestDomain(params)

            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. get the testDomain object from map
     * 2. Update existing testDomain in DB
     * This method is in transactional block and will roll back in case of any exception
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            TestDomain testDomain = (TestDomain) result.get(PROJECT)
            testDomainService.update(testDomain)
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
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, PROJECT_UPDATE_SUCCESS_MESSAGE)
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
     * Build testDomain object for update
     *
     * @param params - serialize parameters from UI
     * @return - testDomain object
     */
    private TestDomain getTestDomain(Map params) {
        TestDomain oldTestDomain = (TestDomain) params.get(PROJECT)
        params.startDate = DateUtility.parseMaskedDate(params.startDate.toString())
        params.endDate = DateUtility.parseMaskedDate(params.endDate.toString())
        TestDomain newTestDomain = new TestDomain(params)
        oldTestDomain.name = newTestDomain.name
        oldTestDomain.code = newTestDomain.code
        AppUser systemUser = super.getAppUser()
        oldTestDomain.updatedOn = new Date()
        oldTestDomain.updatedBy = systemUser.id

        // write approval flag holds previous state if user is not config manager

        return oldTestDomain
    }

    /**
     * 1. Check TestDomain object existance
     * 2. Check for duplicate testDomain code
     * 3. Check for duplicate testDomain name
     * 4. Check parameters
     *
     * @param testDomain - object of TestDomain
     * @param params - a map from caller method
     * @return -a string containing null value or error message depending on duplicate check
     */
    private String checkValidation(Map params) {
        String errMsg
        //Check parameters

        long testDomainId = Long.parseLong(params.id.toString())
        TestDomain testDomain = testDomainService.read(testDomainId)

        //check TestDomain object existance
        errMsg = checkTestDomainExistance(testDomain, params)
        if (errMsg != null) return errMsg

        // Check your custom validation here

        params.put(PROJECT, testDomain)
        return null
    }

    /**
     * check TestDomain object existance
     *
     * @param testDomain - an object of TestDomain
     * @param params - a map from caller method
     * @return - error message or null
     */

    private String checkTestDomainExistance(TestDomain testDomain, Map params) {
        long version = Long.parseLong(params.version.toString())
        if (!testDomain || testDomain.version != version) {
            return ENTITY_NOT_FOUND_ERROR_MESSAGE
        }
        return null
    }

}
