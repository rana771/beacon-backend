package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.integration.elearning.ELearningPluginConnector
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger

/*Renders html of 'count' for Unread message by user*/
class GetCountUnreadAppMessageTaglibActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    public Map executePreCondition(Map parameters) {
        return parameters
    }

    /** Get unread message count
     *  build the html for 'count' inbox link
     * @param result - map returned from preCondition
     * @return - html string
     */
    public Map execute(Map result) {
        Map attrs = (Map) result
        try {
            int count = getUnreadMessageCount()
            String html = buildUI(count)
            attrs.html = html
            return attrs
        } catch (Exception e) {
            log.error(e.getMessage())
            return super.setError(attrs, FALSE)
        }
    }

    private String buildUI(int count) {

        String inboxUrl = "/appMessage/show"
        if (PluginConnector.isPluginInstalled(ELearningPluginConnector.PLUGIN_NAME)) {
            inboxUrl = "/elMail/showMessage"
        }
        String script = """
            <script type='text/javascript'>
                function showInboxMessage()
                {
                    var loc = '${inboxUrl}';
                    router.navigate(formatLink(loc));
                    return false;
                }
            </script>
        """

        // read map values
        String html = """ <a href="javascript:void(0);" onclick="showInboxMessage();"><span class="fa fa-inbox"></span>&nbsp;Inbox (${count})</a> """
        return html + script
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
     * There is no buildSuccessResultForUI, so return the same map as received
     *
     * @param result - resulting map from execute
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
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

    /*
    * Get unread message count by user
    * */
    private int getUnreadMessageCount() {
        Map queryParams = [appUserId: super.getAppUser().id]
        List<GroovyRowResult> result = executeSelectSql(UNREAD_MSG_COUNT, queryParams)
        if (result.size() < 0) {
            throw new RuntimeException("Failed to get count of message")
        }
        int count = (int) result[0].count
        return count
    }

    private static final String UNREAD_MSG_COUNT = """
        SELECT COUNT(DISTINCT(id)) count
        FROM app_message
        WHERE is_read = FALSE
        AND app_user_id = :appUserId
    """
}
