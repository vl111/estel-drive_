package com.esteldrive.esteldrive.ui.manual

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.View
import android.widget.Button
import android.support.v4.view.ViewPager
import android.view.KeyEvent
import com.esteldrive.esteldrive.R
import com.esteldrive.esteldrive.ui.regestration.Regestration


class Manual : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual)

        var sPref = getPreferences(MODE_PRIVATE)
        val ed = sPref.edit()


        val tabl: TabLayout = findViewById(R.id.tabL)
        val btn1: Button = findViewById(R.id.button2)
        btn1.setOnClickListener(View.OnClickListener {
            ed.putBoolean("skiped", true)
            ed.commit()

            val intent = Intent(this, Regestration::class.java)
            startActivity(intent)
            this.finish()
        })

        val fragment1 = ActivityManual1.newInstance()
        val fragment2 = ActivityManual1.newInstance()
        val fragment3 = ActivityManual1.newInstance()
        val pageAdapter: MainPagerAdapter? = MainPagerAdapter(supportFragmentManager)
        pageAdapter?.addFragment(fragment1)

        pageAdapter?.addFragment(fragment2)
        pageAdapter?.addFragment(fragment3)


        val viewPager: ViewPager = findViewById(R.id.pager1)
        viewPager.adapter = pageAdapter
        tabl.setupWithViewPager(viewPager)
    }


    private fun updateViewPager() {
        findViewById<ViewPager>(R.id.pager1).let {
            it as ViewPager
            (it.adapter as MainPagerAdapter).last = it.currentItem
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return false
    }
}
