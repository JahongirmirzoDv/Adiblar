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
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.chsd.adiblar.R
import com.chsd.adiblar.adapters.LiblaryAdapter
import com.chsd.adiblar.adapters.RvAdapter
import com.chsd.adiblar.databinding.FragmentLiblaryBinding
import com.chsd.adiblar.models.Writer
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LiblaryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LiblaryFragment : Fragment() {
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

    lateinit var binding: FragmentLiblaryBinding
    lateinit var firestore: FirebaseFirestore
    lateinit var favoritesList: ArrayList<Writer>
    lateinit var rvAdapter: LiblaryAdapter
    lateinit var list: ArrayList<Writer>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLiblaryBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()
        favoritesList = ArrayList()
        list = ArrayList()
        firestore.collection("Favorites")
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    favoritesList.clear()
                    for (snap in it.result!!) {
                        var value = snap.toObject(Writer::class.java)
                        favoritesList.add(value)
                    }
                }
            }

        setAdapter(favoritesList)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        firestore.collection("Favorites")
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    favoritesList.clear()
                    for (snap in it.result!!) {
                        var value = snap.toObject(Writer::class.java)
                        favoritesList.add(value)
                    }
                    rvAdapter.notifyDataSetChanged()
                }
            }
        binding.search.setOnClickListener {
            binding.search.visibility = View.GONE
            binding.content.visibility = View.GONE
            binding.searchCard.visibility = View.VISIBLE
        }
        binding.cross.setEndIconOnClickListener {
            binding.searchEdit.text?.clear()
            binding.search.visibility = View.VISIBLE
            binding.content.visibility = View.VISIBLE
            binding.searchCard.visibility = View.GONE
        }
        binding.searchEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                list.clear()
                binding.searchEdit.hint = ""
                if (s.toString() != "") {
                    for (i in favoritesList) {
                        if (i.name?.contains(s.toString())!!) {
                            if (!list.contains(i)) {
                                list.add(i)
                            }
                        }
                    }
                    setAdapter(list)
                } else {
                    setAdapter(favoritesList)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    fun setAdapter(list: ArrayList<Writer>) {
        rvAdapter =
            LiblaryAdapter(list, requireParentFragment(), object : LiblaryAdapter.onPress {
                @SuppressLint("NotifyDataSetChanged")
                override fun onclick(writer: Writer, position: Int) {
                    firestore.collection("Favorites")
                        .document(writer.name.toString()).set(writer)
                        .addOnSuccessListener {

                        }
                    firestore.collection("Favorites")
                        .get()
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                favoritesList.clear()
                                for (snap in it.result!!) {
                                    var value = snap.toObject(Writer::class.java)
                                    favoritesList.add(value)
                                }
                            }
                        }

                    for (i in favoritesList) {
                        if (writer.name == i.name) {
                            firestore.collection("Favorites").document(writer.name.toString())
                                .delete().addOnSuccessListener {
                                    Toast.makeText(
                                        requireContext(),
                                        "delete",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }.addOnSuccessListener {
                                    rvAdapter.notifyItemRemoved(position)
                                }
                        }
                    }
                }

                override fun myIntent(writer: Writer) {
                    val bundle = Bundle()
                    bundle.putSerializable("data", writer)
                    findNavController().navigate(R.id.writerViewFragment, bundle)
                }
            })
        binding.lbRv.adapter = rvAdapter
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LiblaryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LiblaryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}