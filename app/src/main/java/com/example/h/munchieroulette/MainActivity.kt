package com.example.h.munchieroulette

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Debug
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.RotateAnimation
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(),View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cLayoutRoulette.setOnClickListener(this)
    }
    override fun onClick(v: View){

        when(v.id){
            cLayoutRoulette.id -> {
                cLayoutRoulette.animate().rotation(10f).start()
                Log.d("Test", "clicked")}
            else -> {
            }
        }
    }
}
