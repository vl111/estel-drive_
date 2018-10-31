package com.esteldrive.esteldrive.ui.main.list_view_models.users

import android.graphics.Bitmap

open class ModelUserItem constructor(userPick: String?, title: String?, icon: Int?, filePhoto: Bitmap?) {

    var userPick = userPick
    var icon = icon
    var title = title
    var filePhoto = filePhoto

}
