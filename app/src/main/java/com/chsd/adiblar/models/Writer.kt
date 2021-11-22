package com.chsd.adiblar.models

import java.io.Serializable

class Writer:Serializable {
    var image: String? = null
    var name: String? = null
    var birth_date: String? = null
    var death_date: String? = null
    var information: String? = null

    constructor(
        image: String?,
        name: String?,
        birth_date: String?,
        death_date: String?,
        information: String?
    ) {
        this.image = image
        this.name = name
        this.birth_date = birth_date
        this.death_date = death_date
        this.information = information
    }

    constructor()
}