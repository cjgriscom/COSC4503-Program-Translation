package io.chandler.cosc4503.assign3.tree

import io.chandler.cosc4503.assign3.ParserToken.Type.*
import io.chandler.cosc4503.assign3.Visitor
import io.chandler.cosc4503.assign3.ParseErrors
import io.chandler.cosc4503.assign3.Parser

class AssignStatement
	constructor(public var prefID : String, parser : Parser)
	: TreeNode(parser) {
	
	public var num : String? = null
	
	public override fun accept(v : Visitor) {
		v.visit(this)
	}
	
	public override fun parse() : Boolean {
		// AssignStmt ->  [Id] ASSIGN <NUM | QUOTE CHAR QUOTE> 
		if (!parser.isType(EQUAL)) return ParseErrors.EXPECTED.throwWith(EQUAL, parser)
		parser.advance()
		if (!parser.isType(NUM)) return ParseErrors.EXPECTED.throwWith(NUM, parser)
		num = parser.curToken.data
		return true
	}
	
}