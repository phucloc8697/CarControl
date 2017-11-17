package com.example.phucloc.carcontrol

import android.bluetooth.BluetoothSocket
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import java.util.logging.Handler


/**
 * Created by Phuc Loc on 11/16/2017.
 */
/*
    ThreadConnected:
    Background Thread to handle Bluetooth data communication
    after connected
     */
class DataConnectedThread(private val connectedBluetoothSocket: BluetoothSocket, private val listener: OnRunInUIListener) : Thread() {
    private val connectedInputStream: InputStream?
    private val connectedOutputStream: OutputStream?

    interface OnRunInUIListener {
        fun onNeedToast(message: String)
    }

    init {
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null

        try {
            inputStream = connectedBluetoothSocket.inputStream
            outputStream = connectedBluetoothSocket.outputStream
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        connectedInputStream = inputStream
        connectedOutputStream = outputStream
    }

    override fun run() {
        val buffer = ByteArray(1024)
        var bytes: Int
        while (true) {
            try {
                bytes = connectedInputStream!!.read(buffer)
                val strReceived = String(buffer, 0, bytes)
                val msgReceived = (bytes.toString() +
                        " bytes received:\n"
                        + strReceived)
                this.listener.onNeedToast(msgReceived)

            } catch (e: IOException) {
                // TODO Auto-generated catch block
                e.printStackTrace()

                val msgConnectionLost = "Connection lost:\n" + e.message
                this.listener.onNeedToast(msgConnectionLost)
                break
            }
            sleep(3000)
        }
    }

    fun write(buffer: ByteArray) {
        try {
            connectedOutputStream!!.write(buffer)
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

    }

    fun cancel() {
        try {
            connectedBluetoothSocket.close()
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

    }
}