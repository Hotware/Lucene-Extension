package com.github.hotware.lucene.extension.bean.converter;

import org.apache.lucene.document.Document;

public interface DocumentToBeanConverter {
	
	public <T> T documentToBean(Class<T> clazz, Document document);

}
