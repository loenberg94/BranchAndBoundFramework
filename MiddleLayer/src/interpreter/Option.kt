package interpreter

abstract class Option{
    fun isSome():Boolean{ return this is Some<*>
    }
    fun isNone():Boolean{ return this is None
    }
}

class Some<T>(v:T): Option(){
    private val value: T = v

    fun getVal():T{
        return value
    }
}

class None: Option()
