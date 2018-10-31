package com.esteldrive.esteldrive.ui.main

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.features.ReturnMode
import com.esteldrive.esteldrive.R
import com.esteldrive.esteldrive.ui.main.main_page_download_photo.DownloadPhotoMP
import com.esteldrive.esteldrive.ui.regestration.Regestration
import com.facebook.login.LoginManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.util.regex.Pattern

class MainPage : Fragment() {

    private var mAuth: FirebaseAuth? = null

    var mDatabase: DatabaseReference? = null
    var storage: FirebaseStorage? = null

    private val TAG: String = "GMAILLOG"

    var userPhoto: ImageView? = null
    var imgFile: File? = null
    var nameField: TextView? = null
    var user: FirebaseUser? = null

    var statusTV: TextView? = null

    var adminButton: Button? = null
    var firstPopup: PopupMenu? = null

    var photoBtmp: MutableList<Bitmap?>? = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var policy: StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy)

        mDatabase = FirebaseDatabase.getInstance().getReference()
        mDatabase!!.keepSynced(true)
        storage = FirebaseStorage.getInstance()

        var view: View = inflater!!.inflate(R.layout.activity_main_page, container, false)
        userPhoto = view.findViewById(R.id.profile_image)
        nameField = view.findViewById(R.id.nameField)

        statusTV = view.findViewById(R.id.statusTV)

        userPhoto?.setOnClickListener {
            var imPicker: ImagePicker = ImagePicker.create(this).returnMode(ReturnMode.ALL).limit(1).showCamera(false).single()
            imPicker.start()
        }

        mAuth = FirebaseAuth.getInstance();
        user = mAuth!!.currentUser

        var logoutButton: Button = view.findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener({
            signOut()
        })

        adminButton = view.findViewById(R.id.adminButton)
        firstPopup = PopupMenu(activity, adminButton)
        firstPopup!!.getMenuInflater().inflate(R.menu.popup_menu, firstPopup!!.getMenu())

        adminButton!!.setOnClickListener({
            firstPopup!!.setOnMenuItemClickListener({ item: MenuItem? ->
                when (item!!.itemId) {
                    R.id.suItem -> {
                        checkStatus({ p ->
                            if (p) {
                                Toast.makeText(activity, item!!.title, Toast.LENGTH_SHORT).show()
                                chooseCountry(adminButton!!)
                            } else {
                                Toast.makeText(activity, "Rejected", Toast.LENGTH_SHORT).show()
                            }

                        }, getResources().getResourceEntryName(item.itemId).substring(0, 2).toUpperCase())
                    }
                    R.id.ruItem -> {
                        checkStatus({ p ->
                            if (p) {
                                Toast.makeText(activity, item!!.title, Toast.LENGTH_SHORT).show()
                                delOrAdd(adminButton!!, getResources().getResourceEntryName(item.itemId).substring(0, 2).toUpperCase())
                            } else {
                                Toast.makeText(activity, "Rejected", Toast.LENGTH_SHORT).show()
                            }

                        }, "S" + getResources().getResourceEntryName(item.itemId).substring(0, 2).toUpperCase())
                    }
                    R.id.uaItem -> {
                        checkStatus({ p ->
                            if (p) {
                                Toast.makeText(activity, item!!.title, Toast.LENGTH_SHORT).show()
                                delOrAdd(adminButton!!, getResources().getResourceEntryName(item.itemId).substring(0, 2).toUpperCase())
                            } else {
                                Toast.makeText(activity, "Rejected", Toast.LENGTH_SHORT).show()
                            }

                        }, "S" + getResources().getResourceEntryName(item.itemId).substring(0, 2).toUpperCase())
                    }
                    R.id.bgItem -> {
                        checkStatus({ p ->
                            if (p) {
                                Toast.makeText(activity, item!!.title, Toast.LENGTH_SHORT).show()
                                delOrAdd(adminButton!!, getResources().getResourceEntryName(item.itemId).substring(0, 2).toUpperCase())
                            } else {
                                Toast.makeText(activity, "Rejected", Toast.LENGTH_SHORT).show()
                            }

                        }, "S" + getResources().getResourceEntryName(item.itemId).substring(0, 2).toUpperCase())
                    }
                    R.id.roItem -> {
                        checkStatus({ p ->
                            if (p) {
                                Toast.makeText(activity, item!!.title, Toast.LENGTH_SHORT).show()
                                delOrAdd(adminButton!!, getResources().getResourceEntryName(item.itemId).substring(0, 2).toUpperCase())
                            } else {
                                Toast.makeText(activity, "Rejected", Toast.LENGTH_SHORT).show()
                            }

                        }, "S" + getResources().getResourceEntryName(item.itemId).substring(0, 2).toUpperCase())
                    }
                    R.id.mdItem -> {
                        checkStatus({ p ->
                            if (p) {
                                Toast.makeText(activity, item!!.title, Toast.LENGTH_SHORT).show()
                                delOrAdd(adminButton!!, getResources().getResourceEntryName(item.itemId).substring(0, 2).toUpperCase())
                            } else {
                                Toast.makeText(activity, "Rejected", Toast.LENGTH_SHORT).show()
                            }

                        }, "S" + getResources().getResourceEntryName(item.itemId).substring(0, 2).toUpperCase())
                    }
                }
                true
            })
            firstPopup!!.show()
        })

        return view
    }

    private fun chooseCountry(btn: Button) {
        var popup = PopupMenu(activity, btn)
        popup.getMenuInflater().inflate(R.menu.popup_menu2, popup.getMenu())
        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener() { item: MenuItem? ->
            when (item!!.itemId) {
                R.id.suaItem -> {
                    Toast.makeText(activity, item.title, Toast.LENGTH_SHORT).show()
                    delOrAdd(btn, getResources().getResourceEntryName(item.itemId).substring(0, 3).toUpperCase())
                }
                R.id.sruItem -> {
                    Toast.makeText(activity, item.title, Toast.LENGTH_SHORT).show()
                    delOrAdd(btn, getResources().getResourceEntryName(item.itemId).substring(0, 3).toUpperCase())
                }
                R.id.sbgItem -> {
                    Toast.makeText(activity, item.title, Toast.LENGTH_SHORT).show()
                    delOrAdd(btn, getResources().getResourceEntryName(item.itemId).substring(0, 3).toUpperCase())
                }
                R.id.sroItem -> {
                    Toast.makeText(activity, item.title, Toast.LENGTH_SHORT).show()
                    delOrAdd(btn, getResources().getResourceEntryName(item.itemId).substring(0, 3).toUpperCase())
                }
                R.id.smdItem -> {
                    Toast.makeText(activity, item.title, Toast.LENGTH_SHORT).show()
                    delOrAdd(btn, getResources().getResourceEntryName(item.itemId).substring(0, 3).toUpperCase())
                }
            }
            true
        })
        popup.show()
    }

    private fun delOrAdd(btn: Button, strCountry: String) {
        var popup = PopupMenu(activity, btn)
        popup.getMenuInflater().inflate(R.menu.popup_menu3, popup.getMenu())
        popup.setOnMenuItemClickListener({ item: MenuItem? ->
            when (item!!.itemId) {
                R.id.delUserItem -> {
                    Toast.makeText(activity, item.title, Toast.LENGTH_SHORT).show()
                    makeListOfUsers(strCountry, "del")
                }
                R.id.addUserItem -> {
                    Toast.makeText(activity, item.title, Toast.LENGTH_SHORT).show()
                    makeListOfUsers(strCountry, "add")
                }
            }
            true
        })
        popup.show()
    }

    private fun makeListOfUsers(strCountry: String, strDelOrAdd: String) {
        var intent = Intent(activity, UsersList::class.java)
        intent.putExtra("loacationDB", strCountry)
        intent.putExtra("delOrAdd", strDelOrAdd)
        startActivity(intent)
    }

    private fun pastNoImagePhoto() {
        userPhoto!!.setImageResource(R.drawable.noimage)
    }

    private fun pastUserPhoto() {

        if (photoBtmp!!.size > 0 && photoBtmp!!.get(0) != null) {
            userPhoto!!.setImageBitmap(photoBtmp!!.get(0))
            return
        }

        var userPhotoURL: String = user!!.photoUrl.toString()

        if (userPhotoURL.contains("http") && isOnline()) {
            DownloadPhotoMP(userPhoto!!, userPhotoURL, photoBtmp, activity!!).execute()
        } else {
            imgFile = File(userPhotoURL)
            if (imgFile!!.exists()) {
                photoBtmp!!.clear()
                photoBtmp!!.add(BitmapFactory.decodeFile(userPhotoURL))
                userPhoto!!.setImageBitmap(photoBtmp!!.get(0));
            } else {
                pastNoImagePhoto()
            }
        }
    }

    private fun setStatus() {
        var status: MutableList<String?> = mutableListOf()
        if (mDatabase != null && user != null)
            mDatabase!!.child("Users").child(user!!.uid).child("status").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.children.forEach { d ->
                        run {
                            var u: String? = d.key.toString()
                            if (d.getValue(Boolean::class.java)!!) {
                                status!!.add(context!!.getResources().getString(
                                        getResources().getIdentifier(u, "string", context!!.getPackageName())))

                                var p = Pattern.compile("(SU|S.{2})")
                                var m = p.matcher(u)
                                if (m.matches()) {
                                    adminButton!!.setVisibility(View.VISIBLE)
                                }

                            }
                        }
                    }
                    var str = ""
                    statusTV!!.text = ""
                    for (i in 0..status.size - 1) {
                        str = str + " " + status.get(i)
                    }
                    statusTV!!.text = str
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(TAG, "onCancelled", databaseError.toException())
                }
            })
    }

    private fun checkStatus(callback: (Boolean) -> Unit, str: String) {
        var u = false
        if (mDatabase != null && user != null)
            mDatabase!!.child("Users").child(user!!.uid).child("status").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.children.forEach { d ->
                        run {
                            if (d.key.toString().equals(str) && d.getValue(Boolean::class.java)!!)
                                u = true
                        }
                    }
                    callback(u)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(TAG, "onCancelled", databaseError.toException())
                }
            })

    }

    private fun pastUserInfo() {

        adminButton!!.setVisibility(View.GONE)
        if (mDatabase != null && user != null) {
            pastUserPhoto()
            nameField!!.setText(user!!.displayName)
        } else {
            Handler().postDelayed({
                pastUserInfo()
            }, 500)
        }
    }

    private fun signOut() {
        mAuth!!.signOut()
        LoginManager.getInstance().logOut()

        val intent = Intent(activity, Regestration::class.java)
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()

        setStatus()
        handlePopupItems()
        pastUserInfo()

    }

    private fun handlePopupItems() {
        checkStatus({ p ->
            if (!p)
                firstPopup!!.getMenu().findItem(R.id.suItem).setVisible(false)
            else
                firstPopup!!.getMenu().findItem(R.id.suItem).setVisible(true)
        }, getResources().getResourceEntryName(R.id.suItem).substring(0, 2).toUpperCase())
        checkStatus({ p ->
            if (!p)
                firstPopup!!.getMenu().findItem(R.id.ruItem).setVisible(false)
            else
                firstPopup!!.getMenu().findItem(R.id.ruItem).setVisible(true)
        }, "S" + getResources().getResourceEntryName(R.id.ruItem).substring(0, 2).toUpperCase())
        checkStatus({ p ->
            if (!p)
                firstPopup!!.getMenu().findItem(R.id.uaItem).setVisible(false)
            else
                firstPopup!!.getMenu().findItem(R.id.uaItem).setVisible(true)
        }, "S" + getResources().getResourceEntryName(R.id.uaItem).substring(0, 2).toUpperCase())
        checkStatus({ p ->
            if (!p)
                firstPopup!!.getMenu().findItem(R.id.bgItem).setVisible(false)
            else
                firstPopup!!.getMenu().findItem(R.id.bgItem).setVisible(true)
        }, "S" + getResources().getResourceEntryName(R.id.bgItem).substring(0, 2).toUpperCase())
        checkStatus({ p ->
            if (!p)
                firstPopup!!.getMenu().findItem(R.id.roItem).setVisible(false)
            else
                firstPopup!!.getMenu().findItem(R.id.roItem).setVisible(true)
        }, "S" + getResources().getResourceEntryName(R.id.roItem).substring(0, 2).toUpperCase())
        checkStatus({ p ->
            if (!p)
                firstPopup!!.getMenu().findItem(R.id.mdItem).setVisible(false)
            else
                firstPopup!!.getMenu().findItem(R.id.mdItem).setVisible(true)
        }, "S" + getResources().getResourceEntryName(R.id.mdItem).substring(0, 2).toUpperCase())

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val images: MutableList<com.esafirm.imagepicker.model.Image>? = ImagePicker.getImages(data);
        if (images != null && !images.isEmpty()) {
            imgFile = File(images.get(0).getPath())
        }

        user = mAuth!!.currentUser
        var strURL = "usersPhotos/" + user!!.uid
        var uri: Uri = android.net.Uri.parse(java.net.URI(imgFile!!.toURI().toString()).toString())
        storage!!.getReference(strURL).putFile(uri).addOnSuccessListener { d ->
            var uri2 = d.downloadUrl
            if (!user!!.photoUrl.toString().equals(imgFile!!.absolutePath.toString())) {
                var profileUpdates: UserProfileChangeRequest? = null
                if (imgFile != null && imgFile!!.exists()) {
                    profileUpdates = UserProfileChangeRequest.Builder()
                            .setPhotoUri(uri2/*Uri.parse(imgFile!!.absolutePath.toString())*/)
                            .build()
                } else {
                    profileUpdates = UserProfileChangeRequest.Builder()
                            .build()
                }


                user!!.updateProfile(profileUpdates)
                        .addOnCompleteListener(object : OnCompleteListener<Void> {
                            override fun onComplete(task: Task<Void>) {
                                if (task.isSuccessful()) {
                                    photoBtmp!!.clear()
                                    pastUserPhoto()
                                    Log.d(TAG, "User profile updated. " + user!!.photoUrl.toString())
                                    mDatabase!!.child("Users").child(user!!.uid).child("photo").setValue(user!!.photoUrl.toString())
                                }
                            }
                        })
            }


        }
    }

    private fun isOnline(): Boolean {
        var cm: ConnectivityManager = activity!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (cm.getActiveNetworkInfo() == null)
            return false
        var netInfo: NetworkInfo = cm.getActiveNetworkInfo()
        return netInfo != null && netInfo.isConnectedOrConnecting()
    }

    companion object {
        fun newInstance() = MainPage()
    }
}

