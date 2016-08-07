package com.mis.beacon

class CustomerMessageLog {
    Campaign    campaign
    Date        dateVisited
    String      customerId

    static constraints = {
        campaign(nullable: false)
        dateVisited(nullable: false)
        customerId(nullable: true,blank: true)
    }
}
