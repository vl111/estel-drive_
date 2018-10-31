package com.esteldrive.esteldrive.ui.main.list_view_models.links

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.esteldrive.esteldrive.R

open class LinksArrayAdapter(context: Context, resource: Int, list: MutableList<LinksModelItem?>) :
        ArrayAdapter<LinksModelItem?>(context, resource, list) {

    var resource: Int
    var list: MutableList<LinksModelItem?>
    var vi: LayoutInflater

    init {
        this.resource = resource
        this.list = list
        this.vi = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var retView: View = LayoutInflater.from(context).inflate(resource, parent, false)

        var t = retView.findViewById<View>(R.id.item_title_link) as TextView
        t.setText(list.get(position)!!.title)

        return retView
    }

}
