<%--
  Created by IntelliJ IDEA.
  User: ripon.rana
  Date: 4/6/2016
  Time: 10:35 AM
--%>

<table>
    <tr><td>
        <g:form method="POST" action="qrVendorReport">
            <button id="vendorReport" name="vendorReport" type="submit" data-role="button"
                    class="k-button k-button-icontext"
                    role="button" tabindex="5"
                    aria-disabled="false"><span class="k-icon k-i-file"></span>QR code Vendor Report
            </button>

        </g:form>
    </td></tr>
    <tr><td>&nbsp;</td></tr>
    <tr><td>
        <g:form method="POST" action="visitorReport">
            <button id="visitorReport" name="visitorReport" type="submit" data-role="button"
                    class="k-button k-button-icontext"
                    role="button" tabindex="5"
                    aria-disabled="false"><span class="k-icon k-i-file"></span>QR code Visitors Report
            </button>
        </g:form>
    </td></tr>

    <tr><td>&nbsp;</td></tr>
    <tr><td>
        <g:form method="POST" action="visitorEngagementReport">
            <button id="campaignReport" name="campaignReport" type="submit" data-role="button"
                    class="k-button k-button-icontext"
                    role="button" tabindex="5"
                    aria-disabled="false"><span class="k-icon k-i-file"></span>Visitor Engagement Campaign Report
            </button>
        </g:form>
    </td></tr>
</table>