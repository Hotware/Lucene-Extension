package com.github.hotware.lucene.extension.hsearch.jpa.query;

import java.util.List;

import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.hibernate.search.filter.FullTextFilter;

import com.github.hotware.lucene.extension.hsearch.jpa.entity.EntityProvider;

public interface JPAHSearchQuery<T> {

	//COPIED from HSearchQuery:
	
	public JPAHSearchQuery<T> sort(Sort sort);

	public JPAHSearchQuery<T> filter(Filter filter);

	public JPAHSearchQuery<T> firstResult(int firstResult);

	public JPAHSearchQuery<T> maxResults(int maxResults);

	public Query getLuceneQuery();

	public <R> List<R> queryDto(Class<R> returnedType);

	public List<Object[]> queryProjection(String... projection);

	public int queryResultSize();

	public FullTextFilter enableFullTextFilter(String name);

	public void disableFullTextFilter(String name);
	
	//NEW METHODS:
	
	public <R> List<R> query(EntityProvider entityProvider, Class<R> returnedType);

}
