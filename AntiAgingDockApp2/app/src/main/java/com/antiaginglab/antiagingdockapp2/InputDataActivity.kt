package com.antiaginglab.antiagingdockapp2

import android.app.DatePickerDialog
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
import androidx.appcompat.app.AlertDialog
import androidx.core.view.children
import androidx.fragment.app.FragmentActivity
import com.antiaginglab.antiagingdockapp2.databinding.ActivityInputDataBinding
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class InputDataActivity : AppCompatActivity(), ToolBarCustomViewDelegate, DatePickerDialog.OnDateSetListener {

    private var fileName = ""

    private var basicInfoList: MutableList<String> = mutableListOf()        // 「基本情報」の結果を格納するためのリスト
    private var radioBtnList: MutableList<String> = mutableListOf()         // 「からだの症状」と「こころの症状」の結果を格納するためのリスト
    private var lifestyleHabitList: MutableList<String> = mutableListOf()   // 「生活習慣」の結果を格納するためのリスト
    private var allInfoList: MutableList<String> = mutableListOf()          // 全てのアンケート結果を格納するためのリスト

    private lateinit var binding: ActivityInputDataBinding

    private lateinit var questionAdapter: QuestionAdapter
    private lateinit var questionAdapter2: QuestionAdapter

    private lateinit var questionList: ArrayList<String>
    private lateinit var questionList2: ArrayList<String>


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInputDataBinding.inflate(layoutInflater)
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
                "ふとりやすい",
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
                "抜け毛",
                "頭痛",
                "めまい",
                "耳鳴り",
                "腰痛",
                "関節痛",
                "むくみ",
                "汗をかきやすい",
                "頻尿",
                "のぼせ",
                "冷え性"
        )
        questionList2 = arrayListOf(
            "イライラする",
            "怒りっぽい",
            "意欲がわかない",
            "幸せと感じない",
            "生きがいがない",
            "日常生活が楽しくない",
            "自信を失った",
            "人と話すのが嫌",
            "憂うつ",
            "役に立つ人間でない",
            "眠りが浅い",
            "寝付きが悪い",
            "くよくよする",
            "ど忘れをする",
            "集中できない",
            "問題を解決できない",
            "容易に判断できない",
            "心配事でよく眠れない",
            "緊張感",
            "理由なく不安になる",
            "何か恐怖心を感じる"
        )

        // 作成したリストをアダプターにセット
        questionAdapter = QuestionAdapter(this, questionList)
        questionAdapter2 = QuestionAdapter(this, questionList2)

        binding.lvQuestion.adapter = questionAdapter
        binding.lvQuestion2.adapter = questionAdapter2

        // ファイル名を生成
        fileName = makeFileName()

        // 生年月日を入力するeditTextをクリックしたときの処理
        binding.editTxtBirthday.setOnClickListener {
            val datePicker = DatePicker(this)
            datePicker.calendarViewShown = false
            showDatePickerDialog(binding.editTxtBirthday)
        }

        // 送信ボタンをクリックした時の処理
        binding.btnSend1.setOnClickListener {

            // 送信処理を開始して良いかアラートダイアログで確認
            AlertDialog.Builder(this)
                .setTitle("この内容で送信しますか？")
                .setMessage("送信した内容を後から編集できません。")
                // アラートダイアログで「送信する」が押された場合
                .setPositiveButton("送信する"){ _, _ ->
                    // ============ 患者の基本情報を取得 ============
                    val idAndName = getAllInputData(binding.idAndNameContainer)
                    val haveIdAndName = editTextValidation(idAndName)
                    if (haveIdAndName){
                        basicInfoList.addAll(idAndName)
                    } else {
                        basicInfoList.clear()
                        return@setPositiveButton
                    }

                    val birthday = getAllInputData(binding.birthdayContainer)
                    val haveBirthday = editTextValidation(birthday)
                    if (haveBirthday) {
                        basicInfoList.addAll(birthday)
                    } else {
                        basicInfoList.clear()
                        return@setPositiveButton
                    }

                    val weightAndHeight = getAllInputData(binding.weightAndHeightContainer)
                    val haveWeightAndHeight = editTextValidation(weightAndHeight)
                    if (haveWeightAndHeight) {
                        basicInfoList.addAll(weightAndHeight)
                    } else {
                        basicInfoList.clear()
                        return@setPositiveButton
                    }

                    //「生活習慣」の結果を取得
                    val lifestyleHabitQuestion = getAllInputData(binding.lifestyleQuestionContainer)
                    val haveAllAnsOnLifestyleHabit = editTextValidation(lifestyleHabitQuestion)
                    if (haveAllAnsOnLifestyleHabit) {
                        lifestyleHabitList.addAll(lifestyleHabitQuestion)
                    } else {
                        basicInfoList.clear()
                        lifestyleHabitList.clear()
                        return@setPositiveButton
                    }

                    // ============ ラジオボタンを一行一行読み込む ============
                    //「からだの症状」の結果を取得
                    val questionAdapterBody = binding.lvQuestion
                    var isSelectedOneAnsOnBody = readEachRadioBtn(questionAdapterBody)
                    if (!isSelectedOneAnsOnBody) {
                        basicInfoList.clear()
                        radioBtnList.clear()
                        lifestyleHabitList.clear()

                        return@setPositiveButton
                    }

                    //「こころの症状」の結果を取得
                    val questionAdapterMental = binding.lvQuestion2
                    var isSelectedOneAnsOnMental = readEachRadioBtn(questionAdapterMental)
                    if (!isSelectedOneAnsOnMental) {
                        basicInfoList.clear()
                        radioBtnList.clear()
                        lifestyleHabitList.clear()

                        return@setPositiveButton
                    }

                    // 基本情報とアンケート結果をまとめる
                    allInfoList.addAll(basicInfoList)       // 基本情報の結果を追加
                    allInfoList.addAll(radioBtnList)        // 「からだの症状」と「こころの症状」の結果を追加
                    allInfoList.addAll(lifestyleHabitList)  // 「生活習慣」の結果を追加

                    // 患者の基本情報とアンケート情報の両方のバリデーション結果がtrueの場合ファイル作成
                    var isSuccess = false
                    if ( isSelectedOneAnsOnBody && isSelectedOneAnsOnMental && haveIdAndName && haveBirthday && haveWeightAndHeight && haveAllAnsOnLifestyleHabit) {
                        val filePath = "/data/data/com.antiaginglab.antiagingdockapp2/files/${fileName}"
                        val csvFile = File(filePath)
                        if (csvFile.exists()) {
                            // すでにファイルが存在する場合
                            isSuccess = addToFile(allInfoList)
                            if (isSuccess) {
                                // ファイルにデータを記録したら、リスト内のデータをクリアにする
                                basicInfoList.clear()
                                radioBtnList.clear()
                                lifestyleHabitList.clear()
                                allInfoList.clear()

                                // ラジオボタンの結果をクリアにする
                                clearRadioBtn()
                                // editTextに入力された値をクリアする
                                clearForm(binding.containerForBasicInfo)
                                clearForm(binding.lifestyleQuestionContainer)
                            }
                        } else {
                            // ファイルがない場合
                            isSuccess = createFile(allInfoList)
                            if (isSuccess) {
                                // ファイルにデータを記録したら、リスト内のデータをクリアにする
                                basicInfoList.clear()
                                radioBtnList.clear()
                                lifestyleHabitList.clear()
                                allInfoList.clear()

                                // ラジオボタンの結果をクリアにする
                                clearRadioBtn()
                                // editTextに入力された値をクリアする
                                clearForm(binding.containerForBasicInfo)
                                clearForm(binding.lifestyleQuestionContainer)
                            }
                        }

                        // トースト表示
                        showToast(this, R.drawable.toast_ok)
                    }
                }
                // アラートダイアログでキャンセルが押された場合
                .setNegativeButton("キャンセル") { _, _ ->
                    return@setNegativeButton
                }
                // アラートダイアログ表示
                .show()
        }
    }

    // ===== DatePickerDialogで日付が選択された時の処理 =====
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val birthday = String.format("%d/%02d/%02d", year, month+1, dayOfMonth)
        binding.editTxtBirthday.setText(birthday)
    }

    // ===== ツールバーの設定 =====
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

        val title = getString(R.string.title_input_activity)
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

    // ===== DatePickerDialogを表示する =====
    private fun showDatePickerDialog(v:View) {
        val newFragment = DatePickerFragment()
        newFragment.show(supportFragmentManager, "datePicker")
    }

    // ===== ファイルが存在しない場合、ファイルを作成して書き込み =====
    private fun createFile(patientsDataList: MutableList<String>) : Boolean {
        // 出力ファイルの作成
        val file = File(applicationContext.filesDir, fileName)
        val fw = FileWriter(file, false)
        val pw = PrintWriter(BufferedWriter(fw))

        // --- ヘッダーの指定 ---
        // 基本情報
        pw.print("ID")
        pw.print(",")
        pw.print("名前")
        pw.print(",")
        pw.print("誕生日")
        pw.print(",")
        pw.print("体重")
        pw.print(",")
        pw.print("身長")
        pw.print(",")

        // からだの症状
        pw.print("目が疲れる")
        pw.print(",")
        pw.print("目がかすむ")
        pw.print(",")
        pw.print("眼痛")
        pw.print(",")
        pw.print("肩がこる")
        pw.print(",")
        pw.print("筋肉痛・こり")
        pw.print(",")
        pw.print("動悸")
        pw.print(",")
        pw.print("息切れ")
        pw.print(",")
        pw.print("ふとりやすい")
        pw.print(",")
        pw.print("るいそう・やせ")
        pw.print(",")
        pw.print("だるい")
        pw.print(",")
        pw.print("健康感がない")
        pw.print(",")
        pw.print("口渇")
        pw.print(",")
        pw.print("肌の不調")
        pw.print(",")
        pw.print("食欲不振")
        pw.print(",")
        pw.print("胃が張る")
        pw.print(",")
        pw.print("胃痛")
        pw.print(",")
        pw.print("風邪をひきやすい")
        pw.print(",")
        pw.print("咳や痰")
        pw.print(",")
        pw.print("下痢")
        pw.print(",")
        pw.print("便秘")
        pw.print(",")
        pw.print("白髪")
        pw.print(",")
        pw.print("抜け毛")
        pw.print(",")
        pw.print("頭痛")
        pw.print(",")
        pw.print("めまい")
        pw.print(",")
        pw.print("耳鳴り")
        pw.print(",")
        pw.print("腰痛")
        pw.print(",")
        pw.print("関節痛")
        pw.print(",")
        pw.print("むくみ")
        pw.print(",")
        pw.print("汗をかきやすい")
        pw.print(",")
        pw.print("頻尿")
        pw.print(",")
        pw.print("のぼせ")
        pw.print(",")
        pw.print("冷え性")
        pw.print(",")

        // こころの症状
        pw.print("いらいらする")
        pw.print(",")
        pw.print("怒りっぽい")
        pw.print(",")
        pw.print("意欲がわかない")
        pw.print(",")
        pw.print("幸せと感じない")
        pw.print(",")
        pw.print("生きがいがない")
        pw.print(",")
        pw.print("日常生活が楽しくない")
        pw.print(",")
        pw.print("自信を失った")
        pw.print(",")
        pw.print("人と話すのが嫌")
        pw.print(",")
        pw.print("憂うつ")
        pw.print(",")
        pw.print("役に立つ人間でない")
        pw.print(",")
        pw.print("眠りが浅い")
        pw.print(",")
        pw.print("寝つきが悪い")
        pw.print(",")
        pw.print("くよくよする")
        pw.print(",")
        pw.print("ど忘れをする")
        pw.print(",")
        pw.print("集中できない")
        pw.print(",")
        pw.print("問題を解決できない")
        pw.print(",")
        pw.print("容易に判断できない")
        pw.print(",")
        pw.print("心配事でよく眠れない")
        pw.print(",")
        pw.print("緊張感")
        pw.print(",")
        pw.print("理由なく不安になる")
        pw.print(",")
        pw.print("何か恐怖心を感じる")
        pw.print(",")

        // 生活習慣
        pw.print("タバコ")
        pw.print(",")
        pw.print("酒(/1日)")
        pw.print(",")
        pw.print("酒(/週)")
        pw.print(",")
        pw.print("運動")
        pw.print(",")
        pw.print("睡眠時間")
        pw.print(",")
        pw.print("水分摂取")
        pw.print(",")
        pw.print("TV画面などの注視時間")
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

        return true
    }

    // ===== ファイルが存在する場合、ファイルに追記 =====
    private fun addToFile(patientsDataList: MutableList<String>) : Boolean{
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

        return true
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

    // =====radioButtonの結果をクリアにする
    private fun clearRadioBtn(){
        for (i in 0 until binding.lvQuestion.adapter.count) {
            val view = binding.lvQuestion.adapter.getView(i, binding.lvQuestion.getChildAt(i), binding.lvQuestion)
            val radioGroup = view!!.findViewById<RadioGroup>(R.id.radio_group)
            radioGroup.clearCheck()
        }

        for (i in 0 until binding.lvQuestion2.adapter.count) {
            val view = binding.lvQuestion2.adapter.getView(i, binding.lvQuestion2.getChildAt(i), binding.lvQuestion2)
            val radioGroup = view!!.findViewById<RadioGroup>(R.id.radio_group)
            radioGroup.clearCheck()
        }
    }

    // ===== editTextに入力された値を全て取得する =====
    private fun getAllInputData(group: ViewGroup): MutableList<String> {
        var editDataList = mutableListOf<String>()

        group.children.forEachIndexed { i, view ->
            if (view is EditText) {
                editDataList.add(i, view.text.toString())
            }
        }
        return editDataList
    }

    // ===== ラジオボタンの結果を1行1行読み取る =====
    private fun readEachRadioBtn(questionAdapter: ListView): Boolean {
        var isSelectedOneAns = false
        for (i in 0 until questionAdapter.adapter.count) {
            val question = questionAdapter.adapter.getItem(i)
            val view = questionAdapter.adapter.getView(i, questionAdapter.getChildAt(i), questionAdapter)

            // 選択されたラジオボタンの番号を取得
            val selectedNum = getSelectedRadioBtn(view)
            isSelectedOneAns = radioBtnValidation(selectedNum)

            // ラジオボタンが一つも選択されていない場合はアラートを表示
            if(!isSelectedOneAns) {
                showAlertDialog(question)

                return false
            }
            // 選択されたボタンをリストに保存
            radioBtnList.add(selectedNum)
        }
        return true
    }

    // ===== 選択されたラジオボタンの番号を取得 =====
    private fun getSelectedRadioBtn(view: View): String {
        val radioGroup = view!!.findViewById<RadioGroup>(R.id.radio_group)
        val id = radioGroup.checkedRadioButtonId
        val radioButton = radioGroup.findViewById<RadioButton>(id)
        val selectedNum = radioGroup.indexOfChild(radioButton) + 1
        return selectedNum.toString()
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
    private fun radioBtnValidation(selectedNum: String): Boolean {
        if (selectedNum == "0") {
            return false
        }
        return true
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

    // ===== アラートダイアログを表示する =====
    private fun showAlertDialog(question: Any) {
        AlertDialog.Builder(this)
            .setTitle("回答を選択してください！")
            .setMessage("「${question}」の項目で回答を選択していません！")
            .setPositiveButton("OK"){ _, _ ->  }
            .show()
    }
}