package com.github.hotware.lucene.extension.hsearch;

import java.util.Arrays;
import java.util.Map;

import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.engine.metadata.impl.TypeMetadata;

import com.github.hotware.lucene.extension.hsearch.custom.SearchFactoryBuilder;
import com.github.hotware.lucene.extension.hsearch.manager.HibernateSearchLuceneManager;
import com.github.hotware.lucene.extension.hsearch.manager.HibernateSearchLuceneManagerImpl;
import com.github.hotware.lucene.extension.hsearch.manager.SearchConfigurationImpl;

import junit.framework.TestCase;

public class SearchFactoryBuilderTest extends TestCase {

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

		public void setTest(String test) {
			this.test = test;
		}

		@Field(store = Store.YES)
		public String getTest() {
			return this.test;
		}

	}

	public void test() {
		SearchFactoryBuilder builder = new SearchFactoryBuilder();
		builder.configuration(new SearchConfigurationImpl());
		builder.createCleanSearchFactoryState();
		// hack :D. but this has to happen after cleaned the SearchFactoryState
		Map<Class<?>, TypeMetadata> metaData = builder.getMetaData(Arrays.asList(TopLevel.class));
		metaData.get(TopLevel.class).getAllDocumentFieldMetadata();
	}

	public void testInConstructorOfManager() {
		HibernateSearchLuceneManager manager = new HibernateSearchLuceneManagerImpl(
				new SearchConfigurationImpl(), Arrays.asList(TopLevel.class));
		System.out.println(manager);
	}
	
}
