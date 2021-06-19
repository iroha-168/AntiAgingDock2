package com.antiaginglab.antiagingdockapp2

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.antiaginglab.antiagingdockapp2.databinding.ActivityInputDataBinding
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/*
CSVファイルにデータを書き込み、内部ストレージに保存する
 */
class InputDataActivity : AppCompatActivity(), ToolBarCustomViewDelegate {

    private var fileName = ""
    private lateinit var binding: ActivityInputDataBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // bindingの初期化とsetContentViewを行う
        binding = ActivityInputDataBinding.inflate(layoutInflater)
            .apply { setContentView(this.root) }

        // デフォルトのアクションバーを非表示にする
        supportActionBar?.hide()

        // カスタムツールバーを設置
        setCustomToolBar()

        // ファイル名を生成
        fileName = makeFileName()

        // 送信ボタンをクリックした時の処理
        binding.btnSend.setOnClickListener {

            val patientsDataList = getAllInputData(binding.containerForEditText)

            val filePath = "/data/data/com.antiaginglab.antiagingdockapp2/files/${fileName}"
            val csvFile = File(filePath)
            if (csvFile.exists()) {
                addToFile(patientsDataList)
            } else {
                createFile(patientsDataList)
            }

            // editTextに入力された値をクリアする
            clearForm(binding.containerForEditText)

            // トースト表示
            // TODO: お年寄りの方が確認出来やすいように、大きめの画像をトーストとして表示させるようにする
            val tst = Toast.makeText(this, "送信しました", Toast.LENGTH_LONG)
            tst.setGravity(Gravity.CENTER, 0, 0)
            tst.show()
        }
    }


    override fun onClickedLeftButton(){ }

    override fun onClickedRightButton() {
        var menu = findViewById<View>(R.id.btn_right)
        val popup = PopupMenu(this, menu)
        menuInflater.inflate(R.menu.main_menu, popup.getMenu())
        popup.show()

        popup.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.menu_item_complete -> {
                    // メニューがクリックされたらファイル名を保持したまま確認画面へ画面遷移
                    val intent = Intent(this, ConfirmActivity::class.java)
                    intent.putExtra("FILE_NAME", fileName)
                    startActivity(intent)
                }
            }
            false
        }
    }


    // ===== ファイルが存在しない場合、ファイルを作成して書き込み =====
    private fun createFile(patientsDataList: MutableList<String>) {
        // 出力ファイルの作成
        val file = File(applicationContext.filesDir, fileName)
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
    private fun addToFile(patientsDataList: MutableList<String>) {
        // 出力ファイルの作成
        val file = File(applicationContext.filesDir, fileName)
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

    // ===== ファイル名を生成 =====
    @RequiresApi(Build.VERSION_CODES.O)
    private fun makeFileName(): String {
        val date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val rand = (0..1000).random().toString()

        return "patients_data_" + date + "_" + rand + ".csv"
    }

    // ===== setCustomToolBar()を実装 =====
    private fun setCustomToolBar() {
        val toolBarCustomView = ToolBarCustomView(this)
        toolBarCustomView.delegate = this

        val title = getString(R.string.title_tool_bar)
        toolBarCustomView.configure(title, true, false)

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

    // ===== editTextに入力された値をクリアにする =====
    private fun clearForm(group: ViewGroup) {
        val count = group.childCount
        for (i in 0 until count) {
            val view = group.getChildAt(i)
            if (view is EditText) {
                view.setText("")
            }

            if (view is ViewGroup && view.childCount > 0) {
                clearForm(view)
            }
        }
    }

    // ===== editTextに入力された値を全て取得する =====
    private fun getAllInputData(group: ViewGroup): MutableList<String> {
        var editDataList = mutableListOf<String>()

        group.children.forEachIndexed { i, view ->
            if (view is EditText) {
                Log.d("TAG", view.text.toString())
                editDataList.add(i, view.text.toString())
            }
        }
        return editDataList
    }
}