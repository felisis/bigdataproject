package com.felis.mldl.camera


import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import okhttp3.RequestBody
import retrofit2.Call

interface CameraService {

    // 이미지, 동영상 등의 파일을 서버로 보내기 위해 필요한 것 : Multipart
    // POST : 새로운 데이터를 생성할 때 필요
    // RequestBody : 서버에 요청할 때 필요
    @Multipart
    @POST("home/")
    fun uploadFile(
        // 여기가 인풋을 정의하는 곳
        @Part file: MultipartBody.Part?
    ): Call<RequestBody?>? // 아웃풋을 정의하는 곳

    // 웹 서버 연결에 필요한 URL을 적어준다.
    companion object {
        const val DJANGO_SITE = "http://15.164.24.10:8000/"
        const val IMAGELOAD_SITE = "http://15.164.24.10:8000/media/999.png"
    }
}