package com.temma.lox;

import java.util.List;

record LoxFunction(Function declaration) implements LoxCallable {

	@Override
	public Object call(Interpreter interpreter, List<Object> arguments) {
		Environment environment = new Environment(interpreter.globals);
		for (int i = 0; i < declaration.params().size(); i++) {
			environment.define(declaration.params().get(i).lexeme, arguments.get(i));
		}
		interpreter.executeBlock(declaration.body(), environment);
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
