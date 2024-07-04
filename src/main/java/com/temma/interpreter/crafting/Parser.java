package com.temma.interpreter.crafting;

import java.util.List;

public class Parser {

	private static class ParseError extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}

	private final List<Token> tokens;
	private int current = 0;

	Parser(List<Token> tokens) {
		this.tokens = tokens;
	}
	
	Expr parse() {
		try {
			return expression();
		} catch (ParseError error) {
			return null;
		}
	}

	private Expr expression() {
		return equality();
	}

	private Expr equality() {
		Expr expr = comparison();
		while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
			Token operator = previous();
			Expr right = comparison();
			expr = new Binary(expr, operator, right);
		}
		return expr;
	}

	private Expr comparison() {
		Expr expr = term();
		while (match(TokenType.LESS_EQUAL, TokenType.LESS, TokenType.GREATER, TokenType.GREATER_EQUAL)) {
			Token operator = previous();
			Expr right = term();
			expr = new Binary(expr, operator, right);
		}
		return expr;
	}

	private Expr term() {
		Expr expr = factor();
		while (match(TokenType.MINUS, TokenType.PLUS)) {
			Token operator = previous();
			Expr right = factor();
			expr = new Binary(expr, operator, right);
		}
		return expr;
	}

	private Expr factor() {
		Expr expr = unary();
		while (match(TokenType.STAR, TokenType.SLASH)) {
			Token operator = previous();
			Expr right = unary();
			expr = new Binary(expr, operator, right);
		}
		return expr;
	}

	private Expr unary() {
		if (match(TokenType.BANG, TokenType.MINUS)) {
			Token operator = previous();
			Expr right = unary();
			return new Unary(operator, right);
		}
		return primary();
	}

	private Expr primary() {
		if (match(TokenType.FALSE)) {
			return new Literal(false);
		}
		if (match(TokenType.TRUE)) {
			return new Literal(true);
		}
		if (match(TokenType.NIL)) {
			return new Literal(null);
		}
		if (match(TokenType.NUMBER, TokenType.STRING)) {
			return new Literal(previous().literal);
		}
		if (match(TokenType.LEFT_PAREN)) {
			Expr expr = expression();
			consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
			return new Grouping(expr);
		}
		throw error(peek(), "Expect expression.");
	}

	private void synchronize() {
		advance();

		while (!isAtEnd()) {
			if (previous().type == TokenType.SEMICOLON) {
				return;
			}

			switch (peek().type) {
			case CLASS:
			case FUN:
			case VAR:
			case FOR:
			case IF:
			case WHILE:
			case PRINT:
			case RETURN:
				return;
			default:
				advance();
			}
		}
	}

	private Token consume(TokenType type, String message) {
		if (check(type)) {
			return advance();
		}
		throw error(peek(), message);
	}

	private boolean match(TokenType... tokenTypes) {
		for (TokenType type : tokenTypes) {
			if (check(type)) {
				advance();
				return true;
			}
		}
		return false;
	}

	private ParseError error(Token token, String message) {
		Lox.error(token, message);
		return new ParseError();
	}

	private Token advance() {
		if (!isAtEnd()) {
			current++;
		}
		return previous();
	}

	private boolean check(TokenType type) {
		if (isAtEnd()) {
			return false;
		}
		return peek().type == type;
	}

	private boolean isAtEnd() {
		return peek().type == TokenType.EOF;
	}

	private Token peek() {
		return tokens.get(current);
	}

	private Token previous() {
		return tokens.get(current - 1);
	}

}
