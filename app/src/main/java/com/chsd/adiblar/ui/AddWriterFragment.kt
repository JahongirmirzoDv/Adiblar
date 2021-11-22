package com.chsd.adiblar.ui

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.chsd.adiblar.R
import com.chsd.adiblar.databinding.FragmentAddWriterBinding
import com.chsd.adiblar.models.Writer
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.PermissionListener


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 *
 * A simple [Fragment] subclass.
 * Use the [AddWriterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddWriterFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        Dexter.withContext(requireContext())
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) { /* ... */
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: com.karumi.dexter.listener.PermissionRequest?,
                    p1: PermissionToken?
                ) {

                }

            }).check()

    }

    lateinit var binding: FragmentAddWriterBinding
    lateinit var firestore: FirebaseFirestore
    lateinit var firebaseStorage: FirebaseStorage
    lateinit var reference: StorageReference
    var imagePath: String = ""
    private val TAG = "AddWriterFragment"

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddWriterBinding.inflate(inflater, container, false)
        var list = arrayOf("Mumtoz adabiyoti", "O’zbek adabiyoti", "Jahon adabiyoti")
        firestore = FirebaseFirestore.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        reference = firebaseStorage.getReference("images")
        binding.addImage.setOnClickListener {
            getimage.launch("image/*")
        }
        val categoryAdapter = ArrayAdapter(
            binding.root.context,
            android.R.layout.simple_list_item_1, list
        )
        binding.spinner.setAdapter(categoryAdapter)
        binding.spinner.inputType = 0
        binding.spinner.setOnTouchListener { v, event ->
            binding.spinner.showDropDown()
            true
        }

        binding.save.setOnClickListener {
            binding.progress.visibility = View.VISIBLE
            binding.container.alpha = 0.5F
            val name = binding.name.text.toString()
            val brd = binding.birthYear.text.toString()
            val dd = binding.deathYear.text.toString()
            val info = binding.information.text.toString()
            val spinner = binding.spinner.text.toString()
            firestore.collection(spinner)
                .get()
                .addOnCompleteListener {
                    var isHave = false
                    if (it.isSuccessful) {
                        for (snap in it.result!!) {
                            val adib = snap.toObject(Writer::class.java)
                            if (adib.name?.lowercase() == name.lowercase()
                            ) {
                                isHave = true
                            }
                        }
                    }
                    if (!isHave) {
                        if (binding.name.text.isNotEmpty() && binding.birthYear.text.isNotEmpty() && binding.deathYear.text.isNotEmpty() && binding.information.text.isNotEmpty()) {
                            binding.spinner.setOnItemClickListener { parent, view, position, id ->
                                Toast.makeText(requireContext(), "$position", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            val task =
                                reference.child(System.currentTimeMillis().toString())
                                    .putFile(imagePath.toUri())
                            task.addOnSuccessListener {
                                if (it.task.isSuccessful) {
                                    it.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                                        val writer = Writer(
                                            uri.toString(),
                                            name,
                                            brd,
                                            dd,
                                            info
                                        )
                                        firestore.collection(spinner)
                                            .document(binding.name.text.toString()).set(writer)
                                            .addOnSuccessListener {
                                                binding.progress.visibility = View.INVISIBLE
                                                binding.container.alpha = 1F
                                                binding.writerImage.setImageResource(R.color.wr_image)
                                                binding.name.text.clear()
                                                binding.birthYear.text.clear()
                                                binding.deathYear.text.clear()
                                                binding.spinner.text.clear()
                                                binding.information.text.clear()
                                            }
                                    }
                                }
                            }.addOnFailureListener {
                                it.printStackTrace()
                            }
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Bu yozuvchi oldin qo'shilgan",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        return binding.root
    }


    private val getimage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                imagePath = uri.toString()
                binding.writerImage.setImageURI(uri)
            }
        }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddWriterFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddWriterFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}