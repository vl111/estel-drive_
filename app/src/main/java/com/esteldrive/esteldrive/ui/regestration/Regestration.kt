package com.esteldrive.esteldrive.ui.regestration


import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.esteldrive.esteldrive.KeyBoardHide
import com.esteldrive.esteldrive.R
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*


class Regestration : AppCompatActivity() {

    private var mAuthListener: FirebaseAuth.AuthStateListener? = null;

    var emailET: EditText? = null
    var passwordET: EditText? = null
    var loginB: Button? = null

    var mDatabase: DatabaseReference? = null

    private var mCallbackManager: CallbackManager? = null
    private final var TAG: String = "FACELOG"
    private final var TAG1: String = "GMAILLOG"
    private var mAuth: FirebaseAuth? = null

    var loginButton: LoginButton? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_regestration)
        val kbh = KeyBoardHide()
        kbh.setupUI(findViewById(R.id.parentPanel), this@Regestration);

        mDatabase = FirebaseDatabase.getInstance().getReference()
        mDatabase!!.keepSynced(true)

        mAuth = FirebaseAuth.getInstance()
        mCallbackManager = CallbackManager.Factory.create()
        loginButton = findViewById(R.id.login_button)

        emailET = findViewById(R.id.etEmail)
        passwordET = findViewById(R.id.etPassword)
        loginB = findViewById(R.id.button2)

        loginB?.setOnClickListener(View.OnClickListener {
            val currentUser = mAuth!!.getCurrentUser()
            if (currentUser == null) {
                signIn(emailET?.text.toString(), passwordET?.text.toString())
            } else {
                signOut()
                loginB!!.text = this.resources.getString(
                        resources.getIdentifier("login", "string", this.getPackageName()))
               }

            })

        val regButton: TextView = findViewById(R.id.tv3)
        regButton.setOnClickListener({
            val currentUser = mAuth!!.getCurrentUser()
            if (currentUser == null) {
                val intent = Intent(this, RegestrationPage::class.java)
                signOut()
                startActivity(intent)
            }

        })

        val passworRestore: TextView = findViewById(R.id.tv4)
        passworRestore.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Your Email")
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT /*or InputType.TYPE_TEXT_VARIATION_PASSWORD*/
            input.setPadding(30, 30, 30, 30)
            builder.setView(input)

            builder.setPositiveButton("OK", { dialog, which ->
                if (!input.text.equals("")) {
                    mAuth!!.sendPasswordResetEmail(input.text.toString())
                            .addOnSuccessListener({
                                Toast.makeText(this@Regestration, "Email sent.",
                                        Toast.LENGTH_SHORT).show()
                            }).addOnFailureListener {
                        Toast.makeText(this@Regestration, "Failed to send Email.",
                                Toast.LENGTH_SHORT).show()
                    }
                } else
                    dialog.cancel()
            })
            builder.setNegativeButton("Cancel", { dialog, which ->
                dialog.cancel()
            })
            builder.show()
        }

        mAuthListener = FirebaseAuth.AuthStateListener {
            val user: FirebaseUser? = mAuth!!.currentUser
            if (user != null) {
                Log.d(TAG1, "google:signed_in:" + user.uid);
            } else {
                Log.d(TAG1, "google:signed_out:");
            }
           }



        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        loginButton!!.setLoginBehavior(LoginBehavior.WEB_ONLY)
        loginButton!!.setReadPermissions(Arrays.asList("public_profile", "email"))
        loginButton!!.registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onError(error: FacebookException?) {
                Log.d(TAG, "facebook:onError", error);
            }

            override fun onSuccess(result: LoginResult?) {
                Log.d(TAG, "facebook:onSuccess:" + result);
                handleFacebookAccessToken(result!!.getAccessToken());
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }
        });
    }

    public override fun onStart() {
        super.onStart()

        if (mAuth!!.getCurrentUser() !== null)
            this.finish()
        else
            signOut()
    }

    public override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            mAuth!!.removeAuthStateListener { mAuthListener }
        }
    }

    private fun signIn(email: String, password: String) {
        if (!validateForm()) {
            return
        }

        mAuth!!.signInWithEmailAndPassword(email, password).addOnCompleteListener(this@Regestration) { task ->
            run {
                if (!task.isSuccessful) {
                    Toast.makeText(this@Regestration, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                } else {
                    loginB!!.text = this.resources.getString(
                            resources.getIdentifier("logout", "string", this.getPackageName()))
                    this.finish()
                }
            }
        }
    }

    private fun signOut() {
        mAuth!!.signOut()
        LoginManager.getInstance().logOut()
    }

    private fun validateForm(): Boolean {
        var valid: Boolean = true

        var email: String = emailET?.text.toString()
        if (TextUtils.isEmpty(email))
            valid = false

        var password: String = passwordET?.text.toString()
        if (TextUtils.isEmpty(password))
            valid = false

        return valid
    }

    private fun updateUI() {
        Toast.makeText(this@Regestration, "You are logged in", Toast.LENGTH_LONG).show()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        mCallbackManager!!.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.i(TAG, "handleFacebookAccessToken:" + token.token)

        try {
            val credential: AuthCredential = FacebookAuthProvider.getCredential(token.getToken())
            mAuth!!.signInWithCredential(credential)
                    .addOnCompleteListener(this@Regestration) { task ->
                        if (task.isSuccessful()) {
                            Log.i(TAG, "signInWithCredential:success")

                            var user: FirebaseUser? = mAuth!!.currentUser
                            if (user!!.photoUrl == null) {
                                var profileUpdates: UserProfileChangeRequest? = null
                                profileUpdates = UserProfileChangeRequest.Builder()
                                        .setDisplayName(user!!.displayName.toString())
                                        .setPhotoUri(Uri.parse("https://graph.facebook.com/" + Profile.getCurrentProfile().getId() + "/picture?type=large"))
                                        .build()

                                user!!.updateProfile(profileUpdates)
                                        .addOnCompleteListener(object : OnCompleteListener<Void> {
                                            override fun onComplete(task: Task<Void>) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "User profile updated. " + user.photoUrl.toString())
                                                    this@Regestration.finish()
                                                }
                                            }
                                        })
                            } else {
                                this@Regestration.finish()
                            }

                            mDatabase!!.child("Users").child(user!!.uid).child("photo").setValue(user.photoUrl.toString())
                            mDatabase!!.child("Users").child(user!!.uid).child("fullName").setValue(user!!.displayName.toString())
                            mDatabase!!.child("Users").child(user!!.uid).child("email").setValue(user.email.toString())

                            loginB!!.text = this.resources.getString(
                                    resources.getIdentifier("logout", "string", this.getPackageName()))
                            } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException())
                            Toast.makeText(this@Regestration, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show()
                        }
                    }
        } catch (e: Exception) {
            e.printStackTrace()
            Handler().postDelayed({
                handleFacebookAccessToken(token)
            }, 500)
        }


    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return false
    }
}
