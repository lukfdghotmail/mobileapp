import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.example.mapsdemo.R
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import kotlinx.android.synthetic.main.activity_realtime.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import realtime.ApiInterface
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var markerOptions:MarkerOptions
    private lateinit var marker:Marker
    private lateinit var cameraPosition:CameraPosition
    var defaultLongitude = -122.088426
    var defaultLatitude  = 37.388064
    lateinit var googleMap:GoogleMap
    lateinit var pusher:Pusher

    fun getRetrofitObject(): ApiInterface? {
        val httpClient = OkHttpClient.Builder()
        val builder = Retrofit.Builder()
            .baseUrl("http://10.0.3.2:4000/")
            .addConverterFactory(ScalarsConverterFactory.create())

        val retrofit = builder
            .client(httpClient.build())
            .build()
        return retrofit.create(ApiInterface::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        markerOptions = MarkerOptions()
        val latLng = LatLng(defaultLatitude, defaultLongitude)
        markerOptions.position(latLng)
        cameraPosition = CameraPosition.Builder()
            .target(latLng)
            .zoom(17f).build()
        simulateButton.setOnClickListener {
            callServerToSimulate()
        }
    }
        private fun callServerToSimulate() {
            val jsonObject = JSONObject()
            jsonObject.put("latitude", defaultLatitude)
            jsonObject.put("longitude", defaultLongitude)

            val body = RequestBody.create(
                MediaType.parse("application/json"),
                jsonObject.toString()
            )
        }
            getRetrofitObject().sendCoordinates(body).enqueue(object:Callback<String>{
                override fun onResponse(call: Call<String>?, response: Response<String>?) {
                    Log.d("TAG",response!!.body().toString())
                }

                override fun onFailure(call: Call<String>?, t: Throwable?) {
                    Log.d("TAG",t!!.message)
                }
            })
        }
    }

    private fun setupPusher() {
        val options = PusherOptions()
        options.setCluster("eu")
        pusher = Pusher("179aaba57d0d7c218ac6", options)

        val channel = pusher.subscribe("my-channel")

        channel.bind("new-values") { channelName, eventName, data ->
            val jsonObject = JSONObject(data)
            val lat:Double = jsonObject.getString("latitude").toDouble()
            val lon:Double = jsonObject.getString("longitude").toDouble()

            runOnUiThread {
                val newLatLng = LatLng(lat, lon)
                marker.position = newLatLng
                cameraPosition = CameraPosition.Builder()
                    .target(newLatLng)
                    .zoom(17f).build()
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            }
        }
    }
    val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
    mapFragment.getMapAsync(this)
    setupPusher()

    override fun onMapReady(googleMap: GoogleMap?) {
        this.googleMap = googleMap!!
        marker = googleMap.addMarker(markerOptions)
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }
    override fun onResume() {
        super.onResume()
        pusher.connect()
    }

    override fun onPause() {
        super.onPause()
        pusher.disconnect()
    }
}
