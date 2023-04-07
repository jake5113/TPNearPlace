package com.jake5113.tpkaosearchapp.network

import com.jake5113.tpkaosearchapp.model.KakaoSearchPlaceResponse
import com.jake5113.tpkaosearchapp.model.NidUserInfoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface RetrofitApiService {

    // 네아로 사용자정보 API
    @GET("/v1/nid/me")
    fun getNidUserInfo(@Header("Authorization") authorization: String): Call<NidUserInfoResponse>


    // 카카오 키워드 검색 API
    @Headers("Authorization: KakaoAK e06c0929674d93ab7146639c2a377937")
    @GET("/v2/local/search/keyword.json")
    fun searchPlace(
        @Query("query") query: String,
        @Query("y") latitude: String,
        @Query("x") longitude: String
    ): Call<KakaoSearchPlaceResponse>
}