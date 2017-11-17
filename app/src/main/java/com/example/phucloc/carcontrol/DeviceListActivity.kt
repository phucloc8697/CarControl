package com.example.phucloc.carcontrol

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_device_list.*

class DeviceListActivity : AppCompatActivity(), DeviceAdapter.OnItemClickListener {

    private var bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private var deviceList: ArrayList<BluetoothDevice> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_list);
        rclv_device.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        btn_scan.setOnClickListener {
            this.scanDevice();
        }
    }

    override fun onResume() {
        super.onResume()
        this.checkBluetoothEnable();
        this.scanDevice();
    }

    private fun checkBluetoothEnable() {
        if (!bluetoothAdapter.isEnabled()) {
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(intent, 0)

        }
    }

    private fun scanDevice() {
        val pairedDevices = bluetoothAdapter.bondedDevices
        this.deviceList.clear()
        for (device in pairedDevices) {
            this.deviceList.add(device)
        }
        val adapter = DeviceAdapter(this.deviceList, this)
        rclv_device.adapter = adapter
    }

    override fun onItemClick(device: BluetoothDevice) {
        val intent = Intent()
        intent.putExtra(MainActivity.EXTRA_DEVICE, device)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
