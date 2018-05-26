package com.example.phucloc.carcontrol

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : FragmentActivity(), View.OnClickListener {

    private lateinit var manualFragment: ManualFragment
    private val MODE_MANUAL = 0
    private val MODE_GYRO = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        changeMode(0)
        manualFragment = ManualFragment()
        supportFragmentManager.beginTransaction().add(main_frame.id, manualFragment).commit()

        initEvent()
    }

    private fun initEvent() {
        btn_manual.setOnClickListener(this)
        btn_gyro.setOnClickListener(this)
    }

    private fun changeMode(mode: Int) {
        btn_manual.setBackgroundResource(R.drawable.border_button)
        btn_manual.setTextColor(resources.getColor(R.color.colorPrimary))

        btn_gyro.setBackgroundResource(R.drawable.border_button)
        btn_gyro.setTextColor(resources.getColor(R.color.colorPrimary))

        if (mode == MODE_MANUAL) {
            btn_manual.setBackgroundResource(R.drawable.fill_button)
            btn_manual.setTextColor(Color.BLACK)
        } else if (mode == MODE_GYRO) {
            btn_gyro.setBackgroundResource(R.drawable.fill_button)
            btn_gyro.setTextColor(Color.BLACK)
        }
    }

    override fun onClick(v: View?) {
        if (v == btn_manual) {
            changeMode(MODE_MANUAL)
        } else if (v == btn_gyro) {
            changeMode(MODE_GYRO)
        }
    }
}
