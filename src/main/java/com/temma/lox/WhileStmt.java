package com.temma.lox;

record WhileStmt(Expr condition, Stmt body) implements Stmt {

	@Override
	public void accept(StmtVisitor visitor) {
		visitor.visitWhileStmt(this);
	}
}
