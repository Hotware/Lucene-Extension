package com.github.hotware.lucene.extension.hsearch.manager;

import java.io.Closeable;
import java.util.List;

import org.apache.lucene.search.Query;
import org.hibernate.search.backend.spi.WorkType;
import org.hibernate.search.indexes.IndexReaderAccessor;
import org.hibernate.search.query.dsl.QueryContextBuilder;
import org.hibernate.search.stat.Statistics;

import com.github.hotware.lucene.extension.hsearch.query.HSearchQuery;

public interface SearchFactory extends Closeable {
	
	public IndexReaderAccessor getIndexReaderAccessor();
	
	public QueryContextBuilder buildQueryBuilder();
	
	public void optimize();
	
	public void optimize(Class<?> entity);
	
	public Statistics getStatistics();

	public void doWork(Iterable<Object> objects, WorkType workType);
	
	public <T> HSearchQuery<T> createQuery(Query query, Class<T> targetedEntity);
	
	public <R> List<R> query(HSearchQuery<?> query, Class<R> returnedType);
	
}
