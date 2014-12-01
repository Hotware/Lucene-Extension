package com.github.hotware.lucene.extension.hsearch.query;

import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.hibernate.search.filter.FullTextFilter;
import org.hibernate.search.query.engine.spi.HSQuery;
import org.hibernate.search.spatial.Coordinates;

public class HSearchQuery<T> {
	
	private final HSQuery hsquery;
	
	public HSearchQuery(HSQuery hsquery) {
		this.hsquery = hsquery;
	}

	public HSearchQuery<T> sort(Sort sort) {
		this.hsquery.sort(sort);
		return this;
	}

	public HSearchQuery<T> filter(Filter filter) {
		this.hsquery.filter(filter);
		return this;
	}

	public HSearchQuery<T> firstResult(int firstResult) {
		this.hsquery.firstResult(firstResult);
		return this;
	}

	public HSearchQuery<T> maxResults(int maxResults) {
		this.hsquery.maxResults(maxResults);
		return this;
	}

	public Query getLuceneQuery() {
		return this.hsquery.getLuceneQuery();
	}

	public int queryResultSize() {
		return this.hsquery.queryResultSize();
	}

	public Explanation explain(int documentId) {
		return this.hsquery.explain(documentId);
	}

	public FullTextFilter enableFullTextFilter(String name) {
		return hsquery.enableFullTextFilter(name);
	}

	public void disableFullTextFilter(String name) {
		this.hsquery.disableFullTextFilter(name);
	}

	public HSearchQuery<T> setSpatialParameters(Coordinates center, String fieldName) {
		this.hsquery.setSpatialParameters(center, fieldName);
		return this;
	}

}
