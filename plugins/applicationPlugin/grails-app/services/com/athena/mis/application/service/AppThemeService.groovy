package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.AppTheme
import com.athena.mis.integration.accounting.AccPluginConnector
import com.athena.mis.integration.arms.ArmsPluginConnector
import com.athena.mis.integration.budget.BudgPluginConnector
import com.athena.mis.integration.datapipeline.DataPipeLinePluginConnector
import com.athena.mis.integration.document.DocumentPluginConnector
import com.athena.mis.integration.elearning.ELearningPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.integration.fixedasset.FxdPluginConnector
import com.athena.mis.integration.inventory.InvPluginConnector
import com.athena.mis.integration.procurement.ProcPluginConnector
import com.athena.mis.integration.projecttrack.PtPluginConnector
import com.athena.mis.integration.qsmeasurement.QsPluginConnector
import com.athena.mis.integration.sarb.SarbPluginConnector
import org.apache.log4j.Logger

/**
 * AppThemeService is used to handle only CRUD related object manipulation
 * (e.g. list, read, update etc.)
 */
class AppThemeService extends BaseDomainService {

    private Logger log = Logger.getLogger(getClass())

    private static final String SORT_BY_KEY = "key"
    // appTheme key constants
    public static final String KEY_WELCOME_TEXT = "app.welcomeTitle"
    public static final String KEY_COMPANY_LOGO_LEFTMENU = "app.leftMenu.CompanyLogo"
    public static final String KEY_COMPANY_NAME = "app.companyName"
    public static final String KEY_COMPANY_COPYRIGHT_LEFTMENU = "app.leftMenu.companyCopyright"
    public static final String KEY_COMPANY_WEBSITE = "app.companyWebsite"
    public static final String KEY_IMG_LOGIN_TOP_RIGHT = "app.login.imgTopRight"
    public static final String KEY_CSS_MAIN_COMPONENTS = "app.cssMainComponents"
    public static final String KEY_CSS_BOOTSTRAP_CUSTOM = "app.cssBootstrapCustom"
    public static final String KEY_CSS_KENDO_CUSTOM = "app.cssKendoCustom"
    public static final String KEY_LOGIN_PAGE_CAUTION = "app.login.pageCaution"
    public static final String KEY_BUSINESS_SUPPORT = "app.login.businessSupport"
    public static final String KEY_ADVERTISING_PHRASE = "app.login.advertisingPhrase"
    public static final String KEY_KENDO_THEME = "app.kendoTheme"
    public static final String KEY_PRODUCT_COPYRIGHT = "app.login.productCopyright"
    public static final String KEY_NO_ACCESS_PAGE = "app.noAccessPage"

    // e-learning
    public static final String KEY_EL_LOGO = "el.logo"
    public static final String KEY_EL_BANNER = "el.banner"
    public static final String KEY_EL_FOOTER = "el.footer"
    public static final String KEY_EL_HOME_RESOURCE = "el.home.resource"

    @Override
    public void init() {
        domainClass = AppTheme.class
    }

    /**
     * Method to get list of appTheme
     * @return - appTheme list
     */
    @Override
    public List list() {
        return AppTheme.list(sort: AppTheme.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true)
    }

    /**
     * Method to get the count of appTheme
     * @param companyId - company id
     * @return - an integer value of appTheme count
     */
    public int countByCompanyId(long companyId) {
        int count = AppTheme.countByCompanyId(companyId)
        return count
    }

    /**
     * Method to find the appTheme list
     * @param companyId - company id
     * @return - a list of appTheme
     */
    public List findAllByCompanyId(long companyId) {
        List themeList = AppTheme.findAllByCompanyId(companyId, [max: resultPerPage, offset: start, sort: SORT_BY_KEY, order: ASCENDING_SORT_ORDER, readOnly: true])
        return themeList
    }

    /**
     * Method to find appTheme object
     * @param themeId - appTheme id
     * @param companyId - company id
     * @return - an object of appTheme
     */
    public AppTheme findByIdAndCompanyId(long themeId, long companyId) {
        AppTheme theme = AppTheme.findByIdAndCompanyId(themeId, companyId, [readOnly: true])
        return theme
    }

    /**
     * Method to find appTheme object
     * @param name - AppTheme.key
     * @param companyId - company id
     * @return - an object of appTheme
     */
    public AppTheme findByKeyAndCompanyId(String name, long companyId) {
        AppTheme theme = AppTheme.findByKeyAndCompanyId(name, companyId, [readOnly: true])
        return theme
    }

    /**
     * Method to find appTheme object
     * @param name - AppTheme.key
     * @param pluginId - pluginId
     * @param companyId - company id
     * @return - an object of appTheme
     */
    public AppTheme findByKeyAndPluginAndCompanyId(String name, int pluginId, long companyId) {
        AppTheme theme = AppTheme.findByKeyAndPluginIdAndCompanyId(name, pluginId, companyId, [readOnly: true])
        return theme
    }

    private static final String DELETE_ALL = """
        DELETE FROM app_theme WHERE company_id = :companyId
    """

    /**
     * Delete all appTheme by companyId
     * @param companyId - id of company
     */
    public void deleteAllByCompanyId(long companyId) {
        Map queryParams = [
                companyId: companyId
        ]
        executeUpdateSql(DELETE_ALL, queryParams)
    }

