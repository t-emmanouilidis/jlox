package com.temma.lox;

interface StmtVisitor {

    default void visit(Stmt stmt) {
        stmt.accept(this);
    }

    void visitExpressionStmt(ExpressionStmt stmt);

    void visitPrintStmt(Print stmt);

    void visitVariableDeclaration(VarDeclaration varDeclaration);

	void visitBlock(Block block);
}
