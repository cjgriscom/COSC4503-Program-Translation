package io.chandler.cosc4503.assign3.tree

import io.chandler.cosc4503.assign3.ParserToken.Type.*
import io.chandler.cosc4503.assign3.Visitor
import io.chandler.cosc4503.assign3.ParseErrors
import io.chandler.cosc4503.assign3.Parser

class Block
	constructor(parser : Parser)
	: TreeNode(parser) {
	
	public var block = ArrayList<Statement>()
	
	public override fun accept(v : Visitor) {
		v.visit(this)
	}
	
	public override fun parse() : Boolean {
		// Block -> L_CURLY (Statement)* R_CURLY
		
		if (parser.isType(L_CURLY)) {
			parser.advance()
			while (!parser.isType(R_CURLY)) {
				var statement = Statement(parser) 
				if (!statement.parse()) return false
				block.add(statement)
				parser.advance()
			}
			// End on R_CURLY
		} else {
			return false
		}
		return true
	}
	
}