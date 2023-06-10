package com.example.mystory.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mystory.R
import com.example.mystory.data.Result
import com.example.mystory.databinding.FragmentRegisterBinding
import com.example.mystory.factory.ViewModelFactory
import com.example.mystory.ui.viewmodel.RegisterVewModel
import com.example.mystory.util.Util.showNotification
import com.google.android.material.snackbar.Snackbar

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding
    private lateinit var factory : ViewModelFactory
    private val registerViewModel: RegisterVewModel by viewModels { factory }
    private lateinit var alertDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        factory = ViewModelFactory.getInstance(context as Context)
        setupAction()
        playAnim()
    }

    private fun setupAction() {
        binding?.btnRegisterRegister?.setOnClickListener {
            val name = binding?.edRegisterName?.text.toString()
            val email = binding?.edRegisterEmail?.text.toString()
            val password = binding?.edRegisterPassword?.text.toString()
            when {
                name.isEmpty() -> {
                    binding?.edRegisterName?.error = getString(R.string.name_empty)
                }

                email.isEmpty() -> {
                    binding?.edRegisterEmail?.error = getString(R.string.email_empty)
                }

                password.isEmpty() -> {
                    binding?.edRegisterPassword?.error = getString(R.string.password_empty)
                }

                !binding?.edRegisterPassword?.error.isNullOrEmpty() -> {
                    binding?.edRegisterPassword?.error = getString(R.string.password_error)
                }

                else -> {
                    registerViewModel.registerUser(name, email, password).observe(viewLifecycleOwner){ result ->
                        if(result != null){
                            when(result){
                                is Result.Loading -> setLoading(true)
                                is Result.Success -> {
                                    setLoading(false)
                                    alertDialog = showNotification(
                                        getString(R.string.register_success),
                                        R.drawable.baseline_check_24,
                                        requireContext()
                                    )
                                    alertDialog.show()
                                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                                }
                                is Result.Error -> {
                                    setLoading(false)
                                    alertDialog = showNotification(
                                        getString(R.string.register_failed),
                                        R.drawable.baseline_close_24,
                                        requireContext()
                                    )
                                    alertDialog.show()
                                    Log.d("TAG", "setupAction: ")
                                }
                                is Result.Authorized, is Result.ServerError -> {showSnackbar(it.toString())}
                            }
                        }
                    }
                }
            }
        }
    }


    private fun showSnackbar(message : String){
        Snackbar.make(view as View,message,Snackbar.LENGTH_SHORT).show()
    }

    private fun setLoading(isLoading : Boolean){
        binding?.pbRegister?.isVisible = isLoading
    }

    private fun playAnim() {
        val tvDetail =
            ObjectAnimator.ofFloat(binding?.tvDetailName, View.ALPHA, VALUE_ANIMATION).setDuration(DURATION)
        val name = ObjectAnimator.ofFloat(binding?.edRegisterName, View.ALPHA, VALUE_ANIMATION).setDuration(DURATION)
        val email =
            ObjectAnimator.ofFloat(binding?.edRegisterEmail, View.ALPHA, VALUE_ANIMATION).setDuration(DURATION)
        val password =
            ObjectAnimator.ofFloat(binding?.edRegisterPassword, View.ALPHA, VALUE_ANIMATION).setDuration(DURATION)
        val tvDaftar = ObjectAnimator.ofFloat(binding?.textView2, View.ALPHA, VALUE_ANIMATION).setDuration(DURATION)
        val btnLogin =
            ObjectAnimator.ofFloat(binding?.btnRegisterLogin, View.ALPHA, VALUE_ANIMATION).setDuration(DURATION)
        val btnRegister =
            ObjectAnimator.ofFloat(binding?.btnRegisterRegister, View.ALPHA, VALUE_ANIMATION).setDuration(DURATION)

        val playTogether = AnimatorSet().apply {
            playTogether(tvDaftar, btnRegister)
        }

        AnimatorSet().apply {
            playSequentially(
                tvDetail,
                name,
                email,
                password,
                playTogether,
                btnLogin
            )

            start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object{
        const val DURATION : Long= 500
        const val VALUE_ANIMATION : Float = 1f
    }
}