package io.chandler.cosc4503.assign3;

import java.util.ArrayList;

import io.chandler.cosc4503.assign3.tree.AssignStatement;
import io.chandler.cosc4503.assign3.tree.Block;
import io.chandler.cosc4503.assign3.tree.BreakStatement;
import io.chandler.cosc4503.assign3.tree.CaseStatement;
import io.chandler.cosc4503.assign3.tree.DefaultStatement;
import io.chandler.cosc4503.assign3.tree.ExitStatement;
import io.chandler.cosc4503.assign3.tree.FunctionCall;
import io.chandler.cosc4503.assign3.tree.MainFunction;
import io.chandler.cosc4503.assign3.tree.Program;
import io.chandler.cosc4503.assign3.tree.ReturningStatement;
import io.chandler.cosc4503.assign3.tree.Statement;
import io.chandler.cosc4503.assign3.tree.SwitchStatement;
import io.chandler.cosc4503.assign3.tree.Type;
import io.chandler.cosc4503.assign3.tree.VarDeclaration;
import io.chandler.cosc4503.assign3.tree.WhileStatement;

public class PrettyPrintVisitor implements Visitor {
	
	int indent = 0;
	
	public PrettyPrintVisitor(Program p) {
		p.accept(this);
	}
	
	public void pushIndent() {
		indent += 2;
	}

	public void popIndent() {
		indent -= 2;
	}
	
	public void printlnWithIndent(String s) {
		for (int i = 0; i < indent; i++) System.out.print(" ");
		System.out.println(s);
	}
	
	public void printWithIndent(String s) {
		for (int i = 0; i < indent; i++) System.out.print(" ");
		System.out.print(s);
	}
	
	public void print(String s) {
		System.out.print(s);
	}
	
	public void println(String s) {
		System.out.println(s);
	}
	
	@Override
	public void visit(AssignStatement node) {
		print(" = " + node.getNum());
	}

	@Override
	public void visit(Block node) {
		println("{");
		pushIndent();
		for (Statement s : node.getBlock()) {
			printWithIndent("");
			s.accept(this);
		}
		popIndent();
		printWithIndent("}");
	}

	@Override
	public void visit(BreakStatement node) {
		print("break");
	}

	private void printCaseBlock(ArrayList<Statement> block) {
		if (block.size() == 0) println("");
		else if (block.size() == 1) block.get(0).accept(this);
		else {
			println("{");
			pushIndent();
			for (Statement s : block) {

				printWithIndent("");
				s.accept(this);
			}
			popIndent();
			printlnWithIndent("}");
		}
	}
	
	@Override
	public void visit(CaseStatement node) {
		printWithIndent("case ");
		if (node.getId() != null) print(node.getId());
		else if (node.getLiteral() != null) print(node.getLiteral());
		print(": ");
		printCaseBlock(node.getBlock());
	}

	@Override
	public void visit(DefaultStatement node) {
		printWithIndent("default: ");
		printCaseBlock(node.getBlock());
	}

	@Override
	public void visit(ExitStatement node) {
		print("(" + node.getValue() + ")");
	}

	@Override
	public void visit(FunctionCall node) {
		print("()");
	}

	@Override
	public void visit(MainFunction node) {
		printWithIndent("int main()");
		pushIndent();
	}

	@Override
	public void visit(Program node) {
		node.getMainFunction().accept(this);
		println(" {");
		for (Statement s : node.getStatements()) {
			printWithIndent("");
			s.accept(this);
		}
		popIndent();
		printlnWithIndent("}");
	}

	@Override
	public void visit(ReturningStatement node) {
		if (node.getResolvedNode() != null) {
			print(node.getId());
			node.getResolvedNode().accept(this);
		}
		else if (node.getLiteral() != null) print(node.getLiteral());
		else if (node.getId() != null) print(node.getId());
	}

	@Override
	public void visit(Statement node) {
		if (node.getExitStatement() != null) print("exit");
		
		if (node.getBlock() != null) node.getBlock().accept(this);
		else if (node.getBreakStatement() != null) node.getBreakStatement().accept(this);
		else if (node.getExitStatement() != null) node.getExitStatement().accept(this);
		else if (node.getReturningStatement() != null)  node.getReturningStatement().accept(this);
		else if (node.getSwitchStatement() != null) node.getSwitchStatement().accept(this);
		else if (node.getVarDeclaration() != null) node.getVarDeclaration().accept(this);
		else if (node.getWhileStatement() != null) node.getWhileStatement().accept(this);
		
		if (node.getBreakStatement() != null || 
				node.getExitStatement() != null ||
				node.getReturningStatement() != null) {

			println(";");
		} else {
			println("");
		}
	}

	@Override
	public void visit(SwitchStatement node) {
		print("switch (");
		node.getCondition().accept(this);
		println(") {");
		pushIndent();
		for (CaseStatement s : node.getCases()) {
			s.accept(this);
		}
		if (node.getDefaultCase() != null) node.getDefaultCase().accept(this);
		popIndent();
		printWithIndent("}");
	}

	@Override
	public void visit(Type node) {
		print(node.getType());
	}

	@Override
	public void visit(VarDeclaration node) {
		node.getType().accept(this);
		print(" " + node.getId());
		if (node.getHasAssignStatement()) {
			node.getAssignStatement().accept(this);
		}
		print(";");
	}

	@Override
	public void visit(WhileStatement node) {
		print("while (");
		node.getCondition().accept(this);
		println(") {");
		pushIndent();
		for (Statement s : node.getBlock()) {
			printWithIndent("");
			s.accept(this);
		}
		popIndent();
		printWithIndent("}");
		
	}
	
}
