package com.example.ktshw1.utils

import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import timber.log.Timber

class ClickToFullTextSpan(
    private val textView: TextView,
    private val fullText: String
): ClickableSpan() {
    override fun onClick(widget: View) {
        Timber.d("Clicked on span!! I need full text!!!")
        textView.text = fullText
    }
}