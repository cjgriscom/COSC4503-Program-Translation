package io.chandler.cosc4503.assign0

class Token constructor(public final val type: Type, public final val data: String,
						public final val linenum: Int = 0, public final val charnum: Int = 0) {

	enum class Type {
		STRING, OPEN, CLOSE, OPERATORS, COMMENT
	}
	
	
}