package io.chandler.cosc4503.assign1

import java.io.File

fun main(args: Array<String>) {

	if (args.size != 1) {
		System.err.println("Expected a single argument containing the name of the input file")
	} else {
		var xfile = File(args[0])
		if (xfile.exists()) {
			Lexer().lex(xfile)
		} else {
			System.err.println("File does not exist: " + xfile.absolutePath)
		}
	}
}