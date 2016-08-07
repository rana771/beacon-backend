package com.mis.beacon

import javax.sql.DataSource

class CustomerPointDetails {
    CustomerRewardPoint customerRewardPoint
    Date                datePointReceive
    Integer             point
    Campaign            campaign

    static constraints = {
        campaign(nullable: true)
    }
}
