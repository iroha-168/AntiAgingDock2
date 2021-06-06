package com.antiaginglab.antiagingdockapp2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

/*
内部ストレージに保存しているCSVファイルをFirebaseのstorageに登録する
 */
class ConfirmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm)
    }
}