package com.example.mapsdemo

import com.example.mapsdemo.R
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.example.mapsdemo.model.BikeStation
import com.example.mapsdemo.model.Stations

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.station_list.*
import kotlinx.android.synthetic.main.station_list.view.*
import okhttp3.*
import java.io.IOException


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var stationList: List<BikeStation>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


        fun getBikeStationJsonData() {


            Log.i(getString(R.string.DEBUG_MAINACTIVITY), "Loading JSON data")


            var url =
                "https://api.jcdecaux.com/vls/v1/stations?contract=dublin&apiKey=163597812dfb8e11bcdaa6297a730b46529a5bcd"


            Log.i("MapsActivity", url)


            //Create a request object


            val request = Request.Builder().url(url).build()


            //Create a client


            val client = OkHttpClient()




            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    //   TODO("Not yet implemented")
                    Log.i("MapsActivity", "JSON HTTP CALL FAILED")
                }

                override fun onResponse(call: Call, response: Response) {
                    // TODO("Not yet implemented")
                    Log.i(getString(R.string.DEBUG_MAINACTIVITY), "JSON HTTP CALL SUCCEEDED")


                    val body = response?.body?.string()
                    //  println("json loading" + body)
                    Log.i(getString(R.string.DEBUG_MAINACTIVITY), body)
                    var jsonBody = "{\"stations\": " + body + "}"


                    val gson = GsonBuilder().create()
                    stationList = gson.fromJson(jsonBody, Stations::class.java).stations


                    renderMarkers()


                }


            })


        }


        fun renderMarkers() {


            runOnUiThread {


                stationList.forEach {
                    val position = LatLng(it.position.lat, it.position.lng)
                    var marker = mMap.addMarker(
                        MarkerOptions().position(position).title("Marker in ${it.address}")
                    )
                    marker.setTag(it.number)
                    Log.i("MAPACTIVITY",
                        "${it.address} : ${it.position.lat} : ${it.position.lng}"
                    )
                }


                val centreLocation = LatLng(53.349562, -6.278198)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centreLocation, 16.0f))
            }
        }

        override fun onMapReady(googleMap: GoogleMap) {

            Log.i("MAINACTIVITY", "renderMarkers called")

            mMap = googleMap
            getBikeStationJsonData()

            mMap.setOnMarkerClickListener { marker ->


                if (marker.isInfoWindowShown) {

                    marker.hideInfoWindow()
                } else {

                    marker.showInfoWindow()
                }



                Log.i(getString(R.string.DEBUG_MAINACTIVITY), "Marker is clicked")
                Log.i(
                    getString(R.string.DEBUG_MAINACTIVITY),
                    "Marker id (tag) is " + marker.getTag().toString()
                )
                Log.i(getString(R.string.DEBUG_MAINACTIVITY), "Marker address is  " + marker.title)


                true
            }


        }
    }





/**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        var bikes = listOf(
            BikeStation(110, "PHIBSBOROUGH ROAD", "Phibsborough Road", 53.356307, -6.273717),
            BikeStation(12, "ECCLES STREET", "Eccles Street", 53.359246, -6.269779),
            BikeStation(34, "PORTOBELLO HARBOUR", "Portobello Harbour", 53.330362, -6.265163),
            BikeStation(78, "MATER HOSPITAL", "Mater Hospital", 53.359967, -6.264828)
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            bikes.forEach {
                val position = LatLng(it.lng, it.lat)
                var marker =
                    mMap.addMarker(
                        MarkerOptions().position(position).title("Marker in ${it.address}")
                    )
                marker.setTag(it.number)
            }
        }
    }
    }
        // Add a marker in Sydney and move the camera
        // val sydney = LatLng(-34.0, 151.0)
        //  var marker1 = mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
/*
        val smithfield = LatLng(53.34952, -6.278198)
        var marker1 = mMap.addMarker(MarkerOptions().position(smithfield).title("Marker in Smithfield"))
        marker1.setTag(42)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(smithfield,12.0f))

        val parnell= LatLng(53.353462, -6.265305)
        var marker2 = mMap.addMarker(MarkerOptions().position(parnell).title("Marker in Parnell"))
        marker1.setTag(30)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(parnell,12.0f))

        val clonmel= LatLng(53.336021, -6.26298)
        var marker3 = mMap.addMarker(MarkerOptions().position(clonmel).title("Marker in Clonmel"))
        marker1.setTag(54)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(clonmel,12.0f))

        val christchurch= LatLng(53.343368,	-6.27012)
        var marker4 = mMap.addMarker(MarkerOptions().position(christchurch).title("Marker in Christchurch"))
        marker1.setTag(6)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(christchurch,12.0f))

        val avondale= LatLng(53.359405,	-6.276142)
        var marker5 = mMap.addMarker(MarkerOptions().position(avondale).title("Marker in Avondale"))
        marker1.setTag(108)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(clonmel,12.0f))

        val mount= LatLng(53.33796,	-6.24153)
        var marker6 = mMap.addMarker(MarkerOptions().position(mount).title("Marker in Mount"))
        marker1.setTag(56)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(clonmel,12.0f))

        val grantham= LatLng(53.334123,	-6.265436)
        var marker7 = mMap.addMarker(MarkerOptions().position(grantham).title("Marker in Grantham"))
        marker1.setTag(18)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(grantham,12.0f))

            mMap.setOnMarkerClickListener { marker ->

            if (marker.isInfoWindowShown) {
                marker.hideInfoWindow()
            } else {
                marker.showInfoWindow()
            }

            Log.i("MAPACTIVITY", "Marker is clicked")
            Log.i("MAPACTIVITY", "Marker id (tag) is "+ marker.tag.toString())
                Log.i("MAPACTIVITY", "Marker is clicked" + marker.title)
                true
        }
     }
}
*/
     */