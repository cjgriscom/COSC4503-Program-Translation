package io.chandler.cosc4503.assign3.tree

import io.chandler.cosc4503.assign3.ParserToken.Type.*
import io.chandler.cosc4503.assign3.Visitor
import io.chandler.cosc4503.assign3.ParseErrors
import io.chandler.cosc4503.assign3.Parser

class Statement
	constructor(parser : Parser)
	: TreeNode(parser) {
	
	public var block : Block? = null
	public var varDeclaration: VarDeclaration? = null
	public var exitStatement : ExitStatement? = null
	public var switchStatement : SwitchStatement? = null
	public var whileStatement : WhileStatement? = null
	public var returningStatement : ReturningStatement? = null
	public var breakStatement : BreakStatement? = null
	
	public override fun accept(v : Visitor) {
		v.visit(this)
	}
	
	public override fun parse() : Boolean {
		// Statement -> BLOCK
		//   | VarDeclaration SEMICOLON
		//   | ExitStmt SEMICOLON
		//   | SwitchStmt
		//   | WhileStmt
		//   | ReturningStatement
		//   | BreakStmt SEMICOLON
		
		if (parser.isType(L_CURLY)) {
			var st = Block(parser)
			if (!st.parse()) return false
			block = st;
		} else if (parser.isType(ID) && parser.isDatatype()) {
			var st = VarDeclaration(parser)
			if (!st.parse()) return false
			varDeclaration = st
		} else if (parser.isType(ID) && parser.isValue("while")) {
			var st = WhileStatement(parser)
			if (!st.parse()) return false
			whileStatement = st
		} else if (parser.isType(ID) && parser.isValue("switch")) {
			var st = SwitchStatement(parser)
			if (!st.parse()) return false
			switchStatement = st
		} else if (parser.isType(ID) && parser.isValue("exit")) {
			var st = ExitStatement(parser)
			if (!st.parse()) return false
			parser.advance()
			if (!parser.isType(SEMICOLON)) return ParseErrors.EXPECTED.throwWith(SEMICOLON, parser)
			exitStatement = st
		} else if (parser.isType(ID) && parser.isValue("break")) {
			var st = BreakStatement(parser)
			if (!st.parse()) return false
			parser.advance()
			if (!parser.isType(SEMICOLON)) return ParseErrors.EXPECTED.throwWith(SEMICOLON, parser)
			breakStatement = st
		} else if (parser.isType(ID)) {
			var st = ReturningStatement(false, false, parser)
			if (!st.parse()) return false
			//parser.advance()
			if (!parser.isType(SEMICOLON)) return ParseErrors.EXPECTED.throwWith(SEMICOLON, parser)
			returningStatement = st
			
		} else {
			return ParseErrors.EXPECTED.throwWith("statement", parser)
		}
		return true
	}
	
}