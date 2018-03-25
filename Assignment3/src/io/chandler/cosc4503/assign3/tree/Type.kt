package io.chandler.cosc4503.assign3.tree

import io.chandler.cosc4503.assign3.ParserToken.Type.*
import io.chandler.cosc4503.assign3.Visitor
import io.chandler.cosc4503.assign3.ParseErrors
import io.chandler.cosc4503.assign3.Parser

class Type
	constructor(parser : Parser)
	: TreeNode(parser) {
	
	public var type : String? = null
	
	public override fun accept(v : Visitor) {
		v.visit(this)
	}
	
	public override fun parse() : Boolean {
		// Type ->  INT
		if (!parser.isType(ID) || !parser.isDatatype()) return ParseErrors.EXPECTED.throwWith("datatype", parser)
		type = parser.curToken.data
		return true
	}
	
}