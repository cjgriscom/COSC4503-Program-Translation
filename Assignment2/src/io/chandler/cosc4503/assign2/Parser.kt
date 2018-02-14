package io.chandler.cosc4503.assign2

import io.chandler.cosc4503.assign2.ParserToken.Type.BEGIN
import io.chandler.cosc4503.assign2.ParserToken.Type.CHAR
import io.chandler.cosc4503.assign2.ParserToken.Type.COLON
import io.chandler.cosc4503.assign2.ParserToken.Type.END
import io.chandler.cosc4503.assign2.ParserToken.Type.EQUAL
import io.chandler.cosc4503.assign2.ParserToken.Type.ID
import io.chandler.cosc4503.assign2.ParserToken.Type.L_CURLY
import io.chandler.cosc4503.assign2.ParserToken.Type.L_PAREN
import io.chandler.cosc4503.assign2.ParserToken.Type.NUM
import io.chandler.cosc4503.assign2.ParserToken.Type.R_CURLY
import io.chandler.cosc4503.assign2.ParserToken.Type.R_PAREN
import io.chandler.cosc4503.assign2.ParserToken.Type.SEMICOLON
import java.util.TreeMap

/*
 Chandler Griscom
 Program Translation
 Assignment 2
 Recursive Descent Parser
 */
class Parser constructor(parserTokens : ArrayList<ParserToken>){
	
	var symbolTable = TreeMap<String, String>()
	
	var iter = parserTokens.iterator()
	var curToken = iter.next()
	fun advance() {curToken = iter.next()}
	fun isType(t : ParserToken.Type) : Boolean {return curToken.type == t}
	fun isValue(v : String) : Boolean {return curToken.data.equals(v)}
	fun isDatatype() : Boolean {return curToken.data.equals("int") || curToken.data.equals("char")}
	
	fun parse() : Boolean {
		// Goal
		var success = program()
		if (success) {
			if (iter.hasNext()) {
				return ParseErrors.INCOMPLETE.throwErr(this)
			} else {
				return true
			}
		} else {
			return false
		}
	}
	
	fun program() : Boolean {
		//Program -> MainFunction LEFT_BRACE Statement* RIGHT_BRACE
		//  <EOF>
		if (!isType(BEGIN)) return ParseErrors.EXPECTED.throwWith(BEGIN, this)
		advance()
		if (!MainFunction()) return false
		advance()
		if (!isType(L_CURLY)) return ParseErrors.EXPECTED.throwWith(L_CURLY, this)
		advance()
		while (!isType(R_CURLY)) {
			if (!Statement()) return false
			advance()
		}
		advance()
		if (!isType(END)) return ParseErrors.EXPECTED.throwWith(END, this)
		return true
	}

	fun MainFunction() : Boolean {
		// MainFunction -> VOID MAIN LEFT_PARAN RIGHT_PARAN
		if (!isType(ID) || !isValue("int")) return ParseErrors.EXPECTED.throwWith(ID, "int", this)
		advance()
		if (!isType(ID) || !isValue("main")) return ParseErrors.EXPECTED.throwWith(ID, "main", this)
		advance()
		if (!isType(L_PAREN)) return ParseErrors.EXPECTED.throwWith(L_PAREN, this)
		advance()
		if (!isType(R_PAREN)) return ParseErrors.EXPECTED.throwWith(R_PAREN, this)
		return true
	}
	
