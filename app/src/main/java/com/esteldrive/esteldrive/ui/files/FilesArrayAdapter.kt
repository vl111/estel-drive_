package com.esteldrive.esteldrive.ui.files

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.esteldrive.esteldrive.R

open class FilesArrayAdapter(context: Context, resource: Int, list: MutableList<FilesModelItem?>) :
        ArrayAdapter<FilesModelItem?>(context, resource, list) {

    var resource: Int
    var list: MutableList<FilesModelItem?>
    var vi: LayoutInflater

    init {
        this.resource = resource
        this.list = list
        this.vi = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var retView: View = LayoutInflater.from(context).inflate(resource, parent, false)

        var t = retView.findViewById<View>(R.id.item_file_db_title) as TextView
        t.setText(list.get(position)!!.title)
        var i = retView.findViewById<View>(R.id.file_db_icon) as ImageView
        if (list.get(position)!!.isFolder) {
            i.setImageResource(R.drawable.ic_folder)
        }else{
            i.setImageResource(R.drawable.ic_file)
        }

        return retView
    }

    internal class ViewHolder {
        var image: ImageView? = null
    }

}

