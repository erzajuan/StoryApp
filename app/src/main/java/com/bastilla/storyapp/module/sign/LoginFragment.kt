package com.bastilla.storyapp.module.sign

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.bastilla.storyapp.R
import com.bastilla.storyapp.databinding.FragmentLoginBinding
import com.bastilla.storyapp.module.main.MainActivity

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var factory: ViewModelFactory
    private val viewModel: SignViewModel by activityViewModels { factory }
    private lateinit var message: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        factory = ViewModelFactory.getInstance(requireActivity())

        viewModel.message.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                message = it
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                isLoading(it)
            }
        }

        binding.tvRegister.setOnClickListener {
            activity?.supportFragmentManager?.commit {
                replace(R.id.fragment_container, RegisterFragment())
                addToBackStack(null)
            }
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Email atau Password kurang tepat", Toast.LENGTH_SHORT)
                    .show()
            } else {
                viewModel.login(email, password)
                viewModel.error.observe(viewLifecycleOwner) { event ->
                    event.getContentIfNotHandled()?.let { error ->
                        if (!error) {
                            viewModel.message.observe(viewLifecycleOwner) { event ->
                                event.getContentIfNotHandled()?.let {
                                    message = it
                                }
                            }
                            viewModel.isLoading.observe(viewLifecycleOwner) { event ->
                                event.getContentIfNotHandled()?.let {
                                    isLoading(it)
                                }
                            }
                            viewModel.user.observe(viewLifecycleOwner) { event ->
                                event.getContentIfNotHandled()?.let {
                                    viewModel.setUserToken(it.token)
                                    viewModel.setUserName(it.name)

                                    viewModel.setUserEmail(email)
                                    val intent = Intent(activity, MainActivity::class.java)
                                    startActivity(intent)
                                    activity?.finish()
                                }
                            }
                        } else {
                            binding.edtPassword.apply {
                                text?.clear()
                                setError(null)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun isLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.btnLogin.isEnabled = false
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.btnLogin.isEnabled = true
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

}