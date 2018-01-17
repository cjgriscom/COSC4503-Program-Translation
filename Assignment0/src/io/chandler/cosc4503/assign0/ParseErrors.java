package io.chandler.cosc4503.assign0;


public enum ParseErrors {
	
	NESTING_ERROR("Nesting error at "),
	EOF_NESTING_ERROR("EOF nesting error at "),
	EOF_INCOMPLETE("EOF with incomplete parsing at "),
	
	;
	
	String english;
	
	ParseErrors(String english) {
		this.english = english;
	}
	
	public String prefix() {return english;}
	
	public void print(Parser l) {
		System.err.println(english + "line " + l.getLinenum());
	}
}