    private static final String VALUE_CSS_MAIN_COMPONENTS = """
        html,
        body {
            height: 100%;
            margin: 0;
            padding: 0;
        }

        #containerParent {
            border-style: solid;
            border-width: 5px 0 0 0;
            border-top-color: #DDDDDD;
        }

        #containerLeftMenu {
            border-style: solid;
            border-width: 0 5px 0 0;
            border-color: #DDDDDD;
        }

        /*Various classes for buttons */
        .save, .clear-form, .search {
            padding: 0.3em 0.5em;
            height: 2.0833em;
            border: 1px solid #ccc;
            background: #f6f6f6;
            background-image: -moz-linear-gradient(top, #ffffff, #efefef);
            background-image: -webkit-gradient(linear, left top, left bottom, from(#ffffff), to(#efefef));
            filter: progid:DXImageTransform.Microsoft.Gradient(startColorStr=#ffffff, endColorStr=#efefef);
            -ms-filter: "progid:DXImageTransform.Microsoft.Gradient(startColorStr=#FFFFFF, endColorStr=#EFEFEF)";
            -moz-border-radius: 5px;
            -webkit-border-radius: 5px;
            border-radius: 5px;
            white-space: nowrap;
            vertical-align: middle;
            cursor: pointer;
            overflow: visible;
        }

        .save:hover, .clear-form:hover, .search:hover {
            border-color: #999;
            background: #f3f3f3;
            outline: 0;
            -moz-box-shadow: 0 0 3px #999;
            -webkit-box-shadow: 0 0 3px #999;
            box-shadow: 0 0 3px #999
        }

        .save:disabled {
            border-color: #999;
            background: #f3f3f3;
            outline: 0;
            -moz-box-shadow: 0 0 3px #999;
            -webkit-box-shadow: 0 0 3px #999;
            box-shadow: 0 0 3px #999
        }

        /*for small button in voucher Dr/Cr create*/
        .small_btn {
            padding: 0.1em 0.5em;
            border: 1px solid #ccc;
            background: #f6f6f6;
            background-image: -moz-linear-gradient(top, #ffffff, #efefef);
            background-image: -webkit-gradient(linear, left top, left bottom, from(#ffffff), to(#efefef));
            filter: progid:DXImageTransform.Microsoft.Gradient(startColorStr=#ffffff, endColorStr=#efefef);
            -ms-filter: "progid:DXImageTransform.Microsoft.Gradient(startColorStr=#FFFFFF, endColorStr=#EFEFEF)";
            -moz-border-radius: 5px;
            -webkit-border-radius: 5px;
            border-radius: 5px;
            white-space: nowrap;
            vertical-align: middle;
            cursor: pointer;
            overflow: visible;
        }

        /*for arrow button in role-right mapping*/
        .arrow_btn {
            padding: 0.1em 0.5em;
            border: 1px solid #ccc;
            background: #f6f6f6;
            background-image: -moz-linear-gradient(top, #ffffff, #efefef);
            background-image: -webkit-gradient(linear, left top, left bottom, from(#ffffff), to(#efefef));
            filter: progid:DXImageTransform.Microsoft.Gradient(startColorStr=#ffffff, endColorStr=#efefef);
            -ms-filter: "progid:DXImageTransform.Microsoft.Gradient(startColorStr=#FFFFFF, endColorStr=#EFEFEF)";
            -moz-border-radius: 5px;
            -webkit-border-radius: 5px;
            border-radius: 5px;
            white-space: nowrap;
            vertical-align: middle;
            cursor: pointer;
            overflow: visible;
            width: 40px;
            height: 22px;
            text-align: center;
        }

        .small_btn:hover {
            border-color: #999;
            background: #f3f3f3;
            outline: 0;
            -moz-box-shadow: 0 0 3px #999;
            -webkit-box-shadow: 0 0 3px #999;
            box-shadow: 0 0 3px #999
        }

        .small_btn_top:hover {
            border-color: #999;
            background: #f3f3f3;
            outline: 0;
            -moz-box-shadow: 0 0 3px #999;
            -webkit-box-shadow: 0 0 3px #999;
            box-shadow: 0 0 3px #999
        }

        /*for button container*/
        .buttons {
            border-bottom: 1px solid #BDC7D8;
            border-top: 1px solid #BDC7D8;
            clear: right;
            color: #666666;
            padding: 5px 8px;
        }

        /* class for rolling spinner */
        .spinner {
            padding: 5px;
            right: 0;
            clear: both;
            float: left;
            margin-bottom: 30px;
            position: relative;
        }

        /*for top right welcome panel*/
        div.welcomeText {
            float: right;
            border-bottom: 1px solid #BDC7D8;
            border-left: 1px solid #BDC7D8;
            padding: 3px;
            background-color: #f0f0f2;
            font-size: 12px;
        }

        div.welcomeText .textHolder {
            border-bottom-color: #BDC7D8;
            border-bottom-style: solid;
            border-bottom-width: 1px;
            font-weight: bold;
            padding-bottom: 3px;
            padding-left: 6px;
        }

        div.welcomeText .buttonHolder {
            padding-top: 3px;
        }

        /*for dock menu buttons*/
        span.dockMenuText {
            -moz-border-radius: 5px;
            border-radius: 4px;
            padding: 5px 6px;
            margin-left: 15px;
            cursor: pointer;
        }

        span.dockMenuTextActive {
            background: #5b74a8;
            padding: 5px 6px;
            color: #FFF;
        }

        span.dockMenuText:hover {
            background: #5b74a8;
            padding: 5px 6px;
            color: #FFF;
        }

        /*for heading text of each page
        currently not used (@todo:clean if not used in future)
        */
        div.heading {
            float: left;
            cursor: pointer;
        }

        span.headingText {
            -moz-border-radius: 5px 5px 5px 5px;
            border-radius: 7px;
            font-weight: bold;
            padding: 2px 5px 2px 5px;
            margin-left: 15px;
        }

        span.headingTextActive {
            background: #5b74a8;
            padding: 2px 5px 2px 5px;
            color: #FFF;
        }

        span.headingText:hover {
            background: #5b74a8;
            padding: 2px 5px 2px 5px;
            color: #FFF;
        }

        /* External table style of each form*/
        .create-form-table {
            border: 1px solid #BDC7D8;
            width: 100%;
            height: 100%;
            border-spacing: 0;
            border-collapse: collapse;
        }

        /* internal table style of each form*/
        .create-internal-table {
            width: 100%;
            height: 100%;
            border-spacing: 0;
            border-collapse: collapse;
        }

        /*style for optional label*/
        .label-holder {
            background: none repeat scroll 0 0 #f5f8fb;
            border-bottom: 1px solid #FFFFFF;
            border-right: 1px solid #BDC7D8;
            color: #000000;
            font-size: 12px;
            margin-right: 7px;
            padding: 3px 3px;
            text-align: right;
            vertical-align: top;
            width: 110px;
            white-space: nowrap;
            height: 17px;
        }

        /*style for optional label of bottom-most cell*/
        .label-holder-last {
            background: none repeat scroll 0 0 #f5f8fb;
            border-bottom: 1px solid #BDC7D8;
            border-right: 1px solid #BDC7D8;
            color: #000000;
            font-size: 12px;
            margin-right: 7px;
            padding: 3px 3px;
            text-align: right;
            vertical-align: top;
            width: 110px;
            white-space: nowrap;
            height: 17px;
        }

        /*style for report labels with variable width*/
        .label-holder-variable {
            background: none repeat scroll 0 0 #f5f8fb;
            border-bottom: 1px solid #FFFFFF;
            border-right: 1px solid #BDC7D8;
            color: #000000;
            font-size: 12px;
            margin-right: 7px;
            padding: 3px 3px;
            text-align: right;
            vertical-align: top;
            white-space: nowrap;
            height: 17px;
        }

        /*for report column header with left alignment(used in budget report)*/
        .columnHeaderLeft {
            background: none repeat scroll 0 0 #f5f8fb;
            border-right: 1px solid #BDC7D8;
            color: #333333;
            font-size: 13px;
            margin-right: 7px;
            padding: 3px 3px;
            text-align: left;
            vertical-align: top;
            white-space: nowrap;
            height: 17px;
        }

        /*for report column header with right alignment(used in budget report)*/
        .columnHeaderRight {
            background: none repeat scroll 0 0 #f5f8fb;
            border-right: 1px solid #BDC7D8;
            color: #333333;
            font-size: 13px;
            margin-right: 7px;
            padding: 3px 3px;
            text-align: right;
            vertical-align: top;
            white-space: nowrap;
            height: 17px;
        }

        /*style for mandatory label*/
        .label-holder-req {
            background: none repeat scroll 0 0 #ffdfdf;
            border-bottom: 1px solid #FFFFFF;
            border-right: 1px solid #BDC7D8;
            color: #000000;
            font-size: 12px;
            margin-right: 7px;
            padding: 3px 3px;
            text-align: right;
            vertical-align: top;
            width: 110px;
            white-space: nowrap;
            height: 17px;
        }

        /*style for mandatory label of bottom-most cell*/
        .label-holder-req-last {
            background: none repeat scroll 0 0 #ffdfdf;
            border-bottom: 1px solid #BDC7D8;
            border-right: 1px solid #BDC7D8;
            color: #000000;
            font-size: 12px;
            margin-right: 7px;
            padding: 3px 3px;
            text-align: right;
            vertical-align: top;
            width: 110px;
            white-space: nowrap;
            height: 17px;
        }

        /* style for left menu link */
        .menuText {
            padding-left: 25px;
            color: #333333;
        }

        /* style for left sub-menu link */
        .menuTextSub {
            padding-left: 23px;
            color:#333333;
        }

        .tabText {
            padding-left: 24px;
            font-weight: bold;
        }

        /*style for date text fields*/
        input[type=text].dateMask {
            width: 75px;
        }

        /*for table cell*/
        .create-form-field {
            padding: 3px;
            vertical-align: top;
            font-size: 12px;
        }

        /*for top-right logout,change password links*/
        .linkText a:link, .linkText a:visited {
            -moz-border-radius: 4px;
            border-radius: 4px;
            padding: 2px 6px
        }

        .linkText a:hover {
            background: #5b74a8;
            color: #FFF;
        }

        .flexDiv div {
            margin: 0 0 0 0;
        }

        /*for dash board container*/
        div.dashboardContainer {
            width: 100%;
            height: 100%;
            margin: 0 0 0 20px
        }

        div.pGroup > select {
            width: auto;
        }
        span.pcontrol > input[type="text"] {
            width: auto;
        }

        /*Override label of bootstrap
        @todo: eliminate when bootstrap implemented
        */
        label{
            font-weight: normal;
            margin-bottom: 0;
        }

        /*For dashboard fix
        @todo: temp fix for bootstrap implemented
        */
        ul#tabs li a {
            height: 58px;
        }
        div#content{
            width: 875px;
            margin: 50px auto auto 65px;
        }

        /*flex grid adjustments
        @todo: temp adjustment due to bootstrap
        */

        /*For html report fields; temp fix for bootstrap*/
        .info_box{
            font-size: 12px;
        }

        /* Style for label required */
        .label-required {
          color:red
        }
        #dockMenuContainer ul li {
          cursor:pointer;
        }
        #dockMenuContainer ul.nav > li > a {
          padding: 7px 11px;
        }

        /* Style for kendo grid menu*/
        ul.kendoGridMenu li i {
            margin: 5px;
            color: #428BCA;
            font-size: 15px;
        }
        .text-right {
            text-align: right !important;
        }
        .c-gray{color: #8a8a8a !important;}
        .c-green{color: #11772D !important;}
        .c-gold{color: #A9763D !important;}
        .c-red{color: #C80000 !important;}
        .c-orange{color: #FE8D00 !important;}.k-state-selected .menuTextSub,.k-state-selected .menuText{color: #ffffff;}

        .expand-div {
            cursor: pointer;
        }

        .h4, h4 {
            font-size: 17px;
        }
    """

