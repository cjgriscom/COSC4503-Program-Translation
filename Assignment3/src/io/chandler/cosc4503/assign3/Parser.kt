package io.chandler.cosc4503.assign3

import io.chandler.cosc4503.assign3.ParserToken.Type.CHAR
import io.chandler.cosc4503.assign3.ParserToken.Type.COLON
import io.chandler.cosc4503.assign3.ParserToken.Type.END
import io.chandler.cosc4503.assign3.ParserToken.Type.EQUAL
import io.chandler.cosc4503.assign3.ParserToken.Type.ID
import io.chandler.cosc4503.assign3.ParserToken.Type.L_CURLY
import io.chandler.cosc4503.assign3.ParserToken.Type.L_PAREN
import io.chandler.cosc4503.assign3.ParserToken.Type.NUM
import io.chandler.cosc4503.assign3.ParserToken.Type.R_CURLY
import io.chandler.cosc4503.assign3.ParserToken.Type.R_PAREN
import io.chandler.cosc4503.assign3.ParserToken.Type.SEMICOLON
import io.chandler.cosc4503.assign3.tree.Program
import java.util.TreeMap

/*
 Chandler Griscom
 Program Translation
 Assignment 3
 Recursive Descent Parser
 */
class Parser constructor(parserTokens : ArrayList<ParserToken>){
	var program : Program? = null
	
	var symbolTable = TreeMap<String, String>()
	
	var iter = parserTokens.iterator()
	var curToken = iter.next()
	var peek = iter.next()
	fun advance() {
		curToken = peek
		if (iter.hasNext()) {
			peek = iter.next()
		} else {
			peek = ParserToken(END, "")
		}
	}
	fun isType(tok : ParserToken, t : ParserToken.Type) : Boolean {return tok.type == t}
	fun isType(t : ParserToken.Type) : Boolean {return curToken.type == t}
	fun isValue(v : String) : Boolean {return curToken.data.equals(v)}
	fun isDatatype() : Boolean {return curToken.data.equals("int") || curToken.data.equals("char")}
	
	fun parse() : Boolean {
		// Goal
		var program = Program(this)
		if (program.parse()) {
			if (iter.hasNext()) {
				return ParseErrors.INCOMPLETE.throwErr(this)
			}
			this.program = program
			return true
		} else {
			return false
		}
	}

}


