package com.example.h.munchieroulette
import android.Manifest
import android.app.ActionBar
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

private const val TAG = "MainActivity"
private const val BASE_URL = "https://api.yelp.com/v3/"
private const val API_KEY = "CDGCfp9-Opqk3eMG0wrg-weBv6aG0_DA96Xq2kkhBKgyR8Yd1P3xnjndQjEWqwpUYO7cyY7xqIIYLEsSdOu5aHufBDaUDk75r9QuO9jyaSEL4VOgCZw2QDH9AIoUX3Yx"
@Suppress("DEPRECATED_IDENTITY_EQUALS")
class MainActivity : AppCompatActivity(),View.OnClickListener {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var spinning: Boolean = false
    private var lastDir: Float = 0f
    // We create a Random instance to make our wheel spin randomly
    private var random: Random = Random()
    private var degree: Float = 0f
    private  var degreeOld: Float = 0f
    // We have 16 sectors on the wheel, we divide 360 by this value to have angle for each sector
    private val SECTOR = 360f / 16f
    var mLocation: Location? = null
    private var longitude: Float = 0.0f;
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
    private val sectorDegrees: Float = (360f / 16f).toFloat()
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

    //Handle for requesting location permissiion
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
    private fun spinRoulette(){
        var resultTV : TextView = findViewById(R.id.resultTextView)
        degreeOld = degree % 360

        // we calculate random angle for rotation of our wheel
        degree = (random.nextInt(360) + 720).toFloat()


        // rotation effect on the center of the wheel
        var rotateAnim: RotateAnimation = RotateAnimation(
            degreeOld.toFloat(), degree.toFloat(),
            RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f)
        rotateAnim.setDuration(3600)
        rotateAnim.setFillAfter(true)
        rotateAnim.setInterpolator(DecelerateInterpolator())
        rotateAnim.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                resultTV.setText("")
            }

            override fun onAnimationEnd(animation: Animation?) {
                getSectorRestaurantPopup(360f - (degree % 360))// the subtraction is b/c the wheel is turning clockwise so the sector landing would be from left
            }

            override fun onAnimationRepeat(animation: Animation?) {
                TODO("Not yet implemented")
            }
        })

        //Start animation
        cLayoutRoulette.startAnimation(rotateAnim)
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

    //Get Sector's popup window
    private fun getSectorRestaurantPopup(degrees: Float) {
        var degreesWithOffset: Float = degrees - sectorDegrees
        var i: Int = 0
        var text: String? = null
        lateinit var sectorYelpRestaurant: YelpRestaurant

        //degrees = 0 to 11.25 will have negative degreesWithOffset. Convert to 348.75 359.99 accordingly
        if(degreesWithOffset < 0){
            degreesWithOffset = 360 + degreesWithOffset
        }
        var start: Float
        var end: Float
        do {
            // start and end of each sector on the wheel
            start = i * sectorDegrees
            end = start + sectorDegrees
            Log.d(TAG, "i:$i start:$start end:$end")

            if(degreesWithOffset >= start && degreesWithOffset < end){
                // degrees is in [start;end[
                // so text is equals to sectors[i];
                sectorYelpRestaurant = restaurants[i] as YelpRestaurant
                text = sectorYelpRestaurant.name
            }

            i++
        }while (text == null && i < restaurants.size)
        Log.d(TAG, "Degree:$degreesWithOffset i:$i start:$start end:$end")
        //Toast.makeText(this, "Degree:" + degreesWithOffset + "i:" + i, Toast.LENGTH_LONG).show()
        //todo handler for sector yelp restuarant
        showRestaurantPopup(sectorYelpRestaurant)
    }

    //Display restaurant popup window
    private fun showRestaurantPopup(sectorYelpRestaurant: YelpRestaurant) {
        val mainLayout = layoutInflater.inflate(R.layout.activity_main, null)
        val popupWindow = PopupWindow(mainLayout, ActionBar.LayoutParams.MATCH_PARENT,ActionBar.LayoutParams.MATCH_PARENT,true )
        val popupView = layoutInflater.inflate(R.layout.layout_popup, null)
        val popupWindowImage : ImageView = popupView.findViewById(R.id.popupWindowImageView)
        val popupRestaurantName : TextView = popupView.findViewById(R.id.restaurantNameTextView)
        val popupAddress : TextView = popupView.findViewById(R.id.addressTextView)
        val popupRating : TextView = popupView.findViewById(R.id.ratingNumberTextView)

        popupWindow.contentView = popupView
        val closeWindowImageButton = popupView.findViewById<ImageButton>(R.id.closeWindowImageButton)
        closeWindowImageButton.setOnClickListener{
            popupWindow.dismiss()
        }
        popupWindow.showAtLocation(cLayoutParent, 0, 0, 0)
        //TODO("pretify popup window")
        //fill restaurant image using picasso to directly pull image from imageUrl
        Picasso.get().load(sectorYelpRestaurant.imageUrl).into(popupWindowImage)
        popupRestaurantName.setText(sectorYelpRestaurant.name)
        popupAddress.setText(sectorYelpRestaurant.location.address)
        popupRating.setText(sectorYelpRestaurant.rating.toString())

    }
}
