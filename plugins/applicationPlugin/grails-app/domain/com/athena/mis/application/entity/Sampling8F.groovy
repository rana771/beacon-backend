package com.athena.mis.application.entity

class Sampling8F {

    String field1
    String field2
    String field3
    String field4
    String field5
    long field6
    double field7
    float field8

    static constraints = {
    }

    static mapping = {
        version false
        id name: 'field6', generator: 'assigned'
    }
}
