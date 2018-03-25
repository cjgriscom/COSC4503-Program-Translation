package io.chandler.cosc4503.assign3.tree

import io.chandler.cosc4503.assign3.ParserToken.Type.*
import io.chandler.cosc4503.assign3.Visitor
import io.chandler.cosc4503.assign3.ParseErrors
import io.chandler.cosc4503.assign3.Parser

class SwitchStatement
	constructor(parser : Parser)
	: TreeNode(parser) {
	
	public var condition : ReturningStatement? = null
	public var cases = ArrayList<CaseStatement>()
	public var defaultCase : DefaultStatement? = null
	
	public override fun accept(v : Visitor) {
		v.visit(this)
	}
	
	public override fun parse() : Boolean {
		// SwitchStmt -> SWITCH LEFT_PARAN ReturningStmtOrLiteral RIGHT_PARAN LEFT_BRACE
		// (CaseStmt)* (DefaultStmt)? RIGHT_BRACE
		if (!parser.isType(ID) || !parser.isValue("switch")) return ParseErrors.EXPECTED.throwWith(ID, "switch", parser)
		parser.advance()
		if (!parser.isType(L_PAREN)) return ParseErrors.EXPECTED.throwWith(L_PAREN, parser)
		parser.advance()
		val st = ReturningStatement(false, true, parser)
		if (!st.parse()) return false
		condition = st
		//parser.advance()
		if (!parser.isType(R_PAREN)) return ParseErrors.EXPECTED.throwWith(R_PAREN, parser)
		parser.advance()
		if (!parser.isType(L_CURLY)) return ParseErrors.EXPECTED.throwWith(L_CURLY, parser)
		parser.advance()
		while (parser.isType(ID) && parser.isValue("case")) {
			val case = CaseStatement(parser)
			if (!case.parse()) return false
			cases.add(case)
			//parser.advance()
		}
		if (parser.isType(ID) && parser.isValue("default")) {
			val case = DefaultStatement(parser)
			if (!case.parse()) return false
			defaultCase = case
			//parser.advance()
		}
		if (!parser.isType(R_CURLY)) return ParseErrors.EXPECTED.throwWith(R_CURLY, parser)
		return true
	}
	
}