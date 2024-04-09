package org.example.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClientUI()
        }
    }
}

@Composable
fun ClientUI() {
    var message by remember { mutableStateOf("") }
    var response by remember { mutableStateOf("") }
    var isConnected by remember { mutableStateOf(false) }
    val job = remember { Job() }
    val scope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        onDispose {
            job.cancel() // Cancel the coroutine when the composable is disposed
        }
    }

    Column(Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Message to Server") }
        )
        Button(onClick = {
            CoroutineScope(Dispatchers.IO).launch {
                if (!isConnected) {
                    setupClient("192.168.101.246", 4001) { receivedMsg ->
                        response = receivedMsg
                        isConnected = true
                    }
                }
                sendMessageToServer("192.168.101.246", 4001, message)
                message = "" //clear the message input after send
            }
        }) {
            Text("Send")
        }
        Text("Response from server: $response")
    }
}

suspend fun sendMessageToServer(ip: String, port: Int, message: String): String {
    return withContext(Dispatchers.IO) {
        try {
            Socket(ip, port).use { socket ->
                val writer = PrintWriter(socket.getOutputStream(), true)
                writer.println(message)
                "Message sent"
            }
        } catch (e: Exception) {
            e.localizedMessage ?: "Error while sending the message"
        }
    }
}

suspend fun setupClient(ip: String, port: Int, onMessageReceived: (String) -> Unit) {
    withContext(Dispatchers.IO) {
        try {
            Socket(ip, port).use { socket ->
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                while (true) {
                    val receivedMsg = reader.readLine()
                    withContext(Dispatchers.Main) {
                        onMessageReceived(receivedMsg)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ClientUIPreview() {
    ClientUI()
}
