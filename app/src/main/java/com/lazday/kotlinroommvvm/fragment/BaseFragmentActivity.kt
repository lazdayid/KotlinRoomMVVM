package com.lazday.kotlinroommvvm.fragment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lazday.kotlinroommvvm.R

class BaseFragmentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_fragment)
    }
}