package com.github.hotware.lucene.extension.hsearch.manager;

import java.io.IOException;

public interface HibernateSearchLuceneManager extends AutoCloseable {

	public SearchFactory getSearchFactory();

	/**
	 * closes everything (except for the directory)
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException;

}