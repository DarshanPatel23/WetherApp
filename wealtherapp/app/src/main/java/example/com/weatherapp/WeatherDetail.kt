package example.com.weatherapp

import android.app.ProgressDialog
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.gson.Gson
import example.com.weatherapp.bg.OnTaskCompleted
import example.com.weatherapp.bg.ServerGetRequest
import example.com.weatherapp.model.ResultModel
import kotlinx.android.synthetic.main.weather_detail.*
import java.lang.Exception


class WeatherDetail : AppCompatActivity(), OnTaskCompleted {
    private var mProgressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weather_detail)

        var bundle = intent.extras
        if (bundle == null) {
            bundle = Bundle()
        }

        val mCityName = bundle.getString("city_name", "")

        setSupportActionBar(weather_detail_toolbar)
        assert(supportActionBar != null)
        supportActionBar!!.title = mCityName

        mProgressDialog = ProgressDialog(this)
        mProgressDialog!!.setMessage(resources.getString(R.string.loading))
        mProgressDialog!!.setCancelable(false)
        mProgressDialog!!.show()

        if (Utl.checkIfInternetServiceAvailable(this@WeatherDetail)) {
            val request = ServerGetRequest(
                URL.BASE_URL + "?q=$mCityName" + "&appid=" + Global.AUTH_KEY, this
            )
            request.execute()
        } else {
            if (mProgressDialog != null) {
                mProgressDialog!!.dismiss()
            }
            Utl.displaySnackbar(city_temp, resources.getString(R.string.internet_connection))
        }
    }

    override fun onTaskComplete(result: String?) {
        if (!result.isNullOrBlank()) {
            try {
                val mResult = Gson().fromJson(result.toString(), ResultModel::class.java)
                if (mResult.main.temp.isNotBlank()) {
                    city_temp.text = mResult.main.temp.toDouble().toInt().toString()
                    city_temp.append(resources.getString(R.string.degree))
                }

                temp_min.text = resources.getString(R.string.min)
                if (mResult.main.temp_min.isNotBlank()) {
                    temp_min.append(" " + mResult.main.temp_min.toDouble().toInt().toString() + resources.getString(R.string.degree))
                }
                temp_max.text = resources.getString(R.string.max)
                if (mResult.main.temp_max.isNotBlank()) {
                    temp_max.append(" " + mResult.main.temp_max.toDouble().toInt().toString() + resources.getString(R.string.degree))
                }

                humidity.text = resources.getString(R.string.humidity)
                humidity_val.text = mResult.main.humidity
                humidity_val.append("%")

                windspeed.text = resources.getString(R.string.windspeed)
                windspeed_val.text = mResult.wind.speed
                windspeed_val.append(" m/s")

                if (mResult.visibility.isNotBlank()) {
                    visiblity.text = resources.getString(R.string.visibility)
                    visiblity_val.text = (mResult.visibility.toInt() / 1000).toString()
                    visiblity_val.append(" Km")
                    visiblity.visibility = View.VISIBLE
                    visiblity_val.visibility = View.VISIBLE
                } else {
                    visiblity.visibility = View.GONE
                    visiblity_val.visibility = View.GONE
                }

                Glide.with(this@WeatherDetail)
                    .setDefaultRequestOptions(RequestOptions())
                    .asBitmap()
                    .load(URL.ICON_URL + mResult.weather.get(0).icon + ".png")
                    .into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            city_cloud.setImageBitmap(resource)
                            city_cloud.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
                        }
                    })
            } catch (e: Exception) {
                e.printStackTrace();
                Utl.displaySnackbar(city_temp, "error")
            }
        } else {
            Utl.displaySnackbar(city_temp, "Something went wrong!!!")
        }
        if (mProgressDialog != null) {
            mProgressDialog!!.dismiss()
        }
    }
}