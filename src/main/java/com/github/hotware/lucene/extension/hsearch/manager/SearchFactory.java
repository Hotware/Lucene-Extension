package com.github.hotware.lucene.extension.hsearch.manager;

import java.io.Closeable;

import org.hibernate.search.indexes.IndexReaderAccessor;
import org.hibernate.search.query.dsl.QueryContextBuilder;
import org.hibernate.search.stat.Statistics;

public interface SearchFactory extends Closeable {
	
	public IndexReaderAccessor getIndexReaderAccessor();
	
	public QueryContextBuilder buildQueryBuilder();
	
	public void optimize();
	
	public void optimize(Class<?> entity);
	
	public Statistics getStatistics();
	
	public void index(Iterable<Object> objects);
	
}
