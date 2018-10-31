package com.esteldrive.esteldrive

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.esteldrive.esteldrive.ui.main.FilesCountries
import com.esteldrive.esteldrive.ui.main.Links
import com.esteldrive.esteldrive.ui.main.MainPage
import com.esteldrive.esteldrive.ui.manual_bottomtab.ManualBT
import com.esteldrive.esteldrive.ui.regestration.Regestration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class MainActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    var fragmentMainPage: Fragment? = null
    var fragmentFiles: Fragment? = null
    var fragmentLinks: Fragment? = null
    var fragmentManual: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()

        fragmentMainPage = MainPage.newInstance()
        fragmentFiles = FilesCountries.newInstance()
        fragmentLinks = Links.newInstance()

        val intent = Intent(this, Regestration::class.java)
        startActivity(intent)

        val bottomNV: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNV.setOnNavigationItemSelectedListener { item: MenuItem? ->
            when (item?.itemId) {
                R.id.action_main -> {
                    replaceFragment(fragmentMainPage!!)
                    true
                }
                R.id.action_files -> {
                    replaceFragment(fragmentFiles!!)
                    true
                }
                R.id.action_links -> {
                    replaceFragment(fragmentLinks!!)
                    true
                }
                R.id.action_manual -> {
                    replaceFragment(ManualBT())
                    true
                }
            }
            true
        }

        replaceFragment(fragmentMainPage!!)
    }

    override fun onStart() {
        super.onStart()
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
    }

    override fun onBackPressed() {
    }


}
