package com.chsd.adiblar.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.chsd.adiblar.models.Writer
import com.chsd.adiblar.ui.WritersRvFragment

class InnerViewPager(var pos:String,
    fragmentManager: FragmentManager
) : FragmentStatePagerAdapter(
    fragmentManager,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    override fun getCount(): Int = 3

    override fun getItem(position: Int): Fragment {
       return WritersRvFragment.newInstance(position.toString())
    }
}