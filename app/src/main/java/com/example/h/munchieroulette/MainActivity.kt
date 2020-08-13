package com.example.h.munchieroulette
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.RotateAnimation
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import android.Manifest
import android.view.animation.LinearInterpolator
import android.widget.TextView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.w3c.dom.Text

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
    private var longitude: Float = 0.0f; //TODO: Change to local variable for lan and Long
    private var latitude: Float = 0.0f;
    val RequestPermissionCode = 1
    private val term: String = "food";
    private var radius: Int = 40000//TODO fun for meters to miles. max:40000 meters
    private val limit: Int = 16
    private val restaurantID: IntArray = intArrayOf(
        R.id.restaurantTextView1,
        R.id.restaurantTextView2,
        R.id.restaurantTextView3,
        R.id.restaurantTextView4,
        R.id.restaurantTextView5,
        R.id.restaurantTextView6,
        R.id.restaurantTextView7,
        R.id.restaurantTextView8,
        R.id.restaurantTextView9,
        R.id.restaurantTextView10,
        R.id.restaurantTextView11,
        R.id.restaurantTextView12,
        R.id.restaurantTextView13,
        R.id.restaurantTextView14,
        R.id.restaurantTextView15,
        R.id.restaurantTextView16
    )
    lateinit var restaurants:List<Any>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        getLastLocation()
        cLayoutRoulette.setOnClickListener(this)
    }

    //Pull restaurants near my location
    fun queryRestaurants(){
        //setup retrofit instance
        val retrofit =
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        //retrofit takes in parameter class instance of using interface yelp service
        val yelpService = retrofit.create(YelpService::class.java)

        //call method from interface.Async so the process doesn't stop for this but continue on. Don't hold the thread
        yelpService.searchRestaurants( "Bearer $API_KEY",term, latitude, longitude, radius, limit).enqueue(object : Callback<YelpSearchResult>{
            override fun onResponse(call: Call<YelpSearchResult>, response: Response<YelpSearchResult>) {
                restaurants = response.body()?.restaurants as List<Any>
                /*
                restaurants.[i].
                    categories[o or 1]
                    distanceInMeters
                    imageUrl
                    location.address
                    name
                    numReview
                    price
                    rating
                 */
                //TODO:Set Restaurants title to textview on wheel
                setRestaurantsTitleToWheel(restaurants, limit)
            }
            override fun onFailure(call: Call<YelpSearchResult>, t: Throwable) {
                Log.i(TAG, "onFailure $t")
            }
        })
        Log.i(TAG, "3." + latitude.toString() + longitude.toString())

    }

    //Set restaurants titles to wheel
    fun setRestaurantsTitleToWheel(restaurants: List<Any>,limit: Int){
        for(x in 0..limit-1){
            var yelpRestaurant: YelpRestaurant = restaurants[x] as YelpRestaurant
            var name: String = yelpRestaurant.name
            var restaurantTextView: TextView = findViewById(restaurantID[x])
            restaurantTextView.setText(name)
        }
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
                spinRoulette()
            }
            else -> {
            }
        }
    }

    //Get most recent location Lat and Long
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
                        queryRestaurants()
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

        cLayoutRoulette.animate()
            .rotationBy(1000f)
            .setDuration(3000)
            .setInterpolator(LinearInterpolator())
            .start()

        //Random

        var yelpRandomRestaurant: YelpRestaurant = restaurants.random() as YelpRestaurant
        var name: String = yelpRandomRestaurant.name
        Toast.makeText(this, name, Toast.LENGTH_LONG).show()
        /*
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
         */
    }
}
