package com.example.h.munchieroulette
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.content.pm.PackageManager
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
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.jar.Manifest

@Suppress("DEPRECATED_IDENTITY_EQUALS")
class MainActivity : AppCompatActivity(),View.OnClickListener {
    private var spinning: Boolean = false
    private var lastDir: Float = 0f
    private var random: Random = Random()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
    override fun onClick(v: View){

        when(v.id){
            cLayoutRoulette.id -> {
                spinRoulette()
            }
            else -> {
            }
        }
    }

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
            Log.d("Test", "clicked")
        }
    }
}
