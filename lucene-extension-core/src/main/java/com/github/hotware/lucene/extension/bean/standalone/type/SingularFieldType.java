package com.github.hotware.lucene.extension.bean.standalone.type;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexableField;

/**
 * types that store their stuff in a singular field 
 * (that can still have multiple values, but most of 
 * the time the already supported Set and List are sufficient)
 * 
 * @author Martin
 */
public interface SingularFieldType extends Type {

	/**
	 * converts the data back to the original value
	 * 
	 * @return the original value
	 */
	public Object toBeanValue(IndexableField field);
	
	/**
	 * adds a field of this Type to the given Document
	 * 
	 * @param document
	 *            the Document to put the field into
	 * @param name
	 *            the fields name in the Document
	 * @param value
	 *            the original value
	 * @param fieldType
	 *            the <b>frozen</b> FieldType to use
	 * @param objectFieldType
	 *            the Class of the original value
	 */
	public void handleDocFieldValue(Document document, String fieldName,
			Object value, FieldType fieldType, Class<?> objectFieldType);

}
