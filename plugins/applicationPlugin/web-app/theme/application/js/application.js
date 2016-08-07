var kendoNotificationModel;
$(document).ready(function () {
    var popupSpan = "<span id='globalPopupNotification' style='display:none;'></span>";
    kendoNotificationModel = $(popupSpan).kendoNotification(
        {
            position: {bottom: 5, right: 10}, autoHideAfter: 3500,
            templates: [{
                type: "success",
                template: $("#tmplKendoSuccess").html()
            }, {
                type: "error",
                template: $("#tmplKendoError").html()
            }, {
                type: "info",
                template: $("#tmplKendoInfo").html()
            }]

        }).data("kendoNotification");
    // for left menu click function
    $('#dockMenuBudget').click(function () {
        renderBudgetMenu(null, true);
        showLeftMenu();     // method declared in _scriptMain.gsp
    });
    $('#dockMenuProc').click(function () {
        renderProcurementMenu(null, true);
        showLeftMenu();     // method declared in _scriptMain.gsp
    });
    $('#dockMenuInv').click(function () {
        renderInventoryMenu(null, true);
        showLeftMenu();     // method declared in _scriptMain.gsp
    });
    $('#dockMenuAcc').click(function () {
        renderAccountingMenu(null, true);
        showLeftMenu();     // method declared in _scriptMain.gsp
    });
    $('#dockMenuQs').click(function () {
        renderQsMenu(null, true);
        showLeftMenu();     // method declared in _scriptMain.gsp
    });
    $('#dockMenuSettings').click(function () {
        renderApplicationMenu(null, true);
        showLeftMenu();     // method declared in _scriptMain.gsp
    });
    $('#dockMenuFixedAsset').click(function () {
        renderFixedAssetMenu(null, true);
        showLeftMenu();     // method declared in _scriptMain.gsp
    });
    $('#dockMenuExchangeHouse').click(function () {
        renderExchangeHouseMenu(null, true);
        showLeftMenu();     // method declared in _scriptMain.gsp
    });
    $('#dockMenuProjectTrack').click(function () {
        renderProjectTrackMenu(null, true);
        showLeftMenu();     // method declared in _scriptMain.gsp
    });
    $('#dockMenuArms').click(function () {
        renderArmsMenu(null, true);
        showLeftMenu();     // method declared in _scriptMain.gsp
    });
    $('#dockMenuSarb').click(function () {
        renderSarbMenu(null, true);
        showLeftMenu();     // method declared in _scriptMain.gsp
    });
    $('#dockMenuDocument').click(function () {
        renderDocumentMenu(null, true);
        showLeftMenu();     // method declared in _scriptMain.gsp
    });
    $('#dockMenuDataPipeLine').click(function () {
        renderDataPipeLineMenu(null, true);
        showLeftMenu();     // method declared in _scriptMain.gsp
    });

    $('#dockMenuElearning').click(function () {
        renderELearningMenu(null, true);
        showLeftMenu();     // method declared in _scriptMain.gsp
    });
    $('#dockMenuIctPool').click(function () {
        renderIctPoolMenu(null, true);
    });
    $('#dockMenuBeacon').click(function () {
        renderBeaconMenu(null, true);
    });
});

// assign kendo validator & bind onSubmit event
function initializeForm(form, func) {
    $(form).kendoValidator({validateOnBlur: false});

    $(form).submit(function (e) {
        func();
        return false;
    });
}

// return empty data source for kendo dropDown
function getKendoEmptyDataSource(dataModel, unselectedText) {
    var textMember = 'name'; // default value
    if (dataModel) {
        textMember = dataModel.options.dataTextField;
    }
    var unselectedData = new Object();
    unselectedData['id'] = '';
    unselectedData[textMember] = 'Please Select...'; // default value
    if (unselectedText)unselectedData[textMember] = unselectedText;
    return new kendo.data.DataSource({data: [unselectedData]});
}

// return a fully blank dataSoure
function getBlankDataSource() {
    return new kendo.data.DataSource({data: []});
}

