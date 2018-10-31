package com.esteldrive.esteldrive.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.esteldrive.esteldrive.R
import com.esteldrive.esteldrive.ui.main.list_view_models.links.LinksArrayAdapter
import com.esteldrive.esteldrive.ui.main.list_view_models.links.LinksModelItem
import com.google.firebase.database.*

class Links : Fragment() {
    private val TAG: String = "Links"

    var listView: ListView? = null

    var mDatabase: DatabaseReference? = null

    val linksInfoList: MutableList<LinksModelItem?> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater!!.inflate(R.layout.activity_links, container, false)

        mDatabase = FirebaseDatabase.getInstance().getReference()
        mDatabase!!.keepSynced(true)
        listView = view!!.findViewById<View>(R.id.linksLV) as ListView

        return view
    }

    private fun getLinks() {
        if (mDatabase != null) {
            mDatabase!!.child("Links").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    linksInfoList.clear()
                    dataSnapshot.children.forEach { d ->
                        run {
                            var title = d.key.toString()
                            var value = d.getValue(String::class.java)
                            linksInfoList.add(LinksModelItem(title, value))
                        }
                    }
                    updateListView()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(TAG, "onCancelled", databaseError.toException())
                }
            })
        }
    }

    private fun updateListView() {
        if (activity != null) {
            listView!!.setOnItemClickListener({ parent, view, position, id ->
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(linksInfoList.get(position)!!.value))
                startActivity(browserIntent)
            })
            val adapter = LinksArrayAdapter(activity!!.applicationContext,
                    R.layout.item_layout_links, linksInfoList)
            listView!!.adapter = adapter
        }
    }


    companion object {
        fun newInstance() = Links()
    }

    override fun onStart() {
        super.onStart()
        getLinks()
    }
}
