package com.temma.interpreter.crafting;

import static com.temma.interpreter.crafting.TokenType.AND;
import static com.temma.interpreter.crafting.TokenType.BANG;
import static com.temma.interpreter.crafting.TokenType.BANG_EQUAL;
import static com.temma.interpreter.crafting.TokenType.CLASS;
import static com.temma.interpreter.crafting.TokenType.COMMA;
import static com.temma.interpreter.crafting.TokenType.DOT;
import static com.temma.interpreter.crafting.TokenType.ELSE;
import static com.temma.interpreter.crafting.TokenType.EQUAL;
import static com.temma.interpreter.crafting.TokenType.EQUAL_EQUAL;
import static com.temma.interpreter.crafting.TokenType.FALSE;
import static com.temma.interpreter.crafting.TokenType.FOR;
import static com.temma.interpreter.crafting.TokenType.FUN;
import static com.temma.interpreter.crafting.TokenType.GREATER;
import static com.temma.interpreter.crafting.TokenType.GREATER_EQUAL;
import static com.temma.interpreter.crafting.TokenType.IDENTIFIER;
import static com.temma.interpreter.crafting.TokenType.IF;
import static com.temma.interpreter.crafting.TokenType.LEFT_BRACE;
import static com.temma.interpreter.crafting.TokenType.LEFT_PAREN;
import static com.temma.interpreter.crafting.TokenType.LESS;
import static com.temma.interpreter.crafting.TokenType.LESS_EQUAL;
import static com.temma.interpreter.crafting.TokenType.MINUS;
import static com.temma.interpreter.crafting.TokenType.NIL;
import static com.temma.interpreter.crafting.TokenType.NUMBER;
import static com.temma.interpreter.crafting.TokenType.OR;
import static com.temma.interpreter.crafting.TokenType.PLUS;
import static com.temma.interpreter.crafting.TokenType.PRINT;
import static com.temma.interpreter.crafting.TokenType.RETURN;
import static com.temma.interpreter.crafting.TokenType.RIGHT_BRACE;
import static com.temma.interpreter.crafting.TokenType.RIGHT_PAREN;
import static com.temma.interpreter.crafting.TokenType.SEMICOLON;
import static com.temma.interpreter.crafting.TokenType.SLASH;
import static com.temma.interpreter.crafting.TokenType.STAR;
import static com.temma.interpreter.crafting.TokenType.STRING;
import static com.temma.interpreter.crafting.TokenType.SUPER;
import static com.temma.interpreter.crafting.TokenType.THIS;
import static com.temma.interpreter.crafting.TokenType.TRUE;
import static com.temma.interpreter.crafting.TokenType.VAR;
import static com.temma.interpreter.crafting.TokenType.WHILE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class Scanner {

    private static final Map<String, TokenType> KEYWORDS = Map.ofEntries(
          Map.entry("and", AND),
          Map.entry("class", CLASS),
          Map.entry("else", ELSE),
          Map.entry("false", FALSE),
          Map.entry("for", FOR),
          Map.entry("fun", FUN),
          Map.entry("if", IF),
          Map.entry("nil", NIL),
          Map.entry("or", OR),
          Map.entry("print", PRINT),
          Map.entry("return", RETURN),
          Map.entry("super", SUPER),
          Map.entry("this", THIS),
          Map.entry("true", TRUE),
          Map.entry("var", VAR),
          Map.entry("while", WHILE));

    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(':
                addToken(LEFT_PAREN);
                break;
            case ')':
                addToken(RIGHT_PAREN);
                break;
            case '{':
                addToken(LEFT_BRACE);
                break;
            case '}':
                addToken(RIGHT_BRACE);
                break;
            case ',':
                addToken(COMMA);
                break;
            case '.':
                addToken(DOT);
                break;
            case '-':
                addToken(MINUS);
                break;
            case '+':
                addToken(PLUS);
                break;
            case ';':
                addToken(SEMICOLON);
                break;
            case '*':
                addToken(STAR);
                break;
            case '!':
                addToken(matchNext('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(matchNext('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(matchNext('<') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(matchNext('>') ? GREATER_EQUAL : GREATER);
                break;
            case '/':
                if (matchNext('/')) {
                    while (peek() != '\n' && !isAtEnd()) {
                        advance();
                    }
                } else {
                    addToken(SLASH);
                }
                break;
            case ' ':
            case '\t':
            case '\r':
                break;
            case '\n':
                line++;
                break;
            case '"':
                string();
                break;
            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    Lox.error(line, "Unexpected character.");
                }
        }
    }

    private void identifier() {
        while (isAlphanumeric(peek())) {
            advance();
        }

        String text = source.substring(start, current);
        TokenType type = KEYWORDS.get(text);
        if (type == null) {
            type = IDENTIFIER;
        }
        addToken(type);
    }

    private boolean isAlphanumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
              (c >= 'A' && c <= 'Z') ||
              c == '_';
    }

    private void number() {
        while (isDigit(peek())) {
            advance();
        }

        if (peek() == '.' && isDigit(peekNext())) {
            advance();

            while (isDigit(peek())) {
                advance();
            }
        }

        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') {
                line++;
            }
            advance();
        }

        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.");
            return;
        }

        advance();

        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    private char peek() {
        if (isAtEnd()) {
            return '\0';
        }
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) {
            return '\0';
        }
        return source.charAt(current + 1);
    }

    private boolean matchNext(char expected) {
        if (isAtEnd()) {
            return false;
        }
        if (source.charAt(current) != expected) {
            return false;
        }
        current++;
        return true;
    }

    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(TokenType tokenType) {
        addToken(tokenType, null);
    }

    private void addToken(TokenType tokenType, Object literal) {
        tokens.add(new Token(tokenType, source.substring(start, current), literal, line));
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }
}
