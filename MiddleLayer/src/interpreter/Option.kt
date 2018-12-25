package Interpreter.util

abstract class Option<T>(v: T?){
    val value: T? = v

    fun isSome():Boolean{ return this is Some<*> }
    fun isNone():Boolean{ return this is None }
}

class Some<T : Any>(v:T):Option<T>(v){
    init {
        requireNotNull(v)
    }
}

class None: Option<Int>(null)