    private static final String VALUE_CSS_BOOTSTRAP_CUSTOM = """
        .panel-primary > .panel-heading {
             background-color: #F5F5F5;
             border-color: #CCCCCC;
             color: #515967;
        }

        .panel-footer {
             border-top: 1px solid #cccccc;
        }

        .panel-title {
            font-size: 15px;
            margin-left: 25px;
        }

        .panel-primary {
            border-color: #cccccc;
        }

        .panel-heading {
            padding: 5px 15px;
        }

        .panel-footer {
            padding: 5px 15px;
        }

        .form-group {
            margin-bottom: 7px;
        }

        body {
            font-size: 14px;
            line-height: 1.2;
        }

        .form-horizontal .control-label {
           padding-top: 2px;
        }

        .panel-body {
           padding: 7px;
        }

        .panel {
            margin-bottom: 7px;
        }

        .table {
          margin-bottom:7px;
        }

        /* For fixed width of html report label */
        td.active {
          width:15%;
        }

        /* For Container,Row etc. with no-padding */
        .no-padding-margin {
          padding-left:0;
          padding-right:0;
          margin-left:0;
          margin-right:0;
        }

        .text-right {
            text-align: right !important;
        }

        .doc-icon {
             font-size:  1.5em;
        }

        /* with no right padding */
        .no-right-padding {
           padding-right: 0 !important;
           margin: 0 !important;
        }

        .form-control {
            height: 30px;
            box-shadow: 0 0 1px rgba(0, 0, 0, 0.075) inset;
        }

        .btn {
            padding: 3px 12px;
        }

        /* override blockquote font-size */
            blockquote {
            font-size: 14px;
        }

        .popover {
        font-size: 14px;
        }

        .panel-icon {
            font-size: 15px;
            color: #428bca;
            padding-top: 2px;
        }
    """

    private static
    final String VALUE_CSS_KENDO_CUSTOM = """
        /*Application Kendo CSS is used to overwrite the default behaviour of kendo ui components*/

        input[type=text].k-textbox,input[type=password].k-textbox {
            height: 1.8em;
        }
        .k-textbox[type=text]:disabled,.k-textbox[type=password]:disabled,textarea:hover:disabled,textarea:disabled {
            background-color: #E3E3E3;
            border-color: #C5C5C5;
            color: #9F9E9E;
            opacity: 0.7;
            cursor: default;
            outline: 0 none
        }

        .k-menu .k-item > .k-link {
            padding: 0 0.9em 0;
        }

        #application_top_panel .k-widget,
        #application_top_panel .k-textbox {
            width: 100%;
        }

        /* Omit kendo behaviors on List view of html Reports */
        tbody.k-widget {
          border-style:none;
          position:static;
        }
        span.k-tooltip {
            padding: 2px 0;
        }/* For kendo grid filter text box */

        .k-filter-row .k-dropdown-operator {
           right:0;
        }
        .k-grid tbody>tr:last-child>td {
           border-bottom: 1px solid #F9F0F0;
        }
        .k-filtercell > span {
           padding-right:2.1em;
        }.k-grid .k-button{
          margin:0;
        }
        .k-grid .k-menu.k-menu-horizontal{
            border-style: none;
        }
    """

    private static final String VALUE_BUSINESS_SUPPORT = "<div class=\"col-md-4\">\n" +
            "    <div class=\"panel panel-default\">\n" +
            "        <div class=\"panel-heading\">\n" +
            "            <div class=\"panel-title\">Service</div>\n" +
            "        </div>\n" +
            "\n" +
            "        <div class=\"panel-body\">\n" +
            "            <div>No two businesses are alike.</div>\n" +
            "\n" +
            "            <div>&nbsp;</div>\n" +
            "\n" +
            "            <div>Athena offers fully customized solutions to meet the needs of your growing business and to help your company provide a unique and exceptional service to your customers</div>\n" +
            "        </div>\n" +
            "    </div>\n" +
            "</div>\n" +
            "\n" +
            "<div class=\"col-md-4\">\n" +
            "    <div class=\"panel panel-default\">\n" +
            "        <div class=\"panel-heading\">\n" +
            "            <div class=\"panel-title\">Modules</div>\n" +
            "        </div>\n" +
            "\n" +
            "        <div class=\"panel-body\">\n" +
            "            <div>1. Budget</div>\n" +
            "\n" +
            "            <div>2. Procurement</div>\n" +
            "\n" +
            "            <div>3. Inventory</div>\n" +
            "\n" +
            "            <div>4. Accounting</div>\n" +
            "\n" +
            "            <div>&nbsp;</div>\n" +
            "        </div>\n" +
            "    </div>\n" +
            "</div>\n" +
            "\n" +
            "<div class=\"col-md-4\">\n" +
            "    <div class=\"panel panel-default\">\n" +
            "        <div class=\"panel-heading\">\n" +
            "            <div class=\"panel-title\">Support</div>\n" +
            "        </div>\n" +
            "\n" +
            "        <div class=\"panel-body\">\n" +
            "            <div>Financial software needs support and updates as always.</div>\n" +
            "\n" +
            "            <div>&nbsp;</div>\n" +
            "\n" +
            "            <div>Our support includes data security, on demand report generation, regular   backup and periodic updates to ensure safely, security and keep you   always on track.</div>\n" +
            "        </div>\n" +
            "    </div>\n" +
            "</div>"


