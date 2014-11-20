package de.hotware.lucene.extension.filter.tagging;

/**
 * Interface to let the user decide how tagged terms get stored into the
 * index
 * 
 * @author Martin
 */
public interface IndexFormatProvider {

	/**
	 * @param tagName
	 *            name of the found tag
	 * @param term
	 *            the original term
	 * @return the term to store in the index
	 */
	public String produce(String tagName, String term);

}