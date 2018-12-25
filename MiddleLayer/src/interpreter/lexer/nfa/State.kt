package Lexer.nfa

import interpreter.Token

class State {
    val id:Int
    val type: StateType
    val token:Token?
    val links:List<Link>

    constructor(type: StateType = StateType.TRANSITION, token:Token? = null, links:List<Link>){
        this.id = 0
        this.type = type
        this.token = token
        this.links = links
    }

    constructor(id:Int, type: StateType = StateType.TRANSITION, token:Token?, links:List<Link>){
        this.id = id
        this.type = type
        this.token = token
        this.links = links
    }

    fun addLink(link: Link): State {
        return State(this.id, this.type, this.token, this.links + link)
    }
}