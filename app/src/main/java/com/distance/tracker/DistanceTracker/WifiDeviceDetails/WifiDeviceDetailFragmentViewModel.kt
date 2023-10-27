package com.distance.tracker.DistanceTracker.WifiDeviceDetails

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.net.wifi.rtt.RangingRequest
import android.net.wifi.rtt.RangingResult
import android.net.wifi.rtt.RangingResultCallback
import android.net.wifi.rtt.WifiRttManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.distance.tracker.DistanceTracker.Model.WifiDevice
import com.distance.tracker.Utils.ViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import java.lang.Exception
import java.util.concurrent.Executor


class WifiDeviceDetailFragmentViewModel: ViewModel() {
    private lateinit var rangingRequest: RangingRequest
    private lateinit var wifiManager: WifiManager
    private var wifiRttManager: WifiRttManager? = null
    private lateinit var devicesList: ArrayList<WifiDevice>
    val mainHandler = Handler(Looper.getMainLooper())
    val tag = "ScanWifiDevicesFragmentViewModel"
    val scanWifiDevices = MutableLiveData<ViewState<String>>()

    //noinspection NewApi
     fun initializeWifiManager(context: Context?){
        wifiRttManager = context?.getSystemService(Context.WIFI_RTT_RANGING_SERVICE) as WifiRttManager?
        wifiManager = context?.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

     fun isLocationPermissionEnable(context: Context?): Boolean{
        return context?.let { ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) } == PackageManager.PERMISSION_GRANTED
    }

    //noinspection MissingPermission,NewApi Calling the below method only if location permission is enabled.
     fun scanNetworkDistance(wifiDevice: WifiDevice) {
        try {
            scanWifiDevices.postValue(ViewState.Loading)
            val wifiNetwork: ScanResult? = wifiManager.scanResults.firstOrNull { it.BSSID == wifiDevice.macAddress.toString() }
            wifiNetwork?.let {
                rangingRequest = RangingRequest.Builder().run {
                    addAccessPoint(it)
                    build()
                }
                val executor: Executor = Dispatchers.Default.asExecutor()
                wifiRttManager?.startRanging(
                    rangingRequest,
                    executor,
                    object : RangingResultCallback() {
                        override fun onRangingResults(results: List<RangingResult>) {
                            results.forEach { result ->
                              val  distance =
                                    if (it.is80211mcResponder) result.distanceMm.toString() else "Nil"
                                    scanWifiDevices.postValue(ViewState.Success(distance))
                            }
                            if (it.is80211mcResponder) mainHandler.postDelayed({scanNetworkDistance(wifiDevice)}, 1000)
                        }

                        override fun onRangingFailure(code: Int) {
                            Log.i(tag, code.toString())
                            scanWifiDevices.postValue(ViewState.Failure("Unable to calculate the distance"))
                        }
                    })
            }
        }catch (ex: Exception){
            Log.i(tag, ex.message.toString())
            scanWifiDevices.postValue(ViewState.Failure(ex.message.toString()))
        }
    }

}