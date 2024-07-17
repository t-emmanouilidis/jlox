package com.temma.lox;

class AstPrinter implements ExprVisitor<String> {

    @Override
    public String visit(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public String visitBinaryExpr(Binary expr) {
        return parenthesize(expr.operator().lexeme,
              expr.left(), expr.right());
    }

    @Override
    public String visitGroupingExpr(Grouping expr) {
        return parenthesize("group", expr.expression());
    }

    @Override
    public String visitLiteralExpr(Literal expr) {
        if (expr.value() == null) {
            return "nil";
        }
        return expr.value().toString();
    }

    @Override
    public String visitUnaryExpr(Unary expr) {
        return parenthesize(expr.operator().lexeme, expr.right());
    }

    @Override
    public String visitVariableExpr(Variable variable) {
        return "";
    }

    private String parenthesize(String name, Expr... exprs) {
        var sb = new StringBuilder();
        sb.append("(")
              .append(name);
        for (Expr expr : exprs) {
            sb.append(" ").append(expr.accept(this));
        }
        return sb.append(")").toString();
    }

    public static void main(String[] args) {
        Expr expression = new Binary(
              new Unary(
                    new Token(TokenType.MINUS, "-", null, 1),
                    new Literal(123)),
              new Token(TokenType.STAR, "*", null, 1),
              new Grouping(new Literal(45.67)));
        System.out.println(new AstPrinter().visit(expression));
    }

}
