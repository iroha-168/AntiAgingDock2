package com.antiaginglab.antiagingdockapp2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.antiaginglab.antiagingdockapp2.databinding.ActivityConfirmBinding

/*
内部ストレージに保存しているCSVファイルをFirebaseのstorageに登録する
 */
class ConfirmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfirmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityConfirmBinding.inflate(layoutInflater)
                .apply { setContentView(this.root) }


    }
}