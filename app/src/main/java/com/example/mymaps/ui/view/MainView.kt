package com.example.mymaps.ui.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.example.mymaps.R
import com.example.mymaps.databinding.ActivityMainBinding
import com.example.mymaps.interfaces.iPresenter
import com.example.mymaps.interfaces.iView
import com.example.mymaps.model.data.dbsqlite.typedata.Map
import com.example.mymaps.model.data.dbsqlite.adminDB.AdminMapsDB
import com.example.mymaps.presenter.PresenterDataImpl
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MAPBOX_ACCESS_TOKEN_RESOURCE_NAME
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.*
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("Lifecycle")
class MainView : AppCompatActivity(), iView, OnMapClickListener , OnMapLongClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var presenter: iPresenter
    var listFavoriteLocations = ArrayList<Point>()

    private val responseLauncher = registerForActivityResult(StartActivityForResult()){ activityResult ->
        if (activityResult.resultCode.equals(RESULT_OK)){
            val longitude = activityResult.data?.getDoubleExtra("longitude", -74.0498149)
            val latitude = activityResult.data?.getDoubleExtra("latitude",4.6760501)
            binding.mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(Point.fromLngLat(longitude!!, latitude!!)).build())
        }else{onMapReady()}

    }
    private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener {
        binding.mapView.getMapboxMap().setCamera(CameraOptions.Builder().bearing(it).build())
    }

    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        binding.mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(it).build())
        binding.mapView.gestures.focalPoint = binding.mapView.getMapboxMap().pixelForCoordinate(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        MAPBOX_ACCESS_TOKEN_RESOURCE_NAME
        setContentView(binding.root)

        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                onMapReady()
            }
            else -> viewMapNotPermission(-74.0498149, 4.6760501)
        }

        presenter = PresenterDataImpl(this)
        searchData()
        binding.mapView.getMapboxMap().apply {
            addOnMapLongClickListener(this@MainView)
            addOnMapClickListener(this@MainView)
        }
        binding.customTb.tvLocateMe.setOnClickListener{onMapReady()}
        binding.customTb.tvFavorites.setOnClickListener{goToFavoriteLocations()}
    }

    private fun viewMapNotPermission(longitude: Double, latitude: Double){
        binding.mapView.getMapboxMap()
            .loadStyleUri(Style.MAPBOX_STREETS, object : Style.OnStyleLoaded {
                override fun onStyleLoaded(style: Style) {
                    addAnotationToMap(longitude, latitude)
                }
            })
    }

    private val onMoveListener = object : OnMoveListener {
        override fun onMove(detector: MoveGestureDetector): Boolean {
            TODO("Not yet implemented")
        }

        override fun onMoveBegin(detector: MoveGestureDetector) {
            onCameraTrackingDismissed()
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }

    private fun onMapReady() {
        binding.mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .zoom(14.0)
                .build()
        )
        binding.mapView.getMapboxMap().loadStyleUri(
            Style.MAPBOX_STREETS
        ) {
            initLocationComponent()
            setupGesturesListener()
        }
    }

    private fun setupGesturesListener() {
        binding.mapView.gestures.addOnMoveListener(onMoveListener)
    }

    private fun initLocationComponent() {
        val locationComponentPlugin = binding.mapView.location
        locationComponentPlugin.updateSettings {
            this.enabled = true
            this.locationPuck = LocationPuck2D(
                bearingImage = AppCompatResources.getDrawable(
                    this@MainView,
                    R.drawable.ic_my_location,
                ),
                shadowImage = AppCompatResources.getDrawable(
                    this@MainView,
                    R.drawable.ic_my_location,
                ),
                scaleExpression = interpolate {
                    linear()
                    zoom()
                    stop {
                        literal(0.0)
                        literal(0.6)
                    }
                    stop {
                        literal(20.0)
                        literal(1.0)
                    }
                }.toJson()
            )
        }
        locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
    }

    private fun onCameraTrackingDismissed() {
        Toast.makeText(this, "Current location", Toast.LENGTH_SHORT).show()
        binding.mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        binding.mapView.location
            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        binding.mapView.gestures.removeOnMoveListener(onMoveListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.location
            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        binding.mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        binding.mapView.gestures.removeOnMoveListener(onMoveListener)
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
                var list = 0..100
                runOnUiThread(Runnable {
                    for (pos in list) {
                        addAnotationToMap(
                            locations[pos].longitude,
                            locations[pos].latitude
                        )
                    }
                })
                delay(5000)
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


    override fun onMapLongClick(point: Point): Boolean {
        var dialog = AlertDialog.Builder(this)
        var editText = EditText(this)
        editText.setHint("Name ubication")
        dialog.setView(editText)
        dialog.setTitle("Do you want to add the location to favorites?")
        dialog.setPositiveButton(R.string.save_ubication, DialogInterface.OnClickListener{ dialog, id ->
            listFavoriteLocations.add(point)
            var textEdit = editText.editableText.toString()
            val db = AdminMapsDB(this@MainView)
            db.insertLocation(textEdit, point.longitude(), point.latitude())
            addAnotationToMap(point.longitude(), point.latitude(), textEdit )
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
        responseLauncher.launch(intent)
    }
}
