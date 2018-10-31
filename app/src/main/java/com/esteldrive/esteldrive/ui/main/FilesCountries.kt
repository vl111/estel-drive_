package com.esteldrive.esteldrive.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import com.esteldrive.esteldrive.R
import com.esteldrive.esteldrive.ui.files.DatabaseFiles
import com.esteldrive.esteldrive.ui.main.list_view_models.countries.CountryArrayAdapter
import com.esteldrive.esteldrive.ui.main.list_view_models.countries.CountryModelItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.util.regex.Pattern


class FilesCountries : Fragment() {
    private val TAG: String = "FILES"

    private val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL = 1

    private var mAuth: FirebaseAuth? = null
    var user: FirebaseUser? = null

    var mDatabase: DatabaseReference? = null
    var storage: FirebaseStorage? = null

    var listView: ListView? = null
    val countryInfoList: MutableList<CountryModelItem?> = mutableListOf()

    var position: Int = -1

    var statusStr = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater!!.inflate(R.layout.activity_files_countries, container, false)

        mDatabase = FirebaseDatabase.getInstance().getReference()
        mDatabase!!.keepSynced(true)
        storage = FirebaseStorage.getInstance()

        mAuth = FirebaseAuth.getInstance()
        user = mAuth!!.currentUser

        listView = view!!.findViewById<View>(R.id.countriesLV) as ListView

        return view
    }

    private fun getStatus() {
        statusStr = ""
        var countriesPast = ""
        if (mDatabase != null && user != null) {
            mDatabase!!.child("Users").child(user!!.uid).child("status").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    countryInfoList.clear()
                    dataSnapshot.children.forEach { d ->
                        run {
                            var u: String? = d.key.toString()
                            statusStr = statusStr + " " + u
                            var c = u
                            if (u!!.length == 3)
                                c = u.substring(1, 3)

                            var p = Pattern.compile("(.{2})")
                            var m = p.matcher(c)
                            if (m.matches() && !c!!.equals("SU") && !countriesPast.contains(c)) {
                                if (d.getValue(Boolean::class.java)!!) {
                                    countriesPast = countriesPast + " " + u
                                    if (context != null) {
                                        var zz: Bitmap = BitmapFactory.decodeResource(context!!.resources,
                                                resources!!.getIdentifier(c!!.toLowerCase() + "_flag",
                                                        "drawable", context!!.getPackageName()))
                                        var list = getStrings()
                                        var fullName = list.get(1).get(list.get(0).indexOf(c))
                                        countryInfoList.add(CountryModelItem(u, zz, c,fullName))
                                    }

                                }
                            }
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
            listView!!.setOnItemClickListener({ parent, view, _position, id ->
                position = _position
                if (ContextCompat.checkSelfPermission(activity!!,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(activity!!,
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL)
                } else {
                    openFiles()
                }


            })
            val adapter = CountryArrayAdapter(activity!!.applicationContext,
                    R.layout.item_layout_country, countryInfoList)
            listView!!.adapter = adapter
        }
    }

    private fun openFiles() {
        var intent = Intent(activity, DatabaseFiles::class.java)
        intent.putExtra("loacationDB", countryInfoList.get(position)!!.isoCode)
        if (countryInfoList.get(position)!!.title!!.length > 2
                || statusStr.contains("S" + countryInfoList.get(position)!!.title!!))
            intent.putExtra("isAdmin", true)
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        getStatus()
    }

    companion object {
        fun newInstance() = FilesCountries()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    openFiles()

                } else {
                    Toast.makeText(activity, "Permission denied", Toast.LENGTH_LONG).show()
                }
                return
            }

            else -> {
            }
        }
    }

    private fun getStrings():MutableList<MutableList<String>>{
        var list :  MutableList<MutableList<String>> =  mutableListOf(mutableListOf())
        list.add(mutableListOf())
        list.add(mutableListOf())
        list.get(0).add("RU")
        list.get(1).add(getResources().getString(R.string.russia))
        list.get(0).add("BG")
        list.get(1).add(getResources().getString(R.string.bulgaria))
        list.get(0).add("MD")
        list.get(1).add(getResources().getString(R.string.moldova))
        list.get(0).add("RO")
        list.get(1).add(getResources().getString(R.string.romania))
        list.get(0).add("UA")
        list.get(1).add(getResources().getString(R.string.ukraine))

        return list
    }
}