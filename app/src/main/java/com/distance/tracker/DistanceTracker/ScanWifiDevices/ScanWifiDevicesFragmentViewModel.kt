package com.distance.tracker.DistanceTracker.ScanWifiDevices

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.net.wifi.rtt.RangingRequest
import android.net.wifi.rtt.RangingResult
import android.net.wifi.rtt.RangingResultCallback
import android.net.wifi.rtt.WifiRttManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.distance.tracker.DistanceTracker.Model.WifiDevice
import com.distance.tracker.Utils.ViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import java.lang.Exception
import java.util.concurrent.Executor


class ScanWifiDevicesFragmentViewModel(): ViewModel() {
    private lateinit var rangingRequest: RangingRequest
    private lateinit var wifiManager: WifiManager
    private var wifiRttManager: WifiRttManager? = null
    private lateinit var devicesList: ArrayList<WifiDevice>
    val tag = "ScanWifiDevicesFragmentViewModel"
    val scanWifiDevices = MutableLiveData<ViewState<ArrayList<WifiDevice>>>()

    //noinspection NewApi
     fun initializeWifiManager(context: Context?){
        wifiRttManager = context?.getSystemService(Context.WIFI_RTT_RANGING_SERVICE) as WifiRttManager?
        wifiManager = context?.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

     fun isLocationPermissionEnable(context: Context?): Boolean{
        return context?.let { ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) } == PackageManager.PERMISSION_GRANTED
    }

    //noinspection MissingPermission,NewApi Calling the below method only if location permission is enabled.
    fun scanWifiDevices(){
        try {
            scanWifiDevices.postValue(ViewState.Loading)
            val wifiNetworks: List<ScanResult> = wifiManager.scanResults.take(10)
            rangingRequest = RangingRequest.Builder().run {
                addAccessPoints(wifiNetworks)
                build()
            }
            devicesList = arrayListOf()
            val executor: Executor = Dispatchers.Main.asExecutor()
            wifiRttManager?.startRanging(rangingRequest, executor, object : RangingResultCallback() {
                @RequiresApi(Build.VERSION_CODES.TIRAMISU)
                override fun onRangingResults(results: List<RangingResult>) {
                    results.forEach {result ->
                        val wifiNetwork =
                            wifiNetworks.first { it.BSSID == result.macAddress.toString() }
                        devicesList.add(WifiDevice(name = wifiNetwork.SSID, macAddress = result.macAddress, wifiNetwork.is80211mcResponder, if (wifiNetwork.is80211mcResponder) result.distanceMm else null))
                    }
                    scanWifiDevices.postValue(ViewState.Success(devicesList))
                }

                override fun onRangingFailure(code: Int) {
                    Log.i(tag, code.toString())
                }
            })
        }catch (ex: Exception){
            Log.i(tag, ex.message.toString())
            scanWifiDevices.postValue(ViewState.Failure(ex.message.toString()))
        }
    }
}