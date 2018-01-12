package io.chandler.cosc4503.assign0

class Token constructor(public final val type: Type, public final val data: String) {

	enum class Type {
		STRING, OPEN, CLOSE, OPERATORS, COMMENT
	}
	
	
}