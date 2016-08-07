<%@ page import="com.athena.mis.application.service.ReservedRoleService;" %>
<app:checkSystemUser isPowerUser="true">
    <app:ifAnyUrl urls="/vehicle/show,/project/show,/supplier/show,/appEmployee/show,/appCustomer/show,
/itemType/show,/item/showInventoryItem,/item/showNonInventoryItem,/appCountry/show,/appDesignation/show,/currency/show,/appBank/show,
/district/show,/appPage/show">

        <li><a href="#"><span><i class="tab-icon fa fa-gear c-gold"></i><span class="tabText">Settings</span></span>
        </a>
            <ul class="menuTab">
                <sec:access url="/vehicle/show">
                    <li><a class='autoload' href="#vehicle/show"><span><i class="pre-icon vehicle"></i></span> <span
                            class="menuText">Vehicle</span></a></li>
                </sec:access>
                <sec:access url="/project/show">
                    <li><a class='autoload' href="#project/show"><span><i class="pre-icon project"></i></span> <span
                            class="menuText">Project</span></a></li>
                </sec:access>
                <sec:access url="/supplier/show">
                    <li><a class='autoload' href="#supplier/show"><span><i class="pre-icon supplier"></i>
                    </span> <span
                            class="menuText">Supplier</span></a></li>
                </sec:access>
                <sec:access url="/appCustomer/show">
                    <li><a class='autoload' href="#appCustomer/show"><span><i class="pre-icon customer"></i>
                    </span> <span
                            class="menuText">Customer</span></a></li>
                </sec:access>
                <sec:access url="/appDesignation/show">
                    <li><a class="autoload" href="#appDesignation/show"><span><i class="pre-icon designation"></i>
                    </span><span class="menuText">Designation</span></a></li>
                </sec:access>
                <sec:access url="/appEmployee/show">
                    <li><a class='autoload' href="#appEmployee/show"><span><i class="pre-icon employee"></i>
                    </span> <span
                            class="menuText">Employee</span></a></li>
                </sec:access>
                <sec:access url="/itemType/show">
                    <li><a class='autoload' href="#itemType/show"><span><i class="pre-icon employee"></i>
                    </span> <span
                            class="menuText">Item Type</span></a></li>
                </sec:access>

                <app:ifAnyUrl urls="/item/showInventoryItem,/item/showNonInventoryItem">
                    <li><span><i class="pre-icon item"></i></span> <span class="menuText">Item</span>
                        <ul class="menuDivSub">
                            <sec:access url="/item/showInventoryItem">
                                <li><a class='autoload' href="#item/showInventoryItem"><span><i
                                        class="pre-icon-sub pre-icon move"></i></span> <span
                                        class="menuTextSub">Inventory</span></a></li>
                            </sec:access>
                            <sec:access url="/item/showNonInventoryItem">
                                <li><a class='autoload' href="#item/showNonInventoryItem"><span><i
                                        class="pre-icon-sub pre-icon budget"></i></span> <span
                                        class="menuTextSub">Non Inventory</span></a></li>
                            </sec:access>
                        </ul>
                    </li>
                </app:ifAnyUrl>
                <sec:access url="/currency/show">
                    <li><a class='autoload' href="#currency/show"><span><i
                            class="pre-icon currency"></i></span> <span class="menuText">Currency</span></a>
                    </li>
                </sec:access>
                <sec:access url="/appCountry/show">
                    <li><a class="autoload" href="#appCountry/show"><span><i class="pre-icon country"></i>
                    </span><span class="menuText">Country</span></a></li>
                </sec:access>
                <sec:access url="/district/show">
                    <li><a class='autoload' href="#district/show"><span><i
                            class="pre-icon district"></i></span> <span class="menuText">District</span></a>
                    </li>
                </sec:access>
                <sec:access url="/appBank/show">
                    <li><a class='autoload' href="#appBank/show"><span><i
                            class="pre-icon bank"></i></span> <span class="menuText">Bank</span></a>
                    </li>
                </sec:access>
                <sec:access url="/appPage/show">
                    <li><a class='autoload' href="#appPage/show"><span><i
                            class="pre-icon bank"></i></span> <span class="menuText">Page</span></a>
                    </li>
                </sec:access>
            </ul>
        </li>
    </app:ifAnyUrl>
</app:checkSystemUser>

