package com.example.mystory.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.example.mystory.R
import com.example.mystory.data.preference.UserPreference
import com.example.mystory.data.preference.dataStore
import com.example.mystory.databinding.FragmentLoginBinding
import com.example.mystory.factory.ViewModelFactory
import com.example.mystory.ui.viewmodel.LoginViewModel
import com.example.mystory.util.Util.showNotification

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding
    private var loginViewModel: LoginViewModel? = null
    private lateinit var alertDialog: AlertDialog

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
        setupViewModel()
        setupAction()
        playAnim()
        binding?.btnLoginRegister?.setOnClickListener {
            findNavController().safeNavigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }
    }

    private fun playAnim() {
        val tvDetail =
            ObjectAnimator.ofFloat(binding?.tvDetailName, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding?.edLoginEmail, View.ALPHA, 1f).setDuration(500)
        val password =
            ObjectAnimator.ofFloat(binding?.edLoginPassword, View.ALPHA, 1f).setDuration(500)
        val tvDaftar = ObjectAnimator.ofFloat(binding?.textView2, View.ALPHA, 1f).setDuration(500)
        val btnLogin =
            ObjectAnimator.ofFloat(binding?.btnLoginLogin, View.ALPHA, 1f).setDuration(500)
        val btnRegister =
            ObjectAnimator.ofFloat(binding?.btnLoginRegister, View.ALPHA, 1f).setDuration(500)

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

    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(context?.dataStore as DataStore<Preferences>))
        )[LoginViewModel::class.java]
        loginViewModel?.loginResult?.observe(viewLifecycleOwner) { login ->
            loginViewModel?.saveUser(
                login?.loginResult?.name,
                login?.loginResult?.userId,
                login?.loginResult?.token
            )
        }

        loginViewModel?.message?.observe(viewLifecycleOwner) { message ->
            when (message) {
                "200" -> {
                    alertDialog = showNotification(
                        getString(R.string.login_success),
                        R.drawable.baseline_check_24,
                        requireContext()
                    )
                    alertDialog.show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        alertDialog.dismiss()
                        findNavController().safeNavigate(LoginFragmentDirections.actionLoginFragmentToMainFragment())
                    }, 2000)
                }
            }
            loginViewModel?.message?.postValue(null)
        }

        loginViewModel?.error?.observe(viewLifecycleOwner) {error ->
            when(error){
                "error" -> {
                    alertDialog = showNotification(
                        getString(R.string.login_failed),
                        R.drawable.baseline_close_24,
                        requireContext()
                    )
                    alertDialog.show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        alertDialog.dismiss()
                    }, 2000)
                }
            }
        }

        loginViewModel?.loading?.observe(requireActivity()) { isLoading ->
            binding?.pbLogin?.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
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

                password.length < 8 -> {
                    binding?.edLoginPassword?.error = getString(R.string.password_error)
                }

                else -> {
                    loginViewModel?.login(email, password, requireContext())
                }
            }
        }
    }

    private fun NavController.safeNavigate(direction: NavDirections) {
        currentDestination?.getAction(direction.actionId)?.run {
            navigate(direction)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}