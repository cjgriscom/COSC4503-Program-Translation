package io.chandler.cosc4503.assign3.tree

import io.chandler.cosc4503.assign3.ParserToken.Type.*
import io.chandler.cosc4503.assign3.Visitor
import io.chandler.cosc4503.assign3.ParseErrors
import io.chandler.cosc4503.assign3.Parser

class CaseStatement // TODO postadvance
	constructor(parser : Parser)
	: TreeNode(parser) {
	
	public var id : String? = null
	public var literal : String? = null
	
	public var block = ArrayList<Statement>()
	
	public override fun accept(v : Visitor) {
		v.visit(this)
	}
	
	public override fun parse() : Boolean {
		// CaseStmt ->  CASE <ID | NUM | CHAR> COLON (Statement)*
		if (!parser.isType(ID) || !parser.isValue("case")) return ParseErrors.EXPECTED.throwWith(ID, "case", parser)
		parser.advance()
		if (parser.isType(ID)) {
			id = parser.curToken.data
			parser.advance()
		} else if (parser.isType(NUM) || parser.isType(CHAR)) {
			literal = parser.curToken.data
			parser.advance()
		} else return ParseErrors.EXPECTED.throwWith("NUM, CHAR, or ID", parser)
		if (!parser.isType(COLON)) return ParseErrors.EXPECTED.throwWith(COLON, parser)
		parser.advance()
		while (!parser.isType(R_CURLY) && !(parser.isType(ID) && (parser.isValue("default") || parser.isValue("case")))) {
			var statement = Statement(parser) 
			if (!statement.parse()) return false
			block.add(statement)
			parser.advance()
		}
		return true
	}
	
}