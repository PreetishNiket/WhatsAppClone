package com.example.whatsappclone.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ViewPagerAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {

    private val fragments:ArrayList<Fragment> = ArrayList()
    private val str:ArrayList<String> = ArrayList()

    override fun getItem(position: Int): Fragment =fragments[position]

    override fun getCount(): Int =fragments.size

    fun add(fragment: Fragment, string: String)
    {
        fragments.add(fragment)
        str.add(string)

    }

    override fun getPageTitle(position: Int): CharSequence? {
        return str[position]
    }
}