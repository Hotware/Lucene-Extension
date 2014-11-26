package com.github.hotware.lucene.extension.hsearch.internal;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.IndexableField;
import org.hibernate.search.bridge.LuceneOptions;
import org.hibernate.search.bridge.TwoWayFieldBridge;

import static com.github.hotware.lucene.extension.hsearch.internal.Util.*;

/**
 * maybe don't index the ID field?
 * 
 * @author Martin Braun
 */
public class BeanConverterFieldBridge implements TwoWayFieldBridge {

	@Override
	public void set(String fieldName, Object value, Document document,
			LuceneOptions luceneOptions) {
		if (fieldName.equals(HibernateSearchDTO.ID_FIELD_NAME)) {
			luceneOptions.addFieldToDocument(fieldName, String.valueOf(value),
					document);
		} else {
			BEAN_CONVERTER.writeBeanToDocument(value, document);
			document.add(new StoredField("__clazz", value.getClass().getName()));
		}
	}

	@Override
	public Object get(String fieldName, Document document) {
		if (fieldName.equals(HibernateSearchDTO.ID_FIELD_NAME)) {
			return Integer.parseInt(document.get(fieldName));
		} else {
			IndexableField[] clazzFields = document.getFields("__clazz");
			if (clazzFields.length != 1) {
				throw new IllegalArgumentException(
						"the index must contain at least one __clazz field for each Document!");
			}
			Class<?> clazz = CLASSES_FOR_NAME.computeIfAbsent(
					clazzFields[0].stringValue(), (className) -> {
						try {
							return Class.forName(className);
						} catch (ClassNotFoundException e) {
							throw new RuntimeException(e);
						}
					});
			return BEAN_CONVERTER.documentToBean(clazz, document);
		}
	}

	@Override
	public String objectToString(Object arg0) {
		throw new AssertionError("this should never even be called!");
	}

}
