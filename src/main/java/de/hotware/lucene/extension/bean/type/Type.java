package de.hotware.lucene.extension.bean.type;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexableField;

/**
 * the main extension point for providing different types to handle in Lucene
 * Beans <br />
 * <br />
 * For slightly easier usage use {@link BaseType}
 * 
 * @author Martin Braun
 */
public interface Type {

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
	public void handleDocFieldValue(Document document, String name,
			Object value, FieldType fieldType, Class<?> objectFieldType);

	/**
	 * converts the data back to the original value
	 * 
	 * @return the original value
	 */
	public Object toBeanValue(IndexableField field);

	/**
	 * set the options for the FieldType you want the Field in your Document to
	 * have here, as it gets frozen afterwards. this is called after all other
	 * stuff is set up in the fieldtype.
	 * 
	 * <b>Note: you can change the index, store and tokenized attribute in here,
	 * but it is discouraged to do so</b>
	 */
	public void configureFieldType(FieldType fieldType);

}
