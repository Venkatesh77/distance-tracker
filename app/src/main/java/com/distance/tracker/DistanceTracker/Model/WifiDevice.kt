package com.distance.tracker.DistanceTracker.Model

import android.net.MacAddress
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WifiDevice(var name: String?, var macAddress: MacAddress?, var isSupportRtt: Boolean, var distance: Int?) : Parcelable