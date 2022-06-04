package com.bastilla.storyapp.module.main.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bastilla.storyapp.databinding.ItemStoryCardBinding
import com.bastilla.storyapp.model.StoryModel
import com.bastilla.storyapp.module.detail.DetailActivity
import com.bastilla.storyapp.utils.DiffUtilCallback
import com.bumptech.glide.Glide

class StoryAdapter : PagingDataAdapter<StoryModel, StoryAdapter.ViewHolder>(
    DIFF_CALLBACK
) {
    private var listStory = ArrayList<StoryModel>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemStoryCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))

    }

    class ViewHolder(private var binding: ItemStoryCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoryModel?) {
            with(binding) {
                Glide.with(storyImage)
                    .load(story?.image) // URL Avatar
                    .into(storyImage)
                storyName.text = story?.name
                storyDescription.text = story?.description
                itemView.setOnClickListener {
                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            itemView.context as Activity,
                            Pair(storyImage, "profile"),
                            Pair(storyName, "name"),
                        )
                    val intent = Intent(itemView.context, DetailActivity::class.java)
                    intent.putExtra(DetailActivity.EXTRA_STORY, story)
                    itemView.context.startActivity(intent, optionsCompat.toBundle())
                }

            }
        }

    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryModel>() {
            override fun areItemsTheSame(oldItem: StoryModel, newItem: StoryModel): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: StoryModel, newItem: StoryModel): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}
