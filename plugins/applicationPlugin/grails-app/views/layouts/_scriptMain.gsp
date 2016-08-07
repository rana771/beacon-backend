<script type="text/javascript">
    // init global vars
    var resetReqMapConfirmDialog = null;
    var taskCreateConfirmDialog = null;
    var taskCancelConfirmDialog = null;
    var myLayout, currentMenu = false;
    var leftMenuKendoModel = null, router = null;
    var MENU_ID_APPLICATION = '0';
    var MENU_ID_BUDGET = '1';
    var MENU_ID_PROCUREMENT = '2';
    var MENU_ID_INVENTORY = '3';
    var MENU_ID_ACCOUNTING = '4';
    var MENU_ID_QS = '5';
    var MENU_ID_FIXED_ASSET = '6';
    var MENU_ID_EXCHANGE_HOUSE = '9';
    var MENU_ID_PROJECT_TRACK = '10';
    var MENU_ID_ARMS = '11';
    var MENU_ID_SARB = '12';
    var MENU_ID_DOCUMENT = '13';
    var MENU_ID_DATA_PIPE_LINE = '14';
    var MENU_ID_E_LEARNING = '15';
    var MENU_ID_ICT_POOL = '17';
    var MENU_ID_BEACON = '18';

    $(document).ready(function () {
        var logoutUrl = "<g:createLink controller="logout"/>";
        onLoadMainLayout(logoutUrl);
    });

    function onLoadMainLayout() {
        var docMenuHeight = $('#dockMenuContainer').parent().height();
        var fullHeight = $(window).height();
        $('#contentHolder').parent().height(fullHeight - docMenuHeight - 10);

        initRouter();
        //bindAutoLoadClass();
        $.ajaxSetup({
            error: function (x, e) {
                if (x.status == 0) {
                    redirectToLogoutPage();
                } else if (x.status == 404 || x.status == 403 || x.status == 405) {
                    //alert('Requested URL not found.');
                    showLoadingSpinner(false);
                    redirectToLogoutPage();
                } else if (x.status == 500) {
                    redirectToLogoutPage();
                } else if (x.status == 414) {
                    alert('Error.\nRequest URL is too long.');
                } else if (e == 'parsererror') {
                    alert('Error.\nParsing JSON Request failed.');
                } else if (e == 'timeout') {
                    alert('Request Time out.');
                } else if (x.status == 705) {
                    var responseText = x.responseText;
                    $('#responseText').val(responseText);
                    $('#runTimeExceptionErrorModal').modal('show'); // defined in commonModals.gsp
                }
                else {
                    alert('Unknown Error.\n Exception:' + e + '\nResponse:' + +x.responseText + '\nStatus code:' + x.status);
                }
            }
        });
        try {
            errorData = $("<span></span>").html(errorData).text();
            errorData = errorData.toJSON();
        } catch (e) {
            errorData = false;
        }

        if (errorData) {
            $(errorData).each(function (idx) {
                showError(errorData[idx]);
            });
        }
    }

    function initRouter() {
        router = new kendo.Router();
        router.route("/:controller/:action", function (controller, action, params) {
        });
        router.bind("change", function (e) {
            var url = e.url;
            if ((url == "/") || (url == "")) {
                loadInitialLeftMenu();
                setFavicon();
                return false;
            }
            if (url.endsWith('Menu')) {
                loadLeftMenu(url, null);
            } else {
                load(url);
            }
        });
        router.start();
    }

    function clearDocMenuHover() {
        $('#dockMenuContainer > ul > li').removeClass('active');
    }


    function renderMenu(menuId, loc, url, loadHistory) {
        showLoadingSpinner(true);
        var currMenu = window.location.hash.substr(1);
        if (loadHistory) {
            if ((currentMenu.toString() == menuId) && (currMenu == loc)) {
                loadLeftMenu(loc, null);
            } else {
                currentMenu = menuId;
                router.navigate(loc);
            }
        } else {
            currentMenu = menuId;
            loadLeftMenu(loc, url);
        }
    }

    function renderApplicationMenu(url, loadHistory) {
        var loc = "application/renderApplicationMenu";
        renderMenu(MENU_ID_APPLICATION, loc, url, loadHistory);
    }

    function renderBudgetMenu(url, loadHistory) {
        var loc = "budgPlugin/renderBudgetMenu";
        renderMenu(MENU_ID_BUDGET, loc, url, loadHistory);
    }

    function renderProcurementMenu(url, loadHistory) {
        var loc = "procPlugin/renderProcurementMenu";
        renderMenu(MENU_ID_PROCUREMENT, loc, url, loadHistory);
    }

    function renderInventoryMenu(url, loadHistory) {
        var loc = "invPlugin/renderInventoryMenu";
        renderMenu(MENU_ID_INVENTORY, loc, url, loadHistory);
    }
    function renderAccountingMenu(url, loadHistory) {
        var loc = "accPlugin/renderAccountingMenu";
        renderMenu(MENU_ID_ACCOUNTING, loc, url, loadHistory);
    }
    function renderQsMenu(url, loadHistory) {
        var loc = "qsPlugin/renderQsMenu";
        renderMenu(MENU_ID_QS, loc, url, loadHistory);
    }
    function renderFixedAssetMenu(url, loadHistory) {
        var loc = "fxdPlugin/renderFixedAssetMenu";
        renderMenu(MENU_ID_FIXED_ASSET, loc, url, loadHistory);
    }
    function renderExchangeHouseMenu(url, loadHistory) {
        var loc = "exhExchangeHouse/renderExchangeHouseMenu";
        renderMenu(MENU_ID_EXCHANGE_HOUSE, loc, url, loadHistory);
    }
    function renderProjectTrackMenu(url, loadHistory) {
        var loc = "ptPlugin/renderProjectTrackMenu";
        renderMenu(MENU_ID_PROJECT_TRACK, loc, url, loadHistory);
    }

    function renderArmsMenu(url, loadHistory) {
        var loc = "arms/renderArmsMenu";
        renderMenu(MENU_ID_ARMS, loc, url, loadHistory);
    }

    function renderSarbMenu(url, loadHistory) {
        var loc = "sarb/renderSarbMenu";
        renderMenu(MENU_ID_SARB, loc, url, loadHistory);
    }

    function renderDocumentMenu(url, loadHistory) {
        var loc = "document/renderDocumentMenu";
        renderMenu(MENU_ID_DOCUMENT, loc, url, loadHistory);
    }

    function renderDataPipeLineMenu(url, loadHistory) {
        var loc = "dataPipeLine/renderDataPipeLineMenu";
        renderMenu(MENU_ID_DATA_PIPE_LINE, loc, url, loadHistory);
    }

    function renderELearningMenu(url, loadHistory) {
        var loc = "elearning/renderElearnMenu";
        renderMenu(MENU_ID_E_LEARNING, loc, url, loadHistory);
    }

    function renderIctPoolMenu(url, loadHistory) {
        var loc = "ictPool/renderIctPoolMenu";
        renderMenu(MENU_ID_ICT_POOL, loc, url, loadHistory);
    }

    function renderBeaconMenu(url, loadHistory) {
        var loc = "vsBeacon/renderBeaconMenu";
        renderMenu(MENU_ID_BEACON, loc, url, loadHistory);
    }

    function loadLeftMenu(loc, url) {
        selectDockMenu(loc);
        jQuery.ajax({
            type: 'post',
            url: loc,
            success: function (data, textStatus) {
                if (data == 'false') {
                    redirectToLogoutPage();
                } else {
                    populateLeftMenuAndDashBoard(data, url);
                }
            }
        });
    }

    function selectDockMenu(loc) {
        clearDocMenuHover();
        if (loc.endsWith('renderBudgetMenu')) {
            $('#dockMenuBudget').addClass('active');
            currentMenu = MENU_ID_BUDGET;
        } else if (loc.endsWith('renderApplicationMenu')) {
            $('#dockMenuSettings').addClass('active');
            currentMenu = MENU_ID_APPLICATION;

        } else if (loc.endsWith('renderProcurementMenu')) {
            $('#dockMenuProc').addClass('active');
            currentMenu = MENU_ID_PROCUREMENT;

        } else if (loc.endsWith('renderInventoryMenu')) {
            $('#dockMenuInv').addClass('active');
            currentMenu = MENU_ID_INVENTORY;

        } else if (loc.endsWith('renderAccountingMenu')) {
            $('#dockMenuAcc').addClass('active');
            currentMenu = MENU_ID_ACCOUNTING;

        } else if (loc.endsWith('renderQsMenu')) {
            $('#dockMenuQs').addClass('active');
            currentMenu = MENU_ID_QS;
        }
        else if (loc.endsWith('renderFixedAssetMenu')) {
            $('#dockMenuFixedAsset').addClass('active');
            currentMenu = MENU_ID_FIXED_ASSET;
        }
        else if (loc.endsWith('renderExchangeHouseMenu')) {
            $('#dockMenuExchangeHouse').addClass('active');
            currentMenu = MENU_ID_EXCHANGE_HOUSE;
        }
        else if (loc.endsWith('renderProjectTrackMenu')) {
            $('#dockMenuProjectTrack').addClass('active');
            currentMenu = MENU_ID_PROJECT_TRACK;
        }
        else if (loc.endsWith('renderArmsMenu')) {
            $('#dockMenuArms').addClass('active');
            currentMenu = MENU_ID_ARMS;
        }
        else if (loc.endsWith('renderSarbMenu')) {
            $('#dockMenuSarb').addClass('active');
            currentMenu = MENU_ID_SARB;
        }
        else if (loc.endsWith('renderDocumentMenu')) {
            $('#dockMenuDocument').addClass('active');
            currentMenu = MENU_ID_DOCUMENT;
        } else if (loc.endsWith('renderDataPipeLineMenu')) {
            $('#dockMenuDataPipeLine').addClass('active');
            currentMenu = MENU_ID_DATA_PIPE_LINE;
        } else if (loc.endsWith('renderElearnMenu')) {
            $('#dockMenuElearning').addClass('active');
            currentMenu = MENU_ID_E_LEARNING;
        } else if (loc.endsWith('renderIctPoolMenu')) {
            $('#dockMenuIctPool').addClass('active');
            currentMenu = MENU_ID_ICT_POOL;
        }else if (loc.endsWith('renderBeaconMenu')) {
            $('#dockMenuBeacon').addClass('active');
            currentMenu = MENU_ID_BEACON;
        }
    }

    function populateLeftMenuAndDashBoard(data, url) {
        var lstTemplates = $(data.lstTemplates);
        var menuData = lstTemplates[0].content;
        var dashBoardData = lstTemplates[1].content;
        var pluginId = lstTemplates[2].content;
        populateLeftMenu(menuData);
        populateCopyright(pluginId);
        bindAutoLoadClass();
        if (url == null) {
            populateDashBoard(dashBoardData);     // load corresponding dashboard
            leftMenuKendoModel.select(">li:first");  // select first tab
            leftMenuKendoModel.expand(">li:first");  // select first tab
        } else {
            findAndSetTabId(url);
            markSelectMenuItem(url);
        }
        showLoadingSpinner(false);
    }

    function populateCopyright(pluginId) {
        $('#copyrightDiv').attr('plugin_id', pluginId);
        $('#copyrightDiv').reloadMe();
    }

    /*
     Populate Kendo menu and init
     * */
    function populateLeftMenu(menuData) {
        $('#leftMenuKendo').html(menuData);

        if (leftMenuKendoModel) {
            leftMenuKendoModel.destroy();
        }
        $("#leftMenuKendo").kendoPanelBar({
            expandMode: "single"
        });
        leftMenuKendoModel = $("#leftMenuKendo").data("kendoPanelBar");
    }

    function populateDashBoard(data) {
        $('#contentHolder').html(data);
    }

    function loadInitialLeftMenu() {
        var childSize = $('#dockMenuContainer > ul').children('li').size();
        if (childSize > 0) {
            var defaultChild = $('#dockMenuContainer >ul').children('li[isDefault=true]');
            if (defaultChild.length > 0) {
                defaultChild.click();
            } else {
                var firstChild = $('#dockMenuContainer >ul').children('li:first-child');
                firstChild.click();
            }
        }
    }

    function loadNumberedMenu(menuNumber, url) {
        if ((currentMenu) && (menuNumber == currentMenu)) {
            if (url) {                       // url may not be supplied in case of non-menu contents(e.g. change pass)
                findAndSetTabId(url);       // set menu Tab
                markSelectMenuItem(url);    // mark menu selected
            }
            return false;
        }
        var childSize = $('#dockMenuContainer > ul').children('li').size();
        if (childSize > 0) {
            var specificChild = $('#dockMenuContainer > ul').children("li:nth-child(" + menuNumber + ")")

            // Now we have to reload a new menu(other than current one)
            clearDocMenuHover();
            specificChild.addClass('active');  // mark selected
            switch (menuNumber) {
                case MENU_ID_APPLICATION:
                    renderApplicationMenu(url, false);     // admin menu
                    break;
                case MENU_ID_BUDGET:
                    renderBudgetMenu(url, false);
                    break;
                case MENU_ID_PROCUREMENT:
                    renderProcurementMenu(url, false);
                    break;
                case MENU_ID_INVENTORY:
                    renderInventoryMenu(url, false);
                    break;
                case MENU_ID_ACCOUNTING:
                    renderAccountingMenu(url, false);
                    break;
                case MENU_ID_QS:
                    renderQsMenu(url, false);
                    break;
                case MENU_ID_FIXED_ASSET:
                    renderFixedAssetMenu(url, false);
                    break;
                case MENU_ID_EXCHANGE_HOUSE:
                    renderExchangeHouseMenu(url, false);
                    break;
                case MENU_ID_PROJECT_TRACK:
                    renderProjectTrackMenu(url, false);
                    break;
                case MENU_ID_ARMS:
                    renderArmsMenu(url, false);
                    break;
                case MENU_ID_SARB:
                    renderSarbMenu(url, false);
                    break;
                case MENU_ID_DOCUMENT:
                    renderDocumentMenu(url, false);
                    break;
                case MENU_ID_DATA_PIPE_LINE:
                    renderDataPipeLineMenu(url, false);
                    break;
                case MENU_ID_E_LEARNING:
                    renderELearningMenu(url, false);
                    break;
                case MENU_ID_ICT_POOL:
                    renderIctPoolMenu(url, false);
                    break;
                case MENU_ID_BEACON:
                    renderBeaconMenu(url, false);
                    break;
                default:
                    renderApplicationMenu(url, false);
            }
        }
    }

    function findAndSetTabId(url) {
        var divObj = $("a[href='" + url + "']").parents('li:last');
        var tabid = parseInt($(divObj).prevAll("li").size());
        setTabId(tabid);
    }

    function markSelectMenuItem(url) {
        $("#leftMenuKendo a").removeClass('k-state-selected selected k-state-focused');
        if ($("#leftMenuKendo a[href='" + url + "']").closest('ul').hasClass('menuDivSub')) {
            var item = $("#leftMenuKendo a[href='" + url + "']").closest('ul.menuDivSub').parent('li'); // to expand subMenu
            leftMenuKendoModel.expand(item);
        }
        $("#leftMenuKendo a[href='" + url + "']").addClass('k-state-selected selected').focus();
    }

    function redirectToLogoutPage() {
        window.location = "<g:createLink controller="logout"/>";
    }

    function toggleLeftMenu() {
        if ($("#containerLeftMenu").hasClass('hide_left_menu')) {
            $("#containerLeftMenu").show();
            $("#containerLeftMenu").removeClass('hide_left_menu').addClass('show_left_menu');
            $("#topMenuDiv").css('padding-left', 0);
            $("#contentHolder").css('padding-left', 5);
            $("#mainRightContainer").removeClass('col-md-12').addClass('col-md-10');
        } else {
            $("#containerLeftMenu").hide();
            $("#containerLeftMenu").removeClass('show_left_menu').addClass('hide_left_menu');
            $("#topMenuDiv").css('padding-left', 15);
            $("#contentHolder").css('padding-left', 20);
            $("#mainRightContainer").removeClass('col-md-10').addClass('col-md-12');
        }
    }

    function hideLeftMenu() {
        if ($("#containerLeftMenu").hasClass('show_left_menu')) {
            $("#containerLeftMenu").hide();
            $("#containerLeftMenu").removeClass('show_left_menu').addClass('hide_left_menu');
            $("#topMenuDiv").css('padding-left', 15);
            $("#contentHolder").css('padding-left', 20);
            $("#mainRightContainer").removeClass('col-md-10').addClass('col-md-12');
        }
    }

    function showLeftMenu() {
        if ($("#containerLeftMenu").hasClass('hide_left_menu')) {
            $("#containerLeftMenu").show();
            $("#containerLeftMenu").removeClass('hide_left_menu').addClass('show_left_menu');
            $("#topMenuDiv").css('padding-left', 0);
            $("#contentHolder").css('padding-left', 5);
            $("#mainRightContainer").removeClass('col-md-12').addClass('col-md-10');
        }
    }

</script>