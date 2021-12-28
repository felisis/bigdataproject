package com.felis.mldl.register

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface RegisterService{
    @FormUrlEncoded
    @POST("regist_user/")
    fun requestRegister(
        @Field("user_id") user_id:String,
        @Field("password") password:String
    ) : Call<Register>
}