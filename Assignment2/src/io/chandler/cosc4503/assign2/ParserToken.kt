package io.chandler.cosc4503.assign2

/*
 Chandler Griscom
 Program Translation
 Assignment 2
 Lever Token Class
 */
class ParserToken constructor(public final val type: Type, public final val data: String,
						public final val linenum: Int = 0, public final val charnum: Int = 0) {

	enum class Type {
		BEGIN, END,
		NAMESPACE, USING, INCLUDE, NUM, CHAR, ID,
		OPERATOR, EQUAL, BANG, ASTERISK, L_ANGLE, R_ANGLE, QUESTION_MARK, COMMA, SEMICOLON, COLON, POUND,
		//L_SINGLE_QUOTE, R_SINGLE_QUOTE, L_DOUBLE_QUOTE, R_DOUBLE_QUOTE,
		L_PAREN, R_PAREN, L_SQUARE, R_SQUARE, L_CURLY, R_CURLY
	}
	
	
}