package com.temma.lox;

record ReturnStmt(Token keyword, Expr value) implements Stmt {

	@Override
	public void accept(StmtVisitor visitor) {
		visitor.visitReturnStmt(this);
	}
}
