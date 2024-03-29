package com.example.mymaps.ui.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.example.mymaps.R
import com.example.mymaps.databinding.ActivityMainBinding
import com.example.mymaps.interfaces.iPresenter
import com.example.mymaps.interfaces.iView
import com.example.mymaps.model.data.dbsqlite.adminDB.AdminMapsDB
import com.example.mymaps.model.data.dbsqlite.adminDB.AdminMapsGeoJsonDB
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
import com.mapbox.maps.plugin.locationcomponent.*
import kotlinx.coroutines.*

@SuppressLint("Lifecycle")
class MainView : AppCompatActivity(), iView, OnMapClickListener , OnMapLongClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var presenter: iPresenter
    var listFavoriteLocations = ArrayList<Point>()

    private val responseLauncher = registerForActivityResult(StartActivityForResult()){ activityResult ->
        if (activityResult.resultCode == RESULT_OK){
            val longitude = activityResult.data?.getDoubleExtra("longitude", -74.0498149)
            val latitude = activityResult.data?.getDoubleExtra("latitude",4.6760501)
            binding.mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(Point.fromLngLat(longitude!!, latitude!!)).zoom(5.0).build())
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
        val itemSelect = object : AdapterView.OnItemSelectedListener {
            @SuppressLint("UseCompatLoadingForDrawables")
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (binding.customTb.spinnerBasemap.selectedItem.toString()) {
                    "Mapbox Streets" -> binding.customTb.imageMap.setImageDrawable(resources.getDrawable(R.drawable.map))
                    "Satellite" -> binding.customTb.imageMap.setImageDrawable(resources.getDrawable(R.drawable.satellite))
                    "Traffic day" -> binding.customTb.imageMap.setImageDrawable(resources.getDrawable(R.drawable.traffic))
                    else -> binding.customTb.imageMap.setImageDrawable(resources.getDrawable(R.drawable.map))
                }
                loadStyle()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        binding.customTb.spinnerBasemap.onItemSelectedListener = itemSelect

        loadSpinner()
        loadInitialData()
        binding.floatingAlert.setOnClickListener{
            binding.floatingMenu.collapse()
        }
        Handler(Looper.getMainLooper()).postDelayed({
        }, 2000)
    }

    override fun onResume() {
        super.onResume()
        searchData()
        showSavedPoints()
    }

    private fun loadInitialData(){
        loadMap()
    }

    private fun loadMap(){
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                onMapReady()
            }
            else -> viewMapNotPermission(-74.0498149, 4.6760501)
        }

        presenter = PresenterDataImpl(this, this)
        binding.mapView.getMapboxMap().apply {
            addOnMapLongClickListener(this@MainView)
            addOnMapClickListener(this@MainView)
        }
        binding.floatingButtonLocation.setOnClickListener{onMapReady()}
        binding.customTb.tvFavorites.setOnClickListener{goToFavoriteLocations()}
    }

    private fun loadSpinner(){
        val listStyles = mutableListOf<String>()
        listStyles.add("Mapbox Streets")
        listStyles.add("Satellite")
        listStyles.add("Traffic day")
        binding.customTb.spinnerBasemap.adapter = ArrayAdapter(this, R.layout.spinner_styles, listStyles)
    }

    private fun viewMapNotPermission(longitude: Double, latitude: Double){
        binding.mapView.getMapboxMap()
            .loadStyleUri(getStyleMap(), object : Style.OnStyleLoaded {
                override fun onStyleLoaded(style: Style) {
                    addAnnotationToMap(longitude, latitude, isAlert = false)
                }
            })
    }

    private fun getStyleMap() = when (binding.customTb.spinnerBasemap.selectedItem.toString()) {
            "Mapbox Streets" -> Style.MAPBOX_STREETS
            "Satellite" -> Style.SATELLITE
            "Traffic day" -> Style.TRAFFIC_DAY
            else -> Style.MAPBOX_STREETS
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
                .zoom(10.0)
                .build()
        )
        binding.mapView.getMapboxMap().loadStyleUri(
            getStyleMap()
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
                    R.drawable.ic_baseline_my_location_24,
                ),
                shadowImage = AppCompatResources.getDrawable(
                    this@MainView,
                    R.drawable.ic_baseline_my_location_24,
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

    fun addAnnotationToMap(longitude : Double, latitude : Double, texto : String = "", isAlert: Boolean){

        val image = if (isAlert) R.drawable.ic_baseline_report_24 else R.drawable.ic_location
        bitmapFromDrawableRes(this,
            image
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

    private fun showSavedPoints(){
        val db = AdminMapsDB(this@MainView)
        val points =db.showLocations()
        GlobalScope.launch {
            runOnUiThread(Runnable {
                for (pos in points) {
                    addAnnotationToMap(
                        pos.longitude,
                        pos.latitude,
                        pos.nameLocation,
                        pos.type == "Alert"
                    )
                }
            })
            delay(5000)
        }
        db.close()
    }

    override fun showLocations() {
        val db = AdminMapsGeoJsonDB(this@MainView)
        val points =db.showLocations()
        GlobalScope.launch {
                var list = 0..100
                runOnUiThread(Runnable {
                    for (pos in list) {
                        addAnnotationToMap(
                            points[pos].longitude,
                            points[pos].latitude,
                            isAlert = false
                        )
                    }
                })
                delay(5000)
        }
        db.close()
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
        return false
    }

    override fun onMapClick(point: Point): Boolean {
        createPoint(point)
        return true
    }

    private fun createPoint(point: Point){

        var dialog: AlertDialog? = null
        val alertDialog = AlertDialog.Builder(this)
        val customLayout = layoutInflater.inflate(R.layout.dialog_personal, null)
        val btnSave = customLayout.findViewById<Button>(R.id.btnSave)
        val spinner = customLayout.findViewById<Spinner>(R.id.spinnerTypePoints)
        val listType = mutableListOf<String>()
        listType.add("Normal")
        listType.add("Alert")
        spinner.adapter = ArrayAdapter(this, R.layout.spinner_dialog, listType)
        val etNamePoint = customLayout.findViewById<EditText>(R.id.etNamePoint)
        alertDialog.setView(customLayout)
        dialog = alertDialog.create()
        dialog.show()
        btnSave.setOnClickListener {
            listFavoriteLocations.add(point)
            val textEdit = etNamePoint.text.toString()
            val db = AdminMapsDB(this@MainView)
            db.insertLocation(textEdit, point.longitude(), point.latitude(), spinner.selectedItem.toString())
            val isAlert = spinner.selectedItem.toString()=="Alert"
            addAnnotationToMap(point.longitude(), point.latitude(), textEdit , isAlert)
            dialog.dismiss()
        }
    }

    private fun loadStyle(){
        binding.mapView.getMapboxMap().loadStyleUri(getStyleMap())
    }

    private fun goToFavoriteLocations(){
        val intent = Intent(this, FavoriteLocations::class.java)
        responseLauncher.launch(intent)
    }
}
