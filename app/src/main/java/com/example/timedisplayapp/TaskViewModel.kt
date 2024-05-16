package com.example.timedisplayapp

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class TaskViewModel(application: Application) : AndroidViewModel(application) {
    //    val idCntFlow = MutableStateFlow(idCnt) // public
    //    val tasksFlow = MutableStateFlow(tasks)
    //    val activeTaskIdFlow = MutableStateFlow(activeTaskId)
    // 一坨答辩但是能跑
    private val sharedPreferences =
        application.getSharedPreferences("TaskData", Context.MODE_PRIVATE)
    private val gson = Gson()

    // 此处已经假定 json 中的数据同时完整或缺失
    val idCntState: MutableIntState = run {
        Log.d("TaskViewModel", "start idCntState")
        val idCnt = sharedPreferences.getInt("idCnt", 1)
        mutableIntStateOf(idCnt)
    }
    val tasksState: MutableState<Map<Int, Task>> = run {
        Log.d("TaskViewModel", "start tasksState")
        val tasksJson = sharedPreferences.getString("tasks", "")!!
        Log.d("TaskViewModel", "tasksJson: $tasksJson")
        val type = object : TypeToken<Map<Int, Task>>() {}.type
        Log.d("TaskViewModel", "#1")
        val tasks: Map<Int, Task> =
            if (tasksJson.isEmpty()) {
                mapOf(1 to Task("Task 1"))
            } else {
                gson.fromJson(tasksJson, type)
            }

        Log.d("TaskViewModel", "#2")
        mutableStateOf(tasks)
    }
    val activeTaskIdState: MutableIntState = run {
        Log.d("TaskViewModel", "start activeTaskIdState")
        val activeTaskId = sharedPreferences.getInt("activeTaskId", 1)
        Log.d("TaskViewModel", "end activeTaskIdState")
        mutableIntStateOf(activeTaskId)
    }

//    constructor(application: Application) : super() {
//
//    }
//    val tasksFlow: MutableStateFlow<Map<Int, Task>> = run {
//        val tasksJson = sharedPreferences.getString("tasks", "")
//        val type = object : TypeToken<Map<Int, Task>>() {}.type
//        val tasks: Map<Int, Task> = gson.fromJson(tasksJson, type)
//        MutableStateFlow(tasks)
//    }
//    val activeTaskIdFlow: MutableStateFlow<Int?> = run {
//        val activeTaskId = sharedPreferences.getInt("activeTaskId", 0)
//        MutableStateFlow(activeTaskId)
//    }

    fun saveToDisk() {
        Log.d("TaskViewModel", "saveToDisk")
        val tasksJson = gson.toJson(tasksState.value)

        sharedPreferences.edit().apply {
            putInt("idCnt", idCntState.intValue)
            putString("tasks", tasksJson)
            putInt("activeTaskId", activeTaskIdState.intValue)
            apply()
        }
    }

//    fun loadFromDisk() {
//        val idCnt = sharedPreferences.getInt("idCnt", 0)
//        val tasksJson = sharedPreferences.getString("tasks", "")
//        val activeTaskId = sharedPreferences.getInt("activeTaskId", 0)
//
//        val type = object : TypeToken<Map<Int, Task>>() {}.type
//        val tasks: Map<Int, Task> = gson.fromJson(tasksJson, type)
//        // Use idCnt, tasks, and activeTaskId as needed
//    }

//    fun tick() {
//        tasksState.value = tasksState.value.map { (id, task) ->
//            if (id == activeTaskIdState.intValue) {
//                // 自动更新的核心代码...
//                id to task.copy(activeTick = task.activeTick + 1)
//            } else {
//                id to task
//            }
//        }.toMap()
//    }

//    fun getState() {
//
//    }

//    override fun onCleared() {
//        super.onCleared()
//        Log.d("DiskSave", "save count")
//        viewModelScope.launch(Dispatchers.IO) {
//        }
//    }

//    fun startCounter() {
//        viewModelScope.launch {
//            while (true) {
//                delay(1000)
//                count_flow.value += 1
//            }
//        }
//    }
}