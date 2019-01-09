package example.com.weatherapp.model

class ResultModel {
    var id: String = ""

    var dt: String = ""

    var clouds: Clouds = Clouds()

    var coord: Coord = Coord()

    var wind: Wind = Wind()

    var cod: String = ""

    var visibility: String = ""

    var sys: Sys = Sys()

    var name: String = ""

    var base: String = ""

    var weather: ArrayList<Weather> = ArrayList()

    var main: Main = Main()
}
