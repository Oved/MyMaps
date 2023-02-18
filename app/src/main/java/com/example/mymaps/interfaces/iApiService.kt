package com.example.mymaps.interfaces

import com.example.mymaps.model.data.FeatureCollection
import retrofit2.Call
import retrofit2.http.GET

interface iApiService {

    @GET("/naturalearth-3.3.0/ne_50m_populated_places_simple.geojson")
    fun service():Call<FeatureCollection>
}