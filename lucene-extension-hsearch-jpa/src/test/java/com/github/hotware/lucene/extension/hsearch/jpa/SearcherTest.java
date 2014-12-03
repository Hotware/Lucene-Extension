package com.github.hotware.lucene.extension.hsearch.jpa;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.apache.lucene.search.Query;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.hotware.lucene.extension.hsearch.factory.SearchConfigurationImpl;
import com.github.hotware.lucene.extension.hsearch.factory.SearchFactory;
import com.github.hotware.lucene.extension.hsearch.factory.SearchFactoryFactory;
import com.github.hotware.lucene.extension.hsearch.jpa.entity.EntityManagerEntityProvider;
import com.github.hotware.lucene.extension.hsearch.jpa.entity.EntityProvider;
import com.github.hotware.lucene.extension.hsearch.jpa.event.EventConsumer;
import com.github.hotware.lucene.extension.hsearch.jpa.event.EventProvider;
import com.github.hotware.lucene.extension.hsearch.jpa.factory.JPASearchFactory;
import com.github.hotware.lucene.extension.hsearch.jpa.factory.JPASearchFactoryFactory;
import com.github.hotware.lucene.extension.hsearch.jpa.query.JPAHSearchQuery;
import com.github.hotware.lucene.extension.hsearch.jpa.test.entities.Place;
import com.github.hotware.lucene.extension.hsearch.jpa.test.entities.Sorcerer;

public class SearcherTest {

	private EntityManagerFactory emf;

	private Place valinor;

	@Before
	public void setup() {
		this.emf = Persistence.createEntityManagerFactory("Standalone");

		EntityManager em = this.emf.createEntityManager();
		try {
			EntityTransaction tx = em.getTransaction();
			tx.begin();

			Sorcerer gandalf = new Sorcerer();
			gandalf.setName("Gandalf");
			em.persist(gandalf);

			Sorcerer saruman = new Sorcerer();
			saruman.setName("Saruman");
			em.persist(saruman);

			Sorcerer radagast = new Sorcerer();
			radagast.setName("Radagast");
			em.persist(radagast);

			Sorcerer alatar = new Sorcerer();
			alatar.setName("Alatar");
			em.persist(alatar);

			Sorcerer pallando = new Sorcerer();
			pallando.setName("Pallando");
			em.persist(pallando);

			// populate this database with some stuff
			Place helmsDeep = new Place();
			helmsDeep.setName("Helm's Deep");
			Set<Sorcerer> sorcerersAtHelmsDeep = new HashSet<>();
			sorcerersAtHelmsDeep.add(gandalf);
			helmsDeep.setSorcerers(sorcerersAtHelmsDeep);
			em.persist(helmsDeep);

			Place valinor = new Place();
			valinor.setName("Valinor");
			Set<Sorcerer> sorcerersAtValinor = new HashSet<>();
			sorcerersAtValinor.add(saruman);
			valinor.setSorcerers(sorcerersAtValinor);
			em.persist(valinor);

			this.valinor = valinor;

			em.flush();
			tx.commit();
		} finally {
			if (em != null) {
				em.close();
			}
		}

	}

	@Test
	public void test() throws InterruptedException, IOException {
		EntityProvider entityProvider = null;
		JPASearchFactory searchFactory = null;
		try {
			entityProvider = new EntityManagerEntityProvider(
					this.emf.createEntityManager());
			EmptyEventProvider eventProvider = new EmptyEventProvider();
			searchFactory = JPASearchFactoryFactory.createJPASearchFactory(
					eventProvider, new SearchConfigurationImpl(),
					Arrays.asList(Place.class));

			eventProvider.sendEvent(this.valinor);

			Query query = searchFactory.buildQueryBuilder().forEntity(Place.class).get()
					.keyword().onField("name").matching("valinor").createQuery();
			JPAHSearchQuery<Place> jpaQuery = searchFactory.createQuery(query, Place.class);
			List<Place> places = jpaQuery.query(entityProvider, Place.class);
			
			assertEquals(1, places.size());
		} finally {
			if (entityProvider != null) {
				entityProvider.close();
			}
			if (searchFactory != null) {
				searchFactory.close();
			}
		}
	}

	@After
	public void cleanUp() {
		this.emf.close();
	}

	public static final class EmptyEventProvider implements EventProvider {

		private EventConsumer eventConsumer;

		@Override
		public void setEventConsumer(EventConsumer eventConsumer) {
			this.eventConsumer = eventConsumer;
		}

		public void sendEvent(Place place) {
			this.eventConsumer.index(Place.class, place);
		}

	}

}
