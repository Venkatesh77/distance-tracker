package com.distance.tracker.DistanceTracker.WifiDeviceDetails

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.distance.tracker.DistanceTracker.Model.WifiDevice
import com.distance.tracker.R
import com.distance.tracker.Utils.ViewState
import com.distance.tracker.databinding.FragmentWifiDeviceDetailBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.P)
class WifiDeviceDetailFragment : Fragment() {
    private lateinit var viewBinding: FragmentWifiDeviceDetailBinding
    private val wifiDeviceDetailFragmentViewModel: WifiDeviceDetailFragmentViewModel by viewModels()
    private val tag = "ScanWifiDevicesFragment"
    private val args : WifiDeviceDetailFragmentArgs by navArgs()
    private lateinit var wifiDeviceArg: WifiDevice

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentWifiDeviceDetailBinding.inflate(inflater, container, false)
        wifiDeviceArg = args.wifiDevice
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wifiDeviceDetailFragmentViewModel.initializeWifiManager(context)
        if(wifiDeviceDetailFragmentViewModel.isLocationPermissionEnable(context)){
            lifecycleScope.launch(Dispatchers.Default) {
                wifiDeviceDetailFragmentViewModel.scanNetworkDistance(wifiDeviceArg)
            }
        }else{
            requestPermissionLauncher.launch(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
        wifiDeviceDetailFragmentViewModel.scanWifiDevices.observe(viewLifecycleOwner){
            render(it)
        }
        viewBinding.tvDeviceName.text = wifiDeviceArg.name
        wifiDeviceArg.macAddress?.let {  viewBinding.tvMacAddress.text = it.toString() }
        wifiDeviceArg.distance?.let { viewBinding.tvDistance.text = it.toString() }
    }

    // noinspection NotifyDataSetChanged
    private fun render(state: ViewState<String>){
        when(state){
            is ViewState.Success -> {
                Log.d(tag, "success")
                viewBinding.tvDistance.text = state.data
            }
            is ViewState.Failure -> {
                Toast.makeText(requireContext(), getText(R.string.error), Toast.LENGTH_SHORT).show()
            }
            is ViewState.Loading -> {
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                lifecycleScope.launch(Dispatchers.Default) {
                    wifiDeviceDetailFragmentViewModel.scanNetworkDistance(wifiDeviceArg)
                }
            } else {
                Log.d(tag, "permission denied")
            }
        }
}

