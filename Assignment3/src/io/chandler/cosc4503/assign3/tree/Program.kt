package io.chandler.cosc4503.assign3.tree

import io.chandler.cosc4503.assign3.ParserToken.Type.*
import io.chandler.cosc4503.assign3.Visitor
import io.chandler.cosc4503.assign3.ParseErrors
import io.chandler.cosc4503.assign3.Parser

class Program
	constructor(parser : Parser)
	: TreeNode(parser) {
	
	public var mainFunction : MainFunction? = null
	public var statements = ArrayList<Statement>()
	
	public override fun accept(v : Visitor) {
		v.visit(this)
	}
	
	public override fun parse() : Boolean {
		//Program -> MainFunction LEFT_BRACE Statement* RIGHT_BRACE
		//  <EOF>
		if (!parser.isType(BEGIN)) return ParseErrors.EXPECTED.throwWith(BEGIN, parser)
		parser.advance()
		val mainFn = MainFunction(parser)
		if (!mainFn.parse()) return false
		mainFunction = mainFn
		parser.advance()
		if (!parser.isType(L_CURLY)) return ParseErrors.EXPECTED.throwWith(L_CURLY, parser)
		parser.advance()
		while (!parser.isType(R_CURLY)) {
			var statement = Statement(parser)
			if (!statement.parse()) return false
			statements.add(statement)
			parser.advance()
		}
		parser.advance()
		if (!parser.isType(END)) return ParseErrors.EXPECTED.throwWith(END, parser)
		return true
	}
	
}