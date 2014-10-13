package de.hotware.lucene.extension.manager;

import java.io.IOException;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;

import de.hotware.lucene.extension.bean.BeanConverter;
import de.hotware.lucene.extension.bean.BeanInformationCache;

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
	 * returns the <b>shared</b> {@link org.apache.lucene.index.IndexWriter}
	 * used in this instance. <br />
	 * <br />
	 * so if an {@link java.lang.OutOfMemoryError} occurs, be sure to call
	 * {@link #shouldReopenIndexWriter()}
	 */
	public IndexWriter getIndexWriter();

	/**
	 * @throws IOException
	 *             if the (re-)opening of a IndexSearcher failed
	 */
	public IndexSearcher getIndexSearcher() throws IOException;

	/**
	 * tells the manager that the index-writer has to be re-opened
	 * 
	 * @throws IOException
	 */
	public void shouldReopenIndexWriter() throws IOException;

	/**
	 * closes everything (except for the directory)
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException;

}
