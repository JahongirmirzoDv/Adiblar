package com.chsd.adiblar.ui

import android.annotation.SuppressLint
import android.os.Binder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.chsd.adiblar.R
import com.chsd.adiblar.adapters.RvAdapter
import com.chsd.adiblar.databinding.FragmentHomeBinding
import com.chsd.adiblar.databinding.FragmentWritersRvBinding
import com.chsd.adiblar.models.Writer
import com.google.firebase.firestore.FirebaseFirestore
import me.everything.android.ui.overscroll.HorizontalOverScrollBounceEffectDecorator
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import me.everything.android.ui.overscroll.adapters.RecyclerViewOverScrollDecorAdapter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"

/**
 * A simple [Fragment] subclass.
 * Use the [WritersRvFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WritersRvFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
    }

    lateinit var binding: FragmentWritersRvBinding
    lateinit var list: ArrayList<Writer>
    private val TAG = "WritersRvFragment"
    lateinit var firestore: FirebaseFirestore
    lateinit var rvAdapter: RvAdapter
    lateinit var favoritesList: ArrayList<Writer>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentWritersRvBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()
        list = ArrayList()
        favoritesList = ArrayList()
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
        rvAdapter =
            RvAdapter(list, favoritesList, requireParentFragment(), object : RvAdapter.onPress {
                override fun onclick(writer: Writer) {
                    firestore.collection("Favorites")
                        .document(writer.name.toString()).set(writer)
                        .addOnSuccessListener {
                            Log.d(TAG, "onclick3: ${writer.name}")
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
                    if (favoritesList.size == 0) {
                        firestore.collection("Favorites")
                            .document(writer.name.toString()).set(writer)
                            .addOnSuccessListener {
                                Log.d(TAG, "onclick2: ${writer.name}")
                            }
                    } else {
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
                                    }
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
        binding.innerRv.adapter = rvAdapter
        OverScrollDecoratorHelper.setUpOverScroll(
            binding.innerRv,
            OverScrollDecoratorHelper.ORIENTATION_VERTICAL
        )
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        var path = ""
        super.onResume()
        path = if (param1 == "0") {
            "Mumtoz adabiyoti"
        } else if (param1 == "1") {
            "O’zbek adabiyoti"
        } else {
            "Jahon adabiyoti"
        }
        firestore.collection(path)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    list.clear()
                    for (snap in it.result!!) {
                        var value = snap.toObject(Writer::class.java)
                        list.add(value)
                    }
                    rvAdapter.notifyDataSetChanged()
                }
            }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WritersRvFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
            WritersRvFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)

                }
            }
    }
}