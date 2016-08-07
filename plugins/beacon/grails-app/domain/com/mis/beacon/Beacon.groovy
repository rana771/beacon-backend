package com.mis.beacon

class Beacon {
    Marchant    marchant
    String      name
//    String      description
    String      uuid
    Integer     major
    Integer     minor
    Integer     signalInterval
    Integer     transmissionPower
    Zone        zone

    Double      latitude
    Double      longitude

    static constraints = {
        marchant(nullable: false)
        name(blank: false,nullable: false,unique: 'marchant')
        uuid(nullable: false,blank: false,unique: 'marchant')
        major(nullable: false)
        minor(nullable: false)
        signalInterval(nullable: true)
        transmissionPower(nullable: true)
        zone(nullable: true)
        latitude(nullable: true)
        longitude(nullable: true)
    }

    @Override
    String toString() {
        return name
    }
}
