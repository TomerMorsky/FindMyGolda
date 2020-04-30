package com.example.findmygolda.location

import android.location.Location

interface ILocationChanged {
    fun LocationChanged(location : Location)
}