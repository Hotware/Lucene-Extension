package com.github.hotware.lucene.extension.hsearch.internal;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.AnalyzerWrapper;

import static com.github.hotware.lucene.extension.hsearch.internal.Util.*;

public abstract class BeanConverterWrappingAnalyzer extends AnalyzerWrapper {

	public BeanConverterWrappingAnalyzer() {
		super(Analyzer.GLOBAL_REUSE_STRATEGY);
	}

	@Override
	protected final Analyzer getWrappedAnalyzer(String fieldName) {
		return BEAN_CONVERTER.getPerFieldAnalyzerWrapper(this
				.getConvertedClass());
	}

	public abstract Class<?> getConvertedClass();

}
