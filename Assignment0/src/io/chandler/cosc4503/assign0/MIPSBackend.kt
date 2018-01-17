package io.chandler.cosc4503.assign0

import io.chandler.cosc4503.assign0.Parser.TreeEntry
import java.io.File
import java.io.FileOutputStream
import java.io.PrintStream
import java.util.Stack
import java.util.TreeMap

class MIPSBackend {
	var valid = true
	
	fun output(outfile: File, parsed : TreeEntry) : Boolean {
		if (outfile.exists()) outfile.delete()
		
		var addedTempInt = false
		
		var text = ArrayList<String>()
		var data = ArrayList<String>()
		
		var inMain = false
			
		var pos = 0
		var structPosStack = Stack<Int>()
		var treeEntryStack = Stack<TreeEntry>()
		structPosStack.push(1)
		treeEntryStack.push(parsed)
		
		mainLoop@while (true) {
			while (pos >= treeEntryStack.peek().subStructure.size) {
				pos = structPosStack.pop();
				treeEntryStack.pop()
				if (treeEntryStack.isEmpty()) break@mainLoop
			}
			var entry = treeEntryStack.peek().subStructure.get(pos)
			if (entry.sig.name == "MAIN_METHOD" || entry.sig.name == "OPEN_STRUCTURE") {
				pos++
				structPosStack.push(pos)
				pos = 0
				treeEntryStack.push(entry)
				if (entry.sig.name == "MAIN_METHOD") inMain = true
			} else {
				pos++
				// Process nos-structure cases
				if (inMain) { // Ignore if not in main ^_^
					if (entry.sig.name == "COMMENT") {
						text.add("#${entry.lineN}:     ${entry.params[0]}")
					} else if (entry.sig.name == "DECLARE_ASSIGN") {
						data.add("#${entry.lineN}:     int ${entry.params[0]} = ${entry.params[1]};")
						data.add("${entry.params[0]}: .word ${entry.params[1]}")
					} else if (entry.sig.name == "DECLARE") {
						data.add("#${entry.lineN}:     int ${entry.params[0]};")
						data.add("${entry.params[0]}: .word 0")
					} else if (entry.sig.name == "ASSIGN") {
						text.add("#${entry.lineN}:     ${entry.params[0]} = ${entry.params[1]};")
						text.add("la \$t0, ${entry.params[0]}")
						text.add("li \$t8, ${entry.params[1]}")
						text.add("sw \$t8, 0(\$t0)")
					} else if (entry.sig.name == "RETURN") {
						text.add("#${entry.lineN}:     return ${entry.params[0]};")
						text.add("li \$v0, 10")
						text.add("syscall")
					} else if (entry.sig.name == "OUTPUT_VAR") {
						text.add("#${entry.lineN}:     cout << ${entry.params[0]};")
						text.add("la \$t0, ${entry.params[0]}")
						text.add("lw \$a0, 0(\$t0)")
						text.add("li \$v0, 1")
						text.add("syscall")
					} else if (entry.sig.name == "OUTPUT_INT") {
						if (!addedTempInt) {
							data.add("TEMP_INT: .word 0")
							addedTempInt = true
						}
						text.add("#${entry.lineN}:     cout << ${entry.params[0]};")
						text.add("la \$t0, TEMP_INT")
						text.add("li \$t8, ${entry.params[0]}")
						text.add("sw \$t8, 0(\$t0)")
						text.add("lw \$a0, 0(\$t0)")
						text.add("li \$v0, 1")
						text.add("syscall")
					}
				}
			}
			
			
		}
		
		outfile.printWriter().use { out ->
			println("Writing to: " + outfile)
		
			out.println(".data")
			for (s in data) out.println(s)
			out.println(".text")
			for (s in text) out.println(s)
			
		}
		
		return true
	}
}