function initKendoDropdown(control, paramTextMember, paramValueMember, paramDataSource) {
    if (!paramTextMember) paramTextMember = 'name';
    if (!paramValueMember) paramValueMember = 'id';
    if (!paramDataSource) paramDataSource = getKendoEmptyDataSource();

    $(control).kendoDropDownList({
        dataTextField: paramTextMember,
        dataValueField: paramValueMember,
        dataSource: paramDataSource
    });
    return $(control).data("kendoDropDownList");
}


function trimFormValues(form) {
    // iterate over all of the inputs for the form
    $(':input', form).each(function () {
        var type = this.type.toLowerCase();
        var tag = this.tagName.toLowerCase(); // normalize case

        // for inputs and textareas
        if (type == 'text' || type == 'hidden' || tag == 'textarea') {
            this.value = jQuery.trim(this.value);
        }
    });
}

function validateForm(form) {
    trimFormValues(form);
    var frmValidator = $(form).kendoValidator({
        validateOnBlur: false
    }).data("kendoValidator");
    if (!frmValidator.validate()) {
        return false;
    }
    return true;
}

function clearForm(frm, focusElement) {
    clearErrors(frm);
    clearFormValues(frm);
    if (focusElement) focusFieldElement(focusElement);
}

function focusFieldElement(focusElement) {
    $(focusElement).focus();
}

function clearErrors(form) {
    var frmValidator = $(form).kendoValidator({
        validateOnBlur: false
    }).data("kendoValidator");
    frmValidator.hideMessages();
}

function clearFormValues(form) {
    // iterate over all of the inputs for the form
    $(':input', form).each(function () {
        var type = this.type;
        var tag = this.tagName.toLowerCase(); // normalize case

        // password inputs, and textareas
        if (type == 'text' || type == 'password' || type == 'hidden' || tag == 'textarea' || type == 'email' || type == 'tel') {
            this.value = "";
        }

        // checkboxes and radios need to have their checked state cleared
        else if (type == 'checkbox' || type == 'radio')
            this.checked = false;

        // select elements need to have their 'selectedIndex' property set to -1
        else if (tag == 'select') {
            var dropDownKendo = $(this).data("kendoDropDownList");
            dropDownKendo.value('');
        }
    });

}

function setButtonDisabled(button, isDisable) {
    if (isDisable) {
        $(button).attr('disabled', 'disabled');
    } else {
        $(button).removeAttr('disabled');
    }

}

/*Method to disable kendo button*/
function setButtonDisabledKendo(button, isDisable) {
    var buttonObject = $(button).kendoButton({}).data("kendoButton");
    if (isDisable) {
        buttonObject.enable(false);
    } else {
        buttonObject.enable(true);
    }
}


function bindAutoLoadClass() {
    $('a.autoload').click(function (e) {
        var href = $(this).attr('href');
        var currentUrl = document.location.href;
        var currentPage = currentUrl.substring(currentUrl.indexOf('#', 0), currentUrl.length);
        if (currentPage == href) {
            var reqUrl = href.substring(1, href.length);
            load(reqUrl);
        }
    });
}


function setTabId(tabid) {
    leftMenuKendoModel.expand(">li:nth-child(" + (tabid + 1) + ")");
}

function load(loc) {
    showLoadingSpinner(true);
    jQuery.ajax({
        type: 'post',
        url: loc,                      //+ "?ajaxid=" + new Date().getTime()
        success: function (data, textStatus) {
            $('#contentHolder').html(data);
            showLoadingSpinner(false);
        },
        complete: function (XMLHttpRequest, textStatus) {
            showLoadingSpinner(false);

        }
    });
}
function setFavicon() {
    var link = $("link[type='image/x-icon']").remove().attr("href");
    $('<link href="' + link + '" rel="shortcut icon" type="image/x-icon" />').appendTo('head');
}

kendo.data.DataSource.prototype.options.error = function (e) {
    afterAjaxError(e.xhr, null);
};

function onErrorKendo(e) {
    afterAjaxError(e.XMLHttpRequest, e.XMLHttpRequest.statusText);
}

