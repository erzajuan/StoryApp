package com.bastilla.storyapp.module.sign

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.bastilla.storyapp.R
import com.bastilla.storyapp.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var factory: ViewModelFactory
    private val viewModel: SignViewModel by activityViewModels { factory }
    private lateinit var message: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

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
        binding.btnRegister.setOnClickListener {
            val name = binding.edtName.text.toString()
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()

            if (name.isEmpty() or email.isEmpty() or password.isEmpty()) {
                Toast.makeText(context, "Form Tidak Boleh Kosong", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.register(name, email, password)
                viewModel.error.observe(viewLifecycleOwner) { event ->
                    event.getContentIfNotHandled()?.let { error ->
                        if (!error) {
                            activity?.supportFragmentManager?.commit {
                                replace(R.id.fragment_container, LoginFragment())
                                Toast.makeText(
                                    activity,
                                    "Berhasil Membuat Akun",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Gagal Membuat Akun",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun isLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.btnRegister.isEnabled = false
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.btnRegister.isEnabled = true
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

}