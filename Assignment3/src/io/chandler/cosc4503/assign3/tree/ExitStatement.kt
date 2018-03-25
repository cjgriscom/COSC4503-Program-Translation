package io.chandler.cosc4503.assign3.tree

import io.chandler.cosc4503.assign3.ParserToken.Type.*
import io.chandler.cosc4503.assign3.Visitor
import io.chandler.cosc4503.assign3.ParseErrors
import io.chandler.cosc4503.assign3.Parser

class ExitStatement
	constructor(parser : Parser)
	: TreeNode(parser) {
	
	public var value : String? = null
	
	public override fun accept(v : Visitor) {
		v.visit(this)
	}
	
	public override fun parse() : Boolean {
		// ExitStmt ->  EXIT L_PARAN (YES | NO) R_PARAN
		if (!parser.isType(ID) || !parser.isValue("exit")) return ParseErrors.EXPECTED.throwWith(ID, "exit", parser)
		parser.advance()
		if (!parser.isType(L_PAREN)) return ParseErrors.EXPECTED.throwWith(L_PAREN, parser)
		parser.advance()
		if (!parser.isType(ID) || !(parser.isValue("YES") || parser.isValue("NO")))
			return ParseErrors.EXPECTED.throwWith("YES or NO", parser)
		value = parser.curToken.data
		parser.advance()
		if (!parser.isType(R_PAREN)) return ParseErrors.EXPECTED.throwWith(R_PAREN, parser)
		return true
	}
	
}