    private static
    final String VALUE_BUSINESS_SUPPORT_FOR_DMS = "<div class=\"col-md-6\" style=\"padding-right: 0px;\">\n" +
            "        <div class=\"form-group\">\n" +
            "             <!-- carousel start -->\n" +
            "            <div id=\"carousel-example-generic\" class=\"carousel slide\" data-ride=\"carousel\">\n" +
            "              <!-- Indicators -->\n" +
            "              <ol class=\"carousel-indicators\">\n" +
            "                <li data-target=\"#carousel-example-generic\" data-slide-to=\"0\" ></li>\n" +
            "                <li data-target=\"#carousel-example-generic\" data-slide-to=\"1\" class=\"active\"></li>\n" +
            "              </ol>\n" +
            "\n" +
            "              <!-- Wrapper for slides -->\n" +
            "              <div class=\"carousel-inner\">\n" +
            "                <div class=\"item active\">\n" +
            "                  <img style=\"width:100%\" src=\"/plugins/applicationplugin-0.1/theme/application/images/flexslider/dms_slide1.png\" alt=\"...\">\n" +
            "                  <div class=\"carousel-caption\">\n" +
            "                 <!--   <h2>this is my first imgae of carosol</h2> -->\n" +
            "                  </div>\n" +
            "                </div>\n" +
            "                 <div class=\"item\">\n" +
            "                  <img style=\"width:100%\" src=\"/plugins/applicationplugin-0.1/theme/application/images/flexslider/dms_slide2.png\" alt=\"...\">\n" +
            "                  <div class=\"carousel-caption\">\n" +
            "                  <!--  <h4>this is my first imgae of carosol</h4> -->\n" +
            "                  </div>\n" +
            "\n" +
            "                </div>\n" +
            "              </div>\n" +
            "\n" +
            "              <!-- Controls -->\n" +
            "              <a class=\"left carousel-control\" href=\"#carousel-example-generic\" role=\"button\" data-slide=\"prev\">\n" +
            "                <span class=\"glyphicon glyphicon-chevron-left\"></span>\n" +
            "              </a>\n" +
            "              <a class=\"right carousel-control\" href=\"#carousel-example-generic\" role=\"button\" data-slide=\"next\">\n" +
            "                <span class=\"glyphicon glyphicon-chevron-right\"></span>\n" +
            "              </a>\n" +
            "          </div>\n" +
            "          <!-- carousel end -->\n" +
            "        </div>\n" +
            "    </div>\n" +
            "\n" +
            "    <!-- Business support logo start -->\n" +
            "    <div class=\"col-md-4\" style=\"padding-right: 0px;\">\n" +
            "           <div class=\"panel panel-default\">\n" +
            "                   <div class=\"panel-heading\">\n" +
            "                           <div class=\"panel-title\">Source Database</div>\n" +
            "                   </div>\n" +
            "\n" +
            "                   <div class=\"panel-body\">\n" +
            "                                   <div class=\"col-md-6\">\n" +
            "                                           <img src='/plugins/applicationplugin-0.1/theme/application/images/login/postgres_sql_logo.png' style='height:90px;'>\n" +
            "                                   </div>\n" +
            "                                   <div class=\"col-md-6\">\n" +
            "                                           <img src='/plugins/applicationplugin-0.1/theme/application/images/login/my_sql_logo.png' style='height:80px;'>\n" +
            "                                   </div>\n" +
            "                   </div>\n" +
            "                   <div class=\"panel-body\">\n" +
            "                                   <div class=\"col-md-6\">\n" +
            "                                           <img src='/plugins/applicationplugin-0.1/theme/application/images/login/mssql-server_logo.png' style='height:90px;'>\n" +
            "                                   </div>\n" +
            "                                   <div class=\"col-md-6\">\n" +
            "                                           <img src='/plugins/applicationplugin-0.1/theme/application/images/login/oracle_logo.png' style='height:72px;'>\n" +
            "                                   </div>\n" +
            "                   </div>\n" +
            "           </div>\n" +
            "    </div>\n" +
            "    <!-- Business support logo end -->\n" +
            "\n" +
            "    <!-- Compliance start -->\n" +
            "   <div class=\"col-md-2\">\n" +
            "           <div class=\"panel panel-default\">\n" +
            "                   <div class=\"panel-heading\">\n" +
            "                           <div class=\"panel-title\">Target Database</div>\n" +
            "                   </div>\n" +
            "                   <div class=\"panel-body\">\n" +
            "                                   <div class=\"col-md-12\">\n" +
            "                                           <img src='/plugins/applicationplugin-0.1/theme/application/images/login/red_shift_logo.png' style='height:115px;' class='img-responsive center-block'>\n" +
            "                                   </div>\n" +
            "                   </div>\n" +
            "\n" +
            "                    <div class=\"panel-body\">\n" +
            "                                   <div class=\"col-md-12\">\n" +
            "                                           <img src='/plugins/applicationplugin-0.1/theme/application/images/login/green_plum_logo.png' style='height:65px;' class='img-responsive center-block'>\n" +
            "                                   </div>\n" +
            "                    </div>\n" +
            "           </div>\n" +
            "   </div>\n" +
            "  <!-- carousel end -->"

    private static final String VALUE_BUSINESS_SUPPORT_FOR_EXH = "<div class=\"col-md-6\">\n" +
            "        <div class=\"form-group\">\n" +
            "             <!-- carousel start -->\n" +
            "            <div id=\"carousel-example-generic\" class=\"carousel slide\" data-ride=\"carousel\">\n" +
            "              <!-- Indicators -->\n" +
            "              <ol class=\"carousel-indicators\">\n" +
            "                <li data-target=\"#carousel-example-generic\" data-slide-to=\"0\" ></li>\n" +
            "                <li data-target=\"#carousel-example-generic\" data-slide-to=\"1\" class=\"active\"></li>\n" +
            "              </ol>\n" +
            "\n" +
            "              <!-- Wrapper for slides -->\n" +
            "              <div class=\"carousel-inner\">\n" +
            "                <div class=\"item active\">\n" +
            "                  <img style=\"width:100%\" src=\"/plugins/applicationplugin-0.1/theme/application/images/flexslider/slide1.jpg\" alt=\"...\">\n" +
            "                  <div class=\"carousel-caption\">\n" +
            "                 <!--   <h2>this is my first imgae of carosol</h2> -->\n" +
            "                  </div>\n" +
            "                </div>\n" +
            "                 <div class=\"item\">\n" +
            "                  <img style=\"width:100%\" src=\"/plugins/applicationplugin-0.1/theme/application/images/flexslider/slide2.jpg\" alt=\"...\">\n" +
            "                  <div class=\"carousel-caption\">\n" +
            "                  <!--  <h4>this is my first imgae of carosol</h4> -->\n" +
            "                  </div>\n" +
            "\n" +
            "                </div>\n" +
            "              </div>\n" +
            "\n" +
            "              <!-- Controls -->\n" +
            "              <a class=\"left carousel-control\" href=\"#carousel-example-generic\" role=\"button\" data-slide=\"prev\">\n" +
            "                <span class=\"glyphicon glyphicon-chevron-left\"></span>\n" +
            "              </a>\n" +
            "              <a class=\"right carousel-control\" href=\"#carousel-example-generic\" role=\"button\" data-slide=\"next\">\n" +
            "                <span class=\"glyphicon glyphicon-chevron-right\"></span>\n" +
            "              </a>\n" +
            "          </div>\n" +
            "          <!-- carousel end -->\n" +
            "        </div>\n" +
            "    </div>\n" +
            "\n" +
            "    <div class=\"col-md-6\">\n" +
            "        <div class=\"panel panel-default\">\n" +
            "            <div class=\"panel-heading\">\n" +
            "                <div class=\"panel-title\">Anti Money Laundering Compliance</div>\n" +
            "            </div>\n" +
            "\n" +
            "            <div class=\"panel-body\">\n" +
            "                <div>Collection of Compliance rule are implemented and update frequently.</div>\n" +
            "\n" +
            "               <div>\n" +
            "                <img src=\"/plugins/applicationplugin-0.1/theme/application/images/login/hmt.png\" style=\"width:90px; height:auto\">\n" +
            "                <img src=\"/plugins/applicationplugin-0.1/theme/application/images/login/ofac.jpg\" style=\"width:auto; height:auto; padding-left:50px\">\n" +
            "               </div>\n" +
            "            </div>\n" +
            "        </div>\n" +
            "\n" +
            "        <div class=\"panel panel-default\">\n" +
            "            <div class=\"panel-heading\">\n" +
            "                <div class=\"panel-title\">Support</div>\n" +
            "            </div>\n" +
            "\n" +
            "            <div class=\"panel-body\">\n" +
            "                <!-- <div>Financial software needs support and updates as always.</div> -->\n" +
            "\n" +
            "                <!-- <div>&nbsp;</div> -->\n" +
            "\n" +
            "                  <div style=\"display:block\"> \n" +
            "                      <span style=\"float:left; width:100px;\"><img src=\"/plugins/applicationplugin-0.1/theme/application/images/login/hotline-logo.png\" style=\"width:100%\"></span>\n" +
            "                  <span style=\"float: left;\">\n" +
            "                     <span style=\"font-size:120%; color:#888888; font-weight:bold; font-family:cambria;\">\n" +
            "                      For Technical :  \n" +
            "                       </span>\n" +
            "                      <br>\n" +
            "                       <span style=\"font-size:120%; color:#00a600; font-weight:bold; font-family:cambria;\">\n" +
            "                        +880 1511230055\n" +
            "                       </span> \n" +
            "  <br>\n" +
            "                    <span style=\"font-size:120%; color:#888888; font-weight:bold; font-family:cambria;\">\n" +
            "                      For Business : \n" +
            "                    </span>\n" +
            "                     <br>\n" +
            "                     <span style=\"font-size:120%; color:#00a600; font-weight:bold; font-family:cambria;\">\n" +
            "                     +44(0)20 7790 2434\n" +
            "                     </span>  \n" +
            "                  </span>\n" +
            "                  </div>\n" +
            "                <!-- <div>Our support\n" +
            "                includes data security, on demand report generation, regular   backup\n" +
            "                and periodic updates to ensure safety, security and keep you   always on\n" +
            "                track.</div> -->\n" +
            "            </div>\n" +
            "        </div>\n" +
            "    </div>"

