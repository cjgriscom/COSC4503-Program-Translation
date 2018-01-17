package io.chandler.cosc4503.assign0

import java.util.Deque
import java.util.LinkedList
import java.util.Queue
import java.util.Stack

// Very crude parser design using a Deque and signature matching
class Parser {
	var valid = true
	var linenum = 0
	
	// Special string signatures
	final var S_LITERAL = "_LIT_"
	final var S_VARIABLE = "_VAR_"
	final var S_WILDCARD = "_*_"
	
	final var shiftReg : Deque<Token> = LinkedList()
	
	final var TOP = Signature("TOP", arrayOf())
	
	final var structures = TreeEntry(TOP, 0)
	final var structStack = Stack<TreeEntry>()
	init {
		structStack.push(structures)
	}
	
	class Signature constructor (
			public final val name : String,
			public final val tokens : Array<Token>,
			// Avoid consuming n=consumeMask tokens
			public final val consumeMask : Int = 0,
			public final val expectStruct : Boolean = false
			) {
	}
	
	class TreeEntry constructor(public var sig: Signature,
								public var lineN : Int) {
		var subStructure = ArrayList<TreeEntry>()
		var params = ArrayList<String>()
		
		override fun toString() : String {
			var sb = StringBuilder()
			sb.append(lineN)
			sb.append(":")
			sb.append(sig.name)
			sb.append("( ")
			for (p in params) sb.append(p + " ")
			sb.append(")\n")
			if (!subStructure.isEmpty()) {
				sb.append(">\n")
				for (te in subStructure) sb.append(te.toString())
				sb.append("<\n")
			}
			return sb.toString()
		}
	}
	
	final var sigListSorted : List<Signature> // Populated by init{}
	
	fun shiftRegMatches(sig: Signature) : Boolean {
		if (shiftReg.size >= sig.tokens.size) {
			var shiftRegIter = shiftReg.reversed().iterator()
			sig.tokens.reversed().forEach({sigToken -> 
				var shiftRegOut = shiftRegIter.next()
				if (shiftRegOut.type != sigToken.type) return false
				if (sigToken.data == S_LITERAL) {
					// only doing integer case
					for (c in shiftRegOut.data) {
						if (!Character.isDigit(c)) return false
					}
				} else if (sigToken.data == S_VARIABLE) {
					if (shiftRegOut.data[0] == '_') return false
					for (c in shiftRegOut.data) {
						if (c != '_'
								&& !Character.isDigit(c)
								&& !Character.isAlphabetic(c.toInt())) return false
					}
				} else if (sigToken.data == S_WILDCARD) {
					// Bueno
				} else {
					if (sigToken.data != shiftRegOut.data) return false
				}
			})
		} else {
			return false
		}
		return true
	}
	
	fun processShiftReg(sig: Signature, te: TreeEntry?) {
		var sigIter = sig.tokens.reversed().iterator()
		
		var pos = 0
		var preserve : Deque<Token> = LinkedList()
		while (pos++ < sig.consumeMask) {
			preserve.add(shiftReg.removeLast())
			sigIter.next()
		}
		
		pos = 0
		while (pos++ < sig.tokens.size - sig.consumeMask) {
			var sigToken = sigIter.next()
			var token = shiftReg.removeLast()
			
			if (te != null && sigToken.data.startsWith("_") && sigToken.data.endsWith("_")) {
				te.params.add(0, token.data)
			}
		}
		
		pos = 0
		while (pos++ < sig.consumeMask) {
			shiftReg.add(preserve.removeLast())
		}
	}
	
