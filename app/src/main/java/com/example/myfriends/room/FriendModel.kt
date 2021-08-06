package com.example.myfriends

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FriendModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val name: String,
    val school: String,
    val github: String,
    val photoProfile: String
)
