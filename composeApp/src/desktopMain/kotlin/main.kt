package org.example.project

import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.readUTF8Line
import io.ktor.utils.io.writeStringUtf8
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess

fun main() {
    runBlocking {
        val selectorManager = SelectorManager(Dispatchers.IO)
        val socket = aSocket(selectorManager).tcp().connect("192.168.101.246", 4001)

        val receiveChannel = socket.openReadChannel()
        val sendChannel = socket.openWriteChannel(autoFlush = true)

        launch(Dispatchers.IO) {
            try {
                while (true) {
                    val message = receiveChannel.readUTF8Line()
                    if (message == null) {
                        println("No message received or connection closed.")
                        break
                    }
                    println(message)
                }
            } catch (e: Exception) {
                println("Error: ${e.localizedMessage}")
            } finally {
                socket.close()
                selectorManager.close()
                exitProcess(0)
            }
        }

        launch(Dispatchers.IO) {
            try {
                while (true) {
                    val myMessage = readln()
                    sendChannel.writeStringUtf8("$myMessage\n")
                }
            } catch (e: Exception) {
                println("Error: ${e.localizedMessage}")
            }
        }
    }
}
