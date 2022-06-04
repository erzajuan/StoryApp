package com.bastilla.storyapp.module.main.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bastilla.storyapp.databinding.FragmentHomeBinding
import com.bastilla.storyapp.module.main.adapter.LoadingStateAdapter
import com.bastilla.storyapp.module.main.adapter.StoryAdapter
import com.bastilla.storyapp.module.main.maps.MapsActivity
import com.bastilla.storyapp.module.sign.ViewModelFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var factory: ViewModelFactory
    private val viewModel: HomeViewModel by activityViewModels { factory }
    private lateinit var adapter: StoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        factory = ViewModelFactory.getInstance(requireActivity())
        binding.swipeRefreshLayout.setOnRefreshListener {
            getStories()
        }
        viewModel.message.observe(viewLifecycleOwner) {
            Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
        }
        getStories()
        adapter = StoryAdapter()

        binding.rvStory.layoutManager = LinearLayoutManager(activity)
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        binding.btnMaps.setOnClickListener {
            val intent = Intent(activity, MapsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getStories() {
        viewModel.getUserToken().observe(viewLifecycleOwner) {
            binding.swipeRefreshLayout.isRefreshing = true
            viewModel.getStories(it).observe(viewLifecycleOwner) { stories ->
                adapter.submitData(lifecycle, stories)
            }
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}