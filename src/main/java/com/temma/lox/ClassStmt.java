package com.temma.lox;

import java.util.List;

public record ClassStmt(Token name, Variable superclass, List<Function> methods) implements Stmt {

	@Override
	public void accept(StmtVisitor visitor) {
		visitor.visitClassDecl(this);
	}
}
