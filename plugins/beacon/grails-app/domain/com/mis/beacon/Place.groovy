package com.mis.beacon

class Place {
    String name
    String latitude
    String longitude
    String geoFrenchRadius

    Date createdOn
    long createdBy
    long updatedBy
    Date updatedOn

    static constraints = {
        name(blank: false,nullable: false,unique: true)
        latitude(blank: false,nullable: false)
        longitude(blank: false,nullable: false)
        createdOn(blank: false,nullable: false)
        createdBy(blank: false,nullable: false)
        geoFrenchRadius(blank:true,nullable: true)
        updatedBy(blank:true,nullable: true)
        updatedOn(blank:true,nullable: true)
    }
}
