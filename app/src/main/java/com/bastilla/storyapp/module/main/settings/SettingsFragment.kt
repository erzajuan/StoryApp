package com.bastilla.storyapp.module.main.settings

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.activityViewModels
import com.bastilla.storyapp.databinding.FragmentSettingsBinding
import com.bastilla.storyapp.module.sign.SignActivity
import com.bastilla.storyapp.module.sign.SignViewModel
import com.bastilla.storyapp.module.sign.ViewModelFactory
import java.util.*

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var factory: ViewModelFactory
    private val viewModel: SettingsViewModel by activityViewModels { factory }
    private val viewModelSign: SignViewModel by activityViewModels { factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        factory = ViewModelFactory.getInstance(requireActivity())

        initObserve()
        initView()
    }

    private fun initView() {

        binding.localName.text = Locale.getDefault().displayName
        binding.localName.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
        binding.btnLogout.setOnClickListener {
            startActivity(Intent(activity, SignActivity::class.java))
            activity?.finish()
        }

        binding.switchMode.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            viewModel.saveThemeMode(isChecked)
        }

        binding.btnLogout.setOnClickListener {
            viewModelSign.logout()
            startActivity(Intent(activity, SignActivity::class.java))
            activity?.finish()
        }
    }

    private fun initObserve() {
        viewModel.getThemeMode().observe(viewLifecycleOwner) {
            if (it) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            binding.switchMode.isChecked = it
        }

        viewModel.getUserName().observe(viewLifecycleOwner) {
            binding.tvName.text = it
        }
        viewModel.getUserEmail().observe(viewLifecycleOwner) {
            binding.tvEmail.text = it
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}