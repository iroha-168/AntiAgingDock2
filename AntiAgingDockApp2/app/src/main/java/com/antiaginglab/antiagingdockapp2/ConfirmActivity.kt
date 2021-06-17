package com.antiaginglab.antiagingdockapp2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import com.antiaginglab.antiagingdockapp2.databinding.ActivityConfirmBinding
import java.io.File

/*
内部ストレージに保存しているCSVファイルをFirebaseのstorageに登録する
 */
class ConfirmActivity : AppCompatActivity(), ToolBarCustomViewDelegate {

    private val viewModel by viewModels<ConfirmViewModel>()
    private lateinit var binding: ActivityConfirmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityConfirmBinding.inflate(layoutInflater)
                .apply { setContentView(this.root) }

        // デフォルトのアクションバーを非表示にする
        supportActionBar?.hide()

        // ツールバーの表示
        setCustomToolBar()

        // 「いいえ」ボタン押下で前の画面に戻る
        binding.btnNo.setOnClickListener {
            finish()
        }

        // 「はい」ボタン押下でFirebase Storageにcsvファイルを保存する
        binding.btnYes.setOnClickListener {

            val filename = intent.getStringExtra("FILE_NAME")
            val csvFile = File(application.filesDir, filename)
            viewModel.saveToFirebase(csvFile)

            val tst = Toast.makeText(this, "保存しました", Toast.LENGTH_LONG)
            tst.setGravity(Gravity.CENTER, 0, 0)
            tst.show()
        }
    }

    override fun onClickedLeftButton() {
        finish()
    }

    override fun onClickedRightButton() {}

    // ===== setCustomToolBar()を実装 =====
    private fun setCustomToolBar() {
        val toolBarCustomView = ToolBarCustomView(this)
        toolBarCustomView.delegate = this

        val title = getString(R.string.title_tool_bar)
        toolBarCustomView.configure(title, false, true)

        // カスタムツールバーを挿入するコンテナ(入れ物)を指定
        val layout: LinearLayout = binding.containerForToolbar
        // ツールバーの表示をコンテナに合わせる
        toolBarCustomView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        )
        // カスタムツールバーを表示する
        layout.addView(toolBarCustomView)
    }
}