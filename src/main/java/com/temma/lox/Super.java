package com.temma.lox;

record Super(Token keyword, Token method) implements Expr {

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.visitSuperExpr(this);
    }
    
}
