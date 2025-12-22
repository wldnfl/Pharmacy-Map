package com.example.pharmacymap.data.remote

import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Path
import org.simpleframework.xml.Root

@Root(name = "body", strict = false)
data class PharmacyBody(

    @field:Path("items")
    @field:ElementList(entry = "item", inline = true, required = false)
    var itemList: MutableList<PharmacyItem> = mutableListOf()
)