<%@ page import="com.athena.mis.application.service.ReservedRoleService;" %>
<app:ifAnyUrl urls="/marchant/show">

    <li><a href="#"><span><i class="tab-icon fa fa-gear c-gold"></i><span class="tabText">Beacon</span></span>
    </a>
        <ul class="menuTab">
            <sec:access url="/marchant/show">
                <li><a class='autoload' href="#marchant/show"><span><i class="pre-icon project"></i>
                </span> <span
                        class="menuText">Merchant</span></a></li>
            </sec:access>

            <sec:access url="/beacon/show">
                <li><a class='autoload' href="#beacon/show"><span><i class="pre-icon supplier"></i></span> <span
                        class="menuText">Beacon</span></a></li>
            </sec:access>
            <sec:access url="/zone/show">
                <li><a class='autoload' href="#zone/show"><span><i class="pre-icon supplier"></i></span> <span
                        class="menuText">Zone</span></a></li>
            </sec:access>
            <sec:access url="/place/show">
                <li><a class='autoload' href="#place/show"><span><i class="pre-icon supplier"></i></span> <span
                        class="menuText">Place</span></a></li>
            </sec:access>
            <sec:access url="/device/show">
                <li><a class='autoload' href="#device/show"><span><i class="pre-icon supplier"></i></span> <span
                        class="menuText">Add Device</span></a></li>
            </sec:access>
            <sec:access url="/campaign/show">
                <li><a class='autoload' href="#campaign/show"><span><i class="pre-icon supplier"></i>
                </span> <span
                        class="menuText">Campaign</span></a></li>
            </sec:access>
            <sec:access url="/content/show">
                <li><a class='autoload' href="#content/show"><span><i class="pre-icon supplier"></i>
                </span> <span
                        class="menuText">Content</span></a></li>
            </sec:access>
            <sec:access url="/report/showReport">
                <li><a class='autoload' href="#report/showReport"><span><i class="pre-icon supplier"></i>
                </span> <span
                        class="menuText">Report</span></a></li>
            </sec:access>
        </ul>
    </li>
</app:ifAnyUrl>





