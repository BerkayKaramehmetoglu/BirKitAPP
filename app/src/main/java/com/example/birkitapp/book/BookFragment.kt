package com.example.birkitapp.book

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.example.birkitapp.R
import com.example.birkitapp.databinding.FragmentBookBinding
import com.example.birkitapp.utils.Utils
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.UUID

class BookFragment : Fragment() {
    private lateinit var design: FragmentBookBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var selectedImage: Uri? = null
    private var selectedBitmap: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        design = FragmentBookBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        db = Firebase.firestore
        storage = Firebase.storage

        registerLaunchers()

        design.bookImageAdd.setOnClickListener {
            selectImages(it)
        }

        design.bookSubmit.setOnClickListener {
            uploadBook(it)
        }

        return design.root
    }

    private fun uploadBook(view: View) {
        val uuid = UUID.randomUUID()
        val reference = storage.reference

        val bookName = design.bookName.text.toString().trim()
        val bookPageCount = design.bookPageCount.text.toString().trim()

        if (bookName.isEmpty()) {
            Utils.toastMessage(requireContext(), "Kitap adı boş olamaz.")
            return
        }

        if (bookPageCount.isEmpty()) {
            Utils.toastMessage(requireContext(), "Kitap sayfa sayısı boş olamaz.")
            return
        }

        if (bookPageCount.toIntOrNull() == null || bookPageCount.toInt() <= 0) {
            Utils.toastMessage(requireContext(), "Geçerli bir sayfa sayısı girin.")
            return
        }

        val imageReference = reference.child("$uuid.jpg")
        if (selectedImage != null) {
            imageReference.putFile(selectedImage!!).addOnSuccessListener {
                imageReference.downloadUrl.addOnSuccessListener {
                    enterValues(view, it.toString(), uuid.toString())
                }
            }.addOnFailureListener {
                Utils.toastMessage(requireContext(), it.localizedMessage)
            }
        }
    }

    private fun enterValues(view: View, downloadUrl: String, uuid: String) {
        val postBook = hashMapOf<String, Any>()
        postBook["user_email"] = auth.currentUser!!.email.toString()
        postBook["book_url"] = downloadUrl
        postBook["book_name"] = design.bookName.text.toString().uppercase()
        postBook["book_page_count"] = design.bookPageCount.text.toString()
        postBook["book_read_page"] = "0"
        postBook["uuid"] = "$uuid.jpg"

        db.collection("Books").add(postBook).addOnSuccessListener {
            Utils.toastMessage(requireContext(), "Kitabınız Kitaplığınıza Eklendi")
            Navigation.findNavController(view).navigate(R.id.action_bookFragment_to_homeFragment)
        }.addOnFailureListener {
            Utils.toastMessage(requireContext(), "Kitabınız Eklenirken Bir Hata Oluştu")
        }

    }

    private fun selectImages(view: View) {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    permission
                )
            ) {
                Snackbar.make(view, "Galeriye erişim izni gerekli", Snackbar.LENGTH_INDEFINITE)
                    .setAction("İzin Ver") {
                        permissionLauncher.launch(permission)
                    }.show()
            } else {
                permissionLauncher.launch(permission)
            }
        } else {
            openGallery()
        }
    }

    private fun openGallery() {
        val intentToGallery =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activityResultLauncher.launch(intentToGallery)
    }

    private fun registerLaunchers() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    result.data?.data?.let { imageUri ->
                        selectedImage = imageUri
                        try {
                            selectedBitmap = if (Build.VERSION.SDK_INT >= 28) {
                                val source = ImageDecoder.createSource(
                                    requireActivity().contentResolver,
                                    imageUri
                                )
                                ImageDecoder.decodeBitmap(source)
                            } else {
                                MediaStore.Images.Media.getBitmap(
                                    requireActivity().contentResolver,
                                    imageUri
                                )
                            }
                            design.bookImageAdd.setImageBitmap(selectedBitmap)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    openGallery()
                } else {
                    Utils.toastMessage(requireContext(), "İzni Reddettiniz")
                }
            }
    }
}