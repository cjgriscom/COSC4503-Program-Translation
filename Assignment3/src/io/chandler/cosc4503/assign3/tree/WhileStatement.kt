package io.chandler.cosc4503.assign3.tree

import io.chandler.cosc4503.assign3.ParserToken.Type.*
import io.chandler.cosc4503.assign3.Visitor
import io.chandler.cosc4503.assign3.ParseErrors
import io.chandler.cosc4503.assign3.Parser

class WhileStatement
	constructor(parser : Parser)
	: TreeNode(parser) {
	
	public var condition : ReturningStatement? = null
	public var block = ArrayList<Statement>()
	
	public override fun accept(v : Visitor) {
		v.visit(this)
	}
	
	public override fun parse() : Boolean {
		// WhileStmt -> WHILE LEFT_PARAN <ReturningStmtOrLiteralOrID> RIGHT_PARAN
		//              LEFT_BRACE (Statement)* RIGHT_BRACE
		
		if (!parser.isType(ID) || !parser.isValue("while")) return ParseErrors.EXPECTED.throwWith(ID, "while", parser)
		parser.advance()
		if (!parser.isType(L_PAREN)) return ParseErrors.EXPECTED.throwWith(L_PAREN, parser)
		parser.advance()
		val st = ReturningStatement(true, true, parser)
		if (!st.parse()) return false
		condition = st
		//parser.advance()
		if (!parser.isType(R_PAREN)) return ParseErrors.EXPECTED.throwWith(R_PAREN, parser)
		parser.advance()
		if (!parser.isType(L_CURLY)) return ParseErrors.EXPECTED.throwWith(L_CURLY, parser)
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