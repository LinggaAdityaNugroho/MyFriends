package com.example.myfriends

import androidx.room.*

@Dao
interface FriendDao {

    @Insert
    suspend fun addFriend(friendModel: FriendModel)

    @Update
    suspend fun editFriend(friendModel: FriendModel)

    @Delete
    suspend fun deleteFriend(friendModel: FriendModel)

    @Query("SELECT * FROM friendmodel ORDER BY id ASC")
    suspend fun getFriend(): List<FriendModel>

    @Query("SELECT * FROM friendmodel WHERE name LIKE :searchQuery OR school LIKE :searchQuery OR github LIKE :searchQuery OR github LIKE :searchQuery")
    fun getsearchDatabase(searchQuery: String): List<FriendModel>

}