package io.chandler.cosc4503.assign3;

import io.chandler.cosc4503.assign3.tree.*;

/*
Program
|~~Main
|  |~~Block
|  |  |


 */

public class PostfixVisitor implements Visitor {
	
	boolean waitPref = true;
	boolean firstNode = true;
	boolean embed = false;
	int indent = 0;
	
	public PostfixVisitor(Program p) {
		p.accept(this);
	}
	
	private void indent() {
		for (int i = 0; i < indent; i++) System.out.print("  ");
	}
	
	public void printHeading(String s) {
		if (noPrefix()) indent();
		System.out.println(s);
	}
	
	private boolean noPrefix() {
		boolean temp = waitPref;
		waitPref = false;
		return !temp;
	}
	
	public void prefix(String s) {
		indent();
		System.out.print(s + ": ");
		waitPref = true;
	}
	
	public void satisfyText(String type, String data) {
		printHeading(type);
		indent++;
		indent();
		System.out.println(data);
		indent--;
	}
	
	public void println(String data) {
		if (noPrefix()) indent();
		System.out.println(data);
	}
	
	private void open(TreeNode node) {
		printHeading(node.getClass().getSimpleName());
		indent++;
	}
	private void close(TreeNode node) {
		indent--;
	}
	
	@Override
	public void visit(AssignStatement node) {
		open(node);
		prefix("Var");
		satisfyText("ID", node.getPrefID());
		prefix("Value");
		satisfyText("Literal", node.getNum());
		close(node);
	}

	@Override
	public void visit(Block node) {
		open(node);
		for (Statement s : node.getBlock()) {
			s.accept(this);
		}
		close(node);
	}

	@Override
	public void visit(BreakStatement node) {
		open(node);
		close(node);
	}
	
	@Override
	public void visit(CaseStatement node) {
		open(node);
		prefix("Selection");
		if (node.getId() != null) satisfyText("ID",node.getId());
		if (node.getLiteral() != null) satisfyText("Literal",node.getLiteral());
		prefix("Block");
		for (Statement s : node.getBlock()) s.accept(this);
		close(node);
	}

	@Override
	public void visit(DefaultStatement node) {
		open(node);
		prefix("Block");
		for (Statement s : node.getBlock()) s.accept(this);
		close(node);
	}

	@Override
	public void visit(ExitStatement node) {
		open(node);
		prefix("Argument");
		satisfyText("ID", node.getValue());
		close(node);
	}

	@Override
	public void visit(FunctionCall node) {
		open(node);
		prefix("Function");
		satisfyText("ID", node.getPrefID());
		close(node);
	}

	@Override
	public void visit(MainFunction node) {
		open(node);
		close(node);
	}

	@Override
	public void visit(Program node) {
		open(node);
		node.getMainFunction().accept(this);
		for (Statement s : node.getStatements()) {
			s.accept(this);
		}
		close(node);
	}

	@Override
	public void visit(ReturningStatement node) {
		open(node);
		if (node.getResolvedNode() != null) node.getResolvedNode().accept(this);
		else if (node.getLiteral() != null) satisfyText("Literal", node.getLiteral());
		else if (node.getId() != null) satisfyText("ID", node.getId());
		close(node);
	}

	@Override
	public void visit(Statement node) {
		open(node);
		if (node.getBlock() != null) node.getBlock().accept(this);
		else if (node.getBreakStatement() != null) node.getBreakStatement().accept(this);
		else if (node.getExitStatement() != null) node.getExitStatement().accept(this);
		else if (node.getReturningStatement() != null)  node.getReturningStatement().accept(this);
		else if (node.getSwitchStatement() != null) node.getSwitchStatement().accept(this);
		else if (node.getVarDeclaration() != null) node.getVarDeclaration().accept(this);
		else if (node.getWhileStatement() != null) node.getWhileStatement().accept(this);
		close(node);
	}

	@Override
	public void visit(SwitchStatement node) {
		open(node);
		prefix("Condition");
		node.getCondition().accept(this);
		for (CaseStatement s : node.getCases()) {
			s.accept(this);
		}
		if (node.getDefaultCase() != null) node.getDefaultCase().accept(this);
		close(node);
	}

	@Override
	public void visit(Type node) {
		open(node);
		println(node.getType());
		close(node);
	}

	@Override
	public void visit(VarDeclaration node) {
		open(node);
		node.getType().accept(this);
		satisfyText("ID", node.getId());
		if (node.getHasAssignStatement()) {
			node.getAssignStatement().accept(this);
		}
		close(node);
	}

	@Override
	public void visit(WhileStatement node) {
		open(node);
		prefix("Condition");
		node.getCondition().accept(this);
		for (Statement s : node.getBlock()) {
			s.accept(this);
		}
		close(node);
		
	}
	
}