function afterAjaxError(XMLHttpRequest, textStatus) {

    if ((XMLHttpRequest == undefined) || (XMLHttpRequest.status == 200)) {
        return false;
    }
    if (XMLHttpRequest.status == 0) {
        redirectToLogoutPage(); // defined in main.gsp
    } else if (XMLHttpRequest.status == 404) {
        redirectToLogoutPage(); // defined in main.gsp
        showLoadingSpinner(false);
    }
    else if (XMLHttpRequest.status == 500) {
        redirectToLogoutPage(); // defined in main.gsp
    }

    // this is a custom response code for unhandled Exception
    else if (XMLHttpRequest.status == 705) {
        var responseText = XMLHttpRequest.responseText;
        $('#responseText').val(responseText);
        $('#runTimeExceptionErrorModal').modal('show'); // defined in commonModals.gsp
    }
    else if (XMLHttpRequest.status == 403) {
        //var responseUrl = this.url;
        var requestedUrl = "Requested URL : " + currentGridURL;
        $('#responseText').val(requestedUrl);
        $('#runTimeExceptionErrorModal').modal('show'); // defined in commonModals.gsp
    }

    else if (textStatus == 'parsererror') {
        alert('Error.\nParsing JSON Request failed.');
    } else if (textStatus == 'timeout') {
        alert('Request Time out.');
    } else {
        alert('Unknow Error...\n' + XMLHttpRequest.responseText);
    }
    return false;
}

function showLoadingSpinner(show) {
    if (show) {
        $('#spinner').show();
    } else {
        $('#spinner').hide();
    }
}


function onCompleteAjaxCall(XMLHttpRequest, textStatus) {
    showLoadingSpinner(false);
}

// function trims the leading / for hash loading
function formatLink(link) {
    if (link.indexOf('/') == 0) {
        link = link.substring(1, link.length);
    }
    return link;
}


function showError(errDescrip) {
    if (errDescrip.length == 0) {
        return false;
    }
    kendoNotificationModel.show({message: errDescrip}, "error");
}

function showSuccess(successDescrip) {
    if (successDescrip.length == 0) {
        return false;
    }
    kendoNotificationModel.show({message: successDescrip}, "success");
}

function showInfo(infoDescrip) {
    if (!infoDescrip || infoDescrip.length == 0) {
        return false;
    }
    kendoNotificationModel.show({message: infoDescrip}, "info");
}

String.prototype.endsWith = function (str) {
    return (this.match(str + "$") == str);
}

function checkAmountValidity(value) {
    try {
        value = $.trim(value);
        if (value.length <= 0) {
            return false
        }
        // check amount pattern
        var regexForAmount = /^[0-9]\d{0,10}(\.\d{1,2})?$/;
        if ((isNaN(value)) || (value <= 0) || (regexForAmount.test(value) == false)) {
            return false;
        }

        return true;
    } catch (ex) {
        return false;
    }
}

String.prototype.isEmpty = function () {
    try {
        return ($.trim(this).length == 0);
    } catch (e) {
        return true;
    }
}

function isEmpty(val) {
    var val2;
    val2 = $.trim(val);
    return (val2.length == 0);
}

/*Function is responsible to refresh dropDown by url
 * 1. Extract all attributes
 * 2. build url parameter with attributes
 * 3. fire the url, receive html data & update div with data
 * */

$.fn.reloadMe = function (callbackFunc, customDivToUpdate) {
    var attributes = this.get(0).attributes;
    var params = "?" + attributes[0].name + "=" + attributes[0].value;
    for (var i = 1; i < attributes.length; i++) {
        params = params + "&" + attributes[i].name + "=" + encodeURIComponent(attributes[i].value);
    }
    var url = this.attr('url');
    var divToUpdate = (typeof customDivToUpdate != "undefined") ? $(customDivToUpdate) : $(this).parent().closest('div');
    var isDropDown = this.is('select');
    if (isDropDown) {
        $(this).prev('span.k-dropdown-wrap').find('span.k-icon').toggleClass('k-loading');
    } else {
        showLoadingSpinner(true);
    }
    jQuery.ajax({
        type: 'post', dataType: 'html', url: url + params,
        success: function (data, textStatus) {
            $(divToUpdate).html(data);
            if (callbackFunc) callbackFunc();
        },
        complete: function (XMLHttpRequest, textStatus) {
            if (isDropDown) {
                $(this).prev('span.k-dropdown-wrap').find('span.k-icon').toggleClass('k-loading');
            } else {
                showLoadingSpinner(false);
            }
        }
    });
};

