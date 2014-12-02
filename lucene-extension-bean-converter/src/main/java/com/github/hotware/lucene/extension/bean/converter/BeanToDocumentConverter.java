package com.github.hotware.lucene.extension.bean.converter;

import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.Document;

public interface BeanToDocumentConverter {

	public <T> Document writeBeanToDocument(Object bean, Document document);

	public Document beanToDocument(Object bean);

	public PerFieldAnalyzerWrapper getPerFieldAnalyzerWrapper(Class<?> clazz);

}
