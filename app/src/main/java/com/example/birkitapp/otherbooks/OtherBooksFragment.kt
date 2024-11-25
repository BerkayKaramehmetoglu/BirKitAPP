package com.example.birkitapp.otherbooks

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.birkitapp.databinding.FragmentOtherBooksBinding
import com.example.birkitapp.home.BooksModel
import com.example.birkitapp.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class OtherBooksFragment : Fragment() {
    private lateinit var design: FragmentOtherBooksBinding
    private var adapter: RVAdapterOtherBooks? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private val booksList: ArrayList<BooksModel> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        design = FragmentOtherBooksBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        db = Firebase.firestore

        return design.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = RVAdapterOtherBooks(requireContext(), booksList)
        design.otherRcycler.layoutManager = LinearLayoutManager(requireContext())
        design.otherRcycler.adapter = adapter

        Utils.getBooks(auth, db, requireContext(), false, booksList, adapter)
    }

}