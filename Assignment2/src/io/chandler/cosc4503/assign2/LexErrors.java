package io.chandler.cosc4503.assign2;

/*
Chandler Griscom
Program Translation
Assignment 2
Lexer Error Strings
*/
public enum LexErrors {
	
	NESTING_ERROR("Nesting error at "),
	QUOTE_LITERAL("Unexpected quote character at "),
	CLOSING_BRACKET("Unexpected closing bracket at "),
	ILLEGAL_CHAR("Illegal character at "),
	MISSING_QUOTE("Missing terminating quote at "),
	MISSING_BRACKET("Missing terminating bracket at "),
	MISSING_COMMENT("Missing comment closure "),
	
	;
	
	String english;
	
	LexErrors(String english) {
		this.english = english;
	}
	
	public String prefix() {return english;}
	
	public void print(Lexer l) {
		System.err.println(english + "line " + l.getLinenum() + ", character " + l.getCharnum() + ": '" + l.getC() + "'");
	}
}
