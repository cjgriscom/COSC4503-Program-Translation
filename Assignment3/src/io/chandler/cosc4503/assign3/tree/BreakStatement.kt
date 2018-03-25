package io.chandler.cosc4503.assign3.tree

import io.chandler.cosc4503.assign3.ParserToken.Type.*
import io.chandler.cosc4503.assign3.Visitor
import io.chandler.cosc4503.assign3.ParseErrors
import io.chandler.cosc4503.assign3.Parser

class BreakStatement
	constructor(parser : Parser)
	: TreeNode(parser) {
	
	public override fun accept(v : Visitor) {
		v.visit(this)
	}
	
	public override fun parse() : Boolean {
		// BreakStmt -> BREAK
		if (!parser.isType(ID) || !parser.isValue("break")) return ParseErrors.EXPECTED.throwWith(ID, "break", parser)
		return true
	}
	
}