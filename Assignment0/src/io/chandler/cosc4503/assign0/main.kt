package io.chandler.cosc4503.assign0

import kotlin.system.*

fun main(args: Array<String>) {
	//val cmdParser : Compiler = Compiler(args)
	val cmp : Compiler = Compiler(args)
	if (!cmp.valid()) exitProcess(1)
	cmp.lex()
	if (!cmp.valid()) exitProcess(1)
	cmp.parse()
	if (!cmp.valid()) exitProcess(1)
	cmp.translate()
	if (!cmp.valid()) exitProcess(1)
	
}