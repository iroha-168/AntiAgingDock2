package com.antiaginglab.antiagingdockapp2

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
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

            // トースト表示
            showToast(this)
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

        val title = getString(R.string.title_confirm_activity)
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

    // ===== 画像のトーストを表示する =====
    private fun showToast(cont: Context) {
        val view = layoutInflater.inflate(R.layout.toast_layout, null)

        val tst = Toast.makeText(cont, "", Toast.LENGTH_SHORT)
        tst.setGravity(Gravity.CENTER, 0, -100)
        tst.setView(view)

        // トーストの画像を変更
        val img = view.findViewById<ImageView>(R.id.img_toast)
        img.setImageResource(R.drawable.toast_neko_w_trans)

        // 表示
        tst.show()
    }
}