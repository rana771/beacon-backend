<%@ page import="com.athena.mis.application.service.ReservedRoleService; com.athena.mis.integration.elearning.ELearningPluginConnector; com.athena.mis.application.config.AppSysConfigCacheService; com.athena.mis.PluginConnector; com.athena.mis.integration.datapipeline.DataPipeLinePluginConnector; com.athena.mis.integration.document.DocumentPluginConnector; com.athena.mis.integration.sarb.SarbPluginConnector; com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector; com.athena.mis.integration.arms.ArmsPluginConnector; com.athena.mis.integration.projecttrack.PtPluginConnector; com.athena.mis.integration.fixedasset.FxdPluginConnector; com.athena.mis.integration.qsmeasurement.QsPluginConnector; com.athena.mis.integration.accounting.AccPluginConnector; com.athena.mis.integration.inventory.InvPluginConnector; com.athena.mis.integration.procurement.ProcPluginConnector; com.athena.mis.integration.budget.BudgPluginConnector" %>
<div class='pane_title'>
    <i class="fa fa-bars show_left_menu" style="margin: 7px 5px; cursor: pointer" onclick="toggleLeftMenu();"></i>
    <i id="spinner" class="fa fa-2x fa-refresh fa-spin" style="margin: 2px 4px;color:#9F9F9F"></i>

    <div id="dockMenuContainer" class="pull-right">
        <ul class="nav nav-pills">
            <app:ifAllUrl urls="/budgPlugin/renderBudgetMenu">
                <app:renderDocMenu plugin_id="${BudgPluginConnector.PLUGIN_ID}">
                </app:renderDocMenu>
            </app:ifAllUrl>

            <app:ifAllUrl urls="/procPlugin/renderProcurementMenu">
                <app:renderDocMenu plugin_id="${ProcPluginConnector.PLUGIN_ID}">
                </app:renderDocMenu>
            </app:ifAllUrl>

            <app:ifAllUrl urls="/invPlugin/renderInventoryMenu">
                <app:renderDocMenu plugin_id="${InvPluginConnector.PLUGIN_ID}">
                </app:renderDocMenu>
            </app:ifAllUrl>

            <app:ifAllUrl urls="/accPlugin/renderAccountingMenu">
                <app:renderDocMenu plugin_id="${AccPluginConnector.PLUGIN_ID}">
                </app:renderDocMenu>
            </app:ifAllUrl>

            <app:ifAllUrl urls="/qsPlugin/renderQsMenu">
                <app:renderDocMenu plugin_id="${QsPluginConnector.PLUGIN_ID}">
                </app:renderDocMenu>
            </app:ifAllUrl>

            <app:ifAllUrl urls="/fxdPlugin/renderFixedAssetMenu">
                <app:renderDocMenu plugin_id="${FxdPluginConnector.PLUGIN_ID}">
                </app:renderDocMenu>
            </app:ifAllUrl>

            <app:ifAllUrl urls="/ptPlugin/renderProjectTrackMenu">
                <app:renderDocMenu plugin_id="${PtPluginConnector.PLUGIN_ID}">
                </app:renderDocMenu>
            </app:ifAllUrl>

            <sec:access url="/arms/renderArmsMenu">
                <app:renderDocMenu plugin_id="${ArmsPluginConnector.PLUGIN_ID}">
                </app:renderDocMenu>
            </sec:access>

            <sec:access url="/exhExchangeHouse/renderExchangeHouseMenu">
                <app:renderDocMenu plugin_id="${ExchangeHousePluginConnector.PLUGIN_ID}">
                </app:renderDocMenu>
            </sec:access>

            <sec:access url="/sarb/renderSarbMenu">
                <app:renderDocMenu plugin_id="${SarbPluginConnector.PLUGIN_ID}"
                                   depends_on="${ExchangeHousePluginConnector.PLUGIN_ID}">
                </app:renderDocMenu>
            </sec:access>

            <app:ifAnyUrl urls="/elearning/renderElearnMenu">
                <app:renderDocMenu plugin_id="${ELearningPluginConnector.PLUGIN_ID}"
                                   depends_on="${DocumentPluginConnector.PLUGIN_ID}" caption="${message(code: 'app.menu.elearningmenu',default: 'E-Learning',locale:session['org.springframework.web.servlet.i18n.SessionLocaleResolver.LOCALE']?:Locale.default)}" >
                </app:renderDocMenu>
            </app:ifAnyUrl>

            <app:ifAnyUrl urls="/document/renderDocumentMenu">
                <app:renderDocMenu plugin_id="${DocumentPluginConnector.PLUGIN_ID}" caption="${message(code:'app.menu.contentmenu',default:'Content',locale:session['org.springframework.web.servlet.i18n.SessionLocaleResolver.LOCALE']?:Locale.default)}">
                </app:renderDocMenu>
            </app:ifAnyUrl>

            <app:ifAnyUrl
                    urls="/dataPipeLine/renderDataPipeLineMenu">
                <app:renderDocMenu plugin_id="${DataPipeLinePluginConnector.PLUGIN_ID}">
                </app:renderDocMenu>
            </app:ifAnyUrl>

            <app:checkSysConfig key="${AppSysConfigCacheService.APP_SHOW_APPLICATION_MENU}" value="true" pluginId="${PluginConnector.PLUGIN_ID}">
                <app:ifAllUrl urls="/application/renderApplicationMenu">
                    <app:renderDocMenu plugin_id="${PluginConnector.PLUGIN_ID}" caption="${message(code:'app.menu.applicationmenu',default:'Application',locale:session['org.springframework.web.servlet.i18n.SessionLocaleResolver.LOCALE']?:Locale.default)}">
                    </app:renderDocMenu>
                </app:ifAllUrl>
            </app:checkSysConfig>

            <app:checkSysConfig key="${AppSysConfigCacheService.APP_SHOW_APPLICATION_MENU}" value="false"
                                pluginId="${PluginConnector.PLUGIN_ID}">
                <app:hasRoleType id="${ReservedRoleService.ROLE_TYPE_APP_DEVELOPMENT},${ReservedRoleService.ROLE_TYPE_APP_ADMIN},${ReservedRoleService.ROLE_TYPE_APP_RESELLER}">
                    <app:ifAllUrl urls="/application/renderApplicationMenu">
                        <app:renderDocMenu plugin_id="${PluginConnector.PLUGIN_ID}"
                                           caption="${message(code: 'app.menu.applicationmenu', default: 'Application', locale: session['org.springframework.web.servlet.i18n.SessionLocaleResolver.LOCALE'] ?: Locale.default)}">
                        </app:renderDocMenu>
                    </app:ifAllUrl>
                </app:hasRoleType>
            </app:checkSysConfig>


            <li class="dropdown">
                <a data-toggle="dropdown">
                    <span class="fa fa-lg fa-user" style="color: #428BCA"></span>&nbsp;<span
                        class="fa fa-caret-down"></span>
                </a>
                <ul role="menu" class="dropdown-menu dropdown-menu-right" style="z-index: 9999">
                    <li style="text-align: center" id="username"><app:sessionUser property='username'></app:sessionUser></li>
                    <li class='divider'></li>
                    <li><app:unReadMessage></app:unReadMessage></li>
                    <li><a href="#appUser/managePassword"><span class="fa fa-key"></span>&nbsp;Change Password</a>
                    </li>
                    <li><a href="#appUser/showProfile"><span class="fa fa-edit"></span>&nbsp;Edit Profile</a>
                    </li>
                    <li class='divider'></li>
                    <li class='dropdown-header'>Language Support<li>
                    <li><a href="appUser/switchToBangla">Bangla</a></li>
                    <li><a href="appUser/switchToEnglish">English</a></li>
                    <app:switchUser></app:switchUser>
                    <li class='divider'></li>
                    <li><a href="<g:createLink controller="logout"/>"><span
                            class="fa fa-sign-out"></span>&nbsp;Logout</a></li>
                </ul>
            </li>
        </ul>
    </div>
</div>