package com.github.hotware.lucene.extension.filter.tagging;

import java.util.function.BinaryOperator;

/**
 * Interface to let the user decide how tagged terms get stored into the
 * index
 * 
 * @author Martin
 */
public interface IndexFormatProvider extends BinaryOperator<String> {

	/**
	 * @param tagName
	 *            name of the found tag
	 * @param term
	 *            the original term
	 * @return the term to store in the index
	 */
	public String produce(String tagName, String term);
	
	public default String apply(String first, String second) {
		return this.produce(first, second);
	};

}