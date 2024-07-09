package com.temma.lox;

public interface StmtVisitor {

    default void visit(Stmt stmt) {
        stmt.accept(this);
    }

    void visitExpressionStmt(ExpressionStmt stmt);

    void visitPrintStmt(Print stmt);

    void visitVariableDeclaration(VarDeclaration varDeclaration);
}
