package com.mis.beacon

import com.athena.mis.application.entity.AppUser

class Marchant {
    public static final String DEFAULT_SORT_FIELD = "name"
    String      name
    String      description
//    String      companyName
    String      companyAddress
//    String      suite
//    TimeZone    timeZone
//    Package     aPackage
//    Boolean     isEnableSubAccount
    String      companyPhone
    String      email
    String      apiKey
    AppUser     appUser

    static constraints = {
        name(nullable: false,blank: false)
        description(nullable: true,blank: true)
        companyAddress(nullable: false,blank: false)
        companyPhone(nullable: false,blank: false)
        email(nullable: false,blank: false,unique: true)
        apiKey(nullable: false,blank: false,unique: true)
        appUser(nullable: false)
    }
    @Override
    String toString() {
        return name
    }
}
