package com.temma.lox;

interface StmtVisitor {

    default void visit(Stmt stmt) {
        stmt.accept(this);
    }

    void visitExpressionStmt(ExpressionStmt stmt);

    void visitPrintStmt(Print stmt);

    void visitVariableDeclaration(VarDeclaration varDeclaration);

	void visitBlock(Block block);

	void visitIfStmt(IfStmt ifStmt);

	void visitWhileStmt(WhileStmt whileStmt);

	void visitFunctionDecl(Function function);

	void visitReturnStmt(ReturnStmt return1);

    void visitClassDecl(ClassStmt classStmt);
}
