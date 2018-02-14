package io.chandler.cosc4503.assign2

import io.chandler.cosc4503.assign2.Lexer.BuildingTypes.*
import io.chandler.cosc4503.assign2.Lexer.State.*
import java.io.File
import java.io.FileReader
import java.util.Arrays
import java.util.Stack
import java.util.TreeMap
import java.util.TreeSet

/*
 Chandler Griscom
 Program Translation
 Assignment 2
 Lexer State Machine
 */
class Lexer {
	var debugScan = false
	var stringChars = TreeSet<Char>()
	init {
		for (c in 0..128) {
			if (	Character.isAlphabetic(c) ||
					Character.isDigit(c) ||
					c.toChar() == '.' ||
					//c.toChar() == '#' ||
					c.toChar() == '_') {
				stringChars.add(c.toChar())
			}
		}
	}
	
	var pairMap = TreeMap<Char, Char>()
	var pairChars = TreeSet<Char>()
	init {
		pairMap.put('[', ']');
		pairMap.put('(', ')');
		pairMap.put('{', '}');
		pairMap.put('\'', '\'');
		pairMap.put('"', '"');
		for (pair in pairMap) {
			pairChars.add(pair.key);
			pairChars.add(pair.value);
		}
	}
	
	var opChars = TreeSet<Char>(Arrays.asList(
			'&', '|', '^', '~',
			'+', '-', '/', '*', '%',
			'=', '!', '<', '>', '?',
			',', ';', ':', '#'
	))
	
	var whiteSpace = TreeSet<Char>(Arrays.asList(
			' ', '\n', '\r', '\t', ((-1).toChar())
	))
	
	var tokens = ArrayList<IntermediateToken>()
	
	var state = WAITING
	var stateStack = Stack<State>()
	
	var buildingType = PHRASE
	
	var buffer = StringBuilder()
	var bufferStack = Stack<StringBuilder>()
	
	var nestStack = Stack<Char>()
	
	var charnum = 0
	var linenum = 1
	
	var c = ' '
	
	enum class State {
		WAITING, BUILDING, COMMENT, ESCAPECHAR
	}
	
	enum class BuildingTypes {
		DOUBLEQUOTE, SINGLEQUOTE, PHRASE, OPERATORS
	}
	
	fun pushToken(token : IntermediateToken) {
		if (token.data.isEmpty()) {
			if (debugScan) println("Skipping empty token " + token.type + ":" + token.data)
		} else {
			if (debugScan) println("Adding token " + token.type + ":" + token.data)
			tokens.add(token)
		}
	}
	
	fun resetBuffer() {
		
		buffer.delete(0, buffer.length)
	}
	
