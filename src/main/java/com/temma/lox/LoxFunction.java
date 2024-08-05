package com.temma.lox;

import java.util.List;

record LoxFunction(Function declaration, Environment closure) implements LoxCallable {

	@Override
	public Object call(Interpreter interpreter, List<Object> arguments) {
		Environment environment = new Environment(closure);
		for (int i = 0; i < declaration.params().size(); i++) {
			environment.define(declaration.params().get(i).lexeme, arguments.get(i));
		}
		try {
			interpreter.executeBlock(declaration.body(), environment);
		} catch (Return returnValue) {
			return returnValue.value;
		}
		return null;
	}

	@Override
	public int arity() {
		return declaration.params().size();
	}

	@Override
	public String toString() {
		return "<fn " + declaration.name().lexeme + ">";
	}
}
