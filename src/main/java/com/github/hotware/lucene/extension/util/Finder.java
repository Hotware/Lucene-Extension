package com.github.hotware.lucene.extension.util;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;

import com.github.hotware.lucene.extension.bean.converter.BeanConverter;

/**
 * Utility Class for easier searching in Lucene
 * 
 * @author Martin Braun
 * @param <T>
 *            the bean class to return from the Index. Unlike
 *            {@link SearchPager} this does not support to return raw Documents,
 *            as that's not that hard to accomplish without this class ;)
 */
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
		if (topDocs.totalHits > 1) {
			throw new IllegalArgumentException(
					"query resulted in more than one results!");
		}
		int docId = topDocs.scoreDocs[0].doc;
		Document doc = this.indexSearcher.doc(docId);
		return this.beanConverter.documentToBean(this.beanClazz, doc);
	}

}
