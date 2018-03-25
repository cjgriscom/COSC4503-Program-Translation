package io.chandler.cosc4503.assign3.tree

import io.chandler.cosc4503.assign3.ParserToken.Type.*
import io.chandler.cosc4503.assign3.Visitor
import io.chandler.cosc4503.assign3.ParseErrors
import io.chandler.cosc4503.assign3.Parser

class FunctionCall
	constructor(public var prefID : String, parser : Parser)
	: TreeNode(parser) {
	
	public override fun accept(v : Visitor) {
		v.visit(this)
	}
	
	public override fun parse() : Boolean {
		// FunctionCall ->  [ID] LEFT_PARAN RIGHT_PARAN
		if (!parser.isType(L_PAREN)) return ParseErrors.EXPECTED.throwWith(L_PAREN, parser)
		parser.advance()
		if (!parser.isType(R_PAREN)) return ParseErrors.EXPECTED.throwWith(R_PAREN, parser)
		return true
	}
	
}