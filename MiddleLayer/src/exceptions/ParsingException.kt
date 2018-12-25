package exceptions

class ParsingException(val expected:String, val received: String, val position:Int, val row:Int, val column:Int):Exception() {
    
    override fun toString(): String {
        return super.toString()
    }
}