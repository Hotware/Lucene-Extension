package com.github.hotware.lucene.extension.hsearch.internal;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.AnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.hibernate.search.util.impl.PassThroughAnalyzer;

public class BeanConverterWrappingAnalyzer extends AnalyzerWrapper {
	
	public BeanConverterWrappingAnalyzer() {
		super(Analyzer.GLOBAL_REUSE_STRATEGY);
	}

	@Override
	protected Analyzer getWrappedAnalyzer(String fieldName) {
		if (fieldName.equals(HibernateSearchDTO.ID_FIELD_NAME)) {
			return PassThroughAnalyzer.INSTANCE;
		} else {
			//FIXME: real analysis is needed here.
			return new StandardAnalyzer();
		}
	}

}
