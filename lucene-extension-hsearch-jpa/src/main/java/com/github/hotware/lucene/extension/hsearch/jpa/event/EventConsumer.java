package com.github.hotware.lucene.extension.hsearch.jpa.event;

import java.util.Arrays;

public interface EventConsumer {
	
	public <T> void index(Class<T> entityClass, Iterable<T> entities);
	
	public default <T> void index(Class<T> entityClass, T entity) {
		this.index(entityClass, Arrays.asList(entity));
	}

	public <T> void update(Class<T> entityClass, Iterable<T> entities);
	
	public default <T> void update(Class<T> entityClass, T entity) {
		this.update(entityClass, Arrays.asList(entity));
	}

	public <T> void delete(Class<T> entityClass, Iterable<T> entities);
	
	public default <T> void delete(Class<T> entityClass, T entity) {
		this.delete(entityClass, Arrays.asList(entity));
	}
	
	public <T> void purge(Class<T> entityClass);

}
