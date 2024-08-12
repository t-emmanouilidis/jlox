package com.temma.lox;

record SetExpr(Expr object, Token name, Expr value) implements Expr {

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.visitSetExpr(this);
    }
}
