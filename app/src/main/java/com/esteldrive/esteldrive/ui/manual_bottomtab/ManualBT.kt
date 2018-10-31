package com.esteldrive.esteldrive.ui.manual_bottomtab

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.esteldrive.esteldrive.R
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class ManualBT : Fragment() {

    var mDatabase: DatabaseReference? = null
    var storage: FirebaseStorage? = null
    var pageAdapter: ImageAdapter? = null
    var view1: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view1 = inflater!!.inflate(R.layout.activity_manual_bt, container, false)
        mDatabase = FirebaseDatabase.getInstance().getReference()
        mDatabase!!.keepSynced(true)
        storage = FirebaseStorage.getInstance()
        return view1
    }

    override fun onStart() {
        super.onStart()

        var saveDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).toString() +
                File.separator + "Estel-drive" + File.separator + "manual"
        var folder = File(saveDir)
        if (!folder.exists())
            folder.mkdirs()

        mDatabase!!.child("ft").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEachIndexed { index, d ->
                    run {
                        val prefs = activity!!.getSharedPreferences("manual", AppCompatActivity.MODE_PRIVATE)

                        var file = File(saveDir + File.separator + "val" + index + ".jpeg")
                        if ((!file.exists() || !prefs.getBoolean(d.getValue(String::class.java), false)) && isOnline()) {

                            DownloadImgAsyncTask({ str ->
                                storage!!.getReferenceFromUrl(str)
                                        .getFile(file).addOnSuccessListener {
                                    file.createNewFile()

                                    val editor: SharedPreferences.Editor = activity!!.getSharedPreferences("manual", AppCompatActivity.MODE_PRIVATE).edit()
                                    editor.putBoolean(str, true)
                                    editor.apply()
                                }
                            }, d.getValue(String::class.java)/*, frList.get(index)!!.getImgView(), file*/).execute()
                        } else {
                        }
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError?) {

            }
        })
    }

    override fun onPause() {
        super.onPause()
        pageAdapter = null

    }

    override fun onResume() {
        super.onResume()
        context?.let {
            val tabl: TabLayout = view1!!.findViewById(R.id.tabL)
            val viewPager: ViewPager = view1!!.findViewById(R.id.pager1)
            pageAdapter = ImageAdapter(it)
            viewPager.adapter = pageAdapter
            tabl.setupWithViewPager(viewPager)
        }

    }

    private fun isOnline(): Boolean {
        var cm: ConnectivityManager = activity!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (cm.getActiveNetworkInfo() == null)
            return false
        var netInfo: NetworkInfo = cm.getActiveNetworkInfo()
        return netInfo != null && netInfo.isConnectedOrConnecting()
    }
}
