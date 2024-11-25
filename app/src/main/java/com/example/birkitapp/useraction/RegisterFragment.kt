package com.example.birkitapp.useraction

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.birkitapp.R
import com.example.birkitapp.databinding.FragmentRegisterBinding
import com.example.birkitapp.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterFragment : Fragment() {
    private lateinit var design: FragmentRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        design = FragmentRegisterBinding.inflate(inflater, container, false)
        auth = Firebase.auth

        design.registerToLogin.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_registerFragment_to_loginFragment)
        }

        design.registerSubmit.setOnClickListener {
            if (!checkLayoutEmpty()) return@setOnClickListener

            val email = design.registerEmail.text.toString().trim()
            val password = design.registerPassword.text.toString().trim()

            registerUser(it, email, password)
        }

        return design.root
    }

    private fun registerUser(view: View, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Utils.toastMessage(requireContext(), "Kayıt Başarılı")
                    Navigation.findNavController(view).navigate(R.id.action_registerFragment_to_loginFragment)
                } else {
                    Utils.toastMessage(requireContext(), it.exception?.message)
                }
            }
    }

    private fun checkLayoutEmpty(): Boolean {
        return when {
            design.registerName.text.isNullOrEmpty() -> {
                Utils.toastMessage(requireContext(), "Kullanıcı Adı Boş Bırakılamaz")
                false
            }

            design.registerEmail.text.isNullOrEmpty() -> {
                Utils.toastMessage(requireContext(), "Email Boş Bırakılamaz")
                false
            }

            design.registerPassword.text.isNullOrEmpty() -> {
                Utils.toastMessage(requireContext(), "Şifre Alanı Boş Bırakılamaz")
                false
            }

            else -> true
        }
    }
}