<app:hasRoleType id="${ReservedRoleService.ROLE_TYPE_APP_DEVELOPMENT}">
    <app:checkSystemUser isConfigManager="true">
        <app:ifAnyUrl urls="/appUser/showOnlineUser,/systemConfiguration/show,/appUser/showForCompanyUser,
                 /systemEntityType/show,/appTheme/showTheme,/company/show,/contentCategory/show,/appMail/show,
                 /role/showMyRole,/appDbInstance/show,/testData/show">
            <li><a href="#"><span><i class="tap-pre development-tab"></i></span><span
                    class="tabText">Config Management</span>
            </a>
                <ul class="menuTab">
                    <sec:access url="/appUser/showOnlineUser">
                        <li><a class='autoload' href="#appUser/showOnlineUser"><span><i
                                class="pre-icon agent"></i></span> <span
                                class="menuText">Who's Online</span></a></li>
                    </sec:access>
                    <sec:access url="/systemConfiguration/show">
                        <li><a class='autoload' href="#systemConfiguration/show?plugin=1"><span><i
                                class="pre-icon sys-configuration"></i></span> <span
                                class="menuText">System Configuration</span></a></li>
                    </sec:access>
                    <sec:access url="/systemEntityType/show">
                        <li><a class='autoload' href="#systemEntityType/show?plugin=1"><span><i
                                class="pre-icon system-entity-type"></i></span> <span
                                class="menuText">System Entity Type</span></a></li>
                    </sec:access>
                    <sec:access url="/company/show">
                        <li><a class='autoload' href="#company/show"><span><i class="pre-icon company"></i></span> <span
                                class="menuText">Company</span></a></li>
                    </sec:access>
                    <sec:access url="/appTheme/showTheme">
                        <li><a class='autoload' href="#appTheme/showTheme?plugin=1"><span><i
                                class="pre-icon custom-group-balance"></i></span> <span
                                class="menuText">Theme</span></a></li>
                    </sec:access>
                    <sec:access url="/appMail/show">
                        <li><a class='autoload' href="#appMail/show?plugin=1"><span><i
                                class="pre-icon fa fa-envelope"></i></span> <span
                                class="menuText">Mail</span></a></li>
                    </sec:access>
                    <sec:access url="/appSms/showSms">
                        <li><a class='autoload' href="#appSms/showSms?plugin=1"><span><i
                                class="pre-icon fa fa-twitch"></i></span> <span
                                class="menuText">SMS</span></a></li>
                    </sec:access>
                    <sec:access url="/contentCategory/show">
                        <li><a class='autoload' href="#contentCategory/show"><span><i
                                class="pre-icon group-ledger"></i></span> <span
                                class="menuText">Content category</span></a></li>
                    </sec:access>
                    <app:ifAnyUrl urls="/appDbInstance/show,/appServerInstance/show,/appShellScript/showSql">
                        <li><span><i class="pre-icon fa fa-keyboard-o c-orange"></i></span> <span
                                class="menuText">Script</span>
                            <ul class="menuDivSub">
                                <sec:access url="/appShellScript/show">
                                    <li><a class='autoload' href="#appShellScript/show?plugin=1"><span><i
                                            class="pre-icon-sub pre-icon fa fa-keyboard-o c-orange"></i>
                                    </span> <span class="menuTextSub">Shell</span></a>
                                    </li>
                                </sec:access>
                                <sec:access url="/appShellScript/showSql">
                                    <li><a class='autoload' href="#appShellScript/showSql?plugin=1"><span><i
                                            class="pre-icon-sub pre-icon fa fa-database c-gold"></i>
                                    </span> <span class="menuTextSub">SQL</span></a>
                                    </li>
                                </sec:access>
                            </ul>
                        </li>
                    </app:ifAnyUrl>
                    <sec:access url="/appSchedule/show">
                        <li><a class='autoload' href="#appSchedule/show?plugin=1"><span><i
                                class="pre-icon fa fa-clock-o"></i></span> <span
                                class="menuText">Schedule Job</span></a></li>
                    </sec:access>
                    <sec:access url="/vendor/show">
                        <li><a class='autoload' href="#vendor/show">
                            <span><i class="pre-icon fa fa-th-list c-gold"></i></span>
                            <span class="menuText">Vendor</span>
                        </a>
                        </li>
                    </sec:access>
                    <app:ifAnyUrl urls="/appDbInstance/show,/appServerInstance/show">
                        <li><span><i class="pre-icon fa fa-cloud c-green"></i></span> <span
                                class="menuText">Cluster</span>
                            <ul class="menuDivSub">
                                <sec:access url="/appDbInstance/show">
                                    <li><a class='autoload' href="#appDbInstance/show"><span><i
                                            class="pre-icon-sub pre-icon fa fa-database c-gold"></i></span> <span
                                            class="menuTextSub">Database Credentials</span></a></li>
                                </sec:access>
                                <sec:access url="/appServerInstance/show">
                                    <li><a class='autoload' href="#appServerInstance/show"><span><i
                                            class="pre-icon-sub pre-icon fa fa-building-o c-orange"></i></span> <span
                                            class="menuTextSub">SSH Credentials</span></a></li>
                                </sec:access>
                            </ul>
                        </li>
                    </app:ifAnyUrl>

                    <sec:access url="/role/showMyRole">
                        <li><a class='autoload' href="#role/showMyRole"><span><i
                                class="pre-icon role"></i></span> <span
                                class="menuText">My Roles</span></a></li>
                    </sec:access>
                    <sec:access url="/testData/show">
                        <li><a class='autoload' href="#testData/show"><span><i
                                class="pre-icon store_summary"></i></span> <span
                                class="menuText">Data</span></a></li>
                    </sec:access>
                    <sec:access url="/appVersion/show">
                        <li><a class='autoload' href="#appVersion/show"><span><i
                                class="pre-icon fa fa-history"></i></span> <span
                                class="menuText">Release History</span></a></li>
                    </sec:access>
                </ul>
            </li>
        </app:ifAnyUrl>
    </app:checkSystemUser>
