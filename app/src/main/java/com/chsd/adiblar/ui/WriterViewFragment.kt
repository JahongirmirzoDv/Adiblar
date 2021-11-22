package com.chsd.adiblar.ui

import android.annotation.SuppressLint
import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Color
import android.os.Bundle
import android.provider.CalendarContract
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.chsd.adiblar.R
import com.chsd.adiblar.databinding.FragmentWriterViewBinding
import com.chsd.adiblar.models.Writer
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WriterViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WriterViewFragment : Fragment() {
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

    lateinit var binding: FragmentWriterViewBinding
    lateinit var firestore: FirebaseFirestore
    lateinit var favoritesList: ArrayList<Writer>
    private val TAG = "WriterViewFragment"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWriterViewBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()
        favoritesList = ArrayList()
        val writer = arguments?.getSerializable("data") as Writer
        binding.name.text = writer.name
        binding.activityYear.text = "(${writer.birth_date}-${writer.death_date})"
        binding.description.text = writer.information
        Glide.with(requireContext()).load(writer.image).into(binding.image)
        firestore.collection("Favorites")
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    favoritesList.clear()
                    for (snap in it.result!!) {
                        var value = snap.toObject(Writer::class.java)
                        favoritesList.add(value)
                        if (writer.name == value.name) {
                            binding.savedBackground.setCardBackgroundColor(Color.parseColor("#00B238"))
                            binding.saved.setImageResource(R.drawable.ribbon2)
                        }
                    }
                }
            }
        binding.savedBackground.setOnClickListener {
            firestore.collection("Favorites")
                .document(writer.name.toString()).set(writer)
                .addOnSuccessListener {
                    binding.savedBackground.setCardBackgroundColor(Color.parseColor("#00B238"))
                    binding.saved.setImageResource(R.drawable.ribbon2)
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
                        binding.savedBackground.setCardBackgroundColor(Color.parseColor("#00B238"))
                        binding.saved.setImageResource(R.drawable.ribbon2)
                    }
            } else {
                for (i in favoritesList) {
                    if (writer.name == i.name) {
                        firestore.collection("Favorites").document(writer.name.toString())
                            .delete().addOnSuccessListener {
                                binding.savedBackground.setCardBackgroundColor(Color.WHITE)
                                binding.saved.setImageResource(R.drawable.ribbon)
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
        binding.backBackground.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.searchBackground.setOnClickListener {
            binding.backBackground.visibility = View.INVISIBLE
            binding.backImg.visibility = View.INVISIBLE
            binding.backImg.visibility = View.INVISIBLE
            binding.savedBackground.visibility = View.INVISIBLE
            binding.saved.visibility = View.INVISIBLE
            binding.searchBackground.visibility = View.INVISIBLE
            binding.searchImg.visibility = View.INVISIBLE
            binding.search.visibility = View.VISIBLE
            binding.mainContent.transitionToState(R.id.withSearch1, 500)
        }
        binding.cross.setEndIconOnClickListener {
            binding.searchEdit.text?.clear()
            val systemService =
                requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            systemService.hideSoftInputFromWindow(binding.searchEdit.windowToken, 0)
            binding.backBackground.visibility = View.VISIBLE
            binding.backImg.visibility = View.VISIBLE
            binding.backImg.visibility = View.VISIBLE
            binding.savedBackground.visibility = View.VISIBLE
            binding.saved.visibility = View.VISIBLE
            binding.searchBackground.visibility = View.VISIBLE
            binding.searchImg.visibility = View.VISIBLE
            binding.search.visibility = View.GONE
            binding.mainContent.transitionToState(R.id.start, 500)
        }
        binding.searchEdit.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = binding.searchEdit.text.toString().trim()
                val replaceText = "<span style='background-color:yellow'>$searchText</span>"
                val fullText = binding.description.text.toString()
                val modifiedText = fullText.replace(searchText, replaceText, true)
                binding.description.text = Html.fromHtml(modifiedText)
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
         * @return A new instance of fragment WriterViewFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WriterViewFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}