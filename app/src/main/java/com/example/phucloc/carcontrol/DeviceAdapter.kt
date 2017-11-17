package com.example.phucloc.carcontrol

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

/**
 * Created by Phuc Loc on 11/15/2017.
 */

class DeviceAdapter(val list: ArrayList<BluetoothDevice>, listener: OnItemClickListener) : RecyclerView.Adapter<DeviceAdapter.ViewHolder>() {

    var listener: OnItemClickListener

    init {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(device: BluetoothDevice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_device, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceAdapter.ViewHolder, position: Int) {
        holder.bindingItems(this.list[position], this.listener)
    }

    override fun getItemCount(): Int {
        return this.list.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bindingItems(device: BluetoothDevice, listener: OnItemClickListener) {
            val txtName = itemView.findViewById<TextView>(R.id.txt_name)
            txtName.text = device.name
            itemView.setOnClickListener {
                listener.onItemClick(device)
            }
        }
    }

}