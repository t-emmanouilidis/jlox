package com.temma.lox;

public interface Visitor<T, U> {

    U visit(T node);

}
