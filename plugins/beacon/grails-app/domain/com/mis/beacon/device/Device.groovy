package com.mis.beacon.device

import com.mis.beacon.Content
import com.mis.beacon.Marchant

class Device {
    Marchant        marchant
    String          name;
    DeviceType      deviceType
    String          details;
    Network         network
    Boolean         isScheduleBroadcast;
    Content         content

    Float           latitude;
    Float           longitude;
    String          tags;

    static constraints = {
        marchant(nullable: false)
        name(blank: false,nullable: false,unique: 'marchant')
        deviceType(nullable: false)
        details(blank: true,nullable: true)
        network(nullable: true)
        isScheduleBroadcast(nullable: true)
        content(nullable: false)
        latitude(nullable: true)
        longitude(nullable: true)
        tags(blank: true,nullable: true)
    }
}
