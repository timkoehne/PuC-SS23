import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket


fun main() {
    val server = ServerSocket(3000)

    while(true){
        val client = server.accept()
        Thread{ handleClient(client)}.start()
    }
}

fun handleClient(client: Socket){
    println("Connection established")
    val output = PrintWriter(client.getOutputStream(), true)
    val input = BufferedReader(InputStreamReader(client.inputStream))
    val textReceived = input.readText()
    println(textReceived)

    val highlightListener = HighlightListener()
    highlightListener.parseAndWalk(textReceived)

    for (highlight in highlightListener.highlights)
        println(highlight)

    //TODO highlighter.highlights to json before sending

    val test = highlightListener.highlights
    println("Responding with $test")
    output.println(test)
}