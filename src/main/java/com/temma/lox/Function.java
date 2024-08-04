package com.temma.lox;

import java.util.List;

record Function(Token name, List<Token> params, List<Stmt> body) implements Stmt {

	@Override
	public void accept(StmtVisitor visitor) {
		visitor.visitFunctionDecl(this);
	}

}
