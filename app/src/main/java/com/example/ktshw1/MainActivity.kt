package com.example.ktshw1

import android.R.attr.button
import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.util.*


class MainActivity : AppCompatActivity(R.layout.activity_main) {
    @ColorInt
    private var color: Int? = null
    private var hand: ImageView? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        color = resources.getColor(R.color.black, theme)
        hand = findViewById(R.id.hand)
        val btn = findViewById<Button>(R.id.change_color)
        val rnd = Random()
        btn?.setOnClickListener {
            setImageColor(hand!!, Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)))
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setImageColor(v: ImageView, c: Int) {
        v.imageTintList = ColorStateList.valueOf(c)
        color = c
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(COLOR_KEY, color!!)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        color = savedInstanceState.getInt(COLOR_KEY)
        setImageColor(hand!!, color!!)
    }

    companion object {
        private val COLOR_KEY = "color"
    }
}