</app:hasRoleType>

<sec:ifAllGranted roles="ROLE_RESELLER">
    <app:ifAnyUrl urls="/company/showForReseller,/appUser/showForCompanyUser,/appUser/showAllUser">
        <li><a href="#"><span><i class="tap-pre development-tab"></i></span><span class="tabText">Reseller</span></a>

            <ul class="menuTab">
                <sec:access url="/company/showForReseller">
                    <li><a class='autoload' href="#company/showForReseller"><span><i class="pre-icon company"></i>
                    </span> <span
                            class="menuText">Company</span></a></li>
                </sec:access>
                <sec:access url="/appUser/showForCompanyUser">
                    <li><a class='autoload' href="#appUser/showForCompanyUser"><span><i class="pre-icon app-user"></i>
                    </span> <span
                            class="menuText">Company User</span></a></li>
                </sec:access>
                <sec:access url="/appUser/showAllUser">
                    <li><a class='autoload' href="#appUser/showAllUser"><span><i class="pre-icon application_user"></i>
                    </span> <span
                            class="menuText">All User</span></a></li>
                </sec:access>
            </ul>
        </li>
    </app:ifAnyUrl>
</sec:ifAllGranted>

<app:checkSystemUser isConfigManager="true">
    <app:ifAnyUrl
            urls="/benchmark/show,/benchmark/showForTruncateSampling,/benchmarkStar/show,/benchmarkStar/showForTruncateSampling">
        <li><a href="#"><span><i class="tap-pre budget-tab"></i></span><span class="tabText">Benchmark</span></a>

            <ul class="menuTab">
                <li><span><i class="pre-icon accounting voucher"></i></span> <span
                        class="menuText">Flat Table Write</span>
                    <ul class="menuDivSub">
                        <sec:access url="/benchmark/show">
                            <li><a class='autoload' href="#benchmark/show"><span><i
                                    class="pre-icon-sub pre-icon move"></i></span> <span
                                    class="menuTextSub">Write Load</span></a></li>
                        </sec:access>
                        <sec:access url="/benchmark/showForTruncateSampling">
                            <li><a class='autoload' href="#benchmark/showForTruncateSampling"><span><i
                                    class="pre-icon-sub pre-icon budget"></i></span> <span
                                    class="menuTextSub">Truncate</span></a></li>
                        </sec:access>
                    </ul>
                </li>
                <sec:access url="/benchmarkStar/show">
                    <li><span><i class="pre-icon item"></i></span> <span class="menuText">Relational Table Write</span>
                        <ul class="menuDivSub">
                            <li><a class='autoload' href="#benchmarkStar/show"><span><i
                                    class="pre-icon-sub pre-icon move"></i></span> <span
                                    class="menuTextSub">Write Load</span></a></li>
                            <sec:access url="/benchmarkStar/showForTruncateSampling">
                                <li><a class='autoload' href="#benchmarkStar/showForTruncateSampling"><span><i
                                        class="pre-icon-sub pre-icon budget"></i></span> <span
                                        class="menuTextSub">Truncate</span></a></li>
                            </sec:access>
                        </ul>
                    </li>
                </sec:access>
            </ul>
        </li>
    </app:ifAnyUrl>
</app:checkSystemUser>

