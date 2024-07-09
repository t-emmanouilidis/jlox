package com.temma.lox;

record Literal(Object value) implements Expr {

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.visitLiteralExpr(this);
    }

}
