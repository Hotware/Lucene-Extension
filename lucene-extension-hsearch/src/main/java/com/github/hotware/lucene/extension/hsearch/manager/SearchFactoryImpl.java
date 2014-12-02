package com.github.hotware.lucene.extension.hsearch.manager;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.search.Query;
import org.hibernate.search.backend.spi.Work;
import org.hibernate.search.backend.spi.WorkType;
import org.hibernate.search.backend.spi.Worker;
import org.hibernate.search.engine.spi.SearchFactoryImplementor;
import org.hibernate.search.indexes.IndexReaderAccessor;
import org.hibernate.search.query.dsl.QueryContextBuilder;
import org.hibernate.search.stat.Statistics;

import com.github.hotware.lucene.extension.bean.hsearch.HibernateSearchDocumentToDtoConverter;
import com.github.hotware.lucene.extension.bean.hsearch.annotations.DtoOverEntity;
import com.github.hotware.lucene.extension.hsearch.query.HSearchQuery;

public class SearchFactoryImpl implements SearchFactory {

	private final SearchFactoryImplementor searchFactoryImplementor;
	private final HibernateSearchDocumentToDtoConverter beanConverter;

	public SearchFactoryImpl(SearchFactoryImplementor searchFactoryImplementor) {
		super();
		this.searchFactoryImplementor = searchFactoryImplementor;
		this.beanConverter = new HibernateSearchDocumentToDtoConverter(
				(clazz) -> {
					return this.searchFactoryImplementor
							.getIndexedTypeDescriptor(clazz);
				});
	}

	@Override
	public IndexReaderAccessor getIndexReaderAccessor() {
		return this.searchFactoryImplementor.getIndexReaderAccessor();
	}

	@Override
	public void close() throws IOException {
		this.searchFactoryImplementor.close();
	}

	@Override
	public QueryContextBuilder buildQueryBuilder() {
		return this.searchFactoryImplementor.buildQueryBuilder();
	}

	@Override
	public void optimize() {
		this.searchFactoryImplementor.optimize();
	}

	@Override
	public void optimize(Class<?> entity) {
		this.searchFactoryImplementor.optimize(entity);
	}

	@Override
	public Statistics getStatistics() {
		return this.searchFactoryImplementor.getStatistics();
	}

	@Override
	public void doWork(Iterable<Object> objects, WorkType workType) {
		TransactionContextImpl tc = new TransactionContextImpl();
		Worker worker = this.searchFactoryImplementor.getWorker();
		for (Object object : objects) {
			worker.performWork(new Work(object, workType), tc);
		}
		tc.end();
	}

	@Override
	public <R> List<R> query(HSearchQuery<?> query, Class<R> returnedType) {
		if(returnedType.isAnnotationPresent(DtoOverEntity.class)) {
//			hsquery.
		} else {
			
		}
		return null;
	}

	@Override
	public <T> HSearchQuery<T> createQuery(Query query, Class<T> targetedEntity) {
		// TODO Auto-generated method stub
		return null;
	}
}
