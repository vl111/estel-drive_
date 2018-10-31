package com.esteldrive.esteldrive.ui.main.list_view_models.users

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.esteldrive.esteldrive.R

open class UsersArrayAdapter(context: Context, resource: Int, list: MutableList<ModelUserItem?>) :
        ArrayAdapter<ModelUserItem>(context, resource, list) {

    var resource: Int
    var list: MutableList<ModelUserItem?>
    var vi: LayoutInflater

    init {
        this.resource = resource
        this.list = list
        this.vi = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var holder: ViewHolder? = null
        var retView: View = LayoutInflater.from(context).inflate(resource, parent, false)

        holder = ViewHolder()

        holder.image = retView.findViewById(R.id.item_icon)

        var t = retView.findViewById<View>(R.id.item_title) as TextView
        t.setText(list.get(position)!!.title)
        if (list.get(position)!!.icon != null) {
            var i = retView.findViewById<View>(R.id.item_icon) as ImageView
            i.setImageResource(list.get(position)!!.icon!!)
        }
        if (list.get(position)!!.userPick != null && list.get(position)!!.filePhoto == null) {
            var i = retView.findViewById<View>(R.id.user_pick) as ImageView
            DownloadPhotos(i, list.get(position)!!.userPick!!, list.get(position)!!, context).execute()
        } else if (list.get(position)!!.filePhoto != null) {
            var i = retView.findViewById<View>(R.id.user_pick) as ImageView
            i.setImageBitmap(list.get(position)!!.filePhoto)
        }

        return retView
    }

    internal class ViewHolder {
        var image: ImageView? = null
    }

}
