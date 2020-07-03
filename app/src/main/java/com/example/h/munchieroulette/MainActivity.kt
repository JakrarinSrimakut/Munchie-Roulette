package com.example.h.munchieroulette

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.RotateAnimation
import androidx.constraintlayout.widget.ConstraintLayout
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var cLayoutRoulette: ConstraintLayout
    private var random: Random = Random()
    private var lastDir: Float = 0.0f
    private var spinning: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cLayoutRoulette = findViewById<ConstraintLayout>(R.id.cLayoutRoulette);
    }

    fun spinRoulette(){
        if(!spinning){
            var newDir = random.nextInt(1800).toFloat()
            var pivotX = cLayoutRoulette.width/2f
            var pivotY = cLayoutRoulette.height/2f

            var rotate:Animation = RotateAnimation(lastDir, newDir, pivotX, pivotX)
            rotate.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {
                    spinning = false
                }
                override fun onAnimationEnd(p0: Animation?) {
                    spinning = false
                }

                override fun onAnimationRepeat(p0: Animation?) {

                }
            })
            lastDir = newDir;
            cLayoutRoulette.startAnimation(rotate)
        }
    }
}
