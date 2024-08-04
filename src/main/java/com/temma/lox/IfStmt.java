package com.temma.lox;

record IfStmt(Expr condition, Stmt thenBranch, Stmt elseBranch) implements Stmt {

	@Override
	public void accept(StmtVisitor visitor) {
		visitor.visitIfStmt(this);
	}
}
