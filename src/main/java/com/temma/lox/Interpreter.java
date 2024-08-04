package com.temma.lox;

import java.util.List;

class Interpreter implements ExprVisitor<Object>, StmtVisitor {

    private Environment environment = new Environment();

    void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }
    }

    private String stringify(Object object) {
        if (object == null) {
            return "nil";
        }
        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }
        return object.toString();
    }

    @Override
    public Object visitBinaryExpr(Binary expr) {
        Object left = evaluate(expr.left());
        Object right = evaluate(expr.right());

        switch (expr.operator().type) {
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
            case GREATER:
                checkNumberOperands(expr.operator(), left, right);
                return (double) left > (double) right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator(), left, right);
                return (double) left >= (double) right;
            case LESS:
                checkNumberOperands(expr.operator(), left, right);
                return (double) left < (double) right;
            case LESS_EQUAL:
                checkNumberOperands(expr.operator(), left, right);
                return (double) left <= (double) right;
            case MINUS:
                checkNumberOperands(expr.operator(), left, right);
                return (double) left - (double) right;
            case PLUS:
                if (left instanceof Double d1 && right instanceof Double d2) {
                    return d1 + d2;
                } else if (left instanceof String s1 && right instanceof String s2) {
                    return s1 + s2;
                }
                throw new RuntimeError(expr.operator(), "Operands must be two numbers or two strings");
            case SLASH:
                checkNumberOperands(expr.operator(), left, right);
                return (double) left / (double) right;
            case STAR:
                checkNumberOperands(expr.operator(), left, right);
                return (double) left * (double) right;
            default:
        }
        return null;
    }

    @Override
    public Object visitGroupingExpr(Grouping expr) {
        return evaluate(expr.expression());
    }

    @Override
    public Object visitLiteralExpr(Literal expr) {
        return expr.value();
    }

    @Override
    public Object visitUnaryExpr(Unary expr) {
        Object right = evaluate(expr.right());

        if (expr.operator().type == TokenType.MINUS) {
            checkNumberOperand(expr.operator(), right);
            return -(double) right;
        } else if (expr.operator().type == TokenType.BANG) {
            return !isTruthy(right);
        }
        return null;
    }

    @Override
    public void visitVariableDeclaration(VarDeclaration stmt) {
        Object value = null;
        if (stmt.initializer() != null) {
            value = evaluate(stmt.initializer());
        }

        environment.define(stmt.name().lexeme, value);
    }

    @Override
    public Object visitVariableExpr(Variable variable) {
        return environment.get(variable.name());
    }

    @Override
    public Object visitAssignExpr(Assign assign) {
        Object value = evaluate(assign.value());
        environment.assign(assign.name(), value);
        return value;
    }

    @Override
    public void visitPrintStmt(Print stmt) {
        Object value = evaluate(stmt.value());
        System.out.println(stringify(value));
    }

    @Override
    public void visitExpressionStmt(ExpressionStmt stmt) {
        evaluate(stmt.expression());
    }
    
    @Override
	public void visitBlock(Block block) {
		executeBlock(block.stmts(), new Environment(environment));
	}
    
    @Override
    public void visitIfStmt(IfStmt ifStmt) {
    	if (isTruthy(evaluate(ifStmt.condition()))) {
    		execute(ifStmt.thenBranch());
    	} else if (ifStmt.elseBranch() != null) {
    		execute(ifStmt.elseBranch());
    	}
    }
    
    @Override
	public Object visitLogicalExpr(Logical expr) {
    	Object left = evaluate(expr.left());
    	if (expr.operator().type == TokenType.OR) {
    		if (isTruthy(left)) {
    			return left;
    		}
    	} else {
    		if (!isTruthy(left)) {
    			return left;
    		}
    	}
		return evaluate(expr.right());
	}

    private void executeBlock(List<Stmt> stmts, Environment environment) {
		Environment previous = this.environment;
		try {
			this.environment = environment;
			
			for (Stmt stmt : stmts) {
				execute(stmt);
			}
		} finally {
			this.environment = previous;
		}
	}
    
    private void execute(Stmt stmt) {
    	stmt.accept(this);
    }
    
    private Object evaluate(Expr expr) {
    	return expr.accept(this);
    }

	private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) {
            return;
        }
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperands(Token operator, Object leftOperand, Object rightOperand) {
        if (leftOperand instanceof Double && rightOperand instanceof Double) {
            return;
        }
        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null) {
            return false;
        }
        return a.equals(b);
    }

    private boolean isTruthy(Object object) {
        if (object == null) {
            return false;
        }
        if (object instanceof Boolean bool) {
            return bool;
        }
        return true;
    }
}
