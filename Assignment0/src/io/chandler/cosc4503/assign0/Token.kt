package io.chandler.cosc4503.assign0

class Token constructor(public final val type: Type, public final val data: String,
						public final val linenum: Int, public final val charnum: Int) {

	enum class Type {
		STRING, OPEN, CLOSE, OPERATORS, COMMENT
	}
	
	
}