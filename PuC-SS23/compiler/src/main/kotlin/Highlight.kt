class Highlight(var type: String, var start: Int, var end: Int){
    override fun toString(): String {
        return "$type from $start to $end"
    }
}