package com.antiaginglab.antiagingdockapp2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class QuestionAdapter(context: Context, var questionList: Array<String>) : ArrayAdapter<String>(context, 0, questionList) {

    private val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // アンケート項目を取得
        val question = questionList[position]

        // レイアウトの設定
        var view = convertView
        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.listitem_question, parent, false)
        }

        // 各Viewの設定
        val textView = view?.findViewById<TextView>(R.id.txt_question)

        // 取得したViewにアンケート項目をセットする
        textView?.text = question

        // viewを返す
        return view!!
    }
}