package com.example.mymaps.ui.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mymaps.R
import com.example.mymaps.databinding.ActivityLauncherBinding
import com.google.android.material.snackbar.Snackbar

class Splash : AppCompatActivity() {

    private lateinit var binding : ActivityLauncherBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLauncherBinding.inflate(layoutInflater)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(binding.root)
        doAnimation()
        Handler().postDelayed(Runnable {
            permissions()
        },3000)
    }

    private fun doAnimation() {
        val animation1 = AnimationUtils.loadAnimation(this, R.anim.move_up)
        val animation2 = AnimationUtils.loadAnimation(this, R.anim.move_bottom)
        binding.ivSplash.setAnimation(animation2)
        binding.tvSplash2.setAnimation(animation2)
        binding.image.setAnimation(animation1)
    }

    private fun permissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED -> {
                    intent()
                }
                ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.ACCESS_FINE_LOCATION) -> {
                    Snackbar.make(binding.image, "Location permissions are required to show your current location.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK"){
                            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }.show()
                }
                else -> requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        } else {
            intent()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted ->
        if (isGranted) intent() else intent()
    }

    private fun intent(){
        val intent = Intent(this@Splash, MainView::class.java)
        startActivity(intent)
        finish()
    }
}