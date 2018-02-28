package com.tomclaw.drawa.util

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE

fun View?.toggle() {
    if (this?.visibility == VISIBLE) hide() else show()
}

fun View?.show() {
    this?.visibility = VISIBLE
}

fun View?.hide() {
    this?.visibility = GONE
}