/*function getGridHeight(numberOfGridBar) {
 var extraHieght = 0;
 if ($('#message_div').length) {
 extraHieght = $('#message_div').height() + 4;
 }
 var totalHeight = (27 * 2 + 31 + 8 + 2) + 5;
 if (numberOfGridBar != 3) {
 totalHeight += 27;
 }

 var gridHeight = $("#contentHolder").height() - ($("#application_top_panel").height() + 2 + totalHeight + extraHieght);
 return gridHeight;
 }*/
function getFullGridHeight() {
    var totalHeight = (27 * 2 + 31 + 8 + 2) + 5;
    if ($('.tDiv').length) {
        totalHeight += 27;
    }
    var gridHeight = $("#contentHolder").height() - totalHeight;
    return gridHeight;
}

function getFullGridHeightKendo() {
    var gridHeight = $("#contentHolder").height() - 20;
    return gridHeight;
}

function getGridHeightKendo() {
    var height = $("#contentHolder").height() - $("#application_top_panel").height() - 30;
    return height;
}

function clearGridKendo(gridModel) {
    gridModel.dataSource.data([]);
}

// function to check if one or more grid row is selected or not
/*function executeCommonPreConditionForSelect(flexGrid, objName, singleOnly) {
 var objectName = 'row';
 if (objName) {
 objectName = objName;
 }
 var ids = $('.trSelected', flexGrid);
 if (ids.length == 0) {
 showError("Please select " + objectName + " to perform this operation");
 return false;
 }
 if (singleOnly && (ids.length > 1)) {
 showError("Please select only one " + objectName + " to perform this operation");
 return false;
 }

 return true;
 }*/

// function to check if one or more grid row is selected or not
function executeCommonPreConditionForSelectKendo(gridModel, objName, singleOnly) {
    var objectName = 'row';
    if (objName) {
        objectName = objName;
    }
    var row = gridModel.select();

    if (row.size() == 0) {
        showError("Please select " + objectName + " to perform this operation");
        return false;
    }
    if (singleOnly && (row.size() > 1)) {
        showError("Please select only one " + objectName + " to perform this operation");
        return false;
    }
    return true;
}

/* function returns objectId from grid row.
 In case of multi select return '_' separated ids
 */
function getSelectedIdFromGridKendo(gridModel) {
    var objectId = '';
    var row = gridModel.select();
    var data;
    if (row.size() == 1) {
        data = gridModel.dataItem(row);
        objectId = data.id;
    } else if (row.size() > 1) {
        row.each(function (e) {
            data = gridModel.dataItem($(this));
            objectId += data.id + '_';
        });
    }
    return objectId;
}

/* function returns object from grid row.
 */
function getSelectedObjectFromGridKendo(gridModel) {
    var obj = {};
    var row = gridModel.select();
    if (row.size() == 1) {
        var data = gridModel.dataItem(row);
        if (data.fields) {  // for declared datasource
            $.each(data.fields, function (pName, pVal) {
                obj[pName] = data.get(pName);
            });
        } else {    // for non-declared datasource
            obj = data;
        }
    }
    return obj;
}


/* function returns specific 'cell value' of a row
 Used to check client side validation.
 */
function getSelectedValueFromGridKendo(gridModel, propertyName) {
    var row = gridModel.select();
    var data = gridModel.dataItem(row);
    var propertyValue = data.get(propertyName);
    return propertyValue;
}

/*Checks if grid data contains any error*/
function checkIsErrorGridKendo(data) {
    if (data.isError) {
        showError(data.message);
    }
}

/*Trim long text to display in kendo Grid cell*/
function trimTextForKendo(txt, len) {
    if (txt == null || txt == undefined) return '';
    if (txt.length > len) {
        txt = txt.substring(0, (len)) + '...';
    }
    return txt;
}

/*Serialise form data & convert to json object
 * used in kendo upload control to provide additional form data
 */
function serializeFormAsObject(frm) {
    var data = {};
    $(frm).serializeArray().map(function (x) {
        data[x.name] = x.value;
    });
    return data;
}