	fun parse(tokens: ArrayList<Token>) : Boolean {
		for (t in tokens) {
			linenum = t.linenum
			shiftReg.add(t)
			//print ("Reg:")
			//for (tt in shiftReg) print(" " + tt.type + ":" + tt.data)
			//println()
			var matches = true
			while (matches) {
				matches = false
				for (sig in sigListSorted) {
					if (shiftRegMatches(sig)) {
						println ("Parse match: " + sig.name)
						var te : TreeEntry
						if (sig.name.equals("OPEN_STRUCTURE")) {
							var currentTe = structStack.peek().subStructure.last()
							if (currentTe.sig.expectStruct) {
								te = currentTe
							} else {
								te = TreeEntry(sig, t.linenum)
								structStack.peek().subStructure.add(te)
							}
							structStack.push(te)
							processShiftReg(sig, te)
						} else if (sig.name.equals("CLOSE_STRUCTURE")) {
							structStack.pop()
							processShiftReg(sig, null)
						} else {
							te = TreeEntry(sig, t.linenum)
							structStack.peek().subStructure.add(te)
							processShiftReg(sig, te)
						}
						matches = true
						break
					}
				}
			}
			
			if (structStack.size < 1) {
				valid = false
				ParseErrors.NESTING_ERROR.print(this)
				return false
			}
		}
		if (structStack.size != 1) {
			valid = false
			ParseErrors.EOF_NESTING_ERROR.print(this)
			return false
		}
		if (shiftReg.size != 0) {
			valid = false
			ParseErrors.EOF_INCOMPLETE.print(this)
			return false
		}
		println(structures)
		return true
	}
	
	
	init {
		var sigList = ArrayList<Signature>()
		
		sigList.add(Signature("COMMENT", arrayOf(
			Token(Token.Type.COMMENT, S_WILDCARD)
		)))
		
		sigList.add(Signature("OPEN_STRUCTURE", arrayOf(
			Token(Token.Type.OPEN, "{")
		)))
		
		sigList.add(Signature("CLOSE_STRUCTURE", arrayOf(
			Token(Token.Type.CLOSE, "}")
		)))
		
		sigList.add(Signature("USING_NAMESPACE", arrayOf(
			Token(Token.Type.STRING, "using"),
			Token(Token.Type.STRING, "namespace"),
			Token(Token.Type.STRING, S_VARIABLE),
			Token(Token.Type.OPERATORS, ";")
		)))
		
		sigList.add(Signature("INCLUDE", arrayOf(
			Token(Token.Type.STRING, "#include"),
			Token(Token.Type.OPERATORS, "<"),
			Token(Token.Type.STRING, S_WILDCARD),
			Token(Token.Type.OPERATORS, ">")
		)))
		
		sigList.add(Signature("MAIN_METHOD", arrayOf(
			Token(Token.Type.STRING, "int"),
			Token(Token.Type.STRING, "main"),
			Token(Token.Type.OPEN, "("),
			Token(Token.Type.STRING, "int"),
			Token(Token.Type.STRING, S_VARIABLE),
			Token(Token.Type.OPERATORS, ","),
			Token(Token.Type.STRING, "char"),
			Token(Token.Type.OPERATORS, "**"),
			Token(Token.Type.STRING, S_VARIABLE),
			Token(Token.Type.CLOSE, ")"),
			Token(Token.Type.OPEN, "{")
		), 1, true)) // <-- don't eat the open bracket, expect struct
		
		sigList.add(Signature("OUTPUT_VAR", arrayOf(
			Token(Token.Type.STRING, "cout"),
			Token(Token.Type.OPERATORS, "<<"),
			Token(Token.Type.STRING, S_VARIABLE),
			Token(Token.Type.OPERATORS, ";")
		)))
		
		sigList.add(Signature("OUTPUT_INT", arrayOf(
			Token(Token.Type.STRING, "cout"),
			Token(Token.Type.OPERATORS, "<<"),
			Token(Token.Type.STRING, S_LITERAL),
			Token(Token.Type.OPERATORS, ";")
		)))
		
		sigList.add(Signature("RETURN", arrayOf(
			Token(Token.Type.STRING, "return"),
			Token(Token.Type.STRING, S_LITERAL),
			Token(Token.Type.OPERATORS, ";")
		)))
		
		sigList.add(Signature("DECLARE", arrayOf(
			Token(Token.Type.STRING, "int"),
			Token(Token.Type.STRING, S_VARIABLE),
			Token(Token.Type.OPERATORS, ";")
		)))
		
		sigList.add(Signature("DECLARE_ASSIGN", arrayOf(
			Token(Token.Type.STRING, "int"),
			Token(Token.Type.STRING, S_VARIABLE),
			Token(Token.Type.OPERATORS, "="),
			Token(Token.Type.STRING, S_LITERAL),
			Token(Token.Type.OPERATORS, ";")
		)))
		
		sigList.add(Signature("ASSIGN", arrayOf(
			Token(Token.Type.STRING, S_VARIABLE),
			Token(Token.Type.OPERATORS, "="),
			Token(Token.Type.STRING, S_LITERAL),
			Token(Token.Type.OPERATORS, ";")
		)))
		
		sigListSorted = sigList.sortedWith(compareBy({ -it.tokens.size }))
	}
}