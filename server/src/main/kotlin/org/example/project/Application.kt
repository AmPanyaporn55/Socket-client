package org.example.project

import java.net.Socket
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter

fun connectToServer(ip: String, port: Int, message: String) {
    val socket = Socket(ip, port)
    println("Connected to server at $ip on port $port")

    val writer = PrintWriter(socket.getOutputStream(), true)
    val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

    writer.println(message)
    println("Sent to server: $message")

    val response = reader.readLine()
    println("Received from server: $response")

    socket.close()
}

fun main() {
    val serverIp = "192.168.101.246" // replace with your server IP
    connectToServer(serverIp, 4001, "Hello Server!")
}

