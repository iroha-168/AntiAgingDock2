package com.antiaginglab.antiagingdockapp2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.viewModels
import com.antiaginglab.antiagingdockapp2.databinding.ActivityConfirmBinding

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

        // TODO: ツールバーの表示
        setCustomToolBar()

        // 「いいえ」ボタン押下で前の画面に戻る
        binding.btnNo.setOnClickListener {
            finish()
        }

        // 「はい」ボタン押下でFirebase Storageにcsvファイルを保存する
        binding.btnYes.setOnClickListener {
            // TODO: ストレージに保存するのを許可するボタンをタップするとviewModelを呼び出す
            // TODO: InputDataActivityで作成したFileNameをこの画面で受け取り、ViewModelに渡す
            // viewModel.saveToFirebase(csvFile)

            // TODO: CompleteAvtivityに画面遷移
        }
    }

    override fun onClickedLeftButton() {}

    override fun onClickedRightButton() {}

    // ===== setCustomToolBar()を実装 =====
    private fun setCustomToolBar() {
        val toolBarCustomView = ToolBarCustomView(this)
        toolBarCustomView.delegate = this

        val title = getString(R.string.title_tool_bar)
        toolBarCustomView.configure(title, true, true)

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