	// Process FSM
	fun update() : Boolean {
		//println(c)
		
		// Stage 1: Check next state
		var nextState = when (state) {
			ESCAPECHAR -> {
				stateStack.pop()
			}
			WAITING -> {
				if (whiteSpace.contains(c)) {
					WAITING
				} else {
					if (c == '"') {
						buildingType = DOUBLEQUOTE
						BUILDING
					} else if (c == '\'') {
						buildingType = SINGLEQUOTE
						BUILDING
					} else if (/*Opening*/ pairMap.contains(c)) {
						pushToken(IntermediateToken(IntermediateToken.Type.OPEN, c + "", linenum, charnum))
						nestStack.push(c)
						WAITING
					} else if (/*Closing*/ pairChars.contains(c)) {
						if (nestStack.size == 0) {
							LexErrors.CLOSING_BRACKET.print(this)
							return false
						}
						var nestOpen = nestStack.pop()
						if (pairMap.get(nestOpen) != c) {
							LexErrors.CLOSING_BRACKET.print(this)
							return false
						}
						pushToken(IntermediateToken(IntermediateToken.Type.CLOSE, c + "", linenum, charnum))
						WAITING
					} else if (stringChars.contains(c)) {
						buildingType = PHRASE
						BUILDING
					} else if (opChars.contains(c)) {
						buildingType = OPERATORS
						BUILDING
					} else {
						LexErrors.ILLEGAL_CHAR.print(this)
						return false
					}
				}
			}
			BUILDING -> {
				if (buildingType == PHRASE && whiteSpace.contains(c)) {
					pushToken(IntermediateToken(IntermediateToken.Type.STRING, buffer.toString(), linenum, charnum))
					WAITING
				} else if (buildingType == OPERATORS && whiteSpace.contains(c)) {
					pushToken(IntermediateToken(IntermediateToken.Type.OPERATORS, buffer.toString(), linenum, charnum))
					WAITING
				} else if (buildingType == PHRASE && pairChars.contains(c)) {
					pushToken(IntermediateToken(IntermediateToken.Type.STRING, buffer.toString(), linenum, charnum))
					if (c == '"' || c == '\'') {
						// Illegal
						LexErrors.QUOTE_LITERAL.print(this)
						return false
					} else if (/*Opening*/ pairMap.contains(c)) {
						pushToken(IntermediateToken(IntermediateToken.Type.OPEN, c + "", linenum, charnum))
						nestStack.push(c)
						WAITING
					} else /*Closing*/ {
						if (nestStack.size == 0) {
							LexErrors.CLOSING_BRACKET.print(this)
							return false
						}
						var nestOpen = nestStack.pop()
						if (pairMap.get(nestOpen) != c) {
							LexErrors.CLOSING_BRACKET.print(this)
							return false
						}
						pushToken(IntermediateToken(IntermediateToken.Type.CLOSE, c + "", linenum, charnum))
						WAITING
					}
				} else {
					if ((buildingType == DOUBLEQUOTE || buildingType == SINGLEQUOTE)) {
						if (c == '\\' && (buildingType == DOUBLEQUOTE || buildingType == SINGLEQUOTE)) {
							stateStack.push(state)
							ESCAPECHAR
						} else if (c == '"' && buildingType == DOUBLEQUOTE) {
							buffer.append(c)
							pushToken(IntermediateToken(IntermediateToken.Type.STRING, buffer.toString(), linenum, charnum))
							WAITING
						} else if (c == '\'' && buildingType == SINGLEQUOTE) {
							buffer.append(c)
							pushToken(IntermediateToken(IntermediateToken.Type.STRING, buffer.toString(), linenum, charnum))
							WAITING
						} else {
							BUILDING
						}
					} else if (stringChars.contains(c)) {
						if (buildingType == OPERATORS) {
							pushToken(IntermediateToken(IntermediateToken.Type.OPERATORS, buffer.toString(), linenum, charnum))
							resetBuffer()
						}
						buildingType = PHRASE
						BUILDING
					} else if (opChars.contains(c)) {
						if (buildingType == PHRASE) {
							pushToken(IntermediateToken(IntermediateToken.Type.STRING, buffer.toString(), linenum, charnum))
							resetBuffer()
							buildingType = OPERATORS
							BUILDING
						} else if (buildingType == OPERATORS && buffer.endsWith("/") && c == '*') {
							stateStack.push(state)
							buffer.deleteCharAt(buffer.length - 1)
							bufferStack.push(buffer)
							buffer = StringBuilder("/")
							COMMENT
						} else if (buildingType == OPERATORS && buffer.endsWith("/") && c == '/') {
							stateStack.push(state)
							buffer.deleteCharAt(buffer.length - 1)
							bufferStack.push(buffer)
							buffer = StringBuilder("/")
							COMMENT
						} else {
							BUILDING
						}
					} else {
						LexErrors.ILLEGAL_CHAR.print(this)
						return false
					}
				}
			}
			COMMENT -> {
				if (	(buffer.startsWith("/*") && buffer.endsWith("*") && c == '/') ||
						(buffer.startsWith("//") && c == '\n')
						) {
					buffer.append(c)
					pushToken(IntermediateToken(IntermediateToken.Type.COMMENT, buffer.toString().trim(), linenum, charnum))
					buffer = bufferStack.pop()
					stateStack.pop()
				} 
				else COMMENT 
			}
		}
		
		// Stage 2: Process buffer
		if (nextState != WAITING) {
			if (state == COMMENT) {
				if (nextState == COMMENT) buffer.append(c)
			} else {
				buffer.append(c)
			}
		} else if (state != WAITING) {
			resetBuffer()
		}
		
		// Stage 3: Update state
		state = nextState
		if (c == '\n') {
			linenum++
			charnum = 0
		} else {
			charnum++
		}
		
		return true
	}
	
