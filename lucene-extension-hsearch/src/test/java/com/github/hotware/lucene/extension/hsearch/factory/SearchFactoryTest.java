package com.github.hotware.lucene.extension.hsearch.factory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.backend.spi.Work;
import org.hibernate.search.backend.spi.WorkType;
import org.hibernate.search.cfg.spi.SearchConfiguration;
import org.hibernate.search.engine.spi.SearchFactoryImplementor;
import org.hibernate.search.spi.SearchFactoryBuilder;

import com.github.hotware.lucene.extension.hsearch.event.NoEventEventProvider;
import com.github.hotware.lucene.extension.hsearch.factory.SearchConfigurationImpl;
import com.github.hotware.lucene.extension.hsearch.factory.SearchFactory;
import com.github.hotware.lucene.extension.hsearch.factory.SearchFactoryFactory;

import junit.framework.TestCase;

public class SearchFactoryTest extends TestCase {

	@Indexed
	public static class TopLevel {

		private int id;
		private Embedded embedded;

		public void setId(int id) {
			this.id = id;
		}

		@DocumentId
		public int getId() {
			return this.id;
		}

		@IndexedEmbedded
		public Embedded getEmbedded() {
			return embedded;
		}

		public void setEmbedded(Embedded embedded) {
			this.embedded = embedded;
		}

	}

	public static class Embedded {

		private String test;
		private TopLevel topLevel;

		public void setTest(String test) {
			this.test = test;
		}

		@Field(store = Store.YES)
		public String getTest() {
			return this.test;
		}
		
		@ContainedIn
		public TopLevel getTopLevel() {
			return this.topLevel;
		}
		
		public void setTopLevel(TopLevel topLevel) {
			this.topLevel = topLevel;
		}

	}
	
	public void testWithoutNewClasses() {
		SearchConfiguration searchConfiguration = new SearchConfigurationImpl();
		List<Class<?>> classes = Arrays.asList(TopLevel.class);
		
		SearchFactoryBuilder builder = new SearchFactoryBuilder();
		builder.configuration(searchConfiguration).buildSearchFactory();
		classes.forEach((clazz) -> {
			builder.addClass(clazz);
		});
		SearchFactoryImplementor impl = builder.buildSearchFactory();
		
		TopLevel tl = new TopLevel();
		tl.setId(123);
		Embedded eb = new Embedded();
		tl.setEmbedded(eb);
		TransactionContextImpl tc = new TransactionContextImpl();
		                 
		impl.getWorker().performWork(new Work(tl, WorkType.ADD), tc);
	}

	public void test() throws IOException {
		try(SearchFactory factory = SearchFactoryFactory.createSearchFactory(new NoEventEventProvider(),
				new SearchConfigurationImpl(), Arrays.asList(TopLevel.class, Embedded.class))) {
			
			TopLevel tl = new TopLevel();
			tl.setId(123);
			Embedded eb = new Embedded();
			tl.setEmbedded(eb);
			eb.setTopLevel(tl);
			factory.doIndexwork(eb, WorkType.ADD);
			
			//indexing starting from the contained entity should work as well :)
			assertEquals(1, factory.getStatistics().getNumberOfIndexedEntities(TopLevel.class.getName()));
		}
	}

}
