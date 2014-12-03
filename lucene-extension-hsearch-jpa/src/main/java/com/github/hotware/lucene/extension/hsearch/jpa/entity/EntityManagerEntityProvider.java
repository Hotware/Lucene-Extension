package com.github.hotware.lucene.extension.hsearch.jpa.entity;

import javax.persistence.EntityManager;

public class EntityManagerEntityProvider implements EntityProvider {

	private final EntityManager entityManager;

	public EntityManagerEntityProvider(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public <T> T get(Class<T> entityClass, Object id) {
		return this.entityManager.find(entityClass, id);
	}

	@Override
	public void close() {
		this.entityManager.close();
	}

}
