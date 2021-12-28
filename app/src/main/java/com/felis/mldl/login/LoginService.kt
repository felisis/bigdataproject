package com.felis.mldl.login

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface LoginService {
    @FormUrlEncoded
    @POST("app_login/")
    fun requestLogin(
        @Field("user_id") user_id:String,
        @Field("password") user_pw:String
    ) : Call<Login>
}