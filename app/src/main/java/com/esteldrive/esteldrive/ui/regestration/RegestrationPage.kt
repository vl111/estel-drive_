package com.esteldrive.esteldrive.ui.regestration

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.esteldrive.esteldrive.KeyBoardHide
import com.esteldrive.esteldrive.R
import com.facebook.CallbackManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.util.Log
import android.widget.ImageView
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.features.ReturnMode
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.File


class RegestrationPage : AppCompatActivity() {

    var mDatabase: DatabaseReference? = null
    var nameET: EditText? = null
    var emailET: EditText? = null
    var passwordET: EditText? = null
    var passwordConfirmET: EditText? = null
    var doneB: Button? = null
    private var mCallbackManager: CallbackManager? = null
    private var mAuth: FirebaseAuth? = null

    var storage: FirebaseStorage? = null

    private val TAG: String = "GMAILLOG"

    var userPhoto: ImageView? = null
    var imgFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_regestration_page)
        val kbh: KeyBoardHide = KeyBoardHide()
        kbh.setupUI(findViewById(R.id.parentPanel), this@RegestrationPage);

        mDatabase = FirebaseDatabase.getInstance().getReference()
        mDatabase!!.keepSynced(true)
        storage = FirebaseStorage.getInstance()

        userPhoto = findViewById(R.id.profile_image)
        userPhoto?.setOnClickListener {
            var imPicker: ImagePicker = ImagePicker.create(this).returnMode(ReturnMode.ALL).limit(1).showCamera(false).single()
            imPicker.start()
        }


        userPhoto!!.setImageResource(R.drawable.noimage);

        nameET = findViewById(R.id.etName)
        emailET = findViewById(R.id.etEmail)
        passwordET = findViewById(R.id.etPassword)
        passwordConfirmET = findViewById(R.id.etConfirmPassword)
        doneB = findViewById(R.id.done)

        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();

        doneB?.setOnClickListener(View.OnClickListener { view1 ->
            createAccount({ p, u ->
                u.updateProfile(p!!)
                        .addOnCompleteListener(object : OnCompleteListener<Void> {
                            override fun onComplete(task: Task<Void>) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User profile updated. " + u.photoUrl.toString())

                                }
                            }
                        })

            }, emailET?.text.toString(), passwordET?.text.toString())
           })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val images: MutableList<com.esafirm.imagepicker.model.Image>? = ImagePicker.getImages(data);
        if (images != null && !images.isEmpty()) {
            imgFile = File(images.get(0).getPath())
            userPhoto!!.setImageBitmap(BitmapFactory.decodeFile(imgFile!!.absolutePath));
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    private fun createAccount(callback: (UserProfileChangeRequest, FirebaseUser) -> Unit, email: String, password: String) {
        if (!validateForm()) {
            return
        }

        mAuth!!.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this@RegestrationPage) { task ->
            run {
                if (!task.isSuccessful) {
                    Toast.makeText(this@RegestrationPage, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@RegestrationPage, "Account created.",
                            Toast.LENGTH_SHORT).show()

                    var user: FirebaseUser? = mAuth!!.currentUser
                    mDatabase!!.child("Users").child(user!!.uid).child("email").setValue(user!!.email)
                    var profileUpdates: UserProfileChangeRequest? = null
                    if (imgFile != null && imgFile!!.exists()) {

                        var strURL = "usersPhotos/" + user.uid
                        var uri: Uri = android.net.Uri.parse(java.net.URI(imgFile!!.toURI().toString()).toString())
                        storage!!.getReference(strURL).
                                /*storageRef!!.child(strURL).*/putFile(uri).addOnSuccessListener { d ->
                            var uri2 = d.downloadUrl

                            profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName(nameET!!.text.toString())
                                    .setPhotoUri(uri2/*Uri.parse(imgFile!!.absolutePath.toString())*/)
                                    .build()
                            mDatabase!!.child("Users").child(user!!.uid).child("photo").setValue(uri2.toString())
                            if (nameET!!.text.toString() != "")
                                mDatabase!!.child("Users").child(user!!.uid).child("fullName").setValue(nameET!!.text.toString())
                            else
                                mDatabase!!.child("Users").child(user!!.uid).child("fullName").setValue(" ")

                            callback(profileUpdates!!, user!!)

                        }


                    } else {
                        if (nameET!!.text.toString() != "") {
                            profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName(nameET!!.text.toString())
                                    .build()

                            mDatabase!!.child("Users").child(user!!.uid).child("fullName").setValue(nameET!!.text.toString())
                        } else {
                            profileUpdates = UserProfileChangeRequest.Builder()
                                    .build()
                            mDatabase!!.child("Users").child(user!!.uid).child("fullName").setValue(" ")
                        }

                        callback(profileUpdates!!, user!!)
                    }
                }
            }
        }
    }

    private fun validateForm(): Boolean {
        var valid = true

        var name: String = nameET?.text.toString()
        if (TextUtils.isEmpty(name))
            valid = false

        var email: String = emailET?.text.toString()
        if (TextUtils.isEmpty(email))
            valid = false

        var password: String = passwordET?.text.toString()
        if (TextUtils.isEmpty(password))
            valid = false

        var passwordConfirm: String = passwordConfirmET?.text.toString()
        if (TextUtils.isEmpty(passwordConfirm) || TextUtils.isEmpty(passwordConfirm) != TextUtils.isEmpty(password))
            valid = false

        return valid
    }
}
