package de.hotware.lucene.extension.util;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import de.hotware.lucene.extension.bean.converter.BeanConverter;

public class Finder<T> {

	private final IndexSearcher indexSearcher;
	private final BeanConverter beanConverter;
	private final Class<T> beanClazz;

	public Finder(IndexSearcher indexSearcher, BeanConverter beanConverter,
			Class<T> beanClazz) {
		super();
		this.indexSearcher = indexSearcher;
		this.beanConverter = beanConverter;
		this.beanClazz = beanClazz;
	}
	
	public T findOne(Query query) throws IOException {
		TopDocs topDocs = indexSearcher.search(query, 1);
		if(topDocs.totalHits > 1) {
			throw new IllegalArgumentException("query resulted in more than one results!");
		}
		int docId = topDocs.scoreDocs[0].doc;
		Document doc = this.indexSearcher.doc(docId);
		return this.beanConverter.documentToBean(this.beanClazz, doc);
	}

}
