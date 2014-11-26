package com.github.hotware.lucene.extension.hsearch.manager;

import java.io.IOException;

import org.hibernate.search.backend.AddLuceneWork;
import org.hibernate.search.backend.LuceneWork;
import org.hibernate.search.backend.TransactionContext;
import org.hibernate.search.backend.impl.WorkVisitor;
import org.hibernate.search.backend.spi.Worker;
import org.hibernate.search.engine.spi.SearchFactoryImplementor;
import org.hibernate.search.indexes.IndexReaderAccessor;
import org.hibernate.search.query.dsl.QueryContextBuilder;
import org.hibernate.search.stat.Statistics;

public class SearchFactoryImpl implements SearchFactory {
	
	private final SearchFactoryImplementor searchFactoryImplementor;

	public SearchFactoryImpl(SearchFactoryImplementor searchFactoryImplementor) {
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
	public void index(Iterable<Object> objects) {
		TransactionContextImpl tc = new TransactionContextImpl();
		Worker worker = this.searchFactoryImplementor.getWorker();
		tc.end();
	}


}
