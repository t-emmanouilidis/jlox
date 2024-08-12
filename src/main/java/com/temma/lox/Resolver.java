package com.temma.lox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

class Resolver implements ExprVisitor<Void>, StmtVisitor {

    private final Interpreter interpreter;
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();
    private FunctionType currentFunction = FunctionType.NONE;

    Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    @Override
    public void visitBlock(Block block) {
        beginScope();
        resolve(block.stmts());
        endScope();
    }


    @Override
    public void visitExpressionStmt(ExpressionStmt stmt) {
        resolve(stmt.expression());
    }

    @Override
    public void visitPrintStmt(Print stmt) {
        resolve(stmt.value());
    }

    @Override
    public void visitVariableDeclaration(VarDeclaration varDeclaration) {
        declare(varDeclaration.name());
        if (varDeclaration.initializer() != null) {
            resolve(varDeclaration.initializer());
        }
        define(varDeclaration.name());
    }

    private void declare(Token name) {
        if (scopes.isEmpty()) {
            return;
        }
        Map<String, Boolean> scope = scopes.peek();
        if (scope.containsKey(name.lexeme)) {
            Lox.error(name, "Already a variable with this name in this scope.");
        }
        scope.put(name.lexeme, false);
    }

    private void define(Token name) {
        if (scopes.isEmpty()) {
            return;
        }
        scopes.peek().put(name.lexeme, true);
    }

    @Override
    public void visitIfStmt(IfStmt ifStmt) {
        resolve(ifStmt.condition());
        resolve(ifStmt.thenBranch());
        if (ifStmt.elseBranch() != null) {
            resolve(ifStmt.elseBranch());
        }
    }

    @Override
    public void visitWhileStmt(WhileStmt whileStmt) {
        resolve(whileStmt.condition());
        resolve(whileStmt.body());
    }

    @Override
    public void visitFunctionDecl(Function function) {
        declare(function.name());
        define(function.name());
        resolveFunction(function, FunctionType.FUNCTION);
    }

    private void resolveFunction(Function function, FunctionType type) {
        FunctionType enclosingFunction = currentFunction;
        currentFunction = type;
        beginScope();
        for (Token param : function.params()) {
            declare(param);
            define(param);
        }
        resolve(function.body());
        endScope();
        currentFunction = enclosingFunction;
    }

    @Override
    public void visitReturnStmt(ReturnStmt returnStmt) {
        if (currentFunction == FunctionType.NONE) {
            Lox.error(returnStmt.keyword(), "Can't return from top-level code.");
        }
        resolve(returnStmt.value());
    }

    @Override
    public Void visitBinaryExpr(Binary expr) {
        resolve(expr.left());
        resolve(expr.right());
        return null;
    }

    @Override
    public Void visitGroupingExpr(Grouping expr) {
        resolve(expr.expression());
        return null;
    }

    @Override
    public Void visitLiteralExpr(Literal expr) {
        return null;
    }

    @Override
    public Void visitUnaryExpr(Unary expr) {
        resolve(expr.right());
        return null;
    }

    @Override
    public Void visitVariableExpr(Variable variable) {
        if (!scopes.isEmpty() && scopes.peek().get(variable.name().lexeme) == Boolean.FALSE) {
            Lox.error(variable.name(), "Can't read local variable in its own initializer.");
        }
        resolveLocal(variable, variable.name());
        return null;
    }

    private void resolveLocal(Expr expr, Token name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name.lexeme)) {
                interpreter.resolve(expr, scopes.size() - 1 - i);
                return;
            }
        }
    }

    @Override
    public Void visitAssignExpr(Assign assign) {
        resolve(assign.value());
        resolveLocal(assign, assign.name());
        return null;
    }

    @Override
    public Void visitLogicalExpr(Logical logical) {
        resolve(logical.left());
        resolve(logical.right());
        return null;
    }

    @Override
    public Void visitCallExpr(Call call) {
        resolve(call.callee());
        for (Expr argument : call.arguments()) {
            resolve(argument);
        }
        return null;
    }

    private void endScope() {
        scopes.pop();
    }

    private void beginScope() {
        scopes.push(new HashMap<>());
    }

    void resolve(List<Stmt> statements) {
        statements.forEach(this::resolve);
    }

    private void resolve(Stmt stmt) {
        stmt.accept(this);
    }

    private void resolve(Expr expr) {
        expr.accept(this);
    }

    private enum FunctionType {
        NONE,
        FUNCTION
    }
}
