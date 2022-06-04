package com.bastilla.storyapp.module.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.bastilla.storyapp.databinding.ActivityDetailBinding
import com.bastilla.storyapp.model.StoryModel
import com.bumptech.glide.Glide

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailViewModel by viewModels()
    private lateinit var story: StoryModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)


        story = intent.getParcelableExtra(EXTRA_STORY)!!
        viewModel.setDetailStory(story)
        showStory()
    }

    private fun showStory() {
        with(binding) {
            tvDetailName.text = viewModel.stories.name
            tvDetailDescription.text = viewModel.stories.description
            Glide.with(imgStory)
                .load(viewModel.stories.image)
                .into(imgStory)
        }
    }


    companion object {
        const val EXTRA_STORY = "extra_story"
    }
}