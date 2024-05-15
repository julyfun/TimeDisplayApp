package com.example.timedisplayapp

import android.content.pm.ActivityInfo
import androidx.activity.enableEdgeToEdge
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.TextField
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timedisplayapp.ui.theme.TimeDisplayAppTheme
import java.util.*


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT


        setContent {
            TimeDisplayAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TaskManager(
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TaskManager(modifier: Modifier = Modifier) {
    // [state]
    var id_cnt by rememberSaveable { mutableIntStateOf(1) }
    var tasks by rememberSaveable { mutableStateOf(mapOf(1 to Task("Task 1"))) }
    var activeTaskId by rememberSaveable { mutableStateOf<Int?>(1) }

    // [other]
    var newTaskName by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    var showInputField by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        tasks[1]?.activate()
        while (true) {
            kotlinx.coroutines.delay(50)
            if (activeTaskId != null) {
                tasks = tasks.map { (id, task) ->
                    if (id == activeTaskId) {
                        // 自动更新的核心代码...
                        id to task.copy(activeTick = task.activeTick + 1)
                    } else {
                        id to task
                    }
                }.toMap()
                Log.d("TaskManager", "activeTick: ${tasks[activeTaskId!!]?.activeTick}")
            }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        tasks.forEach { (id, task) ->
            var offsetX by remember { mutableStateOf(0f) }
            var offsetY by remember { mutableStateOf(0f) }
            Button(
                onClick = {
                    if (id == activeTaskId) {
                        task.deactivate()
                        activeTaskId = null
                    } else {
                        if (activeTaskId != null) {
                            tasks[activeTaskId!!]?.deactivate()
                        }
                        task.activate()
                        activeTaskId = id
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(color = Color.Black)
                    .draggable(
                        state = rememberDraggableState { delta ->
                            offsetX += delta
                        },
                        orientation = Orientation.Horizontal,
                        onDragStopped = {
                            Log.d("TaskManager", "offsetX: $offsetX")
                            if (offsetX < -100 && tasks.size > 1) { // Threshold for downward drag
                                tasks = tasks.filterKeys { it != id }
                                if (activeTaskId == id) {
                                    activeTaskId = null
                                }
                            }
                            offsetX = 0f
                        }
                    )
                    .draggable(
                        state = rememberDraggableState { delta ->
                            offsetY += delta
                        },
                        orientation = Orientation.Vertical,
                        onDragStopped = {
                            Log.d("TaskManager", "offsetY: $offsetY")
                            if (offsetY < -100) { // Threshold for downward drag
                                showInputField = true
                            }
                            offsetY = 0f
                        }
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = task.backGroundColor,
                ),
                // 去除圆角
                shape = RoundedCornerShape(0.dp),
            ) {
                Text("${task.name}: ${task.formatTime()}", fontSize = 36.sp)
            }
        }

        if (showInputField) {
            val focusRequester = remember { FocusRequester() }
            TextField(
                value = newTaskName,
                onValueChange = { newTaskName = it },
                placeholder = { Text("Enter new task") },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = {
                    if (!newTaskName.isEmpty()) {
                        id_cnt++
                        tasks = tasks + (id_cnt to Task(newTaskName))
                    }
                    keyboardController?.hide()  // Hide the keyboard
                    newTaskName = ""
                    Log.d("TaskManager", "Task added: $newTaskName $id_cnt")
                    focusManager.clearFocus()
                    showInputField = false
                }),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .weight(1f)
            )
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
                keyboardController?.show()
            }
        }
    }
}
