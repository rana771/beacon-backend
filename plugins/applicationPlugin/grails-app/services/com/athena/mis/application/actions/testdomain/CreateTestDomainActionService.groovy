package com.athena.mis.application.actions.testdomain

import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.TestDomain
import com.athena.mis.application.service.TestDomainService
import com.athena.mis.ActionServiceIntf
import com.athena.mis.utility.DateUtility
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional


class CreateTestDomainActionService  extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String TESTDOMAIN = "testDomain"
    private static final String TESTDOMAIN_SAVE_SUCCESS_MESSAGE = "TestDomain has been saved successfully"

    TestDomainService testDomainService

    /**
     * 1. check Validation
     * 2. build testDomain object
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
            // build testDomain object
            TestDomain testDomain = getTestDomain(params, user)
            params.put(TESTDOMAIN, testDomain)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. receive testDomain object from executePreCondition method
     * 2. create new testDomain
     * This method is in transactional block and will roll back in case of any exception
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            TestDomain testDomain = (TestDomain) result.get(TESTDOMAIN)
            // save new testDomain object in DB
            testDomainService.create(testDomain)
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
        return super.setSuccess(result, TESTDOMAIN_SAVE_SUCCESS_MESSAGE)
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
     * Build testDomain object
     *
     * @param params - serialize parameters from UI
     * @param user - an object of AppUser
     * @return - testDomain object
     */
    private TestDomain getTestDomain(Map params, AppUser user) {
        TestDomain testDomain = new TestDomain(params)
        testDomain.createdOn = new Date()
        testDomain.createdBy = user.id
        testDomain.companyId = user.companyId
        testDomain.updatedBy = 0
        testDomain.updatedOn = null
        return testDomain
    }

    /**
     * 1. check for duplicate testDomain name
     * 2. check for duplicate testDomain code
     *
     * @param testDomain -object of TestDomain
     * @param user - an object of AppUser
     * @return -a string containing null value or error message depending on duplicate check
     */
    private String checkValidation(Map params, AppUser user) {
        String errMsg
            //write your validation message here

        return null
    }



}

