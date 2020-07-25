package com.example.h.munchieroulette
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Debug
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import android.Manifest
import com.google.android.gms.location.FusedLocationProviderClient

private const val TAG = "MainActivity"
private const val BASE_URL = "https://api.yelp.com/v3/"
private const val API_KEY = "CDGCfp9-Opqk3eMG0wrg-weBv6aG0_DA96Xq2kkhBKgyR8Yd1P3xnjndQjEWqwpUYO7cyY7xqIIYLEsSdOu5aHufBDaUDk75r9QuO9jyaSEL4VOgCZw2QDH9AIoUX3Yx"
@Suppress("DEPRECATED_IDENTITY_EQUALS")
class MainActivity : AppCompatActivity(),View.OnClickListener {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var spinning: Boolean = false
    private var lastDir: Float = 0f
    private var random: Random = Random()
    var mLocation: Location? = null
    private var longitude: Float = 0.0f;
    private var latitude: Float = 0.0f;
    val RequestPermissionCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //setup retrofit instance
        val retrofit =
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        //retrofit takes in parameter class instance of using interface yelp service
        val yelpService = retrofit.create(YelpService::class.java)

        //call method from interface.Async so the process doesn't stop for this but continue on. Don't hold the thread

        yelpService.searchRestaurants( "Bearer $API_KEY","Avocado Toast", "New York").enqueue(object : Callback<YelpSearchResult>{
            override fun onResponse(call: Call<YelpSearchResult>, response: Response<YelpSearchResult>) {
                Log.i(TAG, "onResponse $response")
            }
            override fun onFailure(call: Call<YelpSearchResult>, t: Throwable) {
                Log.i(TAG, "onFailure $t")
            }
        })

        /*
        //Get permission to use location
        if (ContextCompat.checkSelfPermission(this@MainActivity,
                android.Manifest.permission.ACCESS_FINE_LOCATION) !==
            PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
            } else {
                ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
        }
         */
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission()
        }

        cLayoutRoulette.setOnClickListener(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(this@MainActivity,
                            android.Manifest.permission.ACCESS_FINE_LOCATION) ===
                                PackageManager.PERMISSION_GRANTED)) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    //when a view is clicked run process according to its view id
    override fun onClick(v: View){

        when(v.id){
            cLayoutRoulette.id -> {
                //getLastLocation
                spinRoulette()
            }
            else -> {
            }
        }
    }

    fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission()
        } else {
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    mLocation = location
                    if (location != null) {
                        latitude = location.latitude.toFloat()
                        longitude = location.longitude.toFloat()
                    }
                }
        }
    }


    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            RequestPermissionCode
        )
        this.recreate()
    }

    //Spin the wheel
    fun spinRoulette(){
        if(!spinning){
            var newDir: Float = random.nextInt(1800).toFloat()
            var pivotX: Float = cLayoutRoulette.getWidth()/2f;
            var pivotY: Float = cLayoutRoulette.getHeight()/2f;

            var rotate: Animation = RotateAnimation(lastDir, newDir, pivotX, pivotY)
            rotate.setDuration(2500)
            rotate.setFillAfter(true)

            rotate.setAnimationListener(object: AnimationListener{
                override fun onAnimationStart(p0: Animation?) {
                    spinning = true
                }

                override fun onAnimationEnd(p0: Animation?) {
                    spinning = false
                }

                override fun onAnimationRepeat(p0: Animation?) {
                    TODO("Not yet implemented")
                }
            })

            lastDir = newDir //next spin know where to start off
            cLayoutRoulette.startAnimation(rotate)
        }
    }
}
