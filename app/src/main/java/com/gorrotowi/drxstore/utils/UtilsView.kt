package com.gorrotowi.drxstore.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

fun ViewGroup.inflateView(@LayoutRes resourceId: Int): View {
    return LayoutInflater.from(this.context).inflate(resourceId, this, false)
}