import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import javax.sound.sampled.Line

fun main() {
    val server = ServerSocket(3000)

    while (true) {
        val client = server.accept()
        Thread { handleClient(client) }.start()
    }
}


data class LinePosition(val line: Int, val pos: Int) {
    override fun toString(): String {
        return "line: $line, pos: $pos"
    }
}

fun findStartAndEndPos(highlight: Highlight, lines: List<String>, lineEnding: String): Pair<LinePosition, LinePosition> {
    var startPos: LinePosition? = null
    var endPos: LinePosition? = null

    var lineStart = 0
    var lineEnd: Int
    for ((lineNum, lineText) in lines.withIndex()) {
        lineEnd = lineStart + lineText.length

        if (highlight.start in lineStart..lineEnd) {
            startPos = LinePosition(lineNum, highlight.start - lineStart)
        }

        if (highlight.end in lineStart..lineEnd) {
            endPos = LinePosition(lineNum, highlight.end - lineStart)
        }

        lineStart += lineText.length + lineEnding.length
    }

    if ((startPos == null) || (endPos == null))
        throw Error("Start or End Position is null")
    return Pair(startPos, endPos)
}

fun convertStringIndicesToLineIndices(highlights: List<Highlight>, textReceived: String): MutableList<Highlight> {
    val newHighlights = mutableListOf<Highlight>()

    val lineEnding = if (textReceived.contains("\r\n")) "\r\n" else "\n"

    if (lineEnding == "\r\n") {
        println("lineEnding \\r\\n")
    } else {
        println("lineEnding \\n")
    }


    val lines = textReceived.split(lineEnding)

    for (highlight in highlights) {

        var (startPos, endPos) = findStartAndEndPos(highlight, lines, lineEnding)

        for (lineNum in startPos.line..endPos.line) {
            var start = 0
            var end = lines[lineNum].length

            if (startPos.line == lineNum) {
                start = startPos.pos
            }
            if (endPos.line == lineNum) {
                end = endPos.pos
            }

            //remove last highlight if there is another one starting at the same position
            //this corrects highlighting with function(1)(2)
            if (newHighlights.isNotEmpty()) {
                val lastAdded = newHighlights.last()
                if (lastAdded.type == highlight.type && lastAdded.lineNum == lineNum && lastAdded.start == start) {
//                    println("removing ${newHighlights.last()}")
                    newHighlights.removeLast()
                }
            }
            //add new highlight
            newHighlights.add(Highlight(highlight.type, start, end, lineNum, end - start))
        }

    }
    return newHighlights
}

fun handleClient(client: Socket) {
    try {
        println("Connection established")
        val output = PrintWriter(client.getOutputStream(), true)
        val input = BufferedReader(InputStreamReader(client.inputStream))
        val textReceived = input.readText()
        println(textReceived)

        val highlightListener = HighlightListener()
        highlightListener.parseAndWalk(textReceived)
    //
        val convertedHighlights = convertStringIndicesToLineIndices(highlightListener.highlights, textReceived)
    //    for (highlight in convertedHighlights) {
    //        println(highlight)
    //    }
        println("Responding with $convertedHighlights")
        output.println(convertedHighlights)


    //    val highlights = convertStringIndicesToLineIndices2(listOf(Highlight("keyword", 110, 121, 0, 0)), textReceived)
    //    for (highlight in highlights)
    //        print(highlight)
    //    output.println(highlights)
    } catch(e: Exception) {
        println("Invalid program:")
        println(e)
    }
}