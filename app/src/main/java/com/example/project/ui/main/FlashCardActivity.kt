package com.example.project.ui.main

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.project.R


class flash_card : AppCompatActivity() {
    private lateinit var card: View
    private lateinit var front: View
    private lateinit var back: View

    private var isFront = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flash_card)
        setControl()
        setEvent()
    }

    private fun setControl() {
        card = findViewById(R.id.cardContainer)
        front = findViewById(R.id.cardFront)
        back = findViewById(R.id.cardBack)
    }

    private fun setEvent() {
        card.setOnClickListener {
            if (isFront) flip(front, back) else flip(back, front)
            isFront = !isFront
        }
    }

    private fun flip(from: View, to: View) {
        val scale = applicationContext.resources.displayMetrics.density
        from.cameraDistance = 8000 * scale
        to.cameraDistance = 8000 * scale

        val flipOut = ObjectAnimator.ofFloat(from, "rotationY", 0f, 90f)
        flipOut.duration = 300

        val flipIn = ObjectAnimator.ofFloat(to, "rotationY", -90f, 0f)
        flipIn.duration = 300

        flipOut.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                from.visibility = View.GONE
                to.visibility = View.VISIBLE
                flipIn.start()
            }
        })

        flipOut.start()
    }
}