	fun Statement() : Boolean {
		// Statement -> LEFT_BRACE (Statement)* RIGHT_BRACE
		//   | VarDeclaration <AssignStmtPartial>? SEMICOLON
		//   | ExitStmt SEMICOLON
		//   | SwitchStmt
		//   | WhileStmt
		//   | BreakStmt SEMICOLON
		
		if (isType(L_CURLY)) {
			advance()
			while (!isType(R_CURLY)) {
				if (!Statement()) return false
				advance()
			}
			// End on R_CURLY
		} else if (isType(ID) && isDatatype()) {
			if (!VarDeclaration()) return false
			advance()
			if (isType(EQUAL)) {
				if (!AssignStmtPartial()) return false
				// Semicolon
				advance()
				if (!isType(SEMICOLON)) return ParseErrors.EXPECTED.throwWith(SEMICOLON, this)
			} else {
				// Semicolon
				advance()
				if (!isType(SEMICOLON)) return ParseErrors.EXPECTED.throwWith(SEMICOLON, this)
			}
			
		} else if (isType(ID) && isValue("while")) {
			if (!WhileStmt()) return false
		} else if (isType(ID) && isValue("switch")) {
			if (!SwitchStmt()) return false
		} else if (isType(ID) && isValue("exit")) {
			if (!ExitStmt()) return false
			advance()
			if (!isType(SEMICOLON)) return ParseErrors.EXPECTED.throwWith(SEMICOLON, this)
		} else if (isType(ID) && isValue("break")) {
			if (!BreakStmt()) return false
			advance()
			if (!isType(SEMICOLON)) return ParseErrors.EXPECTED.throwWith(SEMICOLON, this)
		} else if (isType(ID)) {
			if (!ReturningStmtPostAdvance(false, false)) return false
			//advance()
			if (!isType(SEMICOLON)) return ParseErrors.EXPECTED.throwWith(SEMICOLON, this)
		} else {
			return ParseErrors.EXPECTED.throwWith("statement", this)
		}
		return true
	}
	
	fun Type() : Boolean {
		// Type ->  INT
		if (!isType(ID) || !isDatatype()) return ParseErrors.EXPECTED.throwWith("datatype", this)
		return true
	}
	
	fun VarDeclaration() : Boolean {
		// VarDeclaration -> Type Id <EQUAL <NUM | QUOTE CHAR QUOTE>>
		if (!Type()) return false
		var type = curToken.data
		advance()
		if (!isType(ID)) return ParseErrors.EXPECTED.throwWith(ID, this)
		
		symbolTable.put(curToken.data, type)
		return true
	}
	
	fun AssignStmtPartial() : Boolean {
		// TODO
		// AssignStmt ->  [Id] ASSIGN <NUM | QUOTE CHAR QUOTE> 
		if (!isType(EQUAL)) return ParseErrors.EXPECTED.throwWith(EQUAL, this)
		advance()
		if (!isType(NUM)) return ParseErrors.EXPECTED.throwWith(NUM, this)
		return true
	}
	
	fun FunctionCallPartial() : Boolean {
		// FunctionCall ->  [ID] LEFT_PARAN RIGHT_PARAN
		if (!isType(L_PAREN)) return ParseErrors.EXPECTED.throwWith(L_PAREN, this)
		advance()
		if (!isType(R_PAREN)) return ParseErrors.EXPECTED.throwWith(R_PAREN, this)
		return true
	}
	
	fun ReturningStmtPostAdvance(allowLiteral : Boolean, allowID : Boolean) : Boolean {
		// ReturningStmt ->  AssignStmt | FunctionCall | ExitStmt
		// or optionally  | ID | Char | NUM
		if (isType(ID)) {
			if (isValue("exit")) {
				if (!ExitStmt()) return false
				advance() // Do postadvance
			} else {
				var id = curToken.data
				advance() // This does postadvance only for ID
				if (isType(L_PAREN)) {
					if (!symbolTable.containsKey(id)) symbolTable.put(id, "undeclared method")
					if (!FunctionCallPartial()) return false
					advance() // Do postadvance
				} else if (isType(EQUAL)) {
					if (!symbolTable.containsKey(id)) symbolTable.put(id, "undeclared variable")
					if (!AssignStmtPartial()) return false
					advance() // Do postadvance
				} else if (allowID) {
					if (!symbolTable.containsKey(id)) symbolTable.put(id, "undeclared variable")
					// bueno
				} else {
					return ParseErrors.EXPECTED.throwWith("function call or assignment", this)
				}
			}
		} else if (allowLiteral) {
			if (isType(CHAR) || isType(NUM)) {
				// bueno
				advance() // Do postadvance
			} else return ParseErrors.EXPECTED.throwWith("ID, NUM, or CHAR", this)
		} else return ParseErrors.EXPECTED.throwWith(ID, this)
		return true
	}
	
