package com.example.pharmacymap.data.remote

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "item", strict = false)
data class PharmacyItem(

    @field:Element(name = "dutyName", required = false)
    var dutyName: String = "",

    @field:Element(name = "dutyAddr", required = false)
    var dutyAddr: String = "",

    @field:Element(name = "dutyTel1", required = false)
    var dutyTel1: String = "",

    @field:Element(name = "wgs84Lat", required = false)
    var wgs84Lat: String = "",

    @field:Element(name = "wgs84Lon", required = false)
    var wgs84Lon: String = ""
)
