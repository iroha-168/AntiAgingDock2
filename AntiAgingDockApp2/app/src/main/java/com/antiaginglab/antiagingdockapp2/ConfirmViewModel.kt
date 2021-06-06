package com.antiaginglab.antiagingdockapp2

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.Exception


class ConfirmViewModel : ViewModel() {

    private val repository = ConfirmRepository()

    fun saveToFirebase(csvFile: File) = viewModelScope.launch {
        try {
            // 非同期処理でrepositoryのメソッドを呼び出す
            withContext(Dispatchers.Default) {  }
        } catch (e: Exception) {
            Log.d("ERROR", e.toString())
        }
    }
}