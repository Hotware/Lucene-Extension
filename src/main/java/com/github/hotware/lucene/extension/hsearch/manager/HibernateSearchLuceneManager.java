package com.github.hotware.lucene.extension.hsearch.manager;

import java.io.IOException;
import org.hibernate.search.indexes.IndexReaderAccessor;

import com.github.hotware.lucene.extension.bean.converter.BeanConverter;

public interface HibernateSearchLuceneManager extends AutoCloseable {

	/**
	 * @return the (thread-safe) BeanConverter used by this instance
	 */
	public BeanConverter getBeanConverter();


	public IndexReaderAccessor getIndexReaderAccessor();

	/**
	 * closes everything (except for the directory)
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException;

}
