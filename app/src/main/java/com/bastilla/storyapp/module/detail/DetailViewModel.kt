package com.bastilla.storyapp.module.detail

import androidx.lifecycle.ViewModel
import com.bastilla.storyapp.model.StoryModel

class DetailViewModel: ViewModel() {
    lateinit var stories: StoryModel

    fun setDetailStory(story: StoryModel) : StoryModel{
        stories = story
        return stories
    }

}