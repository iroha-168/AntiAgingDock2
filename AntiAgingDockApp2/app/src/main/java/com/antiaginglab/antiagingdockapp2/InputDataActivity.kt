package com.antiaginglab.antiagingdockapp2

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.antiaginglab.antiagingdockapp2.databinding.ActivityInputDataBinding
import com.google.android.gms.common.util.CollectionUtils.listOf
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter

/*
CSVファイルにデータを書き込み、内部ストレージに保存する
 */
class InputDataActivity : AppCompatActivity(), ToolBarCustomViewDelegate {

    // viewModelの初期化
    private val viewModel by viewModels<ConfirmViewModel>()
    // bindingクラスをlateinit varで宣言
    private lateinit var binding: ActivityInputDataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // bindingの初期化とsetContentViewを行う
        binding = ActivityInputDataBinding.inflate(layoutInflater)
            .apply { setContentView(this.root) }

        // TODO: デフォルトのアクションバーを非表示にする
        supportActionBar?.hide()

        // TODO: カスタムツールバーを設置
        /*
         * setCustomToolBar()
         * 表示するツールバーの設定や配置を行うメソッド
         */
        setCustomToolBar()

        // TODO: editTextに入力された値を受け取る
        var name = "アンミカ"
        var height = "171.0"
        var weight = "58.0"
        val patientsDataList = listOf(name, height, weight)

        // ファイルが存在するか確認
        val filePath = "/data/data/com.antiaginglab.antiagingdockapp2/files/patients_data.csv"
        val csvFile = File(filePath)
        if (csvFile.exists()) {
            Log.d("TAG1", "ファイルが存在します")
            addToFile(patientsDataList)
        } else {
            Log.d("TAG2", "ファイルが存在しません")
            createFile(patientsDataList)
        }

        // viewModelを呼び出す
        viewModel.saveToFirebase(csvFile)
    }


    // ===== ファイルが存在しない場合、ファイルを作成して書き込み =====
    private fun createFile(patientsDataList: List<String>) {
        // 出力ファイルの作成
        val file = File(applicationContext.filesDir, "patients_data.csv")
        val fw = FileWriter(file, false)
        val pw = PrintWriter(BufferedWriter(fw))

        // ヘッダーの指定
        pw.print("名前")
        pw.print(",")
        pw.print("身長")
        pw.print(",")
        pw.print("体重")
        pw.println()

        // データを書き込む
        val listSize = patientsDataList.size
        for (i in 0 until listSize) {
            if(i == listSize-1) {
                pw.print(patientsDataList[listSize - 1])
                pw.println()
            } else {
                pw.print(patientsDataList[i])
                pw.print(",")
            }
        }

        // ファイルを閉じる
        pw.close()
    }

    // ===== ファイルが存在する場合、ファイルに追記 =====
    private fun addToFile(patientsDataList: List<String>) {
        // 出力ファイルの作成
        val file = File(applicationContext.filesDir, "patients_data.csv")
        val fw = FileWriter(file, true)
        val pw = PrintWriter(BufferedWriter(fw))

        // データを書き込む
        val listSize = patientsDataList.size
        for (i in 0 until listSize) {
            if(i == listSize-1) {
                pw.print(patientsDataList[listSize - 1])
                pw.println()
            } else {
                pw.print(patientsDataList[i])
                pw.print(",")
            }
        }

        // ファイルを閉じる
        pw.close()
    }

    // ===== setCustomToolBar()を実装 =====
    private fun setCustomToolBar() {
        val toolBarCustomView = ToolBarCustomView(this)
        toolBarCustomView.delegate = this

        val title = getString(R.string.title_tool_bar)
        toolBarCustomView.configure(title, false, false)

        // カスタムツールバーを挿入するコンテナ(入れ物)を指定
        val layout: LinearLayout = findViewById(R.id.container_for_toolbar)
        // ツールバーの表示をコンテナに合わせる
        toolBarCustomView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        )
        // カスタムツールバーを表示する
        layout.addView(toolBarCustomView)
    }

    override fun onClickedLeftButton() {
        // 前の画面に戻る
        finish()
    }

    override fun onClickedRightButton() {
        // TODO: メニューを表示
        // TODO: メニュークリックでConfirmActivityに画面遷移
    }
}




