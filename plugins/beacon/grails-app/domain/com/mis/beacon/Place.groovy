package com.mis.beacon

class Place {
    String name
    String latitude
    String longitude
    String geoFrenchRadius

    static constraints = {
        name(blank: false,nullable: false,unique: true)
        latitude(blank: false,nullable: false)
        longitude(blank: false,nullable: false)
        geoFrenchRadius(blank:true,nullable: true)
    }
}
