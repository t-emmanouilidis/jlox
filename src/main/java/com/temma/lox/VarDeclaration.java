package com.temma.lox;

public record VarDeclaration(Token name, Expr initializer) implements Stmt {

    @Override
    public void accept(StmtVisitor visitor) {
        visitor.visitVariableDeclaration(this);
    }
}
