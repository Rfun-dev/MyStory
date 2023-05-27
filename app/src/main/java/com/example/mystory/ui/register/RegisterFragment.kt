package com.example.mystory.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mystory.R
import com.example.mystory.databinding.FragmentRegisterBinding
import com.example.mystory.ui.viewmodel.RegisterVewModel
import com.example.mystory.util.Util.showNotification

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding
    private lateinit var registerViewModel: RegisterVewModel
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
        setupAction()
        setupViewModel()
        playAnim()
        binding?.btnRegisterLogin?.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
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

                password.length < 8 -> {
                    binding?.edRegisterPassword?.error = getString(R.string.password_error)
                }

                else -> {
                    registerViewModel.register(name, email, password)
                }
            }
        }
    }

    private fun setupViewModel() {
        registerViewModel = ViewModelProvider(requireActivity())[RegisterVewModel::class.java]
        registerViewModel.message.observe(requireActivity()) { message ->
            if (message == "201") {
                alertDialog = showNotification(
                    getString(R.string.register_success),
                    R.drawable.baseline_check_24,
                    requireContext()
                )
                alertDialog.show()
                Handler(Looper.getMainLooper()).postDelayed({
                    alertDialog.dismiss()
                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                }, 2000)
            }
        }
        registerViewModel.error.observe(requireActivity()) {error ->
            when(error){
                "error" -> {
                    alertDialog = showNotification(
                        getString(R.string.register_failed),
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

        registerViewModel.isLoading.observe(requireActivity()) { isLoading ->
            if (isLoading) {
                binding?.pbRegister?.visibility = View.VISIBLE
            } else {
                binding?.pbRegister?.visibility = View.GONE
            }
        }

    }

    private fun playAnim() {
        val tvDetail =
            ObjectAnimator.ofFloat(binding?.tvDetailName, View.ALPHA, 1f).setDuration(500)
        val name = ObjectAnimator.ofFloat(binding?.edRegisterName, View.ALPHA, 1f).setDuration(500)
        val email =
            ObjectAnimator.ofFloat(binding?.edRegisterEmail, View.ALPHA, 1f).setDuration(500)
        val password =
            ObjectAnimator.ofFloat(binding?.edRegisterPassword, View.ALPHA, 1f).setDuration(500)
        val tvDaftar = ObjectAnimator.ofFloat(binding?.textView2, View.ALPHA, 1f).setDuration(500)
        val btnLogin =
            ObjectAnimator.ofFloat(binding?.btnRegisterLogin, View.ALPHA, 1f).setDuration(500)
        val btnRegister =
            ObjectAnimator.ofFloat(binding?.btnRegisterRegister, View.ALPHA, 1f).setDuration(500)

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

    override fun onStop() {
        super.onStop()
        registerViewModel.message.postValue(null)
        registerViewModel.error.postValue(null)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}