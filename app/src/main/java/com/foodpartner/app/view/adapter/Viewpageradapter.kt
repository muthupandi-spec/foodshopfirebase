package com.foodpartner.app.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class viewpageradapter (fm: FragmentManager) :
    FragmentPagerAdapter(fm) {
    private val fragments: MutableList<Fragment> = ArrayList()
    private val fragmentTitle: MutableList<String> = ArrayList()
    fun add(fragment: Fragment, s: String) {
        fragments.add(fragment)
        fragmentTitle.add(s)

    }
    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }
    override fun getCount(): Int {
        return fragments.size
    }
    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentTitle[position]
    }
}