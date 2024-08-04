package com.temma.lox;

record Logical(Expr left, Token operator, Expr right) implements Expr {

	@Override
	public <R> R accept(ExprVisitor<R> visitor) {
		return visitor.visitLogicalExpr(this);
	}

}
