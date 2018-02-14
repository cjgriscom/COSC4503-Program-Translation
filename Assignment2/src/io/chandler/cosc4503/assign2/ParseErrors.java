package io.chandler.cosc4503.assign2;

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
	
	String english;
	
	ParseErrors(String english) {
		this.english = english;
	}
	
	public String prefix() {return english;}
	
	public boolean throwWith(ParserToken.Type type, Parser p) {
		System.err.println(english + type + " at line " + p.getCurToken().getLinenum() + ", character " + p.getCurToken().getCharnum() + ": " + p.getCurToken().getType() + ":" + p.getCurToken().getData());
		if (1==1)throw new RuntimeException();
		return false;
	}
	
	public boolean throwWith(ParserToken.Type type, String data, Parser p) {
		System.err.println(english + type + ":\"" + data + "\" at line " + p.getCurToken().getLinenum() + ", character " + p.getCurToken().getCharnum() + ": " + p.getCurToken().getType() + ":" + p.getCurToken().getData());
		if (1==1)throw new RuntimeException();
		return false;
	}
	
	public boolean throwWith(String data, Parser p) {
		System.err.println(english + "\"" + data + "\" at line " + p.getCurToken().getLinenum() + ", character " + p.getCurToken().getCharnum() + ": " + p.getCurToken().getType() + ":" + p.getCurToken().getData());
		if (1==1)throw new RuntimeException();
		return false;
	}
	
	public boolean throwErr(Parser p) {
		System.err.println(english + "at line " + p.getCurToken().getLinenum() + ", character " + p.getCurToken().getCharnum() + ": " + p.getCurToken().getType() + ":" + p.getCurToken().getData());
		if (1==1)throw new RuntimeException();
		return false;
	}
}