	fun lex(src : File, parserTokens : ArrayList<ParserToken>) : Boolean {
		
		FileReader(src).use {
			var c : Int
			
			do {
				c = it.read()
				this.c = c.toChar()
				if (!update()) return false
			} while (c != -1);
			
			if (stateStack.size > 0) {
				if (state == COMMENT) {
					this.c = ' '
					LexErrors.MISSING_COMMENT.print(this);
				} else {
					throw RuntimeException("Unknown state error")
					//TODO not sure what could happen here
				}
			} else if (nestStack.size > 0) {
				var peek = nestStack.peek()
				var closure = pairMap.get(peek)
				if (closure != null) this.c = closure // Feed to debug output
				if (peek == '"' || peek == '\'') {
					LexErrors.MISSING_QUOTE.print(this);
				} else {
					LexErrors.MISSING_BRACKET.print(this);
				}
				return false
			}
		}
		
		// Dump output to console
		
		for (t in tokens) {
			when (t.type) {
				IntermediateToken.Type.STRING -> {
					var s = t.data
					when (s) {
						"namespace" -> parserTokens.add(ParserToken(ParserToken.Type.NAMESPACE, t.data, t.linenum, t.charnum))
						"using" -> parserTokens.add(ParserToken(ParserToken.Type.USING, t.data, t.linenum, t.charnum))
						"include" -> parserTokens.add(ParserToken(ParserToken.Type.INCLUDE, t.data, t.linenum, t.charnum))
						else -> {
							if (isDigit(s)) parserTokens.add(ParserToken(ParserToken.Type.NUM, t.data, t.linenum, t.charnum))
							else if (isValidID(s)) parserTokens.add(ParserToken(ParserToken.Type.ID, t.data, t.linenum, t.charnum))
							else if (isValidChar(s)) parserTokens.add(ParserToken(ParserToken.Type.CHAR, t.data, t.linenum, t.charnum))
						} 
					}
				}
				IntermediateToken.Type.OPERATORS -> {
					for (c in t.data) {
						/*
			 			'&', '|', '^', '~',
						'+', '-', '/', '*', '%',
						'=', '!', '<', '>', '?',
						',', ';', ':', '#'
						*/
						when (c) {
							'&', '|', '^', '~',
							'+', '-', '/', '%' -> parserTokens.add(ParserToken(ParserToken.Type.OPERATOR, "$c", t.linenum, t.charnum))
							'=' -> parserTokens.add(ParserToken(ParserToken.Type.EQUAL, "$c", t.linenum, t.charnum))
							'!' -> parserTokens.add(ParserToken(ParserToken.Type.BANG, "$c", t.linenum, t.charnum))
							'*' -> parserTokens.add(ParserToken(ParserToken.Type.ASTERISK, "$c", t.linenum, t.charnum))
							'<' -> parserTokens.add(ParserToken(ParserToken.Type.L_ANGLE, "$c", t.linenum, t.charnum))
							'>' -> parserTokens.add(ParserToken(ParserToken.Type.R_ANGLE, "$c", t.linenum, t.charnum))
							'?' -> parserTokens.add(ParserToken(ParserToken.Type.QUESTION_MARK, "$c", t.linenum, t.charnum))
							',' -> parserTokens.add(ParserToken(ParserToken.Type.COMMA, "$c", t.linenum, t.charnum))
							';' -> parserTokens.add(ParserToken(ParserToken.Type.SEMICOLON, "$c", t.linenum, t.charnum))
							':' -> parserTokens.add(ParserToken(ParserToken.Type.COLON, "$c", t.linenum, t.charnum))
							'#' -> parserTokens.add(ParserToken(ParserToken.Type.POUND, "$c", t.linenum, t.charnum))
						}
					}
				}
				IntermediateToken.Type.OPEN -> {
					for (c in t.data) {
						// ' " ( [ {
						when (c) {
							//'\'' -> parserTokens.add(ParserToken(ParserToken.Type.L_SINGLE_QUOTE, "$c", t.linenum, t.charnum))
							//'"'  -> parserTokens.add(ParserToken(ParserToken.Type.L_DOUBLE_QUOTE, "$c", t.linenum, t.charnum))
							'('  -> parserTokens.add(ParserToken(ParserToken.Type.L_PAREN, "$c", t.linenum, t.charnum))
							'['  -> parserTokens.add(ParserToken(ParserToken.Type.L_SQUARE, "$c", t.linenum, t.charnum))
							'{'  -> parserTokens.add(ParserToken(ParserToken.Type.L_CURLY, "$c", t.linenum, t.charnum))
						}
					}
				}
				IntermediateToken.Type.CLOSE -> {
					for (c in t.data) {
						// ' " ( [ {
						when (c) {
							//'\'' -> parserTokens.add(ParserToken(ParserToken.Type.R_SINGLE_QUOTE, "$c", t.linenum, t.charnum))
							//'"'  -> parserTokens.add(ParserToken(ParserToken.Type.R_DOUBLE_QUOTE, "$c", t.linenum, t.charnum))
							')'  -> parserTokens.add(ParserToken(ParserToken.Type.R_PAREN, "$c", t.linenum, t.charnum))
							']'  -> parserTokens.add(ParserToken(ParserToken.Type.R_SQUARE, "$c", t.linenum, t.charnum))
							'}'  -> parserTokens.add(ParserToken(ParserToken.Type.R_CURLY, "$c", t.linenum, t.charnum))
						}
					}
				}
				IntermediateToken.Type.COMMENT -> {
					// Do nothing
				}
			}
		}
		
		return true
	}

	fun isDigit(s: String): Boolean {
		for (c in s) {
			if (!Character.isDigit(c)) return false
		}
		return true
	}

	fun isValidID(s: String) : Boolean {
		if (s[0] == '_' || Character.isDigit(s[0])) return false
		for (c in s) {
			if (c != '_'
					&& !Character.isDigit(c)
					&& !Character.isAlphabetic(c.toInt())) return false
		}
		return true
	}

	fun isValidChar(s: String) : Boolean {
		return s.length == 3 && s[0] == '\'' && s[2] == '\''
	}
	
}