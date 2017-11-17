package com.example.phucloc.carcontrol

import android.widget.Toast
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.util.*


/**
 * Created by Phuc Loc on 11/16/2017.
 */
//Background Thread to handle BlueTooth connecting
class ConnectBlueToothDeviceThread(private val bluetoothDevice: BluetoothDevice, private val myUUID: UUID, private val listener: OnRunInUIListener) : Thread() {

    private var bluetoothSocket: BluetoothSocket? = null
    private val TAG = "ConnectBTDeviceThread"

    interface OnRunInUIListener {
        fun onNeedToast(message: String)
        fun startThreadConnected(bluetoothSocket: BluetoothSocket)
    }

    init {
        try {
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(this.myUUID)
            this.listener.onNeedToast("bluetoothSocket: \n" + bluetoothSocket!!)
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

    }

    override fun run() {
        try {
            bluetoothSocket!!.connect()
            //connect successful
            val msgconnected = ("connect successful:\n"
                    + "BluetoothSocket: " + bluetoothSocket + "\n"
                    + "BluetoothDevice: " + bluetoothDevice)
            kotlin.run {
                this.listener.onNeedToast(msgconnected)
            }
            this.listener.startThreadConnected(bluetoothSocket!!)
        } catch (e: IOException) {
            e.printStackTrace()

            val eMessage = e.message
            kotlin.run {
                this.listener.onNeedToast("something wrong bluetoothSocket.connect(): \n" + eMessage)
            }

            try {
                bluetoothSocket!!.close()
            } catch (e1: IOException) {
                // TODO Auto-generated catch block
                e1.printStackTrace()
            }

        }
    }

    fun cancel() {
        this.listener.onNeedToast("close bluetoothSocket")
        try {
            bluetoothSocket!!.close()
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

    }

}