    private static final String VALUE_ADVERTISING_PHRASE = "The Most Secured, Reliable, Scalable, Web2.0 Solution"
    private static
    final String VALUE_ADVERTISING_PHRASE_DPL = "Comprehensive, easy to use, reliable Data Pipeline solution."

    private static
    final String VALUE_KENDO_THEME = """<link rel="stylesheet" href="/plugins/applicationplugin-0.1/theme/application/css/kendo/kendo.silver.min.css"/>
                                        <link rel="stylesheet" href="/plugins/applicationplugin-0.1/theme/application/css/kendo/kendo.silver.mobile.min.css"/>
    """

    private static
    final String VALUE_PRODUCT_COPYRIGHT = '<div style="min-height:45px; margin-left: 5px; padding-left: 44px; background: url(/plugins/applicationplugin-0.1/theme/application/images/login/athena_login_logo.png) no-repeat;">\n' +
            '            Corolla MIS<br/>\n' +
            '            Copyright &copy; 2012 Athena Software Associates Ltd. All rights reserved.\n' +
            '        </div>';
    private static
    final String VALUE_PRODUCT_COPYRIGHT_EXH = "<div style=\"min-height:45px; margin-left: 5px; padding-left: 44px; background: url(/plugins/applicationplugin-0.1/theme/application/images/login/athena_login_logo.png) no-repeat;\">\n" +
            "            <a href=\"http://www.remittance.com.bd\">iRemittance</a><br/>\n" +
            "            Copyright &copy; 2015 <a href=\"http://www.athena.com.bd\">Athena Software Associates Ltd.</a> All rights reserved.\n" +
            "        </div>"

    private static
    final String VALUE_PRODUCT_COPYRIGHT_FOR_DMS = '<div style="min-height:45px; margin-left: 5px; padding-left: 44px; background: url(/plugins/applicationplugin-0.1/theme/application/images/login/athena_login_logo.png) no-repeat;">\n' +
            '            <br/>\n' +
            '            Athena Data Pipeline Software Appliance.\n' +
            '        </div>';

