package com.antiaginglab.antiagingdockapp2

import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.suspendCoroutine


class ConfirmRepository {
    private val storage = Firebase.storage

    suspend fun save(csvFile: File) : Task<Void> {
        return suspendCoroutine {

            val storageRef = storage.reference

            val file = Uri.fromFile(csvFile)
            val ref = storageRef.child("files/${file.lastPathSegment}")
            val uploadTask = ref.putFile(file)
            uploadTask.addOnSuccessListener { taskSnapshot ->
                Log.d("SUCCESS", taskSnapshot.toString())
            }

        }
    }
}