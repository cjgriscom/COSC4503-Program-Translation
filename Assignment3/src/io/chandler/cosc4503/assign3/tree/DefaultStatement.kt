package io.chandler.cosc4503.assign3.tree

import io.chandler.cosc4503.assign3.ParserToken.Type.*
import io.chandler.cosc4503.assign3.Visitor
import io.chandler.cosc4503.assign3.ParseErrors
import io.chandler.cosc4503.assign3.Parser

class DefaultStatement // TODO postadvance
	constructor(parser : Parser)
	: TreeNode(parser) {
	
	public var block = ArrayList<Statement>()
	
	public override fun accept(v : Visitor) {
		v.visit(this)
	}
	
	public override fun parse() : Boolean {
		// DefaultStmt -> DEFAULT COLON (Statement)*
		if (!parser.isType(ID) || !parser.isValue("default")) return ParseErrors.EXPECTED.throwWith(ID, "default", parser)
		parser.advance()
		if (!parser.isType(COLON)) return ParseErrors.EXPECTED.throwWith(COLON, parser)
		parser.advance()
		while (!parser.isType(R_CURLY)) {
			var statement = Statement(parser) 
			if (!statement.parse()) return false
			block.add(statement)
			parser.advance()
		}
		return true
	}
	
}