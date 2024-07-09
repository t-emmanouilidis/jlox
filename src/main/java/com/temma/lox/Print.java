package com.temma.lox;

public record Print(Expr value) implements Stmt {

    @Override
    public void accept(StmtVisitor visitor) {
        visitor.visitPrintStmt(this);
    }
}
