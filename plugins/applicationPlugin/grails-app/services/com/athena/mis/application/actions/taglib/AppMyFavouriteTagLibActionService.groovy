package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppMyFavourite
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.service.AppMyFavouriteService

import javax.servlet.http.HttpServletRequest

/*Renders html of 'icon' for page bookmark */

class AppMyFavouriteTagLibActionService extends BaseService implements ActionServiceIntf {

    private static final String ID = "id"
    private static final String IS_MY_FAVOURITE = "isMyFavourite"

    AppMyFavouriteService appMyFavouriteService

    /**
     * No pre conditions required
     * @param parameters - a map of given attributes
     * @return - same map of input parameter
     */
    public Map executePreCondition(Map params) {
        try {
            params.put(ID, 0L)
            params.put(IS_MY_FAVOURITE, Boolean.FALSE)
            // get url
            HttpServletRequest request = (HttpServletRequest) params.request
            String strUrl = request.getForwardURI()
            String queryString = request.getQueryString();
            if (queryString == null) {
                strUrl = strUrl.substring(1, strUrl.length())
            } else {
                String tmpRemoveStr = "&isBookMark"
                if(queryString.contains(tmpRemoveStr)){
                    strUrl = strUrl + "?" + queryString.replace(queryString.substring(queryString.indexOf(tmpRemoveStr)), "")
                }
                strUrl = strUrl.substring(1, strUrl.length())
            }
            AppUser user = super.getAppUser()
            AppMyFavourite myFavourite = appMyFavouriteService.findByUrlAndUserId(strUrl, user.id)
            if (myFavourite) {
                params.put(ID, myFavourite.id)
                params.put(IS_MY_FAVOURITE, Boolean.TRUE)
            }
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Build html
     *  1. build the html
     *  2. put html object in common map
     * @param parameters - a map of given attributes
     * @return - a map with all previous values including output html
     */
    public Map execute(Map params) {
        try {
            String html = buildHtml(params)
            params.html = html
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * do not nothing for post condition
     */
    public Map executePostCondition(Map obj) {
        return obj
    }

    /**
     * do not nothing for build success result
     */
    public Map buildSuccessResultForUI(Map obj) {
        return obj
    }

    /**
     * do not nothing for build failure result
     */
    public Map buildFailureResultForUI(Map obj) {
        return obj
    }

    /**
     * Build html
     * @return - html string
     */
    private String buildHtml(Map params) {
        boolean isFavourite = params.get(IS_MY_FAVOURITE)
        long id = params.get(ID)
        String html = """
             <div class="pull-right" id="addIcon">
                <span class="fa fa-star-o bookmark" onclick="addToMyFavourite();return false;"
                    title="Bookmark" style="cursor:pointer;">
                </span>
            </div>

            <div class="pull-right" id="delIcon">
                <span class="fa fa-remove" onclick="removeMyFavourite();return false;"
                    title="Remove From My Favourite" style="cursor:pointer;">
                </span>
            </div>
        """
        String script = """
            <script type="text/javascript">
                \$('#addIcon').hide();
                \$('#delIcon').hide();

                var path = location.hash;
                path = path.replace(/#|&/g,function(match) {return (match=="#")?"":"_";});
                var isMyFavourite = ${isFavourite};
                var id = ${id};

                if (isMyFavourite == true) {
                     \$('#delIcon').show();
                      } else {
                     \$('#addIcon').show();
                 }

                function addToMyFavourite() {
                    showLoadingSpinner(true);
                    \$.ajax({
                        url: "/appMyFavourite/create?path=" + path,
                        success: executePostConditionForAdd,
                        error: function (XMLHttpRequest, textStatus, errorThrown) {
                            afterAjaxError(XMLHttpRequest, textStatus)
                        },
                        complete: function (XMLHttpRequest, textStatus) {
                            showLoadingSpinner(false);
                        },
                        dataType: 'json',
                        type: 'post'
                    });
                }

                function executePostConditionForAdd(data) {
                    if (data.isError) {
                        showError(data.message);
                        return false;
                    }
                    showSuccess(data.message);
                    \$('#addIcon').hide();
                    \$('#delIcon').show();
                    id = data.id;
                }

                function executePreConditionForDeleteMyFavourite() {
                    if (!confirm('Are you sure you want to delete this page from "My Favourite"?')) {
                        return false;
                    }
                    return true;
                }

                function removeMyFavourite() {
                    if (executePreConditionForDeleteMyFavourite() == false) {
                        return;
                    }
                    showLoadingSpinner(true);
                    \$.ajax({
                        url: "/appMyFavourite/delete?id=" + id,
                        success: executePostConditionForDeleteMyFavourite,
                        error: function (XMLHttpRequest, textStatus, errorThrown) {
                            afterAjaxError(XMLHttpRequest, textStatus)
                        },
                        complete: function (XMLHttpRequest, textStatus) {
                            showLoadingSpinner(false);
                        },
                        dataType: 'json',
                        type: 'post'
                    });
                }

                function executePostConditionForDeleteMyFavourite(data) {
                    if (data.isError) {
                        showError(data.message);
                        return false;
                    }
                    showSuccess(data.message);
                    \$('#addIcon').show();
                    \$('#delIcon').hide();
                }
            </script>
        """
        return html + script
    }
}
