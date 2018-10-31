package com.esteldrive.esteldrive.ui.main.list_view_models.countries

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.esteldrive.esteldrive.R

open class CountryArrayAdapter(context: Context, resource: Int, list: MutableList<CountryModelItem?>) :
        ArrayAdapter<CountryModelItem?>(context, resource, list) {

    var resource: Int
    var list: MutableList<CountryModelItem?>
    var vi: LayoutInflater

    init {
        this.resource = resource
        this.list = list
        this.vi = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var retView: View = LayoutInflater.from(context).inflate(resource, parent, false)

        var t = retView.findViewById<View>(R.id.item_title) as TextView
        t.setText(list.get(position)!!.fullName)
        if (list.get(position)!!.filePhoto != null) {
            var i = retView.findViewById<View>(R.id.country_pick) as ImageView
            i.setImageBitmap(list.get(position)!!.filePhoto)
        }

        return retView
    }

}
