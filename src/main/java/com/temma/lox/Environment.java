package com.temma.lox;

import java.util.HashMap;
import java.util.Map;

public class Environment {

    private final Map<String, Object> values = new HashMap<>();

    public void define(String name, Object value) {
        this.values.put(name, value);
    }

}
