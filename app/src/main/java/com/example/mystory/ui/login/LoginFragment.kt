package com.example.mystory.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mystory.R
import com.example.mystory.data.Result
import com.example.mystory.data.api.respone.LoginResult
import com.example.mystory.data.preference.UserPreference
import com.example.mystory.data.preference.dataStore
import com.example.mystory.databinding.FragmentLoginBinding
import com.example.mystory.factory.PrefViewModelFactory
import com.example.mystory.factory.ViewModelFactory
import com.example.mystory.ui.viewmodel.LoginViewModel
import com.example.mystory.ui.viewmodel.PrefViewModel
import com.example.mystory.util.Util.showNotification
import com.google.android.material.snackbar.Snackbar

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding
    private lateinit var alertDialog: AlertDialog
    private lateinit var loginFactory : ViewModelFactory
    private lateinit var prefFactory : PrefViewModelFactory
    private val prefViewModel : PrefViewModel by viewModels { prefFactory }
    private val loginViewModel: LoginViewModel by viewModels { loginFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginFactory = ViewModelFactory.getInstance(context as Context)
        prefFactory = PrefViewModelFactory(UserPreference.getInstance(context?.dataStore as DataStore<Preferences>))
        playAnim()
        setupAction()
        prefViewModel.getUser().observe(viewLifecycleOwner){
            if(!(it?.userId?.isEmpty() as Boolean)){
                findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
            }
        }
        binding?.btnLoginRegister?.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun playAnim() {
        val tvDetail =
            ObjectAnimator.ofFloat(binding?.tvDetailName, View.ALPHA, VALUE_ANIMATION).setDuration(DURATION)
        val email = ObjectAnimator.ofFloat(binding?.edLoginEmail, View.ALPHA, VALUE_ANIMATION).setDuration(DURATION)
        val password =
            ObjectAnimator.ofFloat(binding?.edLoginPassword, View.ALPHA, VALUE_ANIMATION).setDuration(DURATION)
        val tvDaftar = ObjectAnimator.ofFloat(binding?.textView2, View.ALPHA, VALUE_ANIMATION).setDuration(DURATION)
        val btnLogin =
            ObjectAnimator.ofFloat(binding?.btnLoginLogin, View.ALPHA, VALUE_ANIMATION).setDuration(DURATION)
        val btnRegister =
            ObjectAnimator.ofFloat(binding?.btnLoginRegister, View.ALPHA, VALUE_ANIMATION).setDuration(DURATION)

        val playTogether = AnimatorSet().apply {
            playTogether(tvDaftar, btnLogin)
        }

        AnimatorSet().apply {
            playSequentially(
                tvDetail,
                email,
                password,
                playTogether,
                btnRegister
            )

            start()
        }
    }

    private fun setLoading(isLoading : Boolean){
        binding?.pbLogin?.isVisible = isLoading
    }

    private fun setupAction() {
        binding?.btnLoginLogin?.setOnClickListener {
            val email = binding?.edLoginEmail?.text.toString()
            val password = binding?.edLoginPassword?.text.toString()
            when {
                email.isEmpty() -> {
                    binding?.edLoginEmail?.error = getString(R.string.email_empty)
                }

                password.isEmpty() -> {
                    binding?.edLoginPassword?.error = getString(R.string.password_empty)
                }
                !binding?.edLoginPassword?.error.isNullOrEmpty()-> {
                    binding?.edLoginPassword?.error = getString(R.string.password_error)
                }

                else -> {
                    loginViewModel.login(email, password).observe(requireActivity()){ result ->
                        if(result != null){
                            when(result){
                                is Result.Loading -> setLoading(true)
                                is Result.Error -> {
                                    setLoading(false)
                                    alertDialog = showNotification(
                                        getString(R.string.login_failed),
                                        R.drawable.baseline_close_24,
                                        requireContext()
                                    )
                                    alertDialog.show()
                                }
                                is Result.Success -> {
                                    prefViewModel.loginUser(result.data.loginResult as LoginResult)
                                    setLoading(false)
                                    alertDialog = showNotification(
                                        getString(R.string.login_success),
                                        R.drawable.baseline_check_24,
                                        requireContext()
                                    )
                                    alertDialog.show()
                                    findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
                                }
                                is Result.ServerError, is Result.Authorized -> {
                                   showSnackbar(result.toString())
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showSnackbar(message : String){
        Snackbar.make(view as View,message, Snackbar.LENGTH_SHORT).show()
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