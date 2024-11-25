package com.example.birkitapp.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.birkitapp.R
import com.example.birkitapp.databinding.FragmentHomeBinding
import com.example.birkitapp.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class HomeFragment : Fragment() {
    private lateinit var design: FragmentHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private val booksList: ArrayList<BooksModel> = arrayListOf()
    private var adapter: RVAdapterHome? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        design = FragmentHomeBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        db = Firebase.firestore
        storage = Firebase.storage

        return design.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = RVAdapterHome(requireContext(), booksList, db, storage)
        design.homeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        design.homeRecyclerView.adapter = adapter

        Utils.getBooks(auth, db, requireContext(), true, booksList, adapter)

        val toolbar = view.findViewById<Toolbar>(R.id.homeToolbar)
        (requireContext() as AppCompatActivity).setSupportActionBar(toolbar)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.signOut -> {
                        auth.signOut()
                        Navigation.findNavController(view)
                            .navigate(R.id.action_homeFragment_to_loginFragment)
                        Utils.toastMessage(
                            requireContext(),
                            "Görüşürüz"
                        )
                        true
                    }

                    R.id.insertBook -> {
                        Navigation.findNavController(view)
                            .navigate(R.id.action_homeFragment_to_bookFragment)
                        true
                    }

                    R.id.otherBooks -> {
                        Navigation.findNavController(view)
                            .navigate(R.id.action_homeFragment_to_otherBooksFragment)
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
}