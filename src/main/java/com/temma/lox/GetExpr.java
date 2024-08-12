package com.temma.lox;

record GetExpr(Expr object, Token name) implements Expr {

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.visitGetExpr(this);
    }
}
