/**
 * DHTML date validation script for dd/mm/yyyy. Courtesy of SmartWebby.com (http://www.smartwebby.com/dhtml/)
 */
// Declaring valid date character, minimum year and maximum year
var dtCh = "/";
var minYear = 1900;
var maxYear = 2100;

function isInteger(s) {
    var i;
    for (i = 0; i < s.length; i++) {
        // Check that current character is number.
        var c = s.charAt(i);
        if (((c < "0") || (c > "9"))) return false;
    }
    // All characters are numbers.
    return true;
}

function stripCharsInBag(s, bag) {
    var i;
    var returnString = "";
    // Search through string's characters one by one.
    // If character is not in bag, append to returnString.
    for (i = 0; i < s.length; i++) {
        var c = s.charAt(i);
        if (bag.indexOf(c) == -1) returnString += c;
    }
    return returnString;
}

function daysInFebruary(year) {
    // February has 29 days in any year evenly divisible by four,
    // EXCEPT for centurial years which are not also divisible by 400.
    return (((year % 4 == 0) && ( (!(year % 100 == 0)) || (year % 400 == 0))) ? 29 : 28 );
}
function DaysArray(n) {
    for (var i = 1; i <= n; i++) {
        this[i] = 31
        if (i == 4 || i == 6 || i == 9 || i == 11) {
            this[i] = 30
        }
        if (i == 2) {
            this[i] = 29
        }
    }
    return this
}

function getDate(control, cName, showInErrorDiv) {
    var dtStr = control.val();
    var daysInMonth = DaysArray(12)
    var pos1 = dtStr.indexOf(dtCh)
    var pos2 = dtStr.indexOf(dtCh, pos1 + 1)
    var strDay = dtStr.substring(0, pos1)
    var strMonth = dtStr.substring(pos1 + 1, pos2)
    var strYear = dtStr.substring(pos2 + 1)
    strYr = strYear
    if (strDay.charAt(0) == "0" && strDay.length > 1) strDay = strDay.substring(1)
    if (strMonth.charAt(0) == "0" && strMonth.length > 1) strMonth = strMonth.substring(1)
    for (var i = 1; i <= 3; i++) {
        if (strYr.charAt(0) == "0" && strYr.length > 1) strYr = strYr.substring(1)
    }
    month = parseInt(strMonth)
    day = parseInt(strDay)
    year = parseInt(strYr)
    if (pos1 == -1 || pos2 == -1) {
        if (showInErrorDiv == true) {
            try {
                showError("The date format in " + cName + " should be : dd/mm/yyyy");
            } catch (e) {
            }
        } else {
            showError("The date format in " + cName + " should be : dd/mm/yyyy");
        }
        return false
    }
    if (strMonth.length < 1 || month < 1 || month > 12) {
        if (showInErrorDiv == true) {
            try {
                showError("Please enter a valid month in " + cName);
            } catch (e) {
            }
        } else {
            showError("Please enter a valid month in " + cName);
        }
        return false
    }
    if (strDay.length < 1 || day < 1 || day > 31 || (month == 2 && day > daysInFebruary(year)) || day > daysInMonth[month]) {
        if (showInErrorDiv == true) {
            try {
                showError("Please enter a valid day in " + cName);
            } catch (e) {
            }
        } else {
            showError("Please enter a valid day in " + cName);
        }
        return false
    }
    if (strYear.length != 4 || year == 0 || year < minYear || year > maxYear) {
        if (showInErrorDiv == true) {
            try {
                showError("Please enter a valid 4 digit year between " + minYear + " and " + maxYear + " in " + cName);
            } catch (e) {
            }
        } else {
            showError("Please enter a valid 4 digit year between " + minYear + " and " + maxYear + " in " + cName);
        }
        return false
    }
    if (dtStr.indexOf(dtCh, pos2 + 1) != -1 || isInteger(stripCharsInBag(dtStr, dtCh)) == false) {
        if (showInErrorDiv == true) {
            try {
                showError("Please enter a valid date in " + cName);
            } catch (e) {
            }
        } else {
            showError("Please enter a valid date in " + cName);
        }
        return false
    }
    return new Date(year, month - 1, day);
    //return true
}

function checkDates(control_start, control_end) {
    if (($.trim(control_start.val()).length != 10) || ($.trim(control_end.val()).length != 10)) {
        showError("Please enter Start-Date and End-Date");
        return false;
    }

    var retDateFrom = getDate(control_start, "Start-Date");
    var retDateTo = getDate(control_end, "End-Date");
    if (retDateFrom == false || retDateTo == false || retDateFrom == undefined || retDateTo == undefined) {
        return false;
    }
    if (retDateFrom > new Date()) {
        showError('Start-Date can not be future date');
        return false;
    }

    if (retDateFrom && retDateTo && retDateFrom > retDateTo) {
        showError('Start-Date can not be greater than the End-Date');
        return false;
    }
    var ONE_DAY = 1000 * 60 * 60 * 24;
    var dateDiff = Math.round((retDateTo.getTime() - retDateFrom.getTime()) / ONE_DAY);
    if (dateDiff > 365) {
        showError('Date difference can not be more than 365 days');
        return false;
    }

    return true;
}

function checkCustomDate(control, cname) {
    if ($.trim(control.val()).length != 10) {
        showError("Please enter " + cname + " Date");
        return false;
    }

    var retDateFrom = getDate(control, cname + " Date");
    if (retDateFrom == false || retDateFrom == undefined) {
        return false;
    }
    if (retDateFrom > new Date()) {
        showError(cname + ' Date can not be future date');
        return false;
    }
    return true;
}

function customValidateDate(control_start, start_name, control_end, end_name) {
    if (($.trim(control_start.val()).length != 10) || ($.trim(control_end.val()).length != 10)) {
        showError("Please enter "+start_name+" and "+end_name);
        return false;
    }

    var retDateFrom = getDate(control_start, start_name);
    var retDateTo = getDate(control_end, end_name);
    if (retDateFrom == false || retDateTo == false || retDateFrom == undefined || retDateTo == undefined) {
        return false;
    }

    if (retDateFrom > retDateTo) {
        showError(start_name + ' can not be greater than the ' + end_name);
        return false;
    }
    return true;
}


// get date from string format 'dd/mm/yyyy'
function getDateObjectFromString(strDate) {
    var pos1 = strDate.indexOf(dtCh);
    var pos2 = strDate.indexOf(dtCh, pos1 + 1);
    var strDay = strDate.substring(0, pos1);
    var strMonth = strDate.substring(pos1 + 1, pos2);
    var strYear = strDate.substring(pos2 + 1) ;
    if (strDay.charAt(0) == "0" && strDay.length > 1) strDay = strDay.substring(1) ;
    if (strMonth.charAt(0) == "0" && strMonth.length > 1) strMonth = strMonth.substring(1) ;
    for (var i = 1; i <= 3; i++) {
        if (strYear.charAt(0) == "0" && strYear.length > 1) strYear = strYear.substring(1)   ;
    }
    return new Date(parseInt(strYear), parseInt(strMonth) - 1, parseInt(strDay));
}