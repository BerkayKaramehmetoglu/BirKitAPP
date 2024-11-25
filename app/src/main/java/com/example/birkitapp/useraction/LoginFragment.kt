package com.example.birkitapp.useraction

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.birkitapp.R
import com.example.birkitapp.databinding.FragmentLoginBinding
import com.example.birkitapp.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {
    private lateinit var design: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentUser = auth.currentUser
        if (currentUser != null) {
            Navigation.findNavController(view)
                .navigate(R.id.action_loginFragment_to_homeFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        design = FragmentLoginBinding.inflate(inflater, container, false)
        auth = Firebase.auth

        design.loginToRegister.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_loginFragment_to_registerFragment)
        }

        design.loginSubmit.setOnClickListener {
            if (!checkLayoutEmpty()) return@setOnClickListener

            val email = design.loginEmail.text.toString()
            val password = design.loginPassword.text.toString()

            loginUser(it, email, password)
        }
        return design.root
    }

    private fun loginUser(view: View, email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                Utils.toastMessage(
                    requireContext(),
                    "Bir KitAPP'e Hoş Geldin"
                )
                Navigation.findNavController(view)
                    .navigate(R.id.action_loginFragment_to_homeFragment)
            } else {
                Utils.toastMessage(requireContext(), it.exception?.message)
            }
        }
    }

    private fun checkLayoutEmpty(): Boolean {
        return when {
            design.loginEmail.text.isNullOrEmpty() -> {
                Utils.toastMessage(requireContext(), "Email Boş Bırakılamaz")
                false
            }

            design.loginPassword.text.isNullOrEmpty() -> {
                Utils.toastMessage(requireContext(), "Şifre Alanı Boş Bırakılamaz")
                false
            }

            else -> true
        }
    }
}