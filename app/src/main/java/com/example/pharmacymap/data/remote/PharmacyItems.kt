package com.example.pharmacymap.data.remote

import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "items", strict = false)
data class PharmacyItems(

    @field:ElementList(entry = "item", inline = true, required = false)
    var itemList: MutableList<PharmacyItem> = mutableListOf()
)
