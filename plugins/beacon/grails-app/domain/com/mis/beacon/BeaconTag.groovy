package com.mis.beacon

class BeaconTag {
    Beacon beacon
    String tag

    static constraints = {
        beacon(nullable: false)
        tag(nullable: false,blank: false,unique: 'beacon')
    }
}
