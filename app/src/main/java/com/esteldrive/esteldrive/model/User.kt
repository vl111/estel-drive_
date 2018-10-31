package com.esteldrive.esteldrive.model

class User {
    var email: String? = null
    var objectId: String? = null
    var fullName: String? = null
    var photo: String? = null
    var status: Status? = null

    class Status {
        var RU: Boolean? = null
        var SRU: Boolean? = null
        var SU: Boolean? = null
        var UA: Boolean? = null
        var SUA: Boolean? = null
    }
}