<app:ifAnyUrl
        urls="/appUser/show,/role/show,/requestMap/show,/appGroup/show">
    <li><a href="#"><span><i class="tap-pre user-management"></i></span><span class="tabText">User Management</span></a>

        <ul class="menuTab">
            <app:checkSystemUser isPowerUser="true">
                <sec:access url="/appUser/show">
                    <li><a class='autoload' href="#appUser/show"><span><i class="pre-icon application_user"></i>
                    </span> <span
                            class="menuText">User</span></a></li>
                </sec:access>
                <sec:access url="/role/show">
                    <li><a class='autoload' href="#role/show"><span><i class="pre-icon role"></i></span> <span
                            class="menuText">Role</span></a></li>
                </sec:access>
            </app:checkSystemUser>
            <app:checkSystemUser isConfigManager="true">
                <sec:access url="/requestMap/show">
                    <li><a class='autoload' href="#requestMap/show"><span><i class="pre-icon role-right"></i>
                    </span> <span
                            class="menuText">Role Right Mapping</span></a></li>
                </sec:access>
            </app:checkSystemUser>
            <sec:access url="/appGroup/show">
                <li><a class="autoload" href="#appGroup/show"><span><i class="pre-icon user-group"></i>
                </span><span class="menuText">Group</span></a></li>
            </sec:access>
        </ul>
    </li>
</app:ifAnyUrl>

        <app:ifAnyUrl
                urls="/appMail/showForCompose,/appMail/showForSend,/appMail/showAnnouncement,/appMail/showForSend,/appMessage/show">
            <li><a href="#"><span><i class="tap-pre inventory-tab"></i></span><span class="tabText"><g:message code="app.menu.communication"/> </span>
            </a>
                <ul class="menuTab">
                    <app:ifAnyUrl urls="/appMail/showAnnouncement,/appMail/showForSend">
                        <li><span><i class="pre-icon fa fa-envelope"></i></span> <span
                                class="menuText"><g:message code="app.menu.announcement"/></span>
                            <ul class="menuDivSub">
                                <sec:access url="/appMail/showAnnouncement">
                                    <li><a class='autoload' href="#appMail/showAnnouncement"><span><i
                                            class="pre-icon-sub fa fa-pencil-square"></i></span> <span
                                            class="menuTextSub"><g:message code="app.menu.compose"/></span></a></li>
                                </sec:access>
                                <sec:access url="/appMail/showForSend">
                                    <li><a class='autoload' href="#appMail/showForSend"><span><i
                                            class="pre-icon-sub fa fa-send"></i></span> <span
                                            class="menuTextSub"><g:message code="app.menu.sentMail"/></span></a></li>
                                </sec:access>

                            </ul>
                        </li>
                    </app:ifAnyUrl>
                    <app:ifAnyUrl urls="/appMessage/show,/appMail/showForComposeMail,/appMail/showForSentMail">
                        <li><span><i class="pre-icon fa fa-envelope"></i></span> <span
                                class="menuText"><g:message code="app.menu.mail"/></span>
                            <ul class="menuDivSub">
                                <sec:access url="/appMail/showForComposeMail">
                                    <li><a class='autoload' href="#appMail/showForComposeMail"><span><i
                                            class="pre-icon-sub fa fa-pencil-square"></i></span> <span
                                            class="menuTextSub"><g:message code="app.menu.compose"/></span></a></li>
                                </sec:access>
                                <sec:access url="/appMessage/show">
                                    <li><a class='autoload' href="#appMessage/show"><span><i
                                            class="pre-icon-sub fa fa-inbox"></i></span> <span
                                            class="menuTextSub"><g:message code="app.menu.inbox"/></span></a></li>
                                </sec:access>
                                <sec:access url="/appMail/showForSentMail">
                                    <li><a class='autoload' href="#appMail/showForSentMail"><span><i
                                            class="pre-icon-sub fa fa-send"></i></span> <span
                                            class="menuTextSub"><g:message code="app.menu.sentItems"/></span></a></li>
                                </sec:access>
                            </ul>
                        </li>
                    </app:ifAnyUrl>
                    <app:ifAnyUrl urls="/appSms/showForCompose,/appSms/showForSend">
                        <li><span><i class="pre-icon fa fa-comment"></i></span> <span class="menuText"><g:message code="app.menu.sms"/></span>
                            <ul class="menuDivSub">
                                <sec:access url="/appSms/showForCompose">
                                    <li><a class='autoload' href="#appSms/showForCompose"><span><i
                                            class="pre-icon-sub fa fa-pencil-square"></i></span> <span
                                            class="menuTextSub"><g:message code="app.menu.compose"/></span></a></li>
                                </sec:access>
                                <sec:access url="/appSms/showForSend">
                                    <li><a class='autoload' href="#appSms/showForSend"><span><i
                                            class="pre-icon-sub fa fa-send"></i></span> <span
                                            class="menuTextSub"><g:message code="app.menu.sentSms"/></span></a></li>
                                </sec:access>
                            </ul>
                        </li>
                    </app:ifAnyUrl>
                </ul>
            </li>
        </app:ifAnyUrl>



