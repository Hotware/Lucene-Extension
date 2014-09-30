package de.hotware.lucene.extension.manager;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;

import de.hotware.lucene.extension.bean.BeanConverter;
import de.hotware.lucene.extension.bean.BeanInformationCache;

/**
 * 
 * @author Martin Braun
 */
public interface LuceneManager {
	
	public BeanInformationCache getBeanInformationCache();
	
	public BeanConverter getBeanConverter();
	
	public IndexWriter getIndexWriter();
	
	public IndexSearcher getIndexSearcher();
	
	/**
	 * tells the manager that the index-writer has to be re-opened
	 */
	public void shouldReopenIndexWriter();

}
