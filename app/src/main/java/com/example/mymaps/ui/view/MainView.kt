package com.example.mymaps.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
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
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import com.mapbox.maps.plugin.gestures.OnMapLongClickListener
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.gestures.addOnMapLongClickListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("Lifecycle")
class MainView : AppCompatActivity(), iView, OnMapClickListener , OnMapLongClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var presenter: iPresenter
    var listFavoriteLocations = ArrayList<Point>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        MAPBOX_ACCESS_TOKEN_RESOURCE_NAME
        setContentView(binding.root)
        binding.mapView.getMapboxMap()
            .loadStyleUri(Style.MAPBOX_STREETS, object : Style.OnStyleLoaded {
                override fun onStyleLoaded(style: Style) {
                    addAnotationToMap(-74.0498149, 4.6760501)
                }
            })
        presenter = PresenterDataImpl(this)
        searchData()
        binding.mapView.getMapboxMap().apply {
            addOnMapLongClickListener(this@MainView)
            addOnMapClickListener(this@MainView)
        }

        binding.customTb.linearFavorites.setOnClickListener{goToFavoriteLocations()}
    }

    private fun addAnotationToMap(longitude : Double, latitude : Double, texto : String = ""){
        bitmapFromDrawableRes(this,
            R.drawable.ic_location
        )?.let {
            val annotationApi = binding.mapView.annotations
            val pointAnnotationManager = annotationApi.createPointAnnotationManager()
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                .withPoint(Point.fromLngLat(longitude, latitude))
                .withIconImage(it)
                .withTextField(texto)
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
        GlobalScope.launch {
            //for (i in locations.indices) {
                var list = 0..100
                runOnUiThread(Runnable {
                    for (pos in list) {
                        addAnotationToMap(
                            locations[pos].longitude,
                            locations[pos].latitude
                        )
                    }
                })
                delay(10000)
            //}
        }
    }

    override fun showError(message: String) {

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

    override fun onMapLongClick(point: Point): Boolean {
        var dialog = AlertDialog.Builder(this)
        var editText = EditText(this)
        editText.setHint("Name ubication")
        dialog.setView(editText)
        dialog.setTitle("Do you want to add the location to favorites?")
        dialog.setPositiveButton(R.string.save_ubication, DialogInterface.OnClickListener{ dialog, id ->
            listFavoriteLocations.add(point)
            var textEdit = editText.editableText
            addAnotationToMap(point.longitude(), point.latitude(), textEdit.toString() )
        })
        dialog.setNegativeButton(R.string.cancel_save, DialogInterface.OnClickListener{ dialog, id ->
            dialog.cancel()
        })
        var alertDialog = dialog.create()
        alertDialog.show()
        alertDialog.setCancelable(false)
        return true
    }

    override fun onMapClick(point: Point): Boolean {
        return false
    }

    private fun goToFavoriteLocations(){
        val intent = Intent(this, FavoriteLocations::class.java)
        //intent.putExtra("list_favorites", listFavoriteLocations)
        startActivity(intent)
    }
}
