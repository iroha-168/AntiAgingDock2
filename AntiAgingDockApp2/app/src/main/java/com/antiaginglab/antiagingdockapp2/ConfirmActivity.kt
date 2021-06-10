package com.antiaginglab.antiagingdockapp2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.antiaginglab.antiagingdockapp2.databinding.ActivityConfirmBinding

/*
内部ストレージに保存しているCSVファイルをFirebaseのstorageに登録する
 */
class ConfirmActivity : AppCompatActivity() {

    private val viewModel by viewModels<ConfirmViewModel>()
    private lateinit var binding: ActivityConfirmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityConfirmBinding.inflate(layoutInflater)
                .apply { setContentView(this.root) }

        // TODO: ストレージに保存するのを許可するボタンをタップするとviewModelを呼び出す
        // TODO: InputDataActivityで作成したFileNameをこの画面で受け取り、ViewModelに渡す
//        viewModel.saveToFirebase(csvFile)

    }
}