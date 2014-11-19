/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <martinbraun123@aol.com> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Martin Braun
 * ----------------------------------------------------------------------------
 */
package de.hotware.lucene.extension.bean.converter;

import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
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

	public PerFieldAnalyzerWrapper getPerFieldAnalyzerWrapper(Class<?> clazz);

}