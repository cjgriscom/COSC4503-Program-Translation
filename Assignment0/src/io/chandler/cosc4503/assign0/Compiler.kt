package io.chandler.cosc4503.assign0

import java.io.File

class Compiler constructor (args: Array<String>) {
	var valid = false
	var file : File? = null
	var tokens : ArrayList<Token>? = null
	
	init {
		if (args.size != 1) {
			System.err.println("Expected a single argument containing the name of the input file")
		} else {
			var xfile = File(args[0])
			if (xfile.exists()) {
				valid = true
				file = xfile
			} else {
				System.err.println("File does not exist: " + xfile.absolutePath)
			}
		}
		
	}
	
	fun valid() : Boolean {
		return valid
	}
	
	fun lex() {
		var lexer : Lexer = Lexer()
		if (file?.let { file -> lexer.lex(file) } ?: false) {
			tokens = lexer.tokens
		} else {
			System.err.println("Could not perform lex step")
			valid = false
		}
	}
	
	fun parse() {
		var parser : Parser = Parser()
	}
	
}