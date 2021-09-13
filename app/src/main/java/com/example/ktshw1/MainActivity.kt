package com.example.ktshw1

import android.R.attr.button
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.util.*


class MainActivity : AppCompatActivity() {
    var color: Int = R.color.black

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val hand = findViewById<ImageView>(R.id.hand)
        val btn = findViewById<Button>(R.id.change_color)
        val rnd = Random()
        color = resources.getColor(R.color.black, theme)
        btn?.setOnClickListener {
            setImageColor(hand, Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)))
        }
    }

    private fun setImageColor(v: ImageView, c: Int) {
        v.imageTintList = ColorStateList.valueOf(c)
        color = c
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("color", color)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val hand = findViewById<ImageView>(R.id.hand)
        color = savedInstanceState.getInt("color")
        setImageColor(hand, color)
    }
}