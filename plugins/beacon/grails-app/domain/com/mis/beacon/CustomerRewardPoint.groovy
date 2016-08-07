package com.mis.beacon

class CustomerRewardPoint {
    Long    userId
    Integer rewardPoint//++
    Integer allTotalPoint
    Integer redeemPoint
    Integer lastScanPoint
//    Campaign campaign
    static constraints = {
        allTotalPoint(nullable:true)
        redeemPoint(nullable:true)
        lastScanPoint(nullable: true)
    }
}
