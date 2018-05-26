package com.example.phucloc.carcontrol

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : FragmentActivity(), View.OnClickListener, DataConnectedThread.OnRunInUIListener, ConnectBlueToothDeviceThread.OnRunInUIListener {

    private lateinit var manualFragment: ManualFragment
    private lateinit var gyroFragment: GyroFragment
    private val MODE_MANUAL = 0
    private val MODE_GYRO = 1
    private var currentMode = MODE_MANUAL

    companion object {
        lateinit var dataThread: DataConnectedThread
        var isConnected = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        manualFragment = ManualFragment()
        gyroFragment = GyroFragment()

        initEvent()

        if (savedInstanceState == null) {
            changeMode(MODE_GYRO)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if (outState != null) {
            outState.putInt("mode", currentMode)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState != null) {
            currentMode = savedInstanceState.getInt("mode")
            changeMode(currentMode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ManualFragment.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val device = data.extras.getParcelable<BluetoothDevice>(ManualFragment.EXTRA_DEVICE)
                Toast.makeText(this, device.name, Toast.LENGTH_SHORT).show()
                this.startThread(device)
            }
        }
    }

    private fun initEvent() {
        btn_manual.setOnClickListener(this)
        btn_gyro.setOnClickListener(this)
        btn_scan_device.setOnClickListener(this)
    }

    private fun changeMode(mode: Int) {
        currentMode = mode

        btn_manual.setBackgroundResource(R.drawable.border_button)
        btn_manual.setTextColor(resources.getColor(R.color.colorPrimary))

        btn_gyro.setBackgroundResource(R.drawable.border_button)
        btn_gyro.setTextColor(resources.getColor(R.color.colorPrimary))

        if (mode == MODE_MANUAL) {
            btn_manual.setBackgroundResource(R.drawable.fill_button)
            btn_manual.setTextColor(Color.BLACK)
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            supportFragmentManager.beginTransaction().replace(main_frame.id, manualFragment).addToBackStack(null).commit()
        } else if (mode == MODE_GYRO) {
            btn_gyro.setBackgroundResource(R.drawable.fill_button)
            btn_gyro.setTextColor(Color.BLACK)
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            supportFragmentManager.beginTransaction().replace(main_frame.id, gyroFragment).addToBackStack(null).commit()
        }
    }

    fun startThread(device: BluetoothDevice) {
        val uuid = device.uuids[0].uuid
        val thread = ConnectBlueToothDeviceThread(device, uuid, this)
        thread.start()
    }

    override fun startThreadConnected(bluetoothSocket: BluetoothSocket) {
        dataThread = DataConnectedThread(bluetoothSocket, this)
        dataThread.start()
        isConnected = true
    }

    override fun onNeedToast(message: String) {
        this.runOnUiThread() {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClick(v: View?) {
        if (v == btn_manual) {
            changeMode(MODE_MANUAL)
        } else if (v == btn_gyro) {
            changeMode(MODE_GYRO)
        } else if (v == btn_scan_device) {
            val intent = Intent(this, DeviceListActivity::class.java)
            startActivityForResult(intent, ManualFragment.REQUEST_CODE)
        }
    }
}
