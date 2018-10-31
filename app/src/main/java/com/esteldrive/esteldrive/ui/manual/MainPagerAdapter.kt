package com.esteldrive.esteldrive.ui.manual

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class MainPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val fragments: MutableList<Fragment> = mutableListOf()

    var last: Int = 0
        set(last) {
            field = last
            notifyDataSetChanged()
        }

    fun addFragment(fragment: Fragment) = fragments.add(fragment)

    fun indexOf(fragment: Fragment): Int = fragments.indexOf(fragment)

    override fun getCount(): Int =fragments.size

    override fun getItem(position: Int): Fragment? = fragments[position]


}
