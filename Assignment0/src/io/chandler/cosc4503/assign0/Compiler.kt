package io.chandler.cosc4503.assign0

import io.chandler.cosc4503.assign0.Parser.TreeEntry
import java.io.File

class Compiler constructor (args: Array<String>) {
	var valid = false
	var file : File? = null
	var outfile : File? = null
	var tokens : ArrayList<Token>? = null
	var structures : TreeEntry? = null
	
	init {
		if (args.size != 1) {
			System.err.println("Expected a single argument containing the name of the input file")
		} else {
			var xfile = File(args[0])
			if (xfile.exists()) {
				valid = true
				file = xfile
				outfile = File(xfile.absolutePath.replaceAfterLast(".", "") + "asm")
			} else {
				valid = false
				System.err.println("File does not exist: " + xfile.absolutePath)
			}
		}
		
	}
	
	fun valid() : Boolean {
		return valid
	}
	
	fun lex() {
		println("--Begin Lex--")
		var lexer : Lexer = Lexer()
		if (file?.let { file -> lexer.lex(file) } ?: false) {
			tokens = lexer.tokens
			println("--Completed Lex--")
		} else {
			System.err.println("Could not complete lex step")
			valid = false
		}
	}
	
	fun parse() {
		println("--Begin Parse--")
		var parser : Parser = Parser()
		if (tokens?.let { tokens -> parser.parse(tokens) } ?: false) {
			structures = parser.structures
			println("--Completed Parse--")
		} else {
			System.err.println("Could not complete parse step")
			valid = false
		}
		
		
	}
	
	fun translate() {
		println("--Begin Translate--")
		var backend : MIPSBackend = MIPSBackend()
		if (structures?.let { structures ->
				outfile?.let { outfile -> backend.output(outfile, structures) }  } ?: false) {
			println("--Completed Translate--")
		} else {
			System.err.println("Could not complete translate step")
			valid = false
		}
		
		
	}
	
}