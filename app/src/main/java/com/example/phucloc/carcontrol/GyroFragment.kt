package com.example.phucloc.carcontrol

import android.content.Context.SENSOR_SERVICE
import android.graphics.drawable.GradientDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_gyro.*

class GyroFragment : Fragment(), SensorEventListener {

    private lateinit var rootView: View
    private lateinit var rotationSensor: Sensor
    private lateinit var sensorManager: SensorManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_gyro, container, false)

        initSensor()
        return rootView
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    private fun initSensor() {
        sensorManager = this.activity.getSystemService(SENSOR_SERVICE) as SensorManager
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null && MainActivity.isConnected) {
            val rotationMatrix = FloatArray(16)
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
            val remappedRotationMatrix = FloatArray(16)
            SensorManager.remapCoordinateSystem(rotationMatrix,
                    SensorManager.AXIS_X,
                    SensorManager.AXIS_Z,
                    remappedRotationMatrix)
            val orientations = FloatArray(3)
            SensorManager.getOrientation(remappedRotationMatrix, orientations)
            for (i in 0..2) {
                orientations[i] = Math.toDegrees(orientations[i].toDouble()).toFloat()
            }
            processOrientation(orientations)
        }
    }

    private fun processOrientation(orientations: FloatArray) {
        val y = orientations[1]
        val z = orientations[2]

        if (z < -45) {
            txt_status.setText("Forward")
            MainActivity.dataThread.write(Constant.COMMAND_FORWARD.toByteArray())
        } else if (z > 45) {
            txt_status.setText("Backward")
            MainActivity.dataThread.write(Constant.COMMAND_BACKWARD.toByteArray())
        } else if (y < -45) {
            txt_status.setText("Left")
            MainActivity.dataThread.write(Constant.COMMAND_LEFT.toByteArray())
        } else if (y > 45) {
            txt_status.setText("Right")
            MainActivity.dataThread.write(Constant.COMMAND_RIGHT.toByteArray())
        } else {
            txt_status.setText("Stop")
            MainActivity.dataThread.write(Constant.COMMAND_STOP.toByteArray())
        }
    }
}
