package com.example.birkitapp.useraction

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.birkitapp.R
import com.example.birkitapp.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private lateinit var design: FragmentLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        design = FragmentLoginBinding.inflate(inflater, container, false)

        design.loginToRegister.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_loginFragment_to_registerFragment)
        }

        return design.root
    }

}