package com.github.hotware.lucene.extension.filter.tagging;

import java.util.Set;

import org.apache.lucene.util.Attribute;

public interface TagAttribute extends Attribute {
	
	/**
	 * @return the internal set (can still be modified)
	 */
	public Set<String> getTags();
	
	/**
	 * copies the array into the internal set
	 */
	public void addTags(String... tags);

	/**
	 * copies the iterable into the internal set
	 */
	public void addTags(Iterable<String> tags);

}
