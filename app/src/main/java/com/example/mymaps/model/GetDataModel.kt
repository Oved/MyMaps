package com.example.mymaps.model

import android.content.Context
import com.example.mymaps.interfaces.iApiService
import com.example.mymaps.interfaces.iModel
import com.example.mymaps.interfaces.iPresenter
import com.example.mymaps.model.data.dbsqlite.adminDB.AdminMapsGeoJsonDB
import com.example.mymaps.model.data.dbsqlite.typedata.Feature
import com.example.mymaps.model.data.dbsqlite.typedata.Map
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class GetDataModel(val presenter: iPresenter, context: Context) : iModel {

    private val BASE_URL = "https://d2ad6b4ur7yvpq.cloudfront.net"
    private val context = context
    override fun getDataApi() {
        val db = AdminMapsGeoJsonDB(context)
        if (db.showLocations().isEmpty()){
            GlobalScope.launch {
                doRequest()
            }
        }else{
            val locationsDB = db.showLocations()
            db.close()
            presenter.showLocations()
        }
    }

    private fun doRequest() {
        var locations: MutableList<Map> = mutableListOf()
        val retrofit = getRetrofit(BASE_URL)
        val service = retrofit.create(iApiService::class.java)
        val call = service.service().execute()

        if (call.isSuccessful) {
            val featureCollection = call.body()
            val listFeat: List<Feature>? = featureCollection?.features
            for (i in 0 until listFeat?.size!!) {
                locations.add(listFeat[i].properties)
            }
        } else {
            presenter.showError("Ha ocurrido un error")
        }
        saveLocations(locations)
    }

    private fun saveLocations(locations: MutableList<Map>){
        val db = AdminMapsGeoJsonDB(context)
        for (value in locations) {
            db.insertLocationsGeoJson(value.longitude, value.latitude)
        }
        db.close()
        presenter.showLocations()

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