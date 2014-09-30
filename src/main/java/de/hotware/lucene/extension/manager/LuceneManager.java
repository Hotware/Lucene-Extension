package de.hotware.lucene.extension.manager;

import java.io.IOException;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;

import de.hotware.lucene.extension.bean.BeanConverter;
import de.hotware.lucene.extension.bean.BeanInformationCache;

/**
 * 
 * @author Martin Braun
 */
public interface LuceneManager extends AutoCloseable {
	
	public BeanInformationCache getBeanInformationCache();
	
	public BeanConverter getBeanConverter();
	
	public IndexWriter getIndexWriter();
	
	public IndexSearcher getIndexSearcher() throws IOException;
	
	/**
	 * tells the manager that the index-writer has to be re-opened
	 * @throws IOException 
	 */
	public void shouldReopenIndexWriter() throws IOException;
	
	/**
	 * closes everything (except for the directory)
	 * @throws IOException
	 */
	public void close() throws IOException;

}
