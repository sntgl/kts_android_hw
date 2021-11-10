package com.example.ktshw1.networking

import Networking
import com.example.ktshw1.model.User

class ProfileRepository {

    suspend fun getId(): User {
        return Networking.redditApi.me()
    }
}