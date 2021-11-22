package com.chsd.adiblar.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chsd.adiblar.R
import com.chsd.adiblar.adapters.LiblaryAdapter
import com.chsd.adiblar.databinding.FragmentSearchBinding
import com.chsd.adiblar.models.Writer
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    lateinit var binding: FragmentSearchBinding
    lateinit var firestore: FirebaseFirestore
    lateinit var list: ArrayList<Writer>
    lateinit var list1: ArrayList<Writer>
    lateinit var list2: ArrayList<Writer>
    lateinit var gr: ArrayList<Writer>
    lateinit var rvList: ArrayList<Writer>
    private val TAG = "SearchFragment"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()
        list = ArrayList()
        list1 = ArrayList()
        list2 = ArrayList()
        gr = ArrayList()
        rvList = ArrayList()

        binding.cross.setEndIconOnClickListener {
            binding.searchEdit.text?.clear()
        }

        val liblaryAdapter = LiblaryAdapter(
            rvList,
            requireParentFragment(),
            object : LiblaryAdapter.onPress {
                override fun onclick(writer: Writer, position: Int) {

                }

                override fun myIntent(writer: Writer) {

                }

            })
        binding.searchRv.adapter = liblaryAdapter
        binding.searchEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                gr.clear()
                rvList.clear()
                binding.searchEdit.hint = ""
                firestore.collection("Mumtoz adabiyoti")
                    .get()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            list.clear()
                            for (snap in it.result!!) {
                                var value = snap.toObject(Writer::class.java)
                                list.add(value)
                            }
                        }
                    }
                firestore.collection("O’zbek adabiyoti")
                    .get()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            list1.clear()
                            for (snap in it.result!!) {
                                var value = snap.toObject(Writer::class.java)
                                list1.add(value)
                            }
                        }
                    }
                firestore.collection("Jahon adabiyoti")
                    .get()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            list2.clear()
                            for (snap in it.result!!) {
                                var value = snap.toObject(Writer::class.java)
                                list2.add(value)
                            }
                        }
                    }
                gr.addAll(list)
                gr.addAll(list1)
                gr.addAll(list2)
                if (s.toString() != "") {
                    for (i in gr) {
                        if (i.name?.contains(s.toString())!!) {
                            if (!rvList.contains(i)) {
                                rvList.add(i)
                            }
                        }
                    }
                }
                liblaryAdapter.notifyDataSetChanged()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })


        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}