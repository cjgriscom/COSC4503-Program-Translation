package io.chandler.cosc4503.assign3.tree

import io.chandler.cosc4503.assign3.ParserToken.Type.*
import io.chandler.cosc4503.assign3.Visitor
import io.chandler.cosc4503.assign3.ParseErrors
import io.chandler.cosc4503.assign3.Parser

class VarDeclaration
	constructor(parser : Parser)
	: TreeNode(parser) {
	
	public var type : Type? = null
	public var id : String? = null
	public var assignStatement : AssignStatement? = null
	public var hasAssignStatement = false
	
	public override fun accept(v : Visitor) {
		v.visit(this)
	}
	
	public override fun parse() : Boolean {
		// VarDeclaration -> Type Id
		//		SEMICOLON |
		//		AssignStatement SEMICOLON
		val type = Type(parser) 
		if (!type.parse()) return false
		this.type = type
		
		parser.advance()
		if (!parser.isType(ID)) return ParseErrors.EXPECTED.throwWith(ID, parser)
		
		id = parser.curToken.data
		parser.symbolTable.put(id!!, type.type!!)
		
		parser.advance()
		if (parser.isType(EQUAL)) {
			var ast = AssignStatement(id!!, parser)
			if (!ast.parse()) return false
			hasAssignStatement = true
			assignStatement = ast
			// Semicolon
			parser.advance()
			if (!parser.isType(SEMICOLON)) return ParseErrors.EXPECTED.throwWith(SEMICOLON, parser)
		} else {
			// Semicolon
			parser.advance()
			if (!parser.isType(SEMICOLON)) return ParseErrors.EXPECTED.throwWith(SEMICOLON, parser)
		}
		return true
	}
	
}