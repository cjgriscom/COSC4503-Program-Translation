package io.chandler.cosc4503.assign3.tree

import io.chandler.cosc4503.assign3.ParserToken.Type.*
import io.chandler.cosc4503.assign3.Visitor
import io.chandler.cosc4503.assign3.ParseErrors
import io.chandler.cosc4503.assign3.Parser

class ReturningStatement // TODO postadvance
	constructor(private val allowLiteral : Boolean, private val allowID : Boolean, parser : Parser)
	: TreeNode(parser) {
	
	public var assignStatement : AssignStatement? = null
	public var functionCall : FunctionCall? = null
	public var exitStatement : ExitStatement? = null
	
	// One of the following is non-null
	public var literal : String? = null
	public var resolvedNode : TreeNode? = null
	public var id : String? = null // id is always set for resolvedNode
	
	public override fun accept(v : Visitor) {
		v.visit(this)
	}
	
	public override fun parse() : Boolean {
		// ReturningStmt ->  AssignStmt | FunctionCall | ExitStmt
		// or optionally  | ID | Char | NUM
		
		if (parser.isType(ID)) {
			id = parser.curToken.data
			if (parser.isValue("exit")) {
				val stt = ExitStatement(parser)
				if (!stt.parse()) return false
				exitStatement = stt
				resolvedNode = stt
				parser.advance() // Do postadvance
			} else {
				var id = parser.curToken.data
				parser.advance() // This does postadvance only for ID
				if (parser.isType(L_PAREN)) {
					if (!parser.symbolTable.containsKey(id)) parser.symbolTable.put(id, "undeclared method")
					val stt = FunctionCall(id, parser)
					if (!stt.parse()) return false
					functionCall = stt
					resolvedNode = stt
					parser.advance() // Do postadvance
				} else if (parser.isType(EQUAL)) {
					if (!parser.symbolTable.containsKey(id)) parser.symbolTable.put(id, "undeclared variable")
					val stt = AssignStatement(id, parser)
					if (!stt.parse()) return false
					assignStatement = stt
					resolvedNode = stt
					parser.advance() // Do postadvance
				} else if (allowID) {
					if (!parser.symbolTable.containsKey(id)) parser.symbolTable.put(id, "undeclared variable")
					// bueno
					this.id = id
				} else {
					return ParseErrors.EXPECTED.throwWith("function call or assignment", parser)
				}
			}
		} else if (allowLiteral) {
			if (parser.isType(CHAR) || parser.isType(NUM)) {
				// bueno
				literal = parser.curToken.data
				parser.advance() // Do postadvance
			} else return ParseErrors.EXPECTED.throwWith("ID, NUM, or CHAR", parser)
		} else return ParseErrors.EXPECTED.throwWith(ID, parser)
		return true
	}
	
}