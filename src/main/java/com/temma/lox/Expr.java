package com.temma.lox;

interface Expr {

    <R> R accept(ExprVisitor<R> visitor);
}
