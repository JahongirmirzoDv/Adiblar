package com.chsd.adiblar.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.chsd.adiblar.R
import com.chsd.adiblar.adapters.InnerViewPager
import com.chsd.adiblar.databinding.FragmentWritersBinding
import com.chsd.adiblar.models.Writer
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WritersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WritersFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getStringArrayList(ARG_PARAM1)
        }
    }

    lateinit var binding: FragmentWritersBinding
    lateinit var titleList: ArrayList<String>
    lateinit var firestore: FirebaseFirestore

    private val TAG = "WritersFragment"
    lateinit var innerViewPager: InnerViewPager

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWritersBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()
        search()
        loadTitle()
        binding.tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            @SuppressLint("ResourceAsColor")
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.customView?.findViewById<LinearLayout>(R.id.lan)
                    ?.setBackgroundResource(R.color.back)
                tab?.customView?.findViewById<TextView>(R.id.inner_tab)
                    ?.setTextColor(Color.WHITE)
            }

            @SuppressLint("ResourceAsColor")
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.customView?.findViewById<LinearLayout>(R.id.lan)
                    ?.setBackgroundResource(R.color.white)
                tab?.customView?.findViewById<TextView>(R.id.inner_tab)
                    ?.setTextColor(Color.LTGRAY)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
        innerViewPager = InnerViewPager("0", requireFragmentManager())
        binding.innerViewpager.adapter = innerViewPager
        binding.tab.setupWithViewPager(binding.innerViewpager)
        setTabs()

        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun search() {
        binding.search.setOnClickListener {
            findNavController().navigate(R.id.searchFragment)
        }
    }

    private fun loadTitle() {
        titleList = ArrayList()
        titleList.add("Mumtoz adabiyoti")
        titleList.add("O’zbek adabiyoti")
        titleList.add("Jahon adabiyoti")
    }

    private fun setTabs() {
        for (i in 0 until 3) {
            val tabview =
                LayoutInflater.from(requireContext()).inflate(R.layout.custom_tab, null)
            val tabAt = binding.tab.getTabAt(i)
            tabAt?.customView = tabview
            binding.tab.getTabAt(i)?.customView?.findViewById<TextView>(R.id.inner_tab)
                ?.setTextColor(Color.LTGRAY)
            binding.tab.getTabAt(0)?.customView?.findViewById<LinearLayout>(R.id.lan)
                ?.setBackgroundResource(R.color.back)
            binding.tab.getTabAt(0)?.customView?.findViewById<TextView>(R.id.inner_tab)
                ?.setTextColor(Color.WHITE)


            tabview.findViewById<TextView>(R.id.inner_tab)?.text =
                titleList[i]
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WritersFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: ArrayList<String>) =
            WritersFragment().apply {
                arguments = Bundle().apply {
                    putStringArrayList(ARG_PARAM1, param1)
                }
            }
    }
}