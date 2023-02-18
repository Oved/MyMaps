package com.example.mymaps.model

import com.example.mymaps.interfaces.iApiService
import com.example.mymaps.interfaces.iModel
import com.example.mymaps.interfaces.iPresenter
import com.example.mymaps.model.data.Feature
import com.example.mymaps.model.data.Map
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class GetDataModel(val presenter: iPresenter) : iModel {

    private val BASE_URL = "https://d2ad6b4ur7yvpq.cloudfront.net"

    override fun getDataApi() {
        GlobalScope.launch {
            presenter.showLocations(doRequest())
        }
    }

    private suspend fun doRequest(): List<Map> {
        var locations: MutableList<Map> = mutableListOf()
        val retrofit = getRetrofit(BASE_URL)
        val service = retrofit.create(iApiService::class.java)
        val call = service.service().execute()

        if (call.isSuccessful) {
            val featureCollection = call.body()
            val listFeat: List<Feature>? = featureCollection?.features
            for (i in 0 until listFeat?.size!!) {
                locations.add(listFeat[i].map)
            }
            return locations
        } else {
            println("Ocurrió un error")
            return locations
        }
    }

    private fun getRetrofit(url: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(url)
            .client(getClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS)
            .build()
    }
}