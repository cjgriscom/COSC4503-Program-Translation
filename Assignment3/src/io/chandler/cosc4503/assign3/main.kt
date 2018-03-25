package io.chandler.cosc4503.assign3

import java.io.File

/*
 Chandler Griscom
 Program Translation
 Assignment 3
 Lexer/Parser Main Method
 */
fun main(args: Array<String>) {

	if (args.size != 2) {
		printUsage()
	} else {
		var xfile = File(args[1])
		if (xfile.exists()) {
			var tokens = ArrayList<ParserToken>()
			tokens.add(ParserToken(ParserToken.Type.BEGIN, "", 0, 0))
			Lexer().lex(xfile, tokens)
			tokens.add(ParserToken(ParserToken.Type.END, "", tokens.last().linenum + 1, 0))
			var parser = Parser(tokens)
			if (parser.parse()) {
				when(args[0].toLowerCase()) {
					"getsymbols" -> {
						println("Symbols:")
						for (s in parser.symbolTable.keys) println(s + ": " + parser.symbolTable.get(s))
					}
					"prettyprint" -> {
						PrettyPrintVisitor(parser.program)
					}
					"postfix" -> {
						PostfixVisitor(parser.program)
					}
					else -> {
						System.err.println("Unknown command: " + args[0])
						printUsage()
					}
				}
				
			}
		} else {
			System.err.println("File does not exist: " + xfile.absolutePath)
			printUsage()
		}
	}
}

fun printUsage() {
	System.err.println("Usage: (application) (command) (inputfile)")
	System.err.println("  Commands:")
	System.err.println("    getSymbols")
	System.err.println("    prettyPrint")
	System.err.println("    postfix")
}