import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

fun main() {
    val server = ServerSocket(3000)

    while (true) {
        val client = server.accept()
        Thread { handleClient(client) }.start()
    }
}

fun convertStringIndicesToLineIndices(highlightListener: HighlightListener, textReceived: String): MutableList<Highlight> {
    val newHighlights = mutableListOf<Highlight>()
    val lines = textReceived.split("\n")
    for (highlight in highlightListener.highlights) {
        var lineStart = 0
        var lineEnd: Int
        var lineNum = 0
        var startFound = false
        for (lineText in lines) {
            lineEnd = lineStart + lineText.length + lineNum //not quite sure why we need + lineNum, but it works
            if (highlight.start in lineStart..lineEnd) {
                startFound = true
            }

            if (startFound) {
                if (highlight.end <= lineEnd) {
                    highlight.lineNum = lineNum
//                    println("zeile: $lineNum")

                    if (highlight.start in lineStart..lineEnd && highlight.end in lineStart..lineEnd) {
                        newHighlights.add(Highlight(
                                highlight.type,
                                highlight.start - (lineStart + lineNum), //not quite sure why we need + lineNum, but it works
                                0,
                                lineNum,
                                highlight.end - highlight.start + 1,
                        ))
                    } else {
                        newHighlights.add(Highlight(
                                highlight.type,
                                0,
                                0,
                                lineNum,
                                lineText.length - 1,
                        ))
                    }
                    break
                } else {
                    newHighlights.add(Highlight(
                            highlight.type,
                            0,
                            0,
                            lineNum,
                            lineText.length - 1,
                    ))
                    highlight.lineNum = lineNum
//                    println("zeile: $lineNum")
                    highlight.start = 0
//                    println("start: 0")
                    highlight.length = lineText.length - 1
//                    println("length: ${lineText.length - 1}")
                }
            }
            lineStart += lineText.length
            lineNum++
        }
    }
    return newHighlights
}

fun handleClient(client: Socket) {
    println("Connection established")
    val output = PrintWriter(client.getOutputStream(), true)
    val input = BufferedReader(InputStreamReader(client.inputStream))
    val textReceived = input.readText()

    val highlightListener = HighlightListener()
    highlightListener.parseAndWalk(textReceived)

    val convertedHighlights = convertStringIndicesToLineIndices(highlightListener, textReceived)
    for (highlight in convertedHighlights) {
        println(highlight)
    }
    println("Responding with $convertedHighlights")
    output.println(convertedHighlights)
}