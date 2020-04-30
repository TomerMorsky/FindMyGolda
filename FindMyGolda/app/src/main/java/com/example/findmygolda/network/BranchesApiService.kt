package com.example.findmygolda.network

import com.example.findmygolda.database.BranchEntity
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://wow-final.firebaseio.com/"
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()



interface BranchesApiService {
    @GET("golda.json")
    fun getProperties():
            Deferred<List<BranchEntity>>
}

object BranchApi {
    val retrofitService : BranchesApiService by lazy {
        retrofit.create(BranchesApiService::class.java) }
}