package com.example.birkitapp.useraction

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.birkitapp.R
import com.example.birkitapp.databinding.FragmentLoginBinding
import com.example.birkitapp.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {
    private lateinit var design: FragmentRegisterBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        design = FragmentRegisterBinding.inflate(inflater, container, false)

        design.registerToLogin.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_registerFragment_to_loginFragment)
        }

        return design.root
    }
}