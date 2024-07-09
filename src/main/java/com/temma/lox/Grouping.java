package com.temma.lox;

record Grouping(Expr expression) implements Expr {

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.visitGroupingExpr(this);
    }

}
