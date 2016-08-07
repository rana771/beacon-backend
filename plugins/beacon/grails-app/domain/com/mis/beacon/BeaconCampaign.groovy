package com.mis.beacon

class BeaconCampaign {
    Beacon      beacon
    Campaign    campaign

    static constraints = {
        beacon(nullable: false)
        campaign(nullable: false)
    }
}
