package com.temma.lox;

import java.util.HashMap;
import java.util.Map;

public class Environment {

    private final Map<String, Object> values = new HashMap<>();

    public void define(String name, Object value) {
        this.values.put(name, value);
    }

    Object get(Token name) {
        Object value = values.get(name.lexeme);
        if (value == null) {
            throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'");
        }
        return value;
    }

}
