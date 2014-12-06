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
import org.junit.Test;

import com.github.hotware.lucene.extension.hseach.entity.jpa.EntityManagerEntityProvider;
import com.github.hotware.lucene.extension.hsearch.entity.EntityProvider;
import com.github.hotware.lucene.extension.hsearch.event.EventConsumer;
import com.github.hotware.lucene.extension.hsearch.event.EventProvider;
import com.github.hotware.lucene.extension.hsearch.factory.SearchConfigurationImpl;
import com.github.hotware.lucene.extension.hsearch.factory.SearchFactory;
import com.github.hotware.lucene.extension.hsearch.factory.SearchFactoryFactory;
import com.github.hotware.lucene.extension.hsearch.jpa.test.entities.Place;
import com.github.hotware.lucene.extension.hsearch.jpa.test.entities.Sorcerer;
import com.github.hotware.lucene.extension.hsearch.query.HSearchQuery;

public class IntegrationTest {

	private int valinorId = 0;

	public void setup(EntityManagerFactory emf) {
		EntityManager em = emf.createEntityManager();
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

			valinorId = valinor.getId();

			em.flush();
			tx.commit();
		} finally {
			if (em != null) {
				em.close();
			}
		}

	}

	@Test
	public void testHibernate() throws IOException {
		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("Hibernate");
		this.setup(emf);
		try {
			this.test(emf);
		} finally {
			emf.close();
		}
	}

//	@Test
//	public void testEclipseLink() throws IOException {
//		EntityManagerFactory emf = Persistence
//				.createEntityManagerFactory("EclipseLink");
//		this.setup(emf);
//		try {
//			this.test(emf);
//		} finally {
//			emf.close();
//		}
//	}

	public void test(EntityManagerFactory emf) throws IOException {
		EntityProvider entityProvider = null;
		SearchFactory searchFactory = null;
		try {
			EntityManager em;
			entityProvider = new EntityManagerEntityProvider(
					em = emf.createEntityManager());
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			EmptyEventProvider eventProvider = new EmptyEventProvider();
			searchFactory = SearchFactoryFactory.createSearchFactory(
					eventProvider, new SearchConfigurationImpl(),
					Arrays.asList(Place.class));

			Place valinorDb = em.find(Place.class, valinorId);
			// this should not trigger an PostUpdate and doesn't
			// TODO: unit-testify this
			em.merge(valinorDb);

			valinorDb.setName("Valinor123");
			valinorDb.setName("Valinor");
			// this shouldn't trigger an PostUpdate and doesn't
			em.merge(valinorDb);

			{
				// this is done to test the behaviour of PostUpdate because of
				// this:
				// http://stackoverflow.com/questions/12097485/why-does-a-jpa-preupdate-annotated-method-get-called-during-a-query
				//
				// this was tested by hand, but should maybe changed into a unit
				// test? PostUpdate will only get called when there is an actual
				// change present (at least for Hibernate & EclipseLink) so we
				// should be fine
				// to use PostUpdate for automatically updating our index
				Place place = (Place) em.createQuery("SELECT a FROM Place a")
						.getResultList().get(0);

				Sorcerer newSorcerer = new Sorcerer();
				newSorcerer.setName("Odalbort the Unknown");

				place.getSorcerers().add(newSorcerer);

				place = (Place) em.createQuery("SELECT a FROM Place a")
						.getResultList().get(0);
			}

			// check if the consumer was set correctly
			eventProvider.sendEvent(valinorDb);

			// TODO: check if this is done correctly in the index
			searchFactory.purgeAll(Place.class);
			searchFactory.index(valinorDb);
			searchFactory.delete(valinorDb);
			searchFactory.index(valinorDb);
			searchFactory.update(valinorDb);

			Query query = searchFactory.buildQueryBuilder()
					.forEntity(Place.class).get().keyword().onField("name")
					.matching("valinor").createQuery();
			HSearchQuery<Place> jpaQuery = searchFactory.createQuery(query,
					Place.class);
			List<Place> places = jpaQuery.query(entityProvider, Place.class);

			assertEquals(1, places.size());
			assertEquals("Valinor", places.get(0).getName());

			System.out.println("finished integration test");
			tx.commit();
		} finally {
			if (entityProvider != null) {
				entityProvider.close();
			}
			if (searchFactory != null) {
				searchFactory.close();
			}
		}
	}

	public static final class EmptyEventProvider implements EventProvider {

		private EventConsumer eventConsumer;

		@Override
		public void setEventConsumer(EventConsumer eventConsumer) {
			this.eventConsumer = eventConsumer;
		}

		public void sendEvent(Place place) {
			this.eventConsumer.index(place);
		}

	}

}
