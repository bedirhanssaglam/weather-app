package com.android.weatherapp.presentation.view

import android.Manifest
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.weatherapp.R
import com.android.weatherapp.databinding.ActivityMainBinding
import com.android.weatherapp.domain.model.WeatherResponse
import com.android.weatherapp.presentation.extensions.showToast
import com.android.weatherapp.presentation.utils.AppConstants
import com.android.weatherapp.presentation.utils.DateUtils
import com.android.weatherapp.presentation.utils.LocationUtils
import com.android.weatherapp.presentation.utils.getWeatherIconDrawable
import com.android.weatherapp.presentation.viewmodel.WeatherViewModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null

    private val viewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        setupLocationPermissions()
        observeWeatherData()
        setupOptionsMenu()
    }

    private fun setupLocationPermissions() {
        if (!LocationUtils.isLocationEnabled(this)) {
            showToast(getString(R.string.location_provider_turn_off_warning))
            showLocationSettingsDialog()
        } else {
            checkLocationPermissions()
        }

    }

    private fun showLocationSettingsDialog() {
        Toast.makeText(this, getString(R.string.location_provider_turn_off_warning), Toast.LENGTH_SHORT).show()
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    private fun checkLocationPermissions() {
        Dexter.withActivity(this).withPermissions(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if (report!!.areAllPermissionsGranted()) {
                    viewModel.requestLocationData(this@MainActivity)
                }

                if (report.isAnyPermissionPermanentlyDenied) {
                    showPermissionDeniedDialog()
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>?,
                token: PermissionToken?
            ) {
                showRationalDialogForPermissions()
            }
        }).onSameThread().check()
    }

    private fun showPermissionDeniedDialog() {
        Toast.makeText(
            this@MainActivity,
            getString(R.string.denied_location_permission_warning),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.dont_have_location_permission_warning))
            .setPositiveButton(getString(R.string.go_to_the_settings)) { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri: Uri = Uri.fromParts(AppConstants.PACKAGE, packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun observeWeatherData() {
        viewModel.weather.observe(this) { weatherResponse ->
            weatherResponse?.let { setupUi(it) }
        }
    }

    private fun setupUi(weatherResponse: WeatherResponse) {
        for (i: Int in weatherResponse.weather.indices) {
            binding!!.apply {
                tvMain.text = weatherResponse.weather[i].main
                tvMainDescription.text = weatherResponse.weather[i].description
                tvTemp.text = "${weatherResponse.main.temp}°C"

                tvHumidity.text = "${weatherResponse.main.humidity} per cent"
                tvMin.text = "${weatherResponse.main.temp_min} min"
                tvMax.text = "${weatherResponse.main.temp_max} max"
                tvSpeed.text = weatherResponse.wind.speed.toString()
                tvName.text = weatherResponse.name
                tvCountry.text = weatherResponse.sys.country

                tvSunriseTime.text = DateUtils.formatUnixTime(weatherResponse.sys.sunrise)
                tvSunsetTime.text = DateUtils.formatUnixTime(weatherResponse.sys.sunset)

                val iconDrawable: Int = getWeatherIconDrawable(weatherResponse.weather[i].icon)
                ivMain.setImageResource(iconDrawable)
            }
        }
    }

    private fun setupOptionsMenu() {
        viewModel.weather.observe(this) { weatherResponse ->
            weatherResponse?.let { setupUi(it) }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                viewModel.requestLocationData(this)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}
