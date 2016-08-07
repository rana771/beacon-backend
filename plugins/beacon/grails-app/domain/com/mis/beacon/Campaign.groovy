package com.mis.beacon

class Campaign {
    String      name
    String      subject
    String      template
    Boolean     isScheduleAlways
    Date        startTime
    Date        endTime

    Integer      bonusRewardPoint



    String      title
    String      message
    String      ticker


    static constraints = {
        name(nullable: false,blank: false)
        subject(nullable: false,blank: false)
        template(nullable: false,blank: false)
        isScheduleAlways(nullable: true)
        startTime(nullable: true)
        endTime(nullable: true)
        bonusRewardPoint(nullable: false)
        title(nullable: false,blank: false)
        message(nullable: false,blank: false)
        ticker(nullable: false,blank: false)
    }
    static mapping = {
        sort id: 'desc'
        template type: 'text'
    }
}