// todo: Azam - enabling kendo grid to filter only on a single column
// extensive test is required. Current implementation does not support
// multiple condition on a single column (e.g., field contains 'a' AND field ENDS WITH 'rica' is not supported)
jQuery.fn.extend({
    enableSingleFiltering: function () {
        return this.each(function () {
            this.currentFilter = false;
            var _this = this;

            _this.originalFilter = this.dataSource.filter;
            this.dataSource.filter = function () {
                if (arguments.length > 0 && arguments[0] === null) {
                    console.log("Resetting filter");
                    _this.currentFilter = false;
                } else if (arguments.length > 0) {
                    var filterOptions = arguments[0];
                    if (filterOptions.filters && filterOptions.filters.length > 0) {
                        var filters = filterOptions.filters;
                        var i;
                        for (i = 0; i < filters.length; i++) {
                            var filter = filters[i];
                            if (_this.currentFilter === false
                                || _this.currentFilter.field !== filter.field) {
                                _this.currentFilter = filter;
                                break;
                            }
                            _this.currentFilter.value = filter.value;
                            _this.currentFilter.operator = filter.operator;
                        }
                    } else {
                        _this.currentFilter = false;
                    }
                }

                if (_this.currentFilter !== false && arguments[0]) {
                    var _args = arguments;
                    _args[0].filters = [_this.currentFilter];
                    console.log(_args);
                    return _this.originalFilter.apply(this, _args);
                } else {
                    return _this.originalFilter.apply(this, arguments);
                }
            };
        });
    }
});

Date.prototype.toString = function () {
    return this.getFullYear() + "-" + paddZero((this.getMonth() + 1)) + "-" + paddZero(this.getDate()) + " " + paddZero(this.getHours()) + ":" + paddZero(this.getMinutes()) + ":" + paddZero(this.getSeconds());
}

function paddZero(val, len) {
    if (new String(val).length == 2) {
        return val;
    }
    return '0'.concat(val);
}

// return menuId against pluginId
function getMenuIdByPluginId(pluginId) {
    var menuId;
    switch (pluginId) {
        case 1:
            menuId = MENU_ID_APPLICATION;
            break;
        case 2:
            menuId = MENU_ID_ACCOUNTING;
            break;
        case 3:
            menuId = MENU_ID_BUDGET;
            break;
        case 4:
            menuId = MENU_ID_INVENTORY;
            break;
        case 5:
            menuId = MENU_ID_PROCUREMENT;
            break;
        case 6:
            menuId = MENU_ID_QS;
            break;
        case 7:
            menuId = MENU_ID_FIXED_ASSET;
            break;
        case 9:
            menuId = MENU_ID_EXCHANGE_HOUSE;
            break;
        case 10:
            menuId = MENU_ID_PROJECT_TRACK;
            break;
        case 11:
            menuId = MENU_ID_ARMS;
            break;
        case 12:
            menuId = MENU_ID_SARB;
            break;
        case 13:
            menuId = MENU_ID_DOCUMENT;
            break;
        case 14:
            menuId = MENU_ID_DATA_PIPE_LINE;
            break;
        case 15:
            menuId = MENU_ID_E_LEARNING;
            break;
        default:
            menuId = MENU_ID_APPLICATION;
    }
    return menuId;
}

var currentGridURL;
function populateGridKendo(gridModel, url) {
    if (typeof url != "undefined") {
        currentGridURL = url;
        gridModel.dataSource.transport.options.read.url = url;
    }
    if (gridModel.dataSource.options.serverPaging == true) {
        gridModel.dataSource.filter([]);
    } else {
        // prevent multiple server I/O
        if (gridModel.dataSource.data().length > 0) {
            gridModel.dataSource.filter([]);
            gridModel.dataSource.read();
        } else {
            gridModel.dataSource.read();
        }
    }
}

/* function selects all rows from grid (if selectable:'multiple')
 */
function selectAllFromGridKendo(gridModel) {
    gridModel.items().each(function () {
        gridModel.select($(this));
    });
}

/* function de-selects all rows from grid
 */
function deSelectAllFromGridKendo(gridModel) {
    gridModel.items().each(function () {
        $(this).removeClass("k-state-selected");
    });
}

/**
 * used in kendo grid amount fields
 */
function roundFloat(value, precision) {
    if (!value || value == 'undefined') value = 0;
    return parseFloat(value).toFixed(precision);
}

