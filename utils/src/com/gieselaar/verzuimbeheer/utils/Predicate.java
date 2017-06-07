package com.gieselaar.verzuimbeheer.utils;

public interface Predicate<T> {
	public enum __filterType{
		regularExpression,
		wildcard;
	}

	boolean search(T type, String regularExpression);
}
