package com.temma.lox;

import java.util.List;

record Block(List<Stmt> stmts) implements Stmt {

	@Override
	public void accept(StmtVisitor visitor) {
		visitor.visitBlock(this);
	}

}
