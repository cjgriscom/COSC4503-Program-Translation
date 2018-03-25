package io.chandler.cosc4503.assign3;

import io.chandler.cosc4503.assign3.tree.*;

public interface Visitor {

	void visit(AssignStatement node);
	void visit(Block node);
	void visit(BreakStatement node);
	void visit(CaseStatement node);
	void visit(DefaultStatement node);
	void visit(ExitStatement node);
	void visit(FunctionCall node);
	void visit(MainFunction node);
	void visit(Program node);
	void visit(ReturningStatement node);
	void visit(Statement node);
	void visit(SwitchStatement node);
	void visit(Type node);
	void visit(VarDeclaration node);
	void visit(WhileStatement node);
}
