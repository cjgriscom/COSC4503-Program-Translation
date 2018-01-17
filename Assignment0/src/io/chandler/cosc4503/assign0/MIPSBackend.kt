package io.chandler.cosc4503.assign0

import io.chandler.cosc4503.assign0.Parser.TreeEntry
import java.io.File
import java.io.FileOutputStream
import java.io.PrintStream
import java.util.TreeMap

class MIPSBackend {
	var valid = true
	
	var varRegMap = TreeMap<String, Int>()
	
	fun output(outfile: File, parsed : TreeEntry) : Boolean {
		println("Writing to: " + outfile)
		var out = PrintStream(FileOutputStream(outfile))
		
		if (outfile.exists()) outfile.delete()
		out.println(".data")
		
		
		
		out.close()
		return true
	}
}