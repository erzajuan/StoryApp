package com.bastilla.storyapp.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bastilla.storyapp.model.StoryModel


@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStory(story: List<StoryModel>?)

    @Query("SELECT * FROM storiesTable")
    fun getAllLocalStory(): PagingSource<Int, StoryModel>

    @Query("DELETE FROM storiesTable")
    suspend fun deleteAll()
}