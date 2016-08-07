package com.athena.mis

/**
 * Interface to be implemented by Action Service to execute specific action. Action
 * may include but not limited to:
 * <ul>
 *     <li>Create Action Service<li>
 *     <li>Select Action Service<li>
 *     <li>Search Action Service<li>
 *     <li>Delete Action Service<li>
 *     <li>DownloadXX Action Service, where XX is the file format to be downloaded such as CSV etc.<li>
 * </ul>
 *
 * Created by user on 10/9/14.
 */
public interface ActionServiceIntf {

    /**
     * Executes pre-condition of an Action such as running
     * validation against a domain object, or to check whether
     * underlying logged-in user has enough privileges to execute an
     * action.
     *
     * @param parameters Usually request parameters
     * @return Returns a map containing a key named IS_ERROR to indicate
     * whether the pre-condition has been met (IS_ERROR=FALSE) or not (IS_ERROR=TRUE)
     */
    public abstract Map executePreCondition(Map parameters);

    /**
     * Executes an Action when pre-condition has been met.
     *
     * @param previousResult Result from running pre-condition.
     * @return Returns a map consisting a key named IS_ERROR to indicate
     * whether the pre-condition has been met (IS_ERROR=FALSE) or not (IS_ERROR=TRUE);
     * along with other necessary elements wrapped in the result map
     */
    public abstract Map execute(Map previousResult);

    /**
     * Executes the post condition of an Action, such as sending emails or
     * logging the access or recording any error situation for later audit
     *
     * @param previousResult Previous result of executing the Action or running the
     * pre-condition (when pre-condition has been failed to meet)
     * @return A map consisting elements along with a key named IS_ERROR to indicate
     * whether the pre-condition has been met (IS_ERROR=FALSE) or not (IS_ERROR=TRUE)
     */
    public abstract Map executePostCondition(Map previousResult);

    /**
     * Builds a success result for UI
     *
     * @param executeResult Execute result of previously running methods. Previously running
     * method could either be:
     * <ul>
     *     <li>executePreCondition @see this.executePreCondition, or<li>
     *     <li>execute @see this.execute<li>
     * </ul>
     * @return A map containing JSON formatted result to be shown in the UI
     */
    public abstract Map buildSuccessResultForUI(Map executeResult);

    /**
     * Builds a failure result for UI
     *
     * @param executeResult Execute result of previously running methods. Previously running
     * method could either be:
     * <ul>
     *     <li>executePreCondition @see this.executePreCondition, or<li>
     *     <li>execute @see this.execute<li>
     * </ul>
     * @return A map containing JSON formatted result to be shown in the UI
     */
    public abstract Map buildFailureResultForUI(Map executeResult);


}