package com.mis.beacon

class ContentType {
    String name
    String description

    static constraints = {
        name(blank: false,nullable: false,unique: true)
        description(blank: true,nullable: true)
    }
}
