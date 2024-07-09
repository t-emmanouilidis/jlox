package com.temma.lox;

public interface Stmt {

    void accept(StmtVisitor visitor);

}
