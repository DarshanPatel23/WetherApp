package example.com.weatherapp.bg

import android.os.AsyncTask
import example.com.weatherapp.Global
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ServerGetRequest(val mURL: String, val mListener: OnTaskCompleted) : AsyncTask<Void, Void, String>() {


    override fun doInBackground(vararg p0: Void?): String {
        return fetchResults()
    }

    private fun fetchResults(): String {
        var result = ""

        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL(mURL)
            urlConnection = url.openConnection() as HttpURLConnection

            urlConnection.setRequestProperty("OS", "Android")

            /* optional request header */
            urlConnection.setRequestProperty("Content-Type", "application/json")

            /* optional request header */
            urlConnection.setRequestProperty("Accept", "application/json")

            urlConnection.readTimeout = Global.ServerReadTimeout
            urlConnection.connectTimeout = Global.ServerConnectionTimeout
            urlConnection.requestMethod = "GET"
            urlConnection.setRequestProperty("User-Agent", "")
            urlConnection.useCaches = false
            urlConnection.connect()

            val responseCode = urlConnection.responseCode

            if (responseCode == HttpURLConnection.HTTP_OK) {
                result = convertInputStreamToString(urlConnection.inputStream)
            } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                result = "Authantication Failed"
            } else {
                result = ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            urlConnection?.disconnect()
        }

        return result
    }

    @Throws(IOException::class)
    private fun convertInputStreamToString(inputStream: InputStream): String {
        val bufferedReader = BufferedReader(
            InputStreamReader(
                inputStream,
                "utf-8"
            )
        )
        var line = bufferedReader.readLine()
        var result = ""
        while (line != null) {
            result += line
            line = bufferedReader.readLine()
        }
        inputStream.close()
        return result
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        mListener.onTaskComplete(result)
    }
}