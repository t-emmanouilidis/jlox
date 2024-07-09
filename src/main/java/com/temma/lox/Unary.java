package com.temma.lox;

record Unary(Token operator, Expr right) implements Expr {

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.visitUnaryExpr(this);
    }

}
