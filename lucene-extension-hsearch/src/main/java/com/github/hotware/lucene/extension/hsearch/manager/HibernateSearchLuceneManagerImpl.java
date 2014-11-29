package com.github.hotware.lucene.extension.hsearch.manager;

import java.io.IOException;

import org.hibernate.search.cfg.spi.SearchConfiguration;
import org.hibernate.search.spi.SearchFactoryBuilder;

public class HibernateSearchLuceneManagerImpl implements
		HibernateSearchLuceneManager {

	private final SearchFactory searchFactory;

	public HibernateSearchLuceneManagerImpl(
			SearchConfiguration searchConfiguration) {
		super();
		SearchFactoryBuilder builder = new SearchFactoryBuilder();
		this.searchFactory = new SearchFactoryImpl(builder.configuration(
				new SearchConfigurationImpl()).buildSearchFactory());
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