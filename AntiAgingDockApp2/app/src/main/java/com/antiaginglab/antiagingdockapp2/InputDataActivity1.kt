package com.antiaginglab.antiagingdockapp2

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.children
import com.antiaginglab.antiagingdockapp2.databinding.ActivityInputData1Binding
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.Pair

class InputDataActivity1 : AppCompatActivity(), ToolBarCustomViewDelegate {

    private var fileName = ""
    private var checkBoxList: MutableList<String> = mutableListOf()  // チェックボックスの結果を格納するためのリスト
    private var allInfoList: MutableList<String> = mutableListOf() // 全てのアンケート結果を格納するためのリスト

    private lateinit var binding: ActivityInputData1Binding
    private lateinit var questionAdapter: QuestionAdapter
    private lateinit var questionList: ArrayList<String>


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInputData1Binding.inflate(layoutInflater)
                .apply { setContentView(this.root) }

        // デフォルトのアクションバーを非表示にする
        supportActionBar?.hide()

        // カスタムツールバーの設置
        setCustomToolBar()

        // アンケート項目のデータリストを作成
        questionList = arrayListOf(
                "目が疲れる",
                "目がかすむ",
                "肩がこる",
                "眼痛",
                "筋肉痛・こり",
                "動悸",
                "息切れ",
                "太りやすい",
                "るいそう・やせ",
                "だるい",
                "健康感がない",
                "口渇",
                "肌の不調",
                "食欲不振",
                "胃が張る",
                "胃痛",
                "風邪をひきやすい",
                "咳や痰",
                "下痢",
                "便秘",
                "白髪",
                "抜け毛"
        )

        // 作成したリストをアダプターにセット
        questionAdapter = QuestionAdapter(this, questionList)
        binding.lvQuestion.adapter = questionAdapter

        // ファイル名を生成
        fileName = makeFileName()

        // 送信ボタンをクリックした時の処理
        binding.btnSend1.setOnClickListener {
            // FIXME: 患者の基本情報を取得
            val patientsBasicInfo = getAllInputData(binding.containerForBasicInfo)
            // FIXME: 患者の基本情報のバリデーションが正確に判断されるように
            val haveAllData = editTextValidation(patientsBasicInfo)

            // TODO: アンケート情報(チェックボックスに入力された値)を取得
            for (i in 0 until binding.lvQuestion.adapter.count) {
                var question = binding.lvQuestion.adapter.getItem(i)
                var view = binding.lvQuestion.adapter.getView(i, binding.lvQuestion.getChildAt(i), binding.lvQuestion)
                var checkbox1 = view!!.findViewById<CheckBox>(R.id.checkbox_1)
                var checkbox2 = view!!.findViewById<CheckBox>(R.id.checkbox_2)
                var checkbox3 = view!!.findViewById<CheckBox>(R.id.checkbox_3)
                var checkbox4 = view!!.findViewById<CheckBox>(R.id.checkbox_4)
                var checkbox5 = view!!.findViewById<CheckBox>(R.id.checkbox_5)

                val checkboxMap : Map<String, CheckBox> = mapOf("1" to checkbox1, "2" to checkbox2, "3" to checkbox3, "4" to checkbox4, "5" to checkbox5)

                // trueの結果を番号として取得する
                // TODO: アンケート情報(チェックボックス)のバリデーションチェック
                val resultOfCheckBoxValidation: Pair<Boolean, String> = checkBoxValidation(checkboxMap, question)
                val isSelectedOneAns = resultOfCheckBoxValidation.first
                val selectedAns = resultOfCheckBoxValidation.second

                // TODO: チェックボックスが１つだけ選択されている場合
                if (isSelectedOneAns) {
                    checkBoxList.add(selectedAns)
                }
            }


            //FIXME: 患者の基本情報とアンケート情報の両方のバリデーション結果がtrueの場合ファイル作成
            if (haveAllData) {
                val filePath = "/data/data/com.antiaginglab.antiagingdockapp2/files/${fileName}"
                val csvFile = File(filePath)

                if (csvFile.exists()) {
                    // FIXME: 患者の基本情報とアンケート結果をcsvファイルに入力
                    addToFile(patientsBasicInfo)
                } else {
                    // FIXME: 患者の基本情報とアンケート結果をcsvファイルに入力
                    createFile(patientsBasicInfo)
                }

                // TODO: チェックボックスに入力された値をクリアにする

                // editTextに入力された値をクリアする
                clearForm(binding.containerForBasicInfo)

                // トースト表示
                showToast(this, R.drawable.toast_ok)
            }
        }

    }

    // ============= ツールバーの設定 =============
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

    private fun setCustomToolBar() {
        val toolBarCustomView = ToolBarCustomView(this)
        toolBarCustomView.delegate = this

        val title = getString(R.string.title_tool_bar)
        toolBarCustomView.configure(title, true, false)

        // カスタムツールバーを挿入するコンテナ(入れ物)を指定
        val layout: LinearLayout = binding.containerForToolBar
        // ツールバーの表示をコンテナに合わせる
        toolBarCustomView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        )
        // カスタムツールバーを表示する
        layout.addView(toolBarCustomView)
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

    // ===== 全てのeditTextが入力されているかチェック =====
    private fun editTextValidation(patientsDataList: MutableList<String>): Boolean {
        val listSize = patientsDataList.size

        for (i in 0 until listSize) {
            if (patientsDataList[i].isEmpty()) {
                showToast(this, R.drawable.toast_ng_kuma_w_trans)
                return false
            }
        }
        return true
    }

    // ===== チェックボックスが1つだけ選択されているかチェック =====
    private fun checkBoxValidation(checkboxMap: Map<String, CheckBox>, question: Any): Pair<Boolean, String> {
        var checkboxNum = ""
        var numOfTrue = 0

        checkboxMap.forEach { (num, checkbox) ->
            checkboxNum = num

            // FIXME: チェックボックスの正しいチェック状態を取得できない
            Log.d("CHECKBOX", checkbox.toString())
            Log.d("STATUS", checkbox.isChecked.toString())

            if(checkbox.isChecked) {
                numOfTrue ++
                if (numOfTrue >= 2) {
                    // TODO: １行につき2つ以上trueがある場合(２つ選択がある)
                    Log.d("TAG", "「${question}」の項目で2つ回答を選択していませんか？")    // TODO: 最終的にはトーストを表示
                    return Pair(false, "null")
                } else if (numOfTrue == 0) {
                    // TODO: １行につき１つもtrueがない場合(1つも選択されていない)
                    Log.d("TAG", "「${question}」の項目で当てはまる項目を1つ選択していますか？")    // TODO: 最終的にはトーストを表示
                    return Pair(false, "null")
                }
            }
        }
        return Pair(true, checkboxNum)
    }

    // ===== 画像のトーストを表示する =====
    private fun showToast(cont: Context, res: Int) {

        val view = layoutInflater.inflate(R.layout.toast_layout, null)

        val tst = Toast.makeText(cont, "", Toast.LENGTH_SHORT)
        tst.setGravity(Gravity.CENTER, 0, -100)
        tst.setView(view)

        // トーストの画像を変更
        val img = view.findViewById<ImageView>(R.id.img_toast)
        img.setImageResource(res)

        // 表示
        tst.show()
    }

}