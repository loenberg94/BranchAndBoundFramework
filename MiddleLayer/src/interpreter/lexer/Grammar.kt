package interpreter

import org.intellij.lang.annotations.Language
import kotlin.reflect.KProperty

abstract class Grammar {
    private val _tokens = arrayListOf<TokenDefinition>()

    open val tokens get() = _tokens

    fun token(@Language("RegExp") pattern: String) = TokenDefinition(pattern)
    fun token(pattern: Regex) = TokenDefinition(pattern)

    open val tokenizer: Tokenizer by lazy {Tokenizer(tokens)}

    protected operator fun TokenDefinition.provideDelegate(thisRef: Grammar, property: KProperty<*>) : TokenDefinition =
            also {
                _tokens.add(it)
            }

    protected operator fun TokenDefinition.getValue(thisRef: Grammar, property: KProperty<*>) : TokenDefinition = this
}