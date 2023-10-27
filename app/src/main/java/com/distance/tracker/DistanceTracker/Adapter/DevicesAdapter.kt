package com.distance.tracker.DistanceTracker.Adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.distance.tracker.DistanceTracker.Model.WifiDevice
import com.distance.tracker.Notes.Model.Note
import com.distance.tracker.R
import com.distance.tracker.Utils.OnItemClickListener

class DevicesAdapter (private val mDevices: List<WifiDevice>) : RecyclerView.Adapter<DevicesAdapter.ViewHolder>()
{
    private lateinit var mListener: OnItemClickListener
    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    inner class ViewHolder(itemView: View, listener: OnItemClickListener) : RecyclerView.ViewHolder(itemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val tvDeviceName: TextView = itemView.findViewById(R.id.tv_device_name)
        val tvMacAddress: TextView = itemView.findViewById(R.id.tv_mac_address)
        val viewRttStatus: View = itemView.findViewById(R.id.view_rtt_status)
        val tvDistance: TextView = itemView.findViewById(R.id.tv_distance)
        val cvDevice: CardView = itemView.findViewById(R.id.cv_device)
        init {
            cvDevice.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DevicesAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.device_row, parent, false)
        // Return a new holder instance
        return ViewHolder(contactView, mListener)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(viewHolder: DevicesAdapter.ViewHolder, position: Int) {
        // Get the data model based on position
        val wifiDevice: WifiDevice = mDevices[position]
        // Set item views based on your views and data model
        viewHolder.tvDeviceName.text = wifiDevice.name
        viewHolder.tvMacAddress.text = if(wifiDevice.macAddress != null) wifiDevice.macAddress.toString() else ""
        viewHolder.tvDistance.text = if(wifiDevice.distance != null) wifiDevice.distance.toString() else "- -"
        val rttColor = if(wifiDevice.isSupportRtt) R.color.green else R.color.red
        viewHolder.viewRttStatus.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(viewHolder.viewRttStatus.context, rttColor))
    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return mDevices.size
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        mListener = listener
    }

}