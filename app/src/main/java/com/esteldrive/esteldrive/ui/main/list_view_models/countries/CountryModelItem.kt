package com.esteldrive.esteldrive.ui.main.list_view_models.countries

import android.graphics.Bitmap

open class CountryModelItem constructor(title: String?, filePhoto: Bitmap?, isoCode: String, fullName: String) {

    var title = title
    var filePhoto = filePhoto
    var isoCode = isoCode
    var fullName = fullName

}