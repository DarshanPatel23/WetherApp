package example.com.weatherapp

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.support.design.widget.Snackbar
import android.view.View

object Utl {
    fun displaySnackbar(view: View, msg: String) {
        val snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show()
    }

    fun checkIfInternetServiceAvailable(mContext: Context?): Boolean {
        var activeNetworkInfo: NetworkInfo? = null
        if (mContext != null) {
            val connectivityManager = mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
        } else
            return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
    }
}