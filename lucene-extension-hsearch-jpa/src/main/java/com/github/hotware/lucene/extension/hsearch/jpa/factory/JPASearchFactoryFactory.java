package com.github.hotware.lucene.extension.hsearch.jpa.factory;

import java.util.List;

import org.hibernate.search.cfg.spi.SearchConfiguration;

import com.github.hotware.lucene.extension.hsearch.factory.SearchFactory;
import com.github.hotware.lucene.extension.hsearch.factory.SearchFactoryFactory;
import com.github.hotware.lucene.extension.hsearch.jpa.event.EventProvider;

public final class JPASearchFactoryFactory {

	public static JPASearchFactory createJPASearchFactory(EventProvider eventProvider,
			SearchConfiguration searchConfiguration, List<Class<?>> classes) {
		SearchFactory plainSearchFactory = SearchFactoryFactory
				.createSearchFactory(searchConfiguration, classes);
		JPASearchFactory jpaFactory = new JPASearchFactoryImpl(plainSearchFactory);
		eventProvider.setEventConsumer(jpaFactory);
		return jpaFactory;
	}

}
