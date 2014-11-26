package com.github.hotware.lucene.extension.hsearch.manager;

import java.io.IOException;

import org.hibernate.search.indexes.IndexReaderAccessor;

import com.github.hotware.lucene.extension.bean.converter.BeanConverter;
import com.github.hotware.lucene.extension.bean.field.BeanInformationCache;
import com.github.hotware.lucene.extension.hsearch.internal.Util;

public class HibernateSearchLuceneManagerImpl implements HibernateSearchLuceneManager {
		
	public HibernateSearchLuceneManagerImpl(BeanConverter beanConverter,
			BeanInformationCache beanInformationCache) {
		super();
	}

	@Override
	public BeanConverter getBeanConverter() {
		return Util.BEAN_CONVERTER;
	}

	@Override
	public IndexReaderAccessor getIndexReaderAccessor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}



}
