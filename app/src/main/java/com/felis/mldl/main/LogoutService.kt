package com.felis.mldl.main

import retrofit2.Call
import retrofit2.http.*

interface LogoutService {
        @GET("login/logout/")
        fun getLogout(): Call<Logout>
}