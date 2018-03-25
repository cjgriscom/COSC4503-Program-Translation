package io.chandler.cosc4503.assign3;

import io.chandler.cosc4503.assign3.Parser;
import io.chandler.cosc4503.assign3.ParserToken;

/*
Chandler Griscom
Program Translation
Assignment 2
Lexer Error Strings
*/
public enum ParseErrors {
	
	EXPECTED("Expected "),
	INCOMPLETE("Incomplete parsing "),
	;

	private static boolean ALLOW_ERROR = true;
	private static boolean DUMP_STACKTRACE = false;
	String english;
	
	ParseErrors(String english) {
		this.english = english;
	}
	
	public String prefix() {return english;}
	
	public boolean throwWith(ParserToken.Type type, Parser p) {
		System.err.println(english + type + " at line " + p.getCurToken().getLinenum() + ", character " + p.getCurToken().getCharnum() + "; found " + p.getCurToken().getType() + ":" + p.getCurToken().getData());
		if (DUMP_STACKTRACE) throw new RuntimeException();
		return ALLOW_ERROR;
	}
	
	public boolean throwWith(ParserToken.Type type, String data, Parser p) {
		System.err.println(english + type + ":\"" + data + "\" at line " + p.getCurToken().getLinenum() + ", character " + p.getCurToken().getCharnum() + "; found " + p.getCurToken().getType() + ":" + p.getCurToken().getData());
		if (DUMP_STACKTRACE) throw new RuntimeException();
		return ALLOW_ERROR;
	}
	
	public boolean throwWith(String data, Parser p) {
		System.err.println(english + "\"" + data + "\" at line " + p.getCurToken().getLinenum() + ", character " + p.getCurToken().getCharnum() + "; found " + p.getCurToken().getType() + ":" + p.getCurToken().getData());
		if (DUMP_STACKTRACE) throw new RuntimeException();
		return ALLOW_ERROR;
	}
	
	public boolean throwErr(Parser p) {
		System.err.println(english + "at line " + p.getCurToken().getLinenum() + ", character " + p.getCurToken().getCharnum() + "; found " + p.getCurToken().getType() + ":" + p.getCurToken().getData());
		if (DUMP_STACKTRACE) throw new RuntimeException();
		return ALLOW_ERROR;
	}
}
