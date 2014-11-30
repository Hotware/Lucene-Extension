package com.github.hotware.lucene.extension.hsearch.manager;

import java.io.IOException;
import java.util.Map;

import org.apache.lucene.search.Query;
import org.hibernate.search.backend.spi.Work;
import org.hibernate.search.backend.spi.WorkType;
import org.hibernate.search.backend.spi.Worker;
import org.hibernate.search.engine.metadata.impl.TypeMetadata;
import org.hibernate.search.engine.spi.SearchFactoryImplementor;
import org.hibernate.search.indexes.IndexReaderAccessor;
import org.hibernate.search.query.dsl.QueryContextBuilder;
import org.hibernate.search.query.engine.spi.HSQuery;
import org.hibernate.search.stat.Statistics;

public class SearchFactoryImpl implements SearchFactory {

	private final SearchFactoryImplementor searchFactoryImplementor;

	public SearchFactoryImpl(SearchFactoryImplementor searchFactoryImplementor,
			Map<Class<?>, TypeMetadata> metaData) {
		super();
		this.searchFactoryImplementor = searchFactoryImplementor;
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
	public HSQuery query(Query query) {
		return this.searchFactoryImplementor.createHSQuery().luceneQuery(query);
	}

}
