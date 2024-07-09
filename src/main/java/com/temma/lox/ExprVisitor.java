package com.temma.lox;

interface ExprVisitor<U> {

    default U visit(Expr expr) {
        return expr.accept(this);
    }

    U visitBinaryExpr(Binary expr);

    U visitGroupingExpr(Grouping expr);

    U visitLiteralExpr(Literal expr);

    U visitUnaryExpr(Unary expr);

    U visitVariableExpr(Variable variable);
}
