package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import org.apache.log4j.Logger

class HistoryBackTaglibActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String PARAM_OID = "?oId="
    private static final String PARAM_CID = "&cId="
    private static final String PARAM_URL = "&url="
    private static final String PARAM_P_URL = "&pUrl="
    private static final String PARAM_PLUGIN_ID = "&plugin="
    private static final String CONCAT_HASH = "#"
    /**
     * Do nothing in precondition method
     * @param params - parameter from UI
     * @return - return the same map
     */
    public Map executePreCondition(Map params) {
        return params
    }

    /**
     *
     * @param result - A map returned from precondition method
     * @return - A map consisting of desired html
     */
    public Map execute(Map result) {
        try {
            String location = EMPTY_SPACE
            if (result.url.equals("pt_dash_board")) {
                location = "#ptPlugin/renderProjectTrackMenu"
                String htmlTag = buildHtml(location)
                result.html = htmlTag
            } else if (result.url.equals("dpl_dash_board")) {
                location = "#dataPipeLine/renderDataPipeLineMenu"
                String htmlTag = buildHtml(location)
                result.html = htmlTag
            }else{
                String url = result.url ? result.url : result.p_url
                String oId = result.o_id ? result.o_id : result.p_id
                String cId = result.c_id ? result.c_id : EMPTY_SPACE

                String pluginId = result.plugin ? result.plugin : EMPTY_SPACE
                if (oId && result.url) {
                    if (url.find("#")) {
                        url = result.url
                    } else {
                        url = CONCAT_HASH + result.url
                    }
                    if (pluginId.equals(EMPTY_SPACE)) {
                        location = url + PARAM_OID + oId;
                        if (!cId.equals(EMPTY_SPACE)) {
                            location = url + PARAM_OID + oId + PARAM_CID + cId + PARAM_URL + result.url + PARAM_P_URL + result.p_url;
                        }
                    } else {
                        location = url + PARAM_OID + oId + PARAM_PLUGIN_ID + pluginId;
                    }
                    String html = buildHtml(location)
                    result.html = html
                } else if (result.url) {
                    if (url.find("#")) {
                        url = result.url
                    } else {
                        url = CONCAT_HASH + result.url
                    }
                    String html = buildHtml(url)
                    result.html = html
                }
            }

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

    private String buildHtml(String location) {
        String html = """
              <div class="pull-right" style="padding-right:10px">
                    <span class="fa fa-backward" onclick="historyBack();return false;"
                        title="Back" style="cursor:pointer;">
                    </span>
              </div>
        """
        String script = """
             <script type="text/javascript">
                    function historyBack(){
                        document.location = '${location}';
                    };
            </script>
        """
        return html + script
    }
}
