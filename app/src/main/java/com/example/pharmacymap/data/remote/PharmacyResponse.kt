package com.example.pharmacymap.data.remote

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "response", strict = false)
data class PharmacyResponse(

    @field:Element(name = "body")
    var body: PharmacyBody? = null
)