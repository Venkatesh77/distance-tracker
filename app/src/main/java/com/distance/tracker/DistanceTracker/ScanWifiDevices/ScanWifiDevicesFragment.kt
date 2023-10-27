package com.distance.tracker.DistanceTracker.ScanWifiDevices

import android.Manifest
import android.content.pm.PackageManager
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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.distance.tracker.DistanceTracker.Adapter.DevicesAdapter
import com.distance.tracker.DistanceTracker.Model.WifiDevice
import com.distance.tracker.Notes.ListNotes.ListNotesFragmentDirections
import com.distance.tracker.R
import com.distance.tracker.Utils.OnItemClickListener
import com.distance.tracker.Utils.ViewState
import com.distance.tracker.databinding.FragmentScanWifiDevicesBinding


@RequiresApi(Build.VERSION_CODES.P)
class ScanWifiDevicesFragment : Fragment() {
    private lateinit var viewBinding: FragmentScanWifiDevicesBinding
    private var devicesList = ArrayList<WifiDevice>()
    private val adapter : DevicesAdapter = DevicesAdapter(devicesList)
    private val scanWifiDevicesFragmentViewModel: ScanWifiDevicesFragmentViewModel by viewModels()
    private val tag = "ScanWifiDevicesFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentScanWifiDevicesBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       if(context?.packageManager?.hasSystemFeature(PackageManager.FEATURE_WIFI_RTT) == true) {
           scanWifiDevicesFragmentViewModel.initializeWifiManager(context)
           if (scanWifiDevicesFragmentViewModel.isLocationPermissionEnable(context)) {
               scanWifiDevicesFragmentViewModel.scanWifiDevices()
           } else {
               requestPermissionLauncher.launch(
                   Manifest.permission.ACCESS_FINE_LOCATION
               )
           }

           scanWifiDevicesFragmentViewModel.scanWifiDevices.observe(viewLifecycleOwner) {
               render(it)
           }

           viewBinding.recyclerView.layoutManager = LinearLayoutManager(activity)
           viewBinding.recyclerView.adapter = adapter

           setOnClickListener()
       }else {
           viewBinding.recyclerView.visibility = View.GONE
           viewBinding.tvNoSupport.visibility = View.VISIBLE
           viewBinding.loader.hide()
       }
    }

    private fun setOnClickListener(){
        adapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                findNavController().navigate(ScanWifiDevicesFragmentDirections.actionScanWifiDevicesFragmentToWifiDeviceDetailFragment(devicesList[position]))
            }
        })
    }

    // noinspection NotifyDataSetChanged
    private fun render(state: ViewState<List<WifiDevice>>){
        when(state){
            is ViewState.Success -> {
                viewBinding.loader.hide()
                devicesList.clear()
                devicesList.addAll(state.data as ArrayList<WifiDevice>)
                adapter.notifyDataSetChanged()
            }
            is ViewState.Failure -> {
                viewBinding.loader.hide()
                Toast.makeText(requireContext(), getText(R.string.notes), Toast.LENGTH_SHORT).show()
            }
            is ViewState.Loading -> {
                viewBinding.loader.show()
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                scanWifiDevicesFragmentViewModel.scanWifiDevices()
            } else {
                Log.d(tag, "permission denied")
            }
        }
}

