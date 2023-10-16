package com.db.apps.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MyRetrofitClient {
    private var retrofit: Retrofit?=null

    fun getClient(baseUrl: String): Retrofit{
        if(retrofit==null){
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }
}