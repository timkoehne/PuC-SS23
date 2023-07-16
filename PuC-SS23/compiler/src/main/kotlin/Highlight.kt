class Highlight(var type: String, var start: Int, var end: Int, var lineNum: Int, var length: Int) {
    override fun toString(): String {
        return "{\"name\": \"$type\", \"lineNum\": $lineNum, \"start\": $start, \"end\": $end, \"length\": $length}"
    }
}