function setAlignCenter() {
    return "text-align:center";
}
function setAlignRight() {
    return "text-align:right";
}

function formatAmount(amount) {
    return "৳ " + formatNumberTwo(amount);
}

function formatNumberTwo(amount) {
    return kendo.toString(amount, "##,###.00");
}
function formatNumberFour(amount) {
    return kendo.toString(amount, "##,###.0000");
}

/** applicable only for filterable fields those have long value(e.g- id) to show
 * effect - restrict decimal inputs
 */
function formatFilterablePositiveInteger(args) {
    var element = args instanceof jQuery ? args : args.element;
    element.kendoNumericTextBox({
        format: "#####",
        decimals: 0,
        spinners: false
    });

}
/** applicable only for filterable fields those have double value with four decimal(e.g- 2.4521 TON Cement) to show
 * effect - allow four decimal inputs
 */
function formatFilterableFourDecimalInteger(args) {
    var element = args instanceof jQuery ? args : args.element;
    element.kendoNumericTextBox({
        format: "#####.0000",
        decimals: 4,
        spinners: false
    });
}
/* Used to format date in filterable */
function formatFilterableDate(args) {
    var element = args instanceof jQuery ? args : args.element;
    element.kendoDatePicker({
        format: "dd/MM/yyyy"
    });
}

/* Only used in project track plugin to format backlog idea */
function formatIdea(actor, purpose, benefit, calculatedHour) {
    var idea = '<b>As a &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; : </b>' + actor + '</br>' +
        '<b>I want to : </b>' + purpose + '</br>' +
        '<b>So that &nbsp;&nbsp; : </b>' + benefit + '</br>' +
        '<b>Estimated Hour : </b>' + calculatedHour + ' hrs';
    return idea;
}

/**
 *  decode html
 * @param value
 * @returns {*|jQuery}
 */
function htmlDecode(value) {
    return $('<div/>').html(value).text();
}

function funcSendMailToReportErrorForTag(result) {
    var message = result.message;
    var classSignature = result.classSignature;
    var url = "/appMail/sendMailForError?transactionCode=" + 'SendErrorMailActionService' + "&message=" + message + "&classSignature=" + classSignature + "&txtErrorComments=";
    $.ajax({
        url: url,
        success: executePostConditionForSendErrorMail,
        complete: function (XMLHttpRequest, textStatus) {
            showLoadingSpinner(false);
        },
        dataType: 'json',
        type: 'post'
    });
    return false;
}

function executePostConditionForSendErrorMail(data) {
    if (data.isError) {
        showError(data.message);
    } else {
        showSuccess(data.message);
    }
}

function adjustGridContentHeight(grid) {
    setTimeout(function () {
        var contentArea = grid.find(".k-grid-content");
        var otherElementHeight = 0;

        var otherElements = grid.children().not(".k-grid-content");

        otherElements.each(function () {
            otherElementHeight += $(this).outerHeight(true);
        });
        var finalHeight = grid.height() - otherElementHeight;
        contentArea.height(finalHeight);
    }, 200);
}

function expandCreatePanel(div, grid, func) {
    $(div).parents('.panel').find('.panel-body').slideToggle(function () {
        if (grid != null) {
            setGridHeight(grid);
            if (func) func();
        }
    });
    $(div).parents('.panel').find('.panel-footer').slideToggle();

    if ($(div).find('i').hasClass('glyphicon-chevron-up')) {
        $(div).find('i').removeClass('glyphicon-chevron-up').addClass('glyphicon-chevron-down');
    } else {
        $(div).find('i').removeClass('glyphicon-chevron-down').addClass('glyphicon-chevron-up');
    }
}

function showCreatePanel(div, grid, func) {
    $(div).parents('.panel').find('.panel-body').slideDown(function () {
        if (grid != null) {
            setGridHeight(grid);
            if (func) func();
        }
    });
    $(div).parents('.panel').find('.panel-footer').slideDown();
    $(div).find('i').removeClass('glyphicon-chevron-down').addClass('glyphicon-chevron-up');
}

function setGridHeight(grid) {
    grid.height(getGridHeightKendo());
    adjustGridContentHeight(grid);
}