package com.gieselaar.verzuimbeheer.utils;

import java.util.ArrayList;
import java.util.List;

import com.gieselaar.verzuimbeheer.utils.Predicate.__filterType;

/**
 * 
 * @author Tonny Gieselaar
 *
 * Utilities for Lists.
 */
public class ListUtils {
	/**
	 * 
	 * @param target
	 * 		The List to search in
	 * @param predicate
	 * 		The compare function which implements the Predicate<T> interface
	 * @param wildcard
	 * 		The wildcard string to search for, will be passed to the 
	 * 		Predicate<T>.search function. 
	 * @param type
	 * 		Indicates if the wildcard is a regular expression or a simple
	 * 		wildcard
	 * @return
	 */
	public static <T> List<T> applyFilter(List<T> target, Predicate<T> predicate, String wildcard, __filterType type){
		List<T> result = new ArrayList<T>();
		String filter;
		if (type == __filterType.wildcard)
			filter = WildcardRegEx.wildcardToRegex(wildcard);
		else
			filter = wildcard;
		for (T element: target){
			if (predicate.search(element, filter)){
				result.add(element);
			}
		}
		return result;
	}
}
