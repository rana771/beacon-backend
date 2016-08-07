package com.mis.beacon

class Zone {
    Marchant    marchant
    String      name
    String      color
    String      description

    static constraints = {
        name(blank: false,nullable: false,unique: true)
        color(blank: true,unique: true)
        description(blank: true,nullable: true)
    }

}
