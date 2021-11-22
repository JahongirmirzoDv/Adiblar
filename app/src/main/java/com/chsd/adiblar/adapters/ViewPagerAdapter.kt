package com.chsd.adiblar.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.chsd.adiblar.models.Writer
import com.chsd.adiblar.ui.LiblaryFragment
import com.chsd.adiblar.ui.SettingsFragment
import com.chsd.adiblar.ui.WritersFragment

class ViewPagerAdapter(var list: ArrayList<String>, fragment: Fragment) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = list.size

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                WritersFragment()
            }
            1 -> {
                LiblaryFragment()
            }
            else -> {
                SettingsFragment()
            }
        }
    }

}