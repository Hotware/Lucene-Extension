package com.github.hotware.lucene.extension.hsearch.manager;

import java.io.IOException;
import java.util.List;

import org.hibernate.search.cfg.spi.SearchConfiguration;
import org.hibernate.search.engine.spi.SearchFactoryImplementor;
import org.hibernate.search.spi.SearchFactoryBuilder;

public class HibernateSearchLuceneManagerImpl implements
		HibernateSearchLuceneManager {

	private final SearchFactory searchFactory;
	private final SearchFactoryImplementor searchFactoryImplementor;

	public HibernateSearchLuceneManagerImpl(
			SearchConfiguration searchConfiguration, List<Class<?>> classes) {
		super();
		SearchFactoryBuilder builder = new SearchFactoryBuilder();
		builder.configuration(
				searchConfiguration).buildSearchFactory();
		classes.forEach((clazz) -> {
			builder.addClass(clazz);
		});
		this.searchFactoryImplementor = builder.buildSearchFactory();
		this.searchFactory = new SearchFactoryImpl(
				this.searchFactoryImplementor);
	}

	@Override
	public void close() throws IOException {
		this.searchFactory.close();
	}

	@Override
	public SearchFactory getSearchFactory() {
		return this.searchFactory;
	}

}