package com.example.pharmacymap.data.remote

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "body", strict = false)
data class PharmacyBody(

    @field:Element(name = "items", required = false)
    var items: PharmacyItems? = null
)
