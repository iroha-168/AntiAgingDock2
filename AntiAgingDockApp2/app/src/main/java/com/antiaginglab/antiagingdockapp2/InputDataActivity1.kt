package com.antiaginglab.antiagingdockapp2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.antiaginglab.antiagingdockapp2.databinding.ActivityInputData1Binding

class InputDataActivity1 : AppCompatActivity() {

    private lateinit var binding: ActivityInputData1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInputData1Binding.inflate(layoutInflater)
                .apply { setContentView(this.root) }

        
    }
}