package com.github.hotware.lucene.extension.hsearch.jpa.query;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.hibernate.search.engine.ProjectionConstants;
import org.hibernate.search.filter.FullTextFilter;

import com.github.hotware.lucene.extension.hsearch.jpa.entity.EntityProvider;
import com.github.hotware.lucene.extension.hsearch.query.HSearchQuery;

public class JPAHSearchQueryImpl<T> implements JPAHSearchQuery<T> {

	private final HSearchQuery<T> hsearchQuery;

	public JPAHSearchQueryImpl(HSearchQuery<T> hsearchQuery) {
		this.hsearchQuery = hsearchQuery;
	}

	@Override
	public JPAHSearchQuery<T> sort(Sort sort) {
		this.hsearchQuery.sort(sort);
		return this;
	}

	@Override
	public JPAHSearchQuery<T> filter(Filter filter) {
		this.hsearchQuery.filter(filter);
		return this;
	}

	@Override
	public JPAHSearchQuery<T> firstResult(int firstResult) {
		this.hsearchQuery.firstResult(firstResult);
		return this;
	}

	@Override
	public JPAHSearchQuery<T> maxResults(int maxResults) {
		this.hsearchQuery.maxResults(maxResults);
		return this;
	}

	@Override
	public Query getLuceneQuery() {
		return this.hsearchQuery.getLuceneQuery();
	}

	@Override
	public <R> List<R> queryDto(Class<R> returnedType) {
		return this.hsearchQuery.queryDto(returnedType);
	}

	@Override
	public List<Object[]> queryProjection(String... projection) {
		return this.hsearchQuery.queryProjection(projection);
	}

	@Override
	public int queryResultSize() {
		return this.hsearchQuery.queryResultSize();
	}

	@Override
	public FullTextFilter enableFullTextFilter(String name) {
		return this.hsearchQuery.enableFullTextFilter(name);
	}

	@Override
	public void disableFullTextFilter(String name) {
		this.hsearchQuery.disableFullTextFilter(name);
	}

	@Override
	public <R> List<R> query(EntityProvider entityProvider,
			Class<R> returnedType) {
		return this.queryProjection(ProjectionConstants.ID).stream()
				.map((arr) -> {
					return entityProvider.get(returnedType, arr[0]);
				}).collect(Collectors.toList());
	}

}