	fun WhileStmt() : Boolean {
		// WhileStmt -> WHILE LEFT_PARAN <ReturningStmtOrLiteralOrID> RIGHT_PARAN
		//              LEFT_BRACE (Statement)* RIGHT_BRACE
		
		if (!isType(ID) || !isValue("while")) return ParseErrors.EXPECTED.throwWith(ID, "while", this)
		advance()
		if (!isType(L_PAREN)) return ParseErrors.EXPECTED.throwWith(L_PAREN, this)
		advance()
		if (!ReturningStmtPostAdvance(true, true)) return false
		//advance()
		if (!isType(R_PAREN)) return ParseErrors.EXPECTED.throwWith(R_PAREN, this)
		advance()
		if (!isType(L_CURLY)) return ParseErrors.EXPECTED.throwWith(L_CURLY, this)
		advance()
		while (!isType(R_CURLY)) {
			if (!Statement()) return false
			advance()
		}
		return true
	}
	
	fun SwitchStmt() : Boolean {
		// SwitchStmt -> SWITCH LEFT_PARAN ReturningStmtOrLiteral RIGHT_PARAN LEFT_BRACE
		// (CaseStmt)* (DefaultStmt)? RIGHT_BRACE
		if (!isType(ID) || !isValue("switch")) return ParseErrors.EXPECTED.throwWith(ID, "switch", this)
		advance()
		if (!isType(L_PAREN)) return ParseErrors.EXPECTED.throwWith(L_PAREN, this)
		advance()
		if (!ReturningStmtPostAdvance(false, true)) return false
		//advance()
		if (!isType(R_PAREN)) return ParseErrors.EXPECTED.throwWith(R_PAREN, this)
		advance()
		if (!isType(L_CURLY)) return ParseErrors.EXPECTED.throwWith(L_CURLY, this)
		advance()
		while (isType(ID) && isValue("case")) {
			if (!CaseStmtPostAdvance()) return false
			//advance()
		}
		if (isType(ID) && isValue("default")) {
			if (!DefaultStmtPostAdvance()) return false
			//advance()
		}
		if (!isType(R_CURLY)) return ParseErrors.EXPECTED.throwWith(R_CURLY, this)
		return true
	}
	
	fun CaseStmtPostAdvance() : Boolean {
		// CaseStmt ->  CASE <ID | NUM | CHAR> FULL_COLON (Statement)*
		
		if (!isType(ID) || !isValue("case")) return ParseErrors.EXPECTED.throwWith(ID, "case", this)
		advance()
		if (isType(NUM) || isType(CHAR) || isType(ID)) {
			advance()
		} else return ParseErrors.EXPECTED.throwWith("NUM, CHAR, or ID", this)
		if (!isType(COLON)) return ParseErrors.EXPECTED.throwWith(COLON, this)
		advance()
		while (!isType(R_CURLY) && !(isType(ID) && (isValue("default") || isValue("case")))) {
			if (!Statement()) return false
			advance()
		}
		return true
	}
	
	fun ExitStmt() : Boolean {
		// ExitStmt ->  EXIT LEFT_PARAN (YES | NO) RIGHT_PARAN
		if (!isType(ID) || !isValue("exit")) return ParseErrors.EXPECTED.throwWith(ID, "exit", this)
		advance()
		if (!isType(L_PAREN)) return ParseErrors.EXPECTED.throwWith(L_PAREN, this)
		advance()
		if (!isType(ID) || !(isValue("YES") || isValue("NO"))) return ParseErrors.EXPECTED.throwWith("YES or NO", this)
		advance()
		if (!isType(R_PAREN)) return ParseErrors.EXPECTED.throwWith(R_PAREN, this)
		return true
	}
	
	fun DefaultStmtPostAdvance() : Boolean {
		// DefaultStmt -> DEFAULT COLON (Statement)*
		if (!isType(ID) || !isValue("default")) return ParseErrors.EXPECTED.throwWith(ID, "default", this)
		advance()
		if (!isType(COLON)) return ParseErrors.EXPECTED.throwWith(COLON, this)
		advance()
		while (!isType(R_CURLY)) {
			if (!Statement()) return false
			advance()
		}
		return true
	}
	
	fun BreakStmt() : Boolean {
		// BreakStmt -> BREAK
		if (!isType(ID) || !isValue("break")) return ParseErrors.EXPECTED.throwWith(ID, "break", this)
		return true
	}
}


