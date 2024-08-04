package com.temma.lox;

import java.util.List;

record Call(Expr callee, Token paren, List<Expr> arguments) implements Expr {

	@Override
	public <R> R accept(ExprVisitor<R> visitor) {
		return visitor.visitCallExpr(this);
	}
}
