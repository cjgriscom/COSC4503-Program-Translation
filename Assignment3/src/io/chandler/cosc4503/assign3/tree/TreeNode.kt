package io.chandler.cosc4503.assign3.tree

import io.chandler.cosc4503.assign3.Parser
import io.chandler.cosc4503.assign3.Visitor

abstract class TreeNode {
	protected var parser : Parser
	
	public constructor(parser : Parser) {
		this.parser = parser
	}
	
	public abstract fun accept(v : Visitor)
	public abstract fun parse() : Boolean
	
}