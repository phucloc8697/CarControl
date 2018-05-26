package com.example.phucloc.carcontrol

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_manual.view.*

class ManualFragment : Fragment(), DataConnectedThread.OnRunInUIListener, ConnectBlueToothDeviceThread.OnRunInUIListener, View.OnTouchListener {

    private lateinit var dataThread: DataConnectedThread
    private var isConnected = false
    private lateinit var rootView: View

    companion object {
        fun newInstance(): ManualFragment {
            return ManualFragment()
        }
        val REQUEST_CODE = 99
        var EXTRA_DEVICE = "device"
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_manual, container, false)
        // Example of a call to a native method
        rootView.ic_up.setOnTouchListener(this)
        rootView.ic_down.setOnTouchListener(this)
        rootView.ic_left.setOnTouchListener(this)
        rootView.ic_right.setOnTouchListener(this)
        rootView.ic_speaker.setOnTouchListener(this)
        rootView.btn_scan_device.setOnClickListener {
            val intent = Intent(activity, DeviceListActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }
        rootView.ic_light.setOnClickListener {
            if (this.isConnected) {
                if (rootView.ic_light.alpha == 0.5f) {
                    rootView.ic_light.alpha = 1f
                    this.dataThread.write("7".toByteArray())
                } else {
                    rootView.ic_light.alpha = 0.5f
                    this.dataThread.write("8".toByteArray())
                }
            }
        }
        return rootView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val device = data.extras.getParcelable<BluetoothDevice>(EXTRA_DEVICE)
                Toast.makeText(activity, device.name, Toast.LENGTH_SHORT).show()
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
        activity.runOnUiThread() {
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun startThreadConnected(bluetoothSocket: BluetoothSocket) {
        this.dataThread = DataConnectedThread(bluetoothSocket, this)
        this.dataThread.start()
        this.isConnected = true
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        if (this.isConnected) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    when (view) {
                        rootView.ic_up -> this.dataThread.write(Constant.COMMAND_FORWARD.toByteArray())
                        rootView.ic_down -> this.dataThread.write(Constant.COMMAND_BACKWARD.toByteArray())
                        rootView.ic_left -> this.dataThread.write(Constant.COMMAND_LEFT.toByteArray())
                        rootView.ic_right -> this.dataThread.write(Constant.COMMAND_RIGHT.toByteArray())
                    }
                }
                MotionEvent.ACTION_UP -> {
                    when (view) {
                        rootView.ic_up -> this.dataThread.write("0".toByteArray())
                        rootView.ic_down -> this.dataThread.write("0".toByteArray())
                        rootView.ic_left -> this.dataThread.write("0".toByteArray())
                        rootView.ic_right -> this.dataThread.write("0".toByteArray())
                    }
                }
            }
        }
        return true
    }
}
