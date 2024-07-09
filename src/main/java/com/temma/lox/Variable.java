package com.temma.lox;

record Variable(Token name) implements Expr {

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.visitVariableExpr(this);
    }
}
