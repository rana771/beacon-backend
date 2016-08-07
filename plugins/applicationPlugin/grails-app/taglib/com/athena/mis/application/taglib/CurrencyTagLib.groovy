package com.athena.mis.application.taglib

import com.athena.mis.BaseTagLibExecutor
import com.athena.mis.application.actions.taglib.GetForeignCurrencyTaglibActionService
import com.athena.mis.application.actions.taglib.GetLocalCurrencyTagLibActionService

class CurrencyTagLib extends BaseTagLibExecutor {

    static namespace = "app"

    GetLocalCurrencyTagLibActionService getLocalCurrencyTagLibActionService
    GetForeignCurrencyTaglibActionService getForeignCurrencyTaglibActionService
    /**
     * Render currency code of country which belongs to logged in user
     * example: <app:localCurrency property="name"}"></app:localCurrency>
     * example: <app:localCurrency property="symbol"}"></app:localCurrency>
     *
     * @attr property REQUIRED - property name of Currency domain
     */
    def localCurrency = { attrs, body ->
        attrs.body = body
        super.executeTag(getLocalCurrencyTagLibActionService, attrs)
        out << (String) attrs.html
    }

    /**
     * Render currency code of country
     * @attr property REQUIRED - property name of currency domain
     * @attr country_id - country id of country
     */
    def foreignCurrency = { attrs, body ->
        attrs.body = body
        super.executeTag(getForeignCurrencyTaglibActionService, attrs)
        out << (String) attrs.html
    }
}
