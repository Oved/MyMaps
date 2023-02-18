package com.example.mymaps.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import com.example.mymaps.R
import com.example.mymaps.databinding.ActivityMainBinding
import com.example.mymaps.interfaces.iPresenter
import com.example.mymaps.interfaces.iView
import com.example.mymaps.model.data.Map
import com.example.mymaps.presenter.PresenterDataImpl
import com.mapbox.geojson.Point
import com.mapbox.maps.MAPBOX_ACCESS_TOKEN_RESOURCE_NAME
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager

@SuppressLint("Lifecycle")
class MainView : AppCompatActivity(), iView {

    private lateinit var binding: ActivityMainBinding
    private lateinit var presenter: iPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        MAPBOX_ACCESS_TOKEN_RESOURCE_NAME
        setContentView(binding.root)
        binding.mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS, object : Style.OnStyleLoaded{
            override fun onStyleLoaded(style: Style) {
                addAnotationToMap()
            }
        })
        presenter = PresenterDataImpl(this)
        searchData()

    }

    private fun addAnotationToMap(){
        bitmapFromDrawableRes(this,
            R.drawable.ic_location
        )?.let {
            val annotationApi = binding.mapView?.annotations
            val pointAnnotationManager = annotationApi.createPointAnnotationManager()
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                .withPoint(Point.fromLngLat(-74.0498149, 4.6760501))
                .withIconImage(it)
            pointAnnotationManager.create(pointAnnotationOptions)
        }
    }

    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }

    private fun searchData(){
        presenter.searchData()
    }

    override fun showLocations(locations: List<Map>) {
        locations.size

//        for (location in locations.indices) {
//            val anotation = binding.mapView.annotations
//            val pointAnotationManager = anotation.createPointAnnotationManager()
//            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
//                .withPoint(
//                    Point.fromLngLat(
//                        locations[location].latitude,
//                        locations[location].longitude
//                    )
//                )
////            .withIconImage("R.drawable.ic_location")
//
//            pointAnotationManager.create(pointAnnotationOptions)
//
//        }
    }

    private suspend fun showPoints(point : Map){
        val anotation = binding.mapView.annotations
        val pointAnotationManager = anotation.createPointAnnotationManager()
        val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
            .withPoint(Point.fromLngLat(point.latitude, point.longitude))
//            .withIconImage("R.drawable.ic_location")

        pointAnotationManager.create(pointAnnotationOptions)
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }


    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }
}