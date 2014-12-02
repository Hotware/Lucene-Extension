package com.github.hotware.lucene.extension.hsearch.query;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.hibernate.search.filter.FullTextFilter;
import org.hibernate.search.query.engine.spi.HSQuery;

import com.github.hotware.lucene.extension.hsearch.dto.HibernateSearchQueryExecutor;

public class HSearchQuery<T> {

	// TODO: check if more methods are from hsquery are needed here

	private final HSQuery hsquery;
	private final AtomicBoolean frozen;
	private final HibernateSearchQueryExecutor queryExec;

	public HSearchQuery(HSQuery hsquery, HibernateSearchQueryExecutor queryExec) {
		this.hsquery = hsquery;
		this.frozen = new AtomicBoolean(false);
		this.queryExec = queryExec;
	}

	public HSearchQuery<T> sort(Sort sort) {
		this.checkNotFrozen();
		this.hsquery.sort(sort);
		return this;
	}

	public HSearchQuery<T> filter(Filter filter) {
		this.checkNotFrozen();
		this.hsquery.filter(filter);
		return this;
	}

	public HSearchQuery<T> firstResult(int firstResult) {
		this.checkNotFrozen();
		this.hsquery.firstResult(firstResult);
		return this;
	}

	public HSearchQuery<T> maxResults(int maxResults) {
		this.checkNotFrozen();
		this.hsquery.maxResults(maxResults);
		return this;
	}

	public Query getLuceneQuery() {
		this.checkNotFrozen();
		return this.hsquery.getLuceneQuery();
	}

	public <R> List<R> queryDto(Class<R> returnedType) {
		this.frozen.set(true);
		return this.queryExec.executeHSQuery(this.hsquery, returnedType);
	}

	public List<Object[]> queryProjection(String... projection) {
		this.frozen.set(true);
		String[] projectedFieldsBefore = this.hsquery.getProjectedFields();
		List<Object[]> ret;
		{
			this.hsquery.getTimeoutManager().start();
			
			this.hsquery.projection(projection);
			ret = this.hsquery.queryEntityInfos().stream().map((entityInfo) -> {
				return entityInfo.getProjection();
			}).collect(Collectors.toList());
			
			this.hsquery.getTimeoutManager().stop();
		}
		this.hsquery.projection(projectedFieldsBefore);
		return ret;
	}

	public int queryResultSize() {
		this.frozen.set(true);
		this.hsquery.getTimeoutManager().start();
		int resultSize = this.hsquery.queryResultSize();
		this.hsquery.getTimeoutManager().stop();
		return resultSize;
	}

	public FullTextFilter enableFullTextFilter(String name) {
		this.checkNotFrozen();
		return hsquery.enableFullTextFilter(name);
	}

	public void disableFullTextFilter(String name) {
		this.checkNotFrozen();
		this.hsquery.disableFullTextFilter(name);
	}

	private void checkNotFrozen() {
		if (this.frozen.get()) {
			throw new IllegalStateException(
					"this query was already used once and cannot be changed anymore");
		}
	}

}
