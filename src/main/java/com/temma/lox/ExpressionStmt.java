package com.temma.lox;

public record ExpressionStmt(Expr expression) implements Stmt {

    @Override
    public void accept(StmtVisitor visitor) {
        visitor.visitExpressionStmt(this);
    }
}
