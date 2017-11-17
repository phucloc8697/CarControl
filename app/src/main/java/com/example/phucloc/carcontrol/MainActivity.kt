package com.example.phucloc.carcontrol

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_device_list.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), DataConnectedThread.OnRunInUIListener, ConnectBlueToothDeviceThread.OnRunInUIListener, View.OnTouchListener {

    private lateinit var dataThread: DataConnectedThread
    private var isConnected = false

    companion object {
        val REQUEST_CODE = 99
        var EXTRA_DEVICE = "device"
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Example of a call to a native method
        ic_up.setOnTouchListener(this)
        ic_down.setOnTouchListener(this)
        ic_left.setOnTouchListener(this)
        ic_right.setOnTouchListener(this)
        ic_speaker.setOnTouchListener(this)
        btn_scan_device.setOnClickListener {
            val intent = Intent(this, DeviceListActivity::class.java)
            startActivityForResult(intent, MainActivity.REQUEST_CODE)
        }
        ic_light.setOnClickListener {
            if (this.isConnected) {
                if (ic_light.alpha == 0.5f) {
                    ic_light.alpha = 1f
                    this.dataThread.write("7".toByteArray())
                } else {
                    ic_light.alpha = 0.5f
                    this.dataThread.write("8".toByteArray())
                }
            }
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MainActivity.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val device = data.extras.getParcelable<BluetoothDevice>(MainActivity.EXTRA_DEVICE)
                Toast.makeText(this, device.name, Toast.LENGTH_SHORT).show()
                this.startThread(device)
            }
        }
    }

    fun startThread(device: BluetoothDevice) {
        val uuid = device.uuids[0].uuid
        val thread = ConnectBlueToothDeviceThread(device, uuid, this)
        thread.start()
    }

    override fun onNeedToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun startThreadConnected(bluetoothSocket: BluetoothSocket) {
        this.dataThread = DataConnectedThread(bluetoothSocket, this)
        dataThread.start()
        this.isConnected = true
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        if (this.isConnected) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    when (view) {
                        ic_up -> this.dataThread.write("1".toByteArray())
                        ic_down -> this.dataThread.write("2".toByteArray())
                        ic_left -> this.dataThread.write("3".toByteArray())
                        ic_right -> this.dataThread.write("4".toByteArray())
                        ic_speaker -> this.dataThread.write("5".toByteArray())
                    }
                }
                MotionEvent.ACTION_UP -> {
                    when (view) {
                        ic_up -> this.dataThread.write("0".toByteArray())
                        ic_down -> this.dataThread.write("0".toByteArray())
                        ic_left -> this.dataThread.write("0".toByteArray())
                        ic_right -> this.dataThread.write("0".toByteArray())
                        ic_speaker -> this.dataThread.write("6".toByteArray())
                    }
                }
            }
        }
        return true
    }

}
