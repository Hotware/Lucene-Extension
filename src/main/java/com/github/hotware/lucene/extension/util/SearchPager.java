package com.github.hotware.lucene.extension.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;

import com.github.hotware.lucene.extension.bean.converter.BeanConverter;

/**
 * Utility Class for easier paging in Lucene
 * 
 * @author Martin Braun
 * @param <T>
 *            the bean class to return from the Index or {@link Document} if you
 *            want the plain Documents
 */
public class SearchPager<T> {

	private final IndexSearcher indexSearcher;
	private final TopDocs topDocs;
	private final BeanConverter beanConverter;
	private final Class<T> beanClazz;
	private final int pageSize;

	/**
	 * @param indexSearcher
	 * @param topDocs
	 * @param beanConverter
	 *            the beanConverter to use or null if beanClazz =
	 *            {@link Document}
	 * @param beanClazz
	 * @param pageSize
	 */
	public SearchPager(IndexSearcher indexSearcher, TopDocs topDocs,
			BeanConverter beanConverter, Class<T> beanClazz, int pageSize) {
		super();
		this.indexSearcher = indexSearcher;
		this.topDocs = topDocs;
		if (beanClazz.equals(Document.class) && beanConverter != null) {
			throw new IllegalArgumentException("beanConverter may not be null "
					+ "if beanClazz is equal to Document. Wouldn't make sense");
		}
		this.beanConverter = beanConverter;
		this.beanClazz = beanClazz;
		this.pageSize = pageSize;
	}

	@SuppressWarnings("unchecked")
	public List<T> getPage(int page) throws IOException {
		List<T> results = new ArrayList<>();
		for (int cur = page * this.pageSize; cur < this.topDocs.totalHits; ++cur) {
			int docId = this.topDocs.scoreDocs[cur].doc;
			Document doc = this.indexSearcher.doc(docId);
			if (beanClazz.equals(Document.class)) {
				results.add((T) doc);
			} else {
				T bean = (T) this.beanConverter.documentToBean(this.beanClazz,
						doc);
				results.add(bean);
			}
		}
		return results;
	}

	public int getPageCount() {
		return (int) Math.ceil((double) topDocs.totalHits / (double) pageSize);
	}

}
