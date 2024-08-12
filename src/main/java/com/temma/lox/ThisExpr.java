package com.temma.lox;

record ThisExpr(Token keyword) implements Expr {

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.visitThisExpr(this);
    }
}
