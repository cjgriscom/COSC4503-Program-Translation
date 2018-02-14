package io.chandler.cosc4503.assign2

import java.io.File

/*
 Chandler Griscom
 Program Translation
 Assignment 2
 Lexer/Parser Main Method
 */
fun main(args: Array<String>) {

	if (args.size != 1) {
		System.err.println("Expected a single argument containing the name of the input file")
	} else {
		var xfile = File(args[0])
		if (xfile.exists()) {
			var tokens = ArrayList<ParserToken>()
			tokens.add(ParserToken(ParserToken.Type.BEGIN, "", 0, 0))
			Lexer().lex(xfile, tokens)
			tokens.add(ParserToken(ParserToken.Type.END, "", tokens.last().linenum + 1, 0))
			Parser(tokens).parse()
			
		} else {
			System.err.println("File does not exist: " + xfile.absolutePath)
		}
	}
}