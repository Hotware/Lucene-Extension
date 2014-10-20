package de.hotware.lucene.extension.bean;

import org.apache.lucene.document.Document;

/**
 * A Bean converter can convert Beans that are annotated with BeanField(s)s into
 * Lucene Documents
 * 
 * @author Martin Braun
 */
public interface BeanConverter {

	public <T> T documentToBean(Class<T> clazz, Document document);

	public Document beanToDocument(Object bean);

}