    private static final String VALUE_NO_ACCESS_PAGE = "<div class=\"jumbotron\">\n" +
            "    <div class=\"container\">\n" +
            "        <div class=\"row\">\n" +
            "            <div class=\"col-md-10\">\n" +
            "                <h1>Sorry, this page is not available.</h1>\n" +
            "\n" +
            "                <p>Lets try one of the following remedies:</p>\n" +
            "                <ul>\n" +
            "                    <li>If you typed the page address in the address bar, make sure that it is spelled correctly.</li>\n" +
            "\n" +
            "                    <li>Click the <a\n" +
            "                            href=\"javascript:history.back(1)\">back</a> button to go back to the previous page.\n" +
            "                    </li>\n" +
            "                    <li>Click <a href=\"/\">here</a>  to go directly to the Application home page.\n" +
            "                    </li>\n" +
            "                    <li>If all else fails, contact with system administrator.</li>\n" +
            "                </ul>\n" +
            "            </div>\n" +
            "            <div class=\"col-md-2\">\n" +
            "                <span class=\"fa fa-exclamation-triangle\" style=\"font-size: 15em;color: #428BCA\"></span>\n" +
            "            </div>\n" +
            "        </div>\n" +
            "    </div>\n" +
            "</div>"
    /**
     * Method to create default data for MIS plugin
     */
    public boolean createDefaultData(long companyId) {
        try {
            if (PluginConnector.isPluginInstalled(ExchangeHousePluginConnector.PLUGIN_NAME)) {
                createDefaultDataExchangeHouse(companyId)
                return true
            } else if (PluginConnector.isPluginInstalled(DocumentPluginConnector.PLUGIN_NAME)) {
                createDefaultDataDocument(companyId)
                return true
            } else if (PluginConnector.isPluginInstalled(DataPipeLinePluginConnector.PLUGIN_NAME)) {
                createDefaultDataDataPipeLine(companyId)
                return true
            }
            new AppTheme(key: KEY_WELCOME_TEXT, value: "Welcome to MIS", companyId: companyId, description: 'Welcome title of the company', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_COMPANY_LOGO_LEFTMENU, value: "/images/athena_logo.png", companyId: companyId, description: 'Company logo on left menu panel', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_COMPANY_NAME, value: "Athena Software Associates Ltd.", companyId: companyId, description: 'Name of the company', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_COMPANY_COPYRIGHT_LEFTMENU, value: "Athena Software </br>Associates Ltd.© 2012", companyId: companyId, description: 'Company copy right text for left menu panel', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_PRODUCT_COPYRIGHT, value: VALUE_PRODUCT_COPYRIGHT, companyId: companyId, description: 'Name of the product', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_COMPANY_WEBSITE, value: "http://www.athena.com.bd", companyId: companyId, description: 'URL of the company web site', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_IMG_LOGIN_TOP_RIGHT, value: "/images/login/top_right_mis.png", companyId: companyId, description: 'Top right image of login page', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_CSS_MAIN_COMPONENTS, value: VALUE_CSS_MAIN_COMPONENTS, companyId: companyId, description: 'The CSS that defines styles of html controls throughout the project. Whole CSS is rendered dynamically in main template', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_CSS_BOOTSTRAP_CUSTOM, value: VALUE_CSS_BOOTSTRAP_CUSTOM, companyId: companyId, description: 'The CSS overwrites styles of default Bootstrap. Whole CSS is rendered dynamically in main template', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_CSS_KENDO_CUSTOM, value: VALUE_CSS_KENDO_CUSTOM, companyId: companyId, description: 'The CSS overwrites styles of default Kendo. Whole CSS is rendered dynamically in main template', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_LOGIN_PAGE_CAUTION, value: "&nbsp;", companyId: companyId, description: 'The text displayed above business support on login page', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_BUSINESS_SUPPORT, value: VALUE_BUSINESS_SUPPORT, companyId: companyId, description: 'Description of business support', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_ADVERTISING_PHRASE, value: VALUE_ADVERTISING_PHRASE, companyId: companyId, description: 'Advertising phrase', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_NO_ACCESS_PAGE, value: VALUE_NO_ACCESS_PAGE, companyId: companyId, description: 'Template for No Access page', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_KENDO_THEME, value: VALUE_KENDO_THEME, companyId: companyId,
                    description: """DEFAULT = kendo.default.min.css;\t
                    BLACK = kendo.black.min.css;\t
                    BLUEOPAL = kendo.blueopal.min.css;\t
                    BOOTSTRAP = kendo.bootstrap.min.css;\t
                    FIORI = kendo.fiori.min.css;\t
                    FLAT = kendo.fiori.min.css;\t
                    HIGH_CONYRAST = kendo.highcontrast.min.css;\t
                    MATERIAL = kendo.material.min.css;\t
                    MATERIAL_BLACK = kendo.materialblack.min.css;\t
                    METRO = kendo.metro.min.css;\t
                    METRO_BLACK = kendo.metroblack.min.css;\t
                    MOONLIGHT = kendo.moonlight.min.css;\t
                    NOVA = kendo.nova.min.css;\t
                    OFFICE_365 = kendo.office365.min.css;\t
                    SILVER = kendo.silver.min.css;\t
                    UNIFORM = kendo.uniform.min.css;
                    """, pluginId: PluginConnector.PLUGIN_ID).save(false)
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Method to create default data for Exchange house plugin
     */
    public boolean createDefaultDataExchangeHouse(long companyId) {
        try {
            new AppTheme(key: KEY_WELCOME_TEXT, value: "Welcome to ARMS(Agent)", companyId: companyId, description: 'Welcome title of the company', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_COMPANY_LOGO_LEFTMENU, value: "/images/athena_logo.png", companyId: companyId, description: 'Company logo on left menu panel', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_COMPANY_NAME, value: "Athena Software Associates Ltd.", companyId: companyId, description: 'Name of the company', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_COMPANY_COPYRIGHT_LEFTMENU, value: "Athena Software </br>Associates Ltd.© 2012", companyId: companyId, description: 'Company copy right text for left menu panel', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_COMPANY_COPYRIGHT_LEFTMENU, value: "Athena Software </br>Associates Ltd.© 2012", companyId: companyId, description: 'Company copy right text for left menu panel', pluginId: ExchangeHousePluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_PRODUCT_COPYRIGHT, value: VALUE_PRODUCT_COPYRIGHT_EXH, companyId: companyId, description: 'Name of the product', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_COMPANY_WEBSITE, value: "http://www.athena.com.bd", companyId: companyId, description: 'URL of the company web site', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_IMG_LOGIN_TOP_RIGHT, value: "/images/login/top_right_exh.png", companyId: companyId, description: 'Top right image of login page', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_CSS_MAIN_COMPONENTS, value: VALUE_CSS_MAIN_COMPONENTS, companyId: companyId, description: 'The CSS that defines styles of html controls throughout the project. Whole CSS is rendered dynamically in main template', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_CSS_BOOTSTRAP_CUSTOM, value: VALUE_CSS_BOOTSTRAP_CUSTOM, companyId: companyId, description: 'The CSS overwrites styles of default Bootstrap. Whole CSS is rendered dynamically in main template', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_CSS_KENDO_CUSTOM, value: VALUE_CSS_KENDO_CUSTOM, companyId: companyId, description: 'The CSS overwrites styles of default Kendo. Whole CSS is rendered dynamically in main template', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_LOGIN_PAGE_CAUTION, value: "'Southeast Financial Services- 02077902434' will be displayed on your bank or card statement for all transactions processed through this website.", companyId: companyId, description: 'The text displayed above business support on login page', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_BUSINESS_SUPPORT, value: VALUE_BUSINESS_SUPPORT_FOR_EXH, companyId: companyId, description: 'Description of business support', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_ADVERTISING_PHRASE, value: VALUE_ADVERTISING_PHRASE, companyId: companyId, description: 'Advertising phrase', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_NO_ACCESS_PAGE, value: VALUE_NO_ACCESS_PAGE, companyId: companyId, description: 'Template for No Access page', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_KENDO_THEME, value: VALUE_KENDO_THEME, companyId: companyId,
                    description: """DEFAULT = kendo.default.min.css;\t
                    BLACK = kendo.black.min.css;\t
                    BLUEOPAL = kendo.blueopal.min.css;\t
                    BOOTSTRAP = kendo.bootstrap.min.css;\t
                    FIORI = kendo.fiori.min.css;\t
                    FLAT = kendo.fiori.min.css;\t
                    HIGH_CONYRAST = kendo.highcontrast.min.css;\t
                    MATERIAL = kendo.material.min.css;\t
                    MATERIAL_BLACK = kendo.materialblack.min.css;\t
                    METRO = kendo.metro.min.css;\t
                    METRO_BLACK = kendo.metroblack.min.css;\t
                    MOONLIGHT = kendo.moonlight.min.css;\t
                    NOVA = kendo.nova.min.css;\t
                    OFFICE_365 = kendo.office365.min.css;\t
                    SILVER = kendo.silver.min.css;\t
                    UNIFORM = kendo.uniform.min.css;
                """, pluginId: PluginConnector.PLUGIN_ID).save(false)
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Method to create default data for Exchange house plugin
     */
    public boolean createDefaultDataArms(long companyId) {
        try {
            new AppTheme(key: KEY_COMPANY_COPYRIGHT_LEFTMENU, value: "Athena Software </br>Associates Ltd.© 2012", companyId: companyId, description: 'Company copy right text for left menu panel', pluginId: ArmsPluginConnector.PLUGIN_ID).save(false)
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Method to create default data for Exchange house plugin
     */
    public boolean createDefaultDataSarb(long companyId) {
        try {
            new AppTheme(key: KEY_COMPANY_COPYRIGHT_LEFTMENU, value: "Athena Software </br>Associates Ltd.© 2012", companyId: companyId, description: 'Company copy right text for left menu panel', pluginId: SarbPluginConnector.PLUGIN_ID).save(false)
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Method to create default data for Document plugin
     */
    public boolean createDefaultDataDocument(long companyId) {
        try {
            new AppTheme(key: KEY_WELCOME_TEXT, value: "Welcome to Athena", companyId: companyId, description: 'Welcome title of the company', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_COMPANY_LOGO_LEFTMENU, value: "/images/athena_logo.png", companyId: companyId, description: 'Company logo on left menu panel', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_COMPANY_NAME, value: "Athena Software Associates Ltd.", companyId: companyId, description: 'Name of the company', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_COMPANY_COPYRIGHT_LEFTMENU, value: "Athena Software </br>Associates Ltd.© 2012", companyId: companyId, description: 'Company copy right text for left menu panel', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_COMPANY_COPYRIGHT_LEFTMENU, value: "Athena Software </br>Associates Ltd.© 2012", companyId: companyId, description: 'Company copy right text for left menu panel', pluginId: DocumentPluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_PRODUCT_COPYRIGHT, value: VALUE_PRODUCT_COPYRIGHT_FOR_DMS, companyId: companyId, description: 'Name of the product', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_COMPANY_WEBSITE, value: "http://www.athena.com.bd", companyId: companyId, description: 'URL of the company web site', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_IMG_LOGIN_TOP_RIGHT, value: "/images/login/top_right_doc.png", companyId: companyId, description: 'Top right image of login page', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_CSS_MAIN_COMPONENTS, value: VALUE_CSS_MAIN_COMPONENTS, companyId: companyId, description: 'The CSS that defines styles of html controls throughout the project. Whole CSS is rendered dynamically in main template', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_CSS_BOOTSTRAP_CUSTOM, value: VALUE_CSS_BOOTSTRAP_CUSTOM, companyId: companyId, description: 'The CSS overwrites styles of default Bootstrap. Whole CSS is rendered dynamically in main template', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_CSS_KENDO_CUSTOM, value: VALUE_CSS_KENDO_CUSTOM, companyId: companyId, description: 'The CSS overwrites styles of default Kendo. Whole CSS is rendered dynamically in main template', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_LOGIN_PAGE_CAUTION, value: "&nbsp;", companyId: companyId, description: 'The text displayed above business support on login page', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_BUSINESS_SUPPORT, value: VALUE_BUSINESS_SUPPORT_FOR_DMS, companyId: companyId, description: 'Description of business support', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_ADVERTISING_PHRASE, value: VALUE_ADVERTISING_PHRASE_DPL, companyId: companyId, description: 'Advertising phrase', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_KENDO_THEME, value: VALUE_KENDO_THEME, companyId: companyId,
                    description: """DEFAULT = kendo.default.min.css;\t
                    BLACK = kendo.black.min.css;\t
                    BLUEOPAL = kendo.blueopal.min.css;\t
                    BOOTSTRAP = kendo.bootstrap.min.css;\t
                    FIORI = kendo.fiori.min.css;\t
                    FLAT = kendo.fiori.min.css;\t
                    HIGH_CONYRAST = kendo.highcontrast.min.css;\t
                    MATERIAL = kendo.material.min.css;\t
                    MATERIAL_BLACK = kendo.materialblack.min.css;\t
                    METRO = kendo.metro.min.css;\t
                    METRO_BLACK = kendo.metroblack.min.css;\t
                    MOONLIGHT = kendo.moonlight.min.css;\t
                    NOVA = kendo.nova.min.css;\t
                    OFFICE_365 = kendo.office365.min.css;\t
                    SILVER = kendo.silver.min.css;\t
                    UNIFORM = kendo.uniform.min.css;
                """, pluginId: PluginConnector.PLUGIN_ID).save(false)
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Method to create default data for Data PipeLine plugin
     */
    public boolean createDefaultDataDataPipeLine(long companyId) {
        try {
            new AppTheme(key: KEY_WELCOME_TEXT, value: "Welcome to Athena", companyId: companyId, description: 'Welcome title of the company', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_COMPANY_LOGO_LEFTMENU, value: "/images/athena_logo.png", companyId: companyId, description: 'Company logo on left menu panel', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_COMPANY_NAME, value: "Athena Software Associates Ltd.", companyId: companyId, description: 'Name of the company', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_COMPANY_COPYRIGHT_LEFTMENU, value: "Athena Software </br>Associates Ltd.© 2012", companyId: companyId, description: 'Company copy right text for left menu panel', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_COMPANY_COPYRIGHT_LEFTMENU, value: "Athena Data Pipeline Software Appliance", companyId: companyId, description: 'Company copy right text for left menu panel', pluginId: DataPipeLinePluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_PRODUCT_COPYRIGHT, value: VALUE_PRODUCT_COPYRIGHT_FOR_DMS, companyId: companyId, description: 'Name of the product', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_COMPANY_WEBSITE, value: "http://www.athena.com.bd", companyId: companyId, description: 'URL of the company web site', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_IMG_LOGIN_TOP_RIGHT, value: "/images/login/top_right_doc.png", companyId: companyId, description: 'Top right image of login page', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_CSS_MAIN_COMPONENTS, value: VALUE_CSS_MAIN_COMPONENTS, companyId: companyId, description: 'The CSS that defines styles of html controls throughout the project. Whole CSS is rendered dynamically in main template', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_CSS_BOOTSTRAP_CUSTOM, value: VALUE_CSS_BOOTSTRAP_CUSTOM, companyId: companyId, description: 'The CSS overwrites styles of default Bootstrap. Whole CSS is rendered dynamically in main template', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_CSS_KENDO_CUSTOM, value: VALUE_CSS_KENDO_CUSTOM, companyId: companyId, description: 'The CSS overwrites styles of default Kendo. Whole CSS is rendered dynamically in main template', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_LOGIN_PAGE_CAUTION, value: "&nbsp;", companyId: companyId, description: 'The text displayed above business support on login page', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_BUSINESS_SUPPORT, value: VALUE_BUSINESS_SUPPORT_FOR_DMS, companyId: companyId, description: 'Description of business support', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_ADVERTISING_PHRASE, value: VALUE_ADVERTISING_PHRASE_DPL, companyId: companyId, description: 'Advertising phrase', pluginId: PluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_KENDO_THEME, value: VALUE_KENDO_THEME, companyId: companyId,
                    description: """DEFAULT = kendo.default.min.css;\t
                    BLACK = kendo.black.min.css;\t
                    BLUEOPAL = kendo.blueopal.min.css;\t
                    BOOTSTRAP = kendo.bootstrap.min.css;\t
                    FIORI = kendo.fiori.min.css;\t
                    FLAT = kendo.fiori.min.css;\t
                    HIGH_CONYRAST = kendo.highcontrast.min.css;\t
                    MATERIAL = kendo.material.min.css;\t
                    MATERIAL_BLACK = kendo.materialblack.min.css;\t
                    METRO = kendo.metro.min.css;\t
                    METRO_BLACK = kendo.metroblack.min.css;\t
                    MOONLIGHT = kendo.moonlight.min.css;\t
                    NOVA = kendo.nova.min.css;\t
                    OFFICE_365 = kendo.office365.min.css;\t
                    SILVER = kendo.silver.min.css;\t
                    UNIFORM = kendo.uniform.min.css;
                """, pluginId: PluginConnector.PLUGIN_ID).save(false)
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Method to create default data for ProjectTrack plugin
     */
    public boolean createDefaultDataProjectTrack(long companyId) {
        try {
            new AppTheme(key: KEY_COMPANY_COPYRIGHT_LEFTMENU, value: "Athena Software </br>Associates Ltd.© 2012", companyId: companyId, description: 'Company copy right text for left menu panel', pluginId: PtPluginConnector.PLUGIN_ID).save(false)
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Method to create default data for Procurement plugin
     */
    public boolean createDefaultDataProcurement(long companyId) {
        try {
            new AppTheme(key: KEY_COMPANY_COPYRIGHT_LEFTMENU, value: "Athena Software </br>Associates Ltd.© 2012", companyId: companyId, description: 'Company copy right text for left menu panel', pluginId: ProcPluginConnector.PLUGIN_ID).save(false)
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Method to create default data for Budget plugin
     */
    public boolean createDefaultDataBudget(long companyId) {
        new AppTheme(key: KEY_COMPANY_COPYRIGHT_LEFTMENU, value: "Athena Software </br>Associates Ltd.© 2012", companyId: companyId, description: 'Company copy right text for left menu panel', pluginId: BudgPluginConnector.PLUGIN_ID).save(false)
    }

    /**
     * Method to create default data for Accounting plugin
     */
    public boolean createDefaultDataAccounting(long companyId) {
        try {
            new AppTheme(key: KEY_COMPANY_COPYRIGHT_LEFTMENU, value: "Athena Software </br>Associates Ltd.© 2012", companyId: companyId, description: 'Company copy right text for left menu panel', pluginId: AccPluginConnector.PLUGIN_ID).save(false)
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Method to create default data for Inventory plugin
     */
    public boolean createDefaultDataInventory(long companyId) {
        try {
            new AppTheme(key: KEY_COMPANY_COPYRIGHT_LEFTMENU, value: "Athena Software </br>Associates Ltd.© 2012", companyId: companyId, description: 'Company copy right text for left menu panel', pluginId: InvPluginConnector.PLUGIN_ID).save(false)
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Method to create default data for QS plugin
     */
    public boolean createDefaultDataQs(long companyId) {
        try {
            new AppTheme(key: KEY_COMPANY_COPYRIGHT_LEFTMENU, value: "Athena Software </br>Associates Ltd.© 2012", companyId: companyId, description: 'Company copy right text for left menu panel', pluginId: QsPluginConnector.PLUGIN_ID).save(false)
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Method to create default data for Fixed Asset plugin
     */
    public boolean createDefaultDataFixedAsset(long companyId) {
        try {
            new AppTheme(key: KEY_COMPANY_COPYRIGHT_LEFTMENU, value: "Athena Software </br>Associates Ltd.© 2012", companyId: companyId, description: 'Company copy right text for left menu panel', pluginId: FxdPluginConnector.PLUGIN_ID).save(false)
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    private static final String VALUE_EL_LOGO = """
        <img width="50px" src="/plugins/applicationplugin-0.1/theme/application/images/login/athena_login_logo.png">
        <span style="font-size: 15px;"><b>E-learning Solution</b></span>
    """

    private static final String VALUE_EL_BANNER = """
        <div class="col-md-1"></div>
        <div class="col-md-10" style="background-color: #ffffff">
            <img class="img-responsive" src="/plugins/elearning-0.1/images/banner.png">
        </div>
        <div class="col-md-1"></div>
    """

    private static final String VALUE_EL_FOOTER = """
        <div lass="col-md-12">
            <hr style="margin: 1px"/>
        </div>
        <div class="col-md-1"></div>

        <div class="col-md-10 footer" id="footer-menu">
            <div class="row">
                <div class="col-md-4">
                    <ul class="list-unstyled list-inline links">
                        <li class="last footer-contact" style="padding-left: 20px;"><a style="color: #333;"
                                                                                     href="#/appPage/showPage?id=2">Contact Us</a></li>
                        <li class="last footer-contact"><a style="color: #333;" href="#/appPage/showPage?id=1">About Us</a></li>
                    </ul>
                </div>

                <div class="col-md-4" id="social-link">
                    <div class="row">
                        <div class="col-md-2"></div>
                        <div class="col-md-8">
                            <ul class="list-unstyled list-inline">
                                <li><a href="#"><i
                                        class="fa fa-facebook-square social"></i></a></li>
                                <li><a href="#"><i class="fa fa-twitter-square social"></i></a></li>
                                <li><a href="#"><i class="fa fa-google-plus-square social"></i></a>
                                </li>
                                <li><a href="#"><i class="fa fa-youtube-square social"></i></a></li>
                            </ul>
                        </div>
                        <div class="col-md-2"></div>
                    </div>
                </div>

                <div class="col-md-4">
                    <ul class="list-unstyled list-inline text-left">
                        <span class="pull-right" style="padding-right: 20px;">
                            Planning: a2i programme <br>
                            Implementation: Bangladesh Open University <br>
                            Powered by: <a href="http://www.athena.com.bd" target="_blank">Athena Software Associates Ltd.</a>
                        </span>
                    </ul>
                </div>
            </div>
        </div>

        <div class="col-md-1"></div>
    """

    private static final String VALUE_EL_HOME_RESOURCE = """
        <script>
            window.HELP_IMPROVE_VIDEOJS = false;
        </script>

        <div class='panel panel-primary'>
            <div class="panel-heading">
                <span class="pull-left"><i class="panel-icon fa fa-video-camera"></i></span>
                <div class="panel-title">মাননীয় প্রধানমন্ত্রী শেখ হাসিনার বক্তব্য</div>
            </div>

            <div class="panel-body">
                <div class="embed-responsive embed-responsive-16by9">
                    <video class="embed-responsive-item" controls preload="auto"
                           poster="http://www.campuslive24.com/en/wp-content/uploads/2015/04/pm5.jpg">
                        <source src="/elResource/video?id=1" type='video/mp4'>
                    </video>
                </div>
            </div>
        </div>

        <div class='panel panel-primary'>
            <div class="panel-heading">
                 <span class="pull-left"><i class="panel-icon fa fa-user"></i></span>
                <div class="panel-title">Active participant of this week</div>
            </div>

            <div class="panel-body">
                <div id="carousel-example-generic" class="carousel slide" data-ride="carousel">
                    <ol class="carousel-indicators">
                        <li data-target="#carousel-example-generic" data-slide-to="0" class="active"></li>
                        <li data-target="#carousel-example-generic" data-slide-to="1"></li>
                    </ol>

                    <div class="carousel-inner" role="listbox">
                        <div class="item active">
                            <div class="container-fluid">
                                <div class="row">
                                    <div class="col-md-4" align="center">
                                        <img class="img-responsive"
                                             src="http://www.victorclasses.com/images/student/student-512.png"
                                             alt="Quazi Md. Qutub Mia">
                                        Quazi Md. Qutub Mia
                                    </div>

                                    <div class="col-md-4" align="center">
                                        <img class="img-responsive"
                                             src="http://www.victorclasses.com/images/student/student-512.png"
                                             alt="Syed Mesbah Ahmed">
                                        Syed Mesbah Ahmed
                                    </div>

                                    <div class="col-md-4" align="center">
                                        <img class="img-responsive"
                                             src="https://nicolemichaelides.files.wordpress.com/2015/09/student-2-512.png"
                                             alt="Rumana Afroz">
                                        Rumana Afroz
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="item">
                            <div class="container-fluid">
                                <div class="row">
                                    <div class="col-md-4" align="center">
                                        <img class="img-responsive"
                                             src="http://www.victorclasses.com/images/student/student-512.png"
                                             alt="Syed Salahuddin">
                                        Syed Salahuddin
                                    </div>

                                    <div class="col-md-4" align="center">
                                        <img class="img-responsive"
                                             src="https://nicolemichaelides.files.wordpress.com/2015/09/student-2-512.png"
                                             alt="Meherun Nessa">
                                        Meherun Nessa
                                    </div>

                                    <div class="col-md-4" align="center">
                                        <img class="img-responsive"
                                             src="http://www.victorclasses.com/images/student/student-512.png"
                                             alt="Mirza Ehsanul Haque">
                                        Mirza Ehsanul Haque
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    """

    /**
     * Method to create default data for e-learning plugin
     */
    public boolean createDefaultDataElearning(long companyId) {
        try {
            new AppTheme(key: KEY_EL_LOGO, value: VALUE_EL_LOGO, companyId: companyId, description: 'Elearning logo', pluginId: ELearningPluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_EL_BANNER, value: VALUE_EL_BANNER, companyId: companyId, description: 'Elearning banner', pluginId: ELearningPluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_EL_FOOTER, value: VALUE_EL_FOOTER, companyId: companyId, description: 'Elearning footer', pluginId: ELearningPluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_EL_HOME_RESOURCE, value: VALUE_EL_HOME_RESOURCE, companyId: companyId, description: 'Elearning home page video resource', pluginId: ELearningPluginConnector.PLUGIN_ID).save(false)
            new AppTheme(key: KEY_COMPANY_COPYRIGHT_LEFTMENU, value: """<span style="font-size: 8px">Powered By<br/>Athena Software Associates Ltd.</span>""", companyId: companyId, description: 'Company copy right text for left menu panel', pluginId: ELearningPluginConnector.PLUGIN_ID).save(false)
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    @Override
    public void createDefaultSchema() {
        String keyIndex = "CREATE UNIQUE INDEX app_theme_key_plugin_id_company_id_idx ON app_theme(lower(key), plugin_id, company_id);"
        executeSql(keyIndex)
    }

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }
}
