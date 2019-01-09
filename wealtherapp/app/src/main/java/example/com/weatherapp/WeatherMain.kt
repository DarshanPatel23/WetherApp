package example.com.weatherapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_weather.*
import java.util.*


class WeatherMain : AppCompatActivity() {
    private var locationTrack: LocationTrack? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        setSupportActionBar(toolbar)
        assert(supportActionBar != null)
        supportActionBar!!.title = resources.getString(R.string.app_name)

        detect_location.setOnClickListener {
            var isEnable = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                isEnable = isPermissionGranted()
            }
            if (isEnable) {
                locationTrack = LocationTrack(this@WeatherMain)

                if (locationTrack!!.canGetLocation()) {
                    val longitude = locationTrack!!.getLongitude()
                    val latitude = locationTrack!!.getLatitude()

                    val geocoder = Geocoder(this, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(latitude, longitude, 1)

                    if (longitude == 0.0 && latitude == 0.0) {
                        Snackbar.make(weather_find_btn, "Something went wrong", Snackbar.LENGTH_SHORT).show()
                    } else {
                        if (addresses.size > 0) {
                            val cityName = addresses[0].locality;
                            val i = Intent(this@WeatherMain, WeatherDetail::class.java)
                            i.putExtra("city_name", cityName)
                            startActivity(i)
                        }
                    }
                } else {
                    locationTrack!!.showSettingsAlert()
                }
            }
        }

        weather_find_btn.setOnClickListener {
            if (edt_cityname.text.isNullOrBlank()) {
                Snackbar.make(weather_find_btn, "Please enter city name", Snackbar.LENGTH_SHORT).show()
            } else {
                val i = Intent(this@WeatherMain, WeatherDetail::class.java)
                i.putExtra("city_name", edt_cityname.text.toString())
                startActivity(i)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (i in permissions.indices) {
            val permission = permissions[i]
            val grantResult = grantResults[i]
            if (permission == Manifest.permission.ACCESS_FINE_LOCATION
                || permission == Manifest.permission.ACCESS_COARSE_LOCATION
            ) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    detect_location.performClick()
                    break
                } else {
                    Utl.displaySnackbar(weather_find_btn, resources.getString(R.string.allow_permission))
                    break
                }
            }
        }
    }

    private val REQ_CODE: Int = 121
    private fun isPermissionGranted(): Boolean {
        if (ContextCompat.checkSelfPermission(this@WeatherMain, Manifest.permission.ACCESS_FINE_LOCATION) == -1
            || ContextCompat.checkSelfPermission(this@WeatherMain, Manifest.permission.ACCESS_COARSE_LOCATION) == -1
        ) {
            ActivityCompat.requestPermissions(
                this@WeatherMain, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                    , Manifest.permission.ACCESS_COARSE_LOCATION
                ), REQ_CODE
            )
            return false
        } else {
            return true
        }
    }
}