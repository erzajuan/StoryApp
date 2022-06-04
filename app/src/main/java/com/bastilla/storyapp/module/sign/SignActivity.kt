package com.bastilla.storyapp.module.sign

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import com.bastilla.storyapp.R
import com.bastilla.storyapp.databinding.ActivitySignBinding

class SignActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.commit {
            replace(R.id.fragment_container, LoginFragment(), LoginFragment::class.java.simpleName)
        }
    }

}