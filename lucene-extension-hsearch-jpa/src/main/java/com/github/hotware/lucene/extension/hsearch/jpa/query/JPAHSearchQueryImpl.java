package com.github.hotware.lucene.extension.hsearch.jpa.query;

import java.util.List;

import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.hibernate.search.filter.FullTextFilter;

import com.github.hotware.lucene.extension.hsearch.query.HSearchQuery;

public class JPAHSearchQueryImpl<T> implements JPAHSearchQuery<T> {

	private final HSearchQuery<T> hsearchQuery;

	public JPAHSearchQueryImpl(HSearchQuery<T> hsearchQuery) {
		this.hsearchQuery = hsearchQuery;
	}

	public JPAHSearchQuery<T> sort(Sort sort) {
		this.hsearchQuery.sort(sort);
		return this;
	}

	public JPAHSearchQuery<T> filter(Filter filter) {
		this.hsearchQuery.filter(filter);
		return this;
	}

	public JPAHSearchQuery<T> firstResult(int firstResult) {
		this.hsearchQuery.firstResult(firstResult);
		return this;
	}

	public JPAHSearchQuery<T> maxResults(int maxResults) {
		this.hsearchQuery.maxResults(maxResults);
		return this;
	}

	public Query getLuceneQuery() {
		return hsearchQuery.getLuceneQuery();
	}

	public <R> List<R> queryDto(Class<R> returnedType) {
		return hsearchQuery.queryDto(returnedType);
	}

	public List<Object[]> queryProjection(String... projection) {
		return hsearchQuery.queryProjection(projection);
	}

	public int queryResultSize() {
		return hsearchQuery.queryResultSize();
	}

	public FullTextFilter enableFullTextFilter(String name) {
		return hsearchQuery.enableFullTextFilter(name);
	}

	public void disableFullTextFilter(String name) {
		hsearchQuery.disableFullTextFilter(name);
	}

}
