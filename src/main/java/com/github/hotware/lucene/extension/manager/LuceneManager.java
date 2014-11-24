/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <martinbraun123@aol.com> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Martin Braun
 * ----------------------------------------------------------------------------
 */
package com.github.hotware.lucene.extension.manager;

import java.io.IOException;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ReferenceManager;

import com.github.hotware.lucene.extension.bean.converter.BeanConverter;
import com.github.hotware.lucene.extension.bean.field.BeanInformationCache;

/**
 * A LuceneManager helps you to handle all Low-Level Index-Management.
 * 
 * @author Martin Braun
 */
public interface LuceneManager extends AutoCloseable {

	/**
	 * @return the BeanInformationCache used by this instance
	 */
	public BeanInformationCache getBeanInformationCache();

	/**
	 * @return the (thread-safe) BeanConverter used by this instance
	 */
	public BeanConverter getBeanConverter();

	/**
	 * returns a <b>new</b> {@link org.apache.lucene.index.IndexWriter} <br />
	 * be sure to close it.
	 * @throws IOException 
	 */
	public IndexWriter getIndexWriter(IndexWriterConfig config) throws IOException;

	public ReferenceManager<IndexSearcher> getIndexSearcherManager();

	/**
	 * closes everything (except for the directory)
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException;

}
