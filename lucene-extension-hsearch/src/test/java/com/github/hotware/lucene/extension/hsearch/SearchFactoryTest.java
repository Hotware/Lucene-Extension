package com.github.hotware.lucene.extension.hsearch;

import java.io.IOException;
import java.util.Arrays;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;
import com.github.hotware.lucene.extension.hsearch.manager.HibernateSearchLuceneManager;
import com.github.hotware.lucene.extension.hsearch.manager.HibernateSearchLuceneManagerImpl;
import com.github.hotware.lucene.extension.hsearch.manager.SearchConfigurationImpl;

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

		public void setTest(String test) {
			this.test = test;
		}

		@Field(store = Store.YES)
		public String getTest() {
			return this.test;
		}

	}

	public void test() throws IOException {
		try(HibernateSearchLuceneManager luceneManager = new HibernateSearchLuceneManagerImpl(
				new SearchConfigurationImpl(), Arrays.asList(TopLevel.class))) {
			luceneManager.getSearchFactory();
		}
	}

}
